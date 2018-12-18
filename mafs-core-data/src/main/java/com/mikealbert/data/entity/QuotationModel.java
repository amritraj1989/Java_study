package com.mikealbert.data.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.mikealbert.data.beanvalidation.MADate;

/**
 * Mapped to QUOTATION_MODELS table.
 * 
 * Note: Not all columns and associations have been mapped.
 * 
 * @author sibley
 */
@Entity
@Table(name = "QUOTATION_MODELS")
public class QuotationModel extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "QMD_SEQ")
    @SequenceGenerator(name = "QMD_SEQ", sequenceName = "QMD_SEQ", allocationSize = 1)
    @NotNull
    @Column(name = "QMD_ID")
    private Long qmdId;

    @Column(name = "UNIT_NO")
    private String unitNo;

    @Column(name = "QUOTE_STATUS")
    private int quoteStatus;

    @Column(name = "REVISION_NO")
    private Long revisionNo;

    @Column(name = "ORIG_QMD_ID")
    private Long originalQmdId;

    @NotNull
    @Column(name = "BASIC_PRICE", precision = 11, scale = 2)
    private BigDecimal basePrice;
    
    @NotNull
    @Column(name = "TOTAL_PRICE", precision = 11, scale = 2)
    private BigDecimal totalPrice;
    

    @Column(name = "CAPITAL_CONTRIBUTION")
    private BigDecimal capitalContribution;

    @Column(name = "QUOTE_NO")
    private Long quoteNo;

    @Column(name = "PRE_CONTRACT_FIXED_COST")
    private String preContractFixedCost;

    @Column(name = "PRE_CONTRACT_FIXED_INTEREST")
    private String preContractFixedInterest;

    @Column(name = "CONTRACT_PERIOD")
    private Long contractPeriod;

    @Column(name = "CONTRACT_DISTANCE")
    private Long contractDistance;

    @Column(name = "RESIDUAL_VALUE")
    private BigDecimal residualValue;

    @Column(name = "INTEREST_RATE")
    private BigDecimal interestRate;

    @Column(name = "FINANCE_ELEMENT")
    private BigDecimal FinanceElementRental;

    @Column(name = "SERVICE_ELEMENT")
    private BigDecimal serviceElementRental;

    @Column(name = "QUOTE_CAPITAL", precision = 11, scale = 2)
    private BigDecimal quoteCapital;

    @MADate(label = "revision Date")
    @Temporal(TemporalType.DATE)
    @Column(name = "REVISION_DATE")
    private Date revisionDate;
    
    @MADate(label = "required Date")
    @Temporal(TemporalType.DATE)
    @Column(name = "REQUIRED_DATE")
    private Date requiredDate;
    
	@Column(name = "MANUAL_PROFIT_UPDATE", length = 1)
    private String manualProfitUpdate;

    @Column(name = "LCD_LCD_ID", precision = 12)
    private BigDecimal lcdLcdId;

    @Column(name = "PAYMENT_ID", nullable = false, precision = 12)
    private Long paymentId;

    @Column(name = "PAYMENTS_PER_YEAR", precision = 3)
    private BigDecimal paymentsPerYear;

    @Column(name = "PAYMENT_PROFILE", length = 20)
    private String paymentProfile;

    @Column(name = "ADVANCE_PAYMENTS", precision = 3)
    private BigDecimal advancePayments;

    @Column(precision = 11, scale = 2)
    private BigDecimal apr;

    @Column(name = "USED_VEHICLE", nullable = false, length = 1)
    private String usedVehicle;

    @Column(name = "REPLACEMENT_FOR_FMS_ID")
    private Long replacementFmsId;

    @Column(name = "PROJECTED_MONTHS")
    private Long projectedMonths;

    
    @Temporal(TemporalType.DATE)
    @Column(name = "PRINTED_DATE")
    private Date printedDate; 

    @Temporal(TemporalType.DATE)
    @Column(name = "ACCEPTANCE_DATE")
    private Date acceptanceDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "DATE_RECEIVED")
    private Date dateReceived;
    
    @Column(name = "PRINTED_IND", length = 1)
    private String printedInd;
    
    @Column(name = "ORDER_TYPE", length = 1)
    private String orderType;

    @Column(name = "PLATE_TYPE_CODE")
    private String	plateTypeCode;

    @Column(name = "TMP_VIN_NO")
    private String	tmpVinNo;
    
    @Column(name = "VEHICLE_OFF_CONTRACT", length = 1)
    private String vehicleOffContract;

    @Temporal(TemporalType.DATE)
    @Column(name = "AMENDMENT_EFF_DATE")
    private Date amendmentEffectiveDate;
    
    @Column(name = "LAST_AMENDED_USER")
    private String	lastAmendedUser;
    
    @Column(name = "DEPRECIATION_FACTOR", precision = 30, scale = 5)
	 private BigDecimal depreciationFactor;

    @Column(name = "REQUEST_FOR_ACCEPTANCE_YN")
    private String	requestForAcceptanceYn;

    @Column(name = "REQUEST_FOR_ACCEPTANCE_BY")
    private String	requestForAcceptanceBy;

    @Column(name = "REQUEST_FOR_ACCEPTANCE_TYPE")
    private String	requestForAcceptanceType;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "REQUEST_FOR_ACCEPTANCE_DATE")
    private Date requestForAcceptanceDate;
    
    @Column(name = "REJECT_REASON_FROM_QUEUE")
    private String	rejectReasonFromQueue;

    @Column(name = "CLIENT_REQUEST_TYPE")
    private String	clientRequestType;
    
    @Column(name = "FMS_FMS_ID",nullable=true)
    private Long fmsId; 
    
    @Temporal(TemporalType.DATE)
    @Column(name = "VLO_PRINTED_DATE")
    private Date vloPrintedDate;     
    
    @JoinColumns({
        @JoinColumn(name = "SUP_C_ID", referencedColumnName = "C_ID"),
        @JoinColumn(name = "SUP_ACCOUNT_TYPE", referencedColumnName = "ACCOUNT_TYPE"),
        @JoinColumn(name = "SUP_ACCOUNT_CODE", referencedColumnName = "ACCOUNT_CODE")})
    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    private ExternalAccount requestedDeliveryDealer;
    
	@JoinColumn(name = "CLN_CLN_ID", referencedColumnName = "CLN_ID")
    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    private ContractLine contractLine;
    
    @OneToMany(mappedBy = "quotationModel", fetch = FetchType.LAZY)
    private List<ContractLine> contractLineList;

    @OneToMany(mappedBy = "quotationModel", fetch = FetchType.LAZY, cascade = CascadeType.ALL ,orphanRemoval = true)
    private List<QuotationCapitalElement> quotationCapitalElements;

    @JoinColumn(name = "QUO_QUO_ID", referencedColumnName = "QUO_ID")
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    private Quotation quotation;

    @OneToMany(mappedBy = "quotationModel", fetch = FetchType.LAZY)
    private List<QuotationCapitalElementBackup> quotationCapitalElementsBackup;

    @OneToMany(mappedBy = "quotationModel", fetch = FetchType.EAGER, cascade = CascadeType.ALL ,orphanRemoval = true)
    @javax.persistence.OrderBy("qelId ASC")
    private List<QuotationElement> quotationElements;

    @OneToMany(mappedBy = "quotationModel", cascade = CascadeType.ALL ,orphanRemoval = true)
    private List<QuotationModelAccessory> quotationModelAccessories;

    @OneToMany(mappedBy = "quotationModel", cascade = CascadeType.ALL ,orphanRemoval = true)
    private List<QuotationDealerAccessory> quotationDealerAccessories;
    
    @OneToMany(mappedBy = "quotationModel", cascade = CascadeType.ALL ,orphanRemoval = true)
    private List<QuotationStandardAccessory> quotationStandardAccessories;

    @OneToMany(mappedBy = "quotationModel", cascade = CascadeType.ALL ,orphanRemoval = true)
    private List<QuoteModelOptionPack> quoteModelOptionPacks;

    @OneToMany(mappedBy = "quotationModel", cascade = CascadeType.ALL ,orphanRemoval = true)
    private List<UpfitterProgress> unitUpfits;

    @JoinColumn(name = "MDL_MDL_ID", referencedColumnName = "MDL_ID")
    @OneToOne(fetch = FetchType.EAGER)
    private Model model;

    // bi-directional many-to-one association to ResidualTable
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RTB_RTB_ID")
    private ResidualTable residualTable;

    // bi-directional many-to-one association to MaintenanceTable
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MTB_MTB_ID")
    private MaintenanceTable maintenanceTable;

    // bi-directional many-to-one association to QuotationProfitability
    @OneToMany(mappedBy = "quotationModel", fetch = FetchType.LAZY, cascade = CascadeType.ALL ,orphanRemoval = true)
    private List<QuotationProfitability> quotationProfitabilities;

    
    @OneToMany(mappedBy = "quotationModel" , fetch = FetchType.LAZY, cascade = CascadeType.ALL ,orphanRemoval = true)
    private List<QuotationModelFinances> quotationModelFinances;

    
    
 // bi-directional many-to-one association to QuotationStepStructure
    @OneToMany(mappedBy = "quotationModel", fetch = FetchType.LAZY, cascade = CascadeType.ALL ,orphanRemoval = true)
    @Fetch(value = FetchMode.SUBSELECT)
    private List<QuotationStepStructure> QuotationStepStructure;

    @Column(name = "revision_qmd_id")
    private Long revisionQmdId;

    @Column(name = "QUOTE_MAINT_COST")
    private BigDecimal quoteMaintCost;
    
    @Column(name = "RECALC_NEEDED ")
    private String reCalcNeeded;
    
    @Column(name = "STEPPED_CALC")
    private String	steppedCalc;
    
    @Column(name = "CONTRACT_CHANGE_EVENT_PERIOD")
    private	Long	contractChangeEventPeriod;
    
    @Column(name = "QUOTE_START_ODO")
    private Long quoteStartOdo;
    
    @JoinColumn(name = "COLOUR_CODE", referencedColumnName = "COLOUR_CODE")
    @OneToOne(fetch = FetchType.LAZY)
    private ColourCodes colourCode;
    
    @JoinColumn(name = "TRC_TRC_ID", referencedColumnName = "TRC_ID")
    @OneToOne(fetch = FetchType.LAZY)
    private TrimCodes trimCode;
    
    @OneToMany(mappedBy = "quotationModel", fetch = FetchType.LAZY)
    private List<QuotationNotes> quotationNotes;
    
    @OrderBy("sequenceNo ASC")
	@OneToMany(mappedBy = "quotationModel", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UpfitterProgress> upfitterProgress;	 
    
    @JoinColumn(name = "EAAF_EAAF_ID", referencedColumnName = "EAAF_ID")
    @OneToOne(fetch = FetchType.EAGER)
    private ExtAccAffiliate extAcctAffiliate;
    
    @JoinColumn(name = "QUOTE_STATUS", referencedColumnName = "QUOTATION_STATUS", insertable= false, updatable = false)
    @OneToOne(fetch = FetchType.EAGER)
    private QuotationStatusCodes quotationStatusCodes;
    
	@OneToMany(mappedBy = "quotationModel", fetch = FetchType.LAZY, cascade = CascadeType.ALL ,orphanRemoval = true)
    private List<QuoteModelPropertyValue> quoteModelPropertyValues;	
	
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "REVISION_EXP_DATE")
    private Date revisionExpDate;
    
    @Column(name = "REJECT_REASON")
    private String	rejectReason;

    
    @Transient
    private BigDecimal	calculatedCapCost;
    
    @Transient
    private BigDecimal	suppliedResidual;

    @Transient
    private BigDecimal calculatedMontlyRental;
    
    @Transient
    private boolean calcRentalApplicable = true;

    
    public QuotationModel() {
    }

    public Long getQmdId() {
	return qmdId;
    }

    public String getUnitNo() {
	return unitNo;
    }

    public void setUnitNo(String unitNo) {
	this.unitNo = unitNo;
    }

    public int getQuoteStatus() {
	return quoteStatus;
    }

    public void setQuoteStatus(int quoteStatus) {
	this.quoteStatus = quoteStatus;
    }

    public Long getRevisionNo() {
	return revisionNo;
    }

    public void setRevisionNo(Long revisionNo) {
	this.revisionNo = revisionNo;
    }

    public Long getOriginalQmdId() {
	return originalQmdId;
    }

    public void setOriginalQmdId(Long originalQmdId) {
	this.originalQmdId = originalQmdId;
    }

    public BigDecimal getBasePrice() {
	return basePrice;
    }

    public void setBasePrice(BigDecimal basePrice) {
	this.basePrice = basePrice;
    }

    public BigDecimal getTotalPrice() {
	return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
	this.totalPrice = totalPrice;
    }

    public BigDecimal getCapitalContribution() {
	return capitalContribution;
    }

    public void setCapitalContribution(BigDecimal capitalContribution) {
	this.capitalContribution = capitalContribution;
    }

    public Long getQuoteNo() {
	return quoteNo;
    }

    public void setQuoteNo(Long quoteNo) {
	this.quoteNo = quoteNo;
    }

    public void setQmdId(Long qmdId) {
	this.qmdId = qmdId;
    }

    public String getPreContractFixedCost() {
	return preContractFixedCost;
    }

    public void setPreContractFixedCost(String preContractFixedCost) {
	this.preContractFixedCost = preContractFixedCost;
    }

    public ContractLine getContractLine() {
	return contractLine;
    }

    public void setContractLine(ContractLine contractLine) {
	this.contractLine = contractLine;
    }

    public List<ContractLine> getContractLineList() {
	return contractLineList;
    }

    public void setContractLineList(List<ContractLine> contractLineList) {
	this.contractLineList = contractLineList;
    }

    public List<QuotationCapitalElement> getQuotationCapitalElements() {
	return quotationCapitalElements;
    }

    public void setQuotationCapitalElements(List<QuotationCapitalElement> quotationCapitalElements) {
	this.quotationCapitalElements = quotationCapitalElements;
    }

    public Quotation getQuotation() {
	return quotation;
    }

    public void setQuotation(Quotation quotation) {
	this.quotation = quotation;
    }

    public List<QuotationCapitalElementBackup> getQuotationCapitalElementsBackup() {
	return quotationCapitalElementsBackup;
    }

    public void setQuotationCapitalElementsBackup(List<QuotationCapitalElementBackup> quotationCapitalElementsBackup) {
	this.quotationCapitalElementsBackup = quotationCapitalElementsBackup;
    }

    public List<QuotationModelAccessory> getQuotationModelAccessories() {
	return quotationModelAccessories;
    }

    public void setQuotationModelAccessories(List<QuotationModelAccessory> quotationModelAccessories) {
	this.quotationModelAccessories = quotationModelAccessories;
    }

    public List<QuotationDealerAccessory> getQuotationDealerAccessories() {
	return quotationDealerAccessories;
    }

    public void setQuotationDealerAccessories(List<QuotationDealerAccessory> quotationDealerAccessories) {
	this.quotationDealerAccessories = quotationDealerAccessories;
    }

    public List<QuotationStandardAccessory> getQuotationStandardAccessories() {
		return quotationStandardAccessories;
	}

	public void setQuotationStandardAccessories(List<QuotationStandardAccessory> quotationStandardAccessories) {
		this.quotationStandardAccessories = quotationStandardAccessories;
	}

	public List<QuoteModelOptionPack> getQuoteModelOptionPacks() {
		return quoteModelOptionPacks;
	}

	public void setQuoteModelOptionPacks(List<QuoteModelOptionPack> quoteModelOptionPacks) {
		this.quoteModelOptionPacks = quoteModelOptionPacks;
	}

	public List<QuotationElement> getQuotationElements() {
	return quotationElements;
    }

    public void setQuotationElements(List<QuotationElement> quotationElements) {
	this.quotationElements = quotationElements;
    }

    public Long getContractPeriod() {
	return contractPeriod;
    }

    public void setContractPeriod(Long contractPeriod) {
	this.contractPeriod = contractPeriod;
    }

    public Long getContractDistance() {
	return contractDistance;
    }

    public void setContractDistance(Long contractDistance) {
	this.contractDistance = contractDistance;
    }

    public BigDecimal getResidualValue() {
	return residualValue;
    }

    public void setResidualValue(BigDecimal residualValue) {
	this.residualValue = residualValue;
    }

    public BigDecimal getInterestRate() {
	return interestRate;
    }

    public void setInterestRate(BigDecimal interestRate) {
	this.interestRate = interestRate;
    }

    public BigDecimal getFinanceElementRental() {
	return FinanceElementRental;
    }

    public void setFinanceElementRental(BigDecimal financeElementRental) {
	FinanceElementRental = financeElementRental;
    }

    public BigDecimal getServiceElementRental() {
	return serviceElementRental;
    }

    public void setServiceElementRental(BigDecimal serviceElementRental) {
	this.serviceElementRental = serviceElementRental;
    }

    public String getPreContractFixedInterest() {
	return preContractFixedInterest;
    }

    public void setPreContractFixedInterest(String preContractFixedInterest) {
	this.preContractFixedInterest = preContractFixedInterest;
    }

    public Model getModel() {
	return model;
    }

    public void setModel(Model model) {
	this.model = model;
    }

    public BigDecimal getQuoteCapital() {
	return quoteCapital;
    }

    public void setQuoteCapital(BigDecimal quoteCapital) {
	this.quoteCapital = quoteCapital;
    }

    public Date getRevisionDate() {
	return revisionDate;
    }

    public void setRevisionDate(Date revisionDate) {
	this.revisionDate = revisionDate;
    }

    public String getManualProfitUpdate() {
	return manualProfitUpdate;
    }

    public void setManualProfitUpdate(String manualProfitUpdate) {
	this.manualProfitUpdate = manualProfitUpdate;
    }

    public BigDecimal getLcdLcdId() {
	return lcdLcdId;
    }

    public void setLcdLcdId(BigDecimal lcdLcdId) {
	this.lcdLcdId = lcdLcdId;
    }

    public Long getPaymentId() {
	return paymentId;
    }

    public void setPaymentId(Long paymentId) {
	this.paymentId = paymentId;
    }

    public BigDecimal getPaymentsPerYear() {
	return paymentsPerYear;
    }

    public void setPaymentsPerYear(BigDecimal paymentsPerYear) {
	this.paymentsPerYear = paymentsPerYear;
    }

    public String getPaymentProfile() {
	return paymentProfile;
    }

    public void setPaymentProfile(String paymentProfile) {
	this.paymentProfile = paymentProfile;
    }

    public BigDecimal getAdvancePayments() {
	return advancePayments;
    }

    public void setAdvancePayments(BigDecimal advancePayments) {
	this.advancePayments = advancePayments;
    }

    public BigDecimal getApr() {
	return apr;
    }

    public void setApr(BigDecimal apr) {
	this.apr = apr;
    }

    public String getUsedVehicle() {
	return usedVehicle;
    }

    public void setUsedVehicle(String usedVehicle) {
	this.usedVehicle = usedVehicle;
    }

    public ResidualTable getResidualTable() {
	return residualTable;
    }

    public void setResidualTable(ResidualTable residualTable) {
	this.residualTable = residualTable;
    }

    public MaintenanceTable getMaintenanceTable() {
	return maintenanceTable;
    }

    public void setMaintenanceTable(MaintenanceTable maintenanceTable) {
	this.maintenanceTable = maintenanceTable;
    }

    public Long getReplacementFmsId() {
	return replacementFmsId;
    }

    public void setReplacementFmsId(Long replacementFmsId) {
	this.replacementFmsId = replacementFmsId;
    }

    public Long getRevisionQmdId() {
	return revisionQmdId;
    }

    public void setRevisionQmdId(Long revisionQmdId) {
	this.revisionQmdId = revisionQmdId;
    }

    public BigDecimal getQuoteMaintCost() {
	return quoteMaintCost;
    }

    public void setQuoteMaintCost(BigDecimal quoteMaintCost) {
	this.quoteMaintCost = quoteMaintCost;
    }

    public List<QuotationProfitability> getQuotationProfitabilities() {
	return quotationProfitabilities;
    }

    public void setQuotationProfitabilities(List<QuotationProfitability> quotationProfitabilities) {
	this.quotationProfitabilities = quotationProfitabilities;
    }

  
    public BigDecimal getCalculatedMontlyRental() {
	return calculatedMontlyRental;
    }

    public void setCalculatedMontlyRental(BigDecimal calculatedMontlyRental) {
	this.calculatedMontlyRental = calculatedMontlyRental;
    }

    public BigDecimal getCalculatedCapCost() {
	return calculatedCapCost;
    }

    public void setCalculatedCapCost(BigDecimal calculatedCapCost) {
	this.calculatedCapCost = calculatedCapCost;
    }

    public BigDecimal getSuppliedResidual() {
	return suppliedResidual;
    }

    public void setSuppliedResidual(BigDecimal suppliedResidual) {
	this.suppliedResidual = suppliedResidual;
    }

    public boolean isCalcRentalApplicable() {
        return calcRentalApplicable;
    }

    public void setCalcRentalApplicable(boolean calcRentalApplicable) {
        this.calcRentalApplicable = calcRentalApplicable;
    }  

    public Date getPrintedDate() {
	return printedDate;
    }

    public void setPrintedDate(Date printedDate) {
	this.printedDate = printedDate;
    }

    public Date getAcceptanceDate() {
	return acceptanceDate;
    }

    public void setAcceptanceDate(Date acceptanceDate) {
	this.acceptanceDate = acceptanceDate;
    }

    public String getPrintedInd() {
	return printedInd;
    }

    public void setPrintedInd(String printedInd) {
	this.printedInd = printedInd;
    }

    public String getOrderType() {
	return orderType;
    }

    public void setOrderType(String orderType) {
	this.orderType = orderType;
    }   

    public String getPlateTypeCode() {
		return plateTypeCode;
	}

	public void setPlateTypeCode(String plateTypeCode) {
		this.plateTypeCode = plateTypeCode;
	}

	public String getSteppedCalc() {
	return steppedCalc;
    }

    public void setSteppedCalc(String steppedCalc) {
	this.steppedCalc = steppedCalc;
    }

    public List<QuotationModelFinances> getQuotationModelFinances() {
	return quotationModelFinances;
    }

    public void setQuotationModelFinances(List<QuotationModelFinances> quotationModelFinances) {
	this.quotationModelFinances = quotationModelFinances;
    }

    public String getReCalcNeeded() {
	return reCalcNeeded;
    }

    public void setReCalcNeeded(String reCalcNeeded) {
	this.reCalcNeeded = reCalcNeeded;
    }
    
    public Long getContractChangeEventPeriod() {
	return contractChangeEventPeriod;
    }

    public void setContractChangeEventPeriod(Long contractChangeEventPeriod) {
	this.contractChangeEventPeriod = contractChangeEventPeriod;
    }
 
    public Long getProjectedMonths() {
	return projectedMonths;
    }

    public void setProjectedMonths(Long projectedMonths) {
	this.projectedMonths = projectedMonths;
    }
  
	public String getVehicleOffContract() {
		return vehicleOffContract;
	}

	public void setVehicleOffContract(String vehicleOffContract) {
		this.vehicleOffContract = vehicleOffContract;
	}

	public Date getAmendmentEffectiveDate() {
		return amendmentEffectiveDate;
	}

	public void setAmendmentEffectiveDate(Date amendmentEffectiveDate) {
		this.amendmentEffectiveDate = amendmentEffectiveDate;
	}

	public String getLastAmendedUser() {
		return lastAmendedUser;
	}

	public void setLastAmendedUser(String lastAmendedUser) {
		this.lastAmendedUser = lastAmendedUser;
	}

	public List<QuotationStepStructure> getQuotationStepStructure() {
		return QuotationStepStructure;
	}

	public void setQuotationStepStructure(List<QuotationStepStructure> quotationStepStructure) {
		QuotationStepStructure = quotationStepStructure;
	}

	public BigDecimal getDepreciationFactor() {
		return depreciationFactor;
	}

	public void setDepreciationFactor(BigDecimal depreciationFactor) {
		this.depreciationFactor = depreciationFactor;
	}

    public Date getRequiredDate() {
		return requiredDate;
	}

	public void setRequiredDate(Date requiredDate) {
		this.requiredDate = requiredDate;
	}

	public String getRequestForAcceptanceYn() {
		return requestForAcceptanceYn;
	}

	public void setRequestForAcceptanceYn(String requestForAcceptanceYn) {
		this.requestForAcceptanceYn = requestForAcceptanceYn;
	}

	public String getRequestForAcceptanceBy() {
		return requestForAcceptanceBy;
	}

	public void setRequestForAcceptanceBy(String requestForAcceptanceBy) {
		this.requestForAcceptanceBy = requestForAcceptanceBy;
	}

	public String getRequestForAcceptanceType() {
		return requestForAcceptanceType;
	}

	public void setRequestForAcceptanceType(String requestForAcceptanceType) {
		this.requestForAcceptanceType = requestForAcceptanceType;
	}

	public Date getRequestForAcceptanceDate() {
		return requestForAcceptanceDate;
	}

	public void setRequestForAcceptanceDate(Date requestForAcceptanceDate) {
		this.requestForAcceptanceDate = requestForAcceptanceDate;
	}

	public String getRejectReasonFromQueue() {
		return rejectReasonFromQueue;
	}

	public void setRejectReasonFromQueue(String rejectReasonFromQueue) {
		this.rejectReasonFromQueue = rejectReasonFromQueue;
	}

	public Date getDateReceived() {
		return dateReceived;
	}

	public void setDateReceived(Date dateReceived) {
		this.dateReceived = dateReceived;
	}
	
	public Long getQuoteStartOdo() {
		return quoteStartOdo;
	}

	public void setQuoteStartOdo(Long quoteStartOdo) {
		this.quoteStartOdo = quoteStartOdo;
	}

	public String getTmpVinNo() {
		return tmpVinNo;
	}

	public void setTmpVinNo(String tmpVinNo) {
		this.tmpVinNo = tmpVinNo;
	}

	public String getClientRequestType() {
		return clientRequestType;
	}

	public void setClientRequestType(String clientRequestType) {
		this.clientRequestType = clientRequestType;
	}	
	
	public ExternalAccount getRequestedDeliveryDealer() {
		return requestedDeliveryDealer;
	}

	public void setRequestedDeliveryDealer(ExternalAccount requestedDeliveryDealer) {
		this.requestedDeliveryDealer = requestedDeliveryDealer;
	}
	
	@Override
    public String toString() {
		return "com.mikealbert.vision.entity.QuotationModel[ qmdId=" + qmdId + " ]";
    }

	public ColourCodes getColourCode() {
		return colourCode;
	}

	public void setColourCode(ColourCodes colourCode) {
		this.colourCode = colourCode;
	}

	public List<QuotationNotes> getQuotationNotes() {
		return quotationNotes;
	}

	public void setQuotationNotes(List<QuotationNotes> quotationNotes) {
		this.quotationNotes = quotationNotes;
	}

	public TrimCodes getTrimCode() {
		return trimCode;
	}

	public void setTrimCode(TrimCodes trimCode) {
		this.trimCode = trimCode;
	}

	public List<UpfitterProgress> getUpfitterProgress() {
		return upfitterProgress;
	}

	public void setUpfitterProgress(List<UpfitterProgress> upfitterProgress) {
		this.upfitterProgress = upfitterProgress;
	}

	public Long getFmsId() {
		return fmsId;
	}

	public void setFmsId(Long fmsId) {
		this.fmsId = fmsId;
	}

	public Date getVloPrintedDate() {
		return vloPrintedDate;
	}

	public void setVloPrintedDate(Date vloPrintedDate) {
		this.vloPrintedDate = vloPrintedDate;
	}

	public ExtAccAffiliate getExtAccAffiliate() {
		return extAcctAffiliate;
	}

	public void setExtAccAffiliate(ExtAccAffiliate extAcctAffiliate) {
		this.extAcctAffiliate = extAcctAffiliate;
	}

	public QuotationStatusCodes getQuotationStatusCodes() {
		return quotationStatusCodes;
	}

	public void setQuotationStatusCodes(QuotationStatusCodes quotationStatusCodes) {
		this.quotationStatusCodes = quotationStatusCodes;
	}

	public Date getRevisionExpDate() {
		return revisionExpDate;
	}

	public void setRevisionExpDate(Date revisionExpDate) {
		this.revisionExpDate = revisionExpDate;
	}

	public List<QuoteModelPropertyValue> getQuoteModelPropertyValues() {
		return quoteModelPropertyValues;
	}

	public void setQuoteModelPropertyValues(List<QuoteModelPropertyValue> quoteModelPropertyValues) {
		this.quoteModelPropertyValues = quoteModelPropertyValues;
	}

	public String getRejectReason() {
		return rejectReason;
	}

	public void setRejectReason(String rejectReason) {
		this.rejectReason = rejectReason;
	}

}
