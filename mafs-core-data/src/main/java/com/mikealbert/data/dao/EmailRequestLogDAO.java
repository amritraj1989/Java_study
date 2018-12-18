package com.mikealbert.data.dao;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mikealbert.data.entity.EmailRequestLog;

/**
* DAO for EmailRequestLog Entity
* @author sibley
*/

public interface EmailRequestLogDAO extends JpaRepository<EmailRequestLog, Long> {
	@Query("SELECT erl FROM EmailRequestLog erl WHERE erl.processedDate IS NULL")
	public List<EmailRequestLog> findUnprocessedRequests();
	
	@Query("SELECT erl FROM EmailRequestLog erl WHERE erl.processedDate IS NULL AND erl.event = ?1 AND erl.scheduledYN = ?2 AND erl.requestDate <= ?3")
	public List<EmailRequestLog> findByEventAndScheduleYN(String event, String scheduledYN, Date currentTime);	
	
	@Query("SELECT erl FROM EmailRequestLog erl WHERE erl.objectId = ?1 AND erl.objectType = ?2 AND erl.event = ?3")
	public EmailRequestLog findByUniqueKeys(String objectId, String objectType, String event);	

	@Query("SELECT erl FROM EmailRequestLog erl WHERE erl.event = ?1 AND erl.processedDate IS NOT NULL AND  trunc(erl.processedDate) = ?2")
	public List<EmailRequestLog> findByEventProccessedDate(String event, Date processedDate);
	
}
