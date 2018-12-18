package com.mikealbert.data.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mikealbert.data.entity.QuotationCapitalElement;

/**
* DAO for QuotationCapitalElement Entity
* @author Singh
*/

public interface QuotationCapitalElementDAO extends JpaRepository<QuotationCapitalElement, Long> {
	@Query("from QuotationCapitalElement where quotationModel.qmdId = ?1")
	public List<QuotationCapitalElement> findByQmdID(Long qmdId);
	
	@Query("from QuotationCapitalElement where quotationModel.qmdId = ?1 and capitalElement.code = ?2")
	public QuotationCapitalElement findByQmdIDAndCapitalElementCode (Long qmdId, String capitalElementCode);
}

