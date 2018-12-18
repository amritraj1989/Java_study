package com.mikealbert.data.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mikealbert.data.entity.FpNoShowReports;

public interface FpNoShowReportsDAO extends JpaRepository<FpNoShowReports, Long> {
	@Query("SELECT fpn FROM FpNoShowReports fpn WHERE fpn.parameterId = ?1")
	public List<FpNoShowReports> findByParameterId(String parameterId);
	
}
