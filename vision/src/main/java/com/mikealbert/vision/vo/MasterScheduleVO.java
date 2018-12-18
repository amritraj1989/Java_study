package com.mikealbert.vision.vo;

import com.mikealbert.data.entity.MasterSchedule;


public class MasterScheduleVO {

	private MasterSchedule masterSchedule;
	private Long vehicleCount;
	private Long ruleCount;
		

	public MasterSchedule getMasterSchedule() {
		return masterSchedule;
	}

	public void setMasterSchedule(MasterSchedule masterSchedule) {
		this.masterSchedule = masterSchedule;
	}
	
	public Long getRuleCount() {
		return ruleCount;
	}

	public void setRuleCount(Long ruleCount) {
		this.ruleCount = ruleCount;
	}

	public Long getVehicleCount() {
		return vehicleCount;
	}

	public void setVehicleCount(Long vehicleCount) {
		this.vehicleCount = vehicleCount;
	}

}
