package com.mikealbert.data.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;


/**
 * The persistent class for the QUOTE_REQUEST_QUOTES database table.
 * @author Raj
 */
@Entity
@Table(name="QUOTE_REQUEST_QUOTES")
public class QuoteRequestQuote extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="QRQQ_SEQ")    
    @SequenceGenerator(name="QRQQ_SEQ", sequenceName="QRQQ_SEQ", allocationSize=1)  
    @Column(name = "QRQQ_ID")
    private Long quoteRequestQuoteId;
    
    @Column(name = "LEASE_TERM", length = 12)
    private Long leaseTerm;
    
    @Column(name = "LEASE_MILES", length = 12)
    private Long leaseMiles;
    
    @Column(name = "PROJECTED_REPLACE_MONTH", length = 12)
    private Long projectedReplaceMonth;
    
    @Column(name = "DEPRECIATION_METHOD_VALUE", length = 40)
    private BigDecimal depreciationMethodValue;
    
    @Column(name = "QUO_QUO_ID", length = 12)
    private Long quoId;
    
    @Column(name = "STEP_1", length = 12)
    private Long step1;
    
    @Column(name = "STEP_2", length = 12)
    private Long step2;
    
    @Column(name = "STEP_3", length = 12)
    private Long step3;
    
    @Column(name = "STEP_4", length = 12)
    private Long step4;
    
    @Column(name = "STEP_5", length = 12)
    private Long step5;
    
    @JoinColumn(name = "DRIVER_GRADE_GROUP", referencedColumnName = "DRIVER_GRADE_GROUP")
    @ManyToOne(optional = true)
    private DriverGradeGroupCode driverGradeGroupCode;
    
    @JoinColumn(name = "QPR_QPR_ID", referencedColumnName = "QPR_ID")
    @ManyToOne(optional = true)
    private QuotationProfile quotationProfile;
    
    @JoinColumn(name = "QRDM_QRDM_ID", referencedColumnName = "QRDM_ID")
    @ManyToOne(optional = true)
    private QuoteRequestDepreciationMethod quoteRequestDepreciationMethod;
    
    @JoinColumn(name = "QRPT_QRPT_ID", referencedColumnName = "QRPT_ID")
    @ManyToOne(optional = true)
    private QuoteRequestPaymentType quoteRequestPaymentType;
    
    @JoinColumn(name = "QRQ_QRQ_ID", referencedColumnName = "QRQ_ID")
    @ManyToOne(optional = false)
    private QuoteRequest quoteRequest;

	public Long getQuoteRequestQuoteId() {
		return quoteRequestQuoteId;
	}

	public void setQuoteRequestQuoteId(Long quoteRequestQuoteId) {
		this.quoteRequestQuoteId = quoteRequestQuoteId;
	}

	public Long getLeaseTerm() {
		return leaseTerm;
	}

	public void setLeaseTerm(Long leaseTerm) {
		this.leaseTerm = leaseTerm;
	}

	public Long getLeaseMiles() {
		return leaseMiles;
	}

	public void setLeaseMiles(Long leaseMiles) {
		this.leaseMiles = leaseMiles;
	}

	public Long getProjectedReplaceMonth() {
		return projectedReplaceMonth;
	}

	public void setProjectedReplaceMonth(Long projectedReplaceMonth) {
		this.projectedReplaceMonth = projectedReplaceMonth;
	}

	public BigDecimal getDepreciationMethodValue() {
		return depreciationMethodValue;
	}

	public void setDepreciationMethodValue(BigDecimal depreciationMethodValue) {
		this.depreciationMethodValue = depreciationMethodValue;
	}

	public Long getQuoId() {
		return quoId;
	}

	public void setQuoId(Long quoId) {
		this.quoId = quoId;
	}

	public Long getStep1() {
		return step1;
	}

	public void setStep1(Long step1) {
		this.step1 = step1;
	}

	public Long getStep2() {
		return step2;
	}

	public void setStep2(Long step2) {
		this.step2 = step2;
	}

	public Long getStep3() {
		return step3;
	}

	public void setStep3(Long step3) {
		this.step3 = step3;
	}

	public Long getStep4() {
		return step4;
	}

	public void setStep4(Long step4) {
		this.step4 = step4;
	}

	public Long getStep5() {
		return step5;
	}

	public void setStep5(Long step5) {
		this.step5 = step5;
	}

	public DriverGradeGroupCode getDriverGradeGroupCode() {
		return driverGradeGroupCode;
	}

	public void setDriverGradeGroupCode(DriverGradeGroupCode driverGradeGroupCode) {
		this.driverGradeGroupCode = driverGradeGroupCode;
	}

	public QuotationProfile getQuotationProfile() {
		return quotationProfile;
	}

	public void setQuotationProfile(QuotationProfile quotationProfile) {
		this.quotationProfile = quotationProfile;
	}

	public QuoteRequestDepreciationMethod getQuoteRequestDepreciationMethod() {
		return quoteRequestDepreciationMethod;
	}

	public void setQuoteRequestDepreciationMethod(QuoteRequestDepreciationMethod quoteRequestDepreciationMethod) {
		this.quoteRequestDepreciationMethod = quoteRequestDepreciationMethod;
	}

	public QuoteRequestPaymentType getQuoteRequestPaymentType() {
		return quoteRequestPaymentType;
	}

	public void setQuoteRequestPaymentType(QuoteRequestPaymentType quoteRequestPaymentType) {
		this.quoteRequestPaymentType = quoteRequestPaymentType;
	}

	public QuoteRequest getQuoteRequest() {
		return quoteRequest;
	}

	public void setQuoteRequest(QuoteRequest quoteRequest) {
		this.quoteRequest = quoteRequest;
	}

}