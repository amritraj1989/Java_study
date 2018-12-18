package com.mikealbert.vision.view.bean;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.faces.event.AjaxBehaviorEvent;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.mikealbert.data.entity.CostCentreCode;
import com.mikealbert.data.entity.DriverGrade;
import com.mikealbert.data.entity.ExternalAccount;
import com.mikealbert.data.entity.FinanceParameterCategory;
import com.mikealbert.data.enumeration.ClientFinanceTypeCodes;
import com.mikealbert.data.vo.ClientFinanceVO;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.service.CostCenterService;
import com.mikealbert.service.CustomerAccountService;
import com.mikealbert.service.DriverGradeService;
import com.mikealbert.service.FinanceParameterService;
import com.mikealbert.util.MALUtilities;
import com.mikealbert.vision.view.ViewConstants;
import com.mikealbert.vision.view.link.EditViewClientLink;

@Component
@Scope("view")
public class ClientFinanceBean extends StatefulBaseBean implements EditViewClientLink{
	private static final long serialVersionUID = -4589582730235934254L;
	
	@Resource CustomerAccountService customerAccountService;
	@Resource FinanceParameterService financeParameterService;
	@Resource CostCenterService costCenterService;
	@Resource DriverGradeService driverGradeService;
	
	private List<ClientFinanceVO> rowList;
	private List<CostCentreCode> costCenterList;
	private List<DriverGrade> gradeGroupList;
	private String accountCode;
	private String accountType;
	private Long accountCId;
	private ExternalAccount clientAccount;
	private List<FinanceParameterCategory> financeParameterCategories;
	private FinanceParameterCategory selectedFinanceCategory;
	private boolean cancelEnabled = false;
	private boolean costCentersExistForClient = false;
	private boolean gradeGroupsExistForClient = false;
	private int DEFAULT_DATATABLE_HEIGHT = 150;
	
	@PostConstruct
	public void init() {
	    	initializeDataTable(500, 500, new int[] {10, 10, 10, 10, 10, 10}).setHeight(DEFAULT_DATATABLE_HEIGHT); 
		super.openPage();
		try {
			this.setFinanceParameterCategories(lookupCacheService.getFinanceParameterCategories());
			loadData();
		} catch (Exception ex) {
			logger.error(ex);
			super.addErrorMessage("generic.error", ex.getMessage());
		}
	}	
	
	public void loadData() {
		try {
			if(accountCode != null){
				this.clientAccount = customerAccountService.getOpenCustomerAccountByCode(getAccountCode(), getLoggedInUser().getCorporateEntity());
				this.costCentersExistForClient = checkCostCentersExistForClient();
				this.gradeGroupsExistForClient = checkGradeGroupsExistForClient();
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
	
	public Boolean isExistQuoteProfileFinanceParam(Long parameterId) {
		if (financeParameterService.countProfileFinancesByClientAndParameter(clientAccount.getExternalAccountPK().getCId(), clientAccount.getExternalAccountPK().getAccountType(), clientAccount.getExternalAccountPK().getAccountCode(), parameterId) > 0) {
			return true;
		}
		return false;
	}
	
	/**
	 * If a client has cost centers return true, else return false
	 * @return boolean
	 */
	public Boolean checkCostCentersExistForClient(){
		this.costCenterList = costCenterService.getAllCostCentersByAccount(clientAccount);
		if(costCenterList != null && costCenterList.size() > 0){
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * If a client has grade groups return true, else return false
	 * @return boolean
	 */
	public Boolean checkGradeGroupsExistForClient(){
		this.gradeGroupList = driverGradeService.getExternalAccountDriverGrades(clientAccount);
		if(gradeGroupList != null && gradeGroupList.size() > 0){
			return true;
		}else{
			return false;
		}
	}
		
	private void loadRows() throws Exception {
		try{
			FinanceParameterCategory financeCategory = getSelectedFinanceCategory();
			if (!MALUtilities.isEmpty(accountCId) && !MALUtilities.isEmpty(accountType) && !MALUtilities.isEmpty(accountCode)) {
				if (MALUtilities.isEmpty(financeCategory)){
					rowList = financeParameterService.searchClientFinanceParametersByAccount(getAccountCId(), getAccountType(), getAccountCode(), 0L);
				}
				else{
					rowList = financeParameterService.searchClientFinanceParametersByAccount(getAccountCId(), getAccountType(), getAccountCode(), getSelectedFinanceCategory().getFpcId());
				}
			}
		}catch(Exception ex){
			ex.printStackTrace(); //TODO need to handle exception
		}
	}
	
	@Override
	protected void loadNewPage() {
		Map<String, Object> map = super.thisPage.getInputValues();
		
		thisPage.setPageUrl(ViewConstants.CLIENT_FINANCE);
		thisPage.setPageDisplayName(ViewConstants.DISPLAY_NAME_CLIENT_FINANCE);
		this.setAccountCId((Long) map.get(ViewConstants.VIEW_PARAM_C_ID));
		this.setAccountType((String) map.get(ViewConstants.VIEW_PARAM_ACCOUNT_TYPE));
		this.setAccountCode((String) map.get(ViewConstants.VIEW_PARAM_ACCOUNT_CODE));
		if(map.containsKey(ViewConstants.VIEW_PARAM_CANCEL)){
			this.setCancelEnabled(((Boolean) map.get(ViewConstants.VIEW_PARAM_CANCEL)).booleanValue());
		}
	}

	@Override
	protected void restoreOldPage() {
		setAccountCId((Long)thisPage.getRestoreStateValues().get(ViewConstants.VIEW_PARAM_C_ID));
		setAccountType(thisPage.getRestoreStateValues().get(ViewConstants.VIEW_PARAM_ACCOUNT_TYPE).toString());
		setAccountCode(thisPage.getRestoreStateValues().get(ViewConstants.VIEW_PARAM_ACCOUNT_CODE).toString());
		if(thisPage.getRestoreStateValues().containsKey((ViewConstants.VIEW_PARAM_CANCEL))){
			this.setCancelEnabled(((Boolean) thisPage.getRestoreStateValues().get(ViewConstants.VIEW_PARAM_CANCEL)).booleanValue());
		}
		setSelectedFinanceCategory((FinanceParameterCategory) thisPage.getRestoreStateValues().get("FINANCE_PARAMETER_CATEGORY_FILTER"));
	}
	
	public String saveClientFinanceParams(){
		try{
			financeParameterService.saveUpdateOrDeleteClientFinanceParameters(rowList, ClientFinanceTypeCodes.ACCOUNT.getCode());
			super.addSuccessMessage("process.success","Save Client Finance Parameters");
			loadRows();
		} catch (MalBusinessException exception) {
			super.addErrorMessage("generic.error", exception.getMessage());
		}
		catch(Exception ex){
			logger.error(ex);		
			handleException("generic.error",new String[]{ex.getMessage()}, ex, null);
		}
		
		return null;
	}
	
	public void costCentreFinanceParam(ClientFinanceVO clientFinanceVO) {
		restoreStateValues(clientFinanceVO);
		Map<String, Object> nextPageValues = new HashMap<String, Object>();
		nextPageValues.put(ViewConstants.VIEW_PARAM_CLIENT_FINANCE_VO, clientFinanceVO);
  		saveNextPageInitStateValues(nextPageValues);
  		this.forwardToURL(ViewConstants.COST_CENTRE_FINANCE);
	}
	
	public void gradeGroupFinanceParam(ClientFinanceVO clientFinanceVO) {
		restoreStateValues(clientFinanceVO);
		Map<String, Object> nextPageValues = new HashMap<String, Object>();
		nextPageValues.put(ViewConstants.VIEW_PARAM_CLIENT_FINANCE_VO, clientFinanceVO);
  		saveNextPageInitStateValues(nextPageValues);
  		this.forwardToURL(ViewConstants.GRADE_GROUP_FINANCE);
	}
	
	public void restoreStateValues(ClientFinanceVO clientFinanceVO) {
		Map<String, Object> restoreStateValues = new HashMap<String, Object>();
		restoreStateValues.put(ViewConstants.VIEW_PARAM_C_ID, clientFinanceVO.getEaCId());
		restoreStateValues.put(ViewConstants.VIEW_PARAM_ACCOUNT_TYPE, clientFinanceVO.getEaAccountType());
		restoreStateValues.put(ViewConstants.VIEW_PARAM_ACCOUNT_CODE, clientFinanceVO.getEaAccountCode());
		restoreStateValues.put(ViewConstants.VIEW_PARAM_CANCEL, cancelEnabled);
		restoreStateValues.put("FINANCE_PARAMETER_CATEGORY_FILTER", getSelectedFinanceCategory());
		super.saveRestoreStateValues(restoreStateValues);
	}
	
	public void selectOneMenuListener(AjaxBehaviorEvent event) {
	    
	    try {
			loadRows();
		} catch (Exception e) {
			e.printStackTrace(); //TODO need to handle exception
		}
	}	
	
	/**
     * Handles page cancel button click event
     * @return The calling view
     */
	public String cancel(){
		return super.cancelPage();
	}
	
	public List<ClientFinanceVO> getRowList() {
		return rowList;
	}

	public void setRowList(List<ClientFinanceVO> rowList) {
		this.rowList = rowList;
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

	public List<FinanceParameterCategory> getFinanceParameterCategories() {
		return financeParameterCategories;
	}

	public void setFinanceParameterCategories(
			List<FinanceParameterCategory> financeParameterCategories) {
		this.financeParameterCategories = financeParameterCategories;
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
		loadData();
	}

	public FinanceParameterCategory getSelectedFinanceCategory() {
		return selectedFinanceCategory;
	}

	public void setSelectedFinanceCategory(FinanceParameterCategory selectedFinanceCategory) {
		this.selectedFinanceCategory = selectedFinanceCategory;
	}

	public boolean isCancelEnabled() {
		return cancelEnabled;
	}

	public void setCancelEnabled(boolean cancelEnabled) {
		this.cancelEnabled = cancelEnabled;
	}

	public boolean isCostCentersExistForClient() {
		return costCentersExistForClient;
	}

	public void setCostCentersExistForClient(boolean costCentersExistForClient) {
		this.costCentersExistForClient = costCentersExistForClient;
	}

	public boolean isGradeGroupsExistForClient() {
		return gradeGroupsExistForClient;
	}

	public void setGradeGroupsExistForClient(boolean gradeGroupsExistForClient) {
		this.gradeGroupsExistForClient = gradeGroupsExistForClient;
	}

}
