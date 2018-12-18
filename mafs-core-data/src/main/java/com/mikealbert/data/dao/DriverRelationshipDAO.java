package com.mikealbert.data.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mikealbert.data.entity.DriverRelationship;

public interface DriverRelationshipDAO extends JpaRepository<DriverRelationship, Long>{
	@Query("SELECT dr FROM DriverRelationship dr WHERE dr.primaryDriver.drvId = ?1")
	public List<DriverRelationship> findByParentDriverDrvId(Long drvId);
	
	@Query("SELECT dr FROM DriverRelationship dr WHERE dr.secondaryDriver.drvId = ?1")
	public List<DriverRelationship> findByRelatedDriverDrvId(Long drvId);	
}

