package com.mikealbert.data.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mikealbert.data.entity.LatestOdometerReadingV;
import com.mikealbert.data.entity.OdometerReading;

/**
* DAO for OdometerReading Entity
* @author sibley
*/

public interface OdometerReadingDAO extends JpaRepository<OdometerReading, Long> {
	@Query("SELECT odr FROM OdometerReading odr WHERE odr.odorId = (SELECT MAX(odr.odorId) FROM OdometerReading odr WHERE odr.odometer.odoId = ?1)")
	public OdometerReading findMaxByOdoId(Long odoId);
	
	@Query("SELECT odr FROM OdometerReading odr WHERE odr.maintenanceRequest.mrqId IS NOT NULL AND odr.maintenanceRequest.mrqId = ?1")
	public OdometerReading findByMrqId(Long mrqId);	
	
	@Query("SELECT odr FROM OdometerReading odr WHERE odr.maintenanceRequest.mrqId IS NOT NULL AND odr.maintenanceRequest.mrqId = ?1")
	public List<OdometerReading> findListByMrqId(Long mrqId);	
	
	@Query("SELECT lorv FROM LatestOdometerReadingV lorv WHERE lorv.fmsId = ?1")
	public LatestOdometerReadingV findLatestOdometerReading(Long fmsId);
}
