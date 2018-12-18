package com.mikealbert.data.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mikealbert.data.entity.TelematicsMileage;


/**
 * DAO for TelematicsMileage Entity
 * 
 * @author Scholle
 */

public interface TelematicsMileageDAO extends JpaRepository<TelematicsMileage, Long> {
	
	@Query("SELECT tm1 FROM TelematicsMileage tm1 WHERE tm1.fmsID = ?1 and tm1.odoReadingDate = (SELECT MAX(tm2.odoReadingDate) from TelematicsMileage tm2 WHERE tm2.fmsID = tm1.fmsID) ORDER BY tm1.odorID DESC")
    public List<TelematicsMileage> findByFmsId(Long fmsId);

}
