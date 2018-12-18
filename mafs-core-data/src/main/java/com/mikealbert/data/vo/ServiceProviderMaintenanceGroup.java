package com.mikealbert.data.vo;

import java.math.BigDecimal;

public class ServiceProviderMaintenanceGroup {
	private BigDecimal 	id;
	private String 		description;

	public ServiceProviderMaintenanceGroup() {}

	public BigDecimal getId() {
		return id;
	}

	public void setId(BigDecimal id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
