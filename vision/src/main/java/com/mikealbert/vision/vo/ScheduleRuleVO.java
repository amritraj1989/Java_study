package com.mikealbert.vision.vo;

import com.mikealbert.data.entity.MaintScheduleRule;


public class ScheduleRuleVO {

	private MaintScheduleRule maintScheduleRule;
	private String masterScheduleDescription;
	private String makeDescription;
	private String fuelDescription;
	private String driveDescription;

	
	public MaintScheduleRule getMaintScheduleRule() {
		return maintScheduleRule;
	}
	public void setMaintScheduleRule(MaintScheduleRule maintScheduleRule) {
		this.maintScheduleRule = maintScheduleRule;
	}	
	public String getMasterScheduleDescription() {
		return masterScheduleDescription;
	}
	public void setMasterScheduleDescription(String masterScheduleDescription) {
		this.masterScheduleDescription = masterScheduleDescription;
	}
	public String getMakeDescription() {
		return makeDescription;
	}
	public void setMakeDescription(String makeDescription) {
		this.makeDescription = makeDescription;
	}
	public String getFuelDescription() {
		return fuelDescription;
	}
	public void setFuelDescription(String fuelDescription) {
		this.fuelDescription = fuelDescription;
	}
	public String getDriveDescription() {
		return driveDescription;
	}
	public void setDriveDescription(String driveDescription) {
		this.driveDescription = driveDescription;
	}
	
		


}
