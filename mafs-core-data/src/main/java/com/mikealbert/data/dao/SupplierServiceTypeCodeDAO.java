
/**
 * SupplierServiceTypeCodeDAO.java
 * mafs-core-data
 * Oct 7, 2013
 * 2:38:49 PM
 */
package com.mikealbert.data.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.mikealbert.data.entity.SupplierServiceTypeCodes;

/**
 * @author anand.mohan
 *
 */
@Repository
public interface SupplierServiceTypeCodeDAO extends JpaRepository<SupplierServiceTypeCodes, String>{
	@Query("from SupplierServiceTypeCodes where suppServiceTypeCode like ?1 order by suppServiceTypeCode ASC")
	List<SupplierServiceTypeCodes>	find(String srvTypeCode);
}
