package com.mikealbert.data.enumeration;

public enum MaintenanceRequestStatusEnum {

	MAINT_REQUEST_STATUS_PREAUTHORIZED("P", "Preauthorized"),
	MAINT_REQUEST_STATUS_SCHEDULED("S", "Scheduled"),	
    MAINT_REQUEST_STATUS_BOOKED_IN("B", "Booked-In"),	
    MAINT_REQUEST_STATUS_WAITING_ON_CLIENT_APPROVAL("WCA", "Waiting on Client Approval"),
    MAINT_REQUEST_STATUS_INPROGRESS("I", "In-Progress"),
    MAINT_REQUEST_STATUS_WAIT_ON_INVOICE("WI", "Waiting on Invoice"),
    MAINT_REQUEST_STATUS_COMPLETE("C", "Completed"),	
    MAINT_REQUEST_STATUS_CLOSED_NO_PO("CNP", "Closed No PO"),
    MAINT_REQUEST_STATUS_GOODWILL_PENDING("GP", "Goodwill Pending"); 
    
	private String code;
	private String description;
	
	private MaintenanceRequestStatusEnum(String code, String description){
		setCode(code);
		setDescription(description);
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
