package com.mikealbert.data.vo;

import java.math.BigDecimal;

public class DbProcParamsVO {
	/*
	 * To store OUT parameters of DB Procedure and Functions 
	 */
	
	private String successTrueFalse;
	private String message;
	private BigDecimal returnId;
	
	public DbProcParamsVO(){}

	public String getSuccessTrueFalse() {
		return successTrueFalse;
	}

	public void setSuccessTrueFalse(String successTrueFalse) {
		this.successTrueFalse = successTrueFalse;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public BigDecimal getReturnId() {
		return returnId;
	}

	public void setReturnId(BigDecimal returnId) {
		this.returnId = returnId;
	}

}
