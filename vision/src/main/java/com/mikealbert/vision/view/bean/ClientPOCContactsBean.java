package com.mikealbert.vision.view.bean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.faces.event.AjaxBehaviorEvent;

import org.primefaces.component.selectbooleancheckbox.SelectBooleanCheckbox;
import org.primefaces.context.RequestContext;
import org.primefaces.event.data.PageEvent;
import org.primefaces.event.data.SortEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import com.mikealbert.data.entity.AddressTypeCode;
import com.mikealbert.data.entity.ClientPoint;
import com.mikealbert.data.entity.ClientPointRule;
import com.mikealbert.data.entity.ContactAddress;
import com.mikealbert.data.entity.CostCentreCode;
import com.mikealbert.data.entity.ExternalAccount;
import com.mikealbert.data.vo.ClientContactVO;
import com.mikealbert.data.vo.PointOfCommunicationVO;
import com.mikealbert.service.ClientPOCService;
import com.mikealbert.service.ContactService;
import com.mikealbert.service.CostCenterService;
import com.mikealbert.service.CustomerAccountService;
import com.mikealbert.util.MALUtilities;
import com.mikealbert.vision.view.ViewConstants;


@Component
@Scope("view")
public class ClientPOCContactsBean extends StatefulBaseBean {	
    @Resource CustomerAccountService customerAccountService;
    @Resource ClientPOCService clientPOCService;
    @Resource CostCenterService costCenterService;
    @Resource ContactService contactService;
    
    private List<AddressTypeCode> addressTypes;
    
    final static String ASSIGN = "Assign";
    final static String UNASSIGN = "Unassign";
    
	private static final long serialVersionUID = -7170421837551409230L;	
	private String accountCode;
	private String accountType;
	private Long cId;
	private Long clientPointId;
	private CostCentreCode costCenter;	
	private ClientContactVO clientDriver; 
	private List<ClientContactVO> clientContacts;
	private LazyDataModel<ClientContactVO> lazyClientContactVOs  = null;
	private ExternalAccount clientAccount;
	private ClientPoint clientPoint;
	private PointOfCommunicationVO pointOfCommunicationVO;
	//Fields for assignment check of Driver in database
	private boolean driverAssignedToPOC;
	private boolean driverAssignedToCostCenter;
	
	private int resultPerPage = ViewConstants.RECORDS_PER_PAGE_MEDIUM;
	private ClientContactVO selectedContactVO;
	private ClientContactVO assignedClientContactVO;
	
	//Fields for assignment check of Driver on UI  
	private boolean driverMarkAssignedToPOC;
	private boolean driverMarkAssignedToCostCenter;
	
	int DEFAULT_DATATABLE_HEIGHT = 225;
	
	private ContactAddress postInfo;
	private ContactAddress garagedInfo;
	
	
	
		
	/**
	 * Initializes the bean
	 */
    @PostConstruct
    public void init(){
    	logger.info("-- Method name: init start");
    	
    	initializeDataTable(600, 200, new int[] {2, 1, 10, 10, 6, 7, 4, 7, 3, 3, 3, 13}).setHeight(DEFAULT_DATATABLE_HEIGHT);      		
    	super.openPage();
    	preSortPOCRules();
    	
        try {                  	
    		setDriverAssignedToPOC(clientPOCService.isDriverAssigned(getPointOfCommunicationVO().getClientPOC(), null));
    		setDriverMarkAssignedToPOC(isDriverAssignedToPOC());
    		    	
    		if(!MALUtilities.isEmpty(getCostCenter())){
    			setDriverAssignedToCostCenter(clientPOCService.isDriverAssigned(getPointOfCommunicationVO().getClientPOC(), getCostCenter()));        	
    			setDriverMarkAssignedToCostCenter(isDriverAssignedToCostCenter());
    		}
    		
    		/*
    		 * Lazy data initialization
    		 */
    		lazyClientContactVOs = new LazyDataModel<ClientContactVO>() {    		
				private static final long serialVersionUID = 8334345274791682922L;

				
				@Override
    			public List<ClientContactVO> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
					
    				Sort sort = null;
    				int pageIdx = (first == 0) ? 0 : (first / pageSize);
    				PageRequest page = new PageRequest(pageIdx,pageSize);				

    				if(MALUtilities.isNotEmptyString(sortField)){
    					if (sortOrder.name().equalsIgnoreCase(SortOrder.DESCENDING.toString())) {
    						sort = new Sort(Sort.Direction.DESC, clientPOCService.resolveSortByName(sortField));
    					}else{
    						sort = new Sort(Sort.Direction.ASC, clientPOCService.resolveSortByName(sortField));
    					}
    				}
    				
    				List<ClientContactVO> clientContactVOs  = null;    	
    				if(isDirtyData()){
    					clientContactVOs = getClientContacts();
    				} else {
    					clientContactVOs = clientPOCService.getSetupClientContactVOs(getPointOfCommunicationVO().getClientPOC(), getCostCenter(), page, sort);
        				setClientContacts(clientContactVOs);
        				setDirtyData(false);
    				}
    				
//    				initializeSingleSelection(clientContactVOs);
    				
    				return clientContactVOs;
    			} 

    			@Override
    			public ClientContactVO getRowData(String rowKey) {
    				for (ClientContactVO clientContactVO : lazyClientContactVOs) {
    					if (String.valueOf(clientContactVO.getClientContactId()).equals(rowKey))
    						return clientContactVO;
    				}
    				return null;
    			}

    			@Override
    			public Object getRowKey(ClientContactVO clientContactVO) {
    				return clientContactVO.getContactId();
    			}
    		};	
    		
    		lazyClientContactVOs.setPageSize(ViewConstants.RECORDS_PER_PAGE_SMALL);
    		lazyClientContactVOs.setRowCount(clientPOCService.getSetupClientContactVOsCount(getPointOfCommunicationVO().getClientPOC(), getCostCenter()));
			
		} catch(Exception ex) {
			logger.error(ex);
			super.addErrorMessage("generic.error", ex.getMessage());
		}
        
    	logger.info("-- Method name: init end");        
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
    
    public void autoSave(){}  
           
    /**
     * Handles page cancel button click event. 
     * @return The calling view
     */
    public String cancel(){
    	return super.cancelPage();      	
    }
    
    
    /**
     * Method to handle check box toggle event for Driver at POC level 
     * @param event
     */
    public void toggleDriverAssignment(AjaxBehaviorEvent event) {
    	RequestContext context = RequestContext.getCurrentInstance();
    	
    	SelectBooleanCheckbox permit = (SelectBooleanCheckbox) event.getComponent();
	    boolean checked = (Boolean) permit.getValue();
	    if (checked) {
	    	if(!enableMultiSelect()) {
	    		if(getAssignedContactsCount() >= 1) {
	    			setDriverMarkAssignedToPOC(false);
   	    			
   	    			context.addCallbackParam("showSingleSelDlg", true);
   	    			return;
   	    		}
	    	}
	    } 
	    
	    if(checked != isDriverAssignedToPOC()) {
 	    	setDirtyData(true);
 	    } else {
 	    	setDirtyData(false);
 	    }
 	    context.update("dirtyData");
    }
    
    
    /**
     * Method to handle check box toggle event for Driver at cost center level
     * @param event
     */
    public void toggleCostCenterDriverAssignment(AjaxBehaviorEvent event) {
    	RequestContext context = RequestContext.getCurrentInstance();
    	
   	 	SelectBooleanCheckbox permit = (SelectBooleanCheckbox) event.getComponent();
   	    boolean checked = (Boolean) permit.getValue();
   	    if (checked) {
   	    	if(!enableMultiSelect()) {
   	    		if(getAssignedContactsCount() >= 1) {
   	    			setDriverMarkAssignedToCostCenter(false);
   	    			context.addCallbackParam("showSingleSelDlg", true);
   	    			return;
   	    		}
   	    	}
   	    }
   	    
   	    if(checked != isDriverAssignedToCostCenter()) {
	    	setDirtyData(true);
	    } else {
	    	setDirtyData(false);
	    }
   	    
   	    context.update("dirtyData");
    }
    
    
    /** 
     * Check dirty data for contacts
     */
    @SuppressWarnings("unchecked")
	private boolean checkDirtyContacts() {
    	List<ClientContactVO> clientContactVOs;
    	try {
			clientContactVOs = ((List<ClientContactVO>) getLazyClientContactVOs().getWrappedData());
			
			for(ClientContactVO clientContactVO : clientContactVOs) {
				if(clientContactVO.isMarkAssigned() != clientContactVO.isAssigned() || clientContactVO.isContactMethodEmail() != clientContactVO.isContactMethodPhoneMarked() || clientContactVO.isContactMethodMail() != clientContactVO.isContactMethodMailMarked() || clientContactVO.isContactMethodPhone() != clientContactVO.isContactMethodPhoneMarked()) {
					return true;
				} 
			}
		} catch (Exception e) {
			handleException("generic.error",new String[]{e.getMessage()}, e, null);
		}	
    	
    	return false;
    }
    
    /**
     * Get markAssigned/assigned contacts count
     * @return
     */
    
    @SuppressWarnings("unchecked")
    public int getAssignedContactsCount() {
		int count = 0;
		List<ClientContactVO> currentPageClientContactVOs = null;
		try {
			currentPageClientContactVOs = ((List<ClientContactVO>) getLazyClientContactVOs().getWrappedData());
			
			for(ClientContactVO clientContactVO : currentPageClientContactVOs) {
				if(clientContactVO.isMarkAssigned()) {
					count+=1;
				}
			}
			
			if(count == 0) {
				int currentPageRows = currentPageClientContactVOs != null ? currentPageClientContactVOs.size() : 0;
				int totalRows = getLazyClientContactVOs().getRowCount();
				
				if(currentPageRows != totalRows) {
					List<ClientContactVO> allClientContactVOs = clientPOCService.getSetupClientContactVOs(getPointOfCommunicationVO().getClientPOC(), getCostCenter(), null, null);
					
					for(ClientContactVO clientContactVO : allClientContactVOs) {
						if(!containsContact(currentPageClientContactVOs, clientContactVO)) {
							if(clientContactVO.isAssigned()) {
								count+=1;
							}
						}
					}
				}
			}
		} catch (Exception e) {
			handleException("generic.error", new String[] { e.getMessage() }, e, null);
		}
		return count;
    }
    
    private boolean containsContact(List<ClientContactVO> clientContactVOs, ClientContactVO clientContactVO) {
    	for(ClientContactVO currentClientContactVO : clientContactVOs) {
			if(clientContactVO.getContactId().equals(currentClientContactVO.getContactId())) {
				return true;
			}
		}
    	
    	return false;
    }
    
    
    /**
     * Check the assigned status of Driver on POC level or Cost Center level on UI
     * @return
     */
    private boolean isDriverMarkAssigned() {
		if(!MALUtilities.isEmpty(getCostCenter())) {
			return isDriverMarkAssignedToCostCenter();
		}
		return isDriverMarkAssignedToPOC();
	}
    
    /**
     * Method to handle check box toggle event for a Contact 
     * 
     */
    public void toggleContactAssignment(ClientContactVO contactVO) {
		RequestContext context = RequestContext.getCurrentInstance(); 
		
   	    if (contactVO.isMarkAssigned()) {
   	    	contactVO.setContactMethodEmailMarked(contactVO.isContactMethodEmail());
	   	    contactVO.setContactMethodMailMarked(contactVO.isContactMethodMail());
	   	    contactVO.setContactMethodPhoneMarked(contactVO.isContactMethodPhone());

	   	    if(!enableMultiSelect()) {
   	    		if(getAssignedContactsCount() > 1 || isDriverMarkAssigned()) {
   	    			contactVO.setMarkAssigned(false);
   	    			contactVO.setContactMethodEmailMarked(false);
   	 	   	    	contactVO.setContactMethodMailMarked(false);
   	 	   	    	contactVO.setContactMethodPhoneMarked(false);
   	    			context.addCallbackParam("showSingleSelDlg", true);
   	    			return;
   	    		}  
   	    	} 
   	    	
   	    	if(checkDirtyContacts()) {
    	    	setDirtyData(true);
    	    } else {
    	    	setDirtyData(false);
    	    }
    	    context.update("dirtyData");
	    	    
   	    	if(!checkPOBoxAllowed() && contactVO.isPoBoxAvailable()) {
				context.addCallbackParam("showPOWarnDlg", true);
				return;
			}
   	    } else {
   	    	contactVO.setMarkAssigned(false);
   	    	contactVO.setContactMethodEmailMarked(false);
	   	    contactVO.setContactMethodMailMarked(false);
	   	    contactVO.setContactMethodPhoneMarked(false);
	   	    disableEmailDeliveryMethod(contactVO);
	   	    disableMailDeliveryMethod(contactVO);
	   	    disablePhoneDeliveryMethod(contactVO);
	   	    
		   	if(checkDirtyContacts()) {
	 	    	setDirtyData(true);
	 	    } else {
	 	    	setDirtyData(false);
	 	    } 
	   	    context.update("dirtyData");
	   	    
   	    	if(checkContactRequired()) {
   	    		if(getAssignedContactsCount() == 0 && !isDriverMarkAssigned()) {
   	    			context.addCallbackParam("showContactReqDlg", true);
   	    			return;
   	    		}  
   	    	} 
   	    }
    }
    
    
    /**
     * Method to update contact(s) assignment/un-assignment
     * @param showMessage
     */
    @SuppressWarnings("unchecked")
	public void saveUpdateContactsAssignment() {
		List<ClientContactVO> clientContactVOs;
    	try {
			clientContactVOs = ((List<ClientContactVO>)getLazyClientContactVOs().getWrappedData());
			
			for(ClientContactVO clientContactVO : clientContactVOs) {
				if(clientContactVO.isMarkAssigned()){
					if(!clientContactVO.isAssigned()){
						if(!MALUtilities.isEmpty(getCostCenter())){
							clientContactVO.setCostCenterCode(getCostCenter().getCostCentreCodesPK().getCostCentreCode());
						}
						
						clientContactVO = clientPOCService.saveOrUpdateClientPOCContact(getPointOfCommunicationVO(), getCostCenter(), clientContactVO, ClientPOCService.SAVE_MODE_ADD);
						setPointOfCommunicationVO(clientPOCService.getPointOfCommunicationVO(getPointOfCommunicationVO().getClientPOC().getClientPointAccountId()));
						clientContactVO.setContactMethodEmailMarked(clientContactVO.isContactMethodEmail());
						clientContactVO.setContactMethodMailMarked(clientContactVO.isContactMethodMail());
						clientContactVO.setContactMethodPhoneMarked(clientContactVO.isContactMethodPhone());
					} else {
						if(clientContactVO.isContactMethodEmail() != clientContactVO.isContactMethodPhoneMarked() || clientContactVO.isContactMethodMail() != clientContactVO.isContactMethodMailMarked() || clientContactVO.isContactMethodPhone() != clientContactVO.isContactMethodPhoneMarked()) {
							clientContactVO.setContactMethodEmail(clientContactVO.isContactMethodEmailMarked());
							clientContactVO.setContactMethodMail(clientContactVO.isContactMethodMailMarked());
							clientContactVO.setContactMethodPhone(clientContactVO.isContactMethodPhoneMarked());
							clientContactVO = clientPOCService.saveOrUpdateClientPOCContact(getPointOfCommunicationVO(), getCostCenter(), clientContactVO, ClientPOCService.SAVE_MODE_UPDATE);
						}
					}
				} else {
					if(clientContactVO.isAssigned()){
						if(!MALUtilities.isEmpty(getCostCenter())){
							clientContactVO.setCostCenterCode(getCostCenter().getCostCentreCodesPK().getCostCentreCode());
						}
						
						clientContactVO = clientPOCService.saveOrUpdateClientPOCContact(getPointOfCommunicationVO(), getCostCenter(), clientContactVO, ClientPOCService.SAVE_MODE_DELETE);
						setPointOfCommunicationVO(clientPOCService.getPointOfCommunicationVO(getPointOfCommunicationVO().getClientPOC().getClientPointAccountId()));
					} 
				}
			}
		} catch (Exception e) {
			handleException("generic.error",new String[]{e.getMessage()}, e, null);
		}	
	}
    
    
    /**
     * Method to handle Driver assignment at POC level and Cost Center level
     * 
     */
    public void saveUpdateDriverAssignment() {
    	if(displayPOCDriverCheckBox() == true) {
			changeDriverAssignment();
		}
    	
    	if(displayCostCenterDriverCheckBox() == true) {
			if(!MALUtilities.isEmpty(getCostCenter())){
				changeCostCenterDriverAssignment();
			}
		}
    }
    
    /**
     * Method to update contact and driver assignment when dirty data found and user select "Save" option
     * 
     */
    public void saveUpdateContactsDriverAssignment() {
    	RequestContext context = RequestContext.getCurrentInstance();
    	
    	saveUpdateContactsAssignment();
    	saveUpdateDriverAssignment();
    	
    	setDirtyData(false);
    	context.update("dirtyData");
    	super.addSuccessMessage("process.success", "Update");
    }
    
    /**
     * Method to update contact and driver assignment on "Save" option
     * 
     */
    public String saveUpdateContactsDriverAssignmentClose() {
    	saveUpdateContactsDriverAssignment();
    	
    	return super.cancelPage();
    }
    
    
    /**
     * Mark all contacts as unchecked
     */
    @SuppressWarnings("unchecked")
	public void deSelectAllContacts() {
    	RequestContext context = RequestContext.getCurrentInstance();
    	List<ClientContactVO> clientContactVOs;
    	try {
			clientContactVOs = ((List<ClientContactVO>)getLazyClientContactVOs().getWrappedData());
			
			for(ClientContactVO clientContactVO : clientContactVOs) {
				if(clientContactVO.isMarkAssigned()) {
					clientContactVO.setMarkAssigned(false);
					clientContactVO.setContactMethodEmailMarked(false);
					clientContactVO.setContactMethodMailMarked(false);
					clientContactVO.setContactMethodPhoneMarked(false);
				}
			}
			
			if(isDriverAssignedToPOC())
				setDriverMarkAssignedToPOC(false);
			
			if(isDriverAssignedToCostCenter())
				setDriverMarkAssignedToCostCenter(false);
			
			if(checkDirtyContacts()) {
				setDirtyData(true);
			} 
			context.update("dirtyData");
			
		} catch (Exception e) {
			handleException("generic.error",new String[]{e.getMessage()}, e, null);
		}	
	}
    
	
	 /**
     * Method to handle action taken on "dirty data found" dialog on pagination and sorting
     * @param action
     */
    public void handleDirtyDialogAction(String action) {

		if(action.equals("Y")) {
			saveUpdateContactsDriverAssignment();
		} else {
			if(!MALUtilities.isEmpty(getCostCenter())) {
				setDriverMarkAssignedToCostCenter(isDriverAssignedToCostCenter());
			} else {
				setDriverMarkAssignedToPOC(isDriverAssignedToPOC());
			}
		}
		
		setDirtyData(false);

    }
	
	 /**
     * Handles pagination of contacts data table with dirty check  
     * @return
     */
	public void onPageChange(PageEvent event) {
		RequestContext context = RequestContext.getCurrentInstance(); 
    	
		if(isDirtyData()) {
			context.addCallbackParam("dirtyDataFound", true);
			return;
		}
    }
	
	
	 /**
	 * Handles sorting of contacts data table with dirty check
	 * @param event
	 */
	public void onSortOperation(SortEvent event) {
		RequestContext context = RequestContext.getCurrentInstance(); 
		 
		if(isDirtyData()) {
			context.addCallbackParam("dirtyDataFound", true);
			return;
		}
	 }
    
	public void toggleClientContactMethod(ClientContactVO contactVO){
		RequestContext context = RequestContext.getCurrentInstance();
		
		if(checkDirtyContacts()) {
	    	setDirtyData(true);
	    } else {
	    	setDirtyData(false);
	    }
	    context.update("dirtyData");
	}
	
    @SuppressWarnings("unchecked")
	public void assignClientContactMethod(ClientContactVO contactVO){
    	int index; 
    	ClientContactVO contact;
    	List<ClientContactVO> contacts;

    	try {
			contacts = ((List<ClientContactVO>)getLazyClientContactVOs().getWrappedData());    	

			index = contacts.indexOf(contactVO);
			contact = contactVO;
			
			contact = clientPOCService.saveOrUpdateClientPOCContact(getPointOfCommunicationVO(), getCostCenter(), contact, ClientPOCService.SAVE_MODE_UPDATE);    	
			
			contacts.set(index, contact); 

			setPointOfCommunicationVO(clientPOCService.getPointOfCommunicationVO(getPointOfCommunicationVO().getClientPOC().getClientPointAccountId()));

			super.addSuccessMessage("process.success", "Contact (" + contactVO.getLastName() + ", " + contactVO.getFirstName() + ") method change ");			
		} catch (Exception e) {
			handleException("generic.error",new String[]{e.getMessage()}, e, null);
		}    	
    }
    
    /**
     * Updates the POC driver indicator change
     */
    public void changeDriverAssignment(){
    	ClientContactVO driverClientContactVO;
    	
    	try {
    		if(isDriverMarkAssignedToPOC()) {
    			if(!clientPOCService.isDriverAssigned(getPointOfCommunicationVO().getClientPOC(), getCostCenter())){
    				driverClientContactVO = new ClientContactVO();
    				driverClientContactVO.setDriver(true);
    				clientPOCService.saveOrUpdateClientPOCContact(getPointOfCommunicationVO(), getCostCenter(), driverClientContactVO, ClientPOCService.SAVE_MODE_ADD); 
    			}
    		} else {
    			if(clientPOCService.isDriverAssigned(getPointOfCommunicationVO().getClientPOC(), getCostCenter())){
    				clientPOCService.saveOrUpdateClientPOCContact(getPointOfCommunicationVO(), getCostCenter(), clientPOCService.getDriverClientContactVO(getPointOfCommunicationVO().getClientPOC(), null), ClientPOCService.SAVE_MODE_DELETE);   
    			} 
    		}
    		
			setPointOfCommunicationVO(clientPOCService.getPointOfCommunicationVO(getPointOfCommunicationVO().getClientPOC().getClientPointAccountId()));
			setDriverAssignedToPOC(clientPOCService.isDriverAssigned(getPointOfCommunicationVO().getClientPOC(), null));
			setDriverMarkAssignedToPOC(driverAssignedToPOC);
			
		} catch (Exception e) {
			handleException("generic.error",new String[]{e.getMessage()}, e, null);
		}      	
    }
  
    
    /**
     * Updates the Cost Center's driver indicator change
     * 
     */
    public void changeCostCenterDriverAssignment(){
    	ClientContactVO driverClientContactVO;
    	
    	try {
			if(isDriverMarkAssignedToCostCenter()) {
				if(!isDriverAssignedToCostCenter()){
					driverClientContactVO = new ClientContactVO();
					driverClientContactVO.setDriver(true);
					driverClientContactVO.setCostCenterCode(getCostCenter().getCostCentreCodesPK().getCostCentreCode());
					
					clientPOCService.saveOrUpdateClientPOCContact(getPointOfCommunicationVO(), getCostCenter(), driverClientContactVO, ClientPOCService.SAVE_MODE_ADD); 
				} 
			} else {
				if(isDriverAssignedToCostCenter()){
					clientPOCService.saveOrUpdateClientPOCContact(getPointOfCommunicationVO(), getCostCenter(), clientPOCService.getDriverClientContactVO(getPointOfCommunicationVO().getClientPOC(), getCostCenter()), ClientPOCService.SAVE_MODE_DELETE);  
				}
			}
			
			setPointOfCommunicationVO(clientPOCService.getPointOfCommunicationVO(getPointOfCommunicationVO().getClientPOC().getClientPointAccountId()));
			setDriverAssignedToCostCenter(clientPOCService.isDriverAssigned(getPointOfCommunicationVO().getClientPOC(), getCostCenter()));
			setDriverMarkAssignedToCostCenter(driverAssignedToCostCenter);
				
					
		} catch (Exception e) {
			handleException("generic.error",new String[]{e.getMessage()}, e, null);
		}    	
    }
            
    public boolean enableMultiSelect(){
    	boolean isMultiSelectEnabled;
    	isMultiSelectEnabled = getPointOfCommunicationVO().isMultipleRecipientsAssignable();
    	return isMultiSelectEnabled;
    }
    
    
    public boolean checkPOBoxAllowed(){
    	return getPointOfCommunicationVO().isPoBoxAllowed();
    }
    
    public boolean checkContactRequired(){
    	return getPointOfCommunicationVO().isContactRequired();
    }
        
    public boolean disableMailDeliveryMethod(ClientContactVO clientContactVO){
    	boolean isDisabled = true;
    	if(super.hasPermission() 
    			&& clientContactVO.isAssigned() && clientContactVO.isMarkAssigned()
    			&& getPointOfCommunicationVO().isDeliveryMethodMailUpdatable()){
    			//&& getPointOfCommunicationVO().isDefaultMail()){
    		isDisabled = false;
    	}
    	return isDisabled;
    }
    
    public boolean disablePhoneDeliveryMethod(ClientContactVO clientContactVO){
    	boolean isDisabled = true;
    	if(super.hasPermission() 
    			&& clientContactVO.isAssigned() && clientContactVO.isMarkAssigned()
    			&& getPointOfCommunicationVO().isDeliveryMethodPhoneUpdatable()){
    		isDisabled = false;
    	}
    	return isDisabled;
    }
    
    public boolean disableEmailDeliveryMethod(ClientContactVO clientContactVO){
    	boolean isDisabled = true;
    	if(super.hasPermission() 
    			&& clientContactVO.isAssigned() && clientContactVO.isMarkAssigned()
    			&& getPointOfCommunicationVO().isDeliveryMethodEmailUpdatable()){
    			//&& getPointOfCommunicationVO().isDefaultEmail()){
    		isDisabled = false;
    	}
    	return isDisabled;
    }    
    
    public boolean disableDriverAssignment(){
    	boolean isDisabled = true;
    	if(super.hasPermission() 
    			&& getPointOfCommunicationVO().isDriverAssignable()){
    		isDisabled = false;	
    	}
    	return isDisabled;
    }
    
    public boolean disableContactAssignment(ClientContactVO clientContactVO){
    	boolean isDisabled = true;
    	if(super.hasPermission() 
    			&& getPointOfCommunicationVO().isContactAssignable()
    			&& clientContactVO.isAssignable()){
    		isDisabled = false;	
    	}    	
    	return isDisabled;
    }
    
    /**
     * Determines whether to display the cost center info panel. 
     * The scenario in which the panel should be display is when
     * the contacts list is being used to assign contact(s) to a 
     * Cost Center.
     * @return boolean 
     */
    public boolean displayCostCenterInfoPanel(){
    	return MALUtilities.isEmpty(getCostCenter()) ? false : true;
    }
    
    /**
     * Determines whether a driver can directly be assigned to the
     * POC. 
     * @return
     */
    public boolean displayPOCDriverCheckBox(){
    	boolean isDisplay = false;;
    	if(MALUtilities.isEmpty(getCostCenter())
    			&& getPointOfCommunicationVO().isDriverAssignable()){
    		isDisplay = true;
    	} 
    	return isDisplay;
    }
    
    /**
     * Determines whether a driver can directly be assigned to the
     * Cost Center. 
     * @return
     */
    public boolean displayCostCenterDriverCheckBox(){
    	boolean isDisplay = false;;
    	if(!MALUtilities.isEmpty(getCostCenter())
    			&& getPointOfCommunicationVO().isDriverAssignable()){
    		isDisplay = true;
    	} 
    	return isDisplay;
    }    
            
    public Date pocLastUpdate(){
    	return clientPOCService.getDateOfLastUpdate(getPointOfCommunicationVO().getClientPOC());
    }    
           
    public Date costCenterLastUpdate(){
    	return clientPOCService.getDateOfLastUpdate(getPointOfCommunicationVO().getClientPOC(), getCostCenter().getCostCentreCodesPK().getCostCentreCode());
    }
    
    public void contactInfoRequestListener(ClientContactVO contactVO){
    	setSelectedContactVO(clientPOCService.getClientContactVO(getPointOfCommunicationVO(), 
    			clientPOCService.getContact(contactVO.getContactId())));
    	setPostInfo(contactService.getAddress(getSelectedContactVO().getContactId(), new AddressTypeCode(ViewConstants.ADDRESS_TYPE_POST)));
		setGaragedInfo(contactService.getAddress(getSelectedContactVO().getContactId(), new AddressTypeCode(ViewConstants.ADDRESS_TYPE_GARAGED)));
    }
    
    
	/**
	 * Pattern for retrieving stateful data passed from calling view.
	 * This view will expect the account code to be passed with an optional
	 * client point account id (POC). If the client point account id 
	 * is not present, a ClientPointAccount (POC) is created.
	 */
	protected void loadNewPage() {	
		Map<String, Object> map = super.thisPage.getInputValues();
		Long addedClientPointAccountId;
		
		super.thisPage.setPageDisplayName(ViewConstants.DISPLAY_NAME_CLIENT_CONTACTS);		
		super.thisPage.setPageUrl(ViewConstants.CLIENT_POC_CONTACTS);
		
		setClientAccount(customerAccountService.getOpenCustomerAccountByCode((String)map.get(ViewConstants.VIEW_PARAM_ACCOUNT_CODE)));
		setClientPoint(clientPOCService.getClientPoint((Long)map.get(ViewConstants.VIEW_PARAM_CPNT_ID)));
		setCostCenter(costCenterService.getCostCenter(getClientAccount(), (String)map.get(ViewConstants.VIEW_PARAM_COST_CENTER_CODE)));
		
		if(MALUtilities.isEmpty(map.get(ViewConstants.VIEW_PARAM_CPNTA_ID))){
			addedClientPointAccountId = clientPOCService.saveOrUpdateClientPOC(getClientAccount(), getClientPoint()).getClientPointAccountId();
			//setPointOfCommunication(clientPOCService.saveOrUpdateClientPOC(getClientAccount(), getClientPoint()));
			setPointOfCommunicationVO(clientPOCService.getPointOfCommunicationVO(addedClientPointAccountId));
		} else {
			//setPointOfCommunication(clientPOCService.getClientPOC((Long)map.get(ViewConstants.VIEW_PARAM_CPNTA_ID)));
			setPointOfCommunicationVO(clientPOCService.getPointOfCommunicationVO((Long)map.get(ViewConstants.VIEW_PARAM_CPNTA_ID)));
		}													
	} 
	
	/**
	 * Pattern for restoring the view's data
	 */
	protected void restoreOldPage() {}

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

	public ClientContactVO getClientDriver() {
		return clientDriver;
	}

	public void setClientDriver(ClientContactVO clientDriver) {
		this.clientDriver = clientDriver;
	}

	public List<ClientContactVO> getClientContacts() {
		return clientContacts;
	}

	public void setClientContacts(List<ClientContactVO> clientContacts) {
		this.clientContacts = clientContacts;
	}

	public LazyDataModel<ClientContactVO> getLazyClientContactVOs() {
		return lazyClientContactVOs;
	}

	public void setLazyClientContactVOs(LazyDataModel<ClientContactVO> lazyClientContactVOs) {
		this.lazyClientContactVOs = lazyClientContactVOs;
	}

	public Long getClientPointId() {
		return clientPointId;
	}

	public void setClientPointId(Long clientPointId) {
		this.clientPointId = clientPointId;
	}

	public CostCentreCode getCostCenter() {
		return costCenter;
	}

	public void setCostCenter(CostCentreCode costCenter) {
		this.costCenter = costCenter;
	}

	public ExternalAccount getClientAccount() {
		return clientAccount;
	}

	public void setClientAccount(ExternalAccount clientAccount) {
		this.clientAccount = clientAccount;
	}

	public ClientPoint getClientPoint() {
		return clientPoint;
	}

	public void setClientPoint(ClientPoint clientPoint) {
		this.clientPoint = clientPoint;
	}

	public PointOfCommunicationVO getPointOfCommunicationVO() {
		return pointOfCommunicationVO;
	}

	public void setPointOfCommunicationVO(PointOfCommunicationVO pointOfCommunicationVO) {
		this.pointOfCommunicationVO = pointOfCommunicationVO;
	}

	public boolean isDriverAssignedToPOC() {
		return clientPOCService.isDriverAssigned(getPointOfCommunicationVO().getClientPOC(), null);
	}

	public boolean isDriverAssignedToCostCenter() {
		if(!MALUtilities.isEmpty(getCostCenter())){
			return clientPOCService.isDriverAssigned(getPointOfCommunicationVO().getClientPOC(), getCostCenter());        	
		}else{
			return false;
		}		
	}

	public void setDriverAssignedToCostCenter(boolean driverAssignedToCostCenter) {
		this.driverAssignedToCostCenter = driverAssignedToCostCenter;
	}

	public void setDriverAssignedToPOC(boolean driverAssignedToPOC) {
		this.driverAssignedToPOC = driverAssignedToPOC;
	}

	public int getResultPerPage() {
		return resultPerPage;
	}

	public void setResultPerPage(int resultPerPage) {
		this.resultPerPage = resultPerPage;
	}

	public ClientContactVO getSelectedContactVO() {
		return selectedContactVO;
	}

	public void setSelectedContactVO(ClientContactVO selectedContactVO) {
		this.selectedContactVO = selectedContactVO;
	}
	
	
	public ClientContactVO getAssignedClientContactVO() {
		return assignedClientContactVO;
	}

	public void setAssignedClientContactVO(ClientContactVO assignedClientContactVO) {
		this.assignedClientContactVO = assignedClientContactVO;
	}	
	
	public ContactAddress getPostInfo(){
		return postInfo;
	}
	
	public void setPostInfo(ContactAddress postInfo){
		this.postInfo = postInfo;
	}
	
	public ContactAddress getGaragedInfo(){
		return garagedInfo;
	}
	
	public void setGaragedInfo(ContactAddress garagedInfo){
		this.garagedInfo = garagedInfo;
	}

	/**
	 * Sets the contacts datatable selection when selection mode is a single contact.
	 * Multiple contact selection is handled in a different way. Initializing the 
	 * selection for multi contact is not neccessary.
	 * @param clientContactVOs
	 */
	@SuppressWarnings("unused")
	private void initializeSingleSelection(List<ClientContactVO> clientContactVOs){
		if(!getPointOfCommunicationVO().isMultipleRecipientsAssignable()){
			for(ClientContactVO clientContactVO : clientContactVOs){
				if(clientContactVO.isAssigned()){
					setAssignedClientContactVO(clientContactVO);
				}
			}
		}
	}
	
	/**
	 * Sorts the client point rules by rule sort order in ASC order
	 */
	private void preSortPOCRules(){
		List<ClientPointRule> pocRules;
		
		pocRules = getPointOfCommunicationVO().getClientPOC().getClientPoint().getClientPointRules();
		Collections.sort(pocRules, new Comparator<ClientPointRule>() { 
			public int compare(ClientPointRule cr1, ClientPointRule cr2) { 
				return cr1.getClientRule().getSortOrder().compareTo(cr2.getClientRule().getSortOrder()); 
			}
		});			
	}
	
	public boolean isDriverMarkAssignedToPOC() {
		return driverMarkAssignedToPOC;
	}

	public void setDriverMarkAssignedToPOC(boolean driverMarkAssignedToPOC) {
		this.driverMarkAssignedToPOC = driverMarkAssignedToPOC;
	}

	public boolean isDriverMarkAssignedToCostCenter() {
		return driverMarkAssignedToCostCenter;
	}

	public void setDriverMarkAssignedToCostCenter(boolean driverMarkAssignedToCostCenter) {
		this.driverMarkAssignedToCostCenter = driverMarkAssignedToCostCenter;
	}

}
