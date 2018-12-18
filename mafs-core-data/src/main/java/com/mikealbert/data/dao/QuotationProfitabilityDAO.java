
/**
 * QuotationProfitabilityDAO.java
 * mafs-core-data
 * Mar 4, 2013
 * 4:25:13 PM
 */
package com.mikealbert.data.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mikealbert.data.entity.QuotationProfitability;

/**
 * @author anand.mohan
 *
 */
public interface QuotationProfitabilityDAO extends JpaRepository<QuotationProfitability, Long>{
	
    	@Query("SELECT qp FROM QuotationProfitability qp WHERE qp.quotationModel.qmdId = ?1 and qp.profitType = ?2 and qp.profitSource = ?3")
	public	QuotationProfitability findByQmdIdProfitTypeAndProfitSource(Long qmdId,String profitType,String profitSource);
	
	@Query("SELECT qp FROM QuotationProfitability qp WHERE qp.quotationModel.qmdId = ?1 and qp.profitType = ?2 ")
	public	List<QuotationProfitability> findByQmdIdAndProfitType(Long qmdId,String profitType);
	
	@Query("SELECT qp FROM QuotationProfitability qp WHERE qp.quotationModel.qmdId = ?1 and qp.profitSource = ?2")
	public	List<QuotationProfitability> findByQmdIdAndProfitSource(Long qmdId,String profitSource);
}
