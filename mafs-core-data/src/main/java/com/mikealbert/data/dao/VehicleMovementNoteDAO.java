package com.mikealbert.data.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import com.mikealbert.data.entity.VehicleMovementNote;

public interface VehicleMovementNoteDAO extends JpaRepository<VehicleMovementNote, Long>{
	
	
}
