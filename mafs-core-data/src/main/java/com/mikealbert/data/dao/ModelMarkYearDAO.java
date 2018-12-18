package com.mikealbert.data.dao;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.mikealbert.data.entity.ModelMarkYear;

/**
* DAO for ModelMarkYear Entity
* @author Sibley
*/

public interface ModelMarkYearDAO extends JpaRepository<ModelMarkYear, Long> {
	
	@Query("SELECT mmy FROM ModelMarkYear mmy WHERE mmy.modelType.description = ?1 ORDER BY mmy.modelMarkYearDesc DESC")
	public List<ModelMarkYear> findByModelType(String modelType);	
	
	@Query("SELECT DISTINCT (mmy.modelMarkYearDesc) FROM ModelMarkYear mmy JOIN mmy.models mdl WHERE lower(mdl.standardCode) LIKE ?1 ORDER BY mmy.modelMarkYearDesc DESC")
	public List<String> findDistinctYearByCriteria(String mfgCode);
}
