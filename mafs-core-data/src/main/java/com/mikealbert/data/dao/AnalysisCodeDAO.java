package com.mikealbert.data.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mikealbert.data.entity.AnalysisCategory;
import com.mikealbert.data.entity.AnalysisCode;
import com.mikealbert.data.entity.AnalysisCodePK;

/**
* DAO for AnalysisCode Entity
* @author Shafi
*/

public interface AnalysisCodeDAO extends JpaRepository<AnalysisCode, AnalysisCodePK> {
	 
		@Query("select ac FROM AnalysisCode ac WHERE ac.debitCreditMemoInd='Y' AND ac.id.categoryId = ?1 order by ac.description asc")
		public List<AnalysisCode> findByCategoryId(long categoryId);
		
}
