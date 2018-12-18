package com.mikealbert.vision.view.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.primefaces.context.RequestContext;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.mikealbert.data.entity.ClientAgreement;
import com.mikealbert.data.entity.ClientContractServiceElement;
import com.mikealbert.data.entity.ContractAgreement;
import com.mikealbert.data.entity.ExternalAccount;
import com.mikealbert.data.entity.ExternalAccountPK;
import com.mikealbert.data.entity.LeaseElement;
import com.mikealbert.data.vo.ClientFinanceVO;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.service.CustomerAccountService;
import com.mikealbert.service.ServiceElementService;
import com.mikealbert.util.MALUtilities;
import com.mikealbert.vision.view.ViewConstants;

@Component
@Scope("view")
public class ClientAgreementBean extends StatefulBaseBean{
	private static final long serialVersionUID = -4589582730235934254L;

	@Resource CustomerAccountService customerAccountService;
	@Resource ServiceElementService serviceElementService;
	
	private int DEFAULT_DATATABLE_HEIGHT = 150;
	private List<ClientAgreement> clientAgreementList;
	private List<ExternalAccount> clientAccountList;
	private ExternalAccount clientAccount;
	private List<ClientContractServiceElement> clientContractServiceElementList;
	private ClientAgreement originalClientAgreement;
	private ClientAgreement selectedClientAgreement;
	private List<ContractAgreement> contractAgreementList;
	private ContractAgreement selectedContractAgreement;
	private ClientContractServiceElement selectedClientContractServiceElement;
	private LeaseElement selectedServiceElement;
	private boolean disableContractAgreementSelectOneMenu;
	private boolean disableServiceElementLOV;
	private List<ClientAgreement> distinctClientAgreementNumberList;
	private List<Long> listOfLeaseElementsToExcludeFromLov;
	private boolean addClientContractServiceElement;
	private boolean editClientContractServiceElement;
	private boolean addClientAgreement;
	private boolean editClientAgreement;
	
	private String CONTRACT_AGREEMENT_UI_ID = "contractAgreement";
	private String AGREEMENT_NUMBER_UI_ID = "agreementNumber";
	private String AGREEMENT_DATE_UI_ID = "agreementDate";
	private String SERVICE_ELEMENT_UI_ID = "serviceElement";
	private String AGREEMENT_UI_ID = "agreementNumberDlg";
	
	private String dialogHeader;
	final static String DIALOG_HEADER_ADD = "Add";
	final static String DIALOG_HEADER_EDIT = "Add/Edit";
	
	@PostConstruct
	public void init() {
	    	initializeDataTable(500, 500, new int[] {10, 10, 10, 10, 10, 10}).setHeight(DEFAULT_DATATABLE_HEIGHT); 
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
			clientAccountList = customerAccountService.findOpenCustomerAccountsByCode(getClientAccount().getExternalAccountPK().getAccountCode());
			
			if(clientAccountList != null && !clientAccountList.isEmpty()){
				setClientAccount(clientAccountList.get(0));
			}
			
			setClientAgreementList(serviceElementService.getClientAgreementsByClient(clientAccount.getExternalAccountPK().getCId(), clientAccount.getExternalAccountPK().getAccountType(), clientAccount.getExternalAccountPK().getAccountCode()));
			setClientContractServiceElementList(serviceElementService.getClientContractServiceElementsByClient(clientAccount.getExternalAccountPK().getCId(), clientAccount.getExternalAccountPK().getAccountType(), clientAccount.getExternalAccountPK().getAccountCode()));
			setContractAgreementList(serviceElementService.getAllContractAgreements());
		} catch (Exception e) {
			logger.error(e);
			 if(e  instanceof MalBusinessException){				 
				 super.addErrorMessage(e.getMessage()); 
			 }else{
				 super.addErrorMessage("generic.error.occured.while", " building screen.");
			 }
		}
	}
	
	public void editAgreement(ClientAgreement clientAgreement){
		originalClientAgreement = new ClientAgreement();
		BeanUtils.copyProperties(clientAgreement, originalClientAgreement);
		selectedClientAgreement = clientAgreement;
		selectedContractAgreement = clientAgreement.getContractAgreement();
		setDisableContractAgreementSelectOneMenu(true);
		setContractAgreementList(serviceElementService.getAllContractAgreements());
		addClientAgreement = false;
		editClientAgreement = true;
		setDialogHeader(DIALOG_HEADER_EDIT);
	}
	
	public void addAgreement(){
		selectedClientAgreement = new ClientAgreement();
		selectedClientAgreement.setExternalAccount(clientAccount);
		selectedContractAgreement = new ContractAgreement();
		selectedClientAgreement.setActiveInd("Y");
		setDisableContractAgreementSelectOneMenu(false);
		limitContractAgreementList();
		addClientAgreement = true;
		editClientAgreement = false;
		setDialogHeader(DIALOG_HEADER_ADD);
	}
	
	public String saveClientAgreement(){
		boolean validationFail = false;
		//Agreement is required
		if(selectedContractAgreement == null || MALUtilities.isEmpty(selectedContractAgreement)){
			super.addErrorMessageSummary(CONTRACT_AGREEMENT_UI_ID, "required.field", "Agreement ");
			validationFail = true;
		}
		//Agreement Number is required
		if(selectedClientAgreement == null || MALUtilities.isEmpty(selectedClientAgreement.getAgreementNumber())){
			super.addErrorMessageSummary(AGREEMENT_NUMBER_UI_ID, "required.field", "Agreement Number ");
			validationFail = true;
		}
		//Agreement Date is required
		if(selectedClientAgreement == null || MALUtilities.isEmpty(selectedClientAgreement.getAgreementDate())){
			super.addErrorMessageSummary(AGREEMENT_DATE_UI_ID, "required.field", "Agreement Date ");
			validationFail = true;
		}
		//Agreement Number has to be unique
		if(selectedClientAgreement != null && !MALUtilities.isEmpty(selectedClientAgreement.getAgreementNumber())){
			long count = serviceElementService.findByAgreementNumberCount(selectedClientAgreement.getAgreementNumber(), selectedClientAgreement.getClientAgreementId());
			if(count > 0){
				super.addErrorMessageSummary(AGREEMENT_NUMBER_UI_ID, "agreementNo.exists.error", selectedClientAgreement.getAgreementNumber());
				validationFail = true;
			}
		}
		//This will check that no other contract agreements have been created for the Contract Agreement selected
		if(addClientAgreement && selectedContractAgreement != null && !MALUtilities.isEmpty(selectedContractAgreement.getAgreementCode())){
			long count = serviceElementService.getClientAgreementByContractAgreementAndClientCount(selectedContractAgreement.getAgreementCode(), clientAccount.getExternalAccountPK().getCId(), clientAccount.getExternalAccountPK().getAccountType(), clientAccount.getExternalAccountPK().getAccountCode());
			if(count > 0){
				super.addErrorMessageSummary(CONTRACT_AGREEMENT_UI_ID, "contractAgreement.exists.error", selectedContractAgreement.getAgreementCode() );
				limitContractAgreementList();
				validationFail = true;
			}
		}
		if(!validationFail){
			if (editClientAgreement) {
				if (!selectedClientAgreement.getAgreementNumber().equals(originalClientAgreement.getAgreementNumber()) && MALUtilities.compateDates(selectedClientAgreement.getAgreementDate(), originalClientAgreement.getAgreementDate()) != 0) {
					ClientAgreement origClientAgreement = getOriginalClientAgreement();
					origClientAgreement.setActiveInd("N");
					origClientAgreement.setClientAgreementId(null);
					serviceElementService.saveOrUpdateClientAgreement(origClientAgreement, selectedContractAgreement);
				}
			}
			serviceElementService.saveOrUpdateClientAgreement(selectedClientAgreement, selectedContractAgreement);
			setClientAgreementList(serviceElementService.getClientAgreementsByClient(clientAccount.getExternalAccountPK().getCId(), clientAccount.getExternalAccountPK().getAccountType(), clientAccount.getExternalAccountPK().getAccountCode()));
			super.addSuccessMessage("process.success","Save ");
		}else{
			RequestContext context = RequestContext.getCurrentInstance();
			context.addCallbackParam("failure", true);
			return "validationFailure";
		}
		return null;
	}
	
	public String saveAndAddClientAgreement(){
		String validation = saveClientAgreement();
		if(validation == null){
			addAgreement();
		}
		return null;
	}
	
	public void editClientContractServiceElement(ClientContractServiceElement clientContractServiceElement){
		selectedClientContractServiceElement = clientContractServiceElement;
		selectedServiceElement = clientContractServiceElement.getLeaseElement();
		setDisableServiceElementLOV(true);
		limitClientAgreementNumberList();
		compileLeaseElementsToExcludeFromLov();
		editClientContractServiceElement = true;
		addClientContractServiceElement = false;
		setDialogHeader(DIALOG_HEADER_EDIT);
	}
	
	public void addClientContractServiceElement(){
		setSelectedClientContractServiceElement(new ClientContractServiceElement());
		setSelectedServiceElement(new LeaseElement());
		getSelectedClientContractServiceElement().setClientAgreement(new ClientAgreement());
		setDisableServiceElementLOV(false);
		limitClientAgreementNumberList();
		compileLeaseElementsToExcludeFromLov();
		editClientContractServiceElement = false;
		addClientContractServiceElement = true;
		setDialogHeader(DIALOG_HEADER_ADD);
	}
	
	public String saveServiceElementAgreement(){
		boolean validationFail = false;
		boolean elementExistInLov = false;
		//Element Name must not be null
		if(selectedServiceElement == null || MALUtilities.isEmpty(selectedServiceElement.getElementName())){
			super.addErrorMessageSummary(SERVICE_ELEMENT_UI_ID, "required.field", "Service Element ");
			validationFail = true;
		}
		//Agreement Number must not be null
		if(selectedClientContractServiceElement == null || selectedClientContractServiceElement.getClientAgreement() == null || MALUtilities.isEmpty(selectedClientContractServiceElement.getClientAgreement().getClientAgreementId())){
			super.addErrorMessageSummary(AGREEMENT_UI_ID, "required.field", "Agreement Number ");
			validationFail = true;
		}
		
		if (addClientContractServiceElement) {
			//Lease Element must exist in lease_elements table. Check only, if we are going to Add service elements.
			if(selectedServiceElement != null && !MALUtilities.isEmpty(selectedServiceElement.getElementName())){
				LeaseElement checkElement = serviceElementService.getLeaseElementByName(selectedServiceElement);
				if(checkElement == null || MALUtilities.isEmpty(checkElement.getElementName())){
					super.addErrorMessageSummary(SERVICE_ELEMENT_UI_ID, "decode.noMatchFound.msg", "Service Element " + selectedServiceElement.getElementName());
					validationFail = true;
				}else{		
					//Lease Element must not be contracted for the client already
					if(addClientContractServiceElement == true && serviceElementService.getContractElementByClientElementNameAndClientCount(checkElement.getElementName(), clientAccount.getExternalAccountPK().getCId(), clientAccount.getExternalAccountPK().getAccountType(), clientAccount.getExternalAccountPK().getAccountCode()) > 0){
						super.addErrorMessageSummary(SERVICE_ELEMENT_UI_ID, "serviceElement.exists.error", checkElement.getElementName());
						compileLeaseElementsToExcludeFromLov();
						validationFail = true;
					}else{
						//Lease Element exist in lease_elements table, but not exist in LOV
						List<LeaseElement> elementList = serviceElementService.findAllFilterByFinanceTypeAndElementList(selectedServiceElement.getElementName(), this.getListOfLeaseElementsToExcludeFromLov(), null);					
						for (LeaseElement leaseElement : elementList) {
							if (leaseElement.getElementName().equalsIgnoreCase(selectedServiceElement.getElementName())) {
								elementExistInLov = true;
								break;
							}
						}
						if (!elementExistInLov) {
							super.addErrorMessageSummary(SERVICE_ELEMENT_UI_ID, "not.valid", "Service element " + selectedServiceElement.getElementName());						
							validationFail = true;
						}else{							
							selectedServiceElement = checkElement;
							elementExistInLov = false;
						}
					}				
				}
			}			
		} 
		
		if(!validationFail){
			serviceElementService.saveOrUpdateClientContractServiceElement(selectedClientContractServiceElement, selectedServiceElement);
			setClientContractServiceElementList(serviceElementService.getClientContractServiceElementsByClient(clientAccount.getExternalAccountPK().getCId(), clientAccount.getExternalAccountPK().getAccountType(), clientAccount.getExternalAccountPK().getAccountCode()));
			super.addSuccessMessage("process.success","Save ");
		}else{
			RequestContext context = RequestContext.getCurrentInstance();
			context.addCallbackParam("failure", true);
			return "validationFailure";
		}
		return null;
	}
	
	public String saveAndAddServiceElementAgreement(){
		String validation = saveServiceElementAgreement();
		if(validation == null){
			addClientContractServiceElement();
		}
		return null;
	}
	
	public void closeAgreementDialog(){
		setClientAgreementList(serviceElementService.getClientAgreementsByClient(clientAccount.getExternalAccountPK().getCId(), clientAccount.getExternalAccountPK().getAccountType(), clientAccount.getExternalAccountPK().getAccountCode()));
		setClientContractServiceElementList(serviceElementService.getClientContractServiceElementsByClient(clientAccount.getExternalAccountPK().getCId(), clientAccount.getExternalAccountPK().getAccountType(), clientAccount.getExternalAccountPK().getAccountCode()));
	}
	
	public void closeServiceElementDialog(){
		setClientContractServiceElementList(serviceElementService.getClientContractServiceElementsByClient(clientAccount.getExternalAccountPK().getCId(), clientAccount.getExternalAccountPK().getAccountType(), clientAccount.getExternalAccountPK().getAccountCode()));
	}
	
	@Override
	protected void loadNewPage() {
		Map<String, Object> map = super.thisPage.getInputValues();
		thisPage.setPageUrl(ViewConstants.CLIENT_AGREEMENTS);
		thisPage.setPageDisplayName(ViewConstants.DISPLAY_NAME_CLIENT_AGREEMENTS);
		setClientAccount(new ExternalAccount());
		getClientAccount().setExternalAccountPK(new ExternalAccountPK());
		this.getClientAccount().getExternalAccountPK().setCId((Long) map.get(ViewConstants.VIEW_PARAM_C_ID));
		this.getClientAccount().getExternalAccountPK().setAccountType(((String) map.get(ViewConstants.VIEW_PARAM_ACCOUNT_TYPE)));
		this.getClientAccount().getExternalAccountPK().setAccountCode((String) map.get(ViewConstants.VIEW_PARAM_ACCOUNT_CODE));
	}

	@Override
	protected void restoreOldPage() {
		logger.info("-- Method name: restoreOldPage start");
		Map<String, Object> map = super.thisPage.getRestoreStateValues();
		setClientAccount(new ExternalAccount());
		getClientAccount().setExternalAccountPK(new ExternalAccountPK());
		this.getClientAccount().getExternalAccountPK().setCId((Long) map.get(ViewConstants.VIEW_PARAM_C_ID));
		this.getClientAccount().getExternalAccountPK().setAccountType(((String) map.get(ViewConstants.VIEW_PARAM_ACCOUNT_TYPE)));
		this.getClientAccount().getExternalAccountPK().setAccountCode((String) map.get(ViewConstants.VIEW_PARAM_ACCOUNT_CODE));
		logger.info("-- Method name: restoreOldPage end");
	}
	
	public void restoreStateValues(ClientFinanceVO clientFinanceVO) {
		Map<String, Object> restoreStateValues = new HashMap<String, Object>();
		restoreStateValues.put(ViewConstants.VIEW_PARAM_C_ID, clientFinanceVO.getEaCId());
		restoreStateValues.put(ViewConstants.VIEW_PARAM_ACCOUNT_TYPE, clientFinanceVO.getEaAccountType());
		restoreStateValues.put(ViewConstants.VIEW_PARAM_ACCOUNT_CODE, clientFinanceVO.getEaAccountCode());
		super.saveRestoreStateValues(restoreStateValues);
	}
	
	 /**
     * Handles page cancel button click event
     * @return The calling view
     */
    public String done(){
    	return super.cancelPage();      	
    }
	
	public void limitContractAgreementList(){
		List<ClientAgreement> clientAgreementList = serviceElementService.getClientAgreementsByClient(clientAccount.getExternalAccountPK().getCId(), clientAccount.getExternalAccountPK().getAccountType(), clientAccount.getExternalAccountPK().getAccountCode());
		for(ClientAgreement clientAgreement : clientAgreementList){
			for(ContractAgreement contractAgreement : contractAgreementList){
				if(clientAgreement.getContractAgreement().equals(contractAgreement)){
					contractAgreementList.remove(contractAgreement);
					break;
				}
			}
		}
	}
	
	public void limitClientAgreementNumberList(){
		boolean addAgreement = true;
		distinctClientAgreementNumberList = new ArrayList<ClientAgreement>();
		for(ClientAgreement clientAgreement : clientAgreementList){
			addAgreement= true;
			for(ClientAgreement distinctAgreement : distinctClientAgreementNumberList){
				if(clientAgreement.getAgreementNumber().equals(distinctAgreement.getAgreementNumber())){
					addAgreement = false;
					break;
				}
			}
			if(addAgreement){
				distinctClientAgreementNumberList.add(clientAgreement);
			}
		}
	}
	
	public void compileLeaseElementsToExcludeFromLov(){
		List<ClientContractServiceElement> clientContractServiceElementList = serviceElementService.getClientContractServiceElementsByClient(clientAccount.getExternalAccountPK().getCId(), clientAccount.getExternalAccountPK().getAccountType(), clientAccount.getExternalAccountPK().getAccountCode());
		listOfLeaseElementsToExcludeFromLov = new ArrayList<Long>();
		for(ClientContractServiceElement element : clientContractServiceElementList){
			listOfLeaseElementsToExcludeFromLov.add(element.getLeaseElement().getLelId());
		}
	}
	
	public boolean leaseElementExistsInList(LeaseElement leaseElement){
		if(clientContractServiceElementList != null && leaseElement != null && !MALUtilities.isEmpty(leaseElement.getElementName())){
			for(ClientContractServiceElement ccse : clientContractServiceElementList){
				if(ccse.getLeaseElement().getElementName().equals(leaseElement.getElementName())){
					return true;
				}
			}
		}
		return false;
	}
	
	
	public ExternalAccount getClientAccount() {
		return clientAccount;
	}

	public void setClientAccount(ExternalAccount clientAccount) {
		this.clientAccount = clientAccount;
	}

	public List<ClientAgreement> getClientAgreementList() {
		return clientAgreementList;
	}

	public void setClientAgreementList(List<ClientAgreement> clientAgreementList) {
		this.clientAgreementList = clientAgreementList;
	}

	public ClientAgreement getSelectedClientAgreement() {
		return selectedClientAgreement;
	}

	public void setSelectedClientAgreement(ClientAgreement selectedClientAgreement) {
		this.selectedClientAgreement = selectedClientAgreement;
	}

	public ContractAgreement getSelectedContractAgreement() {
		return selectedContractAgreement;
	}

	public void setSelectedContractAgreement(ContractAgreement selectedContractAgreement) {
		this.selectedContractAgreement = selectedContractAgreement;
	}

	public List<ContractAgreement> getContractAgreementList() {
		return contractAgreementList;
	}

	public void setContractAgreementList(List<ContractAgreement> contractAgreementList) {
		this.contractAgreementList = contractAgreementList;
	}

	public boolean isDisableContractAgreementSelectOneMenu() {
		return disableContractAgreementSelectOneMenu;
	}

	public void setDisableContractAgreementSelectOneMenu(
			boolean disableContractAgreementSelectOneMenu) {
		this.disableContractAgreementSelectOneMenu = disableContractAgreementSelectOneMenu;
	}

	public ClientContractServiceElement getSelectedClientContractServiceElement() {
		return selectedClientContractServiceElement;
	}

	public void setSelectedClientContractServiceElement(
			ClientContractServiceElement selectedClientContractServiceElement) {
		this.selectedClientContractServiceElement = selectedClientContractServiceElement;
	}

	public List<ClientContractServiceElement> getClientContractServiceElementList() {
		return clientContractServiceElementList;
	}

	public void setClientContractServiceElementList(
			List<ClientContractServiceElement> clientContractServiceElementList) {
		this.clientContractServiceElementList = clientContractServiceElementList;
	}

	public boolean isDisableServiceElementLOV() {
		return disableServiceElementLOV;
	}

	public void setDisableServiceElementLOV(boolean disableServiceElementLOV) {
		this.disableServiceElementLOV = disableServiceElementLOV;
	}

	public LeaseElement getSelectedServiceElement() {
		return selectedServiceElement;
	}

	public void setSelectedServiceElement(LeaseElement selectedServiceElement) {
		this.selectedServiceElement = selectedServiceElement;
	}

	public List<ClientAgreement> getDistinctClientAgreementNumberList() {
		return distinctClientAgreementNumberList;
	}

	public void setDistinctClientAgreementNumberList(
			List<ClientAgreement> distinctClientAgreementNumberList) {
		this.distinctClientAgreementNumberList = distinctClientAgreementNumberList;
	}

	public List<Long> getListOfLeaseElementsToExcludeFromLov() {
		return listOfLeaseElementsToExcludeFromLov;
	}

	public void setListOfLeaseElementsToExcludeFromLov(
			List<Long> listOfLeaseElementsToExcludeFromLov) {
		this.listOfLeaseElementsToExcludeFromLov = listOfLeaseElementsToExcludeFromLov;
	}

	public boolean isAddClientContractServiceElement() {
		return addClientContractServiceElement;
	}

	public void setAddClientContractServiceElement(
			boolean addClientContractServiceElement) {
		this.addClientContractServiceElement = addClientContractServiceElement;
	}

	public boolean isEditClientContractServiceElement() {
		return editClientContractServiceElement;
	}

	public void setEditClientContractServiceElement(
			boolean editClientContractServiceElement) {
		this.editClientContractServiceElement = editClientContractServiceElement;
	}

	public boolean isAddClientAgreement() {
		return addClientAgreement;
	}

	public void setAddClientAgreement(boolean addClientAgreement) {
		this.addClientAgreement = addClientAgreement;
	}

	public boolean isEditClientAgreement() {
		return editClientAgreement;
	}

	public void setEditClientAgreement(boolean editClientAgreement) {
		this.editClientAgreement = editClientAgreement;
	}

	public String getDialogHeader() {
		return dialogHeader;
	}

	public void setDialogHeader(String dialogHeader) {
		this.dialogHeader = dialogHeader;
	}

	public ClientAgreement getOriginalClientAgreement() {
		return originalClientAgreement;
	}

	public void setOriginalClientAgreement(ClientAgreement originalClientAgreement) {
		this.originalClientAgreement = originalClientAgreement;
	}
}
