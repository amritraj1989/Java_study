package com.mikealbert.data.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mikealbert.data.entity.Report;

/**
* DAO for Report Entity
* @author Amritraj Singh
*/

public interface ReportDAO extends JpaRepository<Report, Long> {
	 
		@Query("SELECT rt FROM Report rt WHERE rt.reportName = ?1")
		public Report findByReportName(String code);
		
}
