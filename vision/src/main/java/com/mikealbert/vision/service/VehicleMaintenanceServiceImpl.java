package com.mikealbert.vision.service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.velocity.tools.generic.NumberTool;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mikealbert.common.MalLogger;
import com.mikealbert.common.MalLoggerFactory;
import com.mikealbert.data.dao.ClientScheduleTypeDAO;
import com.mikealbert.data.dao.DoclDAO;
import com.mikealbert.data.dao.DocumentNumberDAO;
import com.mikealbert.data.dao.ExtAccTaxExemptDAO;
import com.mikealbert.data.dao.ExternalAccountDAO;
import com.mikealbert.data.dao.FleetMasterDAO;
import com.mikealbert.data.dao.FleetNotesDAO;
import com.mikealbert.data.dao.MaintenanceCategoryPropertyValueDAO;
import com.mikealbert.data.dao.MaintenanceCodeDAO;
import com.mikealbert.data.dao.MaintenanceContactsDAO;
import com.mikealbert.data.dao.MaintenanceInvoiceDAO;
import com.mikealbert.data.dao.MaintenancePreferenceAccountDAO;
import com.mikealbert.data.dao.MaintenancePreferencesDAO;
import com.mikealbert.data.dao.MaintenanceProgramDAO;
import com.mikealbert.data.dao.MaintenanceRequestDAO;
import com.mikealbert.data.dao.MaintenanceRequestTaskDAO;
import com.mikealbert.data.dao.MulQuoteEleDAO;
import com.mikealbert.data.dao.QuotationModelDAO;
import com.mikealbert.data.dao.ServiceProviderDAO;
import com.mikealbert.data.dao.VehicleRegistrationVDAO;
import com.mikealbert.data.dao.VehicleReplacementVDAO;
import com.mikealbert.data.dao.VehicleStatusVDAO;
import com.mikealbert.data.dao.VehicleTechnicalInfoDAO;
import com.mikealbert.data.dao.WarrantyUnitLinkDAO;
import com.mikealbert.data.dao.WillowConfigDAO;
import com.mikealbert.data.entity.ClientScheduleType;
import com.mikealbert.data.entity.ContractLine;
import com.mikealbert.data.entity.DocumentNumber;
import com.mikealbert.data.entity.DocumentNumberPK;
import com.mikealbert.data.entity.ExtAccTaxExempt;
import com.mikealbert.data.entity.FleetMaster;
import com.mikealbert.data.entity.FleetNotes;
import com.mikealbert.data.entity.MaintenanceCategoryPropertyValue;
import com.mikealbert.data.entity.MaintenanceCode;
import com.mikealbert.data.entity.MaintenancePreferenceAccount;
import com.mikealbert.data.entity.MaintenanceRechargeCode;
import com.mikealbert.data.entity.MaintenanceRequest;
import com.mikealbert.data.entity.MaintenanceRequestStatus;
import com.mikealbert.data.entity.MaintenanceRequestTask;
import com.mikealbert.data.entity.MaintenanceRequestType;
import com.mikealbert.data.entity.MulQuoteEle;
import com.mikealbert.data.entity.RegionCode;
import com.mikealbert.data.entity.ServiceProvider;
import com.mikealbert.data.entity.ServiceProviderAddress;
import com.mikealbert.data.entity.ServiceProviderMaintenanceCode;
import com.mikealbert.data.entity.VehicleRegistrationV;
import com.mikealbert.data.entity.VehicleReplacementV;
import com.mikealbert.data.entity.VehicleReplacementVPK;
import com.mikealbert.data.enumeration.CorporateEntity;
import com.mikealbert.data.enumeration.VehicleStatus;
import com.mikealbert.data.vo.MaintenanceContactsVO;
import com.mikealbert.data.vo.MaintenancePreferencesVO;
import com.mikealbert.data.vo.MaintenanceProgramVO;
import com.mikealbert.data.vo.VehicleInformationVO;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.exception.MalException;
import com.mikealbert.service.ClientPOCService;
import com.mikealbert.service.ContractService;
import com.mikealbert.service.CostCenterService;
import com.mikealbert.service.LookupCacheService;
import com.mikealbert.service.MaintenanceRequestService;
import com.mikealbert.service.OdometerService;
import com.mikealbert.service.QuotationService;
import com.mikealbert.service.VelocityService;
import com.mikealbert.service.util.email.Email;
import com.mikealbert.service.util.email.EmailAddress;
import com.mikealbert.util.MALUtilities;

@Service("vehicleMaintenanceService")
public class VehicleMaintenanceServiceImpl implements VehicleMaintenanceService{
    @PersistenceContext EntityManager em;	
	@Resource MaintenanceRequestDAO maintenanceRequestDAO;
	@Resource MaintenanceRequestTaskDAO maintenanceRequestTaskDAO;
	@Resource FleetMasterDAO fleetMasterDAO;
	@Resource ContractService contractService;
	@Resource DoclDAO doclDAO;
	@Resource MaintenanceInvoiceDAO maintenanceInvoiceDAO;
	@Resource LookupCacheService lookupCacheService;
	@Resource ServiceProviderDAO serviceProviderDAO;
	@Resource VehicleRegistrationVDAO vehicleRegistrationVDAO;
	@Resource MaintenanceCodeDAO maintenanceCodeDAO;
	@Resource WillowConfigDAO willowConfigDAO;
	@Resource DocumentNumberDAO documentNumberDAO;		
	@Resource MaintenanceProgramDAO maintenanceProgramDAO;
	@Resource MaintenancePreferencesDAO maintenancePreferencesDAO;	
	@Resource OdometerService odometerService;
	@Resource VehicleReplacementVDAO vehicleReplacementVDAO;
	@Resource FleetNotesDAO fleetNotesDAO;
	@Resource VehicleStatusVDAO vehicleStatusVDAO;
	@Resource WillowConfigDAO willowConfig;
	@Resource ExtAccTaxExemptDAO extAccTaxExemptDAO;
	@Resource ExternalAccountDAO externalAccountDAO;
	@Resource MaintenancePreferenceAccountDAO maintenancePreferenceAccountDAO;
	@Resource MaintenanceContactsDAO maintenanceContactsDAO;
	@Resource VehicleTechnicalInfoDAO vehicleTechInfoDAO;
	@Resource WarrantyUnitLinkDAO warrantyUnitLinkDAO;
	@Resource QuotationService quotationService;
	@Resource CostCenterService costCenterService;
	@Resource MaintenanceRequestService maintRequestService;
	@Resource MaintenanceCategoryPropertyValueDAO maintenanceCategoryPropertyValueDAO;
	@Resource MulQuoteEleDAO mulQuoteEleDAO;
	@Resource QuotationModelDAO quotationModelDAO;
	@Resource ClientScheduleTypeDAO	clientScheduleTypeDAO;
	@Resource VelocityService velocityService;

	
	@Resource
	@Qualifier("exceededAuthLimitEmail")
	private Email exceededAuthLimitEmail;
	
	
	public MalLogger logger = MalLoggerFactory.getLogger(this.getClass());
	
	private static final List<String> BUDGETED_MAINTENANCE = Arrays.asList("MAINT_BGT");
	private static final List<String> FULL_MAINTENANCE = Arrays.asList("MAINT", "MAINT1");
	private static final List<String> MAINT_MGMT_ELEMENTS = Arrays.asList("CVMAINT","MAINT_EX_R","MAINT_MGMT","MM","NAP","NATL_ACCT","PREV_MAINT");
	private static final List<String> RISK_MGMT_ELEMENTS = Arrays.asList("RISK_ACCDT", "RISK_RENT", "RISK_REPR", "RISK_SUB");
	
	private static final String CC_EMAIL_LIST = "CC";
	private static final String TO_EMAIL_LIST = "TO";
	
	@Value("${exceeded-auth-limit.resource.subject.path}")
	private String exceededAuthLimitSubjectPath;		

	@Value("${exceeded-auth-limit.resource.body.html.path}")
	private String exceededAuthLimitBodyPath;		

	@Value("${exceeded-auth-limit.resource.message.path}")
	private String exceededAuthLimitMessagePath;		

	@Value("${exceeded-auth-limit.resource.summary.path}")
	private String exceededAuthLimitSummaryPath;		

	
	
	@Deprecated
	@Transactional
	public MaintenanceRequest getMaintenanceRequestByMrqId(long mrqId){
		try{
			MaintenanceRequest maintenanceRequest = maintenanceRequestDAO.getMaintenanceRequestByMrqId(mrqId);
			maintenanceRequest.getMaintenanceRequestTasks().size();
			if(!MALUtilities.isEmpty(maintenanceRequest.getServiceProvider())){
				maintenanceRequest.getServiceProvider().getServiceProviderAddresses().size();
			}
			return maintenanceRequest;
			
		} catch (Exception ex) {
			throw new MalException("generic.error.occured.while", 
					new String[] { "finding a maintenance request" }, ex);				
		}
	}

	@Deprecated
	public List<MaintenanceRequest> getMaintenanceRequestByFmsId(long fmsId) {
		try{
			return maintenanceRequestDAO.findByFmsId(fmsId);
			
		} catch (Exception ex) {
			throw new MalException("generic.error.occured.while", 
					new String[] { "finding a maintenance request" }, ex);				
		}
	}

	@Deprecated
	public MaintenanceRequest getMaintenanceRequestByJobNo(String jobNo) {
		try{
			return maintenanceRequestDAO.findByJobNo(jobNo);
			
		} catch (Exception ex) {
			throw new MalException("generic.error.occured.while", 
					new String[] { "finding a maintenance request" }, ex);				
		}
	}
				
	public int getMaintenanceServiceHistoryCount(List<Long> fmsIds, String providerFilter, String maintCategoryFilter, String maintCodeDescFilter){
		try{
			//Get fmsIds for the vin
			//List<Long>fmsIds = fleetMasterDAO.findFmsIdsByVIN(vin);
			return maintenanceRequestDAO.getMaintRequestByFmsIdsCount(fmsIds, providerFilter, maintCategoryFilter, maintCodeDescFilter, new ArrayList<String>());
		}catch (Exception ex) {
		throw new MalException("generic.error.occured.while", 
				new String[] { "retrieving the number of maintenance records" }, ex);
		}
	}
	
	/**
	 * Concatenates distinct maintenance categories for a given maintenance request
	 * @param mrqId Maintenance Request Id to retrieve tasks
	 * @Return String of distinct Maintenance Categories with comma between each
	 */
	public String concatMaintenanceCategories(Long mrqId){
		String concatCategoryDescriptions = "";
		List<String> maintenanceCategory = maintenanceRequestTaskDAO.findDistinctCategoryCodes(mrqId);
		
		for(String category : maintenanceCategory){
			if (category != null) {
				if(concatCategoryDescriptions.isEmpty()){
					concatCategoryDescriptions = category;
				}else{
					concatCategoryDescriptions += ", " + category;
				}
			}
		}
		return concatCategoryDescriptions;
	}
	
	/**
	 * Calculates the sum of the recharge total for the provided tasks
	 * @param maintRequestTasks Tasks to sum
	 * @return sum of maintenance request tasks recharge total
	 */
	public BigDecimal sumRechargeTotal(List<MaintenanceRequestTask> maintRequestTasks){
		BigDecimal rechargeTotal = new BigDecimal("0.00");
		for(MaintenanceRequestTask task: maintRequestTasks){
			if(!MALUtilities.isEmpty(task.getRechargeTotalCost())){
				rechargeTotal = rechargeTotal.add(task.getRechargeTotalCost());
			}			
		}
		return rechargeTotal;
	}
	
	/**
	 * Retrieves the vehicle information VO based on the maintenance purchase order
	 * @Param mrqId MaintenanceRequest id
	 * @return VehicleInformationVO Contains time sensitive information about the unit at the time the PO was created.
	 */
	@Transactional
	public VehicleInformationVO getVehicleInformationByMrqId(Long mrqId){		
		VehicleInformationVO vehInfo = null;
		
		try{		
			MaintenanceRequest maintRequest = getMaintenanceRequestByMrqId(mrqId);
			vehInfo = getVehicleInformation(maintRequest.getFleetMaster(), maintRequest.getMaintReqDate());						
		} catch (Exception ex) {
			throw new MalException("generic.error.occured.while", 
					new String[] { "finding a maintenance request by mrqId: " + mrqId }, ex);				
		}				
		return vehInfo;
	}
	
	/**
	 * Retrieves the vehicle information VO based on the maintenance fleet master id
	 * @Param fmsId FleetMaster id
	 * @return VehicleInformationVO Contains current information about the unit.
	 */	
	@Transactional
	public VehicleInformationVO getVehicleInformationByFmsId(Long fmsId){
		VehicleInformationVO vehInfo = null;	
		FleetMaster unit = null;

		try {
			unit = fleetMasterDAO.findById(fmsId).orElse(null);
			vehInfo = getVehicleInformation(unit, Calendar.getInstance().getTime());							
		} catch (Exception ex) {
			throw new MalException("generic.error.occured.while", 
					new String[] { "finding a maintenance request by fmsId: " + fmsId }, ex);				
		}				
		return vehInfo;
	}
	
	/**
	 * Initializes the vehicle information VO based on unit and time
	 * @param unit The vehicle
	 * @param date The date in which information about the unit should be queried.
	 * @return
	 */
	private VehicleInformationVO getVehicleInformation(FleetMaster unit, Date date){
		VehicleInformationVO vehInfo = new VehicleInformationVO();	
		ContractLine contractLine = null;
		VehicleRegistrationV vehicleRegistrationV = null;

		unit = fleetMasterDAO.findById(unit.getFmsId()).orElse(null);
		vehicleRegistrationV = vehicleRegistrationVDAO.findById(unit.getFmsId()).orElse(null);

		vehInfo.setUnitNo(unit.getUnitNo());
		vehInfo.setFmsId(unit.getFmsId());
		vehInfo.setUnitDescription(unit.getModel().getModelDescription());
		vehInfo.setVin(unit.getVin());	
		vehInfo.setLicensePlateNo(MALUtilities.isEmpty(vehicleRegistrationV) ? unit.getRegNo() : vehicleRegistrationV.getLicensePlateNo()); 		
		vehInfo.setClientFleetReferenceNumber(unit.getFleetReferenceNumber());
		
		//Retrieve active or pending live contract, respectively.
		contractLine = contractService.getLastActiveContractLine(unit, Calendar.getInstance().getTime());	
		if(MALUtilities.isEmpty(contractLine)){
			contractLine = contractService.getPendingLiveContractLine(unit, Calendar.getInstance().getTime());
		}
		
		if(contractLine != null){
			vehInfo.setProductType(contractLine.getQuotationModel().getQuotation().getQuotationProfile().getPrdProductCode());
			vehInfo.setDrvId(contractLine.getDriver().getDrvId());
			vehInfo.setDriverCostCenter(MALUtilities.isEmpty(contractLine.getDriver().getDriverCurrentCostCenter()) ? null : contractLine.getDriver().getDriverCurrentCostCenter().getCostCenterCode());			
			vehInfo.setDriverCostCenterName(MALUtilities.isEmpty(contractLine.getDriver().getDriverCurrentCostCenter()) ? null : costCenterService.getCostCenterDescription(contractLine.getDriver().getDriverCurrentCostCenter()));
			
			vehInfo.setDriverForeName(contractLine.getDriver().getDriverForename()); //TODO WARNING May need to get the driver from the allocation records instead
			vehInfo.setDriverSurname(contractLine.getDriver().getDriverSurname());
			vehInfo.setDriverPoolManager(MALUtilities.convertYNToBoolean(contractLine.getDriver().getPoolManager()));
			vehInfo.setClientCorporateId(contractLine.getContract().getExternalAccount().getExternalAccountPK().getCId());			
			vehInfo.setClientAccountNumber(contractLine.getContract().getExternalAccount().getExternalAccountPK().getAccountCode());
			vehInfo.setClientAccountType(contractLine.getContract().getExternalAccount().getExternalAccountPK().getAccountType());			
			vehInfo.setClientAccountName(contractLine.getContract().getExternalAccount().getAccountName());
			vehInfo.setClientTaxIndicator(contractLine.getContract().getExternalAccount().getTaxInd());
			vehInfo.setContractStartDate(contractLine.getStartDate());
			vehInfo.setContractEndDate(contractLine.getEndDate());
			vehInfo.setContractActualEndDate(contractLine.getEndDate());
			vehInfo.setStatus(vehicleStatusVDAO.findById(unit.getFmsId()).orElse(null).getVehicleStatus());
			vehInfo.setClnId(contractLine.getClnId());
			vehInfo.setQmdId(contractLine.getQuotationModel().getQmdId());
		}
		
		//Retrieving the replacement unit info only when the originating unit is on contraact and
		// a replacement unit exists
		if(!MALUtilities.isEmpty(vehInfo.getStatus()) && vehInfo.getStatus().equals(VehicleStatus.STATUS_ON_CONTRACT.getDescription())){
			VehicleReplacementV vehicleReplacementV = vehicleReplacementVDAO.findById(
					new VehicleReplacementVPK(vehInfo.getUnitNo(), vehInfo.getClientCorporateId(), vehInfo.getClientAccountType(), vehInfo.getClientAccountNumber())).orElse(null);		
			if(!MALUtilities.isEmpty(vehicleReplacementV)){			
				vehInfo.setReplacementUnitNo(vehicleReplacementV.getReplacementUnitNo());
				if(!MALUtilities.isEmpty(vehicleReplacementV.getInServiceDate())){
					vehInfo.setReplacementUnitDate(vehicleReplacementV.getInServiceDate());
					vehInfo.setReplacementUnitDateType(VehicleMaintenanceService.DATE_TYPE_IN_SERVICE);
				} else if(!MALUtilities.isEmpty(vehicleReplacementV.getDealerDeliverDate())){
					vehInfo.setReplacementUnitDate(vehicleReplacementV.getDealerDeliverDate());
					vehInfo.setReplacementUnitDateType(VehicleMaintenanceService.DATE_TYPE_DEALER_DELIVERY);				
				} else if(!MALUtilities.isEmpty(vehicleReplacementV.getEtaDate())){
					vehInfo.setReplacementUnitDate(vehicleReplacementV.getEtaDate());
					vehInfo.setReplacementUnitDateType(VehicleMaintenanceService.DATE_TYPE_ETA);					
				} else {
					vehInfo.setReplacementUnitDate(null);
					vehInfo.setReplacementUnitDateType(null);					
				}
			}
		}
		
		vehInfo.setVehicleTechInfo(vehicleTechInfoDAO.findByModelId(unit.getMdlMdlId()));
		vehInfo.setWarrantyUnitLinks(warrantyUnitLinkDAO.findByFmsId(unit.getFmsId()));
		return vehInfo;
	}	
	
	/**
	 * Converts a PO Status code to the respective entity
	 * @param code PO Status Code
	 * @return MaintenanceRequestStatus entity
	 */
	public MaintenanceRequestStatus convertPOStatus(String code){
		List<MaintenanceRequestStatus> statuses = lookupCacheService.getMaintenanceRequestStatuses();
		MaintenanceRequestStatus status = null;
		
		for(MaintenanceRequestStatus mrs : statuses){
			if(mrs.getCode().equals(code)){
				status = mrs;
				break;
			}
		}
		
		return status;
		
	}
	
	/**
	 * Converts a PO Type code to the respective entity
	 * @param cdoe PO Type
	 * @return MaintenanceRequestType entity
	 */
	public MaintenanceRequestType convertPOType(String code){
		
		List<MaintenanceRequestType> types = lookupCacheService.getMaintenanceRequestTypes();
		MaintenanceRequestType type = null;
		
		for(MaintenanceRequestType mrt : types){
			if(mrt.getCode().equals(code)){
				type = mrt;
				break;
			}
		}
		
		return type;				
	}	
		
	/**
	 * Converts a maint recharge code to the respective entity
	 * @param cdoe Maintenance Recharge Code
	 * @return MaintenanceRechargeCode entity
	 */	
	public MaintenanceRechargeCode convertMaintenanceRechargeCode(String code){
		List<MaintenanceRechargeCode> maintRechargeCodes = lookupCacheService.getMaintenanceRechargeCodes();
		MaintenanceRechargeCode maintRechargeCode = null;
		
		for(MaintenanceRechargeCode mrc : maintRechargeCodes){
			if(mrc.getCode().equals(code)){
				maintRechargeCode = mrc;
				break;
			}
		}
		
		return maintRechargeCode;		
	}

	@Override
	public List<ServiceProviderMaintenanceCode> getServiceCodesByCodeOrDesc(String serviceCodeOrDesc, String maintCode, String maintCatCode, Long selectedProviderId){
		List<ServiceProviderMaintenanceCode> serviceProviderMaintenanceCodes = null;
		List<ServiceProviderMaintenanceCode> filteredServiceProviderMaintCodes = new ArrayList<ServiceProviderMaintenanceCode>();

		if(!MALUtilities.isEmptyString(maintCatCode)){
			serviceProviderMaintenanceCodes = getServiceCodesByCategoryCode(maintCatCode);
		}else{
			serviceProviderMaintenanceCodes = lookupCacheService.getServiceProviderMaintenanceCodes();
		}
		
		Long parentProviderId = null;
		ServiceProvider provider = serviceProviderDAO.findById(selectedProviderId).orElse(null);
		if(!MALUtilities.isEmpty(provider.getParentServiceProvider())){
			parentProviderId = provider.getParentServiceProvider().getServiceProviderId();
		}
		
		
		if( !MALUtilities.isEmptyString(serviceCodeOrDesc)){
			for(ServiceProviderMaintenanceCode spmc : serviceProviderMaintenanceCodes){
				if(MALUtilities.isNotEmptyString(spmc.getCode()) && spmc.getCode().toUpperCase().startsWith(serviceCodeOrDesc.toUpperCase()) || spmc.getDescription().toUpperCase().contains(serviceCodeOrDesc.toUpperCase()) ){
					if(MALUtilities.isEmptyString(maintCode)){
						if(spmc.getServiceProvider().getServiceProviderId().equals(selectedProviderId) || spmc.getServiceProvider().getServiceProviderId().equals(parentProviderId) ){				
							if(MALUtilities.isNotEmptyString(spmc.getApprovedBy())){
								filteredServiceProviderMaintCodes.add(spmc);
							}
						}
					}else{
						if(spmc.getMaintenanceCode().getCode().equalsIgnoreCase(maintCode) && (spmc.getServiceProvider().getServiceProviderId().equals(selectedProviderId) || spmc.getServiceProvider().getServiceProviderId().equals(parentProviderId)) ){				
							if(MALUtilities.isNotEmptyString(spmc.getApprovedBy())){
								filteredServiceProviderMaintCodes.add(spmc);
							}
						}
					}
				}
			}
		}else{
			for(ServiceProviderMaintenanceCode spmc : serviceProviderMaintenanceCodes){
				if(MALUtilities.isEmptyString(maintCode)){	
					if(spmc.getServiceProvider().getServiceProviderId().equals(selectedProviderId) || spmc.getServiceProvider().getServiceProviderId().equals(parentProviderId) ){				
						if(MALUtilities.isNotEmptyString(spmc.getApprovedBy())){
							filteredServiceProviderMaintCodes.add(spmc);
						}
					}
				}else{
					if(spmc.getMaintenanceCode().getCode().equalsIgnoreCase(maintCode) && (spmc.getServiceProvider().getServiceProviderId().equals(selectedProviderId) || spmc.getServiceProvider().getServiceProviderId().equals(parentProviderId)) ){				
						if(MALUtilities.isNotEmptyString(spmc.getApprovedBy())){
							filteredServiceProviderMaintCodes.add(spmc);
						}
					}
				}
			}			
		}
		
		return filteredServiceProviderMaintCodes;
	}
		
	/**
	 * Evenly distributes the mark up dollar amount across all PO lines that are flagged to be marked up.
	 * The last mark up line, mark up amount, will be adjusted to correct rounding error.
	 * @param MaintenanceRequest Maintenance Purchase Order
	 * @param BigDecimal Mark up dollar amount 
	 */
//	public MaintenanceRequest applyMarkUp(MaintenanceRequest po, BigDecimal markUp){		
//		BigDecimal lineMarkUp;
//		List<MaintenanceRequestTask> lines;
//		int lineCount = 0;			
//		
//		if(!MALUtilities.isEmpty(po.getMaintenanceRequestTasks())){
//			lines = po.getMaintenanceRequestTasks();			
//			lineCount = po.getMaintenanceRequestTasks().size();
//			
//			if(lineCount > 0){
//				lineMarkUp = markUp.divide(new BigDecimal(lineCount), 2, BigDecimal.ROUND_HALF_UP);
//				
//				for(MaintenanceRequestTask line : lines){					
//					if((po.getMaintenanceRequestTasks().indexOf(line) == po.getMaintenanceRequestTasks().size() - 1)){
//						lineMarkUp = lineMarkUp.add(markUp.subtract(lineMarkUp.multiply(new BigDecimal(lineCount))));									
//					}
//					
//					line.setMarkUpAmount(lineMarkUp);						
//				}				
//				
//			}
//			
//			
//		}		
//		
//		return po;
//	}
	
	
	/**
	 * Sums up the lines' total to determine the PO's subtotal.
	 * The PO subtotal does not include markup.
	 * @param MaintenanceRequest Purchase order
	 * @return BigDecimal PO's subtotal, markup not included
	 */
	@Deprecated
	public BigDecimal calculatePOSubTotal(MaintenanceRequest po){
		BigDecimal subTotal = new BigDecimal(0.00).setScale(2);
		
		for(MaintenanceRequestTask line : po.getMaintenanceRequestTasks()){
			subTotal = subTotal.add(line.getTotalCost().setScale(2, BigDecimal.ROUND_HALF_UP));
		}
		
		return subTotal;
	}
	
	@Deprecated
	public BigDecimal calculateMarkUp(MaintenanceRequest po){
		BigDecimal markupTotal = new BigDecimal(0.00).setScale(2);
		BigDecimal markupPercent = new BigDecimal(Double.valueOf(willowConfigDAO.findById("MAINT_RECHARGE_UPLIFT").orElse(null).getConfigValue()) / 100).setScale(2, BigDecimal.ROUND_HALF_UP).setScale(2);
		BigDecimal markupCap = new BigDecimal(Double.valueOf(willowConfigDAO.findById("MAINT_MARKUP_CAP").orElse(null).getConfigValue())).setScale(2, BigDecimal.ROUND_HALF_UP);
		
		if(!MALUtilities.isEmpty(po.getServiceProvider()) && !MALUtilities.convertYNToBoolean(po.getServiceProvider().getNetworkVendor())){
			markupTotal = calculatePOSubTotal(po).multiply(markupPercent).setScale(2, BigDecimal.ROUND_HALF_UP);
			markupTotal = markupTotal.compareTo(markupCap) > 0 ? markupCap : markupTotal;
		}
		
		return markupTotal;
	}
	
	
	/**
	 * Determines whether a PO line is subject to price Mark Up based on business rules.
	 * @param line
	 * @return
	 */
	public boolean isMarkUpLine(MaintenanceRequestTask line){
		boolean isMarkUp = false;
		if(!MALUtilities.convertYNToBoolean(line.getMaintenanceRequest().getServiceProvider().getNetworkVendor())){
			isMarkUp = true;
		}
		return isMarkUp;
	}
	
	/**
	 * Persists the new MaintenanceRequestTask entity
	 * @param Entity 
	 */	
	@Transactional
	public void saveItemTask(MaintenanceRequestTask maintenanceRequestTask){
		maintenanceRequestTaskDAO.save(maintenanceRequestTask);
	}
	
	/**
	 * Finds the default Maintenance Recharge Flag based on Maintenance Program, Request Type, In-Service Date, and Actual Contract Start Date.
	 * @param maintenanceRequest
	 * @return String Y or N
	 */		
	@Transactional
	public String getDefaultMaintRechargeFlag(MaintenanceRequest maintenanceRequest){
	
		FleetMaster fm = fleetMasterDAO.findById(maintenanceRequest.getFleetMaster().getFmsId()).orElse(null);
		ContractLine contractLine = contractService.getLastActiveContractLine(fm, Calendar.getInstance().getTime());
		String maintRequestType = maintenanceRequest.getMaintReqType();
		String rechargeFlag = "Y";

		try{		
			if (contractLine != null) {
				if (contractLine.getInServDate() != null) {
					if (maintenanceRequest.getActualStartDate() != null){
						if (contractLine.getInServDate().after(maintenanceRequest.getActualStartDate())) {
							return rechargeFlag = "N";
						}
					}
				}
				else {// in service date is null on formal extensions so use original contract
					//check if formal extension. approach taken from quotation_report_data.get_contract_source
					if (contractLine.getContract().getDescription().contains("Formal")) {
						//Check if original contract in service date is after maintenance request actual start date
						ContractLine originalContractLine = contractService.getOriginalContractLine(fm);
						if(originalContractLine != null) {
							if (originalContractLine.getInServDate() != null){
								if (maintenanceRequest.getActualStartDate() != null){
									if (originalContractLine.getInServDate().after(maintenanceRequest.getActualStartDate())){
										return rechargeFlag = "N";
									}
								}
							}
						}
					}
				}

				if (contractLine.getActualEndDate() != null) {
					if (maintenanceRequest.getActualStartDate() != null){
						if (contractLine.getActualEndDate().before(maintenanceRequest.getActualStartDate())) {
							return rechargeFlag = "N";
						}
					}
				}
				//Check if vehicle is on order, if yes re charge flag should be N, as par FM-631
				if(contractLine.getInServDate() == null && contractLine.getStartDate() == null){
					//On order condition
					return rechargeFlag = "N";
				}
				List <MaintenanceProgramVO> maintPrograms = maintenanceProgramDAO.getMaintenanceProgramsByQmdId(contractLine.getQuotationModel().getQmdId(),contractLine.getContract().getExternalAccount().getExternalAccountPK().getCId(), contractLine.getContract().getExternalAccount().getExternalAccountPK().getAccountType(), contractLine.getContract().getExternalAccount().getExternalAccountPK().getAccountCode());
				if (!maintPrograms.isEmpty()) {
					for (MaintenanceProgramVO maintProgram : maintPrograms) {
						if (maintProgram.getElementType().equals("SERVICE") && MAINT_MGMT_ELEMENTS.contains(maintProgram.getElementName())) { 							
							rechargeFlag = "Y";
							break;
						}
						else if (maintProgram.getElementType().equals("RISK_MGT")){
							for (MaintenanceProgramVO maintPrograms2 : maintPrograms) {
								if (maintPrograms2.getElementType().equals("MAINT")){
									if (maintRequestType.equals("RISK_MGMT")){
										rechargeFlag = "Y";
										break;
									}
									else {
										rechargeFlag = "N";
										break;
									}
								}
							}
						}							
						else if (maintProgram.getElementType().equals("MAINT")) { 
							for (MaintenanceProgramVO maintPrograms3 : maintPrograms) {
								if (maintPrograms3.getElementType().equals("RISK_MGT")){
									if (maintRequestType.equals("RISK_MGMT")){
										rechargeFlag = "Y";
										break;
									}
									else {
										rechargeFlag = "N";
										break;
									}
								}
								else {
									rechargeFlag = "N";
								}
							}
						}
					}
					return rechargeFlag;
				}
			}
		} catch(Exception ex) {
			throw new MalException("generic.error.occured.while", 
					new String[] { "getting the default recharge code for maintenanceRequest: " + maintenanceRequest.getMrqId() }, ex);				
		}
		return rechargeFlag;
	}
	
	/**
	 * Finds the default Maintenance Recharge Code based on Maintenance Program, Request Type, and Recharge Flag.
	 * @param maintenanceRequest
	 * @return MaintenanceRechargeCode
	 */	
	@Transactional
	public MaintenanceRechargeCode getDefaultMaintRechargeCode(MaintenanceRequest maintenanceRequest){
		
		MaintenanceRechargeCode rechargeCode = null;
		FleetMaster fm = fleetMasterDAO.findById(maintenanceRequest.getFleetMaster().getFmsId()).orElse(null);
		String maintRequestType = maintenanceRequest.getMaintReqType();
		
		try{
		
			ContractLine contractLine = contractService.getLastActiveContractLine(fm, Calendar.getInstance().getTime());
			if(MALUtilities.isEmpty(contractLine)){
				contractLine = contractService.getPendingLiveContractLine(fm, Calendar.getInstance().getTime());
			}
			if(contractLine != null){
				
				List<MaintenanceProgramVO> maintPrograms = maintenanceProgramDAO.getMaintenanceProgramsByQmdId(contractLine.getQuotationModel().getQmdId(),contractLine.getContract().getExternalAccount().getExternalAccountPK().getCId(), contractLine.getContract().getExternalAccount().getExternalAccountPK().getAccountType(), contractLine.getContract().getExternalAccount().getExternalAccountPK().getAccountCode());
	
				if (!maintPrograms.isEmpty() && getDefaultMaintRechargeFlag(maintenanceRequest).equals("Y")) {
					for (MaintenanceProgramVO maintProgram : maintPrograms) {
						if (maintProgram.getElementType().equals("SERVICE") && MAINT_MGMT_ELEMENTS.contains(maintProgram.getElementName()) && maintRequestType.equals("MAINT")) { 
							rechargeCode = convertMaintenanceRechargeCode("MAINT_MGT"); //Maintenance management rebill
							break;
						}
						else if (maintProgram.getElementType().equals("MAINT") && maintRequestType.equals("MAINT")) { 
							rechargeCode = convertMaintenanceRechargeCode("NON_COVER"); //'Not covered by maintenance contract'
							break;
						}
						else if (maintProgram.getElementType().equals("RISK_MGT") && maintRequestType.equals("RISK_MGMT")){ 
							rechargeCode = convertMaintenanceRechargeCode("RISK_MGMT"); //'Incident / Rsk Mgmt repair and / or services rebill'
							break;
						}
						else {  
							rechargeCode = convertMaintenanceRechargeCode("NON_COVER"); //'Not covered by maintenance contract'
						}
					}
				}
				else if (maintPrograms.isEmpty() && getDefaultMaintRechargeFlag(maintenanceRequest).equals("Y")) {
					rechargeCode = convertMaintenanceRechargeCode("NON_COVER");
				}				
			}
		} catch(Exception ex) {
			throw new MalException("generic.error.occured.while", 
					new String[] { "getting the default recharge code for maintenanceRequest: " + maintenanceRequest.getMrqId() }, ex);				
		}
		
		return rechargeCode;
	}
	
	@Override
	public List<MaintenanceCode> getMaintenanceCodesByCategoryCode(
			String categoryCode) {
		// TODO Do we want to use something like Lamdaj, Hamcrest, Guava or Apache Commons Collections in the future?? 
		// Or at least introduce a Predicate interface and Utility class of our own?
		ArrayList<MaintenanceCode> codes = new ArrayList<MaintenanceCode>();
		for(MaintenanceCode code : lookupCacheService.getMaintenanceCodes()){
			if(code.getMaintCatCode().equalsIgnoreCase(categoryCode)){
				codes.add(code);
			}
		}

		return codes;
	}
	
	@Override
	public List<ServiceProviderMaintenanceCode> getServiceCodesByCategoryCode(
			String categoryCode) {

		ArrayList<ServiceProviderMaintenanceCode> codes = new ArrayList<ServiceProviderMaintenanceCode>();
		for(ServiceProviderMaintenanceCode code : lookupCacheService.getServiceProviderMaintenanceCodes()){
			if(code.getMaintenanceCode().getMaintCatCode().equalsIgnoreCase(categoryCode)){
				codes.add(code);
			}
		}

		return codes;
	}
	
	@Override
	public List<MaintenanceCode> getMaintenanceCodesByNameOrCode(
			String nameOrCode, String categoryCode) {
		
		ArrayList<MaintenanceCode> codes = new ArrayList<MaintenanceCode>();
		for(MaintenanceCode code : this.getMaintenanceCodesByCategoryCode(categoryCode)){
			if(code.getCode().toUpperCase().startsWith(nameOrCode.toUpperCase()) || code.getDescription().toUpperCase().contains(nameOrCode.toUpperCase())){
				codes.add(code);
			}
		}

		return codes;
	}

	@Override
	public List<MaintenanceCode> getMaintenanceCodesByNameOrCode(
			String nameOrCode) {
		
		ArrayList<MaintenanceCode> codes = new ArrayList<MaintenanceCode>();
		for(MaintenanceCode code : lookupCacheService.getMaintenanceCodes()){
			if(code.getCode().toUpperCase().startsWith(nameOrCode.toUpperCase()) || code.getDescription().toUpperCase().contains(nameOrCode.toUpperCase())){
				codes.add(code);
			}
		}

		return codes;
	}
	
	/**
	 * Based on the corporate entity, the maintenance PO's next job number is retrieved from the database.
	 * @param CorporateEntity Corporate entity
	 */
	@Transactional
	public String generateJobNumber(CorporateEntity corporateEntity){
		DocumentNumber docNo = null;
		String jobNumber = null;

		docNo = documentNumberDAO.findById(new DocumentNumberPK(corporateEntity.getCorpId(), DocumentNumberDAO.DOMAIN_FLEET_MAINTENANCE)).orElse(null);		
	
		jobNumber = docNo.getPreFix() + String.format("%08d", docNo.getNextNo());
		
		docNo.setNextNo(docNo.getNextNo() + 1);
		docNo = documentNumberDAO.saveAndFlush(docNo);	
		
		return jobNumber;
	}
	
	/**
	 * This method determines whether Maintenance PO is modified or not. 
	 * @return boolean
	 */
	@Override
	public boolean isMaintenancePOModified(MaintenanceRequest originalMaintenanceRequest, MaintenanceRequest modifiedMaintenanceRequest){
		
		if (!originalMaintenanceRequest.equals(modifiedMaintenanceRequest)) {
			return true;
		}
		
		if (isTaskItemListModified(originalMaintenanceRequest.getMaintenanceRequestTasks(), modifiedMaintenanceRequest.getMaintenanceRequestTasks())) {
			return true;
		}
		
		return false;
	}
	
	
	/**
	 * This method determines whether Task Items List modified. 
	 * @return boolean
	 */
	@Override
	public boolean isTaskItemListModified(List<MaintenanceRequestTask> originalRequestTasks, List<MaintenanceRequestTask> modifiedRequestTasks){
		
		if(modifiedRequestTasks.size() != originalRequestTasks.size()) return true;

		for(MaintenanceRequestTask maintenanceRequestTask : modifiedRequestTasks){
			for(MaintenanceRequestTask originalTask : originalRequestTasks){
				if(MALUtilities.isEmpty(maintenanceRequestTask.getMrtId())){
					return true;
				}else{
					if(maintenanceRequestTask.getMrtId().longValue() == originalTask.getMrtId().longValue()){
						if(maintenanceRequestTask.equals(originalTask) == false
								|| maintenanceRequestTask.isCostAvoidanceIndicator() != originalTask.isCostAvoidanceIndicator()
								|| maintenanceCateogoryPropertyValuesListCompare(maintenanceRequestTask,originalTask)){
							return true;
						}
						break;
					}
				}
			}
		}
		return false;
	}
	
	public boolean maintenanceCateogoryPropertyValuesListCompare(MaintenanceRequestTask modifiedMaintenanceRequestTask, MaintenanceRequestTask originalMaintenanceRequestTask){
		List<MaintenanceCategoryPropertyValue> modifiedMaintCatPropertyValueList = modifiedMaintenanceRequestTask.getMaintenanceCategoryPropertyValues();
		List<MaintenanceCategoryPropertyValue> originalMaintCatPropertyValueList = maintenanceCategoryPropertyValueDAO.findByIdAndMaintenanceCategoryCode(originalMaintenanceRequestTask.getMrtId(), originalMaintenanceRequestTask.getMaintCatCode());
		
		if(!MALUtilities.isEmpty(originalMaintCatPropertyValueList) && !MALUtilities.isEmpty(modifiedMaintCatPropertyValueList)){
			if (originalMaintCatPropertyValueList.size() == 0 && modifiedMaintCatPropertyValueList.size() != 0){
				return true;
			}
			
			//Use removeAll and compare list sizes to determine if updates were made.  If no changes were made, originalMaintCatPropertyValueList.size becomes equal to 0.
			originalMaintCatPropertyValueList.removeAll(modifiedMaintCatPropertyValueList);
			
			if (modifiedMaintCatPropertyValueList.size() != 0 && originalMaintCatPropertyValueList.size() != 0) {
	        	return true;
	        }
		}else if(MALUtilities.isEmpty(originalMaintCatPropertyValueList) && !MALUtilities.isEmpty(modifiedMaintCatPropertyValueList)){
			return true;
		}
      
		return false;
		
	}
	
	/**
	 * This method update the authorize person if the task item is modified. 
	 * @return boolean
	 */
	@Override
	@Deprecated
	public void updateModifiedTaskItems(List<MaintenanceRequestTask> originalRequestTasks, List<MaintenanceRequestTask> modifiedRequestTasks, String logedInUser){					
		List<MaintenanceRequestTask> removeRequestTasks = new ArrayList<MaintenanceRequestTask>();
		
		for(MaintenanceRequestTask maintenanceRequestTask : modifiedRequestTasks){
			for(MaintenanceRequestTask originalTask : originalRequestTasks){
				if(!MALUtilities.isEmpty(maintenanceRequestTask.getMrtId()) && !MALUtilities.isEmpty(originalTask.getMrtId())){
					if(maintenanceRequestTask.getMrtId().longValue() == originalTask.getMrtId().longValue()){
						if(maintenanceRequestTask.equals(originalTask) == false){
							originalTask.setDiscountFlag(maintenanceRequestTask.getDiscountFlag());
							originalTask.setMaintCatCode(maintenanceRequestTask.getMaintCatCode());
							originalTask.setMaintenanceCode(maintenanceRequestTask.getMaintenanceCode());
							originalTask.setMaintenanceCodeDesc(maintenanceRequestTask.getMaintenanceCodeDesc());
							originalTask.setRechargeCode(maintenanceRequestTask.getRechargeCode());
							originalTask.setRechargeFlag(maintenanceRequestTask.getRechargeFlag());
							originalTask.setTaskQty(maintenanceRequestTask.getTaskQty());
							originalTask.setUnitCost(maintenanceRequestTask.getUnitCost());
							originalTask.setTotalCost(maintenanceRequestTask.getTotalCost());
							originalTask.setServiceProviderMaintenanceCode(maintenanceRequestTask.getServiceProviderMaintenanceCode());
							originalTask.setAuthorizePerson(logedInUser);
						}else{
							originalTask.setAuthorizePerson(originalTask.getAuthorizePerson());
						}
					}
				}
			}
		}
		
		for(MaintenanceRequestTask originalTask : originalRequestTasks){
			if(MALUtilities.isEmpty(originalTask.getMrtId())){
				removeRequestTasks.add(originalTask);
			}
		}
		
		for(MaintenanceRequestTask originalTask : originalRequestTasks){
			if(!MALUtilities.isEmpty(originalTask.getMrtId())){
				removeRequestTasks.add(originalTask);
				for(MaintenanceRequestTask modifiedTask : modifiedRequestTasks){
					if(originalTask.getMrtId() == modifiedTask.getMrtId()){
						removeRequestTasks.remove(originalTask); //Task found so delete from "removelist"
					}
				}
			}
		}
		
		for(MaintenanceRequestTask removeTask : removeRequestTasks){
			originalRequestTasks.remove(removeTask);
		}
		
		for(MaintenanceRequestTask maintenanceRequestTask : modifiedRequestTasks){
			if(MALUtilities.isEmpty(maintenanceRequestTask.getMrtId())){
				originalRequestTasks.add(maintenanceRequestTask);
			}
		}
	}
	
	public MaintenanceRequestTask copyTaskItem(MaintenanceRequestTask taskItem){
    	//MaintenanceRequestTask copiedTaskItem = new MaintenanceRequestTask(taskItem);
		MaintenanceRequestTask copiedTaskItem = new MaintenanceRequestTask();
		BeanUtils.copyProperties(taskItem, copiedTaskItem, new String[]{"maintenanceRequest", "versionts"});
		copiedTaskItem.setMaintenanceRequest(copyMaintenanceRequest(taskItem.getMaintenanceRequest()));
    	return copiedTaskItem;
    }
	
	public MaintenanceRequest copyMaintenanceRequest(MaintenanceRequest maintenanceRequest){
		MaintenanceRequest copyOfMaintenanceRequest = new MaintenanceRequest();
		MaintenanceRequestTask copyMaintenanceRequestTask = null;
		
		//Shallow Copy only
		BeanUtils.copyProperties(maintenanceRequest, copyOfMaintenanceRequest, new String[]{"serviceProvider","maintenanceRequestTasks", "maintenanceCategoryPropertyValues", "versionts"});
		//Now copy service provider in separate to avoid deep cloning effect on service provider
		copyOfMaintenanceRequest.setServiceProvider(serviceProviderDAO.findById(maintenanceRequest.getServiceProvider().getServiceProviderId()).orElse(null));
		//Create new list for tasks since it was not cloned
		copyOfMaintenanceRequest.setMaintenanceRequestTasks(new ArrayList<MaintenanceRequestTask>());
		
		for(MaintenanceRequestTask task : maintenanceRequest.getMaintenanceRequestTasks()){
			copyMaintenanceRequestTask = new MaintenanceRequestTask();
			
			BeanUtils.copyProperties(task, copyMaintenanceRequestTask, new String[]{"maintenanceRequest", "versionts"});
//TODO Thoroughly test this as we are not directly copying the task from the maintenance request that was passed in. Instead, we are retrieving the MRT directly from the database.			
			copyMaintenanceRequestTask = maintRequestService.intializeMaintenanceCateogryProperties(copyMaintenanceRequestTask);		
			
			copyOfMaintenanceRequest.getMaintenanceRequestTasks().add(copyMaintenanceRequestTask);
		
		}
		return copyOfMaintenanceRequest;
	}
			
	public List<FleetNotes> getFleetNotesByMaintReqId(Long maintenanceRequestId) {
		return fleetNotesDAO.findByMaintenanceRequestId(maintenanceRequestId);
	}		
	
		
	/**
	 * Returns all the region for which a client has tax exempted
	 * @param cid, accountType and accountCode
	 * @return List of RegionCode
	 */
	@Transactional(readOnly = true)
	public List<RegionCode> getTaxExemptedRegions(Long cid, String accountType, String accountCode){
		List<RegionCode> regions = null;
		try{
			List<ExtAccTaxExempt> taxExemptedList = extAccTaxExemptDAO.getExtAccTaxExemptsByAccount(cid, accountType, accountCode);
			if(taxExemptedList != null && taxExemptedList.size() > 0){
				regions = new ArrayList<RegionCode>();
				for(ExtAccTaxExempt extAccTaxExempt : taxExemptedList){
					regions.add(extAccTaxExempt.getRegionCode());
				}
			}
		}catch(Exception ex){
			throw new MalException("generic.error.occured.while", new String[] { "finding a tax exempted regions" }, ex);	
		}
		
		return regions;
	}
	
	/**
	 * Get maintenance account preferences 
	 * @param fm
	 * @return MaintenancePreferenceAccount
	 */	
	@Transactional(readOnly = true)	
	public MaintenancePreferenceAccount getMaintenancePreferenceAccount(VehicleInformationVO vehicleInfo){
		MaintenancePreferenceAccount maintPreferenceAccount = null;
				
		try{
			maintPreferenceAccount = maintenancePreferenceAccountDAO.getMaintenancePreferenceAccountData(vehicleInfo.getClientCorporateId(), vehicleInfo.getClientAccountType(), vehicleInfo.getClientAccountNumber());
		} catch(Exception ex) {
			throw new MalException("generic.error.occured.while", 
					new String[] { "getting maintenance account preferences for unit:" +  vehicleInfo.getUnitNo()}, ex);				
		}
		
		return maintPreferenceAccount;		
	}
	
	/**
	 * Get maintenance programs 
	 * @param fm
	 * @return List of MaintenanceProgramVO
	 */	
	@Transactional(readOnly = true)
	public List<MaintenanceProgramVO> getMaintenancePrograms(VehicleInformationVO vehicleInfo){
		List<MaintenanceProgramVO> maintPrograms = null;
		
		try{
			maintPrograms = maintenanceProgramDAO.getMaintenanceProgramsByQmdId(vehicleInfo.getQmdId(),vehicleInfo.getClientCorporateId(), vehicleInfo.getClientAccountType(), vehicleInfo.getClientAccountNumber());
		} catch(Exception ex) {
			throw new MalException("generic.error.occured.while", 
					new String[] { "getting maintenance programs for unit:" +  vehicleInfo.getUnitNo()}, ex);				
		}
		return maintPrograms;
	}
	/**
	 * This method will return true if the vehicle has maintenance programs that are part of 
	 * FULL_MAINTENANCE, BUDGETED_MAINTENANCE, MAINT_MGMT or RISK_MGMT;
	 * If the vehicle has any of the following NATL_ACCT, NAP, or PREV_MAINT maintenance program only
	 *  then it should return false
	 * @param vehicleInfo
	 * @return boolean value.  
	 */
	@Transactional(readOnly = true)
	public boolean isMaintenanceProgramsForFee(VehicleInformationVO vehicleInfo){
		List<MulQuoteEle> quoteElementList = new ArrayList<MulQuoteEle>();
		
		try{
			if (!MALUtilities.isEmpty(vehicleInfo.getQmdId())) {
				quoteElementList = mulQuoteEleDAO.findMulQuoteEleByQuotationId(quotationModelDAO.findById(vehicleInfo.getQmdId()).orElse(null).getQuotation().getQuoId());
				if(!MALUtilities.isEmpty(quoteElementList) && !quoteElementList.isEmpty() ){
					for(MulQuoteEle quoteElement : quoteElementList){
						if(quoteElement.getSelectedInd().equalsIgnoreCase("Y")){
							if((MAINT_MGMT_ELEMENTS.contains(quoteElement.getLeaseElement().getElementName()) && !quoteElement.getLeaseElement().getElementName().equals("NATL_ACCT") && !quoteElement.getLeaseElement().getElementName().equals("NAP") && !quoteElement.getLeaseElement().getElementName().equals("PREV_MAINT")) 
									|| RISK_MGMT_ELEMENTS.contains(quoteElement.getLeaseElement().getElementName()) 
									|| BUDGETED_MAINTENANCE.contains(quoteElement.getLeaseElement().getElementName()) 
									|| FULL_MAINTENANCE.contains(quoteElement.getLeaseElement().getElementName())){
								return true;
							}
						}
					}
				}
			} else {
				return false;
			}
		}catch(Exception ex) {
			throw new MalException("generic.error.occured.while", 
					new String[] { "determining if unit " +  vehicleInfo.getUnitNo() + " has the specified maintenance programs"}, ex);				
		}
		return false;
	}
	/**
	 * HPS-2946
	 */
	@Transactional(readOnly = true)
	public boolean isBudgetForFee(VehicleInformationVO vehicleInfo){
		List<MulQuoteEle> quoteElementList = new ArrayList<MulQuoteEle>();		
		try{
			if (!MALUtilities.isEmpty(vehicleInfo.getQmdId())) {
				quoteElementList = mulQuoteEleDAO.findMulQuoteEleByQuotationId(quotationModelDAO.findById(vehicleInfo.getQmdId()).orElse(null).getQuotation().getQuoId());
				if(!MALUtilities.isEmpty(quoteElementList) && !quoteElementList.isEmpty() ){
					for(MulQuoteEle quoteElement : quoteElementList){
						if(quoteElement.getSelectedInd().equalsIgnoreCase("Y")){
							if(BUDGETED_MAINTENANCE.contains(quoteElement.getLeaseElement().getElementName())){
								return true;
							}
						}
					}
				}
			} else {
				return false;
			}
		}catch(Exception ex) {
			throw new MalException("generic.error.occured.while", 
					new String[] { "determining if unit " +  vehicleInfo.getUnitNo() + " has the specified maintenance programs"}, ex);				
		}
		return false;
	}
	
	
	/**
	 * Get maintenance preferences 
	 * @param fm
	 * @return List of MaintenancePreferencesVO
	 */	
	@Transactional(readOnly = true)
	public List<MaintenancePreferencesVO> getMaintenancePreferences(VehicleInformationVO vehicleInfo){
		List<MaintenancePreferencesVO> maintPreferences = null;
	
		try{
			maintPreferences = maintenancePreferencesDAO.getMaintenancePreferencesByAccount(vehicleInfo.getClientCorporateId(), vehicleInfo.getClientAccountType(), vehicleInfo.getClientAccountNumber());
		} catch(Exception ex) {
			throw new MalException("generic.error.occured.while", 
					new String[] { "getting maintenance preferences for unit:" +  vehicleInfo.getUnitNo()}, ex);				
		}
		return maintPreferences;
	}	
	
		
	// The specific business rules for this email is to use the selected contact
	// as the To address and the rest as the Cc; I am returning a Map so I can pass back both list
	// per the business ruless
	public Map<String,List<EmailAddress>> getContactEmailAddresses(FleetMaster fm, Long selectedContactId) {
		Map<String,List<EmailAddress>> addresses = new HashMap<String, List<EmailAddress>>();
		
		List<EmailAddress> toEmails = new ArrayList<EmailAddress>();
		List<EmailAddress> ccEmails = new ArrayList<EmailAddress>();
		if(!MALUtilities.isEmpty(fm)){
			VehicleInformationVO vehicleInfo = getVehicleInformationByFmsId(fm.getFmsId());
			
			List<MaintenanceContactsVO> maintContacts;
			try {
				maintContacts = maintRequestService.getContacts(ClientPOCService.POC_NAME_MAINT_EXCEED_AUTH_LIMIT, vehicleInfo);
				
				for(MaintenanceContactsVO maintContact : maintContacts){
					if(maintContact.getContactId() == selectedContactId){
						toEmails.add(new EmailAddress(maintContact.getEmail()));
					}else{
						ccEmails.add(new EmailAddress(maintContact.getEmail()));
					}
				}
				
				addresses.put(TO_EMAIL_LIST , toEmails);
				addresses.put(CC_EMAIL_LIST , ccEmails);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return addresses;
	}


	@Override
	public String generateExceededAuthMsg(VehicleInformationVO vehicleInfo) throws MalBusinessException {		
		Map<String,Object> data = new HashMap<String,Object>();

		List<MaintenanceContactsVO> maintContacts = maintRequestService.getContacts(ClientPOCService.POC_NAME_MAINT_EXCEED_AUTH_LIMIT, vehicleInfo);
		data.put("contacts", maintContacts);

		return velocityService.getMergedTemplate(data, exceededAuthLimitMessagePath);
	}

	@Override
	public String generateExceededAuthEmailSubject(FleetMaster fm) {
		Map<String,Object> data = new HashMap<String,Object>();
		data.put("vehicle", fm);
			
		return velocityService.getMergedTemplate(data, exceededAuthLimitSubjectPath);
	}
	
	@Override
	public String generateExceededAuthEmailBody(MaintenanceRequest req) {
		SimpleDateFormat dateToString = new SimpleDateFormat("MM/dd/yyyy");
		VehicleInformationVO vehicleInfo = this.getVehicleInformationByFmsId(req.getFleetMaster().getFmsId());
		BigDecimal rechargeTotal = maintRequestService.sumRechargeTotal(req);
		//for FM-1155
		BigDecimal	costAvoidanceTotal	=	maintRequestService.sumCostAvoidanceTotal(req);
		// HD-340
		MaintenanceRequest latestMaintRequestProcessed = maintenanceRequestDAO.findLatestMaintenanceRequestByFmsId(req.getFleetMaster().getFmsId());
		
		Map<String,Object> data = new HashMap<String,Object>();
		data.put("number", new NumberTool());
		data.put("maintenanceRequest", req);
		data.put("rechargeTotal", rechargeTotal);
		//for FM-1155
		data.put("costAvoidanceTotal", costAvoidanceTotal);
		data.put("UOM", req.getUnitofMeasureCode().getDescription());
		data.put("driverForename", vehicleInfo.getDriverForeName());
		data.put("driverSurname", vehicleInfo.getDriverSurname());
		
		data.put("costCenterCode",vehicleInfo.getDriverCostCenter());
		data.put("costCenterDesc",vehicleInfo.getDriverCostCenterName());

		if(!MALUtilities.isEmptyString(req.getServiceProviderContactInfo())){
			data.put("serviceProviderAddress", req.getServiceProviderContactInfo());
		}else if(!MALUtilities.isEmpty(req.getServiceProvider()) && !MALUtilities.isEmpty(req.getServiceProvider().getServiceProviderAddresses()) && req.getServiceProvider().getServiceProviderAddresses().size() > 0){
			String address = null;
			for(ServiceProviderAddress serviceProviderAddress : req.getServiceProvider().getServiceProviderAddresses()){
				if(serviceProviderAddress.getDefaultInd() != null && serviceProviderAddress.getDefaultInd().equals("Y")){
					address = maintRequestService.getServiceProviderFormattedAddress(serviceProviderAddress);
					break;
				}
			}			
			if(!MALUtilities.isEmptyString(address)){
				data.put("serviceProviderAddress", address);
			}
		}
		BigDecimal markupTotal = maintRequestService.calculateNonNetworkRechargeMarkup(req);
		data.put("markupTotal", markupTotal);
		if(latestMaintRequestProcessed != null){
			data.put("latestProcessedMaintRequestOdo",latestMaintRequestProcessed.getCurrentOdo());
			data.put("latestProcessedMaintReqStartDate",latestMaintRequestProcessed.getActualStartDate() != null ? dateToString.format(latestMaintRequestProcessed.getActualStartDate()) : "");
			data.put("latestProcessedMaintRequestUMO",latestMaintRequestProcessed.getUnitofMeasureCode().getDescription());
		}else{
			data.put("latestProcessedMaintRequestOdo",0);
			data.put("latestProcessedMaintReqStartDate","");
			data.put("latestProcessedMaintRequestUMO","");
		}
		return velocityService.getMergedTemplate(data, exceededAuthLimitBodyPath);
	}
	
	@Override
	public String generateExceededAuthSummary(CorporateEntity corpEntity,
			String accountType, String accountCode, FleetMaster fm) {
		String summary = null;
		Map<String,Object> data = new HashMap<String,Object>();
		data.put("number", new NumberTool());
    	BigDecimal mafsAuthLimit;
		try {
			mafsAuthLimit = quotationService.getMafsAuthorizationLimit(corpEntity.getCorpId(),accountType,accountCode,fm.getUnitNo());
	    	System.out.println("mafsAuthLimit"+ mafsAuthLimit);
			if(!MALUtilities.isEmpty(mafsAuthLimit)){
				data.put("mafsAuthLimit",mafsAuthLimit);
				summary = velocityService.getMergedTemplate(data, exceededAuthLimitSummaryPath);
	    	}
		} catch (MalBusinessException e) {
			
			throw new MalException("generic.error.occured.while", 
					new String[] { "getting authorization limit for the customer:" +  accountCode}, e);			
		}

		return summary;
	}

	@Override
	@Transactional(readOnly=true)
	public Email generateExceededAuthEmail(MaintenanceRequest req,Long selectedContactId) {
		Map<String,List<EmailAddress>> addresses = this.getContactEmailAddresses(req.getFleetMaster(),selectedContactId);
		if(!MALUtilities.isEmpty(req.getFleetMaster())){
			this.exceededAuthLimitEmail.setTo(addresses.get(TO_EMAIL_LIST));
			this.exceededAuthLimitEmail.setCc(addresses.get(CC_EMAIL_LIST));
			this.exceededAuthLimitEmail.setSubject(this.generateExceededAuthEmailSubject(req.getFleetMaster()));
			this.exceededAuthLimitEmail.setMessage(this.generateExceededAuthEmailBody(req));
		}
		return this.exceededAuthLimitEmail;
	}
	
	public String	getClientScheduleTypeCode(Long clientScheduleId){
		if(clientScheduleId != null){
			ClientScheduleType	clientScheduleType = clientScheduleTypeDAO.findById(clientScheduleId).orElse(null);
			return clientScheduleType != null ? clientScheduleType.getScheduleType() : null;			
		}else{
			return null;
		}
		
	}
	
	@Override
	public String findElementOnQuote(Long qmdId, Long lelId) {		
		return maintenanceProgramDAO.findElementOnQuote(qmdId, lelId);
	}
	
	@Override
	public Long getContractLinesfromfmsId(Long fmsId) {
		return maintenanceProgramDAO.getContractLinesfromfmsId(fmsId);	
	}

	@Override
	public Long getQmdIdfromClnId(Long clnId) {
		return maintenanceProgramDAO.getQmdIdfromClnId(clnId);
	}

	@Override
	public Long getClnIdforDisposedUnit(Long fmsId) {
		return maintenanceProgramDAO.getClnIdforDisposedUnit(fmsId);
	}

	@Override
	public boolean validationCheckForInformalUnit(Long qmdId) {
		return maintenanceProgramDAO.validationCheckForInformalUnit(qmdId);
	}
	@Override
	public int getleaseElementbyFmdId(Long qmdId) {
		return maintenanceProgramDAO.getleaseElementbyFmdId(qmdId);
	}
	@Override
	public Long getClnIdforReleaseUnit(Long fmsId){
		return maintenanceProgramDAO.getClnIdforReleaseUnit(fmsId);
	}
	
}
