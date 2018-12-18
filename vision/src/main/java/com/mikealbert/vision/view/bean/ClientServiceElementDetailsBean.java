package com.mikealbert.vision.view.bean;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.primefaces.context.RequestContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.mikealbert.data.entity.ClientServiceElement;
import com.mikealbert.data.entity.ExternalAccount;
import com.mikealbert.data.entity.ExternalAccountPK;
import com.mikealbert.data.entity.LeaseElement;
import com.mikealbert.data.entity.Product;
import com.mikealbert.data.entity.QuotationProfile;
import com.mikealbert.data.enumeration.ClientServiceElementTypeCodes;
import com.mikealbert.data.enumeration.CorporateEntity;
import com.mikealbert.data.vo.ClientServiceElementParameterVO;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.service.CustomerAccountService;
import com.mikealbert.service.QuotationProfileService;
import com.mikealbert.service.ServiceElementService;
import com.mikealbert.util.MALUtilities;
import com.mikealbert.vision.view.ViewConstants;
import com.mikealbert.vision.view.link.EditViewClientLink;

@Component
@Scope("view")
public class ClientServiceElementDetailsBean extends StatefulBaseBean implements EditViewClientLink{
	private static final long serialVersionUID = 617858233844693478L;
	
	@Resource QuotationProfileService quotationProfileService;
	@Resource ServiceElementService serviceElementService;
	@Resource CustomerAccountService customerAccountService;
	
	private String accountCode;
	private String accountType;
	private Long accountCId;
	private ExternalAccount clientAccount;
	private int DEFAULT_DATATABLE_HEIGHT = 150;
	private List<QuotationProfile> quotationProfileList;
	private ExternalAccount rootParentAccount;
	private List<ClientServiceElement> clientServiceElementList;
	private List<ClientServiceElement> clientServiceElementHistoryList;
	private List<ClientServiceElementParameterVO> serviceElementParameterList;
	private LeaseElement selectedLeaseElement;
	private Boolean validationSuccess;
	private String CLIENT_VALUE = "clientValue";
	private BigDecimal defaultValueTotal;
	private BigDecimal clientValueTotal;
	private BigDecimal productValueTotal;
	private String selectedBillingOption;
	private Long selectedClientServiceElementId;
	private String selectedProduct;
	private boolean productOverride;
	private List<ClientServiceElement> productExclusionsList;
	private List<Product> activeClientProductList;
	
	@PostConstruct
	public void init() {
    	initializeDataTable(500, 500, new int[] {10, 10, 10, 10, 10, 10}).setHeight(DEFAULT_DATATABLE_HEIGHT);
    	super.openPage();
    	loadData();
	}
	
    public void initDialog(String billingOption, Long clientServiceElementId){
    	try {		
    		if(billingOption != null){
    			setSelectedBillingOption(billingOption);
    			setSelectedClientServiceElementId(clientServiceElementId);
    		}
		} catch(Exception e) {
			super.addErrorMessage("generic.error", e.getMessage());
		}  
    }	
	
    public void initProductOverrideDialog(Long clientServiceElementId){
    	try {		
   			setSelectedClientServiceElementId(clientServiceElementId);
		} catch(Exception e) {
			super.addErrorMessage("generic.error", e.getMessage());
		}  
    }    
    
	public void loadData() {
		try {
			setClientAccount(customerAccountService.getOpenCustomerAccountByCode(getClientAccount().getExternalAccountPK().getAccountCode()));
			setRootParentAccount(serviceElementService.getRootParentAccount(clientAccount.getExternalAccountPK().getCId(), clientAccount.getExternalAccountPK().getAccountType(), clientAccount.getExternalAccountPK().getAccountCode()));
			setClientServiceElementList(serviceElementService.getClientServiceElementsByClient(clientAccount.getExternalAccountPK().getCId(), clientAccount.getExternalAccountPK().getAccountType(), clientAccount.getExternalAccountPK().getAccountCode()));
			setClientServiceElementHistoryList(serviceElementService.getClientServiceElementsHistoryByClient(clientAccount.getExternalAccountPK().getCId(), clientAccount.getExternalAccountPK().getAccountType(), clientAccount.getExternalAccountPK().getAccountCode(), ClientServiceElementTypeCodes.ACCOUNT.getId()));
			setProductExclusionsList(serviceElementService.getProductServiceElementsByAccountIncludeRemoved(clientAccount.getExternalAccountPK().getCId(), clientAccount.getExternalAccountPK().getAccountType(), clientAccount.getExternalAccountPK().getAccountCode()));
			setActiveClientProductList(serviceElementService.findActiveClientProductList());
		} catch (Exception e) {
			logger.error(e);
			 if(e  instanceof MalBusinessException){				 
				 super.addErrorMessage(e.getMessage()); 
			 }else{
				 super.addErrorMessage("generic.error.occured.while", " building screen.");
			 }
		}
	}
	
	public void populateQuoteProfile(Long leaseElementId) {
		for (ClientServiceElement cse : getClientServiceElementList()) {
			if (cse.getClientContractServiceElement().getLeaseElement().getLelId() == leaseElementId) {
				setSelectedLeaseElement(cse.getClientContractServiceElement().getLeaseElement());
				break;
			}
		}
		
		quotationProfileList = quotationProfileService.getQuotationProfilesByLeaseElement(leaseElementId, clientAccount);		
	}
	
	public void populateElementValues(Long leaseElementId, Long clientServiceElementId, String productCode) {
		if (!MALUtilities.isEmpty(productCode)) {
			setProductOverride(true);
			setServiceElementParameterList(serviceElementService.getProductServiceElementParametersByClientServiceElement(leaseElementId, clientServiceElementId));			
		} else {
			setProductOverride(false);
			setServiceElementParameterList(serviceElementService.getServiceElementParametersByClientServiceElement(leaseElementId, clientServiceElementId));			
		}
		adjustTotal();
	}
	
	public BigDecimal getDefaultValues(Long leaseElementId, Long clientServiceElementId) {
		setDefaultValueTotal(serviceElementService.getParameterDefaultValuesSum(leaseElementId, clientServiceElementId));
		return getDefaultValueTotal();
	}
	
	public BigDecimal getClientValues(Long clientServiceElementId) {
		setClientValueTotal(serviceElementService.getParameterClientValuesSum(clientServiceElementId));
		return getClientValueTotal();
	}
	
	public BigDecimal getClientValues(Long leaseElementId, Long clientServiceElementId) {
		for (ClientServiceElement cse : clientServiceElementList){
			if (cse.getClientContractServiceElement().getLeaseElement().getLelId() == leaseElementId){
				setClientValueTotal(getClientValues(cse.getClientServiceElementId()));
				break;
			}
			else {
				setClientValueTotal(null);
			}
		}
		return getClientValueTotal();
	}	
	
	public BigDecimal getProductValues(Long clientServiceElementId) {
		setProductValueTotal(serviceElementService.getParameterClientValuesSum(clientServiceElementId));
		return getProductValueTotal();
	}
	
	public void adjustTotal() {
		setDefaultValueTotal(new BigDecimal(0));
		setClientValueTotal(new BigDecimal(0));
		setProductValueTotal(new BigDecimal(0));
		for (ClientServiceElementParameterVO csepVO : getServiceElementParameterList()) {
			if (!MALUtilities.isEmpty(csepVO.getClientValue())) {
				if (isProductOverride()){
					setClientValueTotal(getClientValueTotal().add(csepVO.getClientValue()));
				} else {
					setClientValueTotal(getClientValueTotal().add(csepVO.getClientValue().setScale(5, RoundingMode.HALF_DOWN)).setScale(5, RoundingMode.HALF_UP));
				}
			}
			if (!MALUtilities.isEmpty(csepVO.getDefaultValue())) {
				setDefaultValueTotal(getDefaultValueTotal().add(csepVO.getDefaultValue()));
			}
			if (!MALUtilities.isEmpty(csepVO.getProductValue())) {
				setProductValueTotal(getProductValueTotal().add(csepVO.getProductValue().setScale(5, RoundingMode.HALF_DOWN)).setScale(5, RoundingMode.HALF_UP));
			}			
		}
	}
	
	public Boolean quoteProfileExists(Long leaseElementId) {
		if (quotationProfileService.countQuotationProfilesByLeaseElement(leaseElementId, clientAccount) > 0) {
			return true;
		} else
			return false;
	}
	
	public String save() {
		
		validateServiceElementValues();
		
		if (getValidationSuccess()) {
			try{
				serviceElementService.saveOrUpdateClientServiceElementParameters(getServiceElementParameterList(),isProductOverride());
				super.addSuccessMessage("process.success","Save Client Service Element Parameters");
			} catch(Exception ex){
				logger.error(ex);		
				handleException("generic.error",new String[]{ex.getMessage()}, ex, null);
			}
		} else {
			RequestContext context = RequestContext.getCurrentInstance();
			context.addCallbackParam("failure", true);
		}
		
		return null;
	}
	
	public String changeClientServiceElement(){
		try{
			List<ClientServiceElement> cseList = new ArrayList<ClientServiceElement>();
			ClientServiceElement clientElement = serviceElementService.getClientServiceElement(this.selectedClientServiceElementId);
			clientElement.setBillingOption(this.selectedBillingOption);
			cseList.add(clientElement);
			serviceElementService.saveUpdateClientServiceElements(cseList, ClientServiceElementTypeCodes.ACCOUNT.getCode());	
			loadData();
			RequestContext.getCurrentInstance().update("clientServiceElementDetailsForm");
			super.addSuccessMessage("process.success","Change Billing Options");
		}
		catch(Exception ex){
			logger.error(ex);		
			super.addErrorMessage("generic.error", ex.getMessage());
		}
		
		return null;
	}	
	
	//product exclusion
    public String removeProductServiceElement(){
    	try {	
   			ClientServiceElement clientElement = serviceElementService.getClientServiceElement(this.selectedClientServiceElementId);
   			ClientServiceElement productClientElement = serviceElementService.constructClientServiceElement(clientElement.getClientContractServiceElement(),clientElement.getBillingOption(),CorporateEntity.fromCorpId(getAccountCId()),clientElement.getExternalAccount().getExternalAccountPK().getAccountCode(),null);
   			
   			//Check if product override already exists for this client and lease element
   			ClientServiceElement ce = serviceElementService.getClientProductServiceElementByGradeGroupAndElementWRemoved(productClientElement.getExternalAccount().getExternalAccountPK().getCId(), productClientElement.getExternalAccount().getExternalAccountPK().getAccountType(), productClientElement.getExternalAccount().getExternalAccountPK().getAccountCode(), productClientElement.getClientContractServiceElement().getLeaseElement().getLelId(), selectedProduct);
			if (MALUtilities.isEmpty(ce)){
				serviceElementService.removeProductClientServiceElement(productClientElement, selectedProduct, ClientServiceElementTypeCodes.ACCOUNT.getCode());
				loadData();
				super.addSuccessMessage("process.success"," Client Service Element at Product Level ");
			}
		} catch(Exception e) {
			logger.error(e);
			super.addErrorMessage("generic.error", e.getMessage());
		}
    	
    	return null;
    }
    
    //remove product exclusion
    public String deleteProductRemoval(Long productRemovalClientServiceElementId) {
    	try {	
   			ClientServiceElement clientElement = serviceElementService.getClientServiceElement(productRemovalClientServiceElementId);
			serviceElementService.deleteProductRemovalServiceElement(clientElement, ClientServiceElementTypeCodes.ACCOUNT.getCode());
			loadData();
			super.addSuccessMessage("process.success","Remove Product Removal Service Element ");
		} catch(Exception e) {
			logger.error(e);
			super.addErrorMessage("generic.error", e.getMessage());
		}
    	
    	return null;
    }
		
	
	public void validateServiceElementValues() {
		setValidationSuccess(true);
//SS-254 Mockup		
		//super.addErrorMessageSummary(CLIENT_VALUE, "custom.message", "Client Value is beyond user's authorization limits ");
	}
	
	/*
	 * This is needed for commandLink navigation from the Client Finance page with
	 * one account, to the Client Finance page with another account
	 * */	
	@Override	
	public void editViewClient(ExternalAccount account){
		this.setAccountCId(account.getExternalAccountPK().getCId());
		this.setAccountType(account.getExternalAccountPK().getAccountType());
		this.setAccountCode(account.getExternalAccountPK().getAccountCode());	
		setClientAccount(new ExternalAccount());
		getClientAccount().setExternalAccountPK(new ExternalAccountPK());
		this.getClientAccount().getExternalAccountPK().setCId(this.getAccountCId());
		this.getClientAccount().getExternalAccountPK().setAccountType(this.getAccountType());
		this.getClientAccount().getExternalAccountPK().setAccountCode(this.getAccountCode());		
		loadData();
	}

	@Override
	protected void loadNewPage() {
		Map<String, Object> map = super.thisPage.getInputValues();
		thisPage.setPageUrl(ViewConstants.CLIENT_SERVICE_ELEMENT_DETAILS);
		thisPage.setPageDisplayName(ViewConstants.DISPLAY_NAME_CLIENT_SERVICE_ELEMENT_DETAILS);
		this.setAccountCId((Long) map.get(ViewConstants.VIEW_PARAM_C_ID));
		this.setAccountType((String) map.get(ViewConstants.VIEW_PARAM_ACCOUNT_TYPE));
		this.setAccountCode((String) map.get(ViewConstants.VIEW_PARAM_ACCOUNT_CODE));
		setClientAccount(new ExternalAccount());
		getClientAccount().setExternalAccountPK(new ExternalAccountPK());
		this.getClientAccount().getExternalAccountPK().setCId(this.getAccountCId());
		this.getClientAccount().getExternalAccountPK().setAccountType(this.getAccountType());
		this.getClientAccount().getExternalAccountPK().setAccountCode(this.getAccountCode());
	}

	@Override
	protected void restoreOldPage() {
		logger.info("-- Method name: restoreOldPage start");
		Map<String, Object> map = super.thisPage.getRestoreStateValues();
		setAccountCId((Long) map.get(ViewConstants.VIEW_PARAM_C_ID));
		setAccountType((String) map.get(ViewConstants.VIEW_PARAM_ACCOUNT_TYPE));
		setAccountCode((String) map.get(ViewConstants.VIEW_PARAM_ACCOUNT_CODE));
		setClientAccount(new ExternalAccount());
		getClientAccount().setExternalAccountPK(new ExternalAccountPK());
		this.getClientAccount().getExternalAccountPK().setCId(this.getAccountCId());
		this.getClientAccount().getExternalAccountPK().setAccountType(this.getAccountType());
		this.getClientAccount().getExternalAccountPK().setAccountCode(this.getAccountCode());		
		logger.info("-- Method name: restoreOldPage end");
	}
	
	public void viewEditClientAgreements() {
		logger.info("-- Method name: viewEditClientAgreements start");
		
		Map<String, Object> restoreStateValues = new HashMap<String, Object>();
		restoreStateValues.put(ViewConstants.VIEW_PARAM_C_ID, clientAccount.getExternalAccountPK().getCId());
		restoreStateValues.put(ViewConstants.VIEW_PARAM_ACCOUNT_TYPE, clientAccount.getExternalAccountPK().getAccountType());
		restoreStateValues.put(ViewConstants.VIEW_PARAM_ACCOUNT_CODE, clientAccount.getExternalAccountPK().getAccountCode());
		saveRestoreStateValues(restoreStateValues);
		
		Map<String, Object> nextPageValues = new HashMap<String, Object>();
		nextPageValues.put(ViewConstants.VIEW_PARAM_C_ID, getRootParentAccount().getExternalAccountPK().getCId());				
  		nextPageValues.put(ViewConstants.VIEW_PARAM_ACCOUNT_TYPE, getRootParentAccount().getExternalAccountPK().getAccountType());
  		nextPageValues.put(ViewConstants.VIEW_PARAM_ACCOUNT_CODE, getRootParentAccount().getExternalAccountPK().getAccountCode());
  		saveNextPageInitStateValues(nextPageValues);
  		this.forwardToURL(ViewConstants.CLIENT_AGREEMENTS);

		logger.info("-- Method name: viewEditClientAgreements end");
	}
	
	public void viewEditClientServiceElements() {
		logger.info("-- Method name: viewEditClientServiceElements start");
		
		Map<String, Object> restoreStateValues = new HashMap<String, Object>();
		restoreStateValues.put(ViewConstants.VIEW_PARAM_C_ID, clientAccount.getExternalAccountPK().getCId());
		restoreStateValues.put(ViewConstants.VIEW_PARAM_ACCOUNT_TYPE, clientAccount.getExternalAccountPK().getAccountType());
		restoreStateValues.put(ViewConstants.VIEW_PARAM_ACCOUNT_CODE, clientAccount.getExternalAccountPK().getAccountCode());
		saveRestoreStateValues(restoreStateValues);
		
		Map<String, Object> nextPageValues = new HashMap<String, Object>();
		nextPageValues.put(ViewConstants.VIEW_PARAM_C_ID, getRootParentAccount().getExternalAccountPK().getCId());				
  		nextPageValues.put(ViewConstants.VIEW_PARAM_ACCOUNT_TYPE, getRootParentAccount().getExternalAccountPK().getAccountType());
  		nextPageValues.put(ViewConstants.VIEW_PARAM_ACCOUNT_CODE, getRootParentAccount().getExternalAccountPK().getAccountCode());
  		saveNextPageInitStateValues(nextPageValues);
  		this.forwardToURL(ViewConstants.CLIENT_SERVICE_ELEMENTS);

		logger.info("-- Method name: viewEditClientServiceElements end");
	}
	
	public void viewEditGradeGroupServiceElementDetails() {
		logger.info("-- Method name: viewEditGradeGroupServiceElementDetails start");
		
		Map<String, Object> restoreStateValues = new HashMap<String, Object>();
		restoreStateValues.put(ViewConstants.VIEW_PARAM_C_ID, clientAccount.getExternalAccountPK().getCId());
		restoreStateValues.put(ViewConstants.VIEW_PARAM_ACCOUNT_TYPE, clientAccount.getExternalAccountPK().getAccountType());
		restoreStateValues.put(ViewConstants.VIEW_PARAM_ACCOUNT_CODE, clientAccount.getExternalAccountPK().getAccountCode());
		saveRestoreStateValues(restoreStateValues);
		
		Map<String, Object> nextPageValues = new HashMap<String, Object>();
		nextPageValues.put(ViewConstants.VIEW_PARAM_C_ID, clientAccount.getExternalAccountPK().getCId());			
  		nextPageValues.put(ViewConstants.VIEW_PARAM_ACCOUNT_TYPE, clientAccount.getExternalAccountPK().getAccountType());
  		nextPageValues.put(ViewConstants.VIEW_PARAM_ACCOUNT_CODE, clientAccount.getExternalAccountPK().getAccountCode());
  		saveNextPageInitStateValues(nextPageValues);
  		this.forwardToURL(ViewConstants.GRADE_GROUP_SERVICE_ELEMENT_DETAILS);

		logger.info("-- Method name: viewEditGradeGroupServiceElementDetails end");
	}	
	
	public String getAccountCode() {
		return accountCode;
	}

	public void setAccountCode(String accountCode) {
		this.accountCode = accountCode;
	}

	public String getAccountType() {
		return accountType;
	}

	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}

	public Long getAccountCId() {
		return accountCId;
	}

	public void setAccountCId(Long accountCId) {
		this.accountCId = accountCId;
	}

	public List<QuotationProfile> getQuotationProfileList() {
		return quotationProfileList;
	}

	public void setQuotationProfileList(List<QuotationProfile> quotationProfileList) {
		this.quotationProfileList = quotationProfileList;
	}

	public ExternalAccount getRootParentAccount() {
		return rootParentAccount;
	}

	public void setRootParentAccount(ExternalAccount rootParentAccount) {
		this.rootParentAccount = rootParentAccount;
	}

	public List<ClientServiceElement> getClientServiceElementList() {
		return clientServiceElementList;
	}

	public void setClientServiceElementList(
			List<ClientServiceElement> clientServiceElementList) {
		this.clientServiceElementList = clientServiceElementList;
	}

	public List<ClientServiceElementParameterVO> getServiceElementParameterList() {
		return serviceElementParameterList;
	}

	public void setServiceElementParameterList(List<ClientServiceElementParameterVO> serviceElementParameterList) {
		this.serviceElementParameterList = serviceElementParameterList;
	}

	public LeaseElement getSelectedLeaseElement() {
		return selectedLeaseElement;
	}

	public void setSelectedLeaseElement(LeaseElement selectedLeaseElement) {
		this.selectedLeaseElement = selectedLeaseElement;
	}

	public Boolean getValidationSuccess() {
		return validationSuccess;
	}

	public void setValidationSuccess(Boolean validationSuccess) {
		this.validationSuccess = validationSuccess;
	}

	public List<ClientServiceElement> getClientServiceElementHistoryList() {
		return clientServiceElementHistoryList;
	}

	public void setClientServiceElementHistoryList(
			List<ClientServiceElement> clientServiceElementHistoryList) {
		this.clientServiceElementHistoryList = clientServiceElementHistoryList;
	}

	public ExternalAccount getClientAccount() {
		return clientAccount;
	}

	public void setClientAccount(ExternalAccount clientAccount) {
		this.clientAccount = clientAccount;
	}

	public BigDecimal getDefaultValueTotal() {
		return defaultValueTotal;
	}

	public void setDefaultValueTotal(BigDecimal defaultValueTotal) {
		this.defaultValueTotal = defaultValueTotal;
	}

	public BigDecimal getClientValueTotal() {
		return clientValueTotal;
	}

	public void setClientValueTotal(BigDecimal clientValueTotal) {
		this.clientValueTotal = clientValueTotal;
	}

	public String getSelectedBillingOption() {
		return selectedBillingOption;
	}

	public void setSelectedBillingOption(String selectedBillingOption) {
		this.selectedBillingOption = selectedBillingOption;
	}

	public Long getSelectedClientServiceElementId() {
		return selectedClientServiceElementId;
	}

	public void setSelectedClientServiceElementId(
			Long selectedClientServiceElementId) {
		this.selectedClientServiceElementId = selectedClientServiceElementId;
	}

	public List<ClientServiceElement> getProductExclusionsList() {
		return productExclusionsList;
	}

	public void setProductExclusionsList(
			List<ClientServiceElement> productExclusionsList) {
		this.productExclusionsList = productExclusionsList;
	}

	public BigDecimal getProductValueTotal() {
		return productValueTotal;
	}

	public void setProductValueTotal(BigDecimal productValueTotal) {
		this.productValueTotal = productValueTotal;
	}

	public String getSelectedProduct() {
		return selectedProduct;
	}

	public void setSelectedProduct(String selectedProduct) {
		this.selectedProduct = selectedProduct;
	}

	public boolean isProductOverride() {
		return productOverride;
	}

	public void setProductOverride(boolean productOverride) {
		this.productOverride = productOverride;
	}

	public List<Product> getActiveClientProductList() {
		return activeClientProductList;
	}

	public void setActiveClientProductList(List<Product> activeClientProductList) {
		this.activeClientProductList = activeClientProductList;
	}
}
