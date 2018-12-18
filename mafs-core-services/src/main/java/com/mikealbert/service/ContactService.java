package com.mikealbert.service;

import java.util.List;

import com.mikealbert.data.entity.AddressTypeCode;
import com.mikealbert.data.entity.Contact;
import com.mikealbert.data.entity.ContactAddress;
import com.mikealbert.data.entity.WebsiteUser;
import com.mikealbert.data.enumeration.CorporateEntity;

public interface ContactService {
	public static final String FLEET_ADMIN = "FLEET_ADMIN";
	public static final String CONTACT_TYPE_PRIMARY = "Primary";
	public static final String DEFAULT_IND_SET = "Y";	
	public static final String CONTACT_TYPE_CLIENT_800 = "800 NO";
	
	public ContactAddress getAddress( Long contactId, AddressTypeCode addressTypeCode );
	// TODO: cut need to websiteUsers if possible; leaky code!!
	public Contact determineFleetAdminContact(List<WebsiteUser> websiteUsers);
	public Contact determinePrimaryContact(List<Contact> contacts);
	
	public Contact getFleetAdminContact(String accountCode);
	public Contact getPrimaryContact(String accountCode);
	public Contact getClientSpecifiedContact(String accountCode);
	public String getRiskContactNumber();	
}
