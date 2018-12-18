package com.mikealbert.service.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.mikealbert.data.beanvalidation.MANotNull;
import com.mikealbert.data.entity.DriverGradeGroupCode;
import com.mikealbert.data.entity.ExternalAccount;
import com.mikealbert.data.entity.QuotationProfile;
import com.mikealbert.data.enumeration.VehicleOrderType;
import com.mikealbert.service.bean.validation.MAFormalQuote;

@MAFormalQuote
public abstract class FormalQuoteInput implements Serializable{
	private static final long serialVersionUID = 1L;

	@MANotNull(label="Client", message="Client Account is required")
	private ExternalAccount client;
	
	@MANotNull(label="Profile", message="Client Quote Profile is required")
	private QuotationProfile profile;

	@MANotNull(label="Grade Group Code", message="Grade Group Code is required")
	private DriverGradeGroupCode gradeGroupCode;
	
	@MANotNull(label="Vehicle Order Type", message="Vehicle Order Type is required")	
	private VehicleOrderType orderType;
	
	@MANotNull(label="Term", message="Term is required")	
	private Long term;
	
	@MANotNull(label="Distance", message="Distance is required")	
	private Long distance;
	
	private Long cfgId;
	
	private String unitNo;
	
	private Long odoReading;
	
	@MANotNull(label="Employee No", message="Employee No is required")	
	private String employeeNo;
	
	@MANotNull(label="Desired Formal Quote No", message="Desired Formal Quote No is required")		
	private Long desiredQuoId;
	
	@MANotNull(label="Informal No", message="Employee No is required")		
	private String informalQuoteId;
	
	private boolean calculate;
	
	@MANotNull(label="Client Capital Cost", message="Client Capital Cost is required")		
	private BigDecimal clientCapCost;

	@MANotNull(label="Deal Capital Cost", message="Deal Capital Cost is required")	
	private BigDecimal dealCapCost;
	
	@MANotNull(label="Invoice Adjustment", message="Invoice Adjustment is required")	
	private BigDecimal invoiceAdjustment;

	@MANotNull(label="Capital Contribution", message="Capital Contribution is required")
	private BigDecimal capitalContribution;
	
	@MANotNull(label="IRR", message="IRR is required")	
	private BigDecimal irr;
		
	private BigDecimal approvedMinimumIRR;
	
	private String approviedMinimumIRRAuthorizer;
	
	@MANotNull(label="Global Rate", message="Global Rate is required")	
	private BigDecimal globalRate;
	
	@MANotNull(label="Profit Adjustment", message="Profit Adjustment is required")	
	private BigDecimal profitAdjustment;
	
	@MANotNull(label="lastUpdated", message="Last Updated date is required")	
	private Date lastUpdated;
	
	private boolean hideInvoiceAdjustment;
	
	@MANotNull(label="Capital Elements", message="Capital Elements are required")
	private List<FormalQuoteCapitalElementInputVO> capitalElements;
	
	private List<FormalQuoteUpfitInputVO> upfits;
	
	private List<FormalQuoteUpfitRechargeInputVO> upfitRecharges;
	
	private List<FormalQuoteOptionAccessoryRechargeInputVO> optionalAccessories;

	
	public FormalQuoteInput() {
		setHideInvoiceAdjustment(true);
		setCapitalElements(new ArrayList<FormalQuoteCapitalElementInputVO>());
		setUpfits(new ArrayList<FormalQuoteUpfitInputVO>());
		setUpfitRecharges(new ArrayList<FormalQuoteUpfitRechargeInputVO>());
		setOptionalAccessories(new ArrayList<FormalQuoteOptionAccessoryRechargeInputVO>());
		
	}
	
	public FormalQuoteInput(ExternalAccount client, QuotationProfile profile, DriverGradeGroupCode gradeGroupCode, 
			VehicleOrderType orderType, Long term, Long distance, Long cfgId, Long odoReading, 
			String employeeNo) {
		setClient(client);
		setProfile(profile);
		setGradeGroupCode(gradeGroupCode);
		setOrderType(orderType);
		setTerm(term);
		setDistance(distance);
		setCfgId(cfgId);
		setOdoReading(odoReading);
		setEmployeeNo(employeeNo);
		setHideInvoiceAdjustment(true);
		setCapitalElements(new ArrayList<FormalQuoteCapitalElementInputVO>());
		setUpfits(new ArrayList<FormalQuoteUpfitInputVO>());
		setUpfitRecharges(new ArrayList<FormalQuoteUpfitRechargeInputVO>());
		setOptionalAccessories(new ArrayList<FormalQuoteOptionAccessoryRechargeInputVO>());		
	}
	
	public FormalQuoteInput(ExternalAccount client, QuotationProfile profile, DriverGradeGroupCode gradeGroupCode, 
			VehicleOrderType orderType, Long term, Long distance, Long cfgId, String unitNo, Long odoReading,
			String employeeNo, Long desiredQuoId) {
		setClient(client);
		setProfile(profile);
		setGradeGroupCode(gradeGroupCode);
		setOrderType(orderType);
		setTerm(term);
		setDistance(distance);
		setCfgId(cfgId);
		setUnitNo(unitNo);
		setOdoReading(odoReading);
		setEmployeeNo(employeeNo);
		setDesiredQuoId(desiredQuoId);
		setHideInvoiceAdjustment(true);
		setCapitalElements(new ArrayList<FormalQuoteCapitalElementInputVO>());
		setUpfits(new ArrayList<FormalQuoteUpfitInputVO>());
		setUpfitRecharges(new ArrayList<FormalQuoteUpfitRechargeInputVO>());
		setOptionalAccessories(new ArrayList<FormalQuoteOptionAccessoryRechargeInputVO>());		
	}
	
	public BigDecimal calculateMinimumIRR() {
		return getGlobalRate().add(getProfitAdjustment());
	}	

	public boolean meetsMinimumIRR() {
		return getIrr().compareTo(getGlobalRate().add(getProfitAdjustment())) >= 0;
	}
	
	public boolean meetsApprovedMinimumIRR() {
		return getIrr().compareTo(getApprovedMinimumIRR()) >= 0;
	}
	
	public ExternalAccount getClient() {
		return client;
	}

	public void setClient(ExternalAccount client) {
		this.client = client;
	}

	public QuotationProfile getProfile() {
		return profile;
	}

	public void setProfile(QuotationProfile profile) {
		this.profile = profile;
	}

	public DriverGradeGroupCode getGradeGroupCode() {
		return gradeGroupCode;
	}

	public void setGradeGroupCode(DriverGradeGroupCode gradeGroupCode) {
		this.gradeGroupCode = gradeGroupCode;
	}

	public VehicleOrderType getOrderType() {
		return orderType;
	}

	public void setOrderType(VehicleOrderType orderType) {
		this.orderType = orderType;
	}

	public Long getTerm() {
		return term;
	}

	public void setTerm(Long term) {
		this.term = term;
	}

	public Long getDistance() {
		return distance;
	}

	public void setDistance(Long distance) {
		this.distance = distance;
	}

	public Long getCfgId() {
		return cfgId;
	}

	public void setCfgId(Long cfgId) {
		this.cfgId = cfgId;
	}

	public String getUnitNo() {
		return unitNo;
	}

	public void setUnitNo(String unitNo) {
		this.unitNo = unitNo;
	}

	public Long getOdoReading() {
		return odoReading;
	}

	public void setOdoReading(Long odoReading) {
		this.odoReading = odoReading;
	}

	public String getEmployeeNo() {
		return employeeNo;
	}

	public void setEmployeeNo(String employeeNo) {
		this.employeeNo = employeeNo;
	}

	public Long getDesiredQuoId() {
		return desiredQuoId;
	}

	public void setDesiredQuoId(Long desiredQuoId) {
		this.desiredQuoId = desiredQuoId;
	}

	public String getInformalQuoteId() {
		return informalQuoteId;
	}

	public void setInformalQuoteId(String informalQuoteId) {
		this.informalQuoteId = informalQuoteId;
	}

	public boolean isCalculate() {
		return calculate;
	}

	public void setCalculate(boolean calculate) {
		this.calculate = calculate;
	}

	public BigDecimal getClientCapCost() {
		return clientCapCost;
	}

	public void setClientCapCost(BigDecimal clientCapCost) {
		this.clientCapCost = clientCapCost;
	}

	public BigDecimal getDealCapCost() {
		return dealCapCost;
	}

	public void setDealCapCost(BigDecimal dealCapCost) {
		this.dealCapCost = dealCapCost;
	}

	public BigDecimal getinvoiceAdjustment() {
		return invoiceAdjustment;
	}

	public void setinvoiceAdjustment(BigDecimal invoiceAdjustment) {
		this.invoiceAdjustment = invoiceAdjustment;
	}

	public BigDecimal getCapitalContribution() {
		return capitalContribution;
	}

	public void setCapitalContribution(BigDecimal capitalContribution) {
		this.capitalContribution = capitalContribution;
	}

	public BigDecimal getIrr() {
		return irr;
	}

	public void setIrr(BigDecimal irr) {
		this.irr = irr;
	}

	public BigDecimal getApprovedMinimumIRR() {
		return approvedMinimumIRR;
	}

	public void setApprovedMinimumIRR(BigDecimal approvedMinimumIRR) {
		this.approvedMinimumIRR = approvedMinimumIRR;
	}

	public String getApproviedMinimumIRRAuthorizer() {
		return approviedMinimumIRRAuthorizer;
	}

	public void setApproviedMinimumIRRAuthorizer(String approviedMinimumIRRAuthorizer) {
		this.approviedMinimumIRRAuthorizer = approviedMinimumIRRAuthorizer;
	}

	public BigDecimal getGlobalRate() {
		return globalRate;
	}

	public void setGlobalRate(BigDecimal globalRate) {
		this.globalRate = globalRate;
	}

	public BigDecimal getProfitAdjustment() {
		return profitAdjustment;
	}

	public void setProfitAdjustment(BigDecimal profitAdjustment) {
		this.profitAdjustment = profitAdjustment;
	}

	public Date getLastUpdated() {
		return lastUpdated;
	}

	public void setLastUpdated(Date lastUpdated) {
		this.lastUpdated = lastUpdated;
	}

	public boolean isHideInvoiceAdjustment() {
		return hideInvoiceAdjustment;
	}

	public void setHideInvoiceAdjustment(boolean hideInvoiceAdjustment) {
		this.hideInvoiceAdjustment = hideInvoiceAdjustment;
	}

	public List<FormalQuoteCapitalElementInputVO> getCapitalElements() {
		return capitalElements;
	}

	public void setCapitalElements(List<FormalQuoteCapitalElementInputVO> capitalElements) {
		this.capitalElements = capitalElements;
	}

	public List<FormalQuoteUpfitInputVO> getUpfits() {
		return upfits;
	}

	public void setUpfits(List<FormalQuoteUpfitInputVO> upfits) {
		this.upfits = upfits;
	}

	public List<FormalQuoteUpfitRechargeInputVO> getUpfitRecharges() {
		return upfitRecharges;
	}

	public void setUpfitRecharges(List<FormalQuoteUpfitRechargeInputVO> upfitRecharges) {
		this.upfitRecharges = upfitRecharges;
	}

	public List<FormalQuoteOptionAccessoryRechargeInputVO> getOptionalAccessories() {
		return optionalAccessories;
	}

	public void setOptionalAccessories(List<FormalQuoteOptionAccessoryRechargeInputVO> optionalAccessories) {
		this.optionalAccessories = optionalAccessories;
	}

}