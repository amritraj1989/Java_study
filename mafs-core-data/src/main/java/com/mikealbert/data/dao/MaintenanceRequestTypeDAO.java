package com.mikealbert.data.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import com.mikealbert.data.entity.MaintenanceRequestType;

/**
* DAO for MaintenanceRequestType Entity
* @author sibley
*/

public interface MaintenanceRequestTypeDAO extends JpaRepository<MaintenanceRequestType, String> {}
