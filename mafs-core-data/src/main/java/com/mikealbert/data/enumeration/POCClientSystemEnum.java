package com.mikealbert.data.enumeration;

/**
* POC Systems Enum.
* 
* <P>Enum of POC Client System</p> 
*  
* <P>POCs can be setup for differenct types of client applications, e.g. WEB_CA, TAL, etc
* </p>
*  
*/
public enum POCClientSystemEnum {	
	
	WILLOW("WILLOW", "Willow"),
	TAL("TAL", "Title and License"),
	WEB_CA("WEB_CA", "Web"),
	ALL("ALL", "All systems"),
	MAINT("MAINT", "Maintenance"),
	RECALL("RECALL", "Vehicle Recall");
	
	private String code;
	private String description;
	
	private POCClientSystemEnum(String code, String description){
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
