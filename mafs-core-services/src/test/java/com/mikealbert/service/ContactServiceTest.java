package com.mikealbert.service;

import static org.junit.Assert.*;
import java.util.List;
import javax.annotation.Resource;

import org.junit.Ignore;
import org.junit.Test;
import com.mikealbert.testing.BaseTest;
import com.mikealbert.data.entity.AddressTypeCode;
import com.mikealbert.data.entity.Contact;
import com.mikealbert.data.entity.ContactAddress;
import com.mikealbert.data.entity.PhoneNumber;
import com.mikealbert.data.entity.PhoneNumberType;

public class ContactServiceTest extends BaseTest{
	@Resource ContactService contactService;
	@Resource LookupCacheService lookupCacheService;
	
	/**
	 * Contact will be returned for any client that has a list of them set up.
	 */
	@Test
	public void testGetFleetAdminContact(){
		String accountCode = "00000970";
		Contact fleetAdmin = contactService.getFleetAdminContact(accountCode);
		List<AddressTypeCode> addressTypeCodes = lookupCacheService.getAddressTypeCodes();
		
		ContactAddress address = contactService.getAddress(fleetAdmin.getContactId(),getPostalType(addressTypeCodes));
		assertNotNull(fleetAdmin);
		assertNotNull(address);
		PhoneNumberType workType = new PhoneNumberType("WORK");
		assertNotNull(address.getPhoneNumbers().get(workType));
	}
	
	@Test
	public void testGetPrimaryContact(){
		String accountCode = "00000970";
		Contact primary = contactService.getPrimaryContact(accountCode);
		List<AddressTypeCode> addressTypeCodes = lookupCacheService.getAddressTypeCodes();
		
		ContactAddress address = contactService.getAddress(primary.getContactId(),getPostalType(addressTypeCodes));
		assertNotNull(primary);
		assertNotNull(address);
		PhoneNumberType workType = new PhoneNumberType("WORK");
		assertNotNull(address.getPhoneNumbers().get(workType));
	}
	
	@Test
	public void testGetClientSpecifiedContact(){
		String accountCode = "00000970";
		Contact client = contactService.getClientSpecifiedContact(accountCode);
		List<AddressTypeCode> addressTypeCodes = lookupCacheService.getAddressTypeCodes();
		if(client != null){
			ContactAddress address = contactService.getAddress(client.getContactId(),getPostalType(addressTypeCodes));
			assertNotNull(client);
			assertNotNull(address);
			PhoneNumberType workType = new PhoneNumberType("WORK");
			assertNotNull(address.getPhoneNumbers().get(workType));
		}else{
			assertNull(client);
		}
	}
	
	@Test
	public void testGetClientSpecifiedContactWithoutContact(){
		String accountCode = "00002688";
		Contact client = contactService.getClientSpecifiedContact(accountCode);
		assertNull(client);
	}
	
	@Test
	public void testGetRiskContactNumber(){
		String riskContactNumber = contactService.getRiskContactNumber();
		if(riskContactNumber != null){
			assertNotNull(riskContactNumber);
		}else{
			assertNull(riskContactNumber);
		}
	}
	
	private AddressTypeCode getPostalType(List<AddressTypeCode> addressTypeCodes) {
		AddressTypeCode postalType = null;
		for (AddressTypeCode addressTypeCode : addressTypeCodes) {
	        if (addressTypeCode.getAddressType().equalsIgnoreCase("POST")) {
	        	postalType = addressTypeCode;
	        	break;
	        }
	    }
		return postalType;
	}
}
