package com.mikealbert.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Pageable;

import com.mikealbert.data.entity.CostAvoidanceReason;
import com.mikealbert.data.entity.Doc;
import com.mikealbert.data.entity.Docl;
import com.mikealbert.data.entity.ExternalAccount;
import com.mikealbert.data.entity.FleetMaster;
import com.mikealbert.data.entity.FleetNotes;
import com.mikealbert.data.entity.MaintenanceCategoryUOM;
import com.mikealbert.data.entity.MaintenanceCode;
import com.mikealbert.data.entity.MaintenanceRechargeCode;
import com.mikealbert.data.entity.MaintenanceRequest;
import com.mikealbert.data.entity.MaintenanceRequestStatus;
import com.mikealbert.data.entity.MaintenanceRequestTask;
import com.mikealbert.data.entity.MaintenanceRequestType;
import com.mikealbert.data.entity.MaintenanceRequestUser;
import com.mikealbert.data.entity.ServiceProvider;
import com.mikealbert.data.entity.ServiceProviderAddress;
import com.mikealbert.data.enumeration.CorporateEntity;
import com.mikealbert.data.vo.HistoricalMaintCatCodeVO;
import com.mikealbert.data.vo.MaintCodeFinParamMappingVO;
import com.mikealbert.data.vo.MaintenanceContactsVO;
import com.mikealbert.data.vo.MaintenanceInvoiceCreditVO;
import com.mikealbert.data.vo.ProgressChasingQueueVO;
import com.mikealbert.data.vo.ProgressChasingVO;
import com.mikealbert.data.vo.VehicleInformationVO;
import com.mikealbert.exception.MalBusinessException;

public interface MaintenanceRequestService {
    public static final String DATE_TYPE_IN_SERVICE = "In Service";
    public static final String DATE_TYPE_DEALER_DELIVERY = "Dealer Delivery";
    public static final String DATE_TYPE_ETA = "ETA";	
        
    //public static final String MAINT_REQUEST_REASON_CODE = "NORMAL";
    
	public MaintenanceRequest getMaintenanceRequestByMrqId(long mrqId);
	public List<MaintenanceRequest> getMaintenanceRequestByFmsId(long fmsId);
	public MaintenanceRequest getMaintenanceRequestByJobNo(String jobNo);
	public MaintenanceRequest applyMarkUp(MaintenanceRequest mrq, BigDecimal markUp);
	public MaintenanceRequest saveOrUpdateMaintnenacePO(MaintenanceRequest mrq, String username) throws MalBusinessException;
	public MaintenanceRequest createGoodwillMaintenanceRequest(MaintenanceRequest maintenanceRequest, String username) throws MalBusinessException;	
	public MaintenanceRequest createMarkupLine(MaintenanceRequest mrq, String username, boolean waiveNonNetworkMarkup);

	public String generateJobNumber(CorporateEntity corporateEntity);
	
	public BigDecimal sumTaskTotalToRecharge(List<MaintenanceRequestTask> maintRequestTasks);
	public BigDecimal sumTotalCost(MaintenanceRequest mrq);
	public BigDecimal calculateNonNetworkRechargeMarkup(MaintenanceRequest mrq);
	public BigDecimal calculatePOSubTotal(MaintenanceRequest mrq);
	public BigDecimal sumRechargeTotal(MaintenanceRequest mrq);
	public BigDecimal sumMarkUpTotal(MaintenanceRequest mrq);
	public BigDecimal sumGoodwillTotal(MaintenanceRequest mrq);
	public BigDecimal sumCostAvoidanceTotal(MaintenanceRequest mrq);	
	
	public MaintenanceRequestStatus convertPOStatus(String code);
	public MaintenanceRequestType convertPOType(String code);
	public MaintenanceRechargeCode convertMaintenanceRechargeCode(String code);	
	public CostAvoidanceReason convertCostAvoidanceReason(String code);
	public MaintenanceCode convertMaintenanceCode(String code);	
	
	public MaintenanceRequestTask getTaskItemById(Long mrtId);	

	public void saveItemTask(MaintenanceRequestTask maintenanceRequestTask);
	public boolean isTaskItemListModified(List<MaintenanceRequestTask> originalRequestTasks, List<MaintenanceRequestTask> modifiedRequestTasks);
	public void updateAuthorizePersonForModifiedTaskItems(List<MaintenanceRequestTask> originalRequestTasks, List<MaintenanceRequestTask> modifiedRequestTasks, String logedInUser);
	//public List<MaintenanceRequestTask> copyList(List<MaintenanceRequestTask> list);
	public List<MaintenanceContactsVO> getAllClientContactVOs(String pointOfCommunication, VehicleInformationVO vehicleInformationVO) throws MalBusinessException;
	
	public List<MaintenanceRequest> findPlannedDateEndedMaintenanceRequests();
	public void updateToWaitOnInvoiceMaintenanceRequestStatus(MaintenanceRequest mrq);
	void updateModifiedTaskItems(List<MaintenanceRequestTask> originalRequestTasks,List<MaintenanceRequestTask> modifiedRequestTasks,String logedInUser);
	
	public List<HistoricalMaintCatCodeVO> getHistoricalMaintCatCodes(String vin, String maintCatCode, Long mrqId);
	public List<String> getDistinctHistoricalCatCodes(String vin, Long mrqId);
	
	public void authorizeMRQ(MaintenanceRequest mrq, CorporateEntity corporateEntity, String user) throws MalBusinessException;
	public void completeMRQ(MaintenanceRequest mrq, CorporateEntity corporateEntity, String user) throws MalBusinessException;
	public void cancelAuthorization(MaintenanceRequest mrq, CorporateEntity corporateEntity, String username) throws MalBusinessException;
	public void closeJob(MaintenanceRequest mrq, CorporateEntity corporateEntity, String username) throws MalBusinessException;
	
	//public ArrayList<String> validateStateChange(MaintenanceRequest mrq, String futureStatus) throws MalBusinessException;
	public String notNullActualStart(MaintenanceRequest mrq);
	public String notNullEndDate(MaintenanceRequest mrq);
	public String validateStatusBeforeFutureStatus(MaintenanceRequest mrq, String status);
	public String validateZeroTasks(MaintenanceRequest mrq);
	//public String validateRechargeExceedsLimit(MaintenanceRequest mrq) throws MalBusinessException;
	public String validateFleetMaintAuthBuyerLimit(MaintenanceRequest mrq, CorporateEntity corporateEntity, String user);
	public String validateOffContractNoRecharge(MaintenanceRequest mrq);
	
	public boolean deletePO(MaintenanceRequest mrq);
	
	public MaintenanceRequestUser createMaintRequestUserLog (MaintenanceRequest modifiedMaintRequest, String username);
	
	public List<ProgressChasingQueueVO> getProgressChasingDataList();
	public List<ProgressChasingVO> getProgressChasingByPoStatus(String poStatus, Pageable pageable, String lastUpdatedBy);
	public String getServiceProviderFormattedAddress(ServiceProviderAddress serviceProviderAddress);
	
	public boolean isRechargeTotalWithinLimit(MaintenanceRequest mrq) throws MalBusinessException;
	
	public String getMaintReqServiceProvDetail (Long mrqId);
	public String getMaintReqServiceProvDetail (MaintenanceRequest mrq);	
	public List<Doc> getMaintenanceCreditAP(MaintenanceRequest mrq);
	public List<MaintenanceInvoiceCreditVO> getMaintenanceCreditAPLines(MaintenanceRequest mrq);
	public List<Docl> getMaintenanceCreditARMarkupList(MaintenanceRequest mrq);
	public List<Docl> getMaintenanceCreditARTaxList(MaintenanceRequest mrq);
	public List<Docl> getMaintenanceCreditARLinesWithoutMarkupAndTaxList(MaintenanceRequest mrq);
	
	public String getPayeeInvoiceNo(Long mrqId);
	public String getMafsInvoiceNo(Long mrqId);

	public List<FleetNotes> mashUpFleetNotes(List<FleetMaster> fleetMasters);
	public List<FleetNotes> getJobNotesByMrqId(Long mrqId);
	
	public MaintenanceRequestTask intializeMaintenanceCateogryProperties(MaintenanceRequestTask task);
	
	public List<MaintenanceCategoryUOM> getMaintenanceCategoryUOM(MaintenanceRequestTask task);
	
	public Doc getReleasedPurchaseOrderForMaintReq(MaintenanceRequest mrq);
	
	public Long nextMRTLineNumber(MaintenanceRequest mrq);
	
	public MaintenanceRequestTask resetMaintenanceCateogryPropertyValues(MaintenanceRequestTask task);
	
	public MaintenanceRequest revertLinesMarkUp(MaintenanceRequest po);
	
	public List<MaintCodeFinParamMappingVO> getMaintCodesFinParamsMapping(String maintCode);
	public BigDecimal calculateVehicleRentalFee(MaintenanceRequest mrq);
	public MaintenanceRequestTask createVehicleRentalFeeLine(MaintenanceRequest mrq, String username);
	
	//HPS-2946 New method to impelemnt ERS Fee
	public MaintenanceRequestTask createERSFeeLine(MaintenanceRequest mrq, String username);
	
	public List<MaintenanceContactsVO> getContacts(String pointOfCommunication, VehicleInformationVO vehicleInformationVO) throws MalBusinessException;
	
	public boolean isTaskDiscountedBySupplier(ServiceProvider serviceProvider, MaintenanceRequestTask task);
	public boolean isMaintenanceRequestEditable(MaintenanceRequest mrq);
	public boolean hasWaivedOutOfNetworkSurcharge(MaintenanceRequest mrq);
	public ExternalAccount getPayeeAccount(String accountCode, String accountType,Long cId);
}
