package com.mikealbert.vision.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.mikealbert.data.vo.FinanceParameterVO;

public class EleAmendmentDetailVO  implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private	String	amendmentTypeInd;
	private	String	elementType;
	private	Long	elementId;
	private	String	elementDesc;
	private	BigDecimal	rental;
	private	BigDecimal	noRentals;
	private	BigDecimal	totalRental;
	private Date	effectiveDate;
	private	String	rechargeText ;
	private BigDecimal	invoiceAmt;
	private BigDecimal	rechargeAmt;
	private String	poNumber;
	private Date	poOrderDate;
	private	String	accountInfo;
	private	BigDecimal	dealCost;
	private	BigDecimal	clientCost;
	private boolean inRateTreatment;
	
	
	private List<FinanceParameterVO> financeParameters;
	public String getAmendmentTypeInd() {
		return amendmentTypeInd;
	}
	public void setAmendmentTypeInd(String amendmentTypeInd) {
		this.amendmentTypeInd = amendmentTypeInd;
	}
	public String getElementType() {
		return elementType;
	}
	public void setElementType(String elementType) {
		this.elementType = elementType;
	}
	public String getElementDesc() {
		return elementDesc;
	}
	public void setElementDesc(String elementDesc) {
		this.elementDesc = elementDesc;
	}
	public BigDecimal getRental() {
		return rental;
	}
	public void setRental(BigDecimal rental) {
		this.rental = rental;
	}
	public BigDecimal getNoRentals() {
		return noRentals;
	}
	public void setNoRentals(BigDecimal noRentals) {
		this.noRentals = noRentals;
	}
	public BigDecimal getTotalRental() {
		return totalRental;
	}
	public void setTotalRental(BigDecimal totalRental) {
		this.totalRental = totalRental;
	}
	public Date getEffectiveDate() {
		return effectiveDate;
	}
	public void setEffectiveDate(Date effectiveDate) {
		this.effectiveDate = effectiveDate;
	}
	public String getRechargeText() {
		return rechargeText;
	}
	public void setRechargeText(String rechargeText) {
		this.rechargeText = rechargeText;
	}
	public List<FinanceParameterVO> getFinanceParameters() {
		return financeParameters;
	}
	public void setFinanceParameters(List<FinanceParameterVO> financeParameters) {
		this.financeParameters = financeParameters;
	}
	public BigDecimal getInvoiceAmt() {
		return invoiceAmt;
	}
	public void setInvoiceAmt(BigDecimal invoiceAmt) {
		this.invoiceAmt = invoiceAmt;
	}
	public BigDecimal getRechargeAmt() {
		return rechargeAmt;
	}
	public void setRechargeAmt(BigDecimal rechargeAmt) {
		this.rechargeAmt = rechargeAmt;
	}
	public String getPoNumber() {
		return poNumber;
	}
	public void setPoNumber(String poNumber) {
		this.poNumber = poNumber;
	}
	public Date getPoOrderDate() {
		return poOrderDate;
	}
	public void setPoOrderDate(Date poOrderDate) {
		this.poOrderDate = poOrderDate;
	}	
	public Long getElementId() {
		return elementId;
	}
	public void setElementId(Long elementId) {
		this.elementId = elementId;
	}
	public String getAccountInfo() {
		return accountInfo;
	}
	public void setAccountInfo(String accountInfo) {
		this.accountInfo = accountInfo;
	}
	public BigDecimal getDealCost() {
		return dealCost;
	}
	public void setDealCost(BigDecimal dealCost) {
		this.dealCost = dealCost;
	}
	public BigDecimal getClientCost() {
		return clientCost;
	}
	public void setClientCost(BigDecimal clientCost) {
		this.clientCost = clientCost;
	}
	public boolean isInRateTreatment() {
		return inRateTreatment;
	}
	public void setInRateTreatment(boolean inRateTreatment) {
		this.inRateTreatment = inRateTreatment;
	}
	
	
}
