package com.mikealbert.vision.view.bean;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.faces.event.AjaxBehaviorEvent;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.mikealbert.data.entity.ExternalAccount;
import com.mikealbert.data.entity.FinanceParameterCategory;
import com.mikealbert.data.enumeration.ClientFinanceTypeCodes;
import com.mikealbert.data.vo.ClientFinanceVO;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.service.CustomerAccountService;
import com.mikealbert.service.FinanceParameterService;
import com.mikealbert.util.MALUtilities;
import com.mikealbert.vision.view.ViewConstants;
import com.mikealbert.vision.view.link.EditViewClientLink;

@Component
@Scope("view")
public class GradeGroupFinanceBean extends StatefulBaseBean{
	private static final long serialVersionUID = -4589582730235934254L;
	
	@Resource CustomerAccountService customerAccountService;
	
	private List<ClientFinanceVO> rowList;
	private ClientFinanceVO inputClientFinanceVO;
	private ExternalAccount clientAccount;
	private int DEFAULT_DATATABLE_HEIGHT = 225;
	
	@Resource
	private FinanceParameterService financeParameterService;
	
	@PostConstruct
	public void init() {
	    	initializeDataTable(500, 500, new int[] {25,25,25,25}).setHeight(DEFAULT_DATATABLE_HEIGHT); 
		super.openPage();
		try {
			setClientAccount(customerAccountService.getOpenCustomerAccountByCode(getInputClientFinanceVO().getEaAccountCode()));
			loadData();
		} catch (Exception ex) {
			logger.error(ex);
			super.addErrorMessage("generic.error", ex.getMessage());
		}
	}	
	
	public void loadData() {
		try {
			loadRows();
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
			rowList = financeParameterService.searchClientFinanceParametersForGradeGroupByAccountAndParam(clientAccount.getExternalAccountPK().getCId(), clientAccount.getExternalAccountPK().getAccountType(), clientAccount.getExternalAccountPK().getAccountCode(), inputClientFinanceVO.getParameterId());
		}catch(Exception ex){
			ex.printStackTrace(); //TODO need to handle exception
		}
	}
	
	@Override
	protected void loadNewPage() {
		Map<String, Object> map = super.thisPage.getInputValues();
		
		thisPage.setPageUrl(ViewConstants.GRADE_GROUP_FINANCE);
		thisPage.setPageDisplayName(ViewConstants.DISPLAY_NAME_GRADE_GROUP_FINANCE);
		this.setInputClientFinanceVO((ClientFinanceVO) map.get(ViewConstants.VIEW_PARAM_CLIENT_FINANCE_VO));
	}

	@Override
	protected void restoreOldPage() {
		
	}
	
	public String saveClientFinanceParams(){
		try{
			financeParameterService.saveUpdateOrDeleteClientFinanceParameters(rowList, ClientFinanceTypeCodes.EXTERNAL_ACCOUNT_GRADE_GROUP.getCode());
			super.addSuccessMessage("process.success","Save Client Finance Parameters");
			loadRows();
		}catch(Exception ex){
			logger.error(ex);		
			handleException("generic.error",new String[]{ex.getMessage()}, ex, null);
		}
		
		return null;
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

	public ClientFinanceVO getInputClientFinanceVO() {
		return inputClientFinanceVO;
	}

	public void setInputClientFinanceVO(ClientFinanceVO inputClientFinanceVO) {
		this.inputClientFinanceVO = inputClientFinanceVO;
	}

	public ExternalAccount getClientAccount() {
		return clientAccount;
	}

	public void setClientAccount(ExternalAccount clientAccount) {
		this.clientAccount = clientAccount;
	}

}
