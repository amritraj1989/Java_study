package com.mikealbert.service.vo;

import java.io.Serializable;
import java.math.BigDecimal;

public class FormalQuoteUpfitInputVO implements Serializable{
	private static final long serialVersionUID = -5598512668091781770L;
	
	private Long vcfId;
	private Long ufqId;
	private Long dacId;
	private BigDecimal price;
	
	public FormalQuoteUpfitInputVO() {}
	
	public FormalQuoteUpfitInputVO(Long vcfId, Long ufqId, Long dacId, BigDecimal price) {
		setVcfId(vcfId);
		setUfqId(ufqId);
		setDacId(dacId);
		setPrice(price);
	}

	public Long getVcfId() {
		return vcfId;
	}
	
	public void setVcfId(Long vcfId) {
		this.vcfId = vcfId;
	}

	public Long getUfqId() {
		return ufqId;
	}

	public void setUfqId(Long ufqId) {
		this.ufqId = ufqId;
	}

	public Long getDacId() {
		return dacId;
	}

	public void setDacId(Long dacId) {
		this.dacId = dacId;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	@Override
	public String toString() {
		return "FormalQuoteUpfitInputVO [vcfId=" + vcfId + ", ufqId=" + ufqId + ", dacId=" + dacId + ", price=" + price
				+ "]";
	}
	
	
}
