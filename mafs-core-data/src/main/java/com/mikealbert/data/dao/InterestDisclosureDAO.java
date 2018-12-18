package com.mikealbert.data.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mikealbert.data.entity.InterestDisclosure;

public interface InterestDisclosureDAO extends JpaRepository<InterestDisclosure, Long>{
	
	@Query("SELECT intDisc FROM InterestDisclosure intDisc"
			+ " WHERE intDisc.leaseType = ?1"
			+ " AND intDisc.interestRateType = ?2"
			+ " AND intDisc.preConIntFlag = ?3"
			+ " AND intDisc.preConCostFlag = ?4"
			+ " AND intDisc.formalExt = ?5"
			+ " AND intDisc.disclosureInd = ?6"
			+ " AND intDisc.variableRate = ?7"
			+ " AND intDisc.dateTo IS NULL OR intDisc.dateTo >=trunc(SYSDATE)")
	public InterestDisclosure findInterestDisclosure(String leaseType, String interestRateType, String preConIntFlag, String preConCostFlag, String formalExt, String disclosureInd, String variableRate);	
}
