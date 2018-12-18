package com.mikealbert.data.dao;


import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mikealbert.data.entity.FleetMaster;

/**
* DAO for DriverAllocCodeDAO Entity
* @author sibley
*/
public interface VehicleSearchDAO extends JpaRepository<FleetMaster, Long>, VehicleSearchDAOCustom {
	final String VEHICLE_SEARCH_STATUS_BOTH = "B";
	final String VEHICLE_SEARCH_STATUS_ACTIVE = "A";
	final String VEHICLE_SEARCH_STATUS_INACTIVE = "I";
	
}
