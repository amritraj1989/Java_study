package com.mikealbert.data.vo;

import java.io.Serializable;

public class MaintenancePreferencesVO implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private String preferenceName;
	private String preferenceDescription;
	private String approval;
	private String maintPack;
	private String specialInstructions;
	
	public String getPreferenceName() {
		return preferenceName;
	}
	
	public void setPreferenceName(String preferenceName) {
		this.preferenceName = preferenceName;
	}
	
	public String getPreferenceDescription() {
		return preferenceDescription;
	}
	
	public void setPreferenceDescription(String preferenceDescription) {
		this.preferenceDescription = preferenceDescription;
	}
	
	public String getApproval() {
		return approval;
	}
	
	public void setApproval(String approval) {
		this.approval = approval;
	}
	
	public String getMaintPack() {
		return maintPack;
	}
	
	public void setMaintPack(String maintPack) {
		this.maintPack = maintPack;
	}
	
	public String getSpecialInstructions() {
		return specialInstructions;
	}
	
	public void setSpecialInstructions(String specialInstructions) {
		this.specialInstructions = specialInstructions;
	}

}
