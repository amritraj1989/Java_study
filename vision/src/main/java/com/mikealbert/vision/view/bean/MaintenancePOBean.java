package com.mikealbert.vision.view.bean;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
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
import javax.faces.event.ActionEvent;

import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.ToggleEvent;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import com.mikealbert.common.MalConstants;
import com.mikealbert.data.DataConstants;
import com.mikealbert.data.dao.MaintenanceRequestTaskDAO;
import com.mikealbert.data.dao.VehicleScheduleIntervalDAO;
import com.mikealbert.data.entity.CostAvoidanceReason;
import com.mikealbert.data.entity.Doc;
import com.mikealbert.data.entity.FleetMaster;
import com.mikealbert.data.entity.FleetNotes;
import com.mikealbert.data.entity.LatestOdometerReadingV;
import com.mikealbert.data.entity.MaintenanceCategory;
import com.mikealbert.data.entity.MaintenanceCode;
import com.mikealbert.data.entity.MaintenanceRechargeCode;
import com.mikealbert.data.entity.MaintenanceRepairReasons;
import com.mikealbert.data.entity.MaintenanceRequest;
import com.mikealbert.data.entity.MaintenanceRequestStatus;
import com.mikealbert.data.entity.MaintenanceRequestTask;
import com.mikealbert.data.entity.MaintenanceRequestType;
import com.mikealbert.data.entity.Odometer;
import com.mikealbert.data.entity.ServiceProvider;
import com.mikealbert.data.entity.ServiceProviderDiscount;
import com.mikealbert.data.entity.ServiceProviderMaintenanceCode;
import com.mikealbert.data.entity.SupplierServiceTypeCodes;
import com.mikealbert.data.entity.User;
import com.mikealbert.data.enumeration.CorporateEntity;
import com.mikealbert.data.enumeration.LogBookTypeEnum;
import com.mikealbert.data.enumeration.MaintenanceRequestStatusEnum;
import com.mikealbert.data.util.DisplayFormatHelper;
import com.mikealbert.data.vo.AccountVO;
import com.mikealbert.data.vo.HistoricalMaintCatCodeVO;
import com.mikealbert.data.vo.MaintenanceInvoiceCreditVO;
import com.mikealbert.data.vo.VehicleInformationVO;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.service.ContractService;
import com.mikealbert.service.DriverService;
import com.mikealbert.service.FleetMasterService;
import com.mikealbert.service.GlDistributionService;
import com.mikealbert.service.LogBookService;
import com.mikealbert.service.LookupCacheService;
import com.mikealbert.service.MaintenanceCodeService;
import com.mikealbert.service.MaintenanceInvoiceService;
import com.mikealbert.service.MaintenanceRequestService;
import com.mikealbert.service.OdometerService;
import com.mikealbert.service.QuotationService;
import com.mikealbert.service.ServiceProviderService;
import com.mikealbert.service.SuppServiceTypeCodeService;
import com.mikealbert.service.VehicleScheduleService;
import com.mikealbert.service.WillowConfigService;
import com.mikealbert.service.util.email.Email;
import com.mikealbert.util.MALUtilities;
import com.mikealbert.vision.service.VehicleMaintenanceService;
import com.mikealbert.vision.view.ViewConstants;
import com.mikealbert.vision.view.link.EditViewDriverLink;
import com.mikealbert.vision.vo.LogBookTypeVO;

@Component
@Scope("view")
@SuppressWarnings("deprecation")
public class MaintenancePOBean extends StatefulBaseBean implements EditViewDriverLink {	
	private static final long serialVersionUID = -7170421837551409230L;

	@Resource VehicleMaintenanceService vehicleMaintenanceService;
	@Resource FleetMasterService fleetMasterService;
	@Resource LookupCacheService lookupCacheService;
	@Resource OdometerService odometerService;
	@Resource WillowConfigService willowConfigService;
	@Resource MaintenanceRequestService maintRequestService;
	@Resource DriverService driverService;	
	@Resource MaintenanceRequestTaskDAO maintenanceRequestTaskDAO;
	@Resource LogBookService logBookService;
	@Resource ServiceProviderService serviceProviderService;
	@Resource MaintenanceInvoiceService maintenanceInvoiceService;
	@Resource MaintenanceCodeService maintCodeService;
	@Resource GlDistributionService glDistributionService;
	@Resource QuotationService quotationService;
	@Resource ContractService contractService;	
	@Resource VehicleScheduleService	vehicleScheduleService;
	@Resource VehicleScheduleIntervalDAO	vehicleScheduleIntervalDAO;
	
	private int DEFAULT_DATATABLE_HEIGHT = 225;
	
	private MaintenanceRequest maintenanceRequest;
	private MaintenanceRequest copyOfSavedMaintenanceRequest;
	private FleetMaster unit;
	private String viewMode;
	private Long mrqId;
	private Long fmsId;
	private Long selectedProviderId;
	private Long selectedProviderParentId;
	private String driverId;
	private String maintenanceCategoryCode;
	private LatestOdometerReadingV latestOdometerReading;
	private List<MaintenanceRequestStatus> maintenanceRequestStatuses;
	private List<MaintenanceRequestType> maintenanceRequestTypes;
	private List<MaintenanceCategory> maintenanceCategories;
	private List<MaintenanceRechargeCode> maintenanceRechargeCodes;	
	private List<MaintenanceRepairReasons> maintenanceRepairReasons;
	private List<HistoricalMaintCatCodeVO> historicalMaintCategoryList;
	private List<CostAvoidanceReason> costAvoidanceReasons;
	private List<MaintenanceInvoiceCreditVO> creditTaskList;
	private List<String> rechargeOnlyMaintCodes;
	private List<Doc> creditList;
	private Doc selectedCredit;
	private Doc purchaseOrderDoc;
	private Long currentCreditLineId;
	private Odometer odometer;
	private MaintenanceRequestTask selectedTask;
	private int selectedTaskIndex;
	private BigDecimal markUpAmount = BigDecimal.valueOf(0.00).setScale(2, BigDecimal.ROUND_HALF_UP);
	private String serviceCodeQuery;
	private String maintCodeQuery;
	private String serviceProviderDiscountInfo;
	private String defaultRechargeFlag;
	private MaintenanceRechargeCode defaultRechargeCode;
	private String defaultMarkUpMaintCode;
	private String selectedContactId;
	
	final static String AUTHORIZE_PERSON_UI_ID = "authorizePerson";
	final static String UNIT_PRICE_UI_ID = "unitPrice";
	final static String QTY_UI_ID = "qty";
	final static String MAINTENANCE_CODE_UI_ID = "maintenanceCode";
	final static String MAINTENANCE_CATEGORY_UI_ID = "maintenanceCategory";	
	final static String SERVICE_PROVIDER_UI_ID = "maintenancePOPanel:serviceProvider";
	final static String RECHARGE_CODE = "rechargeCode";
	final static String SERVICE_CODE_UI_ID = "serviceCode";
	final static String START_DATE_UI_ID = "maintenancePOPanel:actualStartDate";
	final static String END_DATE_UI_ID = "maintenancePOPanel:plannedEndDate";
	final static String COST_AVOIDANCE_REASON = "costAvoidanceReason";
	final static String COST_AVOIDANCE_AMOUNT = "costAvoidanceAmount";
	final static String GOODWILL_REASON = "goodwillReason";
	final static String GOODWILL_AMOUNT = "goodwillAmount";
	final static String GOODWILL_PERCENT = "goodwillPercent";
	final static String ODO_UI_ID = "maintenancePOPanel:currentOdometerReading";
	static final String MAINT_REQUEST_REASON_CODE = "NORMAL";
	final static String GROWL_UI_ID = "growl";
	
	final static String SPL_SERVICE_PROVIDER_CODE = "MC-COMDATA";
	final static String SPL_PAYEE_NO = "00160419";
	
	private List<LogBookTypeVO> logBookTypes;	
	
	private List<String> distinctHistoricalCategories;
	private boolean isLeasePlanServiceProvider;
	private List<FleetNotes> jobNotes;
	private boolean isJobNoteExist;
	private List<AccountVO> leasePlanPayeeList;
	private boolean validationSuccess;
	private boolean showHistoricalCategoryIconIndicator;
	private String defaultRentalFeeCategoryCode;
	private String defaultERSCategoryCode;
	private String defaultVehicleRentalFeeCode;
	private String defaultVehicleERSFeeCode;
	private String defaultRentalCategoryCode = "RENTAL";
	//HPS-2946 new category code for ERS
	private String defaultMiscCategoryCode = "MISC_SVCS";
	
	// indicates whether a maintenanceCode was set (a decode resulted in a maintenanceCode, category and service code object being set) 
	// by mafs code or by service provider code. This was introduced and is important so that we can skip decoding a second time
	// if the user already decoded (unless they perform an actions showing that they intend to change something)
	// thru he UI we can "unset" when the user pressees keys or clicks buttins and in thay way hopefully control this
	private boolean maintCodeSet = false;
	// indicates whether a maintenanceCode was found (selected) by mafs code or by service provider code, via autoComplete or within the decode
	private boolean maintCodeSelected = false;
	
	private boolean showChangeGLDistConfirmDialog = false;
	private	boolean	showCreditLink = false;
	private Email alertEmail;
	private List<MaintenanceRechargeCode> maintenanceRechargeCodesFromDB;	
	private String maintenanceMarkupCategoryCode;
	private String maintenanceMarkupCode;
	private String maintenanceMarkupRechargeCode;
	private String	maintRechargeCodeOfSelectedTask;
	private boolean 	hasPermission;
	private boolean		isReadMode;
	private String	serviceTypeCode;
	private VehicleInformationVO vehicleInfo;
	private String accountType;
	private String accountCode;
	private boolean vehicleNotOnContract = false;
	private boolean partialCodeDecodeOccured = false;
	private boolean hasSaveActionOccured = false;
	private boolean isNextDisabled = false;
	private boolean isPrevDisabled = false;
	private MaintenanceRequestTask maintenanceFeeLine;
	private String vehicleNotOnMaintOrRiskMgmt;
	
	private String	selectedAuthCode;
	private boolean hasPermissionOnAuthCode = false;
	private boolean selectedTaskDiscountLocked = false;
	private boolean waiveOutOfNetworkSurcharge = false;

	List<SupplierServiceTypeCodes> serviceTypeCodeList = new ArrayList<SupplierServiceTypeCodes>();
	@Resource
	private	SuppServiceTypeCodeService	suppServiceTypeCodeService;
	
	public Email getAlertEmail() {
		return alertEmail;
	}


	public void setAlertEmail(Email alertEmail) {
		this.alertEmail = alertEmail;
	}
	
	public void populateAlertEmail(){
		Long contactId = 0L;
		if(MALUtilities.isNotEmptyString(selectedContactId)){
			contactId = Long.parseLong(selectedContactId);
		}
		String path = getFaceExternalContext().getRealPath("/images/logo.png"); //TODO: Will be needed while implementing email html template. 
		logger.debug("Path: " + path);
		//		File file = new File(path);
		
		this.alertEmail = vehicleMaintenanceService.generateExceededAuthEmail(this.maintenanceRequest,contactId);
	}
	
	/**
	 * Initializes the bean
	 */
    @PostConstruct
    public void init(){
    	logger.info("-- Method name: init start");
    	initializeDataTable(600, 200, new int[] {2, 12, 15, 15, 6, 2, 13, 3, 8, 9, 5}).setHeight(DEFAULT_DATATABLE_HEIGHT);      	
    	super.openPage();
    	
        try {         	
        	
        	// load the vehicle info (once for this page)
        	if(!MALUtilities.isEmpty(fmsId) && fmsId.intValue() != 0) {
    			setVehicleInfo(vehicleMaintenanceService.getVehicleInformationByFmsId(this.fmsId));      		
    		} else if(!MALUtilities.isEmpty(mrqId) && mrqId.intValue() != 0) {
    			setVehicleInfo(vehicleMaintenanceService.getVehicleInformationByMrqId(this.mrqId));      		
    		}
        	if((!MALUtilities.isEmpty(fmsId) && fmsId.intValue() != 0) || (!MALUtilities.isEmpty(mrqId) && mrqId.intValue() != 0)){
	        	accountType = getVehicleInfo().getClientAccountType();
	        	accountCode = getVehicleInfo().getClientAccountNumber();
        	}
        	
        	defaultMarkUpMaintCode = willowConfigService.getConfigValue("MAINT_NON_NETWORK_CODE");
        	defaultRentalFeeCategoryCode = willowConfigService.getConfigValue("MAINT_RENTAL_CAT_CODE");
        	defaultERSCategoryCode = willowConfigService.getConfigValue("MAINT_ERS_CAT_CODE");//HPS-2946 get ERS category code
    		defaultVehicleRentalFeeCode = willowConfigService.getConfigValue("MAINT_RENTAL_FEE_CODE");
    		defaultVehicleERSFeeCode = willowConfigService.getConfigValue("MAINT_ERS_FEE_CODE");//HPS-2946 get ERS maint code
        	rechargeOnlyMaintCodes = initializeListOfRechargeOnlyMaintCodes();
        	maintenanceRechargeCodesFromDB = lookupCacheService.getMaintenanceRechargeCodes();
        	setMaintenanceRequestTypes(lookupCacheService.getMaintenanceRequestTypes());
        	setMaintenanceCategories(lookupCacheService.getMaintenanceCategories());
        	setMaintenanceRepairReasons(lookupCacheService.getMaintenanceRepairReasons());
        	setMaintenanceRechargeCodes(maintenanceRechargeCodesFromDB);
        	setCostAvoidanceReasons(lookupCacheService.getCostAvoidanceReasons());
        	setMaintenanceMarkupCategoryCode(willowConfigService.getConfigValue("MAINT_MARKUP_LN_CAT_CODE"));
        	setMaintenanceMarkupCode(willowConfigService.getConfigValue("MAINT_MARKUP_LN_CODE"));
        	setMaintenanceMarkupRechargeCode(willowConfigService.getConfigValue("MAINT_MARKUP_LN_RECH_CODE"));        	
        	leasePlanPayeeList = willowConfigService.getLeasePlanPayeeDetail();
        	serviceTypeCodeList	= suppServiceTypeCodeService.getSuppServiceTypeCodes(null);
        	if(viewMode != null && (getViewMode().equals(ViewConstants.VIEW_MODE_EDIT) || getViewMode().equals(ViewConstants.VIEW_MODE_READ))){
        		initEditMode();
        	} else {
        		initAddMode();
        	}
        	
        	//Log Book integration
        	setLogBookTypes(new ArrayList<LogBookTypeVO>());
        	getLogBookTypes().add(new LogBookTypeVO(LogBookTypeEnum.TYPE_ACTIVITY, false));
        	getLogBookTypes().add(new LogBookTypeVO(LogBookTypeEnum.TYPE_EXTERNAL_JOB_NOTES, true));
        	getLogBookTypes().get(1).setRenderZeroEntries(false);    	
        	getLogBookTypes().add(new LogBookTypeVO(LogBookTypeEnum.TYPE_FM_UNIT_NOTES, getMaintenanceRequest().getFleetMaster(), true)); 
        	getLogBookTypes().get(2).setRenderZeroEntries(false);    	
        	getLogBookTypes().add(new LogBookTypeVO(LogBookTypeEnum.TYPE_EXTERNAL_FLEET_NOTES, getMaintenanceRequest().getFleetMaster(), true)); 
        	getLogBookTypes().get(3).setRenderZeroEntries(false);  
        	
        	hasPermission	= super.hasPermission();
        	isReadMode	= isReadModeCheck();
        	setOdometer(odometerService.getCurrentOdometer(getMaintenanceRequest().getFleetMaster()));
    		if(!MALUtilities.isEmpty(getOdometer())){
    			getMaintenanceRequest().setUnitofMeasureCode(odometerService.convertOdoUOMCode(getOdometer().getUomCode()));        	
    		}
        	List<MaintenanceRequestTask> itemTaskList = getMaintenanceRequest().getMaintenanceRequestTasks();
        	resetIndex(itemTaskList);
        	if(!MALUtilities.isEmpty(maintenanceRequest.getFleetMaster())){
        		distinctHistoricalCategories = maintRequestService.getDistinctHistoricalCatCodes(maintenanceRequest.getFleetMaster().getVin(), maintenanceRequest.getMrqId());
        	}
        	loadServiceProviderDiscounts(getMaintenanceRequest().getActualStartDate());
        	
        	setCreditTabInfo(maintenanceRequest);
        	//added for bug FM1116, to avoid multiple calls to db in case of update is called when Ajax completes on UI
        	setShowCreditLink(isCreditExists());
        	// handle initial population
//        	populateAlertEmail();
        	
        	getGLDistData();
        	setVehicleStatusAsOnServiceRequestDate();
        	// make sure the variable that allow maintenance code object lookup are reset.
        	resetMaintCodePrivateVars();
        	setMaintCodeSet(false);
        	adjustTaskDataTableHeight();
        	hasPermissionOnAuthCode = hasPermission("fleetMaintAssocPoToSchedInterval");
        	
        	logger.info("-- Method name: init end");
		} catch(Exception ex) {
			logger.error(ex);
			super.addErrorMessage("generic.error", ex.getMessage());
		}
    }
    
    
    private void initEditMode(){
		thisPage.setPageDisplayName(ViewConstants.DISPLAY_NAME_FLEET_MAINT_EDIT_PO);
		// Added for Bug 16387
				boolean isMarkUpApplicable = false;
    	setMaintenanceRequest(maintRequestService.getMaintenanceRequestByMrqId(getMrqId()));
		    	for (MaintenanceRequestTask mrt : getMaintenanceRequest().getMaintenanceRequestTasks()) {
		    		BigDecimal amount = maintenanceRequestTaskDAO.getActualInvoiceAmount(mrt.getMrtId());
		    		if(!isMarkUpApplicable ){
						if(amount != null){
							isMarkUpApplicable = true;
						}
					}
		            mrt.setActualInvoiceAmount(amount);
		            if(mrt.getMaintCatCode().equals(maintenanceMarkupCategoryCode)){ if(isMarkUpApplicable){mrt.setActualInvoiceAmount(mrt.getRechargeTotalCost());}}
				} // Bug 16387 ends
		setCopyOfSavedMaintenanceRequest(vehicleMaintenanceService.copyMaintenanceRequest(getMaintenanceRequest()));
		setServiceTypeCode(this.maintenanceRequest.getServiceProvider().getServiceTypeCode());
		setSelectedProviderId(this.maintenanceRequest.getServiceProvider().getServiceProviderId());
		
		ServiceProvider parent = serviceProviderService.getParentProvider(this.selectedProviderId);
		if(!MALUtilities.isEmpty(parent)){
			setSelectedProviderParentId(parent.getServiceProviderId());
		}else{
			setSelectedProviderParentId(null);
		}
		
		setLatestOdometerReading(getMaintenanceRequest().getFleetMaster().getLatestOdometerReading());
		setMaintenanceRequestStatuses(lookupCacheService.getMaintenanceRequestStatuses());
		setMarkUpAmount(getMarkUpTotal());
		
		if(!maintRequestService.isMaintenanceRequestEditable(getMaintenanceRequest())){ 
			setViewMode(ViewConstants.VIEW_MODE_READ);
			initReadMode();
		}
		updateLeasePlanServiceProvider(getMaintenanceRequest().getServiceProvider());
		jobNotes = vehicleMaintenanceService.getFleetNotesByMaintReqId(getMrqId());
		if(jobNotes != null && jobNotes.size() > 0){
			setJobNoteExist(true);
		}
		
		waiveOutOfNetworkSurcharge = maintRequestService.hasWaivedOutOfNetworkSurcharge(this.getMaintenanceRequest());
    }
    
    private void initReadMode(){
		thisPage.setPageDisplayName(ViewConstants.DISPLAY_NAME_FLEET_MAINT_VIEW_PO);
    	setSelectedProviderId(this.maintenanceRequest.getServiceProvider().getServiceProviderId());
    	setServiceTypeCode(this.maintenanceRequest.getServiceProvider().getServiceTypeCode());
		ServiceProvider parent = serviceProviderService.getParentProvider(this.selectedProviderId);
		if(!MALUtilities.isEmpty(parent)){
			setSelectedProviderParentId(parent.getServiceProviderId());
		}else{
			setSelectedProviderParentId(null);
		}
    }  
    
    private void initAddMode(){
    	Calendar calendar = Calendar.getInstance();   

    	setMaintenanceRequest(new MaintenanceRequest());
    	getMaintenanceRequest().setMaintenanceRequestTasks(new ArrayList<MaintenanceRequestTask>());
    	setCopyOfSavedMaintenanceRequest(new MaintenanceRequest());
    	getCopyOfSavedMaintenanceRequest().setMaintenanceRequestTasks(new ArrayList<MaintenanceRequestTask>());
    	getMaintenanceRequest().setFleetMaster(getFmsId()!= null? fleetMasterService.getFleetMasterByFmsIdWithOdoList(getFmsId()): null);
    	getMaintenanceRequest().setServiceProvider(new ServiceProvider());
    	getMaintenanceRequest().setMaintReqStatus("B");
    	getMaintenanceRequest().setMaintReqType("MAINT");
    	getMaintenanceRequest().setActualStartDate(calendar.getTime());
    	calendar.add(Calendar.DAY_OF_MONTH, 1);    	
    	getMaintenanceRequest().setPlannedEndDate(calendar.getTime()); 
    	getMaintenanceRequest().setJobNo(vehicleMaintenanceService.generateJobNumber(getLoggedInUser().getCorporateEntity()));
    	getMaintenanceRequest().setServiceProviderMarkupInd(MalConstants.FLAG_N);
    	getMaintenanceRequest().setCurrentOdo(0L);
    	setMaintenanceRequestStatuses(lookupCacheService.getMaintenanceRequestStatuses());
    	setServiceTypeCode("MAINT");

    	waiveOutOfNetworkSurcharge = false;
    	
//    	if (!MALUtilities.isEmpty(getAuthorizationNo())){
//    		getMaintenanceRequest().setCouponBookReference(getAuthorizationNo());
//    	}
    }
            
    /**
     * Handles page save button click event
     * @return The calling view or null based on whether the process succeeded for failed, respectively
     */
    public String save(){ 
    	logger.info("-- Method name: save start");
		
    	String nextPage = null;
    	// since the odometer reading can be edited regardless of the state of the po, we need to check for changes.
    	if (ViewConstants.VIEW_MODE_ADD.equals(getViewMode()) || vehicleMaintenanceService.isMaintenancePOModified(getCopyOfSavedMaintenanceRequest(), getMaintenanceRequest())) {
    		autoSave();
			try {
				checkForExceedAuthLimit();
			} catch (MalBusinessException ex) {
				logger.error(ex);
				validationSuccess = false;
				handleException("generic.error",new String[]{ex.getMessage()}, ex, null);
				RequestContext context = RequestContext.getCurrentInstance();
				context.addCallbackParam("failure", true);
				return nextPage;
			}  			
    	}
    	// this block of code is only needed if a user presses the "X" to delete and then
    	// clicks on the "Save" button without making any other changes. 
		if(hasSaveActionOccured){
			hasSaveActionOccured = false;
			try {
				checkForExceedAuthLimit();
			} catch (MalBusinessException ex) {
				logger.error(ex);
				validationSuccess = false;
				handleException("generic.error",new String[]{ex.getMessage()}, ex, null);
				RequestContext context = RequestContext.getCurrentInstance();
				context.addCallbackParam("failure", true);
				return nextPage;
			} 
		}
    	// if the validation is successful and Odo validation was successful.
		if(validationSuccess && validateOdo()){
			super.addSuccessMessage("process.success","Save PO (" + getMaintenanceRequest().getJobNo() + ")");
		}
    	
		logger.info("-- Method name: save end");
    	return nextPage;
    }
    
    public void autoSave(){ 
    	try{
    		logger.info("-- Method name: autosave start");
    		// there is no need to autosave if nothing has changed
	    	if(vehicleMaintenanceService.isMaintenancePOModified(getCopyOfSavedMaintenanceRequest(), getMaintenanceRequest())){
	    		if(validatePurhaseOrder()) {
	    			if(isLeasePlanServiceProvider() == false){
	    				getMaintenanceRequest().setServiceProviderContactInfo(null);
	    			}
	    			
                    if(maintRequestService.isMaintenanceRequestEditable(getMaintenanceRequest())){
                    	validateAndCorrectTotalAmount(getMaintenanceRequest());
	                    manageMarkUpLines();
	                    manageFeeLines();
	               }

	    			setCopyOfSavedMaintenanceRequest(vehicleMaintenanceService.copyMaintenanceRequest(maintRequestService.saveOrUpdateMaintnenacePO(getMaintenanceRequest(), getLoggedInUser().getEmployeeNo())));

	    			setMaintenanceRequest(maintRequestService.getMaintenanceRequestByMrqId(getCopyOfSavedMaintenanceRequest().getMrqId()));
	    			//added for bug FM1116, to avoid multiple calls to db in case of update is called when Ajax completes on UI
	            	setShowCreditLink(isCreditExists());
	    			resetIndex(getMaintenanceRequest().getMaintenanceRequestTasks());
	    			
	    			if(!maintRequestService.isMaintenanceRequestEditable(getMaintenanceRequest())
	    					&& MALUtilities.isEmpty(getMaintenanceRequest().getMrqMrqId()) ){  //<-- No mrqMrqId means not a Goodwill PO
	    				setViewMode(ViewConstants.VIEW_MODE_READ);
	    				initReadMode();
	    			}else{
	    				setViewMode(ViewConstants.VIEW_MODE_EDIT);
	    			}
	    			
	    			validationSuccess = true; 
	    		} else {
	    			validationSuccess = false;
	    			if(validateItemTaskData() == false){
	    				RequestContext context = RequestContext.getCurrentInstance();
	    				context.addCallbackParam("failure", true);
	    				return;
	    			}
	    		}    		
	    	}else{
	    		validationSuccess = true;
	    	}

		} catch (Exception ex) {
			logger.error(ex);
			validationSuccess = false;
			handleException("generic.error",new String[]{ex.getMessage()}, ex, null);
			RequestContext context = RequestContext.getCurrentInstance();
			context.addCallbackParam("failure", true);
			return;
			}
    	logger.info("-- Method name: autosave end");
	} 


    
	    
    private void validateAndCorrectTotalAmount(MaintenanceRequest mrq){
    	BigDecimal amount = null;
    	if(!MALUtilities.isEmpty(mrq.getMaintenanceRequestTasks())){
			for(MaintenanceRequestTask line : mrq.getMaintenanceRequestTasks()){
				amount = new BigDecimal(0.00);
		    	if(!(MALUtilities.isEmpty(line.getTaskQty()) && MALUtilities.isEmpty(line.getUnitCost()))){
		    		amount = line.getUnitCost().multiply(line.getTaskQty()).setScale(2, BigDecimal.ROUND_HALF_UP); 
					if( amount.compareTo(line.getTotalCost().setScale(2, BigDecimal.ROUND_HALF_UP)) != 0){
						line.setTotalCost(amount);
					}
				}
			}
    	}
    }
    
    private void loadServiceProviderDiscounts(Date startDate) {
    	List<ServiceProviderDiscount> discounts;
    	Long serviceProviderId = getMaintenanceRequest().getServiceProvider().getServiceProviderId();
    	Date effectiveDate = MALUtilities.clearTimeFromDate(Calendar.getInstance().getTime());
    	
    	if (startDate != null) {
    		effectiveDate = MALUtilities.clearTimeFromDate(startDate);
    	}
    	if (getMaintenanceRequest().getActualStartDate() != null) {
    		effectiveDate = MALUtilities.clearTimeFromDate(getMaintenanceRequest().getActualStartDate());
    	}
    	
    	if (serviceProviderId == null) {
    		serviceProviderId = getSelectedProviderId();
    	}
    	
		discounts = serviceProviderService.getServiceProviderDisountsByDate(serviceProviderId, effectiveDate);
		String msg = DisplayFormatHelper.formatSupplierDiscountForDisplay(getMaintenanceRequest().getServiceProvider(), discounts, "<br/>");
		setServiceProviderDiscountInfo(msg);
    }
    
    public void refreshServiceProviderDiscounts(Date startDate) {
    	if (startDate != null) {
    		loadServiceProviderDiscounts(startDate);
    		
			// TODO: this is need to help with performance; but 
			// should then be in a more appropriately named method.
			resetDefaultRechangeFlag();
    	}
    }
    
    
    
    /**
     * Handles page Create PO button click event
     * @return The calling view
     */
    public String completePO(){
    	logger.info("-- Method name: completePO start");
    	try{
    		if(validatePurhaseOrder()){
    			//When maintenance request is in a state prior to 'I'n Progress, attempt to authorize it first.
    			if(maintRequestService.convertPOStatus(getMaintenanceRequest().getMaintReqStatus()).getSortOrder() < 5){
    				maintRequestService.authorizeMRQ(getMaintenanceRequest(), CorporateEntity.MAL, getLoggedInUser().getEmployeeNo());
    				setMaintenanceRequest(maintRequestService.getMaintenanceRequestByMrqId(getMaintenanceRequest().getMrqId()));
    				resetIndex(getMaintenanceRequest().getMaintenanceRequestTasks());
    			}

    			//When maintenance request has been authorized, attempt to 'C'omplete it.
    			if(maintRequestService.convertPOStatus(getMaintenanceRequest().getMaintReqStatus()).getSortOrder() > 4){
    				maintRequestService.completeMRQ(getMaintenanceRequest(), CorporateEntity.MAL, getLoggedInUser().getEmployeeNo()); 
    				setMaintenanceRequest(maintRequestService.getMaintenanceRequestByMrqId(getMaintenanceRequest().getMrqId()));
    			}

    			setCopyOfSavedMaintenanceRequest(vehicleMaintenanceService.copyMaintenanceRequest(getMaintenanceRequest()));
    			resetIndex(getMaintenanceRequest().getMaintenanceRequestTasks());
    			setMaintenanceRequestStatuses(lookupCacheService.getMaintenanceRequestStatuses());

    			//When the maintenance request is in a 'I'n Progress state, or in one after, it has been authorized and
    			//needs to be locked down.
    			if(!maintRequestService.isMaintenanceRequestEditable(getMaintenanceRequest())){
    				setViewMode(ViewConstants.VIEW_MODE_READ);
    			}

    			checkForExceedAuthLimit();	
    			
    			//Display success message when PO is completed or saved
    			if(getMaintenanceRequest().getMaintReqStatus().equals(MaintenanceRequestStatusEnum.MAINT_REQUEST_STATUS_COMPLETE.getCode())){
        			super.addSuccessMessage("process.success", "Complete PO (" + getMaintenanceRequest().getJobNo() + ")");
        			// only show the dialog if the user has the appropriate resource permissions granted.
        			// LOVs and Dialogs don't have a calculated PageId because they don't have a specific 
        			// url/resource path and don't inherit from StatefulBaseBean. I am using a ViewConstant instead.
        			if(this.hasPermission(ViewConstants.DIALOG_ID_GL_CODES)){
            			setShowChangeGLDistConfirmDialog(true);
            			isShowChangeGLDistDialog();
            			getGLDistData();
        			}
    			} else { 	
        			super.addSuccessMessage("process.success", "Save PO (" + getMaintenanceRequest().getJobNo() + ")");
    			}
    			
    		}
	    	} catch(Exception ex) {
	    		handleException("generic.error",new String[]{ex.getMessage()}, ex, null);
				RequestContext context = RequestContext.getCurrentInstance(); //TODO Is this needed?
				context.addCallbackParam("failure", true);				      //TODO Is this needed?
	    }    	
    	logger.info("-- Method name: completePO end");
    	return null;
    }
    
    
    /**
     * Handles page Authorize PO button click event
     * @return The calling view
     */
    public String authorizePO(){
    	logger.info("-- Method name: authorizePO start");
    	try{   
    		if(validatePurhaseOrder()){
    			//When maintenance request is in a state prior to 'I'n Progress, attempt to authorize it first.
    			if(maintRequestService.convertPOStatus(getMaintenanceRequest().getMaintReqStatus()).getSortOrder() < 5){
    				maintRequestService.authorizeMRQ(getMaintenanceRequest(), CorporateEntity.MAL, getLoggedInUser().getEmployeeNo());
    				setMaintenanceRequest(maintRequestService.getMaintenanceRequestByMrqId(getMaintenanceRequest().getMrqId()));     			
    			}

    			setCopyOfSavedMaintenanceRequest(vehicleMaintenanceService.copyMaintenanceRequest(getMaintenanceRequest()));
    			resetIndex(getMaintenanceRequest().getMaintenanceRequestTasks());
    			setMaintenanceRequestStatuses(lookupCacheService.getMaintenanceRequestStatuses());

    			checkForExceedAuthLimit();	

    			//When the maintenance request is in an 'I' in Progress state, or beyond, it has been authorized and
    			//needs to be locked down.
    			if(!maintRequestService.isMaintenanceRequestEditable(getMaintenanceRequest())){
    				setViewMode(ViewConstants.VIEW_MODE_READ);
    			}
    			
    			//Display success message when PO is completed or saved
    			if(getMaintenanceRequest().getMaintReqStatus().equals(MaintenanceRequestStatusEnum.MAINT_REQUEST_STATUS_INPROGRESS.getCode())){
        			super.addSuccessMessage("process.success", "Authorize PO (" + getMaintenanceRequest().getJobNo() + ")");    				
    			} else { 	
        			super.addSuccessMessage("process.success", "Save PO (" + getMaintenanceRequest().getJobNo() + ")");   
    			}
    			
    		}
    	} catch(Exception ex) {
    		handleException("generic.error",new String[]{ex.getMessage()}, ex, null);
			RequestContext context = RequestContext.getCurrentInstance();  //TODO Is this needed?
			context.addCallbackParam("failure", true);			           //TODO Is this needed?
    	} 
    	logger.info("-- Method name: authorizePO end");	
    	return null;
    }
    
    //TODO Implement FM-684
    public String cancelJobAuthorization(){ 
    	try {
    		manageMarkUpLines();
    		manageFeeLines();
    		maintRequestService.cancelAuthorization(getMaintenanceRequest(), CorporateEntity.MAL, getLoggedInUser().getEmployeeNo());
			setMaintenanceRequest(maintRequestService.getMaintenanceRequestByMrqId(getMaintenanceRequest().getMrqId()));     			
			setCopyOfSavedMaintenanceRequest(vehicleMaintenanceService.copyMaintenanceRequest(getMaintenanceRequest()));
			resetIndex(getMaintenanceRequest().getMaintenanceRequestTasks());
			setMaintenanceRequestStatuses(lookupCacheService.getMaintenanceRequestStatuses());
			setViewMode(ViewConstants.VIEW_MODE_EDIT);			
			setMarkUpAmount(new BigDecimal(0.0));
			super.addSuccessMessage("process.success", "Cancel PO Authorization (" + getMaintenanceRequest().getJobNo() + ")"); 
    	} catch(Exception ex) {
    		handleException("generic.error",new String[]{ex.getMessage()}, ex, null);
			RequestContext context = RequestContext.getCurrentInstance();  //TODO Is this needed?
			context.addCallbackParam("failure", true);			           //TODO Is this needed?
    	} 		
    	return null;
    }
    
    //TODO Implement FM-679
    public String closeJob(){
    	try {
    		maintRequestService.closeJob(getMaintenanceRequest(), CorporateEntity.MAL, getLoggedInUser().getEmployeeNo());
			setMaintenanceRequest(maintRequestService.getMaintenanceRequestByMrqId(getMaintenanceRequest().getMrqId()));     			
			setCopyOfSavedMaintenanceRequest(vehicleMaintenanceService.copyMaintenanceRequest(getMaintenanceRequest()));
			resetIndex(getMaintenanceRequest().getMaintenanceRequestTasks());
			setMaintenanceRequestStatuses(lookupCacheService.getMaintenanceRequestStatuses());
			setViewMode(ViewConstants.VIEW_MODE_READ);
			super.addSuccessMessage("process.success", "Close Job - No PO (" + getMaintenanceRequest().getJobNo() + ")");
    	} catch(Exception ex) {
    		handleException("generic.error",new String[]{ex.getMessage()}, ex, null);
			RequestContext context = RequestContext.getCurrentInstance();  //TODO Is this needed?
			context.addCallbackParam("failure", true);			           //TODO Is this needed?
    	}     	
    	return null;
    }
    
    public void deleteAuthNo(){
   		getMaintenanceRequest().setCouponBookReference(null);
   		setSelectedAuthCode(null);
    }
    
    public void resetDefaultRechangeFlag(){
        //Set Recharge values back to null
    	defaultRechargeFlag = null;
    	defaultRechargeCode = null;
    }
    
    private void checkForExceedAuthLimit() throws MalBusinessException {
    	
//    	String msg = vehicleMaintenanceService.generateExceededAuthMsg(this.vehicleInfo);
    	
    	//for  issue 1157, growl will appear if amount is more than auth limit
    	BigDecimal rechargeTotal = maintRequestService.sumRechargeTotal(this.maintenanceRequest);
    	BigDecimal mafsAuthLimit = quotationService.getMafsAuthorizationLimit(getLoggedInUser().getCorporateEntity().getCorpId(),this.accountType,this.accountCode,this.maintenanceRequest.getFleetMaster().getUnitNo());
		String summary = null; 
		if(!MALUtilities.isEmpty(rechargeTotal) && !MALUtilities.isEmpty(mafsAuthLimit) && rechargeTotal.compareTo(mafsAuthLimit) > 0){
			summary	= vehicleMaintenanceService.generateExceededAuthSummary(getLoggedInUser().getCorporateEntity(),this.accountType,this.accountCode,this.maintenanceRequest.getFleetMaster());
		}
		// if the maintenance request status is waiting on client approval then show the growl.
		if(getMaintenanceRequest().getMaintReqStatus().equalsIgnoreCase(MaintenanceRequestStatusEnum.MAINT_REQUEST_STATUS_WAITING_ON_CLIENT_APPROVAL.getCode()) && !MALUtilities.isEmptyString(summary)){   				
			String msg = "";
			try {
				msg = vehicleMaintenanceService.generateExceededAuthMsg(this.vehicleInfo);				
			} catch (MalBusinessException e) { 
				msg = "This PO is over the client's limits. There is no contact set up for the client please contact the Transition Manager to have this set up.";
			} 		
			FacesContext context = FacesContext.getCurrentInstance();     
		    context.addMessage("warnings", new FacesMessage(FacesMessage.SEVERITY_WARN, summary, msg));
		    RequestContext.getCurrentInstance().update(GROWL_UI_ID);		
		}
    }
    
    /**
     * Handles page cancel button click event
     * @return The calling view
     */
    public String cancel(){
    	return super.cancelPage();      	
    }
    
       
    /**
     * Logic that determine whether or not to disabled task edits
     * @param index Row index to determine which task to evaluate. 
     *        -1 means row index was unable to be determined from client
     * @return Boolean
     */
	public boolean isEditTaskDisabled(int index){
		boolean isDisabled = false;
		MaintenanceRequestTask selectedTask;
		
		if(index < 0){
			selectedTask = getSelectedTask();
		} else {
			selectedTask = getMaintenanceRequest().getMaintenanceRequestTasks().get(index);						
		}
		
		//Disabled when task is for network mark up or vehicle rental fee
		if(!MALUtilities.isEmpty(selectedTask)
				&& !MALUtilities.isEmpty(selectedTask.getMaintCatCode()) 
				&& selectedTask.getMaintCatCode().equals(getMaintenanceMarkupCategoryCode())){
			isDisabled = true;
		}else if(!MALUtilities.isEmpty(selectedTask)
				&& !MALUtilities.isEmpty(selectedTask.getMaintCatCode()) 
				&& selectedTask.getMaintCatCode().equals(defaultRentalFeeCategoryCode)
				&& (!MALUtilities.isEmpty(selectedTask.getMaintenanceCode()) && !MALUtilities.isEmpty(selectedTask.getMaintenanceCode().getCode()))
				&& selectedTask.getMaintenanceCode().getCode().equals(defaultVehicleRentalFeeCode)){
			isDisabled = true;
		}else if(!MALUtilities.isEmpty(selectedTask)//HPS-2946 to disable edit button for ERS line
				&& !MALUtilities.isEmpty(selectedTask.getMaintCatCode()) 
				&& selectedTask.getMaintCatCode().equals(defaultERSCategoryCode)
				&& (!MALUtilities.isEmpty(selectedTask.getMaintenanceCode()) && !MALUtilities.isEmpty(selectedTask.getMaintenanceCode().getCode()))
				&& selectedTask.getMaintenanceCode().getCode().equals(defaultVehicleERSFeeCode)){
			isDisabled = true;
		}
		
		return isDisabled;
	}
	
    /**
     * Logic that determine whether or not to disabled task deletion
     * @param index Row index to determine which task to evaluate
     * @return Boolean
     */
	public boolean isDeleteTaskDisabled(int index){
		boolean isDisabled = false;
		
		//Disabled when task is for network mark up
		if(getMaintenanceRequest().getMaintenanceRequestTasks().get(index).getMaintCatCode() != null){
			if(getMaintenanceRequest().getMaintenanceRequestTasks().get(index).getMaintCatCode().equals(getMaintenanceMarkupCategoryCode())){
				isDisabled = true;
			}else if(getMaintenanceRequest().getMaintenanceRequestTasks().get(index).getMaintCatCode().equals(defaultRentalFeeCategoryCode)
					&& getMaintenanceRequest().getMaintenanceRequestTasks().get(index).getMaintenanceCode().getCode().equals(defaultVehicleRentalFeeCode)){
				isDisabled = true;
			}else if(getMaintenanceRequest().getMaintenanceRequestTasks().get(index).getMaintCatCode().equals(defaultERSCategoryCode)//HPS-2946 to disbale delete button for ERS line
					&& getMaintenanceRequest().getMaintenanceRequestTasks().get(index).getMaintenanceCode().getCode().equals(defaultVehicleERSFeeCode)){
				isDisabled = true;
			}
		}
		
		return isDisabled;
	}
	
    public boolean isLogNotesDisabled(){
    	boolean isDisabled = true;
    	if(!MALUtilities.isEmpty(getMaintenanceRequest().getMrqId())){
    		isDisabled = false;
    	}
    	return isDisabled;    	
    }
    
    public boolean isAuthorizePOButtonDisabled(){
    	boolean isDisabled = true;
    	if(!MALUtilities.isEmpty(getMaintenanceRequest().getMaintReqStatus())){
	    	int sortOrder = maintRequestService.convertPOStatus(getMaintenanceRequest().getMaintReqStatus()).getSortOrder();
	    	if(sortOrder < 5 && MALUtilities.isEmpty(getMaintenanceRequest().getMrqMrqId())){
	    		isDisabled = false;
	    	}
    	}
    	return isDisabled;    	
    }
    
    public boolean isCompletePOButtonDisabled(){
    	boolean isDisabled = true;
    	if(!MALUtilities.isEmpty(getMaintenanceRequest().getMaintReqStatus())){
	    	int sortOrder = maintRequestService.convertPOStatus(getMaintenanceRequest().getMaintReqStatus()).getSortOrder();    	
	    	if(sortOrder < 7 && MALUtilities.isEmpty(getMaintenanceRequest().getMrqMrqId())){
	    		isDisabled = false;
	    	}    	
    	}
    	return isDisabled;    	
    }
    
    public boolean isDeletePOButtonDisabled(){
    	boolean isDisabled = true;
    	if(!MALUtilities.isEmpty(getMaintenanceRequest().getMaintReqStatus())){
	    	int sortOrder = maintRequestService.convertPOStatus(getMaintenanceRequest().getMaintReqStatus()).getSortOrder();    	
	    	if(sortOrder < 5 || (sortOrder > 7 && sortOrder != 10)){ //HPS-1459 disable Delete Po button if PO status is Hold
	    		isDisabled = false;
	    	}    	
    	}
    	return isDisabled;
    }  
    
    public boolean isCancelAuthorizationButtonDisabled(){
    	boolean isDisabled = true;
    	if(!MALUtilities.isEmpty(getMaintenanceRequest().getMaintReqStatus())){
	    	boolean isInvoicePosted = maintenanceInvoiceService.hasPostedInvoice(getMaintenanceRequest());
	    	int sortOrder = maintRequestService.convertPOStatus(getMaintenanceRequest().getMaintReqStatus()).getSortOrder();
	
	    	if(isInvoicePosted == false){
	    		if(((sortOrder > 4 && sortOrder < 8) || sortOrder == 10)//HPS-1459 enable Cancel Po Authorize button if PO status is Hold
	        			&& MALUtilities.isEmpty(getMaintenanceRequest().getMrqMrqId())){
	        		isDisabled = false;
	        	}
			}
    	}
    	return isDisabled;    	
    }    
    
    public boolean isCloseJobButtonDisabled(){
    	boolean isDisabled = true;
    	if(!MALUtilities.isEmpty(getMaintenanceRequest().getMaintReqStatus())){
	    	int sortOrder = maintRequestService.convertPOStatus(getMaintenanceRequest().getMaintReqStatus()).getSortOrder(); 
	    	if(!maintenanceInvoiceService.hasPostedInvoice(getMaintenanceRequest())){
	    		if(sortOrder < 7 || sortOrder == 8){
	    			isDisabled = false;
	    		}    	
	    	}
    	}
    	return isDisabled;    	
    } 
    
    public boolean isGLDistributionButtonDisabled(){
    	boolean isDisabled = true;
    	if(!MALUtilities.isEmpty(getMaintenanceRequest().getMaintReqStatus())){
	    	boolean isInvoicePosted = maintenanceInvoiceService.hasPostedInvoice(getMaintenanceRequest());
	    	if(isInvoicePosted == false){
	        	if((getMaintenanceRequest().getMaintReqStatus()).equalsIgnoreCase(MaintenanceRequestStatusEnum.MAINT_REQUEST_STATUS_COMPLETE.getCode())){
	        		isDisabled = false;
	        	}
			}
    	}
    	return isDisabled;    	
    } 
    
    @Override
    public void editViewDriver(String driverId) {
    	Map<String, Object> restoreStateValues = new HashMap<String, Object>();
		restoreStateValues.put(ViewConstants.VIEW_PARAM_MRQ_ID, getMaintenanceRequest().getMrqId());
		restoreStateValues.put(ViewConstants.VIEW_PARAM_FMS_ID, getMaintenanceRequest().getFleetMaster().getFmsId());
		restoreStateValues.put(ViewConstants.VIEW_PARAM_MODE, getViewMode());
		super.saveRestoreStateValues(restoreStateValues);
				
		Map<String, Object> nextPageValues = new HashMap<String, Object>();				
		nextPageValues.put(ViewConstants.VIEW_PARAM_DRIVER_ID, driverId);
		nextPageValues.put(ViewConstants.VIEW_PARAM_CALLERS_NAME, ViewConstants.FLEET_MAINT_PURCHASE_ORDER);			
		super.saveNextPageInitStateValues(nextPageValues);
		super.forwardToURL(ViewConstants.DRIVER_ADD);
    }
    
    public void serviceHistory(String maintCategory){
    	Map<String, Object> restoreStateValues = new HashMap<String, Object>();
		restoreStateValues.put(ViewConstants.VIEW_PARAM_MRQ_ID, getMaintenanceRequest().getMrqId());
		restoreStateValues.put(ViewConstants.VIEW_PARAM_FMS_ID, getMaintenanceRequest().getFleetMaster().getFmsId());
		restoreStateValues.put(ViewConstants.VIEW_PARAM_MODE, getViewMode());
		super.saveRestoreStateValues(restoreStateValues);
		
		Map<String, Object> nextPageValues = new HashMap<String, Object>();				
		nextPageValues.put(ViewConstants.VIEW_PARAM_FMS_ID, getMaintenanceRequest().getFleetMaster().getFmsId());
		nextPageValues.put(ViewConstants.VIEW_PARAM_VIN, getMaintenanceRequest().getFleetMaster().getVin());
		nextPageValues.put(ViewConstants.VIEW_PARAM_MAINT_CATEGORY, maintCategory);
		super.saveNextPageInitStateValues(nextPageValues);
		super.forwardToURL(ViewConstants.FLEET_MAINT_SERVICE_HISTORY);
    }     
    
	/**
	 * Pattern for retrieving stateful data passed from calling view.
	 * This view will expect the unit no to be passed with an optional
	 * maintenance request id. If the maintenance request id is present
	 * the view goes into an view/edit PO mode.
	 */
	protected void loadNewPage() {	
		Map<String, Object> map = super.thisPage.getInputValues();
		
		thisPage.setPageUrl(ViewConstants.FLEET_MAINT_PURCHASE_ORDER);
		setViewMode((String)map.get(ViewConstants.VIEW_PARAM_MODE));			
		
		if(map.containsKey(ViewConstants.VIEW_PARAM_MRQ_ID)){
			thisPage.setPageDisplayName(ViewConstants.DISPLAY_NAME_FLEET_MAINT_VIEW_PO);				
			setMrqId((Long)map.get(ViewConstants.VIEW_PARAM_MRQ_ID));
		} else {
			thisPage.setPageDisplayName(ViewConstants.DISPLAY_NAME_FLEET_MAINT_CREATE_PO);				
			setFmsId((Long)map.get(ViewConstants.VIEW_PARAM_FMS_ID));								
		}
							
	} 
	
	/**
	 * Pattern for restoring the view's data
	 */
	protected void restoreOldPage() {
		Long mrqId;
		Long fmsId;

		mrqId = (Long) thisPage.getRestoreStateValues().get(ViewConstants.VIEW_PARAM_MRQ_ID);
		fmsId = (Long) thisPage.getRestoreStateValues().get(ViewConstants.VIEW_PARAM_FMS_ID);
		
		if (!MALUtilities.isEmpty(mrqId)) {
			setMrqId(mrqId);
		} else {
			setFmsId(fmsId);
		}
		setViewMode((String) thisPage.getRestoreStateValues().get(ViewConstants.VIEW_PARAM_MODE));
	}
	
	public boolean isAddMode(){
		boolean isAddMode = false;
		if(!MALUtilities.isEmpty(getViewMode()) && getViewMode().equals(ViewConstants.VIEW_MODE_ADD))
			isAddMode = true;
		return isAddMode;		
	}
	
	public boolean isEditMode(){
		boolean isEditMode = false;
		if(!MALUtilities.isEmpty(getViewMode()) && getViewMode().equals(ViewConstants.VIEW_MODE_EDIT))
			isEditMode = true;
		return isEditMode;		
	}
	
	public boolean isReadMode(){
		return isReadModeCheck();
	}	
	public boolean isReadModeCheck(){
		boolean isReadMode = false;
		if(!MALUtilities.isEmpty(getViewMode()) && getViewMode().equals(ViewConstants.VIEW_MODE_READ))
			isReadMode = true;
		return isReadMode;		
	}	
		
	public String getFormatedServiceProviderAddress(){
		String formattedAddress = "";
		
		if(!MALUtilities.isEmpty(getMaintenanceRequest())){
			if(!MALUtilities.isEmpty(getMaintenanceRequest().getServiceProvider())){
				if(!MALUtilities.isEmpty(getMaintenanceRequest().getServiceProvider().getServiceProviderAddresses())
						&& this.getMaintenanceRequest().getServiceProvider().getServiceProviderAddresses().size() > 0){
					formattedAddress = maintRequestService.getServiceProviderFormattedAddress(this.getMaintenanceRequest().getServiceProvider().getServiceProviderAddresses().get(0));
				}
			}
		}
		
		return formattedAddress;
	}
	
	/**
	 * Sums up the PO's line items total cost
	 * @return PO Sub total that MAFS will pay to the Service Provider
	 */	
	public BigDecimal getSubTotal(){	
		return maintRequestService.calculatePOSubTotal(getMaintenanceRequest());
	}
	
	/**
	 * Sums up the PO's rechargeable line total and adds the mark up
	 * @return Recharge sub total that will be billed to clients (sales tax not included)
	 */
	public BigDecimal getRechargeSubTotal(){
		BigDecimal amount = maintenanceRequestTaskDAO.getActualCustAmount(getMaintenanceRequest().getMrqId());
		if (amount !=null){
			return amount;
		}
		else{
		return maintRequestService.sumRechargeTotal(getMaintenanceRequest());
	}
	}
	
	/**
	 * Iterates the PO line items total the mark up for the PO.
	 * @return Total mark up based on the summation of the line item mark up
	 */
	public BigDecimal getMarkUpTotal(){
		return maintRequestService.sumMarkUpTotal(getMaintenanceRequest());
	}
	
	/**
	 * Calculates the mark up total based on the business defined percentage and cap
	 * @return BigDecimal Calculated mark up dollar amount
	 */
	public BigDecimal calculateMarkUp(){
		return maintRequestService.calculateNonNetworkRechargeMarkup(getMaintenanceRequest());
	}
	
	public void updateServiceProviderInfo(Long inputId){
		ServiceProvider provider = serviceProviderService.getServiceProvider(inputId);
		if(!MALUtilities.isEmpty(provider)){
			this.updateServiceProviderRelatedFields(provider);
		}
	}
	
	public void decodeProviderByNameOrCode(String nameOrCode,String serviceType){
		maintenanceRequest.setServiceProviderContactInfo(""); //Set contact info to empty every time Service Provider Changes
		if(MALUtilities.isEmpty(nameOrCode)){
			maintenanceRequest.getServiceProvider().setServiceProviderName(null);
			maintenanceRequest.getServiceProvider().setPayeeAccount(null);
			selectedProviderId = null;
			setLeasePlanServiceProvider(false);
			return ;
		}
		// use "paged" service to make a broad decode (i.e. decoding "%a"); come back really quickly
		PageRequest page = new PageRequest(0,2);
		//List<ServiceProvider> providers = serviceProviderService.getServiceProviderByNameOrCode(nameOrCode,page);	
		List<ServiceProvider> providers = serviceProviderService.getServiceProviderByNameOrCodeAndServiceType(nameOrCode,serviceType,page);	

		if(providers.size() == 1){
			this.updateServiceProviderRelatedFields(providers.get(0));
			refreshServiceProviderDiscounts(getMaintenanceRequest().getActualStartDate());
		}else if (providers.size() == 0) {
			super.addErrorMessage(SERVICE_PROVIDER_UI_ID, "decode.noMatchFound.msg", "Service Provider " + nameOrCode);
			maintenanceRequest.getServiceProvider().setServiceProviderName(null);
			maintenanceRequest.getServiceProvider().setPayeeAccount(null);
			selectedProviderId = null;
			setLeasePlanServiceProvider(false);
		}else {
			super.addErrorMessage(SERVICE_PROVIDER_UI_ID, "decode.multipleMatchesFound.msg", "Service Provider " + nameOrCode);
			maintenanceRequest.getServiceProvider().setServiceProviderName(null);
			maintenanceRequest.getServiceProvider().setPayeeAccount(null);
			selectedProviderId = null;
			setLeasePlanServiceProvider(false);
		}
	}
	

	private void updateServiceProviderRelatedFields(ServiceProvider provider){
		getMaintenanceRequest().setServiceProvider(provider);
		getMaintenanceRequest().setPayeeAccount(provider.getPayeeAccount());
		
		//Default the Lease Plan Mark Up indicator to 'N'o. and remove respective mark up line(s).
		//This must be done prior to changing the PO mark up amount so that it does not include
		//the mark up lines that may have been created.
		getMaintenanceRequest().setServiceProviderMarkupInd(MalConstants.FLAG_N);
		manageMarkUpLines();
		manageFeeLines();
		
		selectedProviderId = provider.getServiceProviderId();
		
		ServiceProvider parent = serviceProviderService.getParentProvider(this.selectedProviderId);
		if(!MALUtilities.isEmpty(parent)){
			setSelectedProviderParentId(parent.getServiceProviderId());
		}else{
			setSelectedProviderParentId(null);
		}
		
		if(!MALUtilities.isEmpty(getMaintenanceRequest())){
			for(MaintenanceRequestTask task : getMaintenanceRequest().getMaintenanceRequestTasks()){
				if(!MALUtilities.isEmpty(task.getMaintenanceCode())){
					this.updateServiceProviderCodeFields(task.getMaintenanceCode(),task);
				}
			}
		}
		updateLeasePlanServiceProvider(provider);
		
	}
		
	//TODO Need to rename this method as it no longer applies to just lease plan
	private void updateLeasePlanServiceProvider(ServiceProvider provider){
		setLeasePlanServiceProvider(false);
		if(!MALUtilities.isEmpty(leasePlanPayeeList) && leasePlanPayeeList.size() > 0){
			for(AccountVO account : leasePlanPayeeList){
				if(account.getAccountCode().equals(provider.getPayeeAccount().getExternalAccountPK().getAccountCode()) 
						&&  account.getCorpId() == provider.getPayeeAccount().getExternalAccountPK().getCId() 
						&& account.getAccountType().equals(provider.getPayeeAccount().getExternalAccountPK().getAccountType()) ){
					setLeasePlanServiceProvider(true);
					break;
				}
			}
		}
		if(!MALUtilities.isEmpty(provider.getServiceProviderNumber())
				&& provider.getServiceProviderNumber().equals(SPL_SERVICE_PROVIDER_CODE) 
				&& provider.getPayeeAccount().getExternalAccountPK().getAccountCode().equals(SPL_PAYEE_NO)){
			setLeasePlanServiceProvider(true);
		}
	}
	
    public void onDetailClick(ActionEvent event){
		if(MALUtilities.isEmpty(getMaintenanceRequest().getMaintenanceRequestTasks())){

	    	setSelectedTask(getMaintenanceRequest().getMaintenanceRequestTasks().get(selectedTaskIndex));
		}    	
    }
    	
    /**
     * Handles the row selection event.  
     * @param event
     */
    public void onRowSelect(SelectEvent event){ 
		if(MALUtilities.isEmpty(getMaintenanceRequest().getMaintenanceRequestTasks())){
	    	setSelectedTask(getMaintenanceRequest().getMaintenanceRequestTasks().get(selectedTaskIndex));    		    	
		}      	
    }
    
    private void finishPartialMaintCodeDecode(){
    	// if it is "Add" then the selectTask can be empty; if the select task and maintenance code is empty then this doesn't matter
    	// this is the only error condition detected while debugging (the only one I could detect in debug)
    	if(!MALUtilities.isEmpty(selectedTask) && !MALUtilities.isEmpty(selectedTask.getMaintenanceCode())){
    		// if the maintenance code and maintCodeQuery value matches
    		if(selectedTask.getMaintenanceCode().getCode().equalsIgnoreCase(maintCodeQuery)){
        		// set the description for the maintenance code if it is "empty"
    			if(MALUtilities.isEmptyString(selectedTask.getMaintenanceCodeDesc())){
    		    	partialCodeDecodeOccured = true;
    				setMaintCodeSet(true);
    				this.maintCodeSelected = true;
    				   				
    				logger.debug("finishing partial code decode " + maintCodeQuery);
    				
    				updateSelectedMaintenanceCode(selectedTask.getMaintenanceCode());
    			}
        		
    			
    		}
    	}
    }
    
	/**
	 * This method will be called when user clicks add icon or add button on pop up to add a task item.
	 */
	public void addItemTask(boolean isAutoSave){
		Long lineNumber = 0l;
		
		// if the item is not finished decoding then detect and lookup partial decode values.
		finishPartialMaintCodeDecode();
		
		if(!validatePurhaseOrder() || (!validateItemTaskData())){
			RequestContext context = RequestContext.getCurrentInstance();
			context.addCallbackParam("failure", true);
			return;
		}
		
		if (ViewConstants.VIEW_MODE_ADD.equals(getViewMode()) || getCopyOfSavedMaintenanceRequest().getMrqId() ==0 || vehicleMaintenanceService.isMaintenancePOModified(getCopyOfSavedMaintenanceRequest(), getMaintenanceRequest())) {
			if (isTaskItemModified()) {
				getSelectedTask().setAuthorizePerson(getLoggedInUser().getEmployeeNo());
				getSelectedTask().setAuthorizeDate(Calendar.getInstance().getTime());
			}
			if(isAutoSave){
				autoSave();
				hasSaveActionOccured = true;
			}
		}
		
		lineNumber = MALUtilities.isEmpty(getMaintenanceRequest().getMaintenanceRequestTasks()) ? 1l : getMaintenanceRequest().getMaintenanceRequestTasks().size() + 1l;
		
		selectedTask = new MaintenanceRequestTask();
		selectedTask.setMaintenanceRequest(maintenanceRequest);
		//Optimization: Only call logic to retrieve recharge flag and recharge code during Task Dialog Opening.
		//     Do not call logic to retrieve recharge flag and recharge code when already in the task dialog.
		if(defaultRechargeFlag == null || defaultRechargeFlag.isEmpty()){
			defaultRechargeFlag = getMaintRechargeFlag();
		}
		selectedTask.setRechargeFlag(defaultRechargeFlag);
		
		if(defaultRechargeCode == null){
			defaultRechargeCode = getMaintRechargeCode();
		}
		
		if (defaultRechargeCode != null){
			selectedTask.setRechargeCode(defaultRechargeCode.getCode());
		}
		if(vehicleNotOnContract){
			selectedTask.setRechargeFlag(DataConstants.DEFAULT_N);
			selectedTask.setRechargeCode(null);
		}
		selectedTask.setOutstanding(DataConstants.DEFAULT_N);
		selectedTask.setWasOutstanding(DataConstants.DEFAULT_N);
		selectedTask.setTaskQty(new BigDecimal(1));
		selectedTask.setUnitCost(new BigDecimal("0.00"));
		selectedTask.setTotalCost(new BigDecimal("0.00"));
		selectedTask.setLineNumber(maintRequestService.nextMRTLineNumber(getMaintenanceRequest()));
		selectedTask.setIndex(lineNumber.intValue());
		selectedTask.setCostAvoidanceIndicator(isCostAvoidanceDisplay());
		User user = getLoggedInUser();
		selectedTask.setAuthorizePerson(user.getEmployeeNo());
		getSelectedTask().setAuthorizeDate(Calendar.getInstance().getTime());
		populateSelectedTaskDiscountValues();
    	if(MALUtilities.isEmptyString(selectedTask.getMaintenanceRepairReasonCode())){
    		if(getMaintenanceRequest().getMaintReqType().equals("RISK_MGMT")){
    			selectedTask.setMaintenanceRepairReasonCode("RISK_MGMT");
    		}
    		else{
    			selectedTask.setMaintenanceRepairReasonCode(MAINT_REQUEST_REASON_CODE);
    		}
		}		
		
		if(!MALUtilities.isEmpty(getMaintenanceRequest().getMaintenanceRequestTasks())){
			getMaintenanceRequest().getMaintenanceRequestTasks().add(selectedTask);
		}
		selectedTaskIndex = getMaintenanceRequest().getMaintenanceRequestTasks().indexOf(selectedTask);
		
		setMaintenanceCategoryCode("");
    	// make sure the variable that allow maintenance code object lookup are reset.
    	resetMaintCodePrivateVars();
    	// when adding a new line we could be "setting" the maint code for the prior record.. so we don't want to make it = false
    	// let other code set or unset this.
    	//setMaintCodeSet(false);
    	
		showHistoricalCategoryIconIndicator = false;
		
	}
	
	/**
	 * Edit a task item by clicking on the pencil icon on Maintenance PO screen such that change is transient
	 */
	public void editItemTask(){			

		if (ViewConstants.VIEW_MODE_EDIT.equals(getViewMode()) && vehicleMaintenanceService.isMaintenancePOModified(getCopyOfSavedMaintenanceRequest(), getMaintenanceRequest())) {
			if(!validatePurhaseOrder() || (!validateItemTaskData())){
				RequestContext context = RequestContext.getCurrentInstance();
				context.addCallbackParam("failure", true);
				return;
			}
			
			if (isTaskItemModified()) {
				getSelectedTask().setAuthorizePerson(getLoggedInUser().getEmployeeNo());
				getSelectedTask().setAuthorizeDate(Calendar.getInstance().getTime());
			}
			
			autoSave();
			hasSaveActionOccured = true;
			setSelectedTask(getMaintenanceRequest().getMaintenanceRequestTasks().get(selectedTaskIndex));			
		}		
		// lookup field for service code
		this.selectedTask.setServiceProviderCodeLookup(this.getServiceProviderCode(getMaintenanceRequest().getMaintenanceRequestTasks().get(selectedTaskIndex).getServiceProviderMaintenanceCode()));
    	// lookup field for maintenance category
    	setMaintenanceCategoryCode(new String(this.selectedTask.getMaintCatCode()));
    	
    	if(!MALUtilities.isEmpty(this.selectedTask.getRechargeCode())){
			maintRechargeCodeOfSelectedTask	= this.selectedTask.getRechargeCode();
		}else{
			maintRechargeCodeOfSelectedTask	= null;
		}
    	
    	if(MALUtilities.isEmptyString(selectedTask.getMaintenanceRepairReasonCode())){
    		if(getMaintenanceRequest().getMaintReqType().equals("RISK_MGMT")){
    			selectedTask.setMaintenanceRepairReasonCode("RISK_MGMT");
    		}
    		else{
    			selectedTask.setMaintenanceRepairReasonCode(MAINT_REQUEST_REASON_CODE);
    		}
		}	   	
    	
    	populateSelectedTaskDiscountValues();
    	
    	// make sure the variable that allow maintenance code object lookup are reset.
    	resetMaintCodePrivateVars();
    	// when adding a new line we could be "setting" the maint code for the prior record.. so we don't want to make it = false
    	setMaintCodeSet(true);
	}
	   	
    /**
     * Delete a task item by clicking on the 'x' delete icon on Maintenance PO screen such that change is transient
     */
	public void deleteItemTask(){
		this.performDeleteOfItemTask();
	}
	
    /**
     * Delete a task item by clicking delete button on Maintenance PO Task Item Pop up  such that change is transient
     */
	public void deleteItemTaskFromPopup(){
		this.performDeleteOfItemTask();
	}
	
	private void performDeleteOfItemTask(){	
		getMaintenanceRequest().getMaintenanceRequestTasks().remove(selectedTaskIndex);
		resetIndex(getMaintenanceRequest().getMaintenanceRequestTasks());
		manageMarkUpLines();
		manageFeeLines();
		// FM-1387: AutoSaving before setting the selected task.  By setting the selected task after the autoSave, 
		//    if the mrt_id (or anything else) is updated during the autoSave, the selectedTask will also have this mrt_id.
		autoSave(); 
		hasSaveActionOccured = true;
		
		if(selectedTaskIndex > 0) {			
		    setSelectedTask(getMaintenanceRequest().getMaintenanceRequestTasks().get(selectedTaskIndex - 1));
		    setMaintenanceCategoryCode(selectedTask.getMaintCatCode());	
		} else {
			if(getMaintenanceRequest().getMaintenanceRequestTasks().size() > 0) {
				setSelectedTask(getMaintenanceRequest().getMaintenanceRequestTasks().get(selectedTaskIndex));
				setMaintenanceCategoryCode(selectedTask.getMaintCatCode());	
			}
		}
	}

	/**
	 * This method will be be called when pop up is closed by done/ESC/X that will reset modified maintenance request tasks to null and select the previous task item if exist or 
	 * select the next task item either.
	 */
	public void cancelItemTaskPopup(){
		
		if ((getMaintenanceRequest().getMaintenanceRequestTasks().size() > 0 &&  getMaintenanceRequest().getMaintenanceRequestTasks().size() > selectedTaskIndex && getMaintenanceRequest().getMaintenanceRequestTasks().get(selectedTaskIndex).getMrtId() == null)
				|| (getMaintenanceRequest().getMaintenanceRequestTasks().size() > 0 && getMaintenanceRequest().getMaintenanceRequestTasks().size() > selectedTaskIndex && getMaintenanceRequest().getMaintenanceRequestTasks().get(selectedTaskIndex).getMrtId() == 0 )){
			getMaintenanceRequest().getMaintenanceRequestTasks().remove(selectedTaskIndex);
			setSelectedTaskIndex(getSelectedTaskIndex() - 1);
		}
		
		if(getMaintenanceRequest().getMaintenanceRequestTasks().size() > 0 && getMaintenanceRequest().getMaintenanceRequestTasks().size() <= selectedTaskIndex){
			setSelectedTaskIndex(getMaintenanceRequest().getMaintenanceRequestTasks().size() - 1);
		}
		
		if (isTaskItemModified()) {
			for(MaintenanceRequestTask originalTask : getCopyOfSavedMaintenanceRequest().getMaintenanceRequestTasks()){
				if(!MALUtilities.isEmpty(getSelectedTask().getMrtId())){
					if(getSelectedTask().getMrtId().longValue() == originalTask.getMrtId().longValue()){
						// revert the current task item to the "original" (from the DB)
						getMaintenanceRequest().getMaintenanceRequestTasks().set(getSelectedTaskIndex(), maintRequestService.getTaskItemById(getSelectedTask().getMrtId().longValue()));
						break;
					}
				}
			}
			
			//check for PO change prior to calculate markup because if after then lease plan markup always updates PO therefore always setting flag to true
			if(vehicleMaintenanceService.isMaintenancePOModified(getCopyOfSavedMaintenanceRequest(), getMaintenanceRequest()) &&  validatePurhaseOrder()){
			       hasSaveActionOccured = true;
			}				
			
			//Following the same code as the 'Done' button.
			manageMarkUpLines();
			manageFeeLines();
			
			
			
			autoSave();
//			hasSaveActionOccured = true;
			adjustTaskDataTableHeight();
		}
		
		if(hasSaveActionOccured){
			hasSaveActionOccured = false;
			try {
				checkForExceedAuthLimit();
			} catch (MalBusinessException ex) {
				logger.error(ex);
				validationSuccess = false;
				handleException("generic.error",new String[]{ex.getMessage()}, ex, null);
				RequestContext context = RequestContext.getCurrentInstance();
				context.addCallbackParam("failure", true);
			} 
		}
		
		resetIndex(getMaintenanceRequest().getMaintenanceRequestTasks());
		
		if(getMaintenanceRequest().getMaintenanceRequestTasks().size() > 0){
			setSelectedTask(getMaintenanceRequest().getMaintenanceRequestTasks().get(getSelectedTaskIndex()));
			} else {
			setSelectedTask(null);
		}
		setMaintenanceCategoryCode(null);
    	// make sure the variable that allow maintenance code object lookup are reset.
    	resetMaintCodePrivateVars();
    	setMaintCodeSet(false);
    	
	}
	
	public void populateSelectedTaskDiscountValues() {
		selectedTaskDiscountLocked = false;
		ServiceProvider serviceProviderForDiscountCheck = null;
		if(getMaintenanceRequest().getServiceProvider() != null) {
			if(getMaintenanceRequest().getServiceProvider().getParentServiceProvider() != null) {
				serviceProviderForDiscountCheck = getMaintenanceRequest().getServiceProvider().getParentServiceProvider();
			} else {
				serviceProviderForDiscountCheck = getMaintenanceRequest().getServiceProvider();
			}
		}
		
		if(selectedTask != null){
			if(serviceProviderForDiscountCheck != null && selectedTask.getMaintenanceCode() != null) {
				if(maintRequestService.isTaskDiscountedBySupplier(serviceProviderForDiscountCheck, selectedTask)) {
					selectedTask.setDiscountFlag("Y");
				} else {
					selectedTask.setDiscountFlag(DataConstants.DEFAULT_N);
					selectedTaskDiscountLocked = true;
				}
			} else {
				selectedTask.setDiscountFlag(null);
			}
		}
	}
	
	/**
     * Reset the index of records when records are deleted in transient mode
     */
	private void resetIndex(List<MaintenanceRequestTask> taskList){
		
		if(!MALUtilities.isEmpty(taskList) && taskList.size() > 0){
			Iterator<MaintenanceRequestTask> iterator = taskList.iterator();
			int index = 0;
			while(iterator.hasNext()){
				MaintenanceRequestTask task = iterator.next();
				task.setIndex(++index);
			}
		}		
	}
	
	/**
	 * This method executes when user clicks done button on item task pop up. 
	 */
	public void done(){	
		if (!ViewConstants.VIEW_MODE_READ.equals(getViewMode()) && hasPermission()){
			// if the item is not finished decoding then detect and lookup partial decode values.
			finishPartialMaintCodeDecode();
			
			if(validateItemTaskData()){
				if(isTaskItemListModified()){
					getSelectedTask().setAuthorizePerson(getLoggedInUser().getEmployeeNo());
					getSelectedTask().setAuthorizeDate(Calendar.getInstance().getTime());
					manageMarkUpLines();
					manageFeeLines();
					autoSave();
					hasSaveActionOccured = true;	
					adjustTaskDataTableHeight();
				}
				if(hasSaveActionOccured){
					hasSaveActionOccured = false;
					try {
						checkForExceedAuthLimit();
					} catch (MalBusinessException ex) {
						logger.error(ex);
						validationSuccess = false;
						handleException("generic.error",new String[]{ex.getMessage()}, ex, null);
						RequestContext context = RequestContext.getCurrentInstance();
						context.addCallbackParam("failure", true);
					} 
				}
			}else{
				RequestContext context = RequestContext.getCurrentInstance();
				context.addCallbackParam("failure", true);
			}
		}		
	}
	
	/**
	 * This method determines whether Selected Task Item List modified. 
	 * @return boolean
	 */
	public boolean isTaskItemModified(){
		for(MaintenanceRequestTask originalTask : getCopyOfSavedMaintenanceRequest().getMaintenanceRequestTasks()){
			
			if (MALUtilities.isEmpty(getSelectedTask())) {
				return false;
			}
			
			if(MALUtilities.isEmpty(getSelectedTask().getMrtId())){
				return true;
			}else{
				if(getSelectedTask().getMrtId().longValue() == originalTask.getMrtId().longValue()){
					if((getSelectedTask().equals(originalTask) == false) 
							|| (getSelectedTask().isCostAvoidanceIndicator() != originalTask.isCostAvoidanceIndicator())) {
						return true;
					}
					break;
				}
			}
		}
		if(getMaintenanceRequest().getMaintenanceRequestTasks().size() != getCopyOfSavedMaintenanceRequest().getMaintenanceRequestTasks().size()) return true;
		
		return false;
	}
	
	/**
	 * This method determines whether Task Items List modified. 
	 * @return boolean
	 */
	public boolean isTaskItemListModified(){
			return vehicleMaintenanceService.isTaskItemListModified(getCopyOfSavedMaintenanceRequest().getMaintenanceRequestTasks(), getMaintenanceRequest().getMaintenanceRequestTasks());
	}
	
		
	public void validateServiceProvider(FacesContext context, UIComponent inputComponent, Object value){
		if(MALUtilities.isEmpty(value) && MALUtilities.isEmpty(getMaintenanceRequest().getServiceProvider())){
			super.addErrorMessage(SERVICE_PROVIDER_UI_ID, "required.field", "Service Provider");
			this.maintenanceRequest.setServiceProvider(new ServiceProvider());
			RequestContext reqContext = RequestContext.getCurrentInstance();
			reqContext.addCallbackParam("failure", true);
			return;
		}
	}
	
	/**
	 * Validates the Purchase order level fields only.
	 * @return boolean True indicates that the validation passed. 
	 */
	public boolean validatePurhaseOrder(){
		MaintenanceRequest po = getMaintenanceRequest();
		boolean isValid = true;
		
		if(MALUtilities.isEmptyString(po.getJobNo())){
			super.addErrorMessage("jobNo", "required.field", "Job No");
		    isValid = false;
		}
		
		isValid = validateOdo();
		
		if(MALUtilities.isEmpty(po.getActualStartDate())){
			super.addErrorMessage(START_DATE_UI_ID, "required.field", "Actual Start Date");
			isValid = false;
		}
		if(MALUtilities.isEmpty(po.getPlannedEndDate())){
			super.addErrorMessage(END_DATE_UI_ID, "required.field", "End Date");
			isValid = false;
		}
		if((!MALUtilities.isEmpty(po.getActualStartDate()) && !MALUtilities.isEmpty(po.getPlannedEndDate()))){
			if(MALUtilities.compateDates(po.getPlannedEndDate(), po.getActualStartDate()) < 0){
				super.addErrorMessage(END_DATE_UI_ID, "greater.date.message", new String[]{"End Date","actual start date"});
				isValid = false;
			}
		}
		if(MALUtilities.isEmpty(po.getServiceProvider().getServiceProviderNumber())){
			super.addErrorMessage(SERVICE_PROVIDER_UI_ID, "required.field", "Service Provider");
			maintenanceRequest.getServiceProvider().setServiceProviderName(null);
			maintenanceRequest.getServiceProvider().setPayeeAccount(null);
			selectedProviderId = null;
			setLeasePlanServiceProvider(false);
			isValid = false;
		}
		else if(MALUtilities.isEmpty(po.getServiceProvider().getServiceProviderId())){
			super.addErrorMessage(SERVICE_PROVIDER_UI_ID, "required.field", "Service Provider");
			isValid = false;
		}else{
			if( po.getServiceProvider().getServiceProviderId() == null || po.getServiceProvider().getServiceProviderId() <= 0L ){
				decodeProviderByNameOrCode(po.getServiceProvider().getServiceProviderNumber(),this.getServiceTypeCode());
				if(super.getFacesContext().getMessageList().size() > 0){
					isValid = false;	
				}
			}else{
				if(MALUtilities.isEmpty(po.getServiceProvider().getServiceProviderName())){
					decodeProviderByNameOrCode(po.getServiceProvider().getServiceProviderNumber(),this.getServiceTypeCode());
					if(super.getFacesContext().getMessageList().size() > 0){
						isValid = false;	
					}
				}
			}
		}
		if(vehicleNotOnContract){
			if(isLeasePlanServiceProvider && MALUtilities.convertYNToBoolean(po.getServiceProviderMarkupInd())){
				super.addErrorMessage("vehicle_not_on_contract_markup_msg");
				isValid = false;
			}
		}
		
		return isValid;
	}
	
	private boolean validateOdo(){
		MaintenanceRequest po = getMaintenanceRequest();
		boolean isValid = true;
		if(MALUtilities.isEmpty(po.getCurrentOdo())){
			super.addErrorMessage(ODO_UI_ID, "required.field", "Odo");
			isValid = false;
		}else if(po.getCurrentOdo() == 0){
			super.addErrorMessage(ODO_UI_ID, "generic.field..greater", new String[]{"Odometer reading","0 (Zero)"});
			isValid = false;
		}
		return isValid;
	}
	
	/**
	 * This method will validate task item's required fields.
	 * @return boolean
	 */
	public boolean validateItemTaskData(){
		boolean isValid = true;
		
		if(!MALUtilities.isEmpty(selectedTask)){
						
			if(MALUtilities.isEmpty(selectedTask.getServiceProviderMaintenanceCode())){
	    		if(MALUtilities.isEmptyString(serviceCodeQuery) == false){
	    			isValid = false;
	    			super.addErrorMessageSummary(SERVICE_CODE_UI_ID, "decode.noMatchFound.msg", "Service Code " + serviceCodeQuery);
	    		}    			
			}else{
    			// if the MAFS maintenance code's match (their categories should match as well) then we can use this to check to see if the 
				// serviceCode has been changed after decode (right before clicking on the done button).
				if((!MALUtilities.isEmpty(selectedTask.getMaintenanceCode())) && !selectedTask.getServiceProviderMaintenanceCode().getMaintenanceCode().getCode().equals(selectedTask.getMaintenanceCode().getCode())){
	    			isValid = false;
	    			super.addErrorMessageSummary(SERVICE_CODE_UI_ID, "decode.noMatchFound.msg", "Service Code " + serviceCodeQuery);
				}
			}
			
			if(MALUtilities.isEmpty(selectedTask.getMaintenanceCode())){
				isValid = false;
				if(MALUtilities.isEmptyString(maintCodeQuery)){
					super.addErrorMessageSummary(MAINTENANCE_CODE_UI_ID, "required.field", "Maintenance Code");
				}else{
	    			super.addErrorMessageSummary(MAINTENANCE_CODE_UI_ID, "decode.noMatchFound.msg", "Maintenance Code " + maintCodeQuery);
				}
			}
			
			if(MALUtilities.isEmptyString(selectedTask.getMaintenanceCodeDesc())){
				isValid = false;
				super.addErrorMessageSummary(MAINTENANCE_CODE_UI_ID, "required.field", "Maintenance Code Description");
			}
			if(MALUtilities.isEmptyString(selectedTask.getAuthorizePerson())){
				isValid = false;
				super.addErrorMessageSummary(AUTHORIZE_PERSON_UI_ID, "required.field", "Authorize Person");
			}
			if(MALUtilities.isEmpty(selectedTask.getTaskQty())){
				isValid = false;
				super.addErrorMessageSummary(QTY_UI_ID, "required.field", "Qty");
			}
			if(MALUtilities.isEmpty(selectedTask.getUnitCost())){
				isValid = false;
				super.addErrorMessageSummary(UNIT_PRICE_UI_ID, "required.field", "Unit Price");
			}
			if (selectedTask.getRechargeFlag().equals("Y") && MALUtilities.isEmptyString(selectedTask.getRechargeCode())){
				isValid = false;
				super.addErrorMessageSummary(RECHARGE_CODE, "required.field", "Recharge Reason");
			}
			if (selectedTask.getRechargeFlag().equals("N") && !MALUtilities.isEmptyString(selectedTask.getRechargeCode())){
				isValid = false;
				super.addErrorMessageSummary(RECHARGE_CODE, "custom.message", "Recharge Reason not required");
			}
			
			// Goodwill PO tasks validations
			if(getMaintenanceRequest().isGoodwillIndicator() && selectedTask.isCostAvoidanceIndicator()){
				if(MALUtilities.isEmpty(selectedTask.getCostAvoidanceCode()) ){
					isValid = false;
					super.addErrorMessageSummary(COST_AVOIDANCE_REASON, "custom.message", "Cost Avoidance Reason is required");					
				}
				if(MALUtilities.isEmpty(selectedTask.getCostAvoidanceAmount())){
					isValid = false;
					super.addErrorMessageSummary(COST_AVOIDANCE_AMOUNT, "custom.message", "Cost Avoidance Amount is required");		
				}
				if(MALUtilities.isEmpty(selectedTask.getGoodwillReason())){
					isValid = false;
					super.addErrorMessageSummary(GOODWILL_REASON, "custom.message", "Goodwill Reason is required");							
				}
				if(MALUtilities.isEmpty(selectedTask.getGoodwillCost()) || selectedTask.getGoodwillCost().compareTo(new BigDecimal(0)) < 1){
					isValid = false;
					super.addErrorMessageSummary(GOODWILL_AMOUNT, "custom.message", "Goodwill Amount is required");						
				}
				if(MALUtilities.isEmpty(selectedTask.getGoodwillPercent()) || selectedTask.getGoodwillPercent().compareTo(new BigDecimal(0)) < 1){
					isValid = false;
					super.addErrorMessageSummary(GOODWILL_PERCENT, "custom.message", "Goodwill Percent is required");											
				}
			}
			
			//Cost avoidance validations
			if(!getMaintenanceRequest().isGoodwillIndicator() && selectedTask.isCostAvoidanceIndicator()){
				if(MALUtilities.isEmpty(selectedTask.getCostAvoidanceCode())){
					isValid = false;
					super.addErrorMessageSummary(COST_AVOIDANCE_REASON, "custom.message", "Cost Avoidance Reason is required");					
				}
				if(MALUtilities.isEmpty(selectedTask.getCostAvoidanceAmount())){
					isValid = false;
					super.addErrorMessageSummary(COST_AVOIDANCE_AMOUNT, "custom.message", "Cost Avoidance Amount is required");		
				}				
			}
			//Maintenance Category validations
			if(MALUtilities.isEmpty(selectedTask.getMaintCatCode())){
				isValid = false;
				super.addErrorMessageSummary("custom.message", "Maintenance Category is required");
			}
			
			if(isVehicleNotOnContract() && MALUtilities.convertYNToBoolean(selectedTask.getRechargeFlag())){
				isValid = false;
				super.addErrorMessageSummary("vehicle_not_on_contract_recharge_msg");
			}
		}
		
		return isValid;
	}
	
	
	public void validateAuthorizePerson(FacesContext context, UIComponent inputComponent, Object value){
		if(MALUtilities.isEmpty(value)){
			super.addErrorMessageSummary(AUTHORIZE_PERSON_UI_ID, "required.field", "Authorize Person");
			RequestContext reqContext = RequestContext.getCurrentInstance();
			reqContext.addCallbackParam("failure", true);
			return;
		}
	}
	
	public void validateUnitPrice(FacesContext context, UIComponent inputComponent, Object value){
		if(MALUtilities.isEmpty(value)){
			super.addErrorMessageSummary(UNIT_PRICE_UI_ID, "required.field", "Unit Price");
			RequestContext reqContext = RequestContext.getCurrentInstance();
			reqContext.addCallbackParam("failure", true);
			return;
		}
	}
	
	public void validateMaintenanceCode(FacesContext context, UIComponent inputComponent, Object value){
		if(MALUtilities.isEmpty(value)){
			super.addErrorMessageSummary(MAINTENANCE_CODE_UI_ID,"required.field", "Maintenance Code");
			RequestContext reqContext = RequestContext.getCurrentInstance();
			reqContext.addCallbackParam("failure", true);
			return;
		}
	}
	
    public void previous(){
    	// if the item is not finished decoding then detect and lookup partial decode values.
    	finishPartialMaintCodeDecode();
    			
		if(validateItemTaskData() == false){
			RequestContext context = RequestContext.getCurrentInstance();
			context.addCallbackParam("failure", true);
			return;
		}
		
		if(isTaskItemModified()){
			getSelectedTask().setAuthorizePerson(getLoggedInUser().getEmployeeNo());
			getSelectedTask().setAuthorizeDate(Calendar.getInstance().getTime());
			manageMarkUpLines();
			manageFeeLines();
			autoSave();
			hasSaveActionOccured = true;
		}
		
		if (getSelectedTaskIndex() != 0) {
			setSelectedTaskIndex(getSelectedTaskIndex() - 1);
		}
		setSelectedTask(getMaintenanceRequest().getMaintenanceRequestTasks().get(getSelectedTaskIndex()));
		setMaintenanceCategoryCode(selectedTask.getMaintCatCode());	
    	// make sure the variable that allow maintenance code object lookup are reset.
    	resetMaintCodePrivateVars();
    	setMaintCodeSet(true);
    }
		
    public void next(){
    	// if the item is not finished decoding then detect and lookup partial decode values.
    	finishPartialMaintCodeDecode();
    			
		if(validateItemTaskData() == false){
			RequestContext context = RequestContext.getCurrentInstance();
			context.addCallbackParam("failure", true);
			return;
		}
		
		if(isTaskItemModified()){
			getSelectedTask().setAuthorizePerson(getLoggedInUser().getEmployeeNo());
			getSelectedTask().setAuthorizeDate(Calendar.getInstance().getTime());
			manageMarkUpLines();
			manageFeeLines();
			autoSave();
			hasSaveActionOccured = true;
		}
		
		if (getSelectedTaskIndex() != getMaintenanceRequest().getMaintenanceRequestTasks().size() - 1) {
			setSelectedTaskIndex(getSelectedTaskIndex() + 1);
		}
		setSelectedTask(getMaintenanceRequest().getMaintenanceRequestTasks().get(getSelectedTaskIndex()));
		setMaintenanceCategoryCode(selectedTask.getMaintCatCode());		
    	// make sure the variable that allow maintenance code object lookup are reset.
    	resetMaintCodePrivateVars();
    	setMaintCodeSet(true);
    }
    
    public boolean hasPrevious(){
    	boolean hasPrevious = false;
    	int index = (getSelectedTask() != null)? getSelectedTask().getIndex() - 1 : 0;
    	if(!MALUtilities.isEmpty(getMaintenanceRequest().getMaintenanceRequestTasks()) && getMaintenanceRequest().getMaintenanceRequestTasks().size() > 1 &&  index > 0){
    		hasPrevious = true;
    	}
    	setPrevDisabled(!hasPrevious);
    	return hasPrevious;
    }
    
    public boolean hasNext(){
    	boolean hasNext = false;
    	if(getMaintenanceRequest().getMaintenanceRequestTasks() != null){
	    	int size = getMaintenanceRequest().getMaintenanceRequestTasks().size();
	    	int index = (getSelectedTask() != null)? getSelectedTask().getIndex() - 1 : 0;
	    	if(!MALUtilities.isEmpty(getMaintenanceRequest().getMaintenanceRequestTasks()) && getMaintenanceRequest().getMaintenanceRequestTasks().size() > 1 && index < size - 1){
	    		hasNext = true;
	    	}
    	}
    	setNextDisabled(!hasNext);
    	return hasNext;    	
    }
        
    
    private void resetMaintCodePrivateVars(){
    	maintCodeQuery = null;
    	serviceCodeQuery = null;
    	maintCodeSelected = false;
    }
    
    public List<MaintenanceCode> maintenanceCodeAutoComplete(String query) {   
    	logger.debug("INFO: maint entering autocomplete with query " + query);
    	List<MaintenanceCode> codes;
    	
    	// if an empty query the assume "dropdown" return all
    	if(MALUtilities.isEmptyString(query)){
    		if(MALUtilities.isNotEmptyString(maintCodeQuery)){
	    		logger.debug("INFO: maint autocomplete on dropdown calling service method with  " + maintCodeQuery);
	    		codes = maintCodeService.getMaintenanceCodesByNameOrCode(maintCodeQuery);
    		}else{
    			// else return an empty list (do nothing)
    			codes = new ArrayList<MaintenanceCode>();
    		}
    	} else {
    			maintCodeQuery = query; 
	    		logger.debug("INFO: maint autocomplete calling service method with  " + query);
	    		codes = maintCodeService.getMaintenanceCodesByNameOrCode(query);

    	}
    	
		   	
    	 logger.debug("INFO: autocomplete return X codes:  " + codes.size());
    	 return codes;   
    }
    
    
    public void maintenanceCodeSelect(SelectEvent event){
    	MaintenanceCode code = (MaintenanceCode) event.getObject();
    	logger.debug("INFO: CODE SELECTED : " + code.getCode());
    	updateSelectedMaintenanceCode(code);
    	populateSelectedTaskDiscountValues();
    	maintCodeSelected = true;
    	setMaintCodeSet(true);
    	RequestContext.getCurrentInstance().update(MAINTENANCE_CODE_UI_ID);
   }
    
   private void updateSelectedMaintenanceCode(MaintenanceCode code){
		if(!MALUtilities.isEmpty(code)){
		   this.selectedTask.setMaintenanceCode(code); //
		   this.selectedTask.setMaintenanceCodeDesc(code.getDescription());
		   this.selectedTask.setWorkToBeDone(code.getDescription());
		   this.selectedTask.setMaintCatCode(code.getMaintCatCode());
		   this.selectedTask.setServiceProviderMaintenanceCode(null); //
		   setMaintenanceCategoryCode(new String(code.getMaintCatCode()));
			
		   this.updateServiceProviderCodeFields(code,this.selectedTask);
		}

   }
    
   
    public List<ServiceProviderMaintenanceCode> serviceCodeAutoComplete(String query) {
    	logger.debug("INFO: entering service autocomplete with query " + query);
    	List<ServiceProviderMaintenanceCode> serivceCodes = new ArrayList<ServiceProviderMaintenanceCode>();
    	// if an empty query the assume "dropdown" return all
    	if(MALUtilities.isEmptyString(query)){
    		if(MALUtilities.isNotEmptyString(serviceCodeQuery)){
	    		logger.debug("INFO: service autocomplete on dropdown calling service method with  " + serviceCodeQuery);
	    		serivceCodes = vehicleMaintenanceService.getServiceCodesByCodeOrDesc(serviceCodeQuery, null, null, selectedProviderId);
    		}else{
    			// else return an empty list (do nothing)
    			serivceCodes = new ArrayList<ServiceProviderMaintenanceCode>();
    		}
    	} else {
    			serviceCodeQuery = query; 
	    		logger.debug("INFO: service autocomplete calling service method with  " + query);
	    		serivceCodes = vehicleMaintenanceService.getServiceCodesByCodeOrDesc(query, null, null, selectedProviderId);
    	}
    	
		   	
    	 logger.debug("INFO: service autocomplete return X codes:  " + serivceCodes.size());
    	 return serivceCodes;   
    }
	
	
    public void serviceCodeSelect(SelectEvent event){
    	ServiceProviderMaintenanceCode serviceCode = (ServiceProviderMaintenanceCode) event.getObject();
    	updateSelectedServiceCode(serviceCode);
    	maintCodeSelected = true;
    	setMaintCodeSet(true);
    	RequestContext.getCurrentInstance().update(SERVICE_CODE_UI_ID);
    }
    
    
    private void updateSelectedServiceCode(ServiceProviderMaintenanceCode serviceCode){
    	if(!MALUtilities.isEmpty(serviceCode)){
    		MaintenanceCode maintenanceCode = serviceCode.getMaintenanceCode();
			
    		this.selectedTask.setMaintenanceCode(maintenanceCode);
        	this.selectedTask.setMaintenanceCodeDesc(maintenanceCode.getDescription());
        	this.selectedTask.setWorkToBeDone(maintenanceCode.getDescription());
        	this.selectedTask.setMaintCatCode(maintenanceCode.getMaintCatCode());
        	this.selectedTask.setServiceProviderMaintenanceCode(serviceCode);
        	this.selectedTask.setServiceProviderCodeLookup(serviceCode.getCode());
        	
        	setMaintenanceCategoryCode(maintenanceCode.getMaintCatCode());
    	}
    }

    public void decodeMaintenanceCode(){
    	final String MAINT_CODE_PATTERN = "\\d{3}-\\d{3}";
    	
    	logger.debug("INFO: Maint CODE DECODE FIRED " + maintCodeQuery);
    	
    	logger.debug("Is Maint CODE Set ? " + maintCodeSet);
    	
    	if(partialCodeDecodeOccured){
    		partialCodeDecodeOccured = false;
    		return;
    	}else{
    		partialCodeDecodeOccured = false;
    	}
    	
    	if(!this.isMaintCodeSet()){
    		
    		// if the select item event did not select anything (notFound = true) 
    	    // but there is a code that does not mean we are done.. we could have outpaced
    	    // the autocomplete but still have a valid code entered
    		if(MALUtilities.isNotEmptyString(maintCodeQuery)){
    	    	if(!maintCodeSelected){
    	    		logger.debug("INFO: Maint CODE DECODE - NOT FOUND " + maintCodeQuery);
    	    		
    	    		MaintenanceCode code = maintCodeService.getExactMaintenanceCodeByNameOrCode(maintCodeQuery);
    	    	    if( !MALUtilities.isEmpty(code)){
    	    	    	updateSelectedMaintenanceCode(code);
    	    	    	maintCodeSelected = true;
    	    	    	setMaintCodeSet(true);
    	    	    	RequestContext.getCurrentInstance().update("maintenanceCode");
    	    	    }else{ // we still didn't find anything that means there is no code or the code they entered does not yield an exact match; set the error message
    	    	    		// and return it to the user.
                		this.selectedTask.setMaintenanceCodeDesc(null);
                		this.selectedTask.setMaintCatCode(null);
                		setMaintenanceCategoryCode("");
                		this.setMaintenanceCategoryCode(null);
                		//this.selectedTask.setServiceProviderMaintenanceCode(null);

    	        		if(MALUtilities.isEmptyString(maintCodeQuery) == false){
    	        			super.addErrorMessageSummary(MAINTENANCE_CODE_UI_ID, "decode.noMatchFound.msg", "Maintenance Code " + maintCodeQuery);
    	        		}
    	        		maintCodeSelected = false;
    	        		setMaintCodeSet(false);
    	        		RequestContext context = RequestContext.getCurrentInstance();
    	    			context.addCallbackParam("failure", true);
    	    	    }
    	        	
    	    	}else{
    	    		logger.debug("INFO: Maint CODE DECODE - FOUND " + maintCodeQuery);
    	    		if(!MALUtilities.isEmpty(this.selectedTask.getMaintenanceCode())){
    	    			logger.debug("INFO: Maint CODE DECODE - FOUND " + maintCodeQuery + " already selected " + this.selectedTask.getMaintenanceCode().getCode());
    	    			if(!this.selectedTask.getMaintenanceCode().getCode().equalsIgnoreCase(maintCodeQuery)){
    	    				logger.debug("INFO: Maint CODE DECODE - FOUND " + maintCodeQuery + " mis-matched");
    	    				MaintenanceCode code = maintCodeService.getExactMaintenanceCodeByNameOrCode(maintCodeQuery);
    	    				if(!MALUtilities.isEmpty(code)){
    		    				updateSelectedMaintenanceCode(code);
    		    				maintCodeSelected = true;
    		    				setMaintCodeSet(true);
    		    				RequestContext.getCurrentInstance().update("maintenanceCode");
    	    				}else{
    	    					// if the code is not the length of a valid code (a complete code) 
    	    					// or if the maint code query does not match the the pattern for a valid MAFS code
    	    					// then use the original one
    	    					if((!maintCodeQuery.matches(MAINT_CODE_PATTERN)) || (maintCodeQuery.length() != this.selectedTask.getMaintenanceCode().getCode().length())){
        	    					updateSelectedMaintenanceCode(this.selectedTask.getMaintenanceCode());
        	    					maintCodeSelected = true;
        	    					setMaintCodeSet(true);
    	    					} else{ // otherwise raise an error because the code did not match anything in the DB.
        	    					this.selectedTask.setMaintenanceCodeDesc(null);
            	            		this.selectedTask.setMaintCatCode(null);
            	            		setMaintenanceCategoryCode("");
            	            		this.selectedTask.setServiceProviderMaintenanceCode(null);
            	            		
            	            		if(MALUtilities.isEmptyString(maintCodeQuery) == false){
            	            			super.addErrorMessageSummary(MAINTENANCE_CODE_UI_ID, "decode.noMatchFound.msg", "Maintenance Code " + maintCodeQuery);
            	            		}
            	            		maintCodeSelected = false;
            	            		setMaintCodeSet(false);
            	            		RequestContext context = RequestContext.getCurrentInstance();
            	        			context.addCallbackParam("failure", true);
    	    					}
    	    					RequestContext.getCurrentInstance().update("maintenanceCode");
        	            		

    	    				}
    	    			}else{
    	    				logger.debug("INFO: Maint CODE DECODE - FOUND " + maintCodeQuery + " matched");
    		    			updateSelectedMaintenanceCode(this.selectedTask.getMaintenanceCode());
    		    			maintCodeSelected = true;
    		    			setMaintCodeSet(true);
    		    	    	RequestContext.getCurrentInstance().update("maintenanceCode");
    	    			}
    	    		}else{
    	    			logger.debug("INFO: Maint CODE DECODE - FOUND " + maintCodeQuery + " not selected ");
    	    			MaintenanceCode code = maintCodeService.getExactMaintenanceCodeByNameOrCode(maintCodeQuery);
    	    			if(!MALUtilities.isEmpty(code)){
    	    				updateSelectedMaintenanceCode(code);
    	    				maintCodeSelected = true;
    	    				setMaintCodeSet(true);
    	    				RequestContext.getCurrentInstance().update("maintenanceCode");
    	    			}else{
    	            		this.selectedTask.setMaintenanceCodeDesc(null);
    	            		this.selectedTask.setMaintCatCode(null);
    	            		setMaintenanceCategoryCode("");
    	            		this.selectedTask.setServiceProviderMaintenanceCode(null);
    	            		
    	            		if(MALUtilities.isEmptyString(maintCodeQuery) == false){
    	            			super.addErrorMessageSummary(MAINTENANCE_CODE_UI_ID, "decode.noMatchFound.msg", "Maintenance Code " + maintCodeQuery);
    	            		}
    	            		maintCodeSelected = false;
    	            		setMaintCodeSet(false);
    	            		RequestContext context = RequestContext.getCurrentInstance();
    	        			context.addCallbackParam("failure", true);
    	    			}
    	    	    	
    	    		}
    	    	}
    		}else{
        		this.selectedTask.setMaintenanceCodeDesc(null);
        		this.selectedTask.setMaintCatCode(null);
        		setMaintenanceCategoryCode("");
        		this.selectedTask.setServiceProviderMaintenanceCode(null);
        		setMaintCodeSet(false);
	    		//TODO: is this redundant?
	    		maintCodeSelected = false;
    		}	
    		
    		
    	}
    	
    	logger.debug("Is Maint CODE Updated ? " + maintCodeSet);
    	
    }
    
	public void decodeServiceCode(){
    	logger.debug("INFO: Service CODE DECODE FIRED " + serviceCodeQuery);
    	
    	logger.debug("Is Maint CODE Set (service) ? " + maintCodeSet);
    	
		List<Long> selectedProviderIds = new ArrayList<Long>();
		selectedProviderIds.add(selectedProviderId);
		selectedProviderIds.add(selectedProviderParentId);
		
		if(!this.isMaintCodeSet()){

	    	// if the select item event did not select anything (mainCodeSelected = false) 
		    // but there is a code that does not mean we are done.. we could have outpaced
		    // the autocomplete but still have a valid code entered
			if(MALUtilities.isNotEmptyString(serviceCodeQuery)){
		    	if(!maintCodeSelected){
		    		logger.debug("INFO: Service CODE DECODE - NOT FOUND " + serviceCodeQuery);
		    		
		    		List<ServiceProviderMaintenanceCode> serviceProviderMaintCodes = maintCodeService.getServiceProviderMaintenanceCode(serviceCodeQuery, selectedProviderIds, true);
		    		
		   
		    	    if(!MALUtilities.isEmpty(serviceProviderMaintCodes) && serviceProviderMaintCodes.size() == 1){
		    	    	updateSelectedServiceCode(serviceProviderMaintCodes.get(0));
		    	    	maintCodeSelected = true;
		    	    	setMaintCodeSet(true);
		    	    	RequestContext.getCurrentInstance().update("serviceCode");
		    	    }else if(!MALUtilities.isEmpty(serviceProviderMaintCodes) && serviceProviderMaintCodes.size() > 1){
		    	    	// FM-1272 raise an error because there are multiples
		        		this.selectedTask.setMaintenanceCodeDesc(null);
		        		this.selectedTask.setMaintCatCode(null);
		        		setMaintenanceCategoryCode("");
		        		this.selectedTask.setMaintenanceCode(null);
		    	    	
		        		if(MALUtilities.isEmptyString(serviceCodeQuery) == false){
		        			super.addErrorMessageSummary(SERVICE_CODE_UI_ID, "decode.multipleMatchesFound.msg", "Service Code " + serviceCodeQuery);
		        		}
		        		maintCodeSelected = false;
		        		setMaintCodeSet(false);
		        		RequestContext context = RequestContext.getCurrentInstance();
		    			context.addCallbackParam("failure", true);
		    	    }else{ // we still didn't find anything that means there is no code or the code they entered does not yield an exact match; set the error message
		    	    		// and return it to the user.

		        		this.selectedTask.setMaintenanceCodeDesc(null);
		        		this.selectedTask.setMaintCatCode(null);
		        		setMaintenanceCategoryCode("");
		        		this.selectedTask.setMaintenanceCode(null);
		    	    	
		        		if(MALUtilities.isEmptyString(serviceCodeQuery) == false){
		        			super.addErrorMessageSummary(SERVICE_CODE_UI_ID, "decode.noMatchFound.msg", "Service Code " + serviceCodeQuery);
		        		}
		        		maintCodeSelected = false;
		        		setMaintCodeSet(false);
		        		RequestContext context = RequestContext.getCurrentInstance();
		    			context.addCallbackParam("failure", true);
		    	    }
		        	
		    	}else{
		    		logger.debug("INFO: Service CODE DECODE - FOUND " + serviceCodeQuery);
		    		if(!MALUtilities.isEmpty(this.selectedTask.getServiceProviderMaintenanceCode())){
		    			logger.debug("INFO: Service CODE DECODE - FOUND " + serviceCodeQuery + " already selected " + this.selectedTask.getServiceProviderMaintenanceCode().getCode());
		    			if(!this.selectedTask.getServiceProviderMaintenanceCode().getCode().equalsIgnoreCase(serviceCodeQuery)){
		    				logger.debug("INFO: Service CODE DECODE - FOUND " + serviceCodeQuery + " mis-matched");
		    				List<ServiceProviderMaintenanceCode> serviceProviderMaintCodes = maintCodeService.getServiceProviderMaintenanceCode(serviceCodeQuery, selectedProviderIds, true);
		    	    		
		    				if(!MALUtilities.isEmpty(serviceProviderMaintCodes) && serviceProviderMaintCodes.size() == 1){
		    					updateSelectedServiceCode(serviceProviderMaintCodes.get(0));
		    					maintCodeSelected = true;
		    					setMaintCodeSet(true);
			    				RequestContext.getCurrentInstance().update("serviceCode");
				    	    }else if(!MALUtilities.isEmpty(serviceProviderMaintCodes) && serviceProviderMaintCodes.size() > 1){
				    	    	// TODO: FM-1272 pick the first one!!
				    	    	updateSelectedServiceCode(serviceProviderMaintCodes.get(0));
				    	    	maintCodeSelected = true;
				    	    	setMaintCodeSet(true);
				    	    	RequestContext.getCurrentInstance().update("serviceCode");
		    				}else{
		    					updateSelectedServiceCode(this.selectedTask.getServiceProviderMaintenanceCode());
		    					maintCodeSelected = true;
		    					setMaintCodeSet(true);
				    	    	RequestContext.getCurrentInstance().update("serviceCode");
		    				}
		    			}else{
		    				logger.debug("INFO: Service CODE DECODE - FOUND " + serviceCodeQuery + " matched");
		    				updateSelectedServiceCode(this.selectedTask.getServiceProviderMaintenanceCode());
		    				maintCodeSelected = true;
		    				setMaintCodeSet(true);
			    	    	RequestContext.getCurrentInstance().update("serviceCode");
		    			}
		    		}else{
		    			logger.debug("INFO: Service CODE DECODE - FOUND " + serviceCodeQuery + " not selected ");
		    			List<ServiceProviderMaintenanceCode> serviceProviderMaintCodes = maintCodeService.getServiceProviderMaintenanceCode(serviceCodeQuery, selectedProviderIds, true);
	    	    		
		    			if(!MALUtilities.isEmpty(serviceProviderMaintCodes) && serviceProviderMaintCodes.size() == 1){
		    				updateSelectedServiceCode(serviceProviderMaintCodes.get(0));
		    				maintCodeSelected = true;
		    				setMaintCodeSet(true);
		    				RequestContext.getCurrentInstance().update("serviceCode");
			    	    }else if(!MALUtilities.isEmpty(serviceProviderMaintCodes) && serviceProviderMaintCodes.size() > 1){
			    	    	// FM-1272 raise an error because there are multiples
			        		this.selectedTask.setMaintenanceCodeDesc(null);
			        		this.selectedTask.setMaintCatCode(null);
			        		setMaintenanceCategoryCode("");
			        		this.selectedTask.setMaintenanceCode(null);
			    	    	
			        		if(MALUtilities.isEmptyString(serviceCodeQuery) == false){
			        			super.addErrorMessageSummary(SERVICE_CODE_UI_ID, "decode.multipleMatchesFound.msg", "Service Code " + serviceCodeQuery);
			        		}
			        		maintCodeSelected = false;
			        		setMaintCodeSet(false);
			        		serviceCodeQuery = null;
			        		RequestContext context = RequestContext.getCurrentInstance();
			    			context.addCallbackParam("failure", true);
		    			}else{	    				
		    	    		this.selectedTask.setMaintenanceCodeDesc(null);
		    	    		this.selectedTask.setMaintCatCode(null);
		    	    		setMaintenanceCategoryCode("");
		    	    		this.selectedTask.setMaintenanceCode(null);
		    				
		            		if(MALUtilities.isEmptyString(serviceCodeQuery) == false){
		            			super.addErrorMessageSummary(SERVICE_CODE_UI_ID, "decode.noMatchFound.msg", "Service Code " + serviceCodeQuery);
		            		}
		            		maintCodeSelected = false;
		            		setMaintCodeSet(false);
		            		serviceCodeQuery = null;
		            		RequestContext context = RequestContext.getCurrentInstance();
		        			context.addCallbackParam("failure", true);
		    			}
		    	    	
		    		}
		    	}
			}else{
	    		this.selectedTask.setMaintenanceCodeDesc(null);
	    		this.selectedTask.setMaintCatCode(null);
	    		setMaintenanceCategoryCode("");
	    		this.selectedTask.setMaintenanceCode(null);
	    		setMaintCodeSet(false);
	    		maintCodeSelected = false;
			}
					
		}
		
		logger.debug("Is Maint CODE Updated (service) ? " + maintCodeSet);
    	
	}
    
    
    
        
	// there are 2 service methods to find maintenance codes; one that uses the service code as the starting point
	// (getServiceProviderMaintenanceCode) and one that uses the Mafs code as the starting point (getServiceProviderMaintenanceByMafsCode)
	// the decode feature should be using the former; where as the select of the maintenance code as well as changing the service provider should
	// use the other service method (getServiceProviderMaintenanceByMafsCode).
    private void updateServiceProviderCodeFields(MaintenanceCode code, MaintenanceRequestTask task){
		if(!MALUtilities.isEmpty(code) && !MALUtilities.isEmpty(selectedProviderId) && selectedProviderId != 0){
			List<Long> selectedProviderIds = new ArrayList<Long>();
			selectedProviderIds.add(selectedProviderId);
			selectedProviderIds.add(selectedProviderParentId);
			List<ServiceProviderMaintenanceCode> serviceProviderMaintCodes = maintCodeService.getServiceProviderMaintenanceByMafsCode(code.getCode(), selectedProviderIds, true);
			
			if(serviceProviderMaintCodes.size() == 0){
				task.setServiceProviderMaintenanceCode(null);
		    	task.setServiceProviderCodeLookup(null); //TODO: Make serviceProviderCodeLookup a bean variable rather than a transient item on the entity
		    	setServiceCodeQuery(null);
			}else if(serviceProviderMaintCodes.size() == 1){
				task.setServiceProviderMaintenanceCode(serviceProviderMaintCodes.get(0));
				task.setServiceProviderCodeLookup(new String(serviceProviderMaintCodes.get(0).getCode()));
			}else if(serviceProviderMaintCodes.size() > 1){
				task.setServiceProviderMaintenanceCode(null);
				task.setServiceProviderCodeLookup(null);
				setServiceCodeQuery(null);
			}
		}else{
			task.setServiceProviderMaintenanceCode(null);
			task.setServiceProviderCodeLookup(null);
			setServiceCodeQuery(null);
		}
    }
    
    /**
     * This method will be called to remove network and non-network mark up lines and create new if any changes are made in any line item. 
     * It also calls service to resets the previously created mark up to null.
     */
    public void manageMarkUpLines(){	
    	if(!getMaintenanceRequest().isGoodwillIndicator()){
	    	if(getMaintenanceRequest().getMaintenanceRequestTasks().size() > 0 ){
	    		setMaintenanceRequest(maintRequestService.revertLinesMarkUp(getMaintenanceRequest()));
	    		if(!MALUtilities.isEmpty(getMaintenanceRequest()) && !getMaintenanceRequest().getMaintReqStatus().equals(MalConstants.STATUS_COMPLETE_PO)){
	    			setMarkUpAmount(new BigDecimal(0.0));
	    		}
	    		setMaintenanceRequest(maintRequestService.createMarkupLine(getMaintenanceRequest(), getLoggedInUser().getEmployeeNo(), this.waiveOutOfNetworkSurcharge));
				resetIndex(getMaintenanceRequest().getMaintenanceRequestTasks());
	//			setSelectedTaskIndex(getMaintenanceRequest().getMaintenanceRequestTasks().size() - 1); //Commented against bug FM-1359
	    	}   
    	}
    }
    
    /**
     * This method is called to apply a maintenance fee if the vehicle is not on MAINT_MGMT nor RISK_MGMT programs;
     * Only one fee line can be attributed to one maintenance request;  If a fee exists, do not apply any markup
     */
    public void manageFeeLines(){
    	if(!getMaintenanceRequest().isGoodwillIndicator()){
	    	if(getMaintenanceRequest().getMaintenanceRequestTasks().size() > 0){
	    		if(isVehicleNotOnMaintOrRiskMgmtOrNTLACCT()){
	        		if(isRentalRechargeable()){
		    			maintenanceFeeLine = maintRequestService.createVehicleRentalFeeLine(getMaintenanceRequest(), getLoggedInUser().getEmployeeNo());
		    			if(!MALUtilities.isEmpty(maintenanceFeeLine) && maintenanceFeeLine.getMaintenanceCode() != null){
		    				getMaintenanceRequest().getMaintenanceRequestTasks().add(maintenanceFeeLine);
		    			
			    			//if mafs markup and rental lines apply, only show rental.  if lease plan markup and rental lines apply, show both
			    			String markUpCategoryCode = willowConfigService.getConfigValue("MAINT_MARKUP_LN_CAT_CODE");
			    			String mafsMarkUpCode = willowConfigService.getConfigValue("MAINT_NON_NETWORK_CODE");
			    			for(Iterator<MaintenanceRequestTask> iter  = getMaintenanceRequest().getMaintenanceRequestTasks().iterator(); iter.hasNext();){
			    				MaintenanceRequestTask task = (MaintenanceRequestTask)iter.next();
			    				if(!MALUtilities.isEmpty(task.getMaintCatCode()) && task.getMaintCatCode().equals(markUpCategoryCode) &&
		        						task.getMaintenanceCode().getCode().equals(mafsMarkUpCode)){								
			    					iter.remove();
			    				}
			    			}
		    			}
	        		}else{
	        			for(Iterator<MaintenanceRequestTask> iter  = getMaintenanceRequest().getMaintenanceRequestTasks().iterator(); iter.hasNext();){
	        				MaintenanceRequestTask task = (MaintenanceRequestTask)iter.next();
	        				if(!MALUtilities.isEmpty(task.getMaintCatCode()) && ((task.getMaintCatCode().equals(defaultRentalFeeCategoryCode) &&
	        						task.getMaintenanceCode().getCode().equals(defaultVehicleRentalFeeCode)))){				
	        					iter.remove();
	        				}
	        			}
	        		}
	    		}
	    		//HPS-2946 to remove ERS fee line if the parent line it removed
        		if(isMiscellaneousService()){
        			maintenanceFeeLine = maintRequestService.createERSFeeLine(getMaintenanceRequest(), getLoggedInUser().getEmployeeNo());
 	    			if(!MALUtilities.isEmpty(maintenanceFeeLine) && maintenanceFeeLine.getMaintenanceCode() != null){
	    				getMaintenanceRequest().getMaintenanceRequestTasks().add(maintenanceFeeLine);
	    			
		    			String markUpCategoryCode = willowConfigService.getConfigValue("MAINT_MARKUP_LN_CAT_CODE");
		    			String mafsMarkUpCode = willowConfigService.getConfigValue("MAINT_NON_NETWORK_CODE");
		    			for(Iterator<MaintenanceRequestTask> iter  = getMaintenanceRequest().getMaintenanceRequestTasks().iterator(); iter.hasNext();){
		    				MaintenanceRequestTask task = (MaintenanceRequestTask)iter.next();
		    				if(!MALUtilities.isEmpty(task.getMaintCatCode()) && task.getMaintCatCode().equals(markUpCategoryCode) &&
	        						task.getMaintenanceCode().getCode().equals(mafsMarkUpCode)){								
		    					iter.remove();
		    				}
		    			}
	    			}
        		}else{
        			for(Iterator<MaintenanceRequestTask> iter  = getMaintenanceRequest().getMaintenanceRequestTasks().iterator(); iter.hasNext();){
        				MaintenanceRequestTask task = (MaintenanceRequestTask)iter.next();
        				if(!MALUtilities.isEmpty(task.getMaintCatCode()) && ((task.getMaintCatCode().equals(defaultERSCategoryCode) &&
        						task.getMaintenanceCode().getCode().equals(defaultVehicleERSFeeCode)))){				
        					iter.remove();
        				}
        			}
        		}
        		resetIndex(getMaintenanceRequest().getMaintenanceRequestTasks());
	    	}
	    }
    }
    
    public boolean isVehicleNotOnMaintOrRiskMgmtOrNTLACCT(){    	
    	vehicleNotOnMaintOrRiskMgmt = (!vehicleMaintenanceService.isMaintenanceProgramsForFee(vehicleInfo))? "T" : "F";
    	return (vehicleNotOnMaintOrRiskMgmt.equals("T"))? true : false;
    }
    
   
    public boolean isRentalRechargeable(){
    	boolean isRechargeable = false;
    	List<MaintenanceRequestTask> tasks = getMaintenanceRequest().getMaintenanceRequestTasks(); 
    	if(!MALUtilities.isEmpty(tasks) && !tasks.isEmpty()){
    		for(MaintenanceRequestTask task : tasks){
    			if(task.getMaintCatCode().equals(defaultRentalCategoryCode)){ 
    				isRechargeable = true;
    				break;
    			}
    		}
    	}
    	return isRechargeable;
    }
    
    // HPS-2946 to check if the ERS fee line is rechargeable 
    public boolean isMiscellaneousService(){
    	boolean isMiscellaneousService = false;
    	List<MaintenanceRequestTask> tasks = getMaintenanceRequest().getMaintenanceRequestTasks(); 
    	
    	if(!MALUtilities.isEmpty(tasks) && !tasks.isEmpty()){
    		for(MaintenanceRequestTask task : tasks){
    			if(task.getMaintCatCode().equals(defaultMiscCategoryCode) && !task.getMaintenanceCode().getCode().equals(defaultVehicleERSFeeCode)){ 
    				if(vehicleMaintenanceService.isBudgetForFee(vehicleInfo)){
    					isMiscellaneousService = true;
    					break;
    				}else if(task.getRechargeFlag().equalsIgnoreCase("Y")){
    					isMiscellaneousService = true;
    					break;
    				}
    			}
    		}
    	}    	
    	return isMiscellaneousService;
    }    // HPS-2946 End
    
    private void getGLDistData(){
    	try{
    		if(MaintenanceRequestStatusEnum.MAINT_REQUEST_STATUS_COMPLETE.getCode().equalsIgnoreCase(getMaintenanceRequest().getMaintReqStatus())){
	    		setPurchaseOrderDoc(maintRequestService.getReleasedPurchaseOrderForMaintReq(getMaintenanceRequest()));
	    	}
    	}catch(Exception ex) {
			super.addErrorMessage("generic.error", ex.getMessage());
    	}
    }
    
    
	/**
	 * 
	 * @param serviceProviderMaintenanceCode
	 * @return code value
	 */
	public String getServiceProviderCode(ServiceProviderMaintenanceCode serviceProviderMaintenanceCode){
		String code = "";
		
		if(!MALUtilities.isEmpty(serviceProviderMaintenanceCode)){
			code = serviceProviderMaintenanceCode.getCode();
		}

		return code;
	}
	
	/**
	 * 
	 * @param code
	 * @return
	 */
	public String getMaintenanceRechargeCodeDescription(String code){
		String description = null;
	    MaintenanceRechargeCode rechargeCode = vehicleMaintenanceService.convertMaintenanceRechargeCode(code);
	    
	    if(!MALUtilities.isEmpty(rechargeCode)){
	    	description = rechargeCode.getDescription();
	    }
		return description;
	}
	
	public void setDefaultEndDate(){
		Date actualStartDate = maintenanceRequest.getActualStartDate();
		if(!MALUtilities.isEmpty(actualStartDate)){
			Date plannedEndDate = MALUtilities.addDaysToDateTime(actualStartDate, 1);
			maintenanceRequest.setPlannedEndDate(plannedEndDate);
		}
	}
	
	public int getTaskCount(){
		int count = 0;
		
		if(!MALUtilities.isEmpty(getMaintenanceRequest().getMaintenanceRequestTasks())){
			count = getMaintenanceRequest().getMaintenanceRequestTasks().size();
		}
		
		return count;
	}
	
	/**
	 * IF maintenance request is not completed nor closed, check to see if work has previously been done in the specified category
	 * @param maintCatCode
	 * @return
	 */
	public boolean showHistoricalCategoryIcon(String maintCatCode){
		if(!maintenanceRequest.getMaintReqStatus().equalsIgnoreCase(MaintenanceRequestStatusEnum.MAINT_REQUEST_STATUS_COMPLETE.getCode()) && 
				!maintenanceRequest.getMaintReqStatus().equalsIgnoreCase(MaintenanceRequestStatusEnum.MAINT_REQUEST_STATUS_CLOSED_NO_PO.getCode())){
			for(String category : distinctHistoricalCategories){
				if(maintCatCode.equalsIgnoreCase(category)){
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * Sums up the goodwill amount on all the maintenance request's tasks
	 * @return BigDecimal Goodwill will total for the maintenance request
	 */
	public BigDecimal goodwillTotal(){		
		return maintRequestService.sumGoodwillTotal(getMaintenanceRequest());
	}
	
	/**
	 * Sums up the cost avoidance amount on all the maintenance request's tasks
	 * @return BigDecimal Cost avoidance will total for the maintenance request
	 */	
	public BigDecimal costAvoidanceTotal(){		
		return maintRequestService.sumCostAvoidanceTotal(getMaintenanceRequest());
	}
	
	/**
	 * Scans the maintenance request tasks for a active cost avoidance tasks. 
	 * @return boolean True when an active cost avoidance task has been found
	 */
	public boolean hasCostAvoidance(){
		boolean isCostAvoidance = false;
		    for(MaintenanceRequestTask task : getMaintenanceRequest().getMaintenanceRequestTasks()){
		    	if(task.isCostAvoidanceIndicator()){
		    		isCostAvoidance = true;
		    		break;
		    	}
		    }
		return isCostAvoidance;
	}
	
	public String deletePO(){
		if(getMaintenanceRequest() != null){
			boolean status = true;
			if(getMaintenanceRequest().getMrqId() != null && getMaintenanceRequest().getMrqId() > 0L){
				status = maintRequestService.deletePO(getMaintenanceRequest());			
			}
			if(status == true){
				super.addSuccessMessage("process.success", "Delete PO (" + getMaintenanceRequest().getJobNo() + ")");
			}else{
				super.addErrorMessage("complete_po_delete_error");
			}
		}
		return cancelPage();
	}
	
	public void setCreditTabInfo(MaintenanceRequest mrq){
		if(mrq.getMaintReqStatus().equalsIgnoreCase(MaintenanceRequestStatusEnum.MAINT_REQUEST_STATUS_COMPLETE.getCode())){
			creditList = maintRequestService.getMaintenanceCreditAP(mrq);
			setCreditTaskList(maintRequestService.getMaintenanceCreditAPLines(mrq));
			if(!creditList.isEmpty()){
				this.setSelectedCredit(creditList.get(0));
			}
		}
	}

	public List<MaintenanceInvoiceCreditVO> getIndividualTaskItems(){
		List<MaintenanceInvoiceCreditVO> creditLines = new ArrayList<MaintenanceInvoiceCreditVO>();
		if(creditTaskList != null){
			for(MaintenanceInvoiceCreditVO line: creditTaskList){
				if(line.getMrtId().equals(currentCreditLineId)){
					creditLines.add(line);
				}
			}
		}
		return creditLines;
	}
	
	public void onRowToggleSetCreditLine(ToggleEvent event){
		currentCreditLineId = ((MaintenanceRequestTask)event.getData()).getMrtId();
	}
	
	public boolean rowTogglerRendered(Long mrtId){
		if(creditTaskList != null){
			for(MaintenanceInvoiceCreditVO credit : creditTaskList){
				if(credit.getMrtId().equals(mrtId)){
					return true;
				}
			}
		}
		return false;
	}
	
	public boolean isCreditExists(){
		List<Doc> creditList = new ArrayList<Doc>();
		if (!ViewConstants.VIEW_MODE_ADD.equals(getViewMode()) && getMaintenanceRequest().getMrqId() != null) {
			creditList = maintRequestService.getMaintenanceCreditAP(getMaintenanceRequest());
		}
		if(creditList != null && !creditList.isEmpty()){
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * When log entries exist for the unit, this method will determine what styleClass to use
	 * to make the button noticeable.
	 * @return
	 */
	public String notesLogStyleClass(){
		String styleClass = "";
		
		if(!MALUtilities.isEmpty(getMaintenanceRequest())){
			if(logBookService.hasLogs(getMaintenanceRequest().getFleetMaster(), new ArrayList<LogBookTypeEnum>(Arrays.asList(LogBookTypeEnum.TYPE_FM_UNIT_NOTES)))){
				styleClass = ViewConstants.BUTTON_INDICATOR_STYLE_CLASS;
			}
		}
		
		return styleClass;
	}	
	
	public List<String> initializeListOfRechargeOnlyMaintCodes(){
		List<String> rechargeOnlyMaintCodes = new ArrayList<String>();
		rechargeOnlyMaintCodes.add(willowConfigService.getConfigValue("MAINT_NON_NETWORK_CODE"));
		rechargeOnlyMaintCodes.add(willowConfigService.getConfigValue("MAINT_RENTAL_FEE_CODE"));
		rechargeOnlyMaintCodes.add(willowConfigService.getConfigValue("MAINT_ERS_FEE_CODE"));//HPS-2946 to get ERS fee code
		return rechargeOnlyMaintCodes;
	}
	
	public boolean isMaintCodeRechargeOnly(String currentMaintCode){
/*		for(String maintCode : rechargeOnlyMaintCodes){
			if(currentMaintCode.equals(maintCode)){
				return true;
			}
		}*/
		if(rechargeOnlyMaintCodes.contains(currentMaintCode)){
			return true;
		}
		return false;
	}
	
	public void findMaintenanceRequestEligibility(String authOrComplete){
		boolean eligible = true;
		for(MaintenanceRequestTask mrt : getMaintenanceRequest().getMaintenanceRequestTasks()){
			if(mrt.getMaintCatCode().equalsIgnoreCase("OIL_CHANGE") && mrt.getMaintenanceCode().getOilChange().equalsIgnoreCase("Y")&& MALUtilities.isEmptyString(getMaintenanceRequest().getCouponBookReference())){
				eligible = false;
				break;
			}
		}
		if(eligible){
			if(authOrComplete.equalsIgnoreCase("authorizePO")){
				RequestContext.getCurrentInstance().execute("authorizeHiddenButtonClicked()");
			}else{
				RequestContext.getCurrentInstance().execute("showDialog('completePOWidgetVar')");
			}
		}else{
			RequestContext.getCurrentInstance().execute("showDialog('eligibleMaintenancePOWidgetVar')");
		}		
	}

	public void resetLeasePlanMarkup(){
		getMaintenanceRequest().setServiceProviderMarkupInd(MalConstants.FLAG_N);
	}
	public String getViewMode() {
		return viewMode;
	}

	public void setViewMode(String viewMode) {
		this.viewMode = viewMode;
	}

	public MaintenanceRequest getMaintenanceRequest() {
		return maintenanceRequest;
	}

	public void setMaintenanceRequest(MaintenanceRequest maintenanceRequest) {
		this.maintenanceRequest = maintenanceRequest;
	}

	public MaintenanceRequest getCopyOfSavedMaintenanceRequest() {
		return copyOfSavedMaintenanceRequest;
	}


	public void setCopyOfSavedMaintenanceRequest(
			MaintenanceRequest savedMaintenanceRequest) {
		this.copyOfSavedMaintenanceRequest = savedMaintenanceRequest;
	}


	public Long getMrqId() {
		return mrqId;
	}

	public void setMrqId(Long mrqId) {
		this.mrqId = mrqId;
	}

	public Long getFmsId() {
		return fmsId;
	}

	public String getDriverId() {
		return driverId;
	}


	public void setDriverId(String driverId) {
		this.driverId = driverId;
	}


	public void setFmsId(Long fmsId) {
		this.fmsId = fmsId;
	}



	public FleetMaster getUnit() {
		return unit;
	}

	public void setUnit(FleetMaster unit) {
		this.unit = unit;
	}

	public LatestOdometerReadingV getLatestOdometerReading() {
		return latestOdometerReading;
	}

	public void setLatestOdometerReading(LatestOdometerReadingV latestOdometerReading) {
		this.latestOdometerReading = latestOdometerReading;
	}

	public List<MaintenanceRequestStatus> getMaintenanceRequestStatuses() {
		return maintenanceRequestStatuses;
	}

	public void setMaintenanceRequestStatuses(
			List<MaintenanceRequestStatus> maintenanceRequestStatuses) {
		if(this.maintenanceRequestStatuses != null){
			this.maintenanceRequestStatuses.clear();
		}else{
			this.maintenanceRequestStatuses = new ArrayList<MaintenanceRequestStatus>();
		}
		
		if(getMaintenanceRequest() == null || getMaintenanceRequest().getMaintReqStatus().isEmpty() || maintRequestService.convertPOStatus(getMaintenanceRequest().getMaintReqStatus()).getSortOrder() < 5){
			for(MaintenanceRequestStatus mrs : maintenanceRequestStatuses){
				if(mrs.getSortOrder() < 5){
					this.maintenanceRequestStatuses.add(mrs);
				}
			}
		}else{
			for(MaintenanceRequestStatus mrs : maintenanceRequestStatuses){
				if(mrs.getSortOrder() >= 5){
					this.maintenanceRequestStatuses.add(mrs);
				}
			}
		}
	}
	
	public List<MaintenanceRequestType> getMaintenanceRequestTypes() {
		return maintenanceRequestTypes;
	}

	public void setMaintenanceRequestTypes(List<MaintenanceRequestType> maintenanceRequestTypes) {
		this.maintenanceRequestTypes = maintenanceRequestTypes;
	}

	public List<MaintenanceCategory> getMaintenanceCategories() {
		return maintenanceCategories;
	}

	public void setMaintenanceCategories(List<MaintenanceCategory> maintenanceCategories) {
		this.maintenanceCategories = maintenanceCategories;
	}
	
	public List<MaintenanceRechargeCode> getMaintenanceRechargeCodes() {
		if (selectedTask.getRechargeFlag().equals("N")) {
			setMaintenanceRechargeCodes(null);
		} else {
				setMaintenanceRechargeCodes(maintenanceRechargeCodesFromDB);
		}
		return maintenanceRechargeCodes;
	}

	public void setMaintenanceRechargeCodes(List<MaintenanceRechargeCode> maintenanceRechargeCodes) {
		this.maintenanceRechargeCodes = maintenanceRechargeCodes;
	}	
	
	public List<MaintenanceRepairReasons> getMaintenanceRepairReasons() {
		return maintenanceRepairReasons;
	}


	public void setMaintenanceRepairReasons(List<MaintenanceRepairReasons> maintenanceRepairReasons) {
		this.maintenanceRepairReasons = maintenanceRepairReasons;
	}


	public String getMaintRechargeFlag(){
		if (getSelectedTask() != null && getSelectedTask().getMrtId() != null) {
			return getSelectedTask().getRechargeFlag();
		}
		return vehicleMaintenanceService.getDefaultMaintRechargeFlag(getMaintenanceRequest());
	}
	
	public MaintenanceRechargeCode getMaintRechargeCode(){
		if (getSelectedTask() != null && getSelectedTask().getMrtId() != null) {
			return vehicleMaintenanceService.convertMaintenanceRechargeCode(getSelectedTask().getRechargeCode());
		}
		return vehicleMaintenanceService.getDefaultMaintRechargeCode(getMaintenanceRequest());
	}
	
	public String getPayeeInvoiceNo(){
		return maintRequestService.getPayeeInvoiceNo(getMaintenanceRequest().getMrqId());
	}	
	
	public String getMafsInvoiceNo(){
		return maintRequestService.getMafsInvoiceNo(getMaintenanceRequest().getMrqId());		
	}
	
	public Boolean isPoToggleable(){
		if (!MALUtilities.isEmpty(getMaintenanceRequest().getMaintReqStatus()) && getMaintenanceRequest().getMaintReqStatus().equals(MaintenanceRequestStatusEnum.MAINT_REQUEST_STATUS_COMPLETE.getCode())){
			return true;
		}
		return false;
	}

	public Odometer getOdometer() {
		return odometer;
	}

	public void setOdometer(Odometer odometer) {
		this.odometer = odometer;
	}

	public MaintenanceRequestTask getSelectedTask() {
		return selectedTask;
	}

	public void setSelectedTask(MaintenanceRequestTask selectedTask) {
		this.selectedTask = selectedTask;
		if(!MALUtilities.isEmpty(selectedTask)){
			this.selectedTaskIndex = selectedTask.getIndex() - 1;
		}
	}

	public int getSelectedTaskIndex() {
		return selectedTaskIndex;
	}

	public void setSelectedTaskIndex(int selectedTaskIndex) {
		this.selectedTaskIndex = selectedTaskIndex;
	}

	public BigDecimal getMarkUpAmount() {
		return markUpAmount;
	}

	public void setMarkUpAmount(BigDecimal markUpAmount) {
		this.markUpAmount = markUpAmount;
	}		
	
	public Long getSelectedProviderId() {
		return selectedProviderId;
	}

	public void setSelectedProviderId(Long selectedProviderId) {
		this.selectedProviderId = selectedProviderId;
	}
	   
	public String getMaintenanceCategoryCode() {
		return maintenanceCategoryCode;
	}

	
	public void setMaintenanceCategoryCode(String maintenanceCategoryCode) {

		this.maintenanceCategoryCode = maintenanceCategoryCode;
		if(this.maintenanceCategoryCode != null && !this.maintenanceCategoryCode.isEmpty()){
			this.setShowHistoricalCategoryIconIndicator(showHistoricalCategoryIcon(this.maintenanceCategoryCode));
		}else{
			this.setShowHistoricalCategoryIconIndicator(false);
		}
	}
	
	public void initHistoricalMaintCategoryList(){
		if(this.maintenanceCategoryCode != null && !this.maintenanceCategoryCode.isEmpty()){
			this.setHistoricalMaintCategoryList(maintRequestService.getHistoricalMaintCatCodes(maintenanceRequest.getFleetMaster().getVin(), this.maintenanceCategoryCode, getMaintenanceRequest().getMrqId()));
		}else{
			this.setHistoricalMaintCategoryList(new ArrayList<HistoricalMaintCatCodeVO>());
		}
	}


	public boolean isLeasePlanServiceProvider() {
		return isLeasePlanServiceProvider;
	}

	public void setLeasePlanServiceProvider(boolean isLeasePlanServiceProvider) {
		this.isLeasePlanServiceProvider = isLeasePlanServiceProvider;
	}
	
	public boolean isCostAvoidanceDisplay(){
		boolean isDisplay = false;
		
 		if(!MALUtilities.isEmpty(getSelectedTask())){		
 			isDisplay = getSelectedTask().isCostAvoidanceIndicator();
 		}
 		
		return isDisplay;
	}

	public boolean isGoodwillDisplay(){
		boolean isDisplay = false;				
		isDisplay = getMaintenanceRequest().isGoodwillIndicator();
		return isDisplay;
	}

	public List<FleetNotes> getJobNotes() {
		return jobNotes;
	}


	public void setJobNotes(List<FleetNotes> jobNotes) {
		this.jobNotes = jobNotes;
	}


	public boolean isJobNoteExist() {
		return isJobNoteExist;
	}


	public void setJobNoteExist(boolean isJobNoteExist) {
		this.isJobNoteExist = isJobNoteExist;
	}


	public boolean isValidationSuccess() {
		return validationSuccess;
	}


	public void setValidationSuccess(boolean validationSuccess) {
		this.validationSuccess = validationSuccess;
	}

	public List<HistoricalMaintCatCodeVO> getHistoricalMaintCategoryList() {
		return historicalMaintCategoryList;
	}


	public void setHistoricalMaintCategoryList(
			List<HistoricalMaintCatCodeVO> historicalMaintCategoryList) {
		this.historicalMaintCategoryList = historicalMaintCategoryList;
	}


	public boolean isShowHistoricalCategoryIconIndicator() {
		return showHistoricalCategoryIconIndicator;
	}


	public void setShowHistoricalCategoryIconIndicator(
			boolean showHistoricalCategoryIconIndicator) {
		this.showHistoricalCategoryIconIndicator = showHistoricalCategoryIconIndicator;
	}


	public List<CostAvoidanceReason> getCostAvoidanceReasons() {
		return costAvoidanceReasons;
	}


	public void setCostAvoidanceReasons(List<CostAvoidanceReason> costAvoidanceReasons) {
		this.costAvoidanceReasons = costAvoidanceReasons;
	}


	public String getServiceCodeQuery() {
		return serviceCodeQuery;
	}


	public void setServiceCodeQuery(String serviceCodeQuery) {
		this.serviceCodeQuery = serviceCodeQuery;
	}


	public String getMaintCodeQuery() {
		return maintCodeQuery;
	}


	public void setMaintCodeQuery(String maintCodeQuery) {
		this.maintCodeQuery = maintCodeQuery;
	}


	public String getSelectedContactId() {
		return selectedContactId;
	}


	public void setSelectedContactId(String selectedContactId) {
		this.selectedContactId = selectedContactId;
	}


	public List<MaintenanceInvoiceCreditVO> getCreditTaskList() {
		return creditTaskList;
	}


	public void setCreditTaskList(List<MaintenanceInvoiceCreditVO> creditTaskList) {
		this.creditTaskList = creditTaskList;
	}


	public List<Doc> getCreditList() {
		return creditList;
	}


	public void setCreditList(List<Doc> creditList) {
		this.creditList = creditList;
	}


	public Doc getSelectedCredit() {
		return selectedCredit;
	}


	public void setSelectedCredit(Doc selectedCredit) {
		this.selectedCredit = selectedCredit;
	}
	

	public List<LogBookTypeVO> getLogBookTypes() {
		return logBookTypes;
	}


	public void setLogBookTypes(List<LogBookTypeVO> logBookTypes) {
		this.logBookTypes = logBookTypes;
	}


	public String getServiceProviderDiscountInfo() {
		return serviceProviderDiscountInfo;
	}


	public void setServiceProviderDiscountInfo(String serviceProviderDiscountInfo) {
		this.serviceProviderDiscountInfo = serviceProviderDiscountInfo;
	}


	public boolean isShowChangeGLDistDialog() {
		boolean showGlDistDialogIfRechargeIsNo = false;
		for(MaintenanceRequestTask task : getMaintenanceRequest().getMaintenanceRequestTasks()){
			if(task.getRechargeFlag().equalsIgnoreCase("N")){
				showGlDistDialogIfRechargeIsNo = true;
			}
		}
		return showGlDistDialogIfRechargeIsNo;
	}
	
	public void evalualteRechargeFlagSelect(){
		if(this.selectedTask != null){
			if(!MALUtilities.isEmpty(this.selectedTask.getRechargeFlag()) && this.selectedTask.getRechargeFlag().equals("N")){
				setMaintenanceRechargeCodes(null);
				this.selectedTask.setRechargeCode(null);
			}else{
				if(!MALUtilities.isEmpty(this.selectedTask.getRechargeFlag()) && this.selectedTask.getRechargeFlag().equals("Y")){
					setMaintenanceRechargeCodes(maintenanceRechargeCodesFromDB);
					this.selectedTask.setRechargeCode(maintRechargeCodeOfSelectedTask);
				}
			}
		}
	}

	public void setVehicleStatusAsOnServiceRequestDate(){
		if(maintenanceRequest.getFleetMaster() != null) {
			Long qmdId = quotationService.getQmdIdFromUnitNo(maintenanceRequest.getFleetMaster().getUnitNo());
			if(MALUtilities.isEmpty(qmdId) || qmdId.compareTo(0L) <= 0){
				setVehicleNotOnContract(true);
			}
		}
	}

	public boolean isVehicleNotOnContract() {
		return vehicleNotOnContract;
	}


	public void setVehicleNotOnContract(boolean vehicleNotOnContract) {
		this.vehicleNotOnContract = vehicleNotOnContract;
	}


	public boolean isShowCreditLink() {
		return showCreditLink;
	}


	public void setShowCreditLink(boolean showCreditLink) {
		this.showCreditLink = showCreditLink;
	}


	public Doc getPurchaseOrderDoc() {
		return purchaseOrderDoc;
	}


	public void setPurchaseOrderDoc(Doc purchaseOrderDoc) {
		this.purchaseOrderDoc = purchaseOrderDoc;
	}


	public boolean isShowChangeGLDistConfirmDialog() {
		return showChangeGLDistConfirmDialog;
	}


	public void setShowChangeGLDistConfirmDialog(
			boolean showChangeGLDistConfirmDialog) {
		this.showChangeGLDistConfirmDialog = showChangeGLDistConfirmDialog;
	}


	public Long getSelectedProviderParentId() {
		return selectedProviderParentId;
	}


	public void setSelectedProviderParentId(Long selectedProviderParentId) {
		this.selectedProviderParentId = selectedProviderParentId;
	}


	public String getMaintenanceMarkupCategoryCode() {
		return maintenanceMarkupCategoryCode;
	}


	public void setMaintenanceMarkupCategoryCode(
			String maintenanceMarkupCategoryCode) {
		this.maintenanceMarkupCategoryCode = maintenanceMarkupCategoryCode;
	}


	public String getMaintenanceMarkupCode() {
		return maintenanceMarkupCode;
	}


	public void setMaintenanceMarkupCode(String maintenanceMarkupCode) {
		this.maintenanceMarkupCode = maintenanceMarkupCode;
	}


	public String getMaintenanceMarkupRechargeCode() {
		return maintenanceMarkupRechargeCode;
	}


	public void setMaintenanceMarkupRechargeCode(
			String maintenanceMarkupRechargeCode) {
		this.maintenanceMarkupRechargeCode = maintenanceMarkupRechargeCode;
	}


	public boolean isMaintCodeSet() {
		return maintCodeSet;
	}


	public void setMaintCodeSet(boolean maintCodeSet) {
		this.maintCodeSet = maintCodeSet;
	}
	private void adjustTaskDataTableHeight(){
		if(maintenanceRequest.getMaintenanceRequestTasks() != null){
			if(maintenanceRequest.getMaintenanceRequestTasks().isEmpty()){
				getDataTable().setHeight(20);
			}else{
				if(maintenanceRequest.getMaintenanceRequestTasks().size() < 7){
					getDataTable().setHeight(maintenanceRequest.getMaintenanceRequestTasks().size()*25);
				}else{
					getDataTable().setHeight(DEFAULT_DATATABLE_HEIGHT);
				}
			}
		}else{
			getDataTable().setHeight(20);
		}
	}
	public boolean hasPermission(){
		return hasPermission;
	}
	public List<SupplierServiceTypeCodes> getServiceTypeCodeList() {
		return serviceTypeCodeList;
	}


	public void setServiceTypeCodeList(
			List<SupplierServiceTypeCodes> serviceTypeCodeList) {
		this.serviceTypeCodeList = serviceTypeCodeList;
	}


	public String getServiceTypeCode() {
		return serviceTypeCode;
	}


	public void setServiceTypeCode(String serviceTypeCode) {
		this.serviceTypeCode = serviceTypeCode;
	}


	public VehicleInformationVO getVehicleInfo() {
		return vehicleInfo;
	}


	public void setVehicleInfo(VehicleInformationVO vehicleInfo) {
		this.vehicleInfo = vehicleInfo;
	}


	public boolean isNextDisabled() {
		return isNextDisabled;
	}


	public void setNextDisabled(boolean isNextDisabled) {
		this.isNextDisabled = isNextDisabled;
	}


	public boolean isPrevDisabled() {
		return isPrevDisabled;
	}


	public void setPrevDisabled(boolean isPrevDisabled) {
		this.isPrevDisabled = isPrevDisabled;
	}


	public String getDefaultMarkUpMaintCode() {
		return defaultMarkUpMaintCode;
	}


	public void setDefaultMarkUpMaintCode(String defaultMarkUpMaintCode) {
		this.defaultMarkUpMaintCode = defaultMarkUpMaintCode;
	}


	public MaintenanceRequestTask getMaintenanceFeeLine() {
		return maintenanceFeeLine;
	}


	public void setMaintenanceFeeLine(MaintenanceRequestTask maintenanceFeeLine) {
		this.maintenanceFeeLine = maintenanceFeeLine;
	}
	
	public void captureSchAuthCode(){
		getMaintenanceRequest().setCouponBookReference(selectedAuthCode);
	}
	
	public String getSelectedAuthCode() {
		return selectedAuthCode;
	}


	public void setSelectedAuthCode(String selectedAuthCode) {
		this.selectedAuthCode = selectedAuthCode;
	}

	public boolean isHasPermissionOnAuthCode() {
		return hasPermissionOnAuthCode;
	}


	public void setHasPermissionOnAuthCode(boolean hasPermissionOnAuthCode) {
		this.hasPermissionOnAuthCode = hasPermissionOnAuthCode;
	}


	public boolean isSelectedTaskDiscountLocked() {
		return selectedTaskDiscountLocked;
	}


	public void setSelectedTaskDiscountLocked(boolean selectedTaskDiscountLocked) {
		this.selectedTaskDiscountLocked = selectedTaskDiscountLocked;
	}


	public boolean isWaiveOutOfNetworkSurcharge() {
		return waiveOutOfNetworkSurcharge;
	}


	public void setWaiveOutOfNetworkSurcharge(boolean waiveOutOfNetworkSurcharge) {
		this.waiveOutOfNetworkSurcharge = waiveOutOfNetworkSurcharge;
	}
	
	
	
	
}
