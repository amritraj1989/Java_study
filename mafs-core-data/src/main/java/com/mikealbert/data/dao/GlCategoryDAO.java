package com.mikealbert.data.dao;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mikealbert.data.entity.CostDatabaseCategoryCodes;

public interface GlCategoryDAO extends JpaRepository<CostDatabaseCategoryCodes, Long> {

	@Query("SELECT cdcc FROM CostDatabaseCategoryCodes cdcc WHERE LOWER(cdcc.category) LIKE LOWER(?1) ORDER BY cdcc.category ASC")	
	public List<CostDatabaseCategoryCodes> findCategory(String category, Pageable pageable);	

	@Query("SELECT COUNT(cdcc) FROM CostDatabaseCategoryCodes cdcc WHERE LOWER(cdcc.category) LIKE LOWER(?1)")	
	public Long findCategoryCount(String category);		
}
