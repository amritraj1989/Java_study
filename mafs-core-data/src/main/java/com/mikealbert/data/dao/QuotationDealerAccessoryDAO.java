package com.mikealbert.data.dao;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mikealbert.data.entity.QuotationDealerAccessory;

/**
* DAO for QuotationDealerAccessory Entity
* @author Sibley
*/

public interface QuotationDealerAccessoryDAO extends JpaRepository<QuotationDealerAccessory, Long> {
	@Query("select sum(residualAmt) from QuotationDealerAccessory where quotationModel.qmdId= ?1")
	public BigDecimal	getSumOfResidual(Long qmdId);
	
	@Query("SELECT qda FROM QuotationDealerAccessory qda WHERE qda.externalReferenceNo = ?1")
	public List<QuotationDealerAccessory> findByExternalReferenceNo(String upfitterQuoteNo);
	
	@Query("select qda from QuotationDealerAccessory qda where qda.quotationModel.qmdId= ?1")
	public List<QuotationDealerAccessory> findByQmdId(Long qmdId);
}
