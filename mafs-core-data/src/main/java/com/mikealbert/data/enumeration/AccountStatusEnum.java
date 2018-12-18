package com.mikealbert.data.enumeration;

/**
* Vehicle Status Enum.
* 
* <P>Enum of account status types</p> 
*  
* <P>Note: At the time of writing, an account can been in one of to states: "O"pen or "C"losed.
*          No lookup table exist for the account status. They are merely hard coded upon insert.
* </p>
*  
*/
public enum AccountStatusEnum {	
	
	OPEN("O", "Open"),
	CLOSED("C", "Closed");
	
	private String code;
	private String description;
	
	private AccountStatusEnum(String code, String description){
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
