package com.mikealbert.data.enumeration;

/**
* Time Period Calendar Enum.
* 
* <P>Enum of Time Period Calendar Types</p> 
*  
* <P>Note: At the time of writing, one type existed: "POSTING".
* </p>
*  
*/
public enum TimePeriodCalendarEnum {	
	
	POSTING("POSTING", "Posting");
	
	private String code;
	private String description;
	
	private TimePeriodCalendarEnum(String code, String description){
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
