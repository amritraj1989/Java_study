
/**
 * QuotationStepStructureDAO.java
 * mafs-core-data
 * Jun 21, 2013
 * 3:10:01 PM
 */
package com.mikealbert.data.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mikealbert.data.entity.QuotationStepStructure;
import com.mikealbert.data.entity.QuotationStepStructurePK;

/**
 * @author anand.mohan
 *
 */
public interface QuotationStepStructureDAO extends JpaRepository<QuotationStepStructure, QuotationStepStructurePK> {
	String qryString = "select qss from QuotationStepStructure qss where qss.quotationModel.qmdId = ?1 " +
			"and qss.id.fromPeriod >= (select max(qssi.id.fromPeriod) from QuotationStepStructure qssi w" +
			"here qssi.quotationModel.qmdId  = ?1 and qssi.id.fromPeriod <= ?2 ) order by qss.id.fromPeriod ASC";

	@Query(qryString)
	public List<QuotationStepStructure> findByQmdIdAndStartPeriod(Long qmdId, Long startPeriod);
	

	@Query("select qss from QuotationStepStructure qss where qss.quotationModel.qmdId  = ?1 order by qss.id.fromPeriod ASC")
	public List<QuotationStepStructure> findByQmdId(Long qmdId);
}
