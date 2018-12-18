package com.mikealbert.data.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.mikealbert.data.entity.ObjectLogBook;

/**
* DAO for LogBook Entity
* @author sibley
*/

public interface ObjectLogBookDAO extends JpaRepository<ObjectLogBook, Long> {
	
	@Query("SELECT olb FROM ObjectLogBook olb INNER JOIN olb.logBook lbk " +
	       "    LEFT JOIN FETCH olb.logBookEntries " +
	       "WHERE lbk.type = ?1 AND olb.objectId = ?2 AND olb.objectType = ?3 AND olb.objectName = ?4")
	public ObjectLogBook findByObject(String logBookType, Long objectId, String objectType, String objectName);
		
}
