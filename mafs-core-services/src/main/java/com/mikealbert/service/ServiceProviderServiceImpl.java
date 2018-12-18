package com.mikealbert.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mikealbert.data.dao.ServiceProviderDAO;
import com.mikealbert.data.dao.ServiceProviderDiscountDAO;
import com.mikealbert.data.dao.ServiceProviderIndicatorsDAO;
import com.mikealbert.data.dao.ServiceProviderInvoiceDAO;
import com.mikealbert.data.dao.ServiceProviderMaintenanceCodeDAO;
import com.mikealbert.data.dao.SupplierCategoryCodeDAO;
import com.mikealbert.data.dao.SupplierDAO;
import com.mikealbert.data.entity.ServiceProvider;
import com.mikealbert.data.entity.ServiceProviderDiscount;
import com.mikealbert.data.entity.ServiceProviderIndicators;
import com.mikealbert.data.entity.ServiceProviderInvoiceHeader;
import com.mikealbert.data.entity.ServiceProviderMaintenanceCode;
import com.mikealbert.data.entity.SupplierCategoryCode;
import com.mikealbert.data.util.DisplayFormatHelper;
import com.mikealbert.data.vo.ServiceProviderDELVO;
import com.mikealbert.data.vo.ServiceProviderDILVO;
import com.mikealbert.data.vo.ServiceProviderDiscountVO;
import com.mikealbert.data.vo.ServiceProviderLOVVO;
import com.mikealbert.data.vo.ServiceProviderMaintenanceGroup;
import com.mikealbert.data.vo.ServiceProviderSELVO;
import com.mikealbert.data.vo.ServiceProviderSILVO;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.exception.MalException;
import com.mikealbert.util.MALUtilities;

@Service("serviceProviderService")
public class ServiceProviderServiceImpl implements ServiceProviderService {

	@Resource
	ServiceProviderDAO serviceProviderDAO;
	@Resource
	WillowConfigService willowConfig;
	@Resource
	ServiceProviderIndicatorsDAO serviceProviderIndicatorDAO;
	@Resource
	ServiceProviderDiscountDAO serviceProviderDiscountDAO;
	@Resource
	ServiceProviderInvoiceDAO serviceProviderInvoiceDAO;
	@Resource
	SupplierCategoryCodeDAO supplierCategoryCodeDAO;
	@Resource
	LookupCacheService lookupCacheService;
	@Resource
	ServiceProviderMaintenanceCodeDAO serviceProviderMaintenanceCodeDAO;
	@Resource SupplierDAO supplierDAO;

	public List<ServiceProviderLOVVO> getAllServiceProviders(String provider, String payee, String zipCode, String phoneNumber,String serviceTypeCode, boolean includeOnlyParents, PageRequest page) {
		try {
			List<ServiceProviderLOVVO> serivceProvidersVO = serviceProviderDAO.searchLOVServiceProvider(provider, payee, zipCode, phoneNumber,serviceTypeCode,	includeOnlyParents, page);
			ServiceProvider serviceProvider;
			List<ServiceProviderDiscount> discounts;
			for(ServiceProviderLOVVO serviceProviderVO : serivceProvidersVO){
				serviceProvider = this.getServiceProvider(serviceProviderVO.getServiceProviderId().longValue());
				discounts = this.getServiceProviderDisountsByDate(serviceProvider.getServiceProviderId(), new Date());
				String msg = DisplayFormatHelper.formatSupplierDiscountForDisplay(
						serviceProvider,discounts, "<br/>");
				
				serviceProviderVO.setDiscountMsg(msg);
			}
			
			return serivceProvidersVO;
		} catch (Exception ex) {
			throw new MalException("generic.error.occured.while",
					new String[] { "finding All Service Providers By Name or Number" },
					ex);
		}

	}

	public List<ServiceProviderSILVO> getVLInternalSupplierLocatorServiceProviders(String latitude, String longitude, String radius, String vehicleType, String repairType, String shopCategory, String parentGroupId) {
		try {
			return serviceProviderDAO.searchSILServiceProvider(latitude, longitude, radius, vehicleType, repairType, shopCategory, parentGroupId);
		} catch(Exception ex) {
			throw new MalException("generic.error.occured.while", new String[] { "finding All Internal Supplier Locator Service Providers" }, ex);			
		}
	}
	
	public List<ServiceProviderSELVO> getVLExternalSupplierLocatorServiceProviders(String latitude, String longitude, String radius, String vehicleType, String repairType, String parentGroupId) {
		try {
			return serviceProviderDAO.searchSELServiceProvider(latitude, longitude, radius, vehicleType, repairType, parentGroupId);
		} catch(Exception ex) {
			throw new MalException("generic.error.occured.while", new String[] { "finding All External Supplier Locator Service Providers" }, ex);			
		}
	}

	public List<ServiceProviderDELVO> getDealerExternalLocatorServiceProviders(String latitude, String longitude, String radius, String makId) {
		try {
			return serviceProviderDAO.searchDELServiceProvider(latitude, longitude, radius, makId);
		} catch(Exception ex) {
			throw new MalException("generic.error.occured.while", new String[] { "finding All Dealer External Locator Service Providers" }, ex);			
		}
	}	

	public List<ServiceProviderDILVO> getDealerInternalLocatorServiceProviders(String latitude, String longitude, String radius, String makId) {
		try {
			return serviceProviderDAO.searchDILServiceProvider(latitude, longitude, radius, makId);
		} catch(Exception ex) {
			throw new MalException("generic.error.occured.while", new String[] { "finding All Dealer Internal Locator Service Providers" }, ex);			
		}
	}

	public List<ServiceProviderMaintenanceGroup> getCachedVLInternalServiceProviderMaintenanceGroups() {
		try {
			return lookupCacheService.getVLInternalServiceProviderMaintenanceGroups();
		} catch(Exception ex) {
			throw new MalException("generic.error.occured.while", new String[] { "finding all Service Provider Maintenance Groups" }, ex);			
		}
	}

	public List<ServiceProviderMaintenanceGroup> getCachedVLExternalServiceProviderMaintenanceGroups() {
		try {
			return lookupCacheService.getVLExternalServiceProviderMaintenanceGroups();
		} catch(Exception ex) {
			throw new MalException("generic.error.occured.while", new String[] { "finding all Service Provider Maintenance Groups" }, ex);			
		}
	}
	
	public int getAllServiceProvidersCount(String provider, String payee, String zipCode, String phoneNumber,String serviceTypeCode, boolean includeOnlyParents) {
		try {
			return serviceProviderDAO.searchLOVServiceProviderCount(provider, payee, zipCode, phoneNumber,serviceTypeCode,includeOnlyParents);
		} catch (Exception ex) {
			throw new MalException("generic.error.occured.while",
					new String[] { "finding All Service Providers Count By Name or Number" },
					ex);
		}

	}
	
	public ServiceProvider getServiceProvider(Long serviceProviderId){
		try {
			return serviceProviderDAO.findById(serviceProviderId).orElse(null);
		} catch (Exception ex) {
			throw new MalException("generic.error.occured.while",
					new String[] { "loading Service Provider By serviceProviderID" },
					ex);
		}
	}
	
	public String getPotentialDILServiceProviderCode(Long pServiceProviderId){
		try {
			return serviceProviderDAO.getPotentialDILServiceProviderCode(pServiceProviderId);
		} catch (Exception ex) {
			throw new MalException("generic.error.occured.while",
					new String[] { "loading Potential Service Provider By SS_ID" },
					ex);
		}
	}
	
	@Override
	public List<ServiceProvider> getServiceProviderByName(String serviceProviderName, PageRequest page){
		try {
			if(!serviceProviderName.endsWith("%") && !serviceProviderName.matches("\\%")){
				serviceProviderName = serviceProviderName + "%";
			}
			return serviceProviderDAO.findByServiceProviderName(serviceProviderName,page);
		} catch (Exception ex) {
			throw new MalException("generic.error.occured.while",
					new String[] { "loading Service Provider By serviceProviderName" },
					ex);
		}
	}

	@Override
	public List<ServiceProvider> getServiceProviderByNameOrCode(String serviceProviderNameOrCode, PageRequest page){
		try {
			if(!serviceProviderNameOrCode.endsWith("%") && !serviceProviderNameOrCode.matches("\\%")){
				serviceProviderNameOrCode = serviceProviderNameOrCode + "%";
			}
			return serviceProviderDAO.findByServiceProviderNameOrCode(serviceProviderNameOrCode,page);
		} catch (Exception ex) {
			throw new MalException("generic.error.occured.while",
					new String[] { "loading Service Provider By serviceProviderNameOrCode" },
					ex);
		}
	}
	

	public List<ServiceProvider> getServiceProviderByNameOrCodeAndServiceType(String serviceProviderNameOrCode,String serviceType,PageRequest page){
		try {
			if(!serviceProviderNameOrCode.endsWith("%") && !serviceProviderNameOrCode.matches("\\%")){
				serviceProviderNameOrCode = serviceProviderNameOrCode + "%";
			}
			if(!MALUtilities.isEmpty(serviceType)){
				serviceType = serviceType +"%";
			}else{
				serviceType	= "%";
			}
			return serviceProviderDAO.findByServiceProviderNameOrCodeAndServiceType(serviceProviderNameOrCode, serviceType, page);
		} catch (Exception ex) {
			throw new MalException("generic.error.occured.while",
					new String[] { "loading Service Provider By serviceProviderNameOrCode" },
					ex);
		}
	}
	
	

	/**
	 * Finds a single ServiceProvider by Id
	 * @param Id
	 * @return ServiceProvider with provider addresses
	 */
	@Transactional(readOnly=true)
	public ServiceProvider getServiceProviderById(Long id) {
		ServiceProvider provider = serviceProviderDAO.findById(id).orElse(null);
		// we also want the addresses loaded so internally we trigger an load (access the size() of the collection) before returning.
		provider.getServiceProviderAddresses().size();
		return provider;
	}

	/**
	 * Finds a list of ServiceProviders by Name or Code (partial or whole); 
	 * The list is also filtered by service provider type codes ('DEALER')
	 * 
	 * @param nameOrCode
	 * @return ServiceProviders with provider addresses
	 */
	@Transactional(readOnly=true)
	public List<ServiceProvider> getServiceProvidersByNameOrCode(String nameOrCode) {
		String newVehicleSupplier = willowConfig.getConfigValue("NEW_VEHICLE_SUPPLIER");
		String secondaryNewVehicleSupplier = willowConfig.getConfigValue("SECONDARY_NEW_VEHICLE_SUP");
		List<String> removedServiceTypeList = new ArrayList<String>();
		
		try {
			
			if(!nameOrCode.endsWith("%") && !nameOrCode.matches("\\%")){
				nameOrCode = nameOrCode + "%";
			}
			
			removedServiceTypeList.add(newVehicleSupplier);
			removedServiceTypeList.add(secondaryNewVehicleSupplier);
			
			return serviceProviderDAO.findByServiceProviderNameOrCodeAndType(nameOrCode,removedServiceTypeList);
		} catch (Exception ex) {
			throw new MalException("generic.error.occured.while", 
					new String[] { "finding ServiceProviders by Name or Code" }, ex);			
		}
	}

	/**
	 * This method will determine whether the selected service provider or its parent has po_auto_complete_flag is Y or not.
	 * @param serviceProviderId
	 * @return Boolean
	 */
	@Transactional(readOnly=true)	
	public Boolean isAutoCompleteServiceProvider(ServiceProvider serviceProvider) {
		ServiceProviderIndicators serviceProviderInd = serviceProviderIndicatorDAO
				.findByServiceProviderId(serviceProvider.getServiceProviderId());
		if (MALUtilities.isEmpty(serviceProvider.getServiceProviderId())) {
			return false;
		}
		if ((!MALUtilities.isEmpty(serviceProviderInd)) && MALUtilities.convertYNToBoolean(serviceProviderInd.getPoAutoCompleteFlag())) {
			return true;
		} else {
			if (MALUtilities.isEmpty(serviceProvider)) {
				return false;
			}
			ServiceProvider parentProvider = serviceProviderDAO
					.findById(serviceProvider.getServiceProviderId()).orElse(null)
					.getParentServiceProvider();
			if (MALUtilities.isEmpty(parentProvider)) {
				return false;
			}
			serviceProviderInd = serviceProviderIndicatorDAO
					.findByServiceProviderId(parentProvider
							.getServiceProviderId());
			if (MALUtilities.isEmpty(serviceProviderInd)) {
				return false;
			}
			if ((!MALUtilities.isEmpty(serviceProviderInd)) && MALUtilities.convertYNToBoolean(serviceProviderInd.getPoAutoCompleteFlag())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public List<ServiceProviderDiscount> getServiceProviderDisountsByDate(
			Long serviceProviderId, Date effectiveDate) {
		
		List<ServiceProviderDiscount> serviceProviderDiscountList = null;
		
		try{
			serviceProviderDiscountList = serviceProviderDiscountDAO.getDiscountByServiceProvider(serviceProviderId, effectiveDate);
		}catch(Exception ex){
			throw new MalException("generic.error.occured.while", 
					new String[] { "finding ServiceProvider Discounts by Date" }, ex);		
		}
		return serviceProviderDiscountList;
	}

	@Override
	public ServiceProvider getParentProvider(Long serviceProviderId) {
		// find the service provider and if it has a parent return it
		ServiceProvider parentProvider = null;
		ServiceProvider provider = serviceProviderDAO.findById(serviceProviderId).orElse(null);
		if(!MALUtilities.isEmpty(provider.getParentServiceProvider())){
			parentProvider = provider.getParentServiceProvider();
		}
		return parentProvider;
	}
	
	
	//TODO: the MAFS business rules are in flux a bit, for MVI/Vision we cannot let then create 2 stores under 1 parent with the same number
	// in Willow these restrictions are not there and there are alot of existing stores created that breaks this rule.
	@Transactional
	public ServiceProvider saveServiceProvider(ServiceProvider serviceProvider) throws MalBusinessException {
		//TODO: do we check for an existing service provider and raise a business exception
		if(MALUtilities.isEmpty(serviceProvider.getServiceProviderId())){
			ServiceProvider existingProvider = serviceProviderDAO.getServiceProviderByCodeAndParent(serviceProvider.getServiceProviderNumber(), serviceProvider.getParentServiceProvider().getServiceProviderId());
			if(!MALUtilities.isEmpty(existingProvider)){
			//if(!MALUtilities.isEmpty(existingProvider) && serviceProvider.getServiceProviderName().equalsIgnoreCase(existingProvider.getServiceProviderName())){	
				throw new MalBusinessException("A Service Provider already exists for this number : " + existingProvider.getServiceProviderNumber());
			}
		}
		
		serviceProvider = serviceProviderDAO.saveAndFlush(serviceProvider);

		return serviceProvider;
	}

	@Override
	public ServiceProvider getServiceProviderByProviderCode(
			String serviceProviderCode, ServiceProvider parentProvider) {
		
		ServiceProvider provider = serviceProviderDAO.getServiceProviderByCodeAndParent(serviceProviderCode, parentProvider.getServiceProviderId());

		return provider;
	}

	/**
	 * Finds a single parent provider (BILL_PROF type) by name or partial name (case insensitive like) with some extra string translation to exclude special characters that are allowed in the DB but could not
	 * be in a folder name (used for the "vendor folder" in the maintenance data feeds (interfaces); 
	 * 
	 * @param name
	 * @return a ServiceProvider or NULL if none or multiples
	 */
	//TODO: BILL_PROF needs to be changed to just looking for a parent
	@Override
	public ServiceProvider getParentProviderByNameInFileFmt(
			String parentProviderName) {
		ServiceProvider parent = null;
		List<ServiceProvider> results = serviceProviderDAO.findParentByProviderNameInFileFmt(parentProviderName);
		if(results.size() == 1){
			parent = results.get(0);
		}
		
		return parent;
	}

	@Override
	public List<ServiceProvider> getServiceProvidersByParentId(
			Long parentProviderId) {
		return serviceProviderDAO.findByParent(parentProviderId);
	}

	@Override
	public List<ServiceProvider> getServiceProviderByNameOrCode(String serviceProviderNameOrCode, boolean includeOnlyParents, PageRequest page){
		try {
			if(!serviceProviderNameOrCode.endsWith("%") && !serviceProviderNameOrCode.matches("\\%")){
				serviceProviderNameOrCode = serviceProviderNameOrCode + "%";
			}
			if(includeOnlyParents){
				return serviceProviderDAO.findByServiceProviderNameOrCodeOnlyParents(serviceProviderNameOrCode,page);
			}else{
				return serviceProviderDAO.findByServiceProviderNameOrCode(serviceProviderNameOrCode,page);
			}
		} catch (Exception ex) {
			throw new MalException("generic.error.occured.while",
					new String[] { "loading Service Provider By serviceProviderNameOrCode" },
					ex);
		}	
	}

	@Override
	public ServiceProviderInvoiceHeader saveServiceProviderInvoice(
			ServiceProviderInvoiceHeader invoice) throws MalBusinessException {
		
		return serviceProviderInvoiceDAO.saveAndFlush(invoice);
	}

	@Override
	public void processServiceProviderInvoices(Long loadId,
			Long cId, String userName) throws MalException {
		try{
			serviceProviderInvoiceDAO.processServiceProviderInvoiceLoad(loadId, cId, userName);
		}catch(Exception ex){
			throw new MalException("service.validation", new String[] { ex.getCause().getMessage() });
		}
	}

	@Override
	public List<SupplierCategoryCode> getCachedVLMaintenanceSupplierCategoryCodes() {
		return lookupCacheService.getVLMaintenanceSupplierCategoryCodes();
	}

	@Override
	public List<ServiceProvider> getServiceProviderByExactName(String name) throws MalException {
		try{
			return serviceProviderDAO.findByServiceProviderExactName(name);
		}catch(Exception ex){
			throw new MalException("service.validation", new String[] { ex.getCause().getMessage() });
		}
	}

	
	@Override
	@Transactional
	public void updateServiceProviderDiscounts(long serviceProviderId, List<ServiceProviderDiscountVO> discountList) throws MalException {
		try{
			for(ServiceProviderDiscountVO spdVO : discountList) {
				List<ServiceProviderMaintenanceCode> codeList = serviceProviderMaintenanceCodeDAO.getServiceProviderMaintCodesByMaintenanceCodeIdAndProvider
						(spdVO.getMcoId(), serviceProviderId);
				for(ServiceProviderMaintenanceCode spmc : codeList) {
					spmc.setDiscountFlag(spdVO.isDiscount() ? "Y" : "N");
					serviceProviderMaintenanceCodeDAO.saveAndFlush(spmc);
				}
			}
		}catch(Exception ex){
			throw new MalException("service.validation", new String[] { ex.getCause().getMessage() });
		}
	}

	@Override
	public List<Object> getSupplierByMakeCodeOrName(String makeCode, String manufacturer) throws MalException {

		return supplierDAO.getSupplierByMakeCodeOrName(makeCode, manufacturer);
	}

	
}