package com.mikealbert.data.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mikealbert.data.entity.VrbDiscount;

public interface VrbDiscountDAO extends VrbDiscountDAOCustom, JpaRepository<VrbDiscount, Long>{
	
	@Query("from VrbDiscount where eaAccountCode = ?1 and mdlMdlId = ?2")
	public List<VrbDiscount> findByAccountCodeAndModel(String accountCode, Long modelId);
	
	@Query("SELECT vrb FROM VrbDiscount vrb WHERE vrb.eaAccountCode = ?1 and vrb.mrgMrgId = ?2")
	public List<VrbDiscount> findByAccountCodeAndModelRange(String accountCode, Long mrgId);
	
	@Query("SELECT vrb FROM VrbDiscount vrb WHERE vrb.eaAccountCode = ?1 and vrb.makMakId = ?2")
	public List<VrbDiscount> findByAccountCodeAndMake(String accountCode, Long makId);		
}
