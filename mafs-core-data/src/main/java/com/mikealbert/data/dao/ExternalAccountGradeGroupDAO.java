package com.mikealbert.data.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mikealbert.data.entity.ExternalAccountGradeGroup;

public interface ExternalAccountGradeGroupDAO  extends JpaRepository<ExternalAccountGradeGroup, Long>{
	@Query("FROM ExternalAccountGradeGroup eagg " +
			"   WHERE eagg.externalAccounts.externalAccountPK.cId = ?1 " +
			"       and eagg.externalAccounts.externalAccountPK.accountType = ?2 " +
			"       and eagg.externalAccounts.externalAccountPK.accountCode = ?3 " +
			"       and eagg.driverGradeGroup.driverGradeGroup = ?4 ")
	public ExternalAccountGradeGroup getEagByExternalAccountCodeAndType(long accountCid, String accountType, String accountCode, String gradeCode);
	
	@Query("FROM ExternalAccountGradeGroup eagg WHERE eagg.externalAccounts.externalAccountPK.cId = ?1 and eagg.externalAccounts.externalAccountPK.accountType =?2 and eagg.externalAccounts.externalAccountPK.accountCode =?3 ORDER BY eagg.driverGradeGroup.driverGradeGroup ASC")
	public List<ExternalAccountGradeGroup> getGradeGroupByClient(long cid, String accountType, String accountCode);

}
