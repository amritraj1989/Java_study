package com.mikealbert.data.entity;

import java.io.Serializable;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Fuel Grouping The persistent class for the FUEL_GROUPING database table. Used
 * for creating Master Schedule Rules.
 */
@Entity
@Table(name = "FUEL_GROUPING")
public class FuelGrouping extends BaseEntity implements Serializable {

	private static final long serialVersionUID = 2080399395815188282L;

	@EmbeddedId
    protected FuelGroupingPK fuelGroupingPK;
	
	
	@JoinColumn(name = "FUEL_GROUP_CODE", referencedColumnName = "FUEL_GROUP_CODE")
    @ManyToOne( fetch = FetchType.EAGER)
	private	FuelGroupCode	fuelGroupCode;


	
	public FuelGroupingPK getFuelGroupingPK() {
		return fuelGroupingPK;
	}

	public void setFuelGroupingPK(FuelGroupingPK fuelGroupingPK) {
		this.fuelGroupingPK = fuelGroupingPK;
	}
	

	
	public FuelGroupCode getFuelGroupCode() {
		return fuelGroupCode;
	}

	public void setFuelGroupCode(FuelGroupCode fuelGroupCode) {
		this.fuelGroupCode = fuelGroupCode;
	}

	@Override
	public String toString() {
		return "com.mikealbert.data.entity.FuelGrouping[ fuelTypeGroup =" + this.fuelGroupingPK.getFuelType().getFtvId() + " - " + this.fuelGroupingPK.getGroupKey() + " ]";
	}
}
