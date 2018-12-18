package com.mikealbert.service;

import java.util.List;

import org.springframework.data.domain.Pageable;

import com.mikealbert.data.entity.MaintenanceCode;
import com.mikealbert.data.entity.ServiceProviderMaintenanceCode;
import com.mikealbert.exception.MalBusinessException;

public interface MaintenanceCodeService {
	
	public static final String AMMENDMENT = "AMENDMENT";

	public List<ServiceProviderMaintenanceCode> getServiceProviderMaintenanceCode(String code, List<Long> selectedProviderIds, boolean excludeUnapproved);
	public List<ServiceProviderMaintenanceCode> getServiceProviderMaintenanceByMafsCode(String mafsCode, List<Long> selectedProviderId, boolean excludeUnapproved);
	public List<ServiceProviderMaintenanceCode> getServiceProviderCodeByDescription(String description, Long providerId);
	
	public List<MaintenanceCode> getMaintenanceCodesByNameOrCode(String nameOrCode);
	
	public MaintenanceCode getExactMaintenanceCodeByNameOrCode(String nameOrCode);
	public ServiceProviderMaintenanceCode getExactServiceProviderMaintenanceCode(Long smlId);
	
	public ServiceProviderMaintenanceCode saveServiceProviderMaintCode(ServiceProviderMaintenanceCode serviceProviderMaintCode) throws MalBusinessException;
	
	public void updateServiceProviderMaintCode(ServiceProviderMaintenanceCode serviceProviderMaintCode) throws MalBusinessException;
	
	
	public MaintenanceCode saveMaintenanceCode(MaintenanceCode mainCode) throws MalBusinessException;
	
	public List<MaintenanceCode> findMaintenanceCodesByNameOrCode(String searchValue, Pageable page);
	public Long getMaintenanceCodeCountByNameOrCode(String searchValue);
	
	public boolean isServiceProviderCodeAdded(String code, Long providerId, boolean excludeUnapproved);
	
	
}
