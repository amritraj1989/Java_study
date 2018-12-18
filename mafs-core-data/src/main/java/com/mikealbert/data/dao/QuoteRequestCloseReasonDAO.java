package com.mikealbert.data.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import com.mikealbert.data.entity.QuoteRequestCloseReason;

public interface QuoteRequestCloseReasonDAO extends JpaRepository<QuoteRequestCloseReason, Long> {
	
		
}
