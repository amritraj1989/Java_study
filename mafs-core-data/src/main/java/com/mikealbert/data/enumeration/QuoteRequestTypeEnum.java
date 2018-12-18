package com.mikealbert.data.enumeration;

/**
* Doc Type Enum list all the possible Quote Request Statuses
*  
*/
public enum QuoteRequestTypeEnum {	
	
	IMM_NEED_ALL("IMM_NEED_All", "Immediate Needs - All"),
	IMM_NEED_CLIENT("IMM_NEED_CLIENT", "Immediate Need - Client Locate"),
	IMM_NEED_LOCATE("IMM_NEED_LOCATE", "Immediate Need - MAFS Locate"),
	IMM_NEED_STOCK("IMM_NEED_STOCK", "Immediate Need - Stock"),
	UPFIT_ASSESSMENT("UPFIT_ASSESSMENT", "Upfit Assessment"),
	IMM_NEED_UPFIT_CLIENT_LOCATE("IMM_NEED_UPFIT_CLIENT_LOCATE", "Immediate Need Upfit - Client Locate"),
	IMM_NEED_UPFIT_MAFS_LOCATE("IMM_NEED_UPFIT_MAFS_LOCATE", "Immediate Need Upfit - MAFS Locate"),
	IMM_NEED_UPFIT_STOCK("IMM_NEED_UPFIT_STOCK", "Immediate Need Upfit - Stock");
	
	private String code;
	private String name;
	
	private QuoteRequestTypeEnum(String code, String name){
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
