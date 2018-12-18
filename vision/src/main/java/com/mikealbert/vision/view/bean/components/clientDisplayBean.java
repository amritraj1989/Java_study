package com.mikealbert.vision.view.bean.components;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.faces.context.FacesContext;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.mikealbert.data.entity.ExternalAccount;
import com.mikealbert.data.entity.QuotationProfile;
import com.mikealbert.data.enumeration.CorporateEntity;
import com.mikealbert.service.CustomerAccountService;
import com.mikealbert.service.QuotationProfileService;
import com.mikealbert.util.MALUtilities;
import com.mikealbert.vision.view.bean.BaseBean;
import com.mikealbert.vision.view.link.EditViewClientLink;

@Component
@Scope("view")
public class clientDisplayBean extends BaseBean {	
	private static final long serialVersionUID = -7170421837551409230L;
	
	@Resource CustomerAccountService customerAccountService;
	@Resource QuotationProfileService quotationProfileService;

	private Long cId;
	private String accountCode;
	private String accountType;
	private EditViewClientLink parentBean;
	private List<QuotationProfile> quotationProfiles;
	private List<ExternalAccount> childAccountList;
	private ExternalAccount clientAccount;
	private ExternalAccount parentAccount; 
	private String update;
	private Boolean ajax;

	public Long getcId() {
		return cId;
	}


	public void setcId(Long cId) {
		this.cId = cId;
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
	
	public EditViewClientLink getParentBean() {
		return parentBean;
	}


	public void setParentBean(EditViewClientLink parentBean) {
		this.parentBean = parentBean;
	}


	/**
	 * Initializes the bean
	 */
    @PostConstruct
    public void init(){ 
    	try {
    		FacesContext fc = FacesContext.getCurrentInstance();
    	    cId = fc.getApplication().evaluateExpressionGet(fc,"#{cc.attrs.cId}", Long.class);
    	    accountType = fc.getApplication().evaluateExpressionGet(fc,"#{cc.attrs.accountType}", String.class);
    	    accountCode = fc.getApplication().evaluateExpressionGet(fc,"#{cc.attrs.accountCode}", String.class);
    	    parentBean = fc.getApplication().evaluateExpressionGet(fc,"#{cc.attrs.parentBean}", EditViewClientLink.class);
    	    update = fc.getApplication().evaluateExpressionGet(fc,"#{cc.attrs.update}", String.class);
    	    ajax = fc.getApplication().evaluateExpressionGet(fc,"#{cc.attrs.ajax}", Boolean.class);
    	    loadClientAccountPanel();
    	    

		} catch(Exception e) {	
			super.addErrorMessage("generic.error", e.getMessage());
		}    	
      	    	    	
    }
    
    /**
     * This method assumes that there are only 2 tiers of accounts. Parent Accounts and Child Accounts.
     * The Client Account panel should show the whole account structure no matter whether you are on 
     * a child account or a parent account. 
     */
    private void loadClientAccountPanel(){
		setClientAccount(customerAccountService.getOpenCustomerAccountByCode(getAccountCode(), getLoggedInUser().getCorporateEntity()));
		if(getClientAccount() != null && !MALUtilities.isEmpty(getClientAccount().getExternalAccountPK().getAccountCode())){
			setParentAccount(customerAccountService.getParentAccount(getClientAccount()));
			if(getParentAccount().getExternalAccountPK() == null || (getParentAccount().getExternalAccountPK() != null && MALUtilities.isEmpty(getParentAccount().getExternalAccountPK().getAccountCode()))){
				setParentAccount(clientAccount); // if there is no parent account, assume current account is parent
			}
			setChildAccountList(customerAccountService.getChildAccounts(getParentAccount()));
			setQuotationProfiles(quotationProfileService.fetchCustomerQuotationProfiles(CorporateEntity.MAL.getCorpId(), "C", getClientAccount().getExternalAccountPK().getAccountCode()));
		}
	}

	public String getUpdate() {
		return update;
	}


	public void setUpdate(String update) {
		this.update = update;
	}


	public Boolean getAjax() {
		return ajax;
	}


	public void setAjax(Boolean ajax) {
		this.ajax = ajax;
	}


	public List<QuotationProfile> getQuotationProfiles() {
		return quotationProfiles;
	}


	public void setQuotationProfiles(List<QuotationProfile> quotationProfiles) {
		this.quotationProfiles = quotationProfiles;
	}


	public List<ExternalAccount> getChildAccountList() {
		return childAccountList;
	}


	public void setChildAccountList(List<ExternalAccount> childAccountList) {
		this.childAccountList = childAccountList;
	}


	public ExternalAccount getClientAccount() {
		return clientAccount;
	}


	public void setClientAccount(ExternalAccount clientAccount) {
		this.clientAccount = clientAccount;
	}


	public ExternalAccount getParentAccount() {
		return parentAccount;
	}


	public void setParentAccount(ExternalAccount parentAccount) {
		this.parentAccount = parentAccount;
	}
	
	public void editViewClientFinance(ExternalAccount account){
		if(!MALUtilities.isEmpty(getParentBean())){
    	    if(ajax){
				cId = account.getExternalAccountPK().getCId();
	    	    accountType = account.getExternalAccountPK().getAccountType();
	    	    accountCode = account.getExternalAccountPK().getAccountCode();
	    	    loadClientAccountPanel();
    	    }
			getParentBean().editViewClient(account);
		}
	}
}
