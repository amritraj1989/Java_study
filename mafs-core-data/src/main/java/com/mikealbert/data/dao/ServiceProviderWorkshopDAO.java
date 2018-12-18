package com.mikealbert.data.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mikealbert.data.entity.ServiceProviderWorkshop;


/**
 * DAO for SupplierWorkshop Entity
 * 
 * @author Scholle
 */

public interface ServiceProviderWorkshopDAO extends JpaRepository<ServiceProviderWorkshop, Long> {
}
