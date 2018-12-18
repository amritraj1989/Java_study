package com.mikealbert.data.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mikealbert.data.entity.QuoteModelProperty;

public interface QuoteModelPropertyDAO extends JpaRepository<QuoteModelProperty, String>{
	
	@Query("SELECT qmp FROM QuoteModelProperty qmp WHERE  qmp.name = ?1 ")
	public QuoteModelProperty findByName(String dpName);
	

}
