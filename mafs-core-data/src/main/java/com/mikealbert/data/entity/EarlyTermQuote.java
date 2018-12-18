package com.mikealbert.data.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

/**
 * Mapped to EARLY_TERM_QUOTE table
 */

@Entity
@Table(name = "EARLY_TERM_QUOTE")
public class EarlyTermQuote implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@NotNull
	@Column(name = "ETQ_ID")
	private Long etqId;
	
	@Column(name = "CON_CON_ID")
	private Long conId;
	
	@Column(name = "CLN_CLN_ID")
	private Long clnId;
	
	@Column(name = "FMS_FMS_ID")
	private Long fmsId;
	
    @Temporal(TemporalType.DATE)	
	@Column(name = "QUOTE_DATE")
	private Date quoteDate;
	
    @Temporal(TemporalType.DATE)    
	@Column(name = "QUOTE_END_DATE")
	private Date quoteEndDate;
	
    @Temporal(TemporalType.DATE)    
	@Column(name = "QUOTE_VALID_DATE")
	private Date quoteValidDate;
	
	@Column(name = "WAITING_EXCESS")
	private String waitingExcess;
	
	@Column(name = "REJECT_FLAG")
	private String rejectFlag;
	
	@Column(name = "ACCEPT_FLAG")
	private String acceptFlag;
	
	@Column(name = "DELAYED_INVOICE_IND")
	private String delayedInvoiceInd;
	
	@Column(name = "EXCL_PAY_TO_DATE")
	private String exclPayToDate;
	
	@Column(name = "DPR_DPR_ID")
	private Long dprId;
	
	@Column(name = "COST_VEHICLE")
	private BigDecimal costVehicle;
	
	@Column(name = "MARKET_VALUE")
	private BigDecimal marketValue;
	
	@Column(name = "SALE_PRICE")
	private BigDecimal salePrice;
	
	@Column(name = "AUCTION_COSTS")
	private BigDecimal auctionCosts;
	
	@Column(name = "PROFIT_AMT")
	private BigDecimal profitAmt;
	
	@Column(name = "AMT_EARLY_TERM")
	private BigDecimal amtEarlyTerm;

	@Column(name = "ITEM_CODE_TYPE")
	private String itemCodeType;
	
    @Temporal(TemporalType.DATE)	
	@Column(name = "NORMAL_END_DATE")
	private Date normalEndDate;
	
	@Column(name = "LEASE_ELE")
	private BigDecimal leaseEle;
	
	@Column(name = "FINANCE_ELE")
	private BigDecimal financeEle;
	
	@Column(name = "MAINT_ELE")
	private BigDecimal mainEle;
	
	@Column(name = "LICENCE_AMT")
	private BigDecimal licenseAmt;
	
	@Column(name = "FORMULAE_USED")
	private String formulaeUsed;

	@Column(name = "EST_MILEAGE")
	private Long estMileage;
	
	@Column(name = "EST_SALE_PRICE")
	private BigDecimal estSalePrice;
	
	@Column(name = "THIRD_PARTY_QUOTE")
	private BigDecimal thirdPartyQuote;
	
	@Column(name = "OP_CODE")
	private String opCode;
	
	@Column(name = "REASON_CODE")
	private String reasonCode;
	
	@Column(name = "MANUAL_ET_AMOUNT")
	private BigDecimal manualEtAmount;
	
	@Column(name = "MANUAL_XSM_AMOUNT")
	private BigDecimal manualXsmAmount;
	
	@Column(name = "EXCESS_DISTANCE")
	private BigDecimal excessDistance;
	
	@Column(name = "PRINTED_IND")
	private String printedInd;
	
	@Column(name = "ET_PROGRAM_ELIGIBILITY")
	private String etProgramEligibility;
	
	@Column(name = "XSM_PROGRAM_ELIGIBILITY")
	private String xsmProgramEligibility;

	public Long getEtqId() {
		return etqId;
	}

	public void setEtqId(Long etqId) {
		this.etqId = etqId;
	}

	public Long getConId() {
		return conId;
	}

	public void setConId(Long conId) {
		this.conId = conId;
	}

	public Long getClnId() {
		return clnId;
	}

	public void setClnId(Long clnId) {
		this.clnId = clnId;
	}

	public Long getFmsId() {
		return fmsId;
	}

	public void setFmsId(Long fmsId) {
		this.fmsId = fmsId;
	}

	public Date getQuoteDate() {
		return quoteDate;
	}

	public void setQuoteDate(Date quoteDate) {
		this.quoteDate = quoteDate;
	}

	public Date getQuoteEndDate() {
		return quoteEndDate;
	}

	public void setQuoteEndDate(Date quoteEndDate) {
		this.quoteEndDate = quoteEndDate;
	}

	public Date getQuoteValidDate() {
		return quoteValidDate;
	}

	public void setQuoteValidDate(Date quoteValidDate) {
		this.quoteValidDate = quoteValidDate;
	}

	public String getWaitingExcess() {
		return waitingExcess;
	}

	public void setWaitingExcess(String waitingExcess) {
		this.waitingExcess = waitingExcess;
	}

	public String getRejectFlag() {
		return rejectFlag;
	}

	public void setRejectFlag(String rejectFlag) {
		this.rejectFlag = rejectFlag;
	}

	public String getAcceptFlag() {
		return acceptFlag;
	}

	public void setAcceptFlag(String acceptFlag) {
		this.acceptFlag = acceptFlag;
	}

	public String getDelayedInvoiceInd() {
		return delayedInvoiceInd;
	}

	public void setDelayedInvoiceInd(String delayedInvoiceInd) {
		this.delayedInvoiceInd = delayedInvoiceInd;
	}

	public String getExclPayToDate() {
		return exclPayToDate;
	}

	public void setExclPayToDate(String exclPayToDate) {
		this.exclPayToDate = exclPayToDate;
	}

	public Long getDprId() {
		return dprId;
	}

	public void setDprId(Long dprId) {
		this.dprId = dprId;
	}

	public BigDecimal getCostVehicle() {
		return costVehicle;
	}

	public void setCostVehicle(BigDecimal costVehicle) {
		this.costVehicle = costVehicle;
	}

	public BigDecimal getMarketValue() {
		return marketValue;
	}

	public void setMarketValue(BigDecimal marketValue) {
		this.marketValue = marketValue;
	}

	public BigDecimal getSalePrice() {
		return salePrice;
	}

	public void setSalePrice(BigDecimal salePrice) {
		this.salePrice = salePrice;
	}

	public BigDecimal getAuctionCosts() {
		return auctionCosts;
	}

	public void setAuctionCosts(BigDecimal auctionCosts) {
		this.auctionCosts = auctionCosts;
	}

	public BigDecimal getProfitAmt() {
		return profitAmt;
	}

	public void setProfitAmt(BigDecimal profitAmt) {
		this.profitAmt = profitAmt;
	}

	public BigDecimal getAmtEarlyTerm() {
		return amtEarlyTerm;
	}

	public void setAmtEarlyTerm(BigDecimal amtEarlyTerm) {
		this.amtEarlyTerm = amtEarlyTerm;
	}

	public String getItemCodeType() {
		return itemCodeType;
	}

	public void setItemCodeType(String itemCodeType) {
		this.itemCodeType = itemCodeType;
	}

	public Date getNormalEndDate() {
		return normalEndDate;
	}

	public void setNormalEndDate(Date normalEndDate) {
		this.normalEndDate = normalEndDate;
	}

	public BigDecimal getLeaseEle() {
		return leaseEle;
	}

	public void setLeaseEle(BigDecimal leaseEle) {
		this.leaseEle = leaseEle;
	}

	public BigDecimal getFinanceEle() {
		return financeEle;
	}

	public void setFinanceEle(BigDecimal financeEle) {
		this.financeEle = financeEle;
	}

	public BigDecimal getMainEle() {
		return mainEle;
	}

	public void setMainEle(BigDecimal mainEle) {
		this.mainEle = mainEle;
	}

	public BigDecimal getLicenseAmt() {
		return licenseAmt;
	}

	public void setLicenseAmt(BigDecimal licenseAmt) {
		this.licenseAmt = licenseAmt;
	}

	public String getFormulaeUsed() {
		return formulaeUsed;
	}

	public void setFormulaeUsed(String formulaeUsed) {
		this.formulaeUsed = formulaeUsed;
	}

	public Long getEstMileage() {
		return estMileage;
	}

	public void setEstMileage(Long estMileage) {
		this.estMileage = estMileage;
	}

	public BigDecimal getEstSalePrice() {
		return estSalePrice;
	}

	public void setEstSalePrice(BigDecimal estSalePrice) {
		this.estSalePrice = estSalePrice;
	}

	public BigDecimal getThirdPartyQuote() {
		return thirdPartyQuote;
	}

	public void setThirdPartyQuote(BigDecimal thirdPartyQuote) {
		this.thirdPartyQuote = thirdPartyQuote;
	}

	public String getOpCode() {
		return opCode;
	}

	public void setOpCode(String opCode) {
		this.opCode = opCode;
	}

	public String getReasonCode() {
		return reasonCode;
	}

	public void setReasonCode(String reasonCode) {
		this.reasonCode = reasonCode;
	}

	public BigDecimal getManualEtAmount() {
		return manualEtAmount;
	}

	public void setManualEtAmount(BigDecimal manualEtAmount) {
		this.manualEtAmount = manualEtAmount;
	}

	public BigDecimal getManualXsmAmount() {
		return manualXsmAmount;
	}

	public void setManualXsmAmount(BigDecimal manualXsmAmount) {
		this.manualXsmAmount = manualXsmAmount;
	}

	public BigDecimal getExcessDistance() {
		return excessDistance;
	}

	public void setExcessDistance(BigDecimal excessDistance) {
		this.excessDistance = excessDistance;
	}

	public String getPrintedInd() {
		return printedInd;
	}

	public void setPrintedInd(String printedInd) {
		this.printedInd = printedInd;
	}

	public String getEtProgramEligibility() {
		return etProgramEligibility;
	}

	public void setEtProgramEligibility(String etProgramEligibility) {
		this.etProgramEligibility = etProgramEligibility;
	}

	public String getXsmProgramEligibility() {
		return xsmProgramEligibility;
	}

	public void setXsmProgramEligibility(String xsmProgramEligibility) {
		this.xsmProgramEligibility = xsmProgramEligibility;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
