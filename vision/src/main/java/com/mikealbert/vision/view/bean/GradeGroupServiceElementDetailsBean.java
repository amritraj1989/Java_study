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

import com.mikealbert.data.entity.ClientContractServiceElement;
import com.mikealbert.data.entity.ClientServiceElement;
import com.mikealbert.data.entity.ExternalAccount;
import com.mikealbert.data.entity.Product;
import com.mikealbert.data.enumeration.ClientServiceElementTypeCodes;
import com.mikealbert.data.enumeration.CorporateEntity;
import com.mikealbert.data.vo.ClientServiceElementParameterVO;
import com.mikealbert.data.vo.ServiceElementsVO;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.service.CustomerAccountService;
import com.mikealbert.service.ServiceElementService;
import com.mikealbert.util.MALUtilities;
import com.mikealbert.vision.view.ViewConstants;

@Component
@Scope("view")
public class GradeGroupServiceElementDetailsBean extends StatefulBaseBean {
	private static final long serialVersionUID = -3555144730868876085L;
	
	@Resource CustomerAccountService customerAccountService;
	@Resource ServiceElementService serviceElementService;

	public static final String CUSTOM_EMPTY_MESSAGE = "No Grade Group overrides exist";
	
	private String accountCode;
	private String accountType;
	private Long accountCId;
	private List<ServiceElementsVO> gradeGroupServiceElementList;
	private ExternalAccount clientAccount;	
	private String selectedBillingOption;
	private String customEmptyMessage;
	private BigDecimal defaultValueTotal;
	private BigDecimal clientValueTotal;
	private BigDecimal gradeGroupValueTotal;
	private BigDecimal productValueTotal;
	private List<ClientServiceElementParameterVO> serviceElementParameterList;
	private List<ClientServiceElement> gradeGroupServiceElementHistoryList;
	private Long selectedClientServiceElementId;
	private String selectedGradeGroupCode = "ALL";
	private List<ServiceElementsVO> gradeGroupProductOverrideList;
	private boolean productOverride;
	private String selectedProduct;
	private List<Product> activeClientProductList;	
	
	public Long getSelectedClientServiceElementId() {
		return selectedClientServiceElementId;
	}

	public void setSelectedClientServiceElementId(
			Long selectedClientServiceElementId) {
		this.selectedClientServiceElementId = selectedClientServiceElementId;
	}

	public String getSelectedBillingOption() {
		return selectedBillingOption;
	}

	public void setSelectedBillingOption(String selectedBillingOption) {
		this.selectedBillingOption = selectedBillingOption;
	}

	public String getCustomEmptyMessage() {
		return customEmptyMessage;
	}

	public void setCustomEmptyMessage(String customEmptyMessage) {
		this.customEmptyMessage = customEmptyMessage;
	}

    @PostConstruct
    public void init() {
        super.openPage();
        try {
        	loadData();
        	setCustomEmptyMessage(CUSTOM_EMPTY_MESSAGE);
        } catch (Exception ex) {
        	logger.error(ex);
        	super.addErrorMessage("generic.error", ex.getMessage());
        }
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
			setClientAccount(customerAccountService.getOpenCustomerAccountByCode(getAccountCode(), getLoggedInUser().getCorporateEntity()));
			setGradeGroupServiceElementList(serviceElementService.getGradeGroupServiceElements(getClientAccount().getExternalAccountPK().getCId(), getClientAccount().getExternalAccountPK().getAccountType(), getClientAccount().getExternalAccountPK().getAccountCode()));
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
	
	@Override
	protected void loadNewPage() {
		Map<String, Object> map = super.thisPage.getInputValues();
		thisPage.setPageUrl(ViewConstants.GRADE_GROUP_SERVICE_ELEMENT_DETAILS);
		thisPage.setPageDisplayName(ViewConstants.DISPLAY_NAME_GRADE_GROUP_SERVICE_ELEMENT_DETAILS);
		this.setAccountCId((Long) map.get(ViewConstants.VIEW_PARAM_C_ID));
		this.setAccountType((String) map.get(ViewConstants.VIEW_PARAM_ACCOUNT_TYPE));
		this.setAccountCode((String) map.get(ViewConstants.VIEW_PARAM_ACCOUNT_CODE));
	}

	@Override
	protected void restoreOldPage() {
		logger.info("-- Method name: restoreOldPage start");
		Map<String, Object> map = super.thisPage.getRestoreStateValues();
		this.setAccountCId((Long) map.get(ViewConstants.VIEW_PARAM_C_ID));
		this.setAccountType((String) map.get(ViewConstants.VIEW_PARAM_ACCOUNT_TYPE));
		this.setAccountCode((String) map.get(ViewConstants.VIEW_PARAM_ACCOUNT_CODE));
		logger.info("-- Method name: restoreOldPage end");	
	}

	public void viewEditGradeGroupServiceElements() {
		logger.info("-- Method name: viewEditGradeGroupServiceElements start");
		
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
  		this.forwardToURL(ViewConstants.GRADE_GROUP_SERVICE_ELEMENTS);

		logger.info("-- Method name: viewEditGradeGroupServiceElements end");
	}
	
	public void populateHistory() {
		setGradeGroupServiceElementHistoryList(serviceElementService.getGradeGroupServiceElementsHistoryByClient(clientAccount.getExternalAccountPK().getCId(), clientAccount.getExternalAccountPK().getAccountType(), clientAccount.getExternalAccountPK().getAccountCode(), ClientServiceElementTypeCodes.EXTERNAL_ACCOUNT_GRADE_GROUP.getId()));
	}
	
	public void populateTotal(Long leaseElementId, Long clientServiceElementId, String productCode) {
		setDefaultValueTotal(null);
		setClientValueTotal(null);
		setGradeGroupValueTotal(null);
		setProductValueTotal(null);
		
		List<ClientServiceElementParameterVO> csepVOList = new ArrayList<ClientServiceElementParameterVO>();
		if (!MALUtilities.isEmpty(productCode)) {
			setProductOverride(true);
			csepVOList = serviceElementService.getGradeGroupProductServiceElementParametersByClientServiceElement(leaseElementId, clientServiceElementId);
		} else {
			setProductOverride(false);		
			csepVOList = serviceElementService.getGradeGroupServiceElementParametersByClientServiceElement(leaseElementId, clientServiceElementId);
		}
		for (ClientServiceElementParameterVO csepVO : csepVOList) {
			if (!MALUtilities.isEmpty(csepVO.getDefaultValue())) {
				if (MALUtilities.isEmpty(getDefaultValueTotal())) {
					setDefaultValueTotal(new BigDecimal(0));
				}
				setDefaultValueTotal(getDefaultValueTotal().add(csepVO.getDefaultValue()));
			}
			
			if (!MALUtilities.isEmpty(csepVO.getClientValue())) {
				if (MALUtilities.isEmpty(getClientValueTotal())) {
					setClientValueTotal(new BigDecimal(0));
				}
				setClientValueTotal(getClientValueTotal().add(csepVO.getClientValue()));
			}
			
			if (!MALUtilities.isEmpty(csepVO.getGradeGroupValue())) {
				if (MALUtilities.isEmpty(getGradeGroupValueTotal())) {
					setGradeGroupValueTotal(new BigDecimal(0));
				}
				setGradeGroupValueTotal(getGradeGroupValueTotal().add(csepVO.getGradeGroupValue()));
			}
			
			if (!MALUtilities.isEmpty(csepVO.getProductValue())) {
				if (MALUtilities.isEmpty(getProductValueTotal())) {
					setProductValueTotal(new BigDecimal(0));
				}
				setProductValueTotal(getProductValueTotal().add(csepVO.getProductValue()));
			}			
		}
	}
	
	public BigDecimal getDefaultValues(Long leaseElementId, Long clientServiceElementId, String productCode) {
		populateTotal(leaseElementId, clientServiceElementId, productCode);
		return getDefaultValueTotal();
	}
	
	public void populateElementValues(Long leaseElementId, Long clientServiceElementId, String productCode) {
		if (!MALUtilities.isEmpty(productCode)) {
			setProductOverride(true);
			setServiceElementParameterList(serviceElementService.getGradeGroupProductServiceElementParametersByClientServiceElement(leaseElementId, clientServiceElementId));
		} else {
			setProductOverride(false);
			setServiceElementParameterList(serviceElementService.getGradeGroupServiceElementParametersByClientServiceElement(leaseElementId, clientServiceElementId));
		}
		adjustTotal();
	}
	
	public void adjustTotal() {
		setDefaultValueTotal(new BigDecimal(0));
		setClientValueTotal(new BigDecimal(0));
		setGradeGroupValueTotal(new BigDecimal(0));
		setProductValueTotal(new BigDecimal(0));
		for (ClientServiceElementParameterVO csepVO : getServiceElementParameterList()) {
			if (!MALUtilities.isEmpty(csepVO.getProductValue())) {
				setProductValueTotal(getProductValueTotal().add(csepVO.getProductValue().setScale(5, RoundingMode.HALF_DOWN)).setScale(5, RoundingMode.HALF_UP));
			}			
			if (!MALUtilities.isEmpty(csepVO.getGradeGroupValue())) {
				if (isProductOverride()){
					setGradeGroupValueTotal(getGradeGroupValueTotal().add(csepVO.getGradeGroupValue()));
				} else {					
					setGradeGroupValueTotal(getGradeGroupValueTotal().add(csepVO.getGradeGroupValue().setScale(5, RoundingMode.HALF_DOWN)).setScale(5, RoundingMode.HALF_UP));
				}
			}
			if (!MALUtilities.isEmpty(csepVO.getClientValue())) {
				setClientValueTotal(getClientValueTotal().add(csepVO.getClientValue()));
			}
			if (!MALUtilities.isEmpty(csepVO.getDefaultValue())) {
				setDefaultValueTotal(getDefaultValueTotal().add(csepVO.getDefaultValue()));
			}
		}
	}
	
	public String save() {
		try{
			serviceElementService.saveOrUpdateGradeGroupServiceElementParameters(getServiceElementParameterList(), isProductOverride());
			super.addSuccessMessage("process.success","Save Grade Group Service Element Parameters");
		} catch(Exception ex){
			logger.error(ex);		
			handleException("generic.error",new String[]{ex.getMessage()}, ex, null);
			RequestContext context = RequestContext.getCurrentInstance();
			context.addCallbackParam("failure", true);
		}
		
		return null;
	}
	
	public Boolean isElementAssginedToClient(ClientContractServiceElement ccse) {
		ClientServiceElement clientServiceElement = serviceElementService.getClientServiceElementByClientAndElement(getClientAccount().getExternalAccountPK().getCId(), getClientAccount().getExternalAccountPK().getAccountType(), getClientAccount().getExternalAccountPK().getAccountCode(), ccse.getLeaseElement().getLelId());
		if (!MALUtilities.isEmpty(clientServiceElement)) {
			return true;
		}
		return false;
	}
	
	public String changeGradeGroupServiceElement(){
		try{
			List<ClientServiceElement> cseList = new ArrayList<ClientServiceElement>();
			ClientServiceElement clientElement = serviceElementService.getClientServiceElement(this.selectedClientServiceElementId);
			clientElement.setBillingOption(this.selectedBillingOption);
			cseList.add(clientElement);
			serviceElementService.saveUpdateClientServiceElements(cseList, ClientServiceElementTypeCodes.EXTERNAL_ACCOUNT_GRADE_GROUP.getCode());	
			loadData();
			RequestContext.getCurrentInstance().update("gradeGroupsTbl");
			super.addSuccessMessage("process.success","Change Billing Options");
		}
		catch(Exception ex){
			logger.error(ex);		
			super.addErrorMessage("generic.error", ex.getMessage());
		}
		
		return null;
	}	
	
	//grade group exclusion
    public String removeServiceElement(Long clientServiceElementId){
    	try {	
    		ClientServiceElement gradeGroupClientElement = serviceElementService.getClientServiceElement(clientServiceElementId);
			serviceElementService.removeGradeGroupClientServiceElement(gradeGroupClientElement);
			loadData();
			super.addSuccessMessage("process.success","Grade Group Service Element exclusion ");
		} catch(Exception e) {
			logger.error(e);
			super.addErrorMessage("generic.error", e.getMessage());
		}
    	
    	return null;
    }
    
    //product exclusion
    public String removeProductServiceElement(){
    	try {	
   			ClientServiceElement clientElement = serviceElementService.getClientServiceElement(this.selectedClientServiceElementId);
   			ClientServiceElement productClientElement = serviceElementService.constructClientServiceElement(clientElement.getClientContractServiceElement(),clientElement.getBillingOption(),CorporateEntity.fromCorpId(getAccountCId()),clientElement.getExternalAccount().getExternalAccountPK().getAccountCode(),null,clientElement.getExternalAccountGradeGroup().getDriverGradeGroup().getDriverGradeGroup());
   			
   			//Check if product override already exists for this grade group and lease element
   			ClientServiceElement ce = serviceElementService.getGradeGroupProductServiceElementByGradeGroupAndElementWRemoved(productClientElement.getExternalAccount().getExternalAccountPK().getCId(), productClientElement.getExternalAccount().getExternalAccountPK().getAccountType(), productClientElement.getExternalAccount().getExternalAccountPK().getAccountCode(), productClientElement.getExternalAccountGradeGroup().getEagId(), productClientElement.getClientContractServiceElement().getLeaseElement().getLelId(), selectedProduct);
			if (MALUtilities.isEmpty(ce)){
				serviceElementService.removeProductClientServiceElement(productClientElement, selectedProduct, ClientServiceElementTypeCodes.EXTERNAL_ACCOUNT_GRADE_GROUP.getCode());
				loadData();
				super.addSuccessMessage("process.success","Exclude Grade Group Service Element at Product Level ");				
				
			}
		} catch(Exception e) {
			logger.error(e);
			super.addErrorMessage("generic.error", e.getMessage());
		}
    	
    	return null;
    }  
    
    //remove product exclusion
    public String deleteProductExclusion(Long clientServiceElementId) {
    	try {	
   			ClientServiceElement clientElement = serviceElementService.getClientServiceElement(clientServiceElementId);
			serviceElementService.deleteProductRemovalServiceElement(clientElement, ClientServiceElementTypeCodes.EXTERNAL_ACCOUNT_GRADE_GROUP.getCode());
			loadData();
			super.addSuccessMessage("process.success","Remove Service Element for Product Exclusion ");
		} catch(Exception e) {
			logger.error(e);
			super.addErrorMessage("generic.error", e.getMessage());
		}
    	
    	return null;
    } 
	
    public String done(){
    	return super.cancelPage();      	
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

	public ExternalAccount getClientAccount() {
		return clientAccount;
	}

	public void setClientAccount(ExternalAccount clientAccount) {
		this.clientAccount = clientAccount;
	}

	public List<ServiceElementsVO> getGradeGroupServiceElementList() {
		return gradeGroupServiceElementList;
	}

	public void setGradeGroupServiceElementList(List<ServiceElementsVO> gradeGroupServiceElementList) {
		this.gradeGroupServiceElementList = gradeGroupServiceElementList;
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

	public BigDecimal getGradeGroupValueTotal() {
		return gradeGroupValueTotal;
	}

	public void setGradeGroupValueTotal(BigDecimal gradeGroupValueTotal) {
		this.gradeGroupValueTotal = gradeGroupValueTotal;
	}

	public List<ClientServiceElementParameterVO> getServiceElementParameterList() {
		return serviceElementParameterList;
	}

	public void setServiceElementParameterList(
			List<ClientServiceElementParameterVO> serviceElementParameterList) {
		this.serviceElementParameterList = serviceElementParameterList;
	}

	public List<ClientServiceElement> getGradeGroupServiceElementHistoryList() {
		return gradeGroupServiceElementHistoryList;
	}

	public void setGradeGroupServiceElementHistoryList(
			List<ClientServiceElement> gradeGroupServiceElementHistoryList) {
		this.gradeGroupServiceElementHistoryList = gradeGroupServiceElementHistoryList;
	}

	public String getSelectedGradeGroupCode() {
		return selectedGradeGroupCode;
	}

	public void setSelectedGradeGroupCode(String selectedGradeGroupCode) {
		this.selectedGradeGroupCode = selectedGradeGroupCode;
	}

	public List<ServiceElementsVO> getGradeGroupProductOverrideList() {
		return gradeGroupProductOverrideList;
	}

	public void setGradeGroupProductOverrideList(
			List<ServiceElementsVO> gradeGroupProductOverrideList) {
		this.gradeGroupProductOverrideList = gradeGroupProductOverrideList;
	}

	public boolean isProductOverride() {
		return productOverride;
	}

	public void setProductOverride(boolean productOverride) {
		this.productOverride = productOverride;
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

	public List<Product> getActiveClientProductList() {
		return activeClientProductList;
	}

	public void setActiveClientProductList(List<Product> activeClientProductList) {
		this.activeClientProductList = activeClientProductList;
	}

}
