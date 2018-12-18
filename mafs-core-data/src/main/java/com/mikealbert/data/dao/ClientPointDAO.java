package com.mikealbert.data.dao;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.mikealbert.data.entity.ClientPoint;

/**
* DAO for ClientPoint Entity
* @author sibley
*/

public interface ClientPointDAO extends JpaRepository<ClientPoint, Long> {

	@Query("SELECT cpnt " +
			"   FROM ClientPoint cpnt " +
			"       LEFT OUTER JOIN cpnt.clientPointAccounts cpnta WITH cpnta.externalAccount.externalAccountPK.accountCode = ?1" +
			"   WHERE cpnta IS NULL ")	
	public List<ClientPoint> findByAccountCode(String accountCode);	
	
	@Query("SELECT cpnt " +
			"   FROM ClientPoint cpnt " +
			"       LEFT OUTER JOIN cpnt.clientPointAccounts cpnta WITH cpnta.externalAccount.externalAccountPK.accountCode = ?1" +
			"   WHERE cpnt.clientSystem.clientSystemId IS ?2 ")	
	public List<ClientPoint> findByAccountCodeClientSystem(String accountCode, Long clientSystemId);	
	
	@Query("SELECT cpnt " +
			"   FROM ClientPoint cpnt " +
			"       LEFT OUTER JOIN cpnt.clientPointAccounts cpnta WITH cpnta.externalAccount.externalAccountPK.accountCode = ?1" +
			"   WHERE cpnt.clientPointId IS ?2 ")	
	public List<ClientPoint> findByAccountCodePOC(String accountCode, long clientPointId);	
	
	//may need to change to from clientPontAccounts instead of left outer join
			
}
