package com.mikealbert.vision.view.bean;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.primefaces.model.TreeNode;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import com.mikealbert.common.MalConstants;
import com.mikealbert.data.entity.ClientContact;
import com.mikealbert.data.entity.ClientPoint;
import com.mikealbert.data.entity.ClientPointAccount;
import com.mikealbert.data.entity.ExternalAccount;
import com.mikealbert.data.vo.POCCostCenterVO;
import com.mikealbert.service.ClientPOCService;
import com.mikealbert.service.CustomerAccountService;
import com.mikealbert.util.MALUtilities;
import com.mikealbert.vision.view.ViewConstants;

@Component
@Scope("view")
public class ClientPOCCostCentersBean extends StatefulBaseBean {	
	private static final long serialVersionUID = -7170421837551409230L;
	
    @Resource ClientPOCService clientPOCService;
    @Resource CustomerAccountService customerAccountService;

    static final String DEFAULT_SORT_COLUMN= "description";     
    
	private String accountCode;
	private String accountType;
	private Long cId;
	private ExternalAccount clientAccount;
	private ClientPoint clientPoint;
	private ClientPointAccount poc;	
	private boolean driverAssignedToPOC;
	private LazyDataModel<POCCostCenterVO> lazyPOCCostCenterVOs;
	private int resultPerPage = ViewConstants.RECORDS_PER_PAGE_SMALL;		
	private Map<String, String> sortByMapping;
	private TreeNode root;
	private List<POCCostCenterVO> pocCostCenterVOs;	
	
	int DEFAULT_DATATABLE_HEIGHT = 225;
	
		
	/**
	 * Initializes the bean
	 */
    @PostConstruct
    public void init(){
    	logger.info( "-- Method name: init start");
    	initializeDataTable(600, 200, new int[] {15, 25, 15, 6, 5}).setHeight(DEFAULT_DATATABLE_HEIGHT);      	
    	super.openPage();

        try {
        	
        	sortByMapping = new HashMap<String, String>();
        	sortByMapping.put("code", "costCentreCodePK.costCentreCode");
        	sortByMapping.put("description", "description");        	
        	
        	setLazyPOCCostCenterVOs(new LazyDataModel<POCCostCenterVO>() {    		
				private static final long serialVersionUID = -909006074507169567L;

				@Override
    			public List<POCCostCenterVO> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
    				Sort sort = null;
    				int pageIdx;
    				PageRequest page;
    				//List<POCCostCenterVO> pocCostCenterVOs;

    				if(MALUtilities.isNotEmptyString(sortField)){
    					if (sortOrder.name().equalsIgnoreCase(SortOrder.DESCENDING.toString())) {
    						sort = new Sort(Sort.Direction.DESC, sortByMapping.get(sortField));
    					}else{
    						sort = new Sort(Sort.Direction.ASC, sortByMapping.get(sortField));
    					}
    				} else {
    					sort = new Sort(Sort.Direction.ASC, sortByMapping.get(DEFAULT_SORT_COLUMN));
    				}
    				
    				pageIdx = (first == 0) ? 0 : (first / pageSize);
    				page = new PageRequest(pageIdx, pageSize, sort);	    				
    				
    				setPocCostCenterVOs(clientPOCService.getSetupPOCCostCenterVOs(getPoc(), page));

    				return getPocCostCenterVOs();
    			} 

    			@Override
    			public POCCostCenterVO getRowData(String rowKey) {
    				for (POCCostCenterVO pocCostCenterVO : getLazyPOCCostCenterVOs()) {
    					if (String.valueOf(pocCostCenterVO.getCode()).equals(rowKey))
    						return pocCostCenterVO;
    				}
    				return null;
    			}

    			@Override
    			public Object getRowKey(POCCostCenterVO pocCostCenterVO) {
    				return pocCostCenterVO.getCode();
    			}
		});	  
        	
        	lazyPOCCostCenterVOs.setPageSize(getResultPerPage());
        	lazyPOCCostCenterVOs.setRowCount(clientPOCService.getSetupPOCCostCenterVOCount(poc)); 
        	
        	PageRequest page = new PageRequest(0, 1000000, null);       	
        	setPocCostCenterVOs(clientPOCService.getSetupPOCCostCenterVOs(getPoc(), page));
        	root = initCostCenterTree(null, null);
        	         		            	      	
		} catch(Exception ex) {
			logger.error(ex);
			super.addErrorMessage("generic.error", ex.getMessage());
		}
        
    	logger.info("-- Method name: init end");        
    }
        
    /**
     * Retrieves the contacts (excluding driver) that are assigned to the
     * client's POC Cost Center.
     * @param clientPoint Point of Communication
     * @return List<CLientContact> that are assigned to the client's POC (excluding driver)
     */
    public List<ClientContact> assignedContacts(POCCostCenterVO pocCostCenterVO){
       List<ClientContact> filteredClientContacts = null;
    	
    	if(!MALUtilities.isEmpty(pocCostCenterVO.getClientPointAccountId())){
    		if(!MALUtilities.isEmpty(pocCostCenterVO.getClientContacts())){
    			filteredClientContacts = new ArrayList<ClientContact>();
    			
    			for(ClientContact cc : pocCostCenterVO.getClientContacts()){
    				if(MALUtilities.isEmpty(cc.getDrvInd()) || cc.getDrvInd().equals(MalConstants.FLAG_N)){
        				filteredClientContacts.add(cc);    					
    				}
 
    			}
    		}
    	}
    	
    	return filteredClientContacts;
    } 
    
    public Date pocLastUpdate(){
    	return clientPOCService.getDateOfLastUpdate(getPoc());
    } 
    
    public Date costCenterLastUpdate(POCCostCenterVO pocCostCenterVO){
    	return clientPOCService.getDateOfLastUpdate(getPoc(), pocCostCenterVO.getCode());
    }       
    
    /**
     * Requests the view that will allow the users to assign contacts to a
     * POC. At a minimum the account information and the selected POC needs
     * to be passed to the view.
     */
    public void assignContacts(POCCostCenterVO pocCostCenterVO){
    	
    	Map<String, Object> restoreStateValues = new HashMap<String, Object>();
		restoreStateValues.put(ViewConstants.VIEW_PARAM_ACCOUNT_CODE, getClientAccount().getExternalAccountPK().getAccountCode());
		restoreStateValues.put(ViewConstants.VIEW_PARAM_ACCOUNT_TYPE, getClientAccount().getExternalAccountPK().getAccountType());
		restoreStateValues.put(ViewConstants.VIEW_PARAM_C_ID, String.valueOf(getClientAccount().getExternalAccountPK().getCId()));	
		restoreStateValues.put(ViewConstants.VIEW_PARAM_CPNT_ID, getClientPoint().getClientPointId());
		
		if(!MALUtilities.isEmpty(pocCostCenterVO.getClientPointAccountId())){
			restoreStateValues.put(ViewConstants.VIEW_PARAM_CPNTA_ID, pocCostCenterVO.getClientPointAccountId());
		} else {
			restoreStateValues.put(ViewConstants.VIEW_PARAM_CPNTA_ID, null);			
		}
		
		this.saveRestoreStateValues(restoreStateValues);
		
		Map<String, Object> nextPageValues = new HashMap<String, Object>();				
		nextPageValues.put(ViewConstants.VIEW_PARAM_ACCOUNT_CODE, getClientAccount().getExternalAccountPK().getAccountCode());
		nextPageValues.put(ViewConstants.VIEW_PARAM_ACCOUNT_TYPE, getClientAccount().getExternalAccountPK().getAccountType());
		nextPageValues.put(ViewConstants.VIEW_PARAM_C_ID, String.valueOf(getClientAccount().getExternalAccountPK().getCId()));		
		nextPageValues.put(ViewConstants.VIEW_PARAM_CPNT_ID, getClientPoint().getClientPointId());
		nextPageValues.put(ViewConstants.VIEW_PARAM_COST_CENTER_CODE, pocCostCenterVO.getCode());		
		
		if(!MALUtilities.isEmpty(pocCostCenterVO.getClientPointAccountId())){
			nextPageValues.put(ViewConstants.VIEW_PARAM_CPNTA_ID, pocCostCenterVO.getClientPointAccountId());		
		} else {
			nextPageValues.put(ViewConstants.VIEW_PARAM_CPNTA_ID, null);			
		}
		
		this.saveNextPageInitStateValues(nextPageValues);
		
		this.forwardToURL(ViewConstants.CLIENT_POC_CONTACTS);    	
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
    	if(MALUtilities.isEmpty(getPoc().getClientContacts())
    			|| getPoc().getClientContacts().size() == 0){
    		clientPOCService.saveOrUpdateClientPOC(getPoc(), ClientPOCService.SAVE_MODE_DELETE);
    	}
    	
    	return super.cancelPage();   
    }
    
    public boolean displayCostCenterContactsTable(POCCostCenterVO pocCostCenterVO){
    	boolean isDisplay;
    	
    	if(pocCostCenterVO.getClientContacts().size() == 0) {
    		isDisplay = false;
    	} else if(pocCostCenterVO.getClientContacts().size() == 1 && pocCostCenterVO.isDriverAssigned()) {
    		isDisplay = false;
    	} else {
    		isDisplay = true;
    	}
    	
    	return isDisplay;
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
				
		super.thisPage.setPageDisplayName(ViewConstants.DISPLAY_NAME_CLIENT_POC_COST_CENTERS);		
		super.thisPage.setPageUrl(ViewConstants.CLIENT_POC_COST_CENTERS);
		
		setClientAccount(customerAccountService.getOpenCustomerAccountByCode((String)map.get(ViewConstants.VIEW_PARAM_ACCOUNT_CODE)));	
		setClientPoint(clientPOCService.getClientPoint((Long)map.get(ViewConstants.VIEW_PARAM_CPNT_ID)));
		
		if(MALUtilities.isEmpty(map.get(ViewConstants.VIEW_PARAM_CPNTA_ID))){
			setPoc(clientPOCService.saveOrUpdateClientPOC(getClientAccount(), getClientPoint()));
		} else {
			setPoc(clientPOCService.getClientPOC((Long)map.get(ViewConstants.VIEW_PARAM_CPNTA_ID)));			
		}												
		
		setDriverAssignedToPOC(clientPOCService.isDriverAssigned(getPoc(), null));							
	} 
	
	/**
	 * Pattern for restoring the view's data
	 */
	@Override
	protected void restoreOldPage() {
		super.thisPage.setPageDisplayName(ViewConstants.DISPLAY_NAME_CLIENT_POC_COST_CENTERS);		
		super.thisPage.setPageUrl(ViewConstants.CLIENT_POC_COST_CENTERS);
		
		setAccountCode((String) thisPage.getRestoreStateValues().get(ViewConstants.VIEW_PARAM_ACCOUNT_CODE));
		setAccountType((String) thisPage.getRestoreStateValues().get(ViewConstants.VIEW_PARAM_ACCOUNT_TYPE));
		setcId(Long.valueOf((String) thisPage.getRestoreStateValues().get(ViewConstants.VIEW_PARAM_C_ID)));
		setClientPoint(clientPOCService.getClientPoint((Long)thisPage.getRestoreStateValues().get(ViewConstants.VIEW_PARAM_CPNT_ID)));
		setClientAccount(customerAccountService.getOpenCustomerAccountByCode(getAccountCode()));
		
		if(!MALUtilities.isEmpty(thisPage.getRestoreStateValues().get(ViewConstants.VIEW_PARAM_CPNTA_ID))){
			setPoc(clientPOCService.getClientPOC((Long)thisPage.getRestoreStateValues().get(ViewConstants.VIEW_PARAM_CPNTA_ID)));			
		}
				
		setDriverAssignedToPOC(clientPOCService.isDriverAssigned(getPoc(), null));					
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



	public ClientPointAccount getPoc() {
		return poc;
	}



	public void setPoc(ClientPointAccount poc) {
		this.poc = poc;
	}



	public boolean isDriverAssignedToPOC() {
		return driverAssignedToPOC;
	}



	public void setDriverAssignedToPOC(boolean driverAssignedIndicator) {
		this.driverAssignedToPOC = driverAssignedIndicator;
	}



	public LazyDataModel<POCCostCenterVO> getLazyPOCCostCenterVOs() {
		return lazyPOCCostCenterVOs;
	}



	public void setLazyPOCCostCenterVOs(LazyDataModel<POCCostCenterVO> lazyPOCCostCenterVOs) {
		this.lazyPOCCostCenterVOs = lazyPOCCostCenterVOs;
	}



	public int getResultPerPage() {
		return resultPerPage;
	}



	public void setResultPerPage(int resultPerPage) {
		this.resultPerPage = resultPerPage;
	}

	public TreeNode getRoot() {
		return root;
	}

	public void setRoot(TreeNode root) {
		this.root = root;
	}
	
	
	public List<POCCostCenterVO> getPocCostCenterVOs() {
		return pocCostCenterVOs;
	}

	public void setPocCostCenterVOs(List<POCCostCenterVO> pocCostCenterVOs) {
		this.pocCostCenterVOs = pocCostCenterVOs;
	}

	/**
	 * Algorithm for taking a list of POCCostCenterVOs and building primefaces TreeNode model
	 * for the TreeeData table.
	 * @param parentNode
	 * @param costCenterVO 
	 * @return
	 */
	private TreeNode initCostCenterTree(TreeNode parentNode, POCCostCenterVO costCenterVO){
		TreeNode node = null;
		Map<String, TreeNode> treeNodeMap;		
		
		if(MALUtilities.isEmpty(parentNode) && MALUtilities.isEmpty(costCenterVO)){ 
			treeNodeMap = new HashMap<String, TreeNode>();			
			node = initCostCenterTree(null, new POCCostCenterVO());
			
			for(POCCostCenterVO ccVO : getPocCostCenterVOs()){				
				if(MALUtilities.isEmpty(ccVO.getParentCode())){						
					treeNodeMap.put(ccVO.getCode(), initCostCenterTree(node, ccVO));
				} else {
					treeNodeMap.put(ccVO.getCode(), initCostCenterTree(treeNodeMap.get(ccVO.getParentCode()), ccVO));
				} 					
			}			
		} else { 
			node = new DefaultTreeNode(costCenterVO, parentNode);
			node.setExpanded(true);
		}
		
		return node;
	}
}
