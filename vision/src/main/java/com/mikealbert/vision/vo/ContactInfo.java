package com.mikealbert.vision.vo;

public class ContactInfo {

	private String dealerVendorName; // for ui parent page selection

	private String name;
	private String email;
	private String phone;
	private String dealerPhone;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getDealerVendorName() {
		return dealerVendorName;
	}

	public void setDealerVendorName(String dealerVendorName) {
		this.dealerVendorName = dealerVendorName;
	}

	// used in download to excel feature of upfit search
	public String getExcelFormatedData() {
		return "Contact Name=" + name + "\n" + "Email=" + email + ", Phone =" + phone+", Dealer Phone =" + dealerPhone;
	}

	@Override
	public String toString() {
		return "Contact Name=" + name + ", email=" + email + ", phone=" + phone + ", Dealer Phone =" + dealerPhone;
	}

	public String getDealerPhone() {
		return dealerPhone;
	}

	public void setDealerPhone(String dealerPhone) {
		this.dealerPhone = dealerPhone;
	}

}