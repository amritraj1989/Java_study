package com.mikealbert.data.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mikealbert.data.entity.Contact;

/**
* DAO for Contact Entity
* @author sibley
*/

public interface ContactDAO extends JpaRepository<Contact, Long> {
	
	@Query("SELECT cnt FROM Contact cnt WHERE cnt.externalAccount.externalAccountPK.cId = ?1"
			+ " AND cnt.externalAccount.externalAccountPK.accountType = ?2"
			+ " AND cnt.externalAccount.externalAccountPK.accountCode = ?3"
			+ " ORDER BY cnt.lastName asc, cnt.firstName asc")
	public List<Contact> getContactsByAccountInfo(Long cId, String accountType, String accountCode);
}
