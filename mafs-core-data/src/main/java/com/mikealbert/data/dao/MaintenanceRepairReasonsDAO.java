package com.mikealbert.data.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mikealbert.data.entity.MaintenanceRepairReasons;

/**
* DAO for MaintenanceRepairReasons Entity
* @author opitz
*/

public interface MaintenanceRepairReasonsDAO extends JpaRepository<MaintenanceRepairReasons, String> {}

