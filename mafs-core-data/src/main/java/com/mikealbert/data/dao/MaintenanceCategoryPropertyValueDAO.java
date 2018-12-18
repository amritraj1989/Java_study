package com.mikealbert.data.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mikealbert.data.entity.LogBook;
import com.mikealbert.data.entity.MaintenanceCategoryPropertyValue;

/**
* DAO for MaintenanceCategoryPropertyValue Entity
* @author sibley
*/

public interface MaintenanceCategoryPropertyValueDAO extends JpaRepository<MaintenanceCategoryPropertyValue, Long> {
	@Query("SELECT mpv FROM MaintenanceCategoryPropertyValue mpv WHERE mpv.maintenanceRequestTask.mrtId = ?1 AND mpv.maintenanceCategoryProperty.maintenanceCategory.code = ?2")
	public List <MaintenanceCategoryPropertyValue> findByIdAndMaintenanceCategoryCode(Long id, String maintenanceCategoryCode);	
}
