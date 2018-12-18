package com.mikealbert.data.entity;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.mikealbert.data.beanvalidation.MANotNull;
import com.mikealbert.data.beanvalidation.MASize;

/**
 * Composite Primary Key for QUOTE_PROFILE_CUST table
 * @author sibley
 */
@Embeddable
public class QuoteProfileCustPK implements Serializable {
	private static final long serialVersionUID = 1L;

	@Basic(optional = false)
	@NotNull
	@Column(name = "EA_C_ID")
	private long cId;

	@Basic(optional = false)
	@NotNull
	@Size(min = 1, max = 1)
	@Column(name = "EA_ACCOUNT_TYPE")
	private String accountType;

	@Basic(optional = false)
	@MANotNull(label = "Account")
	@MASize(label = "Account", min = 1, max = 25)
	@Column(name = "EA_ACCOUNT_CODE")
	private String accountCode;

	@JoinColumn(name="QPR_QPR_ID", referencedColumnName = "QPR_ID")
	@ManyToOne(fetch=FetchType.LAZY)
	private QuotationProfile quotationProfile;

	public QuoteProfileCustPK() {}

	public QuoteProfileCustPK(long cId, String accountType, String accountCode, QuotationProfile qprId) {
		this.cId = cId;
		this.accountType = accountType;
		this.accountCode = accountCode;
		this.setQuotationProfile(qprId);
	}

	public long getcId() {
		return cId;
	}

	public void setcId(long cId) {
		this.cId = cId;
	}

	public long getCId() {
		return cId;
	}

	public void setCId(long cId) {
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

	public QuotationProfile getQuotationProfile() {
		return quotationProfile;
	}

	public void setQuotationProfile(QuotationProfile quotationProfile) {
		this.quotationProfile = quotationProfile;
	}

	@Override
	public String toString() {
		return "QuoteProfileCustPK [cId=" + cId + ", accountType=" + accountType + ", accountCode=" + accountCode
				+ ", quotationProfile=" + quotationProfile + "]";
	}


}
