package com.mikealbert.vision.view.bean;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.primefaces.context.RequestContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.mikealbert.data.dao.AnalysisCategoryDAO;
import com.mikealbert.data.dao.AnalysisCodeDAO;
import com.mikealbert.data.dao.DebitCreditMemoTransactionDAO;
import com.mikealbert.data.dao.DebitCreditMemoTypeDAO;
import com.mikealbert.data.dao.DebitCreditStatusCodeDAO;
import com.mikealbert.data.dao.ExternalAccountDAO;
import com.mikealbert.data.dao.GlCodeDAO;
import com.mikealbert.data.dao.TimePeriodDAO;
import com.mikealbert.data.dao.WillowEntityDefaultDAO;
import com.mikealbert.data.dao.WillowUserDAO;
import com.mikealbert.data.entity.AnalysisCategory;
import com.mikealbert.data.entity.AnalysisCode;
import com.mikealbert.data.entity.Contract;
import com.mikealbert.data.entity.DebitCreditMemoTransaction;
import com.mikealbert.data.entity.DebitCreditMemoType;
import com.mikealbert.data.entity.DebitCreditStatusCode;
import com.mikealbert.data.entity.DriverAllocation;
import com.mikealbert.data.entity.ExternalAccount;
import com.mikealbert.data.entity.ExternalAccountPK;
import com.mikealbert.data.entity.FleetMaster;
import com.mikealbert.data.entity.GlCode;
import com.mikealbert.data.entity.GlCodePK;
import com.mikealbert.data.entity.PersonnelBase;
import com.mikealbert.data.entity.TimePeriod;
import com.mikealbert.data.enumeration.CorporateEntity;
import com.mikealbert.data.enumeration.DebitCreditStatusEnum;
import com.mikealbert.data.vo.DebitCreditMemoVO;
import com.mikealbert.data.vo.DebitCreditTransactionVO;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.service.ContractService;
import com.mikealbert.service.CustomerAccountService;
import com.mikealbert.service.DebitCreditMemoService;
import com.mikealbert.service.DriverAllocationService;
import com.mikealbert.service.FleetMasterService;
import com.mikealbert.service.GlCodeService;
import com.mikealbert.service.WillowConfigService;
import com.mikealbert.util.MALUtilities;
import com.mikealbert.vision.view.ViewConstants;


@Component
@Scope("view")
public class DebitCreditMemoAddEditBean extends StatefulBaseBean {
	private static final long serialVersionUID = 1L;
	
	@Resource FleetMasterService fleetMasterService;
	@Resource DebitCreditMemoTypeDAO debitCreditMemoTypeDAO;
	@Resource CustomerAccountService externalAccountService;	
	@Resource ContractService contractService;
	@Resource DebitCreditMemoService debitCreditMemoService;	
	@Resource ExternalAccountDAO externalAccountDAO;
	@Resource GlCodeService glCodeService; 
	@Resource WillowEntityDefaultDAO willowEntityDefaultDAO;
	@Resource WillowUserDAO willowUserDAO;
	@Resource TimePeriodDAO timePeriodDAO;
	@Resource AnalysisCategoryDAO analysisCategoryDAO;
	@Resource AnalysisCodeDAO analysisCodeDAO;
	@Resource WillowConfigService willowConfigService;
	@Resource DebitCreditMemoTransactionDAO debitCreditMemoTransactionDAO;
	@Resource DriverAllocationService driverAllocationService;
	@Resource GlCodeDAO glCodeDao;
	@Resource DebitCreditStatusCodeDAO debitCreditStatusCodeDAO;
	
	@Value("${url.debit.credit.processing}")
	private String debitCreditProcessingBaseURL;	
	
	private static final String ACCOUNT_TYPE_CUSTOMER = "C";
	private static final String MEMO_TYPE = "memoType"; 
	private static final String UNIT_NO = "unitNo"; 
	private static final String REASON = "reason";
	private static final String CLIENT_ACCOUNT_UI_ID = "clientId";	
	private static final String INVOICE_NO = "invoiceNo";
	private static final String TRANSACTION_DATE = "transactionDate";	
//	private static final String TAX_AMOUNT = "taxAmount";
	private static final String NET_AMOUNT = "netAmount";	
	private static final String APPROVER = "approver";
	private static final String GL_CODE = "glCode";	
	private static final String ANALYSIS_CATEGORY = "analysisCategory";	
	private static final String ANALYSIS_CODE = "analysisCode";	
	private static final String RENT_APPLICABLE_DATE = "rentApplicableDate";		
	private static final String MEMO_TYPE_FULL_CREDIT = "FULL CREDIT"; 	
	private static final String MEMO_TYPE_PARTIAL_CREDIT = "PARTIAL CREDIT"; 	
	private static final String ANALYSIS_CATEGORY_FLBILLING = "FLBILLING"; 	
	private static final String ANALYSIS_CATEGORY_FLINFORMAL = "FLINFORMAL"; 
	
	final static String GROWL_UI_ID = "growl";

	private DebitCreditMemoVO debitCreditMemo;
	private DebitCreditMemoType selectedMemoType;
	private	AnalysisCategory selectedAnalysisCategory;
	private	AnalysisCode selectedAnalysisCode;
	private ExternalAccount selectedExternalAccount;
	private String selectedApproverName;
	private String customerLOVParam;	
	private String customerAccountType;
	private String customerAccountCid;	
	private String savedUnitNo;	//field to compare if unit no is changed by user(little hack)
	private String savedAnalysisCode;//field to compare if unit no is changed by user(little hack)	

	private boolean clientUnit = true;
	private boolean taxOnly = false; //RE-1011 - last minute change to do away with tax only functionality
	//private boolean	enableTaxAmount = false; //RE-1097 - undo Full Credit tax amount logic
	private boolean creditDisplay = false;
	private boolean isRetain = false;
	private boolean isDuplicate = false;
	private boolean hasAprrovePermission = false;
	private boolean hasProcessPermission = false;
	private boolean showDeleteButton = false;
	private boolean showSaveButton = false;
	private boolean enableProcessBtn = false;
	private Long paramDcNo;
	private boolean isAddMode;//else edit or view
	private boolean isProcessMode = false;
	
	private boolean	hasGeneralEditPermission = false;
	private boolean	hasApproverEditPermission = false;
	private boolean hasProcessorEditPermission = false;
	
	private static final SimpleDateFormat dateFormatter = new SimpleDateFormat("MM/dd/yy", Locale.US);
	private String jmsMessageId;
	private String status;
	private boolean warningIndicator = false;
	private DriverAllocation driverAllocation;

	
	@PostConstruct
    public void init() {
        super.openPage();
        if(isAddMode()){//add mode
        	addRetainTransaction();
        }else{//edit mode
        	initAddTransaction();

        	if (paramDcNo != null){ //DC Search screen transaction
	    		DebitCreditMemoTransaction debitCreditMemoTransaction = debitCreditMemoService.getDebitCreditMemoTransaction(paramDcNo);
	    		getDebitCreditMemo().setDebitCreditMemoTransaction(debitCreditMemoTransaction);
	    		getDebitCreditMemo().getDebitCreditMemoTransaction().setTotalAmount(debitCreditMemoTransaction.getTotalAmount());
	    		//setTaxOnly("Y".equalsIgnoreCase(debitCreditMemoTransaction.getTaxOnly()) ? true : false);
	    		setClientUnit("Y".equalsIgnoreCase(debitCreditMemoTransaction.getIsClientUnit()) ? true : false);
	    		setSelectedExternalAccount(debitCreditMemoTransaction.getExternalAccount());
	    		setCustomerLOVParam(debitCreditMemoTransaction.getExternalAccount().getExternalAccountPK().getAccountCode());
	    		setSavedUnitNo(debitCreditMemoTransaction.getUnitNo());
	    		setSavedAnalysisCode(debitCreditMemoTransaction.getAnalysisCode());
	    		populateStatus();
	    		
				populateSelectOneMenuValues(debitCreditMemoTransaction);
        	} else {   
	        	initViewEditErrorTransaction(); //DC Upload Error screen transaction
        	}
        }
		adjustUIState();        
    }

	private void initViewEditErrorTransaction() {
		try {	
			DebitCreditTransactionVO dcError = fetchDebitCreditErrorObject();   
			DebitCreditMemoTransaction debitCreditMemoTransaction= new DebitCreditMemoTransaction();
			
			DebitCreditMemoType dcMemoType = debitCreditMemoService.convertDebitCreditMemoType(dcError.getDebitCreditType().toUpperCase());
			debitCreditMemoTransaction.setDcmtDcmtId(dcMemoType.getDcmtId());
			debitCreditMemoTransaction.setTicketNo(dcError.getTicketNo());
			debitCreditMemoTransaction.setReason(dcError.getReason());
			debitCreditMemoTransaction.setLineDescription(dcError.getLineDescription());
		
			ExternalAccount ea = externalAccountDAO.findByAccountCodeAndAccountTypeAndCId(dcError.getAccountCode(), ACCOUNT_TYPE_CUSTOMER, CorporateEntity.MAL.getCorpId());
			if (ea != null){
				debitCreditMemoTransaction.setExternalAccount(ea);
				setSelectedExternalAccount(debitCreditMemoTransaction.getExternalAccount());
				setCustomerLOVParam(debitCreditMemoTransaction.getExternalAccount().getExternalAccountPK().getAccountCode());	
			} else {
				setCustomerLOVParam(null);
			}
			
			debitCreditMemoTransaction.setUnitNo(dcError.getUnitNo());
			debitCreditMemoTransaction.setIsClientUnit(dcError.getIsClientUnit());
			setClientUnit("Y".equalsIgnoreCase(debitCreditMemoTransaction.getIsClientUnit()) ? true : false);
			
			AnalysisCategory aCat = debitCreditMemoService.convertAnalysisCategory(dcError.getCategory());
			debitCreditMemoTransaction.setCategoryId(aCat.getCategoryId());
			debitCreditMemoTransaction.setAnalysisCode(dcError.getAnalysisCode());
			
			BigDecimal netAmount = new BigDecimal(dcError.getNetAmount());
			debitCreditMemoTransaction.setNetAmount(netAmount);
			debitCreditMemoTransaction.setTotalAmount(netAmount);//set total amount to net amount
			debitCreditMemoTransaction.setInvoiceNote(dcError.getInvoiceNote());
			debitCreditMemoTransaction.setTransactionDate(dateFormatter.parse(dcError.getTransactionDate()));
			debitCreditMemoTransaction.setInvoiceNo(dcError.getInvoiceNo());
			if(!MALUtilities.isEmpty(dcError.getRentApplicableDate())){
				debitCreditMemoTransaction.setRentApplicableDate(dateFormatter.parse(dcError.getRentApplicableDate()));
			} else {
				debitCreditMemoTransaction.setRentApplicableDate(null);
			}
			debitCreditMemoTransaction.setSelectedApprover(dcError.getSelectedApprover());
			debitCreditMemoTransaction.setGlCode(dcError.getGlCode());
			debitCreditMemoTransaction.setSubmitter(dcError.getSubmitter());
			debitCreditMemoTransaction.setSource(dcError.getFileName());
			
    		setSavedUnitNo(debitCreditMemoTransaction.getUnitNo());
    		setSavedAnalysisCode(debitCreditMemoTransaction.getAnalysisCode());
	
			getDebitCreditMemo().setDebitCreditMemoTransaction(debitCreditMemoTransaction);
			
			populateSelectOneMenuValues(debitCreditMemoTransaction);
			
			isValidForSave();//for DC upload errors, do validation upon loading 			

		} catch (Exception ex) {
		 	logger.error(ex);
		 	super.addErrorMessage("generic.error", ex.getMessage());
		}
	}

	private DebitCreditTransactionVO fetchDebitCreditErrorObject() {
		DebitCreditTransactionVO dcError = new DebitCreditTransactionVO();
		try {
			RestTemplate restTemplate = new RestTemplate();
			Map<String,String> params = new HashMap<String, String>();
			params.put("jmsMessageId", jmsMessageId);
			final String uri = debitCreditProcessingBaseURL + "/businessErrors/{jmsMessageId}";
			
			dcError = restTemplate.getForObject(uri, DebitCreditTransactionVO.class, params);
		} catch (Exception ex) {
		 	logger.error(ex);
		 	super.addErrorMessage("generic.error", ex.getMessage());
		}	
		return dcError;		
	}

	private void populateSelectOneMenuValues(DebitCreditMemoTransaction debitCreditMemoTransaction) {
		for (DebitCreditMemoType debitCreditMemoType : getDebitCreditMemo().getDebitCreditMemoTypes()) {
			if(debitCreditMemoType.getDcmtId().equals(debitCreditMemoTransaction.getDcmtDcmtId())){
				setSelectedMemoType(debitCreditMemoType);
				updateCreditDisplayFlag();break;
			}
		}
		
		for (AnalysisCategory analysisCategory : getDebitCreditMemo().getAnalysisCategories()) {
			if(analysisCategory.getCategoryId().equals(debitCreditMemoTransaction.getCategoryId())){
				setSelectedAnalysisCategory(analysisCategory);
				loadAnalysisCode();
			}
		}
		
		for (AnalysisCode analysisCode : getDebitCreditMemo().getAnalysisCodes()) {
			if(analysisCode.getId().getCategoryId().equals(debitCreditMemoTransaction.getCategoryId()) 
								&& analysisCode.getId().getAnalysisCode().equals(debitCreditMemoTransaction.getAnalysisCode())){
				
				setSelectedAnalysisCode(analysisCode);break;
		    		
			}
		}
		
		if(debitCreditMemoTransaction.getSelectedApprover() != null){
			for (PersonnelBase personnelBase : getDebitCreditMemo().getApproverNamesList()) {
				if(personnelBase.getEmployeeNo().equals(debitCreditMemoTransaction.getSelectedApprover())){
					setSelectedApproverName(personnelBase.getEmployeeNo());
				}
			}
		}
	} 
    
    public void addRetainTransaction(){
    	try {    		
		 	if(isRetain){
		 		initRetainTransaction();
		 	} else {
		 		initAddTransaction();
		 	}
    	} catch (Exception ex) {
		 	logger.error(ex);
		 	super.addErrorMessage("generic.error", ex.getMessage());
    	}
    }
    
    private void initAddTransaction(){
    	setDebitCreditMemo(new DebitCreditMemoVO());
    	getDebitCreditMemo().setDebitCreditMemoTransaction(new DebitCreditMemoTransaction());
    	getDebitCreditMemo().getDebitCreditMemoTransaction().setStatus(DebitCreditStatusEnum.STATUS_SUBMITTED.getCode());
    	setCustomerLOVParam(null);
		setSelectedExternalAccount(new ExternalAccount(new ExternalAccountPK()));			
		setSelectedMemoType(null);
		//setTaxOnly(false);
		setClientUnit(true);
		setSelectedAnalysisCategory(null);
		setSelectedAnalysisCode(null);
		setSelectedApproverName(null);
		loadData();
    }
    
    private void initRetainTransaction(){
    	getDebitCreditMemo().getDebitCreditMemoTransaction().setDcNo(null);
	warningIndicator = false;
       //	getDebitCreditMemo().getDebitCreditMemoTransaction().setTotalAmount(calculateTotalAmountOutput());
    }
    
    private void loadData(){
        try {
        	loadDebitCreditMemoTypes();
        	loadAnalysisCategory();
        	loadApproverNameList();        
        	loadPermission();
		} catch (Exception e) {
			logger.error(e);
			 if(e  instanceof MalBusinessException){				 
				 super.addErrorMessage(e.getMessage()); 
			 }else{
				 super.addErrorMessage("generic.error.occured.while", " building screen.");
			 }
		}        
    }
    
    private void loadDebitCreditMemoTypes() {
    	getDebitCreditMemo().setDebitCreditMemoTypes(debitCreditMemoService.getDebitCreditMemoTypes());
	}

    private void loadAnalysisCategory() {
    	getDebitCreditMemo().setAnalysisCategories(debitCreditMemoService.getAnalysisCategories());
	} 
    
    public void loadAnalysisCode() {
    	if(selectedAnalysisCategory != null && selectedAnalysisCategory.getCategoryId() != null ){
    		getDebitCreditMemo().setAnalysisCodes(debitCreditMemoService.getAnalysisCodes(selectedAnalysisCategory.getCategoryId()));
    	}else{
    		getDebitCreditMemo().setAnalysisCodes(null);
    	}
	} 
   
    private void loadApproverNameList(){
    	getDebitCreditMemo().setApproverNamesList(debitCreditMemoService.getDebitCreditApproverList()); 
    }
    
    private void loadPermission(){
    	this.hasAprrovePermission = hasPermission("aprroveDebitCreditMemo"); //tax and net amount editing
    	this.hasProcessPermission = hasPermission("processDebitCreditMemo"); //line desc and gl code editing
        
    	if(isAddMode()){
    		this.hasGeneralEditPermission = true;
			if (this.hasProcessPermission){
				this.hasProcessorEditPermission = true; 
			}
    	} else {
    		if (paramDcNo != null){ //DC Search screen transaction
	        	DebitCreditMemoTransaction dcmt = debitCreditMemoService.getDebitCreditMemoTransaction(paramDcNo);
	        	
	    		if (getLoggedInUser().getEmployeeNo().equals(dcmt.getSubmitter()) 
	    				&& (dcmt.getStatus().equals(DebitCreditStatusEnum.STATUS_APPROVED.getCode()) || dcmt.getStatus().equals(DebitCreditStatusEnum.STATUS_SUBMITTED.getCode()))){
	    			this.hasGeneralEditPermission = true;
	    		}
	    		if (this.hasAprrovePermission 
			    		&& (dcmt.getStatus().equals(DebitCreditStatusEnum.STATUS_SUBMITTED.getCode()) || dcmt.getStatus().equals(DebitCreditStatusEnum.STATUS_APPROVED.getCode()))){ //RE-1251
	    			this.hasGeneralEditPermission = true;
	    			this.hasApproverEditPermission = true;
		        }
		        if (this.hasProcessPermission 
		        		&& (dcmt.getStatus().equals(DebitCreditStatusEnum.STATUS_APPROVED.getCode()) || dcmt.getStatus().equals(DebitCreditStatusEnum.STATUS_SUBMITTED.getCode()))){ //RE-1231
		        	this.hasGeneralEditPermission = true;
		        	this.hasProcessorEditPermission = true; 
		        }	        	
    		} else { //DC Upload Error screen transaction
    			DebitCreditTransactionVO dcError = fetchDebitCreditErrorObject();
    			dcError.setStatus("S"); //For the purposes of determining permissions, treat this transaction as submitted.
        	
	    		if (getLoggedInUser().getEmployeeNo().equals(dcError.getSubmitter())){ 
	    			this.hasGeneralEditPermission = true;
	    		}
	    		if (this.hasAprrovePermission){ 
	    			this.hasGeneralEditPermission = true;	    			
	    			this.hasApproverEditPermission = true;
		        }
		        if (this.hasProcessPermission) {
		        	this.hasGeneralEditPermission = true;
		        	this.hasProcessorEditPermission = true; 
		        }
    		}
    	}
    }
    
	private  void adjustUIState() {
		if(!isAddMode()){
			if ((this.hasGeneralEditPermission || this.hasProcessorEditPermission || this.hasApproverEditPermission)){
				this.showSaveButton = true;	
				this.showDeleteButton = true;				
			}
			
			if (this.hasProcessorEditPermission){
				if (debitCreditMemo.getDebitCreditMemoTransaction().getStatus() != null && debitCreditMemo.getDebitCreditMemoTransaction().getStatus().equalsIgnoreCase(DebitCreditStatusEnum.STATUS_APPROVED.getCode())){
					this.enableProcessBtn = true;
				}
			}
		}
	}   
	
	public String processDebitCreditMemos(){
		setProcessMode(true);	
		
    	if(saveTransaction()){
    		try {
    			debitCreditMemoService.processDebitCreditMemo(getLoggedInUser().getEmployeeNo(), debitCreditMemo.getDebitCreditMemoTransaction());
    			super.addSuccessMessage("custom.message","DC# " + debitCreditMemo.getDebitCreditMemoTransaction().getDcNo() + " processed successfully."); 

    			
    			
    			if (driverAllocation == null || isDuplicate){
            		showSaveButton = false;
            		showDeleteButton = false;
            		enableProcessBtn = false;
        			return null;
        		}
    			
    			return cancel();
			} catch (Exception e) {
				logger.error(e, "Unable to create doc/docl entry for dc no: "+debitCreditMemo.getDebitCreditMemoTransaction().getDcNo());
				super.addErrorMessage("custom.message","Process Error: "+e.getMessage()); 
			}
    		
    	}
    	setProcessMode(false);
		return null;
	}	

    public String save(){    
    	if(saveTransaction()){
    		if (paramDcNo != null){
        		super.addSuccessMessage("custom.message","DC# " + debitCreditMemo.getDebitCreditMemoTransaction().getDcNo() + " updated successfully."); 
    		} else {
    			deleteFromErrorQueue(); 
    			paramDcNo = debitCreditMemo.getDebitCreditMemoTransaction().getDcNo();
    			super.addSuccessMessage("custom.message","DC# " + debitCreditMemo.getDebitCreditMemoTransaction().getDcNo() + " created successfully and was removed from the Business Validation Error Queue.");
    		}
    		if (driverAllocation == null || isDuplicate){
    			return null;
    		}
    		
    		return cancel();
    	}
    	
    	return null;
    } 
    
    public void saveAndStay(){    
    	if(saveTransaction()){
    		super.addSuccessMessage("custom.message","DC# " + debitCreditMemo.getDebitCreditMemoTransaction().getDcNo() + " created successfully."); 
    		this.isRetain = false;
    		addRetainTransaction();
    	}
    }  
    
    public String saveAndRetain(){
    	if(saveTransaction()){
    		super.addSuccessMessage("custom.message","DC# " + debitCreditMemo.getDebitCreditMemoTransaction().getDcNo() + " created successfully."); 
    		this.isRetain = true;
    		addRetainTransaction();
    	}
    	
    	return null;
    }
    
	private boolean saveTransaction(){
    	try {
			if(!isValidForSave()){
				return false;
			}    			
	
			DebitCreditMemoTransaction dcMemoTransaction = debitCreditMemo.getDebitCreditMemoTransaction();
			
			selectedExternalAccount = externalAccountDAO.findByAccountCodeAndAccountTypeAndCId(customerLOVParam, ACCOUNT_TYPE_CUSTOMER, CorporateEntity.MAL.getCorpId());
			
			dcMemoTransaction.setDcmtDcmtId(selectedMemoType.getDcmtId());
			dcMemoTransaction.setTaxOnly(taxOnly ? "Y" : "N");
			dcMemoTransaction.setExternalAccount(selectedExternalAccount);
			dcMemoTransaction.setIsClientUnit(clientUnit ? "Y" : "N");
			dcMemoTransaction.setCategoryId(selectedAnalysisCategory.getCategoryId());
			dcMemoTransaction.setAnalysisCode(selectedAnalysisCode.getId().getAnalysisCode());
			
			if(! MALUtilities.isEmpty(dcMemoTransaction.getRentApplicableDate())){
				TimePeriod timePeriod = timePeriodDAO.findByStartDateAndContextId(dcMemoTransaction.getRentApplicableDate(), getLoggedInUser().getCorporateEntity().getCorpId());
				dcMemoTransaction.setTpSeqNoAr(timePeriod.getSequenceNo());				
			}else{
				dcMemoTransaction.setTpSeqNoAr(null);	
			}
			
			dcMemoTransaction.setSelectedApprover(selectedApproverName);
			
			
			if(isAddMode()){
				dcMemoTransaction.setDcNo(debitCreditMemoTransactionDAO.getNextPK());
				dcMemoTransaction.setLineDescription(dcMemoTransaction.getUnitNo() + "-" + dcMemoTransaction.getAnalysisCode()+ "-" + "DC " + dcMemoTransaction.getDcNo());
				dcMemoTransaction.setSubmitter(getLoggedInUser().getEmployeeNo());
				dcMemoTransaction.setSubmittedDate(new Date());
			}else{//edit
				if (paramDcNo == null){ //DC Upload Error screen transaction
					dcMemoTransaction.setDcNo(debitCreditMemoTransactionDAO.getNextPK());
					dcMemoTransaction.setLineDescription(dcMemoTransaction.getUnitNo() + "-" + dcMemoTransaction.getAnalysisCode()+ "-" + "DC " + dcMemoTransaction.getDcNo() + "-" + dcMemoTransaction.getLineDescription());
					dcMemoTransaction.setSubmittedDate(new Date());
					dcMemoTransaction.setStatus(DebitCreditStatusEnum.STATUS_SUBMITTED.getCode());
				}
				if( ! getSavedUnitNo().equalsIgnoreCase(dcMemoTransaction.getUnitNo())  
						|| ! getSavedAnalysisCode().equalsIgnoreCase(dcMemoTransaction.getAnalysisCode())){					
					dcMemoTransaction.setLineDescription(dcMemoTransaction.getUnitNo() + "-" + dcMemoTransaction.getAnalysisCode()+ "-" + "DC " + dcMemoTransaction.getDcNo());
				}	    	
	    		
				if(!isProcessMode()){
					//for generic user edit case with no special permission
					if(getLoggedInUser().getEmployeeNo().equals(dcMemoTransaction.getSubmitter()) 
							&& dcMemoTransaction.getStatus().equals(DebitCreditStatusEnum.STATUS_APPROVED.getCode())
							&& ! (this.hasAprrovePermission || this.hasProcessPermission )){						
							dcMemoTransaction.setApprover(null);
							dcMemoTransaction.setApprovedDate(null);
							dcMemoTransaction.setSubmittedDate(new Date());
							dcMemoTransaction.setStatus(DebitCreditStatusEnum.STATUS_SUBMITTED.getCode());
					}
				}
			}
			
			if(!isProcessMode()){			
				//if  an Approver or Processor add/edit own record as special permission
				if(getLoggedInUser().getEmployeeNo().equals(dcMemoTransaction.getSubmitter())) 
				{
					dcMemoTransaction.setApprover(null);
					dcMemoTransaction.setApprovedDate(null);
					dcMemoTransaction.setSubmittedDate(new Date());
					dcMemoTransaction.setStatus(DebitCreditStatusEnum.STATUS_SUBMITTED.getCode());
				}
			}
			
			dcMemoTransaction.setLastModifiedBy(getLoggedInUser().getEmployeeNo());
			dcMemoTransaction.setLastModifiedDate(new Date());
			dcMemoTransaction.setInvoiceNote(dcMemoTransaction.getInvoiceNote().replaceAll("\n", " ").replaceAll("\r", "").trim()); 
			dcMemoTransaction.setLineDescription(dcMemoTransaction.getLineDescription().replaceAll("\n", " ").replaceAll("\r", "").trim()); 
			dcMemoTransaction.setReason(dcMemoTransaction.getReason().replaceAll("\n", " ").replaceAll("\r", "").trim()); 			
			
			dcMemoTransaction.setWarningInd(warningIndicator ? "Y" : "N");
			
			debitCreditMemoService.saveOrUpdateDebitCreditMemo(dcMemoTransaction);
			debitCreditMemo.setDebitCreditMemoTransaction(debitCreditMemoService.getDebitCreditMemoTransaction(dcMemoTransaction.getDcNo()));
			setSavedUnitNo(dcMemoTransaction.getUnitNo());
			setSavedAnalysisCode(dcMemoTransaction.getAnalysisCode());		

    	} catch (Exception e) {
			super.addErrorMessage("custom.message", e.getMessage());
			logger.error(e, "In save transaction method");
		}
    	
    	return true;
    }
	
    @SuppressWarnings("deprecation")
	private boolean isValidForSave(){   
    	boolean isValid = true;
    	isDuplicate = false;

    	if (MALUtilities.isEmpty(selectedMemoType)){
    		super.addErrorMessage(MEMO_TYPE, "required.field", "Type");
    		isValid = false;
    	} /*else {
    		if (selectedMemoType.getDebitCreditType().equals(MEMO_TYPE_FULL_CREDIT)){
    			if (MALUtilities.isEmpty(debitCreditMemo.getDebitCreditMemoTransaction().getTaxAmount())){
    	    		super.addErrorMessage(TAX_AMOUNT, "required.field", "Tax Amount");
    	    		isValid = false;
    			}	
    		}
    	}*/
    	
    	if(MALUtilities.isEmpty(debitCreditMemo.getDebitCreditMemoTransaction().getReason())){
    		super.addErrorMessage(REASON, "required.field", "Reason");
			isValid = false;
    	}
    	
		if(MALUtilities.isEmpty(getCustomerLOVParam())){
			super.addErrorMessage(CLIENT_ACCOUNT_UI_ID, "required.field", "Client");	
			isValid = false;				
		}    	
    	
    	if (MALUtilities.isEmpty(debitCreditMemo.getDebitCreditMemoTransaction().getUnitNo())){
    		super.addErrorMessage(UNIT_NO, "required.field", "Unit Number");
    		isValid = false;
    	} else {
			FleetMaster fleetMaster = fleetMasterService.findByUnitNo(debitCreditMemo.getDebitCreditMemoTransaction().getUnitNo());
			if (fleetMaster == null) {
				super.addErrorMessage(UNIT_NO, "custom.message", "Unit Number not found");
				isValid = false;
			} 
    	}
			
		if(clientUnit && !isCustomerUnit()) {
			super.addErrorMessage(UNIT_NO, "custom.message", "Unit does not belong to Client");
			isValid = false;
		}

		if(!isCustomerValid()) {
			super.addErrorMessage(CLIENT_ACCOUNT_UI_ID, "custom.message", "Invalid Account Code.");
			isValid = false;
		}
		
		if(MALUtilities.isEmpty(getSelectedAnalysisCategory())){
			super.addErrorMessage(ANALYSIS_CATEGORY, "required.field", "Analysis Category");	
			isValid = false;				
		} 		
		
		if(MALUtilities.isEmpty(getSelectedAnalysisCode())){
			super.addErrorMessage(ANALYSIS_CODE, "required.field", "Analysis Code");	
			isValid = false;				
		}		
		
/*		if(taxOnly){
			if(MALUtilities.isEmpty(debitCreditMemo.getDebitCreditMemoTransaction().getTaxAmount()) && !selectedMemoType.getDebitCreditType().equals(MEMO_TYPE_FULL_CREDIT)){
	    		super.addErrorMessage(TAX_AMOUNT, "required.field", "Tax Amount");
	    		isValid = false;
			}
		} else {*/
			if(MALUtilities.isEmpty(debitCreditMemo.getDebitCreditMemoTransaction().getNetAmount())){
	    		super.addErrorMessage(NET_AMOUNT, "required.field", "Net Amount");
	    		isValid = false;
			}
		//}
    		
		if (MALUtilities.isEmpty(debitCreditMemo.getDebitCreditMemoTransaction().getTransactionDate())){
    		super.addErrorMessage(TRANSACTION_DATE, "required.field", "Transaction Date");
    		isValid = false;
		}  			
			
		if(!MALUtilities.isEmpty(getSelectedAnalysisCategory())){
			if(selectedAnalysisCategory.getAnalysisCategory().equals(ANALYSIS_CATEGORY_FLBILLING) || selectedAnalysisCategory.getAnalysisCategory().equals(ANALYSIS_CATEGORY_FLINFORMAL)){
				if(MALUtilities.isEmpty(debitCreditMemo.getDebitCreditMemoTransaction().getRentApplicableDate())){
					super.addErrorMessage(RENT_APPLICABLE_DATE, "required.field", "Rent Applicable Date");	
					isValid = false;				
				}
			}
		}
		
		if(!MALUtilities.isEmpty(debitCreditMemo.getDebitCreditMemoTransaction().getRentApplicableDate())){
			if(debitCreditMemo.getDebitCreditMemoTransaction().getRentApplicableDate().getDate() != 1){
				super.addErrorMessage(RENT_APPLICABLE_DATE, "custom.message", "Rent Applicable Date must be 1st day of the month");	
				isValid = false;					
			}
		}
		
		if(!MALUtilities.isEmpty(debitCreditMemo.getDebitCreditMemoTransaction().getGlCode())){
			if(!validateGlCode()){
				isValid = false;
			}
		}		
		
		if(MALUtilities.isEmpty(getSelectedApproverName())){
			if(!hasPermission("processDebitCreditMemo") && !hasPermission("aprroveDebitCreditMemo")){
				super.addErrorMessage(APPROVER, "required.field", "Approver");	
				isValid = false;	
			}
		}
		
		if(MALUtilities.isNotEmptyString(debitCreditMemo.getDebitCreditMemoTransaction().getInvoiceNo())){
			boolean isInvoiceNoIsValid =  debitCreditMemoService.isInvoiceNoIsValid(debitCreditMemo.getDebitCreditMemoTransaction().getInvoiceNo(), customerLOVParam);
			if( ! isInvoiceNoIsValid){
				super.addErrorMessage(INVOICE_NO,  "custom.message", "Invoice no is not valid for client and debit/credit memo doc types");
				isValid = false;
			}
		}
		
		if(! MALUtilities.isEmpty(debitCreditMemo.getDebitCreditMemoTransaction().getRentApplicableDate())){
			TimePeriod timePeriod = timePeriodDAO.findByStartDateAndContextId(debitCreditMemo.getDebitCreditMemoTransaction().getRentApplicableDate(), getLoggedInUser().getCorporateEntity().getCorpId());
			if(timePeriod == null){
				super.addErrorMessage(RENT_APPLICABLE_DATE, "custom.message", "Rent Applicable Date is not valid for accounting period");
				isValid = false;
			}
		}		
		
		if(isValid){
			isDuplicate = debitCreditMemoService.isDuplicateTransaction(debitCreditMemo.getDebitCreditMemoTransaction().getDcNo(), debitCreditMemo.getDebitCreditMemoTransaction().getUnitNo(), selectedAnalysisCategory.getCategoryId(), selectedAnalysisCode.getId().getAnalysisCode(), 
					debitCreditMemo.getDebitCreditMemoTransaction().getNetAmount(), debitCreditMemo.getDebitCreditMemoTransaction().getRentApplicableDate(), new ExternalAccountPK(CorporateEntity.MAL.getCorpId(),  ACCOUNT_TYPE_CUSTOMER,  customerLOVParam), selectedMemoType.getDcmtId());
			
			if (isDuplicate){
				warningIndicator = true;
				FacesContext context = FacesContext.getCurrentInstance();
			    context.addMessage("warnings", new FacesMessage(FacesMessage.SEVERITY_WARN, "Duplicate Entry", "A duplicate entry already exists for the same billing period."));
			    RequestContext.getCurrentInstance().update(GROWL_UI_ID);
			}
			
			driverAllocation = driverAllocationService.getDriverAllocationByDate(debitCreditMemo.getDebitCreditMemoTransaction().getUnitNo(), 
																					debitCreditMemo.getDebitCreditMemoTransaction().getTransactionDate());
   			if(driverAllocation == null) {
   				warningIndicator = true;
   				FacesContext context = FacesContext.getCurrentInstance();
   			    context.addMessage("warnings", new FacesMessage(FacesMessage.SEVERITY_WARN, "Driver not assigned to unit", "There was no driver allocated to unit #" + debitCreditMemo.getDebitCreditMemoTransaction().getUnitNo() + " for the selected transaction date."));
   			    RequestContext.getCurrentInstance().update(GROWL_UI_ID);
   			}
		}
		
    	return isValid;
    }

    private boolean validateGlCode(){
    	boolean isValidGlCode = true;

		try {
			GlCodePK glPk = new GlCodePK();
			glPk.setCode(debitCreditMemo.getDebitCreditMemoTransaction().getGlCode());
			glPk.setCorpId(CorporateEntity.MAL.getCorpId());
			GlCode gl = glCodeDao.findById(glPk).orElse(null);			
			if(gl == null){ 
				super.addErrorMessage(GL_CODE, "decode.noMatchFound.msg", "GL Code: " + debitCreditMemo.getDebitCreditMemoTransaction().getGlCode());
				isValidGlCode = false;
			}			
		}catch(Exception ex){
			super.addErrorMessage("generic.error", ex.getMessage());
			isValidGlCode = false;
		}
		
		return isValidGlCode;
    }
   
    /*
     * Defined as currently on contract, on order, or was ever on contract for the specified account (RE-649)
     */
    public boolean isCustomerUnit(){
    	boolean isCustomerUnit = false;
    	
    	if (debitCreditMemo.getDebitCreditMemoTransaction().getUnitNo().equals(willowConfigService.getConfigValue("GENERIC_UNIT"))){
    		isCustomerUnit = true; //RE-938 accept unit #99999999 for any client
    		return isCustomerUnit;
    	}
    	
    	FleetMaster fleetMaster = fleetMasterService.findByUnitNo(debitCreditMemo.getDebitCreditMemoTransaction().getUnitNo());
    	if(fleetMaster != null) {
    		List<Contract> contracts = new ArrayList<Contract>();
    		
    		try {
	    		contracts = contractService.getContracts(fleetMaster);
	    		if(contracts != null && contracts.size() > 0){
		    		for(Contract c : contracts){	
		    			if(getCustomerLOVParam().equals(c.getExternalAccount().getExternalAccountPK().getAccountCode())){
		    				isCustomerUnit = true;
		    			}
		    		}
	    		} else {
	    			super.addErrorMessage(UNIT_NO, "custom.message", "Contract not found");
	    		}
    		}catch(Exception ex){
    			super.addErrorMessage("generic.error", ex.getMessage());
    		}	    		
    	}
		
    	return isCustomerUnit;
    }
    
    public boolean isCustomerValid(){
    	boolean isCustomerValid = false;
    	
    	try {
	    		ExternalAccount account = externalAccountDAO.findByAccountCodeAndAccountTypeAndCId(getCustomerLOVParam(),ACCOUNT_TYPE_CUSTOMER, CorporateEntity.MAL.getCorpId());
	    		if(!MALUtilities.isEmpty(account)){
	    			isCustomerValid = true;
	    		}
    		}catch(Exception ex){
    			super.addErrorMessage("generic.error", ex.getMessage());
    		}	    		
    	return isCustomerValid;
    } 
    
	public void decodeAccountByNameOrCode(){
		
		String paramName = (String) getRequestParameter("CUSTOMER_LOV_INPUT");//This code needed when users select through LOV and need refresh
		if (!MALUtilities.isEmptyString(paramName)) {
			customerLOVParam = (String) getRequestParameter(paramName);
		}
		
		 List<ExternalAccount>  externalAccountList =  externalAccountService.findOpenCustomerAccountsByNameOrCode(customerLOVParam, getLoggedInUser().getCorporateEntity(),new PageRequest(0,50));
		 if(externalAccountList.size() == 1){					 
			
			 selectedExternalAccount  =  externalAccountList.get(0);
			 customerLOVParam = selectedExternalAccount.getExternalAccountPK().getAccountCode();
			 
		 } else {
			 if(externalAccountList.size() == 0){
				 super.addErrorMessage(CLIENT_ACCOUNT_UI_ID, "decode.noMatchFound.msg","Client " + customerLOVParam); 
			 } else{
				 super.addErrorMessage(CLIENT_ACCOUNT_UI_ID, "decode.notExactMatch.msg","Client " +  customerLOVParam);			
			 }
			 this.setSelectedExternalAccount(new ExternalAccount());
			 this.selectedExternalAccount.setExternalAccountPK(new ExternalAccountPK());
		 }
	}
	
	public void handleMemoTypeSelect(){
		/*if((!MALUtilities.isEmpty(selectedMemoType) && selectedMemoType.getDebitCreditType().equals(MEMO_TYPE_FULL_CREDIT))){  //|| taxOnly){
			enableTaxAmount = true;
		}else{
			enableTaxAmount	= false;
			debitCreditMemo.getDebitCreditMemoTransaction().setTaxAmount(null);
		}*/   //Shafi as per code review comments
		
		updateCreditDisplayFlag();
		updateTotalAmountOutput();
	}	
	
	private void updateCreditDisplayFlag(){
		this.creditDisplay = false;
		if(!MALUtilities.isEmpty(selectedMemoType)){
			if(selectedMemoType.getDebitCreditType().equals(MEMO_TYPE_FULL_CREDIT) || selectedMemoType.getDebitCreditType().equals(MEMO_TYPE_PARTIAL_CREDIT)){
				creditDisplay = true;
			}
			/*if((selectedMemoType.getDebitCreditType().equals(MEMO_TYPE_FULL_CREDIT))){  
				enableTaxAmount = true;
			}*/ //Shafi as per code review comments
		}
	}
	
/*	public void handleTaxOnlySelect(){
		if(taxOnly || (!MALUtilities.isEmpty(selectedMemoType) && selectedMemoType.getDebitCreditType().equals(MEMO_TYPE_FULL_CREDIT))){
			enableTaxAmount = true;
		}else{
			enableTaxAmount	= false;
			debitCreditMemo.getDebitCreditMemoTransaction().setTaxAmount(null);
		}
		
		if(taxOnly){
			debitCreditMemo.getDebitCreditMemoTransaction().setNetAmount(null);
		}
		updateTotalAmountOutput();
	}*/		
	
	private void updateTotalAmountOutput(){
		BigDecimal net = debitCreditMemo.getDebitCreditMemoTransaction().getNetAmount() != null ? debitCreditMemo.getDebitCreditMemoTransaction().getNetAmount() : BigDecimal.ZERO;
		BigDecimal tax = debitCreditMemo.getDebitCreditMemoTransaction().getTaxAmount() != null ? debitCreditMemo.getDebitCreditMemoTransaction().getTaxAmount() : BigDecimal.ZERO;
		
		debitCreditMemo.getDebitCreditMemoTransaction().setTotalAmount(net.add(tax));		
	}
	
	public void handleAnalysisCategorySelect(){
		if(getDebitCreditMemo().getAnalysisCategories() != null){
			populateAnalysisCodes();
		}
		
		if(!MALUtilities.isEmpty(getSelectedAnalysisCategory())){
			if (!(selectedAnalysisCategory.getAnalysisCategory().equals(ANALYSIS_CATEGORY_FLBILLING) || selectedAnalysisCategory.getAnalysisCategory().equals(ANALYSIS_CATEGORY_FLINFORMAL))){ 
		    	getDebitCreditMemo().getDebitCreditMemoTransaction().setRentApplicableDate(null);
		    	getDebitCreditMemo().getDebitCreditMemoTransaction().setTpSeqNoAr(null);				
			}
		}
		
	}
	
	private void populateAnalysisCodes() {
		if (getDebitCreditMemo().getAnalysisCodes() != null) {
			getDebitCreditMemo().setAnalysisCodes(null);
			setSelectedAnalysisCode(null);
		}
		
		loadAnalysisCode();
	}
    
    public String cancel(){    	
    	return super.cancelPage();  
    }	
	
	public String delete() {
		try {
			if (paramDcNo != null){
				debitCreditMemoService.deleteDebitCreditMemoTransaction(debitCreditMemo.getDebitCreditMemoTransaction());
				super.addSuccessMessage("deleted.success", "DC# " + debitCreditMemo.getDebitCreditMemoTransaction().getDcNo());				
			} else {
				deleteFromErrorQueue();
				super.addSuccessMessage("custom.message","Selected record has been successfully deleted.");					
			}
		} catch (Exception ex) {
			super.addErrorMessage("generic.error", ex.getMessage());
		}
		
		return cancel();
	}
	
	public void deleteFromErrorQueue() {
		try {
			List<String> jmsIdList = new ArrayList<String>();
			jmsIdList.add(jmsMessageId);
			debitCreditMemoService.removeDebitCreditErrorsFromQueue(jmsIdList,debitCreditProcessingBaseURL+ "/businessErrors");
		} catch (Exception ex) {
			super.addErrorMessage("generic.error", ex.getMessage());
		}
	}
    
	private void populateStatus() {
		DebitCreditStatusCode code = debitCreditStatusCodeDAO.findById(debitCreditMemo.getDebitCreditMemoTransaction().getStatus()).orElse(null);
		if(code != null) {
			status = code.getStatusDesc();
		}
				
	}
	
	@Override
	protected void loadNewPage() {
		setSelectedExternalAccount(new ExternalAccount());
		getSelectedExternalAccount().setExternalAccountPK(new ExternalAccountPK());	
		paramDcNo = (Long)super.thisPage.getInputValues().get(ViewConstants.VIEW_PARAM_DC_MEMO_DC_NO);
		jmsMessageId = (String) super.thisPage.getInputValues().get(ViewConstants.VIEW_PARAM_DC_MEMO_ERROR_JMS_ID);
		String viewMode = (String)super.thisPage.getInputValues().get(ViewConstants.VIEW_MODE);
		if(viewMode.equalsIgnoreCase(ViewConstants.VIEW_MODE_ADD)){	
			setAddMode(true);
			thisPage.setPageDisplayName(ViewConstants.DISPLAY_NAME_ADD_DEBIT_CREDIT_MEMO);
		}else{
			setAddMode(false);
			if (paramDcNo != null){
				thisPage.setPageDisplayName(ViewConstants.DISPLAY_NAME_EDIT_DEBIT_CREDIT_MEMO);
			} else {
				thisPage.setPageDisplayName(ViewConstants.DISPLAY_NAME_EDIT_DEBIT_CREDIT_BUSINESS_ERROR);
			}
		}
	}

	@Override
	protected void restoreOldPage() {}
		
	public DebitCreditMemoType getSelectedMemoType() {
		return selectedMemoType;
	}

	public void setSelectedMemoType(DebitCreditMemoType selectedMemoType) {
		this.selectedMemoType = selectedMemoType;
	}

	public String getCustomerLOVParam() {
		return customerLOVParam;
	}

	public void setCustomerLOVParam(String customerLOVParam) {
		this.customerLOVParam = customerLOVParam;
	}

	public ExternalAccount getSelectedExternalAccount() {
		return selectedExternalAccount;
	}

	public void setSelectedExternalAccount(ExternalAccount selectedExternalAccount) {
		this.selectedExternalAccount = selectedExternalAccount;
	}

	public boolean isClientUnit() {
		return clientUnit;
	}

	public void setClientUnit(boolean clientUnit) {
		this.clientUnit = clientUnit;
	}

//	public boolean isTaxOnly() {
//		return taxOnly;
//	}

//	public void setTaxOnly(boolean taxOnly) {
//		this.taxOnly = taxOnly;
//	}

	public AnalysisCategory getSelectedAnalysisCategory() {
		return selectedAnalysisCategory;
	}

	public void setSelectedAnalysisCategory(AnalysisCategory selectedAnalysisCategory) {
		this.selectedAnalysisCategory = selectedAnalysisCategory;
	}

	public AnalysisCode getSelectedAnalysisCode() {
		return selectedAnalysisCode;
	}

	public void setSelectedAnalysisCode(AnalysisCode selectedAnalysisCode) {
		this.selectedAnalysisCode = selectedAnalysisCode;
	}

/*	public boolean isEnableTaxAmount() {
		return enableTaxAmount;
	}

	public void setEnableTaxAmount(boolean enableTaxAmount) {
		this.enableTaxAmount = enableTaxAmount;
	}*/

	public boolean isCreditDisplay() {
		return creditDisplay;
	}

	public void setCreditDisplay(boolean creditDisplay) {
		this.creditDisplay = creditDisplay;
	}

	public String getSelectedApproverName() {
		return selectedApproverName;
	}

	public void setSelectedApproverName(String selectedApproverName) {
		this.selectedApproverName = selectedApproverName;
	}

	public DebitCreditMemoVO getDebitCreditMemo() {
		return debitCreditMemo;
	}

	public void setDebitCreditMemo(DebitCreditMemoVO debitCreditMemo) {
		this.debitCreditMemo = debitCreditMemo;
	}

	public String getCustomerAccountType() {
		return customerAccountType;
	}

	public void setCustomerAccountType(String customerAccountType) {
		this.customerAccountType = customerAccountType;
	}

	public String getCustomerAccountCid() {
		return customerAccountCid;
	}

	public void setCustomerAccountCid(String customerAccountCid) {
		this.customerAccountCid = customerAccountCid;
	}

	public boolean isShowDeleteButton() {
		return showDeleteButton;
	}

	public void setShowDeleteButton(boolean showDeleteButton) {
		this.showDeleteButton = showDeleteButton;
	}

	public boolean isAddMode() {
		return isAddMode;
	}

	public void setAddMode(boolean isAddMode) {
		this.isAddMode = isAddMode;
	}	

	public boolean isHasGeneralEditPermission() {
		return hasGeneralEditPermission;
	}

	public void setHasGeneralEditPermission(boolean hasGeneralEditPermission) {
		this.hasGeneralEditPermission = hasGeneralEditPermission;
	}

	public boolean isHasApproverEditPermission() {
		return hasApproverEditPermission;
	}

	public void setHasApproverEditPermission(boolean hasApproverEditPermission) {
		this.hasApproverEditPermission = hasApproverEditPermission;
	}

	public boolean isHasProcessorEditPermission() {
		return hasProcessorEditPermission;
	}

	public void setHasProcessorEditPermission(boolean hasProcessorEditPermission) {
		this.hasProcessorEditPermission = hasProcessorEditPermission;
	}

	public boolean isShowSaveButton() {
		return showSaveButton;
	}

	public void setShowSaveButton(boolean showSaveButton) {
		this.showSaveButton = showSaveButton;
	}

	public String getSavedUnitNo() {
		return savedUnitNo;
	}

	public void setSavedUnitNo(String savedUnitNo) {
		this.savedUnitNo = savedUnitNo;
	}

	public String getSavedAnalysisCode() {
		return savedAnalysisCode;
	}

	public void setSavedAnalysisCode(String savedAnalysisCode) {
		this.savedAnalysisCode = savedAnalysisCode;
	}

	public boolean isEnableProcessBtn() {
		return enableProcessBtn;
	}

	public void setEnableProcessBtn(boolean enableProcessBtn) {
		this.enableProcessBtn = enableProcessBtn;
	}

	public boolean isProcessMode() {
		return isProcessMode;
	}

	public void setProcessMode(boolean isProcessMode) {
		this.isProcessMode = isProcessMode;
	}
	
    public String getJmsMessageId() {
		return jmsMessageId;
	}

	public void setJmsMessageId(String jmsMessageId) {
		this.jmsMessageId = jmsMessageId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public boolean isWarningIndicator() {
		return warningIndicator;
	}

	public void setWarningIndicator(boolean warningIndicator) {
		this.warningIndicator = warningIndicator;
	}

	public DriverAllocation getDriverAllocation() {
		return driverAllocation;
	}

	public void setDriverAllocation(DriverAllocation driverAllocation) {
		this.driverAllocation = driverAllocation;
	}	

}
