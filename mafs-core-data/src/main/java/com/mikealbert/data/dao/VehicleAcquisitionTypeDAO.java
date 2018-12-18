package com.mikealbert.data.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.mikealbert.data.entity.VehicleAcquisitionType;

/**
* DAO for VehicleAcquisitionType Entity
* @author sibley
*/

public interface VehicleAcquisitionTypeDAO extends JpaRepository<VehicleAcquisitionType, Long> {
	
	@Query("SELECT vat FROM VehicleAcquisitionType vat WHERE vat.code = ?1")
	public VehicleAcquisitionType findByCode(String code);
	
}
