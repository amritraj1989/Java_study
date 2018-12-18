package com.mikealbert.data.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mikealbert.data.entity.InterestTypeCode;
/**
* DAO for interestType Entity
* @author Amritraj
*/

public interface InterestTypeCodeDAO extends JpaRepository<InterestTypeCode, String> {
	
	@Query("SELECT itc FROM InterestTypeCode itc where itc.interestType = ?1")
	public InterestTypeCode findInterestTypeByCode(String interestType);
}
