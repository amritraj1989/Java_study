package com.mikealbert.data.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

@Embeddable
public class FuelGroupingPK implements Serializable {

	private static final long serialVersionUID = 1L;

	@JoinColumn(name = "FTV_FTV_ID", referencedColumnName = "FTV_ID")
	@ManyToOne(fetch = FetchType.EAGER)
	private FuelTypeValues fuelType;
	/*@NotNull
	@Column(name="FTV_FTV_ID")
	private	Long	fuelTypeId;*/

	@NotNull
	@Column(name = "GROUP_KEY")
	private String groupKey;

	

	

	public FuelTypeValues getFuelType() {
		return fuelType;
	}

	public void setFuelType(FuelTypeValues fuelType) {
		this.fuelType = fuelType;
	}

	public String getGroupKey() {
		return groupKey;
	}

	public void setGroupKey(String groupKey) {
		this.groupKey = groupKey;
	}

}
