package com.mikealbert.data.dao;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mikealbert.data.entity.ClientContact;

/**
* DAO for ClientContact Entity
* @author sibley
*/


public interface ClientContactDAO extends ClientContactDAOCustom, JpaRepository<ClientContact, Long>{
	@Query("SELECT ccon FROM ClientContact ccon " +
            "  JOIN ccon.clientPointAccount cpnta " +
			"  WHERE cpnta.clientPointAccountId = ?1 " +
            "      AND ccon.costCentreCode.costCentreCodePK.costCentreCode = ?2 " +
			"      AND ccon.costCentreCode.costCentreCodePK.eaCId = ?3 " +
			"      AND ccon.costCentreCode.costCentreCodePK.eaAccountType = ?4 " +
			"      AND ccon.costCentreCode.costCentreCodePK.eaAccountCode = ?5 ")
	public List<ClientContact> findByCostCenter(Long clientPointAccountId, String costCenterCode, Long cId, String accountType, String accountCode);
		
	@Query("SELECT ccon FROM ClientContact ccon " +
			"   JOIN ccon.clientPointAccount cpnta " +
			"   WHERE cpnta.clientPointAccountId = ?1 " +
			"       AND ccon.costCentreCode IS NULL " +
			"       AND ccon.drvInd = 'Y'")
	public ClientContact findDriverByClientPOC(Long clientPointAccountId);
	
	@Query("SELECT ccon FROM ClientContact ccon " +
			"   JOIN ccon.clientPointAccount cpnta " +
			"   JOIN ccon.costCentreCode ccc" +
			"   WHERE cpnta.clientPointAccountId = ?1 " +
			"       AND ccc.costCentreCodePK.costCentreCode = ?2 " +
			"       AND ccc.costCentreCodePK.eaCId = ?3 " +
			"       AND ccc.costCentreCodePK.eaAccountType = ?4 " +
			"       AND ccc.costCentreCodePK.eaAccountCode = ?5 " +
			"       AND ccon.drvInd = 'Y'")
	public ClientContact findDriverByClientPOCCostCenter(Long clientPointAccountId, String costCenterCode, Long cId, String accountType, String accountCode);		
}
