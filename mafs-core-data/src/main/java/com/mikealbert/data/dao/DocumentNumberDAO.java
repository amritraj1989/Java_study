package com.mikealbert.data.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import com.mikealbert.data.entity.DocumentNumber;
import com.mikealbert.data.entity.DocumentNumberPK;

/**
* DAO for DocumentNumber Entity
* @author sibley
*/

public interface DocumentNumberDAO extends JpaRepository<DocumentNumber, DocumentNumberPK> {
	static final String DOMAIN_FLEET_MAINTENANCE = "FLMAINT";
	
}
