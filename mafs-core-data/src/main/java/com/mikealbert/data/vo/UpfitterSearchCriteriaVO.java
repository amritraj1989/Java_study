package com.mikealbert.data.vo;

import com.mikealbert.data.enumeration.AccountStatusEnum;

public class UpfitterSearchCriteriaVO {

	private String term;
	private AccountStatusEnum accountStatus; 
	private Long modelId;
	private boolean withQuoteNo;

	public String getTerm() {
		return term;
	}

	public void setTerm(String term) {
		this.term = term;
	}

	public AccountStatusEnum getAccountStatus() {
		return accountStatus;
	}

	public void setAccountStatus(AccountStatusEnum accountStatus) {
		this.accountStatus = accountStatus;
	}

	public Long getModelId() {
		return modelId;
	}

	public void setModelId(Long modelId) {
		this.modelId = modelId;
	}

	public boolean isWithQuoteNo() {
		return withQuoteNo;
	}

	public void setWithQuoteNo(boolean withQuoteNo) {
		this.withQuoteNo = withQuoteNo;
	}	
	
}
