package com.mikealbert.data.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mikealbert.data.entity.Contract;

/**
* DAO for ExternalAccont Entity
* @author sibley
*/
public interface ContractDAO extends JpaRepository<Contract, Long>{    
}

