package com.mikealbert.data.vo;

import java.io.Serializable;

public class VendorInvoiceErrorsVO implements Serializable {
	private static final long serialVersionUID = 3110107987200544867L;
	
	private String recordType;
	private String errorFieldName;  
	private String errorDesc; 

	public String getRecordType() {
		return recordType;
	}
	public void setRecordType(String recordType) {
		this.recordType = recordType;
	}
	public String getErrorFieldName() {
		return errorFieldName;
	}
	public void setErrorFieldName(String errorFieldName) {
		this.errorFieldName = errorFieldName;
	}
	public String getErrorDesc() {
		return errorDesc;
	}
	public void setErrorDesc(String errorDesc) {
		this.errorDesc = errorDesc;
	}
	
}
