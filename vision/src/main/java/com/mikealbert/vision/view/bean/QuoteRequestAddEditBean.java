package com.mikealbert.vision.view.bean;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;

import org.apache.commons.lang.SerializationUtils;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import com.mikealbert.data.beanvalidation.MANotNull;
import com.mikealbert.data.dao.ContactDAO;
import com.mikealbert.data.dao.ContactInformationDAO;
import com.mikealbert.data.dao.DocDAO;
import com.mikealbert.data.dao.ExtAccFinFanDAO;
import com.mikealbert.data.dao.OrderTypeDAO;
import com.mikealbert.data.dao.ProductDAO;
import com.mikealbert.data.dao.QuoteRequestConfigurationDAO;
import com.mikealbert.data.dao.SupplierDAO;
import com.mikealbert.data.entity.Contact;
import com.mikealbert.data.entity.ContactAddress;
import com.mikealbert.data.entity.Doc;
import com.mikealbert.data.entity.Driver;
import com.mikealbert.data.entity.DriverGradeGroupCode;
import com.mikealbert.data.entity.ExtAccFinFan;
import com.mikealbert.data.entity.ExternalAccount;
import com.mikealbert.data.entity.ExternalAccountPK;
import com.mikealbert.data.entity.ObjectLogBook;
import com.mikealbert.data.entity.OnbaseUploadedDocs;
import com.mikealbert.data.entity.PhoneNumber;
import com.mikealbert.data.entity.PlateTypeCode;
import com.mikealbert.data.entity.Product;
import com.mikealbert.data.entity.QuotationDealerAccessory;
import com.mikealbert.data.entity.QuotationModel;
import com.mikealbert.data.entity.QuotationProfile;
import com.mikealbert.data.entity.QuoteRequest;
import com.mikealbert.data.entity.QuoteRequestCloseReason;
import com.mikealbert.data.entity.QuoteRequestConfiguration;
import com.mikealbert.data.entity.QuoteRequestDepreciationMethod;
import com.mikealbert.data.entity.QuoteRequestPaymentType;
import com.mikealbert.data.entity.QuoteRequestQuote;
import com.mikealbert.data.entity.QuoteRequestType;
import com.mikealbert.data.entity.QuoteRequestVehicle;
import com.mikealbert.data.entity.VehicleConfigGrouping;
import com.mikealbert.data.entity.VehicleConfigUpfitQuote;
import com.mikealbert.data.entity.VehicleConfiguration;
import com.mikealbert.data.entity.VehicleDeliveryChargeType;
import com.mikealbert.data.enumeration.DepreciationMethodEnum;
import com.mikealbert.data.enumeration.LogBookTypeEnum;
import com.mikealbert.data.enumeration.QuoteRequestStatusEnum;
import com.mikealbert.data.enumeration.QuoteRequestTypeEnum;
import com.mikealbert.data.vo.DocumentFileVO;
import com.mikealbert.data.vo.DriverInfoVO;
import com.mikealbert.data.vo.QuoteRequestQuoteModelVO;
import com.mikealbert.data.vo.QuoteRequestQuoteVO;
import com.mikealbert.data.vo.QuoteRequestVO;
import com.mikealbert.data.vo.VehicleOrderStatusSearchCriteriaVO;
import com.mikealbert.data.vo.VendorLovVO;
import com.mikealbert.data.vo.WillowUserLovVO;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.exception.MalException;
import com.mikealbert.service.CustomerAccountService;
import com.mikealbert.service.DriverService;
import com.mikealbert.service.FleetMasterService;
import com.mikealbert.service.LogBookService;
import com.mikealbert.service.OnbaseArchivalService;
import com.mikealbert.service.OnbaseRetrievalService;
import com.mikealbert.service.PlateTypeCodeService;
import com.mikealbert.service.QuotationService;
import com.mikealbert.service.QuoteRequestSearchService;
import com.mikealbert.service.QuoteRequestService;
import com.mikealbert.service.VehicleConfigurationService;
import com.mikealbert.service.enumeration.OnbaseDocTypeEnum;
import com.mikealbert.service.enumeration.OnbaseIndexEnum;
import com.mikealbert.service.vo.OnbaseKeywordVO;
import com.mikealbert.data.vo.QuotationStepStructureVO;
import com.mikealbert.service.vo.StockUnitVO;
import com.mikealbert.util.MALUtilities;
import com.mikealbert.vision.service.OrderProgressService;
import com.mikealbert.vision.service.QuotingEmailService;
import com.mikealbert.vision.service.UnitProgressService;
import com.mikealbert.vision.service.VendorService;
import com.mikealbert.vision.view.ViewConstants;
import com.mikealbert.vision.vo.ContactInfo;
import com.mikealbert.vision.vo.LogBookTypeVO;
import com.mikealbert.vision.vo.QuoteRequestConfigVO;


@Component
@Scope("view")
public class QuoteRequestAddEditBean extends StatefulBaseBean {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -781387858243583738L;

	private static final String CLIENT_ACCOUNT_UI_ID = "clientId";
	private static final String REQUEST_TYPE_UI_ID = "requestTypeId";
	private static final String VEHICLE_DESC_UI_ID = "accordionPanel:vehicleDescId";
	private static final String DRIVER_INFO_UI_ID = "accordionPanel:driverName";
	private static final String VEHICLE_PLATE_TYPE_UI_ID = "accordionPanel:plateTypeId"; 
	private static final String VEHICLE_DELIVERY_CHARGE_TYPE_UI_ID = "accordionPanel:deliveryChargeId"; 	
	private static final String FIRST_EXTERIOR_COLOR_UI_ID = "firstExteriorColorId";
	private static final String DRIVER_UI_ID = "accordionPanel:driverName";	
	private static final String DRIVER_ZIP_CODE_UI_ID = "accordionPanel:zipCodeId";
	private static final String QUOTE_PROFILE_UI_ID = "accordionPanel:quoteProfileId"; 
    private static final String QUOTE_LEASE_TERM_UI_ID = "accordionPanel:leaseTermId";
    private static final String QUOTE_PROJ_MONTH_UI_ID = "accordionPanel:projReplId";
    private static final String QUOTE_LEASE_MILES_UI_ID = "accordionPanel:leaseMileId";	    
    private static final String QUOTE_DEPR_METHOD_UI_ID = "accordionPanel:deprMethodId"; 
    private static final String QUOTE_DRIVER_GRADE_UI_ID = "accordionPanel:gradeGroupId";
    private static final String DEPRECIATION_METHOD_VALUE_UI_ID = "accordionPanel:deprValueId";
    private static final String QUOTE_CLOSE_REASON_UI_ID = "closeReasonMnu";
    private static final String QUOTE_CLOSE_REASON_NOTE_UI_ID = "closeReasonNoteTA";
    private static final String QUOTE_ASSIGN_USER_UI_ID = "assignedToId";
    private static final String QUOTE_ASSIGN_QUOTE_UI_ID = "accordionPanel:quoteNumberId";
    private static final String DEALER_INPUT_UI_ID =	"dealerCodeId";
    private static final String DEALER_CONTACT_UI_ID =	"contactNameId";
    private static final String DEALER_PHONE_UI_ID =	"contactPhoneId";
    private static final String CONTACT_NAME_UI_ID = "clientContactNameId";
    private static final String CONTACT_PHONE_UI_ID = "clientContactPhoneId";
    private static final String CONTACT_EMAIL_UI_ID = "clientContactEmailId";
    private static final String CONTACT_DRIVER_UI_ID = "clientContactDriverId";
    private static final String REWORK_REASON_UI_ID = "reworkRequestReasonTA";
    private static final String UPFIT_NOTE_UI_ID = "upfitNote";
    private static final String APPLICATION_NAME_UI_ID = "applicationNameId";
    private static int MAX_STEPS = 5;
    private static int DEFAULT_STEP_SIZE = 12;
    
    private static String NOTES_MESSAGE = "Quote Request has notes";
    private static String NOTES_FOLLOW_UP_DATE_MESSAGE = "Quote Request has a note that requires attention";        
	
	@Resource QuoteRequestSearchService quoteRequestSearchService;
	@Resource QuoteRequestService quoteRequestService;
	@Resource CustomerAccountService externalAccountService;
	@Resource PlateTypeCodeService plateTypeCodeService;
	@Resource QuotationService quotationService;
	@Resource DriverService driverService;	
	@Resource ProductDAO productDAO;
	@Resource LogBookService logBookService;
	@Resource ExtAccFinFanDAO extAccFinFanDAO;
	@Resource FleetMasterService fleetMasterService;
	@Resource OnbaseArchivalService onbaseArchivalService;
	@Resource UnitProgressService unitProgressService;
	@Resource VendorService vendorService;
	@Resource SupplierDAO supplierDAO;
	@Resource OnbaseRetrievalService onbaseRetrievalService;
	@Resource OrderProgressService orderProgressService;
	@Resource DocDAO docDAO;
	@Resource QuotingEmailService quotingEmailService;
	@Resource ContactDAO contactDAO;
	@Resource ContactInformationDAO contactInformationDAO;
	@Resource VehicleConfigurationService vehicleConfigurationService;
	@Resource OrderTypeDAO orderTypeDAO;
	@Resource QuoteRequestConfigurationDAO quoteRequestConfigurationDAO;
	
	private List<QuoteRequestType> quoteRequestTypeList = null;
	private List<VehicleDeliveryChargeType> vehicleDeliveryChargeTypeList = null;
	private List<PlateTypeCode> plateTypeCodeList = null;
	private ExternalAccount selectedExternalAccount;
	private QuoteRequestVehicle quoteRequestVehicle = new QuoteRequestVehicle();
	private QuoteRequestVO quoteRequest;
	private QuoteRequestQuote quoteRequestQuote = new QuoteRequestQuote();
	
	private DriverInfoVO selectedDriver;
	private String driverZipCode;
	private String driverDeliveryAddress;
	
	private List<QuotationProfile> quotationProfiles;
	private List<QuoteRequestPaymentType> paymentTypes;
	private List<QuoteRequestDepreciationMethod> depreciationMethods;
	private List<DriverGradeGroupCode> driverGradeGroups = new ArrayList<DriverGradeGroupCode>();
	private List<QuotationStepStructureVO> definitionSteps;
	private List<QuoteRequestQuoteModelVO> quoteRequestQuoteModels;
	private QuoteRequestQuoteModelVO selectedQuoteRequestQuoteModel;
	private String splMsgforStep;
	private String assignedUser;
	private Boolean isQuoteRequestInProgress;
	private String assignedWillowUser;
	
	private String client = null;
	@MANotNull(label = "Client")
	private String customerLOVParam;
	private String viewMode;
	private StockUnitVO stockUnit;
	private long selectedStockUnitFmsId;
	private List<LogBookTypeVO> logBookType = new ArrayList<LogBookTypeVO>();
	private List<QuoteRequestCloseReason> quoteRequestCloseReasons;	
	private QuoteRequestCloseReason selectedQuoteRequestCloseReason;
	private String selectedQuoteRequestCloseReasonNote;
	private List<String> standardEquipment;
	private List<String> modelEquipment;
	private List<String> dealerEquipment;
	private boolean started=false;
	private boolean startButtonDisabled = true;
	private String driverNameInput;
	private boolean displayDriverPanel;
	private String drvUnitFactoryOptionalEquipments;
	private List<String> drvUnitStandardEquipments;
	private List<String> drvUnitDealerAccessories;

	private List<DocumentFileVO> documentFileVOs = new ArrayList<DocumentFileVO>();	
	private DocumentFileVO selectedDocumentFileVO;
	private StreamedContent selectedDocumentToView;

	boolean validDealer = false;
	boolean newDriverAdded = false;
	
	String emailErrorMessage;
	private boolean justFlippedToReworked;

	private String selectedAssignToEmployeeNo;
	private boolean mfrIncentive;
	private String clientContactName;
	private String clientContactPhone;
	private String clientContactEmail;
	private Long selectedClientContactId;
	private boolean clientContactDriver;
	
	private String reworkReason;
	private List<QuoteRequestConfigVO> vehicleConfigs;
	private String applicationName;
	private boolean isValid;
	
	@PostConstruct
	public void init() {
		openPage();	
		loadData();
		initializeLogBook();
		initializeDocuments();
		
	}
		
	private void loadData() {		
		setQuoteRequestTypeList(quoteRequestSearchService.getAllRequestType());
		setVehicleDeliveryChargeTypeList(quoteRequestService.getAllVehicleDeliveryChargeTypes());
		setPlateTypeCodeList(plateTypeCodeService.getPlateTypeCodeList());
		setPaymentTypes(quoteRequestService.getAllQuoteRequestPaymentTypes());
		setDepreciationMethods(quoteRequestService.getAllQuoteRequestDepreciationMethods());
		setQuoteRequestCloseReasons(quoteRequestService.getQuoteRequestCloseReasons());
		
		if(getViewMode().equals(ViewConstants.VIEW_MODE_EDIT) || getViewMode().equals(ViewConstants.VIEW_MODE_READ) || started) {
			populateQuotationProfiles();
			populateGradeGroups();
			started=true;
		}		
	}
	

	public void clearSelectedDriver() {
		selectedDriver = new DriverInfoVO();
		driverZipCode = null;
		displayDriverPanel = false;
		populateQuoteSection();
	}
	
	public void populateDriversSection(){
		if (!MALUtilities.isEmpty(getSelectedDriver()) && !MALUtilities.isEmpty(getSelectedDriver().getDrvId())) {
			List<DriverInfoVO> list = driverService.searchDriverInfo(null, selectedDriver.getDrvId(), selectedDriver.getFmsId(), selectedExternalAccount.getExternalAccountPK(), new PageRequest(0,20), null);
			setSelectedDriver(list.get(0));
			driverNameInput = getSelectedDriver().getFullName();
			displayDriverPanel = true;
			populateQuoteSection();
		}
	}
	
	public void populateQuoteSection() {
		getQuoteRequestQuote().setLeaseTerm(null);
		getQuoteRequestQuote().setLeaseMiles(null);
		getQuoteRequestQuote().setProjectedReplaceMonth(null);
		getQuoteRequestQuote().setQuotationProfile(null);
		getQuoteRequestQuote().setDriverGradeGroupCode(null);
		if (getDriverGradeGroups().size() == 1) {
			getQuoteRequestQuote().setDriverGradeGroupCode(getDriverGradeGroups().get(0));
		} else {
			getQuoteRequestQuote().setDriverGradeGroupCode(null);
		}
		
		if (!MALUtilities.isEmpty(getSelectedDriver()) && !MALUtilities.isEmpty(getSelectedDriver().getDrvId()) && !MALUtilities.isEmpty(getDriverNameInput())) {
			if (!MALUtilities.isEmpty(getSelectedDriver().getQmdId())) {
				String termMile = getSelectedDriver().getTerm();
				String[] termMileArr = termMile.split("/");
				getQuoteRequestQuote().setLeaseTerm(Long.parseLong(termMileArr[0]));
				getQuoteRequestQuote().setLeaseMiles(Long.parseLong(termMileArr[1]));

				try {
					QuotationModel qmd = quotationService.getQuotationModel(getSelectedDriver().getQmdId());
					getQuoteRequestQuote().setQuotationProfile(qmd.getQuotation().getQuotationProfile());
					getQuoteRequestQuote().setProjectedReplaceMonth(qmd.getProjectedMonths());
				} catch (MalBusinessException e) {
					handleException("generic.error.occured.while", new String[] { "retrieving Profile" }, e, "populateQuoteSection");
				}
			}
			DriverGradeGroupCode drvGradeGrpCode = driverService.getDriver(getSelectedDriver().getDrvId()).getDgdGradeCode().getGradeGroup();
			getQuoteRequestQuote().setDriverGradeGroupCode(drvGradeGrpCode);
		}
	}
	
	public void populateQuoteModels() {
		setQuoteRequestQuoteModels(quoteRequestService.getQuoteRequestQuoteModels(getSelectedExternalAccount().getExternalAccountPK(), null));
	}

	private void populateVehicleConfigs() {
		vehicleConfigs = new ArrayList<QuoteRequestConfigVO>();
		List<VehicleConfiguration> vehicleConfigurations = 
				vehicleConfigurationService.getActiveVehicleConfigurationsByAccount(getSelectedExternalAccount().getExternalAccountPK());

		List<QuoteRequestConfiguration> quoteRequestConfigurations =  new ArrayList<QuoteRequestConfiguration>();

		if(getQuoteRequest().getQuoteRequest().getQrqId() != null	) {
			quoteRequestConfigurations = quoteRequestConfigurationDAO.getQuoteRequestConfigurationsByQuoteRequest(getQuoteRequest().getQuoteRequest().getQrqId()); 
		} else {
			getQuoteRequest().getQuoteRequest().setQuoteRequestConfigurations(quoteRequestConfigurations);
		}
		
		
		for(VehicleConfiguration vc: vehicleConfigurations) {
			QuoteRequestConfigVO qrcVO = new QuoteRequestConfigVO();
			qrcVO.setVehicleConfiguration(vc);
			qrcVO.setSelected(false);
			for(QuoteRequestConfiguration qrc : quoteRequestConfigurations) {
				if(qrc.getVehicleConfiguration().getVcfId().compareTo(vc.getVcfId()) == 0) {
					qrcVO.setSelected(true);
					break;
				}
			}
			vehicleConfigs.add(qrcVO);
		}
	
	
	}

	
	public void onRowSelect() {
		getQuoteRequestQuote().setQuoId(getSelectedQuoteRequestQuoteModel().getQuoId());
	}
	
	public void decodeWillowQuote() {
		if (!MALUtilities.isEmpty(getQuoteRequestQuote().getQuoId())) {
			List<QuoteRequestQuoteModelVO> quoteModelList = quoteRequestService.getQuoteRequestQuoteModels(getSelectedExternalAccount().getExternalAccountPK(), getQuoteRequestQuote().getQuoId());
			
			if(quoteModelList.size() == 1){					 
				setSelectedQuoteRequestQuoteModel(quoteModelList.get(0));
				getQuoteRequestQuote().setQuoId(getSelectedQuoteRequestQuoteModel().getQuoId());
			 }else {
				 setSelectedQuoteRequestQuoteModel(null);
				 if(quoteModelList.size() == 0){
					 super.addErrorMessage(QUOTE_ASSIGN_QUOTE_UI_ID, "decode.noMatchFound.msg","Quote " + getQuoteRequestQuote().getQuoId()); 
				 }else{
					 super.addErrorMessage(QUOTE_ASSIGN_QUOTE_UI_ID, "decode.notExactMatch.msg","Quote " +  getQuoteRequestQuote().getQuoId());			
				 }
			 }
		} else {
			setSelectedQuoteRequestQuoteModel(null);
		}
	}
	
	public void onQuoteProfileChange() {
		getQuoteRequestQuote().setQuoteRequestDepreciationMethod(null);
		getQuoteRequestQuote().setDepreciationMethodValue(null);
	}
	
	public void navigateToAddEditDriver(){
    	saveRestoreStateValues(getCurrentPageRestoreStateValuesMap());
    	
		Map<String, Object> nextPageValues = new HashMap<String, Object>();
		saveNextPageInitStateValues(nextPageValues);
		forwardToURL(ViewConstants.DRIVER_ADD);
    }
	
	public void decodeAccountByNameOrCode(){
		
		String paramName = (String) getRequestParameter("CUSTOMER_LOV_INPUT");//This code needed when users select through LOV and need refresh
		if (!MALUtilities.isEmptyString(paramName)) {
			customerLOVParam = (String) getRequestParameter(paramName);
		}
		
		 List<ExternalAccount>  externalAccountList =  externalAccountService.findAllCustomerAccountsByNameOrCode(customerLOVParam, getLoggedInUser().getCorporateEntity(),new PageRequest(0,50));
		 if(externalAccountList.size() == 1){					 
			
			 selectedExternalAccount  =  externalAccountList.get(0);
			 customerLOVParam = selectedExternalAccount.getExternalAccountPK().getAccountCode();

			 populateClientDetails();
			 populateQuotationProfiles();
			 populateGradeGroups();
			 populateQuoteSection();
			 
		 }else {
			 if(externalAccountList.size() == 0){
				 super.addErrorMessage(CLIENT_ACCOUNT_UI_ID, "decode.noMatchFound.msg","Client " + customerLOVParam); 
			 }else{
				 super.addErrorMessage(CLIENT_ACCOUNT_UI_ID, "decode.notExactMatch.msg","Client " +  customerLOVParam);			
			 }
			 this.setSelectedExternalAccount(new ExternalAccount());
			 this.selectedExternalAccount.setExternalAccountPK(new ExternalAccountPK());
		 }
		 
		 getQuoteRequest().getQuoteRequest().setClientAccount(this.selectedExternalAccount);
	}
	
	public void validateCustomClient(FacesContext context, UIComponent inputComponent, Object value) {
		if (MALUtilities.isEmpty(value)){
			super.addErrorMessage(CLIENT_ACCOUNT_UI_ID, "required.field",	"Client Account ");
			this.setSelectedExternalAccount(new ExternalAccount());
			this.selectedExternalAccount.setExternalAccountPK(new ExternalAccountPK());
		}
	}
	
	public void populateClientDetails() {
		if (!MALUtilities.isEmpty(getSelectedExternalAccount())) {
			this.quoteRequest = quoteRequestService.getClientDetails(getQuoteRequest(), selectedExternalAccount.getExternalAccountPK());
			getQuoteRequest().getQuoteRequest().setClientAccount(getQuoteRequest().getClient());
		}
		
		if (!MALUtilities.isEmpty(this.quoteRequest.getClientCreditLimits().getError())) {
			super.addInfoMessage("custom.message", "Unable to retrieve Credit Info: " + this.quoteRequest.getClientCreditLimits().getError());
		}
		
		setQuoteRequestQuote(new QuoteRequestQuote());
		setSelectedDriver(new DriverInfoVO());
		populateQuotationProfiles();
		populateVehicleConfigs();
	}
	
	public void populateDetailsForSelectedClient() {
		// coming from LOV - getting the entity into Hibernate so later save requests on other mapped entities will not fail because this is missing
		selectedExternalAccount = externalAccountService.getCustomerAccount(getLoggedInUser().getCorporateEntity(),
				selectedExternalAccount.getExternalAccountPK().getAccountType(), selectedExternalAccount.getExternalAccountPK().getAccountCode());
		populateClientDetails();
		populateGradeGroups();
	}	
	
	public void populateStockUnitDetails() {
		stockUnit = quoteRequestService.getStockUnitInfo(selectedStockUnitFmsId);
		if(selectedExternalAccount != null && stockUnit != null) {
			ExtAccFinFan extAccFinFan = extAccFinFanDAO.findByAccountAndMake(selectedExternalAccount.getExternalAccountPK().getcId(), 
					selectedExternalAccount.getExternalAccountPK().getAccountType(), 
					selectedExternalAccount.getExternalAccountPK().getAccountCode(), 
					stockUnit.getFleetMaster().getModel().getMake().getMakId());			
			if(extAccFinFan != null) {
				stockUnit.setFleetCode(extAccFinFan.getFinFanNumber());
			} else {
				super.addInfoMessage("custom.message", "Client does not have a fleet code for the selected manufacturer.  Request cannot be submitted.");
			}
			stockUnit.setActiveQuoteList(quotationService.getAllActiveQuotesByFmsId(stockUnit.getFleetMaster().getFmsId()));
			getQuoteRequestVehicle().setFleetmaster(stockUnit.getFleetMaster());
		}
		

	}
	
	
	public void populateStockUnitEquipment() {
		if(stockUnit != null) {
			standardEquipment = fleetMasterService.getStandardEquipmentForFmsId(selectedStockUnitFmsId);
			modelEquipment = fleetMasterService.getModelEquipmentForFmsId(selectedStockUnitFmsId);
			dealerEquipment = fleetMasterService.getDealerEquipmentForFmsId(selectedStockUnitFmsId);
		}
	}
	
	public void populateQuotationProfiles() {
		if (!MALUtilities.isEmpty(getSelectedExternalAccount())) {
			setQuotationProfiles(quoteRequestService.getQuotationProfiles(getSelectedExternalAccount().getExternalAccountPK().getcId(), getSelectedExternalAccount().getExternalAccountPK().getAccountType(), getSelectedExternalAccount().getExternalAccountPK().getAccountCode()));
		}
	}
	
	public void populateGradeGroups() {
		if (!MALUtilities.isEmpty(getSelectedExternalAccount())) {
			setDriverGradeGroups(quoteRequestService.getGradeGroupsByAccount(getSelectedExternalAccount().getExternalAccountPK().getcId(), getSelectedExternalAccount().getExternalAccountPK().getAccountType(), getSelectedExternalAccount().getExternalAccountPK().getAccountCode()));
			if(getDriverGradeGroups() != null && getDriverGradeGroups().size() == 1){
				getQuoteRequestQuote().setDriverGradeGroupCode(getDriverGradeGroups().get(0));
			}
		}
	}
	
	public void onEditStepsDefinition() {
		definitionSteps = new ArrayList<QuotationStepStructureVO>();

		Long term = quoteRequestQuote.getLeaseTerm();
		if (!MALUtilities.isEmpty(getQuoteRequestQuote().getStep1()) && getQuoteRequestQuote().getStep1()>0) {
			for (int i = 1; i <= MAX_STEPS; i++) {
				QuotationStepStructureVO stepStructureVO = new QuotationStepStructureVO();
				stepStructureVO.setStepCount(i);
				try {
					stepStructureVO.setNetPeriod((Long) (QuoteRequestQuote.class.getMethod("getStep"+i).invoke(getQuoteRequestQuote())));
					definitionSteps.add(stepStructureVO);
				} catch (Exception e) {
					super.addErrorMessageSummary("quote.openend.step.define.error");
					RequestContext context = RequestContext.getCurrentInstance();
					context.addCallbackParam("failure", true);
				}
			}
		} else {
			for (int i = 1; i <= MAX_STEPS; i++) {
				QuotationStepStructureVO stepStructureVO = new QuotationStepStructureVO();
				stepStructureVO.setStepCount(i);
				if (term >= DEFAULT_STEP_SIZE && i != MAX_STEPS) {
					stepStructureVO.setNetPeriod(Long.valueOf(DEFAULT_STEP_SIZE));
				} else if (term > 0) {
					stepStructureVO.setNetPeriod(term);
				}
				definitionSteps.add(stepStructureVO);
				term = term - DEFAULT_STEP_SIZE;
			}
		}
	}
	
	public void onEditStepsDone() {
		if (isStepsAreValid()) {
			long start = 1;
			int i = 1;
			@SuppressWarnings("rawtypes")
			Class[] cArg = new Class[1];
	        cArg[0] = Long.class;
			for (QuotationStepStructureVO qssVO : definitionSteps) {
				if (qssVO.getNetPeriod() != null && qssVO.getNetPeriod() > 0) {
					qssVO.setFromPeriod(start);
					qssVO.setToPeriod(start + qssVO.getNetPeriod() - 1);
					try {
						QuoteRequestQuote.class.getMethod("setStep"+i, cArg).invoke(getQuoteRequestQuote(), qssVO.getNetPeriod());
						i = i + 1;
					} catch (Exception e) {
						RequestContext context = RequestContext.getCurrentInstance();
						context.addCallbackParam("failure", true);
					}
				}
			}
		} else {
			RequestContext context = RequestContext.getCurrentInstance();
			context.addCallbackParam("failure", true);
		}
	}
	
	private boolean isStepsAreValid() {
		long totalStepPeriod = 0;
		for (QuotationStepStructureVO qssVO : definitionSteps) {
			if (qssVO.getNetPeriod() != null)
				totalStepPeriod = totalStepPeriod + qssVO.getNetPeriod();
		}

		if (totalStepPeriod != getQuoteRequestQuote().getLeaseTerm()) {
			super.addErrorMessageSummary("quote.openend.step.sum.error");
			return false;

		}

		int missingStepPos = -1;
		for (QuotationStepStructureVO qssVO : definitionSteps) {
			if (qssVO.getNetPeriod() == null)
				missingStepPos = qssVO.getStepCount();
			else {
				if (missingStepPos > 0 && missingStepPos < qssVO.getStepCount()) {
					super.addErrorMessageSummary("quote.openend.step.define.error");
					return false;

				}
			}

		}

		return true;
	}
	
	public void cancelStepsDefinition() {
		definitionSteps = null;
		if (!MALUtilities.isEmpty(getSplMsgforStep())){
			addInfoMessage("quote.calculation.success");
		}
	}
	
	public void selectRequestType() {
		//Clear fields that are not applicable for the selected Request Type
		switch(QuoteRequestTypeEnum.valueOf(quoteRequest.getQuoteRequest().getQuoteRequestType().getCode())) {
		    case IMM_NEED_CLIENT:
		    	getQuoteRequestVehicle().setFleetmaster(null);
		    	getQuoteRequestVehicle().setRequiredAccessories(null);
		    	getQuoteRequestVehicle().setFirstInteriorColor(null);		    	
		    	getQuoteRequestVehicle().setFirstExteriorColor(null);
		    	getQuoteRequestVehicle().setSecondInteriorColor(null);		    	
		    	getQuoteRequestVehicle().setSecondExteriorColor(null);
		    	getQuoteRequestVehicle().setThirdInteriorColor(null);		    	
		    	getQuoteRequestVehicle().setThirdExteriorColor(null);	
		    	getQuoteRequestVehicle().setRefusedColorDescription(null);
			    break;
		    case IMM_NEED_LOCATE:
		    	getQuoteRequestVehicle().setFleetmaster(null);		    	
		    	getQuoteRequestVehicle().setDealershipCode(null);
		    	getQuoteRequestVehicle().setDealershipName(null);
		    	getQuoteRequestVehicle().setDealershipContact(null);
		    	getQuoteRequestVehicle().setDealershipContactPhone(null);
		    	break;
		    case IMM_NEED_STOCK:
		    	getQuoteRequestVehicle().setRequiredAccessories(null);
		    	getQuoteRequestVehicle().setFirstInteriorColor(null);		    	
		    	getQuoteRequestVehicle().setFirstExteriorColor(null);
		    	getQuoteRequestVehicle().setSecondInteriorColor(null);		    	
		    	getQuoteRequestVehicle().setSecondExteriorColor(null);
		    	getQuoteRequestVehicle().setThirdInteriorColor(null);		    	
		    	getQuoteRequestVehicle().setThirdExteriorColor(null);	
		    	getQuoteRequestVehicle().setRefusedColorDescription(null);	
		    	getQuoteRequestVehicle().setDealershipCode(null);
		    	getQuoteRequestVehicle().setDealershipName(null);
		    	getQuoteRequestVehicle().setDealershipContact(null);
		    	getQuoteRequestVehicle().setDealershipContactPhone(null);
		    	getQuoteRequestVehicle().setVehicleDescription(null);		    	
		    	break;
		    case UPFIT_ASSESSMENT:
		    	setDriverNameInput(null);
		    	setDriverZipCode(null);
		    	setDriverDeliveryAddress(null);
		    	getSelectedDriver().setDrvId(null);
		    	getSelectedDriver().setFmsId(null);
		    	getSelectedDriver().setAddressLine1(null);
		    	getSelectedDriver().setAddressLine2(null);
		    	getSelectedDriver().setBusinessAddressLine(null);
		    	getSelectedDriver().setDriverForeName(null);
		    	getSelectedDriver().setDriverPhone(null);
		    	getSelectedDriver().setDriverSurName(null);
		    	getSelectedDriver().setEolDate(null);
		    	getSelectedDriver().setPostCode(null);
		    	getSelectedDriver().setPostCode(null);
		    	getSelectedDriver().setProductType(null);
		    	getSelectedDriver().setQmdId(null);
		    	getSelectedDriver().setQuoteProfileDesc(null);
		    	getSelectedDriver().setRegion(null);
		    	getSelectedDriver().setReturningUnit(false);
		    	getSelectedDriver().setTerm(null);
		    	getSelectedDriver().setTown(null);
		    	getSelectedDriver().setAllocatedUnit(null);
		    	getQuoteRequestVehicle().setRequiredAccessories(null);
		    	getQuoteRequestVehicle().setFirstInteriorColor(null);		    	
		    	getQuoteRequestVehicle().setFirstExteriorColor(null);
		    	getQuoteRequestVehicle().setSecondInteriorColor(null);		    	
		    	getQuoteRequestVehicle().setSecondExteriorColor(null);
		    	getQuoteRequestVehicle().setThirdInteriorColor(null);		    	
		    	getQuoteRequestVehicle().setThirdExteriorColor(null);	
		    	getQuoteRequestVehicle().setRefusedColorDescription(null);	
		    	getQuoteRequestVehicle().setDealershipCode(null);
		    	getQuoteRequestVehicle().setDealershipName(null);
		    	getQuoteRequestVehicle().setDealershipContact(null);
		    	getQuoteRequestVehicle().setDealershipContactPhone(null);
		    	getQuoteRequestVehicle().setVehicleDescription(null);
		    	getQuoteRequestVehicle().setVehicleDeliveryChargeType(null);
		    	getQuoteRequestVehicle().setPlateTypeCode(null);
		    	getQuoteRequestVehicle().setFleetReferenceNumber(null);
		    	getQuoteRequestVehicle().setFleetmaster(null);
		    	
		    	getQuoteRequestQuote().setLeaseTerm(null);
				getQuoteRequestQuote().setLeaseMiles(null);
				getQuoteRequestQuote().setProjectedReplaceMonth(null);
				getQuoteRequestQuote().setQuotationProfile(null);
				getQuoteRequestQuote().setDriverGradeGroupCode(null);
				getQuoteRequestQuote().setQuoteRequestDepreciationMethod(null);
				getQuoteRequestQuote().setDepreciationMethodValue(null);
				getQuoteRequestQuote().setQuoId(null);
				setSelectedQuoteRequestQuoteModel(null);

		    	getDocumentFileVOs().clear();	
	        	setSelectedDocumentFileVO(null);
	        	setSelectedDocumentToView(null);
	        	
		    	break;
		    	
		    case IMM_NEED_UPFIT_CLIENT_LOCATE:
		    	getQuoteRequestVehicle().setFleetmaster(null);		    	
		    	break;
		    	
		    case IMM_NEED_UPFIT_MAFS_LOCATE:
		    	getQuoteRequestVehicle().setDealershipCode(null);
		    	getQuoteRequestVehicle().setDealershipName(null);
		    	getQuoteRequestVehicle().setDealershipContact(null);
		    	break;
		    case IMM_NEED_UPFIT_STOCK:
		    	getQuoteRequestVehicle().setRequiredAccessories(null);
		    	getQuoteRequestVehicle().setFirstInteriorColor(null);		    	
		    	getQuoteRequestVehicle().setFirstExteriorColor(null);
		    	getQuoteRequestVehicle().setSecondInteriorColor(null);		    	
		    	getQuoteRequestVehicle().setSecondExteriorColor(null);
		    	getQuoteRequestVehicle().setThirdInteriorColor(null);		    	
		    	getQuoteRequestVehicle().setThirdExteriorColor(null);	
		    	getQuoteRequestVehicle().setRefusedColorDescription(null);	
		    	getQuoteRequestVehicle().setDealershipCode(null);
		    	getQuoteRequestVehicle().setDealershipName(null);
		    	getQuoteRequestVehicle().setDealershipContact(null);
		    	getQuoteRequestVehicle().setDealershipContactPhone(null);
		    	getQuoteRequestVehicle().setVehicleDescription(null);	
		    	break;
			default:	
	            throw new MalException("generic.error", new String[]{"In selectRequestType() detected unsupported Request Type. Irrelevant fields must be cleared for new Request Type."});				
		}
	}
	
	/**
	 * Beans core implementation for validating and saving a quote request.
	 * The logic will be re-used by other handlers that need to save the quote request 
	 * first before proceeding.
	 * @return boolean indicating whether or not the save succeeded.
	 */
	private boolean coreSave() {
		boolean isSaved = false;
    	QuoteRequest quoteRequest = null;
    	Driver driver;
    	
    	try {   
    		if(isValidForSave()) {

    			//Bind Vehicles
    			if(MALUtilities.isEmpty(getQuoteRequestVehicle().getQrvId())){
    				getQuoteRequestVehicle().setQuoteRequest(getQuoteRequest().getQuoteRequest());
    				getQuoteRequest().getQuoteRequest().getQuoteRequestVehicles().clear();
    				getQuoteRequest().getQuoteRequest().getQuoteRequestVehicles().add(getQuoteRequestVehicle());    			
    			}

    			//Bind Quote
    			if(MALUtilities.isEmpty(getQuoteRequestQuote().getQuoteRequestQuoteId())) {
    				getQuoteRequestQuote().setQuoteRequest(getQuoteRequest().getQuoteRequest());
    				getQuoteRequest().getQuoteRequest().getQuoteRequestQuotes().clear();
    				getQuoteRequest().getQuoteRequest().getQuoteRequestQuotes().add(getQuoteRequestQuote());    				
    			} else {
    				if(!MALUtilities.isEmpty(getQuoteRequestQuote().getQuotationProfile())) {
    					if (MALUtilities.isEmpty(getQuoteRequestQuote().getQuoteRequestDepreciationMethod())) {
    						getQuoteRequestQuote().setDepreciationMethodValue(null);
    					}    					
    				}
    			}
    			
    			if (!MALUtilities.isEmpty(getSelectedQuoteRequestQuoteModel())) {
    				getQuoteRequestQuote().setQuoId(getSelectedQuoteRequestQuoteModel().getQuoId());
    				getQuoteRequest().getQuoteRequest().getQuoteRequestQuotes().get(0).setQuoId(getSelectedQuoteRequestQuoteModel().getQuoId());
    			}
    			
    			//Bind Driver
    			if(!MALUtilities.isEmpty(getSelectedDriver()) && !MALUtilities.isEmpty(getSelectedDriver().getDrvId())){
    				driver = driverService.getDriver(getSelectedDriver().getDrvId());
    				getQuoteRequest().getQuoteRequest().setDriver(driver);
    				getQuoteRequest().getQuoteRequest().setDriverZipCode(null);
    				getQuoteRequest().getQuoteRequest().setReturningUnitYN(getSelectedDriver().isReturningUnit() ? "Y" : "N");
    				if(getSelectedDriver().getFmsId() != null) {
    				getQuoteRequest().getQuoteRequest().setReturningFmsId(getSelectedDriver().getFmsId());
    			}else{
    					getQuoteRequest().getQuoteRequest().setReturningFmsId(null);
    				}
    			}else{
    				getQuoteRequest().getQuoteRequest().setDriverZipCode(getDriverZipCode());
    				setSelectedDriver(new DriverInfoVO());
    				getQuoteRequest().getQuoteRequest().setDriver(null);
    				getQuoteRequest().getQuoteRequest().setReturningFmsId(null);
    				getQuoteRequest().getQuoteRequest().setReturningUnitYN("N");
    			}
    			getQuoteRequest().getQuoteRequest().setDeliveryAddress(getDriverDeliveryAddress());
    			
    			if (!MALUtilities.isEmpty(getAssignedUser())) {
    				if (!MALUtilities.isEmpty(getQuoteRequest().getQuoteRequest().getAssignedTo()) && !getAssignedUser().equals(getQuoteRequest().getQuoteRequest().getAssignedTo())) {
    					getQuoteRequest().getQuoteRequest().setQuoteRequestStatus(quoteRequestService.getQuoteRequestStatus(QuoteRequestStatusEnum.IN_PROGRESS));
    				}
    			} else {
    				if (!MALUtilities.isEmpty(getQuoteRequest().getQuoteRequest().getAssignedTo())) {
    					getQuoteRequest().getQuoteRequest().setQuoteRequestStatus(quoteRequestService.getQuoteRequestStatus(QuoteRequestStatusEnum.IN_PROGRESS));
    				}
    			}

    			//Bind Uploaded Documents
    			for(DocumentFileVO fileVO : getDocumentFileVOs()) {
    			    if(!fileVO.isUploadDoc()) {
    			    	OnbaseUploadedDocs obd = new OnbaseUploadedDocs();
    			    	obd.setFileType(fileVO.getFileType());
    			    	obd.setFileName(fileVO.getFileName().indexOf(".") > 0 ? fileVO.getFileName().substring(0, fileVO.getFileName().indexOf(".")) : fileVO.getFileName());
    			    	obd.setFileData(fileVO.getFileData());
    			    	obd.setNeedToUpload(true);
    			    	obd.setQuoteRequest(getQuoteRequest().getQuoteRequest());
    			    	getQuoteRequest().getQuoteRequest().getQuoteRequestVehicles().get(0).getOnbaseUploadedDocs().add(obd);
    			    }
    			}
    			
    			getQuoteRequest().getQuoteRequest().getQuoteRequestConfigurations().clear();
    			for(QuoteRequestConfigVO qrcVO : vehicleConfigs) {
    				if(qrcVO.isSelected()) {
    					QuoteRequestConfiguration quoteRequestConfiguration = new QuoteRequestConfiguration();
    					quoteRequestConfiguration.setVehicleConfigurationId(qrcVO.getVehicleConfiguration());
    					quoteRequestConfiguration.setQuoteRequest(getQuoteRequest().getQuoteRequest());
    					getQuoteRequest().getQuoteRequest().getQuoteRequestConfigurations().add(quoteRequestConfiguration);
    				}
    			}
    			
    			
    			if(mfrIncentive) {
        			getQuoteRequest().getQuoteRequest().setManufacturerIncentiveYN("Y");
    			} else {
    				getQuoteRequest().getQuoteRequest().setManufacturerIncentiveYN("N");
    			}
    			
    			getQuoteRequest().getQuoteRequest().setContactName(clientContactName);
    			getQuoteRequest().getQuoteRequest().setContactPhone(clientContactPhone);
    			getQuoteRequest().getQuoteRequest().setContactEmail(clientContactEmail);
    			if(clientContactDriver) {
        			getQuoteRequest().getQuoteRequest().setContactDriverYN("Y");
    			} else {
    				getQuoteRequest().getQuoteRequest().setContactDriverYN("N");
    			}

    			
    			
    			quoteRequest = quoteRequestService.save(getQuoteRequest().getQuoteRequest(), super.getLoggedInUser().getEmployeeNo()); 
    			getQuoteRequest().setQuoteRequest(quoteRequest);

    			postSave();
    			
    			isSaved = true;
    		}    			
		} catch (Exception e) {
			super.addErrorMessage("custom.message", e.getMessage());
			logger.error(e, "In validateSave method");
		}
    			
		
		return isSaved;
	}
	
	/**
     * Handles page Save button click event. 
     * @return The calling view
     */    
    public void save(){
    	boolean saved = false;
    	
    	try {
    		
    		if(MALUtilities.isEmpty(getQuoteRequest().getQuoteRequest().getQuoteRequestStatus())) {
        		saved = coreSave();    			
    		} else {
        		switch(QuoteRequestStatusEnum.valueOf(getQuoteRequest().getQuoteRequest().getQuoteRequestStatus().getCode())){
    		        case SAVED:
    		        case CLOSED:	
            		    saved = coreSave(); 
            		    break;
        		    case SUBMITTED:
        			    saved = isValidForSubmit() ? coreSave() : false;
        			    break;
        		    case IN_PROGRESS:
        			    saved = isValidForSubmit() ? coreSave() : false;
        			    break;
        		    case COMPLETED:
			            throw new MalException("generic.error", new String[]{"Save Completed has not been implemented"});        			
        		    default:
    			        throw new MalException("generic.error", new String[]{"Unsupported Quote Status"});        			
        		}    			
    		}
    		
    		if(saved) {
    			super.addSuccessMessage("process.success","Save Quote Request for Client  (" + getQuoteRequest().getQuoteRequest().getClientAccount().getAccountName() + ")");
    		}
    			
		} catch (Exception e) {
			super.addErrorMessage("custom.message", e.getMessage());
			logger.error(e, "In save method");
		}
    	
    }
    
    private void postSave(){
    	
		setQuoteRequest(quoteRequestService.getQuoteRequestVO(getQuoteRequest().getQuoteRequest().getQrqId())); 
		
		if(!MALUtilities.isEmpty(getQuoteRequest().getQuoteRequest().getQuoteRequestQuotes())) {
			if(getQuoteRequest().getQuoteRequest().getQuoteRequestQuotes().size() == 1) {
				setQuoteRequestQuote(getQuoteRequest().getQuoteRequest().getQuoteRequestQuotes().get(0));
			}
		}
		
		if(!MALUtilities.isEmpty(getQuoteRequest().getQuoteRequest().getQuoteRequestVehicles())) {
			if(getQuoteRequest().getQuoteRequest().getQuoteRequestVehicles().size() == 1) {
				setQuoteRequestVehicle(getQuoteRequest().getQuoteRequest().getQuoteRequestVehicles().get(0));
			}
		}
		
		initializeDocuments();
		
    }
    
  /**
  * Handles page submit button click event. 
  * @return The calling view
  */     
    public String submit(){
    	QuoteRequest quoteRequest = null;
    	String nextPage = null;
    	
    	try {
    		if(coreSave() && isValidForSubmit()) {
    			quoteRequest = quoteRequestService.submit(getQuoteRequest().getQuoteRequest(), super.getLoggedInUser().getEmployeeNo());
    			quoteRequest = quoteRequestService.getQuoteRequestVO(quoteRequest.getQrqId()).getQuoteRequest();
    			
    			if(quoteRequest.getQuoteRequestType().getCode().equals(QuoteRequestTypeEnum.UPFIT_ASSESSMENT.getCode())) {
    				quoteRequest = systematicallyAssignQuoteRequest(quoteRequest);
    			}
    			
    			getQuoteRequest().setQuoteRequest(quoteRequest); 

    			super.addSuccessMessage("custom.message","Request has been submitted successfully.  Request Due Date is " + MALUtilities.getNullSafeDatetoString(getQuoteRequest().getQuoteRequest().getDueDate()));    			
    			
    			nextPage = cancelPage();    			
    		}
		} catch (Exception e) {
			super.addErrorMessage("custom.message", e.getMessage());
			logger.error(e, "In submit quote request method");
		}
    	
    	return nextPage;
    }

    private QuoteRequest systematicallyAssignQuoteRequest(QuoteRequest quoteRequest) {
    	try {
    		quoteRequest = quoteRequestService.assignToAccountConsultant(quoteRequest, "TE");
    		quotingEmailService.emailQuoteRequestStatusChange(quoteRequest);
    	} catch(MalBusinessException mbe) {
    		super.addErrorMessage("custom.message", mbe.getMessage());
    		logger.error(mbe, "Error assigning Quote Request. qrq = " + quoteRequest.getQrqId());
    	} catch (Exception e) {
    		logger.error(e, "Error assigning Quote Request. qrq = " + quoteRequest.getQrqId());
    	}
    	return quoteRequest;
    }
    
    
    /**
     * Handles page rework request 
     */     
    public void rework(){
    	QuoteRequest quoteRequest = null;

    	try {
    		if(isValidForRework() && coreSave()) {
    			quoteRequest = quoteRequestService.rework(getQuoteRequest().getQuoteRequest(), getReworkReason(), super.getLoggedInUser().getEmployeeNo());
    			setQuoteRequest(quoteRequestService.getQuoteRequestVO(quoteRequest.getQrqId()));
    			setViewMode(ViewConstants.VIEW_MODE_EDIT);
    			setReworkReason(null);
    			setJustFlippedToReworked(true);
    			setAssignedWillowUser(null);
    			
    			for(LogBookTypeVO lbt : getLogBookType()) {
    				lbt.setReadOnly(false);
    			}
    			
    			RequestContext.getCurrentInstance().execute("postRework();");
    		}    			    			    		
    	} catch (Exception e) {
    		super.addErrorMessage("custom.message", e.getMessage());
    		logger.error(e, "In rework quote request method");
    	}
    }
    
    public void postRework(){
    	if (isJustFlippedToReworked()) {
    		setJustFlippedToReworked(false);
        	super.addSuccessMessage("custom.message","Quote Request is now ready to be reworked.");
    	}
    }    
    
    public void copyQuoteRequest() {
    	QuoteRequest copiedQuoteRequest;
    	try {
    		copiedQuoteRequest = quoteRequestService.copyQuoteRequest(getQuoteRequest().getQuoteRequest().getQrqId(), super.getLoggedInUser().getEmployeeNo());
        	thisPage.getInputValues().put(ViewConstants.VIEW_PARAM_QUOTE_REQUEST_ID, copiedQuoteRequest.getQrqId());
        	thisPage.getInputValues().put(ViewConstants.VIEW_PARAM_MODE, ViewConstants.VIEW_MODE_EDIT);
        	
        	//TODO Clearing document info should be done in loadNewPage.
        	getDocumentFileVOs().clear();	
        	setSelectedDocumentFileVO(null);
        	setSelectedDocumentToView(null);
        	
        	loadNewPage();
        	super.addSuccessMessage("custom.message","Quote Request copied successfully.");
    	} catch (Exception e) {
    		super.addErrorMessage("custom.message", "Copy Quote Request failed with error: " + e.getMessage());
		}
    }
        
    
    public String assignQuoteRequest() {
    	String nextPage = null;
    	QuoteRequest quoteRequest = null;
    	
    	try {

    		if(isValidForAssign() && coreSave()) {    		
//    			if (!MALUtilities.isEmpty(getAssignedUser())) {
//    				if (!MALUtilities.isEmpty(getQuoteRequest().getQuoteRequest().getAssignedTo()) && !getAssignedUser().equals(getQuoteRequest().getQuoteRequest().getAssignedTo())) {
//    					getQuoteRequest().getQuoteRequest().setQuoteRequestStatus(quoteRequestService.getQuoteRequestStatus(QuoteRequestStatusEnum.IN_PROGRESS));
//    				}
//    			} else {
//    				if (!MALUtilities.isEmpty(getQuoteRequest().getQuoteRequest().getAssignedTo())) {
//    					getQuoteRequest().getQuoteRequest().setQuoteRequestStatus(quoteRequestService.getQuoteRequestStatus(QuoteRequestStatusEnum.IN_PROGRESS));
//    				}
//    			}
//    			getQuoteRequest().getQuoteRequest().setAssignedTo(selectedAssignToEmployeeNo);
    			quoteRequest = quoteRequestService.assignToEmployee(getQuoteRequest().getQuoteRequest(), selectedAssignToEmployeeNo);    			
    			getQuoteRequest().setQuoteRequest(quoteRequest);
    			
    			quotingEmailService.emailQuoteRequestStatusChange(getQuoteRequest().getQuoteRequest());
    			
				RequestContext.getCurrentInstance().execute("postAssign();");    			
    		}
    		
    	} catch (Exception e) {
    		super.addErrorMessage("custom.message", e.getMessage());
    		logger.error(e, "In assign quote request method");
    	}
    	
    	return nextPage;
    }
    
    public void decodeAssignedToByName(){
    	String assignedToInput = "";
    	String paramName = (String) getRequestParameter("WILLOW_USER_LOV_INPUT_NAME");
		if (!MALUtilities.isEmptyString(paramName)) {
			assignedToInput = (String) getRequestParameter(paramName);
		}
		if(MALUtilities.isEmpty(assignedToInput)){
			assignedToInput = getAssignedUser();	
		}
		
		 List<WillowUserLovVO> resultList =  quoteRequestService.getWillowUsers(null, assignedToInput, null, new PageRequest(0,10));
		 if(resultList.size() == 1){					 
			 setAssignedUser(resultList.get(0).getLastName() + ", " + resultList.get(0).getFirstName());
			 selectedAssignToEmployeeNo = resultList.get(0).getEmployeeNo();
		 }else {
			 if(resultList.size() == 0){
				 super.addErrorMessageSummary(QUOTE_ASSIGN_USER_UI_ID, "decode.noMatchFound.msg","Assigned to " + assignedToInput); 
			 }else{
				 super.addErrorMessageSummary(QUOTE_ASSIGN_USER_UI_ID, "decode.notExactMatch.msg","Assigned to " +  assignedToInput);			
			 }
			 selectedAssignToEmployeeNo = null;
		 }
    }
    
    private boolean isValidForAssign(){
    	boolean isValid = true;
		
    	if(MALUtilities.isEmpty(selectedAssignToEmployeeNo)) {
    		super.addErrorMessageSummary(QUOTE_ASSIGN_USER_UI_ID, "custom.message", "Assign To is required");
    		isValid = false;
		}
		
    	return isValid;
    }
    
    /**
     * Handles page close button click event. This handler is a bit different in that it will be invoked from a dialog.
     * As such, the error messages report will have to do so via the summary scope. 
     * @return The calling view
     */     
    public String close(){
    	QuoteRequest quoteRequest = null;
    	String nextPage = null;
    	ObjectLogBook logBook;
    	
    	try {

    		if(coreSave() && isValidForClose()) {    		
    			if(MALUtilities.isEmpty(getSelectedQuoteRequestCloseReasonNote())) {
    				setSelectedQuoteRequestCloseReasonNote(getSelectedQuoteRequestCloseReason().getDescription());
    			}
    			
    			quoteRequest = quoteRequestService.close(getQuoteRequest().getQuoteRequest(), getSelectedQuoteRequestCloseReason(), super.getLoggedInUser().getEmployeeNo());    			
    			getQuoteRequest().setQuoteRequest(quoteRequest);
    			
    			logBook = logBookService.createObjectLogBook(getQuoteRequest().getQuoteRequest(), LogBookTypeEnum.TYPE_QUOTE_REQEUST_NOTES);
    			logBookService.addEntry(logBook, super.getLoggedInUser().getEmployeeNo(), getSelectedQuoteRequestCloseReasonNote(), null, false);
    			
    			try {
    				quotingEmailService.emailQuoteRequestStatusChange(getQuoteRequest().getQuoteRequest());
    			} catch(Exception e) {
    	    		emailErrorMessage = e.getMessage();    				
    			}
    			
    			
    			RequestContext.getCurrentInstance().execute("postClose();");    			
    		}
    		
    	} catch (Exception e) {
    		super.addErrorMessage("custom.message", e.getMessage());
    		logger.error(e, "In close quote request method");
    	}

    	return nextPage;
    }    

    public String finishClose() {
		if(MALUtilities.isEmpty(emailErrorMessage)) {
			super.addSuccessMessage("process.success","Close Quote Request");
		} else {
			super.addWarnMessage("process.success","Close Quote Request");				
			super.addWarnMessage("custom.message", emailErrorMessage);
			emailErrorMessage = null;				
		}			
    	
		return cancelPage();
    }
    
	public String cancel() {
		return cancelPage();
	}
		
	
	/**
	 * Lifted code from Vehicle Configuration
	 * @param DocumentFileVO
	 */	
	public void  deleteUploadedDocument(DocumentFileVO documentFileVO){
		getDocumentFileVOs().remove(documentFileVO);
	}	
	/**
	 * Lifted code from Vehicle Configuration
	 * @param event
	 */
	public void fileUploadListener(FileUploadEvent event) {
		
		UploadedFile file = event.getFile();	
		String fileName  = file.getFileName();
		
		logger.info("getFileName--"+fileName);
		
		String fileExtn = null;
		if (fileName.lastIndexOf(".") > 0) {
			fileExtn = fileName.substring(fileName.lastIndexOf(".") + 1);
		}
		
		if (fileExtn != null && fileExtn.equalsIgnoreCase("exe") ) {			
			addErrorMessageSummary("fileupload.not.valid.file.type",new String[]{"."+fileExtn});
			return;
		}	
		if (fileName.lastIndexOf("\\") > 0) {
			fileName = fileName.substring(fileName.lastIndexOf("\\") + 1);
		}
		
		DocumentFileVO documentFileVO = new DocumentFileVO();
		documentFileVO.setFileId(onbaseArchivalService.getNextPK());
		documentFileVO.setFileName(fileName);
		documentFileVO.setFileExt(fileExtn);
		documentFileVO.setFileData(file.getContents());
		documentFileVO.setFileType(fileExtn);
		documentFileVO.setUploadDoc(false);
				
		getDocumentFileVOs().add(0,documentFileVO);
	}
	
	public void viewDocumentListener() {
		OnbaseKeywordVO onbaseKeywordVO;
		List<OnbaseKeywordVO> keyWordVOList;
		String mimeType = "";
		
		setSelectedDocumentToView(null);			
		mimeType =  "application/*";
		
		try {
			if(MALUtilities.isEmpty(getSelectedDocumentFileVO().getFileData())) {
				onbaseKeywordVO =  new OnbaseKeywordVO(OnbaseIndexEnum.UPLOAD_ID.getName() ,String.valueOf(getSelectedDocumentFileVO().getFileId()));
				keyWordVOList = new ArrayList<OnbaseKeywordVO>();
				keyWordVOList.add(onbaseKeywordVO);				
				
				setSelectedDocumentToView(
						new DefaultStreamedContent(
								new ByteArrayInputStream(onbaseRetrievalService.getDoc(OnbaseDocTypeEnum.TYPE_QUOTE_REQUEST, keyWordVOList)),
								mimeType, 
								getSelectedDocumentFileVO().getFileName()) );					
			} else {
				setSelectedDocumentToView(
						new DefaultStreamedContent(
								new ByteArrayInputStream(getSelectedDocumentFileVO().getFileData()),
								mimeType, 
								getSelectedDocumentFileVO().getFileName()) );			
			}

			if(MALUtilities.isEmpty(getSelectedDocumentToView())) {
				super.addErrorMessage("custom.message", "Unable to load file. If problem perists, please notify the Help Desk.");			
			}
		} catch (MalException me) {
			super.addErrorMessage("custom.message", "OnBase has not processed the document. Please try again later. If problem perists, notify the Help Desk.");	
			logger.error(me, "Error attempting to view " + getSelectedDocumentFileVO().getFileName());
		}
	}
		
	public boolean isClientEditable() {
		boolean isEditable = false;
		isEditable = MALUtilities.isEmpty(getQuoteRequest().getQuoteRequest().getQuoteRequestStatus());
		return isEditable;
	}
	
	public boolean isRequestTypeEditable() {
		boolean isEditable = false;
		
		if(MALUtilities.isEmpty(getQuoteRequest().getQuoteRequest().getQuoteRequestStatus())
				|| getQuoteRequest().getQuoteRequest().getQuoteRequestStatus().getCode().equals(QuoteRequestStatusEnum.SAVED.getCode())) {
			isEditable = true;
		}
		
		return isEditable;
	}	
	
	public boolean isSubmitButtonEnabled() {
		boolean isEnable = false;
		
		if(hasPermission()) {
			if(MALUtilities.isEmpty(getQuoteRequest().getQuoteRequest().getQuoteRequestStatus())
					|| getQuoteRequest().getQuoteRequest().getQuoteRequestStatus().getCode().equals(QuoteRequestStatusEnum.SAVED.getCode())) {
				isEnable = true;
			}
		}
		
		return isEnable;
	}	
	
	
	public boolean isCompleteButtonEnabled() {
		boolean isEnable = false;
				
		return isEnable;
	}	
	
	
	public boolean isCloseButtonEnabled() {
		boolean isEnable = false;
		
		if(super.hasPermission()) {
			if(!MALUtilities.isEmpty(getQuoteRequest().getQuoteRequest().getQuoteRequestStatus())) {			
				if(getQuoteRequest().getQuoteRequest().getQuoteRequestStatus().getCode().equals(QuoteRequestStatusEnum.SAVED.getCode())
						|| getQuoteRequest().getQuoteRequest().getQuoteRequestStatus().getCode().equals(QuoteRequestStatusEnum.SUBMITTED.getCode())){
					isEnable = true;
				}

				if(getQuoteRequest().getQuoteRequest().getQuoteRequestStatus().getCode().equals(QuoteRequestStatusEnum.IN_PROGRESS.getCode())
						&& super.hasPermission("quoteRequestAddEdit_manage")) {
					isEnable = true;		    	
				}
			}	
		}
		
		return isEnable;
	}	
	
	public boolean isNoteButtonEnabled() {
		boolean isEnable = false;
		
		if(!MALUtilities.isEmpty(getQuoteRequest().getQuoteRequest().getQuoteRequestStatus())) {
			isEnable = true;
		}
		
		return isEnable;
	}
	
	public boolean isAssignToButtonEnabled() {
		boolean isEnable = false;
		
		if(!MALUtilities.isEmpty(getQuoteRequest().getQuoteRequest().getQuoteRequestStatus())) {
			
		    if(getQuoteRequest().getQuoteRequest().getQuoteRequestStatus().getCode().equals(QuoteRequestStatusEnum.SUBMITTED.getCode())
		    		|| getQuoteRequest().getQuoteRequest().getQuoteRequestStatus().getCode().equals(QuoteRequestStatusEnum.IN_PROGRESS.getCode())){
		    	
		    	if(super.hasPermission("quoteRequestAddEdit_manage")) {
		    		isEnable = true;
		    	}
		    	
		    }
		}
		
		return isEnable;
	}	
		
	public boolean isUploadButtonEnabled() {
		boolean isEnable = false;
		
		if(!MALUtilities.isEmpty(getQuoteRequest().getQuoteRequest().getQuoteRequestStatus())) {
			if(hasPermission()) {
				isEnable = true;				
			}
		}
		
		return isEnable;
	}
	
	public boolean isAttachWillowQuoteButtonEnabled() {
		boolean isEnable = false;

    	if(super.hasPermission("quoteRequestAddEdit_manage")) {
    		if(!MALUtilities.isEmpty(getQuoteRequest().getQuoteRequest().getQuoteRequestStatus())) {
			
    			if(!(getQuoteRequest().getQuoteRequest().getQuoteRequestStatus().getCode().equals(QuoteRequestStatusEnum.CLOSED.getCode())
    					|| getQuoteRequest().getQuoteRequest().getQuoteRequestStatus().getCode().equals(QuoteRequestStatusEnum.COMPLETED.getCode()))){		    	
    				isEnable = true;
    			}
    		}
    	}		    	
		
		return isEnable;
	}
	
	public boolean isWillowQuoteButtonEnabled() {
		boolean isEnable = false;

    	if(super.hasPermission("quoteRequestAddEdit_manage")) {
    		if(!MALUtilities.isEmpty(getQuoteRequest().getQuoteRequest().getQuoteRequestStatus())) {
			
    			if(!getQuoteRequest().getQuoteRequest().getQuoteRequestStatus().getCode().equals(QuoteRequestStatusEnum.COMPLETED.getCode())){		    	
    				isEnable = true;
    			}
    		}
    	}		    	
		
		return isEnable;
	}
	
	public boolean isCopyQuoteButtonEnabled() {
		boolean isEnable = false;

    	if(super.hasPermission("quoteRequestAddEdit")) {
    		if(!MALUtilities.isEmpty(getQuoteRequest().getQuoteRequest().getQuoteRequestStatus())) {
   				isEnable = true;
    		}
    	}		    	
		
		return isEnable;
	}
	
	public boolean isSaveButtonEnabled() {
		boolean isEnable = false;

    	if(super.hasPermission()) {
    		
    		if(!getViewMode().equals(ViewConstants.VIEW_MODE_READ)) {
    			isEnable = true;    			
    		} else {
    			if(isWillowQuoteButtonEnabled()){		    	
    				isEnable = true;				
    			}
    		}
    	}		    	
		
		return isEnable;
	}	

	public boolean isReworkButtonEnabled() {
		boolean isEnable = false;

    	if(super.hasPermission()) {    		
			if(!MALUtilities.isEmpty(getQuoteRequest().getQuoteRequest().getQuoteRequestStatus())) {
				if(getQuoteRequest().getQuoteRequest().getQuoteRequestStatus().getCode().equals(QuoteRequestStatusEnum.COMPLETED.getCode())
						|| getQuoteRequest().getQuoteRequest().getQuoteRequestStatus().getCode().equals(QuoteRequestStatusEnum.CLOSED.getCode())){
					isEnable = true;    			
				}
			}			
    	}		    	
		
		return isEnable;
	}	
	
	public int notesCount(){
		ObjectLogBook objectLogBook;		
		int count = 0;
		
		objectLogBook = logBookService.getObjectLogBook(getQuoteRequest().getQuoteRequest(), LogBookTypeEnum.TYPE_QUOTE_REQEUST_NOTES);
		if(!MALUtilities.isEmpty(objectLogBook)) {
			count = objectLogBook.getLogBookEntries().size();
		}
		
		return count;
	}

	/**
	 * Core validation logic that must pass in order to persist the quote request to the database.
	 * 
	 * @return boolean indicated pass (true) or fail (false)
	 */	
    private boolean isValid(){
    	boolean isValid = true;
		
    	if(MALUtilities.isEmpty(getQuoteRequest().getQuoteRequest())){
			super.addErrorMessage("custom.message", "Quote Request has not been initialized");	
			isValid = false;			
		}
		
    	if(isValid) {
			if(MALUtilities.isEmpty(getCustomerLOVParam()) || MALUtilities.isEmpty(getQuoteRequest().getQuoteRequest().getClientAccount())){
				super.addErrorMessage(CLIENT_ACCOUNT_UI_ID, "custom.message", "Client Account is required");	
				isValid = false;				
			}
			
			if(!MALUtilities.isEmpty(getCustomerLOVParam())
					&& !MALUtilities.isEmpty(getQuoteRequest().getQuoteRequest().getClientAccount())
					&& !getQuoteRequest().getQuoteRequest().getClientAccount().getExternalAccountPK().getAccountCode().equals(getCustomerLOVParam())){
				super.addErrorMessage(CLIENT_ACCOUNT_UI_ID, "custom.message", "A Client Account must be selected");	
				isValid = false;					
			}
			
			if(MALUtilities.isEmpty(getQuoteRequest().getQuoteRequest().getQuoteRequestType())){
				super.addErrorMessage(REQUEST_TYPE_UI_ID, "custom.message", "Request Type is required");	
				isValid = false;				
			}			
			
			
    	}
    	
    	return isValid;
    }
    
	/**
	 * Validation logic that must pass in order to save the Quote Requests
	 * @return boolean indicated pass (true) or fail (false)
	 */
    private boolean isValidForSave(){
    	boolean isValid = isValid();
							
    	if(!MALUtilities.isEmpty(getQuoteRequestVehicle())) {
    		if(!MALUtilities.isEmpty(getQuoteRequestVehicle().getFirstInteriorColor())
    				|| !MALUtilities.isEmpty(getQuoteRequestVehicle().getSecondInteriorColor())
    				|| !MALUtilities.isEmpty(getQuoteRequestVehicle().getSecondExteriorColor())
    				|| !MALUtilities.isEmpty(getQuoteRequestVehicle().getThirdInteriorColor())
    				|| !MALUtilities.isEmpty(getQuoteRequestVehicle().getThirdExteriorColor())) {
    			if(MALUtilities.isEmpty(getQuoteRequestVehicle().getFirstExteriorColor())) {
    				super.addErrorMessage(FIRST_EXTERIOR_COLOR_UI_ID, "custom.message", "First Exterior Color is required");
    				RequestContext.getCurrentInstance().execute("onColorPreferenceClick()");
    				isValid = false;							
    			}					
    		}

    	}
    	    											
    	return isValid;
    }
    
	/**
	 * Validation logic that must pass in order to submit the Quote Requests
	 * 
	 * Vehicle Details Section Validations are in OTD-6314
	 * @return boolean indicated pass (true) or fail (false)
	 */
    private boolean isValidForSubmit(){
    	isValid = true;

        validateContactInfo();
    	
		switch(QuoteRequestTypeEnum.valueOf(quoteRequest.getQuoteRequest().getQuoteRequestType().getCode())) {
		    case IMM_NEED_CLIENT:
	        	validateDriver();
	        	validateQuoteInfo();
	        	validateGradeGroup();
	        	validateDealerInfo();
	        	validatePlateInfo();
			    break;
		    case IMM_NEED_UPFIT_CLIENT_LOCATE:
	        	validateDriver();
	        	validateQuoteInfo();
	        	validateGradeGroup();
	        	validateDealerInfo();
	        	validatePlateInfo();
	        	validateUpfitInfo();
		    	break;
		    case IMM_NEED_LOCATE:
	        	validateDriver();
	        	validateQuoteInfo();
	        	validateGradeGroup();
				validateColors();
				validateVehicleInfo();
				validatePlateInfo();
		    	break;
		    case IMM_NEED_UPFIT_MAFS_LOCATE:
	        	validateDriver();
	        	validateQuoteInfo();
	        	validateGradeGroup();
				validateColors();
				validateVehicleInfo();
				validatePlateInfo();
				validateUpfitInfo();
		    	break;
		    case IMM_NEED_STOCK:
	        	validateDriver();
	        	validateQuoteInfo();
	        	validateGradeGroup();
	        	validatePlateInfo();
				validateStockUnit();
		    	break;
		    case IMM_NEED_UPFIT_STOCK:
	        	validateDriver();
	        	validateQuoteInfo();
	        	validateGradeGroup();
	        	validatePlateInfo();
				validateStockUnit();
				validateUpfitInfo();
		    	break;
		    case UPFIT_ASSESSMENT:
		    	validateUpfitInfo();
		    	break;
			default:	
				isValid = false;
				throw new MalException("generic.error", new String[]{"Cannot validate due to unsupported Request Type."});
		}
    									
    	return isValid;
    }    
    
    
    private void validateDriver() {
    	//TODO Get a better understanding why selectedDriver exist when no driver is selected
    	//Driver check - 
    	if( (MALUtilities.isEmpty(getSelectedDriver()) || MALUtilities.isEmpty(getSelectedDriver().getDrvId()) )
    			&& MALUtilities.isEmpty(getDriverZipCode())) {
    		((UIInput) getComponent(DRIVER_UI_ID)).setValid(false);
    		((UIInput) getComponent(DRIVER_ZIP_CODE_UI_ID)).setValid(false);    		
    		super.addErrorMessage("custom.message", "Driver or Delivery Zip Code is required");	
    		isValid = false;								
    	} else if (!MALUtilities.isEmpty(getDriverZipCode()) && (!MALUtilities.isEmpty(driverNameInput)) ) {
    		((UIInput) getComponent(DRIVER_UI_ID)).setValid(false);
    		((UIInput) getComponent(DRIVER_ZIP_CODE_UI_ID)).setValid(false);    		
    		super.addErrorMessage("custom.message", "Cannot have a Driver and Delivery Zip Code for the same request");	
    		isValid = false;								
    	}
    	else {
    		((UIInput) getComponent(DRIVER_UI_ID)).setValid(true);
    		((UIInput) getComponent(DRIVER_ZIP_CODE_UI_ID)).setValid(true);    		
    	}
    }
    
    private void validateQuoteInfo() {
    	Boolean isProfileOpenEnd = false;

    	//Specific Quote checks
    	if(MALUtilities.isEmpty(getQuoteRequestQuote().getQuotationProfile())) {
			super.addErrorMessage(QUOTE_PROFILE_UI_ID, "custom.message", "Quote Profile is required");	
			isValid = false;	    		
    	} else {
    		Product product = productDAO.findById(getQuoteRequestQuote().getQuotationProfile().getPrdProductCode()).orElse(null);
    		
    		if (product.getProductType().equals("OE")) {
    			isProfileOpenEnd = true;
    		}
    		
    		if(!isProfileOpenEnd) {    			        		
    			if(MALUtilities.isEmpty(getQuoteRequestQuote().getLeaseMiles())) {
    				super.addErrorMessage(QUOTE_LEASE_MILES_UI_ID, "custom.message", "Miles is required");	
    				isValid = false;	    				
    			}    			
    		}
    		
    		if(isProfileOpenEnd) {   
        		if(MALUtilities.isEmpty(getQuoteRequestQuote().getQuoteRequestDepreciationMethod())) {
    				super.addErrorMessage(QUOTE_DEPR_METHOD_UI_ID, "custom.message", "Depreciation Method is required");	
    				isValid = false;	    	    			
        		} else {
        			if(!getQuoteRequestQuote().getQuoteRequestDepreciationMethod().getCode().equals(DepreciationMethodEnum.FULL.getCode())
        					&& MALUtilities.isEmpty(getQuoteRequestQuote().getDepreciationMethodValue())) {
        				super.addErrorMessage(DEPRECIATION_METHOD_VALUE_UI_ID, "custom.message", getQuoteRequestQuote().getQuoteRequestDepreciationMethod().getDescription() + " is required");
        				isValid = false;        				
        			}
        		}
    		}    		
    	}
    	
		if(MALUtilities.isEmpty(getQuoteRequestQuote().getLeaseTerm())) {
			super.addErrorMessage(QUOTE_LEASE_TERM_UI_ID, "custom.message", "Months is required");	
			isValid = false;	    				
		}
    }

    private void validateGradeGroup() {
    	Boolean isProfileOpenEnd = false; //HD-12 added to fix merg

    	if(getQuoteRequestQuote().getQuotationProfile() != null) {
        	Product product = productDAO.findById(getQuoteRequestQuote().getQuotationProfile().getPrdProductCode()).orElse(null); //HD-12 added to fix merge
    		if (product != null && product.getProductType().equals("OE")) {
    			isProfileOpenEnd = true;
    		}    	
    	}
    	
		if(isProfileOpenEnd && MALUtilities.isEmpty(getQuoteRequestQuote().getProjectedReplaceMonth())) { //HD-12 merged in isProfileOpenEnd with no declaration or initialization.
			super.addErrorMessage(QUOTE_PROJ_MONTH_UI_ID, "custom.message", "Proj. Repl. Month is required");	
			isValid = false;	    				
		}		
		
		if(isProfileOpenEnd && MALUtilities.isEmpty(getQuoteRequestQuote().getProjectedReplaceMonth())) {
			super.addErrorMessage(QUOTE_PROJ_MONTH_UI_ID, "custom.message", "Proj. Repl. Month is required");	
			isValid = false;	    				
		}		
		
		if(!MALUtilities.isEmpty(getDriverGradeGroups()) && getDriverGradeGroups().size() > 0) {
			if(MALUtilities.isEmpty(getQuoteRequestQuote().getDriverGradeGroupCode())) {
				super.addErrorMessage(QUOTE_DRIVER_GRADE_UI_ID, "custom.message", "Grade Group is required");	
				isValid = false;	    				
			}
		}else{
			super.addErrorMessage(QUOTE_DRIVER_GRADE_UI_ID, "custom.message", "Client must have at least one grade group set up to submit request");	
			isValid = false;
		}
    }
    
    private void validateContactInfo() {
		if(MALUtilities.isEmpty(clientContactName) && !clientContactDriver) {
			super.addErrorMessage(CONTACT_NAME_UI_ID, "custom.message", "Contact Name is required");
			isValid = false;
		}

		if(clientContactDriver) {
			if(!MALUtilities.isEmpty(clientContactName)) {
				super.addErrorMessage(CONTACT_NAME_UI_ID, "custom.message", "Contact Name is not allowed when driver is marked as the contact");
				isValid = false;
			}
			if(!MALUtilities.isEmpty(clientContactPhone)) {
				super.addErrorMessage(CONTACT_PHONE_UI_ID, "custom.message", "Contact Phone is not allowed when driver is marked as the contact");
				isValid = false;
			}
			if(!MALUtilities.isEmpty(clientContactEmail)) {
				super.addErrorMessage(CONTACT_EMAIL_UI_ID, "custom.message", "Contact Email is not allowed when driver is marked as the contact");
				isValid = false;
			}
			if(MALUtilities.isEmpty(driverNameInput)) {
				super.addErrorMessage(CONTACT_DRIVER_UI_ID, "custom.message", "Cannot mark driver as contact when no driver is entered");
				UIComponent comp = getComponent(DRIVER_INFO_UI_ID);
		   	 	if(comp!= null) ((UIInput) comp).setValid(false); 
				isValid = false;
			}
		} else {
			if(MALUtilities.isEmpty(clientContactPhone) && MALUtilities.isEmpty(clientContactEmail)) {
				super.addErrorMessage(CONTACT_PHONE_UI_ID, "custom.message", "Contact Phone or Contact Email is required");
				UIComponent comp = getComponent(CONTACT_EMAIL_UI_ID);
		   	 	if(comp!= null) ((UIInput) comp).setValid(false); 
		   	 	isValid = false;
			}
		}
    }
    
    private void validateColors() {
		if(!MALUtilities.isEmpty(getQuoteRequestVehicle().getFirstInteriorColor())
				|| !MALUtilities.isEmpty(getQuoteRequestVehicle().getSecondInteriorColor())
				|| !MALUtilities.isEmpty(getQuoteRequestVehicle().getSecondExteriorColor())
				|| !MALUtilities.isEmpty(getQuoteRequestVehicle().getThirdInteriorColor())
				|| !MALUtilities.isEmpty(getQuoteRequestVehicle().getThirdExteriorColor())) {
			if(MALUtilities.isEmpty(getQuoteRequestVehicle().getFirstExteriorColor())) {
				super.addErrorMessage(FIRST_EXTERIOR_COLOR_UI_ID, "custom.message", "First Exterior Color is required");
				RequestContext.getCurrentInstance().execute("onColorPreferenceClick()");
				isValid = false;							
			}					
		}
    }

    private void validateStockUnit() {
		if(MALUtilities.isEmpty(getQuoteRequestVehicle().getFleetmaster())) {
			super.addErrorMessage(FIRST_EXTERIOR_COLOR_UI_ID, "custom.message", "Stock unit selection is required");
			isValid = false;    				
		}

		if(!MALUtilities.isEmpty(getQuoteRequestVehicle()) && MALUtilities.isEmpty(getQuoteRequestVehicle().getVehicleDeliveryChargeType())){
    		super.addErrorMessage(VEHICLE_DELIVERY_CHARGE_TYPE_UI_ID, "custom.message", "Vehicle Delivery Charge is required");	
    		isValid = false;    				
		}
		
    }
    
    private void validateVehicleInfo() {
		if(MALUtilities.isEmpty(getQuoteRequestVehicle().getVehicleDescription())) {
    		super.addErrorMessage(VEHICLE_DESC_UI_ID, "custom.message", "Vehicle Description is required");	
    		isValid = false;    							    				
		}		
    }

    private void validatePlateInfo() {
		if(MALUtilities.isEmpty(getQuoteRequestVehicle().getPlateTypeCode())){
    		super.addErrorMessage(VEHICLE_PLATE_TYPE_UI_ID, "custom.message", "Vehicle Plate Type is required");	
    		isValid = false;    				
		}    				
    }

    
    private void validateDealerInfo() {
		if(!MALUtilities.isEmpty(getQuoteRequestVehicle()) && MALUtilities.isEmpty(getQuoteRequestVehicle().getDealershipCode()) 
				&& MALUtilities.isEmpty(getQuoteRequestVehicle().getDealershipName())){
    		super.addErrorMessage(DEALER_INPUT_UI_ID, "custom.message", "Vehicle Dealership is required");
    		isValid = false;    				
		}
		
		if(!MALUtilities.isEmpty(getQuoteRequestVehicle()) && MALUtilities.isEmpty(getQuoteRequestVehicle().getDealershipContact())){
    		super.addErrorMessage(DEALER_CONTACT_UI_ID, "custom.message", "Vehicle Dealership Contact is required");
    		isValid = false;    				
		}
		
		if(!MALUtilities.isEmpty(getQuoteRequestVehicle()) && MALUtilities.isEmpty(getQuoteRequestVehicle().getDealershipContactPhone())){
    		super.addErrorMessage(DEALER_PHONE_UI_ID, "custom.message", "Vehicle Dealership Phone Number is required");
    		isValid = false;    				
		}
		
    }
    
    private void validateUpfitInfo() {
		if(MALUtilities.isEmpty(getQuoteRequest().getQuoteRequest().getUpfitNote())) {
    		super.addErrorMessage(UPFIT_NOTE_UI_ID, "custom.message", "Upfit comment is required");
    		isValid = false;    				
		}
		if(getQuoteRequest().getQuoteRequest().getQuoteRequestConfigurations() == null 
			|| getQuoteRequest().getQuoteRequest().getQuoteRequestConfigurations().size() == 0 ) {
    		super.addErrorMessage("custom.message", "At least one application must be selected");
    		isValid = false;    				
		}

    }
    
	/**
	 * Validation logic that must pass in order to Close the Quote Requests
	 * @return boolean indicated pass (true) or fail (false)
	 */
    private boolean isValidForClose(){
    	boolean isValid = true;
		
    	if(MALUtilities.isEmpty(getSelectedQuoteRequestCloseReason())) {
    		super.addErrorMessageSummary(QUOTE_CLOSE_REASON_UI_ID, "custom.message", "Reason is required");
    		isValid = false;
		}
		
		if(isValid 
				&& MALUtilities.convertYNToBoolean(getSelectedQuoteRequestCloseReason().getInputRequiredInd())
				&& MALUtilities.isEmpty(getSelectedQuoteRequestCloseReasonNote())) {
    		super.addErrorMessageSummary(QUOTE_CLOSE_REASON_NOTE_UI_ID, "custom.message", "Detail is required");
    		isValid = false;
		}
		
    	return isValid;
    } 

	/**
	 * Validation logic that must pass in order to Rework the Quote Requests
	 * @return boolean indicated pass (true) or fail (false)
	 */
    private boolean isValidForRework(){
    	boolean isValid = true;
		
    	if(MALUtilities.isEmpty(getReworkReason())) {
    		super.addErrorMessageSummary(REWORK_REASON_UI_ID, "custom.message", "Reason is required");
    		isValid = false;
		}
    	
    	return isValid;
    } 
    
	@Override
	public boolean hasPermission() {
		return (super.hasPermission() && !getViewMode().equals(ViewConstants.VIEW_MODE_READ));
	}    
	
	@Override
	public boolean hasPermission(String resource) {
		return (super.hasPermission(resource) && !getViewMode().equals(ViewConstants.VIEW_MODE_READ));
	}  	
    
    public void onStepPay() {
    	
    }
	
	@Override
	protected void loadNewPage() {
		Long quoteRequestId;
		
		thisPage.setPageUrl(ViewConstants.QUOTE_REQUEST_ADD_EDIT);
		
		if(super.hasPermission()) {
			setViewMode((String)thisPage.getInputValues().get(ViewConstants.VIEW_PARAM_MODE));
			if(MALUtilities.isEmpty(getViewMode())) {
				setViewMode(ViewConstants.VIEW_MODE_ADD);
			}
		} else {
			setViewMode(ViewConstants.VIEW_MODE_READ);
		}		
		
		//Based on the passed in mode initialize bean's properties
		if(getViewMode().equals(ViewConstants.VIEW_MODE_ADD)) {
			setSelectedExternalAccount(new ExternalAccount(new ExternalAccountPK()));			
			setQuoteRequest(new QuoteRequestVO());
			getQuoteRequest().setQuoteRequest(new QuoteRequest());
			getQuoteRequest().setQuote(new QuoteRequestQuoteVO());
			getQuoteRequest().getQuoteRequest().setQuoteRequestQuotes(new ArrayList<QuoteRequestQuote>());
			getQuoteRequest().getQuoteRequest().setQuoteRequestVehicles(new ArrayList<QuoteRequestVehicle>());
			selectedDriver = new DriverInfoVO();
			thisPage.setPageDisplayName(ViewConstants.DISPLAY_NAME_QUOTE_REQUEST_ADD);			
		} else { 
			quoteRequestId = (Long)thisPage.getInputValues().get(ViewConstants.VIEW_PARAM_QUOTE_REQUEST_ID);
			loadSavedQuoteRequest(quoteRequestId);	
			
			thisPage.setPageDisplayName(ViewConstants.DISPLAY_NAME_QUOTE_REQUEST_EDIT);
			
			if(getQuoteRequest().getQuoteRequest().getQuoteRequestStatus().getCode().equals(QuoteRequestStatusEnum.IN_PROGRESS.getCode())) {
				setIsQuoteRequestInProgress(true);;
			} else {
				setIsQuoteRequestInProgress(false);
			}
			
			if(getQuoteRequest().getQuoteRequest().getQuoteRequestStatus().getCode().equals(QuoteRequestStatusEnum.CLOSED.getCode())
					|| getQuoteRequest().getQuoteRequest().getQuoteRequestStatus().getCode().equals(QuoteRequestStatusEnum.IN_PROGRESS.getCode())
					|| getQuoteRequest().getQuoteRequest().getQuoteRequestStatus().getCode().equals(QuoteRequestStatusEnum.COMPLETED.getCode())) {
				setViewMode(ViewConstants.VIEW_MODE_READ);
			}
		} 		
	}

	private void loadSavedQuoteRequest(Long quoteRequestId) {
		setQuoteRequest(quoteRequestService.getQuoteRequestVO(quoteRequestId));
		setSelectedExternalAccount(getQuoteRequest().getQuoteRequest().getClientAccount());
		setCustomerLOVParam(getSelectedExternalAccount().getExternalAccountPK().getAccountCode());
		getQuoteRequest().setQuote(new QuoteRequestQuoteVO());	
		setQuoteRequestQuote(getQuoteRequest().getQuoteRequest().getQuoteRequestQuotes().get(0));     //TODO Properly get the item from list
		setQuoteRequestVehicle(getQuoteRequest().getQuoteRequest().getQuoteRequestVehicles().get(0)); //TODO Properly get the item from list
		setDriverZipCode(getQuoteRequest().getQuoteRequest().getDriverZipCode());
		setDriverDeliveryAddress(getQuoteRequest().getQuoteRequest().getDeliveryAddress());
		
		if(!MALUtilities.isEmpty(getQuoteRequest().getQuoteRequest().getAssignedTo())){
			List<WillowUserLovVO> list = quoteRequestService.getWillowUsers(getQuoteRequest().getQuoteRequest().getAssignedTo(), null, null, new PageRequest(0,2));
			if(list != null && list.size() == 1){
				setAssignedWillowUser(list.get(0).getLastName() + ", " + list.get(0).getFirstName());
			}
		} else {
			setAssignedWillowUser(null);
		}
		
		
		//This should never happen, but found this case during development
		if(MALUtilities.isEmpty(getQuoteRequest().getQuoteRequest().getQuoteRequestVehicles())
				|| getQuoteRequest().getQuoteRequest().getQuoteRequestVehicles().size() != 1) {
			super.addErrorMessage("generic.error", "Quote Request vehicle details is invalid. Please contact the Help Desk.");
		} else {
			setQuoteRequestVehicle(getQuoteRequest().getQuoteRequest().getQuoteRequestVehicles().get(0));
			if(getQuoteRequest().getQuoteRequest().getQuoteRequestType().getCode().equals(QuoteRequestTypeEnum.IMM_NEED_STOCK.getCode()) 
					|| getQuoteRequest().getQuoteRequest().getQuoteRequestType().getCode().equals(QuoteRequestTypeEnum.IMM_NEED_UPFIT_STOCK.getCode()) ) {
				if(!MALUtilities.isEmpty(getQuoteRequestVehicle().getFleetmaster())) {
					selectedStockUnitFmsId = getQuoteRequestVehicle().getFleetmaster().getFmsId();
					populateStockUnitDetails();
				}
			}
		}
		if(!MALUtilities.isEmpty(getQuoteRequest().getQuoteRequest().getDriver()) && !MALUtilities.isEmpty(getQuoteRequest().getQuoteRequest().getDriver().getDrvId())){
			List<DriverInfoVO> list = driverService.searchDriverInfo(null, getQuoteRequest().getQuoteRequest().getDriver().getDrvId(), getQuoteRequest().getQuoteRequest().getReturningFmsId(), getSelectedExternalAccount().getExternalAccountPK(), new PageRequest(0, 20), null);
			if(list != null && list.size() == 1){
				setSelectedDriver(list.get(0));
				driverNameInput = getSelectedDriver().getFullName();
				displayDriverPanel = true;
				if(MALUtilities.convertYNToBoolean(getQuoteRequest().getQuoteRequest().getReturningUnitYN())) {
					getSelectedDriver().setReturningUnit(true);
				} else {
					getSelectedDriver().setReturningUnit(false);
				}
			}else{
				setSelectedDriver(new DriverInfoVO());
			}
		}else{
			setSelectedDriver(new DriverInfoVO());
		}
		
		mfrIncentive = MALUtilities.convertYNToBoolean(quoteRequest.getQuoteRequest().getManufacturerIncentiveYN()) ? true : false;
		clientContactName = quoteRequest.getQuoteRequest().getContactName();
		clientContactPhone = quoteRequest.getQuoteRequest().getContactPhone();
		clientContactEmail = quoteRequest.getQuoteRequest().getContactEmail();
		clientContactDriver = MALUtilities.convertYNToBoolean(quoteRequest.getQuoteRequest().getContactDriverYN()) ? true : false;
		populateVehicleConfigs();
	}

	@Override
	protected void restoreOldPage() {
		String viewMode = (String) thisPage.getRestoreStateValues().get("VIEW_MODE");
		Long driverId = (Long) thisPage.getRestoreStateValues().get(ViewConstants.VIEW_PARAM_DRIVER_ID);
		Long quoteRequestId = (Long) thisPage.getRestoreStateValues().get(ViewConstants.VIEW_PARAM_QUOTE_REQUEST_ID);
		
		// Coming back from Add Driver screen. If request Id is created, restore the request else just display the added driver. 
		if(!MALUtilities.isEmpty(quoteRequestId)){
			loadSavedQuoteRequest(quoteRequestId);
			start();
			if(driverId != null){
				newDriverAdded = true;
			}
		}else{
			if(driverId != null){
				Driver drv = driverService.getDriver(driverId);
				setSelectedExternalAccount(drv.getExternalAccount());
			}else{
				setSelectedExternalAccount(new ExternalAccount(new ExternalAccountPK()));
			}
			setQuoteRequest(new QuoteRequestVO());
			getQuoteRequest().setQuoteRequest(new QuoteRequest());
			getQuoteRequest().setQuote(new QuoteRequestQuoteVO());
			getQuoteRequest().getQuoteRequest().setQuoteRequestQuotes(new ArrayList<QuoteRequestQuote>());
			getQuoteRequest().getQuoteRequest().setQuoteRequestVehicles(new ArrayList<QuoteRequestVehicle>());
		}
		
		if(driverId != null){
			List<DriverInfoVO> list = driverService.searchDriverInfo(null, driverId, null, getSelectedExternalAccount().getExternalAccountPK(), new PageRequest(0, 20), null);
			if(list != null && list.size() == 1){
				setSelectedDriver(list.get(0));
				driverNameInput = selectedDriver.getFullName();
				displayDriverPanel = true;
				getQuoteRequest().getQuoteRequest().setDriverZipCode(null);
				setDriverZipCode(null);
				populateQuoteSection();
			}
		}
		setViewMode(viewMode);
		populateVehicleConfigs();
	}
	
	@Override
	public String cancelPage(){
		String nextPage="";
		
		if(super.pageList.size() == 1) {
			if(pageList.get(0).getPageDisplayName()== "Search Quote Requests")
				nextPage = ViewConstants.QUOTE_REQUEST_SEARCH;
			else if(pageList.get(0).getPageDisplayName()== "Add Quote Request") 
				nextPage = ViewConstants.DASHBOARD_PAGE;
		} else {
			nextPage = super.cancelPage();
		}
		
		return nextPage;
	}	
	
	
	private Map<String, Object> getCurrentPageRestoreStateValuesMap() {
		Map<String, Object> restoreStateValues = new HashMap<String, Object>();
		restoreStateValues.put("VIEW_MODE", getViewMode());
		restoreStateValues.put(ViewConstants.VIEW_PARAM_QUOTE_REQUEST_ID, getQuoteRequest().getQuoteRequest().getQrqId());
		return restoreStateValues;
	}
	
	private void initializeLogBook() {
		boolean isReadOnly = false;
		StringBuilder messageBuilder; 
		boolean hasNote, hasFollowUpDate;
		List<LogBookTypeEnum> logBookTypeEnums;
							
		if(!MALUtilities.isEmpty(getQuoteRequest().getQuoteRequest().getQuoteRequestStatus())) {
			if(getQuoteRequest().getQuoteRequest().getQuoteRequestStatus().getCode().equals(QuoteRequestStatusEnum.COMPLETED.getCode())
					|| getQuoteRequest().getQuoteRequest().getQuoteRequestStatus().getCode().equals(QuoteRequestStatusEnum.CLOSED.getCode())) {
				isReadOnly = true;
			}
		}		
		getLogBookType().add(new LogBookTypeVO(LogBookTypeEnum.TYPE_QUOTE_REQEUST_NOTES, isReadOnly));
		
		
		logBookTypeEnums = new ArrayList<LogBookTypeEnum>();
		logBookTypeEnums.add(LogBookTypeEnum.TYPE_QUOTE_REQEUST_NOTES);			
		hasFollowUpDate = logBookService.hasFollowUpDate(getQuoteRequest().getQuoteRequest(), LogBookTypeEnum.TYPE_QUOTE_REQEUST_NOTES);
		hasNote = logBookService.hasLogs(getQuoteRequest().getQuoteRequest(), logBookTypeEnums);
		if(hasFollowUpDate || hasNote){
			messageBuilder = new StringBuilder();
			messageBuilder.append(hasFollowUpDate ? NOTES_FOLLOW_UP_DATE_MESSAGE : NOTES_MESSAGE);
			super.getFacesContext().addMessage("warnings", new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert", messageBuilder.toString()));			
		}     		
	}	
	
	public void initializeDocuments() {
		int numOfFiles = 10;
		DocumentFileVO fileVO;
		
		getDocumentFileVOs().clear();
		setSelectedDocumentFileVO(null);
		setSelectedDocumentToView(null);
		
		for(QuoteRequestVehicle qrv : getQuoteRequest().getQuoteRequest().getQuoteRequestVehicles()) {
			for(OnbaseUploadedDocs oud : qrv.getOnbaseUploadedDocs()) {
				fileVO = new DocumentFileVO();

				fileVO.setFileName(oud.getFileName() + "." + oud.getFileType().toLowerCase());
				fileVO.setFileExt(oud.getFileType());
				fileVO.setFileId(oud.getObdId());
				fileVO.setUploadDoc(true);
				
				if(oud.getFileName().lastIndexOf(".") > 0){
					fileVO.setFileType(oud.getFileName().substring(0, oud.getFileName().lastIndexOf(".")));					
				}else{
					fileVO.setFileType(oud.getFileName());					
				}
				
				getDocumentFileVOs().add(fileVO);				
			}
		}
		
		// TODO Create a comparator class for this
		if(!getDocumentFileVOs().isEmpty()) {
			Collections.sort(getDocumentFileVOs(), new Comparator<DocumentFileVO>() {
				public int compare(DocumentFileVO df1, DocumentFileVO df2) {
					return df1.getFileName().compareTo(df2.getFileName());
				}
			});	
		}		
		
		//TODO Remove this block of code as it is only here for mocking purposes.
		for(int i = 0; i < numOfFiles; i++) {
			fileVO = new DocumentFileVO();
			fileVO.setFileName("File Name " + i);
			fileVO.setUploadDoc(true);			
		}
	}
	
	public void start() {
		if(MALUtilities.isEmpty(getQuoteRequest().getQuoteRequest().getClientAccount())){
			super.addErrorMessage(CLIENT_ACCOUNT_UI_ID, "custom.message", "Client Account is required");	
			started=false;				
		} else {
			started=true;
		}
	}
	
	public void  decodeDealerByNameOrCode(){
		validDealer = false;
		getQuoteRequestVehicle().setDealershipName(null);
		getQuoteRequestVehicle().setDealershipContact(null);
		getQuoteRequestVehicle().setDealershipContactPhone(null);
		if(MALUtilities.isNotEmptyString(getQuoteRequestVehicle().getDealershipCode()) ){
			List<VendorLovVO> result = vendorService.getOrderingOrDeliveringVendors(getQuoteRequestVehicle().getDealershipCode(), SupplierLovBean.DELIVERING_WORKSHOP, new PageRequest(0,10));
			if(result == null || result.size() == 0){					 
				validDealer = false;
				super.addErrorMessage(DEALER_INPUT_UI_ID,"decode.noMatchFound.msg", "Dealer " + getQuoteRequestVehicle().getDealershipCode()); 
			}else if(result.size() == 1){
				VendorLovVO  vendorVO = result.get(0);
				getQuoteRequestVehicle().setDealershipName(vendorVO.getVendorName());
				getQuoteRequestVehicle().setDealershipCode(vendorVO.getVendorCode());
				ContactInfo contactInfo = unitProgressService.getDealerContactInfo(vendorVO.getEaAccountCode());
				if(MALUtilities.isEmpty(contactInfo) ) {
					contactInfo = unitProgressService.getVendorSupplierContactInfo(vendorVO.getEaAccountCode());
				}
				if(!MALUtilities.isEmpty(contactInfo) ) {
					getQuoteRequestVehicle().setDealershipContact(contactInfo.getName());
					getQuoteRequestVehicle().setDealershipContactPhone(contactInfo.getPhone());
				}
				validDealer = true;
			}else if(result.size() > 1){
				validDealer = false;
				super.addErrorMessage(DEALER_INPUT_UI_ID,"decode.notExactMatch.msg", "Dealer " +  getQuoteRequestVehicle().getDealershipCode());			
			}
		}
	}	

	public boolean isSaveWillowQuoteAllowed() {
	    if(super.hasPermission("quoteRequestAddEdit_manage") && isWillowQuoteButtonEnabled()) {
	    	return true;
	    } else {
	    	return false;
	    }
	}
	
	public void populateDriverUnitEquipments() {
	    setDrvUnitStandardEquipments(new ArrayList<String>());
	    setDrvUnitFactoryOptionalEquipments(null);
	    setDrvUnitDealerAccessories(new ArrayList<String>());
	    
	    if(!MALUtilities.isEmpty(getSelectedDriver().getQmdId())){
	    	setDrvUnitStandardEquipments(quotationService.getStandardAccessories(getSelectedDriver().getQmdId())); // Standard equipments
	    	try {
				QuotationModel qmd = quoteRequestService.getQuotationModelWithDealerAccessories(getSelectedDriver().getQmdId()); // dealer accessories
				if(!MALUtilities.isEmpty(qmd) && qmd.getQuotationDealerAccessories() != null && qmd.getQuotationDealerAccessories().size() > 0){
					for(QuotationDealerAccessory qda: qmd.getQuotationDealerAccessories()){
						drvUnitDealerAccessories.add(qda.getDealerAccessory().getDealerAccessoryCode().getDescription());
					}
				}
			} catch (MalBusinessException e1) {
				e1.printStackTrace();
			}
	    	
	    	if(drvUnitDealerAccessories != null && drvUnitDealerAccessories.size() > 0){
		    	Collections.sort(drvUnitDealerAccessories, new Comparator<String>() {
					@Override
					public int compare(String o1, String o2) {
						if(MALUtilities.isEmptyString(o1) || MALUtilities.isEmptyString(o2)) {
							return MALUtilities.isEmptyString(o1) ? 1 : -1;
						} else {
							return o1.compareTo(o2);
						}
					}
				});
	    	}
	    	
	    	VehicleOrderStatusSearchCriteriaVO searchCriteria = new VehicleOrderStatusSearchCriteriaVO();
			searchCriteria.setUnitNo(getSelectedDriver().getAllocatedUnit());
			try {
				Doc doc = docDAO.searchMainPurchaseOrder(searchCriteria);
				if(doc != null){
					setDrvUnitFactoryOptionalEquipments(orderProgressService.getOptionalAccessories(doc.getDocId())); // optional equipments
				}
			} catch (MalBusinessException e) {
				e.printStackTrace();
			}
	    }
	}
	
	public String deleteRequestOnConfirmAction() {
		String nextPage = null;		
		try{
			quoteRequestService.deleteQuoteRequest(getQuoteRequest().getQuoteRequest());
		}catch(Exception ex){
			handleException("generic.error", new String[] { "deleting quote request" }, ex, "delete");
			logger.error(ex);
			return null;
		}
		addSuccessMessage("custom.message", "Quote request has been deleted");			
		nextPage = super.cancelPage();	
		
		return nextPage;
	}
	
	public boolean isDeleteButtonEnabled() {
		boolean isEnable = false;
		
		if(super.hasPermission()) {
			if(!MALUtilities.isEmpty(getQuoteRequest().getQuoteRequest().getQuoteRequestStatus()) && !quoteRequestService.isRework(getQuoteRequest().getQuoteRequest())) {			
				if(getQuoteRequest().getQuoteRequest().getQuoteRequestStatus().getCode().equals(QuoteRequestStatusEnum.SAVED.getCode())
						|| getQuoteRequest().getQuoteRequest().getQuoteRequestStatus().getCode().equals(QuoteRequestStatusEnum.SUBMITTED.getCode())){
					isEnable = true;
				}
			}	
		}
		
		return isEnable;
	}
	
	public void populateClientContactDetails() {
		Contact contact = contactDAO.findById(selectedClientContactId).orElse(null);
		StringBuffer name = new StringBuffer();
		if(contact.getFirstName() != null) {
			name.append(contact.getFirstName());
		}
		if(contact.getLastName() != null) {
			name.append(" " + contact.getLastName());
		}
		
		clientContactName = name.toString();
		clientContactEmail = contact.getEmail();
		clientContactPhone = getSelectedContactDefaultPhone();

	}
	
	private String getSelectedContactDefaultPhone() {
		StringBuffer phoneNumber = new StringBuffer();
		PhoneNumber savedPhone = null;
		ContactAddress contactAddress = contactInformationDAO.findAddressByAddressType(selectedClientContactId, "POST");
		if(contactAddress != null && !contactAddress.getPhoneNumbers().isEmpty()) {
			for (PhoneNumber phone : contactAddress.getPhoneNumbers().values()) {
			    if(phone.getPreferredInd().equalsIgnoreCase("Y") || contactAddress.getPhoneNumbers().size() == 1) {
			    	savedPhone =  (PhoneNumber) SerializationUtils.clone(phone);
			    	break;
			    } else if(phone.getType().getNumberType().equalsIgnoreCase("WORK")) {
			    	savedPhone =  (PhoneNumber) SerializationUtils.clone(phone);
			    }
			}

			if(savedPhone != null) {
		    	if(savedPhone.getAreaCode() != null) {
		    		phoneNumber.append(savedPhone.getAreaCode() + "-");
		    	}
		    	phoneNumber.append(savedPhone.getNumber());
		    	if(savedPhone.getExtensionNumber() != null) {
		    		phoneNumber.append(" " + savedPhone.getExtensionNumber());
		    	}
			}
		
		}
		return phoneNumber.toString();
	}
	
	public void deleteWillowQuote() {
		getQuoteRequestQuote().setQuoId(null);
		setSelectedQuoteRequestQuoteModel(null);
	}
	
	public void clearSelectedDealer() {
		getQuoteRequestVehicle().setDealershipCode(null);
	}
	
    public void navigateToAddEditConfiguration(Long configId){
    	saveRestoreStateValues(getCurrentPageRestoreStateValuesMap());
    	Map<String, Object> nextPageValues = new HashMap<String, Object>();
   		nextPageValues.put(ViewConstants.VIEW_PARAM_VEHICLE_CONFIG_ID, String.valueOf(configId));
    	saveNextPageInitStateValues(nextPageValues);
		forwardToURL(ViewConstants.VEHICLE_CONFIGURATION_ADD);
    }

    public void addConfig() {
    	
    	if(MALUtilities.isEmpty(applicationName)) {
    		super.addErrorMessageSummary(APPLICATION_NAME_UI_ID, "custom.message", "Application Name is required");
    	} else {
        	VehicleConfiguration vehicleConfiguration = new VehicleConfiguration();
        	vehicleConfiguration.setExternalAccount(selectedExternalAccount);
        	vehicleConfiguration.setDescription(applicationName);
        	vehicleConfiguration.setOrderType(orderTypeDAO.findById("S").orElse(null) );		
    		vehicleConfiguration.setEnteredDate(new Date());
    		vehicleConfiguration.setEnteredUser(super.getLoggedInUser().getEmployeeNo());
    		vehicleConfiguration.setObsoleteYN("N");

    		StringTokenizer st = new StringTokenizer(vehicleConfiguration.getDescription(), " ");
    		String configGroupingName = "";
    		while (st.hasMoreElements()) {
    			configGroupingName = (String) st.nextElement();
    			break;
    		}
    		
    		VehicleConfigGrouping vehicleConfigGrouping = new VehicleConfigGrouping();
    		vehicleConfigGrouping.setName(configGroupingName.toUpperCase());
    		vehicleConfigGrouping.setVehicleConfiguration(vehicleConfiguration);
    		vehicleConfigGrouping.setVehicleConfigUpfitQuotes(new ArrayList<VehicleConfigUpfitQuote>());
    		List<VehicleConfigGrouping> vehicleConfigGroupings = new ArrayList<VehicleConfigGrouping>();
    		vehicleConfigGroupings.add(vehicleConfigGrouping);
    		vehicleConfiguration.setVehicleConfigGroupings(vehicleConfigGroupings);
    		
    		vehicleConfigurationService.saveUpdateConfiguration(vehicleConfiguration);	
    		
    		populateVehicleConfigs();
    		
    		super.addSuccessMessage("process.success", "Application (" + vehicleConfiguration.getDescription() + ")");

    		applicationName = null;

    		RequestContext.getCurrentInstance().execute("postAddConfig();");
    	}

    	
    }
	
	public List<QuoteRequestType> getQuoteRequestTypeList() {
		return quoteRequestTypeList;
	}

	public void setQuoteRequestTypeList(List<QuoteRequestType> quoteRequestTypeList) {
		this.quoteRequestTypeList = quoteRequestTypeList;
	}

	public String getClient() {
		return client;
	}

	public void setClient(String client) {
		this.client = client;
	}

	public QuoteRequestVO getQuoteRequest() {
		return quoteRequest;
	}

	public void setQuoteRequest(QuoteRequestVO quoteRequest) {
		this.quoteRequest = quoteRequest;
	}

	public ExternalAccount getSelectedExternalAccount() {
		return selectedExternalAccount;
	}

	public void setSelectedExternalAccount(ExternalAccount selectedExternalAccount) {
		this.selectedExternalAccount = selectedExternalAccount;
	}

	public String getCustomerLOVParam() {
		return customerLOVParam;
	}

	public void setCustomerLOVParam(String customerLOVParam) {
		this.customerLOVParam = customerLOVParam;
	}
	
	public String getViewMode() {
		return viewMode;
	}
	
	public void setViewMode(String viewMode) {
		this.viewMode = viewMode;
	}

	public List<VehicleDeliveryChargeType> getVehicleDeliveryChargeTypeList() {
		return vehicleDeliveryChargeTypeList;
	}

	public void setVehicleDeliveryChargeTypeList(
			List<VehicleDeliveryChargeType> vehicleDeliveryChargeTypeList) {
		this.vehicleDeliveryChargeTypeList = vehicleDeliveryChargeTypeList;
	}

	public QuoteRequestVehicle getQuoteRequestVehicle() {
		return quoteRequestVehicle;
	}

	public void setQuoteRequestVehicle(QuoteRequestVehicle quoteRequestVehicle) {
		this.quoteRequestVehicle = quoteRequestVehicle;
	}

	public List<PlateTypeCode> getPlateTypeCodeList() {
		return plateTypeCodeList;
	}

	public void setPlateTypeCodeList(List<PlateTypeCode> plateTypeCodeList) {
		this.plateTypeCodeList = plateTypeCodeList;
	}

	public List<QuotationProfile> getQuotationProfiles() {
		return quotationProfiles;
	}

	public void setQuotationProfiles(List<QuotationProfile> quotationProfiles) {
		this.quotationProfiles = quotationProfiles;
	}

	public DriverInfoVO getSelectedDriver() {
		return selectedDriver;
	}

	public void setSelectedDriver(DriverInfoVO selectedDriver) {
		this.selectedDriver = selectedDriver;
	}

	public String getDriverZipCode() {
		return driverZipCode;
	}

	public void setDriverZipCode(String driverZipCode) {
		this.driverZipCode = driverZipCode;
	}

	public List<QuoteRequestPaymentType> getPaymentTypes() {
		return paymentTypes;
	}

	public void setPaymentTypes(List<QuoteRequestPaymentType> paymentTypes) {
		this.paymentTypes = paymentTypes;
	}

	public List<QuoteRequestDepreciationMethod> getDepreciationMethods() {
		return depreciationMethods;
	}

	public void setDepreciationMethods(List<QuoteRequestDepreciationMethod> depreciationMethods) {
		this.depreciationMethods = depreciationMethods;
	}

	public QuoteRequestQuote getQuoteRequestQuote() {
		return quoteRequestQuote;
	}

	public void setQuoteRequestQuote(QuoteRequestQuote quoteRequestQuote) {
		this.quoteRequestQuote = quoteRequestQuote;
	}

	public List<DriverGradeGroupCode> getDriverGradeGroups() {
		return driverGradeGroups;
	}

	public void setDriverGradeGroups(List<DriverGradeGroupCode> driverGradeGroups) {
		this.driverGradeGroups = driverGradeGroups;
	}
	public List<QuotationStepStructureVO> getDefinitionSteps() {
		return definitionSteps;
	}

	public void setDefinitionSteps(List<QuotationStepStructureVO> definitionSteps) {
		this.definitionSteps = definitionSteps;
	}

	public String getDriverDeliveryAddress() {
		return driverDeliveryAddress;
	}

	public void setDriverDeliveryAddress(String driverDeliveryAddress) {
		this.driverDeliveryAddress = driverDeliveryAddress;
	}

	public StockUnitVO getStockUnit() {
		return stockUnit;
	}

	public void setStockUnit(StockUnitVO stockUnit) {
		this.stockUnit = stockUnit;
	}

	public long getSelectedStockUnitFmsId() {
		return selectedStockUnitFmsId;
	}

	public void setSelectedStockUnitFmsId(long selectedStockUnitFmsId) {
		this.selectedStockUnitFmsId = selectedStockUnitFmsId;
	}

	public String getSplMsgforStep() {
		return splMsgforStep;
	}

	public void setSplMsgforStep(String splMsgforStep) {
		this.splMsgforStep = splMsgforStep;
	}

	public List<LogBookTypeVO> getLogBookType() {
		return logBookType;
	}

	public void setLogBookType(List<LogBookTypeVO> logBookType) {
		this.logBookType = logBookType;
	}

	public String getAssignedUser() {
		return assignedUser;
	}

	public void setAssignedUser(String assignedUser) {
		this.assignedUser = assignedUser;
	}

	public Boolean getIsQuoteRequestInProgress() {
		return isQuoteRequestInProgress;
	}

	public void setIsQuoteRequestInProgress(Boolean isQuoteRequestInProgress) {
		this.isQuoteRequestInProgress = isQuoteRequestInProgress;
	}

	public QuoteRequestCloseReason getSelectedQuoteRequestCloseReason() {
		return selectedQuoteRequestCloseReason;
	}

	public void setSelectedQuoteRequestCloseReason(QuoteRequestCloseReason selectedQuoteRequestCloseReason) {
		this.selectedQuoteRequestCloseReason = selectedQuoteRequestCloseReason;
	}

	public String getSelectedQuoteRequestCloseReasonNote() {
		return selectedQuoteRequestCloseReasonNote;
	}

	public void setSelectedQuoteRequestCloseReasonNote(String selectedQuoteRequestCloseReasonNote) {
		this.selectedQuoteRequestCloseReasonNote = selectedQuoteRequestCloseReasonNote;
	}

	public List<QuoteRequestCloseReason> getQuoteRequestCloseReasons() {
		return quoteRequestCloseReasons;
	}

	public void setQuoteRequestCloseReasons(List<QuoteRequestCloseReason> quoteRequestCloseReasons) {
		this.quoteRequestCloseReasons = quoteRequestCloseReasons;
	}

	public List<String> getStandardEquipment() {
		return standardEquipment;
	}

	public void setStandardEquipment(List<String> standardEquipment) {
		this.standardEquipment = standardEquipment;
	}

	public List<String> getModelEquipment() {
		return modelEquipment;
	}

	public void setModelEquipment(List<String> modelEquipment) {
		this.modelEquipment = modelEquipment;
	}

	public List<String> getDealerEquipment() {
		return dealerEquipment;
	}

	public void setDealerEquipment(List<String> dealerEquipment) {
		this.dealerEquipment = dealerEquipment;
	}

	public boolean isStarted() {
		return started;
	}

	public void setStarted(boolean started) {
		this.started = started;
	}

	public boolean isStartButtonDisabled() {
		if(!MALUtilities.isEmpty(selectedExternalAccount.getAccountName()) && 
				!MALUtilities.isEmpty(quoteRequest.getQuoteRequest().getQuoteRequestType()) && hasPermission()) {
			startButtonDisabled = false;
		} else {
			startButtonDisabled = true;
		}
		return startButtonDisabled;
	}

	public void setStartButtonDisabled(boolean startButtonDisabled) {
		this.startButtonDisabled = startButtonDisabled;
	}

	public List<DocumentFileVO> getDocumentFileVOs() {
		return documentFileVOs;
	}

	public void setDocumentFileVOs(List<DocumentFileVO> documentFileVOs) {
		this.documentFileVOs = documentFileVOs;
	}

    public List<QuoteRequestQuoteModelVO> getQuoteRequestQuoteModels() {
		return quoteRequestQuoteModels;
	}

	public void setQuoteRequestQuoteModels(List<QuoteRequestQuoteModelVO> quoteRequestQuoteModels) {
		this.quoteRequestQuoteModels = quoteRequestQuoteModels;
	}

	public QuoteRequestQuoteModelVO getSelectedQuoteRequestQuoteModel() {
		return selectedQuoteRequestQuoteModel;
	}

	public void setSelectedQuoteRequestQuoteModel(QuoteRequestQuoteModelVO selectedQuoteRequestQuoteModel) {
		this.selectedQuoteRequestQuoteModel = selectedQuoteRequestQuoteModel;
	}

	public String getDriverNameInput() {
		return driverNameInput;
	}

	public void setDriverNameInput(String driverNameInput) {
		this.driverNameInput = driverNameInput;
	}

	public boolean isDisplayDriverPanel() {
		return displayDriverPanel;
	}

	public void setDisplayDriverPanel(boolean displayDriverPanel) {
		this.displayDriverPanel = displayDriverPanel;
	}

	public boolean isValidDealer() {
		return validDealer;
	}

	public void setValidDealer(boolean validDealer) {
		this.validDealer = validDealer;
	}

	public DocumentFileVO getSelectedDocumentFileVO() {
		return selectedDocumentFileVO;
	}

	public void setSelectedDocumentFileVO(DocumentFileVO selectedDocumentFileVO) {
		this.selectedDocumentFileVO = selectedDocumentFileVO;
	}

	public StreamedContent getSelectedDocumentToView() {
		return selectedDocumentToView;
	}

	public void setSelectedDocumentToView(StreamedContent selectedDocumentToView) {
		this.selectedDocumentToView = selectedDocumentToView;
	}

	public String getDrvUnitFactoryOptionalEquipments() {
		return drvUnitFactoryOptionalEquipments;
	}

	public void setDrvUnitFactoryOptionalEquipments(
			String drvUnitFactoryOptionalEquipments) {
		this.drvUnitFactoryOptionalEquipments = drvUnitFactoryOptionalEquipments;
	}

	public List<String> getDrvUnitStandardEquipments() {
		return drvUnitStandardEquipments;
	}

	public void setDrvUnitStandardEquipments(List<String> drvUnitStandardEquipments) {
		this.drvUnitStandardEquipments = drvUnitStandardEquipments;
	}

	public List<String> getDrvUnitDealerAccessories() {
		return drvUnitDealerAccessories;
	}

	public void setDrvUnitDealerAccessories(List<String> drvUnitDealerAccessories) {
		this.drvUnitDealerAccessories = drvUnitDealerAccessories;
	}

	public boolean isNewDriverAdded() {
		return newDriverAdded;
	}

	public void setNewDriverAdded(boolean newDriverAdded) {
		this.newDriverAdded = newDriverAdded;
	}

	public String getAssignedWillowUser() {
		return assignedWillowUser;
	}

	public void setAssignedWillowUser(String assignedWillowUser) {
		this.assignedWillowUser = assignedWillowUser;
	}

	public String getSelectedAssignToEmployeeNo() {
		return selectedAssignToEmployeeNo;
	}

	public void setSelectedAssignToEmployeeNo(String selectedAssignToEmployeeNo) {
		this.selectedAssignToEmployeeNo = selectedAssignToEmployeeNo;
	}

	public boolean isMfrIncentive() {
		return mfrIncentive;
	}

	public void setMfrIncentive(boolean mfrIncentive) {
		this.mfrIncentive = mfrIncentive;
	}

	public String getReworkReason() {
		return reworkReason;
	}

	public void setReworkReason(String reworkReason) {
		this.reworkReason = reworkReason;
	}

	public String getClientContactName() {
		return clientContactName;
	}

	public void setClientContactName(String clientContactName) {
		this.clientContactName = clientContactName;
	}

	public String getClientContactPhone() {
		return clientContactPhone;
	}

	public void setClientContactPhone(String clientContactPhone) {
		this.clientContactPhone = clientContactPhone;
	}

	public String getClientContactEmail() {
		return clientContactEmail;
	}

	public void setClientContactEmail(String clientContactEmail) {
		this.clientContactEmail = clientContactEmail;
	}

	public Long getselectedClientContactId() {
		return selectedClientContactId;
	}

	public void setselectedClientContactId(Long selectedClientContactId) {
		this.selectedClientContactId = selectedClientContactId;
	}

	public boolean isClientContactDriver() {
		return clientContactDriver;
	}

	public void setClientContactDriver(boolean clientContactDriver) {
		this.clientContactDriver = clientContactDriver;
	}

	public boolean isJustFlippedToReworked() {
		return justFlippedToReworked;
	}

	public void setJustFlippedToReworked(boolean justFlippedToReworked) {
		this.justFlippedToReworked = justFlippedToReworked;
	}

	public List<QuoteRequestConfigVO> getVehicleConfigs() {
		return vehicleConfigs;
	}

	public void setVehicleConfigs(List<QuoteRequestConfigVO> vehicleConfigs) {
		this.vehicleConfigs = vehicleConfigs;
	}

	public String getApplicationName() {
		return applicationName;
	}

	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}


}
