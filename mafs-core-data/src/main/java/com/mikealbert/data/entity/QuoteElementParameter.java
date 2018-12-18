package com.mikealbert.data.entity;

import java.io.Serializable;
import javax.persistence.*;
import java.math.BigDecimal;


/**
 * The persistent class for the QUOTE_ELEMENT_PARAMETERS database table.
 * 
 */
@Entity
@Table(name="QUOTE_ELEMENT_PARAMETERS")
public class QuoteElementParameter extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="QEP_SEQ")    
	@SequenceGenerator(name="QEP_SEQ", sequenceName="QEP_SEQ", allocationSize=1)  
	@Column(name="QEP_ID")
	private long qepId;

	@Column(name="FPR_FORMULA_PARAMETER")
	private BigDecimal fprFormulaParameter;

	@Column(name="PO_PARAMETER_OPTIONS")
	private BigDecimal poParameterOptions;

	//bi-directional many-to-one association to QuotationElement
    @ManyToOne
	@JoinColumn(name="QEL_ID")
	private QuotationElement quotationElement;

    public QuoteElementParameter() {
    }

	public long getQepId() {
		return this.qepId;
	}

	public void setQepId(long qepId) {
		this.qepId = qepId;
	}

	public BigDecimal getFprFormulaParameter() {
		return this.fprFormulaParameter;
	}

	public void setFprFormulaParameter(BigDecimal fprFormulaParameter) {
		this.fprFormulaParameter = fprFormulaParameter;
	}

	public BigDecimal getPoParameterOptions() {
		return this.poParameterOptions;
	}

	public void setPoParameterOptions(BigDecimal poParameterOptions) {
		this.poParameterOptions = poParameterOptions;
	}

	public QuotationElement getQuotationElement() {
		return this.quotationElement;
	}

	public void setQuotationElement(QuotationElement quotationElement) {
		this.quotationElement = quotationElement;
	}
	
}