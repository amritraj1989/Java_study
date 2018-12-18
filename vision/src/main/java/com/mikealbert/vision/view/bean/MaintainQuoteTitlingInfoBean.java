package com.mikealbert.vision.view.bean;

import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.mikealbert.common.MalLogger;
import com.mikealbert.common.MalLoggerFactory;
import com.mikealbert.data.dao.ExternalAccountDAO;
import com.mikealbert.data.dao.QuotationModelDAO;
import com.mikealbert.data.entity.ExtAccAffiliate;
import com.mikealbert.data.entity.ExternalAccount;
import com.mikealbert.data.entity.QuotationModel;
import com.mikealbert.service.CustomerAccountService;
import com.mikealbert.service.QuotationService;
import com.mikealbert.vision.view.ViewConstants;



@Component
@Scope("view")
public class MaintainQuoteTitlingInfoBean extends StatefulBaseBean {
	
	private static final long serialVersionUID = 5459137983854538991L;

	MalLogger logger = MalLoggerFactory.getLogger(this.getClass());

	@Resource
	private	ExternalAccountDAO externalAccountDAO;
	@Resource
	private	QuotationModelDAO quotationModelDAO;
	@Resource
	private	CustomerAccountService customerAccountService;
	@Resource
	private	QuotationService quotationService;

	
	private List<ExtAccAffiliate> rowList;
	private int totalRecords;
	private ExtAccAffiliate selectedExtAccAffiliate;
	private String clientInfo;
	private Long cId;
	private String accountType;
	private String accountCode;
	private long qmdId;
	ExternalAccount externalAccount;
	QuotationModel quotationModel;
	private boolean isSaveDisabled;
	private boolean isSubmittedForAcceptance;
	private	boolean	hasEditPermission;
	
	@PostConstruct
	public void init() {		
		super.openPage();

		initializeDataTable(490, 810, new int[] { 2,2});
		hasEditPermission = true;		
		isSubmittedForAcceptance = quotationModel != null ? quotationService.hasBeenSumittedForAcceptance(quotationModel) : true;
		
		loadRowList();
		setSaveButtonMode(); 		
		if(rowList.size() > 0) {
			selectRow();
		}
		totalRecords = rowList.size();

	}
	
	
	private void loadRowList() {
		clientInfo = externalAccount != null ? accountCode + " - " + externalAccount.getAccountName() : "Client not found";
		rowList = customerAccountService.findAffiliatesForAccount(externalAccount);
	}
	
	private void setSaveButtonMode() {		
		if(hasEditPermission && rowList.size() > 0 && quotationModel != null && !isSubmittedForAcceptance) {
			setSaveDisabled(false);			
		} else {
			setSaveDisabled(true);
		}
	}
	
	private void selectRow() {
		if(quotationModel.getExtAccAffiliate() != null) {
			selectedExtAccAffiliate = quotationModel.getExtAccAffiliate();
		} else {
			selectedExtAccAffiliate = null;			
		}
	}
	
	public void clear() {
		selectedExtAccAffiliate = null;
	}
	
	@Override
	protected void loadNewPage() {
		thisPage.setPageDisplayName("Select Titling Info");
		thisPage.setPageUrl(ViewConstants.QUOTE_TITLING);
		
		Map<String, Object> map = super.thisPage.getInputValues();
		if(map.containsKey(ViewConstants.VIEW_PARAM_C_ID) && map.containsKey(ViewConstants.VIEW_PARAM_ACCOUNT_TYPE) && map.containsKey(ViewConstants.VIEW_PARAM_ACCOUNT_CODE)){
			String c_Id = ((String) map.get(ViewConstants.VIEW_PARAM_C_ID));
			cId = Long.valueOf(c_Id);
			accountType = ((String) map.get(ViewConstants.VIEW_PARAM_ACCOUNT_TYPE));
			accountCode = ((String) map.get(ViewConstants.VIEW_PARAM_ACCOUNT_CODE));
			externalAccount = externalAccountDAO.findByAccountCodeAndAccountTypeAndCId(accountCode, accountType, cId);
		}	
		if(map.containsKey(ViewConstants.VIEW_PARAM_QUOTE_MODEL_ID)){
			String qmd = ((String) map.get(ViewConstants.VIEW_PARAM_QUOTE_MODEL_ID));
			qmdId = Long.valueOf(qmd);
			quotationModel = quotationModelDAO.findById(qmdId).orElse(null);
		}	

	}

	@Override
	protected void restoreOldPage() {
	}	
		
	
	
	public String cancel(){
    	return super.cancelPage();      	
    }


	public void save(){
		try {
			quotationService.saveOrUpdateAccountAffiliate(quotationModel, selectedExtAccAffiliate);				
			super.addSimpleMessage("Titling information saved successfully");
		} catch(Exception e) {
			addErrorMessage("generic.error.occured.while", "saving Titling Information");				
		}
    }
	
	public List<ExtAccAffiliate> getRowList() {
		return rowList;
	}

	public void setRowList(List<ExtAccAffiliate> rowList) {
		this.rowList = rowList;
	}

	public ExtAccAffiliate getSelectedExtAccAffiliate() {
		return selectedExtAccAffiliate;
	}

	public void setSelectedExtAccAffiliate(ExtAccAffiliate selectedExtAccAffiliate) {
		this.selectedExtAccAffiliate = selectedExtAccAffiliate;
	}

	public int getTotalRecords() {
		return totalRecords;
	}

	public void setTotalRecords(int totalRecords) {
		this.totalRecords = totalRecords;
	}

	public boolean isHasEditPermission() {
		return hasEditPermission;
	}

	public void setHasEditPermission(boolean hasEditPermission) {
		this.hasEditPermission = hasEditPermission;
	}


	public String getClientInfo() {
		return clientInfo;
	}


	public void setClientInfo(String clientInfo) {
		this.clientInfo = clientInfo;
	}


	public boolean isSaveDisabled() {
		return isSaveDisabled;
	}


	public void setSaveDisabled(boolean isSaveDisabled) {
		this.isSaveDisabled = isSaveDisabled;
	}


}