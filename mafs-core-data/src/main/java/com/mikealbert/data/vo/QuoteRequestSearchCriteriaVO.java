package com.mikealbert.data.vo;

import java.util.Date;
import java.util.List;

public class QuoteRequestSearchCriteriaVO {
	private Long requestId;
	private String requestorNo;
	private String requestor;
	private String assignedTo;
	private String assignedToNo;
	private List<String> requestTypes;
	private List<String> requestStatuses;
	private String requestedClientCode;
	private String requestedClient;
	private Date dateSubmittedFrom;
	private Date dateSubmittedTo;
	private Date requestedEta;
	private String vehicleDescription;
		
	public QuoteRequestSearchCriteriaVO(){}

	public String getRequestor() {
		return requestor;
	}

	public Long getRequestId() {
		return requestId;
	}

	public void setRequestId(Long requestId) {
		this.requestId = requestId;
	}

	public void setRequestor(String requestor) {
		this.requestor = requestor;
	}

	public String getAssignedTo() {
		return assignedTo;
	}

	public void setAssignedTo(String assignedTo) {
		this.assignedTo = assignedTo;
	}

	public String getRequestedClientCode() {
		return requestedClientCode;
	}

	public void setRequestedClientCode(String requestedClientCode) {
		this.requestedClientCode = requestedClientCode;
	}

	public String getRequestedClient() {
		return requestedClient;
	}

	public void setRequestedClient(String requestedClient) {
		this.requestedClient = requestedClient;
	}

	public Date getDateSubmittedFrom() {
		return dateSubmittedFrom;
	}

	public void setDateSubmittedFrom(Date dateSubmittedFrom) {
		this.dateSubmittedFrom = dateSubmittedFrom;
	}

	public Date getDateSubmittedTo() {
		return dateSubmittedTo;
	}

	public void setDateSubmittedTo(Date dateSubmittedTo) {
		this.dateSubmittedTo = dateSubmittedTo;
	}

	public Date getRequestedEta() {
		return requestedEta;
	}

	public void setRequestedEta(Date requestedEta) {
		this.requestedEta = requestedEta;
	}

	public String getVehicleDescription() {
		return vehicleDescription;
	}

	public void setVehicleDescription(String vehicleDescription) {
		this.vehicleDescription = vehicleDescription;
	}

	public String getRequestorNo() {
		return requestorNo;
	}

	public void setRequestorNo(String requestorNo) {
		this.requestorNo = requestorNo;
	}

	public String getAssignedToNo() {
		return assignedToNo;
	}

	public void setAssignedToNo(String assignedToNo) {
		this.assignedToNo = assignedToNo;
	}

	public List<String> getRequestTypes() {
		return requestTypes;
	}

	public void setRequestTypes(List<String> requestTypes) {
		this.requestTypes = requestTypes;
	}

	public List<String> getRequestStatuses() {
		return requestStatuses;
	}

	public void setRequestStatuses(List<String> requestStatuses) {
		this.requestStatuses = requestStatuses;
	}


}
