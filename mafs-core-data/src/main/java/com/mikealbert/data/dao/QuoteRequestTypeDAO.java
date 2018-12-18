package com.mikealbert.data.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.mikealbert.data.entity.QuoteRequestType;

public interface QuoteRequestTypeDAO extends JpaRepository<QuoteRequestType, Long> {
	
	@Query("SELECT qrt FROM QuoteRequestType qrt WHERE qrt.code = ?1")
	public QuoteRequestType findByCode(String code);	
		
}
