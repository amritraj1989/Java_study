package com.mikealbert.data.vo;

import java.io.Serializable;
import java.util.Date;

public class DriverAllocationVO implements Serializable {

	private static final long serialVersionUID = -8824596455811102837L;
	private Long drvId;
    private Date fromDate;
    private Date toDate; 
    private Long fmsId;
    private String unitNo;
    
	public Long getDrvId() {
		return drvId;
	}
	public void setDrvId(Long drvId) {
		this.drvId = drvId;
	}
	public Date getFromDate() {
		return fromDate;
	}
	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}
	public Date getToDate() {
		return toDate;
	}
	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}
	public Long getFmsId() {
		return fmsId;
	}
	public void setFmsId(Long fmsId) {
		this.fmsId = fmsId;
	}
	public String getUnitNo() {
		return unitNo;
	}
	public void setUnitNo(String unitNo) {
		this.unitNo = unitNo;
	}
}
