package com.mikealbert.data.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mikealbert.data.entity.MaintenanceRequestUser;

/**
 * DAO for ServiceProvider Entity
 * 
 * @author Raj
 */

public interface MaintenanceRequestUserDAO extends JpaRepository<MaintenanceRequestUser, Long> {
	@Query("FROM MaintenanceRequestUser mru WHERE mru.maintenanceRequest.mrqId = ?1")
	public List<MaintenanceRequestUser> getMaintenanceRequestUserByMrqId(Long mrqId);
}