package com.mikealbert.data.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mikealbert.data.entity.FuelGroupCode;
import com.mikealbert.data.entity.MaintScheduleRule;

/**
 * DAO for MaintScheduleRules Entity (aka. Master Schedule Rules)
 * 
 */

public interface MaintScheduleRuleDAO extends JpaRepository<MaintScheduleRule, Long>, MaintScheduleRuleDAOCustom {	

	@Query("SELECT COUNT(msr) FROM MaintScheduleRule msr " +
			" 	WHERE msr.masterSchedule.mschId = ?1" )
	public Long getRulesByMasterIdCount(Long mschId);
	
	@Query(" FROM MaintScheduleRule msr " +
			" 	WHERE msr.masterSchedule.mschId = ?1" )
	public List<MaintScheduleRule>	getRulesByMasterSchedule(Long mschId);
	
	@Query("from MaintScheduleRule where masterSchedule.mschId = ?1 " +
			" and ((?2 is null and year is null) or year = ?2 )"+
			" and ( (?3 is null and  makeModelDesc is null ) or makeModelDesc = ?3 )" +
			" and ((?4 is null and fuelTypeGroup is null ) or (fuelTypeGroup = ?4 ))" +
			" and ((?5 is null and modelTypeDesc is null ) or modelTypeDesc = ?5)" +
			" and ( (?6 is null and makeCode is null) or makeCode = ?6) " +
			" and ((?8 is null and scheduleAccount.externalAccountPK.accountCode is null) or (scheduleAccount.externalAccountPK.accountCode= ?8  and scheduleAccount.externalAccountPK.cId = ?7))" +
			" and ((?9 is null and scheduleAccount.externalAccountPK.accountType is null) or scheduleAccount.externalAccountPK.accountType = ?9) ")
	public List<MaintScheduleRule> find(Long mschId, String year, String makeModelDesc,  String fuelTypeGroup,
			String modelTypeDesc, String makeCode, Long cId, String accountCode, String accountType);
	
	@Query("SELECT distinct fg.fuelGroupCode FROM FuelGrouping fg where fg.fuelGroupingPK.groupKey = ?1")
	public List<FuelGroupCode> getFuelGroupList( String groupKey);
	
	
	@Query("from MaintScheduleRule where " +
			" ((?1 is null and year is null) or year = ?1 )"+
			" and ( (?2 is null and  makeModelDesc is null ) or makeModelDesc = ?2 )" +
			" and ((?3 is null and fuelTypeGroup is null ) or (fuelTypeGroup = ?3 ))" +
			" and ((?4 is null and modelTypeDesc is null ) or modelTypeDesc = ?4)" +
			" and ( (?5 is null and makeCode is null) or makeCode = ?5) " +
			" and (?8 is null or (" +
			"  masterSchedule.mschId = ?6 "+
			" and ((?8 is null and scheduleAccount.externalAccountPK.accountCode is null) or (scheduleAccount.externalAccountPK.accountCode= ?8  and scheduleAccount.externalAccountPK.cId = ?7))" +
			" and ((?9 is null and scheduleAccount.externalAccountPK.accountType is null) or scheduleAccount.externalAccountPK.accountType = ?9)" +
			") )"
			)
	public List<MaintScheduleRule> find(String year, String makeModelDesc,  String fuelTypeGroup,
			String modelTypeDesc, String makeCode,Long mschId, Long cId, String accountCode, String accountType);

	@Query("from MaintScheduleRule where " +
			" ((?1 is null and year is null) or year = ?1 )"+
			" and ( (?2 is null and  makeModelDesc is null ) or makeModelDesc = ?2 )" +
			" and ((?3 is null and fuelTypeGroup is null ) or (fuelTypeGroup = ?3 ))" +
			" and ((?4 is null and modelTypeDesc is null ) or modelTypeDesc = ?4)" +
			" and ( (?5 is null and makeCode is null) or makeCode = ?5) " +
			" and ( (masterSchedule.clientScheduleType.scheduleType = ?9) " +
			"  and ((?7 is null and scheduleAccount.externalAccountPK.accountCode is null) or (scheduleAccount.externalAccountPK.accountCode= ?7  and scheduleAccount.externalAccountPK.cId = ?6))" +
			"  and ((?8 is null and scheduleAccount.externalAccountPK.accountType is null) or scheduleAccount.externalAccountPK.accountType = ?8)" +
			") "
			)
	public List<MaintScheduleRule> findByScheduleType(String year, String makeModelDesc,  String fuelTypeGroup,
			String modelTypeDesc, String makeCode, Long cId, String accountCode, String accountType, String scheduleType);

	
	@Query("from MaintScheduleRule where " +
						" ((?1 is null and fuelTypeGroup is null ) or (fuelTypeGroup = ?1 ))" +
			" and ( (?2 is null and highMileage is null) or highMileage = ?2) ")
	public List<MaintScheduleRule> find(String fuelTypeGroup,String highMileage);
			
	
	
}