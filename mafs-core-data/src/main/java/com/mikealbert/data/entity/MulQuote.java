package com.mikealbert.data.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the MUL_QUOTE database table.
 * 
 */
@Entity
@Table(name="MUL_QUOTE")
public class MulQuote implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private MulQuotePK id;

	@Column(name="CHECK_IND")
	private String checkInd;

	@Column(name="DAC_DAC_ID")
	private java.math.BigDecimal dacDacId;

	private String description;

	//bi-directional many-to-one association to Quotation
    @ManyToOne
	@JoinColumn(name="QUO_QUO_ID")
	private Quotation quotation;

    public MulQuote() {
    }

	public MulQuotePK getId() {
		return this.id;
	}

	public void setId(MulQuotePK id) {
		this.id = id;
	}
	
	public String getCheckInd() {
		return this.checkInd;
	}

	public void setCheckInd(String checkInd) {
		this.checkInd = checkInd;
	}

	public java.math.BigDecimal getDacDacId() {
		return this.dacDacId;
	}

	public void setDacDacId(java.math.BigDecimal dacDacId) {
		this.dacDacId = dacDacId;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Quotation getQuotation() {
		return this.quotation;
	}

	public void setQuotation(Quotation quotation) {
		this.quotation = quotation;
	}
	
}