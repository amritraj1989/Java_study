package com.mikealbert.data.vo;

import com.mikealbert.data.entity.MaintenanceCode;

public class MaintenanceCodeVO {
	
	private Long mrqId;
	private MaintenanceCode maintenanceCode;
	
	public MaintenanceCode getMaintenanceCode() {
		return maintenanceCode;
	}
	public void setMaintenanceCode(MaintenanceCode maintenanceCode) {
		this.maintenanceCode = maintenanceCode;
	}
	public Long getMrqId() {
		return mrqId;
	}
	public void setMrqId(Long mrqId) {
		this.mrqId = mrqId;
	}

}
