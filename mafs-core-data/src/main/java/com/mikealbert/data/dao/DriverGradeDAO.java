package com.mikealbert.data.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mikealbert.data.entity.DriverGrade;

/**
* DAO for DriverGrade Entity
* @author sibley
*/
public interface DriverGradeDAO extends JpaRepository<DriverGrade, String>{
	@Query("SELECT dg FROM DriverGrade dg, DriverGradeGroupCode dggc, ExternalAccountGradeGroup eagg " +
			"   WHERE eagg.externalAccounts.externalAccountPK.accountCode = ?1 " +
			"       and eagg.externalAccounts.externalAccountPK.accountType = ?2 " +
			"       and dggc.driverGradeGroup = eagg.driverGradeGroup.driverGradeGroup " +
			"       and dg.gradeCode = dggc.driverGradeGroup" +
			"   ORDER BY dg.gradeCode ASC ")
	public List<DriverGrade> getByExternalAccountCodeAndType(String accountCode, String accountType);
}
