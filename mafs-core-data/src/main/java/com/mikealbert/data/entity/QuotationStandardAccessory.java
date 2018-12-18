package com.mikealbert.data.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;


/**
 * The persistent class for the QUOTATION_STANDARD_ACCESSORIES database table.
 * 
 */
@Entity
@Table(name="QUOTATION_STANDARD_ACCESSORIES")
public class QuotationStandardAccessory extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 8494767101981018078L;

	@Id
	@SequenceGenerator(name="QSA_SEQ_GEN", sequenceName="QSA_SEQ", allocationSize=1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="QSA_SEQ_GEN")
	@Column(name="QSA_ID")
	private Long qsaId;
	
	@Column(name="PRINT_YN")
	private String printYn;
	
	@JoinColumn(name = "QMD_QMD_ID", referencedColumnName = "QMD_ID")
    @ManyToOne(optional = false)
    private QuotationModel quotationModel;
	
	//bi-directional many-to-one association to StandardAccessory
	@ManyToOne
	@JoinColumn(name="SAC_SAC_ID")
	private StandardAccessory standardAccessory;

	public QuotationStandardAccessory() {
	}

	public Long getQsaId() {
		return this.qsaId;
	}

	public void setQsaId(Long qsaId) {
		this.qsaId = qsaId;
	}

	public String getPrintYn() {
		return this.printYn;
	}

	public void setPrintYn(String printYn) {
		this.printYn = printYn;
	}

	public QuotationModel getQuotationModel() {
		return quotationModel;
	}

	public void setQuotationModel(QuotationModel quotationModel) {
		this.quotationModel = quotationModel;
	}

	public StandardAccessory getStandardAccessory() {
		return this.standardAccessory;
	}

	public void setStandardAccessory(StandardAccessory standardAccessory) {
		this.standardAccessory = standardAccessory;
	}

}