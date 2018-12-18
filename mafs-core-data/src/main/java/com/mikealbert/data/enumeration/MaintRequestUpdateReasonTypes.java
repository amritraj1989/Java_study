package com.mikealbert.data.enumeration;

/**
* Maintenance Request Change Enum.
* 
* <P>Enum of Maintenance Request Change Types</p> 
*  
*  
*/
public enum MaintRequestUpdateReasonTypes {
	
	UPDATE_REASON_ADD("ADD", "Added New Task"),
	UPDATE_REASON_CHANGE_MAINT_CODE("CHANGE", "Modified Maintenance Codes"),
	UPDATE_REASON_PRICE_CHANGE("PRICE", "Modified Unit Price");
		
	private String code;
	private String description;
	
	private MaintRequestUpdateReasonTypes(String code, String description){
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
