package com.mikealbert.data.enumeration;

public enum AcquisitionTypeEnum {

	BAIL("BAIL", "Bailment"),
	FIN("FIN", "Financing-only"),
	POOL("POOL", "Pool"),
	FACTORY("FACTORY", "Factory"),
	LOCATE("LOCATE", "Locate"),
	STOCK("STOCK", "Stock");
	
	
	private String code;
	private String description;
	
	private AcquisitionTypeEnum(String code, String description){
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
