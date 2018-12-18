package com.mikealbert.data.vo;


public class VendorLovVO {
	private String vendorName;
	private String addressLine1;
	private String addressLine2;
	private String addressLine3;
	private String addressLine4;
	private String postCode;
	private	String	townCity;
	private	String	region;
	private String phoneNumber;
	private String vendorCode;
	private String email;
	private String eaAccountCode;
	private Long rowId; // may be vendor Id for supplier or accountCode

	public Long getRowId() {
		return rowId;
	}
	public void setRowId(Long rowId) {
		this.rowId = rowId;
	}	
	
	public String getVendorName() {
		return vendorName;
	}
	public void setVendorName(String vendorName) {
		this.vendorName = vendorName;
	}
	public String getAddressLine1() {
		return addressLine1;
	}
	public void setAddressLine1(String addressLine1) {
		this.addressLine1 = addressLine1;
	}
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	public String getVendorCode() {
		return vendorCode;
	}
	public void setVendorCode(String vendorCode) {
		this.vendorCode = vendorCode;
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
	public String getAddressLine2() {
		return addressLine2;
	}
	public void setAddressLine2(String addressLine2) {
		this.addressLine2 = addressLine2;
	}
	public String getAddressLine3() {
		return addressLine3;
	}
	public void setAddressLine3(String addressLine3) {
		this.addressLine3 = addressLine3;
	}
	public String getAddressLine4() {
		return addressLine4;
	}
	public void setAddressLine4(String addressLine4) {
		this.addressLine4 = addressLine4;
	}
	public String getPostCode() {
		return postCode;
	}
	public void setPostCode(String postCode) {
		this.postCode = postCode;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getEaAccountCode() {
		return eaAccountCode;
	}
	public void setEaAccountCode(String eaAccountCode) {
		this.eaAccountCode = eaAccountCode;
	}	
		
}