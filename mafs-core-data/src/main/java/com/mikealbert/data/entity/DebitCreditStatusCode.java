package com.mikealbert.data.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;



/**
 * The persistent class for the DEBIT_CREDIT_STATUS_CODES database table.
 * 
 */
@Entity
@Table(name="DEBIT_CREDIT_STATUS_CODES")
public class DebitCreditStatusCode implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="DC_STATUS_CODE")
	private String dcStatusCode;
	
	@Column(name="STATUS_DESC")
	private String statusDesc;

	public String getDcStatusCode() {
		return dcStatusCode;
	}

	public void setDcStatusCode(String dcStatusCode) {
		this.dcStatusCode = dcStatusCode;
	}

	public String getStatusDesc() {
		return statusDesc;
	}

	public void setStatusDesc(String statusDesc) {
		this.statusDesc = statusDesc;
	}
		
}
