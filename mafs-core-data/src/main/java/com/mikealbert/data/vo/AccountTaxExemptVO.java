package com.mikealbert.data.vo;

public class AccountTaxExemptVO {

	public AccountTaxExemptVO(String state, String taxExemptNo) {
		super();
		this.state = state;
		this.taxExemptNo = taxExemptNo;
	}

	private String state;
	private String taxExemptNo;

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getTaxExemptNo() {
		return taxExemptNo;
	}

	public void setTaxExemptNo(String taxExemptNo) {
		this.taxExemptNo = taxExemptNo;
	}

}
