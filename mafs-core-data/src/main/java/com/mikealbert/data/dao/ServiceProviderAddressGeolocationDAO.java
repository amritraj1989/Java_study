package com.mikealbert.data.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mikealbert.data.entity.ServiceProviderAddressGeolocation;


/**
 * DAO for ServiceProviderAddressGeolocation Entity
 * 
 * @author Scholle
 */

public interface ServiceProviderAddressGeolocationDAO extends JpaRepository<ServiceProviderAddressGeolocation, Long> {
}
