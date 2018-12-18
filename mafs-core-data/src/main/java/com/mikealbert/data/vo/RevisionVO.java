package com.mikealbert.data.vo;

import java.math.BigDecimal;
import java.util.Date;


public class RevisionVO {
	private Long quoId;
	private Long qmdId;
	private Long contractPeriod;
	private Long contractDistance;
	private Long quoteNo;
	private Long revisionNo;
	private String statusCode;
	private String statusDescription;
	private Date printedDate;
	private Date revisionDate;
	private BigDecimal finalNBV;
	private BigDecimal depreciationFactor;
	
	public Long getQuoId() {
		return quoId;
	}
	public void setQuoId(Long quoId) {
		this.quoId = quoId;
	}
	public Long getQmdId() {
		return qmdId;
	}
	public void setQmdId(Long qmdId) {
		this.qmdId = qmdId;
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
	public String getStatusCode() {
		return statusCode;
	}
	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}
	public String getStatusDescription() {
		return statusDescription;
	}
	public void setStatusDescription(String statusDescription) {
		this.statusDescription = statusDescription;
	}
	public Date getPrintedDate() {
		return printedDate;
	}
	public void setPrintedDate(Date printedDate) {
		this.printedDate = printedDate;
	}
	public Date getRevisionDate() {
		return revisionDate;
	}
	public void setRevisionDate(Date revisionDate) {
		this.revisionDate = revisionDate;
	}
	public BigDecimal getFinalNBV() {
		return finalNBV;
	}
	public void setFinalNBV(BigDecimal finalNBV) {
		this.finalNBV = finalNBV;
	}
	public BigDecimal getDepreciationFactor() {
		return depreciationFactor;
	}
	public void setDepreciationFactor(BigDecimal depreciationFactor) {
		this.depreciationFactor = depreciationFactor;
	}

	

}
