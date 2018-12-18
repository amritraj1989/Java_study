package com.mikealbert.data.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.mikealbert.data.entity.WarrantyUnitLink;
import java.util.List;

/**
 * DAO for WarrantyDetail Entity
 * 
 * @author Raj
 */

public interface WarrantyUnitLinkDAO extends JpaRepository<WarrantyUnitLink, Long> {
	@Query("FROM WarrantyUnitLink wul WHERE wul.fleetMaster.fmsId = ?1")
	public List<WarrantyUnitLink> findByFmsId(Long fmsId);
}