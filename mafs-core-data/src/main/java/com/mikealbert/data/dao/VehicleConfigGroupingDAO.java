package com.mikealbert.data.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mikealbert.data.entity.VehicleConfigGrouping;



public interface VehicleConfigGroupingDAO extends JpaRepository<VehicleConfigGrouping, Long> {
	
	@Query("SELECT distinct (vuq.upfitterQuote.externalAccount.accountName) FROM VehicleConfigGrouping vcg JOIN vcg.vehicleConfigUpfitQuotes vuq WHERE vcg.vcgId = ?1 and vuq.obsoleteYn = 'N'")
	public List<String> getVendorsByConfigGroupingId(Long vcgId);
	
}
