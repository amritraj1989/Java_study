package com.mikealbert.data.dao;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.mikealbert.data.entity.VehicleMovement;

public interface VehicleMovementDAO extends JpaRepository<VehicleMovement, Long>{
	
	@Query("from VehicleMovement vm where vm.fleetMaster.fmsId = ?1 and vm.vehTranTypeCode = 'TRANSPORT' and vm.vehStatusCode = 'OPEN'")
	public VehicleMovement findOpenTranportByFmsId(Long fmsId);
	
	@Query("Select vm from VehicleMovement vm where vm.fleetMaster.fmsId = ?1 and vm.vehStatusCode not in('CLOSED', 'CANCELLED')")
	public List<VehicleMovement> findOpenVehicleMovementByFmsId(Long fmsId);
}
