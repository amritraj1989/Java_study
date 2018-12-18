package com.mikealbert.data.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mikealbert.data.entity.FinanceParameterCategory;

/**
* DAO for FinanceParameterCategory Entity
*/

public interface FinanceParameterCategoryDAO extends JpaRepository<FinanceParameterCategory, String>{
	
	@Query("FROM FinanceParameterCategory fpc WHERE fpc.fpcId = ?1")
	public FinanceParameterCategory getFinanceParameterCategoryByFpcId(long fpcId);
	
}
