package com.mikealbert.data.entity;

import java.io.Serializable;
import javax.persistence.*;
import java.math.BigDecimal;


/**
 * The persistent class for the PARAMETER_OPTIONS database table.
 * 
 */
@Entity
@Table(name="PARAMETER_OPTIONS")
public class ParameterOption implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="POX_ID")
	private long poxId;

	@Column(name="ALPHA_VALUE")
	private String alphaValue;

	private String description;

	@Column(name="FPR_ID")
	private BigDecimal fprId;

	@Column(name="NUMERIC_VALUE")
	private BigDecimal numericValue;

    public ParameterOption() {
    }

	public long getPoxId() {
		return this.poxId;
	}

	public void setPoxId(long poxId) {
		this.poxId = poxId;
	}

	public String getAlphaValue() {
		return this.alphaValue;
	}

	public void setAlphaValue(String alphaValue) {
		this.alphaValue = alphaValue;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public BigDecimal getFprId() {
		return this.fprId;
	}

	public void setFprId(BigDecimal fprId) {
		this.fprId = fprId;
	}

	public BigDecimal getNumericValue() {
		return this.numericValue;
	}

	public void setNumericValue(BigDecimal numericValue) {
		this.numericValue = numericValue;
	}

}