package com.mikealbert.service.enumeration;


public enum OnbaseDocTypeEnum {
	
	TYPE_VENDOR_QUOTE("Vendor Quote", "Onbase Vendor Quote Doc Type"),
	TYPE_PURCHASE_ORDER("Lease/Unit Folder - Purchase Documents", "Purchase Order Related Docs"),
	TYPE_CLIENT_ORDER_CONFIRMATION("Lease/Unit Folder - Client Order Confirmation", "Client Order Confirmation Docs"),
	TYPE_QUOTE_REQUEST("Quote Request Document", "Quote Request Supporting Docs"),	
	TYPE_SCHEDULE_A("Lease/Unit Folder - Schedule A/Addenda", "Schedule A Doc"),
	TYPE_AMORTIZATION_SCHEDULE("Lease/Unit Folder - Amortization Schedule", "Amortization Schedule Doc"),
	TYPE_REGISTRATION_HISTORY("Registration History", "Registration History"),
	
	TYPE_SUB_MAIN_PURCHASE_ORDER("Main Purchase Order", "Main Purchase Order doc"),
	TYPE_SUB_THIRD_PARTY_PURCHASE_ORDER("Third Party Purchase Order", "Third Party Purchase Order doc"),
	TYPE_SUB_VEHICLE_ORDER_SUMMARY("Vehicle Order Summary", "Vehicle Order Summary doc"),
	TYPE_SUB_COURTESY_DELIVERY_INSTRUCTION("Courtesy Delivery Instruction", "Courtesy Delivery Instruction doc");

	private String value;
	private String description;
	
	private OnbaseDocTypeEnum(String value, String description){
		this.setValue(value);
		this.setDescription(description);
	}

	public OnbaseDocTypeEnum getOnbaseDocTypeEnum(String value){
		for(OnbaseDocTypeEnum doctype : values()){
			if(doctype.getValue().equals(value)){
				return doctype;
			}
		}
		throw new IllegalArgumentException();
	}
	

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
