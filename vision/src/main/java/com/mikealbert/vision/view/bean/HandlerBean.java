package com.mikealbert.vision.view.bean;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.mikealbert.common.MalLogger;
import com.mikealbert.common.MalLoggerFactory;
import com.mikealbert.data.entity.Driver;
import com.mikealbert.data.entity.FleetMaster;
import com.mikealbert.data.entity.QuotationModel;
import com.mikealbert.data.enumeration.DocumentNameEnum;
import com.mikealbert.data.vo.DriverSearchVO;
import com.mikealbert.data.vo.VehicleSearchCriteriaVO;
import com.mikealbert.data.vo.VehicleSearchResultVO;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.service.DriverAllocationService;
import com.mikealbert.service.DriverService;
import com.mikealbert.service.FleetMasterService;
import com.mikealbert.service.PurchaseOrderService;
import com.mikealbert.service.QuotationService;
import com.mikealbert.service.RentalCalculationService;
import com.mikealbert.service.reporting.JasperReportService;
import com.mikealbert.util.MALUtilities;
import com.mikealbert.vision.service.PurchasingEmailService;
import com.mikealbert.vision.view.ViewConstants;

/**
 * This is the landing page from Willow when trying to access vision;
 *
 */
@Component
@Scope("view")
public class HandlerBean extends HandlerBaseBean{

	private static final long serialVersionUID = 7598046037577322358L;
	private MalLogger logger = MalLoggerFactory.getLogger(this.getClass());
	
	@Resource DriverService driverService;
	@Resource DriverAllocationService driverAllocService;
	@Resource FleetMasterService fleetMasterService;
	@Resource QuotationService quotationService;
	@Resource RentalCalculationService rentalCalculationService;
	@Resource JasperReportBean jasperReportBean;
	@Resource JasperReportService jasperReportService;
	@Resource PurchasingEmailService purchasingEmailService;
	@Resource PurchaseOrderService purchaseOrderService;

	//TODO: This is temporary to address prod issue
	
	private DriverSearchVO selectedSearchVO  = null;
	private Long fmsId = null;
	private String unitNo = null;
	private String driverName = null;
	private String isSearchRequired = "false";
	private String driverId;
	private String module;
	private String origin;
	private String workClass;
	private Long cId = null;
	private String accountType = null;
	private String accountCode = null;
	private Long qmdId = null;

	@PostConstruct
	public void init() {
		openPage();
	}

	/**
	 * Method called when Handler is opened;  It retrieves the request parameters such as
	 * MODULE, FMS_ID, DRV_ID, and ORIGIN which will determine where the handler should go next;<br>
	 * 1) Based upon the module a top level condition is met (drivers, admin, etc..)
	 * 2) For "drivers": When a driver is passed as a request parameter and we are not coming from FLEQ001,
	 * navigate to 'Driver Search' then to 'Driver Add Edit'; (Navigating to 'Driver Add Edit'
	 * in this case occurs in 'DriverSearchBean')
	 * 3) For "drivers": When a driver is passed as a request parameter and we are coming from FLEQ001, navigate
	 * to 'Driver Search';
	 * 4) For "drivers": When a driver is not passed as a request parameter and we are coming from QM001 or QM148,
	 * navigate directly to 'Driver Add Edit';
	 * 5) For "drivers": If one of the above conditions are not met, then navigate to 'Driver Search'
	 * 6) For "admin": we select the Admin menu group and the Work Class Permissions menu and then navigate to 'Work Class Permissions'
	 */
	public void determineNewPage() {
		boolean pageForwarded = false;
		this.module = this.getRequestParameter("module");
		this.origin = this.getRequestParameter("origin");

		if(this.module.equalsIgnoreCase("drivers")){
			this.driverId = this.getRequestParameter("drv_id");
			this.isSearchRequired = "false";
			if(isNotNull(driverId)){
				Driver driver  = driverService.getDriver(Long.parseLong(this.driverId));
				if(driver != null){
					this.driverName = driver.getDriverSurname() + ", " + driver.getDriverForename();
				}

				if(!MALUtilities.isEmptyString(origin) && !origin.equalsIgnoreCase("FLEQ001")){  // if not coming from eq001 with a driver id go to add edit
					if(driver != null){
						// this is to "simulate" selecting a driver so we can follow the natural flow
						selectedSearchVO = new DriverSearchVO();
						selectedSearchVO.setDrvId(driver.getDrvId());
						this.isSearchRequired = "true";
						navigateToAddEditDriver();
						pageForwarded = true;
					}
				}else{ // else we are coming from eq001 we need to stay on driver search; and optionally (if supplied) also filter by unit
					// TODO: longer term the search should handle FMS ID and the Vin LOV should also pass back an FMS ID
					if(!MALUtilities.isEmptyString(this.getRequestParameter("fmsid"))){
						this.fmsId = isNotNull(this.getRequestParameter("fmsid"))? Long.parseLong(this.getRequestParameter("fmsid")) : null;
						//check to see if they have (or have had) allocations for this unit; it should be a good indicator for vehicle that has been put in service
						if(driverAllocService.unitHasHistoricAllocations(this.fmsId)){
							// lookup the unit number
							FleetMaster vehic = fleetMasterService.getFleetMasterByFmsId(this.fmsId);
							if(vehic != null){
								this.unitNo = vehic.getUnitNo();
							}
						}
					}
					//Setting this variable to true will require a search to be performed in Driver Search
					this.isSearchRequired = "true";
					navigateToDriverSearch();
					pageForwarded = true;
				}
			}else{
				if(!MALUtilities.isEmptyString(origin) && (origin.equalsIgnoreCase("FLQM001") || origin.equalsIgnoreCase("FLQM148"))){ //go to Driver Add Edit when coming from FLQM001 or FLQM148
					navigateToDriverAdd();
					pageForwarded = true;
				}
			}

			if(pageForwarded == false){
				navigateToDriverSearch();
			}
		} else if(module.equalsIgnoreCase("admin")) {
			navigateToAdminWorkClass();
		} else if(module.equalsIgnoreCase("rental_calcs")) {
			navigateToRentalCalcs();
		} else if(module.equalsIgnoreCase("client_contact_maint")) {
			navigateToClientContacts();
		} else if(module.equalsIgnoreCase("process_queue")){
			if(!MALUtilities.isEmptyString(origin) && origin.equalsIgnoreCase("UTMENULP")){
				navigateToProcessQueue();
			}
		} else if(module.equalsIgnoreCase("Fleet_Maintenance")) {
			if(!MALUtilities.isEmptyString(origin) && origin.equalsIgnoreCase("FLEQ001")){
				navigateToFleetMaintServiceHistory();
			} else {
				navigateToFleetMaintVehicleSearch();
			}
		} else if (module.equalsIgnoreCase("Client_Finance_Params")) {
			navigateToClientFinance();
		} else if(module.equalsIgnoreCase("Supplier_Codes")) {
			navigateToFleetProviderMaintCodeMaint();
		} else if(module.equalsIgnoreCase("trim_maintenance")) {
			navigateToTrimSearch();
		} else if(module.equalsIgnoreCase("unit_progress")) {
			if(!MALUtilities.isEmptyString(origin) && origin.equalsIgnoreCase("FLEQ001")){
				navigateToInServiceDateProgress();
			} else {
				navigateToUnitProgressSearch();
			}
		} else if(module.equalsIgnoreCase("upfit_notes")) {
			navigateToUnitUpfitNotes();
		} else if(module.equalsIgnoreCase("quote_dealer_accesory")) {
			navigateToQuoteDealerAccessory();
		} else if (module.equalsIgnoreCase("Client_Elements")) {
			navigateToClientElements();
		} else if (module.equalsIgnoreCase("Service_Elements")) {
				navigateToServiceElements();
		} else if(module.equalsIgnoreCase("vehicle_order_status")) {
			navigateToVehicleOrderStatus();
		} else if(module.equalsIgnoreCase("maintain_master_schedule")) {
			navigateToMaintainMasterSchedule();
		} else if(module.equalsIgnoreCase("client_facing_queue")) {
			navigateToClientFacingQueue();
		} else if(module.equalsIgnoreCase("acceptance_queue")) {
			navigateToAcceptanceQueue();
		} else if(module.equalsIgnoreCase("order_progress_search")) {
			navigateToOrderProgressSearch();
		} else if(module.equalsIgnoreCase("search_debit_credit_memo")) {
			navigateToDebitCreditMemoSearch();			
		} else if(module.equalsIgnoreCase("maint_po")){
			navigateToMainPOReport();			
		} else if(module.equalsIgnoreCase("vehicle_order_summary")){
			navigateVehicleOrderSummaryReport();			
		} else if(module.equalsIgnoreCase("third_party_po")){
			navigateThirdPartyPOReport();			
		} else if(module.equalsIgnoreCase("config_search")) {
			navigateToVehicleConfigurationSearch();
		} else if(module.equalsIgnoreCase("coversheet_address")) {
			navigateToPrintCoverSheetForPurchaseOrder();
		} else if(module.equalsIgnoreCase("update_grade_group")) {
			navigateToUpdateGradeGroup();	
		} else if(module.equalsIgnoreCase("update_projected_months")) {
			navigateToUpdateProjectedMonths();			
		} else if(module.equalsIgnoreCase("quote_request")) {
			navigateToQuoteRequestSearch();
		} else if(module.equalsIgnoreCase("quote_titling_info")) {
			navigateToQuoteTitlingInfo();
		} else if(module.equalsIgnoreCase("contract_management")) {
			navigateToContractManagementVehicleSearch();
		} else if(module.equalsIgnoreCase("amortization_schedule")) {
			navigateToAmortizationScheduleReport();
		} else if(module.equalsIgnoreCase("schedule_a")) {
			navigateToScheduleA();			
		}
	
	}

	/**
	 * Sets up the values to be passed to the 'Driver Search' page, then navigates
	 * to the 'Driver Search' page.
	 */
	private void navigateToDriverSearch() {
		Map<String, Object> nextPageValues = new HashMap<String, Object>();

		if(selectedSearchVO != null){
			if(selectedSearchVO.getDrvId() != null){
				nextPageValues.put(ViewConstants.VIEW_PARAM_DRIVER_ID, String.valueOf(selectedSearchVO.getDrvId()));
			}
		}
		if(this.unitNo != null){
			nextPageValues.put(ViewConstants.VIEW_PARAM_UNIT_NO, String.valueOf(this.unitNo));
		}
		if(this.driverId != null){
			nextPageValues.put(ViewConstants.VIEW_PARAM_DRIVER_ID, this.driverId);
		}
		if(this.driverName != null){
			nextPageValues.put(ViewConstants.VIEW_PARAM_DRIVER_NAME, String.valueOf(this.driverName));
		}
		if(this.isSearchRequired != null){
			nextPageValues.put(ViewConstants.VIEW_PARAM_DRIVER_SEARCH_REQ, String.valueOf(this.isSearchRequired));
		}

		saveNextPageInitStateValues(nextPageValues);
		menuBean.setMenuSelection(ViewConstants.DISPLAY_NAME_DRIVER_MODULE_GROUP,ViewConstants.DISPLAY_NAME_DRIVER_SEARCH);
		forwardToURL(ViewConstants.DRIVER_SEARCH);
	}

	/**
	 * Sets the 'Driver Add' menu option, then Navigates directly to the 'Driver Add' page
	 */
	private void navigateToDriverAdd(){
		menuBean.setMenuSelection(ViewConstants.DISPLAY_NAME_DRIVER_MODULE_GROUP,ViewConstants.DISPLAY_NAME_DRIVER_ADD);
		forwardToURL(ViewConstants.DRIVER_ADD);
    }
	/**
	 * Sets the 'Process Queue' menu option, then Navigates directly to the 'Process Queue' page
	 */
	private void navigateToProcessQueue(){
		menuBean.setMenuSelection(ViewConstants.DISPLAY_NAME_RENTAL_CALCULATION_GROUP, ViewConstants.DISPLAY_NAME_UNIT_RECONCILIATION);
		forwardToURL(ViewConstants.UNIT_RECONCILIATION);
    }
	
	/**
	 * Sets the 'Process Queue' menu option, then Navigates directly to the 'Process Queue' page
	 */
	private void navigateToAcceptanceQueue(){
		menuBean.setMenuSelection(ViewConstants.DISPLAY_NAME_ORDER_TO_DELIVERY_GROUP, ViewConstants.DISPLAY_NAME_ACCEPTANCE_QUEUE);
		forwardToURL(ViewConstants.ACCEPTANCE_QUEUE);
    }
	
	private void navigateToOrderProgressSearch() {
		menuBean.setMenuSelection(ViewConstants.DISPLAY_NAME_ORDER_TO_DELIVERY_GROUP, ViewConstants.DISPLAY_NAME_ORDER_PROGRESS_SEARCH);
		forwardToURL(ViewConstants.ORDER_PROGRESS_SEARCH);
	}

	private void navigateToDebitCreditMemoSearch() {
		menuBean.setMenuSelection(ViewConstants.DISPLAY_NAME_DEBIT_CREDIT_MEMO_GROUP, ViewConstants.DISPLAY_NAME_SEARCH_DEBIT_CREDIT_MEMO);
		forwardToURL(ViewConstants.SEARCH_DEBIT_CREDIT_MEMO);
	}	
	/** This simulates the navigation flow as if it were going through Driver Search page then to
	 * Driver Add Edit; In reality, this method it is navigating directly to Driver Add Edit; */
	private void navigateToAddEditDriver(){
	   	//For simulating the navigation flow, Driver Search page is used as "thisPage"
	    thisPage.setPageDisplayName(ViewConstants.DISPLAY_NAME_DRIVER_SEARCH);
	    thisPage.setPageUrl(ViewConstants.DRIVER_SEARCH);

    	Map<String, Object> restoreStateValues = new HashMap<String, Object>();
    	restoreStateValues.put(ViewConstants.DRIVER_ID, this.driverId);
		restoreStateValues.put("DRIVER_NAME", this.driverName);
		restoreStateValues.put("SELECTD_INDEX", 0);

		saveRestoreStateValues(restoreStateValues);

		Map<String, Object> nextPageValues = new HashMap<String, Object>();
		nextPageValues.put(ViewConstants.VIEW_PARAM_DRIVER_ID, String.valueOf(selectedSearchVO.getDrvId()));
		saveNextPageInitStateValues(nextPageValues);
		menuBean.setMenuSelection(ViewConstants.DISPLAY_NAME_DRIVER_MODULE_GROUP,ViewConstants.DISPLAY_NAME_DRIVER_SEARCH);
		forwardToURL(ViewConstants.DRIVER_ADD);
    }

	/**
	 * Sets the values for the next page (Work Class Admin). Then forwards to Work Class Admin page.
	 */
	private void navigateToAdminWorkClass(){
		//Setting the handler's URL to the dashboard is needed because we are saving the next page init state.
		// When setting next page init, this 'null page' gets added to the page list. (Needed for Cancel button to work correctly)
		this.thisPage.setPageUrl(ViewConstants.DASHBOARD_PAGE);

		Map<String, Object> nextPageValues = new HashMap<String, Object>();
		this.workClass = this.getRequestParameter("menu_name");
		if(this.workClass != null){
			nextPageValues.put(ViewConstants.VIEW_PARAM_WORK_CLASS, String.valueOf(this.workClass));
		}
		saveNextPageInitStateValues(nextPageValues);

		menuBean.setMenuSelection(ViewConstants.DISPLAY_NAME_ADMIN_MODULE_GROUP,ViewConstants.DISPLAY_NAME_ADMIN_WORK_CLASS_PERMISSIONS);
		forwardToURL(ViewConstants.ADMIN_WORK_CLASS_PERMISSIONS);
	}

	/**
	 * Sets the values for the next page (Work Class Admin). Then forwards to Work Class Admin page.
	 */
	private void navigateToRentalCalcs(){
		//Setting the handler's URL to the dashboard is needed because we are saving the next page init state.
		// When setting next page init, this 'null page' gets added to the page list. (Needed for Cancel button to work correctly)
		this.thisPage.setPageUrl(ViewConstants.DASHBOARD_PAGE);

		Map<String, Object> nextPageValues = new HashMap<String, Object>();
		String quote_id = this.getRequestParameter("QMD_ID");
		String requestedPage = this.getRequestParameter("REQ_PAGE");

		if(quote_id != null){
			nextPageValues.put(ViewConstants.VIEW_PARAM_QUOTE_MODEL_ID, Long.valueOf(quote_id));
		}
		saveNextPageInitStateValues(nextPageValues);

			if (quote_id != null && requestedPage != null) {
				if (requestedPage.equals("CAP_COST")) {
					forwardToURL(ViewConstants.CAPITAL_COSTS);
				}
			} else if (quote_id != null) {
        	    String leaseType = quotationService.getLeaseType(Long.parseLong(quote_id));
        	    menuBean.setMenuSelection(ViewConstants.DISPLAY_NAME_RENTAL_CALCULATION_GROUP, ViewConstants.DISPLAY_NAME_QUOTE_OVERVIEW);
        	    if (leaseType.equals(QuotationService.OPEN_END_LEASE)) {
        		forwardToURL(ViewConstants.QUOTE_OVERVIEW_OE);
        	    } else {
        		forwardToURL(ViewConstants.QUOTE_OVERVIEW);
        	    }
        	} else {
			menuBean.setMenuSelection(ViewConstants.DISPLAY_NAME_RENTAL_CALCULATION_GROUP,"Capital Cost Inquiry");
			forwardToURL(ViewConstants.CAPITAL_COSTS);

		}

	}

	private void navigateToFleetMaintVehicleSearch(){
		menuBean.setMenuSelection(ViewConstants.DISPLAY_NAME_FLEET_MAINT_GROUP,ViewConstants.DISPLAY_NAME_FLEET_MAINT_VEHICLE_SEARCH);
		forwardToURL(ViewConstants.FLEET_MAINT_VEHICLE_SEARCH);
	}

	private void navigateToTrimSearch(){
		menuBean.setMenuSelection(ViewConstants.DISPLAY_NAME_TRIM_MAINTENANCE_GROUP,ViewConstants.DISPLAY_NAME_TRIM_SEARCH);
		forwardToURL(ViewConstants.TRIM_SEARCH);
	}

	private void navigateToUnitProgressSearch(){
		menuBean.setMenuSelection(ViewConstants.DISPLAY_NAME_ORDER_TO_DELIVERY_GROUP,ViewConstants.DISPLAY_NAME_UNIT_PROGRESS_CHASING);
		forwardToURL(ViewConstants.UNIT_PROGRESS_SEARCH);
	}
	//HPS-2045
	private void navigateToUpdateGradeGroup(){
		menuBean.setMenuSelection(ViewConstants.DISPLAY_NAME_RENTAL_CALCULATION_GROUP, ViewConstants.DISPLAY_NAME_UPDATE_GRADE_GROUP);
		forwardToURL(ViewConstants.UPDATE_GRADE_GROUP);
	}
	//HD-3
	private void navigateToUpdateProjectedMonths(){
		menuBean.setMenuSelection(ViewConstants.DISPLAY_NAME_RENTAL_CALCULATION_GROUP, ViewConstants.DISPLAY_NAME_UPDATE_PROJECTED_MONTHS);
		forwardToURL(ViewConstants.UPDATE_PROJECTED_MONTHS);
	}	
	private void navigateToFleetProviderMaintCodeMaint(){
		menuBean.setMenuSelection(ViewConstants.DISPLAY_NAME_FLEET_MAINT_GROUP,ViewConstants.DISPLAY_NAME_PROVIDER_MAINT_CODE);
		forwardToURL(ViewConstants.PROVIDER_MAINT_CODE);
	}
	
	private void navigateToQuoteRequestSearch() {
		menuBean.setMenuSelection(ViewConstants.DISPLAY_NAME_QUOTE_REQUEST_GROUP,ViewConstants.DISPLAY_NAME_QUOTE_REQUEST_SEARCH);
		forwardToURL(ViewConstants.QUOTE_REQUEST_SEARCH);
	}

	private void navigateToContractManagementVehicleSearch(){
		menuBean.setMenuSelection(ViewConstants.DISPLAY_NAME_CONTRACT_MANAGEMENT_GROUP,ViewConstants.DISPLAY_NAME_CONTRACT_MGMT_VEH_SEARCH);
		forwardToURL(ViewConstants.CONTRACT_MGMT_VEH_SEARCH);
	}

	private void navigateToFleetMaintServiceHistory(){

		thisPage.setPageDisplayName(ViewConstants.DISPLAY_NAME_FLEET_MAINT_VEHICLE_SEARCH);
		thisPage.setPageUrl(ViewConstants.FLEET_MAINT_VEHICLE_SEARCH);

		if(!MALUtilities.isEmptyString(this.getRequestParameter("fmsid"))){
			this.fmsId = isNotNull(this.getRequestParameter("fmsid"))? Long.parseLong(this.getRequestParameter("fmsid")) : null;
				// lookup the unit number
				FleetMaster fleetMaster = fleetMasterService.getFleetMasterByFmsId(this.fmsId);
				if(fleetMaster != null){
					Map<String, Object> restoreStateValues = new HashMap<String, Object>();
					VehicleSearchCriteriaVO searchCriteria = new VehicleSearchCriteriaVO();
					searchCriteria.setUnitNo(fleetMaster.getUnitNo());
					restoreStateValues.put("VEHICLE_SEARCH_CRITERIA", searchCriteria);
					restoreStateValues.put("SELECTD_INDEX", 0);
					saveRestoreStateValues(restoreStateValues);

					Map<String, Object> nextPageInitValues = new HashMap<String, Object>();
					VehicleSearchResultVO selectedVehicleSearchResultVO = new VehicleSearchResultVO();
					selectedVehicleSearchResultVO.setFmsId(this.fmsId);
					nextPageInitValues.put(ViewConstants.VIEW_PARAM_FMS_ID, this.fmsId);
					nextPageInitValues.put(ViewConstants.VIEW_PARAM_VIN, fleetMaster.getVin());


					saveNextPageInitStateValues(nextPageInitValues);
				}
		}

		forwardToURL(ViewConstants.FLEET_MAINT_SERVICE_HISTORY);
	}

	private void navigateToClientFinance() {
		this.thisPage.setPageUrl(ViewConstants.DASHBOARD_PAGE);

		this.cId = Long.parseLong(this.getRequestParameter("c_id"));
		this.accountType = this.getRequestParameter("account_type");
		this.accountCode = this.getRequestParameter("account_code");

		if (!MALUtilities.isEmpty(this.cId) && !MALUtilities.isEmptyString(this.accountType) && !MALUtilities.isEmptyString(this.accountCode)) {
			Map<String, Object> nextPageInitValues = new HashMap<String, Object>();
			nextPageInitValues.put(ViewConstants.VIEW_PARAM_C_ID, this.cId);
			nextPageInitValues.put(ViewConstants.VIEW_PARAM_ACCOUNT_TYPE, this.accountType);
			nextPageInitValues.put(ViewConstants.VIEW_PARAM_ACCOUNT_CODE, this.accountCode);
			saveNextPageInitStateValues(nextPageInitValues);
		}

		forwardToURL(ViewConstants.CLIENT_FINANCE);
	}

	/** Navigates the user to the Client Contacts maintenance screen */
	private void navigateToClientContacts(){
		String account_code = this.getRequestParameter("account_code");
		String account_type = this.getRequestParameter("account_type");
		String c_id = this.getRequestParameter("c_id");

	   	//For simulating the navigation flow, Driver Search page is used as "thisPage"
	    thisPage.setPageDisplayName(ViewConstants.DISPLAY_NAME_CLIENT_POC);
	    thisPage.setPageUrl(ViewConstants.CLIENT_CONTACTS_MAINTENANCE);

    	Map<String, Object> restoreStateValues = new HashMap<String, Object>();
    	restoreStateValues.put(ViewConstants.VIEW_PARAM_ACCOUNT_CODE, account_code);
		restoreStateValues.put(ViewConstants.VIEW_PARAM_ACCOUNT_TYPE, account_type);
		restoreStateValues.put(ViewConstants.VIEW_PARAM_C_ID, c_id);

		saveRestoreStateValues(restoreStateValues);

		Map<String, Object> nextPageValues = new HashMap<String, Object>();
		nextPageValues.put(ViewConstants.VIEW_PARAM_ACCOUNT_CODE, account_code);
		nextPageValues.put(ViewConstants.VIEW_PARAM_ACCOUNT_TYPE, account_type);
		nextPageValues.put(ViewConstants.VIEW_PARAM_C_ID, c_id);

		saveNextPageInitStateValues(nextPageValues);
		//menuBean.setMenuSelection(ViewConstants.DISPLAY_NAME_DRIVER_MODULE_GROUP,ViewConstants.DISPLAY_NAME_DRIVER_SEARCH);
		forwardToURL(ViewConstants.CLIENT_CONTACTS_MAINTENANCE);
    }

	private void navigateToClientElements() {
		this.thisPage.setPageUrl(ViewConstants.DASHBOARD_PAGE);

		this.cId = Long.parseLong(this.getRequestParameter("c_id"));
		this.accountType = this.getRequestParameter("account_type");
		this.accountCode = this.getRequestParameter("account_code");

		if (!MALUtilities.isEmpty(this.cId) && !MALUtilities.isEmptyString(this.accountType) && !MALUtilities.isEmptyString(this.accountCode)) {
			Map<String, Object> nextPageInitValues = new HashMap<String, Object>();
			nextPageInitValues.put(ViewConstants.VIEW_PARAM_C_ID, this.cId);
			nextPageInitValues.put(ViewConstants.VIEW_PARAM_ACCOUNT_TYPE, this.accountType);
			nextPageInitValues.put(ViewConstants.VIEW_PARAM_ACCOUNT_CODE, this.accountCode);
			saveNextPageInitStateValues(nextPageInitValues);
		}

		forwardToURL(ViewConstants.CLIENT_SERVICE_ELEMENT_DETAILS);
	}

	private void navigateToServiceElements() {
		this.qmdId = Long.parseLong(this.getRequestParameter("QMD_ID"));

		try {
			QuotationModel quotationModel = quotationService.getQuotationModel(qmdId);
			//For formal extensions; if the condition is met to lock the screen down then we will by-pass this screen altogether and forward on to
			//Quote Overview.
			if(!MALUtilities.isEmptyString(origin) && origin.equalsIgnoreCase("FLQM148") && !rentalCalculationService.isQuoteEditable(quotationModel)){
				navigateToRentalCalcs();
			}else {
				this.thisPage.setPageUrl(ViewConstants.DASHBOARD_PAGE);
				if (!MALUtilities.isEmpty(this.qmdId)) {
					Map<String, Object> nextPageInitValues = new HashMap<String, Object>();
					nextPageInitValues.put(ViewConstants.VIEW_PARAM_QUOTE_MODEL_ID, this.qmdId);
					nextPageInitValues.put(ViewConstants.VIEW_PARAM_ORIGIN, this.origin);
					saveNextPageInitStateValues(nextPageInitValues);
				}
				forwardToURL(ViewConstants.SERVICE_ELEMENT_AMENDMENTS);
			}
		} catch (MalBusinessException e) {
			e.printStackTrace();
		}
	}

	private void navigateToUnitUpfitNotes(){
	    String fmsId = this.getRequestParameter("fmsid");
	    if(!MALUtilities.isEmptyString(fmsId)) {
	    	Map<String, Object> nextPageValues = new HashMap<String, Object>();
			nextPageValues.put(ViewConstants.VIEW_PARAM_FMS_ID, fmsId);
			saveNextPageInitStateValues(nextPageValues);
	    }

		forwardToURL(ViewConstants.UNIT_UPFIT_NOTES);
    }

	private void navigateToQuoteDealerAccessory() {
		String qmdId = this.getRequestParameter("QMD_ID");
	    if(!MALUtilities.isEmptyString(qmdId)) {
	    	Map<String, Object> nextPageValues = new HashMap<String, Object>();
			nextPageValues.put(ViewConstants.VIEW_PARAM_QUOTE_MODEL_ID, qmdId);
			saveNextPageInitStateValues(nextPageValues);
	    }

		forwardToURL(ViewConstants.QUOTE_DLR_ACCSRY);

	}

	private void navigateToMaintainMasterSchedule(){
		menuBean.setMenuSelection(ViewConstants.DISPLAY_NAME_FLEET_MAINT_GROUP,ViewConstants.DISPLAY_NAME_MAINTAIN_MASTER_SCHEDULE);
		forwardToURL(ViewConstants.MASTER_SCHEDULES);
	}


	private void navigateToVehicleOrderStatus(){
		if(!MALUtilities.isEmptyString(origin) && (origin.equalsIgnoreCase("FLEQ001") || origin.equalsIgnoreCase("FAFLTENQ") || origin.equalsIgnoreCase("FLPO024") || origin.equalsIgnoreCase("FLQM221"))){
			String mainDocId = this.getRequestParameter("main_doc_id");
			Map<String, Object> nextPageValues = new HashMap<String, Object>();
			nextPageValues.put(ViewConstants.VIEW_MODE_READ, "true");
			nextPageValues.put(ViewConstants.VIEW_PARAM_DOC_ID, mainDocId);
			saveNextPageInitStateValues(nextPageValues);
		}
		
		menuBean.setMenuSelection(ViewConstants.DISPLAY_NAME_ORDER_TO_DELIVERY_GROUP,ViewConstants.DISPLAY_NAME_VEHICLE_ORDER_STATUS);
		forwardToURL(ViewConstants.VEHICLE_ORDER_STATUS_SEARCH);
	}

	private void navigateToInServiceDateProgress() {
		String unitNo = this.getRequestParameter("unit_no");
	    if(!MALUtilities.isEmptyString(unitNo)) {
	    	Map<String, Object> nextPageValues = new HashMap<String, Object>();
			nextPageValues.put(ViewConstants.VIEW_PARAM_UNIT_NO, unitNo);
			nextPageValues.put(ViewConstants.VIEW_MODE_READ, "true");
			saveNextPageInitStateValues(nextPageValues);
	    }

	    forwardToURL(ViewConstants.UPFITTER_IN_SERVICE_QUEUE);
	}
	
	private void navigateToClientFacingQueue(){
		menuBean.setMenuSelection(ViewConstants.DISPLAY_NAME_ORDER_TO_DELIVERY_GROUP,ViewConstants.DISPLAY_NAME_CLIENT_FACING_QUEUE);
		forwardToURL(ViewConstants.CLIENT_FACING_QUEUE);
	}
	
	private void navigateToMainPOReport(){
		 String docId =  null;
		try {
			 docId = this.getRequestParameter("doc_id");
			 String stockYn = this.getRequestParameter("stock_yn");
			 boolean isReleaseRequest  = MALUtilities.convertYNToBoolean(this.getRequestParameter("release_event_yn"));
			 JasperPrint print  =  jasperReportService.getMainPurchaseOrderReport(Long.parseLong(docId), stockYn);
			 jasperReportBean.displayPDFReport(print);
			 if(isReleaseRequest){
				 try{
					 purchaseOrderService.archivePurchaseOrderDoc(Long.parseLong(docId), JasperExportManager.exportReportToPdf(print), DocumentNameEnum.MAIN_PURCHASE_ORDER); 
				 } catch (Exception e) {
					logger.error(e ,"Error in archiving po doc "+ docId);
				}
			 }
			
		} catch (Exception e) {
			logger.error(e ,"Error in handler for doc "+ docId);
		}		
	}	
	
	private void navigateVehicleOrderSummaryReport(){
		 String docId =  null;
		try {
			 docId = this.getRequestParameter("doc_id");
			 String stockYn = this.getRequestParameter("stock_yn");
			 boolean isReleaseRequest  = MALUtilities.convertYNToBoolean(this.getRequestParameter("release_event_yn"));			 
			 JasperPrint print  = jasperReportService.getVehicleOrderSummaryReport(Long.parseLong(docId), stockYn);
			 jasperReportBean.displayPDFReport(print);
			 
			 if(isReleaseRequest && ! MALUtilities.convertYNToBoolean(stockYn)){
				 try{
					
					 purchaseOrderService.archivePurchaseOrderDoc(Long.parseLong(docId), JasperExportManager.exportReportToPdf(print), DocumentNameEnum.VEHICLE_ORDER_SUMMARY); 
				 } catch (Exception e) {
					logger.error(e ,"Error in archiving po doc "+ docId);
				}
			 }
			
		} catch (Exception e) {
			logger.error(e ,"Error in handler for doc "+ docId);
		}		
	}	
	private void navigateToPrintCoverSheetForPurchaseOrder(){
		 JasperPrint report;
		 String docId =  null;
		try {
			docId = this.getRequestParameter("doc_id");
			report = jasperReportService.getPurchaseOrderCoverSheetReport(Long.parseLong(docId));
			jasperReportBean.displayPDFReport(report);				
			
		} catch (Exception e) {
			logger.error(e ,"Error in handler for doc "+ docId);
		}	
	}
	
	private void navigateThirdPartyPOReport(){
		 String docId =  null;
		try {
			 docId = this.getRequestParameter("doc_id");
			 String stockYn = this.getRequestParameter("stock_yn");
			 boolean isReleaseRequest  = MALUtilities.convertYNToBoolean(this.getRequestParameter("release_event_yn"));
			 JasperPrint print  = jasperReportService.getThirdPartyPurchaseOrderReport(Long.parseLong(docId), stockYn);
			 jasperReportBean.displayPDFReport(print);
			 if(isReleaseRequest){
				 try {
					 purchaseOrderService.archivePurchaseOrderDoc(Long.parseLong(docId), JasperExportManager.exportReportToPdf(print), DocumentNameEnum.THIRD_PARTY_PURCHASE_ORDER); 
				} catch (Exception e) {
					logger.error(e ,"Error in archiving po doc "+ docId);
				}
				
			 }
		} catch (Exception e) {
			logger.error(e ,"Error in handler for doc "+ docId);
		}		
	}
	
	private void navigateToVehicleConfigurationSearch(){
		menuBean.setMenuSelection(ViewConstants.DISPLAY_NAME_VEHICLE_CONFIGURATION_GROUP, ViewConstants.DISPLAY_NAME_CONFIGURATION_SEARCH);
		forwardToURL(ViewConstants.CONFIGURATION_SEARCH);
    }
		
	private void navigateToQuoteTitlingInfo() {
		
		this.thisPage.setPageUrl(ViewConstants.DASHBOARD_PAGE);
		
		String qmdId = this.getRequestParameter("QMD_ID");
		String cId = this.getRequestParameter("c_id");
		String accountType = this.getRequestParameter("account_type");
		String accountCode = this.getRequestParameter("account_code");
    	Map<String, Object> nextPageValues = new HashMap<String, Object>();

		if(!MALUtilities.isEmptyString(qmdId)) {
			nextPageValues.put(ViewConstants.VIEW_PARAM_QUOTE_MODEL_ID, qmdId);
	    }
	    if(!MALUtilities.isEmptyString(accountCode)) {
			nextPageValues.put(ViewConstants.VIEW_PARAM_C_ID, cId);
	    }
	    if(!MALUtilities.isEmptyString(accountCode)) {
			nextPageValues.put(ViewConstants.VIEW_PARAM_ACCOUNT_TYPE, accountType);
	    }
	    if(!MALUtilities.isEmptyString(accountCode)) {
			nextPageValues.put(ViewConstants.VIEW_PARAM_ACCOUNT_CODE, accountCode);
	    }
		saveNextPageInitStateValues(nextPageValues);

		forwardToURL(ViewConstants.QUOTE_TITLING);

	}
	
	private void navigateToAmortizationScheduleReport(){
		 JasperPrint report;
		 String currentQmdId =  null;
		 String revisionQmdId =  null;
		try {
			currentQmdId = this.getRequestParameter("current_qmd_id");
			revisionQmdId = this.getRequestParameter("revision_qmd_id");
			report = jasperReportService.getOpenEndRevisionAmortizationSchedule(Long.parseLong(currentQmdId), Long.parseLong(revisionQmdId));
			jasperReportBean.displayPDFReport(report);				
			
		} catch (Exception e) {
			logger.error(e ,"Error in Amortization Schedule report handler for current qmd_id: "+ currentQmdId + " and revision qmd_id: " + revisionQmdId);
		}	
	}

	private void navigateToScheduleA(){
		 JasperPrint report;
		 String currentQmdId =  null;
		 String revisionQmdId =  null;
		try {
			currentQmdId = this.getRequestParameter("current_qmd_id");
			revisionQmdId = this.getRequestParameter("revision_qmd_id");
			report = jasperReportService.getOpenEndRevisionScheduleA(Long.parseLong(currentQmdId), Long.parseLong(revisionQmdId));
			jasperReportBean.displayPDFReport(report);				
			
		} catch (Exception e) {
			logger.error(e ,"Error in Schedule A report handler for current qmd_id: "+ currentQmdId + " and revision qmd_id: " + revisionQmdId);
		}	
	}
	
	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}

}
