package com.mikealbert.vision.view.bean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.primefaces.context.RequestContext;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.mikealbert.data.entity.ClientContractServiceElement;
import com.mikealbert.data.entity.ClientServiceElement;
import com.mikealbert.data.entity.ClientServiceElementParameter;
import com.mikealbert.data.entity.ExternalAccount;
import com.mikealbert.data.entity.Product;
import com.mikealbert.data.enumeration.ClientServiceElementTypeCodes;
import com.mikealbert.data.enumeration.CorporateEntity;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.service.CustomerAccountService;
import com.mikealbert.service.ServiceElementService;
import com.mikealbert.util.MALUtilities;
import com.mikealbert.vision.view.ViewConstants;

@Component
@Scope("view")
public class ClientServiceElementsBean extends StatefulBaseBean {
	private static final long serialVersionUID = -479501943545874457L;
	
	@Resource ServiceElementService serviceElementService;
	@Resource CustomerAccountService customerAccountService;
	
	
	private String accountCode;
	private String accountType;
	private Long accountCId;
	private ExternalAccount parentAccount; 	
	private ExternalAccount clientAccount;	
	private List<ExternalAccount> childAccountList;	
	private List<ClientServiceElement> clientServiceElementList;
	private List<ClientServiceElement> parentServiceElementsList;	
	private List<ClientServiceElement> childServiceElementsList;
	private List<ClientServiceElement> accountsServiceElementsList;
	private boolean isParentAccountExist = false;
	private List<ClientContractServiceElement> clientContractServiceElementList = new ArrayList<ClientContractServiceElement>();
	private ClientContractServiceElement selectedClientContractServiceElement;
	private List<ExternalAccount> accountList;	
	private List<String> selectedAccountListString;
	private boolean isExpanded;
	private String selectedBillingOption;
	private List<ClientServiceElement> removeElementList;
	private String selectedProduct;
	private List<ClientServiceElement> productClientServiceElementsList;
	private List<Product> activeClientProductList;

	private TreeNode parentRoot;
    private TreeNode childRoot;
    private TreeNode parentRootNode;
    private TreeNode childRootNode;

	@PostConstruct
	public void init() {
		super.openPage();
		try {
			loadData();
		} catch (Exception ex) {
			logger.error(ex);
			super.addErrorMessage("generic.error", ex.getMessage());
		}
	}    
    
	public void loadData() {
		try {
			setClientAccount(customerAccountService.getOpenCustomerAccountByCode(getAccountCode(), getLoggedInUser().getCorporateEntity()));
			if(getClientAccount() != null && !MALUtilities.isEmpty(getClientAccount().getExternalAccountPK().getAccountCode())){
				setParentAccount(customerAccountService.getParentAccount(getClientAccount()));
				if(getParentAccount().getExternalAccountPK() == null || (getParentAccount().getExternalAccountPK() != null && MALUtilities.isEmpty(getParentAccount().getExternalAccountPK().getAccountCode()))){
					setParentAccount(clientAccount); // if there is no parent account, assume current account is parent
				}
				setChildAccountList(customerAccountService.getChildAccounts(getParentAccount()));
				if (getChildAccountList().size() > 0) {
					setParentAccountExist(true);
				}
			}  
			loadRows();
			populateContractedServiceElements();
			populateAccountList();
			
		} catch (Exception e) {
			logger.error(e);
			 if(e  instanceof MalBusinessException){				 
				 super.addErrorMessage(e.getMessage()); 
			 }else{
				 super.addErrorMessage("generic.error.occured.while", " building screen.");
			 }
		}
	}	
	
    public void loadRows() {
    	accountsServiceElementsList = new ArrayList<ClientServiceElement>();
    	//build parent account/service elements tree
    	parentRootNode = new DefaultTreeNode("parentRoot", null);
    	TreeNode parentAccountNode = null;
    	TreeNode parentServiceElementNode = null;
    	
    	parentServiceElementsList = serviceElementService.getClientServiceElementsByClient(getParentAccount().getExternalAccountPK().getCId(), getParentAccount().getExternalAccountPK().getAccountType(), getParentAccount().getExternalAccountPK().getAccountCode());
    	accountsServiceElementsList.addAll(parentServiceElementsList);
    	
    	productClientServiceElementsList = serviceElementService.getProductServiceElementsByAccount(getParentAccount().getExternalAccountPK().getCId(), getParentAccount().getExternalAccountPK().getAccountType(), getParentAccount().getExternalAccountPK().getAccountCode());
    	activeClientProductList = serviceElementService.findActiveClientProductList();
    	
    	parentAccountNode = new DefaultTreeNode(getParentAccount().getExternalAccountPK().getAccountCode() + " - " + getParentAccount().getAccountName(), parentRootNode);
    	for (ClientServiceElement parentServiceElement : parentServiceElementsList) {
    		parentServiceElementNode = new DefaultTreeNode(parentServiceElement.getClientContractServiceElement().getLeaseElement().getElementName() + " - " + parentServiceElement.getClientContractServiceElement().getLeaseElement().getDescription() + " (" + serviceElementService.convertBillingOptionToReadableString(parentServiceElement.getBillingOption()) + ")", parentAccountNode);
    		parentAccountNode.setExpanded(isExpanded);
    	}
    	
    	//build child account/service elements tree
    	childRootNode = new DefaultTreeNode("childRoot", null);
    	TreeNode childAccountNode = null;
    	TreeNode childServiceElementNode = null;  
    	
		for (ExternalAccount childAccount : childAccountList) {
			childAccountNode = new DefaultTreeNode(childAccount.getExternalAccountPK().getAccountCode() + " - " + childAccount.getAccountName(), childRootNode);
			childServiceElementsList = serviceElementService.getClientServiceElementsByClient(childAccount.getExternalAccountPK().getCId(), childAccount.getExternalAccountPK().getAccountType(), childAccount.getExternalAccountPK().getAccountCode());
			accountsServiceElementsList.addAll(childServiceElementsList);
			
			productClientServiceElementsList.addAll(serviceElementService.getProductServiceElementsByAccount(childAccount.getExternalAccountPK().getCId(), childAccount.getExternalAccountPK().getAccountType(), childAccount.getExternalAccountPK().getAccountCode()));
			
			for (ClientServiceElement childServiceElements : childServiceElementsList) {
	    		childServiceElementNode = new DefaultTreeNode(childServiceElements.getClientContractServiceElement().getLeaseElement().getElementName() + " - " + childServiceElements.getClientContractServiceElement().getLeaseElement().getDescription() + " (" + serviceElementService.convertBillingOptionToReadableString(childServiceElements.getBillingOption()) + ")", childAccountNode);
	    	}	
			childAccountNode.setExpanded(isExpanded);
		}
    }  
	
    public void collapsingOrExpanding() {
    	if(isExpanded == false){
    		isExpanded = true;
    	}
    	else {
    		isExpanded = false;
    	}
    	loadRows();
	}
    
    public void populateContractedServiceElements() {
    	clientContractServiceElementList = serviceElementService.getClientContractServiceElementsByClient(getParentAccount().getExternalAccountPK().getCId(), getParentAccount().getExternalAccountPK().getAccountType(), getParentAccount().getExternalAccountPK().getAccountCode());
    	sortContractedServiceElements();
    }
    
    public void populateAccountList() {
    	accountList = new ArrayList<ExternalAccount>(getChildAccountList());
    	accountList.add(0, getParentAccount());
    }
    
	private void sortContractedServiceElements(){
		if(this.clientContractServiceElementList != null && this.clientContractServiceElementList.size() > 0){
			Collections.sort(this.clientContractServiceElementList, new Comparator<ClientContractServiceElement>() {
				public int compare(ClientContractServiceElement ccse1, ClientContractServiceElement ccse2) {
					String name1 = ccse1.getLeaseElement().getElementName();
					String name2 = ccse2.getLeaseElement().getElementName();
					return name1.toLowerCase().compareTo(name2.toLowerCase());						
				}
			});			
		}		
	} 
	
	@Override    
	protected void loadNewPage() {
		Map<String, Object> map = super.thisPage.getInputValues();
		thisPage.setPageUrl(ViewConstants.CLIENT_SERVICE_ELEMENTS);
		thisPage.setPageDisplayName(ViewConstants.DISPLAY_NAME_CLIENT_SERVICE_ELEMENTS);
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
	
	 /**
     * Handles page cancel button click event
     * @return The calling view
     */
    public String done(){
    	return super.cancelPage();      	
    }
	
    public void addClientServiceElementsButtonClick(){
    	selectedBillingOption = "MONTHLY";
    	//Per SS-557, bypass Billing Option Dialog and default to MONTHLY. Selection may be implemented at a later date -->
    	addClientServiceElements();
    }
	
	public String addClientServiceElements(){
		try{
			if(selectedAccountListString.contains("ALL")){
				selectedAccountListString.clear();
				for(ExternalAccount account : accountList){
					selectedAccountListString.add(account.getExternalAccountPK().getAccountCode());
				}
			}
			List<ClientServiceElement> clientServiceElementList = new ArrayList<ClientServiceElement>();
			if (!MALUtilities.isEmpty(selectedAccountListString) && selectedAccountListString.size() > 0 && selectedClientContractServiceElement != null) { 
				for(String accountString : selectedAccountListString){
					clientServiceElementList.add(serviceElementService.constructClientServiceElement(selectedClientContractServiceElement,selectedBillingOption,CorporateEntity.fromCorpId(getAccountCId()),accountString,selectedProduct));				
				}
				for(ClientServiceElement accountServiceElement : accountsServiceElementsList){
					for(Iterator<ClientServiceElement> iter = clientServiceElementList.iterator(); iter.hasNext();){
						ClientServiceElement element = (ClientServiceElement)iter.next();
						if (element.getProduct() == null){
							if(!MALUtilities.isEmpty(element.getExternalAccount()) && (element.getExternalAccount().equals(accountServiceElement.getExternalAccount()) && element.getClientContractServiceElement().getLeaseElement().getLelId() == (accountServiceElement.getClientContractServiceElement().getLeaseElement().getLelId()))){
								iter.remove();	
							}
						}
					}
				}
				for(ClientServiceElement productServiceElement : productClientServiceElementsList){
					for(Iterator<ClientServiceElement> iter = clientServiceElementList.iterator(); iter.hasNext();){
						ClientServiceElement elemnt = (ClientServiceElement)iter.next();
						if (elemnt.getProduct() != null){
							if(!MALUtilities.isEmpty(elemnt.getExternalAccount()) && (elemnt.getExternalAccount().equals(productServiceElement.getExternalAccount()) && elemnt.getClientContractServiceElement().getLeaseElement().getLelId() == (productServiceElement.getClientContractServiceElement().getLeaseElement().getLelId())
									&& elemnt.getProduct().getProductCode().equals(productServiceElement.getProduct().getProductCode()))) {
								iter.remove();	
							}
						}
					}
				}
			}			
			if(!clientServiceElementList.isEmpty()  && clientServiceElementList.size() > 0){
				serviceElementService.saveUpdateClientServiceElements(clientServiceElementList,ClientServiceElementTypeCodes.ACCOUNT.getCode());
				super.addSuccessMessage("process.success","Save Client Service Elements");
				isExpanded = false;
				loadRows();				
			}
			setSelectedClientContractServiceElement(null);
			setSelectedAccountListString(null);		
			
			RequestContext context = RequestContext.getCurrentInstance();
			context.update("clientServiceElementsForm");
		}
		catch(Exception ex){
			logger.error(ex);		
			super.addErrorMessage("generic.error", ex.getMessage());
		}
		
		return null;
	}	
	
	public Boolean existsServiceElementParameter() {
		Boolean exists = false;
		removeElementList  = new ArrayList<ClientServiceElement>();
		if(selectedAccountListString.contains("ALL")){
			selectedAccountListString.clear();
			for(ExternalAccount account : accountList){
				selectedAccountListString.add(account.getExternalAccountPK().getAccountCode());
			}
		}
		
		for(String account : selectedAccountListString){
			ClientServiceElement clientServiceElement = new ClientServiceElement();
			List <ClientServiceElement> clientServiceElements = new ArrayList<ClientServiceElement>();
			if (selectedClientContractServiceElement != null) {
				
				if(selectedProduct != null){
					clientServiceElement = serviceElementService.getClientProductServiceElementByClientAndElement(getAccountCId(), getAccountType(), account, selectedClientContractServiceElement.getLeaseElement().getLelId(), selectedProduct);
					if(clientServiceElement != null){
						clientServiceElements.add(clientServiceElement);
					}
				}
				else {
					clientServiceElements = serviceElementService.getClientServiceElementsByClientAndElementIncludeRemoved(getAccountCId(), getAccountType(), account, selectedClientContractServiceElement.getLeaseElement().getLelId());
				}
				
				if(!clientServiceElements.isEmpty()  && clientServiceElements.size() > 0){
					for (ClientServiceElement cse : clientServiceElements) {
						cse.setEndDate(new Date());
						cse.setRemovedFlag("N");
						removeElementList.add(cse);
						List<ClientServiceElementParameter> csepList = serviceElementService.findByClientServiceElementId(cse.getClientServiceElementId());
						if (csepList.size() > 0 && !exists) {
							exists = true;
						}						
					}
					
				}
			}
		}
		return exists;
	}
	
	public void showHideRemoveServiceElementConfirmDialog() {
		if (existsServiceElementParameter()) {
			RequestContext context = RequestContext.getCurrentInstance();
			context.addCallbackParam("failure", true);
		}
	}
	
	public String removeClientServiceElements(){
		try{
			if (!MALUtilities.isEmpty(selectedAccountListString) && selectedAccountListString.size() > 0 && selectedClientContractServiceElement != null) {
				if(!removeElementList.isEmpty()  && removeElementList.size() > 0){
					serviceElementService.saveUpdateClientServiceElements(removeElementList,ClientServiceElementTypeCodes.ACCOUNT.getCode());
					super.addSuccessMessage("process.success","Remove Client Service Elements");
					isExpanded = false;
					loadRows();				
				}				
			}
			setSelectedClientContractServiceElement(null);
			setSelectedAccountListString(null);
			removeElementList.clear();
		}
		catch(Exception ex){
			logger.error(ex);		
			super.addErrorMessage("generic.error", ex.getMessage());
		}
		
		return null;
	}		
	    
    public TreeNode getParentRoot() {
    	return parentRoot;
	} 
    
    public TreeNode getChildRoot() {
    	return childRoot;
	} 

	public TreeNode getParentRootNode() {
		return parentRootNode;
	}

	public void setParentRootNode(TreeNode parentRootNode) {
		this.parentRootNode = parentRootNode;
	}

	public TreeNode getChildRootNode() {
		return childRootNode;
	}

	public void setChildRootNode(TreeNode childRootNode) {
		this.childRootNode = childRootNode;
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

	public ExternalAccount getParentAccount() {
		return parentAccount;
	}

	public void setParentAccount(ExternalAccount parentAccount) {
		this.parentAccount = parentAccount;
	}

	public ExternalAccount getClientAccount() {
		return clientAccount;
	}

	public void setClientAccount(ExternalAccount clientAccount) {
		this.clientAccount = clientAccount;
	}

	public List<ExternalAccount> getChildAccountList() {
		return childAccountList;
	}

	public void setChildAccountList(List<ExternalAccount> childAccountList) {
		this.childAccountList = childAccountList;
	}

	public boolean isParentAccountExist() {
		return isParentAccountExist;
	}

	public void setParentAccountExist(boolean isParentAccountExist) {
		this.isParentAccountExist = isParentAccountExist;
	}

	public List<ClientContractServiceElement> getClientContractServiceElementList() {
		return clientContractServiceElementList;
	}

	public void setClientContractServiceElementList(
			List<ClientContractServiceElement> clientContractServiceElementList) {
		this.clientContractServiceElementList = clientContractServiceElementList;
	}

	public List<ExternalAccount> getAccountList() {
		return accountList;
	}

	public void setAccountList(List<ExternalAccount> accountList) {
		this.accountList = accountList;
	}

	public ClientContractServiceElement getSelectedClientContractServiceElement() {
		return selectedClientContractServiceElement;
	}

	public void setSelectedClientContractServiceElement(
			ClientContractServiceElement selectedClientContractServiceElement) {
		this.selectedClientContractServiceElement = selectedClientContractServiceElement;
	}

	public List<String> getSelectedAccountListString() {
		return selectedAccountListString;
	}

	public void setSelectedAccountListString(
			List<String> selectedAccountListString) {
		this.selectedAccountListString = selectedAccountListString;
	}

	public List<ClientServiceElement> getClientServiceElementList() {
		return clientServiceElementList;
	}

	public void setClientServiceElementList(List<ClientServiceElement> clientServiceElementList) {
		this.clientServiceElementList = clientServiceElementList;
	}
	
	public List<ClientServiceElement> getParentServiceElementsList() {
		return parentServiceElementsList;
	}

	public void setParentServiceElementsList(
			List<ClientServiceElement> parentServiceElementsList) {
		this.parentServiceElementsList = parentServiceElementsList;
	}

	public List<ClientServiceElement> getChildServiceElementsList() {
		return childServiceElementsList;
	}

	public void setChildServiceElementsList(
			List<ClientServiceElement> childServiceElementsList) {
		this.childServiceElementsList = childServiceElementsList;
	}

	public List<ClientServiceElement> getAccountsServiceElementsList() {
		return accountsServiceElementsList;
	}

	public void setAccountsServiceElementsList(
			List<ClientServiceElement> accountsServiceElementsList) {
		this.accountsServiceElementsList = accountsServiceElementsList;
	}	
	
    public boolean isExpanded() {
		return isExpanded;
	}

	public void setExpanded(boolean isExpanded) {
		this.isExpanded = isExpanded;
	}	

	public String getSelectedBillingOption() {
		return selectedBillingOption;
	}

	public void setSelectedBillingOption(String selectedBillingOption) {
		this.selectedBillingOption = selectedBillingOption;
	}

	public List<ClientServiceElement> getProductClientServiceElementsList() {
		return productClientServiceElementsList;
	}

	public void setProductClientServiceElementsList(
			List<ClientServiceElement> productClientServiceElementsList) {
		this.productClientServiceElementsList = productClientServiceElementsList;
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


