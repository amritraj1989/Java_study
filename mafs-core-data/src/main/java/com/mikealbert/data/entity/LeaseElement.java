package com.mikealbert.data.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * The persistent class for the LEASE_ELEMENTS database table.
 * @author Singh
 */
@Entity
@Table(name="LEASE_ELEMENTS")
public class LeaseElement extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="LEL_ID", unique=true, nullable=false, precision=12)
	private long lelId;

	@Column(name="ACTUAL_INCOME_TABLE", length=10)
	private String actualIncomeTable;

	@Column(name="BILL_LAST_MONTH_INFORMAL", length=1)
	private String billLastMonthInformal;

	@Column(name="BILL_MONTHLY", length=1)
	private String billMonthly;

	@Column(name="BILL_PAST_FORMAL", length=1)
	private String billPastFormal;

	@Column(name="CATEGORY_CODE", nullable=false, length=10)
	private String categoryCode;

	@Column(name="CONV_REDUNDANT_ELE_YN", nullable=false, length=1)
	private String convRedundantEleYn;

	@Column(length=80)
	private String description;

	@Column(name="DISPLAY_ON_QUOTE", nullable=false, length=1)
	private String displayOnQuote;

	@Column(name="EFF_CHANGE_FROM_ANNIV", nullable=false, length=1)
	private String effChangeFromAnniv;

	@Column(name="ELEMENT_NAME", nullable=false, length=10)
	private String elementName;

	@Column(name="ELEMENT_TYPE", nullable=false, length=10)
	private String elementType;

	@Column(name="FINAL_PAYMENT_IND", nullable=false, length=1)
	private String finalPaymentInd;

	@Column(name="FIXED_VALUE", precision=11, scale=2)
	private BigDecimal fixedValue;

	@Column(name="FLAT_RATE_YN", nullable=false, length=1)
	private String flatRateYn;

	@Column(length=100)
	private String formulae;

	@Column(name="ICA_C_ID", precision=12)
	private BigDecimal icaCId;

	@Column(length=10)
	private String internal;

	@Column(name="LINKED_TO", length=10)
	private String linkedTo;

	@Column(name="MASS_MULTI_QUOTE", nullable=false, length=1)
	private String massMultiQuote;

	@Column(length=2000)
	private String note;

	@Column(name="ONCE_OFF_CHARGE_IND", nullable=false, length=1)
	private String onceOffChargeInd;

	@Column(name="OVR_CDB_CODE", length=10)
	private String ovrCdbCode;

	@Column(name="OVR_GL_C_ID", precision=12)
	private BigDecimal ovrGlCId;

	@Column(name="OVR_GL_CODE", length=25)
	private String ovrGlCode;

	@Column(name="PAYMENT_INCREASE_IND", nullable=false, length=1)
	private String paymentIncreaseInd;

	@Column(name="PO_REQUIRED_YN", nullable=false, length=1)
	private String poRequiredYn;

	@Column(name="PRO_CDB_CODE", length=10)
	private String proCdbCode;

	@Column(name="PRO_GL_C_ID", precision=12)
	private BigDecimal proGlCId;

	@Column(name="PRO_GL_CODE", length=25)
	private String proGlCode;

	@Column(name="RECLAIM_ALL_AT_ET", length=1)
	private String reclaimAllAtEt;

	@Column(name="TAX_TREATMENT", nullable=false, length=1)
	private String taxTreatment;

	@Column(length=60)
	private String terms;

	@Column(name="VAT_RECOVERY", nullable=false, length=1)
	private String vatRecovery;

	@Column(name="PROCESSOR_NAME", nullable=false)
	private String processorName;
	
	@Column(name="PRINT_VQ", nullable=false, length=1)
	private String printVqInd;

	@Column(name="IN_RATE_TREATMENT", nullable=false, length=1)
	private String inRateTreatmentYn;
	
    public LeaseElement() {
    }

	public long getLelId() {
		return this.lelId;
	}

	public void setLelId(long lelId) {
		this.lelId = lelId;
	}

	public String getActualIncomeTable() {
		return this.actualIncomeTable;
	}

	public void setActualIncomeTable(String actualIncomeTable) {
		this.actualIncomeTable = actualIncomeTable;
	}

	public String getBillLastMonthInformal() {
		return this.billLastMonthInformal;
	}

	public void setBillLastMonthInformal(String billLastMonthInformal) {
		this.billLastMonthInformal = billLastMonthInformal;
	}

	public String getBillMonthly() {
		return this.billMonthly;
	}

	public void setBillMonthly(String billMonthly) {
		this.billMonthly = billMonthly;
	}

	public String getBillPastFormal() {
		return this.billPastFormal;
	}

	public void setBillPastFormal(String billPastFormal) {
		this.billPastFormal = billPastFormal;
	}

	public String getCategoryCode() {
		return this.categoryCode;
	}

	public void setCategoryCode(String categoryCode) {
		this.categoryCode = categoryCode;
	}

	public String getConvRedundantEleYn() {
		return this.convRedundantEleYn;
	}

	public void setConvRedundantEleYn(String convRedundantEleYn) {
		this.convRedundantEleYn = convRedundantEleYn;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDisplayOnQuote() {
		return this.displayOnQuote;
	}

	public void setDisplayOnQuote(String displayOnQuote) {
		this.displayOnQuote = displayOnQuote;
	}

	public String getEffChangeFromAnniv() {
		return this.effChangeFromAnniv;
	}

	public void setEffChangeFromAnniv(String effChangeFromAnniv) {
		this.effChangeFromAnniv = effChangeFromAnniv;
	}

	public String getElementName() {
		return this.elementName;
	}

	public void setElementName(String elementName) {
		this.elementName = elementName;
	}

	public String getElementType() {
		return this.elementType;
	}

	public void setElementType(String elementType) {
		this.elementType = elementType;
	}

	public String getFinalPaymentInd() {
		return this.finalPaymentInd;
	}

	public void setFinalPaymentInd(String finalPaymentInd) {
		this.finalPaymentInd = finalPaymentInd;
	}

	public BigDecimal getFixedValue() {
		return this.fixedValue;
	}

	public void setFixedValue(BigDecimal fixedValue) {
		this.fixedValue = fixedValue;
	}

	public String getFlatRateYn() {
		return this.flatRateYn;
	}

	public void setFlatRateYn(String flatRateYn) {
		this.flatRateYn = flatRateYn;
	}

	public String getFormulae() {
		return this.formulae;
	}

	public void setFormulae(String formulae) {
		this.formulae = formulae;
	}

	public BigDecimal getIcaCId() {
		return this.icaCId;
	}

	public void setIcaCId(BigDecimal icaCId) {
		this.icaCId = icaCId;
	}

	public String getInternal() {
		return this.internal;
	}

	public void setInternal(String internal) {
		this.internal = internal;
	}

	public String getLinkedTo() {
		return this.linkedTo;
	}

	public void setLinkedTo(String linkedTo) {
		this.linkedTo = linkedTo;
	}

	public String getMassMultiQuote() {
		return this.massMultiQuote;
	}

	public void setMassMultiQuote(String massMultiQuote) {
		this.massMultiQuote = massMultiQuote;
	}

	public String getNote() {
		return this.note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public String getOnceOffChargeInd() {
		return this.onceOffChargeInd;
	}

	public void setOnceOffChargeInd(String onceOffChargeInd) {
		this.onceOffChargeInd = onceOffChargeInd;
	}

	public String getOvrCdbCode() {
		return this.ovrCdbCode;
	}

	public void setOvrCdbCode(String ovrCdbCode) {
		this.ovrCdbCode = ovrCdbCode;
	}

	public BigDecimal getOvrGlCId() {
		return this.ovrGlCId;
	}

	public void setOvrGlCId(BigDecimal ovrGlCId) {
		this.ovrGlCId = ovrGlCId;
	}

	public String getOvrGlCode() {
		return this.ovrGlCode;
	}

	public void setOvrGlCode(String ovrGlCode) {
		this.ovrGlCode = ovrGlCode;
	}

	public String getPaymentIncreaseInd() {
		return this.paymentIncreaseInd;
	}

	public void setPaymentIncreaseInd(String paymentIncreaseInd) {
		this.paymentIncreaseInd = paymentIncreaseInd;
	}

	public String getPoRequiredYn() {
		return this.poRequiredYn;
	}

	public void setPoRequiredYn(String poRequiredYn) {
		this.poRequiredYn = poRequiredYn;
	}

	public String getProCdbCode() {
		return this.proCdbCode;
	}

	public void setProCdbCode(String proCdbCode) {
		this.proCdbCode = proCdbCode;
	}

	public BigDecimal getProGlCId() {
		return this.proGlCId;
	}

	public void setProGlCId(BigDecimal proGlCId) {
		this.proGlCId = proGlCId;
	}

	public String getProGlCode() {
		return this.proGlCode;
	}

	public void setProGlCode(String proGlCode) {
		this.proGlCode = proGlCode;
	}

	public String getReclaimAllAtEt() {
		return this.reclaimAllAtEt;
	}

	public void setReclaimAllAtEt(String reclaimAllAtEt) {
		this.reclaimAllAtEt = reclaimAllAtEt;
	}

	public String getTaxTreatment() {
		return this.taxTreatment;
	}

	public void setTaxTreatment(String taxTreatment) {
		this.taxTreatment = taxTreatment;
	}

	public String getTerms() {
		return this.terms;
	}

	public void setTerms(String terms) {
		this.terms = terms;
	}

	public String getVatRecovery() {
		return this.vatRecovery;
	}

	public void setVatRecovery(String vatRecovery) {
		this.vatRecovery = vatRecovery;
	}

	public String getProcessorName() {
		return processorName;
	}

	public void setProcessorName(String processorName) {
		this.processorName = processorName;
	}
	   
	@Override
    public String toString() {
        return "com.mikealbert.entity.LeaseElement[ lelId=" + lelId + " ]";
    }

	public String getPrintVqInd() {
		return printVqInd;
	}

	public void setPrintVqInd(String printVqInd) {
		this.printVqInd = printVqInd;
	}
	
	public String getInRateTreatmentYn() {
		return inRateTreatmentYn;
	}

	public void setInRateTreatmentYn(String inRateTreatmentYn) {
		this.inRateTreatmentYn = inRateTreatmentYn;
	}	
}