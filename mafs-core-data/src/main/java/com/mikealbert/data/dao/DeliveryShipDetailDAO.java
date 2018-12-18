package com.mikealbert.data.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mikealbert.data.entity.DeliveryShipDetail;

public interface DeliveryShipDetailDAO extends JpaRepository<DeliveryShipDetail, Long>{
	
	@Query("SELECT dsd FROM DeliveryShipDetail dsd WHERE dsd.docId = ?1")
	public DeliveryShipDetail findByDocId(Long docId);

}
