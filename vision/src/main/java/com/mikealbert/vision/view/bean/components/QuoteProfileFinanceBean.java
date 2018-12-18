package com.mikealbert.vision.view.bean.components;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.mikealbert.data.entity.ExternalAccount;
import com.mikealbert.data.vo.QuotationProfileFinanceVO;
import com.mikealbert.service.FinanceParameterService;
import com.mikealbert.util.MALUtilities;
import com.mikealbert.vision.view.bean.BaseBean;

@Component
@Scope("view")
public class QuoteProfileFinanceBean extends BaseBean {	
	private static final long serialVersionUID = -7170421837551409230L;

	@Resource FinanceParameterService financeParameterService;
	
	private List<QuotationProfileFinanceVO> quotationProfileFinanceList;
	private Long cId;
	private String accountType;
	private String accountCode;
	private Long parameterId;
	
	public void initDialog(ExternalAccount clientAccount, Long parameterId){
		setcId(clientAccount.getExternalAccountPK().getCId());
		setAccountType(clientAccount.getExternalAccountPK().getAccountType());
		setAccountCode(clientAccount.getExternalAccountPK().getAccountCode());
		setParameterId(parameterId);

    	try {		
    		if (!MALUtilities.isEmpty(getcId()) && MALUtilities.isNotEmptyString(getAccountType()) && MALUtilities.isNotEmptyString(getAccountCode()) && !MALUtilities.isEmpty(getParameterId())) {
    	    	loadQuotationProfileFinanceParams();
    	    }
		} catch(Exception e) {
			super.addErrorMessage("generic.error", e.getMessage());
		}  
    }
	
    private void loadQuotationProfileFinanceParams(){
    	setQuotationProfileFinanceList(financeParameterService.getProfileFinancesByClientAndParameter(getcId(), getAccountType(), getAccountCode(), getParameterId()));
    }

	public List<QuotationProfileFinanceVO> getQuotationProfileFinanceList() {
		return quotationProfileFinanceList;
	}

	public void setQuotationProfileFinanceList(
			List<QuotationProfileFinanceVO> quotationProfileFinanceList) {
		this.quotationProfileFinanceList = quotationProfileFinanceList;
	}

	public Long getcId() {
		return cId;
	}

	public void setcId(Long cId) {
		this.cId = cId;
	}

	public String getAccountType() {
		return accountType;
	}

	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}

	public String getAccountCode() {
		return accountCode;
	}

	public void setAccountCode(String accountCode) {
		this.accountCode = accountCode;
	}

	public Long getParameterId() {
		return parameterId;
	}

	public void setParameterId(Long parameterId) {
		this.parameterId = parameterId;
	}
}
