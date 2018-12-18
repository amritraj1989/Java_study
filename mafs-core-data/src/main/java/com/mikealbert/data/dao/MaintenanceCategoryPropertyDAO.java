package com.mikealbert.data.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.mikealbert.data.entity.MaintenanceCategoryProperty;

/**
* DAO for MaintenanceCategoryProperty Entity
* @author sibley
*/

public interface MaintenanceCategoryPropertyDAO extends JpaRepository<MaintenanceCategoryProperty, Long> {
	@Query("SELECT mcp FROM MaintenanceCategoryProperty mcp WHERE mcp.maintenanceCategory.code = ?1")
	public List<MaintenanceCategoryProperty> findByMaintenanceCategoryCode(String code);		
}
