package com.mikealbert.data.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mikealbert.data.entity.VehicleTechnicalInfo;

/**
 * DAO for VehicleTechnicalInfo Entity
 * 
 * @author Raj
 */

public interface VehicleTechnicalInfoDAO extends JpaRepository<VehicleTechnicalInfo, Long> {
	@Query("FROM VehicleTechnicalInfo vtd WHERE vtd.vehicleTehnicalDataId = ?1")
	public VehicleTechnicalInfo findByVehicleTechnicalDataId(Long vehicleTechDataId);
	
	@Query("FROM VehicleTechnicalInfo vtd WHERE vtd.model.modelId = ?1")
	public VehicleTechnicalInfo findByModelId(Long modelId);
}