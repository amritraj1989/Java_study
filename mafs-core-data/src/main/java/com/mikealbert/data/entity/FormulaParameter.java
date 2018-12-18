package com.mikealbert.data.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


/**
 * The persistent class for the FORMULA_PARAMETERS database table.
 * 
 */
@Entity
@Table(name="FORMULA_PARAMETERS")
public class FormulaParameter implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="FPR_ID")
	private long fprId;

	@Column(name="LEL_LEL_ID")
	private long lelLelId;

	@Column(name="PARAMETER_NAME")
	private String parameterName;

	@Column(name="PARAMETER_TYPE")
	private String parameterType;

	@Column(name="SEQUENCE_NO")
	private long sequenceNo;

	@Column(name="SHOW_FIELD")
	private String showField;

    public FormulaParameter() {
    }

	public long getFprId() {
		return this.fprId;
	}

	public void setFprId(long fprId) {
		this.fprId = fprId;
	}

	public long getLelLelId() {
		return this.lelLelId;
	}

	public void setLelLelId(long lelLelId) {
		this.lelLelId = lelLelId;
	}

	public String getParameterName() {
		return this.parameterName;
	}

	public void setParameterName(String parameterName) {
		this.parameterName = parameterName;
	}

	public String getParameterType() {
		return this.parameterType;
	}

	public void setParameterType(String parameterType) {
		this.parameterType = parameterType;
	}

	public long getSequenceNo() {
		return this.sequenceNo;
	}

	public void setSequenceNo(long sequenceNo) {
		this.sequenceNo = sequenceNo;
	}

	public String getShowField() {
		return this.showField;
	}

	public void setShowField(String showField) {
		this.showField = showField;
	}

}