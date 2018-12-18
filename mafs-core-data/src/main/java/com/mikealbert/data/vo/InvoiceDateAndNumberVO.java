package com.mikealbert.data.vo;

import java.io.Serializable;
import java.util.Date;

public class InvoiceDateAndNumberVO implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private String docNo;
	private Date docDate;

	public String getDocNo() {
		return docNo;
	}
	public void setDocNo(String docNo) {
		this.docNo = docNo;
	}
	public Date getDocDate() {
		return docDate;
	}
	public void setDocDate(Date docDate) {
		this.docDate = docDate;
	}

}
