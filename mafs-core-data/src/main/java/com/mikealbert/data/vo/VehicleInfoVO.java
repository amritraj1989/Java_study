package com.mikealbert.data.vo;

import java.util.List;

public class VehicleInfoVO {

	private String modelDesc;
	private String bolyColor;
	private String interiorColor;
	private String powertrain;
	private String standardEquipments;
	private String optionalEquipments;
	private String portInstalledEquipments;
	private List<EquipmentVO> optionalEquipmentList;
	private List<EquipmentVO> portInstalledOptionsList;

	public String getModelDesc() {
		return modelDesc;
	}

	public void setModelDesc(String modelDesc) {
		this.modelDesc = modelDesc;
	}

	public String getBolyColor() {
		return bolyColor;
	}

	public void setBolyColor(String bolyColor) {
		this.bolyColor = bolyColor;
	}

	public List<EquipmentVO> getOptionalEquipmentList() {
		return optionalEquipmentList;
	}

	public void setOptionalEquipmentList(List<EquipmentVO> optionalEquipmentList) {
		this.optionalEquipmentList = optionalEquipmentList;
	}

	public List<EquipmentVO> getPortInstalledOptionsList() {
		return portInstalledOptionsList;
	}

	public void setPortInstalledOptionsList(
			List<EquipmentVO> portInstalledOptionsList) {
		this.portInstalledOptionsList = portInstalledOptionsList;
	}

	public String getOptionalEquipments() {
		return optionalEquipments;
	}

	public void setOptionalEquipments(String optionalEquipments) {
		this.optionalEquipments = optionalEquipments;
	}

	public String getInteriorColor() {
		return interiorColor;
	}

	public void setInteriorColor(String interiorColor) {
		this.interiorColor = interiorColor;
	}

	public String getPortInstalledEquipments() {
		return portInstalledEquipments;
	}

	public void setPortInstalledEquipments(String portInstalledEquipments) {
		this.portInstalledEquipments = portInstalledEquipments;
	}

	public String getStandardEquipments() {
		return standardEquipments;
	}

	public void setStandardEquipments(String standardEquipments) {
		this.standardEquipments = standardEquipments;
	}

	public String getPowertrain() {
		return powertrain;
	}

	public void setPowertrain(String powertrain) {
		this.powertrain = powertrain;
	}

}