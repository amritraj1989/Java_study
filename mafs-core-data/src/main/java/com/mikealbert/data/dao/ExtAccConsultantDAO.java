package com.mikealbert.data.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.mikealbert.data.entity.ExtAccConsultant;

public interface ExtAccConsultantDAO extends JpaRepository<ExtAccConsultant, Long>{
	
	@Query("from ExtAccConsultant eac where eac.externalAccounts.externalAccountPK.cId = ?1 and eac.externalAccounts.externalAccountPK.accountType = ?2 and eac.externalAccounts.externalAccountPK.accountCode = ?3 and eac.roleType = ?4 and eac.defaultInd = 'Y'")
	public ExtAccConsultant findByExtAccountAndRoleType(Long cId, String accountType, String accountCode, String roleType);
}
