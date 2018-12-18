package com.mikealbert.data.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;


/**
 * The persistent class for the QUOTE_MODEL_PROPERTY_VALUES database table.
 * 
 */
@Entity
@Table(name="QUOTE_MODEL_PROPERTY_VALUES")
public class QuoteModelPropertyValue extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="QMPV_SEQ")    
    @SequenceGenerator(name="QMPV_SEQ", sequenceName="QMPV_SEQ", allocationSize=1)
    @Column(name = "QMPV_ID")
    private Long qmpvId;
    
	@Column(name="PROPERTY_VALUE")
	private String propertyValue;
	
	@JoinColumn(name = "QMD_QMD_ID", referencedColumnName = "QMD_ID")
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	private QuotationModel quotationModel;

	//bi-directional many-to-one association to DocProperty
	@ManyToOne
	@JoinColumn(name="QMP_QMP_ID")
	private QuoteModelProperty quoteModelProperty;

	public Long getQmpvId() {
		return qmpvId;
	}

	public void setQmpvId(Long qmpvId) {
		this.qmpvId = qmpvId;
	}

	public String getPropertyValue() {
		return propertyValue;
	}

	public void setPropertyValue(String propertyValue) {
		this.propertyValue = propertyValue;
	}

	public QuotationModel getQuotationModel() {
		return quotationModel;
	}

	public void setQuotationModel(QuotationModel quotationModel) {
		this.quotationModel = quotationModel;
	}
	
	public QuoteModelProperty getQuoteModelProperty() {
		return quoteModelProperty;
	}

	public void setQuoteModelProperty(QuoteModelProperty quoteModelProperty) {
		this.quoteModelProperty = quoteModelProperty;
	}


}