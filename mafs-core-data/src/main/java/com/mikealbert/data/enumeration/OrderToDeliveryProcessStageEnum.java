package com.mikealbert.data.enumeration;

/**
* Process Enum.
* 
* <P>Enum of Order To Delivery Process Stages</p> 
*  
* <P>
* </p>
*  
*/
public enum OrderToDeliveryProcessStageEnum {	
	
	ACCEPTANCE("ACCEPTANCE", "Contains the quotes that have be submitted for acceptance", "QUOTATION_MODELS", "ACCEPTANCE_QUEUE_V"),
	CLIENT_FACING("CLIENT_FACING", "Contains quotes that require the client-facting department to contact the client", "QUOTATION_MODELS", "CLIENT_FACING_QUEUE_V"),
	UPFIT("UPFIT", "Contains accepted quotes for units that have upfits", "DOCS", "UPFIT_QUEUE_V"),
	IN_SERVICE("IN_SERVICE", "Contains accepted quotes for units that have upfits and can be placed in service", "DOCS", "IN_SERVICE_QUEUE_V"),
	MANUFACTURER("MANUFACTURER", "Contains the PO for units that are on order", "DOCS", "MANUFACTURER_PROGRESSS_QUEUE_V"),
	PO_RELEASE("PO_RELEASE", "Contains the quotes for PO processing", "QUOTATION_MODELS|DOCS", "PURCHASE_ORDER_RELEASE_QUEUE_V"),
	THIRD_PARTY_PO_RELEASE("THD_PTY_RELEASE", "Contains the Open Third Party PO", "DOCS", "THIRD_PARTY_PO_QUEUE_V");
	
	private String code;
	private String description;
	private String objectName;
	private String viewName;
	
	private OrderToDeliveryProcessStageEnum(String code, String description, String objectName, String viewName){
		setCode(code);
		setDescription(description);
		setObjectName(objectName);
		setViewName(viewName);
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

	public String getObjectName() {
		return objectName;
	}

	public void setObjectName(String objectName) {
		this.objectName = objectName;
	}

	public String getViewName() {
		return viewName;
	}

	public void setViewName(String viewName) {
		this.viewName = viewName;
	}
	
	
}
