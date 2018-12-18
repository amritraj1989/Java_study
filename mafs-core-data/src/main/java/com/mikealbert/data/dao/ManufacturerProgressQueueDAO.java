package com.mikealbert.data.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mikealbert.data.entity.FleetMaster;


public interface ManufacturerProgressQueueDAO extends ManufacturerProgressQueueDAOCustom, JpaRepository<FleetMaster, Long>{

}
