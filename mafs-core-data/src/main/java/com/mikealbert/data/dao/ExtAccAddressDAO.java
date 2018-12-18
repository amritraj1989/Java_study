package com.mikealbert.data.dao;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.mikealbert.data.entity.ExtAccAddress;
import com.mikealbert.data.entity.ExternalAccountGradeGroup;

/**
* DAO for ExtAccAddress Entity
* @author ravresh
*/
public interface ExtAccAddressDAO extends JpaRepository<ExternalAccountGradeGroup, Long>{
	
	@Query("from ExtAccAddress eaa where eaa.externalAccount.externalAccountPK.cId = ?1 and eaa.externalAccount.externalAccountPK.accountType =?2 and eaa.externalAccount.externalAccountPK.accountCode =?3")
	public List<ExtAccAddress> getExtAccAddressessByAccount(long cid, String accountType, String accountCode);	
	
    
}

