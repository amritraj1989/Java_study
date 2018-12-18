package com.mikealbert.data.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import com.mikealbert.data.entity.MaintenanceCode;
import com.mikealbert.data.entity.MaintenanceRechargeCode;

/**
* DAO for MaintenanceRechargeCode Entity
* @author sibley
*/

public interface MaintenanceRechargeCodeDAO extends JpaRepository<MaintenanceRechargeCode, String> {}
