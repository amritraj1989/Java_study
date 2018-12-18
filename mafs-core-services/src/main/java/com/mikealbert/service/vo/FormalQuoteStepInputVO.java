package com.mikealbert.service.vo;

import java.io.Serializable;

public class FormalQuoteStepInputVO implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private int numberOfMonths;

	public FormalQuoteStepInputVO() {}
	
	public FormalQuoteStepInputVO(int numberOfMonths) {
		this.numberOfMonths = numberOfMonths;
	}
	
	public int getNumberOfMonths() {
		return numberOfMonths;
	}

	public void setNumberOfMonths(int numberOfMonths) {
		this.numberOfMonths = numberOfMonths;
	}
}
