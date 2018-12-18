package com.mikealbert.data.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mikealbert.data.entity.OraSession;

public interface OraSessionDAO extends JpaRepository<OraSession,Long>, OraSessionDAOCustom {
	
}
