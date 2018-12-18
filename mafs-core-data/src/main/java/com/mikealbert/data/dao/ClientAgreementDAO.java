package com.mikealbert.data.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mikealbert.data.entity.ClientAgreement;

public interface ClientAgreementDAO extends JpaRepository<ClientAgreement, Long> {
	
	@Query("FROM ClientAgreement ca WHERE ca.externalAccount.externalAccountPK.cId = ?1 AND ca.externalAccount.externalAccountPK.accountType = ?2 AND ca.externalAccount.externalAccountPK.accountCode = ?3 AND ca.activeInd = 'Y' ORDER BY ca.contractAgreement.agreementDescription ASC")
	public List<ClientAgreement> findByExternalAccount(Long cId, String accountType, String accountCode);
	
	@Query("SELECT count(*) FROM ClientAgreement ca WHERE lower(ca.agreementNumber) = lower(?1) AND ca.clientAgreementId != ?2")
	public long findByAgreementNumberCount(String agreementNumber, Long clientAgreementId);
	
	@Query("SELECT ca FROM ClientAgreement ca WHERE lower(ca.agreementNumber) = lower(?1)")
	public ClientAgreement findByAgreementNumber(String agreementNumber);
	
	@Query("SELECT count(*) FROM ClientAgreement ca WHERE lower(ca.contractAgreement) = lower(?1) AND ca.externalAccount.externalAccountPK.cId = ?2 AND ca.externalAccount.externalAccountPK.accountType = ?3 AND ca.externalAccount.externalAccountPK.accountCode = ?4")
	public long findByContractAgreementAndClientCount(String contractAgreement, Long cId, String accountType, String accountCode);
	
	@Query("SELECT ca FROM ClientAgreement ca WHERE lower(ca.contractAgreement) = lower(?1) AND ca.externalAccount.externalAccountPK.cId = ?2 AND ca.externalAccount.externalAccountPK.accountType = ?3 AND ca.externalAccount.externalAccountPK.accountCode = ?4 AND ca.activeInd = 'Y'")
	public ClientAgreement findByContractAgreementAndClient(String contractAgreement, Long cId, String accountType, String accountCode);
	
}
