package com.mikealbert.data.entity;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;

@Embeddable
public class CustomerQuotationProfilePK implements Serializable{
	private static final long serialVersionUID = 1L;
	
    @JoinColumns({
        @JoinColumn(name = "EA_C_ID", referencedColumnName = "C_ID"),
        @JoinColumn(name = "EA_ACCOUNT_TYPE", referencedColumnName = "ACCOUNT_TYPE"),
        @JoinColumn(name = "EA_ACCOUNT_CODE", referencedColumnName = "ACCOUNT_CODE")})
    @ManyToOne(optional = false)
    private ExternalAccount externalAccount;
    
    @JoinColumn(name = "QPR_QPR_ID", referencedColumnName = "QPR_ID")
    @ManyToOne(optional = false)
    private QuotationProfile quotationProfile;

	public ExternalAccount getExternalAccount() {
		return externalAccount;
	}

	public void setExternalAccount(ExternalAccount externalAccount) {
		this.externalAccount = externalAccount;
	}

	public QuotationProfile getQuotationProfile() {
		return quotationProfile;
	}

	public void setQuotationProfile(QuotationProfile quotationProfile) {
		this.quotationProfile = quotationProfile;
	}

}
