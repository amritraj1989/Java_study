package com.mikealbert.data.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mikealbert.data.entity.ClientPointAccount;

/**
* DAO for ClientPointAccount Entity
* @author sibley
*/

public interface ClientPointAccountDAO extends JpaRepository<ClientPointAccount, Long> {
	@Query("SELECT cpnta " +
			"   FROM ClientPointAccount cpnta " +
			"   WHERE cpnta.externalAccount.externalAccountPK.accountCode = ?1 " +
			"		AND cpnta.externalAccount.externalAccountPK.accountType = ?2 " +
			"   	AND cpnta.externalAccount.externalAccountPK.cId = ?3 order by cpnta.clientPoint.clientSystem.description asc ")	
	public List<ClientPointAccount> findByAccountCode(String accountCode, String accountType, Long cId);
	
	@Query("SELECT cpnta " +
			"   FROM ClientPointAccount cpnta " +
			"   WHERE cpnta.externalAccount.externalAccountPK.accountCode = ?1 " +
			"   	AND cpnta.externalAccount.externalAccountPK.accountType = ?2 " +
			"   	AND cpnta.externalAccount.externalAccountPK.cId = ?3 " +
			"       AND cpnta.clientPoint.clientPointId = ?4 ")	
	public ClientPointAccount findByAccountCodePOC(String accountCode, String accountType, Long cId,  Long clientPontId );
		
	@Query("SELECT count(ccon.clientContactId) FROM ClientContact ccon " +
			"   WHERE ccon.clientPointAccount.clientPointAccountId = ?1 " +
			"       AND ccon.costCentreCode IS NULL " +
			"       AND ccon.drvInd = 'Y'")
	public Long countDrivers(Long clientPointAccountId);
	
	@Query("SELECT count(ccon.clientContactId) FROM ClientContact ccon " +
			"   JOIN ccon.clientPointAccount cpnta " +
			"   JOIN ccon.costCentreCode ccc " +
			"   WHERE cpnta.clientPointAccountId = ?1 " +
			"       AND ccc.costCentreCodePK.costCentreCode = ?2 " +
			"       AND ccc.costCentreCodePK.eaCId = ?3 " +
			"       AND ccc.costCentreCodePK.eaAccountType = ?4 " +
			"       AND ccc.costCentreCodePK.eaAccountCode = ?5 " +
			"       AND ccon.drvInd = 'Y'")
	public Long countDrivers(Long clientPointAccountId, String costCenterCode, Long cId, String accountType, String accountCode);	
}
