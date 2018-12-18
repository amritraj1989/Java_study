package com.mikealbert.data.enumeration;

/**
* List of MAFS Reports.
* 
* <P>Enum for MAFS reports</p> 
*  
*  
*/
public enum ReportNameEnum {
	
	CLIENT_ORDER_CONFIRMATION("clientOrderConfirmation", "FLPOR010", "Order Confirmation"),
	CLIENT_ORDER_CONFIRMATION_EMAILED("clientOrderConfirmationEmailed", "NONE", ""),
	MAIN_PURHCASE_ORDER("mainPoReport", "NONE", "Purchase Order"),
	COURTESY_DELIVERY_INSTRUCTION("courtesyDeliveryInstructions", "NONE", "Courtesy Delivery Instruction"),
	THIRD_PARTY_PURHCASE_ORDER("thirdPartyPoReport", "NONE", "Third Party Purchase Order"),
	VEHICLE_PURCHASE_ORDER_SUMMARY("vehicleOrderSummaryReport", "NONE", "Vehicle Order Summary"),
	PRINT_COVER_SHEET("printCoverSheet", "NONE", "Vehicle Order Summary Coversheet"),
	SCHEDULE_A("scheduleA", "FLPOR029", "Schedule A"),
	AMORTIZATION_SCHEDULE("amortizationSchedule", "FLPOR035", "Vehicle Lease Amortization Schedule"),
	OPEN_END_REVISION_DOCUMENT("oeConRevTerms", "NONE", "Open End Revision Document"),
	OPEN_END_REVISION_SCHEDULE_A("oeRevisionScheduleA", "NONE", "Open End Revision Schedule A"),
	OPEN_END_REVISION_AMORTIZATION("oeRevisionAmortizationSchedule", "NONE", "Open End Revision Amortization Schedule");
	
	private String fileName;
	private String moduleName;
	private String description;
	
	private ReportNameEnum(String fileName, String moduleName, String description){
		setFileName(fileName);
		setModuleName(moduleName);
		setDescription(description);
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getModuleName() {
		return moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}	
	
}
