package com.mikealbert.data.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mikealbert.data.entity.CapitalElement;

/**
* DAO for CapitalElement Entity
* @author Singh
*/

public interface CapitalElementDAO extends CapitalElementDAOCustom, JpaRepository<CapitalElement, Long> {
	 
		@Query("SELECT ce FROM CapitalElement ce WHERE ce.code = ?1")
		public CapitalElement findByCode(String code);
		
}
