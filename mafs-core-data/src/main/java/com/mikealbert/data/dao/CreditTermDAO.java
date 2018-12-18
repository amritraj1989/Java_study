package com.mikealbert.data.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mikealbert.data.entity.CreditTerm;
import com.mikealbert.data.entity.CreditTermPK;

public interface CreditTermDAO extends JpaRepository<CreditTerm, CreditTermPK>{
	
	@Query("SELECT ct FROM CreditTerm ct WHERE  ct.id.cId = ?1 and ct.id.extAccType = ?2 and ct.id.creditTermsCode = ?3")
	public List<CreditTerm> findCreditTerm(long cID, String extAccType, String creditTermsCode);
		
}	