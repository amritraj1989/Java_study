package com.mikealbert.data.enumeration;

/**
* Doc Property Enum.
* 
* <P>Enum of document WILLOW2K.SUPPLIER_CATEGORY_CODES </p> 
*  
*  
*/
public enum SupplierCategoryCodeEnum {	
	
	PUR_DLR("PUR_DLR", "Purchasing, ordering or delivery dealership"),	
	UPFITTER("UPFITTER", "Upfitter vendor");
	
	private String code;
	private String description;
	
	private SupplierCategoryCodeEnum(String code, String description){
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
