package com.mikealbert.vision.view.bean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.faces.event.AjaxBehaviorEvent;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.mikealbert.common.MalConstants;
import com.mikealbert.data.entity.ClientContact;
import com.mikealbert.data.entity.ClientPoint;
import com.mikealbert.data.entity.ClientSystem;
import com.mikealbert.data.entity.Contact;
import com.mikealbert.data.entity.CostCentreCode;
import com.mikealbert.data.entity.ExternalAccount;
import com.mikealbert.data.vo.PointOfCommunicationVO;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.service.ClientPOCService;
import com.mikealbert.service.CostCenterService;
import com.mikealbert.service.CustomerAccountService;
import com.mikealbert.util.MALUtilities;
import com.mikealbert.vision.view.ViewConstants;
import com.mikealbert.vision.view.link.EditViewClientLink;

@Component
@Scope("view")
public class ClientPOCBean extends StatefulBaseBean implements EditViewClientLink {	
	private static final long serialVersionUID = -7170421837551409230L;
	
    @Resource ClientPOCService clientPOCService;
    @Resource CustomerAccountService customerAccountService;
    @Resource CostCenterService costCenterService;
	
	private String accountCode;
	private String accountType;
	private Long cId;
	private ClientPoint selectedClientPoint;
	private ExternalAccount clientAccount;
	private PointOfCommunicationVO defaultPOCVO;
	private String defaultClientContactName;
	private List<PointOfCommunicationVO> pocVOs;
	private boolean contactsAssignable;
	private boolean costCenterAvailable;
	private List<CostCentreCode> availableCostCenters;	
	private ClientSystem selectedClientSystem;
	private List<ClientSystem> clientSystems;
	private List<PointOfCommunicationVO> rowList;
	
	int DEFAULT_DATATABLE_HEIGHT = 225;
	
		
	/**
	 * Initializes the bean
	 */
    @PostConstruct
    public void init(){    	
    	logger.info( "-- Method name: init start");
    	initializeDataTable(600, 500, new int[] {15, 15, 15, 15, 10, 10}).setHeight(DEFAULT_DATATABLE_HEIGHT);      	
    	super.openPage();
    	
        try { 
			setClientSystems(lookupCacheService.getClientSystemsWithPOCsExceptSystemAll());
			loadData();
			popDefaultPOC();
			setAvailableCostCenters(costCenterService.getCostCenters(getClientAccount()));
        	setCostCenterAvailable(!MALUtilities.isEmpty(getAvailableCostCenters()) && getAvailableCostCenters().size() > 0);
        	setContactsAssignable(hasContacts(getDefaultPOCVO()));	
		} catch(Exception ex) {
			logger.error(ex);
			super.addErrorMessage("generic.error", ex.getMessage());
		}
        
    	logger.info("-- Method name: init end");        
    }
    
	public void loadData() {
		try {
			if(accountCode != null){
				this.clientAccount = customerAccountService.getOpenCustomerAccountByCode(getAccountCode(), getLoggedInUser().getCorporateEntity());
				loadRows();
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
       
    public void selectOneMenuListener(AjaxBehaviorEvent event) {
	    try {
	    	loadRows();
			popDefaultPOC();
	    } catch (Exception e) {
			 logger.error(e);
			 if(e  instanceof MalBusinessException){				 
				 super.addErrorMessage(e.getMessage()); 
			 }else{
				 super.addErrorMessage("generic.error.occured.while", " building screen.");
			 }
		}
	}	
    
    private void loadRows() throws Exception {
		try{
			ClientSystem clientSystem = getSelectedClientSystem();
			if (!MALUtilities.isEmpty(getcId()) && !MALUtilities.isEmpty(getAccountType()) && !MALUtilities.isEmpty(getAccountCode())) {
				if (MALUtilities.isEmpty(clientSystem)){
					rowList = clientPOCService.getPointOfCommunicationVOs(getClientAccount());
				}
				else{
//					rowList = clientPOCService.getPointOfCommunicationVOs(getClientAccount(), clientSystem.getClientSystemId());
					rowList = getSelectedAndDefaultPOCRows(clientSystem);
				}
				setPocVOs(rowList);	
				Collections.sort(rowList, pointOfCommunicationVOComparator);
		}
				
		}catch(Exception ex){
			ex.printStackTrace(); 
		}
	}    
   
	/*
	 * Sort PontOfCommunicationVO's based on ClientSystem Description (POC Category Description) ASC order
	 * 
	 */
	Comparator<PointOfCommunicationVO> pointOfCommunicationVOComparator = new Comparator<PointOfCommunicationVO>() {		
		public int compare(PointOfCommunicationVO pocVO1, PointOfCommunicationVO pocVO2) {
			String pocCategory1 = pocVO1.getPoc().getClientSystem().getDescription();
			String pocCategory2 = pocVO2.getPoc().getClientSystem().getDescription();
				
			return  pocCategory1.compareToIgnoreCase(pocCategory2);
		}
	};
    
    /**
     * Load Default and selected category POC details 
     * @param clientSystem
     * @return
     */
    private List<PointOfCommunicationVO> getSelectedAndDefaultPOCRows(ClientSystem clientSystem) {
    	List<PointOfCommunicationVO> rowList = new ArrayList<PointOfCommunicationVO>();
    	List<PointOfCommunicationVO> records = clientPOCService.getPointOfCommunicationVOs(getClientAccount());
    	
    	for(PointOfCommunicationVO pointOfCommunicationVO : records) {
			if(pointOfCommunicationVO.getPoc().getName().contains("DEFAULT") || clientSystem.getClientSystemId().equals(pointOfCommunicationVO.getPoc().getClientSystem().getClientSystemId())) {
				rowList.add(pointOfCommunicationVO);
			}
		}
    	return rowList;
	}
    
    /**
     * Requests the view that will allow the user to assign contacts to the
     * client's default POC. 
     */
    public void editClientDefaultContacts(){
    	assignContacts(getDefaultPOCVO());
    }    
    
    /*
     * Initialize the next page values
     */
	private void initNextPageValues(PointOfCommunicationVO pocVO) {
		Map<String, Object> nextPageValues = new HashMap<String, Object>();				
		nextPageValues.put(ViewConstants.VIEW_PARAM_ACCOUNT_CODE, getAccountCode());
		nextPageValues.put(ViewConstants.VIEW_PARAM_ACCOUNT_TYPE, getAccountType());
		nextPageValues.put(ViewConstants.VIEW_PARAM_C_ID, getcId().toString());		
		nextPageValues.put(ViewConstants.VIEW_PARAM_CPNT_ID, pocVO.getPoc().getClientPointId());
		nextPageValues.put(ViewConstants.VIEW_PARAM_CLIENT_SYSTEM_FILTER, getSelectedClientSystem());
		nextPageValues.put(ViewConstants.VIEW_PARAM_DEFAULT_POC_VO, getDefaultPOCVO());
		
		if(!MALUtilities.isEmpty(pocVO.getClientPOC())){	
			nextPageValues.put(ViewConstants.VIEW_PARAM_CPNTA_ID, pocVO.getClientPOC().getClientPointAccountId());		
		} else {
			nextPageValues.put(ViewConstants.VIEW_PARAM_CPNTA_ID, null);			
		}
		
		this.saveNextPageInitStateValues(nextPageValues);
	}    

	/*
     * Initialize the restore state values
     */
	private void initRestoreStateValues(PointOfCommunicationVO pocVO) {
		Map<String, Object> restoreStateValues = new HashMap<String, Object>();
		restoreStateValues.put(ViewConstants.VIEW_PARAM_ACCOUNT_CODE, getAccountCode());
		restoreStateValues.put(ViewConstants.VIEW_PARAM_ACCOUNT_TYPE, getAccountType());
		restoreStateValues.put(ViewConstants.VIEW_PARAM_C_ID, getcId().toString());	
		restoreStateValues.put(ViewConstants.VIEW_PARAM_CPNT_ID, pocVO.getPoc().getClientPointId());
		restoreStateValues.put(ViewConstants.VIEW_PARAM_CLIENT_SYSTEM_FILTER, getSelectedClientSystem());
		restoreStateValues.put(ViewConstants.VIEW_PARAM_DEFAULT_POC_VO, getDefaultPOCVO());
		
		if(!MALUtilities.isEmpty(pocVO.getClientPOC())){
			restoreStateValues.put(ViewConstants.VIEW_PARAM_CPNTA_ID, pocVO.getClientPOC().getClientPointAccountId());
		} else {
			restoreStateValues.put(ViewConstants.VIEW_PARAM_CPNTA_ID, null);			
		}
		
		super.saveRestoreStateValues(restoreStateValues);
	}
	
	/**
     * Requests the view that will allow the users to assign contacts to a
     * POC. At a minimum the account information and the selected POC needs
     * to be passed to the view.
     */
    public void assignContacts(PointOfCommunicationVO pocVO){
    	
    	initRestoreStateValues(pocVO);    	
		
		initNextPageValues(pocVO);
		
		this.forwardToURL(ViewConstants.CLIENT_POC_CONTACTS);    	
    }
    
    /**
     * Requests the view that will allow the users to view the POC's 
     * Cost Centers and enable the user to assign contacts to them. 
     */
    public void viewCostCenters(PointOfCommunicationVO pocVO){
    	
    	initRestoreStateValues(pocVO);
		
    	initNextPageValues(pocVO);
		
		super.forwardToURL(ViewConstants.CLIENT_POC_COST_CENTERS);    	
    }

    
    /**
     * Handles page save button click event
     * @return The calling view or null based on whether the process succeeded for failed, respectively
     */
    public String save(){ 
    	logger.info("-- Method name: save start");
		
    	String nextPage = null;
    	
		logger.info("-- Method name: save end");
		
    	return nextPage;
    }
    
    public void autoSave(){ 
    	try{
    		logger.info("-- Method name: autosave start");

		} catch (Exception ex) {
			logger.error(ex);
			handleException("generic.error",new String[]{ex.getMessage()}, ex, null);
			return;
		}
    	logger.info("-- Method name: autosave end");
    }  
    
           
    /**
     * Handles page cancel button click event
     * @return The calling view
     */
    public String cancel(){
    	//return super.cancelPage();
    	return null;
    }
    
    
    /**
     * Retrieves the contacts (excluding driver) that are assigned to the
     * client's POC.
     * @param clientPoint Point of Communication
     * @return List<CLientContact> that are assigned to the client's POC (excluding driver)
     */
    public List<ClientContact> assignedContacts(PointOfCommunicationVO pocVO){
       List<ClientContact> filteredClientContacts = null;
    	
    	if(!MALUtilities.isEmpty(pocVO) && !MALUtilities.isEmpty(pocVO.getClientPOC())){
    		if(!MALUtilities.isEmpty(pocVO.getClientPOC().getClientContacts())){
    			filteredClientContacts = new ArrayList<ClientContact>();
    			
    			for(ClientContact cc : pocVO.getClientPOC().getClientContacts()){
    				if((MALUtilities.isEmpty(cc.getDrvInd()) || cc.getDrvInd().equals(MalConstants.FLAG_N)) 
    						&& MALUtilities.isEmpty(cc.getCostCentreCode())){
        				filteredClientContacts.add(cc);    					
    				}
 
    			}
    		}
    	}
    	
    	return filteredClientContacts;
    } 
    
    /**
     * Determines whether contacts have been assigned to the client POC or not.
     * Driver is excluded from the evaluation
     * @param pocVO
     * @return boolean indicating whether a contacts are assigned or not
     */
    public boolean hasContacts(PointOfCommunicationVO pocVO){
    	boolean hasContacts = false;
    	List<ClientContact> clientContacts;
    	
    	clientContacts = assignedContacts(pocVO);
    	if(!MALUtilities.isEmpty(clientContacts) && clientContacts.size() > 0){
    		hasContacts = true;
    	} 
    	
    	return hasContacts;
    }
    
    /**
     * Evaluates the select POCVO and determines whether the 
     * the POC is assigned to the client and has at lease one
     * cost center with a contact assigned or not.
     * @param pocVO
     * @return boolean indicating whether a contact is assigned to CC
     */
    public boolean hasCostCenterContact(PointOfCommunicationVO pocVO){
    	boolean hasCostCenterContact = false;
    	List<ClientContact> clientContacts;    	
    	
    	if(!MALUtilities.isEmpty(pocVO.getClientPOC())){
    		clientContacts = pocVO.getClientPOC().getClientContacts();
        	for(ClientContact clientContact : clientContacts){
        		if(!MALUtilities.isEmpty(clientContact.getCostCentreCode())){
        			hasCostCenterContact = true;
        			break;
        		}
        	}    		
    	}   		    		
    	
    	return hasCostCenterContact;
    }
    
        
    /**
     * Extracts the default POC from the POC list.
     * The default POC will be stored in a separate property.
     * The default POC should only have one contact (not driver)
     * assigned, per business rule. That contact's first and last name
     * is formatted and saved to a property.
     */
    private void popDefaultPOC(){
    	Contact contact;
    	List<ClientContact> defaultContacts;
    	
    	setDefaultClientContactName(null);
    	
    	for(PointOfCommunicationVO pocVO : getPocVOs()){
    		if (pocVO.getPoc().getName().contains("DEFAULT")){
    			setDefaultPOCVO(pocVO);
    			break;
    		}
    	}
    	
    	getPocVOs().remove(getDefaultPOCVO());
    	
    	defaultContacts = assignedContacts(getDefaultPOCVO());
    	if(!MALUtilities.isEmpty(defaultContacts) && defaultContacts.size() > 0){
    		contact = defaultContacts.get(0).getContact();
    		setDefaultClientContactName(contact.getLastName() + ", " + contact.getFirstName());    		
    	}
    }
    
    /**
     * Determines whether a driver has been assigned to the POC or not
     * @param clientPoint The client point relative to the account
     * @return boolean based on the client point's driver indicator
     */
    public boolean isDriverAssigned(PointOfCommunicationVO pocVO){
    	boolean driverAssigned = false;
    	if(pocVO.isDriverAssigned()){
    		driverAssigned = true;
    	}
    	return driverAssigned;
    }
    
	/**
	 * Pattern for retrieving stateful data passed from calling view.
	 * This view will expect the unit no to be passed with an optional
	 * maintenance request id. If the maintenance request id is present
	 * the view goes into an view/edit PO mode.
	 */
    @Override
	protected void loadNewPage() {	
		Map<String, Object> map = super.thisPage.getInputValues();		
		thisPage.setPageUrl(ViewConstants.CLIENT_CONTACTS_MAINTENANCE);		
		setAccountCode((String)map.get(ViewConstants.VIEW_PARAM_ACCOUNT_CODE));
		setAccountType((String)map.get(ViewConstants.VIEW_PARAM_ACCOUNT_TYPE));
		setcId(Long.valueOf((String)map.get(ViewConstants.VIEW_PARAM_C_ID)));	
		setClientAccount(customerAccountService.getOpenCustomerAccountByCode((String)map.get(ViewConstants.VIEW_PARAM_ACCOUNT_CODE)));	
		setSelectedClientSystem((ClientSystem)map.get(ViewConstants.VIEW_PARAM_CLIENT_SYSTEM_FILTER));
	} 
	
	/**
	 * Pattern for restoring the view's data
	 */
	@Override
	protected void restoreOldPage() {
		setAccountCode((String) thisPage.getRestoreStateValues().get(ViewConstants.VIEW_PARAM_ACCOUNT_CODE));
		setAccountType((String) thisPage.getRestoreStateValues().get(ViewConstants.VIEW_PARAM_ACCOUNT_TYPE));
		setcId(Long.valueOf((String) thisPage.getRestoreStateValues().get(ViewConstants.VIEW_PARAM_C_ID)));		
		setClientAccount(customerAccountService.getOpenCustomerAccountByCode(getAccountCode()));
		setSelectedClientSystem((ClientSystem) thisPage.getRestoreStateValues().get(ViewConstants.VIEW_PARAM_CLIENT_SYSTEM_FILTER));
		setDefaultPOCVO((PointOfCommunicationVO) thisPage.getRestoreStateValues().get(ViewConstants.VIEW_PARAM_DEFAULT_POC_VO));
		
	}
	
	/*
	 * This is needed for commandLink navigation from the Client Contact page with
	 * one account, to the Client Contact page with another account
	 * */	
	@Override	
	public void editViewClient(ExternalAccount account){
        try {
        	setcId(account.getExternalAccountPK().getCId());
    		setAccountType(account.getExternalAccountPK().getAccountType());
    		setAccountCode(account.getExternalAccountPK().getAccountCode());	
    		setClientAccount(customerAccountService.getOpenCustomerAccountByCode(getAccountCode()));
    		loadData(); 
    		popDefaultPOC();
        	setAvailableCostCenters(costCenterService.getCostCenters(getClientAccount()));
        	setCostCenterAvailable(!MALUtilities.isEmpty(getAvailableCostCenters()) && getAvailableCostCenters().size() > 0);
        	setContactsAssignable(hasContacts(getDefaultPOCVO()));
		} catch(Exception ex) {
			logger.error(ex);
			super.addErrorMessage("generic.error", ex.getMessage());
		}				
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


	public Long getcId() {
		return cId;
	}

	public void setcId(Long cId) {
		this.cId = cId;
	}

	public ClientPoint getSelectedClientPoint() {
		return selectedClientPoint;
	}


	public void setSelectedClientPoint(ClientPoint selectedClientPoint) {
		this.selectedClientPoint = selectedClientPoint;
	}

	public ExternalAccount getClientAccount() {
		return clientAccount;
	}


	public void setClientAccount(ExternalAccount clientAccount) {
		this.clientAccount = clientAccount;
	}


	public PointOfCommunicationVO getDefaultPOCVO() {
		return defaultPOCVO;
	}

	public void setDefaultPOCVO(PointOfCommunicationVO defaultPOCVO) {
		this.defaultPOCVO = defaultPOCVO;
	}

	public String getDefaultClientContactName() {
		return defaultClientContactName;
	}


	public void setDefaultClientContactName(String defaultClientContactName) {
		this.defaultClientContactName = defaultClientContactName;
	}

	public List<PointOfCommunicationVO> getPocVOs() {
		return pocVOs;
	}

	public void setPocVOs(List<PointOfCommunicationVO> pocVOs) {
		this.pocVOs = pocVOs;
	}

	public boolean isContactsAssignable() {
		return contactsAssignable;
	}
	
	public void setContactsAssignable(boolean contactsAssignable) {
		this.contactsAssignable = contactsAssignable;
	}

	public boolean isCostCenterAvailable() { 		
		return costCenterAvailable;
	}

	public void setCostCenterAvailable(boolean costCenterAvailable) {
		this.costCenterAvailable = costCenterAvailable;
	}

	public List<CostCentreCode> getAvailableCostCenters() {
		return availableCostCenters;
	}

	public void setAvailableCostCenters(List<CostCentreCode> availableCostCenters) {
		this.availableCostCenters = availableCostCenters;
	}

	public ClientSystem getSelectedClientSystem() {
		return selectedClientSystem;
	}

	public void setSelectedClientSystem(ClientSystem selectedClientSystem) {
		this.selectedClientSystem = selectedClientSystem;
	}

	public List<ClientSystem> getClientSystems() {
		return clientSystems;
	}

	public void setClientSystems(
			List<ClientSystem> clientSystems) {
		this.clientSystems = clientSystems;
	}
	
	public List<PointOfCommunicationVO> getRowList() {
		return rowList;
	}

	public void setRowList(List<PointOfCommunicationVO> rowList) {
		this.rowList = rowList;
	}
	
}
