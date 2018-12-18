package com.mikealbert.data.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mikealbert.data.entity.DebitCreditStatusCode;

/**
 * DAO for DebitCreditStatusCode Entity
 * 
 */

public interface DebitCreditStatusCodeDAO extends JpaRepository<DebitCreditStatusCode, String> {
	
	@Query("SELECT dcsc FROM DebitCreditStatusCode dcsc ORDER BY dcsc.dcStatusCode")
	public List<DebitCreditStatusCode> findDebitCreditStatusList();

}
