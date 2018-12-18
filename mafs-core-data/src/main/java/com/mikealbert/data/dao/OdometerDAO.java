package com.mikealbert.data.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mikealbert.data.entity.Odometer;

/**
* DAO for Odometer Entity
* @author sibley
*/

public interface OdometerDAO extends JpaRepository<Odometer, Long> {
	@Query("SELECT o FROM Odometer o WHERE o.odoId = (SELECT MAX(o.odoId) FROM Odometer o WHERE o.fleetMaster.fmsId = ?1)")
	public Odometer findMaxByFmsId(Long fmsId); 
}
