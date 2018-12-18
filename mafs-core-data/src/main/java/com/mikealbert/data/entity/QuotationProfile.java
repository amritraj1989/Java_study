package com.mikealbert.data.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


/**
 * The persistent class for the QUOTATION_PROFILES database table.
 *  @author Singh
 */
@Entity
@Table(name="QUOTATION_PROFILES")
public class QuotationProfile extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="QPR_ID", unique=true, nullable=false, precision=12)
	private long qprId;

	@Column(name="CAP_IND", nullable=false, length=1)
	private String capInd;

	@Column(name="DISCLOSURE_IND", length=1)
	private String disclosureInd;
	
	@Column(name="EARLY_TERMINATION_TYPE", length=10)
	private String earlyTerminationType;

    @Temporal( TemporalType.DATE)
	@Column(name="EFFECTIVE_FROM", nullable=false)
	private Date effectiveFrom;

    @Temporal( TemporalType.DATE)
	@Column(name="EFFECTIVE_TO")
	private Date effectiveTo;

	@Column(name="ITC_INTEREST_TYPE", nullable=false, length=10)
	private String itcInterestType;

	@Column(name="POOLED_MILEAGE_IND", nullable=false, length=1)
	private String pooledMileageInd;

	@Column(name="POOLED_UNDER_MILEAGE_IND", nullable=false, length=1)
	private String pooledUnderMileageInd;

	@Column(name="PRD_PRODUCT_CODE", nullable=false, length=10)
	private String prdProductCode;

	@Column(name="PRE_CONTRACT_FIXED_COST", nullable=false, length=1)
	private String preContractFixedCost;

	@Column(name="PRE_CONTRACT_FIXED_INTEREST", nullable=false, length=1)
	private String preContractFixedInterest;

	@Column(name="PROFILE_CODE", nullable=false, length=10)
	private String profileCode;

	@Column(name="PROFILE_STATUS", nullable=false, length=25)
	private String profileStatus;
	
	@Column(name="DESCRIPTION", nullable=false, length=80)
	private String description;	

	@Column(name="INSURANCE_DAYS", precision=3)
	private BigDecimal insuranceDays;

	@Column(name="INSURANCE_RISK_TYPE", length=10)
	private String insuranceRiskType;

	@Column(name="INTEREST_RATE", precision=30, scale=5)
	private BigDecimal interestRate;
	
	@Column(name="TABLE_CODE1", nullable=false, length=10)
	private String tableCode1;
	
	@Column(name="TABLE_CODE", nullable=false, length=10)
	private String tableCode;

	@Column(name="VARIABLE_RATE", nullable=false, length=1)
	private String variableRate;

	@Column(name="RF_REVIEW_FREQUENCY", length=10)
	private String reviewFrequency;
	
	
	@OneToMany(mappedBy="quotationProfile")
	 @javax.persistence.OrderBy("id.effectiveFrom ASC")
	private List<QuoteProfileProgram> quoteProfilePrograms;
	
    @JoinColumn(name = "EXCESS_MILE_ID", referencedColumnName = "EXCESS_MILE_ID")
    @ManyToOne(optional = true)
	private ExcessMileage excessMileage;
    
    @JoinColumn(name = "UOM_CODE", referencedColumnName = "UOM_CODE")
    @ManyToOne(fetch=FetchType.LAZY)
	private UomCode uomCode; 
    
    @OneToMany(mappedBy = "quoteProfileCustPK.quotationProfile", fetch = FetchType.LAZY)
    private List<QuoteProfileCust> quoteProfileCusts;    

    @Column(name="BUDG_ADJ_MAINT_VAL", precision=30, scale=5)
	private BigDecimal budgAdjMaintVal;
    @Column(name="BUDG_ADJ_MAINT_PERC", precision=30, scale=5)
   	private BigDecimal budgAdjMaintPerc;
    @Column(name="marg_disc_maint_val", precision=30, scale=5)
   	private BigDecimal budgDiscMaintVal;
    @Column(name="marg_disc_maint_perc", precision=30, scale=5)
   	private BigDecimal budgDiscMaintPerc;
	
    @Column(name= "PART_RECHARGE_CAP_ELE")
    private String partRechargeCapEle;	 
    
    @Column(name="REPORT_NAME")
	private String reportName;
    
    @Column(name="REPORT_NAME_3")
	private String reportName3;
    
	public QuotationProfile() {
    }

	public long getQprId() {
		return this.qprId;
	}

	public void setQprId(long qprId) {
		this.qprId = qprId;
	}
	
	public String getCapInd() {
		return this.capInd;
	}

	public void setCapInd(String capInd) {
		this.capInd = capInd;
	}


	public String getDisclosureInd() {
		return disclosureInd;
	}

	public void setDisclosureInd(String disclosureInd) {
		this.disclosureInd = disclosureInd;
	}

	public String getEarlyTerminationType() {
		return this.earlyTerminationType;
	}

	public void setEarlyTerminationType(String earlyTerminationType) {
		this.earlyTerminationType = earlyTerminationType;
	}

	public Date getEffectiveFrom() {
		return this.effectiveFrom;
	}

	public void setEffectiveFrom(Date effectiveFrom) {
		this.effectiveFrom = effectiveFrom;
	}

	public Date getEffectiveTo() {
		return this.effectiveTo;
	}

	public void setEffectiveTo(Date effectiveTo) {
		this.effectiveTo = effectiveTo;
	}

	public String getItcInterestType() {
		return this.itcInterestType;
	}

	public void setItcInterestType(String itcInterestType) {
		this.itcInterestType = itcInterestType;
	}

	public String getPooledMileageInd() {
		return this.pooledMileageInd;
	}

	public void setPooledMileageInd(String pooledMileageInd) {
		this.pooledMileageInd = pooledMileageInd;
	}

	public String getPooledUnderMileageInd() {
		return this.pooledUnderMileageInd;
	}

	public void setPooledUnderMileageInd(String pooledUnderMileageInd) {
		this.pooledUnderMileageInd = pooledUnderMileageInd;
	}

	public String getPrdProductCode() {
		return this.prdProductCode;
	}

	public void setPrdProductCode(String prdProductCode) {
		this.prdProductCode = prdProductCode;
	}

	public String getPreContractFixedCost() {
		return this.preContractFixedCost;
	}

	public void setPreContractFixedCost(String preContractFixedCost) {
		this.preContractFixedCost = preContractFixedCost;
	}

	public String getPreContractFixedInterest() {
		return this.preContractFixedInterest;
	}

	public void setPreContractFixedInterest(String preContractFixedInterest) {
		this.preContractFixedInterest = preContractFixedInterest;
	}

	public String getProfileCode() {
		return this.profileCode;
	}

	public void setProfileCode(String profileCode) {
		this.profileCode = profileCode;
	}

	public String getProfileStatus() {
		return this.profileStatus;
	}

	public void setProfileStatus(String profileStatus) {
		this.profileStatus = profileStatus;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<QuoteProfileProgram> getQuoteProfilePrograms() {
		return quoteProfilePrograms;
	}

	public void setQuoteProfilePrograms(List<QuoteProfileProgram> quoteProfilePrograms) {
		this.quoteProfilePrograms = quoteProfilePrograms;
	}

	public BigDecimal getInsuranceDays() {
		return insuranceDays;
	}
	public void setInsuranceDays(BigDecimal insuranceDays) {
		this.insuranceDays = insuranceDays;
	}
	public String getInsuranceRiskType() {
		return insuranceRiskType;
	}
	public void setInsuranceRiskType(String insuranceRiskType) {
		this.insuranceRiskType = insuranceRiskType;
	}
	public BigDecimal getInterestRate() {
		return interestRate;
	}
	public void setInterestRate(BigDecimal interestRate) {
		this.interestRate = interestRate;
	}
	
	public String getTableCode1() {
		return tableCode1;
	}

	public void setTableCode1(String tableCode1) {
		this.tableCode1 = tableCode1;
	}

	public ExcessMileage getExcessMileage() {
		return excessMileage;
	}

	public void setExcessMileage(ExcessMileage excessMileage) {
		this.excessMileage = excessMileage;
	}
	

	public UomCode getUomCode() {
		return uomCode;
	}

	public void setUomCode(UomCode uomCode) {
		this.uomCode = uomCode;
	}

	private List<QuoteProfileCust> getQuoteProfileCusts() {
		return quoteProfileCusts;
	}

	private void setQuoteProfileCusts(List<QuoteProfileCust> quoteProfileCusts) {
		this.quoteProfileCusts = quoteProfileCusts;
	}

	public String getTableCode() {
		return tableCode;
	}

	public void setTableCode(String tableCode) {
		this.tableCode = tableCode;
	}
	

	public BigDecimal getBudgAdjMaintVal() {
		return budgAdjMaintVal;
	}

	public void setBudgAdjMaintVal(BigDecimal budgAdjMaintVal) {
		this.budgAdjMaintVal = budgAdjMaintVal;
	}

	public BigDecimal getBudgAdjMaintPerc() {
		return budgAdjMaintPerc;
	}

	public void setBudgAdjMaintPerc(BigDecimal budgAdjMaintPerc) {
		this.budgAdjMaintPerc = budgAdjMaintPerc;
	}

	public BigDecimal getBudgDiscMaintVal() {
		return budgDiscMaintVal;
	}

	public void setBudgDiscMaintVal(BigDecimal budgDiscMaintVal) {
		this.budgDiscMaintVal = budgDiscMaintVal;
	}

	public BigDecimal getBudgDiscMaintPerc() {
		return budgDiscMaintPerc;
	}

	public void setBudgDiscMaintPerc(BigDecimal budgDiscMaintPerc) {
		this.budgDiscMaintPerc = budgDiscMaintPerc;
	}

	@Override
    public String toString() {
        return "com.mikealbert.vision.entity.QUOTATION_PROFILES[ qprId=" + qprId + " ]";
    }

	public String getVariableRate() {
		return variableRate;
	}

	public void setVariableRate(String variableRate) {
		this.variableRate = variableRate;
	}

	public String getReviewFrequency() {
		return reviewFrequency;
	}

	public void setReviewFrequency(String reviewFrequency) {
		this.reviewFrequency = reviewFrequency;
	}

	public String getPartRechargeCapEle() {
		return partRechargeCapEle;
	}

	public void setPartRechargeCapEle(String partRechargeCapEle) {
		this.partRechargeCapEle = partRechargeCapEle;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (qprId ^ (qprId >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof QuotationProfile))
			return false;
		QuotationProfile other = (QuotationProfile) obj;
		if (qprId != other.qprId)
			return false;
		return true;
	}

	public String getReportName() {
		return reportName;
	}

	public void setReportName(String reportName) {
		this.reportName = reportName;
	}

	public String getReportName3() {
		return reportName3;
	}

	public void setReportName3(String reportName3) {
		this.reportName3 = reportName3;
	}
	

}