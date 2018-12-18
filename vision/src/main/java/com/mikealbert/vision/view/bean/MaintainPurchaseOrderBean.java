package com.mikealbert.vision.view.bean;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.faces.event.AjaxBehaviorEvent;

import net.sf.jasperreports.engine.JasperPrint;

import org.primefaces.context.RequestContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.mikealbert.common.MalMessage;
import com.mikealbert.data.dao.DocDAO;
import com.mikealbert.data.dao.PurchaseOrderReportDAO;
import com.mikealbert.data.entity.Doc;
import com.mikealbert.data.entity.FleetMaster;
import com.mikealbert.data.entity.FleetMasterVinDetails;
import com.mikealbert.data.entity.Quotation;
import com.mikealbert.data.enumeration.AcquisitionTypeEnum;
import com.mikealbert.data.enumeration.DocumentStatus;
import com.mikealbert.data.enumeration.EmailRequestEventEnum;
import com.mikealbert.data.enumeration.ReportNameEnum;
import com.mikealbert.data.enumeration.VehicleOrderType;
import com.mikealbert.data.vo.DbProcParamsVO;
import com.mikealbert.data.vo.VendorLovVO;
import com.mikealbert.service.DriverService;
import com.mikealbert.service.FleetMasterService;
import com.mikealbert.service.ProcessStageService;
import com.mikealbert.service.PurchaseOrderReportService;
import com.mikealbert.service.PurchaseOrderService;
import com.mikealbert.service.QuotationService;
import com.mikealbert.service.VinDecoderService;
import com.mikealbert.service.reporting.JasperReportService;
import com.mikealbert.service.vo.VehiclePurchaseOrderVO;
import com.mikealbert.util.MALUtilities;
import com.mikealbert.vision.service.PurchasingEmailService;
import com.mikealbert.vision.service.UnitProgressService;
import com.mikealbert.vision.service.VendorService;
import com.mikealbert.vision.view.ViewConstants;
import com.mikealbert.vision.vo.ClientOrderConfirmationListItemVO;
import com.mikealbert.vision.vo.ContactInfo;
import com.mikealbert.vision.vo.DocumentListItemVO;
import com.mikealbert.vision.vo.MainPurchaseOrderListItemVO;
import com.mikealbert.vision.vo.OrderSummaryListItemVO;

@Component
@Scope("view")
public class MaintainPurchaseOrderBean extends StatefulBaseBean {
	
	private static final long serialVersionUID = -8580470339366224389L;
	
	@Resource PurchaseOrderService purchaseOrderService;
	@Resource DocDAO docDAO;
	@Resource VendorService vendorService;
	@Resource JasperReportService jasperReportService;	
	@Resource PurchasingEmailService purchasingEmailService;
	@Resource VinDecoderService vinDecoderService;
	@Resource FleetMasterService fleetMasterService;
	@Resource PurchaseOrderReportService purchaseOrderReportService;
	@Resource MalMessage malMessage;
	@Resource QuotationService quotationService;
	@Resource DriverService driverService;
	@Resource UnitProgressService unitProgressService;
	@Resource ProcessStageService processStageService;
	
	private static final String ORDERING_INPUT_UI_ID =	"orderingDealer";
	private static final String ORDERING_LEAD_TIME_INPUT_UI_ID =	"orderingLeadTime";
	private static final String DELIVERING_INPUT_UI_ID =	"deliveringDealer";
	private static final String TOTAL_COST_INPUT_UI_ID = "totalCost";
	private static final String VIN_UI_ID = "vin";
	private static final String ACQUISITION_TYPE_UI_ID = "acquisitionType";
	private static final String USER_ACTION_RELEASE = "RELEASE";
	private static final String USER_ACTION_CONFIRM = "CONFIRM";
	private static final String ORDER_CONFIRMATION_EMAIL_SUCCESS_MESSAGE = "order.confirmation.successful";
	private static final String CALENDER_UI_ID = "confirmDateCal"; // Added for HD-246
	
	@Value("$email{email.purchasing.enabled}")
	private String purchasingEmailActive;

	@Value("${url.locator.internal.dealer.supplier}")
	private String baseURL;
	
	private VehiclePurchaseOrderVO maintainPoVO;
	List<String> acquisitionTypeList = new ArrayList<String>();
	List<String> logisticsList = new ArrayList<String>();

	private boolean userConfirmation = false;
	private boolean chromeValidationDone = false;
	private String userAction = "";
	private boolean showPOReport = false;
	private boolean isOrderingDlrValid = false;
	private boolean isDeliveringDlrValid = false;
	
	private FleetMasterVinDetails fleetMasterVinDetails;
	private boolean showWarning = false;
	private boolean showConfirmDocuments = false;
	private String warningMessage;
	private String totalCostFromUI;
	//Added for HD-246
	private Date confirmDate;	
	//Document List Component Parameters
	private List<DocumentListItemVO> documentListItems = new ArrayList<DocumentListItemVO>();
	private DocumentListItemVO selectedDocumentItem;
	private String documentListMessage;
	private boolean postConfirmCompleted;
	String leaseType = "";
	private List<String> selectedOptionalAccessories = new ArrayList<String>();
	private List<String> selectedPortInstalledAccessories = new ArrayList<String>();
	private List<String> selectedDealerInstalledAccessories = new ArrayList<String>();
	private List<String> vehicleInfoList = new ArrayList<String>();
	private List<String> selectedVehicleInfoList = new ArrayList<String>();
	private List<String> selectedPowertrainInfoList = new ArrayList<String>();
	private List<String> selectedLogisticsList = new ArrayList<String>();
	
	boolean verifyDialogDoneButtonEnabled = false;
	boolean equipmentVerified = false;
	
	@PostConstruct
	public void init() {
		openPage();
		Long qmdId = null;
		Long mainPoDocId = null;
		Long psoId = null;
		if (thisPage.getInputValues().get("qmdId") != null){
			qmdId =  (Long)thisPage.getInputValues().get("qmdId");
			leaseType = quotationService.getLeaseType(qmdId);
		}
		if (thisPage.getInputValues().get("docId") != null){
			mainPoDocId =  (Long)thisPage.getInputValues().get("docId");
		}
		
		if (thisPage.getInputValues().get("psoId") != null){
			psoId =  (Long)thisPage.getInputValues().get("psoId");
		}
		if(mainPoDocId != null){
			try {			
				maintainPoVO = loadData(qmdId, mainPoDocId);
				maintainPoVO.setPsoId(psoId);
			} catch (Exception ex) {
				addErrorMessage("custom.message", "Page Load Error...");
				logger.error(ex);
			}
		}
		
		setPostConfirmCompleted(false);
		super.setDirtyData(false);
		adjustVerifyEquipmentStatus();
	}
	
	private VehiclePurchaseOrderVO loadData(Long qmdId, Long mainPoDocId) {
		
		maintainPoVO = purchaseOrderService.getMainPurchaseOrderDetails(qmdId, mainPoDocId);
		maintainPoVO.setContextId(getLoggedInUser().getCorporateEntity().getCorpId());
		maintainPoVO.setOldVin(maintainPoVO.getVin());
		maintainPoVO.setOldOrderingDealerCode(maintainPoVO.getOrderingDealerCode());
		maintainPoVO.setOldDeliveringDealerCode(maintainPoVO.getDeliveringDealerCode());
		
		acquisitionTypeList.add(AcquisitionTypeEnum.BAIL.getDescription());
		acquisitionTypeList.add(AcquisitionTypeEnum.FIN.getDescription());
		acquisitionTypeList.add(AcquisitionTypeEnum.POOL.getDescription());
		
		if(maintainPoVO.getOrderType().equalsIgnoreCase(VehicleOrderType.LOCATE.getCode())
				|| DocumentStatus.PURCHASE_ORDER_STATUS_OPEN.getCode().equals(maintainPoVO.getPoStatus())){
			equipmentVerified = true;
		}else{
			if(DocumentStatus.PURCHASE_ORDER_STATUS_RELEASED.getCode().equals(maintainPoVO.getPoStatus())){
				
				vehicleInfoList.add("Model Description: " + maintainPoVO.getVehicleDescription());
				vehicleInfoList.add("Body Color: " + maintainPoVO.getColor());
				vehicleInfoList.add("Interior Color: " + maintainPoVO.getTrim());
				vehicleInfoList.add("Requested Date: " + maintainPoVO.getRequestedDate());
			}
		}
		if(maintainPoVO.isDriverAddressUpdated()){
			addSuccessMessage("custom.message", "Delivery Address was updated to driver's current garaged address");
		}
		if(!MALUtilities.isEmpty(maintainPoVO.getPoLogisticNotes())){
			logisticsList.add(maintainPoVO.getPoLogisticNotes());
		}
		return maintainPoVO;
	}
	
	public void checkVehicleAndEquipmentVerify(AjaxBehaviorEvent event){
		if(maintainPoVO.getOptionalAccessories().size()      				== selectedOptionalAccessories.size()
				&& maintainPoVO.getDealerInstalledAccessories().size()      == selectedDealerInstalledAccessories.size()
				&& maintainPoVO.getPortInstalledAccessories().size()		== selectedPortInstalledAccessories.size()
				&& selectedPowertrainInfoList.size() == maintainPoVO.getPowertrainInfo().size()
				&& selectedVehicleInfoList.size()    == vehicleInfoList.size()
				&& logisticsList.size() 			 == selectedLogisticsList.size()){
			verifyDialogDoneButtonEnabled = true;
		}else{
			verifyDialogDoneButtonEnabled = false;
		}
	}
	
	public void verifyEquipmentAction(){
		equipmentVerified = true;
		clearSelectedLists();		
	}
	
	private void adjustVerifyEquipmentStatus(){
		acquisitionTypeListner();	
	}
	
	
	public void acquisitionTypeListner(){
		if(!MALUtilities.isEmpty(maintainPoVO.getAcquisitionType())
				&& maintainPoVO.getOrderType().equalsIgnoreCase(VehicleOrderType.FACTORY.getCode())
				&& maintainPoVO.getAcquisitionType().equalsIgnoreCase("Bailment") 
				&& maintainPoVO.getPoStatus().equalsIgnoreCase(DocumentStatus.PURCHASE_ORDER_STATUS_RELEASED.getCode())){
			equipmentVerified = true;
		}else{
			if(maintainPoVO.getOrderType().equalsIgnoreCase(VehicleOrderType.LOCATE.getCode())
					|| DocumentStatus.PURCHASE_ORDER_STATUS_OPEN.getCode().equals(maintainPoVO.getPoStatus()))
					equipmentVerified = true;
				else
					equipmentVerified = false;
		}
	}
	
	public void clearSelectedLists(){
		selectedOptionalAccessories.clear();
		selectedDealerInstalledAccessories.clear();
		selectedPortInstalledAccessories.clear();
		selectedVehicleInfoList.clear();
		selectedPowertrainInfoList.clear();
		selectedLogisticsList.clear();
	}

	protected void loadNewPage() {
		thisPage.setPageDisplayName(ViewConstants.DISPLAY_NAME_MAINTAIN_PURCHASE_ORDER);
		thisPage.setPageUrl(ViewConstants.MAINTAIN_PURCHASE_ORDER);
	}

	protected void restoreOldPage() {

	}
	
	public void  decodeOrderingDealerByNameOrCode(){		
		isOrderingDlrValid = false;
		maintainPoVO.setOrderingDealerName("");
		maintainPoVO.setOrderingDealerEmail("");
		if(MALUtilities.isNotEmptyString(maintainPoVO.getOrderingDealerCode()) ){
			List<VendorLovVO> result = vendorService.getOrderingOrDeliveringVendors(maintainPoVO.getOrderingDealerCode(), SupplierLovBean.ORDERING_WORKSHOP, new PageRequest(0,10));	
			if(result == null || result.size() == 0){					 
				isOrderingDlrValid = false;
				super.addErrorMessage(ORDERING_INPUT_UI_ID,"decode.noMatchFound.msg", "Ordering Dealer " + maintainPoVO.getOrderingDealerCode()); 
			}else if(result.size() == 1){
				VendorLovVO  vendorVO = result.get(0);
				maintainPoVO.setOrderingDealerName(vendorVO.getVendorName());
				maintainPoVO.setOrderingDealerCode(vendorVO.getVendorCode());
				maintainPoVO.setOrderingDealerId(vendorVO.getRowId());
				ContactInfo contactInfo = null;
				contactInfo = unitProgressService.getVendorSupplierContactInfo(vendorVO.getEaAccountCode());
				if(!MALUtilities.isEmpty(contactInfo) ) {
					maintainPoVO.setOrderingDealerEmail(contactInfo.getEmail());
				}
				
				isOrderingDlrValid = true;
			}else if(result.size() > 1){
				isOrderingDlrValid = false;
				super.addErrorMessage(ORDERING_INPUT_UI_ID,"decode.notExactMatch.msg", "Ordering Dealer " +  maintainPoVO.getOrderingDealerCode());
			}
		}
	}
	
	public void  decodeDeliveringDealerDealerByNameOrCode(){
		isDeliveringDlrValid = false;
		maintainPoVO.setDeliveringDealerName("");
		maintainPoVO.setDeliveringDealerEmail("");
		if(MALUtilities.isNotEmptyString(maintainPoVO.getDeliveringDealerCode()) ){
			List<VendorLovVO> result = vendorService.getOrderingOrDeliveringVendors(maintainPoVO.getDeliveringDealerCode(), SupplierLovBean.DELIVERING_WORKSHOP, new PageRequest(0,10));
			if(result == null || result.size() == 0){					 
				isDeliveringDlrValid = false;
				super.addErrorMessage(DELIVERING_INPUT_UI_ID,"decode.noMatchFound.msg", "Delivering Dealer " + maintainPoVO.getDeliveringDealerCode()); 
			}else if(result.size() == 1){
				VendorLovVO  vendorVO = result.get(0);
				maintainPoVO.setDeliveringDealerName(vendorVO.getVendorName());
				maintainPoVO.setDeliveringDealerCode(vendorVO.getVendorCode());
				maintainPoVO.setDeliveringDealerId(vendorVO.getRowId());
				
				ContactInfo contactInfo = null;
				if( !MALUtilities.isEmpty(maintainPoVO.getOrderType()) && VehicleOrderType.LOCATE.getCode().equals(maintainPoVO.getOrderType())){
					contactInfo = unitProgressService.getDealerContactInfo(vendorVO.getEaAccountCode());
				}
				
				if(MALUtilities.isEmpty(contactInfo) ) {
					contactInfo = unitProgressService.getVendorSupplierContactInfo(vendorVO.getEaAccountCode());
				}
				if(!MALUtilities.isEmpty(contactInfo) ) {
					maintainPoVO.setDeliveringDealerEmail(contactInfo.getEmail());
				}
				isDeliveringDlrValid = true;
			}else if(result.size() > 1){
				isDeliveringDlrValid = false;
				super.addErrorMessage(DELIVERING_INPUT_UI_ID,"decode.notExactMatch.msg", "Delivering Dealer " +  maintainPoVO.getDeliveringDealerCode());			
			}
		}
	}	
	
	public void demoPostConfirmListener() {
	    RequestContext.getCurrentInstance().execute("showPostConfirmDocuments()");	
	}
	
	public String closeDemoPostConfirm(){
		return super.cancelPage();
	}
	
	public String emailOrderConfirmationAction(){
		return null;
	}
	
	public void emailOrderConfirmationListener(){
		getDocumentListItems().get(0).setEmailable(false);			
		getDocumentListItems().get(0).setEmailed(true);		
		super.addInfoMessage("custom.message", "Client Order Confirmation has been emailed");
	}
	
	/**
	 * 
	 * @param selectedDocmentLink
	 */
	public void viewDocumentListener(DocumentListItemVO selectedDocmentLink){		
	    setSelectedDocumentItem(selectedDocmentLink);	
		RequestContext.getCurrentInstance().execute("showPDF()");
	}
	
	public void loadLocatorListener(){
		
		Quotation quotation;
		UriComponents urlBuilder;		
		Long draId = null;
		Long makId = null;
		Long radius = null;
		
		try {
			makId = maintainPoVO.getMakId();
			radius = 50L;
			
			if(!MALUtilities.convertYNToBoolean(maintainPoVO.getStockYN())) {
				quotation = quotationService.getQuote(maintainPoVO.getQuoId());				
				if(!MALUtilities.isEmpty(quotation.getDrvDrvId())){
					draId = driverService.getDriverAddress(quotation.getDrvDrvId(), DriverService.GARAGED_ADDRESS_TYPE).getDraId();
				}
			}
		} catch(Exception e) {
			logger.error(e);
		}
				
		urlBuilder = UriComponentsBuilder.fromHttpUrl(baseURL + "/{draId}/{makId}/{radius}").buildAndExpand(draId, makId, radius);
		RequestContext.getCurrentInstance().execute("loadLocatorApp('" + urlBuilder.toString() + "')");		
	}
		
	private boolean validateInput(){
		boolean isValid = true;
		
		if(MALUtilities.isNotEmptyString(maintainPoVO.getDeliveringDealerCode()) && 
								MALUtilities.isEmpty(maintainPoVO.getDeliveringDealerName())){
			decodeDeliveringDealerDealerByNameOrCode();
			if(!isDeliveringDlrValid)
				isValid = false;
		}
		if(! ("R".equals(maintainPoVO.getPoStatus()))){
			if(MALUtilities.isEmpty(maintainPoVO.getOrderingDealerCode())){
				isValid = false;
				super.addErrorMessage(ORDERING_INPUT_UI_ID,"required.field", "Ordering Dealer ");
				
			}else if(MALUtilities.isEmpty(maintainPoVO.getOrderingDealerName())){
				decodeOrderingDealerByNameOrCode();
				if(!isOrderingDlrValid)
					isValid = false;
			}
		}
		if (!MALUtilities.isEmpty(maintainPoVO.getTotalCost())) {
			BigDecimal cost = new BigDecimal(maintainPoVO.getTotalCost());
			if(cost.compareTo(BigDecimal.ZERO) == 0){
				isValid = false;
				super.addErrorMessage(TOTAL_COST_INPUT_UI_ID,"err.value.zero.message", "Total Cost");
			}
		}
		if(!MALUtilities.isEmpty(maintainPoVO.getAcquisitionType()) && maintainPoVO.getAcquisitionType().equals(AcquisitionTypeEnum.BAIL.getDescription())){
			maintainPoVO.setContextId(getLoggedInUser().getCorporateEntity().getCorpId());
			String bailmentDealerCode = purchaseOrderService.getBailmentDealerCode(maintainPoVO);
			if(MALUtilities.isEmpty(bailmentDealerCode)){
				isValid = false;
				super.addErrorMessage(ACQUISITION_TYPE_UI_ID,"custom.message", "There is no Bailment Code associated with the Ordering Dealer and Make combination to display on the purchase order.  "
																				+ "Please contact the Help Desk to get it set up."
						 );
			}
		}
		
		return isValid;
	}
	
	private boolean validateVin()   {
		
		Map<String, Object> resultMap = null;
		boolean isValid = true;		
		showWarning = false;
		if(! userConfirmation){
			fleetMasterVinDetails = null;
			 resultMap = vinDecoderService.validateVINAndReturnMessage(maintainPoVO.getVin(), maintainPoVO.getFmsId());			
		
			if (resultMap != null && !resultMap.isEmpty()) {
				boolean isFail = MALUtilities.isEmpty(resultMap.get(VinDecoderService.ERROR_TYPE_BLOCKER)) ? false : (Boolean) resultMap.get(VinDecoderService.ERROR_TYPE_BLOCKER);
				boolean isWarning = MALUtilities.isEmpty(resultMap.get(VinDecoderService.ERROR_TYPE_WARNING)) ? false : (Boolean) resultMap.get(VinDecoderService.ERROR_TYPE_WARNING);
				String errorMessage = MALUtilities.isEmpty(resultMap.get(VinDecoderService.MESSAGE)) ? null : (String) resultMap.get(VinDecoderService.MESSAGE);
				if (isFail) {
					userConfirmation = false;
					addErrorMessage(VIN_UI_ID,"plain.message",new String[] { errorMessage });
					isValid = false;
					showWarning = false;
				}
				if (isWarning) {
					warningMessage = errorMessage;
					showWarning = true;
					isValid = false;
					userConfirmation =false;
					
				}
			}
		}
		
		
		if(  ( isValid || userConfirmation) && ! chromeValidationDone && 
							( USER_ACTION_RELEASE.equals(userAction) ||  "R".equals(maintainPoVO.getPoStatus()) ) ){
			
				fleetMasterVinDetails = vinDecoderService.validateVINByChrome(maintainPoVO.getVin(), null);	
				chromeValidationDone = true;
				if(fleetMasterVinDetails != null && !MALUtilities.isEmpty(fleetMasterVinDetails.getYear())
												 && !MALUtilities.isEmpty(fleetMasterVinDetails.getMakeDesc()) ){
					
						Integer modelYear = Integer.valueOf(fleetMasterVinDetails.getYear());
						String makeName = fleetMasterVinDetails.getMakeDesc();
						String chromeVinDesc= modelYear +"/"+makeName;
					
						if(modelYear.intValue() != maintainPoVO.getModelYear() || (! makeName.equalsIgnoreCase(maintainPoVO.getMakeDesc()))  ){//Error						
							addErrorMessage(VIN_UI_ID,"vin.ws.validation.mismatch.error",chromeVinDesc);
							showWarning = false;
							isValid = false;
						}
				}else{//warning --No info for vin or wrong vin					
					warningMessage = talMessage.getMessage("vin.ws.validation.error");
					showWarning = true;
					isValid = false;	
				}
			}
		
		
		return isValid;
	}
	
	private boolean  validateAll()   {
		
		boolean isValid = validateInput();
		if(isValid){
			isValid = validatePoStatus();
		}
		if(isValid && MALUtilities.isNotEmptyString(maintainPoVO.getVin())){
			if(USER_ACTION_RELEASE.equals(userAction) || ! maintainPoVO.getVin().equalsIgnoreCase(MALUtilities.getNullSafeString(maintainPoVO.getOldVin()))){
				isValid =  validateVin();				
			}
		}
		
		return isValid;
	}
	
	private boolean validatePoStatus(){
		boolean isValid = true;
		Doc doc = docDAO.findById(maintainPoVO.getDocId()).orElse(null);
		if(!maintainPoVO.getPoStatus().equals(doc.getDocStatus())){
			isValid = false;
			super.addErrorMessage("custom.message", "The entry has been updated. Please refresh the display to reflect the update.");
		}
		return isValid;
	}
	
	private void resetFlag(){
		userConfirmation = false;
		showWarning = false;
		chromeValidationDone = false;
		userAction = "";
		showConfirmDocuments = false;
	}
	
	public String cancelOnConfirmAction() {		
		resetFlag();
		return null;
	}
	
	public String saveOnConfirmAction() {
		String nextPage = null;		
		
		userConfirmation = true;
		showWarning = false;
		
		if(USER_ACTION_RELEASE.equals(userAction)){			
			if (releasePO()) {
				if(isShowPOReport()){
					showReleaseDocuments();
				}else{
					addSuccessMessage("custom.message", "Purchase order for Unit No " + maintainPoVO.getUnitNumber() + " released  successfully");
					nextPage = super.cancelPage();	
				}
			}
		}else if(USER_ACTION_CONFIRM.equals(userAction)){			
				if(confirmPO()){
					prepareConfirmationDocuments();
				}
		}else{			
			if (save()) {
				addSuccessMessage("custom.message", "Purchase order for Unit No " + maintainPoVO.getUnitNumber() + " saved successfully");			
				nextPage = super.cancelPage();	
			}
		}	
		
		return nextPage;
	}

	public String saveAction() {
		
		String nextPage = null;		
		resetFlag();
		if (save()) {		
			addSuccessMessage("custom.message", "Purchase order for Unit No " + maintainPoVO.getUnitNumber() + " saved successfully");
			nextPage = super.cancelPage();	
		}

		return nextPage;
	}
	
	private boolean save() {
		
		boolean isSaved  = false;
 		if (validateAll()) {			
			DbProcParamsVO parameterVO = null;
			try {
			
				parameterVO = purchaseOrderService.saveOrUpdatePO(maintainPoVO);
				
				if(!MALUtilities.isEmpty(parameterVO) && MALUtilities.isEmptyString(parameterVO.getMessage())){
					if(maintainPoVO.isVinChanged()){
						saveFleetMasterVinDetails();	
					}
					maintainPoVO.setOldOrderingDealerCode(maintainPoVO.getOrderingDealerCode());
					maintainPoVO.setOldDeliveringDealerCode(maintainPoVO.getDeliveringDealerCode());					
					maintainPoVO.setOldVin(maintainPoVO.getVin());
					isSaved = true;			
				}else{
					if(!MALUtilities.isEmpty(parameterVO) && !MALUtilities.isEmptyString(parameterVO.getMessage())){
						throw new Exception(parameterVO.getMessage());
					}
				}
				
			} catch (Exception e) {
				isSaved = false;	
				if(MALUtilities.isNotEmptyString(parameterVO.getMessage())){
					addErrorMessage("custom.message", parameterVO.getMessage());	
				}else{
					logger.error(e ,"Save PO...");
					addErrorMessage("generic.error.occured.while", "saving Purchase Order");
				}
			}
		}
	
		return isSaved;
		
	}

	public String  releasePOAction() {
		String nextPage = null;		
		
		if( VehicleOrderType.FACTORY.getCode().equals(maintainPoVO.getOrderType()) 
				&& MALUtilities.isEmpty(maintainPoVO.getAcquisitionType()) 
				&& MALUtilities.isEmpty(maintainPoVO.getOrderingLeadTime())){//In this case value should be get populated from standard_delivery_lead_time field of models table		
			
			super.addErrorMessage(ORDERING_LEAD_TIME_INPUT_UI_ID,"custom.message", "Standard lead time is missing for the selected model.  Please request lead time to be updated in order to release the purchase order.");
			return null;
		}
		
		if(	 MALUtilities.isEmpty(maintainPoVO.getOrderingLeadTime())){
			super.addErrorMessage(ORDERING_LEAD_TIME_INPUT_UI_ID,"required.field", "Ordering Lead Time ");
			return null;
		}
		List<Doc> mainPOs = purchaseOrderService.getMultipleMainPO(maintainPoVO.getQmdId()); 
		if(mainPOs.size() == 1){
			resetFlag();
			userAction = USER_ACTION_RELEASE;		
			
			boolean isSuccess =  releasePO();
			if(isSuccess){
				if(isShowPOReport()){
					showReleaseDocuments();
				}else{
					addSuccessMessage("custom.message", "Purchase order for Unit No " + maintainPoVO.getUnitNumber() + " released  successfully");
					nextPage = super.cancelPage();	
				}
			}
			
			return nextPage;	
		}else{
			String multipleDocNum="";
			for(Doc docForMainPO : mainPOs){
				if(docForMainPO.getDocStatus().equals("R")){
					super.addErrorMessage("custom.message","Main PO ("+docForMainPO.getDocNo()+") is already released for this unit. Please contact helpdesk for further assistance.");
					return null;
				}	
				if(MALUtilities.isEmptyString(multipleDocNum))
					multipleDocNum = docForMainPO.getDocNo(); 
				else
					multipleDocNum = multipleDocNum+", "+docForMainPO.getDocNo();
			}
			super.addErrorMessage("custom.message","Multiple Open Main POs ("+multipleDocNum+") found for this unit. Please contact helpdesk for further assistance.");
			return null;
		}
		
	}
	
	public boolean  releasePO() {			
		
		boolean isSuccess =  save();
		String fakeNull = "NO EMAIL AVAILABLE";		
		
		if( isSuccess ){		
			DbProcParamsVO parameterVO = null;
			
			try {
				String  employeeNo = getLoggedInUser().getEmployeeNo();
				Long  corpId = getLoggedInUser().getCorporateEntity().getCorpId();
				 parameterVO =  purchaseOrderService.releaseMainPurchaseOrder(maintainPoVO.getDocId(), corpId, employeeNo);//new DbProcParamsVO();//
				 saveFleetMasterVinDetails();	
				if(MALUtilities.isNotEmptyString(parameterVO.getMessage())){
					throw new Exception(parameterVO.getMessage());				
				}
				try {
					if(MALUtilities.isEmpty(maintainPoVO.getOrderingDealerEmail()) || maintainPoVO.getOrderingDealerEmail().contains(fakeNull) || maintainPoVO.getOrderType().equals(VehicleOrderType.LOCATE.getCode())) {
						setShowPOReport(true);
					} else {
						purchasingEmailService.requestEmail(maintainPoVO.getDocId(), EmailRequestEventEnum.MAIN_PO, true, super.getLoggedInUser().getEmployeeNo());
						if(leaseType.equalsIgnoreCase("PUR")){
							purchasingEmailService.requestEmail(maintainPoVO.getDocId(), EmailRequestEventEnum.COURTESY_DELIVERY_INSTRUCTION_DOC, true, super.getLoggedInUser().getEmployeeNo());
							addSuccessMessage("main.po.and.cd.doc.email.successful", "");
						}else{
							addSuccessMessage("main.po.email.successful", "");
						}
												
					}
				} catch (Exception e) {	
					setShowPOReport(true);			
					logger.error(e ,"Staging Email for purchase order document...");
					addErrorMessage("generic.error.occured.while", "Emailing purchase order document");					
				}
				
				maintainPoVO.setPoStatus("R");
				isSuccess = true;
								
				try {					
					purchaseOrderService.archiveMainPurchaseOrderDoc(maintainPoVO.getDocId(), maintainPoVO.getStockYN());
					if(leaseType.equalsIgnoreCase("PUR")){
						purchaseOrderService.archiveCourtesyDeliveryInstructionDoc(maintainPoVO.getDocId());
					}
				} catch (Exception e) {
					logger.error(e ,"Archiving purchase order document in Onbase...");
					addErrorMessage("generic.error.occured.while", "Archiving purchase order document in Onbase");
				}				
				
				
			} catch (Exception e) {
				isSuccess = false;
				if(MALUtilities.isNotEmptyString(parameterVO.getMessage())){
					addErrorMessage("custom.message", parameterVO.getMessage());
					
				}else{
					logger.error(e ,"Release PO...");
					addErrorMessage("generic.error.occured.while", "releasing purchase order");
				
				}
			}
					
		}
		
		return isSuccess;			
	}
	
	//Get called for document view dialog done button event 
	public String navigateToPreviousPagePostConfirm(){
		setPostConfirmCompleted(false);
		if(userAction.equals(USER_ACTION_CONFIRM)){
			addSuccessMessage("custom.message", "Purchase order for Unit No " + maintainPoVO.getUnitNumber() + " confirmed  successfully");		
		}else{
			addSuccessMessage("custom.message", "Purchase order for Unit No " + maintainPoVO.getUnitNumber() + " released  successfully");
		}
		return super.cancelPage();
	}
	
	public void confirmPOAction() {				
		boolean isValid = true;
		if(MALUtilities.isEmpty(maintainPoVO.getOrderingLeadTime())){			
			super.addErrorMessage(ORDERING_LEAD_TIME_INPUT_UI_ID,"required.field", "Ordering Lead Time ");
			isValid = false;
		}
		
		if(MALUtilities.isEmpty(maintainPoVO.getDeliveringDealerCode())){			
			super.addErrorMessage(DELIVERING_INPUT_UI_ID,"required.field", "Delivering Dealer ");
			isValid = false;
		}
		Date sysdate  = new Date();
		if(MALUtilities.isEmpty(confirmDate) || confirmDate.after(sysdate)){
			super.addErrorMessage(CALENDER_UI_ID,"custom.message", "Confirmation Date is required and must be today or in the past");	 // added for HD-246
			isValid = false;
		}
		if(! isValid){
			return;
		}		
		resetFlag();
		setShowConfirmDocuments(false);
		userAction = USER_ACTION_CONFIRM;		
		boolean isSuccess =  confirmPO();		
		if(isSuccess){
			prepareConfirmationDocuments();
		}
	}
	
//	public void confirmPODiagAction() {				
//		Date sysdate  = new Date();
//		boolean isValid = true; 					
//		if(MALUtilities.isEmpty(confirmDate) || confirmDate.after(sysdate)){
//			super.addErrorMessage(CALENDER_UI_ID,"custom.message", "Confirmation Date is required and must be today or in the past");	 // added for HD-246
//			isValid = false;
//		}
//		if(! isValid){
//			return;
//		}		
//		resetFlag();
//		setShowConfirmDocuments(false);
//		userAction = USER_ACTION_CONFIRM;		
//		boolean isSuccess =  confirmPO();		
//		if(isSuccess){
//			prepareConfirmationDocuments();
//		}
//	}
	
	private void showReleaseDocuments(){
	
		setDocumentListItems(new ArrayList<DocumentListItemVO>());		
		getDocumentListItems().add(new MainPurchaseOrderListItemVO(getMaintainPoVO().getDocId(), ReportNameEnum.MAIN_PURHCASE_ORDER, getMaintainPoVO().getStockYN()));
		if(leaseType.equalsIgnoreCase("PUR")){
			getDocumentListItems().add(new DocumentListItemVO(getMaintainPoVO().getDocId(), ReportNameEnum.COURTESY_DELIVERY_INSTRUCTION));
		}		
		setShowConfirmDocuments(true);
		setPostConfirmCompleted(false);
	}	
	
	private void prepareConfirmationDocuments() {
	    JasperPrint orderConformationReport;
		boolean emailedInThisEvent = false;	    
	    boolean emailable = false;	
	    boolean emailedOrViewedInPast = false;
	    boolean isStock = false;
	    
		setDocumentListItems(new ArrayList<DocumentListItemVO>());		
		isStock = MALUtilities.convertYNToBoolean(getMaintainPoVO().getStockYN());		
		emailable = purchasingEmailActive.toLowerCase().equals("true") ? true : false; //TODO Move down into core purchasing email service
		
	    try {
			//Stock Orders do not have a client. Could there be a scenario where this is a stock quote?
			if(!isStock && emailable) {    	
				emailable = purchasingEmailService.isClientConfirmationEmailable(maintainPoVO.getDocId());
				if(emailable){
					emailedOrViewedInPast = purchaseOrderReportService.hasClientConfirmationBeenGenerated(maintainPoVO.getDocId());
					if(!emailedOrViewedInPast){
						orderConformationReport = jasperReportService.getClientOrderConfirmationReport(maintainPoVO.getDocId());
						purchasingEmailService.emailClientConfirmation(orderConformationReport, maintainPoVO.getDocId(), super.getLoggedInUser().getEmployeeNo());
						emailedInThisEvent = true;
						setDocumentListMessage(malMessage.getMessage(ORDER_CONFIRMATION_EMAIL_SUCCESS_MESSAGE));						
					}
				}
			}
	    } catch (Exception e) {
	    	logger.error(e);
	    	emailable = false;
	    	emailedInThisEvent = false;
	    }

	    //Display Order Confirmation document when it has not been emailed (required viewing).
	    //Or, display when it has been emailed outside of this event (optional viewing).
	    //Otherwise, it has been emailed as part of this event and does not need to be displayed.
		if(!isStock) {
			if (!emailable) {
				getDocumentListItems().add(new ClientOrderConfirmationListItemVO(getMaintainPoVO().getDocId(), ReportNameEnum.CLIENT_ORDER_CONFIRMATION, getMaintainPoVO().getStockYN()));
				getDocumentListItems().get(getDocumentListItems().size() - 1).setEmailable(emailable);
				getDocumentListItems().get(getDocumentListItems().size() - 1).setEmailed(emailedInThisEvent);				
			} else {
				if(!emailedInThisEvent && emailedOrViewedInPast){
					getDocumentListItems().add(new ClientOrderConfirmationListItemVO(getMaintainPoVO().getDocId(), ReportNameEnum.CLIENT_ORDER_CONFIRMATION, getMaintainPoVO().getStockYN()));
					getDocumentListItems().get(getDocumentListItems().size() - 1).setEmailable(emailable);
					getDocumentListItems().get(getDocumentListItems().size() - 1).setEmailed(emailedInThisEvent);								
					getDocumentListItems().get(getDocumentListItems().size() - 1).setRequired(false);;					
				}
			}			
		}			
				
		if(!isStock && !MALUtilities.isEmpty(purchaseOrderReportService.getPurchaseOrderCoverSheetReportVO(getMaintainPoVO().getDocId()).getFullAddress())) {
			getDocumentListItems().add(new OrderSummaryListItemVO(getMaintainPoVO().getDocId(), ReportNameEnum.VEHICLE_PURCHASE_ORDER_SUMMARY , getMaintainPoVO().getStockYN()));			
			getDocumentListItems().add(new DocumentListItemVO(getMaintainPoVO().getDocId(), ReportNameEnum.PRINT_COVER_SHEET));
			getDocumentListItems().get(getDocumentListItems().size() - 1).setRequired(false);		
		}
		
		// Do not display the dialog if there are no documents to list.
		if(getDocumentListItems().isEmpty()) {
			setShowConfirmDocuments(false);
			setPostConfirmCompleted(true);
		} else {
			setShowConfirmDocuments(true);
			setPostConfirmCompleted(false);
		}
		

						
	}	
	
	public boolean confirmPO() {	
	
		boolean isSuccess =  save();
		if(isSuccess){
			DbProcParamsVO parameterVO = null;
			
			try {
				
				parameterVO = purchaseOrderService.confirmPurchaseOrder(maintainPoVO.getDocId(), getLoggedInUser().getEmployeeNo(), this.getConfirmDate());// added confirmDate for HD-246																    ; 
				
				if(MALUtilities.isNotEmptyString(parameterVO.getMessage())){
					throw new Exception(parameterVO.getMessage());				
				}
				
				processStageService.excludeStagedObject(maintainPoVO.getPsoId());//excluse this object from
				
				if(! MALUtilities.convertYNToBoolean(maintainPoVO.getStockYN()) ){
					try {
						 purchaseOrderService.archiveVehicleOrderSummary(maintainPoVO.getDocId());
					} catch (Exception e) {
						logger.error(e ,"Archiving Vehicle Order Summary Doc in Onbase...");
						addErrorMessage("generic.error.occured.while", "Archiving Vehicle Order Summary Doc in Onbase");
					}
				}
				
				if(! MALUtilities.convertYNToBoolean(maintainPoVO.getStockYN()) ){
					try {
						 purchaseOrderService.archiveClientOrderConfirmation(maintainPoVO.getDocId());						
					} catch (Exception e) {
						logger.error(e ,"Archiving Client Order Confirmation Doc in Onbase...");
						addErrorMessage("generic.error.occured.while", "Archiving Client Order Confirmation Doc in Onbase");
					}
				}
				
			} catch (Exception e) {
				isSuccess = false;
				if(MALUtilities.isNotEmptyString(parameterVO.getMessage())){
					addErrorMessage("custom.message", parameterVO.getMessage());
				
				}else{
					logger.error(e ,"Confirm PO...");
					addErrorMessage("generic.error.occured.while", "confirming purchase order");
				}
			}
		
		}
		
		return isSuccess;
	}
		
	private void saveFleetMasterVinDetails(){
		
		try {
			 if(fleetMasterVinDetails != null){
					
				 FleetMaster fleetMaster = fleetMasterService.findByUnitNo(maintainPoVO.getUnitNumber());					
					
					if(fleetMaster != null){
						
						FleetMasterVinDetails fleetMasterVinDetailByFmsId = fleetMaster.getVehicleVinDetails();//inserted though FMS trigger
						fleetMasterVinDetailByFmsId.setFleetMaster(fleetMaster);
						fleetMasterVinDetailByFmsId.setVin(fleetMasterVinDetails.getVin());
						fleetMasterVinDetailByFmsId.setYear(fleetMasterVinDetails.getYear());
						fleetMasterVinDetailByFmsId.setMakeDesc(fleetMasterVinDetails.getMakeDesc());
						fleetMasterVinDetailByFmsId.setModelDesc(fleetMasterVinDetails.getModelDesc());
						fleetMasterVinDetailByFmsId.setModelTypeDesc(fleetMasterVinDetails.getModelTypeDesc());
						fleetMasterVinDetailByFmsId.setEngineDesc(fleetMasterVinDetails.getEngineDesc());
						fleetMasterVinDetailByFmsId.setFuelType(fleetMasterVinDetails.getFuelType());
						fleetMasterVinDetailByFmsId.setStyleDesc(fleetMasterVinDetails.getStyleDesc());
						fleetMasterVinDetailByFmsId.setTrimDesc(fleetMasterVinDetails.getTrimDesc());
						
						vinDecoderService.saveFleetMasterVinDetail(fleetMasterVinDetailByFmsId);
					}
				}
			} catch (Exception e) {
				logger.error(e);
				addErrorMessage("generic.error.occured.while", "saving decoded vin details");
			}
		
	}	
	public String cancel() {
		return super.cancelPage();
	}
	
	public VehiclePurchaseOrderVO getMaintainPoVO() {
		return maintainPoVO;
	}

	public void setMaintainPoVO(VehiclePurchaseOrderVO maintainPoVO) {
		this.maintainPoVO = maintainPoVO;
	}

	public List<String> getAcquisitionTypeList() {
		return acquisitionTypeList;
	}

	public void setAcquisitionTypeList(List<String> acquisitionTypeList) {
		this.acquisitionTypeList = acquisitionTypeList;
	}

	public boolean isUserConfirmation() {
		return userConfirmation;
	}

	public void setUserConfirmation(boolean userConfirmation) {
		this.userConfirmation = userConfirmation;
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

	public String getTotalCostFromUI() {
		return totalCostFromUI;
	}

	public void setTotalCostFromUI(String totalCostFromUI) {
		this.totalCostFromUI = totalCostFromUI;
	}

	public boolean isShowPOReport() {
		return showPOReport;
	}

	public void setShowPOReport(boolean showPOReport) {
		this.showPOReport = showPOReport;
	}

	public boolean isShowConfirmDocuments() {
		return showConfirmDocuments;
	}

	public void setShowConfirmDocuments(boolean showConfirmDocuments) {
		this.showConfirmDocuments = showConfirmDocuments;
	}

	public List<DocumentListItemVO> getDocumentListItems() {
		return documentListItems;
	}

	public void setDocumentListItems(List<DocumentListItemVO> documentListItems) {
		this.documentListItems = documentListItems;
	}

	public DocumentListItemVO getSelectedDocumentItem() {
		return selectedDocumentItem;
	}

	public void setSelectedDocumentItem(DocumentListItemVO selectedDocumentItem) {
		this.selectedDocumentItem = selectedDocumentItem;
	}

	public String getDocumentListMessage() {
		return documentListMessage;
	}

	public void setDocumentListMessage(String documentListMessage) {
		this.documentListMessage = documentListMessage;
	}

	public boolean isPostConfirmCompleted() {
		return postConfirmCompleted;
	}

	public void setPostConfirmCompleted(boolean postConfirmCompleted) {
		this.postConfirmCompleted = postConfirmCompleted;
	}

	public Date getConfirmDate() {
		return confirmDate;
	}

	public void setConfirmDate(Date confirmDate) {
		this.confirmDate = confirmDate;
	}

	public List<String> getVehicleInfoList() {
		return vehicleInfoList;
	}

	public void setVehicleInfoList(List<String> vehicleInfoList) {
		this.vehicleInfoList = vehicleInfoList;
	}

	public List<String> getSelectedVehicleInfoList() {
		return selectedVehicleInfoList;
	}

	public void setSelectedVehicleInfoList(List<String> selectedVehicleInfoList) {
		this.selectedVehicleInfoList = selectedVehicleInfoList;
	}

	public List<String> getSelectedPowertrainInfoList() {
		return selectedPowertrainInfoList;
	}

	public void setSelectedPowertrainInfoList(
			List<String> selectedPowertrainInfoList) {
		this.selectedPowertrainInfoList = selectedPowertrainInfoList;
	}

	public boolean isVerifyDialogDoneButtonEnabled() {
		return verifyDialogDoneButtonEnabled;
	}

	public void setVerifyDialogDoneButtonEnabled(
			boolean verifyDialogDoneButtonEnabled) {
		this.verifyDialogDoneButtonEnabled = verifyDialogDoneButtonEnabled;
	}

	public boolean isEquipmentVerified() {
		return equipmentVerified;
	}

	public void setEquipmentVerified(boolean equipmentVerified) {
		this.equipmentVerified = equipmentVerified;
	}

	public List<String> getSelectedOptionalAccessories() {
		return selectedOptionalAccessories;
	}

	public void setSelectedOptionalAccessories(
			List<String> selectedOptionalAccessories) {
		this.selectedOptionalAccessories = selectedOptionalAccessories;
	}

	public List<String> getSelectedPortInstalledAccessories() {
		return selectedPortInstalledAccessories;
	}

	public void setSelectedPortInstalledAccessories(
			List<String> selectedPortInstalledAccessories) {
		this.selectedPortInstalledAccessories = selectedPortInstalledAccessories;
	}

	public List<String> getSelectedDealerInstalledAccessories() {
		return selectedDealerInstalledAccessories;
	}

	public void setSelectedDealerInstalledAccessories(
			List<String> selectedDealerInstalledAccessories) {
		this.selectedDealerInstalledAccessories = selectedDealerInstalledAccessories;
	}

	public List<String> getLogisticsList() {
		return logisticsList;
	}

	public void setLogisticsList(List<String> logisticsList) {
		this.logisticsList = logisticsList;
	}

	public List<String> getSelectedLogisticsList() {
		return selectedLogisticsList;
	}

	public void setSelectedLogisticsList(List<String> selectedLogisticsList) {
		this.selectedLogisticsList = selectedLogisticsList;
	}

}
