package com.mikealbert.data.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.mikealbert.data.entity.ClientMethod;

/**
* DAO for ClientMethod Entity
* @author sibley
*/

public interface ClientMethodDAO extends JpaRepository<ClientMethod, Long> {
	@Query("SELECT cmet " +
			"   FROM ClientMethod cmet " +
			"   WHERE cmet.name = ?1 ")
	public ClientMethod findByName(String clientMethodName);
}
