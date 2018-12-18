package com.mikealbert.data.vo;

import java.util.List;

public class VehicleConfigurationSearchResultVO {

	private Long vehicleConfigId;
	private Long vehicleConfigGroupingId;
	private String clientName;
	private String configDescription;
	private List<VehicleConfigModelVO> vehicleConfigModelVOs;
	private List<String> vendorName;
	private String locateFlag;
	private String vehConfingObsoleteInd;

	public Long getVehicleConfigId() {
		return vehicleConfigId;
	}

	public void setVehicleConfigId(Long vehicleConfigId) {
		this.vehicleConfigId = vehicleConfigId;
	}

	public Long getVehicleConfigGroupingId() {
		return vehicleConfigGroupingId;
	}

	public void setVehicleConfigGroupingId(Long vehicleConfigGroupingId) {
		this.vehicleConfigGroupingId = vehicleConfigGroupingId;
	}

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	public String getConfigDescription() {
		return configDescription;
	}

	public void setConfigDescription(String configDescription) {
		this.configDescription = configDescription;
	}

	public List<VehicleConfigModelVO> getVehicleConfigModelVOs() {
		return vehicleConfigModelVOs;
	}

	public void setVehicleConfigModelVOs(List<VehicleConfigModelVO> vehicleConfigModelVOs) {
		this.vehicleConfigModelVOs = vehicleConfigModelVOs;
	}

	public List<String> getVendorName() {
		return vendorName;
	}

	public void setVendorName(List<String> vendorName) {
		this.vendorName = vendorName;
	}

	public String getLocateFlag() {
		return locateFlag;
	}

	public void setLocateFlag(String locateFlag) {
		this.locateFlag = locateFlag;
	}

	public String getVehConfingObsoleteInd() {
		return vehConfingObsoleteInd;
	}

	public void setVehConfingObsoleteInd(String vehConfingObsoleteInd) {
		this.vehConfingObsoleteInd = vehConfingObsoleteInd;
	}

}
