package com.mikealbert.data.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the QUOTATION_PROFILE_PAYMENTS database table.
 * 
 */
@Entity
@Table(name="QUOTATION_PROFILE_PAYMENTS")
public class QuotationProfilePayment implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private QuotationProfilePaymentPK id;

	//bi-directional many-to-one association to PaymentHeader
    @ManyToOne
	@JoinColumn(name="PM_PAYMENT_ID",insertable=false, updatable=false)
	private PaymentHeader paymentHeader;

    public QuotationProfilePayment() {
    }

	public QuotationProfilePaymentPK getId() {
		return this.id;
	}

	public void setId(QuotationProfilePaymentPK id) {
		this.id = id;
	}
	
	public PaymentHeader getPaymentHeader() {
		return this.paymentHeader;
	}

	public void setPaymentHeader(PaymentHeader paymentHeader) {
		this.paymentHeader = paymentHeader;
	}
	
}