package com.mikealbert.data.entity;

import java.io.Serializable;
import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;


/**
 * The persistent class for the PAYMENT_HEADERS database table.
 * 
 */
@Entity
@Table(name="PAYMENT_HEADERS")
public class PaymentHeader implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="PAYMENT_HEADER_ID")
	private long paymentHeaderId;

	@Column(name="ANNUAL_PERIODS")
	private Long annualPeriods;

	@Column(name="FIRST_PAYMENT")
	private BigDecimal firstPayment;

	@Column(name="PAYMENT_DESCRIPTION")
	private String paymentDescription;

	@Column(name="PAYMENT_NAME")
	private String paymentName;

	@Column(name="PMT_PAYMENT_FREQUENCY_CODE")
	private String pmtPaymentFrequencyCode;

	@Column(name="RENTAL_PAUSE_IND")
	private String rentalPauseInd;

	@Column(name="RENTAL_UNITS_DEPOSIT")
	private BigDecimal rentalUnitsDeposit;

	//bi-directional many-to-one association to MulQuotePay
	@OneToMany(mappedBy="paymentHeader")
	private List<MulQuotePay> mulQuotePays;

	//bi-directional many-to-one association to PaymentDetail
	@OneToMany(mappedBy="paymentHeader")
	private List<PaymentDetail> paymentDetails;

	//bi-directional many-to-one association to PaymentProfileCode
    @ManyToOne
	@JoinColumn(name="PAYMENT_PROFILE")
	private PaymentProfileCode paymentProfileCode;

	    public PaymentHeader() {
    }

	public long getPaymentHeaderId() {
		return this.paymentHeaderId;
	}

	public void setPaymentHeaderId(long paymentHeaderId) {
		this.paymentHeaderId = paymentHeaderId;
	}

	public Long getAnnualPeriods() {
		return this.annualPeriods;
	}

	public void setAnnualPeriods(Long annualPeriods) {
		this.annualPeriods = annualPeriods;
	}

	public BigDecimal getFirstPayment() {
		return this.firstPayment;
	}

	public void setFirstPayment(BigDecimal firstPayment) {
		this.firstPayment = firstPayment;
	}

	public String getPaymentDescription() {
		return this.paymentDescription;
	}

	public void setPaymentDescription(String paymentDescription) {
		this.paymentDescription = paymentDescription;
	}

	public String getPaymentName() {
		return this.paymentName;
	}

	public void setPaymentName(String paymentName) {
		this.paymentName = paymentName;
	}

	public String getPmtPaymentFrequencyCode() {
		return this.pmtPaymentFrequencyCode;
	}

	public void setPmtPaymentFrequencyCode(String pmtPaymentFrequencyCode) {
		this.pmtPaymentFrequencyCode = pmtPaymentFrequencyCode;
	}

	public String getRentalPauseInd() {
		return this.rentalPauseInd;
	}

	public void setRentalPauseInd(String rentalPauseInd) {
		this.rentalPauseInd = rentalPauseInd;
	}

	public BigDecimal getRentalUnitsDeposit() {
		return this.rentalUnitsDeposit;
	}

	public void setRentalUnitsDeposit(BigDecimal rentalUnitsDeposit) {
		this.rentalUnitsDeposit = rentalUnitsDeposit;
	}

	public List<MulQuotePay> getMulQuotePays() {
		return this.mulQuotePays;
	}

	public void setMulQuotePays(List<MulQuotePay> mulQuotePays) {
		this.mulQuotePays = mulQuotePays;
	}
	
	public List<PaymentDetail> getPaymentDetails() {
		return this.paymentDetails;
	}

	public void setPaymentDetails(List<PaymentDetail> paymentDetails) {
		this.paymentDetails = paymentDetails;
	}
	
	public PaymentProfileCode getPaymentProfileCode() {
		return this.paymentProfileCode;
	}

	public void setPaymentProfileCode(PaymentProfileCode paymentProfileCode) {
		this.paymentProfileCode = paymentProfileCode;
	}
	

}