package com.mikealbert.service.vo;

import java.io.Serializable;
import java.math.BigDecimal;

public class FormalQuoteOptionAccessoryRechargeInputVO implements Serializable{
	private static final long serialVersionUID = -8362878977711986553L;

	private String code;
	private String level;
	private BigDecimal price;
	
	public String getCode() {
		return code;
	}

	public FormalQuoteOptionAccessoryRechargeInputVO() {}
	
	public FormalQuoteOptionAccessoryRechargeInputVO(String code, String level, BigDecimal price) {
		setCode(code);
		setLevel(level);
		setPrice(price);
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	@Override
	public String toString() {
		return "FormalQuoteOptionAccessoryRechargeInputVO [code=" + code + ", level=" + level + ", price=" + price + "]";
	}	
}
