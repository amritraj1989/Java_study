package com.mikealbert.service.vo;

import java.util.Date;
import java.util.List;

import com.mikealbert.data.entity.FleetMaster;
import com.mikealbert.data.vo.ActiveQuoteVO;

public class StockUnitVO {
	private FleetMaster fleetMaster;
	private String description;
	private String exteriorColor;
	private String interiorColor;
	private String fleetCode;
	private Date etaDate;
	private Date receivedDate;
	private Long odoReading;
	private List<ActiveQuoteVO> activeQuoteList;  

	public FleetMaster getFleetMaster() {
		return fleetMaster;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getExteriorColor() {
		return exteriorColor;
	}
	public void setExteriorColor(String exteriorColor) {
		this.exteriorColor = exteriorColor;
	}
	public String getInteriorColor() {
		return interiorColor;
	}
	public void setInteriorColor(String interiorColor) {
		this.interiorColor = interiorColor;
	}
	public String getFleetCode() {
		return fleetCode;
	}
	public void setFleetCode(String fleetCode) {
		this.fleetCode = fleetCode;
	}
	public void setFleetMaster(FleetMaster fleetMaster) {
		this.fleetMaster = fleetMaster;
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
	public Long getOdoReading() {
		return odoReading;
	}
	public void setOdoReading(Long odoReading) {
		this.odoReading = odoReading;
	}
	public List<ActiveQuoteVO> getActiveQuoteList() {
		return activeQuoteList;
	}
	public void setActiveQuoteList(List<ActiveQuoteVO> activeQuoteList) {
		this.activeQuoteList = activeQuoteList;
	}

}