package com.mikealbert.vision.view;


public class ViewConstants {
	public static final String DEFAULT = "default";
	public static final String DASHBOARD_PAGE = "/view/dashboard";
	public static final String VIEW_EXPIRED = "/view/viewExpired";
	public static final String ERROR = "/view/error";

	public static final String DRIVER_SEARCH = "/view/drivers/driverSearch";
	public static final String DRIVER_ADD = "/view/drivers/driverAddEdit";
	public static final String CAPITAL_COST_OVERVIEW = "/view/rentalCalc/capitalCostsOverview";
	public static final String CAPITAL_COSTS = "/view/rentalCalc/capitalCosts";
	public static final String SERVICE_ELEMENTS = "/view/rentalCalc/serviceElements";
	public static final String QUOTE_OVERVIEW = "/view/rentalCalc/quoteOverview";
	public static final String QUOTE_OVERVIEW_OE = "/view/rentalCalc/quoteOverviewOE";
	public static final String OPEN_END_REVISION = "/view/rentalCalc/openEndQuoteRevision";
	public static final String QUOTATION_PROFILES = "/view/rentalCalc/quotationProfiles";
	public static final String DRIVER_ALLOCATION = "/view/drivers/driverAllocation";
	public static final String DRIVER_ALLOCATION_HISTORY = "/view/drivers/driverAllocationHistory";
	public static final String DRIVER_RELATIONSHIP = "/view/drivers/driverRelationship";
	public static final String DRIVER_OVERVIEW = "/view/drivers/driverOverview";
	public static final String CAPITAL_COST_ENTRY = "/view/rentalCalc/capitalCostEntry";
	public static final String UNIT_RECONCILIATION = "/view/rentalCalc/unitReconciliation";
	public static final String AMENDMENT_HISTORY = "/view/rentalCalc/amendmentHistory";
	public static final String UPDATE_GRADE_GROUP = "/view/rentalCalc/updateGradeGroup"; // HPS-2045
	public static final String UPDATE_PROJECTED_MONTHS = "/view/rentalCalc/updateProjectedMonths"; // HD-3
	public static final String CLIENT_CONTACTS_MAINTENANCE = "/view/clientContacts/clientPOC";
	public static final String CLIENT_POC_CONTACTS = "/view/clientContacts/clientPOCContacts";
	public static final String CLIENT_POC_COST_CENTERS = "/view/clientContacts/clientPOCCostCenters";

	public static final String ADMIN_WORK_CLASS_PERMISSIONS = "/view/admin/adminWorkClassPermissions";
	public static final String FLEET_MAINT_VEHICLE_SEARCH = "/view/fleet/vehicleSearch";
	public static final String FLEET_MAINT_SERVICE_HISTORY = "/view/fleet/maintenanceServiceHistory";
	public static final String FLEET_MAINT_PURCHASE_ORDER = "/view/fleet/maintenancePO";
	public static final String PROGRESS_CHASING = "/view/fleet/progressChasing";
	public static final String CLIENT_FINANCE = "/view/financeParameters/clientFinance";
	public static final String COST_CENTRE_FINANCE = "/view/financeParameters/costCentreFinance";
	public static final String GRADE_GROUP_FINANCE = "/view/financeParameters/gradeGroupFinance";

	public static final String CLIENT_SERVICE_ELEMENTS = "/view/serviceElements/clientServiceElements";
	public static final String CLIENT_SERVICE_ELEMENT_DETAILS = "/view/serviceElements/clientServiceElementDetails";
	public static final String CLIENT_AGREEMENTS = "/view/serviceElements/clientAgreement";
	public static final String GRADE_GROUP_SERVICE_ELEMENTS = "/view/serviceElements/gradeGroupServiceElements";
	public static final String GRADE_GROUP_SERVICE_ELEMENT_DETAILS = "/view/serviceElements/gradeGroupServiceElementDetails";
	public static final String SERVICE_ELEMENT_AMENDMENTS = "/view/serviceElements/serviceElementAmendments";

	public static final String PROVIDER_MAINT_CODE = "/view/fleet/providerMaintCodeMgmt";
	public static final String ADD_PROVIDER_MAINT_CODE = "/view/fleet/addServiceProviderMaintCode";
	public static final String TRIM_SEARCH = "/view/pricing/modelSearch";
	public static final String MODEL_PRICING = "/view/pricing/maintainModelPricing";
	public static final String UPFITTER_QUOTE = "/view/pricing/maintainUpfitterQuote";
	public static final String UNIT_PROGRESS_SEARCH = "/view/progressQueues/unitProgressSearch";
	public static final String CLIENT_FACING_QUEUE = "/view/progressQueues/clientFacingQueue";
	public static final String ACCEPTANCE_QUEUE = "/view/progressQueues/acceptanceQueue";
	public static final String MANUFACTURER_PROGRESS_QUEUE = "/view/progressQueues/manufacturerProgressQueue";
	public static final String UPFITTER_IN_SERVICE_QUEUE = "/view/progressQueues/upfitterInServiceQueue";
	public static final String ORDER_PROGRESS_SEARCH = "/view/progressQueues/orderProgressSearch";
	public static final String UNIT_UPFIT_NOTES = "/view/pricing/launchUnitUpfitNotes";
	public static final String PURCHASE_ORDER_RELEASE_QUEUE = "/view/progressQueues/purchaseOrderReleaseQueue";
	public static final String MAINTAIN_PURCHASE_ORDER = "/view/progressQueues/maintainPurchaseOrder";
	public static final String THIRD_PARTY_PROGRESS_QUEUE = "/view/progressQueues/thirdPartyProgressQueue";
	public static final String QUOTE_REQUEST_SEARCH = "/view/quoteRequest/quoteRequestSearch";
	public static final String QUOTE_REQUEST_ADD_EDIT = "/view/quoteRequest/quoteRequestAddEdit";

	public static final String QUOTE_DLR_ACCSRY = "/view/quote/maintainQuoteDealerAccessory";
	public static final String VEHICLE_ORDER_STATUS_SEARCH = "/view/progressQueues/vehicleOrderStatusSearch";
	public static final String CONFIGURATION_SEARCH = "/view/vehicleConfiguration/vehicleConfigurationSearch";
	public static final String VEHICLE_CONFIGURATION_ADD = "/view/vehicleConfiguration/vehicleConfigurationAddEdit";
	
	public static final String INVOICE_ENTRY = "/view/rentalCalc/invoiceEntry";
	public static final String POST_INVOICE	= "/view/rentalCalc/postInvoice";
	
	public static final String DEBIT_CREDIT_MEMO_ADD_EDIT = "/view/debitCreditMemos/debitCreditMemoAddEdit";
	public static final String SEARCH_DEBIT_CREDIT_MEMO = "/view/debitCreditMemos/debitCreditMemoSearch";
	public static final String ERROR_SEARCH_DEBIT_CREDIT_MEMO = "/view/debitCreditMemos/debitCreditMemoErrorSearch";
	public static final String DEBIT_CREDIT_MEMO_ERRORS = "/view/debitCreditMemos/debitCreditMemoErrors";
	public static final String DEBIT_CREDIT_MEMO_FORMAT_ERRORS = "/view/debitCreditMemos/debitCreditMemoFormatErrors";
	
	public static final String CONTRACT_MGMT_VEH_SEARCH = "/view/contractManagement/contractVehicleSearch";

	public static final String TASKS = "/view/maintSchedules/tasks";
	public static final String TASK_ADD_EDIT = "/view/maintSchedules/taskAddEdit";
	public static final String MASTER_SCHEDULES = "/view/maintSchedules/schedules";
	public static final String SCHEDULE_ADD_EDIT = "/view/maintSchedules/scheduleAddEdit";
	public static final String SCHEDULE_RULES = "/view/maintSchedules/scheduleRules";
	public static final String SCHEDULE_RULE_ADD_EDIT = "/view/maintSchedules/scheduleRuleAddEdit";
	public static final String SCHEDULE_REPORT = "/view/maintSchedules/report";
	public static final String SCHEDULE_QUEUES = "/view/maintSchedules/maintScheduleQueues";
	public static final String VIN_DETAILS = "/view/maintSchedules/vinDetails";		
	public static final String FUEL_TYPE_MAPPING = "/view/maintSchedules/fuelTypeMapping";
	public static final String MAINTAIN_SUPPLIER_DISCOUNTS = "/view/fleet/maintainSupplierDiscounts";
	public static final String UPFIT_ASSESSMENT = "/view/vehicleConfiguration/upfitAssessment";
	public static final String QUOTE_TITLING = "/view/quote/maintainQuoteTitlingInfo";
	public static final String WEB_USER_SEARCH = "/view/drivers/webUserSearch";
	public static final String WEB_USER_EDIT = "/view/drivers/webUserEdit";

	
	public static final String PAGE_LIST = "pageList";
	public static final String CLIENT_STATE = "clientState";
	public static final String PAGED_DATA = "pagedData";

	public static final String DRIVER_ID = "DriverId";
	public static final int    LOV_RECORD_SIZE = 500;
	public static final double MILE_TO_KM_CONVERSION_FACTOR = 1.61;
	public static final String DISPLAY_NAME_DRIVER_MODULE_GROUP = "Driver Mgmt";
	public static final String DISPLAY_NAME_ADMIN_MODULE_GROUP = "Admin";
	public static final String DISPLAY_NAME_RENTAL_CALCULATION_GROUP = "Rental Calculation";
	public static final String DISPLAY_NAME_FLEET_MAINT_GROUP = "Fleet Maintenance";
	public static final String DISPLAY_NAME_TRIM_MAINTENANCE_GROUP = "Trim Maintenance";
	public static final String DISPLAY_NAME_ORDER_TO_DELIVERY_GROUP = "Order To Delivery";
	public static final String DISPLAY_NAME_VEHICLE_CONFIGURATION_GROUP = "Vehicle Configuration";
	public static final String DISPLAY_NAME_QUOTE_REQUEST_GROUP = "Quote Request";
	public static final String DISPLAY_NAME_CONTRACT_MANAGEMENT_GROUP = "Contract Management";
	
	public static final String DISPLAY_NAME_DEBIT_CREDIT_MEMO_GROUP = "Debit/Credit Memo";

	public static final String DISPLAY_NAME_DRIVER_ADD = "Add Driver";
	public static final String DISPLAY_NAME_DRIVER_EDIT = "Edit Driver";
	public static final String DISPLAY_NAME_DRIVER_ALLOCATION = "Driver Allocation";
	public static final String DISPLAY_NAME_DRIVER_ALLOCATION_HISTORY = "Driver by Unit";
	public static final String DISPLAY_NAME_DRIVER_RELATIONSHIP = "Related Drivers";
	public static final String DISPLAY_NAME_DRIVER_SEARCH = "Search Drivers";
	public static final String DISPLAY_NAME_ADMIN_WORK_CLASS_PERMISSIONS = "Work Class Permissions";
	public static final String DISPLAY_NAME_CAPITAL_COST_OVERVIEW = "Capital Cost Overview";
	public static final String DISPLAY_NAME_QUOTE_OVERVIEW = "Quote Overview";
	public static final String DISPLAY_NAME_UNIT_RECONCILIATION = "Unit Reconciliation";
	public static final String DISPLAY_NAME_FLEET_MAINT_VEHICLE_SEARCH = "Search Vehicles";
	public static final String DISPLAY_NAME_FLEET_MAINT_SERVICE_HISTORY = "Service History";
	public static final String DISPLAY_NAME_FLEET_MAINT_CREATE_PO = "Create Maint. PO";
	public static final String DISPLAY_NAME_FLEET_MAINT_VIEW_PO = "View Maint. PO";
	public static final String DISPLAY_NAME_FLEET_MAINT_EDIT_PO = "Edit Maint. PO";
	public static final String DISPLAY_NAME_PROGRESS_CHASING = "View Progress Chasing";
//	public static final String DISPLAY_NAME_MODEL_SEARCH = "Search Trims";
	public static final String DISPLAY_NAME_TRIM_SEARCH = "Search Trims";
	public static final String DISPLAY_NAME_MODEL_PRICING = "Maintain Trim";
	public static final String DISPLAY_NAME_MAINT_UPFITTER_QUOTE = "Maintain Vendor's Quote";
//	public static final String DISPLAY_NAME_UPFIT_PROGRESS = "View Upfit Progress";
	public static final String DISPLAY_NAME_UNIT_PROGRESS_CHASING = "Unit Progress Chasing";
	public static final String DISPLAY_NAME_UPDATE_GRADE_GROUP = "Update Grade Group";//HPS-2045
	public static final String DISPLAY_NAME_UPDATE_PROJECTED_MONTHS = "Update Projected Replacement Month";//HD-3
	public static final String DISPLAY_NAME_MANUFACTURER_PROGRESS_QUEUE = "Manufacturer Progress Queue";
	public static final String DISPLAY_NAME_PURCHASE_ORDER_RELEASE_QUEUE = "Purchase Order Release Queue";
	public static final String DISPLAY_NAME_THIRD_PARTY_PROGRESS_QUEUE = "Third Party Progress Queue";
	public static final String DISPLAY_NAME_MAINTAIN_PURCHASE_ORDER = "Maintain Purchase Order";
	public static final String DISPLAY_NAME_UPFIT_PROGRESS_QUEUE = "Upfit Progress Queue";
	public static final String DISPLAY_NAME_IN_SERVICE_DATE_PROGRESS_QUEUE = "In Service Date Progress Queue";
	public static final String DISPLAY_NAME_CLIENT_FACING_QUEUE = "Client Facing Queue";
	public static final String DISPLAY_NAME_ACCEPTANCE_QUEUE = "Acceptance Queue";
	public static final String DISPLAY_NAME_ORDER_PROGRESS_SEARCH = "Order Progress Search";
	public static final String DISPLAY_NAME_UNIT_UPFIT_NOTES = "Upfit Notes";
	public static final String DISPLAY_NAME_MAINT_QUOTE_DLR_ACCSRY = "Maintain Dealer Accessories";
	public static final String DISPLAY_NAME_VEHICLE_ORDER_STATUS = "Vehicle Order Status";
	public static final String DISPLAY_NAME_CONFIGURATION_SEARCH = "Search Configuration";
	public static final String DISPLAY_NAME_VEHICLE_CONFIGURATION_ADD = "Add Configuration";
	public static final String DISPLAY_NAME_VEHICLE_CONFIGURATION_EDIT = "Edit Configuration";
	public static final String DISPLAY_NAME_MAINTAIN_MASTER_SCHEDULE = "Master Schedules";
	public static final String DISPLAY_NAME_VIN_DETAILS = "VIN Decoder Details";	

	public static final String DISPLAY_NAME_CLIENT_FINANCE = "Client Finance Parameters";
	public static final String DISPLAY_NAME_COST_CENTRE_FINANCE = "Cost Center Finance Parameters";
	public static final String DISPLAY_NAME_GRADE_GROUP_FINANCE = "Grade Group Finance Parameters";
	public static final String DISPLAY_NAME_CLIENT_POC = "Client Points of Communication";
	public static final String DISPLAY_NAME_CLIENT_CONTACTS = "Client Contacts";
	public static final String DISPLAY_NAME_CLIENT_POC_COST_CENTERS= "POC Cost Centers";
	public static final String DISPLAY_NAME_CLIENT_SERVICE_ELEMENTS = "Client Service Elements";
	public static final String DISPLAY_NAME_CLIENT_SERVICE_ELEMENT_DETAILS = "Client Service Element Details";
	public static final String DISPLAY_NAME_CLIENT_AGREEMENTS = "Client Agreements";
	public static final String DISPLAY_NAME_GRADE_GROUP_SERVICE_ELEMENTS = "Grade Group Service Elements";
	public static final String DISPLAY_NAME_GRADE_GROUP_SERVICE_ELEMENT_DETAILS = "Grade Group Service Element Details";
	public static final String DISPLAY_NAME_SERVICE_ELEMENT_AMENDMENTS = "Service Element Amendments";
	public static final String DISPLAY_NAME_SERVICE_ELEMENT_FORMAL_EXTENSION = "Service Element Formal Extensions";
	public static final String DISPLAY_NAME_SERVICE_ELEMENT_INFORMAL_AMENDMENTS = "Service Element Informal Amendments";
	
	public static final String DISPLAY_NAME_OER= "Open End Quote Revision";

	public static final String DISPLAY_NAME_PROVIDER_MAINT_CODE = "Provider Maint Codes";
	public static final String DISPLAY_NAME_ADD_PROVIDER_MAINT_CODE = "Add New Provider Maint Code";
	public static final String DISPLAY_NAME_INTERFACE_ERRORS = "Interface Errors";

	public static final String DISPLAY_NAME_QUOTE_REQUEST_SEARCH = "Search Quote Requests";
	public static final String DISPLAY_NAME_QUOTE_REQUEST_ADD = "Add Quote Request";
	public static final String DISPLAY_NAME_QUOTE_REQUEST_EDIT = "Edit Quote Request";
	
	public static final String DISPLAY_NAME_ADD_DEBIT_CREDIT_MEMO = "Add Debit/Credit Memo";
	public static final String DISPLAY_NAME_EDIT_DEBIT_CREDIT_MEMO = "Edit Debit/Credit Memo";
	public static final String DISPLAY_NAME_EDIT_DEBIT_CREDIT_BUSINESS_ERROR = "Edit Business Validation Error";	
	public static final String DISPLAY_NAME_SEARCH_DEBIT_CREDIT_MEMO = "Search Debit/Credit Memo";	
	public static final String DISPLAY_NAME_ERROR_SEARCH_DEBIT_CREDIT_MEMO = "Business Validation Errors";		
	public static final String DISPLAY_NAME_DEBIT_CREDIT_MEMO_ERRORS = "Debit/Credit Upload Errors";
	public static final String DISPLAY_NAME_DEBIT_CREDIT_MEMO_FORMAT_ERRORS = "Edit Format Errors";
	
	public static final String DISPLAY_NAME_CONTRACT_MGMT_VEH_SEARCH = "Search Contract Vehicles";
	public static final String DISPLAY_NAME_SEARCH_WEB_USERS = "Search Web Users";
	public static final String DISPLAY_NAME_EDIT_WEB_USER = "Edit Web User";
	
	public static final String VIEW_PARAM_DRIVER_ALLOCATION = "driverAllocation";
	public static final String VIEW_PARAM_UNIT_NO = "unitNo";
	public static final String VIEW_PARAM_DRIVER_ID = "driverId";
	public static final String VIEW_PARAM_MODE = "mode";
	public static final String VIEW_PARAM_DRIVER_NAME = "driverName";
	public static final String VIEW_PARAM_CALLERS_NAME = "callersName";
	public static final String VIEW_PARAM_DRIVER_SEARCH_REQ = "isSearchRequired";
	public static final String VIEW_PARAM_WORK_CLASS = "workClass";
	public static final String VIEW_PARAM_QUOTE_ID = "quoteId";
	public static final String VIEW_PARAM_QUOTE_MODEL_ID = "qmdId";
	public static final String VIEW_REV_PARAM_QUOTE_MODEL_ID = "revQmdId";
	public static final String VIEW_PARAM_FORWARDED_PAGE = "FORWARDED_PAGE";
	public static final String VIEW_PARAM_PREVIOUS_PAGE = "PREVIOUS_PAGE";
	public static final String VIEW_PARAM_DEPRECIATION_FACTOR = "depreciationFactor";
	public static final String VIEW_PARAM_FMS_ID = "fmsId";
	public static final String VIEW_PARAM_MRQ_ID = "mrqId";
	public static final String VIEW_PARAM_VIN = "vin";
	public static final String VIEW_SELECTED_SEARCH_VO = "selectedSearchVO";
	public static final String VIEW_PARAM_MAINT_CATEGORY = "maintCategory";
	public static final String VIEW_PARAM_PO_STATUS = "poStatus";
	public static final String VIEW_PARAM_INDEX = "index";
	public static final String VIEW_PARAM_MDL_ID = "mdlId";
	public static final String VIEW_PARAM_MDL_IDS = "mdlIds";
	public static final String VIEW_PARAM_C_ID = "cId";
	public static final String VIEW_PARAM_ACCOUNT_TYPE = "accountType";
	public static final String VIEW_PARAM_ACCOUNT_CODE = "accountCode";
	public static final String VIEW_PARAM_CLIENT_FINANCE_VO = "clientFinanceVO";
	public static final String VIEW_PARAM_POINT_OF_COMMUNICATION_VO = "pocVO";
	public static final String VIEW_PARAM_CANCEL = "cancel";
	public static final String DIALOG_ID_GL_CODES = "glCodes";
	public static final String VIEW_PARAM_CPNT_ID = "cpntId";
	public static final String VIEW_PARAM_CPNTA_ID = "cpntaId";
	public static final String VIEW_PARAM_COST_CENTER_CODE = "costCenterCode";
	public static final String VIEW_PARAM_CLIENT_SYSTEM_FILTER = "clientSystem";
	public static final String VIEW_PARAM_DEFAULT_POC_VO = "defaultPOCVO";
	public static final String VIEW_PARAM_ORIGIN = "origin";
	public static final String VIEW_PARAM_DOC_ID = "docId";
	public static final String VIEW_PARAM_SCHEDULE_ID = "scheduleId";
	public static final String VIEW_PARAM_TASK_ID = "taskId";
	public static final String VIEW_PARAM_COPY_SCHEDULE = "copySchedule";
	public static final String VIEW_PARAM_SCHEDULE_RULE_ID = "scheduleRuleId";
	public static final String VIEW_PARAM_VEHICLE_CONFIG_ID = "vehicleConfigId";
	public static final String VIEW_PARAM_QUOTE_REQUEST_ID = "quoteRequestId";	
	public static final String VIEW_PARAM_QUOTATION_PROFILE_DESC = "quotationProfileDesc";
	public static final String VIEW_PARAM_QUOTATION_PROFILE_VARIABLE_RATE = "variableRate";
	public static final String VIEW_PARAM_QUOTATION_PROFILE_INTEREST_TYPE = "interestType";
	public static final String VIEW_PARAM_QUOTATION_PROFILE_PRODUCT_CODE = "productCode";
	public static final String CONTRACT_CHANGE_PERIOD = "contractChangeEventPeriod"; 
	
	public static final String VIEW_PARAM_DC_MEMO_DC_NO = "debitCreditMemoTransactionId";
	public static final String VIEW_PARAM_DC_MEMO_ERROR_JMS_ID = "debitCreditMemoErrorJmsId";
	

	public static final int LEFT_MENU_PANEL_WIDTH = 275;
	public static final int SCREEN_RESOLUTION_MINIMUM_WIDTH= 500;
	public static final int SCREEN_RESOLUTION_MINIMUM_HEIGHT= 500;
	public static final String SCREEN_RESOLUTION_WIDTH= "SCREEN_RESOLUTION_WIDTH";
	public static final String SCREEN_RESOLUTION_HEIGHT= "SCREEN_RESOLUTION_HEIGHT";
	public static final String PANEL_MIN_WIDTH = "750px";
	public static final String BREADCRUMB_MIN_WIDTH = "740px";
	public 	static 	final	int	RECORDS_PER_PAGE =  500;
	public 	static 	final	int	RECORDS_PER_PAGE_MEDIUM =  250;
	public 	static 	final	int	RECORDS_PER_PAGE_SMALL =  50;
	public 	static 	final	int	RECORDS_PER_PAGE_X_SMALL =  25;
	public static final int DATATABLE_MIN_WIDTH = 750;

	//TODO Figure out a better way to manage the different modes for a multipurpose view
	public static final String VIEW_MODE = "mode";
	public static final String VIEW_MODE_READ = "Read";
	public static final String VIEW_MODE_ADD = "Add";
	public static final String VIEW_MODE_EDIT = "Edit";
	public static final String VIEW_MODE_FINALIZE = "Finalize";


	//TODO Is this a suitable place for this?
	public static final String USER_READ_ONLY_ROLE = "READONLY";

	public static final String DEFAULT_DRIVER_STATUS = "New Driver";
	public static final String VIEW_PARAM_INCLUDE_TEMP_EQUIPMENT = "VIEW_PARAM_INCLUDE_TEMP_EQUIPMENT";

	public static final String BUTTON_INDICATOR_STYLE_CLASS = "mafs-button-indicator"; 	

	public static final String VIEW_PARAM_RELEASED_PO_DOC_ID = "VIEW_PARAM_RELEASED_PO_DOC_ID";
	public static final String VIEW_PARAM_RELEASED_PO_DOC_NO = "VIEW_PARAM_RELEASED_PO_DOC_NO";
	public static final String VIEW_PARAM_INVOICE_HEADER_DOC_NO = "VIEW_PARAM_INVOICE_HEADER_DOC_NO";
	public static final String VIEW_PARAM_INVOICE_HEADER_DOC_ID = "VIEW_PARAM_INVOICE_HEADER_DOC_ID";
	public static final String VIEW_PARAM_INV_HEADER_PROCESS = "VIEW_PARAM_INV_HEADER_PROCESS";

	public static final String ADDRESS_TYPE_POST = "POST";
	public static final String ADDRESS_TYPE_GARAGED = "GARAGED";


	public static final String LABEL_PROGRESS_CHASING = "Progress Chasing Notes";
	
	//data table prop,  used to pass from one page to another
	public static final String DT_DEFAULT_SORT_VE = "DEFAULT_SORT_VE";
	public static final String DT_SELECTD_PAGE_START_INDEX = "SELECTD_PAGE_START_INDEX";
	public static final String DT_DEFAULT_SORT_ORDER = "DEFAULT_SORT_ORDER";
	public static final String DT_SELECTED_ITEM = "SELECTED_ITEM";
	public static final String HTML_BULLET_CODE = "&#8226;&#160;";
	public static final String HTML_BREAK_LINE = "<br/>";
	public static final String PIPE_SEPARATOR = "||";
	


}