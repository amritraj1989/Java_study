package com.mikealbert.data.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mikealbert.data.entity.DriverAllocation;

/**
* DAO for DriverAllocation Entity
* @author sibley
*/
public interface DriverAllocationDAO extends JpaRepository<DriverAllocation, Long>, DriverAllocationDAOCustom {
	@Query("SELECT da FROM DriverAllocation da WHERE da.fleetMaster.fmsId = ?1 ORDER BY da.allocationDate DESC")
	public List<DriverAllocation> findByFmsId(Long fmsId);
	
	@Query("SELECT da FROM DriverAllocation da WHERE da.driver.drvId = ?1 ORDER BY da.allocationDate DESC")
	public List<DriverAllocation> findByDrvId(Long drvId);	
	
	@Query("SELECT da FROM DriverAllocation da LEFT JOIN FETCH da.driver d WHERE da.fleetMaster.fmsId = ?1  AND (da.deallocationDate IS NULL OR da.deallocationDate > CURRENT_DATE) AND  da.allocationDate <= CURRENT_DATE")	
	public DriverAllocation findByFmsIdCurrentDriverAllocation(long fmsId);	
	
	@Query("SELECT da FROM DriverAllocation da WHERE da.driver.drvId = ?1  AND (da.deallocationDate IS NULL OR da.deallocationDate > CURRENT_DATE) AND  da.allocationDate <= CURRENT_DATE")	
	public List<DriverAllocation> findByDrvIdCurrentDriverAllocations(long drvId);	

	@Query("SELECT COUNT(da) FROM DriverAllocation da WHERE da.driver.drvId = ?1  AND (da.deallocationDate IS NULL OR da.deallocationDate > CURRENT_DATE) AND  da.allocationDate <= CURRENT_DATE")	
	public long findByDrvIdCurrentDriverAllocationsCount(long drvId);	
}
