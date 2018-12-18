package com.mikealbert.service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.persistence.NoResultException;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mikealbert.data.dao.LeaseElementDAO;
import com.mikealbert.data.dao.MaintenanceProgramDAO;
import com.mikealbert.data.dao.VehicleSearchDAO;
import com.mikealbert.data.entity.ContractLine;
import com.mikealbert.data.entity.ExternalAccount;
import com.mikealbert.data.entity.FleetMaster;
import com.mikealbert.data.entity.LeaseElement;
import com.mikealbert.data.entity.QuotationModel;
import com.mikealbert.data.vo.VehicleSearchCriteriaVO;
import com.mikealbert.data.vo.VehicleSearchResultVO;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.exception.MalException;
import com.mikealbert.service.ContractService;
import com.mikealbert.service.FleetMasterService;
import com.mikealbert.service.QuotationService;
import com.mikealbert.util.MALUtilities;

/**
 * A unit number search will yield only the vehicle with that exact unit number. </br>
 * 
 * A driver name search (all or partial, i.e. implicit wild card to the right will yield 
 * all units currently in service or on contract assigned to that driver </br>
 * 
 * A VIN search (last six digits), will yield only units that contain all of the last 
 * six digits of the VIN should be displayed.</br>
 * 
 * A license plate number search will yield the most current unit in TAL with that 
 * license plate number. If TAL does not have the plate number, Fleet Masters Reg No is searched.</br>
 * 
 * A clientï¿½s fleet reference number will yield only units with that fleet reference -- same or different clients </br>
 * 
 * A client name or number search will yield only units that are currently in service or on contract with that client</br>
 * 
 * A PO number search will yield units with purchase orders containing all of the PO number. </br>
 * 
 * A service provider (maintenance supplier partial or full) search in conjunction with the payee invoice number will yield units 
 * that were serviced by that service provider and payee invoice number</br>
 * 
 * A MAFS invoice number search will yield all units that appears on that MAFS invoice</br>
 * 
 * Searching by PO number, vendor & vendor invoice number and MAFS invoice number, returned data for client and driver will be what is reflected on the PO/invoice as of: </br>
 *     
 *     The actual start date, if null </br>
 *     The planned start date; if null </br>
 *     The most recent date.</br>
 *     
 * Implementation of {@link com.mikealbert.vision.service.VehicleSearchService}
 */
@Service("vehicleSearchService")
@Transactional
public class VehicleSearchServiceImpl implements VehicleSearchService {
	@Resource VehicleSearchDAO vehicleSearchDAO;
	@Resource QuotationService quotationService;
	@Resource ContractService contractService;
	@Resource FleetMasterService fleetMasterService;
	@Resource MaintenanceProgramDAO maintenanceProgramDAO;
	@Resource LeaseElementDAO leaseElementDAO;


	/**
	 * Retrieves a list vehicle/units based on the passed in search criteria. 
	 * @param vehicleSearchCriteriaVO The search criteria to use to perform the search
	 * @return vehicleSearchCriteriaVO Contains a collection of the search results
	 * @throws MalBusinessException 
	 */
	public List<VehicleSearchResultVO> findBySearchCriteria(VehicleSearchCriteriaVO vehicleSearchCriteriaVO, Pageable pageable, Sort sort) throws MalBusinessException{
		validateSearchCriteria(vehicleSearchCriteriaVO);		
		List<VehicleSearchResultVO> results = vehicleSearchDAO.searchVehicles(vehicleSearchCriteriaVO, pageable, sort);
		List<LeaseElement> leaseElementList = leaseElementDAO.findAllMaintenanceLeaseElements();
		ExternalAccount clientAccount;
		QuotationModel qmd;
		ContractLine contractLine;
		VehicleSearchResultVO vehicleSearchResultVO;
		Long qmdIdfromClnId;
		Long qmdIdforDisposedUnit;

		//For PO searches, only one result record should be returned. 
		//In the case where multiple result records are returned from the PO search, 
		//return the first result record. This is an unlikely case, but a few docs 
		//exist that will yield multiple result records, i.e. multiple invoices for the same PO.
		if(!MALUtilities.isEmpty(vehicleSearchCriteriaVO.getPurchaseOrderNumber())){
			if(!MALUtilities.isEmpty(results) && results.size() > 1){
				vehicleSearchResultVO = results.get(0);
				results.removeAll(results);
				results.add(vehicleSearchResultVO);
			}			
		}

		//Were client account is null, this means that there is either no contract
		//or the contract does not have a start and in service date. If this is the
		//case, then the client account will only be retrieved if there is a contract line.
		try { 
		for(VehicleSearchResultVO result : results){
			Long clnIdforReleaseUnit=maintenanceProgramDAO.getClnIdforReleaseUnit(result.getFmsId());
			if(!MALUtilities.isEmpty(clnIdforReleaseUnit)){
				qmdIdfromClnId=maintenanceProgramDAO.getQmdIdfromClnId(clnIdforReleaseUnit);
				if(!MALUtilities.isEmpty(qmdIdfromClnId)|| qmdIdfromClnId !=null){
					//For checking Informal Unit
					boolean informalUnit=maintenanceProgramDAO.validationCheckForInformalUnit(qmdIdfromClnId);
					if(informalUnit==true){
						for(LeaseElement leaseElement : leaseElementList){
							Long lelID;
							lelID = leaseElement.getLelId();
							String unitOnMaintenance =  maintenanceProgramDAO.findElementOnQuote(qmdIdfromClnId, lelID);
							if("Y".equalsIgnoreCase(unitOnMaintenance)){
								result.setVehicleUnderMaintenanceFlag(false);
								break;
							}
							if("N".equalsIgnoreCase(unitOnMaintenance)){
								result.setVehicleUnderMaintenanceFlag(true);
							}
						}
					}
					else if(informalUnit==false){
						int leaseElementCount =0;
						if(!MALUtilities.isEmpty(result.getFmsId())){
							leaseElementCount =  maintenanceProgramDAO.getleaseElementbyFmdId(qmdIdfromClnId);
							if(leaseElementCount==0){
								result.setVehicleUnderMaintenanceFlag(true);
							}
						}
					}
				}
			}
			else{
				Long clnId=maintenanceProgramDAO.getContractLinesfromfmsId(result.getFmsId());
				if(!MALUtilities.isEmpty(clnId)){
					qmdIdfromClnId=maintenanceProgramDAO.getQmdIdfromClnId(clnId);
					if(!MALUtilities.isEmpty(qmdIdfromClnId)|| qmdIdfromClnId !=null){
						//For checking Informal Unit
						boolean informalUnit=maintenanceProgramDAO.validationCheckForInformalUnit(qmdIdfromClnId);
						if(informalUnit==true){
							for(LeaseElement leaseElement : leaseElementList){
								Long lelID;
								lelID = leaseElement.getLelId();
								String unitOnMaintenance =  maintenanceProgramDAO.findElementOnQuote(qmdIdfromClnId, lelID);
								if("Y".equalsIgnoreCase(unitOnMaintenance)){
									result.setVehicleUnderMaintenanceFlag(false);
									break;
								}
								if("N".equalsIgnoreCase(unitOnMaintenance)){
									result.setVehicleUnderMaintenanceFlag(true);
								}
							}
						}
						else if(informalUnit==false){
							int leaseElementCount =0;
							if(!MALUtilities.isEmpty(result.getFmsId())){
								leaseElementCount =  maintenanceProgramDAO.getleaseElementbyFmdId(qmdIdfromClnId);
								if(leaseElementCount==0){
									result.setVehicleUnderMaintenanceFlag(true);
								}
							}
						}
					} 

				}

				else if(MALUtilities.isEmpty(clnId)){
					qmdIdforDisposedUnit = null;
					qmdIdforDisposedUnit=maintenanceProgramDAO.getClnIdforDisposedUnit(result.getFmsId());
					if(!MALUtilities.isEmpty(qmdIdforDisposedUnit) && (qmdIdforDisposedUnit !=null) && (qmdIdforDisposedUnit != 0)){	
						boolean Unit=maintenanceProgramDAO.validationCheckForInformalUnit(qmdIdforDisposedUnit);
						if(Unit==true){
							for(LeaseElement leaseElement : leaseElementList){
								Long lelID;
								lelID = leaseElement.getLelId();
								String unitOnMaintenance =  maintenanceProgramDAO.findElementOnQuote(qmdIdforDisposedUnit, lelID);
								if("Y".equalsIgnoreCase(unitOnMaintenance)){
									result.setVehicleUnderMaintenanceFlag(false);
									break;
								}
								if("N".equalsIgnoreCase(unitOnMaintenance)){
									result.setVehicleUnderMaintenanceFlag(true);	
								}
							}

						}
						else if(Unit==false){
							int leaseElementCount =0;
							if(!MALUtilities.isEmpty(result.getFmsId())){
								leaseElementCount =  maintenanceProgramDAO.getleaseElementbyFmdId(qmdIdforDisposedUnit);
								if(leaseElementCount==0){
									result.setVehicleUnderMaintenanceFlag(true);
								}
							}
						}
					} 

				}
			}
			if(MALUtilities.isEmpty(result.getClientAccountNumber())){
				Long qmdId = quotationService.getQmdIdFromUnitNo(result.getUnitNo());
				if(qmdId != null && !MALUtilities.isEmpty(qmdId)){
					qmd =  quotationService.getQuotationModel(quotationService.getQmdIdFromUnitNo(result.getUnitNo()));
				}else{
					continue;
				}

				if(!MALUtilities.isEmpty(qmd)) {
					if(qmd.getContractLineList().size() > 0){
						contractLine = qmd.getContractLineList().get(0);
						clientAccount = contractLine.getContract().getExternalAccount();
						result.setClientAccountNumber(clientAccount.getExternalAccountPK().getAccountCode());
						result.setClientAccountName(clientAccount.getAccountName());
						result.setClientAccountType(clientAccount.getExternalAccountPK().getAccountType());
						result.setClientCorpEntity(clientAccount.getExternalAccountPK().getCId());					
					}
				}
			}
		}		
		}catch(Exception e){
			throw new MalException(e.getMessage());
		}
			return results;
	}	

	/**
	 * Counts number of items in result list.
	 * @param vehicleSearchCriteriaVO The search criteria to use to perform the search
	 * @return int Number records in result list
	 * @throws MalBusinessException 
	 */
	public int findBySearchCriteriaCount(VehicleSearchCriteriaVO vehicleSearchCriteriaVO) throws MalBusinessException{
		int count = 0;

		validateSearchCriteria(vehicleSearchCriteriaVO);
		count = vehicleSearchDAO.searchVehiclesCount(vehicleSearchCriteriaVO);

		//For PO searches, only one result record should be returned. 
		if(!MALUtilities.isEmpty(vehicleSearchCriteriaVO.getPurchaseOrderNumber())){
			if(count > 1){
				count = 1;
			}			
		}

		return count;
	}

	/**
	 * Validates search criterion for violation of business rules
	 * @param vehicleSearchCriteriaVO Vehicle search criteria
	 * @throws MalBusinessException Exception
	 */
	private void validateSearchCriteria(VehicleSearchCriteriaVO vehicleSearchCriteriaVO) throws MalBusinessException{
		ArrayList<String> messages = new ArrayList<String>();

		//For service provider search, service provider name and invoice number are required.
		if( !(MALUtilities.isEmpty(vehicleSearchCriteriaVO.getServiceProviderInvoiceNumber()) 
				&& MALUtilities.isEmpty(vehicleSearchCriteriaVO.getServiceProviderName())) ){
			if(MALUtilities.isEmpty(vehicleSearchCriteriaVO.getServiceProviderInvoiceNumber()) 
					|| MALUtilities.isEmpty(vehicleSearchCriteriaVO.getServiceProviderName())){
				messages.add("Service Provider name and Payee Invoice number are required");
			}
		}		

		//Corporate entity must be specified
		if(MALUtilities.isEmpty(vehicleSearchCriteriaVO.getCorporateEntity())){
			messages.add("Corporate entity is required");
		}

		/*		
		//At a minimum, six characters of the VIN is required
		if(!MALUtilities.isEmptyString(vehicleSearchCriteriaVO.getVIN()) 
				&& vehicleSearchCriteriaVO.getVIN().length() < 6 ) {
			messages.add("VIN must contain six characters");			
		}
		 */
		if(messages.size() > 0)
			throw new MalBusinessException("service.validation", messages.toArray(new String[messages.size()]));		
	}



}

