package com.mikealbert.data.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mikealbert.data.entity.GeotabDatabaseSync;

public interface GeotabDatabaseSyncDAO  extends GeotabDatabaseSyncDAOCustom, JpaRepository<GeotabDatabaseSync, Long> {
	
	@Query("FROM GeotabDatabaseSync geotabDatabaseSync WHERE geotabDatabaseSync.activeInd='Y'")
	public List<GeotabDatabaseSync> findActiveDatabaseName();
}
