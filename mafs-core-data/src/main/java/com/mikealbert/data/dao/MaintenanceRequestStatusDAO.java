package com.mikealbert.data.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import com.mikealbert.data.entity.MaintenanceRequestStatus;

/**
* DAO for MaintenanceRequestStatus Entity
* @author sibley
*/

public interface MaintenanceRequestStatusDAO extends JpaRepository<MaintenanceRequestStatus, String> {}
