package com.mikealbert.vision.view.bean.components;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.faces.context.FacesContext;

import org.primefaces.context.RequestContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.mikealbert.common.MalLogger;
import com.mikealbert.common.MalLoggerFactory;
import com.mikealbert.common.MalMessage;
import com.mikealbert.data.dao.DistDAO;
import com.mikealbert.data.dao.DriverDAO;
import com.mikealbert.data.dao.ExternalAccountDAO;
import com.mikealbert.data.dao.FleetMasterVinDetailsDAO;
import com.mikealbert.data.dao.ProgressTypeCodeDAO;
import com.mikealbert.data.entity.Dist;
import com.mikealbert.data.entity.Doc;
import com.mikealbert.data.entity.DocPropertyValue;
import com.mikealbert.data.entity.Driver;
import com.mikealbert.data.entity.ExternalAccount;
import com.mikealbert.data.entity.ExternalAccountPK;
import com.mikealbert.data.entity.FleetMaster;
import com.mikealbert.data.entity.FleetMasterVinDetails;
import com.mikealbert.data.entity.Make;
import com.mikealbert.data.entity.Model;
import com.mikealbert.data.entity.ModelMarkYear;
import com.mikealbert.data.entity.ProgressTypeCode;
import com.mikealbert.data.entity.QuotationModel;
import com.mikealbert.data.entity.SupplierProgressHistory;
import com.mikealbert.data.enumeration.DocPropertyEnum;
import com.mikealbert.data.enumeration.DocumentStatus;
import com.mikealbert.data.enumeration.LogBookTypeEnum;
import com.mikealbert.data.enumeration.UnitProgressCodeEnum;
import com.mikealbert.data.vo.VehicleOrderStatusSearchCriteriaVO;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.service.DocPropertyValueService;
import com.mikealbert.service.DocumentService;
import com.mikealbert.service.FleetMasterService;
import com.mikealbert.service.PurchaseOrderService;
import com.mikealbert.service.QuotationService;
import com.mikealbert.service.VinDecoderService;
import com.mikealbert.util.MALUtilities;
import com.mikealbert.vision.service.PurchasingEmailService;
import com.mikealbert.vision.service.SupplierService;
import com.mikealbert.vision.view.ViewConstants;
import com.mikealbert.vision.view.bean.StatefulBaseBean;
import com.mikealbert.vision.vo.LogBookTypeVO;

@Component
@Scope("view")
public class MaintainVehicleOrderStatusBean extends StatefulBaseBean {

	private static final long serialVersionUID = 1L;
	private MalLogger logger = MalLoggerFactory.getLogger(this.getClass());
	@Resource SupplierService supplierService;
	@Resource ProgressTypeCodeDAO progressTypeCodeDAO;
	@Resource DocumentService documentService;
	@Resource QuotationService quotationService;
	@Resource ExternalAccountDAO externalAccountDAO;
	@Resource FleetMasterService fleetMasterService;
	@Resource DriverDAO driverDAO;
//	@Resource InvoiceEntryService invoiceEntryService;
	@Resource DistDAO distDAO;
	@Resource private VinDecoderService vinDecoderService;
	@Resource protected MalMessage malMessage;
	@Resource FleetMasterVinDetailsDAO fleetMasterVinDetailsDAO;
	@Resource PurchaseOrderService purchaseOrderService;
	@Resource DocPropertyValueService docPropertyValueService;
	@Resource PurchasingEmailService purchasingEmailService;
	
	private VehicleOrderStatusSearchCriteriaVO searchCriteria;
	private Long mainPODocId;
	private String clientId;
	private String onCloseCallback;
	private String resource;
	private boolean showSearch;
	private boolean showNotes;
	private boolean showUpfitProgress;	
	private boolean showCancel;
	private boolean enquiryMode;
	private String unitDesc;
	private String unitNo;
	private long fmsId;
	private Long qmdId;
	private long makeId;
	private String docPONo;
	private String factoryNo;
	private String vin;
	private String vinLabel;
	private String factoryNoLabel;
	private String vehicleDesc;
	private String clientName;
	private String vendorName;
	private String driverName;
	private String requestedDate;
	private String acquisitionType;
	private String orderingLeadTime;
	private String upfitYN;
	private boolean hasManagePermission;
	private boolean hasEditPermission;
	
	private SupplierProgressHistory selectedSupplierProgressHistory;
	private List<SupplierProgressHistory> supplierProgressHistoryList ;
	private List<ProgressTypeCode> progressTypeCodeList ;
	private List<String> poOrderNoList ;
	private boolean fetchAllEntry = false;
	private String  toggleLabel =  "Show All";
	private String  MANAGE_PERMISSION_NAME =  "manage_veh_order_status";
	private String  EDIT_PERMISSION_NAME =  "edit_veh_order_status";
	private boolean disableRestore;
	
	private FleetMaster selectedFleetMaster;	
	private List<LogBookTypeVO> logBookType;	
	
	private boolean userConfirmation = false;
	private boolean chromeValidationDone = false;
	private boolean showWarning = false;
	private boolean addNotes = false;
	private boolean needRefresh = false;
	private String warningMessage;
	
	private QuotationModel selectedQuotationModel;
	private List<DocumentStatus> upfitPOStatuses;	
	private QuotationModel quotationModel = null;
	private Doc doc = null;
	private FleetMaster fleetMaster = null;
	private Driver driver = null;
	
	@PostConstruct
	public void init() {
		this.openPage();
		try {
    		FacesContext fc = FacesContext.getCurrentInstance();			
			initializeDataTable(150, 550, new int[] { 10, 20, 14, 20, 10, 10, 6}).setHeight(250);
			supplierProgressHistoryList = new ArrayList<SupplierProgressHistory>();
			
			setLogBookType(new ArrayList<LogBookTypeVO>());
			getLogBookType().add(new LogBookTypeVO(LogBookTypeEnum.TYPE_VEHICLE_STATUS_NOTES, LogBookTypeEnum.TYPE_UPFIT_PRG_NOTES, ViewConstants.LABEL_PROGRESS_CHASING, true));
			getLogBookType().get(0).getRelatedLogBookTypes().add(LogBookTypeEnum.TYPE_IN_SERV_PRG_NOTES);
			getLogBookType().get(0).getRelatedLogBookTypes().add(LogBookTypeEnum.TYPE_CLIENT_FACING_NOTES);
			getLogBookType().get(0).getRelatedLogBookTypes().add(LogBookTypeEnum.TYPE_BASE_VEH_ORDER_NOTES);	
			getLogBookType().get(0).getRelatedLogBookTypes().add(LogBookTypeEnum.TYPE_PURCHASE_ORDER_NOTES);
			getLogBookType().get(0).setRenderEntrySource(true);			
			getLogBookType().add(new LogBookTypeVO(LogBookTypeEnum.TYPE_EXTERNAL_TAL_FILE_NOTES, true));
			setSelectedFleetMaster(null);
			
			setUpfitPOStatuses(new ArrayList<DocumentStatus>());
			getUpfitPOStatuses().add(DocumentStatus.PURCHASE_ORDER_STATUS_OPEN);
			getUpfitPOStatuses().add(DocumentStatus.PURCHASE_ORDER_STATUS_RELEASED);			
			
			setShowNotes(Boolean.parseBoolean(fc.getApplication().evaluateExpressionGet(fc,"#{cc.attrs.showNotes}", String.class)));
			setShowUpfitProgress(fc.getApplication().evaluateExpressionGet(fc,"#{cc.attrs.showUpfitProgress}", Boolean.class));			
			setShowSearch(fc.getApplication().evaluateExpressionGet(fc,"#{cc.attrs.showSearch}", Boolean.class));
			setShowCancel(fc.getApplication().evaluateExpressionGet(fc,"#{cc.attrs.showCancel}", Boolean.class));
			setEnquiryMode(fc.getApplication().evaluateExpressionGet(fc,"#{cc.attrs.enquiryMode}", Boolean.class));
			if(isEnquiryMode()) {
				setMainPODocId(fc.getApplication().evaluateExpressionGet(fc,"#{cc.attrs.mainPODocId}", Long.class));
				if(!MALUtilities.isEmpty(getMainPODocId())) {
					doc = documentService.getDocumentByDocId(getMainPODocId());
					loadData(doc);
				}
			} else {
				setSearchCriteria(new VehicleOrderStatusSearchCriteriaVO());
				setAddNotes(fc.getApplication().evaluateExpressionGet(fc,"#{cc.attrs.addNotes}", Boolean.class));
				if(addNotes){
					getLogBookType().get(0).setReadOnly(!addNotes);
				}
			}
			setDisableRestore(true);
			setHasEditPermission(hasPermission(EDIT_PERMISSION_NAME));
			if(isHasEditPermission()){
				progressTypeCodeList =progressTypeCodeDAO.findAllOrderByProgressType();
			}else{
				progressTypeCodeList =progressTypeCodeDAO.findAllNonSysGenOrderByProgressType();
			}
			setSelectedSupplierProgressHistory(null);
			setHasManagePermission(hasPermission(MANAGE_PERMISSION_NAME));	
			setNeedRefresh(false);
		} catch (Exception e) {
			super.addErrorMessage("generic.error", e.getMessage());
		}

	}
	
	/**
	 * Called from Vehicle Order Status dialog on UNTPRG
	 */
	public void service() {
		clearVinValidationData();
		setDisableRestore(true);
		setFetchAllEntry(false);
		setToggleLabel("Show All");
		getDataTable().initialize(250, 750, 750, 0, 0, getDataTable().getCols());
		FacesContext fc = FacesContext.getCurrentInstance();			
		Long docId  = fc.getApplication().evaluateExpressionGet(fc,"#{cc.attrs.mainPODocId}", Long.class);	
		boolean isStockUnit = fc.getApplication().evaluateExpressionGet(fc,"#{cc.attrs.isStockUnit}", Boolean.class);
		showSearch = fc.getApplication().evaluateExpressionGet(fc,"#{cc.attrs.showSearch}", Boolean.class);
		showNotes = fc.getApplication().evaluateExpressionGet(fc,"#{cc.attrs.showNotes}", Boolean.class);
		showUpfitProgress = fc.getApplication().evaluateExpressionGet(fc,"#{cc.attrs.showUpfitProgress}", Boolean.class);
		showCancel = fc.getApplication().evaluateExpressionGet(fc,"#{cc.attrs.showCancel}", Boolean.class);
		String clientId = (fc.getApplication().evaluateExpressionGet(fc,"#{cc.attrs.clientId}", String.class));
		String updateId = fc.getApplication().evaluateExpressionGet(fc,"#{cc.attrs.update}", String.class);
		logger.info("MaintainVehicleOrderStatusBean showSearch: "+showSearch+" docId=="+docId+" isStockUnit "+isStockUnit +" clientId "+clientId );
		fmsId = fc.getApplication().evaluateExpressionGet(fc,"#{cc.attrs.fmsId}", Long.class);
		unitNo = (fc.getApplication().evaluateExpressionGet(fc,"#{cc.attrs.unitNo}", String.class));
		service(docId, isStockUnit, unitNo);
		setNeedRefresh(false);
		RequestContext.getCurrentInstance().update(updateId);
	}
	
	public void service(Long docId, boolean isStockUnit, String unitNo) {
		try {
			clearDisplayValue();
			if(docId <= 0) {
				addErrorMessageSummary("custom.message", "Could not find purchase order");
				return;
			}
		    this.doc = documentService.getDocumentByDocId(docId);
			populateValue(doc, isStockUnit, unitNo);
			supplierProgressHistoryList = supplierService.getSupplierProgressHistory(mainPODocId, fetchAllEntry);
		} catch (Exception e) {
			super.addErrorMessage("generic.error", e.getMessage());
		}
	}
	//This method get call from <f:metadata
	public void fastPathService() {
		String mainPODocIdStr = this.getRequestParameter("mainPODocId");
		
		if(MALUtilities.isNotEmptyString(mainPODocIdStr)){
			this.mainPODocId = Long.parseLong(mainPODocIdStr);
			Doc poDoc = documentService.getDocumentByDocId(mainPODocId);
			loadData(poDoc);	
		}
		
	}
	
	private void clearVinValidationData() {
		setUserConfirmation(false);
		setChromeValidationDone(false);
		setShowWarning(false);
	}
	
	public void clearDisplayValue(){
		setUnitDesc("");
		setVin("");
		setVinLabel("");
		setUnitNo("");
		setClientName("");
		setFactoryNo("");
		setFactoryNoLabel("");
		setMainPODocId(null);
		setDocPONo("");
		setVendorName("");
		setVendorName("");
		setRequestedDate("");
		setDriverName("");
		setAcquisitionType("");
		setOrderingLeadTime("");
		setUpfitYN("");
		supplierProgressHistoryList.clear();
	}
	
	/** Called from Vehicle Order Status Search Screen
	 * @param mainPODoc
	 * @throws MalBusinessException
	 */
	public void populateValue(Doc mainPODoc) throws MalBusinessException {
		this.fleetMaster = fleetMasterService.getFleetMasterByDocId(mainPODoc.getDocId());
		setSelectedFleetMaster(fleetMaster);
		
		setFmsId(fleetMaster.getFmsId());
		setVin(fleetMaster.getVin());
		setVinLabel(fleetMaster.getVin());
		setUnitNo(fleetMaster.getUnitNo());
		setUnitDesc(fleetMaster.getModel().getModelDescription());
		setMakeId(fleetMaster.getModel().getMake().getMakId());
		
		this.quotationModel = quotationService.getQuotationModelFromUnitNo(fleetMaster.getUnitNo());
		if(quotationModel != null) {
			if(quotationModel.getQuotation().getDrvDrvId() != null) {
				this.driver = driverDAO.findById(quotationModel.getQuotation().getDrvDrvId()).orElse(null);
				if(!MALUtilities.isEmpty(driver)) {
					setDriverName(driver.getDriverSurname() + ", " + driver.getDriverForename());
				}
			}

			setQmdId(quotationModel.getQmdId());
			setClientName(quotationModel.getQuotation().getExternalAccount().getAccountName());		
			setRequestedDate(quotationService.getClientRequestTypeValue(qmdId));
		}
		
		setUpfitYN(purchaseOrderService.getUnitLeadTimeByDocId(mainPODoc.getDocId()) > 0 ? "Yes" : "No" );
		List<DocPropertyValue> docPropertyValueList = docPropertyValueService.findAllByDocId(mainPODoc.getDocId());
		for(DocPropertyValue dp : docPropertyValueList){
			if(dp.getDocProperty().getName().equals(DocPropertyEnum.ACQUISITION_TYPE.name())){
				setAcquisitionType(dp.getPropertyValue());
			}else if(dp.getDocProperty().getName().equals(DocPropertyEnum.PO_ORDERING_LEAD_TIME.name())){
				setOrderingLeadTime(dp.getPropertyValue());
			}
		}
		
		setFactoryNo(mainPODoc.getExternalRefNo());
		setDocPONo(mainPODoc.getDocNo());	
		setMainPODocId(mainPODoc.getDocId());
		setFactoryNoLabel(mainPODoc.getExternalRefNo());
		if(mainPODoc.getAccountCode() != null) {
			ExternalAccount vendor = externalAccountDAO.findById(new ExternalAccountPK(mainPODoc.getEaCId(), mainPODoc.getAccountType(), mainPODoc.getAccountCode())).orElse(null);
			if(!MALUtilities.isEmpty(vendor)) {
				setVendorName(vendor.getAccountName());
			}
		}
	}
	
	/**
	 * Called from Vehicle Order Status dialog on UNTPRG
	 * @param mainPODoc
	 * @param isStockUnit
	 * @throws MalBusinessException
	 */
	public  void populateValue(Doc mainPODoc, boolean isStockUnit, String unitNo) throws MalBusinessException{
		
		if(mainPODoc.getGenericExtId() != null){//may be null for stock order
			this.quotationModel = quotationService.getQuotationModel(mainPODoc.getGenericExtId());
			
			if(quotationModel.getQuotation().getDrvDrvId() != null){
				 this.driver = driverDAO.findById(quotationModel.getQuotation().getDrvDrvId()).orElse(null);
				if(!MALUtilities.isEmpty(driver)) {
					setDriverName(driver.getDriverSurname() + ", " + driver.getDriverForename());
				}
			}	
			setQmdId(quotationModel.getQmdId());
			setClientName(quotationModel.getQuotation().getExternalAccount().getAccountName());		
			setRequestedDate(quotationService.getClientRequestTypeValue(qmdId));
		}		
		if(!MALUtilities.isEmpty(fmsId) && fmsId != 0l){
			this.fleetMaster = fleetMasterService.getFleetMasterByFmsId(fmsId);
		}else if(!MALUtilities.isEmpty(unitNo)){
			this.fleetMaster = fleetMasterService.findByUnitNo(unitNo);
		}else{
			List<Dist> distList  = distDAO.findDistByDocId(mainPODoc.getDocId());
			this.fleetMaster = fleetMasterService.getFleetMasterByFmsId(Long.parseLong(distList.get(0).getCdbCode1()));
		}
		
		setFmsId(fleetMaster.getFmsId());
		setVin(fleetMaster.getVin());
		setVinLabel(fleetMaster.getVin());
		setUnitNo(fleetMaster.getUnitNo());
		setUnitDesc(fleetMaster.getModel().getModelDescription());
		setMakeId(fleetMaster.getModel().getMake().getMakId());
		
		ExternalAccount vendor =  null;
		if (isStockUnit) {
			Doc stockOriginalMainPO = documentService.getMainPODocOfStockUnit(fleetMaster.getFmsId());
			if(stockOriginalMainPO == null){
				addErrorMessageSummary("custom.message", "Could not find unique main purchase order");
				return;
			}
			setFactoryNo(stockOriginalMainPO.getExternalRefNo());
			setFactoryNoLabel(stockOriginalMainPO.getExternalRefNo());
			setDocPONo(stockOriginalMainPO.getDocNo());	
			setMainPODocId(stockOriginalMainPO.getDocId());	//Setting  original main PO for stock 	
			vendor =  externalAccountDAO.findById(new ExternalAccountPK(stockOriginalMainPO.getEaCId(), stockOriginalMainPO.getAccountType(),  stockOriginalMainPO.getAccountCode())).orElse(null);
		}else{
			setFactoryNo(mainPODoc.getExternalRefNo());
			setFactoryNoLabel(mainPODoc.getExternalRefNo());
			setDocPONo(mainPODoc.getDocNo());	
			setMainPODocId(mainPODoc.getDocId());	
			if(mainPODoc.getAccountCode() != null){
				vendor =  externalAccountDAO.findById(new ExternalAccountPK(mainPODoc.getEaCId(), mainPODoc.getAccountType(),  mainPODoc.getAccountCode())).orElse(null);
			}
		}
		
		if(vendor != null){
			setVendorName(vendor.getAccountName());
		}
		
		setUpfitYN(purchaseOrderService.getUnitLeadTimeByDocId(mainPODoc.getDocId()) > 0 ? "Yes" : "No" );
		List<DocPropertyValue> docPropertyValueList = docPropertyValueService.findAllByDocId(mainPODoc.getDocId());
		for(DocPropertyValue dp : docPropertyValueList){
			if(dp.getDocProperty().getName().equals(DocPropertyEnum.ACQUISITION_TYPE.name())){
				setAcquisitionType(dp.getPropertyValue());
			}else if(dp.getDocProperty().getName().equals(DocPropertyEnum.PO_ORDERING_LEAD_TIME.name())){
				setOrderingLeadTime(dp.getPropertyValue());
			}
		}
	}
	
	/** Called from Vehicle Order Status Search Screen
	 * @throws MalBusinessException
	 */
	public void performSearch() {
		try {
			clearVinValidationData();
			clearDisplayValue();
			toggleLabel  = "Show All";
			fetchAllEntry =  false;
			if(!MALUtilities.isEmptyString(searchCriteria.getUnitNo())  || !MALUtilities.isEmptyString(searchCriteria.getPurchaseOrderNumber()) || !MALUtilities.isEmptyString(searchCriteria.getFactoryNo())){
				Doc doc = documentService.searchMainPurchaseOrder(searchCriteria);
				if(doc != null){				
					setMainPODocId(doc.getDocId());				
					loadData(doc);				
				}
			}
		} catch (MalBusinessException e) {
			super.addErrorMessageSummary("generic.error", e.getMessage());
		}
	}

	private void loadData(Doc doc) {
		try {
			populateValue(doc);
			supplierProgressHistoryList = supplierService.getSupplierProgressHistory(mainPODocId,fetchAllEntry);
		} catch (MalBusinessException e) {
			super.addErrorMessage("generic.error", e.getMessage());
		}				
	}
	
	private boolean isBlankRowAdded() {
		boolean rowAdded = false;
		for(SupplierProgressHistory supplierProgressHistory : supplierProgressHistoryList) {
			if(supplierProgressHistory.getSphId() != null && supplierProgressHistory.getSphId() <= 0) {
				rowAdded = true;
			}
		}
		return rowAdded;
	}
	
	public void progressTypeChangeListener() {
		SupplierProgressHistory selectedSupplierProgressHistory = getSelectedSupplierProgressHistory();
		
		if(!MALUtilities.isEmpty(selectedSupplierProgressHistory.getProgressType())) {
			for(ProgressTypeCode progressTypeCode : getProgressTypeCodeList()) {
				if(progressTypeCode.getProgressType().equals(selectedSupplierProgressHistory.getProgressType())) {
					selectedSupplierProgressHistory.setProgressTypeCode(progressTypeCode);
					break;
				}
			}
		} else {
			selectedSupplierProgressHistory.setProgressTypeCode(null);
		}
	}
	
	public void upfitLogisticsListener(){
		Doc mainPO;
		
		try {
			mainPO = documentService.getDocumentByDocId(mainPODocId);
			setSelectedQuotationModel(quotationService.getQuotationModel(mainPO.getGenericExtId()));
		} catch (MalBusinessException e) {
			logger.debug(e.getMessage());
			super.addErrorMessage("generic.error", e.getMessage());
		}
	}	
	
	public void add(){
		if(MALUtilities.isEmpty(getMainPODocId())) {
			super.addErrorMessageSummary("custom.message", "Please perform a valid search first");
			return;
		}
		
		setDisableRestore(false);
		if(isBlankRowAdded()){
			return;
		}
		
		for (SupplierProgressHistory supplierProgressHistory : supplierProgressHistoryList) {
			supplierProgressHistory.setUpdateMode(false);
		}
		SupplierProgressHistory  supplierProgressHistory = new SupplierProgressHistory();
		supplierProgressHistory.setSphId(-1L);
		supplierProgressHistory.setUpdateMode(true);
		supplierProgressHistory.setDocId(getMainPODocId());
		supplierProgressHistory.setEnteredDate(new Date());
		supplierProgressHistory.setOpCode(getLoggedInUser().getEmployeeNo());
		setSelectedSupplierProgressHistory(supplierProgressHistory);
		supplierProgressHistoryList.add(0, supplierProgressHistory);
		
	}
	
	public void editSupplierProgress(SupplierProgressHistory supplierProgressHistory){
		setSelectedSupplierProgressHistory(supplierProgressHistory);
		setDisableRestore(false);
		for (Iterator<SupplierProgressHistory> supplierProgressHistoryItr = supplierProgressHistoryList.iterator(); supplierProgressHistoryItr.hasNext();){
			SupplierProgressHistory sph  = supplierProgressHistoryItr.next();
			
			if(sph.getSphId() == selectedSupplierProgressHistory.getSphId()){
				sph.setUpdateMode(true);
			}else{
				sph.setUpdateMode(false);
			}	
			if(sph.getSphId()== null || sph.getSphId() <= 0){				
				if(sph.getSphId() == selectedSupplierProgressHistory.getSphId()){
					addErrorMessageSummary("custom.message", "Please select a Vehicle Order Status");
				}
				supplierProgressHistoryItr.remove();
			}
		    	  
		}
		
	}
	
	public void save(){		
		
		if(getSelectedSupplierProgressHistory() == null){
			addErrorMessageSummary("custom.message", "Please select a Vehicle Order Status");
			return;
		}
		if(getSelectedSupplierProgressHistory().isUpdateMode() == false){
			addErrorMessageSummary("custom.message", "Selected Vehicle Order Status is not in edit mode");
			return;
		}
		if(validateInput()){
			try {
				
				SupplierProgressHistory supplierProgressHistory = getSelectedSupplierProgressHistory();	
				
				supplierProgressHistory.setOpCode(getLoggedInUser().getEmployeeNo());
				supplierProgressHistory.setEnteredDate(new Date());
				
				boolean vehicleReadyNotificationIsNeeded = false;				
				if((MALUtilities.isEmpty(supplierProgressHistory.getSphId()) || supplierProgressHistory.getSphId() < 0)
						&& UnitProgressCodeEnum.VEHRDY.getCode().equals(supplierProgressHistory.getProgressType())){//Notify on Add only
					
					if(purchasingEmailService.isVehicleReadyNotificationEmailable(supplierProgressHistory, quotationModel)) {						
						if(fleetMaster != null && quotationModel != null && driver != null) {
							vehicleReadyNotificationIsNeeded = true;
						}
					}
				}
			
				if(vehicleReadyNotificationIsNeeded  ){//not applicable for stock order		
					
					logger.debug("Before call emailVehicleReadyNotification sph " + supplierProgressHistory.getSphId());
					supplierService.saveVehicleReadyAndSendNotification(supplierProgressHistory, doc ,fleetMaster , quotationModel, driver , super.getLoggedInUser().getEmployeeNo(), super.getLoggedInUser().getCorporateEntity());
					logger.debug("After call emailVehicleReadyNotification sph " + supplierProgressHistory.getSphId());		
					
				}else {
					supplierService.saveSupplierProgressHistory(supplierProgressHistory);
				}
				
				setNeedRefresh(true);
				//Updated for OTD-822 : ETA calculation from 15_RECEIVD status
				if(!supplierProgressHistory.getProgressType().equals(UnitProgressCodeEnum.ETA.getCode())){
					supplierService.performPostSupplierUpdateJob("Y", "Y", 1L, supplierProgressHistory.getProgressType(), supplierProgressHistory.getEnteredDate(), supplierProgressHistory.getActionDate(), 
																getFmsId(), getMakeId(), getQmdId(), getMainPODocId(), getLoggedInUser().getEmployeeNo(), null);
					
				}
				supplierProgressHistoryList = supplierService.getSupplierProgressHistory(mainPODocId,fetchAllEntry);
				setSelectedSupplierProgressHistory(null);
				
				addInfoMessageSummary("saved.success", "Vehicle Order Status");
			} catch(MalBusinessException mbe) {
				addErrorMessageSummary("custom.message", mbe.getMessage());
			} catch (Exception e) {
				logger.error(e);
				addErrorMessageSummary("custom.message", e.getMessage());
			}
		}
		
		setDisableRestore(true);
	}
		
	public void showSearh(){
		showSearch = true;
		clearDisplayValue();		
	}
	
	public void toggleFetchAllEntry(){		
		if(toggleLabel.equals("Show All")){
			toggleLabel  = "Show Latest";
			fetchAllEntry =  true;
		}else{
			toggleLabel  = "Show All";
			fetchAllEntry =  false;
		}
		
		setSelectedSupplierProgressHistory(null);
		supplierProgressHistoryList = supplierService.getSupplierProgressHistory(mainPODocId,fetchAllEntry);
	}
	
	public void delete(){
		
		if(getSelectedSupplierProgressHistory() == null){
			addErrorMessageSummary("custom.message", "Please select a Vehicle Order Status");
			return;
		}
		
		try {		
			 for (Iterator<SupplierProgressHistory> supplierProgressHistoryItr = supplierProgressHistoryList.iterator(); supplierProgressHistoryItr.hasNext();){
			      if(supplierProgressHistoryItr.next().getSphId() == selectedSupplierProgressHistory.getSphId()){	
			    	  if(selectedSupplierProgressHistory.getSphId() > 0){// mean its already  saved in DB not a new row 
			    		  supplierService.deleteSupplierProgressHistory(selectedSupplierProgressHistory.getSphId());
			    	  }
			    	  supplierProgressHistoryItr.remove();
			    	  supplierProgressHistoryList = supplierService.getSupplierProgressHistory(mainPODocId ,fetchAllEntry);
					  setSelectedSupplierProgressHistory(null);
			    	  addInfoMessageSummary("custom.message", "Vehicle Order Status deleted successfully");
			    	  setNeedRefresh(true);
			    	  break;
			      }
			    }
		} catch (Exception e) {
			logger.error(e);
			addErrorMessageSummary("custom.message", "Could not delete Vehicle Order Status");
		}
		
	}
	
	public void deleteSupplierProgressListener(SupplierProgressHistory supplierProgressHistory){
		setSelectedSupplierProgressHistory(supplierProgressHistory);
		for (SupplierProgressHistory supplierProgressHistory1 : supplierProgressHistoryList) {
			supplierProgressHistory1.setUpdateMode(false);
		}
	}
	
	
	public void restore(){
		setDisableRestore(true);
		setSelectedSupplierProgressHistory(null);
		for (Iterator<SupplierProgressHistory> supplierProgressHistoryItr = supplierProgressHistoryList.iterator(); supplierProgressHistoryItr.hasNext();){
			SupplierProgressHistory  supplierProgressHistory  = supplierProgressHistoryItr.next();
			supplierProgressHistory.setUpdateMode(false);
				
			if(supplierProgressHistory.getSphId()== null || supplierProgressHistory.getSphId() <= 0){
				supplierProgressHistoryItr.remove();
			}
		    	  
		}
	}
	private boolean validateInput(){
			boolean success= true;
			
	
			if(getSelectedSupplierProgressHistory().isUpdateMode()){
				if(getSelectedSupplierProgressHistory().getProgressType() == null){
					addErrorMessageSummary("required.field", "Status Code");
					success= false;
				}
				if(getSelectedSupplierProgressHistory().getActionDate() == null){
					addErrorMessageSummary("required.field", "Status Date");
					success= false;
				}
				if(MALUtilities.isEmpty(getSelectedSupplierProgressHistory().getSupplier())){
					addErrorMessageSummary("required.field", "Source");
					success= false;
				}
			}
			if(success){
			
				 List<SupplierProgressHistory> dbSuppList =  supplierService.getSupplierProgress(mainPODocId, getSelectedSupplierProgressHistory().getProgressType(),
																								getSelectedSupplierProgressHistory().getActionDate());
					if((getSelectedSupplierProgressHistory().getSphId() == null || getSelectedSupplierProgressHistory().getSphId() <= 0) && dbSuppList.size() > 0){						
						success = false;	
					}else if(dbSuppList.size() > 0  && ! dbSuppList.get(0).getSphId().equals(getSelectedSupplierProgressHistory().getSphId()) ){
						success = false;		
					}
						
					if(! success){
						addErrorMessageSummary("custom.message", "A record already exists with Status Code of "+getSelectedSupplierProgressHistory().getProgressType()
													+" and Status Date of "+MALUtilities.getNullSafeDatetoString(getSelectedSupplierProgressHistory().getActionDate()));
					}
				
			}
		
		return success;
	}
	public void onHideListener(){
		RequestContext.getCurrentInstance().execute(getOnCloseCallback());		
	}

	public void saveVin() {
		RequestContext context = RequestContext.getCurrentInstance();
		showWarning = false;
		warningMessage = null;
		Map<String, Object> resultMap = null;
		FleetMasterVinDetails fleetMasterVinDetails = null;
		if(!userConfirmation && !MALUtilities.isEmptyString(getVinLabel())) {
			 resultMap = vinDecoderService.validateVINAndReturnMessage(getVinLabel(), getFmsId());			
		}	
		try {
			if(((resultMap != null &&  resultMap.size() == 0)  || userConfirmation) && chromeValidationDone == false){
				 fleetMasterVinDetails = vinDecoderService.validateVINByChrome(getVinLabel(),getFmsId());
				 setChromeValidationDone(true);
				 
				 resultMap = new HashMap<String, Object>();
				 if(fleetMasterVinDetails != null && !MALUtilities.isEmpty(fleetMasterVinDetails.getYear()) && !MALUtilities.isEmpty(fleetMasterVinDetails.getMakeDesc()) ){
						
					Integer modelYear = Integer.valueOf(fleetMasterVinDetails.getYear());
					String makeName = fleetMasterVinDetails.getMakeDesc();
					//String modelName = vinDecoderResponseVO.getBestModelName();
					String chromeVinDesc= modelYear +"/"+makeName;
					FleetMaster fleetMaster = fleetMasterService.getFleetMasterByFmsId(getFmsId());	
					Model model =  fleetMaster.getModel();
					ModelMarkYear modelMarkYear = model.getModelMarkYear();
					Make make  = model.getMake();
					if(modelYear.intValue() != Integer.parseInt(modelMarkYear.getModelMarkYearDesc()) 
							|| (! makeName.equalsIgnoreCase(make.getMakeDesc()))  ){
						
						resultMap.put(VinDecoderService.ERROR_TYPE_BLOCKER, true);						
						resultMap.put(VinDecoderService.MESSAGE, malMessage.getMessage("vin.ws.validation.mismatch.error", chromeVinDesc));	
					}
				 }else{
					 resultMap.put(VinDecoderService.ERROR_TYPE_WARNING, true);
					 resultMap.put(VinDecoderService.MESSAGE, malMessage.getMessage("vin.ws.validation.error"));//No info for vin or wrong vin	
				 }
			}
			if (resultMap != null && !resultMap.isEmpty()) {
				boolean isFail = MALUtilities.isEmpty(resultMap.get(VinDecoderService.ERROR_TYPE_BLOCKER)) ? false : (Boolean) resultMap.get(VinDecoderService.ERROR_TYPE_BLOCKER);
				boolean isWarning = MALUtilities.isEmpty(resultMap.get(VinDecoderService.ERROR_TYPE_WARNING)) ? false : (Boolean) resultMap.get(VinDecoderService.ERROR_TYPE_WARNING);
				String errorMessage = MALUtilities.isEmpty(resultMap.get(VinDecoderService.MESSAGE)) ? null : (String) resultMap .get(VinDecoderService.MESSAGE);
				
				if (isFail) {
					setChromeValidationDone(false);
					setUserConfirmation(false);
					addErrorMessageSummary("plain.message",new String[] { errorMessage });
		        	context.addCallbackParam("failure", true);
		        	return;
				}
				if (isWarning) {
					setShowWarning(true);
					setWarningMessage(errorMessage);
					context.update("maintainVehicleOrderStatusCCId:confirmVinDialogId");
		        	context.addCallbackParam("warning", true);
		        	return;
				}
			}
		
		
			if(!MALUtilities.isEmptyString(getVinLabel())) {
				FleetMaster fleetMaster = fleetMasterService.getFleetMasterByFmsId(getFmsId());
				fleetMaster.setVin(getVinLabel());
				fleetMasterService.saveUpdateFleetMaster(fleetMaster);
				setVin(getVinLabel());
				if(fleetMasterVinDetails != null){
					FleetMasterVinDetails fleetMasterVinDetailsRecordByFmsId = vinDecoderService.getFleetMasterVinDetailByFmsId(getFmsId());
					fleetMasterVinDetailsRecordByFmsId.setFleetMaster(fleetMasterVinDetails.getFleetMaster());
					fleetMasterVinDetailsRecordByFmsId.setVin(fleetMasterVinDetails.getVin());
					fleetMasterVinDetailsRecordByFmsId.setYear(fleetMasterVinDetails.getYear());
					fleetMasterVinDetailsRecordByFmsId.setMakeDesc(fleetMasterVinDetails.getMakeDesc());
					fleetMasterVinDetailsRecordByFmsId.setModelDesc(fleetMasterVinDetails.getModelDesc());
					fleetMasterVinDetailsRecordByFmsId.setModelTypeDesc(fleetMasterVinDetails.getModelTypeDesc());
					fleetMasterVinDetailsRecordByFmsId.setEngineDesc(fleetMasterVinDetails.getEngineDesc());
					fleetMasterVinDetailsRecordByFmsId.setFuelType(fleetMasterVinDetails.getFuelType());
					fleetMasterVinDetailsRecordByFmsId.setStyleDesc(fleetMasterVinDetails.getStyleDesc());
					fleetMasterVinDetailsRecordByFmsId.setTrimDesc(fleetMasterVinDetails.getTrimDesc());
					
					vinDecoderService.saveFleetMasterVinDetail(fleetMasterVinDetailsRecordByFmsId);
				}
				addInfoMessageSummary("saved.success", "VIN");
			} else {
		    	context.addCallbackParam("empty", true);
			}
		} catch (Exception e) {
			addErrorMessageSummary("custom.message", "Could not update VIN");
	    	context.addCallbackParam("failure", true);
		}
	}
	
	public void saveOnConfirm() {
		setUserConfirmation(true); 
		saveVin();
	}
	
	public void cancelOnConfirm() {
		setUserConfirmation(false);
		setChromeValidationDone(false);
	}
	
	public void cancelSaveVin() {
		setVinLabel("");
	}
	
	public void saveFactoryNo() {
		try {
			if(!MALUtilities.isEmptyString(getFactoryNoLabel())) {
				Doc doc = documentService.getDocumentByDocId(mainPODocId);
				doc.setExternalRefNo(getFactoryNoLabel());
				documentService.saveUpdateDoc(doc);
				setFactoryNo(getFactoryNoLabel());
				addInfoMessageSummary("saved.success", "Factory No");
			}
		} catch (Exception e) {
			addErrorMessageSummary("custom.message", "Could not update Factory No");
		}
	}
	
	public void cancelSaveFactoryNo() {
		if(!MALUtilities.isEmptyString(getFactoryNo())) {
			setFactoryNoLabel(getFactoryNo());
		} else {
			setFactoryNoLabel("");
		}
	}
	
	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public List<SupplierProgressHistory> getSupplierProgressHistoryList() {
		return supplierProgressHistoryList;
	}

	public void setSupplierProgressHistoryList(List<SupplierProgressHistory> supplierProgressHistoryList) {
		this.supplierProgressHistoryList = supplierProgressHistoryList;
	}

	public String getOnCloseCallback() {
		return onCloseCallback;
	}

	public void setOnCloseCallback(String onCloseCallback) {
		this.onCloseCallback = onCloseCallback;
	}

	public String getResource() {
		return resource;
	}

	public void setResource(String resource) {
		this.resource = resource;
	}

	public String getUnitNo() {
		return unitNo;
	}

	public void setUnitNo(String unitNo) {
		this.unitNo = unitNo;
	}
	public String getFactoryNo() {
		return factoryNo;
	}

	public void setFactoryNo(String factoryNo) {
		this.factoryNo = factoryNo;
	}

	public String getVin() {
		return vin;
	}

	public void setVin(String vin) {
		this.vin = vin;
	}

	public String getVinLabel() {
		return vinLabel;
	}

	public void setVinLabel(String vinLabel) {
		this.vinLabel = vinLabel;
	}

	public String getFactoryNoLabel() {
		return factoryNoLabel;
	}

	public void setFactoryNoLabel(String factoryNoLabel) {
		this.factoryNoLabel = factoryNoLabel;
	}

	public String getVehicleDesc() {
		return vehicleDesc;
	}

	public void setVehicleDesc(String vehicleDesc) {
		this.vehicleDesc = vehicleDesc;
	}

	public boolean isShowSearch() {
		return showSearch;
	}

	public void setShowSearch(boolean showSearch) {
		this.showSearch = showSearch;
	}

	public boolean isShowNotes() {
		return showNotes;
	}

	public void setShowNotes(boolean showNotes) {
		this.showNotes = showNotes;
	}

	public boolean isShowUpfitProgress() {
		return showUpfitProgress;
	}

	public void setShowUpfitProgress(boolean showUpfitProgress) {
		this.showUpfitProgress = showUpfitProgress;
	}

	public boolean isShowCancel() {
		return showCancel;
	}

	public void setShowCancel(boolean showCancel) {
		this.showCancel = showCancel;
	}

	public List<ProgressTypeCode> getProgressTypeCodeList() {
		return progressTypeCodeList;
	}

	public void setProgressTypeCodeList(List<ProgressTypeCode> progressTypeCodeList) {
		this.progressTypeCodeList = progressTypeCodeList;
	}

	public List<String> getPoOrderNoList() {
		return poOrderNoList;
	}

	public void setPoOrderNoList(List<String> poOrderNoList) {
		this.poOrderNoList = poOrderNoList;
	}

	public Long getMainPODocId() {
		return mainPODocId;
	}

	public void setMainPODocId(Long mainPODocId) {
		this.mainPODocId = mainPODocId;
	}

	public SupplierProgressHistory getSelectedSupplierProgressHistory() {
		return selectedSupplierProgressHistory;
	}

	public void setSelectedSupplierProgressHistory(SupplierProgressHistory selectedSupplierProgressHistory) {
		this.selectedSupplierProgressHistory = selectedSupplierProgressHistory;
	}

	public String getDriverName() {
		return driverName;
	}

	public void setDriverName(String driverName) {
		this.driverName = driverName;
	}

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	public String getVendorName() {
		return vendorName;
	}

	public void setVendorName(String vendorName) {
		this.vendorName = vendorName;
	}

	public String getUnitDesc() {
		return unitDesc;
	}

	public void setUnitDesc(String unitDesc) {
		this.unitDesc = unitDesc;
	}

	public String getDocPONo() {
		return docPONo;
	}

	public void setDocPONo(String docPONo) {
		this.docPONo = docPONo;
	}

	public String getRequestedDate() {
		return requestedDate;
	}

	public void setRequestedDate(String requestedDate) {
		this.requestedDate = requestedDate;
	}

	public boolean isFetchAllEntry() {
		return fetchAllEntry;
	}

	public void setFetchAllEntry(boolean fetchAllEntry) {
		this.fetchAllEntry = fetchAllEntry;
	}

	public long getFmsId() {
		return fmsId;
	}

	public void setFmsId(long fmsId) {
		this.fmsId = fmsId;
	}

	public Long getQmdId() {
		return qmdId;
	}

	public void setQmdId(Long qmdId) {
		this.qmdId = qmdId;
	}

	public long getMakeId() {
		return makeId;
	}

	public void setMakeId(long makeId) {
		this.makeId = makeId;
	}

	public String getToggleLabel() {
		return toggleLabel;
	}

	public void setToggleLabel(String toggleLabel) {
		this.toggleLabel = toggleLabel;
	}

	public boolean isDisableRestore() {
		return disableRestore;
	}

	public void setDisableRestore(boolean disableRestore) {
		this.disableRestore = disableRestore;
	}

	public VehicleOrderStatusSearchCriteriaVO getSearchCriteria() {
		return searchCriteria;
	}

	public void setSearchCriteria(VehicleOrderStatusSearchCriteriaVO searchCriteria) {
		this.searchCriteria = searchCriteria;
	}

	public boolean isHasManagePermission() {
		return hasManagePermission;
	}

	public void setHasManagePermission(boolean hasManagePermission) {
		this.hasManagePermission = hasManagePermission;
	}

	public FleetMaster getSelectedFleetMaster() {
		return selectedFleetMaster;
	}

	public void setSelectedFleetMaster(FleetMaster selectedFleetMaster) {
		this.selectedFleetMaster = selectedFleetMaster;
	}

	public List<LogBookTypeVO> getLogBookType() {
		return logBookType;
	}

	public void setLogBookType(List<LogBookTypeVO> logBookType) {
		this.logBookType = logBookType;
	}

	public boolean isEnquiryMode() {
		return enquiryMode;
	}

	public void setEnquiryMode(boolean enquiryMode) {
		this.enquiryMode = enquiryMode;
	}

	public boolean isUserConfirmation() {
		return userConfirmation;
	}

	public void setUserConfirmation(boolean userConfirmation) {
		this.userConfirmation = userConfirmation;
	}

	public boolean isChromeValidationDone() {
		return chromeValidationDone;
	}

	public void setChromeValidationDone(boolean chromeValidationDone) {
		this.chromeValidationDone = chromeValidationDone;
	}

	public boolean isShowWarning() {
		return showWarning;
	}

	public void setShowWarning(boolean showWarning) {
		this.showWarning = showWarning;
	}

	public String getWarningMessage() {
		return warningMessage;
	}

	public void setWarningMessage(String warningMessage) {
		this.warningMessage = warningMessage;
	}

	public boolean isHasEditPermission() {
		return hasEditPermission;
	}

	public void setHasEditPermission(boolean hasEditPermission) {
		this.hasEditPermission = hasEditPermission;
	}

	public String getAcquisitionType() {
		return acquisitionType;
	}

	public void setAcquisitionType(String acquisitionType) {
		this.acquisitionType = acquisitionType;
	}

	public String getOrderingLeadTime() {
		return orderingLeadTime;
	}

	public void setOrderingLeadTime(String orderingLeadTime) {
		this.orderingLeadTime = orderingLeadTime;
	}

	public String getUpfitYN() {
		return upfitYN;
	}

	public void setUpfitYN(String upfitYN) {
		this.upfitYN = upfitYN;
	}

	public QuotationModel getSelectedQuotationModel() {
		return selectedQuotationModel;
	}

	public void setSelectedQuotationModel(QuotationModel selectedQuotationModel) {
		this.selectedQuotationModel = selectedQuotationModel;
	}

	public List<DocumentStatus> getUpfitPOStatuses() {
		return upfitPOStatuses;
	}

	public void setUpfitPOStatuses(List<DocumentStatus> upfitPOStatuses) {
		this.upfitPOStatuses = upfitPOStatuses;
	}

	@Override
	protected void loadNewPage() {
		thisPage.setPageUrl(ViewConstants.VEHICLE_ORDER_STATUS_SEARCH);
		
	}

	@Override
	protected void restoreOldPage() {
		
		
	}	
	public String cancel(){    	
    	return super.cancelPage();  
    }

	public boolean isAddNotes() {
		return addNotes;
	}

	public void setAddNotes(boolean addNotes) {
		this.addNotes = addNotes;
	}

	public boolean isNeedRefresh() {
		return needRefresh;
	}

	public void setNeedRefresh(boolean needRefresh) {
		this.needRefresh = needRefresh;
	}

}
