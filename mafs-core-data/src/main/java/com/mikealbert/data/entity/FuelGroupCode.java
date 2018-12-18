package com.mikealbert.data.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "FUEL_GROUP_CODES")
public class FuelGroupCode extends BaseEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "FUEL_GROUP_CODE")
	private String fuelGroupCode;

	@Column(name = "FUEL_GROUP_DESCRIPTION")
	private String fuelGroupDescription;

	public String getFuelGroupCode() {
		return fuelGroupCode;
	}

	public void setFuelGroupCode(String fuelGroupCode) {
		this.fuelGroupCode = fuelGroupCode;
	}

	public String getFuelGroupDescription() {
		return fuelGroupDescription;
	}

	public void setFuelGroupDescription(String fuelGroupDescription) {
		this.fuelGroupDescription = fuelGroupDescription;
	}

}
