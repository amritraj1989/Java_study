package com.mikealbert.data.vo;

import java.math.BigDecimal;
import java.util.Date;

public class CnbvVO {

	private long period;
	private Date transDate;
	private long qmdId;
	private BigDecimal custCapCost;
	private BigDecimal residualValue;
	private BigDecimal cnbv;
	
	public BigDecimal getCnbv() {
		return cnbv;
	}
	public void setCnbv(BigDecimal cnbv) {
		this.cnbv = cnbv;
	}
	public long getPeriod() {
		return period;
	}
	public void setPeriod(long period) {
		this.period = period;
	}
	public Date getTransDate() {
		return transDate;
	}
	public void setTransDate(Date transDate) {
		this.transDate = transDate;
	}
	public long getQmdId() {
		return qmdId;
	}
	public void setQmdId(long qmdId) {
		this.qmdId = qmdId;
	}
	public BigDecimal getCustCapCost() {
		return custCapCost;
	}
	public void setCustCapCost(BigDecimal custCapCost) {
		this.custCapCost = custCapCost;
	}
	public BigDecimal getResidualValue() {
		return residualValue;
	}
	public void setResidualValue(BigDecimal residualValue) {
		this.residualValue = residualValue;
	}

}
