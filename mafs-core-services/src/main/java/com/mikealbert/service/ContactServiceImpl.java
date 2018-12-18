package com.mikealbert.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mikealbert.data.dao.ContactInformationDAO;
import com.mikealbert.data.entity.AddressTypeCode;
import com.mikealbert.data.entity.Contact;
import com.mikealbert.data.entity.ContactAddress;
import com.mikealbert.data.entity.ExternalAccount;
import com.mikealbert.data.entity.WebsiteUser;
import com.mikealbert.data.enumeration.CorporateEntity;

@Service("contactService")
public class ContactServiceImpl implements ContactService{
	
	@Resource ContactInformationDAO contactInformationDAO;
	@Resource CustomerAccountService customerAccountService;
	@Resource WillowConfigService willowConfigService;
		
    public ContactAddress getAddress( Long contactId, AddressTypeCode addressTypeCode )
    {
    	ContactAddress address = contactInformationDAO.findAddressByAddressType(contactId, addressTypeCode.getAddressType());
		return address;
    }
    
	/**
	 * For a provided contact list, the primary contact is found and returned.
	 * 
	 * @param contacts List of Contacts
	 * @return Primary contact
	 */
	@Override
	public Contact determinePrimaryContact(List<Contact> contacts){
		Contact primary = null; 
		for(Contact contact : contacts){
			if(contact.getContactType().equalsIgnoreCase(ContactService.CONTACT_TYPE_PRIMARY) && contact.getDefaultInd().equalsIgnoreCase(CustomerAccountServiceImpl.DEFAULT_IND_SET)){
				primary = contact;
				break;
			}
		}
		
		return primary;
	}   
	
    private Contact determineClientSpecifiedContact(List<Contact> contacts){
		Contact client = null; 
		for(Contact contact : contacts){
			if(contact.getContactType().equalsIgnoreCase(ContactService.CONTACT_TYPE_CLIENT_800) && contact.getDefaultInd().equalsIgnoreCase(CustomerAccountServiceImpl.DEFAULT_IND_SET)){
				client = contact;
				break;
			}
		}
		
		return client;
	}
	
	/**
	 * For a provided list of website users, the fleet admin is found and returned.
	 * 
	 * @param websiteUsers List of Web Users
	 * @return Fleet admin 
	 */
    @Override
	public Contact determineFleetAdminContact(List<WebsiteUser> websiteUsers){
		Contact admin = null; 

		for(WebsiteUser webUser : websiteUsers){
			if(webUser.getWebsiteUserType().equalsIgnoreCase(FLEET_ADMIN)){
				admin = webUser.getContact();
				break;
			}
		}
		return admin;
	}
	
	
    @Override
    @Transactional(readOnly = true)
	public Contact getFleetAdminContact(String accountCode) {    	
		ExternalAccount customerAccount = customerAccountService.getCustomerAccount(CorporateEntity.MAL, "C", accountCode);
		return determineFleetAdminContact(customerAccount.getWebsiteUsers());
	}

	@Override
    @Transactional(readOnly = true)
	public Contact getPrimaryContact(String accountCode) {
		ExternalAccount customerAccount = customerAccountService.getCustomerAccount(CorporateEntity.MAL, "C", accountCode);
		return determinePrimaryContact(customerAccount.getContacts());
	}

	@Override
    @Transactional(readOnly = true)
	public Contact getClientSpecifiedContact(String accountCode) {
		ExternalAccount customerAccount = customerAccountService.getCustomerAccount(CorporateEntity.MAL, "C", accountCode);
		return determineClientSpecifiedContact(customerAccount.getContacts());
	}

	@Override
	public String getRiskContactNumber() {
		return willowConfigService.getConfigValue(WillowConfigService.RISK_MGT_CONTACT_PHONE);
	}
}
