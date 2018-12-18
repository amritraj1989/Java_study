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
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.mikealbert.data.entity.ClientContractServiceElement;
import com.mikealbert.data.entity.ClientServiceElement;
import com.mikealbert.data.entity.ClientServiceElementParameter;
import com.mikealbert.data.entity.DriverGrade;
import com.mikealbert.data.entity.ExternalAccount;
import com.mikealbert.data.entity.ExternalAccountGradeGroup;
import com.mikealbert.data.entity.Product;
import com.mikealbert.data.enumeration.ClientServiceElementTypeCodes;
import com.mikealbert.data.enumeration.CorporateEntity;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.service.CustomerAccountService;
import com.mikealbert.service.DriverGradeService;
import com.mikealbert.service.ServiceElementService;
import com.mikealbert.util.MALUtilities;
import com.mikealbert.vision.view.ViewConstants;


@Component
@Scope("view")
public class GradeGroupServiceElementsBean extends StatefulBaseBean {
	private static final long serialVersionUID = -3555144730868876085L;
	
	@Resource DriverGradeService driverGradeService;
	@Resource ServiceElementService serviceElementService;
	@Resource CustomerAccountService customerAccountService;
	
	private String accountCode;
	private String accountType;
	private Long accountCId;
	private List<DriverGrade> gradeGroupsList;
	private List<ClientServiceElement> clientServiceElementsList; 
	private List<ClientContractServiceElement> clientContractServiceElementList;
	private ExternalAccount parentAccount; 
	private ExternalAccount clientAccount;
	private String selectedBillingOption;
	
	private List<String> selectedGradeGroupsListString;
	private List<String> selectedClientServiceElementString;
	private List<String> selectedClientContractServiceElementString;
	private ClientServiceElement clientServiceElement;
	private List<ClientServiceElement> gradeGroupServiceElements;
	private List<ClientServiceElement> removeElementList;
	private List<String> selectedGradeGroupsListString2;
	private List<String> ggListString;
	private String selectedProduct;
	private String selectedProduct2;
	private List<ClientServiceElement> productGradeGroupServiceElementsList;
	private List<Product> activeClientProductList;
	
	//still constructing
	String fromProp;

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
			}  

			gradeGroupsList = driverGradeService.getExternalAccountDriverGrades(clientAccount);
			clientServiceElementsList = serviceElementService.getClientServiceElementsByClient(clientAccount.getExternalAccountPK().getCId(), clientAccount.getExternalAccountPK().getAccountType(), clientAccount.getExternalAccountPK().getAccountCode());
			clientContractServiceElementList = serviceElementService.getClientContractServiceElementsByClient(getParentAccount().getExternalAccountPK().getCId(), getParentAccount().getExternalAccountPK().getAccountType(), getParentAccount().getExternalAccountPK().getAccountCode());
			activeClientProductList = serviceElementService.findActiveClientProductList();
	    	sortContractedServiceElements();
	    	
	    	//remove assigned client service elements from contracted service elements list
			for(ClientServiceElement cse : clientServiceElementsList){
				for(Iterator<ClientContractServiceElement> iter = clientContractServiceElementList.iterator(); iter.hasNext();){
					ClientContractServiceElement element = (ClientContractServiceElement)iter.next();
					if(!MALUtilities.isEmpty(element.getLeaseElement().getLelId()) && (element.getLeaseElement().getLelId() == (cse.getClientContractServiceElement().getLeaseElement().getLelId()))) {
						iter.remove();	
					}
				}
			}			
		} catch (Exception e) {
			logger.error(e);
			if(e  instanceof MalBusinessException){				 
				super.addErrorMessage(e.getMessage()); 
			}else{
				super.addErrorMessage("generic.error.occured.while", " building screen.");
			}
		}
	}
	
    public void addServiceElementsButtonClick(String fromProperty){
    	selectedBillingOption = "MONTHLY";
    	fromProp = fromProperty;
    	//Per SS-557, bypass Billing Option Dialog and default to MONTHLY. Selection may be implemented at a later date -->
    	addGradeGroupServiceElements(fromProp);
    }
    
    //rework
    public void removeServiceElementsButtonClick(String fromProperty){
    	fromProp = fromProperty;
    }    
    
	public String addGradeGroupServiceElements(String from){
		try{
			List<ClientServiceElement> cseList = new ArrayList<ClientServiceElement>();
			if (from.equals("1")) {
				ggListString = selectedGradeGroupsListString;
				ggListString = checkGradeGroupListForAll(ggListString);
				selectedClientServiceElementString = checkClientServiceElementListForAll(selectedClientServiceElementString);
				if (!MALUtilities.isEmpty(ggListString) && ggListString.size() > 0 && !MALUtilities.isEmpty(selectedClientServiceElementString) && selectedClientServiceElementString.size() > 0) {
					for(String seString : selectedClientServiceElementString){
						for(String ggString : ggListString){
							ClientServiceElement cseTest = serviceElementService.getClientServiceElement(Long.parseLong(seString));
							clientServiceElement = serviceElementService.constructClientServiceElement(cseTest.getClientContractServiceElement(),selectedBillingOption,CorporateEntity.fromCorpId(getAccountCId()),getAccountCode(),selectedProduct,ggString);
							cseList.add(clientServiceElement);
						}
					}
				}
				for(String ggString : ggListString){
					ExternalAccountGradeGroup eagg = serviceElementService.getEagByExternalAccountCodeAndType(getAccountCId(), getAccountType(), getAccountCode(), ggString);
					gradeGroupServiceElements = serviceElementService.getGradeGroupServiceElementsByClientAndGradeGroupWRemoved(clientAccount.getExternalAccountPK().getCId(), clientAccount.getExternalAccountPK().getAccountType(), clientAccount.getExternalAccountPK().getAccountCode(), eagg.getEagId());
					//need to loop thru and remove any that already exists
					for(ClientServiceElement gradeGroupServiceElement : gradeGroupServiceElements){
						for(Iterator<ClientServiceElement> iter = cseList.iterator(); iter.hasNext();){
							ClientServiceElement element = (ClientServiceElement)iter.next();
							if (element.getProduct() == null){
								if(!MALUtilities.isEmpty(element.getExternalAccountGradeGroup()) && (element.getExternalAccountGradeGroup().equals(gradeGroupServiceElement.getExternalAccountGradeGroup()) && element.getClientContractServiceElement().getLeaseElement().getLelId() == (gradeGroupServiceElement.getClientContractServiceElement().getLeaseElement().getLelId()))) {
									iter.remove();	
								}
							}
						}
					}
					productGradeGroupServiceElementsList = serviceElementService.getGradeGroupProductServiceElementsByAccountAndGradeGroup(clientAccount.getExternalAccountPK().getCId(), clientAccount.getExternalAccountPK().getAccountType(), clientAccount.getExternalAccountPK().getAccountCode(), eagg.getEagId());
					//need to loop thru product overrides at grade group level and remove any that exist
					for(ClientServiceElement productServiceElement : productGradeGroupServiceElementsList){
						for(Iterator<ClientServiceElement> iter = cseList.iterator(); iter.hasNext();){
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
			}
				
			if (from.equals("2")) {
				ggListString = selectedGradeGroupsListString2;
				ggListString = checkGradeGroupListForAll(ggListString);
				selectedClientContractServiceElementString = checkClientContractServiceElementListForAll(selectedClientContractServiceElementString);
				if (!MALUtilities.isEmpty(ggListString) && ggListString.size() > 0 && !MALUtilities.isEmpty(selectedClientContractServiceElementString) && selectedClientContractServiceElementString.size() > 0)	{
					for(String cseString : selectedClientContractServiceElementString){
						for(String ggString : ggListString){
							ClientContractServiceElement ccse = serviceElementService.getClientContractServiceElement(Long.parseLong(cseString));
							clientServiceElement = serviceElementService.constructClientServiceElement(ccse,selectedBillingOption,CorporateEntity.fromCorpId(getAccountCId()),getAccountCode(),selectedProduct2,ggString);
							cseList.add(clientServiceElement);
						}
					}
				}
				for(String ggString : ggListString){
					ExternalAccountGradeGroup eagg = serviceElementService.getEagByExternalAccountCodeAndType(getAccountCId(), getAccountType(), getAccountCode(), ggString);
					gradeGroupServiceElements = serviceElementService.getGradeGroupServiceElementsByClientAndGradeGroup(clientAccount.getExternalAccountPK().getCId(), clientAccount.getExternalAccountPK().getAccountType(), clientAccount.getExternalAccountPK().getAccountCode(), eagg.getEagId());
					//need to loop thru and remove any that already exists
					for(ClientServiceElement gradeGroupServiceElement : gradeGroupServiceElements){
						for(Iterator<ClientServiceElement> iter = cseList.iterator(); iter.hasNext();){
							ClientServiceElement element = (ClientServiceElement)iter.next();
							if (element.getProduct() == null){
								if(!MALUtilities.isEmpty(element.getExternalAccountGradeGroup()) && (element.getExternalAccountGradeGroup().equals(gradeGroupServiceElement.getExternalAccountGradeGroup()) && element.getClientContractServiceElement().getLeaseElement().getLelId() == (gradeGroupServiceElement.getClientContractServiceElement().getLeaseElement().getLelId()))) {
									iter.remove();	
								}
							}
						}
					}
					productGradeGroupServiceElementsList = serviceElementService.getGradeGroupProductServiceElementsByAccountAndGradeGroup(clientAccount.getExternalAccountPK().getCId(), clientAccount.getExternalAccountPK().getAccountType(), clientAccount.getExternalAccountPK().getAccountCode(), eagg.getEagId());
					//need to loop thru product overrides at grade group level and remove any that exist
					for(ClientServiceElement productServiceElement : productGradeGroupServiceElementsList){
						for(Iterator<ClientServiceElement> iter = cseList.iterator(); iter.hasNext();){
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
			}
			
			if(!cseList.isEmpty()  && cseList.size() > 0){
				serviceElementService.saveUpdateClientServiceElements(cseList,ClientServiceElementTypeCodes.EXTERNAL_ACCOUNT_GRADE_GROUP.getCode());
				super.addSuccessMessage("process.success","Save Grade Group Service Elements");
			}
			setSelectedClientServiceElementString(null);
			setSelectedGradeGroupsListString(null);		
			setSelectedGradeGroupsListString2(null);
			setGgListString(null);
		}
		catch(Exception ex){
			logger.error(ex);		
			super.addErrorMessage("generic.error", ex.getMessage());
		}
		
		return null;
	}		
	
	public List<String> checkGradeGroupListForAll(List<String> list){
		if(list.contains("ALL")){
			list.clear();
			for(DriverGrade gradeGroup : gradeGroupsList){
				list.add(gradeGroup.getGradeCode());
			}
		}
		return list;
	}
	
	public List<String> checkClientServiceElementListForAll(List<String> list){
		if(list.contains("ALL")){
			list.clear();
			for(ClientServiceElement clientServElement : clientServiceElementsList){
				list.add(String.valueOf(clientServElement.getClientServiceElementId()));
			}
		}
		return list;
	}	
	
	public List<String> checkClientContractServiceElementListForAll(List<String> list){
		if(list.contains("ALL")){
			list.clear();
			for(ClientContractServiceElement clientContractServiceElement : clientContractServiceElementList){
				list.add(String.valueOf(clientContractServiceElement.getClientContractServiceElementId()));
			}
		}
		return list;
	}
	
	public String removeGradeGroupServiceElements(String from){
		try{
			if (from.equals("1")) {
				ggListString = checkGradeGroupListForAll(selectedGradeGroupsListString);
				if (!MALUtilities.isEmpty(ggListString) && ggListString.size() > 0 && !MALUtilities.isEmpty(selectedClientServiceElementString) && selectedClientServiceElementString.size() > 0) { 				
					if(!removeElementList.isEmpty()  && removeElementList.size() > 0){
						serviceElementService.saveUpdateClientServiceElements(removeElementList,ClientServiceElementTypeCodes.EXTERNAL_ACCOUNT_GRADE_GROUP.getCode());
						super.addSuccessMessage("process.success","Remove Grade Group Service Elements");
					}				
				}
			}
			if (from.equals("2")) {
				ggListString = checkGradeGroupListForAll(selectedGradeGroupsListString2);
				if (!MALUtilities.isEmpty(ggListString) && ggListString.size() > 0 && !MALUtilities.isEmpty(selectedClientContractServiceElementString) && selectedClientContractServiceElementString.size() > 0) { 				
					if(!removeElementList.isEmpty()  && removeElementList.size() > 0){
						serviceElementService.saveUpdateClientServiceElements(removeElementList,ClientServiceElementTypeCodes.EXTERNAL_ACCOUNT_GRADE_GROUP.getCode());
						super.addSuccessMessage("process.success","Remove Grade Group Service Elements");
					}				
				}
			}
			setSelectedClientServiceElementString(null);
			setSelectedClientContractServiceElementString(null);
			setSelectedGradeGroupsListString(null);
			setGgListString(null);
			removeElementList.clear();
		}
		catch(Exception ex){
			logger.error(ex);		
			super.addErrorMessage("generic.error", ex.getMessage());
		}
		
		return null;
	}
	
	
	
	//TODO: consider moving this into the service it is duplicated within another class as well.
	public Boolean existsServiceElementParameter(String from) {
		Boolean exists = false;
		removeElementList  = new ArrayList<ClientServiceElement>();
		
		if (from.equals("1")) {
			ggListString = checkGradeGroupListForAll(selectedGradeGroupsListString);
			selectedClientServiceElementString = checkClientServiceElementListForAll(selectedClientServiceElementString);
			
			for(String se : selectedClientServiceElementString){
				for(String gg : ggListString){
					ClientServiceElement clientServiceElement = new ClientServiceElement();
					ClientServiceElement ggServiceElement = new ClientServiceElement();
					//this is client's service element from list
					clientServiceElement = serviceElementService.getClientServiceElement(Long.parseLong(se));
							
					if (clientServiceElement != null) {
						ExternalAccountGradeGroup eagg = serviceElementService.getEagByExternalAccountCodeAndType(getAccountCId(), getAccountType(), getAccountCode(), gg);				
						if (eagg != null) {
							if(selectedProduct != null){
								ggServiceElement = serviceElementService.getGradeGroupProductServiceElementByGradeGroupAndElement(getAccountCId(), getAccountType(), getAccountCode(), eagg.getEagId(), clientServiceElement.getClientContractServiceElement().getLeaseElement().getLelId(), selectedProduct);
							}
							else {
								ggServiceElement = serviceElementService.getGradeGroupServiceElementByGradeGroupAndElement(getAccountCId(), getAccountType(), getAccountCode(), eagg.getEagId(), clientServiceElement.getClientContractServiceElement().getLeaseElement().getLelId());
							}
							if(ggServiceElement != null){
								ggServiceElement.setEndDate(new Date());
								//Need to set removed flag to N for the case of an element that was previously removed from the details page with the REMOVE link
								//Setting the removed flag to N causes history to be lost on these exclusions (strike-throughs),
								//but the business said it does not care about this history, so this works for now.
								ggServiceElement.setRemovedFlag("N");
								removeElementList.add(ggServiceElement);
								List<ClientServiceElementParameter> csepList = serviceElementService.findByClientServiceElementId(ggServiceElement.getClientServiceElementId());
								if (csepList.size() > 0 && !exists) {
									exists = true;
								}
							}
						}
					}
				}
			}
		}
		if (from.equals("2")) {
			ggListString = checkGradeGroupListForAll(selectedGradeGroupsListString2);
			selectedClientContractServiceElementString = checkClientContractServiceElementListForAll(selectedClientContractServiceElementString);
			
			for(String se : selectedClientContractServiceElementString){
				for(String gg : ggListString){
					ClientContractServiceElement clientContractServiceElement = new ClientContractServiceElement();
					ClientServiceElement ggServiceElement = new ClientServiceElement();
					//this is client's contracted service element from list
					clientContractServiceElement = serviceElementService.getClientContractServiceElement(Long.parseLong(se));
							
					if (clientContractServiceElement != null){
						ExternalAccountGradeGroup eagg = serviceElementService.getEagByExternalAccountCodeAndType(getAccountCId(), getAccountType(), getAccountCode(), gg);				
						if (eagg != null) {
							if(selectedProduct2 != null){
								ggServiceElement = serviceElementService.getGradeGroupProductServiceElementByGradeGroupAndElement(getAccountCId(), getAccountType(), getAccountCode(), eagg.getEagId(), clientContractServiceElement.getLeaseElement().getLelId(), selectedProduct2);
							}
							else {							
								ggServiceElement = serviceElementService.getGradeGroupServiceElementByGradeGroupAndElement(getAccountCId(), getAccountType(), getAccountCode(), eagg.getEagId(), clientContractServiceElement.getLeaseElement().getLelId());
							}
							if(ggServiceElement != null){
								ggServiceElement.setEndDate(new Date());
								removeElementList.add(ggServiceElement);
								List<ClientServiceElementParameter> csepList = serviceElementService.findByClientServiceElementId(ggServiceElement.getClientServiceElementId());
								if (csepList.size() > 0 && !exists) {
									exists = true;
								}
							}
						}
					}
				}
			}
		}		
		return exists;
	}	
	
	public void showHideRemoveServiceElementConfirmDialog(String from) {
		if (existsServiceElementParameter(from)) {
			RequestContext context = RequestContext.getCurrentInstance();
			context.addCallbackParam("failure", true);
		}
	}	
	
	@Override
	protected void loadNewPage() {
		Map<String, Object> map = super.thisPage.getInputValues();
		thisPage.setPageUrl(ViewConstants.GRADE_GROUP_SERVICE_ELEMENTS);
		thisPage.setPageDisplayName(ViewConstants.DISPLAY_NAME_GRADE_GROUP_SERVICE_ELEMENTS);
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
	
	 /**
     * Handles page cancel button click event
     * @return The calling view
     */
    public String done(){
    	return super.cancelPage();      	
    }	

	public List<DriverGrade> getGradeGroupsList() {
		return gradeGroupsList;
	}

	public void setGradeGroupsList(List<DriverGrade> gradeGroupsList) {
		this.gradeGroupsList = gradeGroupsList;
	}
	
	public List<ClientContractServiceElement> getClientContractServiceElementList() {
		return clientContractServiceElementList;
	}

	public void setClientContractServiceElementList(
			List<ClientContractServiceElement> clientContractServiceElementList) {
		this.clientContractServiceElementList = clientContractServiceElementList;
	}

	public List<ClientServiceElement> getClientServiceElementsList() {
		return clientServiceElementsList;
	}

	public void setClientServiceElementsList(
			List<ClientServiceElement> clientServiceElementsList) {
		this.clientServiceElementsList = clientServiceElementsList;
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

	public String getSelectedBillingOption() {
		return selectedBillingOption;
	}

	public void setSelectedBillingOption(String selectedBillingOption) {
		this.selectedBillingOption = selectedBillingOption;
	}

	public List<String> getSelectedGradeGroupsListString() {
		return selectedGradeGroupsListString;
	}

	public void setSelectedGradeGroupsListString(
			List<String> selectedGradeGroupsListString) {
		this.selectedGradeGroupsListString = selectedGradeGroupsListString;
	}

	public ClientServiceElement getClientServiceElement() {
		return clientServiceElement;
	}

	public void setClientServiceElement(ClientServiceElement clientServiceElement) {
		this.clientServiceElement = clientServiceElement;
	}

	public List<ClientServiceElement> getGradeGroupServiceElements() {
		return gradeGroupServiceElements;
	}

	public void setGradeGroupServiceElements(
			List<ClientServiceElement> gradeGroupServiceElements) {
		this.gradeGroupServiceElements = gradeGroupServiceElements;
	}

	public List<String> getSelectedClientServiceElementString() {
		return selectedClientServiceElementString;
	}

	public void setSelectedClientServiceElementString(
			List<String> selectedClientServiceElementString) {
		this.selectedClientServiceElementString = selectedClientServiceElementString;
	}

	public List<String> getSelectedClientContractServiceElementString() {
		return selectedClientContractServiceElementString;
	}

	public void setSelectedClientContractServiceElementString(
			List<String> selectedClientContractServiceElementString) {
		this.selectedClientContractServiceElementString = selectedClientContractServiceElementString;
	}

	public List<String> getSelectedGradeGroupsListString2() {
		return selectedGradeGroupsListString2;
	}

	public void setSelectedGradeGroupsListString2(
			List<String> selectedGradeGroupsListString2) {
		this.selectedGradeGroupsListString2 = selectedGradeGroupsListString2;
	}

	public List<String> getGgListString() {
		return ggListString;
	}

	public void setGgListString(List<String> ggListString) {
		this.ggListString = ggListString;
	}

	public String getFromProp() {
		return fromProp;
	}

	public void setFromProp(String fromProp) {
		this.fromProp = fromProp;
	}

	public String getSelectedProduct() {
		return selectedProduct;
	}

	public void setSelectedProduct(String selectedProduct) {
		this.selectedProduct = selectedProduct;
	}

	public String getSelectedProduct2() {
		return selectedProduct2;
	}

	public void setSelectedProduct2(String selectedProduct2) {
		this.selectedProduct2 = selectedProduct2;
	}

	public List<ClientServiceElement> getProductGradeGroupServiceElementsList() {
		return productGradeGroupServiceElementsList;
	}

	public void setProductGradeGroupServiceElementsList(
			List<ClientServiceElement> productGradeGroupServiceElementsList) {
		this.productGradeGroupServiceElementsList = productGradeGroupServiceElementsList;
	}

	public List<Product> getActiveClientProductList() {
		return activeClientProductList;
	}

	public void setActiveClientProductList(List<Product> activeClientProductList) {
		this.activeClientProductList = activeClientProductList;
	}
}
