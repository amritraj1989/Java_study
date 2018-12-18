package com.mikealbert.data.dao;

import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.mikealbert.data.entity.UpfitterQuote;

/**
* DAO for UpfitterQuote Entity
* @author sibley
*/

public interface UpfitterQuoteDAO extends JpaRepository<UpfitterQuote, Long> {
	@Query("SELECT ufq FROM UpfitterQuote ufq WHERE ufq.externalAccount.externalAccountPK.cId = ?1 AND ufq.externalAccount.externalAccountPK.accountType = ?2 AND ufq.externalAccount.externalAccountPK.accountCode = ?3 ")
	public List<UpfitterQuote> findByAccount(long cId, String accountType, String accountCode);
	
	@Query("SELECT ufq FROM UpfitterQuote ufq WHERE ufq.externalAccount.externalAccountPK.cId = ?1 AND ufq.externalAccount.externalAccountPK.accountType = ?2 AND ufq.externalAccount.externalAccountPK.accountCode = ?3 AND UPPER(ufq.quoteNumber) = UPPER(?4)")
	public List<UpfitterQuote> findByAccountAndQuoteNumber(long cId, String accountType, String accountCode, String quoteNumber);
	
	@Query("SELECT DISTINCT ufq "
			+ "FROM UpfitterQuote ufq "
			+ "    INNER JOIN ufq.dealerAccessoryPrices dpl"
			+ "    LEFT JOIN ufq.vehicleConfigUpfitQuotes vuq "
			+ "WHERE ufq.externalAccount.externalAccountPK.cId = ?1 "
			+ "    AND ufq.externalAccount.externalAccountPK.accountType = ?2 "
			+ "AND ufq.externalAccount.externalAccountPK.accountCode = ?3 "
			+ "AND dpl.dealerAccessory.model.modelId = ?4 "
			+ "AND UPPER(ufq.quoteNumber) LIKE UPPER(?5)"
			+ "AND ufq.vehicleConfigUpfitQuotes  IS EMPTY")	
	public List<UpfitterQuote> findByUpfitterAndTrimAndQuoteNumberWithoutConfiguration(long cId, String accountType, String accountCode, Long modelId, String quoteNumber, Pageable page);
	
	@Query("SELECT DISTINCT ufq "
			+ "FROM UpfitterQuote ufq "
			+ "    INNER JOIN ufq.vehicleConfigUpfitQuotes vuq "
			+ "    INNER JOIN vuq.vehicleConfigGrouping vcg "
			+ "    INNER JOIN vcg.vehicleConfiguration vcf "
			+ "    INNER JOIN vcf.vehicleConfigModels vcm "
			+ "WHERE ufq.externalAccount.externalAccountPK.cId = ?1 "
			+ "    AND ufq.externalAccount.externalAccountPK.accountType = ?2 "
			+ "    AND ufq.externalAccount.externalAccountPK.accountCode = ?3  "
			+ "    AND ?4 = COALESCE(vcm.mfgCode, ?4) "			
			+ "    AND ?5 = COALESCE(vcm.year, ?5) "
			+ "    AND ?6 = COALESCE(vcm.make, ?6) "
			+ "    AND ?7 = COALESCE(vcm.makeModelRange.mrgId, ?7) "
			+ "    AND ?8 = COALESCE(vcm.model.modelId, ?8)"
			+ "    AND UPPER(ufq.quoteNumber) LIKE UPPER(?9)")
	public List<UpfitterQuote> findByUpfitterAndTrimAndQuoteNumberWithConfiguration(long cId, String accountType, String accountCode, String mfgCode, String year, String make, Long mrgId, Long mdlId, String searchCriteria, Pageable page);
	
	@Query("SELECT DISTINCT ufq "
			+ "FROM UpfitterQuote ufq "
			+ "    INNER JOIN ufq.dealerAccessoryPrices dpl "
			+ "    INNER JOIN ufq.vehicleConfigUpfitQuotes vuq "
			+ "    INNER JOIN vuq.vehicleConfigGrouping vcg "
			+ "    INNER JOIN vcg.vehicleConfiguration vcf "
			+ "    INNER JOIN vcf.vehicleConfigModels vcm "
			+ "WHERE ufq.externalAccount.externalAccountPK.cId = ?1 "
			+ "    AND ufq.externalAccount.externalAccountPK.accountType = ?2 "
			+ "    AND ufq.externalAccount.externalAccountPK.accountCode = ?3  "
			+ "    AND ?4 = COALESCE(vcm.mfgCode, ?4) "			
			+ "    AND ?5 = COALESCE(vcm.year, ?5) "
			+ "    AND ?6 = COALESCE(vcm.make, ?6) "
			+ "    AND ?7 = COALESCE(vcm.makeModelRange.mrgId, ?7) "
			+ "    AND ?8 = COALESCE(vcm.model.modelId, ?8)"
			+ "    AND dpl IS NOT EMPTY "
			+ "    AND dpl.dealerAccessory.model.modelId = ?8 "
			+ "    AND UPPER(ufq.quoteNumber) LIKE UPPER(?9)")
	public List<UpfitterQuote> findByUpfitterAndTrimAndQuoteNumberWithConfigurationAndPricing(long cId, String accountType, String accountCode, String mfgCode, String year, String make, Long mrgId, Long mdlId, String searchCriteria, Pageable page);	
}
