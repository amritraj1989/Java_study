package com.mikealbert.data.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mikealbert.data.entity.FleetNotes;

/**
 * DAO for FleetNotes Entity
 * 
 * @author Raj
 */

public interface FleetNotesDAO extends JpaRepository<FleetNotes, Long> {
	@Query("FROM FleetNotes fleetNotes WHERE fleetNotes.maintenanceRequest.mrqId = ?1 order by fleetNotes.fnoId desc")
	public List<FleetNotes> findByMaintenanceRequestId(Long maintenanceRequestId);
	
	@Query("FROM FleetNotes fleetNotes WHERE fleetNotes.fleetMaster.fmsId = ?1 AND fleetNotes.maintenanceRequest.mrqId IS NULL order by fleetNotes.fnoId desc")
	public List<FleetNotes> findFleetNotesByFmsId(Long fmsId);
}