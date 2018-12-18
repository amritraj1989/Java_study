package com.mikealbert.data.vo;

import java.io.Serializable;

import com.mikealbert.data.entity.Contact;
import com.mikealbert.data.entity.ExternalAccount;

public class CustomerContactVO implements Serializable {
	private ExternalAccount customer;
	private Contact primaryContact;
	private Contact fleetAdminContact;
	
	public ExternalAccount getCustomer() {
		return customer;
	}
	public void setCustomer(ExternalAccount customer) {
		this.customer = customer;
	}
	public Contact getPrimaryContact() {
		return primaryContact;
	}
	public void setPrimaryContact(Contact primaryContact) {
		this.primaryContact = primaryContact;
	}
	public Contact getFleetAdminContact() {
		return fleetAdminContact;
	}
	public void setFleetAdminContact(Contact fleetAdminContact) {
		this.fleetAdminContact = fleetAdminContact;
	}
	
}
