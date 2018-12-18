package com.mikealbert.data.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mikealbert.data.entity.ClientFinanceType;

public interface ClientFinanceTypeDAO extends JpaRepository<ClientFinanceType, Long> {
	
}
