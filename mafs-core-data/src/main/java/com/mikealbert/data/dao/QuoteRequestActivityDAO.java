package com.mikealbert.data.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import com.mikealbert.data.entity.QuoteRequestActivity;

public interface QuoteRequestActivityDAO extends JpaRepository<QuoteRequestActivity, Long> {		
}
