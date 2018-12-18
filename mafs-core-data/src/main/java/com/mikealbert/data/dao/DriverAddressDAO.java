package com.mikealbert.data.dao;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mikealbert.data.entity.DriverAddress;


/**
* DAO for DriverAddress Entity
* @author sibley
*/
public interface DriverAddressDAO extends JpaRepository<DriverAddress, Long> {
	
	@Query("SELECT da FROM DriverAddress da WHERE driver.drvId = ?1 order by da.inputDate desc")	
	public List<DriverAddress> findByDrvId(Long drvId);	

	@Query("SELECT da FROM DriverAddress da WHERE driver.drvId = ?1 and da.addressType.addressType = ?2")	
	public DriverAddress findByDrvIdAndType(Long drvId, String type);	

}
