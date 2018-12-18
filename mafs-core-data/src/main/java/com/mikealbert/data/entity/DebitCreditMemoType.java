package com.mikealbert.data.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;



/**
 * The persistent class for the DEBIT_CREDIT_MEMO_TYPES database table.
 * 
 */
@Entity
@Table(name="DEBIT_CREDIT_MEMO_TYPES")
public class DebitCreditMemoType implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="DCMT_ID")
	private Long dcmtId;
	
	@Column(name="DEBIT_CREDIT_TYPE")
	private String debitCreditType;

	@Column(name="TYPE_DESCRIPTION")
	private String typeDescription;

	public Long getDcmtId() {
		return dcmtId;
	}

	public void setDcmtId(Long dcmtId) {
		this.dcmtId = dcmtId;
	}

	public String getDebitCreditType() {
		return debitCreditType;
	}

	public void setDebitCreditType(String debitCreditType) {
		this.debitCreditType = debitCreditType;
	}

	public String getTypeDescription() {
		return typeDescription;
	}

	public void setTypeDescription(String typeDescription) {
		this.typeDescription = typeDescription;
	}	
	
}
