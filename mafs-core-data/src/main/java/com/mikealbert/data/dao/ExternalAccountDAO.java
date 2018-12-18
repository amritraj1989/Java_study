package com.mikealbert.data.dao;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mikealbert.data.entity.ExternalAccount;
import com.mikealbert.data.entity.ExternalAccountPK;

/**
* DAO for ExternalAccont Entity
* @author sibley
*/
public interface ExternalAccountDAO extends JpaRepository<ExternalAccount, ExternalAccountPK>, ExternalAccountDAOCustom{
	static final String ACC_STATUS_OPEN = "O";
	static final String ACC_STATUS_CLOSED = "C";	
	static final String ACCOUNT_TYPE_CUSTOMER = "C";
	static final String ACCOUNT_TYPE_SUPPLIER = "S";
	static final String ACCOUNT_CONTEXT_MAL = "1";
	static final String ACCOUNT_CONTEXT_LTD = "2";
	
    @Query("select ea from ExternalAccount ea where LOWER(ea.accountName) like LOWER(?1) and ea.accStatus = ?2 and ea.externalAccountPK.cId = ?3 order by ea.accountName,ea.externalAccountPK.accountCode")
	public List<ExternalAccount> findByAccountNameAndAccountStatus(String accountName, String accountStatus, long contextId);

    @Query("select ea from ExternalAccount ea where LOWER(ea.accountName) like LOWER(?1) and ea.accStatus = ?2 and ea.externalAccountPK.cId = ?3 order by ea.accountName,ea.externalAccountPK.accountCode")
	public List<ExternalAccount> findByAccountNameAndAccountStatus(String accountName, String accountStatus, long contextId, Pageable pageable);

    @Query("select ea from ExternalAccount ea where LOWER(ea.accountName) like LOWER(?1) and ea.accStatus = ?2 and ea.externalAccountPK.cId = ?3 and ea.externalAccountPK.accountType IN ?4 order by ea.accountName,ea.externalAccountPK.accountCode")
	public List<ExternalAccount> findByAccountNameAndAccountStatusAndAccountTypes(String accountName, String accountStatus, long contextId, List<String> accountTypes);
    
    @Query("select ea from ExternalAccount ea where LOWER(ea.accountName) like LOWER(?1) and ea.accStatus = ?2 and ea.externalAccountPK.cId = ?3 and ea.externalAccountPK.accountType IN ?4 order by ea.accountName,ea.externalAccountPK.accountCode")
	public List<ExternalAccount> findByAccountNameAndAccountStatusAndAccountTypes(String accountName, String accountStatus, long contextId, Pageable pageable, List<String> accountTypes);
  
    @Query("select ea from ExternalAccount ea where ea.externalAccountPK.accountCode like ?1 and ea.accStatus = ?2 and ea.externalAccountPK.cId = ?3 order by ea.accountName,ea.externalAccountPK.accountCode")
	public List<ExternalAccount> findByAccountCodeAndAccountStatus(String accountCode, String accountStatus, long contextId);
	    
    @Query("select ea from ExternalAccount ea where ea.externalAccountPK.accountCode like ?1 and ea.accStatus = ?2 and ea.externalAccountPK.cId = ?3 and ea.externalAccountPK.accountType IN ?4 order by ea.accountName,ea.externalAccountPK.accountCode")
	public List<ExternalAccount> findByAccountCodeAndAccountStatusAndAccountTypes(String accountCode, String accountStatus, long contextId, List<String> accountTypes);
 
    @Query("select ea from ExternalAccount ea where ea.externalAccountPK.accountCode like ?1 and ea.accStatus = ?2 and ea.externalAccountPK.cId = ?3 and ea.externalAccountPK.accountType IN ?4 order by ea.accountName,ea.externalAccountPK.accountCode")
	public List<ExternalAccount> findByAccountCodeAndAccountStatusAndAccountTypes(String accountCode, String accountStatus, long contextId, Pageable pageable, List<String> accountTypes);
    
	@Query("select COUNT(ea) from ExternalAccount ea where LOWER(ea.accountName) like LOWER(?1) and ea.accStatus = ?2 and ea.externalAccountPK.cId = ?3 and ea.externalAccountPK.accountType IN ?4")
	public long getTotalAccountCount(String accountName, String accountStatus, long contextId, List<String> accountTypes); 
	
	@Query("select COUNT(ea) from ExternalAccount ea where ea.externalAccountPK.accountCode like ?1 and ea.accStatus = ?2 and ea.externalAccountPK.cId = ?3 and ea.externalAccountPK.accountType IN ?4")
	public long getTotalAccountCodeCount(String accountCode, String accountStatus, long contextId, List<String> accountTypes); 
	
	@Query("select ea1 from ExternalAccount ea1 where ea1.parentExternalAccount.externalAccountPK.accountCode = ?1 and ea1.accStatus = ?2  and ea1.externalAccountPK.cId = ?3 and ea1.externalAccountPK.accountType IN ?4")
	public List<ExternalAccount> findByParentAccountCodeAndAccountStatusAndAccountTypes(String parentAccountCode, String accountStatus, long contextId, List<String> accountTypes);	
	
    @Query("SELECT ea FROM ExternalAccount ea WHERE ea.externalAccountPK.accountCode = ?1 AND ea.externalAccountPK.accountType = ?2 AND ea.externalAccountPK.cId = ?3 ")
	public ExternalAccount findByAccountCodeAndAccountTypeAndCId(String accountCode, String accountType, long contextId);
	
    @Query("select ea from ExternalAccount ea where ea.externalAccountPK.accountCode like ?1 and ea.externalAccountPK.cId = ?2 and ea.externalAccountPK.accountType IN ?3 order by ea.accountName,ea.externalAccountPK.accountCode")
	public List<ExternalAccount> findAllByAccountCodeAndAccountTypes(String accountCode, long contextId, List<String> accountTypes, Pageable pageable);
    
    @Query("select ea from ExternalAccount ea where LOWER(ea.accountName) like LOWER(?1) and ea.externalAccountPK.cId = ?2 and ea.externalAccountPK.accountType IN ?3 order by ea.accountName,ea.externalAccountPK.accountCode")
	public List<ExternalAccount> findAllByAccountNameAndAccountTypes(String accountName, long contextId, Pageable pageable, List<String> accountTypes);
    
    @Query("select COUNT(ea) from ExternalAccount ea where ea.externalAccountPK.accountCode like ?1 and ea.externalAccountPK.cId = ?2 and ea.externalAccountPK.accountType IN ?3")
	public long getAllStatusAccountCodeCount(String accountCode, long contextId, List<String> accountTypes);
    
    @Query("select COUNT(ea) from ExternalAccount ea where LOWER(ea.accountName) like LOWER(?1) and ea.externalAccountPK.cId = ?2 and ea.externalAccountPK.accountType IN ?3 ")
	public long getAllStatusAccountNameCount(String accountName, long contextId, List<String> accountTypes);
}

