package com.mikealbert.data.vo;

import java.util.List;

public class ThirdPartyPoVO {

	private String logo;
	private String purchaseOrderTitle;
	private String purchaseOrderContactUsText;
	private PurchaseOrderVO purchaseOrderVO;
	private String logistics;
	private String acquisitionType;
	private String bailmentInstructions;
	private VehicleInfoVO vehicleInfoVO;	
	private DeliveringDealerInfoVO deliveringDealerInfoVO;
	private String driverName;
	private String driverPhone;
	private String driverAddress;
	private String driverAddress1;
	private String driverAddress2;
	private String driverCity;
	private String driverState;
	private String driverZip;
	private String driverCityStateZip;
	private String returningVehicle;
	private boolean returningVehicleIndicator;	
	private List<VendorInfoVO> vendorInfoVOList;
	private String purchaseOrderFooterText;
	

	public String getPurchaseOrderTitle() {
		return purchaseOrderTitle;
	}

	public void setPurchaseOrderTitle(String purchaseOrderTitle) {
		this.purchaseOrderTitle = purchaseOrderTitle;
	}

	public String getPurchaseOrderContactUsText() {
		return purchaseOrderContactUsText;
	}

	public void setPurchaseOrderContactUsText(String purchaseOrderContactUsText) {
		this.purchaseOrderContactUsText = purchaseOrderContactUsText;
	}

	public PurchaseOrderVO getPurchaseOrderVO() {
		return purchaseOrderVO;
	}

	public void setPurchaseOrderVO(PurchaseOrderVO purchaseOrderVO) {
		this.purchaseOrderVO = purchaseOrderVO;
	}

	public String getLogistics() {
		return logistics;
	}

	public void setLogistics(String logistics) {
		this.logistics = logistics;
	}

	public String getBailmentInstructions() {
		return bailmentInstructions;
	}

	public void setBailmentInstructions(String bailmentInstructions) {
		this.bailmentInstructions = bailmentInstructions;
	}

	public VehicleInfoVO getVehicleInfoVO() {
		return vehicleInfoVO;
	}

	public void setVehicleInfoVO(VehicleInfoVO vehicleInfoVO) {
		this.vehicleInfoVO = vehicleInfoVO;
	}

	public DeliveringDealerInfoVO getDeliveringDealerInfoVO() {
		return deliveringDealerInfoVO;
	}

	public void setDeliveringDealerInfoVO(
			DeliveringDealerInfoVO deliveringDealerInfoVO) {
		this.deliveringDealerInfoVO = deliveringDealerInfoVO;
	}

	public String getDriverName() {
		return driverName;
	}

	public void setDriverName(String driverName) {
		this.driverName = driverName;
	}

	public String getDriverAddress() {
		return driverAddress;
	}

	public void setDriverAddress(String driverAddress) {
		this.driverAddress = driverAddress;
	}

	public String getDriverCity() {
		return driverCity;
	}

	public void setDriverCity(String driverCity) {
		this.driverCity = driverCity;
	}

	public String getDriverState() {
		return driverState;
	}

	public void setDriverState(String driverState) {
		this.driverState = driverState;
	}

	public String getDriverZip() {
		return driverZip;
	}

	public void setDriverZip(String driverZip) {
		this.driverZip = driverZip;
	}

	public boolean isReturningVehicleIndicator() {
		return returningVehicleIndicator;
	}

	public void setReturningVehicleIndicator(boolean returningVehicleIndicator) {
		this.returningVehicleIndicator = returningVehicleIndicator;
	}

	public List<VendorInfoVO> getVendorInfoVOList() {
		return vendorInfoVOList;
	}

	public void setVendorInfoVOList(List<VendorInfoVO> vendorInfoVOList) {
		this.vendorInfoVOList = vendorInfoVOList;
	}

	public String getPurchaseOrderFooterText() {
		return purchaseOrderFooterText;
	}

	public void setPurchaseOrderFooterText(String purchaseOrderFooterText) {
		this.purchaseOrderFooterText = purchaseOrderFooterText;
	}

	public String getDriverCityStateZip() {
		return driverCityStateZip;
	}

	public void setDriverCityStateZip(String driverCityStateZip) {
		this.driverCityStateZip = driverCityStateZip;
	}

	public String getReturningVehicle() {
		return returningVehicle;
	}

	public void setReturningVehicle(String returningVehicle) {
		this.returningVehicle = returningVehicle;
	}

	public String getDriverAddress1() {
		return driverAddress1;
	}

	public void setDriverAddress1(String driverAddress1) {
		this.driverAddress1 = driverAddress1;
	}

	public String getDriverAddress2() {
		return driverAddress2;
	}

	public void setDriverAddress2(String driverAddress2) {
		this.driverAddress2 = driverAddress2;
	}

	public String getDriverPhone() {
		return driverPhone;
	}

	public void setDriverPhone(String driverPhone) {
		this.driverPhone = driverPhone;
	}

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	public String getAcquisitionType() {
		return acquisitionType;
	}

	public void setAcquisitionType(String acquisitionType) {
		this.acquisitionType = acquisitionType;
	}


}