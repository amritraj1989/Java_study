package com.mikealbert.data.vo;

import java.util.Date;

public class ActiveQuoteVO {

	private long qmdId;
	private String quoteDesc;
	private String status;
	private String originator;
	private Date quoteDate;
	public long getQmdId() {
		return qmdId;
	}
	public void setQmdId(long qmdId) {
		this.qmdId = qmdId;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getOriginator() {
		return originator;
	}
	public void setOriginator(String originator) {
		this.originator = originator;
	}
	public Date getQuoteDate() {
		return quoteDate;
	}
	public void setQuoteDate(Date quoteDate) {
		this.quoteDate = quoteDate;
	}
	public String getQuoteDesc() {
		return quoteDesc;
	}
	public void setQuoteDesc(String quoteDesc) {
		this.quoteDesc = quoteDesc;
	}
	
	
	
	
	
}
