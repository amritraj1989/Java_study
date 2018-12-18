package com.mikealbert.data.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import com.mikealbert.data.entity.VehicleMovementContact;

public interface VehicleMovementContactDAO extends JpaRepository<VehicleMovementContact, Long>{
	
	
}
