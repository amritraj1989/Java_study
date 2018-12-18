
/**
 * MaintenanceTableDAO.java
 * mafs-core-data
 * Mar 11, 2013
 * 12:06:32 PM
 */
package com.mikealbert.data.dao;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mikealbert.data.entity.MaintenanceTable;


/**
 * @author anand.mohan
 *
 */
public interface MaintenanceTableDAO extends JpaRepository<MaintenanceTable, Long>{
	String	findByMdlIdTableCodeAndStatusNativeString = "select * from MAINTENANCE_TABLES WHERE mdl_mdl_id = :mdlId AND table_code = :tableCode AND status = :status"+
	           " AND TRUNC (effective_from) IN (SELECT MAX(TRUNC(effective_from)) "+
	           " FROM maintenance_tables WHERE mdl_mdl_id = :mdlId "+
	           " AND TRUNC(effective_from) <= TRUNC(SYSDATE) AND table_code = :tableCode AND status = :status)";
	           
	//@Query(value = findByMdlIdTableCodeAndStatusNativeString,nativeQuery = true )
	//public MaintenanceTable findByMdlIdTableCodeStatusAndEffectiveFrom(@Param("mdlId")Long mdlId, @Param("tableCode") String tableCode, @Param("status") String status);
	@Query("from MaintenanceTable where mdlMdlId = ?1 and tableCode = ?2 and status = ?3 and effectiveFrom <= ?4")
	public List<MaintenanceTable> findByMdlIdTableCodeStatusAndEffectiveFrom(BigDecimal mdlId, String tableCode,  String status, Date effFrom);
	
	
}
