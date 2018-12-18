
package com.mikealbert.data.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.mikealbert.data.entity.FuelTypeValues;

public interface FuelTypeValuesDAO extends JpaRepository<FuelTypeValues, Long>{
	
	@Query("SELECT ftv FROM FuelTypeValues ftv WHERE ftv.fuelType = ?1")
	public FuelTypeValues findByFuelType(String fuelType);


}
