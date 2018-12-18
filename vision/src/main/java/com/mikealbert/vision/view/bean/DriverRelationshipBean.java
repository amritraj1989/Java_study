package com.mikealbert.vision.view.bean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.primefaces.event.SelectEvent;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.mikealbert.common.MalConstants;
import com.mikealbert.common.MalLogger;
import com.mikealbert.common.MalLoggerFactory;
import com.mikealbert.data.entity.Driver;
import com.mikealbert.data.entity.DriverRelationship;
import com.mikealbert.data.entity.RelationshipType;
import com.mikealbert.service.CustomerAccountService;
import com.mikealbert.service.DriverRelationshipService;
import com.mikealbert.service.DriverService;
import com.mikealbert.service.LookupCacheService;
import com.mikealbert.vision.view.ViewConstants;

@Component
@Scope("view")
public class DriverRelationshipBean extends StatefulBaseBean {	
	private static final long serialVersionUID = -4936009538570606854L;
	MalLogger logger = MalLoggerFactory.getLogger(this.getClass());
	@Resource DriverService driverService;
	@Resource CustomerAccountService customerAccountService;
	@Resource DriverRelationshipService driverRelationshipService;
	@Resource LookupCacheService lookCacheService;
	
	private Driver mainDriver;
	private boolean mainDriverIsInactive;
	private String selectedAccountCode;
	private String driverName;
	private List<Driver> availableDrivers = new ArrayList<Driver>();
	private List<DriverRelationship> driverRelationships = new ArrayList<DriverRelationship>();		
	private List<RelationshipType> relationshipTypes;
	private Driver selectedDriver;	
	private DriverRelationship selectedDriverRelationship;
	private int DEFAULT_DATATABLE_HEIGHT = 175;
	private int ROWS_IN_DEFAULT_DATATABLE_HEIGHT = 4;
		
	/**
	 * Initializes the bean
	 */
    @PostConstruct
    public void init(){
    	logger.debug("init:start");
    	initializeDataTable(600, 200, new int[] { 17, 18, 4, 15}).setHeight(DEFAULT_DATATABLE_HEIGHT);      	
    	super.openPage();
    	
        try { 
        	if(mainDriver == null) {
        		super.addErrorMessage("no.records.found");
        	}   
        	
        	this.mainDriverIsInactive = mainDriver.getActiveInd().equalsIgnoreCase(MalConstants.FLAG_Y) ? false : true;

        	//Get the primary driver's relationships
        	this.setDriverRelationships(driverRelationshipService.getDriverRelationships(mainDriver.getDrvId()));
        	
        	//Possible relationship types
        	this.setRelationshipTypes(this.lookCacheService.getRelationshipTypes());
        	
        	//Filter available drivers list on primary driver last name
        	if(this.driverName == null)
        		this.setDriverName(this.getMainDriver().getDriverSurname());           	
        	this.search();
        	
        	this.sortRelatedDrivers();
        	logger.debug("init:end");
		} catch(Exception ex) {
			logger.error(ex);
			super.addErrorMessage("generic.error", ex.getMessage());
		}
    }
    
    /**
     * Handles the row selection event.  
     * @param event
     */
    public void onRowSelect(SelectEvent event){
    	if (event.getObject() instanceof Driver) {
    		this.selectedDriverRelationship = null;    		
    	} else {
    		this.selectedDriver = null;    		
    	}    	
    }
    
    /**
     * Filters the available driver
     */
    public void search(){ 
    	logger.debug("search:start");
    	List<Driver> filteredAvailableDrivers;    	    	
    	
    	filteredAvailableDrivers = driverRelationshipService.getAvailableDrivers(this.getMainDriver(), this.getDriverName());    	
    	filteredAvailableDrivers.remove(this.mainDriver);   	
    	
    	//Remove related drivers from the available list
    	for(DriverRelationship dr : this.driverRelationships)
    		filteredAvailableDrivers.remove(dr.getSecondaryDriver());
    	    		
        this.availableDrivers.clear();
        this.availableDrivers.addAll(filteredAvailableDrivers);
        if(availableDrivers.size() > ROWS_IN_DEFAULT_DATATABLE_HEIGHT){
        	getDataTable().setMaximumHeight();
        }else{
        	getDataTable().setHeight(DEFAULT_DATATABLE_HEIGHT);
        }
        this.sortAvaialbleDrivers();
        logger.debug("search:end");
    }
    
    
    /**
     * 
     */
    public void editDriver(Driver driver, DriverRelationship driverRelationship){
    	Driver relatedDriver;
		try {	
			if(driverRelationship != null){
				this.selectedDriverRelationship = driverRelationship;
				relatedDriver = this.selectedDriverRelationship.getSecondaryDriver();
			} else {
				this.selectedDriver = driver;
				relatedDriver = this.selectedDriver;
			}
			logger.debug("editDriver:relatedDriver="+relatedDriver.getDrvId());
			//Setup restore map 
			// - Pass the drvId of the primary driver
			// - when user exists the add/edit page, the available driver list should be re-queried.
			Map<String, Object> restoreStateValues = new HashMap<String, Object>();
			restoreStateValues.put(ViewConstants.VIEW_PARAM_DRIVER_ID, this.mainDriver.getDrvId());
			restoreStateValues.put(ViewConstants.VIEW_PARAM_DRIVER_NAME, this.driverName);
			super.saveRestoreStateValues(restoreStateValues);
					
			Map<String, Object> nextPageValues = new HashMap<String, Object>();				
			nextPageValues.put(ViewConstants.VIEW_PARAM_DRIVER_ID, String.valueOf(relatedDriver.getDrvId()));
			nextPageValues.put(ViewConstants.VIEW_PARAM_MODE, ViewConstants.VIEW_MODE_EDIT);
			nextPageValues.put(ViewConstants.VIEW_PARAM_CALLERS_NAME, ViewConstants.DRIVER_RELATIONSHIP);			
			
			super.saveNextPageInitStateValues(nextPageValues);
			logger.debug("editDriver:end");
			super.forwardToURL(ViewConstants.DRIVER_ADD);		
			   
		} catch (Exception ex) {
			logger.error(ex);
			super.addErrorMessage("generic.error", ex.getMessage());
		}
		   	
    }
    
    public void addRelationship(){
    	logger.debug("addRelationship:start");
    	if(this.selectedDriver != null){
        	this.driverRelationships.add(new DriverRelationship(this.mainDriver, this.selectedDriver, null));
    		this.availableDrivers.remove(this.selectedDriver);
    		this.selectedDriverRelationship = null;
    		this.selectedDriver = null;
    	}
    	logger.debug("addRelationship:end");
    }
    
    public void addRelationships(){
    	logger.debug("addRelationships:start");
    	if(this.availableDrivers.size() > 0){
    		for(Driver driver : this.availableDrivers)
    			this.driverRelationships.add(new DriverRelationship(this.mainDriver, driver, null));
    		this.availableDrivers.clear();
    		this.sortRelatedDrivers();
    		this.selectedDriverRelationship = null;
    		this.selectedDriver = null;    			
    	}
    	logger.debug("addRelationships:end");
    } 
    
    public void removeRelationship(){   
    	logger.debug("removeRelationship:start");
    	if(this.selectedDriverRelationship != null){   		
    		this.availableDrivers.add(this.selectedDriverRelationship.getSecondaryDriver());    		
        	this.driverRelationships.remove(this.selectedDriverRelationship);
        	this.selectedDriverRelationship = null;
    		this.selectedDriver = null;
    	} 
    	logger.debug("removeRelationship:end");
    }
    
    public void removeRelationships(){
    	logger.debug("removeRelationships:start");
    	if(this.driverRelationships.size() > 0){
    		for(DriverRelationship dr : this.driverRelationships)
    			this.availableDrivers.add(dr.getSecondaryDriver());
    		this.driverRelationships.clear();
    		this.selectedDriverRelationship = null;
    		this.selectedDriver = null;
    	} 
    	logger.debug("removeRelationships:end");
    }        
        
    /**
     * Handles page save button click event
     * @return The calling view or null based on whether the process succeeded for failed, respectively
     */
    public String save(){ 
    	logger.debug("save:start");
		try {
			if(isValid()) {				
				this.driverRelationships = driverRelationshipService.saveOrUpdateRelatedDrivers(this.mainDriver, this.driverRelationships);
				super.addSuccessMessage("process.success", "Save Related driver changes"); 	
				logger.debug("save:end");
				return this.cancelPage();   
			}
			
		} catch (Exception ex) {
			logger.error(ex);
			super.addErrorMessage("generic.error", ex.getMessage());
		}
		
		return null;
    }
    
    /**
     * Handles page add button click event
     * @return The calling view
     */    
    public String addDriver(){
		try {	
			logger.debug("addDriver:start");
			//Setup restore map 
			// - Pass the drvId of the primary driver
			// - when user exists the add/edit page, the available driver list should be re-queried.
			Map<String, Object> restoreStateValues = new HashMap<String, Object>();
			restoreStateValues.put(ViewConstants.VIEW_PARAM_DRIVER_ID, this.mainDriver.getDrvId());
			restoreStateValues.put(ViewConstants.VIEW_PARAM_DRIVER_NAME, this.driverName);
			super.saveRestoreStateValues(restoreStateValues);
					
			Map<String, Object> nextPageValues = new HashMap<String, Object>();				
			nextPageValues.put(ViewConstants.VIEW_PARAM_DRIVER_ID, String.valueOf(this.mainDriver.getDrvId()));
			nextPageValues.put(ViewConstants.VIEW_PARAM_MODE, ViewConstants.VIEW_MODE_ADD);	
			nextPageValues.put(ViewConstants.VIEW_PARAM_CALLERS_NAME, ViewConstants.DRIVER_RELATIONSHIP);			
			super.saveNextPageInitStateValues(nextPageValues);
			logger.debug("addDriver:end");
			super.forwardToURL(ViewConstants.DRIVER_ADD);
			   
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
    
    public String getAvailableDriversCount(){
    	return String.valueOf(this.availableDrivers.size());
    }
    
    public String getRelatedDriversCount(){
    	return String.valueOf(this.driverRelationships.size());
    }    
            
	/**
	 * Validates user's input
	 * @return True or False based on whether the validation passed or failed, respectively
	 */
	private boolean isValid(){
    	boolean isValid = true;		
		return isValid;
	}
	

	/**
	 * Pattern for retrieving stateful data passed from calling view.
	 */
	protected void loadNewPage() {
		Map<String, Object> map = super.thisPage.getInputValues();
		Long driverId;
		
		thisPage.setPageDisplayName(ViewConstants.DISPLAY_NAME_DRIVER_RELATIONSHIP);
		thisPage.setPageUrl(ViewConstants.DRIVER_RELATIONSHIP);	
		
		if(map.containsKey(ViewConstants.VIEW_PARAM_DRIVER_ID)){
			driverId = Long.parseLong(((map.get(ViewConstants.VIEW_PARAM_DRIVER_ID)).toString()));
			this.mainDriver = driverService.getDriver(driverId);
		}	
	
	} 
	
	/**
	 * Pattern for restoring the view's data
	 */
	protected void restoreOldPage() {
		Long driverId;
		String searchName;
		
		driverId = (Long)thisPage.getRestoreStateValues().get(ViewConstants.VIEW_PARAM_DRIVER_ID);
		searchName = (String)thisPage.getRestoreStateValues().get(ViewConstants.VIEW_PARAM_DRIVER_NAME);
		
		setMainDriver(driverService.getDriver(driverId));					
		setDriverName(searchName);
	}
	
	/**
	 * Sorts the available drivers list in ascending order by lastname, firstname
	 */
	private void sortAvaialbleDrivers(){
		if(this.availableDrivers != null && this.availableDrivers.size() > 0){
			Collections.sort(this.availableDrivers, new Comparator<Driver>() {
				public int compare(Driver driver1, Driver driver2) {	
					String name1 = driver1.getDriverSurname() + ", " + driver1.getDriverForename();
					String name2 = driver2.getDriverSurname() + ", " + driver2.getDriverForename();					
					return name1.toLowerCase().compareTo(name2.toLowerCase());				      	
				}			
			});			
		}		
	}

	/**
	 * Sorts the related drivers list in ascending order by lastname, firstname
	 */
	private void sortRelatedDrivers(){
		if(this.driverRelationships != null && this.driverRelationships.size() > 0){
			Collections.sort(this.driverRelationships, new Comparator<DriverRelationship>() {
				public int compare(DriverRelationship dr1, DriverRelationship dr2) {
					String name1 = dr1.getSecondaryDriver().getDriverSurname() + ", " + dr1.getSecondaryDriver().getDriverForename();
					String name2 = dr2.getSecondaryDriver().getDriverSurname() + ", " + dr2.getSecondaryDriver().getDriverForename();
					return name1.toLowerCase().compareTo(name2.toLowerCase());						
				}			
			});			
		}		
	}
	
	public Driver getMainDriver() {
		return mainDriver;
	}

	public void setMainDriver(Driver mainDriver) {
		this.mainDriver = mainDriver;
	}

	public String getSelectedAccountCode() {
		return selectedAccountCode;
	}

	public void setSelectedAccountCode(String selectedAccountCode) {
		this.selectedAccountCode = selectedAccountCode;
	}

	public String getDriverName() {
		return driverName;
	}

	public void setDriverName(String driverName) {
		this.driverName = driverName;
	}

	public Driver getSelectedDriver() {
		return selectedDriver;
	}

	public void setSelectedDriver(Driver selectedDriver) {
		this.selectedDriver = selectedDriver;
	}
	
	public List<Driver> getAvailableDrivers() {
		return availableDrivers;
	}

	public void setAvailableDrivers(List<Driver> availableDrivers) {
		this.availableDrivers = availableDrivers;
	}

	public List<RelationshipType> getRelationshipTypes() {
		return relationshipTypes;
	}

	public void setRelationshipTypes(List<RelationshipType> relationshipTypes) {
		this.relationshipTypes = relationshipTypes;
	}

	public List<DriverRelationship> getDriverRelationships() {
		return driverRelationships;
	}

	public void setDriverRelationships(List<DriverRelationship> driverRelationships) {
		this.driverRelationships = driverRelationships;
	}

	public DriverRelationship getSelectedDriverRelationship() {
		return selectedDriverRelationship;
	}

	public void setSelectedDriverRelationship(DriverRelationship selectedDriverRelationship) {
		this.selectedDriverRelationship = selectedDriverRelationship;
	}

	public boolean isMainDriverIsInactive() {
		return mainDriverIsInactive;
	}

	public void setMainDriverIsInactive(boolean mainDriverIsInactive) {
		this.mainDriverIsInactive = mainDriverIsInactive;
	}	
	
   
}
