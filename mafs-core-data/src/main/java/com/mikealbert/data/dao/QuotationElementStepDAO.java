
/**
 * QuotationElementStepDAO.java
 * mafs-core-data
 * Mar 5, 2013
 * 6:22:07 PM
 */
package com.mikealbert.data.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.mikealbert.data.entity.QuotationElementStep;

/**
 * @author anand.mohan
 *
 */
public interface QuotationElementStepDAO  extends JpaRepository< QuotationElementStep, Long> {
	@Query("from QuotationElementStep where quotationElement.qelId = ?1")
	public List<QuotationElementStep>	findByQuoteElementId(Long quoteElementId);
	
	@Modifying
	@Query("delete from QuotationElementStep where quotationElement.qelId = ?1 ")
	public void	deleteByQuoteElementId(Long quoteElementId);
	
}
