package com.mikealbert.data.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mikealbert.data.entity.MasterSchedule;

/**
 * DAO for MasterSchedule Entity
 * 
 */

public interface MasterScheduleDAO extends JpaRepository<MasterSchedule, Long> {	

	@Query("SELECT ms FROM MasterSchedule ms ORDER BY ms.clientScheduleType.scheduleType")
	public List<MasterSchedule> getMasterScheduleList();	


	@Query("SELECT ms FROM MasterSchedule ms WHERE ms.masterCode = ?1")
	public List<MasterSchedule> getMasterSchedulesByCode(String code);	

	@Query("SELECT ms FROM MasterSchedule ms WHERE ms.distanceFrequency = ?1 ORDER BY ms.masterCode DESC")
	public List<MasterSchedule> getMasterSchedulesByInterval(int interval);
	

	@Query("SELECT ms FROM MasterSchedule ms where ms.description = ?1")
	public List<MasterSchedule>	getMasterScheduleByDescription(String description);
	
	@Query("SELECT ms FROM MasterSchedule ms WHERE ms.clientScheduleType.scheduleType = ?1 and ms.distanceFrequency = ?2 and ms.hiddenFlag = 'N' ORDER BY ms.masterCode DESC")
	public List<MasterSchedule> findActiveByScheduleTypeAndInterval(String scheduleType, int interval);
	
	@Query("SELECT ms FROM MasterSchedule ms WHERE ms.clientScheduleType.scheduleType = ?1 and ms.hiddenFlag = 'N' order by ms.distanceFrequency")
	public List<MasterSchedule> findActiveByScheduleType(String scheduleType);
	
	@Query("SELECT DISTINCT ms FROM MasterSchedule ms JOIN ms.masterScheduleIntervals mst "
    + " JOIN mst.serviceTask st  WHERE st.srvtId = ?1")
	public List<MasterSchedule> findByServiceTask(Long serviceTaskId);
	
	@Query("SELECT ms FROM MasterSchedule ms WHERE ms.clientScheduleType.scheduleType = ?1  and ms.hiddenFlag = ?2 order by ms.distanceFrequency")
	public List<MasterSchedule> findByScheduleType(String scheduleType,String hiddenFlag);
	
	@Query("SELECT ms FROM MasterSchedule ms WHERE ms.clientScheduleType.scheduleType = ?1 and ms.distanceFrequency = ?2 and ms.hiddenFlag = ?3 ORDER BY ms.masterCode DESC")
	public List<MasterSchedule> findByScheduleTypeAndInterval(String scheduleType,int interval,String hiddenFlag);
	
	
	@Query("SELECT ms FROM MasterSchedule ms WHERE ms.clientScheduleType.scheduleType = ?1 and clientAccount.externalAccountPK.cId = ?2 and clientAccount.externalAccountPK.accountCode = ?3 and clientAccount.externalAccountPK.accountType = ?4 and ms.hiddenFlag = ?5 order by ms.distanceFrequency")
	public List<MasterSchedule> findByScheduleTypeAndClient(String scheduleType,Long cId,String accountCode,String accountType,String hiddenFlag);
	
	@Query("SELECT ms FROM MasterSchedule ms WHERE ms.clientScheduleType.scheduleType = ?1 and clientAccount.externalAccountPK.cId = ?2 and clientAccount.externalAccountPK.accountCode = ?3 and clientAccount.externalAccountPK.accountType = ?4 and ms.distanceFrequency = ?5 and ms.hiddenFlag = ?6 ORDER BY ms.masterCode DESC")
	public List<MasterSchedule> findByScheduleTypeAndClientAndInterval(String scheduleType, Long cId,String accountCode,String accountType,int interval,String hiddenFlag);
	


}