package com.mikealbert.data.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mikealbert.data.entity.DriverAddressGeolocation;


/**
 * DAO for DriverAddressGeolocation Entity
 * 
 * @author Scholle
 */

public interface DriverAddressGeolocationDAO extends JpaRepository<DriverAddressGeolocation, Long> {
}
