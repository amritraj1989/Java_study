package com.mikealbert.data.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mikealbert.data.entity.FormulaParameter;

/**
* DAO for FormulaParameter Entity
* @author Singh
*/

public interface FormulaParameterDAO extends JpaRepository<FormulaParameter, Long> {
	
	@Query("FROM FormulaParameter fp WHERE fp.lelLelId = ?1 order by fp.sequenceNo")
	public List<FormulaParameter> findByLeaseElementId(Long lelId);	

	@Query("FROM FormulaParameter fp WHERE fp.lelLelId = ?1 and fp.parameterType = ?2 order by fp.sequenceNo")
	public List<FormulaParameter> findByLeaseElementIdAndParameterType(Long lelId, String parameterType);	

	
}
