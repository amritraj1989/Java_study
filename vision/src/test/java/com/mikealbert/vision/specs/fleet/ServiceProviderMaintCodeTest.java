package com.mikealbert.vision.specs.fleet;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.data.domain.PageRequest;

import com.mikealbert.data.dao.ServiceProviderMaintenanceCodeDAO;
import com.mikealbert.data.entity.MaintenanceCode;
import com.mikealbert.data.entity.MaintenanceRequestTask;
import com.mikealbert.data.entity.ServiceProvider;
import com.mikealbert.data.entity.ServiceProviderMaintenanceCode;
import com.mikealbert.service.MaintenanceCodeService;
import com.mikealbert.service.ServiceProviderService;
import com.mikealbert.testing.BaseSpec;
import com.mikealbert.util.MALUtilities;
import com.mikealbert.vision.service.VehicleMaintenanceService;

@SuppressWarnings("deprecation")
public class ServiceProviderMaintCodeTest  extends BaseSpec {

	@Resource VehicleMaintenanceService vehicleMaintenanceService;
	@Resource MaintenanceCodeService maintCodeService;
	@Resource ServiceProviderService serviceProviderService;
	@Resource ServiceProviderMaintenanceCodeDAO serviceProviderMaintenanceCodeDAO;
	private String serviceProvider;
	private Long selectedProviderId;
	private MaintenanceRequestTask selectedTask = null;
	private String maintenanceCategoryCode;
	private String maintenanceCode;
	private String maintenanceCodeDesc;
	private String serviceCode;
	private String errorMessage;
	private boolean done = true;
	private boolean save = true;
	
	public void testMaintenanceCodeSelect(String serviceProvider, String maintenanceCatCode, String maintCode){
		try{
		selectedTask = new MaintenanceRequestTask();	
		maintenanceCategoryCode = maintenanceCatCode;
		decodeProviderByNameOrCode(serviceProvider);
		maintenanceCodeAutoComplete(maintCode);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public List<ServiceProviderMaintenanceCode> testSeviceCodeList(String serviceProvider, String maintenanceCatCode, String maintCode){
		List<ServiceProviderMaintenanceCode> serviceCodes = null;
		List<MaintenanceCode> maintCodes = null;
		selectedTask = new MaintenanceRequestTask();
		try{
			maintenanceCategoryCode = maintenanceCatCode;
			decodeProviderByNameOrCode(serviceProvider);
			maintCodes = maintenanceCodeAutoComplete(maintCode);
			this.updateServiceProviderCodeFields(maintCodes.get(0),this.selectedTask);
			serviceCodes = serviceCodeAutoComplete(this.selectedTask.getServiceProviderCodeLookup());
			
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return serviceCodes;
	}
	
	public void testMaintenanceCatCode(String serviceProvider, String maintCode){
		selectedTask = new MaintenanceRequestTask();
		setMaintenanceCategoryCode(null);
		setServiceCode(null);
		List<MaintenanceCode> maintCodes = null;
		try{
			decodeProviderByNameOrCode(serviceProvider);
			maintCodes = maintenanceCodeAutoComplete(maintCode);
			setMaintenanceCategoryCode(new String(maintCodes.get(0).getMaintCatCode()));
			this.updateServiceProviderCodeFields(maintCodes.get(0),this.selectedTask);			
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
	}
	
	
	public void testServiceCodeSelect(String serviceProvider, String maintenanceCatCode, String maintServiceCode){
		maintenanceCategoryCode = maintenanceCatCode;
		setMaintenanceCode(null);
		setServiceCode(null);
		selectedTask = new MaintenanceRequestTask();
		decodeProviderByNameOrCode(serviceProvider);
		serviceCodeAutoComplete(maintServiceCode);
		if(serviceCode == null){
			setErrorMessage("No match found for Service Code " + maintServiceCode + " text should remain in box and cursor should be there to correct it");
		}
	}
    
	public void testMaintCodeCategory(String serviceProvider, String maintServiceCode){
		setMaintenanceCategoryCode(null);
		setMaintenanceCode(null);
		setServiceCode(null);
		selectedTask = new MaintenanceRequestTask();
		decodeProviderByNameOrCode(serviceProvider);
		serviceCodeAutoComplete(maintServiceCode);
		if(serviceCode == null){
			setErrorMessage("No match found for Service Code " + maintServiceCode + " text should remain in box and cursor should be there to correct it");
		}
	}
	
    public void maintenanceCodeSelect(MaintenanceCode code){
    	this.selectedTask.setMaintenanceCode(code);
    	this.selectedTask.setMaintenanceCodeDesc(code.getDescription());
    	this.selectedTask.setWorkToBeDone(code.getDescription());
    	this.selectedTask.setMaintCatCode(code.getMaintCatCode());
    	this.selectedTask.setServiceProviderMaintenanceCode(null);
    	setMaintenanceCategoryCode(new String(code.getMaintCatCode()));
    	this.updateServiceProviderCodeFields(code,this.selectedTask);
    	serviceCodeAutoComplete(this.selectedTask.getServiceProviderCodeLookup());
    	setMaintenanceCode(code.getCode());
   }


    private void updateServiceProviderCodeFields(MaintenanceCode code, MaintenanceRequestTask task){
		if(!MALUtilities.isEmpty(code) && !MALUtilities.isEmpty(selectedProviderId) && selectedProviderId != 0){
			List<Long> serviceProviderIds = new ArrayList<Long>();
			serviceProviderIds.add(selectedProviderId);
			List<ServiceProviderMaintenanceCode> serviceProviderMaintCodes = maintCodeService.getServiceProviderMaintenanceByMafsCode(code.getCode(), serviceProviderIds, false);
			
			if(serviceProviderMaintCodes.size() == 0){
				task.setServiceProviderMaintenanceCode(null);
		    	task.setServiceProviderCodeLookup(null);
		    	setServiceCode(null);
			}else if(serviceProviderMaintCodes.size() == 1){
				task.setServiceProviderMaintenanceCode(serviceProviderMaintCodes.get(0));
				task.setServiceProviderCodeLookup(new String(serviceProviderMaintCodes.get(0).getCode()));
				setServiceCode(serviceProviderMaintCodes.get(0).getCode());
			}else if(serviceProviderMaintCodes.size() > 1){
				task.setServiceProviderMaintenanceCode(null);
				task.setServiceProviderCodeLookup(null);
				setServiceCode(null);
			}
		}else{
			task.setServiceProviderMaintenanceCode(null);
			task.setServiceProviderCodeLookup(null);
			setServiceCode(null);
		}
    }
    
    
    public List<ServiceProviderMaintenanceCode> serviceCodeAutoComplete(String query) {   
    	List<ServiceProviderMaintenanceCode> serivceCodes = new ArrayList<ServiceProviderMaintenanceCode>();
    	
    	if(!MALUtilities.isEmpty(selectedProviderId) && selectedProviderId != 0){
    		serivceCodes = vehicleMaintenanceService.getServiceCodesByCodeOrDesc(query, this.selectedTask.getMaintenanceCode() == null? null : this.selectedTask.getMaintenanceCode().getCode() , maintenanceCategoryCode,selectedProviderId);
    	}
    	if(serivceCodes != null && serivceCodes.size() == 1){
			serviceCodeSelect(serivceCodes.get(0));
		}    	
    	
    	
        return serivceCodes;   
    }

    public void serviceCodeSelect(ServiceProviderMaintenanceCode serviceCode){
		
		MaintenanceCode maintenanceCode = serviceCode.getMaintenanceCode();
				
		this.selectedTask.setMaintenanceCode(maintenanceCode);
    	this.selectedTask.setMaintenanceCodeDesc(maintenanceCode.getDescription());
    	this.selectedTask.setWorkToBeDone(maintenanceCode.getDescription());
    	this.selectedTask.setMaintCatCode(maintenanceCode.getMaintCatCode());
    	this.selectedTask.setServiceProviderMaintenanceCode(serviceCode);
    	this.selectedTask.setServiceProviderCodeLookup(serviceCode.getCode());
    	
    	setMaintenanceCategoryCode(maintenanceCode.getMaintCatCode());
    	setServiceCode(serviceCode.getCode());
    	setMaintenanceCode(maintenanceCode.getCode());
    	setMaintenanceCodeDesc(maintenanceCode.getDescription());
    }
    
    public List<MaintenanceCode> maintenanceCodeAutoComplete(String query) {   
    	List<MaintenanceCode> codes;
    	
    	if(!MALUtilities.isEmptyString(maintenanceCategoryCode)){
    		codes = vehicleMaintenanceService.getMaintenanceCodesByNameOrCode(query,maintenanceCategoryCode);
    	}else{
    		codes = vehicleMaintenanceService.getMaintenanceCodesByNameOrCode(query);
    	}
		if(codes != null && codes.size() == 1){
			maintenanceCodeSelect(codes.get(0));
		}   	
    	    	
        return codes;   
    }

	public void decodeProviderByNameOrCode(String nameOrCode){
		PageRequest page = new PageRequest(0,1);
		if(serviceProvider == null || !serviceProvider.equals(nameOrCode)){
			List<ServiceProvider> providers = serviceProviderService.getServiceProviderByNameOrCode(nameOrCode,page);
			if(providers.size() == 1){
				if(providers.get(0) != null ){
					selectedProviderId = providers.get(0).getServiceProviderId();
				}
			}
		}
	}
	
		
	public Long getSelectedProviderId() {
		return selectedProviderId;
	}


	public void setSelectedProviderId(Long selectedProviderId) {
		this.selectedProviderId = selectedProviderId;
	}


	public MaintenanceRequestTask getSelectedTask() {
		return selectedTask;
	}


	public void setSelectedTask(MaintenanceRequestTask selectedTask) {
		this.selectedTask = selectedTask;
	}


	public String getMaintenanceCategoryCode() {
		return maintenanceCategoryCode;
	}


	public void setMaintenanceCategoryCode(String maintenanceCategoryCode) {
		this.maintenanceCategoryCode = maintenanceCategoryCode;
	}


	public String getServiceProvider() {
		return serviceProvider;
	}


	public void setServiceProvider(String serviceProvider) {
		this.serviceProvider = serviceProvider;
	}


	public String getMaintenanceCode() {
		return maintenanceCode;
	}


	public void setMaintenanceCode(String maintenanceCode) {
		this.maintenanceCode = maintenanceCode;
	}


	public String getServiceCode() {
		return serviceCode;
	}


	public void setServiceCode(String serviceCode) {
		this.serviceCode = serviceCode;
	}

	public String getMaintenanceCodeDesc() {
		return maintenanceCodeDesc;
	}

	public void setMaintenanceCodeDesc(String maintenanceCodeDesc) {
		this.maintenanceCodeDesc = maintenanceCodeDesc;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public boolean isDone() {
		return done;
	}

	public void setDone(boolean done) {
		this.done = done;
	}

	public boolean isSave() {
		return save;
	}

	public void setSave(boolean save) {
		this.save = save;
	}
	
	
    
}
