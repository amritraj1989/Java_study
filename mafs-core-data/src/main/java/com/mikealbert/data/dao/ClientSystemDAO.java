package com.mikealbert.data.dao;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mikealbert.data.entity.ClientSystem;

/**
* DAO for ClientSystem Entity
* @author chaille
*/

public interface ClientSystemDAO extends JpaRepository<ClientSystem, String>{
	
	@Query("FROM ClientSystem cs WHERE cs.clientSystemId = ?1")
	public ClientSystem findClientSystemByClientSystemId(long clientSystemId);
	
	/*
	 * Get all Client Systems that have POC's besides SYSTEM_NAME 'ALL'
	 * For Client System (POC Category) drop down on Client Point of Communications screen
	 */			
	@Query("SELECT DISTINCT cs FROM ClientSystem cs WHERE cs.clientSystemId IN (SELECT cp.clientSystem.clientSystemId from ClientPoint cp) AND cs.clientSystemId <> 4")
	public List<ClientSystem> findAllWithPOCsExceptSystemAll(Sort sort);
}
