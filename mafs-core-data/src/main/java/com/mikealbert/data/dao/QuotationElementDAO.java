package com.mikealbert.data.dao;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mikealbert.data.entity.QuotationElement;

/**
* DAO for QuotationElement Entity
* @author Singh
*/

public interface QuotationElementDAO extends QuotationElementDAOCustom, JpaRepository<QuotationElement, Long> {
	@Query("from QuotationElement where quotationModel.qmdId = ?1 and leaseElement.elementType = 'FINANCE' " +
			"and quotationDealerAccessory.qdaId is null and quotationModelAccessory.qmaId is null and includeYn = 'Y'")
	public	QuotationElement	findMainQuoteElement(Long qmdId);
	
	@Query("select sum(rental) from QuotationElement where quotationModel.qmdId = ?1 and leaseElement.elementType = 'FINANCE'  ")
	public	BigDecimal	getSumOfRental(Long qmdId);
	
	@Query("select sum(rental) from QuotationElement where quotationModel.qmdId = ?1 and leaseElement.elementType != 'FINANCE'  ")
	public	BigDecimal	getSumOfService(Long qmdId);
	
	@Query(" from QuotationElement where quotationModel.qmdId = ?1 and leaseElement.lelId = ?2 and acceptedInd = 'Y'  ")
	public	List<QuotationElement>	findByQmdIdAndLeaseEleId(Long qmdId ,Long leaseElementId);
	
	@Query(" from QuotationElement where leaseElement.lelId = ?1 ")
	public QuotationElement	findByLeaseElementId(Long leaseElementId);
	
	@Query(" from QuotationElement where quotationModel.qmdId = ?1 and quotationDealerAccessory.qdaId =?2 ")
	public List<QuotationElement> findByQmdIdAndQdaId(Long qmdId, Long qdaId);
}
