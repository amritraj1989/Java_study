package com.mikealbert.vision.view.bean;

import static com.mikealbert.vision.comparator.VehicleConfigModelVOComparator.VCM_ID_SORT;
import static com.mikealbert.vision.comparator.VehicleConfigModelVOComparator.VCM_STATUS_SORT;
import static com.mikealbert.vision.comparator.VehicleConfigModelVOComparator.getComparator;
import static com.mikealbert.vision.comparator.VehicleConfigVendorQuoteVOComparator.VUQ_ID_SORT;
import static com.mikealbert.vision.comparator.VehicleConfigVendorQuoteVOComparator.VUQ_STATUS_SORT;
import static com.mikealbert.vision.comparator.VehicleConfigVendorQuoteVOComparator.getComparator;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.faces.event.AjaxBehaviorEvent;

import org.primefaces.component.autocomplete.AutoComplete;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import com.mikealbert.common.MalLogger;
import com.mikealbert.common.MalLoggerFactory;
import com.mikealbert.data.dao.OnbaseUploadedDocsDAO;
import com.mikealbert.data.dao.OrderTypeDAO;
import com.mikealbert.data.entity.ExternalAccount;
import com.mikealbert.data.entity.ExternalAccountPK;
import com.mikealbert.data.entity.MakeModelRange;
import com.mikealbert.data.entity.Model;
import com.mikealbert.data.entity.ObjectLogBook;
import com.mikealbert.data.entity.OnbaseUploadedDocs;
import com.mikealbert.data.entity.UpfitAssessmentAnswer;
import com.mikealbert.data.entity.UpfitterQuote;
import com.mikealbert.data.entity.VehicleConfigGrouping;
import com.mikealbert.data.entity.VehicleConfigModel;
import com.mikealbert.data.entity.VehicleConfigUpfitQuote;
import com.mikealbert.data.entity.VehicleConfiguration;
import com.mikealbert.data.enumeration.AccountStatusEnum;
import com.mikealbert.data.enumeration.LogBookTypeEnum;
import com.mikealbert.data.vo.DocumentFileVO;
import com.mikealbert.data.vo.ModelSearchCriteriaVO;
import com.mikealbert.data.vo.ModelSearchResultVO;
import com.mikealbert.data.vo.UpfitterSearchCriteriaVO;
import com.mikealbert.data.vo.UpfitterSearchResultVO;
import com.mikealbert.data.vo.VehicleConfigModelVO;
import com.mikealbert.data.vo.VehicleConfigVendorQuoteVO;
import com.mikealbert.service.CustomerAccountService;
import com.mikealbert.service.LogBookService;
import com.mikealbert.service.ModelSearchService;
import com.mikealbert.service.ModelService;
import com.mikealbert.service.OnbaseArchivalService;
import com.mikealbert.service.UpfitAssessmentService;
import com.mikealbert.service.UpfitterService;
import com.mikealbert.service.VehicleConfigurationService;
import com.mikealbert.service.enumeration.OnbaseDocTypeEnum;
import com.mikealbert.util.MALUtilities;
import com.mikealbert.vision.view.ViewConstants;
import com.mikealbert.vision.vo.LogBookTypeVO;

@Component
@Scope("view")
public class VehicleConfigurationAddEditBean extends StatefulBaseBean {
	private static final long serialVersionUID = -6624207350306733465L;
	private MalLogger logger = MalLoggerFactory.getLogger(this.getClass());
	
	@Resource VehicleConfigurationService vehicleConfigurationService;
	@Resource CustomerAccountService customerAccountService;
	@Resource UpfitterService upfitterService;
	@Resource ModelSearchService modelSearchService;
	@Resource ModelService modelService;
	@Resource OrderTypeDAO orderTypeDAO;
	@Resource OnbaseArchivalService onbaseArchivalService;	
	@Resource OnbaseUploadedDocsDAO onbaseUploadedDocsDAO;
	@Resource LogBookService logBookService;
	@Resource UpfitAssessmentService upfitAssessmentService;

	private String vehicleConfigId;
	private VehicleConfiguration vehicleConfiguration;
	private List<VehicleConfigGrouping> vehicleConfigGroupings;
	private VehicleConfigGrouping selectedVehicleConfigGrouping;
	private ExternalAccount clientAccount;
	private boolean locateFlag = false;	
	private List<VehicleConfigModelVO> vehicleConfigModelVOs = new ArrayList<VehicleConfigModelVO>();
	private VehicleConfigModelVO selectedVehicleConfigModelVO;
	private List<VehicleConfigModelVO> dialogVehicleConfigModelVOs = new ArrayList<VehicleConfigModelVO>();
	private VehicleConfigModelVO newVehicleConfigModelVO;
	private ModelSearchCriteriaVO criteria;
	private ModelSearchResultVO selectedModel;
	private List<String> years;
	private List<String> makes;

	private UpfitterSearchResultVO configurationVendor;	
	private List<VehicleConfigVendorQuoteVO> vehicleConfigVendorQuoteVOs = new ArrayList<VehicleConfigVendorQuoteVO>();
	private VehicleConfigVendorQuoteVO selectedVehicleConfigVendorQuoteVO;
	private List<UpfitterQuote> upfitterQuoteList = new ArrayList<UpfitterQuote>();
	private boolean vqnEditMode = false;
	
	private String viewMode;
	private boolean showAllVehTypeRecords = false;
	private String  vehTypeToggleLabel =  "Show All";
	private boolean showAllVQRecords = false;
	private String  vendorQuoteToggleLabel =  "Show All";
	private static final String UI_ID_CLIENT = "customerAccount";
	private static final String UI_ID_APPLICATION = "applicationName";
	private static final String UI_ID_VENDOR = "vendorsAC";
	private static final String UI_ID_VENDOR_QUOTE_NO = "vendorQuoteList";
	private static final String UI_ID_VENDOR_QUOTE_DESC = "quoteDescription";
	private static final String UI_ID_QUOTE_DATE = "quoteDate";
	private static final String STATUS_OBSOLETE = "Obsolete";
	private static final String STATUS_ACTIVE = "Active";
	private static final String DOC_TYPE_QUOTE = "Quote";
	private static final String DOC_TYPE_VISUAL = "Visual";
	private static final int SCREEN_FIXED_HIGHT = 570;
	private StreamedContent previewfile;
	private static final String MANAGE_PERMISSION_RESOURCE_NAME  = "manageVehConfig";
	private static final String UPFITTER_QUOTES_OBJECT_TYPE = "UPFITTER_QUOTES";
	private boolean hasManageVehConfigPermission;
	private boolean enableIntExtColorFlag=false;
	private String  vehConfigToggleStatus =  "Active";
	private List<LogBookTypeVO> logBookType = new ArrayList<LogBookTypeVO>();	
	private String upfitAssesmentBtnLabel = "Add Assessment";
	private boolean hasUpfitAssessment = false;
	

	@PostConstruct
	public void init() {
		openPage();
		setHasManageVehConfigPermission(hasPermission(MANAGE_PERMISSION_RESOURCE_NAME));
		setShowAllVehTypeRecords(false);
		setShowAllVQRecords(false);
		setVehTypeToggleLabel("Show All");
		setVendorQuoteToggleLabel("Show All");
		setSelectedVehicleConfigVendorQuoteVO(new VehicleConfigVendorQuoteVO());
		initializeLogBook();
		
		try {
			if(!MALUtilities.isEmptyString(this.vehicleConfigId)) {
				initEditVehicleConfig();
			} else {
				initAddVehicleConfig();
			}
		} catch (Exception e) {
			super.addErrorMessage("generic.error", e.getMessage());
		}				
	}

	private void initEditVehicleConfig() {		
		setViewMode(ViewConstants.VIEW_MODE_EDIT);
		thisPage.setPageDisplayName(ViewConstants.DISPLAY_NAME_VEHICLE_CONFIGURATION_EDIT);
		loadData();
		setUpfitAssessmentExists();
	}

	private void initAddVehicleConfig() {		
		setViewMode(ViewConstants.VIEW_MODE_ADD);
		thisPage.setPageDisplayName(ViewConstants.DISPLAY_NAME_VEHICLE_CONFIGURATION_ADD);
		setVehicleConfiguration(new VehicleConfiguration());
		setVehicleConfigGroupings(new ArrayList<VehicleConfigGrouping>());
		setSelectedVehicleConfigGrouping(new VehicleConfigGrouping());
	}

	private void loadData() {
		vehicleConfiguration = vehicleConfigurationService.getVehicleConfigurationWithGroupingAndModel(Long.valueOf(vehicleConfigId));
		selectedVehicleConfigGrouping = vehicleConfiguration.getVehicleConfigGroupings().get(0);
		clientAccount = vehicleConfiguration.getExternalAccount();
		this.setLocateFlag(vehicleConfiguration.getOrderType().getCode().equalsIgnoreCase("L"));
		if(isLocateFlag() && isHasManageVehConfigPermission())
			setEnableIntExtColorFlag(true);
		
		if(vehicleConfiguration.getObsoleteYN().equals("Y"))
			this.setVehConfigToggleStatus(STATUS_OBSOLETE);
		else
			this.setVehConfigToggleStatus(STATUS_ACTIVE);
		
		VehicleConfigModelVO vehicleConfigModelVO = null;
		for (VehicleConfigModel configModel : vehicleConfiguration.getVehicleConfigModels()) {
			vehicleConfigModelVO =  new VehicleConfigModelVO();
			vehicleConfigModelVO.setVcmId(configModel.getVcmId());
			vehicleConfigModelVO.setMake(configModel.getMake());
			vehicleConfigModelVO.setMfgCode(configModel.getMfgCode());	
			vehicleConfigModelVO.setYear(configModel.getYear());
			if("Y".equals(configModel.getObsoleteYn())){
				vehicleConfigModelVO.setStatus(STATUS_OBSOLETE);
			} else {
				vehicleConfigModelVO.setStatus(STATUS_ACTIVE);
			}
					
			if(!MALUtilities.isEmpty(configModel.getModel())){
				vehicleConfigModelVO.setModelId(configModel.getModel().getModelId());
				vehicleConfigModelVO.setTrim(configModel.getModel().getModelDescription());
			}
			
			if(!MALUtilities.isEmpty(configModel.getMakeModelRange())){
				vehicleConfigModelVO.setMrgId(configModel.getMakeModelRange().getMrgId());
				vehicleConfigModelVO.setModelCode(configModel.getMakeModelRange().getModelCode());
				vehicleConfigModelVO.setModel(configModel.getMakeModelRange().getDescription());
			}
			
			getVehicleConfigModelVOs().add(vehicleConfigModelVO);
		}
			
		for (VehicleConfigGrouping vehicleConfigGrouping : vehicleConfiguration.getVehicleConfigGroupings()) {
			VehicleConfigVendorQuoteVO vehicleConfigVendorQuoteVO = null;
			for (VehicleConfigUpfitQuote vehicleConfigUpfitQuote : vehicleConfigGrouping.getVehicleConfigUpfitQuotes()) {
				vehicleConfigVendorQuoteVO = new VehicleConfigVendorQuoteVO();				
				vehicleConfigVendorQuoteVO.setVuqId(vehicleConfigUpfitQuote.getVuqId());
				vehicleConfigVendorQuoteVO.setQuoteDate(vehicleConfigUpfitQuote.getUpfitterQuote().getQuoteDate());
				vehicleConfigVendorQuoteVO.setQuoteExpDate(vehicleConfigUpfitQuote.getUpfitterQuote().getExpirationDate());
				vehicleConfigVendorQuoteVO.setQuoteNumber(vehicleConfigUpfitQuote.getUpfitterQuote().getQuoteNumber());
				vehicleConfigVendorQuoteVO.setQuoteDescription(vehicleConfigUpfitQuote.getUpfitterQuote().getDescription());				
				vehicleConfigVendorQuoteVO.setcId(vehicleConfigUpfitQuote.getUpfitterQuote().getExternalAccount().getExternalAccountPK().getCId());
				vehicleConfigVendorQuoteVO.setVendorName(vehicleConfigUpfitQuote.getUpfitterQuote().getExternalAccount().getAccountName());
				vehicleConfigVendorQuoteVO.setVendorCode(vehicleConfigUpfitQuote.getUpfitterQuote().getExternalAccount().getExternalAccountPK().getAccountCode());
				vehicleConfigVendorQuoteVO.setVendorType(vehicleConfigUpfitQuote.getUpfitterQuote().getExternalAccount().getExternalAccountPK().getAccountType());
				
				List<OnbaseUploadedDocs> listUploadedDocsForUfq = onbaseUploadedDocsDAO.getOnBaseUploadedDocsByObjectIdAndType(
																								String.valueOf(vehicleConfigUpfitQuote.getUpfitterQuote().getUfqId()),
																								UPFITTER_QUOTES_OBJECT_TYPE);
				if(!MALUtilities.isEmpty(listUploadedDocsForUfq)){
					vehicleConfigVendorQuoteVO.setDocuments(new ArrayList<DocumentFileVO>());
					
					DocumentFileVO documentFileVO = null;
					for (OnbaseUploadedDocs onbaseUploadedDocs : listUploadedDocsForUfq) {
						documentFileVO = new DocumentFileVO();
						documentFileVO.setFileName(onbaseUploadedDocs.getFileName());
						documentFileVO.setFileExt(onbaseUploadedDocs.getFileType());
						documentFileVO.setFileId(onbaseUploadedDocs.getObdId());
						documentFileVO.setUploadDoc(true);
						if(onbaseUploadedDocs.getFileName().lastIndexOf(".") > 0){
							documentFileVO.setFileType(onbaseUploadedDocs.getFileName().substring(0, onbaseUploadedDocs.getFileName().lastIndexOf(".")));
						}else{
							documentFileVO.setFileType(onbaseUploadedDocs.getFileName());
						}
						vehicleConfigVendorQuoteVO.getDocuments().add(documentFileVO);
					}
				}
			//	List<DocumentFileVO> docList =  vehicleConfigVendorQuoteVO.getDocuments();
				if(!vehicleConfigVendorQuoteVO.getDocuments().isEmpty()) {
					Collections.sort(vehicleConfigVendorQuoteVO.getDocuments(), new Comparator<DocumentFileVO>() {
						public int compare(DocumentFileVO df1, DocumentFileVO df2) {
							return df1.getFileName().compareTo(df2.getFileName());
						}
					});
				//	vehicleConfigVendorQuoteVO.setDocuments(docList);	
				}
			
				if("Y".equals(vehicleConfigUpfitQuote.getObsoleteYn())){
					vehicleConfigVendorQuoteVO.setStatus(STATUS_OBSOLETE);
				} else {
					vehicleConfigVendorQuoteVO.setStatus(STATUS_ACTIVE);
				}
				
				getVehicleConfigVendorQuoteVOs().add(vehicleConfigVendorQuoteVO);
			}	
		}
	}

	protected void loadNewPage() {
		thisPage.setPageUrl(ViewConstants.VEHICLE_CONFIGURATION_ADD);
		String vehicleConfigId = (String) thisPage.getInputValues().get(ViewConstants.VIEW_PARAM_VEHICLE_CONFIG_ID);
		this.viewMode = (String) thisPage.getInputValues().get(ViewConstants.VIEW_PARAM_MODE);
		if(vehicleConfigId != null) {
			this.vehicleConfigId = vehicleConfigId;
			this.viewMode = ViewConstants.VIEW_MODE_EDIT;
		}else{
			this.viewMode = ViewConstants.VIEW_MODE_ADD;
		}		
	}

	protected void restoreOldPage() { 
		this.vehicleConfiguration = (VehicleConfiguration) thisPage.getRestoreStateValues().get("VEHICLE_CONFIG");
		if(!MALUtilities.isEmpty(vehicleConfiguration) && !MALUtilities.isEmpty(vehicleConfiguration.getVcfId())){
			this.vehicleConfigId = String.valueOf(vehicleConfiguration.getVcfId());
		}
		
		if(vehicleConfigId != null) {
			this.viewMode = ViewConstants.VIEW_MODE_EDIT;
		}else{
			this.viewMode = ViewConstants.VIEW_MODE_ADD;
		}
		setUpfitAssessmentExists();
			
	}

	public String cancel() {
		return super.cancelPage();
	}
	
	public List<String> autoCompleteMfgCodeListener(String term) {
		List<String> mfgCodes = new ArrayList<String>();
		mfgCodes = modelSearchService.findDistinctMfgCodes(term, new PageRequest(0, 50));
		return mfgCodes;
	}
	
	public void autoCompleteMfgCodeSelectListener() {
		updateSearchCriteria();
		setYears(modelSearchService.findDistinctYears(getCriteria()));
		setMakes(modelSearchService.findDistinctMakes(getCriteria())); 
	}
	
	public void autoCompleteMfgCodeChangeListener(AjaxBehaviorEvent event) {
		AutoComplete uIComponent = (AutoComplete) event.getComponent();
		String mfgCode = (String) uIComponent.getValue();
		updateSearchCriteria();
		criteria.setMfgCode(mfgCode);
		setYears(modelSearchService.findDistinctYears(getCriteria()));
		setMakes(modelSearchService.findDistinctMakes(getCriteria())); 
        
    }

	public List<ExternalAccount> autoCompleteClientListener(String term) {
		List<ExternalAccount> clients = new ArrayList<ExternalAccount>();

		clients = customerAccountService.findAllCustomerAccountsByNameOrCode(term, getLoggedInUser().getCorporateEntity(), new PageRequest(0, 50));
		if(!clients.isEmpty()) {
			Collections.sort(clients, new Comparator<ExternalAccount>() {
				public int compare(ExternalAccount uf1, ExternalAccount uf2) {
					return uf1.getAccountName().toLowerCase().compareTo(uf2.getAccountName().toLowerCase());
				}
			});
		}

		return clients;
	}
	
	public void addVehicleTypeListener() {
		getDialogVehicleConfigModelVOs().clear();
		initializeVehicleTypeDialog();
		
	}
	
	private void initializeVehicleTypeDialog() {
		setNewVehicleConfigModelVO(new VehicleConfigModelVO()); 
		getNewVehicleConfigModelVO().setStatus(STATUS_ACTIVE);
		setCriteria(new ModelSearchCriteriaVO());
		setYears(modelSearchService.findDistinctYears(getCriteria()));
		setMakes(modelSearchService.findDistinctMakes(getCriteria()));
		setSelectedModel(null);
	}
	
	public void changeMfgCode() {
		updateSearchCriteria();
		setYears(modelSearchService.findDistinctYears(getCriteria()));
		setMakes(modelSearchService.findDistinctMakes(getCriteria()));
	}

	/*public void changeMake() {
		updateSearchCriteria();
	}*/
	
	public void changeYear() {
		updateSearchCriteria();
		setMakes(modelSearchService.findDistinctMakes(getCriteria()));
	}
	
	public void updateSearchCriteria() {
		VehicleConfigModelVO configModelVO = getNewVehicleConfigModelVO();
		getCriteria().setMfgCode(configModelVO.getMfgCode() != null ? configModelVO.getMfgCode() : "");
		getCriteria().setYear(configModelVO.getYear() != null ? configModelVO.getYear() : "");
		getCriteria().setModelType("");
		getCriteria().setMake(configModelVO.getMake() != null ? configModelVO.getMake() : "");
		getCriteria().setModel(configModelVO.getModel() != null ? configModelVO.getModel() : "");
		if(MALUtilities.isNotEmptyString(configModelVO.getModel()))  {
			getCriteria().setModelCode(configModelVO.getModelCode() != null ? configModelVO.getModelCode() : "");
		} else {
			getCriteria().setModelCode("");
		}
		getCriteria().setTrim(configModelVO.getTrim() != null ? configModelVO.getTrim() : "");
	}

	public List<ModelSearchResultVO> autoCompleteTrimsListener(String term) {
		updateSearchCriteria();
		getCriteria().setTrim(term);
		List<ModelSearchResultVO> trims = modelSearchService.findModels(getCriteria(), new PageRequest(0, 50), null);
		if(!trims.isEmpty()) {
			Collections.sort(trims, new Comparator<ModelSearchResultVO>() {
				public int compare(ModelSearchResultVO mdl1, ModelSearchResultVO mdl2) {
					return mdl1.getTrim().toLowerCase().compareTo(mdl2.getTrim().toLowerCase());
				}
			});
		}
		
		return trims;
	}
	
	public void autoCompleteTrimSelectListener(SelectEvent selectEvent){
		ModelSearchResultVO modelSearchResultVO = (ModelSearchResultVO) selectEvent.getObject();
		getNewVehicleConfigModelVO().setModelId(modelSearchResultVO.getMdlId());
		getNewVehicleConfigModelVO().setTrim(modelSearchResultVO.getTrim());
	}
	
	public void deleteVehicleTypeListener(VehicleConfigModelVO vehicleConfigModel){
		Iterator<VehicleConfigModelVO> itr = dialogVehicleConfigModelVOs.iterator();
		while (itr.hasNext()) {
			VehicleConfigModelVO configModel = (VehicleConfigModelVO) itr.next();
			if(configModel.getRowKey().equals(vehicleConfigModel.getRowKey())) {
				itr.remove();
			}
		}
		
		initializeVehicleTypeDialog();
	}
	
	public int notesCount(){
		ObjectLogBook objectLogBook;		
		int count = 0;
		
		objectLogBook = logBookService.getObjectLogBook(getVehicleConfiguration(), LogBookTypeEnum.TYPE_VEH_CONFIG_NOTES);
		if(!MALUtilities.isEmpty(objectLogBook)) {
			count = objectLogBook.getLogBookEntries().size();
		}
		
		return count;
	}
	
	public boolean isNoteButtonEnabled() {
		boolean isEnable = false;
		
		if(!MALUtilities.isEmpty(getVehicleConfiguration().getVcfId())) {
			isEnable = true;
		}
		
		return isEnable;
	}	
	
	private void initializeLogBook() {
		boolean isReadOnly = false;									
		getLogBookType().add(new LogBookTypeVO(LogBookTypeEnum.TYPE_VEH_CONFIG_NOTES, isReadOnly));
	}
	
	
	private boolean isValidVehicle() {
		VehicleConfigModelVO vehConfigModelVO = getNewVehicleConfigModelVO();
		boolean isValid = false;
		if(MALUtilities.isNotEmptyString(vehConfigModelVO.getMfgCode())) {
			isValid = true;
		} else if(MALUtilities.isNotEmptyString(vehConfigModelVO.getMake())) {
			isValid = true;
		} else if(MALUtilities.isNotEmptyString(vehConfigModelVO.getModel())) {
			isValid = true;
		} else if(MALUtilities.isNotEmptyString(vehConfigModelVO.getModelCode())) {
			isValid = true;
		} else if(MALUtilities.isNotEmptyString(vehConfigModelVO.getYear())) {
			isValid = true;
		} else if(MALUtilities.isNotEmptyString(vehConfigModelVO.getTrim())) {
			isValid = true;
		}
		
		if(!isValid) {
			addErrorMessageSummary("custom.message", "At least one character is required to select Vehicle Type");
			return false;
		} else {
			if(MALUtilities.isNotEmptyString(vehConfigModelVO.getModel()) && (MALUtilities.isEmpty(vehConfigModelVO.getMrgId()) || vehConfigModelVO.getMrgId().intValue() == 0)) {
				addErrorMessageSummary("custom.message", "Please select a valid Model");
				return false;
			}
			
			int modelsFound = modelSearchService.findModelsCount(getCriteria());
			if(modelsFound == 0) {
				addErrorMessageSummary("custom.message", "Vehicle Type select criteria is not valid");
				return false;
			}
			
			for(VehicleConfigModelVO vehicleConfigModelVO : getVehicleConfigModelVOs()) {
				if(isVehicleTypeExists(vehicleConfigModelVO)) {
					addErrorMessageSummary("custom.message", "Vehicle Type already added to configuration");
					return false;
				}
			}
		}
		
		return true;
	}
	
	private boolean isVehicleTypeExists(VehicleConfigModelVO vehicleConfigModelVO) { 
		VehicleConfigModelVO newVehConfigModel = getNewVehicleConfigModelVO();
		if(MALUtilities.isEmptyString(vehicleConfigModelVO.getMake())) {
			if(MALUtilities.isNotEmptyString(newVehConfigModel.getMake()))
				return false;
		} else if(!vehicleConfigModelVO.getMake().equals(newVehConfigModel.getMake()))
			return false;
		if(MALUtilities.isEmptyString(vehicleConfigModelVO.getMfgCode())) {
			if(MALUtilities.isNotEmptyString(newVehConfigModel.getMfgCode()))
				return false;
		} else if(!vehicleConfigModelVO.getMfgCode().equals(newVehConfigModel.getMfgCode()))
			return false;
		if(MALUtilities.isEmptyString(vehicleConfigModelVO.getModel())) {
			if(MALUtilities.isNotEmptyString(newVehConfigModel.getModel()))
				return false;
		} else if(!vehicleConfigModelVO.getModel().equals(newVehConfigModel.getModel()))
			return false;
		if(MALUtilities.isEmptyString(vehicleConfigModelVO.getModelCode())) {
			if(MALUtilities.isNotEmptyString(newVehConfigModel.getModelCode()))
				return false;
		} else if(!vehicleConfigModelVO.getModelCode().equals(newVehConfigModel.getModelCode()))
			return false;
		if(MALUtilities.isEmptyString(vehicleConfigModelVO.getModelType())) {
			if(MALUtilities.isNotEmptyString(newVehConfigModel.getModelType()))
				return false;
		} else if(!vehicleConfigModelVO.getModelType().equals(newVehConfigModel.getModelType()))
			return false;
		if(MALUtilities.isEmptyString(vehicleConfigModelVO.getTrim())) {
			if(MALUtilities.isNotEmptyString(newVehConfigModel.getTrim()))
				return false;
		} else if(!vehicleConfigModelVO.getTrim().equals(newVehConfigModel.getTrim()))
			return false;
		if(MALUtilities.isEmptyString(vehicleConfigModelVO.getYear())) {
			if(MALUtilities.isNotEmptyString(newVehConfigModel.getYear()))
				return false;
		} else if(!vehicleConfigModelVO.getYear().equals(newVehConfigModel.getYear()))
			return false;
		return true;
	}
	
	public void performSelect() {
		updateSearchCriteria();
		if(!isValidVehicle()) {
			RequestContext.getCurrentInstance().addCallbackParam("failure", true);
		} else {
			dialogVehicleConfigModelVOs.add(getNewVehicleConfigModelVO());
			initializeVehicleTypeDialog();
		}
	}

	public void addVehicleType() {
		RequestContext context = RequestContext.getCurrentInstance();
		boolean isValid = true;
		if(getDialogVehicleConfigModelVOs().isEmpty()) {
			super.addErrorMessageSummary("custom.message", "Please select at least one Vehicle Type ");
			isValid = false;
		}
		
		if(isValid) {
			for(VehicleConfigModelVO configModel : getDialogVehicleConfigModelVOs()) {
				vehicleConfigModelVOs.add(configModel);
			}
			
			getDialogVehicleConfigModelVOs().clear();
			setSelectedVehicleConfigModelVO(null);
			setCriteria(null);
			context.update("configurationVehTypePanel");
		}else {
			context.addCallbackParam("failure", true);
		}
	}
	
	public void deleteVehicleType(VehicleConfigModelVO vehicleConfigModelVO) {
		Iterator<VehicleConfigModelVO> itr = getVehicleConfigModelVOs().iterator();
		while (itr.hasNext()) {
			VehicleConfigModelVO vehicleConfigModel = (VehicleConfigModelVO) itr.next();
			if(vehicleConfigModel.getRowKey().equals(vehicleConfigModelVO.getRowKey())) {
				itr.remove();
			}
		}
	}
	
	public void  deleteUploadedDocument(DocumentFileVO documentFileVO){
		getSelectedVehicleConfigVendorQuoteVO().getDocuments().remove(documentFileVO);
	}
	
	public void toggleVehicleTypeStatus(VehicleConfigModelVO vehicleConfigModelVO) {
		Iterator<VehicleConfigModelVO> itr = getVehicleConfigModelVOs().iterator();
		while (itr.hasNext()) {
			VehicleConfigModelVO vehicleConfigModel = (VehicleConfigModelVO) itr.next();
			if(vehicleConfigModelVO.getVcmId().equals(vehicleConfigModel.getVcmId())) {
				if(vehicleConfigModel.getStatus().equals(STATUS_OBSOLETE)) {
					vehicleConfigModel.setStatus(STATUS_ACTIVE);
				} else {
					vehicleConfigModel.setStatus(STATUS_OBSOLETE);	
				}
			}
		}
	}
	
	public void toggleShowVehicleTypeRecords(){		
		if(vehTypeToggleLabel.equals("Show All")){
			vehTypeToggleLabel  = "Show Active";
			setShowAllVehTypeRecords(true);
		}else{
			vehTypeToggleLabel  = "Show All";
			setShowAllVehTypeRecords(false);
		}
	}
	
	public void toggleShowVendorQuoteRecords(){		
		if(vendorQuoteToggleLabel.equals("Show All")){
			vendorQuoteToggleLabel  = "Show Active";
			setShowAllVQRecords(true);
		}else{
			vendorQuoteToggleLabel  = "Show All";
			setShowAllVQRecords(false);
		}	
	}
	
	public void addVendorQuoteListener() {
		setVqnEditMode(false);
		setSelectedVehicleConfigVendorQuoteVO(new VehicleConfigVendorQuoteVO());
		getSelectedVehicleConfigVendorQuoteVO().setReadOnly(true);
		getSelectedVehicleConfigVendorQuoteVO().setStatus(STATUS_ACTIVE);
		if(getSelectedVehicleConfigVendorQuoteVO().getDocuments() == null){
			getSelectedVehicleConfigVendorQuoteVO().setDocuments(new ArrayList<DocumentFileVO>());
		}
		setConfigurationVendor(null);
	}

	public void addVendorQuote(){
		VehicleConfigVendorQuoteVO vendorQuoteVO = getSelectedVehicleConfigVendorQuoteVO();
		RequestContext context = RequestContext.getCurrentInstance();
		boolean isValid = true;
		
		if (MALUtilities.isEmpty(vendorQuoteVO.getVendorCode())) {
			super.addErrorMessageSummary(UI_ID_VENDOR, "required.field", "Vendor");
			isValid = false;
		}
		if(MALUtilities.isEmpty(vendorQuoteVO.getQuoteNumber()) ){
			super.addErrorMessageSummary(UI_ID_VENDOR_QUOTE_NO, "required.field", "Vendor Quote Number");
			isValid = false;
		}
		if(MALUtilities.isEmpty(vendorQuoteVO.getQuoteDescription()) ){
			super.addErrorMessageSummary(UI_ID_VENDOR_QUOTE_DESC, "required.field", "Vendor Quote Description");
			isValid = false;
		}		
		
		boolean isUniqueQuote = true;
		if( !vqnEditMode) {			
			if((!MALUtilities.isEmpty(vendorQuoteVO.getVendorCode()) && !MALUtilities.isEmpty(vendorQuoteVO.getQuoteNumber()))){
				
				for(VehicleConfigVendorQuoteVO vehConfigVQ : getVehicleConfigVendorQuoteVOs()) {
					if(vehConfigVQ.getVendorCode().equals(vendorQuoteVO.getVendorCode()) && vehConfigVQ.getQuoteNumber().equalsIgnoreCase(vendorQuoteVO.getQuoteNumber()) ) {
						isUniqueQuote = false;
						break;
					}
				} 
			
				if(isUniqueQuote && vendorQuoteVO.isUsedExitingQuote() == false) {
					ExternalAccount vendorAccount = new ExternalAccount(vendorQuoteVO.getcId(), vendorQuoteVO.getVendorType(), vendorQuoteVO.getVendorCode());
					isUniqueQuote = upfitterService.isUniqueQuoteNumber(vendorAccount, vendorQuoteVO.getQuoteNumber());
				}
				
				if(!isUniqueQuote) {
					super.addErrorMessageSummary("unique.upfitter.quote.required");
					isValid = false;
					
				}
			}
		}
		
		if(isUniqueQuote){
			if(MALUtilities.isEmpty(vendorQuoteVO.getQuoteDate())){
				super.addErrorMessageSummary(UI_ID_QUOTE_DATE, "required.field", "Quote Date");
				isValid = false;
			} else if(MALUtilities.compateDates(vendorQuoteVO.getQuoteDate(), Calendar.getInstance().getTime()) > 0){
				super.addErrorMessageSummary("future.date.error", "Quote Date");
				isValid = false;						
			}
			
			if((!MALUtilities.isEmpty(vendorQuoteVO.getQuoteDate()) && !MALUtilities.isEmpty(vendorQuoteVO.getQuoteExpDate()))){
				if(MALUtilities.clearTimeFromDate(vendorQuoteVO.getQuoteDate()).compareTo(MALUtilities.clearTimeFromDate(vendorQuoteVO.getQuoteExpDate())) >= 0){
					super.addErrorMessageSummary("greater.not.equal.date.message", new String[]{"Expiration Date","Quote Date"});
					isValid = false;
				}
			}
			
			if(vendorQuoteVO.getDocuments() == null || vendorQuoteVO.getDocuments().size() == 0){
				addErrorMessageSummary("custom.message", "At least one document needs to be uploaded");
				isValid = false;
			} else {
				int quoteTypeCount = 0;
				for(DocumentFileVO document : vendorQuoteVO.getDocuments()) {
					if(document.getFileType().equals(DOC_TYPE_QUOTE)) {
						quoteTypeCount++;
						if(quoteTypeCount >1 ) {
							addErrorMessageSummary("custom.message", "Only one \"Quote\" type document can be uploaded");	
							isValid = false;
							break;
						} 
					}
				}
			}
		}
		
		if(isValid){
		
			for (DocumentFileVO documentFileVO : getSelectedVehicleConfigVendorQuoteVO().getDocuments()) {
				documentFileVO.setFileName(documentFileVO.getFileType());	
				documentFileVO.setUploadDoc(true);
			}
			
			if(isVqnEditMode()) {
				Iterator<VehicleConfigVendorQuoteVO> itr = getVehicleConfigVendorQuoteVOs().iterator();
				while (itr.hasNext()) {
					VehicleConfigVendorQuoteVO vehicleConfigVendorQuote = (VehicleConfigVendorQuoteVO) itr.next();
					if(vehicleConfigVendorQuote.getRowKey().equals(getSelectedVehicleConfigVendorQuoteVO().getRowKey())) {
						vehicleConfigVendorQuote = getSelectedVehicleConfigVendorQuoteVO();
					}
				}
			} else {
				getVehicleConfigVendorQuoteVOs().add(getSelectedVehicleConfigVendorQuoteVO());
			}
			
			context.update("confgurationVQNPanel");
		}else {
			context.addCallbackParam("failure", true);
		}
	}
	
	public void cancelVendorQuote(){
		Iterator<DocumentFileVO> itr = getSelectedVehicleConfigVendorQuoteVO().getDocuments().iterator();
		while (itr.hasNext()) {
			DocumentFileVO documentFileVO = (DocumentFileVO) itr.next();
			if(!documentFileVO.getFileName().equals(documentFileVO.getFileType())) {
				itr.remove();
			}
		}
	}
	
	public void editVendorQuoteListener(VehicleConfigVendorQuoteVO vendorQuoteVO) {
		setVqnEditMode(true);
		
		UpfitterSearchResultVO upfitterVO = new UpfitterSearchResultVO();
		upfitterVO.setPayeeCorporateId(vendorQuoteVO.getcId());
		upfitterVO.setPayeeAccountCode(vendorQuoteVO.getVendorCode());
		upfitterVO.setPayeeAccountName(vendorQuoteVO.getVendorName());
		upfitterVO.setPayeeAccountType(vendorQuoteVO.getVendorType());
		setConfigurationVendor(upfitterVO);
		
		setSelectedVehicleConfigVendorQuoteVO(vendorQuoteVO);
	}
	
	public void deleteVendorQuote(VehicleConfigVendorQuoteVO vendorQuoteVo) {
		Iterator<VehicleConfigVendorQuoteVO> itr = getVehicleConfigVendorQuoteVOs().iterator();
		while (itr.hasNext()) {
			VehicleConfigVendorQuoteVO vehicleConfigVendorQuoteVO = (VehicleConfigVendorQuoteVO) itr.next();
			if(vehicleConfigVendorQuoteVO.getRowKey().equals(vendorQuoteVo.getRowKey())) {
				itr.remove();
			}
		}
	}
	
	public void toggleVendorQuoteStatus(VehicleConfigVendorQuoteVO vehicleConfigVendorQuoteVO) {
		Iterator<VehicleConfigVendorQuoteVO> itr = getVehicleConfigVendorQuoteVOs().iterator();
		while (itr.hasNext()) {
			VehicleConfigVendorQuoteVO vehicleConfigVendorQuote = (VehicleConfigVendorQuoteVO) itr.next();
			if(vehicleConfigVendorQuoteVO.getVuqId().equals(vehicleConfigVendorQuote.getVuqId())) {
				if(vehicleConfigVendorQuote.getStatus().equals(STATUS_OBSOLETE)) {
					vehicleConfigVendorQuote.setStatus(STATUS_ACTIVE);
				} else {
					vehicleConfigVendorQuote.setStatus(STATUS_OBSOLETE);	
				}
			}
		}
	}
	
	public void autoCompleteVendorSelectListener(SelectEvent selectEvent){
		System.out.println("autoCompleteVendorSelectListener 1"+System.currentTimeMillis());
		getSelectedVehicleConfigVendorQuoteVO().setVendorCode(getConfigurationVendor().getPayeeAccountCode());
		getSelectedVehicleConfigVendorQuoteVO().setVendorName(getConfigurationVendor().getPayeeAccountName());
		getSelectedVehicleConfigVendorQuoteVO().setVendorType(getConfigurationVendor().getPayeeAccountType());
		getSelectedVehicleConfigVendorQuoteVO().setcId(getConfigurationVendor().getPayeeCorporateId());	
		
		upfitterQuoteList.clear();
		getSelectedVehicleConfigVendorQuoteVO().setQuoteNumber(null);
		getSelectedVehicleConfigVendorQuoteVO().setUfqId(null);
		getSelectedVehicleConfigVendorQuoteVO().setQuoteDate(null);
		getSelectedVehicleConfigVendorQuoteVO().setQuoteExpDate(null);
		getSelectedVehicleConfigVendorQuoteVO().setDocuments(new ArrayList<DocumentFileVO>());
		getSelectedVehicleConfigVendorQuoteVO().setReadOnly(true);
		
		System.out.println("autoCompleteVendorSelectListener 2"+System.currentTimeMillis());
		ExternalAccountPK externalAccountPK = new ExternalAccountPK(configurationVendor.getPayeeCorporateId(), 
									configurationVendor.getPayeeAccountType(), configurationVendor.getPayeeAccountCode());			
		upfitterQuoteList = upfitterService.getUpfitterQuotes(new ExternalAccount(externalAccountPK) );
		
		System.out.println("autoCompleteVendorSelectListener 3"+System.currentTimeMillis());
	}
	
	public void handleVendorQuoteDocumentUpload(FileUploadEvent event) {
		
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
		documentFileVO.setFileName(fileName);
		documentFileVO.setFileExt(fileExtn);
		documentFileVO.setFileData(file.getContents());
		documentFileVO.setFileType(DOC_TYPE_VISUAL);
		
		
		getSelectedVehicleConfigVendorQuoteVO().getDocuments().add(0,documentFileVO);
	}
	
	public List<UpfitterSearchResultVO> autoCompleteVendorsListener(String term) {
		UpfitterSearchCriteriaVO searchCriteria = new UpfitterSearchCriteriaVO();		
		searchCriteria.setTerm(term);	
		searchCriteria.setAccountStatus(AccountStatusEnum.OPEN);
		List<UpfitterSearchResultVO> upfitters = upfitterService.searchUpfitters(searchCriteria, new PageRequest(0, 50), null);
		if(!upfitters.isEmpty()) {
			Collections.sort(upfitters, new Comparator<UpfitterSearchResultVO>() {
				public int compare(UpfitterSearchResultVO uf1, UpfitterSearchResultVO uf2) {
					return uf1.getPayeeAccountName().toLowerCase().compareTo(uf2.getPayeeAccountName().toLowerCase());
				}
			});
		}

		return upfitters;
	}

	public void previewDocuments(DocumentFileVO documentFileVO){	
		
		try {	
				setPreviewfile(null);			
				String mimeType = "";
				if(documentFileVO.getFileExt().equalsIgnoreCase("pdf")){
					mimeType = "application/pdf";
				}else if(documentFileVO.getFileExt().equalsIgnoreCase("doc")){
					mimeType =  "application/msword";
				}else if(documentFileVO.getFileExt().equalsIgnoreCase("docx")){
					mimeType =  "pplication/vnd.openxmlformats-officedocument.wordprocessingml.document";
				}else if(documentFileVO.getFileExt().equalsIgnoreCase("GIF")){
					mimeType =  "image/gif";
				}else if(documentFileVO.getFileExt().equalsIgnoreCase("JPG")
						|| documentFileVO.getFileExt().equalsIgnoreCase("JPEG")){
					mimeType =  "mage/jpeg";
				}
				
				byte[] byteArray = null;					
				if(documentFileVO.getFileId() == null || documentFileVO.getFileId().longValue() <= 0) {
					byteArray = documentFileVO.getFileData();
				}else{
					byteArray = vehicleConfigurationService.getUploadedDocument(documentFileVO.getFileId());
				}
			
				setPreviewfile(new DefaultStreamedContent(new ByteArrayInputStream(byteArray), mimeType, documentFileVO.getFileName()+"."+documentFileVO.getFileExt()));
			
		} catch (Exception e) {		
			addErrorMessage("onBase.retrieval.error.msg");
			logger.error(e);
		}
	}
	
	public String save() {
		if(saveConfiguration()) {
			return super.cancelPage();
		} else
			return null;
	}

	private boolean saveConfiguration() {
		try {
			if(!isValid()) {
				return false;
			}		
			
			vehicleConfiguration.setOrderType(locateFlag == true ? orderTypeDAO.findById("L").orElse(null) : orderTypeDAO.findById("S").orElse(null) );			
			
			if(viewMode.equals(ViewConstants.VIEW_MODE_ADD)) {
				
				vehicleConfiguration.setEnteredDate(new Date());
				vehicleConfiguration.setEnteredUser(super.getLoggedInUser().getEmployeeNo());
				vehicleConfiguration.setObsoleteYN("N");
				StringTokenizer st = new StringTokenizer(vehicleConfiguration.getDescription(), " ");
				String configGroupingName = "";
				while (st.hasMoreElements()) {
					configGroupingName = (String) st.nextElement();
					break;
				}
				selectedVehicleConfigGrouping.setName(configGroupingName.toUpperCase());			
				
				List<VehicleConfigModel> vehicleConfigModels = new ArrayList<VehicleConfigModel>();			
				for(VehicleConfigModelVO configModelVO : vehicleConfigModelVOs) {					
					VehicleConfigModel vehicleConfigModel = populateVehicleConfigModel(configModelVO);
					vehicleConfigModel.setVehicleConfiguration(vehicleConfiguration);
					vehicleConfigModels.add(vehicleConfigModel);
				}				
				vehicleConfiguration.setVehicleConfigModels(vehicleConfigModels);	
				
				List<VehicleConfigUpfitQuote> vehicleConfigVendorQuotes = new ArrayList<VehicleConfigUpfitQuote>();				
				for(VehicleConfigVendorQuoteVO vehicleConfigVendorQuoteVO : getVehicleConfigVendorQuoteVOs()) {
					VehicleConfigUpfitQuote configVendorQuote =  populateVehicleConfigUpfitQuote(vehicleConfigVendorQuoteVO);	
					configVendorQuote.setVehicleConfigGrouping(selectedVehicleConfigGrouping);
					vehicleConfigVendorQuotes.add(configVendorQuote);
				}
				
				selectedVehicleConfigGrouping.setVehicleConfigUpfitQuotes(vehicleConfigVendorQuotes);
				selectedVehicleConfigGrouping.setVehicleConfiguration(vehicleConfiguration);
				vehicleConfigGroupings = new ArrayList<VehicleConfigGrouping>();
				vehicleConfigGroupings.add(selectedVehicleConfigGrouping);
				vehicleConfiguration.setVehicleConfigGroupings(vehicleConfigGroupings);
				
			}else{//edit mode
				
				List<VehicleConfigModel> newVehicleConfigModelList = new ArrayList<VehicleConfigModel>();
				for(VehicleConfigModelVO configModelVO : vehicleConfigModelVOs) {	
					if(configModelVO.getVcmId() != null && configModelVO.getVcmId().longValue() > 0){// object is already saved
						for (VehicleConfigModel vehicleConfigModel : vehicleConfiguration.getVehicleConfigModels()) {//finding matching object
							if(configModelVO.getVcmId().equals(vehicleConfigModel.getVcmId())){
								vehicleConfigModel.setObsoleteYn(STATUS_ACTIVE.equals(configModelVO.getStatus()) ? "N" : "Y");
								break;
							}
						}
					}else{
						VehicleConfigModel vehicleConfigModel = populateVehicleConfigModel(configModelVO);
						vehicleConfigModel.setVehicleConfiguration(vehicleConfiguration);
						newVehicleConfigModelList.add(vehicleConfigModel);
					}
				}				
				vehicleConfiguration.getVehicleConfigModels().addAll(newVehicleConfigModelList);
				
				
				List<VehicleConfigUpfitQuote> newVehicleConfigVendorQuotes = new ArrayList<VehicleConfigUpfitQuote>();				
				for(VehicleConfigVendorQuoteVO vehicleConfigVendorQuoteVO : getVehicleConfigVendorQuoteVOs()) {
					if(vehicleConfigVendorQuoteVO.getVuqId() != null && vehicleConfigVendorQuoteVO.getVuqId().longValue() > 0){// object is already saved
						for (VehicleConfigUpfitQuote vehicleConfigUpfitQuote : selectedVehicleConfigGrouping.getVehicleConfigUpfitQuotes()) {//finding matching object
							if(vehicleConfigUpfitQuote.getVuqId().equals(vehicleConfigVendorQuoteVO.getVuqId())){
								vehicleConfigUpfitQuote.setObsoleteYn(STATUS_ACTIVE.equals(vehicleConfigVendorQuoteVO.getStatus()) ? "N" : "Y");
								
								// get already uploaded docs
								List<OnbaseUploadedDocs> alreadyUploadedDocsForUfq = new ArrayList<OnbaseUploadedDocs>();
								if(!MALUtilities.isEmpty(vehicleConfigUpfitQuote.getUpfitterQuote().getUfqId())){
									alreadyUploadedDocsForUfq = onbaseUploadedDocsDAO.getOnBaseUploadedDocsByObjectIdAndType(
																						String.valueOf(vehicleConfigUpfitQuote.getUpfitterQuote().getUfqId()),
																						"UPFITTER_QUOTES");
								}
								//user  uploaded more document on edit case
								for (DocumentFileVO documentFileVO : vehicleConfigVendorQuoteVO.getDocuments()) {
									if((documentFileVO.getFileId() == null || documentFileVO.getFileId().longValue() <= 0) && documentFileVO.isUploadDoc()){//doc is not save yet
										UpfitterQuote  upfitterQuote =   vehicleConfigUpfitQuote.getUpfitterQuote() ;
										OnbaseUploadedDocs onbaseUploadedDocs = populateOnbaseUploadedDocs(documentFileVO ,upfitterQuote);								
										onbaseUploadedDocs.setUpfitterQuote(upfitterQuote);//file name and type was already set in bean									
										alreadyUploadedDocsForUfq.add(onbaseUploadedDocs); 
									}								
								}
								vehicleConfigUpfitQuote.getUpfitterQuote().setOnbaseUploadedDocs(alreadyUploadedDocsForUfq);
							}							
						}
					}else{
						VehicleConfigUpfitQuote configVendorQuote =  populateVehicleConfigUpfitQuote(vehicleConfigVendorQuoteVO);	
						
						configVendorQuote.setVehicleConfigGrouping(selectedVehicleConfigGrouping);
						selectedVehicleConfigGrouping.setVehicleConfiguration(vehicleConfiguration);
						newVehicleConfigVendorQuotes.add(configVendorQuote);
					}
				}
				
				vehicleConfiguration.getVehicleConfigGroupings().get(0).getVehicleConfigUpfitQuotes().addAll(newVehicleConfigVendorQuotes);
				
			}
			
			vehicleConfigurationService.saveUpdateConfiguration(vehicleConfiguration);	
			
			super.addSuccessMessage("process.success", this.viewMode + " configuration (" + vehicleConfiguration.getDescription() + ")");
			return true;

		} catch (Exception ex) {
			super.addErrorMessage("generic.error", ex.getMessage());
			return false;
		}

	}

	private VehicleConfigModel populateVehicleConfigModel(VehicleConfigModelVO configModelVO ){
					
		VehicleConfigModel vehicleConfigModel = new VehicleConfigModel();
		vehicleConfigModel.setLastUpdatedDate(new Date());
		vehicleConfigModel.setLastUpdatedUser(super.getLoggedInUser().getEmployeeNo());
		vehicleConfigModel.setMfgCode(configModelVO.getMfgCode());
		vehicleConfigModel.setMake(configModelVO.getMake());
		vehicleConfigModel.setYear(configModelVO.getYear());
		if(configModelVO.getStatus().equals(STATUS_OBSOLETE)) {
			vehicleConfigModel.setObsoleteYn("Y");
		} else {
			vehicleConfigModel.setObsoleteYn("N");
		}	
		
		if(configModelVO.getModelId() != null && configModelVO.getModelId().intValue() != 0) {
			Model model = modelService.getModelById(configModelVO.getModelId());
			vehicleConfigModel.setModel(model);
		} 
		if(configModelVO.getMrgId() != null && configModelVO.getMrgId().intValue() != 0) {
			MakeModelRange makeModelRange = modelService.getMakeModelRangeById(configModelVO.getMrgId());
			vehicleConfigModel.setMakeModelRange(makeModelRange);
		}
		
		return vehicleConfigModel;
		
	}
	
	private VehicleConfigUpfitQuote populateVehicleConfigUpfitQuote(VehicleConfigVendorQuoteVO configVendorQuoteVO){
		

			VehicleConfigUpfitQuote configVendorQuote = new VehicleConfigUpfitQuote();
			UpfitterQuote upfitterQuote = new UpfitterQuote();
			if(configVendorQuoteVO.isUsedExitingQuote()){
				 upfitterQuote.setUfqId(configVendorQuoteVO.getUfqId());
			}
			
			ExternalAccount vendorAccount = upfitterService.getUpfitterAccount(configVendorQuoteVO.getVendorCode(), getLoggedInUser().getCorporateEntity());
			upfitterQuote.setExternalAccount(vendorAccount);
			upfitterQuote.setQuoteDate(configVendorQuoteVO.getQuoteDate());
			upfitterQuote.setExpirationDate(configVendorQuoteVO.getQuoteExpDate());
			upfitterQuote.setQuoteNumber(configVendorQuoteVO.getQuoteNumber());
			upfitterQuote.setDescription(configVendorQuoteVO.getQuoteDescription());
			if(upfitterQuote.getOnbaseUploadedDocs() == null){
				upfitterQuote.setOnbaseUploadedDocs(new ArrayList<OnbaseUploadedDocs>());
			}
			
			for (DocumentFileVO documentFileVO : configVendorQuoteVO.getDocuments()) {
				if(documentFileVO.isUploadDoc()  && documentFileVO.getFileId() == null ) { //To consider only documents which are added after uploading, handles issue if user uploaded doc but clicked "Cancel" on Vendor Quote dialog 
					OnbaseUploadedDocs onbaseUploadedDocs = populateOnbaseUploadedDocs(documentFileVO , upfitterQuote);
					upfitterQuote.getOnbaseUploadedDocs().add(onbaseUploadedDocs);
				}
			}
			
			configVendorQuote.setUpfitterQuote(upfitterQuote);
			configVendorQuote.setLastUpdatedUser(getLoggedInUser().getEmployeeNo());
			configVendorQuote.setLastUpdatedDate(new Date());
			if(configVendorQuoteVO.getStatus().equals(STATUS_OBSOLETE)) {
				configVendorQuote.setObsoleteYn("Y");
			} else {
				configVendorQuote.setObsoleteYn("N");
			}
			
			return configVendorQuote;
		
	}
	private OnbaseUploadedDocs populateOnbaseUploadedDocs(DocumentFileVO documentFileVO ,UpfitterQuote upfitterQuote){	
		
		OnbaseUploadedDocs onbaseUploadedDocs = new OnbaseUploadedDocs(); 
		onbaseUploadedDocs.setNeedToUpload(true);
		onbaseUploadedDocs.setFileName(documentFileVO.getFileName());
		onbaseUploadedDocs.setFileType(documentFileVO.getFileExt());	
		onbaseUploadedDocs.setObjectId(String.valueOf(upfitterQuote.getUfqId()));
		onbaseUploadedDocs.setObjectType(UPFITTER_QUOTES_OBJECT_TYPE);
		onbaseUploadedDocs.setDocType(OnbaseDocTypeEnum.TYPE_VENDOR_QUOTE.getValue());
		onbaseUploadedDocs.setObsoleteYn("N");
		onbaseUploadedDocs.setFileData(documentFileVO.getFileData());
		onbaseUploadedDocs.setUpfitterQuote(upfitterQuote);//file name and type was already set in bean
		
		return onbaseUploadedDocs;
		
	}
	
	private boolean isValid() {
		boolean isValid = true;
		if(MALUtilities.isEmpty(vehicleConfiguration.getExternalAccount())) {
			addErrorMessage(UI_ID_CLIENT, "required.field", "Client");
			isValid = false;
		} 
		if(MALUtilities.isEmpty(vehicleConfiguration.getDescription())) {
			addErrorMessage(UI_ID_APPLICATION, "required.field", "Application");
			isValid = false;
		}

		return isValid;
	}
	
	public void updateIntExtColor(){
		if(isLocateFlag()){
			setEnableIntExtColorFlag(true);	
		}else{
			setEnableIntExtColorFlag(false);
			getVehicleConfiguration().setColor("");
		}		
	}
	
	public void toggleVehicleConfigStatus() {
		if(vehConfigToggleStatus.equals(STATUS_OBSOLETE)) {
			setVehConfigToggleStatus(STATUS_ACTIVE);
			getVehicleConfiguration().setObsoleteYN("N");
		} else {
			setVehConfigToggleStatus(STATUS_OBSOLETE);
			getVehicleConfiguration().setObsoleteYN("Y");
		}
	}
	
	public void setUpfitAssessmentExists() {
		if(!MALUtilities.isEmpty(getVehicleConfiguration().getVcfId())) {
			List<UpfitAssessmentAnswer> answerList = upfitAssessmentService.getUpfitAssessmentAnswersByVehicleConfigId(getVehicleConfiguration().getVcfId());
			if(!MALUtilities.isEmpty(answerList) && answerList.size() > 0){
				setUpfitAssesmentBtnLabel("View/Edit Assessment");
				setHasUpfitAssessment(true);
			}else{
				setUpfitAssesmentBtnLabel("Add Assessment");
				setHasUpfitAssessment(false);
			}
		}else{
			setUpfitAssesmentBtnLabel("Add Assessment");
			setHasUpfitAssessment(false);
		}
	}	
	
	public boolean isUpfitAssessmentButtonEnabled() {
		boolean isEnable = false;

		if(!MALUtilities.isEmpty(vehicleConfigId)) {
			if(hasUpfitAssessment) {
				isEnable = true;
			} else {
		    	if(super.hasPermission("quoteRequestAddEdit_manage")) {
		    		isEnable = true;
		    	}				
			}
		}
		return isEnable;
	}
	
	public void navigateToUpfitAssessment() {
		Map<String, Object> nextPageValues = new HashMap<String, Object>();
		
		saveRestoreStateValues(getCurrentPageRestoreStateValuesMap());
		
		nextPageValues.put(ViewConstants.VIEW_PARAM_VEHICLE_CONFIG_ID, this.vehicleConfiguration.getVcfId());
		nextPageValues.put("UPFIT_ASSESSMENT", isHasUpfitAssessment());
	    saveNextPageInitStateValues(nextPageValues);
		forwardToURL(ViewConstants.UPFIT_ASSESSMENT);
	}
	
	private Map<String, Object> getCurrentPageRestoreStateValuesMap() {
		
		Map<String, Object> restoreStateValues = new HashMap<String, Object>();
		restoreStateValues.put("VEHICLE_CONFIG", this.vehicleConfiguration);
		
		return restoreStateValues;
		
	}

	public String getVehicleConfigId() {
		return vehicleConfigId;
	}

	public void setVehicleConfigId(String vehicleConfigId) {
		this.vehicleConfigId = vehicleConfigId;
	}

	public VehicleConfiguration getVehicleConfiguration() {
		return vehicleConfiguration;
	}	
	
	public List<VehicleConfigVendorQuoteVO> getVehicleConfigVendorQuoteVOsForDisplay() {
		List<VehicleConfigVendorQuoteVO> displayList = new ArrayList<VehicleConfigVendorQuoteVO>();		
		if(showAllVQRecords){
			displayList = vehicleConfigVendorQuoteVOs;
		}else{
			 for( VehicleConfigVendorQuoteVO vehicleConfigVendorQuoteVO : vehicleConfigVendorQuoteVOs){
				 if(STATUS_ACTIVE.equals(vehicleConfigVendorQuoteVO.getStatus()) || vehicleConfigVendorQuoteVO.getVuqId() == null){
					 displayList.add(vehicleConfigVendorQuoteVO);
				 }
			 }
		}
		Collections.sort(displayList, getComparator(VUQ_STATUS_SORT, VUQ_ID_SORT));
		return displayList;
	}
	
	public List<VehicleConfigVendorQuoteVO> getVehicleConfigVendorQuoteVOs() {
		return vehicleConfigVendorQuoteVOs;
	}

	public void setVehicleConfigVendorQuoteVOs(List<VehicleConfigVendorQuoteVO> vehicleConfigVendorQuoteVOs) {
		this.vehicleConfigVendorQuoteVOs = vehicleConfigVendorQuoteVOs;
	}
	
	public void setVehicleConfiguration(VehicleConfiguration vehicleConfiguration) {
		this.vehicleConfiguration = vehicleConfiguration;
	}

	public List<VehicleConfigGrouping> getVehicleConfigGroupings() {
		return vehicleConfigGroupings;
	}

	public void setVehicleConfigGroupings(List<VehicleConfigGrouping> vehicleConfigGroupings) {
		this.vehicleConfigGroupings = vehicleConfigGroupings;
	}

	public VehicleConfigGrouping getSelectedVehicleConfigGrouping() {
		return selectedVehicleConfigGrouping;
	}

	public void setSelectedVehicleConfigGrouping(VehicleConfigGrouping selectedVehicleConfigGrouping) {
		this.selectedVehicleConfigGrouping = selectedVehicleConfigGrouping;
	}

	public ExternalAccount getClientAccount() {
		return clientAccount;
	}

	public void setClientAccount(ExternalAccount clientAccount) {
		this.clientAccount = clientAccount;
	}

	public List<VehicleConfigModelVO> getVehicleConfigModelVOForDisplay() {
		List<VehicleConfigModelVO> displayList = new ArrayList<VehicleConfigModelVO>();		
		if(showAllVehTypeRecords){
			displayList = vehicleConfigModelVOs;
		}else{
			 for( VehicleConfigModelVO vehicleConfigModelVO : vehicleConfigModelVOs){
				 if(STATUS_ACTIVE.equals(vehicleConfigModelVO.getStatus()) || vehicleConfigModelVO.getVcmId() == null){
					 displayList.add(vehicleConfigModelVO);
				 }
			 }
		}
		
		Collections.sort(displayList, getComparator(VCM_STATUS_SORT, VCM_ID_SORT));
		return displayList;
	}
	
	public List<VehicleConfigModelVO> getVehicleConfigModelVOs() {
		return vehicleConfigModelVOs;
	}
	
	public void setVehicleConfigModelVOs(List<VehicleConfigModelVO> vehicleConfigModelVOs) {
		this.vehicleConfigModelVOs = vehicleConfigModelVOs;
	}

	public VehicleConfigModelVO getSelectedVehicleConfigModelVO() {
		return selectedVehicleConfigModelVO;
	}

	public void setSelectedVehicleConfigModelVO(VehicleConfigModelVO selectedVehicleConfigModelVO) {
		this.selectedVehicleConfigModelVO = selectedVehicleConfigModelVO;
	}

	public List<VehicleConfigModelVO> getDialogVehicleConfigModelVOs() {
		return dialogVehicleConfigModelVOs;
	}

	public void setDialogVehicleConfigModelVOs(List<VehicleConfigModelVO> dialogVehicleConfigModelVOs) {
		this.dialogVehicleConfigModelVOs = dialogVehicleConfigModelVOs;
	}

	public VehicleConfigModelVO getNewVehicleConfigModelVO() {
		return newVehicleConfigModelVO;
	}

	public void setNewVehicleConfigModelVO(VehicleConfigModelVO newVehicleConfigModelVO) {
		this.newVehicleConfigModelVO = newVehicleConfigModelVO;
	}

	public VehicleConfigVendorQuoteVO getSelectedVehicleConfigVendorQuoteVO() {
		return selectedVehicleConfigVendorQuoteVO;
	}

	public void setSelectedVehicleConfigVendorQuoteVO(VehicleConfigVendorQuoteVO selectedVehicleConfigVendorQuoteVO) {
		this.selectedVehicleConfigVendorQuoteVO = selectedVehicleConfigVendorQuoteVO;
	}

	public UpfitterSearchResultVO getConfigurationVendor() {
		return configurationVendor;
	}

	public void setConfigurationVendor(UpfitterSearchResultVO configurationVendor) {
		this.configurationVendor = configurationVendor;		
	}
	
	public void onChangeVendorQuoteNo() {		
		selectedVehicleConfigVendorQuoteVO.setUsedExitingQuote(false);
		selectedVehicleConfigVendorQuoteVO.setReadOnly(false);
		for (UpfitterQuote upfitterQuote : upfitterQuoteList) {				
			if(selectedVehicleConfigVendorQuoteVO.getQuoteNumber() != null && 
					upfitterQuote.getQuoteNumber().equalsIgnoreCase(selectedVehicleConfigVendorQuoteVO.getQuoteNumber())){
				
				selectedVehicleConfigVendorQuoteVO.setUfqId(upfitterQuote.getUfqId());
				selectedVehicleConfigVendorQuoteVO.setUsedExitingQuote(true);
				selectedVehicleConfigVendorQuoteVO.setQuoteDate(upfitterQuote.getQuoteDate());
				selectedVehicleConfigVendorQuoteVO.setQuoteExpDate(upfitterQuote.getExpirationDate());
				selectedVehicleConfigVendorQuoteVO.setQuoteDescription(upfitterQuote.getDescription());
				setUploadedDocsOnVehConfigVendorQuote(upfitterQuote);
			}
		}	
		
		if(!selectedVehicleConfigVendorQuoteVO.isUsedExitingQuote()) {
			selectedVehicleConfigVendorQuoteVO.setUfqId(null);
			selectedVehicleConfigVendorQuoteVO.setQuoteDate(null);
			selectedVehicleConfigVendorQuoteVO.setQuoteExpDate(null);
			selectedVehicleConfigVendorQuoteVO.setDocuments(new ArrayList<DocumentFileVO>());
			if(MALUtilities.isEmptyString(selectedVehicleConfigVendorQuoteVO.getQuoteNumber())) {
				selectedVehicleConfigVendorQuoteVO.setReadOnly(true);	
			} else {
				selectedVehicleConfigVendorQuoteVO.setReadOnly(false);
			}
			
		} else {
			super.addInfoMessageSummary("custom.message", "Adding a document here adds it to all configurations using this vendor quote");
			selectedVehicleConfigVendorQuoteVO.setReadOnly(false);
		}
		if(selectedVehicleConfigVendorQuoteVO.isUsedExitingQuote()){
			List<VehicleConfigUpfitQuote> list = vehicleConfigurationService.getVehicleConfigUpfitQuoteByUpfitterQuote(selectedVehicleConfigVendorQuoteVO.getUfqId());
			if(list != null && list.size() > 0){
				selectedVehicleConfigVendorQuoteVO.setReadOnly(true);
			}
		}
		
	}

	private void setUploadedDocsOnVehConfigVendorQuote(UpfitterQuote upfitterQuote) {
		List<DocumentFileVO> docs = new ArrayList<DocumentFileVO>();
		DocumentFileVO doc = null;
		List<OnbaseUploadedDocs> listUploadedDocsForUfq = onbaseUploadedDocsDAO.getOnBaseUploadedDocsByObjectIdAndType(String.valueOf(upfitterQuote.getUfqId()),
																														UPFITTER_QUOTES_OBJECT_TYPE);
		for(OnbaseUploadedDocs onBaseDoc : listUploadedDocsForUfq) {
			doc = new DocumentFileVO();
			doc.setFileId(onBaseDoc.getObdId());
			doc.setFileName(onBaseDoc.getFileName());
			doc.setFileType(onBaseDoc.getFileName());
			doc.setFileExt(onBaseDoc.getFileType());
			doc.setFileData(onBaseDoc.getFileData());
			doc.setUploadDoc(true);
			docs.add(doc);
		}
		selectedVehicleConfigVendorQuoteVO.setDocuments(docs);
	}

	public List<UpfitterQuote> getUpfitterQuoteList() {
		return upfitterQuoteList;
	}

	public void setUpfitterQuoteList(List<UpfitterQuote> upfitterQuoteList) {
		this.upfitterQuoteList = upfitterQuoteList;
	}

	public boolean isVqnEditMode() {
		return vqnEditMode;
	}

	public void setVqnEditMode(boolean vqnEditMode) {
		this.vqnEditMode = vqnEditMode;
	}

	public String getViewMode() {
		return viewMode;
	}

	public void setViewMode(String viewMode) {
		this.viewMode = viewMode;
	}

	public boolean isShowAllVehTypeRecords() {
		return showAllVehTypeRecords;
	}

	public void setShowAllVehTypeRecords(boolean showAllVehTypeRecords) {
		this.showAllVehTypeRecords = showAllVehTypeRecords;
	}

	public String getVehTypeToggleLabel() {
		return vehTypeToggleLabel;
	}

	public void setVehTypeToggleLabel(String vehTypeToggleLabel) {
		this.vehTypeToggleLabel = vehTypeToggleLabel;
	}

	public boolean isShowAllVQRecords() {
		return showAllVQRecords;
	}

	public void setShowAllVQRecords(boolean showAllVQRecords) {
		this.showAllVQRecords = showAllVQRecords;
	}

	public String getVendorQuoteToggleLabel() {
		return vendorQuoteToggleLabel;
	}

	public void setVendorQuoteToggleLabel(String vendorQuoteToggleLabel) {
		this.vendorQuoteToggleLabel = vendorQuoteToggleLabel;
	}

	public boolean isLocateFlag() {
		return locateFlag;
	}

	public void setLocateFlag(boolean locateFlag) {
		this.locateFlag = locateFlag;
	}

	public List<String> getYears() {
		return years;
	}

	public void setYears(List<String> years) {
		this.years = years;
	}

	public ModelSearchCriteriaVO getCriteria() {
		return criteria;
	}

	public void setCriteria(ModelSearchCriteriaVO criteria) {
		this.criteria = criteria;
	}

	public ModelSearchResultVO getSelectedModel() {
		return selectedModel;
	}

	public void setSelectedModel(ModelSearchResultVO selectedModel) {
		this.selectedModel = selectedModel;
	}

	public List<String> getMakes() {
		return makes;
	}

	public void setMakes(List<String> makes) {
		this.makes = makes;
	}

	public StreamedContent getPreviewfile() {
		return previewfile;
	}

	public void setPreviewfile(StreamedContent previewfile) {
		this.previewfile = previewfile;
	}

	public boolean isHasManageVehConfigPermission() {
		return hasManageVehConfigPermission;
	}

	public void setHasManageVehConfigPermission(boolean hasManageVehConfigPermission) {
		this.hasManageVehConfigPermission = hasManageVehConfigPermission;
	}

	public boolean isEnableIntExtColorFlag() {
		return enableIntExtColorFlag;
	}

	public void setEnableIntExtColorFlag(boolean enableIntExtColorFlag) {
		this.enableIntExtColorFlag = enableIntExtColorFlag;
	}
/*
 * This is a custom function to determine height based on screen resolution .
 */
	public int getDtHeight(int tableId) {
		int dtHeight = 110;
		
		try {
			int resolutionHeight = ((Integer)getSessionScopeMap().get(ViewConstants.SCREEN_RESOLUTION_HEIGHT) != null)? (Integer)getSessionScopeMap().get(ViewConstants.SCREEN_RESOLUTION_HEIGHT) : 800;
				
			resolutionHeight = resolutionHeight - SCREEN_FIXED_HIGHT;
			if(tableId == 1){
				dtHeight = (resolutionHeight * 40)/100;
			}else if(tableId == 2){
				dtHeight = (resolutionHeight * 60)/100;
			}
		} catch (Exception e) {
			//do nothing
		}
		
		
		return dtHeight;
	}

	public String getVehConfigToggleStatus() {
		return vehConfigToggleStatus;
	}

	public void setVehConfigToggleStatus(String vehConfigToggleStatus) {
		this.vehConfigToggleStatus = vehConfigToggleStatus;
	}

	public List<LogBookTypeVO> getLogBookType() {
		return logBookType;
	}

	public void setLogBookType(List<LogBookTypeVO> logBookType) {
		this.logBookType = logBookType;
	}

	public String getUpfitAssesmentBtnLabel() {
		return upfitAssesmentBtnLabel;
	}

	public void setUpfitAssesmentBtnLabel(String upfitAssesmentBtnLabel) {
		this.upfitAssesmentBtnLabel = upfitAssesmentBtnLabel;
	}

	public boolean isHasUpfitAssessment() {
		return hasUpfitAssessment;
	}

	public void setHasUpfitAssessment(boolean hasUpfitAssessment) {
		this.hasUpfitAssessment = hasUpfitAssessment;
	}


}
