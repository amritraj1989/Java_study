package com.mikealbert.data.vo;

import java.util.Date;

public class QuoteRequestSearchResultVO {
	
	private Long qrqId;
	private Long clientCId;
	private String clientAccountName;
	private String clientAccountCode;
	private String clientAccountType;
	private String requestType;
	private String requestor;
	private String assignedTo;
	private int percentCompleted;
	private String toleranceYN;
	private String requestStatus;
	private Date dateRequestSubmitted;
	private Date dueDate;
	private String vehicleDescription;
	private String requestSubStatus;
	private boolean vqPrinted;
	private boolean vqAccepted;
	private boolean poPaid;
	private boolean quoteAccepted;
	private Date completedDate;
	private Date closedDate;
		
	public Long getQrqId() {
		return qrqId;
	}
	public void setQrqId(Long qrqId) {
		this.qrqId = qrqId;
	}
	public String getClientAccountName() {
		return clientAccountName;
	}
	public void setClientAccountName(String clientAccountName) {
		this.clientAccountName = clientAccountName;
	}
	public String getClientAccountCode() {
		return clientAccountCode;
	}
	public void setClientAccountCode(String clientAccountCode) {
		this.clientAccountCode = clientAccountCode;
	}
	public String getClientAccountType() {
		return clientAccountType;
	}
	public void setClientAccountType(String clientAccountType) {
		this.clientAccountType = clientAccountType;
	}
	public Long getClientCId() {
		return clientCId;
	}
	public void setClientCId(Long clientCId) {
		this.clientCId = clientCId;
	}
	public String getRequestType() {
		return requestType;
	}
	public void setRequestType(String requestType) {
		this.requestType = requestType;
	}
	public String getRequestor() {
		return requestor;
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
	public int getPercentCompleted() {
		return percentCompleted;
	}
	public void setPercentCompleted(int percentCompleted) {
		this.percentCompleted = percentCompleted;
	}
	public Date getDateRequestSubmitted() {
		return dateRequestSubmitted;
	}
	public void setDateRequestSubmitted(Date dateRequestSubmitted) {
		this.dateRequestSubmitted = dateRequestSubmitted;
	}
	public String getToleranceYN() {
		return toleranceYN;
	}
	public void setToleranceYN(String toleranceYN) {
		this.toleranceYN = toleranceYN;
	}
	public Date getDueDate() {
		return dueDate;
	}
	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}
	public String getRequestStatus() {
		return requestStatus;
	}
	public void setRequestStatus(String requestStatus) {
		this.requestStatus = requestStatus;
	}
	public String getVehicleDescription() {
		return vehicleDescription;
	}
	public void setVehicleDescription(String vehicleDescription) {
		this.vehicleDescription = vehicleDescription;
	}
	public String getRequestSubStatus() {
		return requestSubStatus;
	}
	public void setRequestSubStatus(String requestSubStatus) {
		this.requestSubStatus = requestSubStatus;
	}
	public boolean isVqPrinted() {
		return vqPrinted;
	}
	public void setVqPrinted(boolean vqPrinted) {
		this.vqPrinted = vqPrinted;
	}
	public boolean isVqAccepted() {
		return vqAccepted;
	}
	public void setVqAccepted(boolean vqAccepted) {
		this.vqAccepted = vqAccepted;
	}
	public boolean isPoPaid() {
		return poPaid;
	}
	public void setPoPaid(boolean poPaid) {
		this.poPaid = poPaid;
	}
	public boolean isQuoteAccepted() {
		return quoteAccepted;
	}
	public void setQuoteAccepted(boolean quoteAccepted) {
		this.quoteAccepted = quoteAccepted;
	}
	public Date getCompletedDate() {
		return completedDate;
	}
	public void setCompletedDate(Date completedDate) {
		this.completedDate = completedDate;
	}
	public Date getClosedDate() {
		return closedDate;
	}
	public void setClosedDate(Date closedDate) {
		this.closedDate = closedDate;
	}
	
}
