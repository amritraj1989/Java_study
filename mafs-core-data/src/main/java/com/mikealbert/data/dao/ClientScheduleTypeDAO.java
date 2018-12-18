package com.mikealbert.data.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mikealbert.data.entity.ClientScheduleType;



/**
 * DAO for ClientScheduleType Entity
 * 
 */

public interface ClientScheduleTypeDAO extends JpaRepository<ClientScheduleType, Long> {
	

	@Query("SELECT c FROM ClientScheduleType c ORDER BY c.scheduleType")
	public List<ClientScheduleType> getClientScheduleTypeList();	

	@Query("SELECT c FROM ClientScheduleType c WHERE c.scheduleType = ?1")
	public ClientScheduleType getClientScheduleTypeByType(String type);	

	
}