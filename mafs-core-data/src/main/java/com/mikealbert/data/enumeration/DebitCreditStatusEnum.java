package com.mikealbert.data.enumeration;

public enum DebitCreditStatusEnum {
	STATUS_SUBMITTED("S", "Submitted"),
	STATUS_APPROVED("A", "Approved"),
	STATUS_PROCESSED("P", "Processed"),
	STATUS_COMPLETED("C", "Completed"),; 
	
	private String code;
	private String description;
	
	private DebitCreditStatusEnum(String code, String description){
		setCode(code);
		setDescription(description);
	}
	
	public String getCode() {
		return code;
	}
	
	public void setCode(String code) {
		this.code = code;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
}
