package com.mikealbert.data.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mikealbert.data.entity.MulQuoteEle;

/**
* DAO for MulQuoteEle Entity
*/
public interface MulQuoteEleDAO extends JpaRepository<MulQuoteEle, Long> {
	
	@Query("from MulQuoteEle where quotation.quoId = ?1 ")
	public	List<MulQuoteEle>	findMulQuoteEleByQuotationId(Long quoId);

	@Query("from MulQuoteEle where quotation.quoId = ?1 and leaseElement.lelId = ?2 ")
	public	MulQuoteEle	findMulQuoteEleByQuoIdLelId(Long quoId, Long lelId);	
}
