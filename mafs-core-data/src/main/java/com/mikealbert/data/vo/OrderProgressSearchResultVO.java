package com.mikealbert.data.vo;

import java.util.Date;
import java.util.List;

public class OrderProgressSearchResultVO {
	private Long qmdId;
	private Long docId;
	private Long fmsId;
	private Long mdlId;
	private Long makeId;
	private String unitNo;
	private String orderType;
	private String accountName;
	private String accountCode;
	private String css;
	private String mfgCode;
	private String year;
	private String make;
	private String model;
	private String trim;
	private List<String> factoryEquipments;
	private Date currentETADate;

	public Long getQmdId() {
		return qmdId;
	}

	public void setQmdId(Long qmdId) {
		this.qmdId = qmdId;
	}

	public String getUnitNo() {
		return unitNo;
	}

	public void setUnitNo(String unitNo) {
		this.unitNo = unitNo;
	}

	public String getOrderType() {
		return orderType;
	}

	public void setOrderType(String orderType) {
		this.orderType = orderType;
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

	public String getCss() {
		return css;
	}

	public void setCss(String css) {
		this.css = css;
	}

	public Long getDocId() {
		return docId;
	}

	public void setDocId(Long docId) {
		this.docId = docId;
	}

	public Long getFmsId() {
		return fmsId;
	}

	public void setFmsId(Long fmsId) {
		this.fmsId = fmsId;
	}

	public Long getMdlId() {
		return mdlId;
	}

	public void setMdlId(Long mdlId) {
		this.mdlId = mdlId;
	}

	public Long getMakeId() {
		return makeId;
	}

	public void setMakeId(Long makeId) {
		this.makeId = makeId;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getMake() {
		return make;
	}

	public void setMake(String make) {
		this.make = make;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getTrim() {
		return trim;
	}

	public void setTrim(String trim) {
		this.trim = trim;
	}

	public String getMfgCode() {
		return mfgCode;
	}

	public void setMfgCode(String mfgCode) {
		this.mfgCode = mfgCode;
	}

	public List<String> getFactoryEquipments() {
		return factoryEquipments;
	}

	public void setFactoryEquipments(List<String> factoryEquipments) {
		this.factoryEquipments = factoryEquipments;
	}

	public Date getCurrentETADate() {
		return currentETADate;
	}

	public void setCurrentETADate(Date currentETADate) {
		this.currentETADate = currentETADate;
	}

}
