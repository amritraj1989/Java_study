package com.mikealbert.data.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mikealbert.data.entity.CapitalElementGroupMapping;


/**
 * The DAO class for the CAPITAL_ELEMENT_GROUP_MAPPING database table.
 * @author Singh
 */

public interface CapitalElementGroupMappingDAO extends JpaRepository<CapitalElementGroupMapping, Long> {
	
	@Query("SELECT cegm FROM CapitalElementGroupMapping cegm JOIN cegm.capitalElementGroup ceg  ORDER BY ceg.groupDisplaySeq , cegm.groupElementDisplaySeq ")
	public List<CapitalElementGroupMapping> getAllElementGroupMapping();
}