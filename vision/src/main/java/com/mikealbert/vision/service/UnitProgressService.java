package com.mikealbert.vision.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.mikealbert.data.entity.DelayReason;
import com.mikealbert.data.entity.FleetMaster;
import com.mikealbert.data.entity.User;
import com.mikealbert.data.enumeration.OrderToDeliveryProcessStageEnum;
import com.mikealbert.data.enumeration.UnitProgressCodeEnum;
import com.mikealbert.data.vo.VendorInfoVO;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.exception.MalException;
import com.mikealbert.vision.vo.ContactInfo;
import com.mikealbert.vision.vo.UnitInfo;
import com.mikealbert.vision.vo.UnitProgressSearchVO;

public interface UnitProgressService {
	
	public List<UnitProgressSearchVO> getUpfitDetailsList() throws MalException;
	public List<UnitProgressSearchVO> getPendingInServiceDateUnitsList() throws MalException;
	public UnitProgressSearchVO getPendingInServiceUnit(String unitNo) throws MalException;
	public List<String> getDealerAccessoriesList(Long thirdPartyPoDocId) throws MalException;
	public ContactInfo getDealerContactInfo(String accountCode) throws MalException;
	public ContactInfo getVendorSupplierContactInfo(String accountCode)throws MalException;
	public UnitInfo getSelectedUnitDetails(Long fmsId)throws MalException;
	public Long getLatestOdoReading(Long fmsId)  throws MalException;
	public void putUnitInService(UnitProgressSearchVO selectedUnitProgress, Long contextId,
			 					Date odoReadingDate, String odoReadingType,String userId) throws MalException;
	public String getGrdStatus(Long contextId, String accountType, String accountCode, Long celId, Long docId);
	public Date getDealerReceivedDate(Long docId, UnitProgressCodeEnum progressStatus);
	public void updateInService(Long fmsId, Date inServiceDate) throws MalException; // added for Bug 16598
	public void putUnitInServiceWithTransport(UnitProgressSearchVO selectedUnitProgress, User loggedInUser, String tranTypeConfigValue, String transPriorityConfigValue);
	public Map<String, List<String>> getDealerAccessoriesWithVendorQuoteNumber(Long qmdId, Long thpyDocId, boolean isStock, String accountCode);
	public List<DelayReason> getDelayReasons();
	public void updateDelayReason(List<String> unitNoList, DelayReason inDelayReasonCode) throws MalBusinessException;
	public UnitProgressSearchVO getUpdatedUnitProgressSearch(Long docId, int searchType);
	public boolean stockValidityCheck(Long qmdId, Long fmsId) throws MalBusinessException;
	public void saveStockFinalAccept(UnitProgressSearchVO selectedUnitProgress, Long fmsId, String odoReadingType, String employeeNo) throws MalException;
	public Long getVehicleOdoReadingByQmdId(Long qmdId);
	public boolean isGrnCreated(Long fmsId);
	public Boolean saveOrUpdateVendorTasks(UnitProgressSearchVO unitProgressVO, FleetMaster fms, String userName);
	
	public UnitProgressSearchVO refreshToleranceFlag(UnitProgressSearchVO unitProgressSearchVO, OrderToDeliveryProcessStageEnum processStage);

}
