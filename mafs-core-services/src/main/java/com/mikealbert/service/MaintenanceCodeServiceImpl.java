package com.mikealbert.service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.googlecode.cqengine.IndexedCollection;
import com.googlecode.cqengine.query.Query;
import com.googlecode.cqengine.resultset.ResultSet;
import com.mikealbert.common.MalLogger;
import com.mikealbert.common.MalLoggerFactory;
import com.mikealbert.data.dao.MaintenanceCodeDAO;
import com.mikealbert.data.dao.ServiceProviderDAO;
import com.mikealbert.data.dao.ServiceProviderMaintenanceCodeDAO;
import com.mikealbert.data.entity.MaintenanceCode;
import com.mikealbert.data.entity.ServiceProviderMaintenanceCode;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.exception.MalException;
import com.mikealbert.util.MALUtilities;
import com.mikealbert.service.LookupQueryAttributes;

import static com.googlecode.cqengine.query.QueryFactory.*;


@Service("maintenanceCodeService")
public class MaintenanceCodeServiceImpl implements MaintenanceCodeService {
	
	@Resource MaintenanceCodeDAO maintenanceCodeDAO;
	@Resource LookupCacheService lookupCacheService;
	@Resource ServiceProviderDAO serviceProviderDAO;
	@Resource ServiceProviderMaintenanceCodeDAO serviceProviderMaintenanceCodeDAO;
	MalLogger logger = MalLoggerFactory.getLogger(this.getClass());
	
	private IndexedCollection<MaintenanceCode> maintenanceCodes;
	private IndexedCollection<ServiceProviderMaintenanceCode> serviceProviderMaintenanceCodes;
	
	private static final String AMMENDMENT = "AMENDMENT";
    
	public void init() {
		maintenanceCodes = lookupCacheService.getMaintenanceCodesIndexed();
		serviceProviderMaintenanceCodes = lookupCacheService.getServiceProviderMaintenanceCodesIndexed();
	}

	@Override
	public List<MaintenanceCode> getMaintenanceCodesByNameOrCode(
			String nameOrCode) {
		
		init();
		
		ArrayList<MaintenanceCode> codes = new ArrayList<MaintenanceCode>();

		Query<MaintenanceCode> query;
		ResultSet<MaintenanceCode> codeResults = null;
		
		if((!MALUtilities.isEmptyString(nameOrCode)) && Character.isDigit(nameOrCode.charAt(0))){
			query = startsWith(LookupQueryAttributes.MAINTENANCE_CODE, nameOrCode);
			codeResults = maintenanceCodes.retrieve(query, queryOptions(orderBy(ascending(LookupQueryAttributes.MAINTENANCE_CODE))));
		}else{
			query = contains(LookupQueryAttributes.MAINTENANCE_CODE_DESC, nameOrCode.toUpperCase());
			codeResults = maintenanceCodes.retrieve(query, queryOptions(orderBy(ascending(LookupQueryAttributes.MAINTENANCE_CODE_DESC))));
		}
		
		
		for(MaintenanceCode code : codeResults){
			codes.add(code);
		}

		return codes;
	}
		
	/**
	 * This method will search ServiceProviderMaintenanceCode based on service code and service provider
	 * This method excludes 
	 * @param Service Provider Maintenance Code and Service Provider
	 * @return ServiceProviderMaintenanceCode entity
	 */	
	public List<ServiceProviderMaintenanceCode> getServiceProviderMaintenanceCode(String code, List<Long> selectedProviderIds, boolean excludeUnapproved){
		List<ServiceProviderMaintenanceCode> serviceProviderMaintenanceCodes = lookupCacheService.getServiceProviderMaintenanceCodes();
		List<ServiceProviderMaintenanceCode> filteredServiceProviderMaintCodes = new ArrayList<ServiceProviderMaintenanceCode>();
		
		for(ServiceProviderMaintenanceCode spmc : serviceProviderMaintenanceCodes){
			for(Long selectedProviderId: selectedProviderIds){
				if(MALUtilities.isNotEmptyString(spmc.getCode()) && spmc.getCode().equalsIgnoreCase(code) && spmc.getServiceProvider().getServiceProviderId().equals(selectedProviderId)){				
					if(excludeUnapproved){
						if(MALUtilities.isNotEmptyString(spmc.getApprovedBy())){
							filteredServiceProviderMaintCodes.add(spmc);
						}
					}else{
						filteredServiceProviderMaintCodes.add(spmc);
					}
				}
			}
		}
		
		return filteredServiceProviderMaintCodes;		
	}
	
	/**
	 * This method will search ServiceProviderMaintenanceCode based on MAFS maintenance code and service provider
	 * @param Service Provider Maintenance Code and Service Provider
	 * @return ServiceProviderMaintenanceCode entity
	 */	
	public List<ServiceProviderMaintenanceCode> getServiceProviderMaintenanceByMafsCode(String maintCode, List<Long> selectedProviderIds, boolean excludeUnapproved){
		List<ServiceProviderMaintenanceCode> serviceProviderMaintenanceCodes = lookupCacheService.getServiceProviderMaintenanceCodes();
		List<ServiceProviderMaintenanceCode> filteredServiceProviderMaintCodes = new ArrayList<ServiceProviderMaintenanceCode>();
		

		
		for(ServiceProviderMaintenanceCode spmc : serviceProviderMaintenanceCodes){
			if(!MALUtilities.isEmpty(spmc.getMaintenanceCode())){
				for(Long selectedProviderId: selectedProviderIds){
					if(spmc.getMaintenanceCode().getCode().equalsIgnoreCase(maintCode) && spmc.getServiceProvider().getServiceProviderId().equals(selectedProviderId)){				
						if(excludeUnapproved){
							if(MALUtilities.isNotEmptyString(spmc.getApprovedBy())){
								filteredServiceProviderMaintCodes.add(spmc);
							}
						}else{
							filteredServiceProviderMaintCodes.add(spmc);
						}
					}
				}
			}
		}
		
		return filteredServiceProviderMaintCodes;		
	}

	@Override
	public MaintenanceCode getExactMaintenanceCodeByNameOrCode(
			String nameOrCode) {

		init();
		
		MaintenanceCode result = null;
		Query<MaintenanceCode> query;
		
		if((!MALUtilities.isEmptyString(nameOrCode)) && (Character.isDigit(nameOrCode.charAt(0)) || nameOrCode.equalsIgnoreCase(AMMENDMENT)) ){
			query = startsWith(LookupQueryAttributes.MAINTENANCE_CODE, nameOrCode);
		}else{
			query = startsWith(LookupQueryAttributes.MAINTENANCE_CODE_DESC, nameOrCode);
		}
		
		ResultSet<MaintenanceCode> codeResults = maintenanceCodes.retrieve(query);
		if(codeResults.size() == 1){
			for(MaintenanceCode code : codeResults){
				result = code;
				break;
			}
			
			
		}
		
		return result;
	}
	
	@Transactional
	public ServiceProviderMaintenanceCode saveServiceProviderMaintCode(ServiceProviderMaintenanceCode serviceProviderMaintCode) throws MalBusinessException {
        

		//check for an existing service provider code and raise a business exception
		if(MALUtilities.isEmpty(serviceProviderMaintCode.getSmlId())){
			ServiceProviderMaintenanceCode existingCode = serviceProviderMaintenanceCodeDAO.getServiceProviderMaintCodeByCodeAndProvider(serviceProviderMaintCode.getCode(),serviceProviderMaintCode.getServiceProvider().getServiceProviderId());
			if(!MALUtilities.isEmpty(existingCode)){
				throw new MalBusinessException("A Vendor/Service Provider Maint Code already exists with this value : " + serviceProviderMaintCode.getCode() + " for Service Provider : " + serviceProviderMaintCode.getServiceProvider().getServiceProviderName());
			}
		}
		serviceProviderMaintenanceCodeDAO.saveAndFlush(serviceProviderMaintCode);

		return serviceProviderMaintCode;
	}
	
	@Transactional
	public void updateServiceProviderMaintCode (
			ServiceProviderMaintenanceCode serviceProviderMaintCode) throws MalBusinessException {

		ServiceProviderMaintenanceCode providerCodeFromDb = serviceProviderMaintenanceCodeDAO.findById(serviceProviderMaintCode.getSmlId()).orElse(null);
		providerCodeFromDb.setCode(serviceProviderMaintCode.getCode());
		providerCodeFromDb.setDescription(serviceProviderMaintCode.getDescription());
		providerCodeFromDb.setApprovedBy(serviceProviderMaintCode.getApprovedBy());
		providerCodeFromDb.setApprovedDate(serviceProviderMaintCode.getApprovedDate());
		
		if(!MALUtilities.isEmpty(serviceProviderMaintCode.getMaintenanceCode())){
			MaintenanceCode maintCodeFromDb = maintenanceCodeDAO.findById(serviceProviderMaintCode.getMaintenanceCode().getMcoId()).orElse(null);
			providerCodeFromDb.setMaintenanceCode(maintCodeFromDb);
		}else{
			providerCodeFromDb.setMaintenanceCode(null);
		}
		
		
		serviceProviderMaintenanceCodeDAO.saveAndFlush(providerCodeFromDb);		
	}
	
	@Transactional
	public MaintenanceCode saveMaintenanceCode(MaintenanceCode mainCode) throws MalBusinessException{
		

		//check for an existing maintenance code and raise a business exception
		if(MALUtilities.isEmpty(mainCode.getMcoId())){
			MaintenanceCode existingCode = maintenanceCodeDAO.findByCode(mainCode.getCode());
			if(!MALUtilities.isEmpty(existingCode)){
				throw new MalBusinessException("A MAFS Maintenance Code already exists with this value : " + mainCode.getCode());
			}
		}
		maintenanceCodeDAO.saveAndFlush(mainCode);

		return mainCode;
		
	}

	@Override
	public List<MaintenanceCode> findMaintenanceCodesByNameOrCode(
			String searchValue, Pageable page) {
		
		try {
			if(MALUtilities.isNotEmptyString(searchValue) && Character.isDigit(searchValue.charAt(0))){
				if(!searchValue.endsWith("%") && !searchValue.matches("\\%")){
					searchValue = searchValue + "%";
				}
			} else{
				searchValue = "%" + searchValue + "%";
			}
			
			return maintenanceCodeDAO.findByMaintCodeOrDescription(searchValue,page);
			
		} catch (Exception ex) {
			throw new MalException("generic.error.occured.while", 
					new String[] { "finding Maintenance Codes by Code or Description" }, ex);			
		}
	}

	@Override
	public Long getMaintenanceCodeCountByNameOrCode(String searchValue) {
		
		try {
			if(MALUtilities.isNotEmptyString(searchValue) && Character.isDigit(searchValue.charAt(0))){
				if(!searchValue.endsWith("%") && !searchValue.matches("\\%")){
					searchValue = searchValue + "%";
				}
			} else{
				searchValue = "%" + searchValue + "%";
			}
			
			return maintenanceCodeDAO.getCountByMaintCodeOrDescription(searchValue);
			
		} catch (Exception ex) {
			throw new MalException("generic.error.occured.while", 
					new String[] { "get Maintenance Codes Count by Code or Description" }, ex);			
		}
		
	}

	@Override
	public boolean isServiceProviderCodeAdded(String code, Long providerId,
			boolean excludeUnapproved) {
		try {
			
			return serviceProviderMaintenanceCodeDAO.isProviderMaintCodeAdded(code, providerId, excludeUnapproved);
			
		} catch (Exception ex) {
			throw new MalException("generic.error.occured.while", 
					new String[] { " checking whether a Vendor Maintanance Code already exists " + code }, ex);			
		}
		
	}

	@Override
	public List<ServiceProviderMaintenanceCode> getServiceProviderCodeByDescription(
			String description, Long providerId) {
	
		return serviceProviderMaintenanceCodeDAO.getServiceProviderMaintCodesByDescription(description,providerId);
	}

	@Override
	public ServiceProviderMaintenanceCode getExactServiceProviderMaintenanceCode(Long smlId) {
		init();
		
		ServiceProviderMaintenanceCode result = null;
		Query<ServiceProviderMaintenanceCode> query;
		query = equal(LookupQueryAttributes.SERVICE_PROVIDER_MAINT_LINK_ID, smlId);
		
		ResultSet<ServiceProviderMaintenanceCode> codeResults = serviceProviderMaintenanceCodes.retrieve(query);
		
		for(ServiceProviderMaintenanceCode code : codeResults){
			result = code;
			break;
		}

		return result;
	}
	


	
}
