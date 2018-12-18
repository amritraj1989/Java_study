package com.mikealbert.data.vo;

import java.io.Serializable;
import java.util.Date;

import com.mikealbert.data.util.DisplayFormatHelper;
import com.mikealbert.util.MALUtilities;


public class DriverInfoVO implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4236689497829310857L;
	
	Long drvId;
	Long fmsId;
	Long qmdId;
	private String driverForeName;
	private String driverSurName;
	private String fullName;
	private String businessAddressLine;
	private String addressLine1;
	private String addressLine2;
	private String town;
	private String region;
	private String postCode;
	private String driverPhone;
	private String allocatedUnit;
	private Date eolDate;
	private boolean returningUnit;
	private String productType;
	private String term;
	private String quoteProfileDesc;
	private String vin;
	private String trim;
	
	public String getFullDeliveryAddress() {
		return DisplayFormatHelper.formatAddressForTable(getBusinessAddressLine(),getAddressLine1(), getAddressLine2(), null, null, getTown(), getRegion(), getPostCode(), "<br/>");
	}
	
	public String getAddressLine1() {
		return addressLine1;
	}
	public void setAddressLine1(String addressLine1) {
		this.addressLine1 = addressLine1;
	}
	public String getAddressLine2() {
		return addressLine2;
	}
	public void setAddressLine2(String addressLine2) {
		this.addressLine2 = addressLine2;
	}
	public String getTown() {
		return town;
	}
	public void setTown(String town) {
		this.town = town;
	}
	public String getRegion() {
		return region;
	}
	public void setRegion(String region) {
		this.region = region;
	}
	public String getPostCode() {
		return postCode;
	}
	public void setPostCode(String postCode) {
		this.postCode = postCode;
	}
	public String getDriverPhone() {
		return driverPhone;
	}
	public void setDriverPhone(String driverPhone) {
		this.driverPhone = driverPhone;
	}
	public String getAllocatedUnit() {
		return allocatedUnit;
	}
	public void setAllocatedUnit(String allocatedUnit) {
		this.allocatedUnit = allocatedUnit;
	}
	public Date getEolDate() {
		return eolDate;
	}
	public void setEolDate(Date eolDate) {
		this.eolDate = eolDate;
	}
	public String getProductType() {
		return productType;
	}
	public void setProductType(String productType) {
		this.productType = productType;
	}
	public String getTerm() {
		return term;
	}
	public void setTerm(String term) {
		this.term = term;
	}
	public String getQuoteProfileDesc() {
		return quoteProfileDesc;
	}
	public void setQuoteProfileDesc(String quoteProfileDesc) {
		this.quoteProfileDesc = quoteProfileDesc;
	}
	public Long getDrvId() {
		return drvId;
	}
	public void setDrvId(Long drvId) {
		this.drvId = drvId;
	}

	public Long getFmsId() {
		return fmsId;
	}

	public void setFmsId(Long fmsId) {
		this.fmsId = fmsId;
	}

	public String getDriverForeName() {
		return driverForeName;
	}

	public void setDriverForeName(String driverForeName) {
		this.driverForeName = driverForeName;
	}

	public String getDriverSurName() {
		return driverSurName;
	}

	public void setDriverSurName(String driverSurName) {
		this.driverSurName = driverSurName;
	}

	public Long getQmdId() {
		return qmdId;
	}

	public void setQmdId(Long qmdId) {
		this.qmdId = qmdId;
	}

	public String getBusinessAddressLine() {
		return businessAddressLine;
	}

	public void setBusinessAddressLine(String businessAddressLine) {
		this.businessAddressLine = businessAddressLine;
	}

	public String getFullName() {
		if(!MALUtilities.isEmpty(getDriverSurName()) && !MALUtilities.isEmpty(getDriverForeName())){
			return getDriverSurName() + ", " + getDriverForeName();
		}else {
			return "";
		}
		
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public boolean isReturningUnit() {
		return returningUnit;
	}

	public void setReturningUnit(boolean returningUnit) {
		this.returningUnit = returningUnit;
	}

	public String getVin() {
		return vin;
	}

	public void setVin(String vin) {
		this.vin = vin;
	}

	public String getTrim() {
		return trim;
	}

	public void setTrim(String trim) {
		this.trim = trim;
	}


}
