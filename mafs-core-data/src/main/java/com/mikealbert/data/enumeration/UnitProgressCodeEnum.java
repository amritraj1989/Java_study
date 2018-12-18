package com.mikealbert.data.enumeration;

/**
* Vehicle Status Enum.
* 
* <P>Enum of unit/supplier progress types</p> 
*  
* </p>
*  @author sibley
*/
public enum UnitProgressCodeEnum {	
	
	VLO("01_VLO", "VLO Received Date"),
	REQ("02_REQ", "Customer Requested Date"),
	ORDER("03_ORDER", "Quotation Acceptance Date"),
	PO("04_PO", "Purchase Order Print Date"),
	CONFIRM("05_CONFIRM", "Order Confirmation Date"),
	PREF("06_PREF", "Preferenced Date"),
	PROD("07_PROD", "Production Date"),
	SHIP1("08_SHIP1", "Factory Ship Date"),
	INT_DLR("09_INT_DLR", "Interim Dealer Received Date"),
	PROD2("10_PROD2", "Production Date 2"),
	SHIP2("10_SHIP2", "Ship Date 2"),
	INT_DLR_11("11_INT_DLR", "Interim Dealer Received Date 2"),
	PROD3_11("11_PROD3", "Production Date 3"),
	SHIP3_11("11_SHIP3", "Ship Date 3"),
	INT_DLR_12("12_INT_DLR", "Interim Dealer Received Date 3"),
	PROD4_12("12_PROD4", "Production Date 4"),
	SHIP4_12("12_SHIP4", "Ship Date 4"),
	INT_DLR_13("13_INT_DLR", "Interim Dealer Received Date 4"),
	PROD5_13("13_PROD5", "Production Date 5"),
	SHIP5_13("13_SHIP5", "Ship Date 5"),
	ETA("14_ETA", "ETA Date"),
	DSMFGDV("15_DSMFGDV", "Desired Manufacturer Delivery Date"),
	UFCMPLT("15_UFCMPLT", "Upfit Complete Date"),	
	RECEIVD("15_RECEIVD", "Dealer Received Date"),
	VEHRDY("15_VEHRDY", "Vehicle Ready Date"),
	MSO_REC("16_MSO_REC", "MSO Received Date"),
	MSOSENT("17_MSOSENT", "MSO Sent Date"),
	INVOICE("18_INVOICE", "Invoice Date"),
	INV_REC("19_INV_REC", "Invoice Received Date"),
	INVPROC("20_INVPROC", "Invoice Processed Date"),
	VMPBILL("21_VMPBILL", "VMP Bill Date"),
	DRAFT("22_DRAFT", "Draft Date"),
	CHECK("23_CHECK", "Check Date"),	
	IN_SERV("24_IN-SERV", "In-Service Date"),
	EXPECTD("25_EXPECTD", "Vehicle Expected Date");
	
	private String code;
	private String description;
	
	private UnitProgressCodeEnum(String code, String description){
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
