
/**
 * QuotationScheduleDAO.java
 * mafs-core-data
 * Mar 6, 2013
 * 6:48:07 PM
 */
package com.mikealbert.data.dao;

import java.math.BigDecimal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mikealbert.data.entity.QuotationSchedule;

/**
 * @author anand.mohan
 *
 */
public interface QuotationScheduleDAO extends JpaRepository<QuotationSchedule, Long>{
	@Query("select SUM(amount) from QuotationSchedule where quotationElement.qelId = ?1 and quotationElement.quotationModel.qmdId = ?2 and quotationElement.leaseElement.lelId = ?3 and docId is not null"  )
	public BigDecimal	sumOfAmountByQuoteElementAndQmdIdAndLease(Long quoteEleId, Long qmdId, Long leaseEleId);
}
