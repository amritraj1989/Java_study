package com.mikealbert.data.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

/**
 * The persistent class for the QUOTATION_ELEMENTS database table.
 * 
 * @author Singh
 */
@Entity
@Table(name = "QUOTATION_ELEMENTS")
public class QuotationElement extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "QEL_SEQ")
    @SequenceGenerator(name = "QEL_SEQ", sequenceName = "QEL_SEQ", allocationSize = 1)
    @Column(name = "QEL_ID", unique = true, nullable = false, precision = 12)
    private Long qelId;

    @Column(name = "ACCEPTED_IND", nullable = false, length = 1)
    private String acceptedInd;

    @Column(precision = 11, scale = 2)
    private BigDecimal apr;

    @Column(name = "CAPITAL_COST", precision = 11, scale = 2)
    private BigDecimal capitalCost;

    @Column(name = "CUSTOMER_PROFIT", precision = 11, scale = 2)
    private BigDecimal customerProfit;

    @Column(precision = 11, scale = 2)
    private BigDecimal depreciation;

    @Column(name = "DEPRECIATION_PROFIT", precision = 11, scale = 2)
    private BigDecimal depreciationProfit;

    @Column(name = "ELEMENT_COST", precision = 11, scale = 2)
    private BigDecimal elementCost;

    @Column(name = "ELEMENT_ORDER", length = 10)
    private String elementOrder;

    @Column(name = "ELEMENT_PROFIT", precision = 11, scale = 2)
    private BigDecimal elementProfit;

    @Column(name = "FINAL_PAYMENT", precision = 11, scale = 2)
    private BigDecimal finalPayment;

    @Column(name = "FINAL_PAYMENT_SEL", nullable = false, length = 1)
    private String finalPaymentSel;

    @Column(name = "INCLUDE_YN", nullable = false, length = 1)
    private String includeYn;

    @Column(precision = 11, scale = 2)
    private BigDecimal interest;

    @Column(name = "INTEREST_PROFIT", precision = 11, scale = 2)
    private BigDecimal interestProfit;

    @Column(name = "LICENSE_FEE", precision = 11, scale = 2)
    private BigDecimal licenseFee;

    @Column(name = "LICENSE_FEE_PROFIT", precision = 11, scale = 2)
    private BigDecimal licenseFeeProfit;

    @Column(name = "MANDATORY_YN", nullable = false, length = 1)
    private String mandatoryYn;

    @Column(name = "MASS_AMEND_AMOUNT", precision = 30, scale = 5)
    private BigDecimal massAmendAmount;

    @Column(name = "MASS_AMEND_TYPE", length = 1)
    private String massAmendType;

    @Column(name = "MODEL_PROFIT", precision = 11, scale = 2)
    private BigDecimal modelProfit;

    @Column(name = "NEW_ACTUAL_FINANCE", precision = 11, scale = 2)
    private BigDecimal newActualFinance;

    @Column(name = "NEW_ACTUAL_SERVICE", precision = 11, scale = 2)
    private BigDecimal newActualService;

    @Column(name = "NO_RENTALS", precision = 11, scale = 2)
    private BigDecimal noRentals;

    @Column(name = "OVERHEAD_AMT", precision = 11, scale = 2)
    private BigDecimal overheadAmt;

    @Column(name = "PO_REQUIRED_YN", nullable = false, length = 1)
    private String poRequiredYn;

    @Column(name = "PROFIT_AMT", precision = 11, scale = 2)
    private BigDecimal profitAmt;
    
    @Column(name = "BILLING_OPTION")
    private String billingOptions;
    
    
    @Column(precision = 30, scale = 5)
    private BigDecimal rental;

    @Column(name = "RESIDUAL_VALUE", precision = 11, scale = 2)
    private BigDecimal residualValue;

    @Column(name = "TAX_CODE", length = 10)
    private String taxCode;

    @Column(name = "TAX_RATE", precision = 4, scale = 2)
    private BigDecimal taxRate;

    @Column(name = "TAX_VALUE", precision = 11, scale = 2)
    private BigDecimal taxValue;

    // bi-directional many-to-one association to LeaseElement
    @ManyToOne
    @JoinColumn(name = "LEL_LEL_ID", nullable = false)
    private LeaseElement leaseElement;

    // bi-directional many-to-one association to QuotationModel
    @ManyToOne
    @JoinColumn(name = "QMD_QMD_ID")
    private QuotationModel quotationModel;

    // bi-directional many-to-one association to QuotationModel
    @ManyToOne
    @JoinColumn(name = "QMA_QMA_ID")
    private QuotationModelAccessory quotationModelAccessory;

    // bi-directional many-to-one association to QuotationModel
    @ManyToOne
    @JoinColumn(name = "QDA_QDA_ID")
    private QuotationDealerAccessory quotationDealerAccessory;

    // bi-directional many-to-one association to QuotationElementStep
    @OneToMany(mappedBy = "quotationElement" ,fetch = FetchType.EAGER, cascade = CascadeType.ALL ,orphanRemoval = true )
    @Fetch(value = FetchMode.SUBSELECT)
    private List<QuotationElementStep> quotationElementSteps;

    @OneToMany(mappedBy = "quotationElement" ,fetch = FetchType.EAGER , cascade = CascadeType.ALL ,orphanRemoval = true)
    @Fetch(value = FetchMode.SUBSELECT)
    private List<QuoteElementParameter> quoteElementParameters;

  //bi-directional many-to-one association to QuotationSchedule
    @OneToMany(mappedBy="quotationElement", fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    private List<QuotationSchedule> quotationSchedules;
    
    @Transient
    private boolean calcRentalApplicable = true;

    
    public QuotationElement() {
    }

    public Long getQelId() {
	return this.qelId;
    }

    public void setQelId(Long qelId) {
	this.qelId = qelId;
    }

    public String getAcceptedInd() {
	return this.acceptedInd;
    }

    public void setAcceptedInd(String acceptedInd) {
	this.acceptedInd = acceptedInd;
    }

    public BigDecimal getApr() {
	return this.apr;
    }

    public void setApr(BigDecimal apr) {
	this.apr = apr;
    }

    public BigDecimal getCapitalCost() {
	return this.capitalCost;
    }

    public void setCapitalCost(BigDecimal capitalCost) {
	this.capitalCost = capitalCost;
    }

    public BigDecimal getCustomerProfit() {
	return this.customerProfit;
    }

    public void setCustomerProfit(BigDecimal customerProfit) {
	this.customerProfit = customerProfit;
    }

    public BigDecimal getDepreciation() {
	return this.depreciation;
    }

    public void setDepreciation(BigDecimal depreciation) {
	this.depreciation = depreciation;
    }

    public BigDecimal getDepreciationProfit() {
	return this.depreciationProfit;
    }

    public void setDepreciationProfit(BigDecimal depreciationProfit) {
	this.depreciationProfit = depreciationProfit;
    }

    public BigDecimal getElementCost() {
	return this.elementCost;
    }

    public void setElementCost(BigDecimal elementCost) {
	this.elementCost = elementCost;
    }

    public String getElementOrder() {
	return this.elementOrder;
    }

    public void setElementOrder(String elementOrder) {
	this.elementOrder = elementOrder;
    }

    public BigDecimal getElementProfit() {
	return this.elementProfit;
    }

    public void setElementProfit(BigDecimal elementProfit) {
	this.elementProfit = elementProfit;
    }

    public BigDecimal getFinalPayment() {
	return this.finalPayment;
    }

    public void setFinalPayment(BigDecimal finalPayment) {
	this.finalPayment = finalPayment;
    }

    public String getFinalPaymentSel() {
	return this.finalPaymentSel;
    }

    public void setFinalPaymentSel(String finalPaymentSel) {
	this.finalPaymentSel = finalPaymentSel;
    }

    public String getIncludeYn() {
	return this.includeYn;
    }

    public void setIncludeYn(String includeYn) {
	this.includeYn = includeYn;
    }

    public BigDecimal getInterest() {
	return this.interest;
    }

    public void setInterest(BigDecimal interest) {
	this.interest = interest;
    }

    public BigDecimal getInterestProfit() {
	return this.interestProfit;
    }

    public void setInterestProfit(BigDecimal interestProfit) {
	this.interestProfit = interestProfit;
    }

    public BigDecimal getLicenseFee() {
	return this.licenseFee;
    }

    public void setLicenseFee(BigDecimal licenseFee) {
	this.licenseFee = licenseFee;
    }

    public BigDecimal getLicenseFeeProfit() {
	return this.licenseFeeProfit;
    }

    public void setLicenseFeeProfit(BigDecimal licenseFeeProfit) {
	this.licenseFeeProfit = licenseFeeProfit;
    }

    public String getMandatoryYn() {
	return this.mandatoryYn;
    }

    public void setMandatoryYn(String mandatoryYn) {
	this.mandatoryYn = mandatoryYn;
    }

    public BigDecimal getMassAmendAmount() {
	return this.massAmendAmount;
    }

    public void setMassAmendAmount(BigDecimal massAmendAmount) {
	this.massAmendAmount = massAmendAmount;
    }

    public String getMassAmendType() {
	return this.massAmendType;
    }

    public void setMassAmendType(String massAmendType) {
	this.massAmendType = massAmendType;
    }

    public BigDecimal getModelProfit() {
	return this.modelProfit;
    }

    public void setModelProfit(BigDecimal modelProfit) {
	this.modelProfit = modelProfit;
    }

    public BigDecimal getNewActualFinance() {
	return this.newActualFinance;
    }

    public void setNewActualFinance(BigDecimal newActualFinance) {
	this.newActualFinance = newActualFinance;
    }

    public BigDecimal getNewActualService() {
	return this.newActualService;
    }

    public void setNewActualService(BigDecimal newActualService) {
	this.newActualService = newActualService;
    }

    public BigDecimal getNoRentals() {
	return this.noRentals;
    }

    public void setNoRentals(BigDecimal noRentals) {
	this.noRentals = noRentals;
    }

    public BigDecimal getOverheadAmt() {
	return this.overheadAmt;
    }

    public void setOverheadAmt(BigDecimal overheadAmt) {
	this.overheadAmt = overheadAmt;
    }

    public String getPoRequiredYn() {
	return this.poRequiredYn;
    }

    public void setPoRequiredYn(String poRequiredYn) {
	this.poRequiredYn = poRequiredYn;
    }

    public BigDecimal getProfitAmt() {
	return this.profitAmt;
    }

    public void setProfitAmt(BigDecimal profitAmt) {
	this.profitAmt = profitAmt;
    }

    public BigDecimal getRental() {
	return this.rental;
    }

    public void setRental(BigDecimal rental) {
	this.rental = rental;
    }

    public BigDecimal getResidualValue() {
	return this.residualValue;
    }

    public void setResidualValue(BigDecimal residualValue) {
	this.residualValue = residualValue;
    }

    public String getTaxCode() {
	return this.taxCode;
    }

    public void setTaxCode(String taxCode) {
	this.taxCode = taxCode;
    }

    public BigDecimal getTaxRate() {
	return this.taxRate;
    }

    public void setTaxRate(BigDecimal taxRate) {
	this.taxRate = taxRate;
    }

    public BigDecimal getTaxValue() {
	return this.taxValue;
    }

    public void setTaxValue(BigDecimal taxValue) {
	this.taxValue = taxValue;
    }

    public LeaseElement getLeaseElement() {
	return this.leaseElement;
    }

    public void setLeaseElement(LeaseElement leaseElement) {
	this.leaseElement = leaseElement;
    }

    public QuotationModel getQuotationModel() {
	return this.quotationModel;
    }

    public void setQuotationModel(QuotationModel quotationModel) {
	this.quotationModel = quotationModel;
    }

    public QuotationModelAccessory getQuotationModelAccessory() {
	return quotationModelAccessory;
    }

    public void setQuotationModelAccessory(QuotationModelAccessory quotationModelAccessory) {
	this.quotationModelAccessory = quotationModelAccessory;
    }

    public QuotationDealerAccessory getQuotationDealerAccessory() {
	return quotationDealerAccessory;
    }

    public void setQuotationDealerAccessory(QuotationDealerAccessory quotationDealerAccessory) {
	this.quotationDealerAccessory = quotationDealerAccessory;
    }

    public List<QuotationElementStep> getQuotationElementSteps() {
	return quotationElementSteps;
    }

    public void setQuotationElementSteps(List<QuotationElementStep> quotationElementSteps) {
	this.quotationElementSteps = quotationElementSteps;
    }

    public List<QuoteElementParameter> getQuoteElementParameters() {
	return quoteElementParameters;
    }

    public void setQuoteElementParameters(List<QuoteElementParameter> quoteElementParameters) {
	this.quoteElementParameters = quoteElementParameters;
    } 

    public List<QuotationSchedule> getQuotationSchedules() {
	return quotationSchedules;
    }

    public void setQuotationSchedules(List<QuotationSchedule> quotationSchedules) {
	this.quotationSchedules = quotationSchedules;
    }

    public boolean isCalcRentalApplicable() {
        return calcRentalApplicable;
    }

    public void setCalcRentalApplicable(boolean calcRentalApplicable) {
        this.calcRentalApplicable = calcRentalApplicable;
    }

	public String getBillingOptions() {
		return billingOptions;
	} 
		
	public void setBillingOptions(String billingOptions) {
		this.billingOptions = billingOptions;
	}
    
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (qelId != null ? qelId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof QuotationElement)) {
            return false;
        }
        QuotationElement other = (QuotationElement) object;
        if ((this.qelId == null && other.qelId != null) || (this.qelId != null && !this.qelId.equals(other.qelId))) {
            return false;
        }
        return true;
    }   
}