package com.mikealbert.vision.view.bean;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;

import org.primefaces.context.RequestContext;
import org.primefaces.event.CloseEvent;
import org.primefaces.event.SelectEvent;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import com.mikealbert.common.MalConstants;
import com.mikealbert.data.beanvalidation.MANotNull;
import com.mikealbert.data.entity.AddressTypeCode;
import com.mikealbert.data.entity.CityZipCode;
import com.mikealbert.data.entity.CostCentreCode;
import com.mikealbert.data.entity.Driver;
import com.mikealbert.data.entity.DriverAddress;
import com.mikealbert.data.entity.DriverCostCenter;
import com.mikealbert.data.entity.DriverGrade;
import com.mikealbert.data.entity.DriverManualStatusCode;
import com.mikealbert.data.entity.ExternalAccount;
import com.mikealbert.data.entity.ExternalAccountPK;
import com.mikealbert.data.entity.PhoneNumber;
import com.mikealbert.data.entity.PhoneNumberType;
import com.mikealbert.data.entity.TitleCode;
import com.mikealbert.service.AddressService;
import com.mikealbert.service.CostCenterService;
import com.mikealbert.service.CustomerAccountService;
import com.mikealbert.service.DriverAllocationService;
import com.mikealbert.service.DriverGradeService;
import com.mikealbert.service.DriverRelationshipService;
import com.mikealbert.service.DriverService;
import com.mikealbert.service.LookupCacheService;
import com.mikealbert.util.MALUtilities;
import com.mikealbert.vision.view.ViewConstants;


@Component
@Scope("view")
public class DriverAddEditBean extends StatefulBaseBean {
	private static final long serialVersionUID = 4882207739743243366L;
	
	private static final String RECHARGE_ACCOUNT_UI_ID = "rechargeAccount"; 
	private static final String CUSTOMER_ACCOUNT_UI_ID = "customerAccount"; 
	private static final String DRIVER_GRADE_UI_ID = "driverGradeGroupCodes";
	
	private static final String CELL_TELEPHONE_TYPE = "0";
	private static final String HOME_TELEPHONE_TYPE = "1";
	private static final String WORK_TELEPHONE_TYPE = "2";
	private static final String CELL_UI_ID = "driverAddressNumberPanel:cellNumber";
	private static final String HOME_UI_ID = "driverAddressNumberPanel:homeNumber";
	private static final String WORK_UI_ID = "driverAddressNumberPanel:workNumber";	
	
	@Resource LookupCacheService lookupCacheService;	
	@Resource DriverGradeService driverGradeService;
	@Resource CustomerAccountService externalAccountService;
	@Resource DriverService driverService;
	@Resource AddressService addressService;
	@Resource CostCenterService costCenterService;
	@Resource DriverRelationshipService driverRelationshipService;
	@Resource DriverAllocationService driverAllocationService;
	
	private ExternalAccount selectedExternalAccount;
	private ExternalAccount selectedRechargeExternalAccount;
	private Driver driver;
	private String driverId;
	private List<TitleCode> titleCodes;	
	private List<DriverGrade> driverGrades;
	@MANotNull(label = "Grade")
	private DriverGrade selectedDriverGrade;
	private List<DriverAddress> driverAddresses;
	private List<PhoneNumber>phoneNumbers;
	private DriverAddress selectedDriverAddress;
	private PhoneNumber selectedPhoneNumber;
	private List<AddressTypeCode> addressTypes;
	private List<AddressTypeCode> availableAddressTypes = new ArrayList<AddressTypeCode>();
	private List<DriverManualStatusCode> driverManualStatuses;
	@MANotNull(label = "Client")
	private String customerLOVParam;
	@MANotNull(label = "Recharge")
	private String rechargeCustomerLOVParam;
	private int selectionIndex = 0;	
	private List<CityZipCode> cities = new ArrayList<CityZipCode>();
	private String selectedCityKey;
	private Map<String,CityZipCode> cityMap = new HashMap<String,CityZipCode>();
	private boolean add;
	private boolean selectedBusinessIndicator = false;	
	private boolean selectedPoolManager = false;
	private boolean disableUpdateButton = true;
	private boolean disableDeleteButton = true;	
	private String viewMode;
	private boolean isStay = false;
	
	private String accountAndRechargeAccountAreDifferent = "false";
	private String similarDriverExistForAccount = "false";
	private String relatedDriverAccountAreDifferent = "false";
	private String activatedParentWithInactiveChildren = "false";
	private String inactivatedParentWithActiveChildren = "false";
	private String allocMultUnitsCostCtrAddressChg = "false";

	//The following are used to determine whether warning dialog has been confirmed
	private String confirmAccountAndRechargeAccountAreDifferent = "false";
	private String confirmIsSimilarDriverAlreadyExistForAccount = "false";
	private String confirmIsRelatedDriverAccountAreDifferent = "false";
	private String confirmDetermineActivatedParentWithInactiveChildren = "false";
	private String confirmDetermineInactivatedParentWithActiveChildren = "false";
	private String confirmIsDriverWithMultUnitsCostCenterAddressChg = "false";

	
	private String addressSearchPerformed = "true";
	private String concatAddress = "";
	private String addressDoneClicked = "false";
	
	
	private PhoneNumber homePhone;
	private PhoneNumber workPhone;
	private PhoneNumber cellPhone;
	private List<CostCentreCode> costCenters;
	private CostCentreCode selectedCostCenter;
	private CostCentreCode initialCostCenter = null;
	private String previousView;
	private String preferredPhone;
	private String driverStatus;
	private boolean hasInactiveParent = false;
	private  String selectedDriverActiveIndicatorFlag = null;
	
	/**
	 * Initializes the bean for adding or editing a driver.
	 */
    @PostConstruct
    public void init(){
    	if(!this.isStay){
    		this.openPage();
    	}
     
    	try { 
    		//Properties that need to be initialized regardless of mode.
    		setTitleCodes(lookupCacheService.getTitleCodes()); 
    		setAddressTypes(driverService.getDriverAddressTypeCodes());
    		setDriverManualStatuses(lookupCacheService.getDriverManaualStatusCodes());
    		setDriverStatus(ViewConstants.DEFAULT_DRIVER_STATUS);
    		// check to see if loaded from the base bean map
        	if(!MALUtilities.isEmptyString(this.driverId) && !MALUtilities.isEmptyString(this.viewMode)
        			&& this.viewMode.equals(ViewConstants.VIEW_MODE_ADD)) { 
        		initAddCopyDriver();   
        	} else if(!MALUtilities.isEmptyString(this.driverId)) {
        		initEditDriver(); 
        	} else { 
        		initAddDriver();
        	}
        	
		} catch(Exception e) {
			super.addErrorMessage("generic.error", e.getMessage());
		}
    }

    /**
     * This helper method is where we need to detect a NONE cost centre and instread select the Null entry for display;
     * this is 50% business concern and 50% UI so it may make sense to move this into a service method (or somewhere else) instead.
     */
    private void initializeSelectedCostCenter() {
    	// if the NONE cost center is set on the driver return "" otherwise return the code
    	String costCentreCodeForCompare =  driver.getDriverCurrentCostCenter().getCostCenterCode().equalsIgnoreCase(MalConstants.NONE) ? "" : driver.getDriverCurrentCostCenter().getCostCenterCode();
    	
    	for (CostCentreCode ccc : costCenters) {
	    	// if NULL == NULL or "String".equals("String")
    		if(ccc.getCostCentreCodesPK().getCostCentreCode().equals(MALUtilities.getNullSafeString(costCentreCodeForCompare))){
	    		setSelectedCostCenter(ccc);
	    		setInitialCostCenter(ccc); //Used to check if cost center has changed on client side
	    		break;			    		
	    	}
		}			    
    }
    
    
    /**
     * Loads the view in Add mode by pre-populating list fields and nullifying 
     * all other input fields.
     */
    private void initAddDriver(){
		setViewMode(ViewConstants.VIEW_MODE_ADD);  
		thisPage.setPageDisplayName(ViewConstants.DISPLAY_NAME_DRIVER_ADD);
		setCustomerLOVParam(null);
		setRechargeCustomerLOVParam(null);
		setSelectedExternalAccount(new ExternalAccount());
		getSelectedExternalAccount().setExternalAccountPK(new ExternalAccountPK());
		setSelectedRechargeExternalAccount(new ExternalAccount());
		getSelectedRechargeExternalAccount().setExternalAccountPK(new ExternalAccountPK());
		setSelectedDriverAddress(null);
		setSelectedPhoneNumber(null);
		setSelectedDriverGrade(null);
		setCostCenters(null);
		setSelectedPoolManager(false);
		setDriver(new Driver());
		setDriverAddresses(new ArrayList<DriverAddress>());
		setAddressDoneClicked("false");

		
		//Default the driver's manual status to the first status in the list    	
		getDriver().setManualStatus(getDriverManualStatuses().get(0).getStatusCode());
		initializeEmptyPhoneNumbers();
		refreshButtons();    	
    }
    
    /**
     * Loads the view in Edit mode by pre-populating all fields from the 
     * driver entity retrieved from the passed in driver id parameter.
     */
    private void initEditDriver(){
		setViewMode(ViewConstants.VIEW_MODE_EDIT);
		thisPage.setPageDisplayName(ViewConstants.DISPLAY_NAME_DRIVER_EDIT);
		setDriver(driverService.getDriver(Long.parseLong(this.driverId)));        		
		setSelectedExternalAccount(driver.getExternalAccount());
		setSelectedRechargeExternalAccount(driver.getExternalAccount1());		        		
		setDriverAddresses(driver.getDriverAddressList());
		setPhoneNumbers(driver.getPhoneNumbers());         		
		setSelectedDriverGrade(driver.getDgdGradeCode());	        		    	    	 
		setDriverGrades(driverGradeService.getExternalAccountDriverGrades(getSelectedExternalAccount()));
		setCostCenters(costCenterService.getCostCenters(getSelectedExternalAccount()));
		setCustomerLOVParam(getSelectedExternalAccount().getExternalAccountPK().getAccountCode());
		setSelectedPoolManager(driver.getPoolManager().compareTo("Y") == 0 ? true:false);
		setDriverStatus(driverService.getDriverStatus(driver.getDrvId()));
		setSelectedDriverActiveIndicatorFlag(driver.getActiveInd());
		setAddressDoneClicked("false");
		//It is possible for an existing account to not have a recharge account
		//If the Recharge Account is NULL we will show NULL value instead of showing External Account in Recharge Account Field
		// for Existing Driver.
		if(getSelectedRechargeExternalAccount() != null){
			setRechargeCustomerLOVParam(getSelectedRechargeExternalAccount().getExternalAccountPK().getAccountCode());
		} 
		if(getDriverAddresses() != null && getDriverAddresses().size() > 0){
			setSelectedDriverAddress(getDriverAddresses().get(0));
			setSelectionIndex(0);
		}
		if(driver.getDriverCurrentCostCenter() != null) {
			initializeSelectedCostCenter();
		}
		//Reset these values to "false" in the case that a save and stay was performed
		confirmAccountAndRechargeAccountAreDifferent = "false";
		confirmIsSimilarDriverAlreadyExistForAccount = "false";
		confirmIsRelatedDriverAccountAreDifferent = "false";
		confirmDetermineActivatedParentWithInactiveChildren = "false";
		confirmDetermineInactivatedParentWithActiveChildren = "false";
		confirmIsDriverWithMultUnitsCostCenterAddressChg = "false";
		
		// does this driver have an inactive parent
		this.hasInactiveParent = determineHasInactiveParent(this.getDriver());
		
    	loadDriverPhoneNumbers();
		refreshButtons();   	
    }
    
    /**
     * Loads the view in Add mode, but pre-populates a subset of fields
     * based on the passed in driver's properties. 
     */
    private void initAddCopyDriver(){
    	thisPage.setPageDisplayName(ViewConstants.DISPLAY_NAME_DRIVER_ADD);
    	Driver mainDriver = driverService.getDriver(Long.parseLong(this.driverId));
    	Driver addDriver = new Driver();    	  	
    	
    	addDriver.setExternalAccount(mainDriver.getExternalAccount());
    	addDriver.setExternalAccount1(mainDriver.getExternalAccount1());
    	addDriver.setDgdGradeCode(mainDriver.getDgdGradeCode());
    	addDriver.setDriverAddressList(new ArrayList<DriverAddress>());
    	addDriver.setPhoneNumbers(new ArrayList<PhoneNumber>());
    	addDriver.setPoolManager(mainDriver.getPoolManager());
		addDriver.setManualStatus(getDriverManualStatuses().get(0).getStatusCode());      	
    	addDriver.setDriverCostCenterList(new ArrayList<DriverCostCenter>());    	
    	 	
    	//Copy Main Driver cost center
    	if(mainDriver.getDriverCurrentCostCenter() != null){
        	DriverCostCenter dcc = new DriverCostCenter();
        	dcc.setCostCenterCode(mainDriver.getDriverCurrentCostCenter().getCostCenterCode());
        	dcc.setDateAllocated(mainDriver.getDriverCurrentCostCenter().getDateAllocated());
        	dcc.setEffectiveToDate(mainDriver.getDriverCurrentCostCenter().getEffectiveToDate());
        	dcc.setExternalAccount(mainDriver.getDriverCurrentCostCenter().getExternalAccount());
        	dcc.setDriver(addDriver);
        	addDriver.getDriverCostCenterList().add(dcc);
        	addDriver.setCostCentreCode(mainDriver.getDriverCurrentCostCenter().getCostCenterCode());
    	}
    	
    	//Copy Main driver garaged address
    	setDriverAddresses(new ArrayList<DriverAddress>());   	
    	for(DriverAddress da : mainDriver.getDriverAddressList()){
    		if(da.getAddressType().getAddressType().equals(DriverService.REQUIRED_ADDRESS_TYPE_CODE)){
    			DriverAddress address = new DriverAddress();
    			address.setAddressType(da.getAddressType());
    			address.setAddressLine1(da.getAddressLine1());
    			address.setAddressLine2(da.getAddressLine2());
    			address.setBusinessInd(da.getBusinessInd());
    			address.setBusinessAddressLine(da.getBusinessAddressLine());
    			address.setDefaultInd(da.getDefaultInd());
    			address.setGeoCode(da.getGeoCode());
    			address.setPostcode(da.getPostcode());
    			address.setStreetNo(da.getStreetNo());
    			address.setTownCityCode(da.getTownCityCode());
    			address.setInputDate(new Date());
    			getDriverAddresses().add(address);
    		}
    	}
    	    	
    	
    	setPhoneNumbers(new ArrayList<PhoneNumber>());     	    	
		setSelectedExternalAccount(addDriver.getExternalAccount());
		setSelectedRechargeExternalAccount(addDriver.getExternalAccount1());		        		         		
		setSelectedDriverGrade(addDriver.getDgdGradeCode());	        		 
		setDriverGrades(driverGradeService.getExternalAccountDriverGrades(getSelectedExternalAccount()));
		setCostCenters(costCenterService.getCostCenters(getSelectedExternalAccount()));
		setCustomerLOVParam(getSelectedExternalAccount().getExternalAccountPK().getAccountCode());
		setSelectedPoolManager(false);					    	

    	setDriver(addDriver); 
		
		//It is possible for an existing account to not have a recharge account
		if(getSelectedRechargeExternalAccount() != null){
			setRechargeCustomerLOVParam(getSelectedRechargeExternalAccount().getExternalAccountPK().getAccountCode());
		} else {
			setSelectedRechargeExternalAccount(getSelectedExternalAccount());
			setRechargeCustomerLOVParam(getSelectedExternalAccount().getExternalAccountPK().getAccountCode());
		}
		if(getDriverAddresses() != null && getDriverAddresses().size() > 0){
			setSelectedDriverAddress(getDriverAddresses().get(0));
			setSelectionIndex(0);
		}
		if(addDriver.getDriverCurrentCostCenter() != null) {
			initializeSelectedCostCenter();
		}
		
		// is the main driver inactive (parent is passed in from related driver's page)
		this.hasInactiveParent = mainDriver.getActiveInd().equalsIgnoreCase(MalConstants.FLAG_Y) ? false : true;
		// TODO: should we populate active indicator from the parent driver?
		
    	loadDriverPhoneNumbers();
		refreshButtons();  		
    }
    
    private boolean determineHasInactiveParent(Driver driver){
    	boolean retVal = false;
    	List<Driver> parentDrivers;
    	
    	if (driverRelationshipService.isRelatedDriver(driver.getDrvId())){
    		parentDrivers = driverRelationshipService.getParentDrivers(driver.getDrvId());
    		for(Driver parentDriver: parentDrivers){
    			if(parentDriver.getActiveInd().equalsIgnoreCase(MalConstants.FLAG_N)){
    				retVal = true;
    				break;
    			}
    		}
    	}
    	return retVal;
    }
    
	/**
	 * Pattern for retrieving stateful data passed from calling view.
	 */
	protected void loadNewPage() {
		thisPage.setPageUrl(ViewConstants.DRIVER_ADD);
		String driverId = (String)thisPage.getInputValues().get(ViewConstants.VIEW_PARAM_DRIVER_ID);
		this.viewMode = (String)thisPage.getInputValues().get(ViewConstants.VIEW_PARAM_MODE);
		//TODO Need to figure out a better way to identify the calling view's name. See Vivek
        this.previousView = (String)thisPage.getInputValues().get(ViewConstants.VIEW_PARAM_CALLERS_NAME);	
		if(driverId != null){
			this.driverId = driverId;
		}
	}

	/**
	 * Pattern for restoring the view's data
	 */
	protected void restoreOldPage() {
		this.driverId = (String) thisPage.getRestoreStateValues().get(ViewConstants.VIEW_PARAM_DRIVER_ID);	
	}
	
	public void decodeRechargeAccountByNameOrCode(){
		String paramName = (String) getRequestParameter("CUSTOMER_LOV_INPUT"); //This code needed when users select through LOV and need refresh
		if (!MALUtilities.isEmptyString(paramName)) {
			rechargeCustomerLOVParam = (String) getRequestParameter(paramName);
		}
		 List<ExternalAccount>  externalAccountList =  externalAccountService.findOpenCustomerAccountsByNameOrCode(rechargeCustomerLOVParam, getLoggedInUser().getCorporateEntity(),new PageRequest(0,50));
		 if(externalAccountList.size() == 1){
			 selectedRechargeExternalAccount  =  externalAccountList.get(0);	
			 rechargeCustomerLOVParam = selectedRechargeExternalAccount.getExternalAccountPK().getAccountCode();
		 }else {
			 if(externalAccountList.size() == 0){		 
					 super.addErrorMessage(RECHARGE_ACCOUNT_UI_ID,"decode.noMatchFound.msg", "Recharge Acc " + rechargeCustomerLOVParam); 
				 }else{
					 super.addErrorMessage(RECHARGE_ACCOUNT_UI_ID,"decode.notExactMatch.msg", "Recharge Acc " +  rechargeCustomerLOVParam);			
				 }
			 this.setSelectedRechargeExternalAccount(new ExternalAccount());
			 this.selectedRechargeExternalAccount.setExternalAccountPK(new ExternalAccountPK());
			 
		 }	
	}
	
	
	
	public void decodeAccountByNameOrCode(){
		
		String paramName = (String) getRequestParameter("CUSTOMER_LOV_INPUT");//This code needed when users select through LOV and need refresh
		if (!MALUtilities.isEmptyString(paramName)) {
			customerLOVParam = (String) getRequestParameter(paramName);
		}
		
		 List<ExternalAccount>  externalAccountList =  externalAccountService.findOpenCustomerAccountsByNameOrCode(customerLOVParam, getLoggedInUser().getCorporateEntity(),new PageRequest(0,50));
		 if(externalAccountList.size() == 1){					 
			
			 selectedExternalAccount  =  externalAccountList.get(0);
			 selectedRechargeExternalAccount = selectedExternalAccount ;
			 customerLOVParam = selectedExternalAccount.getExternalAccountPK().getAccountCode();			 
			 rechargeCustomerLOVParam  = customerLOVParam ;
			 
			 driverGrades = driverGradeService.getExternalAccountDriverGrades(selectedExternalAccount);
			 costCenters = costCenterService.getCostCenters(selectedExternalAccount);
		 }else {
			 if(externalAccountList.size() == 0){
				 super.addErrorMessage(CUSTOMER_ACCOUNT_UI_ID,"decode.noMatchFound.msg","Client " + customerLOVParam); 
			 }else{
				 super.addErrorMessage(CUSTOMER_ACCOUNT_UI_ID,"decode.notExactMatch.msg","Client " +  customerLOVParam);			
			 }
			 if(driverGrades != null)  driverGrades.clear();
			 if(costCenters != null) costCenters.clear();
			 this.setSelectedExternalAccount(new ExternalAccount());
			 this.selectedExternalAccount.setExternalAccountPK(new ExternalAccountPK());
		 }
		 
	}
	
	public void validateCustomClient(FacesContext context, UIComponent inputComponent, Object value) {
		if (MALUtilities.isEmpty(value)){
			super.addErrorMessage(CUSTOMER_ACCOUNT_UI_ID, "required.field",	"Client Account ");
			this.setSelectedExternalAccount(new ExternalAccount());
			this.selectedExternalAccount.setExternalAccountPK(new ExternalAccountPK());
		}
	}

	public void validateCustomRecharge(FacesContext context, UIComponent inputComponent, Object value)  {
		if (MALUtilities.isEmpty(value)){
			super.addErrorMessage(RECHARGE_ACCOUNT_UI_ID,"required.field", "Recharge Account ");
		}

		if (MALUtilities.isEmpty(getSelectedRechargeExternalAccount())) {
		this.setSelectedRechargeExternalAccount(new ExternalAccount());
		this.selectedRechargeExternalAccount.setExternalAccountPK(new ExternalAccountPK());
		}
	}
	public boolean validateClientAccountAndRechargeAccount(){	
		
		boolean success = true;
		String accCode= selectedExternalAccount.getExternalAccountPK().getAccountCode();
		String accName= selectedExternalAccount.getAccountName();
		if(! (accCode.equals(customerLOVParam ) || accName.equals(customerLOVParam)) ){//user edit textbox but not decoded
			 List<ExternalAccount>  externalAccountList =  externalAccountService.findOpenCustomerAccountsByNameOrCode(customerLOVParam, getLoggedInUser().getCorporateEntity(),new PageRequest(0,50));
			 if(externalAccountList.size() == 1){
				 selectedExternalAccount  =  externalAccountList.get(0);	
			 }else{
				 selectedExternalAccount.setAccountName("") ;
				 super.addErrorMessage(CUSTOMER_ACCOUNT_UI_ID,"decode.noMatchFound.msg", "Client " + customerLOVParam); 
				 success = false;
			 }	
			 if( selectedDriverGrade == null ){				  
				  super.addErrorMessage(DRIVER_GRADE_UI_ID,"required.field", "Driver Grade " ); 
				  success = false;
			 }else {			  	
			    driverGrades = driverGradeService.getExternalAccountDriverGrades(selectedExternalAccount);
			    boolean driverGradeFound = false;
			    for (DriverGrade driverGrade : driverGrades) {
			    	if(driverGrade.getGradeCode().equals(selectedDriverGrade.getGradeCode())){
			    		driverGradeFound = true; break;			    		
			    	}
				}			    
			    if(driverGradeFound == false){	
			    		String[] args = new String[]{ selectedDriverGrade.getGradeCode() +" -" +selectedDriverGrade.getGradeDesc()  , customerLOVParam };
			    	 super.addErrorMessage(DRIVER_GRADE_UI_ID , "previous.selected.grade.is.not.applicable", args ); 
			    	 return  false;
			    }
			    	
			 }

		}
		
		String rechargeAccCode= selectedRechargeExternalAccount.getExternalAccountPK().getAccountCode();
		String rechargeAccName= selectedRechargeExternalAccount.getAccountName();
		if(! (rechargeAccCode.equals(rechargeCustomerLOVParam)  || rechargeAccName.equals(rechargeCustomerLOVParam)) ){//user edit textbox but not decoded
			 List<ExternalAccount>  externalAccountList =  externalAccountService.findOpenCustomerAccountsByNameOrCode(rechargeCustomerLOVParam, getLoggedInUser().getCorporateEntity(),new PageRequest(0,50));
			 if(externalAccountList.size() == 1){
				 selectedRechargeExternalAccount  =  externalAccountList.get(0);	
			 }else{
				 this.selectedRechargeExternalAccount.setAccountName("") ;
				 super.addErrorMessage(RECHARGE_ACCOUNT_UI_ID,"decode.noMatchFound.msg", "Recharge Acc " + rechargeCustomerLOVParam); 
				 success = false;
			 }		
		}
		
		return success;
	    
	
}
	public boolean isDriverAccountAndRechargeAccountAreDifferent(){
	
		boolean success = false;
		if(!externalAccountService.isAccountsReleated(selectedExternalAccount, selectedRechargeExternalAccount)){			
			success = true;
		} 
		return success;	
	}
	
	/**
	 * Provides warning message functionality for the following business problem:
	 * The user may not realize that, when changing the driver's address or cost center, 
	 * the user is changing this information for ALL units the driver is allocated to;
	 * 
	 * This method returns true if both of the following criteria is met:
	 * 1) Driver is currently allocated to multiple units And
	 * 2) Driver's cost center OR address has changed
	 * 
	 * @return True if the above criteria is met
	 */
	public boolean isDriverWithMultUnitsCostCenterAddressChg(){
		//Check for multiple allocations
		if(!MALUtilities.isEmpty(driver.getDrvId())){
			if(driverAllocationService.getDriverAllocationsCount(driver.getDrvId()) > 1 ){
				//Check if cost center or address changed
				if(!driverService.isCostCenterCodeAreSame(initialCostCenter,selectedCostCenter)){
					return true;
				}
				else if(!addressService.addressListCompare(driver.getDriverAddressList(), driver.getDrvId())){
					return true;
				}
			}			
		}
		return false;
	}
   
	public boolean isSimilarDriverAlreadyExistForAccount(){
		List<Long>  driverIdList  = driverService.findDriverByLastAndFirstNameAndEmailForAccount(driver.getDriverSurname() ,driver.getDriverForename() , driver.getEmail()
					, selectedExternalAccount.getExternalAccountPK().getAccountCode() ,selectedExternalAccount.getExternalAccountPK().getCId());
		
		if (driverIdList.size() > 0) {
			return true;
		} else {
			return false;
		}
		
	}
	
	public boolean isRelatedDriverAccountAreDifferent(String driverId){
		
		Long drvId =  Long.parseLong(driverId);
		boolean isParent  = driverRelationshipService.isParentDriver(drvId);
		if(isParent){
			 List<Driver>  relatedDrivers = driverRelationshipService.getRelatedDrivers(drvId);
			 for (Driver driver : relatedDrivers) {
				 if(! driver.getExternalAccount().equals(selectedExternalAccount)){
					 return true;
				 }
			}
		}
		
		return false ;
	}
	
	public boolean determineActivatedParentWithInactiveChildren(){
		this.driver.setActiveInd(selectedDriverActiveIndicatorFlag);
		boolean hasChanged  = driverService.hasActiveIndicatorChanged(this.driver);
		boolean isParent  = driverRelationshipService.isParentDriver(this.driver.getDrvId());
		if(hasChanged && isParent && this.driver.getActiveInd().equalsIgnoreCase(MalConstants.FLAG_Y)){
			 List<Driver>  relatedDrivers = driverRelationshipService.getRelatedDrivers(this.driver.getDrvId());
			 for (Driver driver : relatedDrivers) {
				 if(driver.getActiveInd().equalsIgnoreCase(MalConstants.FLAG_N)){
					 return true;
				 }
			}
		}
		
		return false ;
	}
	
	public boolean determineInactivatedParentWithActiveChildren(){
		this.driver.setActiveInd(selectedDriverActiveIndicatorFlag);
		boolean hasChanged  = driverService.hasActiveIndicatorChanged(this.driver);
		boolean isParent  = driverRelationshipService.isParentDriver(this.driver.getDrvId());
		if(hasChanged && isParent && this.driver.getActiveInd().equalsIgnoreCase(MalConstants.FLAG_N)){
			 List<Driver>  relatedDrivers = driverRelationshipService.getRelatedDrivers(this.driver.getDrvId());
			 for (Driver driver : relatedDrivers) {
				 if(driver.getActiveInd().equalsIgnoreCase(MalConstants.FLAG_Y)){
					 return true;
				 }
			}
		}
		
		return false ;
	}
    
    /**
     * Evaluates the driver address list for the required address type.
     * At the time of writing, it is GARAGED. 
     * @return True when the required address has been found. Otherwise false is returned.
     */
    public boolean hasRequiredAddressType(){
        boolean found = false;
 
        //Verify that an address exist for the required address type
        for(DriverAddress driverAddress : driverAddresses){
            if(driverAddress.getAddressType().getAddressType().equals(DriverService.REQUIRED_ADDRESS_TYPE_CODE)){
            	found = true;
                break;
            }
        }

        return found;
    }
    
    /**
     * Evaluates the driver address list for duplicate address types.
     * @return True when duplicate address types are found. Otherwise false is returned.
     */
    public boolean hasDuplicateAddressType(){  
        boolean found = false;    	
        //Verify that there are no duplicate address types.
        for(int i = 0; i < driverAddresses.size(); i++){
            for(int j = i + 1; j < driverAddresses.size(); j++){
                if(driverAddresses.get(i).getAddressType().equals(driverAddresses.get(j).getAddressType())){
                    found = true;
                    break;
                }
            }
        }
        
        return found;
    }
    
    /**
     * Handles the Saves the driver action by navigating to another page.
     */    
    public String cancel(){    	
    	return super.cancelPage();  
    }
    
	public String saveAfterConfirmation(String confirmDialogName) {

		// Determine which dialog the confirmation had come from
		if(confirmDialogName.equalsIgnoreCase("confirmAccountAndRechargeAccountAreDifferent")){
			confirmAccountAndRechargeAccountAreDifferent = "true";
			accountAndRechargeAccountAreDifferent = "false";
		}else if(confirmDialogName.equalsIgnoreCase("confirmIsSimilarDriverAlreadyExistForAccount")){
			confirmIsSimilarDriverAlreadyExistForAccount = "true";
			similarDriverExistForAccount = "false";
		}else if(confirmDialogName.equalsIgnoreCase("confirmIsRelatedDriverAccountAreDifferent")){
			confirmIsRelatedDriverAccountAreDifferent = "true";
			relatedDriverAccountAreDifferent = "false";
		}else if(confirmDialogName.equalsIgnoreCase("confirmDetermineActivatedParentWithInactiveChildren")){
			confirmDetermineActivatedParentWithInactiveChildren = "true";
			activatedParentWithInactiveChildren = "false";
		}else if(confirmDialogName.equalsIgnoreCase("confirmDetermineInactivatedParentWithActiveChildren")){
			confirmDetermineInactivatedParentWithActiveChildren = "true";
			inactivatedParentWithActiveChildren = "false";
		}else if (confirmDialogName.equalsIgnoreCase("confirmIsDriverWithMultUnitsCostCenterAddressChg")){
			confirmIsDriverWithMultUnitsCostCenterAddressChg = "true";
			allocMultUnitsCostCtrAddressChg = "false";
		}

		if (this.isStay)
			return saveAndStay();
		else
			return save();

	}
    
    /**
     * Handles the Saves the driver event by processing the save request. When processing 
     * completes, the response will navigate to another page.
     */
    public String save(){  
    	this.isStay = false;
    	if(saveDriver()){
    		if(pageList.size() > 1) {
    			pageList.get(pageList.size()-2).getRestoreStateValues().put(ViewConstants.VIEW_PARAM_DRIVER_ID, driver.getDrvId());
    		}
    		return super.cancelPage();
    	}else
    		return null;
    }
        
    /**
     * Handles the Save and Stay action by processing the save request. When processing
     * completes, the bean is re-initialized and the page is refreshed so that the user
     * can either continue editing or adding a driver. 
     */
    public String saveAndStay(){    
    	
    	this.isStay = true;
    	
    	if(saveDriver()){
    		init();    		
    	}
    		return null;
    }    
    
    /**
     * Saves the driver
     */    
    private boolean saveDriver(){
    	try{
    		String userName = getLoggedInUser().getEmployeeNo();

			if( isValidToSave() == false){
				return false;
			}
			
			if( isDriverAccountAndRechargeAccountAreDifferent() == true 
					&& confirmAccountAndRechargeAccountAreDifferent == "false"){
				this.accountAndRechargeAccountAreDifferent = "true";
				return false;
			}    			
			if(driverId == null &&  isSimilarDriverAlreadyExistForAccount() == true 
					&& confirmIsSimilarDriverAlreadyExistForAccount == "false"){ // check only for add Drivers
				this.similarDriverExistForAccount = "true";
				return false;
			}
			if(driverId != null &&  isRelatedDriverAccountAreDifferent(driverId) == true 
					&& confirmIsRelatedDriverAccountAreDifferent == "false"){ // check only for edit Drivers 
				this.relatedDriverAccountAreDifferent = "true";
				return false;
			}
			if(driverId != null &&  determineActivatedParentWithInactiveChildren() == true 
					&& confirmDetermineActivatedParentWithInactiveChildren == "false"){ // check only for edit Drivers 
				this.activatedParentWithInactiveChildren = "true";
				return false;
			}
			if(driverId != null &&  determineInactivatedParentWithActiveChildren() == true 
					&& confirmDetermineInactivatedParentWithActiveChildren == "false"){ // check only for edit Drivers 
				this.inactivatedParentWithActiveChildren = "true";
				return false;
			}
			if(driverId != null && isDriverWithMultUnitsCostCenterAddressChg() == true 
					&& confirmIsDriverWithMultUnitsCostCenterAddressChg == "false"){ //check only for edit Drivers
				this.allocMultUnitsCostCtrAddressChg = "true";
				return false;
			}
			
   		
    		selectedDriverGrade = driverGradeService.getDriverGrade(this.selectedDriverGrade.getGradeCode());
    		driver.setDgdGradeCode(selectedDriverGrade);
    		driver.setExternalAccount1(selectedRechargeExternalAccount);  
    		driver.setPoolManager(selectedPoolManager ? "Y":"N");
    		driver.setActiveInd(selectedDriverActiveIndicatorFlag);
    		unloadDriverPhoneNumbers();
    		this.driver = driverService.saveOrUpdateDriver(selectedExternalAccount,  selectedDriverGrade, driver, driverAddresses, phoneNumbers, userName, selectedCostCenter);

    		this.accountAndRechargeAccountAreDifferent = "false";
    		this.similarDriverExistForAccount = "false";
    		this.relatedDriverAccountAreDifferent = "false";
    		this.activatedParentWithInactiveChildren = "false";
    		this.inactivatedParentWithActiveChildren = "false";
    		this.allocMultUnitsCostCtrAddressChg = "false";
    		
    		super.addSuccessMessage("process.success", this.viewMode + " driver (" + driver.getDriverSurname() + ", " + driver.getDriverForename() + ")");	
    		return true;
    		
    		
    	} catch(Exception ex) {
    		super.addErrorMessage("generic.error", ex.getMessage());
    		return false;
    	}	
    }

    
    private boolean isValidToSave() {
    	
    	boolean accountIsValid  = validateClientAccountAndRechargeAccount();
    	boolean phoneIsValid  = validPhoneNumbers();
    	boolean  addressIsValid = validAddresses();
    	boolean  isAnyPhoneNumberIsPresent = isAnyPhoneNumberIsPresent();
    	
    	if(accountIsValid && ! isAnyPhoneNumberIsPresent && phoneIsValid && addressIsValid ){
			return true;
    	}else {
			return false;
		}
    }

    private boolean validAddresses() {
		//Validate addresses
		if(this.hasDuplicateAddressType()) {
			super.addErrorMessage("address.type.duplicate.error");
			return false;
		}
		if(!this.hasRequiredAddressType()) {
			super.addErrorMessage("address.required.type", DriverService.REQUIRED_ADDRESS_TYPE_CODE);
			return false;
		}
		if(driver.getDriverAddressList() != null){
			for(DriverAddress da : driver.getDriverAddressList()){
				if(da.getBusinessInd().contains("Y")){
					if(da.getBusinessAddressLine() == null || da.getBusinessAddressLine().isEmpty()){
						super.addErrorMessage("business.address.required", da.getAddressType().getAddressType());
						return false;
					}
				}
			}
		}
		return true;
    }

	private boolean isAnyPhoneNumberIsPresent() {

		if ((MALUtilities.isEmpty(getCellPhone().getNumber()) 
				&& MALUtilities.isEmpty(getHomePhone().getNumber()) 
				&& MALUtilities.isEmpty(getWorkPhone().getNumber())) | MALUtilities.isEmpty(getPreferredPhone())) {

			super.addErrorMessage("driver.must.have.preferred.phone");
			
			return true;			
		} else {
			return false;
		}
	}
    private boolean validPhoneNumbers() {
    	boolean validFlag = false;
    	if( (isValidatePhoneNumber(getCellPhone(), CELL_UI_ID, "Cellular Telephone Number")) && 
    		(isValidatePhoneNumber(getHomePhone(), HOME_UI_ID, "Home Telephone Number")) &&
    		(isValidatePhoneNumber(getWorkPhone(), WORK_UI_ID, "Work Telephone Number")) ) {
    		validFlag = true;
    	}

    	if(getPreferredPhone() != null) {
        	if(getPreferredPhone().equals(CELL_TELEPHONE_TYPE)) {
        		if((MALUtilities.isEmpty(getCellPhone().getAreaCode())) || (MALUtilities.isEmpty(getCellPhone().getNumber()))) {
    				super.addErrorMessage(CELL_UI_ID, "incomplete.preferred.phone.number", "Cellular Telephone Number");
    				validFlag = false;
        		}
        	} 
        	else if(getPreferredPhone().equals(HOME_TELEPHONE_TYPE)) {
        		if((MALUtilities.isEmpty(getHomePhone().getAreaCode())) || (MALUtilities.isEmpty(getHomePhone().getNumber()))) {
    				super.addErrorMessage(HOME_UI_ID, "incomplete.preferred.phone.number", "Home Telephone Number");
    				validFlag = false;
        		}
        	} 
        	else if(getPreferredPhone().equals(WORK_TELEPHONE_TYPE)) {
        		if((MALUtilities.isEmpty(getWorkPhone().getAreaCode())) || (MALUtilities.isEmpty(getWorkPhone().getNumber()))) {
    				super.addErrorMessage(WORK_UI_ID, "incomplete.preferred.phone.number", "Work Telephone Number");
    				validFlag = false;
        		} 
        	} 
        	else {
    				super.addErrorMessage("driver.must.have.preferred.phone");
    				validFlag = false;
        	}
    	} 
    	else {
    		validFlag = false;
    	}

    	return validFlag;
    }
    
    private boolean isValidatePhoneNumber(PhoneNumber phoneNumber, String htmlId, String phoneType) {
    	boolean validFlag = true;
		if(!MALUtilities.isEmpty(phoneNumber.getNumber())) {
			if(MALUtilities.isEmpty(phoneNumber.getAreaCode())) {
				super.addErrorMessage(htmlId, "incomplete.phone.number", phoneType);				
				validFlag = false;
			}
		}
		else {
			if(!MALUtilities.isEmpty(phoneNumber.getAreaCode())) {
				super.addErrorMessage(htmlId, "incomplete.phone.number", phoneType);
				validFlag = false;
			}
			if(!MALUtilities.isEmpty(phoneNumber.getExtensionNumber())) {
				super.addErrorMessage(htmlId, "incomplete.phone.number", phoneType);				
				validFlag = false;
			}
		}
    	
    	return validFlag;
    }
    
    public void onRowSelect(SelectEvent event) {
    	
		selectedDriverAddress = (DriverAddress) event.getObject();
		this.refreshButtons();
	}
	
    public void addNewAddress() {
    	add = true;
    	selectedDriverAddress = new DriverAddress();
    	AddressTypeCode addressTypeCode = new AddressTypeCode();
    	selectedDriverAddress.setAddressType(addressTypeCode);
    	selectedDriverAddress.setInputDate(new Date());   
    	this.selectedBusinessIndicator = false;
    	setAddressSearchPerformed("true");
    	setAddressDoneClicked("false");
    }
    
    public void editAddress(){
    	concatAddress();
    	setAddressSearchPerformed("false");
    	setAddressDoneClicked("false");
    }
    
    public void deleteAddress(){
    	if(selectedDriverAddress != null) {
            driverAddresses.remove(selectedDriverAddress);    		
    	}
    	selectedDriverAddress = null;
    	refreshButtons();    	
    }
        
    public void processDoneDialog() {
    	setAddressDoneClicked("true");
    	
    	if(selectedBusinessIndicator == false){
    		selectedDriverAddress.setBusinessAddressLine(null);
    	}
    	
    	if(addressEntryError()) {
			RequestContext context = RequestContext.getCurrentInstance();
			context.addCallbackParam("failure", true);
			return;
		}
    	
    	selectedDriverAddress.setBusinessInd(selectedBusinessIndicator ? "Y":"N");
    	
    	if(add) {
    		driverAddresses.add(selectedDriverAddress); 
        	this.selectionIndex = driverAddresses.size() - 1;    		
    	}
    	
    	
    	//Set the actual AddressTypeCode object to the selected address
    	for(AddressTypeCode atc : lookupCacheService.getAddressTypeCodes())
    		if(atc.getAddressType().equals(this.selectedDriverAddress.getAddressType().getAddressType()))
    			this.selectedDriverAddress.setAddressType(atc);
     
    	refreshButtons();
    	dialogClose(null);
    }

    public void dialogClose(CloseEvent event) {
    	add = false;
    	cities.clear();
    	cityMap.clear();
    	selectedCityKey = null;
    }
    
    
    private boolean addressEntryError() {

    	String ADDRESS_TYPE_UI_ID = "addressForm:addressType";
    	String ADDRESS_LINE1_UI_ID = "addressForm:dialogAddressLine1";
    	String POSTCODE_UI_ID = "addressForm:dialogPostcode";
    	String CITY_SELECTOR_UI_ID = "addressForm:dialogCitySelector";
    	String BUSINESS_ADDRESS_LINE_UI_ID = "addressForm:businessAddressLine"; 
    	
    	boolean errorFlag = false;
    	
		if(MALUtilities.isEmpty(selectedDriverAddress.getAddressType().getAddressType())) {
			addErrorMessageSummary(ADDRESS_TYPE_UI_ID,"required.field", "Address Type");
			errorFlag=true;
		}
		if( selectedBusinessIndicator && MALUtilities.isEmpty(selectedDriverAddress.getBusinessAddressLine())) {
			addErrorMessageSummary(BUSINESS_ADDRESS_LINE_UI_ID,"required.field" ,"Business Address Line");
			errorFlag=true;
		}
    	if(MALUtilities.isEmpty(selectedDriverAddress.getAddressLine1())) {
			addErrorMessageSummary(ADDRESS_LINE1_UI_ID,"required.field", "Address Line1");
			errorFlag=true;
    	}
		if(MALUtilities.isEmpty(selectedDriverAddress.getPostcode())) {
			addErrorMessageSummary(POSTCODE_UI_ID, "required.field", "Postal Code");
			errorFlag=true;
		}
		if(MALUtilities.isEmpty(selectedDriverAddress.getGeoCode())) {
			addErrorMessageSummary(CITY_SELECTOR_UI_ID,"required.field", "City/Location");
			errorFlag=true;
		}

		return errorFlag;
    }
    
    
    
    public void doZipSearch(){
    	String postCode = selectedDriverAddress.getPostcode();
    	// use only 3 characters if Canadian post code entered or 5 if US
    	if(postCode.indexOf(" ") == 3 || (!postCode.matches("\\d*") && !postCode.contains("-")))  {
    		postCode = postCode.substring(0, 3);
    	} else if(postCode.length() > 5) {
    		postCode = postCode.substring(0, 5);
    	}
		cities = addressService.getAllCityZipCodesByZipCode(postCode);
		cityMap.clear();
		CityZipCode city;
		setAddressSearchPerformed("true");
		
		Iterator<CityZipCode> iterator = cities.iterator();
		while (iterator.hasNext()) {
			city = iterator.next();
			String key = city.getUniqueUIKey();
			if(! cityMap.containsKey(key)){
					cityMap.put(key, city); 
			}else{
				iterator.remove();
			}
		}
    }

    
    public void loadLocation(AjaxBehaviorEvent e) {
    	selectedDriverAddress.setTownCityCode(cityMap.get(selectedCityKey).getTownCityCode());   
    	selectedDriverAddress.setGeoCode(cityMap.get(selectedCityKey).getFullGeoCode());
    }    
	
	public void clearCityGeo(AjaxBehaviorEvent e){
		selectedDriverAddress.setTownCityCode(null);   
    	selectedDriverAddress.setGeoCode(null);
    	cities.clear();
	}

	private void loadDriverPhoneNumbers() {
		initializeEmptyPhoneNumbers();
		List<PhoneNumber> numberList = getPhoneNumbers();
		for(PhoneNumber phoneNumber : numberList) {
			if(phoneNumber.getType().getNumberType().equals("WORK")) {
				setWorkPhone(phoneNumber);
				if(phoneNumber.getPreferredInd().equals("Y")) {
					setPreferredPhone(WORK_TELEPHONE_TYPE);	
				}
			}
			else if(phoneNumber.getType().getNumberType().equals("HOME")) {
				setHomePhone(phoneNumber);
				if(phoneNumber.getPreferredInd().equals("Y")) {
					setPreferredPhone(HOME_TELEPHONE_TYPE);	
				}
			}
			else if(phoneNumber.getType().getNumberType().equals("CELL")) {
				setCellPhone(phoneNumber);
				if(phoneNumber.getPreferredInd().equals("Y")) {
					setPreferredPhone(CELL_TELEPHONE_TYPE);	
				}
			}
		}
	}

	private void initializeEmptyPhoneNumbers() {
		setCellPhone(new PhoneNumber());
		getCellPhone().setDriver(driver);
		setHomePhone(new PhoneNumber());
		getHomePhone().setDriver(driver);
		setWorkPhone(new PhoneNumber());
		getWorkPhone().setDriver(driver);
		for(PhoneNumberType phoneType : lookupCacheService.getPhoneNumberTypes()) {
			if(phoneType.getNumberType().equals("CELL")) {
				getCellPhone().setType(phoneType);
			}
			if(phoneType.getNumberType().equals("HOME")) {
				getHomePhone().setType(phoneType);
			}
			if(phoneType.getNumberType().equals("WORK")) {
				getWorkPhone().setType(phoneType);
			}
		}
	}
	
	private void unloadDriverPhoneNumbers() {
		if(MALUtilities.isEmpty(getPhoneNumbers())) {
			setPhoneNumbers(new ArrayList<PhoneNumber>());
		}
		else {
			getPhoneNumbers().clear();
		}
		if(!MALUtilities.isEmpty(getHomePhone().getNumber())) {
			getHomePhone().setPreferredInd(getPreferredPhone().equals(HOME_TELEPHONE_TYPE) ? "Y" : "N");
			getPhoneNumbers().add(getHomePhone());
		}
		if(!MALUtilities.isEmpty(getCellPhone().getNumber())) {
			getCellPhone().setPreferredInd(getPreferredPhone().equals(CELL_TELEPHONE_TYPE) ? "Y" : "N");
			getPhoneNumbers().add(getCellPhone());
		}
		if(!MALUtilities.isEmpty(getWorkPhone().getNumber())) {
			getWorkPhone().setPreferredInd(getPreferredPhone().equals(WORK_TELEPHONE_TYPE) ? "Y" : "N");
			getPhoneNumbers().add(getWorkPhone());
		}
	}
	
	/*
	 * Logic that determines the state of the control buttons
	 */
	private void refreshButtons(){
		if(this.viewMode == ViewConstants.VIEW_MODE_ADD){
			if(driverAddresses.isEmpty()) {
				disableUpdateButton=true;
				disableDeleteButton=true;			
			} else {
				disableUpdateButton=false;
				disableDeleteButton=false;			
			}
		} else {
			if (this.selectedDriverAddress != null 
					&& this.selectedDriverAddress.getAddressType().getAddressType().equals(DriverService.REQUIRED_ADDRESS_TYPE_CODE)){				
		        disableUpdateButton=false;
		    	disableDeleteButton=true;	
		    } else if(this.selectedDriverAddress != null) {
				disableUpdateButton=false;
				disableDeleteButton=false;		    	
		    } else {
				disableUpdateButton=true;
				disableDeleteButton=true;		    	
		    }
		}		
	}
	
	public void concatAddress(){
		if(selectedDriverAddress != null){
			if(selectedDriverAddress.getAddressLine1() != null){
				concatAddress = selectedDriverAddress.getAddressLine1();
			}
			if(selectedDriverAddress.getAddressLine2() != null){
				concatAddress = concatAddress.concat(selectedDriverAddress.getAddressLine2());
			}
			if(selectedDriverAddress.getBusinessAddressLine() != null){
				concatAddress = concatAddress.concat(selectedDriverAddress.getBusinessAddressLine());
			}
			if(selectedDriverAddress.getPostcode() != null){
				concatAddress = concatAddress.concat(selectedDriverAddress.getPostcode());
			}
		}
	}
	
	public List<TitleCode> getTitleCodes() {
		return titleCodes;
	}

	public void setTitleCodes(List<TitleCode> titleCodes) {
		this.titleCodes = titleCodes;
	}

	public Driver getDriver() {
		return driver;
	}

	public void setDriver(Driver driver) {
		this.driver = driver;
	}

	public ExternalAccount getSelectedExternalAccount() {
		return selectedExternalAccount;
	}

	public void setSelectedExternalAccount(ExternalAccount externalAccount) {
		this.selectedExternalAccount = externalAccount;
	}

	public List<DriverGrade> getDriverGrades() {
		return driverGrades;
	}

	public void setDriverGrades(List<DriverGrade> driverGrades) {
		this.driverGrades = driverGrades;
	}

	public DriverGrade getSelectedDriverGrade() {
		return selectedDriverGrade;
	}

	public void setSelectedDriverGrade(DriverGrade selectedDriverGrade) {
		this.selectedDriverGrade = selectedDriverGrade;
	}

	public List<DriverAddress> getDriverAddresses() {
		return driverAddresses;
	}

	public void setDriverAddresses(List<DriverAddress> driverAddresses) {
		this.driverAddresses = driverAddresses;
	}

	public List<PhoneNumber> getPhoneNumbers() {
		return phoneNumbers;
	}

	public void setPhoneNumbers(List<PhoneNumber> phoneNumbers) {
		this.phoneNumbers = phoneNumbers;
	}

	public DriverAddress getSelectedDriverAddress() {
		if(selectedDriverAddress == null) {
			selectedDriverAddress = new DriverAddress();
		}
		return selectedDriverAddress;
	}

	public void setSelectedDriverAddress(DriverAddress selectedDriverAddress) {
		this.selectedDriverAddress = selectedDriverAddress;
		if(selectedDriverAddress != null){
			this.selectionIndex = this.driverAddresses.indexOf(selectedDriverAddress);
			this.setSelectedBusinessIndicator(selectedDriverAddress.getBusinessInd().compareTo("Y") == 0 ? true:false);
		}
	}


	public PhoneNumber getSelectedPhoneNumber() {
		return selectedPhoneNumber;
	}

	public void setSelectedPhoneNumber(PhoneNumber selectedPhoneNumber) {
		this.selectedPhoneNumber = selectedPhoneNumber;
	}

	public ExternalAccount getSelectedRechargeExternalAccount() {
		return selectedRechargeExternalAccount;
	}

	public void setSelectedRechargeExternalAccount(
			ExternalAccount selectedRechargeExternalAccount) {
		this.selectedRechargeExternalAccount = selectedRechargeExternalAccount;
	}

	public List<AddressTypeCode> getAddressTypes() {
		return addressTypes;
	}

	public void setAddressTypes(List<AddressTypeCode> addressTypes) {
		this.addressTypes = addressTypes;
	}

	public List<DriverManualStatusCode> getDriverManualStatuses() {
		return driverManualStatuses;
	}

	public void setDriverManualStatuses(List<DriverManualStatusCode> driverManualStatuses) {
		this.driverManualStatuses = driverManualStatuses;
	}

	public List<CityZipCode> getCities() {
		return cities;
	}

	public void setCities(List<CityZipCode> cities) {
		this.cities = cities;
	}

	public String getSelectedCityKey() {
		return selectedCityKey;
	}

	public void setSelectedCityKey(String selectedCityKey) {
		this.selectedCityKey = selectedCityKey;
	}

	public Map<String, CityZipCode> getCityMap() {
		return cityMap;
	}

	public void setCityMap(Map<String, CityZipCode> cityMap) {
		this.cityMap = cityMap;
	}
	
	public boolean isSelectedBusinessIndicator() {
		return selectedBusinessIndicator;
	}

	public void setSelectedBusinessIndicator(boolean selectedBusinessIndicator) {
		this.selectedBusinessIndicator = selectedBusinessIndicator;
	}

	public boolean isDisableUpdateButton() {
		return disableUpdateButton;
	}

	public void setDisableUpdateButton(boolean disableUpdateButton) {
		this.disableUpdateButton = disableUpdateButton;
	}

	public boolean isDisableDeleteButton() {
		return disableDeleteButton;
	}

	public void setDisableDeleteButton(boolean disableDeleteButton) {
		this.disableDeleteButton = disableDeleteButton;
	}

	public List<AddressTypeCode> getAvailableAddressTypes() {

		if (selectedDriverAddress.getAddressType() != null) {
			boolean found;
			availableAddressTypes.clear();
			AddressTypeCode genericAddressTypeCode, driverAddressTypeCode;
			Iterator<AddressTypeCode> genericAddressTypes = getAddressTypes().iterator();
		
			while (genericAddressTypes.hasNext()) {
				genericAddressTypeCode = genericAddressTypes.next();
				Iterator<DriverAddress> driverAddressTypes = driverAddresses.iterator();
				found = false;
				// if add (this is specifically clicking the add button for addresses)
				if(this.add){
					// if the address type is in the generic list and not on the driver (selected driver address or the list on screen) then a new address can be created under that type
					while (driverAddressTypes.hasNext()) {
						driverAddressTypeCode = driverAddressTypes.next().getAddressType();
							if( (driverAddressTypeCode.getAddressType().equals(genericAddressTypeCode.getAddressType())) && 
									(!driverAddressTypeCode.getAddressType().equals(selectedDriverAddress.getAddressType().getAddressType()))) {
								found = true;
								break;  // no need to continue looking as this address type already exists for the driver
							}
					} 
				// if edit then we want to look for only the one that is selected to add it (all others should be "found/excluded"
				}else{
					if(!genericAddressTypeCode.getAddressType().equals(selectedDriverAddress.getAddressType().getAddressType())){
						found = true;
					}
				}
				if (!found) {
					// NOTE: we need to create a shallow copy (clone) to protect against corruption of the object in the list
					availableAddressTypes.add(new AddressTypeCode(genericAddressTypeCode));
				}
					
			}
		}
			
		return availableAddressTypes;
	}
	
	private boolean foundAddressTypeForAdd(AddressTypeCode masterAddressTypeCode,AddressTypeCode onDriverAddressTypeCode,AddressTypeCode selectedAddressTypeCode){
		boolean found = false;
		// if a address type code is on the driver that is in the master and not equal to the one selected
		if( (onDriverAddressTypeCode.getAddressType().equals(masterAddressTypeCode.getAddressType())) && 
				(!onDriverAddressTypeCode.getAddressType().equals(selectedAddressTypeCode.getAddressType()))) {
			found = true;
		}
		
		return found;
	}
	
	private boolean foundAddressTypeForEdit(AddressTypeCode masterAddressTypeCode,AddressTypeCode inListAddressTypeCode,AddressTypeCode selectedAddressTypeCode){
		boolean retval = false;
		
		return retval;
	}
	
	public void setAvailableAddressTypes(List<AddressTypeCode> availableAddressTypes) {
		this.availableAddressTypes = availableAddressTypes;
	}

	public String getCustomerLOVParam() {
		return customerLOVParam;
	}

	public void setCustomerLOVParam(String customerLOVParam) {
		this.customerLOVParam = customerLOVParam;
	}

	public String getRechargeCustomerLOVParam() {
		return rechargeCustomerLOVParam;
	}

	public void setRechargeCustomerLOVParam(String rechargeCustomerLOVParam) {
		this.rechargeCustomerLOVParam = rechargeCustomerLOVParam;
	}
	
	public void removeCustomerAccountsClientLOV(){
		this.setSelectedExternalAccount(new ExternalAccount());
		this.selectedExternalAccount.setExternalAccountPK(new ExternalAccountPK());
		this.setSelectedRechargeExternalAccount(new ExternalAccount());
		this.selectedRechargeExternalAccount.setExternalAccountPK(new ExternalAccountPK());
		this.rechargeCustomerLOVParam = null;
		driverGrades = driverGradeService.getExternalAccountDriverGrades(selectedExternalAccount);
		costCenters = costCenterService.getCostCenters(selectedExternalAccount);
	}
	
	public void removeCustomerAccountsRechargeLOV(){
		this.setSelectedRechargeExternalAccount(new ExternalAccount());
		this.selectedRechargeExternalAccount.setExternalAccountPK(new ExternalAccountPK());
		this.rechargeCustomerLOVParam = null;
	}
	
	public String getViewMode() {
		return viewMode;
	}

	public void setViewMode(String viewMode) {
		this.viewMode = viewMode;
	}

	public int getSelectionIndex() {
		return selectionIndex;
	}

	public void setSelectionIndex(int selectionIndex) {
		this.selectionIndex = selectionIndex;
	}
	public String getAccountAndRechargeAccountAreDifferent() {
		return accountAndRechargeAccountAreDifferent;
	}
	public void setAccountAndRechargeAccountAreDifferent(String accountAndRechargeAccountAreDifferent) {
		this.accountAndRechargeAccountAreDifferent = accountAndRechargeAccountAreDifferent;
	}

	public String getSimilarDriverExistForAccount() {
		return similarDriverExistForAccount;
	}

	public void setSimilarDriverExistForAccount(String similarDriverExistForAccount) {
		this.similarDriverExistForAccount = similarDriverExistForAccount;
	}
	
	public String getRelatedDriverAccountAreDifferent() {
		return relatedDriverAccountAreDifferent;
	}

	public void setRelatedDriverAccountAreDifferent(String relatedDriverAccountAreDifferent) {
		this.relatedDriverAccountAreDifferent = relatedDriverAccountAreDifferent;
	}

	public PhoneNumber getHomePhone() {
		return homePhone == null ? new PhoneNumber() : homePhone;
	}

	public void setHomePhone(PhoneNumber homePhone) {
		this.homePhone = homePhone;
	}

	public PhoneNumber getWorkPhone() {
		return workPhone == null ? new PhoneNumber() : workPhone;
	}

	public void setWorkPhone(PhoneNumber workPhone) {
		this.workPhone = workPhone;
	}

	public PhoneNumber getCellPhone() {
		return cellPhone == null ? new PhoneNumber() : cellPhone;
	}

	public void setCellPhone(PhoneNumber cellPhone) {
		this.cellPhone = cellPhone;
	}


	public boolean isSelectedPoolManager() {
		return selectedPoolManager;
	}

	public void setSelectedPoolManager(boolean selectedPoolManager) {
		this.selectedPoolManager = selectedPoolManager;
	}
	
	public List<CostCentreCode> getCostCenters() {
		return costCenters;
	}

	public void setCostCenters(List<CostCentreCode> costCenters) {
		this.costCenters = costCenters;
	}

	public CostCentreCode getSelectedCostCenter() {
		return selectedCostCenter;
	}

	public void setSelectedCostCenter(CostCentreCode selectedCostCenter) {
		this.selectedCostCenter = selectedCostCenter;
	}

	public String getAddressSearchPerformed() {
		return addressSearchPerformed;
	}

	public void setAddressSearchPerformed(String addressSearchPerformed) {
		this.addressSearchPerformed = addressSearchPerformed;
	}


	public String getPreferredPhone() {
		return preferredPhone;
	}

	public void setPreferredPhone(String preferredPhone) {
		this.preferredPhone = preferredPhone;
	}
     
	public String getDriverStatus() {
		return driverStatus;
	}

	public void setDriverStatus(String driverStatus) {
		this.driverStatus = driverStatus;
	}

	public String getConcatAddress() {
		return concatAddress;
	}

	public void setConcatAddress(String concatAddress) {
		this.concatAddress = concatAddress;
	}

	public String getSelectedDriverActiveIndicatorFlag() {
		return selectedDriverActiveIndicatorFlag;
	}

	public void setSelectedDriverActiveIndicatorFlag(String selectedDriverActiveIndicatorFlag) {
		this.selectedDriverActiveIndicatorFlag = selectedDriverActiveIndicatorFlag;
	}

	public String getAddressDoneClicked() {
		return addressDoneClicked;
	}

	public void setAddressDoneClicked(String addressDoneClicked) {
		this.addressDoneClicked = addressDoneClicked;
	}
	
	public boolean isHasInactiveParent() {
		return hasInactiveParent;
	}

	public void setHasInactiveParent(boolean hasInactiveParent) {
		this.hasInactiveParent = hasInactiveParent;
	}
	
	public String getActivatedParentWithInactiveChildren() {
		return activatedParentWithInactiveChildren;
	}

	public void setActivatedParentWithInactiveChildren(
			String activatedParentWithInactiveChildren) {
		this.activatedParentWithInactiveChildren = activatedParentWithInactiveChildren;
	}
	public String getInactivatedParentWithActiveChildren() {
		return inactivatedParentWithActiveChildren;
	}

	public void setInactivatedParentWithActiveChildren(
			String inactivatedParentWithActiveChildren) {
		this.inactivatedParentWithActiveChildren = inactivatedParentWithActiveChildren;
	}

	public CostCentreCode getInitialCostCenter() {
		return initialCostCenter;
	}

	public void setInitialCostCenter(CostCentreCode initialCostCenter) {
		this.initialCostCenter = initialCostCenter;
	}

	public String getAllocMultUnitsCostCtrAddressChg() {
		return allocMultUnitsCostCtrAddressChg;
	}

	public void setAllocMultUnitsCostCtrAddressChg(
			String allocMultUnitsCostCtrAddressChg) {
		this.allocMultUnitsCostCtrAddressChg = allocMultUnitsCostCtrAddressChg;
	}


}
