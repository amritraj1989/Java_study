package com.mikealbert.vision.specs.fleet;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;

import com.mikealbert.common.MalConstants;
import com.mikealbert.data.DataConstants;
import com.mikealbert.data.dao.MaintenanceRequestDAO;
import com.mikealbert.data.dao.OdometerDAO;
import com.mikealbert.data.dao.OdometerReadingDAO;
import com.mikealbert.data.entity.MaintenanceCode;
import com.mikealbert.data.entity.MaintenanceRechargeCode;
import com.mikealbert.data.entity.MaintenanceRequest;
import com.mikealbert.data.entity.MaintenanceRequestTask;
import com.mikealbert.data.entity.Odometer;
import com.mikealbert.data.entity.OdometerReading;
import com.mikealbert.data.entity.ServiceProvider;
import com.mikealbert.data.entity.ServiceProviderMaintenanceCode;
import com.mikealbert.data.entity.UomCode;
import com.mikealbert.data.enumeration.CorporateEntity;
import com.mikealbert.data.vo.AccountVO;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.service.FleetMasterService;
import com.mikealbert.service.MaintenanceCodeService;
import com.mikealbert.service.MaintenanceRequestService;
import com.mikealbert.service.OdometerService;
import com.mikealbert.service.ServiceProviderService;
import com.mikealbert.service.WillowConfigService;
import com.mikealbert.testing.BaseSpec;
import com.mikealbert.util.MALUtilities;
import com.mikealbert.vision.service.VehicleMaintenanceService;

public class LeasePlanServiceProviderTest extends BaseSpec{
	@Resource MaintenanceCodeService maintCodeService;
	@Resource VehicleMaintenanceService vehicleMaintenanceService;
	@Resource MaintenanceRequestService maintenanceRequestService;	
	@Resource ServiceProviderService serviceProviderService;
	@Resource FleetMasterService fleetMasterService;
	@Resource WillowConfigService willowConfigService;
	@Resource OdometerService odometerService;	
	@Resource OdometerDAO odometerDAO;	
	@Resource OdometerReadingDAO odometerReadingDAO;	
	@Resource private MaintenanceRequestDAO maintenanceRequestDAO;
	
	private boolean leasePlanServiceProvider;
	private MaintenanceRequest maintenanceRequest;
	private Long selectedProviderId;
	private BigDecimal markUpAmount = BigDecimal.valueOf(0.00).setScale(2, BigDecimal.ROUND_HALF_UP);
	private List<AccountVO> leasePlanPayeeList;
	private String savedServiceProviderAddress;
	private String generatedPO;
		
	@Value("${generic.opCode}")  String userName;
	
	public void testLeasePlanAddress(String unitNo, String serviceProviderCode, String serviceProviderAddress){
		initAddMode(unitNo);
		decodeProviderByNameOrCode(serviceProviderCode);
		try{
			getMaintenanceRequest().setServiceProviderContactInfo(serviceProviderAddress);
			addMaintenanceTaskItem(getMaintenanceRequest(), userName);
			setMaintenanceRequest(saveOrUpdateMaintnenacePO(getMaintenanceRequest(), userName, false));
			if(getMaintenanceRequest().getMrqId() != null && getMaintenanceRequest().getMrqId() != 0L){
				savedServiceProviderAddress = getMaintenanceRequest().getServiceProviderContactInfo();
			}
			
			
		}catch(Exception ex){
			ex.printStackTrace();	}
	}
	
	public void updateLeasePlanAddress(String serviceProviderCode, String serviceProviderAddress){
		try{
			maintenanceRequest = maintenanceRequestService.getMaintenanceRequestByJobNo(generatedPO);
			decodeProviderByNameOrCode(serviceProviderCode);
			getMaintenanceRequest().setServiceProviderContactInfo(serviceProviderAddress);
			setMaintenanceRequest(saveOrUpdateMaintnenacePO(getMaintenanceRequest(), userName, true));
			savedServiceProviderAddress = getMaintenanceRequest().getServiceProviderContactInfo();
			
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	private MaintenanceRequest saveOrUpdateMaintnenacePO(MaintenanceRequest po, String username, boolean update) throws MalBusinessException{
		MaintenanceRequest savedPO = null;
		BigDecimal lineMarkUp = null;
		//Stamp the user who's creating or updating the PO
		po.setLastChangedBy(username);
		po.setAuthBy(username);	//TODO Eventually will need to be captured only when the PO has been authorized
		
		//apply recharge total (total cost + mark up) per line
		for(MaintenanceRequestTask line : po.getMaintenanceRequestTasks()){
			if(MALUtilities.convertYNToBoolean(line.getRechargeFlag())){
				lineMarkUp = MALUtilities.isEmpty(line.getMarkUpAmount()) ? new BigDecimal("0.00") : line.getMarkUpAmount();
				line.setRechargeTotalCost(line.getTotalCost().add(lineMarkUp));
			}
		}
		
		//Performing operations specific to new or existing POs
		if(MALUtilities.isEmpty(po.getMrqId())){		
			po.setCreatedBy(username);			
			po.setMaintReqDate(Calendar.getInstance().getTime());
			po.setRequestClass("REQCLASS1"); //TODO: Remove hard code, i.e. make constant, enum, etc.
			po.setRevisionStatus("REVSTAT1");//TODO: Remove hard code, i.e. make constant, enum, etc. 
			po.setAuthMessage("xxxxx"); //TODO: Remove hard code, i.e. make constant, enum, etc. I have no clue as to what this is; this is the value for all POs
		}		
				
		//perform save and updates
		if(update){
			savedPO = maintenanceRequestDAO.saveAndFlush(po);
		}else{
			savedPO = maintenanceRequestDAO.save(po);
		}
		
        //save odometer reading only when there is a new reading.
		if(savedPO.getFleetMaster().getLatestOdometerReading().getOdoReading() != savedPO.getCurrentOdo()){
			Odometer odometer = null;
			OdometerReading odometerReading = null;			
			odometer = odometerDAO.findMaxByFmsId(savedPO.getFleetMaster().getFmsId());
			odometerReading = new OdometerReading();
			odometerReading.setOdometer(odometer);
			odometerReading.setReadingCode("SERVICE");
			odometerReading.setReading(savedPO.getCurrentOdo());
			odometerReading.setUserId(username);
			odometerReading.setReadingDate(Calendar.getInstance().getTime());
			odometerReading.setStmntInd("N");
			odometerReading = odometerReadingDAO.save(odometerReading);	
		}
		
		return savedPO;
	}
	
	
	
	@SuppressWarnings("unchecked")
	private void addMaintenanceTaskItem(MaintenanceRequest po, String username){
		
		MaintenanceRequestTask maintReqTask = new MaintenanceRequestTask(); 
		Long lineNumber = 0l;
		lineNumber = MALUtilities.isEmpty(po.getMaintenanceRequestTasks()) ? 1l : po.getMaintenanceRequestTasks().size() + 1l;
		
		maintReqTask = new MaintenanceRequestTask();
		maintReqTask.setMaintenanceRequest(po);		
		maintReqTask.setRechargeFlag(vehicleMaintenanceService.getDefaultMaintRechargeFlag(po));		
		MaintenanceRechargeCode rechargeCode = vehicleMaintenanceService.getDefaultMaintRechargeCode(po);
		if(!MALUtilities.isEmpty(rechargeCode)){
			maintReqTask.setRechargeCode(rechargeCode.getCode());	
		}
		maintReqTask.setOutstanding(DataConstants.DEFAULT_N);
		maintReqTask.setWasOutstanding(DataConstants.DEFAULT_N);
		maintReqTask.setTaskQty(new BigDecimal(1));
		maintReqTask.setTotalCost(new BigDecimal("0.00"));
		maintReqTask.setLineNumber(lineNumber);
		maintReqTask.setIndex(lineNumber.intValue());				
		maintReqTask.setAuthorizePerson(username);
		if (!MALUtilities.isEmptyString(po.getServiceProvider().getNetworkVendor())){
			maintReqTask.setDiscountFlag(po.getServiceProvider().getNetworkVendor());
		}else{
			maintReqTask.setDiscountFlag(DataConstants.DEFAULT_N);
		}
		maintReqTask.setMaintCatCode("MISC_MAINT");
		String sqlString = "select * from MAINTENANCE_CODES where MAINT_CODE = '100-209'";
		List<MaintenanceCode> maintenanceCodes = em.createNativeQuery(sqlString, MaintenanceCode.class).getResultList(); 
		maintReqTask.setMaintenanceCode(maintenanceCodes.get(0));
		maintReqTask.setTaskQty(new BigDecimal(1));
		maintReqTask.setUnitCost(new BigDecimal(10.50));
		maintReqTask.setTotalCost(new BigDecimal(10.50));
		
		po.getMaintenanceRequestTasks().add(maintReqTask);
	}	
	
    private void initAddMode(String unitNo){
    	Calendar calendar = Calendar.getInstance();   

    	setMaintenanceRequest(new MaintenanceRequest());
    	getMaintenanceRequest().setMaintenanceRequestTasks(new ArrayList<MaintenanceRequestTask>());
    	getMaintenanceRequest().setFleetMaster(fleetMasterService.findByUnitNo(unitNo));
    	getMaintenanceRequest().setServiceProvider(new ServiceProvider());
    	getMaintenanceRequest().setMaintReqStatus("B");
    	getMaintenanceRequest().setMaintReqType("MAINT");
    	getMaintenanceRequest().setActualStartDate(calendar.getTime());
    	calendar.add(Calendar.DAY_OF_MONTH, 1);    	
    	getMaintenanceRequest().setPlannedEndDate(calendar.getTime()); 
    	generatedPO = vehicleMaintenanceService.generateJobNumber(CorporateEntity.MAL);
    	getMaintenanceRequest().setJobNo(generatedPO);
    	getMaintenanceRequest().setCurrentOdo(34162L);
    	UomCode uomCode = odometerService.convertOdoUOMCode("MILE");
    	getMaintenanceRequest().setUnitofMeasureCode(uomCode);
    	getMaintenanceRequest().setServiceProviderMarkupInd(MalConstants.FLAG_N);
    }	
	
	private void decodeProviderByNameOrCode(String nameOrCode){
		//List<ServiceProvider> providers = vehicleMaintenanceService.getServiceProvidersByNameOrCode(nameOrCode);
		PageRequest page = new PageRequest(0,10);
		List<ServiceProvider> providers = serviceProviderService.getServiceProviderByNameOrCode(nameOrCode,page);
		if(providers.size() > 0){
			for(ServiceProvider serviceProvider :providers){
				if(serviceProvider.getServiceProviderNumber().equals(nameOrCode)){				
					this.updateServiceProviderRelatedFields(serviceProvider);
					maintenanceRequest.setServiceProvider(serviceProvider);
					break;
				}
			}
		}
	}
	
	private void updateServiceProviderRelatedFields(ServiceProvider provider){
		maintenanceRequest.setServiceProvider(provider);
		changeMarkUpAmount(calculateMarkUp());
		selectedProviderId = provider.getServiceProviderId();
		if(!MALUtilities.isEmpty(this.maintenanceRequest)){
			for(MaintenanceRequestTask task : maintenanceRequest.getMaintenanceRequestTasks()){
				if(!MALUtilities.isEmpty(task.getMaintenanceCode())){
					this.updateServiceProviderCodeFields(task.getMaintenanceCode(),task);
				}
			}
		}
		setLeasePlanServiceProvider(isLeasePlanServiceProvider(provider));
	}
	
	private boolean isLeasePlanServiceProvider(ServiceProvider provider){
		leasePlanPayeeList = willowConfigService.getLeasePlanPayeeDetail();
		if(!MALUtilities.isEmpty(leasePlanPayeeList) && leasePlanPayeeList.size() > 0){
			for(AccountVO account : leasePlanPayeeList){
				if(account.getAccountCode().equals(provider.getPayeeAccount().getExternalAccountPK().getAccountCode()) 
						&&  account.getCorpId() == provider.getPayeeAccount().getExternalAccountPK().getCId() 
						&& account.getAccountType().equals(provider.getPayeeAccount().getExternalAccountPK().getAccountType()) ){
					return true;
				}
			}
		}else{
			return false;
		}
		return false;
	}
	
    private void updateServiceProviderCodeFields(MaintenanceCode code, MaintenanceRequestTask task){
		if(!MALUtilities.isEmpty(code) && !MALUtilities.isEmpty(selectedProviderId) && selectedProviderId != 0){
			List<Long> serviceProviderIds = new ArrayList<Long>();
			serviceProviderIds.add(selectedProviderId);
			List<ServiceProviderMaintenanceCode> serviceProviderMaintCodes = maintCodeService.getServiceProviderMaintenanceByMafsCode(code.getCode(), serviceProviderIds, false);
			
			if(serviceProviderMaintCodes.size() == 0){
				task.setServiceProviderMaintenanceCode(null);
		    	task.setServiceProviderCodeLookup(null); //TODO: Make serviceProviderCodeLookup a bean variable rather than a transient item on the entity
			}else if(serviceProviderMaintCodes.size() == 1){
				task.setServiceProviderMaintenanceCode(serviceProviderMaintCodes.get(0));
				task.setServiceProviderCodeLookup(new String(serviceProviderMaintCodes.get(0).getCode()));
			}else if(serviceProviderMaintCodes.size() > 1){
				task.setServiceProviderMaintenanceCode(null);
				task.setServiceProviderCodeLookup(null);
			}
		}else{
			task.setServiceProviderMaintenanceCode(null);
			task.setServiceProviderCodeLookup(null);
		}
    }	
	
	private BigDecimal calculateMarkUp(){
		return maintenanceRequestService.calculateNonNetworkRechargeMarkup(getMaintenanceRequest());
	}	

	private void changeMarkUpAmount(BigDecimal newMarkUp){
		setMaintenanceRequest(maintenanceRequestService.applyMarkUp(getMaintenanceRequest(), newMarkUp));
		setMarkUpAmount(newMarkUp);
	}


	public MaintenanceRequest getMaintenanceRequest() {
		return maintenanceRequest;
	}



	public void setMaintenanceRequest(MaintenanceRequest maintenanceRequest) {
		this.maintenanceRequest = maintenanceRequest;
	}



	public BigDecimal getMarkUpAmount() {
		return markUpAmount;
	}



	public void setMarkUpAmount(BigDecimal markUpAmount) {
		this.markUpAmount = markUpAmount;
	}



	public void setLeasePlanServiceProvider(boolean isLeasePlanServiceProvider) {
		this.leasePlanServiceProvider = isLeasePlanServiceProvider;
	}

	public boolean getLeasePlanServiceProvider() {
		return this.leasePlanServiceProvider;
	}

	public String getSavedServiceProviderAddress() {
		return savedServiceProviderAddress;
	}



	public void setSavedServiceProviderAddress(String savedServiceProviderAddress) {
		this.savedServiceProviderAddress = savedServiceProviderAddress;
	}
	
	
}
