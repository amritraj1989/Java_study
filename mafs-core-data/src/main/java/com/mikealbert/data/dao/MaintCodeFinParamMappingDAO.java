package com.mikealbert.data.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mikealbert.data.entity.MaintenanceCode;

public interface MaintCodeFinParamMappingDAO extends MaintCodeFinParamMappingDAOCustom, JpaRepository<MaintenanceCode, Long> {

}
