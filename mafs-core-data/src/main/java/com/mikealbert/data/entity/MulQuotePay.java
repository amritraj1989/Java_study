package com.mikealbert.data.entity;

import java.io.Serializable;
import javax.persistence.*;
import java.math.BigDecimal;


/**
 * The persistent class for the MUL_QUOTE_PAY database table.
 * 
 */
@Entity
@Table(name="MUL_QUOTE_PAY")
public class MulQuotePay implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="TMP_SEQ")
	private long tmpSeq;

	@Column(name="ALL_MIL")
	private String allMil;

	@Column(name="CAP_ACCESSORY")
	private String capAccessory;

	@Column(name="DRV_ACCESSORY")
	private String drvAccessory;

	@Column(name="HIGH_CC")
	private BigDecimal highCc;

	@Column(name="HIGH_CO2")
	private BigDecimal highCo2;

	@Column(name="HIGH_EFF_COST")
	private BigDecimal highEffCost;

	@Column(name="HIGH_FUEL_CONS")
	private BigDecimal highFuelCons;

	@Column(name="HIGH_P11D")
	private BigDecimal highP11d;

	@Column(name="HIGH_RETAIL")
	private BigDecimal highRetail;

	@Column(name="HIGHER_RENTAL")
	private BigDecimal higherRental;

	@Column(name="LOW_CC")
	private BigDecimal lowCc;

	@Column(name="LOW_CO2")
	private BigDecimal lowCo2;

	@Column(name="LOW_EFF_COST")
	private BigDecimal lowEffCost;

	@Column(name="LOW_FUEL_CONS")
	private BigDecimal lowFuelCons;

	@Column(name="LOW_P11D")
	private BigDecimal lowP11d;

	@Column(name="LOW_RETAIL")
	private BigDecimal lowRetail;

	@Column(name="LOWER_RENTAL")
	private BigDecimal lowerRental;

	@Column(name="MILEAGE_ID")
	private BigDecimal mileageId;

	@Column(name="MODEL_TYPE")
	private BigDecimal modelType;

	@Column(name="POH_ID")
	private BigDecimal pohId;

	@Column(name="QUOTE_STATUS")
	private String quoteStatus;

	@Column(name="UOM_CODE")
	private String uomCode;

	//bi-directional many-to-one association to PaymentHeader
    @ManyToOne
	@JoinColumn(name="PAYMENT_ID")
	private PaymentHeader paymentHeader;

	//bi-directional many-to-one association to Quotation
    @ManyToOne
	@JoinColumn(name="QUO_ID")
	private Quotation quotation;

    public MulQuotePay() {
    }

	public long getTmpSeq() {
		return this.tmpSeq;
	}

	public void setTmpSeq(long tmpSeq) {
		this.tmpSeq = tmpSeq;
	}

	public String getAllMil() {
		return this.allMil;
	}

	public void setAllMil(String allMil) {
		this.allMil = allMil;
	}

	public String getCapAccessory() {
		return this.capAccessory;
	}

	public void setCapAccessory(String capAccessory) {
		this.capAccessory = capAccessory;
	}

	public String getDrvAccessory() {
		return this.drvAccessory;
	}

	public void setDrvAccessory(String drvAccessory) {
		this.drvAccessory = drvAccessory;
	}

	public BigDecimal getHighCc() {
		return this.highCc;
	}

	public void setHighCc(BigDecimal highCc) {
		this.highCc = highCc;
	}

	public BigDecimal getHighCo2() {
		return this.highCo2;
	}

	public void setHighCo2(BigDecimal highCo2) {
		this.highCo2 = highCo2;
	}

	public BigDecimal getHighEffCost() {
		return this.highEffCost;
	}

	public void setHighEffCost(BigDecimal highEffCost) {
		this.highEffCost = highEffCost;
	}

	public BigDecimal getHighFuelCons() {
		return this.highFuelCons;
	}

	public void setHighFuelCons(BigDecimal highFuelCons) {
		this.highFuelCons = highFuelCons;
	}

	public BigDecimal getHighP11d() {
		return this.highP11d;
	}

	public void setHighP11d(BigDecimal highP11d) {
		this.highP11d = highP11d;
	}

	public BigDecimal getHighRetail() {
		return this.highRetail;
	}

	public void setHighRetail(BigDecimal highRetail) {
		this.highRetail = highRetail;
	}

	public BigDecimal getHigherRental() {
		return this.higherRental;
	}

	public void setHigherRental(BigDecimal higherRental) {
		this.higherRental = higherRental;
	}

	public BigDecimal getLowCc() {
		return this.lowCc;
	}

	public void setLowCc(BigDecimal lowCc) {
		this.lowCc = lowCc;
	}

	public BigDecimal getLowCo2() {
		return this.lowCo2;
	}

	public void setLowCo2(BigDecimal lowCo2) {
		this.lowCo2 = lowCo2;
	}

	public BigDecimal getLowEffCost() {
		return this.lowEffCost;
	}

	public void setLowEffCost(BigDecimal lowEffCost) {
		this.lowEffCost = lowEffCost;
	}

	public BigDecimal getLowFuelCons() {
		return this.lowFuelCons;
	}

	public void setLowFuelCons(BigDecimal lowFuelCons) {
		this.lowFuelCons = lowFuelCons;
	}

	public BigDecimal getLowP11d() {
		return this.lowP11d;
	}

	public void setLowP11d(BigDecimal lowP11d) {
		this.lowP11d = lowP11d;
	}

	public BigDecimal getLowRetail() {
		return this.lowRetail;
	}

	public void setLowRetail(BigDecimal lowRetail) {
		this.lowRetail = lowRetail;
	}

	public BigDecimal getLowerRental() {
		return this.lowerRental;
	}

	public void setLowerRental(BigDecimal lowerRental) {
		this.lowerRental = lowerRental;
	}

	public BigDecimal getMileageId() {
		return this.mileageId;
	}

	public void setMileageId(BigDecimal mileageId) {
		this.mileageId = mileageId;
	}

	public BigDecimal getModelType() {
		return this.modelType;
	}

	public void setModelType(BigDecimal modelType) {
		this.modelType = modelType;
	}

	public BigDecimal getPohId() {
		return this.pohId;
	}

	public void setPohId(BigDecimal pohId) {
		this.pohId = pohId;
	}

	public String getQuoteStatus() {
		return this.quoteStatus;
	}

	public void setQuoteStatus(String quoteStatus) {
		this.quoteStatus = quoteStatus;
	}

	public String getUomCode() {
		return this.uomCode;
	}

	public void setUomCode(String uomCode) {
		this.uomCode = uomCode;
	}

	public PaymentHeader getPaymentHeader() {
		return this.paymentHeader;
	}

	public void setPaymentHeader(PaymentHeader paymentHeader) {
		this.paymentHeader = paymentHeader;
	}
	
	public Quotation getQuotation() {
		return this.quotation;
	}

	public void setQuotation(Quotation quotation) {
		this.quotation = quotation;
	}
	
}