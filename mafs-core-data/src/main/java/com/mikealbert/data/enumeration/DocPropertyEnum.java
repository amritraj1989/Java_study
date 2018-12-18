package com.mikealbert.data.enumeration;

/**
* Doc Property Enum.
* 
* <P>Enum of document properties</p> 
*  
*  
*/
public enum DocPropertyEnum {	
	
	VENDOR_TASK_COMPLETED("VENDOR_TASK_COMPLETED", "Tasks for this vendor is completed."),
	DEALER_PRICE("DEALER_PRICE", "Dealer Price"),
	ACQUISITION_TYPE("ACQUISITION_TYPE", "Either Bailment/ePool/Financing-only"),
	PO_VIN("PO_VIN", "Purchase Order VIN"),
	PO_ORDERING_LEAD_TIME("PO_ORDERING_LEAD_TIME", "Purchase Order Lead TIme"),
	CONFIRM_DOC_DATE("CONFIRM_DOC_DATE", "The date when the Client Order Confirmation document was generated"),	
	CONFIRM_DOC_EMAIL_DATE("CONFIRM_DOC_EMAIL_DATE", "The date when the Client Order Confirmation document was emailed");
	
	private String code;
	private String description;
	
	private DocPropertyEnum(String code, String description){
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
