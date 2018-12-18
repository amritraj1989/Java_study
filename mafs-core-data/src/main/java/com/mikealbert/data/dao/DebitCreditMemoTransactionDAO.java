package com.mikealbert.data.dao;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mikealbert.data.entity.DebitCreditMemoTransaction;
import com.mikealbert.data.entity.ExternalAccountPK;

/**
* DAO for DebitCreditMemoTransaction Entity
*/

public interface DebitCreditMemoTransactionDAO extends JpaRepository<DebitCreditMemoTransaction, Long> , DebitCreditMemoTransactionDAOCustom {
	
	@Query("SELECT dct FROM DebitCreditMemoTransaction dct WHERE dct.unitNo  = ?1 and  dct.categoryId  =?2 and dct.analysisCode  =?3 and  dct.netAmount  =?4  and  dct.rentApplicableDate  =?5 and dct.externalAccount.externalAccountPK =?6 and dct.dcmtDcmtId = ?7 ")
	public List<DebitCreditMemoTransaction> findDebitCreditMemoForRentApplicableDate(String unitNo , Long categoryId , String analysisCode, BigDecimal netAmount, Date rentApplicableDate, ExternalAccountPK clientPk, Long dcmtId );

	@Query("SELECT dct FROM DebitCreditMemoTransaction dct WHERE dct.unitNo  = ?1 and  dct.categoryId  =?2 and dct.analysisCode  =?3 and  dct.netAmount  =?4  and  dct.externalAccount.externalAccountPK =?5 and dct.dcmtDcmtId = ?6 and dct.rentApplicableDate is null  ")
	public List<DebitCreditMemoTransaction> findDebitCreditMemoWithoutRentApplicableDate(String unitNo , Long categoryId , String analysisCode, BigDecimal netAmount, ExternalAccountPK clientPk, Long dcmtId );

}
