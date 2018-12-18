package com.mikealbert.vision.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class UpfitterPriceAddressVO implements Serializable {
	private static final long serialVersionUID = 2783661992261064418L;
	
	private Long modelId;
    private Long dealerAccessoryId;
    private Long dealerPriceListId;
    private Long payeeCorporateId;
    private String payeeAccountCode;
    private String payeeAccountType;
    private String payeeAccountName;
    private BigDecimal basePrice;
    private BigDecimal vatAmount;
    private Date effectiveDate;
    private String quoteNumber;
	private String defaultFormattedAddresss;
	
	
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
	public Long getDealerPriceListId() {
		return dealerPriceListId;
	}
	public void setDealerPriceListId(Long dealerPriceListId) {
		this.dealerPriceListId = dealerPriceListId;
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
	public String getDefaultFormattedAddresss() {
		return defaultFormattedAddresss;
	}
	public void setDefaultFormattedAddresss(String defaultFormattedAddresss) {
		this.defaultFormattedAddresss = defaultFormattedAddresss;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((dealerAccessoryId == null) ? 0 : dealerAccessoryId.hashCode());
		result = prime * result + ((dealerPriceListId == null) ? 0 : dealerPriceListId.hashCode());
		result = prime * result + ((quoteNumber == null) ? 0 : quoteNumber.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(this == obj)
			return true;
		if(obj == null)
			return false;
		if(getClass() != obj.getClass())
			return false;
		UpfitterPriceAddressVO other = (UpfitterPriceAddressVO) obj;
		if(dealerAccessoryId == null) {
			if(other.dealerAccessoryId != null)
				return false;
		} else if(!dealerAccessoryId.equals(other.dealerAccessoryId))
			return false;
		if(dealerPriceListId == null) {
			if(other.dealerPriceListId != null)
				return false;
		} else if(!dealerPriceListId.equals(other.dealerPriceListId))
			return false;
		if(quoteNumber == null) {
			if(other.quoteNumber != null)
				return false;
		} else if(!quoteNumber.equals(other.quoteNumber))
			return false;
		return true;
	}
	
}