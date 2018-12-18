package com.mikealbert.data.vo;

import java.util.Date;
import java.util.List;

public class StockUnitsLovVO {
	
	private long fmsId;
	private long mdlId;
	private String vin;
	private String unitNo;
	private String standardEDINo;
	private String vehicleDescription;
	private Integer lastOdoReading;
	private String exteriorColor;
	private String trimColor;
	private int quoteCount;
	private Date etaDate;
	private Date receivedDate;
	private List<AccessoryVO> standardAccessories;
	private List<AccessoryVO> optionalAccessories; 
	private List<AccessoryVO> dealerAccessories;
	
	public String getVehicleDescription() {
		return vehicleDescription;
	}
	public void setVehicleDescription(String vehicleDescription) {
		this.vehicleDescription = vehicleDescription;
	}
	public Integer getLastOdoReading() {
		return lastOdoReading;
	}
	public void setLastOdoReading(Integer lastOdoReading) {
		this.lastOdoReading = lastOdoReading;
	}
	public String getExteriorColor() {
		return exteriorColor;
	}
	public void setExteriorColor(String exteriorColor) {
		this.exteriorColor = exteriorColor;
	}
	public String getTrimColor() {
		return trimColor;
	}
	public void setTrimColor(String trimColor) {
		this.trimColor = trimColor;
	}
	public int getQuoteCount() {
		return quoteCount;
	}
	public void setQuoteCount(int quoteCount) {
		this.quoteCount = quoteCount;
	}
	public long getFmsId() {
		return fmsId;
	}
	public void setFmsId(long fmsId) {
		this.fmsId = fmsId;
	}
	public long getMdlId() {
		return mdlId;
	}
	public void setMdlId(long mdlId) {
		this.mdlId = mdlId;
	}
	public String getVin() {
		return vin;
	}
	public void setVin(String vin) {
		this.vin = vin;
	}
	public String getUnitNo() {
		return unitNo;
	}
	public void setUnitNo(String unitNo) {
		this.unitNo = unitNo;
	}
	public Date getEtaDate() {
		return etaDate;
	}
	public void setEtaDate(Date etaDate) {
		this.etaDate = etaDate;
	}
	public Date getReceivedDate() {
		return receivedDate;
	}
	public void setReceivedDate(Date receivedDate) {
		this.receivedDate = receivedDate;
	}
	public String getStandardEDINo() {
		return standardEDINo;
	}
	public void setStandardEDINo(String standardEDINo) {
		this.standardEDINo = standardEDINo;
	}
	public List<AccessoryVO> getStandardAccessories() {
		return standardAccessories;
	}
	public void setStandardAccessories(List<AccessoryVO> standardAccessories) {
		this.standardAccessories = standardAccessories;
	}
	public List<AccessoryVO> getOptionalAccessories() {
		return optionalAccessories;
	}
	public void setOptionalAccessories(List<AccessoryVO> optionalAccessories) {
		this.optionalAccessories = optionalAccessories;
	}
	public List<AccessoryVO> getDealerAccessories() {
		return dealerAccessories;
	}
	public void setDealerAccessories(List<AccessoryVO> dealerAccessories) {
		this.dealerAccessories = dealerAccessories;
	}
	
	
	}
