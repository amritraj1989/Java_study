package com.mikealbert.service.enumeration;


public enum OnbaseIndexEnum {
	
	
	UPLOAD_ID("Upload ID", "upload ID which is id of ONBASE_UPLOADED_DOCS"),
	FILE_EXT("File Ext", "File Ext of uploaded file"),
	
	VENDOR_NAME("Vendor Name", "Vendor Name of uploaded file"),
	VENDOR_CODE("Vendor Code", "Vendor Code of uploaded file"),
	VENDOR_QUOTE_NO("Vendor Quote Number", "Vendor Quote Number  of uploaded file"),
	QUOTE_DATE("Vendor Quote date", "Vendor Quote created date"),
	
	UNIT_NO("Unit #", "Unit no of uploaded file"),
	VIN_LAST_8("VIN Last 8", "Unit no of uploaded file"),
	CORPORATE_NO("Corporate #", "Corporate no of uploaded file"),
	CORPORATE_NAME("Corporate Name", "Corporate name of uploaded file"),	
	CUSTOMER_NO("Customer #", "Customer no of uploaded file"),
	CUSTOMER_NAME("Customer Name", "Customer name of uploaded file"),
	VIN("VIN", "VIN of uploaded file"),
	
	QUOTE_REQUEST_TYPE("Quote Request Type", "Quote request type");
	
	 
	 
	private String name;
	private String description;
	
	private OnbaseIndexEnum(String name, String description){
		this.setName(name);
		this.setDescription(description);
	}

	public OnbaseIndexEnum getOnbaseDocTypeEnum(String value){
		for(OnbaseIndexEnum doctype : values()){
			if(doctype.getName().equals(value)){
				return doctype;
			}
		}
		throw new IllegalArgumentException();
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
