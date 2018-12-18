package com.mikealbert.data.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mikealbert.data.entity.CcAddress;

/**
* DAO for CcAddress Entity
* @author Singh
*/

public interface CcAddressDAO extends JpaRepository<CcAddress, Long> {
	
	@Query("select c from CcAddress c where c.cId = ?1")
	public List<CcAddress> findCcAddress(long cId);
	
}
