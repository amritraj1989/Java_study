package com.mikealbert.data.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mikealbert.data.entity.BuyerLimit;
/**
* DAO for BuyerLimit Entity
* @author Disbrow
*/

public interface BuyerLimitDAO extends JpaRepository<BuyerLimit, String> {
	
	@Query("SELECT bl FROM BuyerLimit bl WHERE bl.buyerLimitPK.cId = ?1 and bl.buyerLimitPK.docType = ?2 and bl.buyerLimitPK.employeeNo = ?3 and bl.buyerLimitPK.tranType = ?4")
	public BuyerLimit findBuyerLimit(Long cId, String docType, String employeeNo, String tranType);	
}
