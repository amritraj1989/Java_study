package com.mikealbert.data.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mikealbert.data.entity.ExtAccAffiliate;

public interface ExtAccAffiliateDAO extends JpaRepository<ExtAccAffiliate, Long>{
	
	@Query("from ExtAccAffiliate eaaf INNER JOIN FETCH eaaf.externalAccountAddress eaa INNER JOIN FETCH eaaf.externalAccountAffiliateType eaaft where eaaf.externalAccount.externalAccountPK.cId = ?1 and eaaf.externalAccount.externalAccountPK.accountType = ?2 and eaaf.externalAccount.externalAccountPK.accountCode = ?3")
	public List<ExtAccAffiliate> findByExtAccount(Long cId, String accountType, String accountCode);
}
