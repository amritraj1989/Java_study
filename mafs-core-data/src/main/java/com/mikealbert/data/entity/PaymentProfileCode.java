package com.mikealbert.data.entity;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;


/**
 * The persistent class for the PAYMENT_PROFILE_CODES database table.
 * 
 */
@Entity
@Table(name="PAYMENT_PROFILE_CODES")
public class PaymentProfileCode implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="PAYMENT_PROFILE")
	private String paymentProfile;

	private String description;

	//bi-directional many-to-one association to PaymentHeader
	@OneToMany(mappedBy="paymentProfileCode")
	private List<PaymentHeader> paymentHeaders;

    public PaymentProfileCode() {
    }

	public String getPaymentProfile() {
		return this.paymentProfile;
	}

	public void setPaymentProfile(String paymentProfile) {
		this.paymentProfile = paymentProfile;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<PaymentHeader> getPaymentHeaders() {
		return this.paymentHeaders;
	}

	public void setPaymentHeaders(List<PaymentHeader> paymentHeaders) {
		this.paymentHeaders = paymentHeaders;
	}
	
}