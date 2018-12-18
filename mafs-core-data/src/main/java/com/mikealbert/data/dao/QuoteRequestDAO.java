package com.mikealbert.data.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mikealbert.data.entity.DriverGradeGroupCode;
import com.mikealbert.data.entity.QuoteRequest;
import com.mikealbert.data.entity.QuoteRequestDepreciationMethod;
import com.mikealbert.data.entity.QuoteRequestPaymentType;

public interface QuoteRequestDAO extends JpaRepository<QuoteRequest, Long>, QuoteRequestDAOCustom{
	
	@Query("SELECT qrq FROM QuoteRequest qrq INNER JOIN qrq.quoteRequestQuotes qrqq WHERE qrqq.quoId = ?1")
	public List<QuoteRequest> findByQuoId(Long quoId);
	
	@Query("FROM QuoteRequestDepreciationMethod qrdp ORDER BY qrdp.name")
	public List<QuoteRequestDepreciationMethod> getAllQuoteRequestDepreciationMethods();
	
	@Query("FROM QuoteRequestDepreciationMethod qrdp WHERE qrdp.code = ?1")
	public QuoteRequestDepreciationMethod getQuoteRequestDepreciationMethodByCode(String code);
	
	@Query("FROM QuoteRequestPaymentType qrpt ORDER BY qrpt.name")
	public List<QuoteRequestPaymentType> getAllQuoteRequestPaymentTypes();
	
	@Query("FROM QuoteRequestPaymentType qrpt WHERE qrpt.code = ?1")
	public QuoteRequestPaymentType getQuoteRequestPaymentTypeByCode(String code);
	
	@Query("SELECT dggc FROM DriverGradeGroupCode dggc, ExternalAccountGradeGroup eagg"
			+ " WHERE dggc.driverGradeGroup.driverGradeGroup = eagg.driverGradeGroup.driverGradeGroup AND eagg.externalAccounts.externalAccountPK.cId = ?1"
			+ " AND eagg.externalAccounts.externalAccountPK.accountType = ?2"
			+ " AND eagg.externalAccounts.externalAccountPK.accountCode = ?3"
			+ " ORDER BY dggc.driverGradeGroup ASC")
	public List<DriverGradeGroupCode> getGradeGroupsByAccount(Long cId, String accountType, String accountCode);
		
}
