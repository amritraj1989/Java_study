package com.mikealbert.data.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mikealbert.data.entity.ServiceProviderDiscount;

import java.util.Date;
import java.util.List;

/**
 * DAO for ServiceProviderDiscount Entity
 * 
 * @author Raj
 */

public interface ServiceProviderDiscountDAO extends JpaRepository<ServiceProviderDiscount, Long> {
	@Query("FROM ServiceProviderDiscount spd WHERE spd.serviceProvider.serviceProviderId = ?1 AND NVL(spd.serviceProvider.networkVendor, 'N') = 'Y' AND (spd.labourDisc IS NOT NULL OR spd.partsDisc IS NOT NULL OR spd.tyreDisc IS NOT NULL) AND spd.effectiveDate = (SELECT TRUNC(MAX(spd1.effectiveDate)) from ServiceProviderDiscount spd1 where spd.serviceProvider.serviceProviderId = spd1.serviceProvider.serviceProviderId AND spd1.effectiveDate <= TRUNC(?2))")
	public List<ServiceProviderDiscount> getDiscountByServiceProvider(Long serviceProviderId, Date effectiveDate);
}