package com.mikealbert.data.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mikealbert.data.entity.VehicleScheduleInterval;

public interface VehicleScheduleIntervalDAO extends JpaRepository<VehicleScheduleInterval, Long> {
	@Query("from VehicleScheduleInterval vsi where vsi.vehicleSchedule.vschId = ?1 order by vsi.authorizationNo desc")
	List<VehicleScheduleInterval> findByVehicleSchedule(Long vschId);
	
	@Query("from VehicleScheduleInterval vsi where vsi.authorizationNo = ?1 ")
	List<VehicleScheduleInterval> findByAuthCode(String authCode);
	
	@Query("from VehicleScheduleInterval vsi where vsi.docNo = ?1 ")
	List<VehicleScheduleInterval> findByDocNo(String docNo);
}
