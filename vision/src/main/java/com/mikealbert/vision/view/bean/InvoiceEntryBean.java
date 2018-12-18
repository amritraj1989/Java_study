package com.mikealbert.vision.view.bean;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.mikealbert.common.MalLogger;
import com.mikealbert.common.MalLoggerFactory;
import com.mikealbert.common.MalMessage;
import com.mikealbert.data.dao.DocDAO;
import com.mikealbert.data.dao.ExternalAccountDAO;
import com.mikealbert.data.dao.FleetMasterVinDetailsDAO;
import com.mikealbert.data.dao.QuotationModelDAO;
import com.mikealbert.data.entity.Doc;
import com.mikealbert.data.entity.FleetMaster;
import com.mikealbert.data.entity.FleetMasterVinDetails;
import com.mikealbert.data.entity.Make;
import com.mikealbert.data.entity.Model;
import com.mikealbert.data.entity.ModelMarkYear;
import com.mikealbert.data.entity.QuotationModel;
import com.mikealbert.data.entity.User;
import com.mikealbert.data.vo.VendorLovVO;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.service.FleetMasterService;
import com.mikealbert.service.VinDecoderService;
import com.mikealbert.util.MALUtilities;
import com.mikealbert.vision.service.InvoiceEntryService;
import com.mikealbert.vision.service.VendorService;
import com.mikealbert.vision.view.ViewConstants;
import com.mikealbert.vision.vo.InvoiceEntryVO;

@Component
@Scope("view")
public class InvoiceEntryBean extends StatefulBaseBean {
	private static final long serialVersionUID = 1L;
	private MalLogger logger = MalLoggerFactory.getLogger(this.getClass());
	private String targetPoDocNo;
	private InvoiceEntryVO invoiceEntryVO;
	private boolean invalidAccessPath;
	private String selectedKeyCode;
	private Long targetQmdId;
	private boolean userConfirmation = false;
	private boolean chromeValidationDone = false;
	private boolean showWarning = false;
	private String warningMessage;
	private Doc docReleasedPo;
	private Long invoiceHeaderId;
	@Resource
	private InvoiceEntryService invoiceEntryService;
	@Resource
	private VinDecoderService vinDecoderService;
	@Resource
	private DocDAO docDAO;
	@Resource
	private	ExternalAccountDAO	externalAccountDAO;
	@Resource
	private	QuotationModelDAO	quotationModelDAO;
	@Resource 
	protected MalMessage malMessage;
	@Resource
	private FleetMasterService fleetMasterService;
	@Resource
	private FleetMasterVinDetailsDAO fleetMasterVinDetailsDAO;
	@Resource 
	VendorService vendorService;
	
	private String INVOICE_AMT_UI_ID = "invoiceAmount";
	private String INVOICE_NUMBER_UI_ID = "invoiceNumber";
	private String RECEIVED_DATE_UI_ID = "receivedDate";
	private String INVOICE_DATE_UI_ID = "invoiceDate";
	private String DUE_DATE_UI_ID = "dueDate";
	private String VIN_UI_ID = "vin";
	private String MSRP_UI_ID = "msrp";
	private String VENDOR_INPUT_UI_ID  = "vendorInput";
	private String SHIP_WEIGHT_UI_ID = "shipWeight";
	private String KEY_CODE_UI_ID = "keyCode";
	private	InvoiceEntryVO oldInvoiceEntryVO;
	private boolean isVinModified = false;
	private String vendorCode;
	private String vendorName;

	@PostConstruct
	public void init() {
		try {
			openPage();
			if (!MALUtilities.isEmpty(targetPoDocNo)) {
				List<String> sourceCode = new ArrayList<String>();
				sourceCode.add("FLQUOTE");
				sourceCode.add("FLORDER");
				sourceCode.add("FLGRD");
				docReleasedPo = docDAO.findByDocNoAndDocTypeAndSourceCodeAndStatusForInvoiceEntry(targetPoDocNo,InvoiceEntryService.DOC_TYPE_PORDER, sourceCode, "R");
				invoiceEntryVO = invoiceEntryService.getInvoiceEntryHeader(targetPoDocNo);
				invoiceEntryVO.setPoNumber(targetPoDocNo);
				String employeeNo = ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getEmployeeNo();
				invoiceEntryVO.setOpCode(employeeNo);
				if (!MALUtilities.isEmpty(invoiceEntryVO.getKeyCode())) {
					selectedKeyCode = invoiceEntryVO.getKeyCode();
				}
				
			} else {
				String docNo = getRequestParameter("docNo");
				if (docNo != null) {
					targetPoDocNo = docNo;
					invoiceEntryVO = invoiceEntryService.getInvoiceEntryHeader(targetPoDocNo);
					invoiceEntryVO.setPoNumber(targetPoDocNo);
				}

			}
			if(invoiceEntryVO != null && invoiceEntryVO.getInvoiceDocId() != null){
				oldInvoiceEntryVO = new InvoiceEntryVO();
				BeanUtils.copyProperties(invoiceEntryVO, oldInvoiceEntryVO);
			}

		} catch (Exception ex) {
			handleException("generic.error", new String[] { "loading" }, ex, "init");
			logger.error(ex);
		}

	}
	
	private boolean ifVoIsUpdated() {
		if (invoiceEntryVO != null && oldInvoiceEntryVO != null) {
			if (!MALUtilities.getNullSafeString(invoiceEntryVO.getInvoiceNumber()).equals(
					MALUtilities.getNullSafeString(oldInvoiceEntryVO.getInvoiceNumber()))) {
				return true;
			}
			if (invoiceEntryVO.getInvoiceAmount().compareTo(oldInvoiceEntryVO.getInvoiceAmount()) != 0) {
				return true;
			}
			if (invoiceEntryVO.getReceivedDate().compareTo(oldInvoiceEntryVO.getReceivedDate()) != 0) {
				return true;
			}
			if (invoiceEntryVO.getInvoiceDate().compareTo(oldInvoiceEntryVO.getInvoiceDate()) != 0) {
				return true;
			}
			if (invoiceEntryVO.getDueDate().compareTo(oldInvoiceEntryVO.getDueDate()) != 0) {
				return true;
			}
			if (!MALUtilities.getNullSafeString(invoiceEntryVO.getVin()).equals(MALUtilities.getNullSafeString(oldInvoiceEntryVO.getVin()))) {
				isVinModified = true;
				chromeValidationDone = false;
				return true;
			}
			if (!invoiceEntryVO.getShipWeight().equals(oldInvoiceEntryVO.getShipWeight())) {
				return true;
			}
			if (!MALUtilities.getNullSafeString(invoiceEntryVO.getKeyCode()).equals(
					MALUtilities.getNullSafeString(oldInvoiceEntryVO.getKeyCode()))) {
				return true;
			}
			if (invoiceEntryVO.getMsrp().compareTo(oldInvoiceEntryVO.getMsrp()) != 0) {
				return true;
			}
			BigDecimal	 newVVW = invoiceEntryVO.getGrossVehicleWeight()!= null ? invoiceEntryVO.getGrossVehicleWeight():BigDecimal.ZERO;
			BigDecimal		OldVVW = oldInvoiceEntryVO.getGrossVehicleWeight()!= null ? oldInvoiceEntryVO.getGrossVehicleWeight():BigDecimal.ZERO;
			if (newVVW.compareTo(OldVVW) != 0) {
				return true;
			}
			if (!MALUtilities.getNullSafeString(invoiceEntryVO.getUpdatedVendorCode()).equals(
					MALUtilities.getNullSafeString(oldInvoiceEntryVO.getUpdatedVendorCode()))) {
				return true;
			}
		} else {
			isVinModified = true;
		}
		return false;
	}

	public void handleKeyCodeSelect() {
		if (MALUtilities.isEmpty(selectedKeyCode)) {
			invoiceEntryVO.setKeyCode(null);
		} else {
			invoiceEntryVO.setKeyCode(selectedKeyCode);
		}
	}

	public void updateDueDate() {
		// set due date
		Map<String, Object> values;
		try {
			if(!MALUtilities.isEmpty(invoiceEntryVO.getInvoiceDate())){
				values = invoiceEntryService.getDueDatePaymentMethodAndDiscDate(docReleasedPo.getCId().longValue(),
						docReleasedPo.getAccountCode(), docReleasedPo.getAccountType(), InvoiceEntryService.DOC_TYPE_INVOICEAP,
						docReleasedPo.getUpdateControlCode(), docReleasedPo.getPaymentTermsCode(), docReleasedPo.getCrtExtAccType(),
						invoiceEntryVO.getInvoiceDate(), docReleasedPo.getTpSeqNo(), "Y", docReleasedPo.getPaymentMethod());
				if (values != null && !values.isEmpty()) {
					Date dueDate = values.get(InvoiceEntryService.DOC_DUE_DATE) != null ? (Date) values.get(InvoiceEntryService.DOC_DUE_DATE)
							: null;
					invoiceEntryVO.setDueDate(dueDate);
					String paymentMethod = values.get(InvoiceEntryService.DOC_PAYMENT_METHOD) != null ? (String) values.get(InvoiceEntryService.DOC_PAYMENT_METHOD)
							: null;
					invoiceEntryVO.setPaymentMethod(paymentMethod);
					String paymentTermCode = values.get(InvoiceEntryService.DOC_PAYMENT_TERM_CODE) != null ? (String) values.get(InvoiceEntryService.DOC_PAYMENT_TERM_CODE)
							: null;
					invoiceEntryVO.setPaymentTermCode(paymentTermCode);
					
					
				}
			}else{
				invoiceEntryVO.setDueDate(null);
			}
			
		} catch (MalBusinessException e) {
			addErrorMessage("generic.error.occured.while", new String[] { "calculating Due date" });
		}

	}

	public String next() {
		try {
			showWarning = false;
			warningMessage = null;
			isVinModified = false;
			if (invoiceEntryVO == null) {
				return null;
			}
			
			if (MALUtilities.isEmpty(invoiceEntryVO.getInvoiceNumber())) {
				addErrorMessage(INVOICE_NUMBER_UI_ID, "required.field", new String[] { "Invoice Number" });
				return null;
			} else {
				if (invoiceEntryVO.getInvoiceNumber().length() > 25) {
					addErrorMessage(INVOICE_NUMBER_UI_ID,"max.length.error", new String[] { "Invoice Number", "25" });
					return null;
				}
			}
			if (MALUtilities.isEmpty(invoiceEntryVO.getInvoiceAmount())) {
				addErrorMessage(INVOICE_AMT_UI_ID,"required.field", new String[] { "Invoice Amount" });
				return null;
			} else {
				if (invoiceEntryVO.getInvoiceAmount().compareTo(BigDecimal.ZERO) == 0) {
					addErrorMessage(INVOICE_AMT_UI_ID ,"err.value.zero.message", new String[] { "Invoice Amount" });
					return null;
				}
			}

			if (MALUtilities.isEmpty(invoiceEntryVO.getReceivedDate())) {
				addErrorMessage(RECEIVED_DATE_UI_ID ,"required.field", new String[] { "Received Date" });
				return null;
			} else {
				if (invoiceEntryVO.getReceivedDate().compareTo(new Date()) > 0) {
					addErrorMessage(RECEIVED_DATE_UI_ID,"future.date.error", new String[] { "Received Date" });
					return null;
				}
			}

			if (MALUtilities.isEmpty(invoiceEntryVO.getInvoiceDate())) {
				addErrorMessage(INVOICE_DATE_UI_ID,"required.field", new String[] { "Invoice Date" });
				return null;
			} else {
				if (invoiceEntryVO.getInvoiceDate().compareTo(new Date()) > 0) {
					addErrorMessage(INVOICE_DATE_UI_ID,"future.date.error", new String[] { "Invoice Date" });
					return null;
				}
			}

			if (MALUtilities.isEmpty(invoiceEntryVO.getDueDate())) {
				addErrorMessage(DUE_DATE_UI_ID,"required.field", new String[] { "Due Date" });
				return null;
			}

			if (MALUtilities.isEmpty(invoiceEntryVO.getVin()) && !invoiceEntryVO.isThirdPartyPO()) {
				addErrorMessage(VIN_UI_ID,"required.field", new String[] { "VIN" });
				return null;
			}
			if (MALUtilities.isEmpty(invoiceEntryVO.getMsrp()) && !invoiceEntryVO.isThirdPartyPO()) {
				addErrorMessage(MSRP_UI_ID,"required.field", new String[] { "MSRP" });
				return null;
			}

			if (!MALUtilities.isEmpty(invoiceEntryVO.getMsrp()) && !invoiceEntryVO.isThirdPartyPO()) {
				if (invoiceEntryVO.getMsrp().compareTo(BigDecimal.ZERO) == 0) {
					addErrorMessage(MSRP_UI_ID,"err.value.zero.message", new String[] { "MSRP" });
					return null;
				}

			}
			
			if(!MALUtilities.isEmpty(invoiceEntryVO.getShipWeight())){
				try{
					new BigDecimal(invoiceEntryVO.getShipWeight());
				}catch(Exception ex){
					addErrorMessage(SHIP_WEIGHT_UI_ID,"invalid.field", new String[] { "Shipping Weight" });
					return null;
				}
				
				if(invoiceEntryVO.getShipWeight().length()> 10){
					addErrorMessage(SHIP_WEIGHT_UI_ID,"max.length.error", new String[] { "Shipping Weight" , "10" });
					return null;	
				}
			}
			if (!MALUtilities.isEmpty(invoiceEntryVO.getKeyCode())) {
				if (invoiceEntryVO.getKeyCode().length() > 10) {
					addErrorMessage(KEY_CODE_UI_ID,"max.length.error", new String[] { "Key Code", "10" });
					return null;
				}
			}
			if (invoiceEntryVO.isThirdPartyPO()) {
				
				if(MALUtilities.isNotEmptyString(getVendorName()) && MALUtilities.isEmpty(getVendorCode())){//vendor input has value but not decoded 
					List<VendorLovVO> result = vendorService.getVendors(getLoggedInUser().getCorporateEntity().getCorpId(), "S" , getVendorName(), new PageRequest(0,10));
					if(result != null && result.size() == 1){
							setVendorCode(result.get(0).getVendorCode());	
					}else{
						addErrorMessage(VENDOR_INPUT_UI_ID,"not.valid", "Change Vendor " + getVendorName());
						return null;
					}
				}
				
				userConfirmation = true;
				isVinModified = false;
				invoiceEntryVO.setUpdatedVendorCode(vendorCode);
			}
			// validate VIN
			if(!userConfirmation){
				ifVoIsUpdated();
			}
			Map<String, Object> resultMap = null;
			if (!userConfirmation && isVinModified ) {
				 resultMap = vinDecoderService.validateVINAndReturnMessage(invoiceEntryVO.getVin(),invoiceEntryVO.getFmsId());			
			}	
			
			FleetMasterVinDetails fleetMasterVinDetails = null;
			if(((resultMap != null &&  resultMap.size() == 0)  || userConfirmation) && chromeValidationDone == false){
				fleetMasterVinDetails = vinDecoderService.validateVINByChrome(invoiceEntryVO.getVin(),invoiceEntryVO.getFmsId());
				 chromeValidationDone = true;
			
				resultMap = new HashMap<String, Object>(); 
				if(fleetMasterVinDetails != null && !MALUtilities.isEmpty(fleetMasterVinDetails.getYear()) && !MALUtilities.isEmpty(fleetMasterVinDetails.getMakeDesc()) ){
					
					Integer modelYear = Integer.valueOf(fleetMasterVinDetails.getYear());
					String makeName = fleetMasterVinDetails.getMakeDesc();
					//String modelName = vinDecoderResponseVO.getBestModelName();
					String chromeVinDesc= modelYear +"/"+makeName;
					FleetMaster fleetMaster = fleetMasterService.getFleetMasterByFmsId(invoiceEntryVO.getFmsId());	
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
				boolean isFail = MALUtilities.isEmpty(resultMap.get(VinDecoderService.ERROR_TYPE_BLOCKER)) ? false
						: (Boolean) resultMap.get(VinDecoderService.ERROR_TYPE_BLOCKER);
				boolean isWarning = MALUtilities.isEmpty(resultMap.get(VinDecoderService.ERROR_TYPE_WARNING)) ? false
						: (Boolean) resultMap.get(VinDecoderService.ERROR_TYPE_WARNING);
				String errorMessage = MALUtilities.isEmpty(resultMap.get(VinDecoderService.MESSAGE)) ? null : (String) resultMap
						.get(VinDecoderService.MESSAGE);
				System.out.println("Error Message:" + errorMessage);
				if (isFail) {
					chromeValidationDone = false;
					userConfirmation = false;
					addErrorMessage(VIN_UI_ID,"plain.message",new String[] { errorMessage });
					return null;
				}
				if (isWarning) {
					warningMessage = errorMessage;
					showWarning = true;
					return null;
				}
			}
						
			if(ifVoIsUpdated() || invoiceEntryVO.getInvoiceDocId() == null){
				
				Map<String, Object> result = invoiceEntryService.saveInvoiceHeader(invoiceEntryVO,targetQmdId);
				
				if(result != null && !result.isEmpty()){
					Doc doc = (Doc)result.get(InvoiceEntryService.INVOICE_HEADER_DOC);
					invoiceHeaderId = doc.getDocId();
				}
				if(fleetMasterVinDetails != null){
					FleetMasterVinDetails fleetMasterVinDetailsRecordByFmsId = vinDecoderService.getFleetMasterVinDetailByFmsId(invoiceEntryVO.getFmsId());
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
				addSuccessMessage("saved.success", "Invoice Header");
				
			}else{
				invoiceHeaderId =  invoiceEntryVO.getInvoiceDocId();
			}
			
			
			Map<String, Object> restoreStateValues = new HashMap<String, Object>();
			restoreStateValues.put(ViewConstants.VIEW_PARAM_INVOICE_HEADER_DOC_NO, targetPoDocNo);
			restoreStateValues.put(ViewConstants.VIEW_PARAM_QUOTE_MODEL_ID, targetQmdId);
			saveRestoreStateValues(restoreStateValues);
			Map<String, Object> nextPageValues = new HashMap<String, Object>();
			nextPageValues.put(ViewConstants.VIEW_PARAM_QUOTE_MODEL_ID, targetQmdId);
			nextPageValues.put(ViewConstants.VIEW_PARAM_RELEASED_PO_DOC_NO, targetPoDocNo);
			nextPageValues.put(ViewConstants.VIEW_PARAM_RELEASED_PO_DOC_ID, docReleasedPo.getDocId());
			nextPageValues.put(ViewConstants.VIEW_PARAM_MODE, ViewConstants.VIEW_MODE_EDIT);
			nextPageValues.put(ViewConstants.VIEW_PARAM_INV_HEADER_PROCESS, true); 
			nextPageValues.put(ViewConstants.VIEW_PARAM_INVOICE_HEADER_DOC_ID, invoiceHeaderId);
			saveNextPageInitStateValues(nextPageValues);
			if(isStockOrder()){
				forwardToURL(ViewConstants.POST_INVOICE);
			}else{
				boolean isOnContract = false;
				//For RC-1760/1764/1951
				//String fleetStatus = quotationModelDAO.getFleetStatus(invoiceEntryVO.getFmsId());
				/*if(!MALUtilities.isEmpty(fleetStatus)){
					//2= on contract 18= pending live
					if("2".equals(fleetStatus) || "18".equals(fleetStatus)){
						isOnContract	= true;
					}
				}*/
				QuotationModel acceptedQuote = quotationModelDAO.findById(targetQmdId).orElse(null);
				if(acceptedQuote != null){
					QuotationModel possibleOnContractQuote	= quotationModelDAO.findByQuoteIdAndStatus(acceptedQuote.getQuotation().getQuoId(), 6);
					if(possibleOnContractQuote != null){
						isOnContract = true;
					}
				}
				if(isOnContract){
					
					nextPageValues.put(ViewConstants.VIEW_PARAM_INVOICE_HEADER_DOC_ID, invoiceHeaderId);
					forwardToURL(ViewConstants.POST_INVOICE);
				}else{
					forwardToURL(ViewConstants.CAPITAL_COSTS);
				}			
			}

		} catch (Exception ex) {
			handleException("generic.error", new String[] { "saving invoice header" }, ex, "init");
			logger.error(ex);
			return null;
		}
		return null;

	}

	public String saveOnConfirm() {
		userConfirmation = true;
		isVinModified = false;
		
		return next();
	}
	
	public String cancelOnConfirm() {
		
		userConfirmation = false;
		chromeValidationDone = false;
		
		return null;
	}

	public String cancel() {

		return super.cancelPage();
	}
	public void  decodeVendorByNameOrCode(){ 

		setVendorCode("");
		if(MALUtilities.isNotEmptyString(getVendorName()) ){
			Long cId = getLoggedInUser().getCorporateEntity().getCorpId();
			List<VendorLovVO> result = vendorService.getVendors(cId, "S" , getVendorName(), new PageRequest(0,10));
			if(result == null || result.size() == 0){					 
				super.addErrorMessage(VENDOR_INPUT_UI_ID,"decode.noMatchFound.msg", "Change Vendor " + getVendorName()); 
			}else if(result.size() == 1){
				VendorLovVO  vendorVO = result.get(0);
				setVendorName(vendorVO.getVendorName());
				setVendorCode(vendorVO.getVendorCode());
			}else if(result.size() > 1){
				super.addErrorMessage(VENDOR_INPUT_UI_ID,"decode.notExactMatch.msg", "Change Vendor " +  getVendorName());			
			}
		}
	}

	@Override
	protected void loadNewPage() {
		thisPage.setPageDisplayName("Invoice Entry");
		thisPage.setPageUrl(ViewConstants.INVOICE_ENTRY);
		if (thisPage.getInputValues().get(ViewConstants.VIEW_PARAM_RELEASED_PO_DOC_NO) != null) {
			targetPoDocNo = (String) thisPage.getInputValues().get(ViewConstants.VIEW_PARAM_RELEASED_PO_DOC_NO);
		}
		if (thisPage.getInputValues().get(ViewConstants.VIEW_PARAM_QUOTE_MODEL_ID) != null) {
			targetQmdId = (Long) thisPage.getInputValues().get(ViewConstants.VIEW_PARAM_QUOTE_MODEL_ID);
		}

	}

	@Override
	protected void restoreOldPage() {
		targetPoDocNo = (String) thisPage.getRestoreStateValues().get(ViewConstants.VIEW_PARAM_INVOICE_HEADER_DOC_NO);
		targetQmdId = (Long) thisPage.getInputValues().get(ViewConstants.VIEW_PARAM_QUOTE_MODEL_ID);

	}

	 private boolean isStockOrder(){
	//	logger.debug("targetQmdId=="+targetQmdId +"-docReleasedPo-getSourceCode"+docReleasedPo.getSourceCode());
		 if(targetQmdId == null){
			 return true;
		 }
		 if(docReleasedPo != null && docReleasedPo.getSourceCode().equals("FLGRD")){
			 return true;
		 }
		 
		 
		 if(docReleasedPo != null && docReleasedPo.getSourceCode().equals("FLORDER")){
			 return true;
		 }

		 List<Doc> docGRDList = docDAO.findDocForGenericExtIdByDocTypeAndSourceCode(targetQmdId , "PORDER" , "FLGRD");//Stock unit 3rd party PO will have source code as FLQUOTE so looking for FLGRD
		 if(docGRDList != null && docGRDList.size() > 0){
			 return true;
		 }
		 
		return false;	
	 }
	public InvoiceEntryVO getInvoiceEntryVO() {
		return invoiceEntryVO;
	}

	public void setInvoiceEntryVO(InvoiceEntryVO invoiceEntryVO) {
		this.invoiceEntryVO = invoiceEntryVO;
	}

	public boolean isInvalidAccessPath() {
		return invalidAccessPath;
	}

	public void setInvalidAccessPath(boolean invalidAccessPath) {
		this.invalidAccessPath = invalidAccessPath;
	}

	public String getSelectedKeyCode() {
		return selectedKeyCode;
	}

	public void setSelectedKeyCode(String selectedKeyCode) {
		this.selectedKeyCode = selectedKeyCode;
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
	
	public String getVendorCode() {
		return vendorCode;
	}

	public void setVendorCode(String vendorCode) {
		this.vendorCode = vendorCode;
	}

	public String getVendorName() {
		return vendorName;
	}

	public void setVendorName(String vendorName) {
		this.vendorName = vendorName;
	}
	

}
