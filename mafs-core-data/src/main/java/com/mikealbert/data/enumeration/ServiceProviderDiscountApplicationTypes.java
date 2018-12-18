package com.mikealbert.data.enumeration;

/**
* Service Provider Discount Application Enum.
* 
* <P>Enum of Service Provider Discount Application Types</p> 
*  
*  
*/
public enum ServiceProviderDiscountApplicationTypes {
	
	DISC_APPL_REBATE("R", "Rebate"),
	DISC_APPL_OFF_INVOICE("O", "Off Invoice");
		
	private String code;
	private String description;
	
	private ServiceProviderDiscountApplicationTypes(String code, String description){
		this.setCode(code);
		this.setDescription(description);
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
