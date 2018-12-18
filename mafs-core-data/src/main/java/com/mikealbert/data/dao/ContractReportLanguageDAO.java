package com.mikealbert.data.dao;

import java.util.Date;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mikealbert.data.entity.ContractReportLanguage;


public interface ContractReportLanguageDAO extends JpaRepository<ContractReportLanguage, Long> {
	
	@Query("from ContractReportLanguage where reportName = 'MMSCHD_RPT' "
			+ "and productCode = 'CE_LTD' and effectiveFromDate < ?1 and location = 1 "
			+ "and (effectiveToDate is  null or ( effectiveFromDate < effectiveToDate) )")
	public	ContractReportLanguage	findDefaultDriverInstructions(Date quoteDate);
	
	@Query("from ContractReportLanguage where reportName = 'MMSCHD_RPT' "
			+ "and productCode = 'CE_LTD' and effectiveFromDate < ?1  and location = 2 " 
			+ "and (effectiveToDate is  null or ( effectiveFromDate < effectiveToDate) )")
	public	ContractReportLanguage findNonUSAText(Date quoteDate);
	
	
	@Query("from ContractReportLanguage where reportName = 'MMSCHD_RPT' "
			+ "and productCode = 'CE_LTD' and effectiveFromDate < ?1  and location = 3 " 
			+ "and (effectiveToDate is  null or ( effectiveFromDate < effectiveToDate) )")
	public	ContractReportLanguage findUSAText(Date quoteDate);
	
}
