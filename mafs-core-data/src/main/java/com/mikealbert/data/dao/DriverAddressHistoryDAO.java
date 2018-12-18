package com.mikealbert.data.dao;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mikealbert.data.entity.DriverAddressHistory;

/**
* DAO for DriverAddress Entity
* @author sibley
*/
public interface DriverAddressHistoryDAO extends JpaRepository<DriverAddressHistory, Long> {
	
	@Query("SELECT dah FROM DriverAddressHistory dah WHERE driver.drvId = ?1 order by dah.inputDate desc")	
	public List<DriverAddressHistory> findByDrvId(Long drvId);	

	@Query("SELECT dah FROM DriverAddressHistory dah WHERE driver.drvId = ?1 order by dah.inputDate asc")	
	public List<DriverAddressHistory> findByDrvIdAsc(Long drvId);	

	@Query("SELECT dah FROM DriverAddressHistory dah WHERE driver.drvId = ?1 and dah.addressType.addressType = ?2 order by dah.inputDate asc")	
	public List<DriverAddressHistory> findByDrvIdAndTypeAsc(Long drvId, String type);	

}
