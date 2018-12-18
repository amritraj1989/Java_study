package com.mikealbert.data.vo;

import java.io.Serializable;
import java.util.Date;

import com.mikealbert.data.entity.DriverCostCenter;


public class DriverCostCenterVO implements Serializable {

	private static final long serialVersionUID = -3870112885630573940L;

	private DriverCostCenter driverCostCenter;
	private String costCenterDescription;

	public DriverCostCenter getDriverCostCenter() {
		return driverCostCenter;
	}
	public void setDriverCostCenter(DriverCostCenter driverCostCenter) {
		this.driverCostCenter = driverCostCenter;
	}
	public String getCostCenterDescription() {
		return costCenterDescription;
	}
	public void setCostCenterDescription(String costCenterDescription) {
		this.costCenterDescription = costCenterDescription;
	}	

	
}
