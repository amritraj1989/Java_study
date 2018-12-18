package com.mikealbert.data.enumeration;

/**
* Doc Type Enum list all the possible state a Quote Request can be in
*  
*/
public enum QuoteRequestStatusEnum {	
	
	SAVED("SAVED", "Saved"),
	SUBMITTED("SUBMITTED", "Submitted"),
	IN_PROGRESS("IN_PROGRESS", "In Progress"),	
	COMPLETED("COMPLETED", "Completed"),
	CLOSED("CLOSED", "Closed");
	
	private String code;
	private String name;
	
	private QuoteRequestStatusEnum(String code, String name){
		this.setCode(code);
		this.setName(name);
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDescription() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	
}
