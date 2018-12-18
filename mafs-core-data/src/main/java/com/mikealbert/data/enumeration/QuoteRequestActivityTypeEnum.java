package com.mikealbert.data.enumeration;

/**
* Doc Type Enum list all the possible Quote Request Activity Type
*  
*/
public enum QuoteRequestActivityTypeEnum {	
	
	REWORK("REWORK", "Rework");
	
	private String code;
	private String name;
	
	private QuoteRequestActivityTypeEnum(String code, String name){
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
