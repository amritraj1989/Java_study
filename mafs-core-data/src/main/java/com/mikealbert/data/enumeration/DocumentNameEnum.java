package com.mikealbert.data.enumeration;

/**
* Doc Type Enum list all type of possible purchase order document.
*  
*/
public enum DocumentNameEnum {	
	
	MAIN_PURCHASE_ORDER("Main Purchase Order", "Main Purchase Order Document"),
	THIRD_PARTY_PURCHASE_ORDER("Third Party Purchase Order", "Third Party Purchase Order Document"),
	VEHICLE_ORDER_SUMMARY("Vehicle Order Summary", "Vehicle Order Summary Document"),
	CLIENT_ORDER_CONFIRMATION("Client Order Confirmation", "Client Order Confirmation Document"),
	SCHEDULE_A("Schedule A", "Schedule A Document"), 
	AMORTIZATION_SCHEDULE("Amortization Schedule", "Amortization Schedule Document"),
	COURTESY_DELIVERY_INSTRUCTION("Courtesy Delivery Instruction", "Courtesy Delivery Instruction Document");
	
	private String name;
	private String description;
	
	private DocumentNameEnum(String code, String description){
		this.setName(code);
		this.setDescription(description);
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
