package com.mikealbert.data.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mikealbert.data.entity.MaintenanceCategory;

/**
* DAO for MaintenanceCategory Entity
*/

public interface MaintenanceCategoryDAO extends JpaRepository<MaintenanceCategory, String> {
	
	@Query("SELECT c FROM MaintenanceCategory c order by c.code")	
	public List<MaintenanceCategory> getMaintenanceCategories();	

	
}
