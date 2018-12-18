package com.mikealbert.data.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mikealbert.data.entity.ClientContractServiceElement;

public interface ClientContractServiceElementDAO extends JpaRepository<ClientContractServiceElement, Long> {
	
	@Query("FROM ClientContractServiceElement ccse WHERE ccse.clientAgreement.externalAccount.externalAccountPK.cId = ?1 AND ccse.clientAgreement.externalAccount.externalAccountPK.accountType = ?2 AND ccse.clientAgreement.externalAccount.externalAccountPK.accountCode = ?3 ORDER BY ccse.leaseElement.elementName ASC")
	public List<ClientContractServiceElement> findByExternalAccount(Long cId, String accountType, String accountCode);
	
	@Query("SELECT count(*) FROM ClientContractServiceElement ccse WHERE ccse.leaseElement.elementName = ?1 AND ccse.clientAgreement.externalAccount.externalAccountPK.cId = ?2 AND ccse.clientAgreement.externalAccount.externalAccountPK.accountType = ?3 AND ccse.clientAgreement.externalAccount.externalAccountPK.accountCode = ?4 ")
	public long findByElementNameAndClientCount(String elementName, Long cId, String accountType, String accountCode);
}
