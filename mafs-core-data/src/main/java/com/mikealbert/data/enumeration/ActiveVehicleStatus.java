package com.mikealbert.data.enumeration;

public enum ActiveVehicleStatus {

	STATUS_ON_ORDER(1, "Vehicle On Order"), 
	STATUS_ON_CONTRACT(2, "On Contract"),	
	STATUS_PENDING_LIVE(18, "Pending Live"),
	STATUS_ON_STOCK(7, "Stock");
	
	private int code;
	private String description;
	
	private ActiveVehicleStatus(int code, String description){
		this.setCode(code);
		this.setDescription(description);
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	
}
