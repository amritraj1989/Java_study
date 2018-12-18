package com.mikealbert.vision.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.mikealbert.data.entity.FleetMaster;
import com.mikealbert.data.entity.FleetNotes;
import com.mikealbert.data.entity.MaintenanceCode;
import com.mikealbert.data.entity.MaintenancePreferenceAccount;
import com.mikealbert.data.entity.MaintenanceRechargeCode;
import com.mikealbert.data.entity.MaintenanceRequest;
import com.mikealbert.data.entity.MaintenanceRequestStatus;
import com.mikealbert.data.entity.MaintenanceRequestTask;
import com.mikealbert.data.entity.MaintenanceRequestType;
import com.mikealbert.data.entity.RegionCode;
import com.mikealbert.data.entity.ServiceProviderMaintenanceCode;
import com.mikealbert.data.enumeration.CorporateEntity;
import com.mikealbert.data.vo.MaintenancePreferencesVO;
import com.mikealbert.data.vo.MaintenanceProgramVO;
import com.mikealbert.data.vo.VehicleInformationVO;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.service.util.email.Email;
import com.mikealbert.service.util.email.EmailAddress;

public interface VehicleMaintenanceService {	
    static final String DATE_TYPE_IN_SERVICE = "In Service";
    static final String DATE_TYPE_DEALER_DELIVERY = "Dealer Delivery";
    static final String DATE_TYPE_ETA = "ETA";	

    @Deprecated
	public MaintenanceRequest getMaintenanceRequestByMrqId(long mrqId);
	
    @Deprecated
	public List<MaintenanceRequest> getMaintenanceRequestByFmsId(long fmsId);
	
    @Deprecated
	public MaintenanceRequest getMaintenanceRequestByJobNo(String jobNo);        
	
	public String concatMaintenanceCategories(Long mrqId);
	
	public VehicleInformationVO getVehicleInformationByMrqId(Long mrqId);
	
	public VehicleInformationVO getVehicleInformationByFmsId(Long fmsId);
	
	public MaintenanceRequestStatus convertPOStatus(String code);
	
	public MaintenanceRequestType convertPOType(String code);

	public MaintenanceRechargeCode convertMaintenanceRechargeCode(String code);	

	@Deprecated
	public BigDecimal calculateMarkUp(MaintenanceRequest po);
	
	@Deprecated
	public BigDecimal calculatePOSubTotal(MaintenanceRequest po);
	
	public void saveItemTask(MaintenanceRequestTask maintenanceRequestTask);
	
	public String getDefaultMaintRechargeFlag(MaintenanceRequest maintenanceRequest);
	
	public MaintenanceRechargeCode getDefaultMaintRechargeCode(MaintenanceRequest maintenanceRequest);
	
	@Deprecated
	public List<MaintenanceCode> getMaintenanceCodesByCategoryCode(String categoryCode);
	@Deprecated
	public List<MaintenanceCode> getMaintenanceCodesByNameOrCode(String nameOrCode, String categoryCode);
	@Deprecated
	public List<MaintenanceCode> getMaintenanceCodesByNameOrCode(String nameOrCode);
	
	public String generateJobNumber(CorporateEntity corporateEntity);
	
	public boolean isMaintenancePOModified(MaintenanceRequest originalMaintenanceRequest, MaintenanceRequest modifiedMaintenanceRequest);
	
	public boolean isTaskItemListModified(List<MaintenanceRequestTask> originalRequestTasks, List<MaintenanceRequestTask> modifiedRequestTasks);
	
	public void updateModifiedTaskItems(List<MaintenanceRequestTask> originalRequestTasks, List<MaintenanceRequestTask> modifiedRequestTasks, String logedInUser);

	public MaintenanceRequestTask copyTaskItem(MaintenanceRequestTask taskItem);
		

//	public List<ServiceProviderMaintenanceCode> getServiceProviderMaintenanceByMafsCode(String mafsCode, Long selectedProviderId);
//	
	public List<FleetNotes> getFleetNotesByMaintReqId(Long maintenanceRequestId);
	
	public List<RegionCode> getTaxExemptedRegions(Long cid, String accountType, String accountCode);
	
	public MaintenancePreferenceAccount getMaintenancePreferenceAccount(VehicleInformationVO vehicleInfo);
	
	public List<MaintenanceProgramVO> getMaintenancePrograms(VehicleInformationVO vehicleInfo);	
	
	public List<MaintenancePreferencesVO> getMaintenancePreferences(VehicleInformationVO vehicleInfo);
		
	public List<ServiceProviderMaintenanceCode> getServiceCodesByCategoryCode(String categoryCode);
	
	public List<ServiceProviderMaintenanceCode> getServiceCodesByCodeOrDesc(String serviceCodeOrDesc, String maintCode, String maintCatCode, Long selectedProviderId);
	
	public MaintenanceRequest copyMaintenanceRequest(MaintenanceRequest maintenanceRequest);
	
	public boolean isMaintenanceProgramsForFee(VehicleInformationVO vehicleInfo);
	
	//HPS-2946
	public boolean isBudgetForFee(VehicleInformationVO vehicleInfo);
	
	public Map<String,List<EmailAddress>> getContactEmailAddresses(FleetMaster fm, Long selectedContactId);
	public String generateExceededAuthSummary(CorporateEntity corpEntity,
			String accountType, String accountCode, FleetMaster fm);

	public String generateExceededAuthMsg(VehicleInformationVO vehicleInfo) throws MalBusinessException;
	public String generateExceededAuthEmailSubject(FleetMaster fm);
	public String generateExceededAuthEmailBody(MaintenanceRequest req);
	public Email generateExceededAuthEmail(MaintenanceRequest req, Long selectedContactId);	
	public String	getClientScheduleTypeCode(Long cstId);
	
	//HD-419
	public String findElementOnQuote(Long qmdId,Long lelId);
	public int getleaseElementbyFmdId(Long qmdId);
	public Long getContractLinesfromfmsId(Long fmsId);
	public Long getQmdIdfromClnId(Long clnId);
	public Long getClnIdforDisposedUnit(Long fmsId);
	public boolean validationCheckForInformalUnit(Long qmdId);
	public Long getClnIdforReleaseUnit(Long fmsId);
	
}
