package com.mikealbert.vision.vo;

import java.io.Serializable;
import java.math.BigDecimal;

public class QuoteDealerAccesoryVO implements Serializable {
	private static final long serialVersionUID = -1799597183542458347L;
	
	private Long quotationDealerAccessoryId;
	private Long dealerAccessoryId;
    private String vendorCode;
    private String vendorName;
    private String quoteNumber;
    private BigDecimal basePrice;
    private BigDecimal vatAmount;
    private BigDecimal rechargeAmount;
    private BigDecimal totalPrice;
    private String freeOfCharge;
    

    public Long getQuotationDealerAccessoryId() {
		return quotationDealerAccessoryId;
	}

	public void setQuotationDealerAccessoryId(Long quotationDealerAccessoryId) {
		this.quotationDealerAccessoryId = quotationDealerAccessoryId;
	}

	public Long getDealerAccessoryId() {
		return dealerAccessoryId;
	}

	public void setDealerAccessoryId(Long dealerAccessoryId) {
		this.dealerAccessoryId = dealerAccessoryId;
	}

	public String getQuoteNumber() {
		return quoteNumber;
	}

	public void setQuoteNumber(String quoteNumber) {
		this.quoteNumber = quoteNumber;
	}

	public String getVendorCode() {
		return vendorCode;
	}

	public void setVendorCode(String vendorCode) {
		this.vendorCode = vendorCode;
	}

	public String getVendorName() {
		return vendorName;
	}

	public void setVendorName(String vendorName) {
		this.vendorName = vendorName;
	}

	public BigDecimal getBasePrice() {
		return basePrice;
	}

	public void setBasePrice(BigDecimal basePrice) {
		this.basePrice = basePrice;
	}

	public BigDecimal getVatAmount() {
		return vatAmount;
	}

	public void setVatAmount(BigDecimal vatAmount) {
		this.vatAmount = vatAmount;
	}

	public BigDecimal getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(BigDecimal totalPrice) {
		this.totalPrice = totalPrice;
	}

	public BigDecimal getRechargeAmount() {
		return rechargeAmount;
	}

	public void setRechargeAmount(BigDecimal rechargeAmount) {
		this.rechargeAmount = rechargeAmount;
	}

	public String getFreeOfCharge() {
		return freeOfCharge;
	}

	public void setFreeOfCharge(String freeOfCharge) {
		this.freeOfCharge = freeOfCharge;
	}

}