package com.mikealbert.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mikealbert.data.dao.FleetMasterDAO;
import com.mikealbert.data.dao.MaintenanceInvoiceDAO;
import com.mikealbert.data.dao.MaintenanceRequestDAO;
import com.mikealbert.data.dao.MaintenanceRequestTaskDAO;
import com.mikealbert.data.dao.ServiceProviderAddressDAO;
import com.mikealbert.data.dao.ServiceProviderDAO;
import com.mikealbert.data.entity.MaintenanceCode;
import com.mikealbert.data.vo.MaintenanceCodeVO;
import com.mikealbert.data.vo.MaintenanceServiceHistoryVO;
import com.mikealbert.data.vo.InvoiceDateAndNumberVO;
import com.mikealbert.data.vo.ServiceProviderAddressVO;
import com.mikealbert.exception.MalException;
import com.mikealbert.util.MALUtilities;

@Service("maintenanceHistoryService")
public class MaintenanceHistoryServiceImpl implements MaintenanceHistoryService {
	
	@Resource FleetMasterDAO fleetMasterDAO;
	@Resource MaintenanceRequestDAO maintenanceRequestDAO;
	@Resource MaintenanceRequestTaskDAO maintenanceRequestTaskDAO;
	@Resource MaintenanceInvoiceDAO maintenanceInvoiceDAO;
	@Resource ServiceProviderDAO serviceProviderDAO;
	@Resource MaintenanceRequestService maintenanceRequestService;
	@Resource ServiceProviderAddressDAO serviceProviderAddressDAO;
	@Resource WillowConfigService willowConfigService;
	
	public List<MaintenanceServiceHistoryVO> getMaintenanceServiceHistoryByVIN(String vin, Pageable pageable , Sort sort, String providerFilter, String maintCategoryFilter, String maintCodeDescFilter){
		List<MaintenanceServiceHistoryVO> purchaseOrdersVO = new ArrayList<MaintenanceServiceHistoryVO>();
		List<Long> fmsIds = new ArrayList<Long>();		
		fmsIds = fleetMasterDAO.findFmsIdsByVIN(vin);	
		purchaseOrdersVO = getMaintenanceServiceHistory(fmsIds, pageable, sort, providerFilter, maintCategoryFilter, maintCodeDescFilter);		
		return purchaseOrdersVO;
	}
	
	public List<MaintenanceServiceHistoryVO> getMaintenanceServiceHistoryByFmsId(Long fmsId, Pageable pageable , Sort sort, String providerFilter, String maintCategoryFilter, String maintCodeDescFilter){
		List<MaintenanceServiceHistoryVO> purchaseOrdersVO = new ArrayList<MaintenanceServiceHistoryVO>();
		List<Long> fmsIds = new ArrayList<Long>();
		fmsIds.add(fmsId);
		purchaseOrdersVO = getMaintenanceServiceHistory(fmsIds, pageable, sort, providerFilter, maintCategoryFilter, maintCodeDescFilter);		
		return purchaseOrdersVO;
	}	
		
	public int getMaintenanceServiceHistoryByVINCount(String vin, String providerFilter, String maintCategoryFilter, String maintCodeDescFilter){
		List<Long>fmsIds = fleetMasterDAO.findFmsIdsByVIN(vin);
		return getMaintenanceServiceHistoryCount(fmsIds, providerFilter, maintCategoryFilter, maintCodeDescFilter);
	}
	
	public int getMaintenanceServiceHistoryByFmsIdCount(Long fmsId, String providerFilter, String maintCategoryFilter, String maintCodeDescFilter){
		List<Long> fmsIds = new ArrayList<Long>();
		fmsIds.add(fmsId);
		return getMaintenanceServiceHistoryCount(fmsIds, providerFilter, maintCategoryFilter, maintCodeDescFilter);		
	}
	
	
	/**
	 * Retrieves maintenance service history information based on the passed-in VIN;
	 * VIN is used because one vin can have multiple fleetMaster records;
	 * @vin VIN used to find vehicle
	 * @return MaintenanceServiceHistory View Object with data populated
	 */
	@Transactional
	private List<MaintenanceServiceHistoryVO> getMaintenanceServiceHistory(List<Long> fmsIds, Pageable pageable , Sort sort, String providerFilter, String maintCategoryFilter, String maintCodeDescFilter){
		String serviceProviderDetail = null;
		List<MaintenanceServiceHistoryVO> purchaseOrdersVO = new ArrayList<MaintenanceServiceHistoryVO>();
		List<ServiceProviderAddressVO> serviceProviderAddressList = new ArrayList<ServiceProviderAddressVO>();
		List<BigDecimal> serviceProviderIds = new ArrayList<BigDecimal>();
		List<Long> mrqIdList = new ArrayList<Long>();
		List<MaintenanceCodeVO> maintenanceCodeVOList = new ArrayList<MaintenanceCodeVO>();
		List<String> excludeMaintCodesFromPOTotal = new ArrayList<String>();
 		//List<Long> fmsIds = new ArrayList<Long>();
		try{
			//Get fmsIds for the vin
			//fmsIds = fleetMasterDAO.findFmsIdsByVIN(vin);
			
			//Get List of maintenance codes that are not paid to vendor and only charged to the customer. Does not exist on PORDER or INVOICEAP. Does exist on INVOICEAR.
			excludeMaintCodesFromPOTotal.add(willowConfigService.getConfigValue("MAINT_NON_NETWORK_CODE"));
			excludeMaintCodesFromPOTotal.add(willowConfigService.getConfigValue("MAINT_RENTAL_FEE_CODE"));
			
			//Get purchase orders based on fmsIds
			purchaseOrdersVO = maintenanceRequestDAO.getMaintenanceServiceHistory(fmsIds, pageable, sort, providerFilter, maintCategoryFilter, maintCodeDescFilter, excludeMaintCodesFromPOTotal);
			
			if(purchaseOrdersVO != null && !purchaseOrdersVO.isEmpty()){
				serviceProviderIds = getServiceProviderIds(purchaseOrdersVO);
				serviceProviderAddressList = serviceProviderAddressDAO.findServiceProviderAddressesByListOfIds(serviceProviderIds);
				mrqIdList = getMrqIds(purchaseOrdersVO);
				maintenanceCodeVOList = getPopupMaintenanceCodes(mrqIdList);
			}
			
			for(MaintenanceServiceHistoryVO purchaseOrderVO: purchaseOrdersVO){

				InvoiceDateAndNumberVO payeeInvoiceVO = (InvoiceDateAndNumberVO) maintenanceInvoiceDAO.getMaintenanceRequestPayeeInvoiceData(purchaseOrderVO.getMrqId());
				if(payeeInvoiceVO != null){
					purchaseOrderVO.setPayeeInvoiceNumber(payeeInvoiceVO.getDocNo());
					purchaseOrderVO.setPayeeInvoiceDate(payeeInvoiceVO.getDocDate());
				}
				
				InvoiceDateAndNumberVO mafsInvoiceVO = (InvoiceDateAndNumberVO) maintenanceInvoiceDAO.getMaintenanceRequestMafsInvoiceNumber(purchaseOrderVO.getMrqId());
				if(mafsInvoiceVO != null){
					purchaseOrderVO.setMafsInvoiceNumber(mafsInvoiceVO.getDocNo());
					purchaseOrderVO.setMafsInvoiceDate(mafsInvoiceVO.getDocDate());
				}
				
				purchaseOrderVO.setCreditFlag(maintenanceInvoiceDAO.isMaintenanceRequestInvoiceCredit(purchaseOrderVO.getMrqId()));
				
				/*if(purchaseOrderVO.getMaintRequestStatus().equalsIgnoreCase(maintenanceRequestService.convertPOStatus("C").getDescription()) && purchaseOrderVO.getDoclTotalCost() != null){
					purchaseOrderVO.setTotalCost(purchaseOrderVO.getDoclTotalCost().add(purchaseOrderVO.getTaskTotalCostRechN()));
				}else{
					purchaseOrderVO.setTotalCost(purchaseOrderVO.getTaskTotalCost());
				}*/ //commented for Bug 16387
				 
				purchaseOrderVO.setTotalCost(purchaseOrderVO.getTaskTotalCost());
				
				// Retrieve the Tooltip data for Service Provider
				serviceProviderDetail = purchaseOrderVO.getServiceProviderName() + "<br>No: " + purchaseOrderVO.getServiceProviderNumber();
				serviceProviderDetail = serviceProviderDetail + "<br>" + getServiceProviderAddressFormattedData(serviceProviderAddressList, purchaseOrderVO.getSupId());
				if(MALUtilities.isNotEmptyString(purchaseOrderVO.getServiceProviderTelephone())){
					serviceProviderDetail = serviceProviderDetail + "<br>" + purchaseOrderVO.getServiceProviderTelephone();
				}
				purchaseOrderVO.setServiceProviderDetails(serviceProviderDetail);
				purchaseOrderVO.setMaintReqServiceProviderDetail(serviceProviderDetail); //TODO: Refactor Do we need this?
				
				//Retrieve Maintenance Codes for the specific maintenance request
				purchaseOrderVO.setListMaintenanceCodes(getMaintenanceCodesForMrqId(maintenanceCodeVOList, purchaseOrderVO.getMrqId()));
			}
		} catch (Exception ex) {
			throw new MalException("generic.error.occured.while", 
					new String[] { "finding a maintenance service request vo" }, ex);				
		}
		
		return purchaseOrdersVO;
	}
	
	private int getMaintenanceServiceHistoryCount(List<Long> fmsIds, String providerFilter, String maintCategoryFilter, String maintCodeDescFilter){
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
	 * List of maintenance codes for a given maintenance request.
	 * @param mrqId Maintenance Request Id to retrieve tasks
	 * @Return Maintenance Code and Description. If description is modified at task level then it is returned.
	 */
	private List<MaintenanceCodeVO> getPopupMaintenanceCodes(List<Long> mrqIdList){
		List<MaintenanceCodeVO> maintenanceCodes = maintenanceRequestTaskDAO.findTasksMaintenanceCodes(mrqIdList);
		return maintenanceCodes;
	}
	
	/**
	 * Retrieves a list of Service Provider Ids
	 * @param serviceHistoryVOList List of Service History Data
	 * @return list of Service Provider Ids
	 */
	private List<BigDecimal> getServiceProviderIds(List<MaintenanceServiceHistoryVO> serviceHistoryVOList){
		List<BigDecimal> serviceProviderIdsList = new ArrayList<BigDecimal>();
		if(serviceHistoryVOList != null){
			for(MaintenanceServiceHistoryVO serviceHistoryVO : serviceHistoryVOList){
				serviceProviderIdsList.add(serviceHistoryVO.getSupId());
			}
		}
		return serviceProviderIdsList;
	}
	
	private String getServiceProviderAddressFormattedData(List<ServiceProviderAddressVO> serviceProviderAddressList, BigDecimal serviceProviderId){
		String serviceProviderDetail = "";
		for(ServiceProviderAddressVO serviceProviderAddressVO : serviceProviderAddressList){
			if(serviceProviderAddressVO.getServiceProviderId().equals(serviceProviderId)){
				serviceProviderDetail = maintenanceRequestService.getServiceProviderFormattedAddress(serviceProviderAddressVO.getServiceProviderAddress());
			}
		}
		return serviceProviderDetail;
	}
	
	/**
	 * Retrieves a list of Maintenance Request Ids from the input value
	 * @param serviceHistoryVOList List of Service History Data
	 * @return list of Maintenance Request Ids
	 */
	private List<Long> getMrqIds(List<MaintenanceServiceHistoryVO> serviceHistoryVOList){
		List<Long> mrqIdsList = new ArrayList<Long>();
		if(serviceHistoryVOList != null){
			for(MaintenanceServiceHistoryVO serviceHistoryVO : serviceHistoryVOList){
				mrqIdsList.add(serviceHistoryVO.getMrqId());
			}
		}
		return mrqIdsList;
	}
	
	/**
	 * Retrieve maintenance codes for the specific maintenance request that is passed as a parameter
	 * @param maintenanceCodeVOList List of all maintenance codes and mrqId
	 * @param mrqId mrqId to find maintenance codes 
	 * @return list of maintenance codes for a specific maintenance request
	 */
	private List<MaintenanceCode> getMaintenanceCodesForMrqId(List<MaintenanceCodeVO> maintenanceCodeVOList, Long mrqId){
		List<MaintenanceCode> maintenanceCodeResultList = new ArrayList<MaintenanceCode>();
		for(MaintenanceCodeVO maintCodeVO : maintenanceCodeVOList){
			if(maintCodeVO.getMrqId().equals(mrqId)){
				maintenanceCodeResultList.add(maintCodeVO.getMaintenanceCode());
			}
		}
		return maintenanceCodeResultList;
	}
	
}
