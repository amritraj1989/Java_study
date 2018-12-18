package com.mikealbert.data.dao;


import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mikealbert.data.entity.ContractLine;
import com.mikealbert.data.entity.FleetMaster;
import com.mikealbert.data.entity.VehicleOdometerReadingsV;

/**
* DAO for DriverAllocCodeDAO Entity
* @author sibley
*/
public interface FleetMasterDAO extends FleetMasterDAOCustom, JpaRepository<FleetMaster, Long>{
	@Query("SELECT fm FROM FleetMaster fm WHERE fm.unitNo = ?1")
	public FleetMaster findByUnitNo(String unitNo);
	
	@Query("SELECT DISTINCT fm FROM FleetMaster fm LEFT JOIN FETCH fm.driverAllocationList da LEFT JOIN FETCH da.driver d WHERE d.drvId = ?1")	
	public List<FleetMaster> findByDrvId(Long drvId);	
	
	@Query("SELECT fm FROM FleetMaster fm LEFT JOIN FETCH fm.driverAllocationList da LEFT JOIN FETCH da.driver d " +
			"WHERE fm.unitNo = ?1 AND (da.deallocationDate IS NULL OR da.deallocationDate > CURRENT_DATE) AND  da.allocationDate <= CURRENT_DATE")
	public FleetMaster findByUnitNoAndFilterCurrentDriverAllocation(String unitNo);

	@Query("SELECT DISTINCT fm FROM FleetMaster fm INNER JOIN FETCH fm.driverAllocationList da INNER JOIN FETCH da.driver d " +
			"WHERE d.drvId = ?1 AND (da.deallocationDate IS NULL OR da.deallocationDate > CURRENT_DATE) AND  da.allocationDate <= CURRENT_DATE")	
	public List<FleetMaster> findByDrvIdAndFilterCurrentDriverAllocation(Long drvId);
	
	@Query("SELECT fm FROM FleetMaster fm WHERE LOWER(fm.vin) LIKE LOWER(?1) order by fm.vin asc, fm.unitNo asc")
	public Page<FleetMaster> findFleetMasterByVIN(String vin, Pageable page);
	
	// created method for JUnit
	@Query("SELECT fm.unitNo FROM FleetMaster fm order by fm.fmsId DESC")
	public Page<String> findLatestFleetMaster(Pageable page);
	
	
	@Query("SELECT COUNT(fm) FROM FleetMaster fm WHERE LOWER(fm.vin) LIKE LOWER(?1)")
	public long countFleetMasterByVIN(String vin);
	
	@Query("SELECT fm.fmsId FROM FleetMaster fm WHERE LOWER(fm.vin) LIKE LOWER(?1)")
	public List<Long> findFmsIdsByVIN(String vin);

	@Query("SELECT fm FROM FleetMaster fm WHERE LOWER(fm.vin) = LOWER(?1) ORDER BY fm.fmsId DESC")
    public List<FleetMaster> findByVIN(String vin);	
	
	@Query("Select odoReading from LatestOdometerReadingV where fmsId = ?1")
	public		Long	getHighMileage(Long fmsId);
	
	@Query("SELECT fm FROM FleetMaster fm JOIN FETCH fm.vehicleOdometerReadings WHERE fm.unitNo = ?1")
	public FleetMaster getOdoReadingWithFleetMaster(String unitNo);
		
	@Query("Select cl FROM ContractLine cl where fleetMaster.fmsId = ?1")
	public	List<ContractLine>	getContractLineList(Long fmsId);
	
	@Query("Select vo FROM VehicleOdometerReadingsV vo where vo.fleetMaster.fmsId = ?1")
	public	List<VehicleOdometerReadingsV>	getVehicleOdometerReadingsList(Long fmsId);

}
