package com.mikealbert.data.vo;

public class EquipmentVO {
	private String description;

	public EquipmentVO(String description){
		setDescription(description);
	}
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
