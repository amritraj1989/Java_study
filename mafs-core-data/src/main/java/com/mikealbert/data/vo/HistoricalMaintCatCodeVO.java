package com.mikealbert.data.vo;

import java.io.Serializable;
import java.util.Date;

public class HistoricalMaintCatCodeVO implements Serializable {
	private static final long serialVersionUID = -8940867060627632836L;
	
	private Long mrtId;
	private Date actualStartDate;
	private int odo;
	private String maintCode;
	private String vendorCodeDesc;
	private String poNumber;
	
	public Long getMrtId() {
		return mrtId;
	}
	public void setMrtId(Long mrtId) {
		this.mrtId = mrtId;
	}
	public Date getActualStartDate() {
		return actualStartDate;
	}
	public void setActualStartDate(Date actualStartDate) {
		this.actualStartDate = actualStartDate;
	}
	public int getOdo() {
		return odo;
	}
	public void setOdo(int odo) {
		this.odo = odo;
	}
	public String getVendorCodeDesc() {
		return vendorCodeDesc;
	}
	public void setVendorCodeDesc(String vendorCodeDesc) {
		this.vendorCodeDesc = vendorCodeDesc;
	}
	public String getMaintCode() {
		return maintCode;
	}
	public void setMaintCode(String maintCode) {
		this.maintCode = maintCode;
	}
	public String getPoNumber() {
		return poNumber;
	}
	public void setPoNumber(String poNumber) {
		this.poNumber = poNumber;
	}
	
}
