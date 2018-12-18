package com.mikealbert.vision.view.bean.components;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.mikealbert.data.entity.MaintenancePreferenceAccount;
import com.mikealbert.data.vo.MaintenanceContactsVO;
import com.mikealbert.data.vo.MaintenancePreferencesVO;
import com.mikealbert.data.vo.MaintenanceProgramVO;
import com.mikealbert.data.vo.VehicleInformationVO;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.service.ClientPOCService;
import com.mikealbert.service.MaintenanceRequestService;
import com.mikealbert.service.QuotationService;
import com.mikealbert.util.MALUtilities;
import com.mikealbert.vision.service.VehicleMaintenanceService;
import com.mikealbert.vision.view.bean.BaseBean;

@Component
@Scope("view")
public class maintenanceProfileBean extends BaseBean {	
	private static final long serialVersionUID = 4806334161874878990L;
	
	@Resource VehicleMaintenanceService vehicleMaintenanceService;
	@Resource QuotationService quotationService;
	@Resource MaintenanceRequestService maintenanceRequestService;
	
	private VehicleInformationVO vehicleInfo;
	private MaintenancePreferenceAccount maintPrefAcct;
	private List <MaintenanceProgramVO> maintPrograms;
	private List <MaintenancePreferencesVO> maintPreferences;
	private List<MaintenanceContactsVO> maintContacts;
	private BigDecimal mafsAuthorizationLimit;
	private BigDecimal driverAuthorizationLimit;
	private	String	scheduleType;
	
    public void initDialog(Long fmsId){
    	try {		
    		if(fmsId != null){

    			setVehicleInfo(vehicleMaintenanceService.getVehicleInformationByFmsId(fmsId));      		

	    		loadMaintenancePreferenceAccount();
	    		getAndSetScheduleType();
	    		loadMaintenancePrograms();  
	    		loadMaintenancePreferences();
	    		loadMaintenanceContacts();
	    		loadAuthorizationLimit();
    		}
    		
		} catch(Exception e) {
			super.addErrorMessage("generic.error", e.getMessage());
		}  
    }
    private	void	getAndSetScheduleType(){
    	if(maintPrefAcct != null){
    		scheduleType = vehicleMaintenanceService.getClientScheduleTypeCode(maintPrefAcct.getClientScheduleId());
    	}
    }
    
    private void loadMaintenancePreferenceAccount(){
		if(!MALUtilities.isEmpty(vehicleInfo.getClnId())){
			maintPrefAcct = vehicleMaintenanceService.getMaintenancePreferenceAccount(vehicleInfo);
		}
    }    
    
    private void loadMaintenancePrograms(){
		if(!MALUtilities.isEmpty(vehicleInfo.getClnId())){
			maintPrograms = vehicleMaintenanceService.getMaintenancePrograms(vehicleInfo);
		}
	} 
    
    private void loadMaintenancePreferences(){
		if(!MALUtilities.isEmpty(vehicleInfo.getClnId())){
			maintPreferences = vehicleMaintenanceService.getMaintenancePreferences(vehicleInfo);
		}			
    }
    
    private void loadMaintenanceContacts(){
    	//mss-459
    	if(maintContacts == null)
    		maintContacts	= new ArrayList<MaintenanceContactsVO>();
    	maintContacts.clear();
    	if(!MALUtilities.isEmpty(vehicleInfo.getClnId())){
    		try {
    			//List<MaintenanceContactsVO>	maintPoAuthList = maintenanceRequestService.getContacts(ClientPOCService.POC_NAME_MAINT_EXCEED_AUTH_LIMIT, vehicleInfo);
    			//getAllClientContactVOs
    			List<MaintenanceContactsVO>	maintPoAuthList = maintenanceRequestService.getAllClientContactVOs(ClientPOCService.POC_NAME_MAINT_EXCEED_AUTH_LIMIT, vehicleInfo);
    			if(maintPoAuthList != null ){
    				for (MaintenanceContactsVO maintenanceContactsVO : maintPoAuthList) {
    					maintenanceContactsVO.setPocDescription("Maintenance PO Authorization Approval");
    					
					}
    				maintContacts.addAll( maintPoAuthList);
    				
    			}
    			List<MaintenanceContactsVO>	vehSchedulePocList = maintenanceRequestService.getAllClientContactVOs(ClientPOCService.POC_NAME_VEHICLE_SCHEDULE, vehicleInfo);
    			if(vehSchedulePocList != null){
    				for (MaintenanceContactsVO maintenanceContactsVO : vehSchedulePocList) {
    					maintenanceContactsVO.setPocDescription("Vehicle Maintenance Schedule");
					}
    				maintContacts.addAll( vehSchedulePocList);
    			}
				
			} catch (Exception e) {
				super.addErrorMessage("generic.error", e.getMessage());
			}
    	}
    }
    
    private void loadAuthorizationLimit() throws MalBusinessException{
    	Long corpId = vehicleInfo.getClientCorporateId();
    	String accountType = vehicleInfo.getClientAccountType();
    	String accountCode = vehicleInfo.getClientAccountNumber();
    	String unitNo = vehicleInfo.getUnitNo();
    	
    	if(!MALUtilities.isEmpty(vehicleInfo.getClnId())){
    		BigDecimal limit = quotationService.getMafsAuthorizationLimit(corpId, accountType, accountCode, unitNo);
	    	setMafsAuthorizationLimit(limit != null ? limit : BigDecimal.ZERO);
	    	limit = quotationService.getDriverAuthorizationLimit(corpId, accountType, accountCode, unitNo);
	    	setDriverAuthorizationLimit(limit != null ? limit : BigDecimal.ZERO);
    	}	    	
    }    
	
	public MaintenancePreferenceAccount getMaintPrefAcct() {
		return maintPrefAcct;
	}
	
	public void setMaintPrefAcct(MaintenancePreferenceAccount maintPrefAcct) {
		this.maintPrefAcct = maintPrefAcct;
	}
	
	public List <MaintenanceProgramVO> getMaintPrograms() {
		return maintPrograms;
	}
	
	public void setMaintPrograms(List <MaintenanceProgramVO> maintPrograms) {
		this.maintPrograms = maintPrograms;
	}
	
	public List <MaintenancePreferencesVO> getMaintPreferences() {
		return maintPreferences;
	}
	
	public void setMaintPreferences(List <MaintenancePreferencesVO> maintPreferences) {
		this.maintPreferences = maintPreferences;
	}
	
	public List<MaintenanceContactsVO> getMaintContacts() {
		return maintContacts;
	}
	
	public void setMaintContacts(List<MaintenanceContactsVO> maintContacts) {
		this.maintContacts = maintContacts;
	}
	
	public BigDecimal getMafsAuthorizationLimit() {
		return mafsAuthorizationLimit;
	}

	public void setMafsAuthorizationLimit(BigDecimal mafsAuthorizationLimit) {
		this.mafsAuthorizationLimit = mafsAuthorizationLimit;
	}

	public BigDecimal getDriverAuthorizationLimit() {
		return driverAuthorizationLimit;
	}

	public void setDriverAuthorizationLimit(BigDecimal driverAuthorizationLimit) {
		this.driverAuthorizationLimit = driverAuthorizationLimit;
	}

	public VehicleInformationVO getVehicleInfo() {
		return vehicleInfo;
	}

	public void setVehicleInfo(VehicleInformationVO vehicleInfo) {
		this.vehicleInfo = vehicleInfo;
	}

	public String getScheduleType() {
		return scheduleType;
	}

	public void setScheduleType(String scheduleType) {
		this.scheduleType = scheduleType;
	}
	
}
