package com.mikealbert.data.vo;

public class QuoteRequestQuoteModelVO {
	Long quoId;
	Long quoteNo;
	Long revisionNo;
	Long qmdId;
	String driverName;
	String modelDesc;
	Long contractPeriod;
	Long contractDistance;
	String quoteStatus;
	
	public Long getQuoId() {
		return quoId;
	}
	public void setQuoId(Long quoId) {
		this.quoId = quoId;
	}
	public Long getQuoteNo() {
		return quoteNo;
	}
	public void setQuoteNo(Long quoteNo) {
		this.quoteNo = quoteNo;
	}
	public Long getRevisionNo() {
		return revisionNo;
	}
	public void setRevisionNo(Long revisionNo) {
		this.revisionNo = revisionNo;
	}
	public Long getQmdId() {
		return qmdId;
	}
	public void setQmdId(Long qmdId) {
		this.qmdId = qmdId;
	}
	public String getDriverName() {
		return driverName;
	}
	public void setDriverName(String driverName) {
		this.driverName = driverName;
	}
	public String getModelDesc() {
		return modelDesc;
	}
	public void setModelDesc(String modelDesc) {
		this.modelDesc = modelDesc;
	}
	public Long getContractPeriod() {
		return contractPeriod;
	}
	public void setContractPeriod(Long contractPeriod) {
		this.contractPeriod = contractPeriod;
	}
	public Long getContractDistance() {
		return contractDistance;
	}
	public void setContractDistance(Long contractDistance) {
		this.contractDistance = contractDistance;
	}
	public String getQuoteStatus() {
		return quoteStatus;
	}
	public void setQuoteStatus(String quoteStatus) {
		this.quoteStatus = quoteStatus;
	}
	}
