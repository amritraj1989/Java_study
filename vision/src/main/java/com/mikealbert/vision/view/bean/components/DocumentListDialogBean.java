package com.mikealbert.vision.view.bean.components;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.faces.context.FacesContext;

import net.sf.jasperreports.engine.JasperPrint;

import org.primefaces.context.RequestContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.mikealbert.common.MalLogger;
import com.mikealbert.common.MalLoggerFactory;
import com.mikealbert.service.reporting.JasperReportService;
import com.mikealbert.util.MALUtilities;
import com.mikealbert.vision.service.PurchasingEmailService;
import com.mikealbert.vision.view.bean.BaseBean;
import com.mikealbert.vision.view.bean.JasperReportBean;
import com.mikealbert.vision.vo.ClientOrderConfirmationListItemVO;
import com.mikealbert.vision.vo.DocumentListItemVO;
import com.mikealbert.vision.vo.MainPurchaseOrderListItemVO;
import com.mikealbert.vision.vo.OrderSummaryListItemVO;
import com.mikealbert.vision.vo.RevisionSchAListItemVO;
import com.mikealbert.vision.vo.UpfitterPurchaseOrderListItemVO;
 
@Component
@Scope("view")
public class DocumentListDialogBean extends BaseBean {	
	@Resource JasperReportService jasperReportService;
	@Resource JasperReportBean jasperReportBean;
	@Resource PurchasingEmailService purchasingEmailService;
	
	private static final long serialVersionUID = 6301246476023944352L;
	private static final String UNREVIEWED_DOCUMENT_MESSAGE = "Please view all required documents before exiting.";
	
	MalLogger logger = MalLoggerFactory.getLogger(this.getClass());	
	private String clientId;
	private List<DocumentListItemVO> documentList;
	private String initialMessage;
	private String onCloseCallback;
	private DocumentListItemVO selectedDocumentItem;
	private String UnviewedDocumentAlertMessage = "NONE";
	private String unitNumber;

		
	/**
	 * Initializes the bean
	 */
    @SuppressWarnings("unchecked")
	public void init(){ 
		FacesContext fc;	

    	try {
    		clearEverything();
    		
    		fc = FacesContext.getCurrentInstance(); 
    		setDocumentList(new ArrayList<DocumentListItemVO>(fc.getApplication().evaluateExpressionGet(fc,"#{cc.attrs.documentVOs}", List.class)));  
    		setInitialMessage(fc.getApplication().evaluateExpressionGet(fc,"#{cc.attrs.initialMessage}", String.class)); 
    		setOnCloseCallback(fc.getApplication().evaluateExpressionGet(fc,"#{cc.attrs.onClose}", String.class));
    		setUnitNumber(fc.getApplication().evaluateExpressionGet(fc, "#{cc.attrs.unitNumber}", String.class));
			super.setDirtyData(false);

		} catch(Exception e) {	
			super.addErrorMessage("generic.error", e.getMessage());
			logger.error(e);
		}    	
      	    	     	
    }
    
    public void displayInitialMessageListener() {
    	if(MALUtilities.isNotEmptyString(getInitialMessage())){
    		super.addInfoMessageSummary("custom.message", getInitialMessage());
    	}
    }


    public void view(){
    	JasperPrint jasperPrint;
    	
    	try {
			switch(getSelectedDocumentItem().getReportName()) {
			    case CLIENT_ORDER_CONFIRMATION:
			        jasperPrint = jasperReportService.getClientOrderConfirmationReport(getSelectedDocumentItem().getId());	
			        break;
			    case THIRD_PARTY_PURHCASE_ORDER:
			    	jasperPrint = jasperReportService.getThirdPartyPurchaseOrderReport(getSelectedDocumentItem().getId(), 
			    			((UpfitterPurchaseOrderListItemVO) getSelectedDocumentItem()).getStockYN());
			    	break;
			    case VEHICLE_PURCHASE_ORDER_SUMMARY:
			    	jasperPrint = jasperReportService.getVehicleOrderSummaryReport(getSelectedDocumentItem().getId(),
			    			((OrderSummaryListItemVO) getSelectedDocumentItem()).getStockYN());
			    	break;
			    case PRINT_COVER_SHEET:
			    	jasperPrint = jasperReportService.getPurchaseOrderCoverSheetReport(getSelectedDocumentItem().getId());
			    	break;
			    case MAIN_PURHCASE_ORDER:
			    	jasperPrint = jasperReportService.getMainPurchaseOrderReport(getSelectedDocumentItem().getId(), 
			    			((MainPurchaseOrderListItemVO) getSelectedDocumentItem()).getStockYN());
			    	break;
			    case COURTESY_DELIVERY_INSTRUCTION:
			    	jasperPrint = jasperReportService.getCourtesyDeliveryInstructionReport(getSelectedDocumentItem().getId());
			    	break; 	
			    case SCHEDULE_A:
			    	jasperPrint = jasperReportService.getOpenEndRevisionScheduleA( ((RevisionSchAListItemVO) getSelectedDocumentItem()).getCurrentQmdId(), 
			    			((RevisionSchAListItemVO) getSelectedDocumentItem()).getRevisionQmdId());
			    	break; 	
			    case AMORTIZATION_SCHEDULE:
			    	jasperPrint = jasperReportService.getOpenEndRevisionAmortizationSchedule( ((RevisionSchAListItemVO) getSelectedDocumentItem()).getCurrentQmdId(), 
			    			((RevisionSchAListItemVO) getSelectedDocumentItem()).getRevisionQmdId());
			    	break; 	
			    default: 
			    	throw new Exception("Unsupported report type detected " + getSelectedDocumentItem().getReportName().getFileName());
			}
		
			jasperReportBean.displayPDFReport(jasperPrint);
		
		} catch (Exception e) {
			logger.error(e);
			super.addErrorMessage("custom.message", e.getMessage());
		}
    }
    
    public void email(DocumentListItemVO documentItem){
    	
    	try {
			switch(documentItem.getReportName()) {
			    case CLIENT_ORDER_CONFIRMATION:
			    	emailClientOrderConfirmation(documentItem);
			    	break;
			    default: 
			    	throw new Exception("Unsupported report type detected " + getSelectedDocumentItem().getReportName().getFileName());
			}
		} catch (Exception e) {
			super.addErrorMessage("custom.message", e.getMessage());
		}    	
    }
    
    /**
     * 
     * @param documentListItem
     */
    private void emailClientOrderConfirmation(DocumentListItemVO documentListItem){
    	JasperPrint jasperPrint;    	
    	ClientOrderConfirmationListItemVO clientOrderConfirmationDocItem = 
    			(ClientOrderConfirmationListItemVO) getDocumentList().get(getDocumentList().indexOf(documentListItem));
		
    	try {
    		//Stock Orders do not have a client. Could there be a scenario where this is a stock quote?
    		if(!MALUtilities.convertYNToBoolean(clientOrderConfirmationDocItem.getStockYN())) {    	
    			clientOrderConfirmationDocItem.setEmailable(purchasingEmailService.isClientConfirmationEmailable(clientOrderConfirmationDocItem.getId()));
    			if(clientOrderConfirmationDocItem.isEmailable()){
    				jasperPrint = jasperReportService.getClientOrderConfirmationReport(clientOrderConfirmationDocItem.getId());
    				purchasingEmailService.emailClientConfirmation(jasperPrint, clientOrderConfirmationDocItem.getId(), super.getLoggedInUser().getEmployeeNo());
    				clientOrderConfirmationDocItem.setEmailed(true);
    				clientOrderConfirmationDocItem.setViewed(true);
    			}
    		}
    	} catch(Exception e) {
    		logger.error(e);
			clientOrderConfirmationDocItem.setEmailed(false);			
		}
    }
    
    /**
     * Clears the component's attributes to bring it back to an initial state.
     */
	private void clearEverything() {		
		setInitialMessage(null);
		setUnitNumber(null);
	}

	public void viewDocumentListener(DocumentListItemVO documentListItem){		
	    setSelectedDocumentItem(documentListItem);
	    getDocumentList().get(getDocumentList().indexOf(documentListItem)).setViewed(true);
		RequestContext.getCurrentInstance().execute("showDocument()");
	}	
			
    /**
     * Initializes the parameters to pass back to the callback function
     */
	public void onHideListener(){
		boolean foundUnReviewedDoc = false;
		
		for(DocumentListItemVO documentListItem : getDocumentList()) {
			if(documentListItem.isRequired() && !documentListItem.isViewed()) {
				foundUnReviewedDoc = true;
				break;
			}
		}
		
		if(foundUnReviewedDoc) {
			setUnviewedDocumentAlertMessage(UNREVIEWED_DOCUMENT_MESSAGE);			
			RequestContext.getCurrentInstance().update("documentListDialog:ccUnviewedDocumentAlertDialogId");			
			RequestContext.getCurrentInstance().execute("showDialog('ccUnviewedDocumentAlertWidgetVar')");			
		} else {
			RequestContext.getCurrentInstance().execute(onCloseCallback);				
		}
	}
	
	
	public void unviewedDocumentAlertYesButtonListener(){
		for(DocumentListItemVO documentListItem : getDocumentList()){
			documentListItem.setViewed(true);
		}
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public List<DocumentListItemVO> getDocumentList() {
		return documentList;
	}

	public void setDocumentList(List<DocumentListItemVO> documentList) {
		this.documentList = documentList;
	}


	public String getInitialMessage() {
		return initialMessage;
	}

	public void setInitialMessage(String initialMessage) {
		this.initialMessage = initialMessage;
	}


	public String getOnCloseCallback() {
		return onCloseCallback;
	}


	public void setOnCloseCallback(String onCloseCallback) {
		this.onCloseCallback = onCloseCallback;
	}


	public DocumentListItemVO getSelectedDocumentItem() {
		return selectedDocumentItem;
	}


	public void setSelectedDocumentItem(DocumentListItemVO selectedDocumentItem) {
		this.selectedDocumentItem = selectedDocumentItem;
	}

	public String getUnviewedDocumentAlertMessage() {
		return UnviewedDocumentAlertMessage;
	}


	public void setUnviewedDocumentAlertMessage(
			String unviewedDocumentAlertMessage) {
		UnviewedDocumentAlertMessage = unviewedDocumentAlertMessage;
	}

	public String getUnitNumber() {
		return unitNumber;
	}

	public void setUnitNumber(String unitNumber) {
		this.unitNumber = unitNumber;
	}
		
}
