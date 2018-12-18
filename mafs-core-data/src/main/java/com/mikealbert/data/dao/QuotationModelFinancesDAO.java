
/**
 * QuotationModelFinancesDAO.java
 * mafs-core-data
 */
package com.mikealbert.data.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mikealbert.data.entity.QuotationModelFinances;

public interface QuotationModelFinancesDAO extends JpaRepository<QuotationModelFinances, Long>{
	@Query("SELECT qmf FROM QuotationModelFinances qmf WHERE qmf.quotationModel.qmdId = ?1 and qmf.parameterKey = ?2")
	public QuotationModelFinances findByQmdIdAndParameterKey(Long qmdId, String parameterKey);
	
	@Query("SELECT qmf FROM QuotationModelFinances qmf WHERE qmf.quotationModel.qmdId = ?1")
	public List<QuotationModelFinances> findByQmdId(Long qmdId);
}
