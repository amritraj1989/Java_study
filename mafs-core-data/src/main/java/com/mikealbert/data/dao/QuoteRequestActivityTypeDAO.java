package com.mikealbert.data.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.mikealbert.data.entity.QuoteRequestActivityType;

public interface QuoteRequestActivityTypeDAO extends JpaRepository<QuoteRequestActivityType, Long> {
	@Query("SELECT qrat FROM QuoteRequestActivityType qrat WHERE qrat.code = ?1")
	public QuoteRequestActivityType findByCode(String code);	
}
