package com.mikealbert.data.dao;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.mikealbert.data.entity.DealerAccessoryPrice;

/**
* DAO for DealerAccessoryPrice Entity
* @author sibley
*/

public interface DealerAccessoryPriceDAO extends JpaRepository<DealerAccessoryPrice, Long> {
	@Query("SELECT dpl FROM DealerAccessoryPrice dpl " +
		    " INNER JOIN dpl.dealerAccessory dac" +
			" WHERE dpl.dealerAccessory.dacId = ?1" +
			"     AND dpl.payeeAccount.externalAccountPK.cId = ?2 " +
			"     AND dpl.payeeAccount.externalAccountPK.accountType = ?3 " +
			"     AND dpl.payeeAccount.externalAccountPK.accountCode = ?4 ")
	public List<DealerAccessoryPrice> findByAccessoryAndVendor(Long dacId, Long cId, String accountType, String accountCode);
	
	@Query("SELECT dpl FROM DealerAccessoryPrice dpl " +
		    " INNER JOIN dpl.dealerAccessory dac" +
			" WHERE dpl.dealerAccessory.dacId = ?1" +
			"     AND dpl.payeeAccount.externalAccountPK.cId IS NULL " +
			"     AND dpl.payeeAccount.externalAccountPK.accountType IS NULL " +
			"     AND dpl.payeeAccount.externalAccountPK.accountCode IS NULL ")
	public List<DealerAccessoryPrice> findByAccessoryAndNullVendor(Long dacId);	
	
	@Query("SELECT dpl FROM DealerAccessoryPrice dpl " +
		    " INNER JOIN dpl.dealerAccessory dac" +
			" WHERE dpl.dealerAccessory.dacId = ?1" +
			"     AND dpl.payeeAccount.externalAccountPK.cId = ?2 " +
			"     AND dpl.payeeAccount.externalAccountPK.accountType = ?3 " +
			"     AND dpl.payeeAccount.externalAccountPK.accountCode = ?4 " +
			"     AND dpl.upfitterQuote IS NULL ")
	public List<DealerAccessoryPrice> findByAccessoryAndVendorAndNullVendorQuote(Long dacId, Long cId, String accountType, String accountCode);	
	
	@Query("SELECT dpl FROM DealerAccessoryPrice dpl " +
		    " INNER JOIN dpl.dealerAccessory dac" +
			" INNER JOIN dpl.upfitterQuote ufq" +
			" INNER JOIN dac.model mdl" +
			" WHERE ufq.ufqId = ?1" +
			"     AND mdl.modelId = ?2 ")
	public List<DealerAccessoryPrice> findByUpfitterQuoteAndTrim(Long ufqId, Long mdlId);	
}
