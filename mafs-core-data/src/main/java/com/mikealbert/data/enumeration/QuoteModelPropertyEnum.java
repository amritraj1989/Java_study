package com.mikealbert.data.enumeration;

/**
* Quotation Model Property Enum.
* 
* <P>Enum of QuotationModelProperties</p> 
*  
*  
*/
public enum QuoteModelPropertyEnum {	
	

	SUPPLIER("SUPPLIER", "SUPPLIER"),
	ACQUISITION_TYPE("ACQUISITION_TYPE", "ACQUISITION_TYPE"),
	ORDERING_LEAD_TIME("ORDERING_LEAD_TIME", "ORDERING_LEAD_TIME"),	
	QMD_VIN("QMD_VIN", "QMD_VIN"),	
	FACTORY_NO("FACTORY_NO", "FACTORY_NO"),	
	OE_REV_ASSMT("OE_REV_ASSMT", "OE_REV_ASSMT"),	
	OE_REV_INT_ADJ("OE_REV_INT_ADJ", "OE_REV_INT_ADJ"),	
	OE_REV_ASSMT_INRATE_YN("OE_REV_ASSMT_INRATE_YN", "OE_REV_ASSMT_INRATE_YN"),	
	OE_REV_INT_ADJ_INRATE_YN("OE_REV_INT_ADJ_INRATE_YN", "OE_REV_INT_ADJ_INRATE_YN"),	
	QUOTE_TYPE("QUOTE_TYPE", "QUOTE_TYPE"),	
	CLOSED("QMD_VIN", "QMD_VIN"),
	INFORMAL_QUOTE_ID("INFORMAL_QUOTE_ID","INFORMAL_QUOTE_ID"),
	QUOTE_PROFILE("QUOTE_PROFILE", "QUOTE_PROFILE");	
	
	
	private String name;
	private String desc;
	
	private QuoteModelPropertyEnum(String name, String desc){
		this.setName(name);
		this.setDesc(desc);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	
	
	
}
