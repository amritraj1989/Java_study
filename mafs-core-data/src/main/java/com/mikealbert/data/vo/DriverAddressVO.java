package com.mikealbert.data.vo;

import java.io.Serializable;
import java.util.Date;

public class DriverAddressVO implements Serializable {

	private static final long serialVersionUID = -7987938010119574333L;
    private String addressLine1;
    private String addressLine2;
    private String cityDescription;
    private String regionDescription;    
    private String postcode;
    private String country;
    private Date fromDate;
	private Date toDate;
	private String type;
	private String businessAddressLine;	
	private String displayAddressLine1;
	private String displayAddressLine2;
	private String displayAddressLine2Details;
	
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
	public String getCityDescription() {
		return cityDescription;
	}
	public void setCityDescription(String cityDescription) {
		this.cityDescription = cityDescription;
	}
	public String getRegionDescription() {
		return regionDescription;
	}
	public void setRegionDescription(String regionDescription) {
		this.regionDescription = regionDescription;
	}
	public String getPostcode() {
		return postcode;
	}
	public void setPostcode(String postcode) {
		this.postcode = postcode;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
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
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getBusinessAddressLine() {
		return businessAddressLine;
	}
	public void setBusinessAddressLine(String businessAddressLine) {
		this.businessAddressLine = businessAddressLine;
	}
	public String getDisplayAddressLine1() {
		return displayAddressLine1;
	}
	public void setDisplayAddressLine1(String displayAddressLine1) {
		this.displayAddressLine1 = displayAddressLine1;
	}
	public String getDisplayAddressLine2() {
		return displayAddressLine2;
	}
	public void setDisplayAddressLine2(String displayAddressLine2) {
		this.displayAddressLine2 = displayAddressLine2;
	}

	public String getDisplayAddressLine2Details() {
		return displayAddressLine2Details;
	}
	public void setDisplayAddressLine2Details(String displayAddressLine2Details) {
		this.displayAddressLine2Details = displayAddressLine2Details;
	}
}
