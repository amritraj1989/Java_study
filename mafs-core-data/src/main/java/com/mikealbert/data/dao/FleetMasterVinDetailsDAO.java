package com.mikealbert.data.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mikealbert.data.entity.FleetMasterVinDetails;


/**
 * DAO for FleetMasterVinDetails Entity
 * 
 * @author Duncan
 */

public interface FleetMasterVinDetailsDAO extends JpaRepository<FleetMasterVinDetails, Long> {

	@Query("SELECT fmvd FROM FleetMasterVinDetails fmvd WHERE LOWER(fmvd.vin) = LOWER(?1) ORDER BY fmvd.fvdId DESC")
	public List<FleetMasterVinDetails> findFleetMasterVinDetailsByVin(String vin);
	
	@Query("SELECT fmvd FROM FleetMasterVinDetails fmvd WHERE fmvd.fleetMaster.fmsId = ?1")
	public FleetMasterVinDetails findFleetMasterVinDetailsByFmsId(Long fmsId);
}