package com.mikealbert.data.entity;

import java.io.Serializable;
import javax.persistence.*;
import java.math.BigDecimal;


/**
 * The persistent class for the PAYMENT_DETAIL database table.
 * 
 */
@Entity
@Table(name="PAYMENT_DETAIL")
public class PaymentDetail implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="PAYMENT_DETAIL_ID")
	private long paymentDetailId;

	@Column(name="PAYMENT_IN_UNIT")
	private BigDecimal paymentInUnit;

	@Column(name="PAYMENT_SEQUENCE")
	private BigDecimal paymentSequence;

	@Column(name="RENTAL_FACTOR")
	private BigDecimal rentalFactor;

	//bi-directional many-to-one association to PaymentHeader
    @ManyToOne
	@JoinColumn(name="PH_PAYMENT_ID")
	private PaymentHeader paymentHeader;

    public PaymentDetail() {
    }

	public long getPaymentDetailId() {
		return this.paymentDetailId;
	}

	public void setPaymentDetailId(long paymentDetailId) {
		this.paymentDetailId = paymentDetailId;
	}

	public BigDecimal getPaymentInUnit() {
		return this.paymentInUnit;
	}

	public void setPaymentInUnit(BigDecimal paymentInUnit) {
		this.paymentInUnit = paymentInUnit;
	}

	public BigDecimal getPaymentSequence() {
		return this.paymentSequence;
	}

	public void setPaymentSequence(BigDecimal paymentSequence) {
		this.paymentSequence = paymentSequence;
	}

	public BigDecimal getRentalFactor() {
		return this.rentalFactor;
	}

	public void setRentalFactor(BigDecimal rentalFactor) {
		this.rentalFactor = rentalFactor;
	}

	public PaymentHeader getPaymentHeader() {
		return this.paymentHeader;
	}

	public void setPaymentHeader(PaymentHeader paymentHeader) {
		this.paymentHeader = paymentHeader;
	}
	
}