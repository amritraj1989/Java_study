package com.mikealbert.data.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mikealbert.data.entity.ContactAddress;

/**
* DAO for ContactAddress Entity - This is specifically used right now soley for ContactInformationDialog from clientPOCCOntacts.xhtml.
* This is a special case where they want the Post and Garaged address and affiliated numbers instead of getting from the POC Rules.
* @author chaille
*/

public interface ContactInformationDAO extends JpaRepository<ContactAddress, Long> {
	
	@Query("from ContactAddress ca where ca.contact.contactId = ?1 and ca.addressType.addressType = ?2")
	public ContactAddress findAddressByAddressType(Long contactId, String addressTypeCode);
	
}

