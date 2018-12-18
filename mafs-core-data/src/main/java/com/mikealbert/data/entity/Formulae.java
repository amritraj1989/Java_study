package com.mikealbert.data.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the FORMULAE database table.
 * 
 */
@Entity
public class Formulae implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="FOR_ID")
	private long forId;

	private String description;

	private String formulae;

	@Column(name="FORMULAE_TYPE")
	private String formulaeType;

    public Formulae() {
    }

	public long getForId() {
		return this.forId;
	}

	public void setForId(long forId) {
		this.forId = forId;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getFormulae() {
		return this.formulae;
	}

	public void setFormulae(String formulae) {
		this.formulae = formulae;
	}

	public String getFormulaeType() {
		return this.formulaeType;
	}

	public void setFormulaeType(String formulaeType) {
		this.formulaeType = formulaeType;
	}

}