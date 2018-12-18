package com.mikealbert.service.vo;

import java.io.Serializable;
import java.math.BigDecimal;

public class FormalQuoteCapitalElementInputVO implements Serializable {
	private static final long serialVersionUID = -1809463363423027981L;
	
	private Long celId;
	private BigDecimal value;
	
	
	public FormalQuoteCapitalElementInputVO(Long celId, BigDecimal value) {
		setCelId(celId);
		setValue(value);
	}
	
	public Long getCelId() {
		return celId;
	}
	public void setCelId(Long celId) {
		this.celId = celId;
	}
	public BigDecimal getValue() {
		return value;
	}
	public void setValue(BigDecimal value) {
		this.value = value;
	}
	
}
