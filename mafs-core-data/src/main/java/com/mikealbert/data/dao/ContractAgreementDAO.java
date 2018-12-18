package com.mikealbert.data.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mikealbert.data.entity.ContractAgreement;

/**
* DAO for ContractAgreements
*/
public interface ContractAgreementDAO extends JpaRepository<ContractAgreement, String>{   
	
	@Query("FROM ContractAgreement ca ORDER BY ca.agreementDescription ASC")
	public List<ContractAgreement> getAllContractAgreements();
}

