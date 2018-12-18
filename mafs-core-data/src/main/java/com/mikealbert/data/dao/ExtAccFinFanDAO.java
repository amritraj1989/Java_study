package com.mikealbert.data.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.mikealbert.data.entity.ExtAccFinFan;

public interface ExtAccFinFanDAO extends JpaRepository<ExtAccFinFan, Long>{
	
	@Query("SELECT eaff FROM ExtAccFinFan eaff "
			+ " JOIN eaff.externalAccount ea "
			+ " JOIN eaff.make mak "
			+ " WHERE ea.externalAccountPK.cId = ?1 "
			+ "     AND ea.externalAccountPK.accountType = ?2 "
			+ "     AND ea.externalAccountPK.accountCode = ?3 "
			+ "     AND mak.makId = ?4")
	public ExtAccFinFan findByAccountAndMake(Long cId, String accountType, String accountCode, Long makId);
}
