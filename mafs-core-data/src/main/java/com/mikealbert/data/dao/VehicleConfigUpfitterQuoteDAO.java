package com.mikealbert.data.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mikealbert.data.entity.VehicleConfigUpfitQuote;



public interface VehicleConfigUpfitterQuoteDAO extends JpaRepository<VehicleConfigUpfitQuote, Long> {
	
	@Query("select vcuq FROM VehicleConfigUpfitQuote vcuq where vcuq.upfitterQuote.ufqId = ?1")
	public List<VehicleConfigUpfitQuote> getVehicleConfigUpfitQuoteByUpfitterQuoteId(Long ufqId);
	
}
