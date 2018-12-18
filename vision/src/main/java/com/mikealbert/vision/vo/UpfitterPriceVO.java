package com.mikealbert.vision.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class UpfitterPriceVO implements Serializable {
	private static final long serialVersionUID = -6380533501642465292L;
	
	private Long modelId;
    private Long dealerAccessoryId;
    private Long payeeCorporateId;
    private String payeeAccountCode;
    private String payeeAccountType;
    private String payeeAccountName;
    private Long dealerPriceListId;
    private Long leadTime;
    private BigDecimal basePrice;
    private BigDecimal totalPrice;
    private BigDecimal msrp;
    private Date effectiveDate;
    private String quoteNumber;
    
    public UpfitterPriceVO(){}
    
	public Long getModelId() {
		return modelId;
	}
	
	public void setModelId(Long modelId) {
		this.modelId = modelId;
	}

	public Long getDealerAccessoryId() {
		return dealerAccessoryId;
	}

	public void setDealerAccessoryId(Long dealerAccessoryId) {
		this.dealerAccessoryId = dealerAccessoryId;
	}

	public Long getPayeeCorporateId() {
		return payeeCorporateId;
	}

	public void setPayeeCorporateId(Long payeeCorporateId) {
		this.payeeCorporateId = payeeCorporateId;
	}

	public String getPayeeAccountCode() {
		return payeeAccountCode;
	}

	public void setPayeeAccountCode(String payeeAccountCode) {
		this.payeeAccountCode = payeeAccountCode;
	}

	public String getPayeeAccountType() {
		return payeeAccountType;
	}

	public void setPayeeAccountType(String payeeAccountType) {
		this.payeeAccountType = payeeAccountType;
	}

	public String getPayeeAccountName() {
		return payeeAccountName;
	}

	public void setPayeeAccountName(String payeeAccountName) {
		this.payeeAccountName = payeeAccountName;
	}

	public Long getDealerPriceListId() {
		return dealerPriceListId;
	}

	public void setDealerPriceListId(Long dealerPriceListId) {
		this.dealerPriceListId = dealerPriceListId;
	}

	public Long getLeadTime() {
		return leadTime;
	}

	public void setLeadTime(Long leadTime) {
		this.leadTime = leadTime;
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

	public BigDecimal getMsrp() {
		return msrp;
	}

	public void setMsrp(BigDecimal msrp) {
		this.msrp = msrp;
	}

	public Date getEffectiveDate() {
		return effectiveDate;
	}

	public void setEffectiveDate(Date effectiveDate) {
		this.effectiveDate = effectiveDate;
	}

	public String getQuoteNumber() {
		return quoteNumber;
	}

	public void setQuoteNumber(String quoteNumber) {
		this.quoteNumber = quoteNumber;
	}
}