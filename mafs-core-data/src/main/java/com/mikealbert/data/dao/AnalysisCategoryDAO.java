package com.mikealbert.data.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mikealbert.data.entity.AnalysisCategory;

/**
* DAO for AnalysisCategory Entity
* @author Shafi
*/

public interface AnalysisCategoryDAO extends JpaRepository<AnalysisCategory, Long> {
	 
		@Query("SELECT ac FROM AnalysisCategory ac WHERE ac.debitCreditMemoInd = 'Y' order by ac.analysisCategory asc")
		public List<AnalysisCategory> findByDebitCreditMemoInd();
		
}
