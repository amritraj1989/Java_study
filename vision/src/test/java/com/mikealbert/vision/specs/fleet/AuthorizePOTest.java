package com.mikealbert.vision.specs.fleet;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.annotation.Resource;
import javax.persistence.Query;

import org.springframework.data.domain.PageRequest;

import com.mikealbert.common.MalConstants;
import com.mikealbert.data.DataConstants;
import com.mikealbert.data.TestQueryConstants;
import com.mikealbert.data.dao.ContractLineDAO;
import com.mikealbert.data.dao.FleetMasterDAO;
import com.mikealbert.data.dao.MaintenanceRequestTaskDAO;
import com.mikealbert.data.dao.QuotationModelDAO;
import com.mikealbert.data.entity.MaintenanceCode;
import com.mikealbert.data.entity.MaintenanceRechargeCode;
import com.mikealbert.data.entity.MaintenanceRequest;
import com.mikealbert.data.entity.MaintenanceRequestTask;
import com.mikealbert.data.entity.QuotationModel;
import com.mikealbert.data.entity.ServiceProvider;
import com.mikealbert.data.entity.UomCode;
import com.mikealbert.data.enumeration.CorporateEntity;
import com.mikealbert.service.FleetMasterService;
import com.mikealbert.service.LookupCacheService;
import com.mikealbert.service.MaintenanceRequestService;
import com.mikealbert.service.OdometerService;
import com.mikealbert.service.ServiceProviderService;
import com.mikealbert.service.UserService;
import com.mikealbert.testing.BaseSpec;
import com.mikealbert.util.MALUtilities;
import com.mikealbert.vision.service.VehicleMaintenanceService;

public class AuthorizePOTest extends BaseSpec{

	
	@Resource OdometerService odometerService;
	@Resource MaintenanceRequestTaskDAO maintenanceRequestTaskDAO;
	@Resource LookupCacheService lookupCacheService;
	@Resource ServiceProviderService serviceProviderService;
	@Resource VehicleMaintenanceService vehicleMaintenanceService;
	@Resource FleetMasterService fleetMasterService;
	@Resource UserService userService;
	@Resource MaintenanceRequestService maintRequestService;
	@Resource FleetMasterDAO fleetMasterDAO;
	@Resource QuotationModelDAO quotationModelDAO;
	@Resource ContractLineDAO contractLineDAO;
	
    public static final String MAINT_REQUEST_STATUS_BOOKED_IN = "B";    
    public static final String USERNAME = "RECCA_B";
    private long mrqId = 0L;
    
    private boolean buyerAuthLimitValidationFailed;
    private boolean clientRechargeAuthorization;
    private boolean isWaitOnClientApprovalStatus;
    private boolean isInProgressStatus;
    private boolean bookedInStatus;
    private boolean completeStatus;

	
    public void testAuthorizationLimitWhileAuthorizingPO(String unitPrice, String qty, String clientApprovalInfo){
		try{			
			
			clientRechargeAuthorization = true;
			buyerAuthLimitValidationFailed = false;
			isWaitOnClientApprovalStatus = false;
			bookedInStatus = false;
			isInProgressStatus = false;
			completeStatus = false;
			
			Query query = em.createNativeQuery(TestQueryConstants.READ_QMD_ID_FOR_ON_CONTRACT_UNIT);
			BigDecimal qmdId = (BigDecimal)query.getSingleResult();
			QuotationModel quotationModel = quotationModelDAO.findById(qmdId.longValue()).orElse(null);
			String unitNo = quotationModel.getUnitNo();
			MaintenanceRequest po = buildPO(unitNo);
			if(!MALUtilities.isEmptyString(clientApprovalInfo)){
				po.setAuthReference(clientApprovalInfo);
			}
			buildPOTaskItem(po, USERNAME, unitPrice, qty);
			MaintenanceRequest savedPO = maintRequestService.saveOrUpdateMaintnenacePO(po, USERNAME);
			mrqId = savedPO.getMrqId();
			em.clear();
			
			MaintenanceRequest fromDBPO1 = maintRequestService.getMaintenanceRequestByMrqId(mrqId);

			clientRechargeAuthorization = maintRequestService.isRechargeTotalWithinLimit(fromDBPO1);

			if(clientRechargeAuthorization){
				String message = maintRequestService.validateFleetMaintAuthBuyerLimit(fromDBPO1, CorporateEntity.MAL, USERNAME);
				if(!MALUtilities.isEmptyString(message)){
					buyerAuthLimitValidationFailed = true;
				}
			}
			
			maintRequestService.authorizeMRQ(fromDBPO1, CorporateEntity.MAL, USERNAME);
			em.clear();
			
			MaintenanceRequest fromDBPO2 = maintRequestService.getMaintenanceRequestByMrqId(mrqId);
			if(fromDBPO2.getMaintReqStatus().equals("WCA")){
				isWaitOnClientApprovalStatus = true;
			}
			if(fromDBPO2.getMaintReqStatus().equals("I")){
				isInProgressStatus = true;
			}
			
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}   
   
	public void testAuthorizationLimitWhileCompletingPO(String unitPrice, String qty, String clientApprovalInfo){
		try{			
			
			MaintenanceRequest fromDBPO1 = maintRequestService.getMaintenanceRequestByMrqId(mrqId);
			fromDBPO1.setAuthReference(clientApprovalInfo);
			clientRechargeAuthorization = maintRequestService.isRechargeTotalWithinLimit(fromDBPO1);

			if(clientRechargeAuthorization){
				String message = maintRequestService.validateFleetMaintAuthBuyerLimit(fromDBPO1, CorporateEntity.MAL, USERNAME);
				if(!MALUtilities.isEmptyString(message)){
					buyerAuthLimitValidationFailed = true;
				}
			}
			
			if(!MALUtilities.isEmptyString(clientApprovalInfo)){
				fromDBPO1.setAuthReference(clientApprovalInfo);
			}
			
			if(!fromDBPO1.getMaintReqStatus().equals("C")){
				maintRequestService.completeMRQ(fromDBPO1, CorporateEntity.MAL, USERNAME);
			}
			em.clear();
			
			MaintenanceRequest fromDBPO2 = maintRequestService.getMaintenanceRequestByMrqId(mrqId);
			if(fromDBPO2.getMaintReqStatus().equals("WCA")){
				isWaitOnClientApprovalStatus = true;
			}
			if(fromDBPO2.getMaintReqStatus().equals("I")){
				isInProgressStatus = true;
			}
			if(fromDBPO2.getMaintReqStatus().equals("C")){
				completeStatus = true;
			}
			
			
		}catch(Exception ex){
			ex.printStackTrace();
		}
	} 
    

	
	private MaintenanceRequest buildPO(String unitNo){
    	Calendar calendar = Calendar.getInstance();
    	MaintenanceRequest po = new MaintenanceRequest();
    	po.setMaintenanceRequestTasks(new ArrayList<MaintenanceRequestTask>());
    	po.setFleetMaster(fleetMasterService.findByUnitNo(unitNo));
    	po.setMaintReqStatus("B");
    	po.setMaintReqType("MAINT");
    	po.setActualStartDate(MALUtilities.getDateObject("07/31/2012", "MM/dd/yyyy"));
    	calendar.add(Calendar.DAY_OF_MONTH, 1);    	
    	po.setPlannedEndDate(calendar.getTime()); 
    	po.setJobNo(vehicleMaintenanceService.generateJobNumber(CorporateEntity.MAL));    	
    	PageRequest page = new PageRequest(0,1);
    	List<ServiceProvider> serviceProviders = serviceProviderService.getServiceProviderByNameOrCode("00086050",page);
    	po.setServiceProvider(serviceProviders.get(0));
    	po.setCurrentOdo(34162L);
    	UomCode uomCode = odometerService.convertOdoUOMCode("MILE");
    	po.setUnitofMeasureCode(uomCode);
    	
		ServiceProvider serviceProvider = getServiceProvider("CSC32");
		po.setServiceProvider(serviceProvider);
		po.setMaintReqStatus(MAINT_REQUEST_STATUS_BOOKED_IN);
		po.setServiceProviderMarkupInd(MalConstants.FLAG_N);
		
    	return po;
	}
	
	
	private void buildPOTaskItem(MaintenanceRequest po, String username, String unitPrice, String qty){
		
		MaintenanceRequestTask maintReqTask = new MaintenanceRequestTask(); 
		Long lineNumber = 0l;
		lineNumber = MALUtilities.isEmpty(po.getMaintenanceRequestTasks()) ? 1l : po.getMaintenanceRequestTasks().size() + 1l;
		
		maintReqTask = new MaintenanceRequestTask();
		maintReqTask.setMaintenanceRequest(po);		
		maintReqTask.setRechargeFlag("Y");		
		MaintenanceRechargeCode rechargeCode = vehicleMaintenanceService.getDefaultMaintRechargeCode(po);
		if(!MALUtilities.isEmpty(rechargeCode)){
			maintReqTask.setRechargeCode(rechargeCode.getCode());	
		}
		maintReqTask.setOutstanding(DataConstants.DEFAULT_N);
		maintReqTask.setWasOutstanding(DataConstants.DEFAULT_N);
		maintReqTask.setLineNumber(lineNumber);
		maintReqTask.setIndex(lineNumber.intValue());				
		maintReqTask.setAuthorizePerson(username);
		if (!MALUtilities.isEmptyString(po.getServiceProvider().getNetworkVendor())){
			maintReqTask.setDiscountFlag(po.getServiceProvider().getNetworkVendor());
		}else{
			maintReqTask.setDiscountFlag(DataConstants.DEFAULT_N);
		}
		String sqlString = "select * from MAINTENANCE_CODES where MAINT_CODE = '500-102'";
		List<MaintenanceCode> maintenanceCodes = em.createNativeQuery(sqlString, MaintenanceCode.class).getResultList(); 
		maintReqTask.setMaintenanceCode(maintenanceCodes.get(0));
		if(!MALUtilities.isEmptyString(qty)){
			maintReqTask.setTaskQty(new BigDecimal(qty));	
		}else{
			maintReqTask.setTaskQty(new BigDecimal(1));	
		}
		
		
		if(!MALUtilities.isEmptyString(unitPrice)){
			maintReqTask.setUnitCost(new BigDecimal(unitPrice));
			if(!MALUtilities.isEmptyString(qty)){
				maintReqTask.setTotalCost(new BigDecimal(unitPrice).multiply(new BigDecimal(qty)));
			}else{
				maintReqTask.setTotalCost(new BigDecimal(unitPrice));
			}
		}else{
			maintReqTask.setUnitCost(new BigDecimal(25000));
			maintReqTask.setTotalCost(new BigDecimal(25000));
		}
		maintReqTask.setMaintCatCode("A/C");
		
		po.getMaintenanceRequestTasks().add(maintReqTask);
		
		maintRequestService.applyMarkUp(po, maintRequestService.calculateNonNetworkRechargeMarkup(po));
				
	}	
	
	private ServiceProvider getServiceProvider(String nameOrCode){
		ServiceProvider serviceProvider = null;
		PageRequest page = new PageRequest(0,2);
		if(!"".equals(nameOrCode)){
			List<ServiceProvider> providers = serviceProviderService.getServiceProviderByNameOrCode(nameOrCode,page);
			if(providers.size() == 1){
				if(providers.get(0) != null ){
					serviceProvider = providers.get(0);
				}
			}
		}
		return serviceProvider;
	}

	public boolean isBuyerAuthLimitValidationFailed() {
		return buyerAuthLimitValidationFailed;
	}

	public void setBuyerAuthLimitValidationFailed(
			boolean buyerAuthLimitValidationFailed) {
		this.buyerAuthLimitValidationFailed = buyerAuthLimitValidationFailed;
	}

	public boolean isClientRechargeAuthorization() {
		return clientRechargeAuthorization;
	}

	public void setClientRechargeAuthorization(boolean clientRechargeAuthorization) {
		this.clientRechargeAuthorization = clientRechargeAuthorization;
	}

	public boolean isWaitOnClientApprovalStatus() {
		return isWaitOnClientApprovalStatus;
	}

	public void setWaitOnClientApprovalStatus(boolean isWaitOnClientApprovalStatus) {
		this.isWaitOnClientApprovalStatus = isWaitOnClientApprovalStatus;
	}


	public boolean isInProgressStatus() {
		return isInProgressStatus;
	}


	public void setInProgressStatus(boolean isInProgressStatus) {
		this.isInProgressStatus = isInProgressStatus;
	}

	public boolean isBookedInStatus() {
		return bookedInStatus;
	}

	public void setBookedInStatus(boolean bookedInStatus) {
		this.bookedInStatus = bookedInStatus;
	}

	public boolean isCompleteStatus() {
		return completeStatus;
	}

	public void setCompleteStatus(boolean completeStatus) {
		this.completeStatus = completeStatus;
	}



	
	
}
