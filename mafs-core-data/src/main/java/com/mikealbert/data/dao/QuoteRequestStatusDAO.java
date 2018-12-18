package com.mikealbert.data.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mikealbert.data.entity.QuoteRequestStatus;

public interface QuoteRequestStatusDAO extends JpaRepository<QuoteRequestStatus, Long> {
	
	@Query("Select qrs from QuoteRequestStatus qrs order by qrs.name asc")
	public List<QuoteRequestStatus> getAllRequestStatus();  
	
	@Query("SELECT qrs FROM QuoteRequestStatus qrs WHERE qrs.code = ?1")
	public QuoteRequestStatus findByCode(String code);
}
