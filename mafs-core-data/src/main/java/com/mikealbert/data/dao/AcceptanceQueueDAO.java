package com.mikealbert.data.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mikealbert.data.entity.AcceptanceQueueV;

public interface AcceptanceQueueDAO extends JpaRepository<AcceptanceQueueV, Long>, AcceptanceQueueDAOCustom{
	
		
}
