package com.mikealbert.data.vo;

import java.util.Date;

public class QuoteVO {
	private String rowKey;
	private Long quoId;
	private Long quoteNo;
	private Long revisionNo;
	private Long qmdId;
	private String statusCode;
	private String statusDescription;
	private Date quoteDate;
	
	public String getRowKey() {
		return rowKey;
	}
	public void setRowKey(String rowKey) {
		this.rowKey = rowKey;
	}
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
	public Date getQuoteDate() {
		return quoteDate;
	}
	public void setQuoteDate(Date quoteDate) {
		this.quoteDate = quoteDate;
	}
}
