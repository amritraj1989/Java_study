package com.mikealbert.data.dao;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mikealbert.data.entity.MaintenanceRequest;

/**
* DAO for MaintenanceRequest Entity
* @author sibley
*/
public interface MaintenanceRequestDAO extends MaintenanceRequestDAOCustom, JpaRepository<MaintenanceRequest, Long>{
	
	@Query("FROM MaintenanceRequest mrq WHERE mrq.mrqId = ?1")
	public MaintenanceRequest getMaintenanceRequestByMrqId(long mrqId);
	
	@Query("FROM MaintenanceRequest mrq WHERE mrq.fleetMaster.fmsId = ?1")
	public List<MaintenanceRequest> findByFmsId(Long fmsId);
	
	@Query("FROM MaintenanceRequest mrq WHERE mrq.jobNo = ?1")
	public MaintenanceRequest findByJobNo(String jobNo);
	
	@Query("select mrq FROM MaintenanceRequest mrq WHERE mrq.fleetMaster.fmsId IN (?1) order by CASE mrq.maintReqStatus WHEN 'B' THEN 1 WHEN 'I' THEN 2 WHEN 'C' THEN 3 END, mrq.actualStartDate desc ")
	public List<MaintenanceRequest> findByFmsIds(List<Long> fmsIds);
	
	@Query("select mrq FROM MaintenanceRequest mrq WHERE mrq.maintReqStatus = 'I' AND mrq.plannedEndDate < ?1")
	public List<MaintenanceRequest> findInProgressRequestsLessThanDate(Date now);

	@Query("select mrq FROM MaintenanceRequest mrq WHERE mrq.mrqId = (select max(mrq1.mrqId) FROM MaintenanceRequest mrq1 WHERE mrq1.maintReqStatus IN ('C', 'I', 'WI', 'GP', 'CNP', 'H') AND mrq1.fleetMaster.fmsId = ?1 )")
	public MaintenanceRequest findLatestMaintenanceRequestByFmsId(Long fmsId);
}
