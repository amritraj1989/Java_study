package com.mikealbert.data.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mikealbert.data.entity.ServiceTask;

/**
 * DAO for ServiceTask Entity
 * 
 */

public interface ServiceTaskDAO extends JpaRepository<ServiceTask, Long> {
	
	
	@Query("SELECT s FROM ServiceTask s ORDER BY s.maintenanceCategory.code, s.serviceCode")
	public List<ServiceTask> getServiceTaskList();	

	@Query("SELECT s FROM ServiceTask s WHERE s.serviceCode = ?1")
	public List<ServiceTask> getServiceTaskByCode(String code);	

	@Query("SELECT s FROM ServiceTask s ORDER BY s.serviceCode")
	public List<ServiceTask> getServiceTaskListSortedByName();	
	
	
}