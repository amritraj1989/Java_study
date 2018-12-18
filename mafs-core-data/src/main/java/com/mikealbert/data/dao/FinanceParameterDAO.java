package com.mikealbert.data.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mikealbert.data.entity.FinanceParameter;

/**
* DAO for FinanceParameter Entity
* @author Singh
*/

public interface FinanceParameterDAO extends JpaRepository<FinanceParameter, String> {
	
	@Query("SELECT fp FROM FinanceParameter fp WHERE fp.parameterKey = ?1 AND fp.status = ?2 order by fp.effectiveFrom desc")
	public List<FinanceParameter> findByParameterKeyAndStatus(String parameterKey, String status);
	
	@Query("FROM FinanceParameter fp WHERE fp.effectiveFrom IN (SELECT MAX(fp1.effectiveFrom) FROM FinanceParameter fp1 WHERE fp1.parameterKey = ?1 and fp1.effectiveFrom <= SYSDATE) AND fp.parameterKey = ?1")
	public FinanceParameter findByParameterKey(String parameterKey);
	
	@Query("SELECT fp FROM FinanceParameter fp WHERE fp.cvalue IS NOT EMPTY AND fp.cvalue = ?1")
	public FinanceParameter findByVrbTypeCode(String vrbTypeCode);
}
