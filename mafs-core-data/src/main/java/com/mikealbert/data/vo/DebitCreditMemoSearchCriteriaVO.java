package com.mikealbert.data.vo;

import java.util.Date;

import com.mikealbert.data.entity.AnalysisCategory;
import com.mikealbert.data.enumeration.DebitCreditStatusEnum;

public class DebitCreditMemoSearchCriteriaVO {

	private Long dcNo;
	private String unitNo;
	private	AnalysisCategory analysisCategory;
	private String statusCode;
	private String clientAccountName;
	private Long clientCid;
	private String clientAccountType;
	private String clientAccountCode;
	private Date submittedSinceDate;
	private String selectedApprover;
	private String selectedApproverNameOrEmpNo;
	private String submitter;
	private String submitterNameOrEmpNo;
	private String approver;
	private String approverNameOrEmpNo;
	private String processor;
	private String processorNameOrEmpNo;
	private String allocator;
	private String allocatorNameOrEmpNo;
	private String ticketNo;
	
	public boolean isProcessedTransactionSearch(){
		if( statusCode != null && statusCode.equalsIgnoreCase(DebitCreditStatusEnum.STATUS_PROCESSED.getCode())){
			return true;
		}else{
			return false;
		}
    }

	public Long getDcNo() {
		return dcNo;
	}

	public void setDcNo(Long dcNo) {
		this.dcNo = dcNo;
	}

	public String getUnitNo() {
		return unitNo;
	}

	public void setUnitNo(String unitNo) {
		this.unitNo = unitNo;
	}

	public AnalysisCategory getAnalysisCategory() {
		return analysisCategory;
	}

	public void setAnalysisCategory(AnalysisCategory analysisCategory) {
		this.analysisCategory = analysisCategory;
	}

	public String getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}

	public String getClientAccountName() {
		return clientAccountName;
	}

	public void setClientAccountName(String clientAccountName) {
		this.clientAccountName = clientAccountName;
	}

	public Long getClientCid() {
		return clientCid;
	}

	public void setClientCid(Long clientCid) {
		this.clientCid = clientCid;
	}

	public String getClientAccountType() {
		return clientAccountType;
	}

	public void setClientAccountType(String clientAccountType) {
		this.clientAccountType = clientAccountType;
	}

	public String getClientAccountCode() {
		return clientAccountCode;
	}

	public void setClientAccountCode(String clientAccountCode) {
		this.clientAccountCode = clientAccountCode;
	}

	public Date getSubmittedSinceDate() {
		return submittedSinceDate;
	}

	public void setSubmittedSinceDate(Date submittedSinceDate) {
		this.submittedSinceDate = submittedSinceDate;
	}
	
	public String getSelectedApprover() {
		return selectedApprover;
	}

	public void setSelectedApprover(String selectedApprover) {
		this.selectedApprover = selectedApprover;
	}

	public String getSelectedApproverNameOrEmpNo() {
		return selectedApproverNameOrEmpNo;
	}

	public void setSelectedApproverNameOrEmpNo(
			String selectedApproverNameOrEmpNo) {
		this.selectedApproverNameOrEmpNo = selectedApproverNameOrEmpNo;
	}
	
	public String getSubmitter() {
		return submitter;
	}

	public void setSubmitter(String submitter) {
		this.submitter = submitter;
	}

	public String getSubmitterNameOrEmpNo() {
		return submitterNameOrEmpNo;
	}

	public void setSubmitterNameOrEmpNo(String submitterNameOrEmpNo) {
		this.submitterNameOrEmpNo = submitterNameOrEmpNo;
	}

	public String getApprover() {
		return approver;
	}

	public void setApprover(String approver) {
		this.approver = approver;
	}

	public String getApproverNameOrEmpNo() {
		return approverNameOrEmpNo;
	}

	public void setApproverNameOrEmpNo(String approverNameOrEmpNo) {
		this.approverNameOrEmpNo = approverNameOrEmpNo;
	}

	public String getProcessor() {
		return processor;
	}

	public void setProcessor(String processor) {
		this.processor = processor;
	}

	public String getProcessorNameOrEmpNo() {
		return processorNameOrEmpNo;
	}

	public void setProcessorNameOrEmpNo(String processorNameOrEmpNo) {
		this.processorNameOrEmpNo = processorNameOrEmpNo;
	}

	public String getAllocator() {
		return allocator;
	}

	public void setAllocator(String allocator) {
		this.allocator = allocator;
	}

	public String getAllocatorNameOrEmpNo() {
		return allocatorNameOrEmpNo;
	}

	public void setAllocatorNameOrEmpNo(String allocatorNameOrEmpNo) {
		this.allocatorNameOrEmpNo = allocatorNameOrEmpNo;
	}	

	@Override
	public String toString() {
		return "DebitCreditMemoSearchCriteriaVO [dcNo=" + dcNo + ", unitNo="
				+ unitNo + ", analysisCategory=" + analysisCategory
				+ ", statusCode=" + statusCode + ", clientAccountName="
				+ clientAccountName + ", clientCid=" + clientCid
				+ ", clientAccountType=" + clientAccountType
				+ ", clientAccountCode=" + clientAccountCode
				+ ", submittedSinceDate=" + submittedSinceDate
				+ ", selectedApprover=" + selectedApprover
				+ ", selectedApproverNameOrEmpNo="
				+ selectedApproverNameOrEmpNo + ", submitter=" + submitter
				+ ", submitterNameOrEmpNo=" + submitterNameOrEmpNo
				+ ", approver=" + approver + ", approverNameOrEmpNo="
				+ approverNameOrEmpNo + ", processor=" + processor
				+ ", processorNameOrEmpNo=" + processorNameOrEmpNo
				+ ", allocator=" + allocator + ", allocatorNameOrEmpNo="
				+ allocatorNameOrEmpNo + ", ticketNo=" + ticketNo + "]";
	}

	public String getTicketNo() {
		return ticketNo;
	}

	public void setTicketNo(String ticketNo) {
		this.ticketNo = ticketNo;
	}
	
	

}
