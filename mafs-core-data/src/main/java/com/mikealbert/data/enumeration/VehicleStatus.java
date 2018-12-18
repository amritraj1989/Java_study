package com.mikealbert.data.enumeration;

/**
* Vehicle Status Enum.
* 
* <P>Enum of vehicle status types</p> 
*  
* <P>Note: Only status codes 1, 2, 3, 4, 5, 7, 12, 18, 21, 22, and 24 
*          are actually being used in production. This is not to say that
*          the remaining status codes will not be used in the future.</p>
*  
*/
public enum VehicleStatus {

	
	STATUS_UNKNOWN("0", "Unknown"),
	STATUS_VEHICLE_ON_ORDER("1", "Vehicle On Order"),
	STATUS_ON_CONTRACT("2", "On Contract"),
	STATUS_OFF_CONTRACT("3", "Off Contract"),
	STATUS_AWAITING_DISPOSAL("4", "Awaiting Disposal"),
	STATUS_DISPOSED_OF("5", "Disposed Of"),
	STATUS_ON_SHORT_TERM_HIRE("6", "On Short Term Hire"),
	STATUS_STOCK("7", "Stock"),
	STATUS_DEMO("8", "Demo"),
	STATUS_POOL("9", "Pool"),
	STATUS_RENTAL("10",	"Rental"),
	STATUS_NON_ASSET_RENTAL("11", "Non-Asset Rental"),
	STATUS_TERMINATED("12",	"Terminated"),
	STATUS_OFF_THE_ROAD("13", "Off The Road"),
	STATUS_WORKSHOP("14", "Workshop"),
	STATUS_MAINTENANCE("15", "Maintenance"),
	STATUS_RESERVATION("16", "Reservation"),
	STATUS_NON_ASSET_WORKSHOP("17",	"Non Asset Workshop"),
	STATUS_PENDING_LIVE("18", "Pending Live"),
	STATUS_LIVE_UNCHECKED("19",	"Live Unchecked"),
	STATUS_PENDING_LIVE_UNCHECKED("20",	"Pending Live Unchecked"),
	STATUS_OUT_OF_SERVICE("21",	"Out Of Service"),
	STATUS_WHOLESALE_PURCHASE("22",	"Wholesale Purchase"),
	STATUS_WRITE_OFF("23",	"Write Off"),
	STATUS_CANCELLED_ORDER("24",	"Cancelled Order");
	
	private String code;
	private String description;
	
	private VehicleStatus(String code, String description){
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
