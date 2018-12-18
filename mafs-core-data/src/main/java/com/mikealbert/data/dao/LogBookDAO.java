package com.mikealbert.data.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.mikealbert.data.entity.LogBook;

/**
* DAO for LogBook Entity
* @author sibley
*/

public interface LogBookDAO extends LogBookDAOCustom, JpaRepository<LogBook, Long> {
		
	@Query("SELECT lbk FROM LogBook lbk WHERE lbk.type = ?1")
	public LogBook findByType(String logBookType);	
}
