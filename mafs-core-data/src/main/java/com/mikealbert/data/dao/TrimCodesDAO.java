package com.mikealbert.data.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mikealbert.data.entity.TrimCodes;

/**
* DAO for TrimCodes Entity
* @author Singh
*/

public interface TrimCodesDAO extends JpaRepository<TrimCodes, Long> {
	
	@Query("select tc from TrimCodes tc where tc.trimCode = ?1")//trim code is unique in system 
	public TrimCodes findByTrimCode(String trimCode);
	
}
