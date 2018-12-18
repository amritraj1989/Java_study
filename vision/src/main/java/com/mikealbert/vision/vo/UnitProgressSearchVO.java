package com.mikealbert.vision.vo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.mikealbert.data.vo.VendorInfoVO;

public class UnitProgressSearchVO {
	
	private Long psoId;
	private String unitNo;
	private String vin;
	private String driver;
	private Long driverId;
	private String deliveringDealerName;
	private String deliveringDealerCode;
	private String accountName;
	private String accountCode;
	private Long docId;
	private List<VendorInfoVO> vendorInfoList = new ArrayList<VendorInfoVO>();
	private Date invoiceProcessedDate;
	private Date factoryShipDate;
	private Date dealerReceivedDate;
	private Date clientETADate;
	private Date vehicleReadyDate;
	private Date followUpDate;
	private String reqdBy;
	
	private Long qmdId;
	private Long stockThDocId;
	private String orderType;
	private Long replacementFmsId;
	private String replacementUnitNo;	
	
	private String plateNo;
	private Date plateExpirationDate;
	
	private Date dealerReceivedDateInput;
	private Date inServiceDateInput;
	private Long odoMeterReading;
	private Date odoReadingDateInput;
	private boolean isStockUnit;
	private boolean isUsedVehicle;
	private String quoteStatus;
	private String modelDesc;
	private String manufacturerName;
	private boolean hasUpfit;
	
	private String createTransport;
	private String orderingContactName;
	private String orderingContactPhone;
	private String delayReason;
	private String exteriorColour;
	private String vehiclePickedUp;
	private boolean tolerance;
	private boolean purchaseUnit;
	
	public Long getPsoId() {
		return psoId;
	}

	public void setPsoId(Long psoId) {
		this.psoId = psoId;
	}

	public String getUnitNo() {
		return unitNo;
	}

	public void setUnitNo(String unitNo) {
		this.unitNo = unitNo;
	}

	public String getVin() {
		return vin;
	}

	public void setVin(String vin) {
		this.vin = vin;
	}

	public String getDriver() {
		return driver;
	}

	public void setDriver(String driver) {
		this.driver = driver;
	}

	public Long getDriverId() {
		return driverId;
	}

	public void setDriverId(Long driverId) {
		this.driverId = driverId;
	}

	public String getDeliveringDealerName() {
		return deliveringDealerName;
	}

	public void setDeliveringDealerName(String deliveringDealerName) {
		this.deliveringDealerName = deliveringDealerName;
	}

	public String getDeliveringDealerCode() {
		return deliveringDealerCode;
	}

	public void setDeliveringDealerCode(String deliveringDealerCode) {
		this.deliveringDealerCode = deliveringDealerCode;
	}

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public String getAccountCode() {
		return accountCode;
	}

	public void setAccountCode(String accountCode) {
		this.accountCode = accountCode;
	}

	public Date getFollowUpDate() {
		return followUpDate;
	}

	public void setFollowUpDate(Date followUpDate) {
		this.followUpDate = followUpDate;
	}

	public String getOrderType() {
		return orderType;
	}

	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}

	public Long getReplacementFmsId() {
		return replacementFmsId;
	}

	public void setReplacementFmsId(Long replacementFmsId) {
		this.replacementFmsId = replacementFmsId;
	}

	public String getReplacementUnitNo() {
		return replacementUnitNo;
	}

	public void setReplacementUnitNo(String replacementUnitNo) {
		this.replacementUnitNo = replacementUnitNo;
	}

	public String getReqdBy() {
		return reqdBy;
	}

	public void setReqdBy(String reqdBy) {
		this.reqdBy = reqdBy;
	}

	public Date getInvoiceProcessedDate() {
		return invoiceProcessedDate;
	}

	public void setInvoiceProcessedDate(Date invoiceProcessedDate) {
		this.invoiceProcessedDate = invoiceProcessedDate;
	}

	public Date getFactoryShipDate() {
		return factoryShipDate;
	}

	public void setFactoryShipDate(Date factoryShipDate) {
		this.factoryShipDate = factoryShipDate;
	}

	public Date getDealerReceivedDate() {
		return dealerReceivedDate;
	}

	public void setDealerReceivedDate(Date dealerReceivedDate) {
		this.dealerReceivedDate = dealerReceivedDate;
	}

	public Date getClientETADate() {
		return clientETADate;
	}

	public void setClientETADate(Date clientETADate) {
		this.clientETADate = clientETADate;
	}

	public Date getVehicleReadyDate() {
		return vehicleReadyDate;
	}

	public void setVehicleReadyDate(Date vehicleReadyDate) {
		this.vehicleReadyDate = vehicleReadyDate;
	}

	public String getPlateNo() {
		return plateNo;
	}

	public void setPlateNo(String plateNo) {
		this.plateNo = plateNo;
	}

	public Date getPlateExpirationDate() {
		return plateExpirationDate;
	}

	public void setPlateExpirationDate(Date plateExpirationDate) {
		this.plateExpirationDate = plateExpirationDate;
	}

	public Date getInServiceDateInput() {
		return inServiceDateInput;
	}

	public void setInServiceDateInput(Date inServiceDateInput) {
		this.inServiceDateInput = inServiceDateInput;
	}

	public Long getOdoMeterReading() {
		return odoMeterReading;
	}

	public void setOdoMeterReading(Long odoMeterReading) {
		this.odoMeterReading = odoMeterReading;
	}

	public Date getOdoReadingDateInput() {
		return odoReadingDateInput;
	}

	public void setOdoReadingDateInput(Date odoReadingDateInput) {
		this.odoReadingDateInput = odoReadingDateInput;
	}
	
	public boolean isStockUnit() {
		return isStockUnit;
	}

	public void setStockUnit(boolean isStockUnit) {
		this.isStockUnit = isStockUnit;
	}

	public boolean isUsedVehicle() {
		return isUsedVehicle;
	}

	public void setUsedVehicle(boolean isUsedVehicle) {
		this.isUsedVehicle = isUsedVehicle;
	}

	public String getQuoteStatus() {
		return quoteStatus;
	}

	public void setQuoteStatus(String quoteStatus) {
		this.quoteStatus = quoteStatus;
	}

	public Long getQmdId() {
		return qmdId;
	}

	public void setQmdId(Long qmdId) {
		this.qmdId = qmdId;
	}
	
	public Long getStockThDocId() {
		return stockThDocId;
	}

	public void setStockThDocId(Long stockThDocId) {
		this.stockThDocId = stockThDocId;
	}

	public Date getDealerReceivedDateInput() {
		return dealerReceivedDateInput;
	}

	public void setDealerReceivedDateInput(Date dealerReceivedDateInput) {
		this.dealerReceivedDateInput = dealerReceivedDateInput;
	}

	public Long getDocId() {
		return docId;
	}

	public void setDocId(Long docId) {
		this.docId = docId;
	}

	public List<VendorInfoVO> getVendorInfoList() {
		return vendorInfoList;
	}

	public void setVendorInfoList(List<VendorInfoVO> vendorInfoList) {
		this.vendorInfoList = vendorInfoList;
	}

	public String getModelDesc() {
		return modelDesc;
	}

	public void setModelDesc(String modelDesc) {
		this.modelDesc = modelDesc;
	}

	public boolean isHasUpfit() {
		return hasUpfit;
	}

	public void setHasUpfit(boolean hasUpfit) {
		this.hasUpfit = hasUpfit;
	}

	public String getCreateTransport() {
		return createTransport;
	}

	public void setCreateTransport(String createTransport) {
		this.createTransport = createTransport;
	}

	public String getOrderingContactName() {
		return orderingContactName;
	}

	public void setOrderingContactName(String orderingContactName) {
		this.orderingContactName = orderingContactName;
	}

	public String getOrderingContactPhone() {
		return orderingContactPhone;
	}

	public void setOrderingContactPhone(String orderingContactPhone) {
		this.orderingContactPhone = orderingContactPhone;
	}

	public String getDelayReason() {
		return delayReason;
	}

	public void setDelayReason(String delayReason) {
		this.delayReason = delayReason;
	}

   public boolean isTolerance() {
		return tolerance;
	}

	public void setTolerance(boolean tolerance) {
		this.tolerance = tolerance;
	}

	@Override
	public String toString() {
		return "UnitProgressSearchVO [unitNo=" + unitNo + "]";
	}

	public boolean isPurchaseUnit() {
		return purchaseUnit;
	}

	public void setPurchaseUnit(boolean purchaseUnit) {
		this.purchaseUnit = purchaseUnit;
	}

	public String getExteriorColour() {
		return exteriorColour;
	}

	public void setExteriorColour(String exteriorColour) {
		this.exteriorColour = exteriorColour;
	}

	public String getVehiclePickedUp() {
		return vehiclePickedUp;
	}

	public void setVehiclePickedUp(String vehiclePickedUp) {
		this.vehiclePickedUp = vehiclePickedUp;
	}

	public String getManufacturerName() {
		return manufacturerName;
	}

	public void setManufacturerName(String manufacturerName) {
		this.manufacturerName = manufacturerName;
	}
	
	
}

