package com.mikealbert.data.enumeration;

public enum DocumentStatus {

	PURCHASE_ORDER_STATUS_OPEN("O", "Open"),
	PURCHASE_ORDER_STATUS_RELEASED("R", "Released"), 
	PURCHASE_ORDER_STATUS_CANCELED("C", "Cancelled"),
	INVOICE_ACCOUNTS_PAYABLE_STATUS_POSTED("P", "Posted"),
	INVOICE_ACCOUNTS_RECEIVABLE_STATUS_POSTED("P", "Posted");

	
	private String code;
	private String description;
	
	private DocumentStatus(String code, String description){
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
	
	public static String getDescriptionByCode(String inputCode) {
	    for(DocumentStatus ds : values()) {
	        if(ds.code.equals(inputCode))
	        	return ds.getDescription();
	    }
	    return null;
	 }
	
}
