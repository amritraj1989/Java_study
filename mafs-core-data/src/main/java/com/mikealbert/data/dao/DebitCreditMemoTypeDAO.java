package com.mikealbert.data.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mikealbert.data.entity.DebitCreditMemoType;

/**
 * DAO for DebitCreditMemoType Entity
 * 
 */

public interface DebitCreditMemoTypeDAO extends JpaRepository<DebitCreditMemoType, Long> {
	
	@Query("SELECT dc FROM DebitCreditMemoType dc ORDER BY dc.debitCreditType")
	public List<DebitCreditMemoType> getDebitCreditMemoTypeList();

}
