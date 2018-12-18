package com.mikealbert.data.entity;

import java.io.Serializable;
import javax.persistence.*;

import java.math.BigDecimal;


/**
 * The persistent class for the QUOTATION_STEP_STRUCTURE database table.
 * 
 */
@Entity
@Table(name="QUOTATION_STEP_STRUCTURE")
public class QuotationStepStructure extends BaseEntity implements Serializable {


	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private QuotationStepStructurePK id;
	
	@Column(name="TO_PERIOD", nullable=false, precision=30, scale=5)
	private BigDecimal toPeriod;

	 @MapsId("qmdQmdId")  //  maps the qmdQmdId attribute of embedded id
     @ManyToOne 
     @JoinColumn(name = "QMD_QMD_ID") 
     QuotationModel quotationModel;
	 
	public QuotationStepStructure() {
	}

	public QuotationStepStructurePK getId() {
		return this.id;
	}

	public void setId(QuotationStepStructurePK id) {
		this.id = id;
	}
	
	public BigDecimal getToPeriod() {
		return this.toPeriod;
	}

	public void setToPeriod(BigDecimal toPeriod) {
		this.toPeriod = toPeriod;
	}


	public QuotationModel getQuotationModel() {
		return quotationModel;
	}

	public void setQuotationModel(QuotationModel quotationModel) {
		this.quotationModel = quotationModel;
	}

	@Override
	public String toString() {
	    return "QuotationStepStructure [id=" + id + ", toPeriod=" + toPeriod + ", quotationModel=" + id.getQmdQmdId() + "]";
	}

	
}