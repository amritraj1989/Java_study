package com.mikealbert.data.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;


/**
 * Mapped to QUOTATION_NOTES table
 */
@Entity
@Table(name = "QUOTATION_NOTES")
public class QuotationNotes implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8855658449476289866L;
	
	 @Id
	 @Column(name = "QNT_ID")
	 private Long qntId;

	// bi-directional many-to-one association to QuotationModel
	 @ManyToOne
	 @JoinColumn(name = "QMD_QMD_ID")
	 private QuotationModel quotationModel;

	 @Column(name = "QUOTE_NOTE")
	 private String quoteNotes;

	public Long getQntId() {
		return qntId;
	}

	public void setQntId(Long qntId) {
		this.qntId = qntId;
	}

	public String getQuoteNotes() {
		return quoteNotes;
	}

	public void setQuoteNotes(String quoteNotes) {
		this.quoteNotes = quoteNotes;
	}

	public QuotationModel getQuotationModel() {
		return quotationModel;
	}

	public void setQuotationModel(QuotationModel quotationModel) {
		this.quotationModel = quotationModel;
	}
	
}
