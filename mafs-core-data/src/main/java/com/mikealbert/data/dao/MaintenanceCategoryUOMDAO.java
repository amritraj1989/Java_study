package com.mikealbert.data.dao;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.mikealbert.data.entity.MaintenanceCategoryUOM;

/**
* DAO for MaintenanceUOM Entity
* @author sibley
*/

public interface MaintenanceCategoryUOMDAO extends JpaRepository<MaintenanceCategoryUOM, Long> {
	@Query("SELECT muc FROM MaintenanceCategoryUOM muc WHERE muc.maintenanceCategory.code = ?1")
	public List<MaintenanceCategoryUOM> findByMaintenanceCategoryCode(String code);		
}
