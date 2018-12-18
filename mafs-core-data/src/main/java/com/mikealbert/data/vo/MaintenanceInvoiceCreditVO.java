package com.mikealbert.data.vo;

import java.math.BigDecimal;
import java.util.Date;

public class MaintenanceInvoiceCreditVO {
	
	private Long docId;
	private String creditNo;
	private Date creditDate;
	private String creditDesc;
	private Long mrtId;
	private String maintCategory;
	private String serviceCode;
	private String serviceCodeDesc;
	private String maintCode;
	private String maintCodeDesc;
	private String rechargeInd;
	private String rechargeCode;
	private String discountInd;
	private int quantity;
	private BigDecimal unitPrice;
	private BigDecimal totalPrice;
	
	public Long getDocId() {
		return docId;
	}
	public void setDocId(Long docId) {
		this.docId = docId;
	}
	public String getCreditNo() {
		return creditNo;
	}
	public void setCreditNo(String creditNo) {
		this.creditNo = creditNo;
	}
	public Date getCreditDate() {
		return creditDate;
	}
	public void setCreditDate(Date creditDate) {
		this.creditDate = creditDate;
	}
	public String getCreditDesc() {
		return creditDesc;
	}
	public void setCreditDesc(String creditDesc) {
		this.creditDesc = creditDesc;
	}
	public String getMaintCategory() {
		return maintCategory;
	}
	public void setMaintCategory(String maintCategory) {
		this.maintCategory = maintCategory;
	}
	public String getServiceCode() {
		return serviceCode;
	}
	public void setServiceCode(String serviceCode) {
		this.serviceCode = serviceCode;
	}
	public String getRechargeInd() {
		return rechargeInd;
	}
	public void setRechargeInd(String rechargeInd) {
		this.rechargeInd = rechargeInd;
	}
	public String getRechargeCode() {
		return rechargeCode;
	}
	public void setRechargeCode(String rechargeCode) {
		this.rechargeCode = rechargeCode;
	}
	public String getDiscountInd() {
		return discountInd;
	}
	public void setDiscountInd(String discountInd) {
		this.discountInd = discountInd;
	}
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	public BigDecimal getUnitPrice() {
		return unitPrice;
	}
	public void setUnitPrice(BigDecimal unitPrice) {
		this.unitPrice = unitPrice;
	}
	public BigDecimal getTotalPrice() {
		return totalPrice;
	}
	public void setTotalPrice(BigDecimal totalPrice) {
		this.totalPrice = totalPrice;
	}
	public String getMaintCode() {
		return maintCode;
	}
	public void setMaintCode(String maintCode) {
		this.maintCode = maintCode;
	}
	public String getMaintCodeDesc() {
		return maintCodeDesc;
	}
	public void setMaintCodeDesc(String maintCodeDesc) {
		this.maintCodeDesc = maintCodeDesc;
	}
	public String getServiceCodeDesc() {
		return serviceCodeDesc;
	}
	public void setServiceCodeDesc(String serviceCodeDesc) {
		this.serviceCodeDesc = serviceCodeDesc;
	}
	public Long getMrtId() {
		return mrtId;
	}
	public void setMrtId(Long mrtId) {
		this.mrtId = mrtId;
	}

}