package com.mikealbert.data.entity;

import java.io.Serializable;

import javax.persistence.*;
import javax.validation.constraints.NotNull;


/**
 * The persistent class for the FUEL_TYPE_VALUES database table.
 * 
 */
@Entity
@Table(name="FUEL_TYPE_VALUES")
public class FuelTypeValues extends BaseEntity implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = -5601051319213279261L;

	@Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="FTV_SEQ")    
    @SequenceGenerator(name="FTV_SEQ", sequenceName="FTV_SEQ", allocationSize=1)
    @Basic(optional = false)
    @NotNull
	@Column(name="FTV_ID")
	private Long ftvId;

	@Column(name="FUEL_TYPE")
	private String fuelType;

	public long getFtvId() {
		return ftvId;
	}

	public void setFtvId(Long ftvId) {
		this.ftvId = ftvId;
	}

	public String getFuelType() {
		return fuelType;
	}

	public void setFuelType(String fuelType) {
		this.fuelType = fuelType;
	}

	

}