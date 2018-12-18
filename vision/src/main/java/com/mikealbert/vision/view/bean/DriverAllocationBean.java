package com.mikealbert.vision.view.bean;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import com.mikealbert.common.MalConstants;
import com.mikealbert.data.entity.Driver;
import com.mikealbert.data.entity.DriverAllocation;
import com.mikealbert.data.entity.FleetMaster;
import com.mikealbert.data.entity.Odometer;
import com.mikealbert.service.ContractService;
import com.mikealbert.service.CustomerAccountService;
import com.mikealbert.service.DriverAllocationService;
import com.mikealbert.service.DriverService;
import com.mikealbert.service.FleetMasterService;
import com.mikealbert.service.LookupCacheService;
import com.mikealbert.service.OdometerService;
import com.mikealbert.util.MALUtilities;
import com.mikealbert.vision.view.ViewConstants;

@Component
@Scope("view")
public class DriverAllocationBean extends StatefulBaseBean {
	private static final long serialVersionUID = -8806821952041781658L;
	

	@Resource FleetMasterService fleetMasterService;
	@Resource ContractService contractService;
	@Resource DriverService driverService;
	@Resource DriverAllocationService driverAllocationService;	
	@Resource LookupCacheService lookupCacheService;
	@Resource OdometerService odometerService;
	@Resource CustomerAccountService customerAccountService;
	
	private List<DriverAllocation> driverAllocations = new ArrayList<DriverAllocation>();
	private DriverAllocation selectedAllocation;	
	private List<FleetMaster> fleetMasters = null;
	private DriverAllocation newAllocation;
	private Long selectedDriverId;
	private String driverName;
	private Odometer odometer;
		
	/**
	 * Initializes the bean
	 */
    @PostConstruct
    public void init(){
    	super.openPage();
    	
        try { 
        	if(selectedAllocation == null) {
        		super.addErrorMessage("no.records.found");
        	}
        	
        	setNewAllocation(new DriverAllocation());
        	setOdometer(odometerService.getCurrentOdometer(getSelectedAllocation().getFleetMaster()));
        	getNewAllocation().setOdoUom(getOdometer().getUomCode());
        	        	
        	this.defaultDeallocationAllocationDates();
        	
		} catch(Exception ex) {
			logger.error(ex);
			super.addErrorMessage("generic.error", ex.getMessage());
		}
    }
    
    /**
     * Sets the new allocation's start date to today. Then sets, the old allocation 
     * end date to yesterday.
     */
    public void defaultDeallocationAllocationDates(){
    	Calendar calendar = Calendar.getInstance();    	
    	this.getNewAllocation().setAllocationDate(calendar.getTime());
		calendar.add(Calendar.DATE, -1);
    	this.getSelectedAllocation().setDeallocationDate(calendar.getTime());
    }
    
    /**
     * Handles page save button click event
     * @return The calling view or null based on whether the process succeeded for failed, respectively
     */
    public String save(){ 
		try {
			if(isValid()) {				
				newAllocation.setOpCode(getLoggedInUser().getEmployeeNo());
				newAllocation.setDriver(driverService.getActiveDriver(selectedDriverId));
				newAllocation.setFleetMaster(selectedAllocation.getFleetMaster());
				driverAllocationService.saveAndUpdateDriversAllocation(selectedAllocation, newAllocation);
				super.addSuccessMessage("process.success", "Allocate driver " + newAllocation.getDriver().getDriverSurname() 
						+ ", " + newAllocation.getDriver().getDriverForename() + " to unit " + newAllocation.getFleetMaster().getUnitNo() );

				return this.cancelPage();   
			}
		} catch (Exception ex) {
			logger.error(ex);
			super.addErrorMessage("generic.error", ex.getMessage());
		}
		
		return null;
    }
    
    /**
     * Handles page cancel button click event
     * @return The calling view
     */
    public String cancel(){
    	return super.cancelPage();      	
    }
            
	/**
	 * Validates user's input
	 * @return True or False based on whether the validation passed or failed, respectively
	 */
	private boolean isValid(){
    	boolean isValid = false;
    	
    	 if(MALUtilities.isEmpty(selectedDriverId)){
    		 decodeNewDriverByName(); //do nothing. Error message is already set;
		} else if(MALUtilities.isEmpty(selectedAllocation.getDeallocationDate())) {
			super.addErrorMessage("required.field", "Deallocation Date");
		} else if(MALUtilities.isEmpty(newAllocation.getAllocationDate())) {
			super.addErrorMessage("newDriverAllocationDate", "required.field", "Allocation Date");
		} else if(MALUtilities.isEmpty(newAllocation.getFromOdoReading())) {
			super.addErrorMessage("odoReading", "required.field", "Odo Reading");
		} else if(this.selectedAllocation.getDeallocationDate().compareTo(this.selectedAllocation.getAllocationDate()) <= 0){
			super.addErrorMessage("required.field", "A later allocation date");
		} else{
			isValid = true;
		}
		
		return isValid;
	}
	
	/**
	 * Decode functionality
	 */
	public boolean decodeNewDriverByName(){		
		boolean success = true;
		String customerCode= selectedAllocation.getFleetMaster().getContractLine().getContract().getExternalAccount().getExternalAccountPK().getAccountCode();
		List<Driver> resultList = driverService.getDrivers(driverName,customerAccountService.getReleatedAccountCodes(customerCode),getLoggedInUser().getCorporateEntity(),MalConstants.FLAG_Y ,new PageRequest(0,50));
		
		if(resultList.size() ==1){
			Driver  driver =  resultList.get(0);
			driverName = driver.getDriverSurname() +", " + driver.getDriverForename();
			selectedDriverId = driver.getDrvId();
		}else{
			 success = false;
			 selectedDriverId = null;
			 if(resultList.size() == 0){
				 super.addErrorMessage("driverName","decode.noMatchFound.msg","Driver " + driverName); 
			 }else{
				 super.addErrorMessage("driverName","decode.notExactMatch.msg","Driver " +  driverName);			
			 }			 
		}
		
		return success;		
	}
	public void validateCustomNewDriver(FacesContext context, UIComponent inputComponent, Object value) {
		if (MALUtilities.isEmpty(value)){
			super.addErrorMessage("driverName", "required.field",	"New Driver ");
			this.setSelectedDriverId(null);		
		}
	}
	
	/**
	 * Navigation code
	 */
	protected void loadNewPage() {
		Map<String, Object> map = super.thisPage.getInputValues();
		String unitNo;
		
		thisPage.setPageDisplayName(ViewConstants.DISPLAY_NAME_DRIVER_ALLOCATION);
		thisPage.setPageUrl(ViewConstants.DRIVER_ALLOCATION);	
		
		if(map.containsKey(ViewConstants.VIEW_PARAM_UNIT_NO)){
			unitNo= ((String) map.get(ViewConstants.VIEW_PARAM_UNIT_NO));
			selectedAllocation = fleetMasterService.getFleetMasterFilterCurrentAllocaton(
					unitNo,null).get(0).getDriverAllocationList().get(0);
		}	
	
	} 
	
	// new navigation code
	protected void restoreOldPage() {}
	
	public List<DriverAllocation> getDriverAllocations() {
		return driverAllocations;
	}

	public void setDriverAllocations(List<DriverAllocation> driverAllocations) {
		this.driverAllocations = driverAllocations;
	}

	public DriverAllocation getSelectedAllocation() {
		return selectedAllocation;
	}

	public void setSelectedAllocation(DriverAllocation selectedAllocation) {	
		this.selectedAllocation = selectedAllocation;
	}

	public List<FleetMaster> getFleetMasters() {
		return fleetMasters;
	}

	public void setFleetMasters(List<FleetMaster> fleetMasters) {
		this.fleetMasters = fleetMasters;
	}

	public DriverAllocation getNewAllocation() {
		return newAllocation;
	}

	public void setNewAllocation(DriverAllocation newAllocation) {
		this.newAllocation = newAllocation;
	}

	public Long getSelectedDriverId() {
		return selectedDriverId;
	}

	public void setSelectedDriverId(Long selectedDriverId) {
		this.selectedDriverId = selectedDriverId;
	}

	public String getDriverName() {
		return driverName;
	}

	public void setDriverName(String driverName) {
		this.driverName = driverName;
	}


	public Odometer getOdometer() {
		return odometer;
	}

	public void setOdometer(Odometer odometer) {
		this.odometer = odometer;
	}   
}
