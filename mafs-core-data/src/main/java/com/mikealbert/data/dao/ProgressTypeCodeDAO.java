package com.mikealbert.data.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mikealbert.data.entity.ProgressTypeCode;



/**
* DAO for ProgressTypeCode Entity
*/

public interface ProgressTypeCodeDAO extends JpaRepository<ProgressTypeCode, String> {
	
	@Query("SELECT ptc FROM ProgressTypeCode ptc order by ptc.progressType asc")
	public List<ProgressTypeCode> findAllOrderByProgressType();
	
	@Query("SELECT ptc FROM ProgressTypeCode ptc where ptc.sysGenerated = 'N' order by ptc.progressType asc")
	public List<ProgressTypeCode> findAllNonSysGenOrderByProgressType();
	
}
