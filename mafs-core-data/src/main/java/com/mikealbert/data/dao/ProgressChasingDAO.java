package com.mikealbert.data.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mikealbert.data.entity.MaintenanceRequest;

public interface ProgressChasingDAO extends ProgressChasingDAOCustom, JpaRepository<MaintenanceRequest, Long> {

}
