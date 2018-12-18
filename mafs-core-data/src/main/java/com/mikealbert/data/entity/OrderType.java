package com.mikealbert.data.entity;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;


/**
 * The persistent class for the ORDER_TYPES database table.
 * 
 */
@Entity
@Table(name="ORDER_TYPES")
public class OrderType extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@NotNull
	@Column(name="CODE")
	private String code;

	@Column(name="DESCRIPTION")
	private String description;

	//bi-directional many-to-one association to VehicleConfiguration
	@OneToMany(mappedBy="orderType")
	private List<VehicleConfiguration> vehicleConfigurations;

	public OrderType() {
	}

	public String getCode() {
		return this.code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<VehicleConfiguration> getVehicleConfigurations() {
		return this.vehicleConfigurations;
	}

	public void setVehicleConfigurations(List<VehicleConfiguration> vehicleConfigurations) {
		this.vehicleConfigurations = vehicleConfigurations;
	}

}