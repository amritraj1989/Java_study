package com.mikealbert.data.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mikealbert.data.entity.DriverCostCenter;

/**
* DAO 
* @author Lizak
*/

public interface DriverCostCenterDAO extends JpaRepository<DriverCostCenter, Long> {
		
}
