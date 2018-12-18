package com.mikealbert.data.enumeration;

public enum DocumentSourceEnum {

	FLEET_MAINTENANCE("FLMAINT"), 
	FLEET_QUOTE("FLQUOTE");
	
	private String code;
	
	private DocumentSourceEnum(String code){
		this.code = code;
	}
	
	public String getCode(){
		return this.code;
	}
	
	
	
}
