package com.mikealbert.data.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Mapped to QUOTATION_STATUS_CODES table
 */
@Entity
@Table(name = "QUOTATION_STATUS_CODES")
public class QuotationStatusCodes implements Serializable {
	

	/**
	 * 
	 */
	private static final long serialVersionUID = -8673436232418937007L;

	@Id
	@Column(name = "QUOTATION_STATUS")
	private int quotationStatus;
	
	@Column(name = "DESCRIPTION")
	private String description;

	public int getQuotationStatus() {
		return quotationStatus;
	}

	public void setQuotationStatus(int quotationStatus) {
		this.quotationStatus = quotationStatus;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	
}
