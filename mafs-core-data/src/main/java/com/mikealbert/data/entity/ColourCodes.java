package com.mikealbert.data.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Mapped to COLOUR_CODES table
 */
@Entity
@Table(name = "COLOUR_CODES")
public class ColourCodes implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6943797093352355881L;
	
	@Id
	@Column(name = "COLOUR_CODE")
	private String colourCode;
	
	@Column(name="DESCRIPTION")
	private String description;
	
	@Column(name="NEW_COLOUR_CODE")
	private String newColourCode;

	public String getColourCode() {
		return colourCode;
	}

	public void setColourCode(String colourCode) {
		this.colourCode = colourCode;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getNewColourCode() {
		return newColourCode;
	}

	public void setNewColourCode(String newColourCode) {
		this.newColourCode = newColourCode;
	}
}
