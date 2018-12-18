package com.mikealbert.data.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mikealbert.data.entity.HirePeriodDistances;

/**
* DAO for HirePeriodDistances Entity
* @author Amritraj
*/

public interface HirePeriodDistanceDAO extends JpaRepository<HirePeriodDistances, Long> {
	

	@Query("SELECT count(*) FROM HirePeriodDistances hpd WHERE hpd.distance = ?1 and hpd.hpdDistId = ?2")
	public long getCountOfTermAndMiles(Long distance, Long hpdDistId);
}
