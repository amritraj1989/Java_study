package com.mikealbert.data.entity;

import java.io.Serializable;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "QUOTE_PROFILE_CUST")
public class CustomerQuotationProfile extends BaseEntity implements Serializable{

	private static final long serialVersionUID = 1L;

	@EmbeddedId
	protected CustomerQuotationProfilePK customerQuotationProfilePK;

	public CustomerQuotationProfilePK getCustomerQuotationProfilePK() {
		return customerQuotationProfilePK;
	}

	public void setCustomerQuotationProfilesPK(
			CustomerQuotationProfilePK customerQuotationProfilePK) {
		this.customerQuotationProfilePK = customerQuotationProfilePK;
	}
	
}
