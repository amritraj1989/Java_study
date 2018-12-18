package com.mikealbert.data.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mikealbert.data.entity.WorkshopCapabilityCode;


/**
 * DAO for WorkshopCapabilityCode Entity
 * 
 * @author Scholle
 */

public interface WorkshopCapabilityCodeDAO extends JpaRepository<WorkshopCapabilityCode, Long>, WorkshopCapabilityCodeDAOCustom {
}
