package com.mikealbert.service.vo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.mikealbert.data.util.DisplayFormatHelper;
import com.mikealbert.util.MALUtilities;

public class VehiclePurchaseOrderVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7155094889921264972L;
	
	private Long qmdId;
	private Long docId;
	private Long quoId;
	private Long makId;
	private Long psoId;//stage id in process stage 
	private Long contextId;
	private String vehicleDescription;
	private String poNumber;
	private String unitNumber;
	private Long fmsId;
	private String requestedDate;
	private Date releasedDate;
	private String deliveryAddressLine1;
	private String deliveryAddressLine2;
	private String townCity;
	private String region;
	private String postalCode;
	private String country;
	private String orderingDealerCode;
	private String orderingDealerName;
	private String orderingDealerEmail;
	private Long orderingDealerId;
	private String deliveringDealerCode;
	private String deliveringDealerName;
	private String deliveringDealerEmail;
	private Long deliveringDealerId;
	private String vin;
	private String color;
	private String returningUnit;
	private String factoryOrderNumber;
	private Long numOfThdPtyPos;
	private String totalCost;
	private String logisticNotes;
	private String poLogisticNotes;
	private String trim;
	private String acquisitionType;
	private String stockYN;
	private String orderType;
	private String poStatus;
	private boolean driverAddressUpdated;
	private Long orderingLeadTime;
	private String standardDeliveryLeadTime;
	private int modelYear;
	private String makeDesc;
	private String oldOrderingDealerCode;
	private String oldDeliveringDealerCode;
	private String oldVin;
	
	private List<String> optionalAccessories;
	private List<String> portInstalledAccessories;
	private List<String> dealerInstalledAccessories;
	private List<String> powertrainInfo;
	
	public String getPoNumber() {
		return poNumber;
	}
	public void setPoNumber(String poNumber) {
		this.poNumber = poNumber;
	}
	public String getUnitNumber() {
		return unitNumber;
	}
	public void setUnitNumber(String unitNumber) {
		this.unitNumber = unitNumber;
	}
	
	public Long getFmsId() {
		return fmsId;
	}
	public void setFmsId(Long fmsId) {
		this.fmsId = fmsId;
	}
	public String getRequestedDate() {
		return requestedDate;
	}
	public void setRequestedDate(String requestedDate) {
		this.requestedDate = requestedDate;
	}
	public String getVin() {
		return vin;
	}
	public void setVin(String vin) {
		this.vin = vin;
	}
	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
	}
	public String getReturningUnit() {
		return returningUnit;
	}
	public void setReturningUnit(String returningUnit) {
		this.returningUnit = returningUnit;
	}
	public String getFactoryOrderNumber() {
		return factoryOrderNumber;
	}
	public void setFactoryOrderNumber(String factoryOrderNumber) {
		this.factoryOrderNumber = factoryOrderNumber;
	}
	public Long getNumOfThdPtyPos() {
		return numOfThdPtyPos;
	}
	public void setNumOfThdPtyPos(Long numOfThdPtyPos) {
		this.numOfThdPtyPos = numOfThdPtyPos;
	}
	public String getVehicleDescription() {
		return vehicleDescription;
	}
	public void setVehicleDescription(String vehicleDescription) {
		this.vehicleDescription = vehicleDescription;
	}
	public Date getReleasedDate() {
		return releasedDate;
	}
	public void setReleasedDate(Date releasedDate) {
		this.releasedDate = releasedDate;
	}
	public String getDeliveryAddressLine1() {
		return deliveryAddressLine1;
	}
	public void setDeliveryAddressLine1(String deliveryAddressLine1) {
		this.deliveryAddressLine1 = deliveryAddressLine1;
	}
	public String getDeliveryAddressLine2() {
		return deliveryAddressLine2;
	}
	public void setDeliveryAddressLine2(String deliveryAddressLine2) {
		this.deliveryAddressLine2 = deliveryAddressLine2;
	}
	public String getTownCity() {
		return townCity;
	}
	public void setTownCity(String townCity) {
		this.townCity = townCity;
	}
	public String getRegion() {
		return region;
	}
	public void setRegion(String region) {
		this.region = region;
	}
	public String getPostalCode() {
		return postalCode;
	}
	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}
	public String getOrderingDealerCode() {
		return orderingDealerCode;
	}
	public void setOrderingDealerCode(String orderingDealerCode) {
		this.orderingDealerCode = orderingDealerCode;
	}
	public String getOrderingDealerName() {
		return orderingDealerName;
	}
	public void setOrderingDealerName(String orderingDealerName) {
		this.orderingDealerName = orderingDealerName;
	}
	public String getDeliveringDealerCode() {
		return deliveringDealerCode;
	}
	public void setDeliveringDealerCode(String deliveringDealerCode) {
		this.deliveringDealerCode = deliveringDealerCode;
	}
	public String getDeliveringDealerName() {
		return deliveringDealerName;
	}
	public void setDeliveringDealerName(String deliveringDealerName) {
		this.deliveringDealerName = deliveringDealerName;
	}
	
	public Long getQmdId() {
		return qmdId;
	}
	public void setQmdId(Long qmdId) {
		this.qmdId = qmdId;
	}
	public Long getDocId() {
		return docId;
	}
	public void setDocId(Long docId) {
		this.docId = docId;
	}
	public Long getQuoId() {
		return quoId;
	}
	public void setQuoId(Long quoId) {
		this.quoId = quoId;
	}
	public String getTotalCost() {
		return totalCost;
	}
	public void setTotalCost(String totalCost) {
		this.totalCost = totalCost;
	}
	public String getLogisticNotes() {
		return logisticNotes;
	}
	public void setLogisticNotes(String logisticNotes) {
		this.logisticNotes = logisticNotes;
	}
	public Long getContextId() {
		return contextId;
	}
	public void setContextId(Long contextId) {
		this.contextId = contextId;
	}
	public String getTrim() {
		return trim;
	}
	public void setTrim(String trim) {
		this.trim = trim;
	}
	public String getFullDeliveryAddress() {
		return DisplayFormatHelper.formatAddressForTable(null,getDeliveryAddressLine1(), getDeliveryAddressLine2(), null, null, getTownCity(), getRegion(), getPostalCode(), "<br/>");
	}
	public String getAcquisitionType() {
		return acquisitionType;
	}
	public void setAcquisitionType(String acquisitionType) {
		this.acquisitionType = acquisitionType;
	}
	public String getStockYN() {
		return stockYN;
	}
	public void setStockYN(String stockYN) {
		this.stockYN = stockYN;
	}
	public String getOrderType() {
		return orderType;
	}
	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}
	public String getPoStatus() {
		return poStatus;
	}
	public void setPoStatus(String poStatus) {
		this.poStatus = poStatus;
	}
	public boolean isDriverAddressUpdated() {
		return driverAddressUpdated;
	}
	public void setDriverAddressUpdated(boolean driverAddressUpdated) {
		this.driverAddressUpdated = driverAddressUpdated;
	}
	public Long getOrderingLeadTime() {
		return orderingLeadTime;
	}
	public void setOrderingLeadTime(Long orderingLeadTime) {
		this.orderingLeadTime = orderingLeadTime;
	}	
	public String getStandardDeliveryLeadTime() {
		return standardDeliveryLeadTime;
	}
	public void setStandardDeliveryLeadTime(String standardDeliveryLeadTime) {
		this.standardDeliveryLeadTime = standardDeliveryLeadTime;
	}
	public int getModelYear() {
		return modelYear;
	}
	public void setModelYear(int modelYear) {
		this.modelYear = modelYear;
	}
	public String getMakeDesc() {
		return makeDesc;
	}
	public void setMakeDesc(String makeDesc) {
		this.makeDesc = makeDesc;
	}
	
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getOldOrderingDealerCode() {
		return oldOrderingDealerCode;
	}
	public void setOldOrderingDealerCode(String oldOrderingDealerCode) {
		this.oldOrderingDealerCode = oldOrderingDealerCode;
	}
	public String getOldDeliveringDealerCode() {
		return oldDeliveringDealerCode;
	}
	public void setOldDeliveringDealerCode(String oldDeliveringDealerCode) {
		this.oldDeliveringDealerCode = oldDeliveringDealerCode;
	}
	public String getOldVin() {
		return oldVin;
	}
	public void setOldVin(String oldVin) {
		this.oldVin = oldVin;
	}
	public Long getOrderingDealerId() {
		return orderingDealerId;
	}
	public void setOrderingDealerId(Long orderingDealerId) {
		this.orderingDealerId = orderingDealerId;
	}
	public Long getDeliveringDealerId() {
		return deliveringDealerId;
	}
	public void setDeliveringDealerId(Long deliveringDealerId) {
		this.deliveringDealerId = deliveringDealerId;
	}
	public boolean isOrderingDealerChanged() {
		return ! MALUtilities.getNullSafeString(getOldOrderingDealerCode()).equals(MALUtilities.getNullSafeString(getOrderingDealerCode()));
	}	
	public boolean isDeliveringDealerChanged() {
		return ! MALUtilities.getNullSafeString(getOldDeliveringDealerCode()).equals(MALUtilities.getNullSafeString(getDeliveringDealerCode()));
	}	
	public boolean isVinChanged() {
		return ! MALUtilities.getNullSafeString(getVin()).equals(MALUtilities.getNullSafeString(getOldVin()));
	}	
	public Long getMakId() {
		return makId;
	}
	public void setMakId(Long makId) {
		this.makId = makId;
	}	
	public Long getPsoId() {
		return psoId;
	}
	public void setPsoId(Long psoId) {
		this.psoId = psoId;
	}
	public String getOrderingDealerEmail() {
		return orderingDealerEmail;
	}
	public void setOrderingDealerEmail(String orderingDealerEmail) {
		this.orderingDealerEmail = orderingDealerEmail;
	}
	public String getDeliveringDealerEmail() {
		return deliveringDealerEmail;
	}
	public void setDeliveringDealerEmail(String deliveringDealerEmail) {
		this.deliveringDealerEmail = deliveringDealerEmail;
	}	
	
	@Override
	public String toString() {
		return "MaintainPurchaseOrderVO [qmdId=" + qmdId + ", docId=" + docId
				+ ", poNumber=" + poNumber + ", unitNumber=" + unitNumber
				+ ", fmsId=" + fmsId + ", stockYN=" + stockYN + ", orderType="
				+ orderType + ", poStatus=" + poStatus + "]";
	}
	public List<String> getOptionalAccessories() {
		return optionalAccessories;
	}
	public void setOptionalAccessories(List<String> optionalAccessories) {
		this.optionalAccessories = optionalAccessories;
	}
	public List<String> getPortInstalledAccessories() {
		return portInstalledAccessories;
	}
	public void setPortInstalledAccessories(List<String> portInstalledAccessories) {
		this.portInstalledAccessories = portInstalledAccessories;
	}
	public List<String> getDealerInstalledAccessories() {
		return dealerInstalledAccessories;
	}
	public void setDealerInstalledAccessories(
			List<String> dealerInstalledAccessories) {
		this.dealerInstalledAccessories = dealerInstalledAccessories;
	}
	public List<String> getPowertrainInfo() {
		return powertrainInfo;
	}
	public void setPowertrainInfo(List<String> powertrainInfo) {
		this.powertrainInfo = powertrainInfo;
	}
	public String getPoLogisticNotes() {
		return poLogisticNotes;
	}
	public void setPoLogisticNotes(String poLogisticNotes) {
		this.poLogisticNotes = poLogisticNotes;
	}
}
