package com.mikealbert.data.entity;

import java.io.Serializable;
import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;


/**
 * The persistent class for the QUOTATION_SCHEDULES database table.
 * 
 */
@Entity
@Table(name="QUOTATION_SCHEDULES")
public class QuotationSchedule  implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="QSC_ID", unique=true, nullable=false, precision=12)
	private long qscId;

	@Column(nullable=false, precision=11, scale=2)
	private BigDecimal amount;

	@Column(name="DOC_ID", precision=12)
	private BigDecimal docId;

	@Column(name="LUMP_SUM_PAYMENT", precision=30, scale=5)
	private BigDecimal lumpSumPayment;

	@Column(name="NO_MONTHS_BILLED", precision=22)
	private BigDecimal noMonthsBilled;

	@Column(name="NO_OF_PAYMENTS", precision=3)
	private BigDecimal noOfPayments;

	@Column(name="NO_OF_UNITS", nullable=false, precision=30, scale=5)
	private BigDecimal noOfUnits;

	@Column(name="PAYMENT_IND", length=1)
	private String paymentInd;

	@Column(name="REDUNDANT_FLAG", length=1)
	private String redundantFlag;

	@Column(name="RENTAL_YN", nullable=false, length=1)
	private String rentalYn;

	@Column(name="SIN_SIN_ID", precision=12)
	private BigDecimal sinSinId;

	@Column(name="TAX_AMOUNT", precision=30, scale=5)
	private BigDecimal taxAmount;

	@Column(name="TAX_PAYMENT_PERCENTAGE", precision=3)
	private BigDecimal taxPaymentPercentage;

    @Temporal( TemporalType.DATE)
	@Column(name="TAX_YEAR_END_DATE")
	private Date taxYearEndDate;

    @Temporal( TemporalType.DATE)
	@Column(name="TRANS_DATE")
	private Date transDate;

	//bi-directional many-to-one association to QuotationElement
    @ManyToOne
	@JoinColumn(name="QEL_QEL_ID", nullable=false)
	private QuotationElement quotationElement;

    public QuotationSchedule() {
    }

	public long getQscId() {
		return this.qscId;
	}

	public void setQscId(long qscId) {
		this.qscId = qscId;
	}

	public BigDecimal getAmount() {
		return this.amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public BigDecimal getDocId() {
		return this.docId;
	}

	public void setDocId(BigDecimal docId) {
		this.docId = docId;
	}

	public BigDecimal getLumpSumPayment() {
		return this.lumpSumPayment;
	}

	public void setLumpSumPayment(BigDecimal lumpSumPayment) {
		this.lumpSumPayment = lumpSumPayment;
	}

	public BigDecimal getNoMonthsBilled() {
		return this.noMonthsBilled;
	}

	public void setNoMonthsBilled(BigDecimal noMonthsBilled) {
		this.noMonthsBilled = noMonthsBilled;
	}

	public BigDecimal getNoOfPayments() {
		return this.noOfPayments;
	}

	public void setNoOfPayments(BigDecimal noOfPayments) {
		this.noOfPayments = noOfPayments;
	}

	public BigDecimal getNoOfUnits() {
		return this.noOfUnits;
	}

	public void setNoOfUnits(BigDecimal noOfUnits) {
		this.noOfUnits = noOfUnits;
	}

	public String getPaymentInd() {
		return this.paymentInd;
	}

	public void setPaymentInd(String paymentInd) {
		this.paymentInd = paymentInd;
	}

	public String getRedundantFlag() {
		return this.redundantFlag;
	}

	public void setRedundantFlag(String redundantFlag) {
		this.redundantFlag = redundantFlag;
	}

	public String getRentalYn() {
		return this.rentalYn;
	}

	public void setRentalYn(String rentalYn) {
		this.rentalYn = rentalYn;
	}

	public BigDecimal getSinSinId() {
		return this.sinSinId;
	}

	public void setSinSinId(BigDecimal sinSinId) {
		this.sinSinId = sinSinId;
	}

	public BigDecimal getTaxAmount() {
		return this.taxAmount;
	}

	public void setTaxAmount(BigDecimal taxAmount) {
		this.taxAmount = taxAmount;
	}

	public BigDecimal getTaxPaymentPercentage() {
		return this.taxPaymentPercentage;
	}

	public void setTaxPaymentPercentage(BigDecimal taxPaymentPercentage) {
		this.taxPaymentPercentage = taxPaymentPercentage;
	}

	public Date getTaxYearEndDate() {
		return this.taxYearEndDate;
	}

	public void setTaxYearEndDate(Date taxYearEndDate) {
		this.taxYearEndDate = taxYearEndDate;
	}

	public Date getTransDate() {
		return this.transDate;
	}

	public void setTransDate(Date transDate) {
		this.transDate = transDate;
	}

	public QuotationElement getQuotationElement() {
		return this.quotationElement;
	}

	public void setQuotationElement(QuotationElement quotationElement) {
		this.quotationElement = quotationElement;
	}
	
}