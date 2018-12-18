package com.mikealbert.vision.vo;

import com.mikealbert.data.entity.VehicleConfiguration;

public class QuoteRequestConfigVO {

	private VehicleConfiguration vehicleConfiguration;

	private boolean selected;

	public VehicleConfiguration getVehicleConfiguration() {
		return vehicleConfiguration;
	}

	public void setVehicleConfiguration(VehicleConfiguration vehicleConfiguration) {
		this.vehicleConfiguration = vehicleConfiguration;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}


}