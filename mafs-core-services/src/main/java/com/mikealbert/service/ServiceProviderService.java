package com.mikealbert.service;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.PageRequest;

import com.mikealbert.data.entity.ServiceProvider;
import com.mikealbert.data.entity.ServiceProviderDiscount;
import com.mikealbert.data.entity.ServiceProviderInvoiceHeader;
import com.mikealbert.data.entity.SupplierCategoryCode;
import com.mikealbert.data.vo.ServiceProviderDELVO;
import com.mikealbert.data.vo.ServiceProviderDILVO;
import com.mikealbert.data.vo.ServiceProviderDiscountVO;
import com.mikealbert.data.vo.ServiceProviderLOVVO;
import com.mikealbert.data.vo.ServiceProviderMaintenanceGroup;
import com.mikealbert.data.vo.ServiceProviderSELVO;
import com.mikealbert.data.vo.ServiceProviderSILVO;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.exception.MalException;

public interface ServiceProviderService {
	public List<ServiceProviderLOVVO> getAllServiceProviders(String provider, String payee, String zipCode, String phoneNumber,String serviceTypeCode, boolean includeOnlyParents, PageRequest page);
	public List<ServiceProviderSILVO> getVLInternalSupplierLocatorServiceProviders(String latitude, String longitude, String radius, String vehicleType, String repairType, String shopCategory, String parentGroupId);
	public List<ServiceProviderSELVO> getVLExternalSupplierLocatorServiceProviders(String latitude, String longitude, String radius, String vehicleType, String repairType, String parentGroupId);
	public List<ServiceProviderDELVO> getDealerExternalLocatorServiceProviders(String latitude, String longitude, String radius, String makId);
	public List<ServiceProviderDILVO> getDealerInternalLocatorServiceProviders(String latitude, String longitude, String radius, String makId);
	public String getPotentialDILServiceProviderCode(Long pServiceProviderId);
	public List<ServiceProviderMaintenanceGroup> getCachedVLInternalServiceProviderMaintenanceGroups();
	public List<ServiceProviderMaintenanceGroup> getCachedVLExternalServiceProviderMaintenanceGroups();
	public int getAllServiceProvidersCount(String provider, String payee, String zipCode, String phoneNumber,String serviceTypeCode, boolean includeOnlyParents);
	public ServiceProvider getServiceProvider(Long serviceProviderId);
	
	public List<ServiceProvider> getServiceProviderByName(String serviceProviderName, PageRequest page);
	public List<ServiceProvider> getServiceProviderByNameOrCode(String serviceProviderNameOrCode, PageRequest page);
	public List<ServiceProvider> getServiceProviderByNameOrCode(String serviceProviderNameOrCode, boolean includeOnlyParents, PageRequest page);
	public Boolean isAutoCompleteServiceProvider (ServiceProvider serviceProvider);
	public List<ServiceProviderDiscount> getServiceProviderDisountsByDate(Long serviceProviderId, Date effectiveDate);
	public ServiceProvider getParentProvider(Long serviceProviderId);
	public List<ServiceProvider> getServiceProviderByNameOrCodeAndServiceType(String serviceProviderNameOrCode,String serviceType,PageRequest page);
	public ServiceProvider getServiceProviderByProviderCode(String serviceProviderCode, ServiceProvider parentProvider);
	public ServiceProvider getParentProviderByNameInFileFmt(String parentProviderName);
	public List<ServiceProvider> getServiceProvidersByParentId(Long parentProviderId);
	
	public ServiceProvider saveServiceProvider(ServiceProvider serviceProvider) throws MalBusinessException;
	
	public ServiceProviderInvoiceHeader saveServiceProviderInvoice(ServiceProviderInvoiceHeader invoice) throws MalBusinessException;
	public void processServiceProviderInvoices(Long loadId, Long cId, String userName) throws MalException;
	public List<ServiceProvider> getServiceProviderByExactName(String name) throws MalException;
	public void updateServiceProviderDiscounts(long serviceProviderId, List<ServiceProviderDiscountVO> discountList) throws MalException;

	public List<SupplierCategoryCode> getCachedVLMaintenanceSupplierCategoryCodes();
	
	public List<Object> getSupplierByMakeCodeOrName(String makeCode, String manufacturer) throws MalException; 
}
