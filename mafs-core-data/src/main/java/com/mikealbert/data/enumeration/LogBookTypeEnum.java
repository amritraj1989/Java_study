package com.mikealbert.data.enumeration;

public enum LogBookTypeEnum {

	TYPE_ACTIVITY("ACTIVITY", "Activity Notes", "Activity Log"),
	TYPE_FM_UNIT_NOTES("FM_UNIT_NOTES", "FM Unit Notes", "FM Unit Notes"),
	TYPE_UPFIT_PRG_NOTES("UPFIT_PRG_NOTES", "Upfit", "Upfit Progress Chasing"),
	TYPE_IN_SERV_PRG_NOTES("INSERV_PRG_NOTES", "In Service", "In Service Date Chasing"),
	TYPE_CLIENT_FACING_NOTES("CLIENT_FACING_NOTES", "Client Facing", "Client Facing"),
	TYPE_BASE_VEH_ORDER_NOTES("BASE_VEH_ORDER_NOTES", "Manufacturer", "Manufacturer Progress Chasing"),	
	TYPE_EXTERNAL_FLEET_NOTES("EXTERNAL_FLEET_NOTES", "EXTERNAL", "Fleet Notes"),
	TYPE_QUOTE_REQEUST_NOTES("QUOTE_REQUEST_NOTES", "Quote Request", "Quote Request Notes"),
	TYPE_VEH_CONFIG_NOTES("VEH_CONFIG_NOTES", "Vehicle Configuration", "Vehicle Configuration Notes"),	
	TYPE_EXTERNAL_JOB_NOTES("EXTERNAL_JOB_NOTES", "EXTERNAL", "Job Notes"),	
	TYPE_EXTERNAL_TAL_FILE_NOTES("EXTERNAL_TAL_FILE_NOTES", "EXTERNAL", "TAL File Notes"),	
	TYPE_PURCHASE_ORDER_NOTES("PURCHASE_ORDER_NOTES", "Purchase Order", "Purchase Order Notes"),
	TYPE_VEHICLE_STATUS_NOTES("VEHICLE_STATUS_NOTES", "Vehicle Status", "Vehicle Status Notes");
	
	private String code;
	private String name;
	private String description;
		
	private LogBookTypeEnum(String code, String name, String description){
		this.setCode(code);
		this.setName(name);
		this.setDescription(description);
	}

	public LogBookTypeEnum getLogBookTypeEnum(String code){
		for(LogBookTypeEnum type : values()){
			if(type.getCode().equals(code)){
				return type;
			}
		}
		throw new IllegalArgumentException();
	}
	
	public String getCode() {
		return code;
	}


	public void setCode(String code) {
		this.code = code;
	}


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	
}
