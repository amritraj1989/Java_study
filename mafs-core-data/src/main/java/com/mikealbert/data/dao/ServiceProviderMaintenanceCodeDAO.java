package com.mikealbert.data.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mikealbert.data.entity.ServiceProviderMaintenanceCode;

/**
* DAO for ServiceProviderMaintenanceCode Entity
* @author sibley
*/

public interface ServiceProviderMaintenanceCodeDAO extends ServiceProviderMaintenanceCodeDAOCustom, JpaRepository<ServiceProviderMaintenanceCode, Long> {
	@Query("FROM ServiceProviderMaintenanceCode smc WHERE LOWER(smc.code) = LOWER(?1) AND smc.serviceProvider.serviceProviderId = ?2 ")
	public ServiceProviderMaintenanceCode getServiceProviderMaintCodeByCodeAndProvider(String serviceProviderMaintCode, Long providerId);
	
	@Query("FROM ServiceProviderMaintenanceCode smc WHERE LOWER(smc.description) = LOWER(?1) AND smc.serviceProvider.serviceProviderId = ?2")
	public List<ServiceProviderMaintenanceCode> getServiceProviderMaintCodesByDescription(String serviceProviderMaintCodeDes, Long providerId);

	@Query("FROM ServiceProviderMaintenanceCode smc WHERE smc.maintenanceCode.mcoId = ?1 AND smc.serviceProvider.serviceProviderId = ?2")
	public List<ServiceProviderMaintenanceCode> getServiceProviderMaintCodesByMaintenanceCodeIdAndProvider(long maintenanceCodeId, Long providerId);

}
