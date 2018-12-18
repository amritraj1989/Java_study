package com.mikealbert.vision.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mikealbert.common.MalLogger;
import com.mikealbert.common.MalLoggerFactory;
import com.mikealbert.data.DataUtilities;
import com.mikealbert.data.comparator.VendorInfoVOComparator;
import com.mikealbert.data.dao.CapitalElementDAO;
import com.mikealbert.data.dao.DealerAccessoryDAO;
import com.mikealbert.data.dao.DelayReasonDAO;
import com.mikealbert.data.dao.DocDAO;
import com.mikealbert.data.dao.DocPropertyDAO;
import com.mikealbert.data.dao.DocPropertyValueDAO;
import com.mikealbert.data.dao.DriverDAO;
import com.mikealbert.data.dao.ExternalAccountDAO;
import com.mikealbert.data.dao.FleetMasterDAO;
import com.mikealbert.data.dao.ProcessStageObjectDAO;
import com.mikealbert.data.dao.ProgressTypeCodeDAO;
import com.mikealbert.data.dao.QuotationModelDAO;
import com.mikealbert.data.dao.SupplierProgressHistoryDAO;
import com.mikealbert.data.dao.UnitProgressDAO;
import com.mikealbert.data.entity.ContractLine;
import com.mikealbert.data.entity.DelayReason;
import com.mikealbert.data.entity.DocPropertyValue;
import com.mikealbert.data.entity.FleetMaster;
import com.mikealbert.data.entity.ObjectLogBook;
import com.mikealbert.data.entity.ProcessStageObject;
import com.mikealbert.data.entity.SupplierProgressHistory;
import com.mikealbert.data.entity.User;
import com.mikealbert.data.enumeration.DocumentStatus;
import com.mikealbert.data.enumeration.LogBookTypeEnum;
import com.mikealbert.data.enumeration.OrderToDeliveryProcessStageEnum;
import com.mikealbert.data.enumeration.UnitProgressCodeEnum;
import com.mikealbert.data.vo.VendorInfoVO;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.exception.MalException;
import com.mikealbert.service.ContractService;
import com.mikealbert.service.FleetMasterService;
import com.mikealbert.service.LogBookService;
import com.mikealbert.service.ProcessStageService;
import com.mikealbert.service.QuoteRequestService;
import com.mikealbert.service.UpfitProgressService;
import com.mikealbert.util.MALUtilities;
import com.mikealbert.vision.vo.ContactInfo;
import com.mikealbert.vision.vo.UnitInfo;
import com.mikealbert.vision.vo.UnitProgressSearchVO;

@Service("unitProgressService")
@Transactional
public class UnitProgressServiceImpl implements UnitProgressService {

	public MalLogger logger = MalLoggerFactory.getLogger(this.getClass());

	@Resource UnitProgressDAO unitProgressDAO;
	@Resource ExternalAccountDAO externalAccountDAO;
	@Resource SupplierProgressHistoryDAO supplierProgressHistoryDAO;
	@Resource DriverDAO driverDAO;
	@Resource DocDAO docDAO;
	@Resource DealerAccessoryDAO dealerAccessoryDAO;
	@Resource QuotationModelDAO quotationModelDAO;
	@Resource CapitalElementDAO capitalElementDAO;
	@Resource VehicleMovementService vehicleMovementService;
	@Resource DelayReasonDAO delayReasonDAO;
	@Resource FleetMasterService fleetMasterService;
	@Resource DocPropertyDAO docPropertyDAO;
	@Resource DocPropertyValueDAO docPropertyValueDAO;
	@Resource OrderProgressService orderProgressService;
	@Resource FleetMasterDAO fleetMasterDAO;
	@Resource LogBookService logBookService;
	@Resource ProcessStageService processStageService;
	@Resource UpfitProgressService upfitProgressService;
	@Resource QuoteRequestService quoteRequestService;
	@Resource ContractService contractService;

	@Transactional(readOnly = true)
	public List<UnitProgressSearchVO> getUpfitDetailsList() {
		List<UnitProgressSearchVO> voList = new ArrayList<UnitProgressSearchVO>();
		List<DocumentStatus> upfitPOStatuses = new ArrayList<DocumentStatus>();

		try {

			Map<Long, List<VendorInfoVO>> vendorNameMap = new HashMap<Long, List<VendorInfoVO>>();

			Long qmdId = null;
			String vendorName = null;
			String vendorCode = null;
			Long vendorContext = null;		
			Long mainThpyDocId = null;
			BigDecimal leadTime = null;
			List<Object[]> poVendorsList = unitProgressDAO.getUpfitTPTPOList();
			VendorInfoVO vendorInfoVO = null;
			String taskCompleted = null;
			String orderType = null;
			boolean isStock;
			boolean isLinked;
			Long sequenceNo;
			
			upfitPOStatuses.add(DocumentStatus.PURCHASE_ORDER_STATUS_OPEN);
			upfitPOStatuses.add(DocumentStatus.PURCHASE_ORDER_STATUS_RELEASED);			
			
			for(Object[] record : poVendorsList) {

				qmdId = ((BigDecimal) record[0]).longValue();
				vendorName = (String) record[1];
				vendorCode = (String) record[2];
				vendorContext = ((BigDecimal) record[3]).longValue();
				mainThpyDocId = record[4] != null ? ((BigDecimal) record[4]).longValue() : null;
				leadTime = ((BigDecimal) record[5]);
				orderType = (String) record[6];				
				taskCompleted = record[7] != null ? (String) record[7] : null;
				isStock = MALUtilities.convertYNToBoolean((String)record[8]);
				isLinked = MALUtilities.convertYNToBoolean((String)record[9]);
				sequenceNo = ((BigDecimal) record[10]).longValue();		
								
				if(leadTime != null && leadTime.longValue() > 0) {
					vendorInfoVO = new VendorInfoVO(vendorName, vendorCode, vendorContext, qmdId, mainThpyDocId, leadTime.longValue());
					
					if(vendorNameMap.containsKey(qmdId) && vendorNameMap.get(qmdId).contains(vendorInfoVO)) {
							vendorInfoVO = vendorNameMap.get(qmdId).get(vendorNameMap.get(qmdId).indexOf(vendorInfoVO));
							vendorInfoVO.setLeadTime((DataUtilities.decodeNullString(vendorInfoVO.getLeadTime(), "0") + leadTime));
					} else {
						vendorInfoVO.setLinked(isLinked);
						vendorInfoVO.setSequenceNo(sequenceNo);

						if(!MALUtilities.isEmpty(taskCompleted)){
							vendorInfoVO.setVendorTaskCompleted(MALUtilities.convertYNToBoolean(taskCompleted));
						}else{
							vendorInfoVO.setVendorTaskCompleted(false);
						}

						if(vendorNameMap.containsKey(qmdId)) {
							vendorNameMap.get(qmdId).add(vendorInfoVO);

						} else {
							List<VendorInfoVO> vendorNameInfoList = new ArrayList<VendorInfoVO>();
							vendorNameInfoList.add(vendorInfoVO);
							vendorNameMap.put(qmdId, vendorNameInfoList);
						}
					}
				}
				
			}

			List<Object[]> resultList = unitProgressDAO.getBasicAcceptedQuotesDetailForUpfitProgressChasing();
			
			for(Object[] record : resultList) {
				int j = 0;
				UnitProgressSearchVO unitProgressSearchVO = new UnitProgressSearchVO();
				unitProgressSearchVO.setPsoId(((BigDecimal) record[j]).longValue());
				unitProgressSearchVO.setStockUnit(MALUtilities.convertYNToBoolean((String) record[j += 1]));
				unitProgressSearchVO.setDriver((String) record[j += 1]);
				unitProgressSearchVO.setAccountName((String) record[j += 1]);
				unitProgressSearchVO.setUnitNo((String) record[j += 1]);
				unitProgressSearchVO.setVin(record[j += 1] != null ? (String) record[j] : "");
				unitProgressSearchVO.setDocId(((BigDecimal) record[j += 1]).longValue());
				unitProgressSearchVO.setQmdId(((BigDecimal) record[j += 1]).longValue());
				unitProgressSearchVO.setVendorInfoList(vendorNameMap.get(unitProgressSearchVO.getQmdId()));				
								
				unitProgressSearchVO.setOrderType((String) record[j += 1]);
				unitProgressSearchVO.setReplacementFmsId(record[j += 1] != null ? ((BigDecimal) record[j]).longValue() : 0L);
				unitProgressSearchVO.setReplacementUnitNo(record[j += 1] != null ? (String) record[j] : null);

				unitProgressSearchVO.setDeliveringDealerName(record[j += 1] != null ? (String) record[j] : null);
				unitProgressSearchVO.setDeliveringDealerCode(record[j += 1] != null ? (String) record[j] : null);

				unitProgressSearchVO.setInvoiceProcessedDate(record[j += 1] != null ? (Date) (record[j]) : null);
				unitProgressSearchVO.setFactoryShipDate(record[j += 1] != null ? (Date) (record[j]) : null);
				unitProgressSearchVO.setDealerReceivedDate(record[j += 1] != null ? (Date) (record[j]) : null);
				unitProgressSearchVO.setClientETADate(record[j += 1] != null ? (Date) (record[j]) : null);
				unitProgressSearchVO.setVehicleReadyDate(record[j += 1] != null ? (Date) (record[j]) : null);
				unitProgressSearchVO.setFollowUpDate(record[j += 1] != null ? (Date) (record[j]) : null);
				unitProgressSearchVO.setReqdBy(record[j += 1] != null ? (String) (record[j]) : null);
				unitProgressSearchVO.setModelDesc(record[j += 1] != null ? (String) (record[j]) : null);
				unitProgressSearchVO.setTolerance(MALUtilities.convertYNToBoolean((String) record[j += 1]));

				//TODO OTD-4655: Due to bad data in DEV2/QA2, the check for null and empty needs to be here.  
				//There should be at lease one upfit vendor. However, until backfill script is completed this may not be the case.
				if(!MALUtilities.isEmpty(unitProgressSearchVO.getVendorInfoList()) && unitProgressSearchVO.getVendorInfoList().size() > 0) {
					Collections.sort(unitProgressSearchVO.getVendorInfoList(), new VendorInfoVOComparator());	
				} 
				
				voList.add(unitProgressSearchVO);
			}
		} catch (Exception e) {
			throw new MalException("Error while getting Upfit Details", e);
		}

		logger.debug("Total unit with upfit lead time is " + voList.size());

		return voList;
	}

	public Map<String, List<String>> getDealerAccessoriesWithVendorQuoteNumber(Long qmdId, Long thpyDocId, boolean isStock, String accountCode) {
		List<Object[]> accessoriesList = null;

		if(isStock) {
			accessoriesList = dealerAccessoryDAO.getStockQuotationDealerAccessoryByQmdIdDocIdAndAccountCode(qmdId, thpyDocId, accountCode);
		} else {
			accessoriesList = dealerAccessoryDAO.getQuotationDealerAccessoryByQmdIdDocId(qmdId, thpyDocId);
		}

		Map<String, List<String>> vendorQuoteNoAccessories = new HashMap<String, List<String>>();
		List<String> accessoryDescList = null;
		String accessoryDesc;
		String vendorQuoteNumber;
		for(Object[] record : accessoriesList) {
			vendorQuoteNumber = record[0] != null ? (String) record[0] : "";
			accessoryDesc = (String) record[1];
			if(vendorQuoteNoAccessories.containsKey(vendorQuoteNumber)) {
				vendorQuoteNoAccessories.get(vendorQuoteNumber).add(accessoryDesc);
			} else {
				accessoryDescList = new ArrayList<String>();
				accessoryDescList.add(accessoryDesc);
				vendorQuoteNoAccessories.put(vendorQuoteNumber, accessoryDescList);
			}
		}

		return vendorQuoteNoAccessories;
	}

	@Deprecated
	private Boolean isVendorWorkCompleted(VendorInfoVO vendorInfo) {
//		DocPropertyValue dpv = null;
//		if(!MALUtilities.isEmpty(vendorInfo.getThpyDocId())){
//			dpv = docPropertyValueDAO.findByNameDocIdVendor("VENDOR_TASK_COMPLETED",vendorInfo.getThpyDocId(), new ExternalAccountPK(vendorInfo.getVendorContext(),ExternalAccountDAO.ACCOUNT_TYPE_SUPPLIER,vendorInfo.getVendorCode()));
//		}else{
//			dpv = docPropertyValueDAO.findByNameDocIdVendor("VENDOR_TASK_COMPLETED",vendorInfo.getMainPoDocId(), new ExternalAccountPK(vendorInfo.getVendorContext(),ExternalAccountDAO.ACCOUNT_TYPE_SUPPLIER,vendorInfo.getVendorCode()));
//		}
//		if (!MALUtilities.isEmpty(dpv)) {
//			if (!MALUtilities.isEmpty(dpv.getPropertyValue()) && dpv.getPropertyValue().equals("Y")) {
//				return true;
//			} else {
//				return false;
//			}
//		}
		return false;
	}

	//	getPendingInServiceDateUnitsList  and getPendingInServiceUnit method should have always same result set otherwise functionality will be broken
	@Transactional(readOnly = true)
	public List<UnitProgressSearchVO> getPendingInServiceDateUnitsList() {

		List<UnitProgressSearchVO> inServiceProgressSearchVOList = new ArrayList<UnitProgressSearchVO>();
		try {
			List<Object[]> resultList = unitProgressDAO.getPendingInServiceUnitsList();
			for(Object[] record : resultList) {			
				inServiceProgressSearchVOList.add(mapPendingInServiceResultSet(record));
			}
		} catch (Exception e) {
			throw new MalException("Error while getting Pending InService Date Units ", e);
		}
		return inServiceProgressSearchVOList;
	}
	
	public UnitProgressSearchVO getPendingInServiceUnit(String unitNo) {
		UnitProgressSearchVO unitProgressSearchVO = null;
		try {
			List<Object[]> resultList = unitProgressDAO.getPendingInServiceUnit(unitNo);
			if(resultList != null && resultList.size() > 0) {
				Object[] record = resultList.get(0);
				unitProgressSearchVO = mapPendingInServiceResultSet(record);
			}
		} catch (Exception e) {
			throw new MalException("Error while getting Unit details", e);
		}
		return unitProgressSearchVO;
	}
	//	getPendingInServiceDateUnitsList  and getPendingInServiceUnit method should have always same result set otherwise functionality will be broken
	private UnitProgressSearchVO mapPendingInServiceResultSet(Object[] record ){
		int j = 0;		
		UnitProgressSearchVO unitProgressSearchVO = new UnitProgressSearchVO();
		unitProgressSearchVO.setPsoId(!MALUtilities.isEmpty(record[j]) ? ((BigDecimal) record[j]).longValue() : null);
		unitProgressSearchVO.setStockUnit(MALUtilities.convertYNToBoolean(record[j += 1].toString()));
		unitProgressSearchVO.setDriver((String) record[j += 1]);
		unitProgressSearchVO.setDriverId(record[j += 1] != null ? ((BigDecimal) record[j]).longValue() : 0L);
		unitProgressSearchVO.setAccountName(record[j += 1] != null ? (String) record[j] : "");
		unitProgressSearchVO.setAccountCode(record[j += 1] != null ? (String) record[j] : "");

		unitProgressSearchVO.setUnitNo((String) record[j += 1]);
		unitProgressSearchVO.setVin(record[j += 1] != null ? (String) record[j] : "");
		if(unitProgressSearchVO.isStockUnit()) {
			unitProgressSearchVO.setStockThDocId(((BigDecimal) record[j += 1]).longValue());
			unitProgressSearchVO.setDocId(unitProgressSearchVO.getStockThDocId());
		} else {
			unitProgressSearchVO.setDocId(((BigDecimal) record[j += 1]).longValue());
		}

		unitProgressSearchVO.setQmdId(((BigDecimal) record[j += 1]).longValue());
		unitProgressSearchVO.setQuoteStatus((String) record[j += 1]);
		unitProgressSearchVO.setUsedVehicle(MALUtilities.convertYNToBoolean((String) record[j += 1]));
		unitProgressSearchVO.setOrderType((String) record[j += 1]);
		unitProgressSearchVO.setPlateNo(record[j += 1] != null ? (String) record[j] : null);
		unitProgressSearchVO.setPlateExpirationDate(record[j += 1] != null ? (Date) record[j] : null);
		unitProgressSearchVO.setReplacementFmsId(record[j += 1] != null ? ((BigDecimal) record[j]).longValue() : 0L);
		unitProgressSearchVO.setReplacementUnitNo(record[j += 1] != null ? (String) record[j] : null);

		unitProgressSearchVO.setDeliveringDealerName(record[j += 1] != null ? (String) record[j] : null);
		unitProgressSearchVO.setDeliveringDealerCode(record[j += 1] != null ? (String) record[j] : null);

		unitProgressSearchVO.setInvoiceProcessedDate(record[j += 1] != null ? (Date) (record[j]) : null);
		unitProgressSearchVO.setDealerReceivedDate(record[j += 1] != null ? (Date) record[j] : null);
		unitProgressSearchVO.setClientETADate(record[j += 1] != null ? (Date) (record[j]) : null);
		unitProgressSearchVO.setVehicleReadyDate(record[j += 1] != null ? (Date) (record[j]) : null);
		unitProgressSearchVO.setFollowUpDate(record[j += 1] != null ? (Date) (record[j]) : null);
		unitProgressSearchVO.setReqdBy(record[j += 1] != null ? (String) record[j] : null);
		unitProgressSearchVO.setModelDesc(record[j += 1] != null ? (String) (record[j]) : null);
		long leadTime = record[j += 1] != null ? ((BigDecimal) (record[j])).longValue() : -1;
		unitProgressSearchVO.setHasUpfit(leadTime > 0 ? true : false);
		unitProgressSearchVO.setDelayReason(record[j += 1] != null ? (String) record[j] : null);
		unitProgressSearchVO.setPurchaseUnit(MALUtilities.convertYNToBoolean((String) record[j += 1]));
		unitProgressSearchVO.setExteriorColour(record[j += 1] != null ? (String) record[j] : null);
		unitProgressSearchVO.setManufacturerName(record[j += 1] != null ? (String) record[j] : null);

		
		return unitProgressSearchVO;
	}
	@Transactional(readOnly = true)
	public List<String> getDealerAccessoriesList(Long thirdPartyPoDocId) {
		return dealerAccessoryDAO.getPOAccessoriesDescription(thirdPartyPoDocId);
	}

	@Transactional(readOnly = true)
	public ContactInfo getDealerContactInfo(String accountCode) {
		ContactInfo contactInfo = null;

		List<Object[]> dealerContactsList;
		try {
			dealerContactsList = externalAccountDAO.getDealerContactDetailsList(1L, "S", accountCode);
			if(dealerContactsList != null && dealerContactsList.size() > 0) {
				Object[] vendorContactDetails = dealerContactsList.get(0);

				contactInfo = new ContactInfo();
				contactInfo.setName((String) vendorContactDetails[1]);
				contactInfo.setPhone((String) vendorContactDetails[2]);
				contactInfo.setEmail((String) (vendorContactDetails[3] != null ? vendorContactDetails[3] : vendorContactDetails[4]));
				contactInfo.setDealerPhone((String) vendorContactDetails[5]);
			}
		} catch (Exception e) {
			throw new MalException("Error while fetching contact info for account " + accountCode, e);
		}

		return contactInfo;
	}

	@Transactional(readOnly = true)
	public ContactInfo getVendorSupplierContactInfo(String accountCode) {
		ContactInfo contactInfo = null;

		List<Object[]> vendorContactsList;
		try {
			vendorContactsList = externalAccountDAO.getVendorContactDetailsList(1L, "S", accountCode);
			if(vendorContactsList != null && vendorContactsList.size() > 0) {
				Object[] vendorContactDetails = vendorContactsList.get(0);

				contactInfo = new ContactInfo();
				contactInfo.setName((String) vendorContactDetails[1]);
				contactInfo.setPhone((String) vendorContactDetails[2]);
				contactInfo.setEmail((String) (vendorContactDetails[3] != null ? vendorContactDetails[3] : vendorContactDetails[4]));
				contactInfo.setDealerPhone((String) vendorContactDetails[5]);
				
			}
		} catch (Exception e) {
			throw new MalException("Error while getting Vendor/Supplier Contact Info for account " + accountCode, e);

		}

		return contactInfo;
	}

	@Override
	public UnitInfo getSelectedUnitDetails(Long fmsId) {
		UnitInfo unitInfo = null;

		List<Object[]> detailsList;
		String vinNo = null;
		try {
			detailsList = unitProgressDAO.getSelectedUnitDetails(fmsId);
			if(detailsList != null && detailsList.size() > 0) {
				Object[] unitDetails = detailsList.get(0);

				unitInfo = new UnitInfo();
				unitInfo.setTrim((String) unitDetails[0]);
				unitInfo.setColor((String) unitDetails[1]);
				vinNo = unitDetails[2] != null ? (String) unitDetails[2] : null;
				unitInfo.setVin(vinNo != null ? vinNo.substring(vinNo.length() - 8, vinNo.length()) : "");
			}
		} catch (Exception e) {
			throw new MalException("Error while getting unit details for fleet master " + fmsId, e);
		}

		return unitInfo;
	}

	public Long getLatestOdoReading(Long fmsId) {
		return unitProgressDAO.getLatestOdoReading(fmsId);
	}

	public Long getVehicleOdoReadingByQmdId(Long qmdId) {
		return unitProgressDAO.getVehicleOdoReadingByQmdId(qmdId);
	}

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void putUnitInService(UnitProgressSearchVO selectedUnitProgress, Long contextId, Date odoReadingDate, String odoReadingType, String userId) throws MalException {

		FleetMaster fms = fleetMasterService.findByUnitNo(selectedUnitProgress.getUnitNo());
		if(fms != null){
			ContractLine cl = contractService.getActiveContractLine(fms,new Date());
			if(cl == null || MALUtilities.isEmpty(cl.getInServDate())){
				cl = contractService.getPendingLiveContractLine(fms,new Date());
			}
			if(cl == null || MALUtilities.isEmpty(cl.getInServDate())){
				unitProgressDAO.putUnitInService(selectedUnitProgress.getUnitNo(), contextId, selectedUnitProgress.getDealerReceivedDateInput(),
						 selectedUnitProgress.getInServiceDateInput(),selectedUnitProgress.getOdoMeterReading(), odoReadingDate, odoReadingType, userId);
			}
		}

		ProcessStageObject  processStageObject =  processStageService.getStagedObject(OrderToDeliveryProcessStageEnum.IN_SERVICE, selectedUnitProgress.getDocId());
		if(MALUtilities.convertYNToBoolean(selectedUnitProgress.getVehiclePickedUp())){
			processStageObject.setProperty1(selectedUnitProgress.getVehiclePickedUp());
			processStageService.excludeStagedObject(processStageObject);
		}else{
			processStageObject.setProperty1(selectedUnitProgress.getVehiclePickedUp());
			processStageObject.setRefreshYN("N");
			processStageService.saveOrUpdateProcessStageObject(processStageObject);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void putUnitInServiceWithTransport(UnitProgressSearchVO selectedUnitProgress, User loggedInUser, String tranTypeConfigValue, String transPriorityConfigValue) {
		// Put Unit in Service
		putUnitInService(selectedUnitProgress, loggedInUser.getCorporateEntity().getCorpId(), new Date(), "GRNODO", loggedInUser.getEmployeeNo());
		
		// Create transport for Returning vehicle
		String diaryNote = "A Pre Collection Check has occurred and an Early Termination is required";
		vehicleMovementService.createTranport("EAA", selectedUnitProgress.getDeliveringDealerCode(), selectedUnitProgress.getAccountCode(), selectedUnitProgress.getReplacementFmsId(), tranTypeConfigValue, transPriorityConfigValue, "EOL", "EOL", "MISC", loggedInUser.getEmployeeNo(), "", "",
				selectedUnitProgress.getOrderingContactName(), selectedUnitProgress.getOrderingContactPhone(), diaryNote, diaryNote);
		
	}
	
	 // added for Bug 16598
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void updateInService(Long fmsId, Date inServiceDate) throws MalException {

		unitProgressDAO.updateInService(fmsId, inServiceDate);

	}

	public String getGrdStatus(Long contextId, String accountType, String accountCode, Long celId, Long docId) {
		return unitProgressDAO.getGrdStatus(contextId, accountType, accountCode, celId, docId);
	}

	/**
	 * When placing a unit in service, the dealer received date can be either
	 * the action date of the 15_RECEIVD progress status.
	 * 
	 * @param Long
	 *            DOC.DOC_ID of the unit's PO
	 * @param String
	 *            Unit's progress status code
	 */
	public Date getDealerReceivedDate(Long docId, UnitProgressCodeEnum progressStatus) {
		Date dealerReceivedDate = null;
		List<SupplierProgressHistory> progressHistory = supplierProgressHistoryDAO.findSupplierProgressHistoryForDoc(docId, new Sort(Sort.Direction.DESC, "sphId"));

		/*
		 * Sort below commented for OTD-1001 Fix //Sort on sphId in DESC ordr
		 * Collections.sort(progressHistory, new
		 * Comparator<SupplierProgressHistory>() { public int
		 * compare(SupplierProgressHistory sph1, SupplierProgressHistory sph2) {
		 * return
		 * Long.valueOf(sph2.getSphId()).compareTo(Long.valueOf(sph1.getSphId())
		 * ); } });
		 */

		// Action Date of the first match, which based on the sphId DESC sort
		// order should be the latest entry
		for(SupplierProgressHistory rec : progressHistory) {
			if(rec.getProgressType().equals(progressStatus.getCode())) {
				dealerReceivedDate = MALUtilities.isEmpty(rec.getActionDate()) ? rec.getEnteredDate() : rec.getActionDate();
				break;
			}
		}

		return dealerReceivedDate;
	}

	@Override
	public List<DelayReason> getDelayReasons() {
		return delayReasonDAO.getDelayReasons();
	}
	
	@Deprecated
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public Boolean saveOrUpdateVendorTasks(UnitProgressSearchVO unitProgressVO, FleetMaster fms, String userName) {
		List<DocPropertyValue> dpvpList = new ArrayList<DocPropertyValue>();
		Boolean allTaskCompleted = true;
		return allTaskCompleted;		
//		try {
//		for (VendorInfo vendorInfo : unitProgressVO.getVendorInfoList()) {
//
//			Long docId = !MALUtilities.isEmpty(vendorInfo.getThpyDocId()) ? vendorInfo.getThpyDocId() : vendorInfo.getMainPoDocId();
//			ExternalAccountPK vendor = new ExternalAccountPK(vendorInfo.getVendorContext(), ExternalAccountDAO.ACCOUNT_TYPE_SUPPLIER, vendorInfo.getVendorCode());
//			DocPropertyValue dpv = docPropertyValueDAO.findByNameDocIdVendor("VENDOR_TASK_COMPLETED", docId, vendor);
//			
//			if (!MALUtilities.isEmpty(dpv)) {
//				dpv.setPropertyValue(vendorInfo.getVendorTaskCompleted() ? "Y" : "N");
//			} else {
//				dpv = new DocPropertyValue();
//				dpv.setDoc(docDAO.findOne(docId));
//				ExternalAccount ea = externalAccountDAO.findOne(vendor);
//				dpv.setExternalAccount(ea);
//				dpv.setDocProperty(docPropertyDAO.findByName("VENDOR_TASK_COMPLETED"));
//				dpv.setPropertyValue(vendorInfo.getVendorTaskCompleted() ? "Y" : "N");
//			}
//			
//			if (dpv.getPropertyValue().equals("N")) {
//				allTaskCompleted = false;
//			}
//			
//			dpvpList.add(dpv);
//		}
//		
//			docPropertyValueDAO.save(dpvpList);
//			if (allTaskCompleted) {
//				createSuppProgHistAndNote (fms, unitProgressVO.getDocId(), userName);
//			    processStageService.excludeStagedObject(unitProgressVO.getPsoId());
//			}
//			return allTaskCompleted;
//		} catch (Exception e) {
//			allTaskCompleted = false;
//			logger.error(e , "Error while completing the Vendor Task");
//			throw new MalException("Error while completing the Vendor Task", e);
//		}
	}
	
		
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void updateDelayReason(List<String> unitNoList, DelayReason delayReasonCode) throws MalBusinessException {
		// DelayReason delayReason = null;
		List<FleetMaster> fleetMasterList = null;
		if(unitNoList != null) {
			fleetMasterList = new ArrayList<FleetMaster>();
			for(String unitNo : unitNoList) {
				fleetMasterList.add(fleetMasterService.findByUnitNo(unitNo));
			}
			for(FleetMaster fleetMaster : fleetMasterList) {
				fleetMaster.setDelayReason(delayReasonCode);
			}
			fleetMasterService.saveUpdateFleetMaster(fleetMasterList);
		}

	}

	public UnitProgressSearchVO getUpdatedUnitProgressSearch(Long docId, int searchType) {
		UnitProgressSearchVO unitProgressSearchVO = null;

		Object[] record = unitProgressDAO.getUpdatedSupllierProgressData(docId);
		if(record != null) {
			int j = 0;
			unitProgressSearchVO = new UnitProgressSearchVO();
			unitProgressSearchVO.setClientETADate(record[j] != null ? (Date) (record[j]) : null);
			unitProgressSearchVO.setVehicleReadyDate(record[j += 1] != null ? (Date) (record[j]) : null);
			unitProgressSearchVO.setInvoiceProcessedDate(record[j += 1] != null ? (Date) (record[j]) : null);
			if(searchType == 1) {
				unitProgressSearchVO.setFactoryShipDate(record[j += 1] != null ? (Date) (record[j]) : null);
				unitProgressSearchVO.setDealerReceivedDate(record[j += 1] != null ? (Date) record[j] : null);
			} else {
				unitProgressSearchVO.setDealerReceivedDate(record[j += 3] != null ? (Date) record[j] : null);
			}
		}

		return unitProgressSearchVO;
	}

	public boolean stockValidityCheck(Long qmdId, Long fmsId) throws MalBusinessException {
		return quotationModelDAO.stockValidityCheck(qmdId, fmsId);
	}

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void saveStockFinalAccept(UnitProgressSearchVO selectedUnitProgress, Long fmsId, String odoReadingType, String employeeNo) throws MalException {
		
		FleetMaster fms = fleetMasterService.findByUnitNo(selectedUnitProgress.getUnitNo());
		if(fms != null){
			ContractLine cl = contractService.getActiveContractLine(fms,new Date());
			if(cl == null){
				cl = contractService.getPendingLiveContractLine(fms,new Date());
			}
			if(cl == null || MALUtilities.isEmpty(cl.getInServDate())){
				
				quotationModelDAO.stockFinalAccept(selectedUnitProgress.getQmdId(), fmsId, selectedUnitProgress.getInServiceDateInput(), 
						selectedUnitProgress.getOdoMeterReading(), selectedUnitProgress.getInServiceDateInput(), odoReadingType, employeeNo);
			}
		}
		
		ProcessStageObject  processStageObject =  processStageService.getStagedObject(OrderToDeliveryProcessStageEnum.IN_SERVICE, selectedUnitProgress.getDocId());
		
		if(MALUtilities.convertYNToBoolean(selectedUnitProgress.getVehiclePickedUp())){
			processStageObject.setProperty1(selectedUnitProgress.getVehiclePickedUp());
			processStageService.excludeStagedObject(processStageObject);
		}else{
			processStageObject.setProperty1(selectedUnitProgress.getVehiclePickedUp());
			processStageObject.setRefreshYN("N");
			processStageService.saveOrUpdateProcessStageObject(processStageObject);
		}
	}
	
	@Override
	public boolean isGrnCreated(Long fmsId){
		String grnCreatedYn = unitProgressDAO.grnCreatedYn(fmsId);
		if (grnCreatedYn.equalsIgnoreCase("Y")) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Updates the VO's tolerance flag with the current value in the database
	 * @param UnitProgressSearchVO VO of item in the queue
	 * @return UnitProgressSearchVO with tolerance flag refreshed
	 */
	@Override
	public UnitProgressSearchVO refreshToleranceFlag(UnitProgressSearchVO unitProgressSearchVO, OrderToDeliveryProcessStageEnum processStage) {
		unitProgressSearchVO.setTolerance(unitProgressDAO.getItemToleranceFlag(unitProgressSearchVO.getDocId(), processStage));
		return 	unitProgressSearchVO;
	}
}
