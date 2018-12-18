package com.mikealbert.data.enumeration;


public enum ClientRequestTypeEnum {

	AS("AS", "ASAP"),
	NS("NS", "Not Specified"),
	OD("OD", "OD");
	
	private String code;
	private String description;
	
	private ClientRequestTypeEnum(String code, String description){
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
	
	public static ClientRequestTypeEnum getClientRequestTypeEnum(String code){
		for(ClientRequestTypeEnum type : values()){
			if(type.getCode().equals(code)){
				return type;
			}
		}
		return null;
	}
}
