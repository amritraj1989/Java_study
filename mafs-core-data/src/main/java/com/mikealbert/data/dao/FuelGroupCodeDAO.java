package com.mikealbert.data.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mikealbert.data.entity.FuelGroupCode;

public interface FuelGroupCodeDAO extends JpaRepository<FuelGroupCode, String>{

	@Query("SELECT fgc FROM FuelGroupCode fgc order by fgc.fuelGroupDescription")
	public List<FuelGroupCode> getAllFuelGroupCodes();

	@Query("SELECT fgc FROM FuelGroupCode fgc where fgc.fuelGroupDescription = ?1")
	public FuelGroupCode getFuelGroupCodeByDescription(String description);

}
