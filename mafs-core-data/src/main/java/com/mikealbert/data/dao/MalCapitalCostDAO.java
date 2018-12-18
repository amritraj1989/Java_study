package com.mikealbert.data.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mikealbert.data.entity.MalCapitalCost;

/**
* DAO for MalCapitalCost Entity
* @author Singh
*/

public interface MalCapitalCostDAO extends JpaRepository<MalCapitalCost, Long> {
	
	@Query("SELECT mcc FROM MalCapitalCost mcc WHERE mcc.doclDocId= ?1")
	public  List<MalCapitalCost>  findMalCapitalCostByDoc(Long docId);
	
	@Query("SELECT mcc FROM MalCapitalCost mcc WHERE mcc.qmdQmdId= ?1")
	public  List<MalCapitalCost>  findMalCapitalCostByFinalizeQuote(Long qmdId);
	
	
	@Query("SELECT mcc FROM MalCapitalCost mcc WHERE mcc.qmdQmdId= ?1 and mcc.elementId = ?2")
	public MalCapitalCost	findByQmdIdAndElementId(Long qmdId, Long elementId);
}
