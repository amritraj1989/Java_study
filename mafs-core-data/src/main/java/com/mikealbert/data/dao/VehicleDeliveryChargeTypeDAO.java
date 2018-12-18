package com.mikealbert.data.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mikealbert.data.entity.VehicleDeliveryChargeType;

public interface VehicleDeliveryChargeTypeDAO extends JpaRepository<VehicleDeliveryChargeType, Long> {
	
	@Query("SELECT vdct FROM VehicleDeliveryChargeType vdct WHERE vdct.code = ?1")
	public VehicleDeliveryChargeType findByCode(String code);	
		
}
