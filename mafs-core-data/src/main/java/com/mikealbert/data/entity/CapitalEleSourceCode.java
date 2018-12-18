package com.mikealbert.data.entity;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;


/**
 * The persistent class for the CAPITAL_ELE_SOURCE_CODES database table.
 * @author Singh
 */
@Entity
@Table(name="CAPITAL_ELE_SOURCE_CODES")
public class CapitalEleSourceCode extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="SOURCE_CODE", unique=true, nullable=false, length=10)
	private String sourceCode;

	@Column(nullable=false, length=100)
	private String description;

	//bi-directional many-to-one association to QuotationCapitalElement
	@OneToMany(mappedBy="capitalEleSourceCode")
	private List<QuotationCapitalElement> quotationCapitalElements;

    public CapitalEleSourceCode() {
    }

	public String getSourceCode() {
		return this.sourceCode;
	}

	public void setSourceCode(String sourceCode) {
		this.sourceCode = sourceCode;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<QuotationCapitalElement> getQuotationCapitalElements() {
		return this.quotationCapitalElements;
	}

	public void setQuotationCapitalElements(List<QuotationCapitalElement> quotationCapitalElements) {
		this.quotationCapitalElements = quotationCapitalElements;
	}
	
}