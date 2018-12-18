package com.mikealbert.data.vo;


public class DebitCreditInvoiceSearchCriteriaVO {

	private Long fmsId;
	private String accountCode;
	private Long analysisCategoryId;
	private String analysisCategory;
	private String analysisCode;
	private Integer invoiceOldInMonth;
	private String status; //doc/l status 

	public Long getFmsId() {
		return fmsId;
	}
	
	public void setFmsId(Long fmsId) {
		this.fmsId = fmsId;
	}

	public String getAccountCode() {
		return accountCode;
	}

	public Long getAnalysisCategoryId() {
		return analysisCategoryId;
	}

	public void setAnalysisCategoryId(Long analysisCategoryId) {
		this.analysisCategoryId = analysisCategoryId;
	}	
	
	public void setAccountCode(String accountCode) {
		this.accountCode = accountCode;
	}

	public String getAnalysisCategory() {
		return analysisCategory;
	}

	public void setAnalysisCategory(String analysisCategory) {
		this.analysisCategory = analysisCategory;
	}

	public String getAnalysisCode() {
		return analysisCode;
	}

	public void setAnalysisCode(String analysisCode) {
		this.analysisCode = analysisCode;
	}

	public Integer getInvoiceOldInMonth() {
		return invoiceOldInMonth;
	}

	public void setInvoiceOldInMonth(Integer invoiceOldInMonth) {
		this.invoiceOldInMonth = invoiceOldInMonth;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "DebitCreditInvoiceSearchCriteriaVO [fmsId=" + fmsId
				+ ", accountCode=" + accountCode + ", analysisCategoryId="
				+ analysisCategoryId + ", analysisCategory=" + analysisCategory
				+ ", analysisCode=" + analysisCode + ", invoiceOldInMonth="
				+ invoiceOldInMonth + ", status=" + status + "]";
	}
	
}
