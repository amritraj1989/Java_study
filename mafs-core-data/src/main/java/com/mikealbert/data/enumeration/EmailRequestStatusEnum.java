package com.mikealbert.data.enumeration;

/**
* Doc Property Enum.
* 
* <P>Enum of Email Statuses </p> 
*  
*  
*/
public enum EmailRequestStatusEnum {	
	PROCESSING("PROCESSING", "Processing email request"),
	SUCCESS("SUCCESS", "Processed successfully"),	
	FAILURE("FAILURE", "Failed to process");
	
	private String code;
	private String description;
	
	private EmailRequestStatusEnum(String code, String description){
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
