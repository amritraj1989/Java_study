package com.mikealbert.data.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 * The persistent class for the PLATE_TYPE_CODES database table.
 * 
 */
@Entity
@Table(name = "PLATE_TYPE_CODES")
public class PlateTypeCode implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@NotNull
	@Column(name = "PLATE_TYPE_CODE", updatable=false, insertable=false)
	private String plateTypeCode;

	@Column(name = "DESCRIPTION", updatable=false, insertable=false)
	private String description;

	public String getPlateTypeCode() {
		return plateTypeCode;
	}

	public void setPlateTypeCode(String plateTypeCode) {
		this.plateTypeCode = plateTypeCode;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((plateTypeCode == null) ? 0 : plateTypeCode.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PlateTypeCode other = (PlateTypeCode) obj;
		if (plateTypeCode == null) {
			if (other.plateTypeCode != null)
				return false;
		} else if (!plateTypeCode.equals(other.plateTypeCode))
			return false;
		return true;
	}

}