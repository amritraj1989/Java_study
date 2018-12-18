package com.mikealbert.data.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mikealbert.data.entity.FuelGroupCode;
import com.mikealbert.data.entity.FuelGrouping;
import com.mikealbert.data.entity.FuelGroupingPK;

public interface FuelGroupingDAO extends JpaRepository<FuelGrouping, FuelGroupingPK> {
	@Query("select distinct fg.fuelGroupCode from FuelGrouping fg where fg.fuelGroupCode.fuelGroupCode = ?1")
	FuelGroupCode	findByFuelGroupCode(String fuelGroupCode);

	@Query("select distinct fg.fuelGroupingPK.groupKey from FuelGrouping fg order by fg.fuelGroupingPK.groupKey asc")
	List<String> findDistinctFuelGroupingKeys();

}
