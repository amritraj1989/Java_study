package com.mikealbert.data.vo;

import com.mikealbert.data.entity.Address;
import com.mikealbert.data.entity.WebsiteUserAssociation;
import com.mikealbert.data.util.DisplayFormatHelper;

public class WebsiteUserAssociationVO {
	
	private String firstName;
	private String lastName;
	private String email;
	private Address address;
	private WebsiteUserAssociation websiteUserAssociation;
	
	public String getFormattedAddress() {
		return DisplayFormatHelper.formatAddressForTable(null,address.getAddressLine1(), address.getAddressLine2(), null, null, address.getTownCityCode().getTownDescription(), 
				address.getRegionDescription(), address.getPostcode(), "<br/>");
	}

	
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public Address getAddress() {
		return address;
	}
	public void setAddress(Address address) {
		this.address = address;
	}
	public WebsiteUserAssociation getWebsiteUserAssociation() {
		return websiteUserAssociation;
	}
	public void setWebsiteUserAssociation(WebsiteUserAssociation websiteUserAssociation) {
		this.websiteUserAssociation = websiteUserAssociation;
	}
}
