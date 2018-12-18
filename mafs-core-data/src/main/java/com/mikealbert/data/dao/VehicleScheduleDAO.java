package com.mikealbert.data.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mikealbert.data.entity.VehicleSchedule;

/**
 * DAO for VehicleSchedule Entity
 * 
 */

public interface VehicleScheduleDAO extends VehicleScheduleDAOCustom,JpaRepository<VehicleSchedule, Long> {	
	
	@Query("SELECT COUNT(vs) FROM VehicleSchedule vs WHERE vs.masterSchedule.mschId = ?1")
	public Long getVehSchdCountByMasterSchd(Long mschId);
	
	@Query("SELECT vs FROM VehicleSchedule vs WHERE vs.fleetMaster.unitNo = ?1")
	public List<VehicleSchedule> getVehSchdByUnitNumber(String unitNumber);
	
	@Query("SELECT vs FROM VehicleSchedule vs WHERE vs.fleetMaster.fmsId = ?1")
	public List<VehicleSchedule> getVehSchdByFmsId(Long fmsId);

	@Query("SELECT vs FROM VehicleSchedule vs WHERE vs.vehSchSeq = ?1")
	public VehicleSchedule getVehicleScheduleBySequence(Long sequence);

}