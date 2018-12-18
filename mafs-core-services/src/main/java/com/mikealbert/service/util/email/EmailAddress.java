package com.mikealbert.service.util.email;

import java.io.Serializable;


/**
 * Abstraction for an email address.
 * 
 *
 */
public class EmailAddress implements Serializable {

	private static final long serialVersionUID = -1429823848131394258L;

	private String address;
	private String displayName;
	
	public EmailAddress(){}
	
	public EmailAddress(String address, String displayName){
		this.address = address;
		this.displayName = displayName;
	}
	
	public EmailAddress(String address){
		this.address = address;
	}

	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}

	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	
	
}
