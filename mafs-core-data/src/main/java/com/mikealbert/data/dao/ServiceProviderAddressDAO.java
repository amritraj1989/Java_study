package com.mikealbert.data.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mikealbert.data.entity.ServiceProviderAddress;

/**
 * DAO for ServiceProviderAddress Entity
 * 
 */

public interface ServiceProviderAddressDAO extends JpaRepository<ServiceProviderAddress, Long>, ServiceProviderAddressDAOCustom {
	
}