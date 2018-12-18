package com.mikealbert.data.enumeration;

/**
* Doc Type Enum list all the possible Quote payment types
*  
*/
public enum QuoteRequestPaymentTypeEnum {	
	
	FULL_DEPRECIATION("FULL_DEPRECIATION", "Full Depreciation"),
	MATCH("MATCH", "Match"),
	PCT_OF_CAPITAL_COST("PCT_OF_CAPITAL_COST", "% OF Capital Cost"),	
	FIXED_AMMOUNT("FIXED_AMMOUNT", "Fixed Amount");
	
	private String code;
	private String name;
	
	private QuoteRequestPaymentTypeEnum(String code, String name){
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
