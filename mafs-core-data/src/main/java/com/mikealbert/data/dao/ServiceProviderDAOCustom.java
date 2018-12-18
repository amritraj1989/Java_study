package com.mikealbert.data.dao;

import java.util.List;

import org.springframework.data.domain.PageRequest;

import com.mikealbert.data.vo.ServiceProviderDELVO;
import com.mikealbert.data.vo.ServiceProviderDILVO;
import com.mikealbert.data.vo.ServiceProviderLOVVO;
import com.mikealbert.data.vo.ServiceProviderMaintenanceGroup;
import com.mikealbert.data.vo.ServiceProviderSELVO;
import com.mikealbert.data.vo.ServiceProviderSILVO;

public interface ServiceProviderDAOCustom  {	
	public int searchLOVServiceProviderCount(String provider, String payee, String zipCode, String phoneNumber,String serviceTypeCode, boolean includeOnlyParents);

	public List<ServiceProviderLOVVO> searchLOVServiceProvider(String provider, String payee, String zipCode, String phoneNumber,String serviceTypeCode, boolean includeOnlyParents, PageRequest pageReq);
	public List<ServiceProviderSILVO> searchSILServiceProvider(String latitude, String longitude, String radius, String vehicleType, String repairType, String shopCategory, String dealerGroupId);
	public List<ServiceProviderSELVO> searchSELServiceProvider(String latitude, String longitude, String radius, String vehicleType, String repairType, String dealerGroupId);
	public List<ServiceProviderDELVO> searchDELServiceProvider(String latitude, String longitude, String radius, String makId);
	public List<ServiceProviderDILVO> searchDILServiceProvider(String latitude, String longitude, String radius, String makId);
	public List<ServiceProviderMaintenanceGroup> getVLServiceProviderMaintenanceGroups(String isNetorkVendor);
	public String getPotentialDILServiceProviderCode(Long pServiceProviderId);
}
