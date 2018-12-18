package com.mikealbert.data.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.mikealbert.data.entity.VehicleConfigModel;

public interface VehicleConfigModelDAO extends JpaRepository<VehicleConfigModel, Long> {
	
	@Query("SELECT DISTINCT (vcm.year) FROM VehicleConfigModel vcm WHERE vcm.year is not null and vcm.obsoleteYn = 'N' order by vcm.year")
	public List<String> findDistinctYearByCriteria();
	
	@Query("SELECT DISTINCT (vcm.make) FROM VehicleConfigModel vcm WHERE vcm.make is not null and vcm.obsoleteYn = 'N' order by vcm.make")
	public List<String> findDistinctMakeByCriteria();
	
}
