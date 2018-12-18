package com.mikealbert.data.dao;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import com.mikealbert.data.entity.QuoteElementParameter;

/**
* DAO for QuoteElementParameter Entity
* @author Singh
*/

public interface QuoteElementParameterDAO extends JpaRepository<QuoteElementParameter, Long> {
	
	@Query("from QuoteElementParameter where quotationElement.qelId = ?1")
	public List<QuoteElementParameter>	findByQuoteElementId(Long quoteElementId);
	
	@Modifying
	@Query("delete from QuoteElementParameter where quotationElement.qelId = ?1 ")
	public void	deleteByQuoteElementId(Long quoteElementId);
}
