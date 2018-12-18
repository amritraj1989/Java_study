package com.mikealbert.data.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mikealbert.data.entity.ServiceProviderIndicators;

/**
 * DAO for ServiceProviderIndicators Entity
 * 
 * @author Raj
 */

public interface ServiceProviderIndicatorsDAO extends JpaRepository<ServiceProviderIndicators, Long> {
	@Query("FROM ServiceProviderIndicators supInd WHERE supInd.serviceProvider.serviceProviderId = ?1")
	public ServiceProviderIndicators findByServiceProviderId(Long serviceProviderId);
}