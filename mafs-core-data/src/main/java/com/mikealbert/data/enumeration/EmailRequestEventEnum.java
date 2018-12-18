package com.mikealbert.data.enumeration;

/**
* Doc Property Enum.
* 
* <P>Enum of Event that triggered the email requests </p> 
*  
*  
*/
public enum EmailRequestEventEnum {	
	
	MAIN_PO("MAIN_PO", "Release of Unit's Purchase Order"),	
	COURTESY_DELIVERY_INSTRUCTION_DOC("COURTESY_DELIVERY_INSTRUCTION_DOC", "Generation of Unit's Courtesy Delivery Instruction"),
	THIRD_PARTY_PO("THIRD_PARTY_PO", "Release of Upfit Purchase Order"),
	LEASE_FINALIZATION_SCHEDULE_A("LEASE_FINALIZATION_SCHEDULE_A", "Lease has been finalized"),
	LEASE_FINALIZATION_AMORTIZATION("LEASE_FINALIZATION_AMORTIZATION", "Lease has been finalized");	
	
	private String code;
	private String description;
	
	private EmailRequestEventEnum(String code, String description){
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
