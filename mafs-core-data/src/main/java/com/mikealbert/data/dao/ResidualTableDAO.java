package com.mikealbert.data.dao;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mikealbert.data.entity.ResidualTable;



public interface ResidualTableDAO extends JpaRepository<ResidualTable, Long> {
	 
	@Query("SELECT rt FROM ResidualTable rt WHERE rt.tableCode = ?1 and rt.status = ?2 and rt.mdlMdlId = ?3 and rt.dacDacId = ?4 and rt.oacOacId = ?5 and rt.ophOphId = ?6 and rt.effectiveTo is null order by rt.effectiveFrom desc")
	public List<ResidualTable> getActiveResidualTables(String tableCode, String status, BigDecimal mdlId, BigDecimal dacId, BigDecimal oacId, BigDecimal ophId);
		
}
