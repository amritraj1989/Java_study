package com.mikealbert.data.dao;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.mikealbert.data.entity.VehicleMovementAddrLink;

public interface VehicleMovementAddrLinkDAO extends JpaRepository<VehicleMovementAddrLink, Long>{
	
	@Query("Select vmal from VehicleMovementAddrLink vmal where vmal.extAccAddress.eaaId = ?1")
	public List<VehicleMovementAddrLink> findByExtAccountAddressId(Long eaalId);
}
