package com.mikealbert.data.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mikealbert.data.entity.ClientFinance;

public interface ClientFinanceDAO extends ClientFinanceDAOCustom, JpaRepository<ClientFinance, Long> {
	
}
