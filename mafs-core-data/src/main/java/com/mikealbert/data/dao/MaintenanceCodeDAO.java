package com.mikealbert.data.dao;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mikealbert.data.entity.MaintenanceCode;

/**
* DAO for MaintenanceCode Entity
* @author sibley
*/

public interface MaintenanceCodeDAO extends JpaRepository<MaintenanceCode, Long> {
	
	@Query("FROM MaintenanceCode mc WHERE mc.code = ?1")
	public MaintenanceCode findByCode(String code);

	@Query("SELECT mc FROM MaintenanceCode mc WHERE LOWER(mc.code) LIKE LOWER(?1) OR LOWER(mc.description) LIKE LOWER(?1) ORDER BY mc.code")
	public List<MaintenanceCode> findByMaintCodeOrDescription(String maintCodeDesc, Pageable pageable);
    
	@Query("SELECT COUNT(mc) FROM MaintenanceCode mc WHERE LOWER(mc.code) LIKE LOWER(?1) OR LOWER(mc.description) LIKE LOWER(?1)")
	public long getCountByMaintCodeOrDescription(String maintCodeDesc);
}
