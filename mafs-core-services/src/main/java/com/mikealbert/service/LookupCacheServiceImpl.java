package com.mikealbert.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.googlecode.cqengine.CQEngine;
import com.googlecode.cqengine.IndexedCollection;
import com.googlecode.cqengine.attribute.Attribute;
import com.googlecode.cqengine.attribute.SimpleAttribute;
import com.googlecode.cqengine.index.hash.HashIndex;
import com.googlecode.cqengine.index.radix.RadixTreeIndex;
import com.googlecode.cqengine.index.suffix.SuffixTreeIndex;
import com.mikealbert.common.MalLogger;
import com.mikealbert.common.MalLoggerFactory;
import com.mikealbert.data.dao.AddressTypeCodeDAO;
import com.mikealbert.data.dao.ClientSystemDAO;
import com.mikealbert.data.dao.CostAvoidanceReasonDAO;
import com.mikealbert.data.dao.DocumentTransactionTypeDAO;
import com.mikealbert.data.dao.DriverAllocCodeDAO;
import com.mikealbert.data.dao.DriverManualStatusCodeDAO;
import com.mikealbert.data.dao.FinanceParameterCategoryDAO;
import com.mikealbert.data.dao.FleetMasterDAO;
import com.mikealbert.data.dao.MaintenanceCategoryDAO;
import com.mikealbert.data.dao.MaintenanceCodeDAO;
import com.mikealbert.data.dao.MaintenanceRechargeCodeDAO;
import com.mikealbert.data.dao.MaintenanceRepairReasonsDAO;
import com.mikealbert.data.dao.MaintenanceRequestStatusDAO;
import com.mikealbert.data.dao.MaintenanceRequestTypeDAO;
import com.mikealbert.data.dao.MakeDAO;
import com.mikealbert.data.dao.ModelTypeDAO;
import com.mikealbert.data.dao.OdometerReadingCodeDAO;
import com.mikealbert.data.dao.OdometerReadingDAO;
import com.mikealbert.data.dao.OptionAccessoryCategoryDAO;
import com.mikealbert.data.dao.PhoneNumberTypeDAO;
import com.mikealbert.data.dao.RelationshipTypeDAO;
import com.mikealbert.data.dao.ResourceDAO;
import com.mikealbert.data.dao.ServiceProviderDAO;
import com.mikealbert.data.dao.ServiceProviderMaintenanceCodeDAO;
import com.mikealbert.data.dao.SupplierCategoryCodeDAO;
import com.mikealbert.data.dao.TitleCodeDAO;
import com.mikealbert.data.dao.UomCodeDAO;
import com.mikealbert.data.dao.WorkClassDAO;
import com.mikealbert.data.dao.WorkshopCapabilityCodeDAO;
import com.mikealbert.data.entity.AddressTypeCode;
import com.mikealbert.data.entity.ClientSystem;
import com.mikealbert.data.entity.CostAvoidanceReason;
import com.mikealbert.data.entity.DocumentTransactionType;
import com.mikealbert.data.entity.DriverAllocCode;
import com.mikealbert.data.entity.DriverManualStatusCode;
import com.mikealbert.data.entity.FinanceParameterCategory;
import com.mikealbert.data.entity.LatestOdometerReadingV;
import com.mikealbert.data.entity.MaintenanceCategory;
import com.mikealbert.data.entity.MaintenanceCode;
import com.mikealbert.data.entity.MaintenanceRechargeCode;
import com.mikealbert.data.entity.MaintenanceRepairReasons;
import com.mikealbert.data.entity.MaintenanceRequestStatus;
import com.mikealbert.data.entity.MaintenanceRequestType;
import com.mikealbert.data.entity.Make;
import com.mikealbert.data.entity.ModelType;
import com.mikealbert.data.entity.OdometerReadingCode;
import com.mikealbert.data.entity.OptionAccessoryCategory;
import com.mikealbert.data.entity.PhoneNumberType;
import com.mikealbert.data.entity.RelationshipType;
import com.mikealbert.data.entity.ServiceProvider;
import com.mikealbert.data.entity.ServiceProviderMaintenanceCode;
import com.mikealbert.data.entity.SupplierCategoryCode;
import com.mikealbert.data.entity.TitleCode;
import com.mikealbert.data.entity.UomCode;
import com.mikealbert.data.entity.WorkshopCapabilityCode;
import com.mikealbert.data.vo.ServiceProviderMaintenanceGroup;
import com.mikealbert.data.vo.StockUnitsLovVO;
import com.mikealbert.exception.MalException;
import com.mikealbert.util.MALUtilities;

/**
 * Implementation of {@link com.mikealbert.vision.service.LookupCacheService}
 */
@Service("lookupCacheService")
public class LookupCacheServiceImpl implements LookupCacheService {
	@Resource TitleCodeDAO titleCodeDAO;
	@Resource DriverManualStatusCodeDAO driverManualStatusCodeDAO;
	@Resource AddressTypeCodeDAO addressTypeCodeDAO;
	@Resource UomCodeDAO uomCodeDAO;	
	@Resource DriverAllocCodeDAO driverAllocCodeDAO;	
	@Resource OdometerReadingCodeDAO odometerReadingCodeDAO;
	@Resource PhoneNumberTypeDAO phoneNumberTypeDAO;
	@Resource RelationshipTypeDAO relationshipTypeDAO;		
	@Resource ResourceDAO resourceDAO;
	@Resource WorkClassDAO workClassDAO;
	@Resource MaintenanceRequestTypeDAO maintenanceRequestTypeDAO;
	@Resource MaintenanceRequestStatusDAO maintenanceRequestStatusDAO;	
	@Resource MaintenanceCodeDAO maintenanceCodeDAO;
	@Resource MaintenanceCategoryDAO maintenanceCategoryDAO;	
	@Resource MaintenanceRechargeCodeDAO maintenanceRechargeCodeDAO;
	@Resource MaintenanceRepairReasonsDAO maintenanceRepairReasonsDAO;
	@Resource ServiceProviderMaintenanceCodeDAO serviceProviderMaintenanceCodeDAO;	
	@Resource CostAvoidanceReasonDAO costAvoidanceReasonDAO;
	@Resource DocumentTransactionTypeDAO documentTransactionTypeDAO;
	@Resource MakeDAO makeDAO;
	@Resource ModelTypeDAO modelTypeDAO;
	@Resource FinanceParameterCategoryDAO financeParameterCategoryDAO;
	@Resource ServiceProviderService serviceProviderService;
	@Resource ServiceProviderDAO serviceProviderDAO;
	@Resource OptionAccessoryCategoryDAO optionAccessoryCategoryDAO;
	@Resource ClientSystemDAO clientSystemDAO;
	@Resource WorkshopCapabilityCodeDAO workshopCapabilityCodeDAO;
	@Resource WillowConfigService willowConfig;
	@Resource SupplierCategoryCodeDAO supplierCategoryCodeDAO;
	@Resource FleetMasterDAO fleetMasterDAO;
	@Resource OdometerReadingDAO odometerReadingDAO;
	@Resource FleetMasterService fleetMasterService;
	
	public MalLogger logger = MalLoggerFactory.getLogger(this.getClass());

	//TODO: and Ideal refactor would be to keep the Attribute(s) in a separate file; for now a little duplication is good enough (see MaintenanceCodeServiceImpl)
    public static final Attribute<MaintenanceCode, String> MAINTENANCE_CODE = new SimpleAttribute<MaintenanceCode, String>("maintenanceCode") {         
    	public String getValue(MaintenanceCode maintenanceCode) { return maintenanceCode.getCode(); }     
    };
	
	// cached by virtue of Spring Singleton Factory (poor man's cache)
	private IndexedCollection<MaintenanceCode> maintenanceCodes;
	private IndexedCollection<ServiceProviderMaintenanceCode> serviceProviderMaintenanceCodes;
	
	
	/**
	 * Returns a list of all title codes.  These codes are cached.
	 * @return List of Title Codes
	 */
	
	@Cacheable("titleCodes")
	public List<TitleCode> getTitleCodes() {
		return titleCodeDAO.findAll(new Sort(Sort.Direction.ASC, "titleCode"));
	}
	
	/**
	 * Returns a list of all driver manual status codes.  These codes are cached.
	 * @return List of driver manual status codes
	 */
	
	@Cacheable("driverManualStatusCodes")
    public List<DriverManualStatusCode> getDriverManaualStatusCodes(){
		return driverManualStatusCodeDAO.findAll();
	}
	
//	/** TODO: commented out because some of the code depended upon the whole object for persistence
//	 * Returns a list of all address type codes.  These codes are cached.
//	 * The cache is hit to avoid a DB call and the results are put into a unmodifiableList
//	 * to avoid collection mis-use; finally because of possible use of this as a (VO) bound to a UI 
//	 * the individual object in the cache are created as "shallow clones"; as is appropriate for lookup data
//	 * this protect the cache from corruption; list mis-use and still allows it to be used in UI binding to capture use selected
//	 * values with no ill-effects (just some increased memory usage)
//	 * @return List of address type codes
//	 */
//	@Override
//    public List<AddressTypeCode> getAddressTypeCodes(){
//		List<AddressTypeCode> clonedList = new ArrayList<AddressTypeCode>(getCachedAddressTypeCodes().size());
//		for(AddressTypeCode forCloning : getCachedAddressTypeCodes()){
//			clonedList.add(new AddressTypeCode(forCloning));
//		}
//		return Collections.unmodifiableList(clonedList);
//	}	
//	// private helper to manage cache
//	@Cacheable("cachedAddressTypeCodes")
//	private List<AddressTypeCode> getCachedAddressTypeCodes(){
//		return addressTypeCodeDAO.findAll(new Sort(Sort.Direction.ASC, "addressType"));
//	}
	
	
	/**
	 * Returns a list of all address type codes.  These codes are cached.
	 * @return List of address type codes
	 */
	
	@Cacheable("addressTypeCodes")
    public List<AddressTypeCode> getAddressTypeCodes(){
		return Collections.unmodifiableList(addressTypeCodeDAO.findAll(new Sort(Sort.Direction.ASC, "addressType")));
	}	

	/**
	 * Returns a list of all unit of measure codes (UOM).  These codes are cached.
	 * @return List of UOM codes
	 */
	
	@Cacheable("uomCodes")
	public List<UomCode> getUomCodes(){
		return uomCodeDAO.findAll(new Sort(Sort.Direction.ASC, "uomCode"));
	}
	
	/**
	 * Returns a list of all driver allocation codes.  These codes are cached.
	 * @return List of allocation codes
	 */
	
	@Cacheable("driverAllocCodes")
	public List<DriverAllocCode> getDriverAllocCodes(){
		return driverAllocCodeDAO.findAll(new Sort(Sort.Direction.ASC, "allocType"));
	}
	
	/**
	 * Returns a list of all odometer reading codes.  These codes are cached.
	 * @return List of odometer reading codes
	 */
	
	@Cacheable("odometerReadingCodes")	
    public List<OdometerReadingCode> getOdometerReadingCodes(){
		return odometerReadingCodeDAO.findAll(new Sort(Sort.Direction.ASC, "code"));
	}
	
	/**
	 * Returns a list of all phone number codes.  These codes are cached.
	 * @return List of phone number codes
	 */
	
	@Cacheable("phoneNumberTypes")		
    public List<PhoneNumberType> getPhoneNumberTypes(){
		return phoneNumberTypeDAO.findAll(new Sort(Sort.Direction.ASC, "code"));
	}
	
	/**
	 * Returns a list of all relationship types.  These types are cached.
	 * @return List of relationship types
	 */
	
	@Cacheable("relationshipTypes")		
    public List<RelationshipType> getRelationshipTypes(){
		return relationshipTypeDAO.findAll(new Sort(Sort.Direction.ASC, "code"));
	}
	
	/**
	 * Returns a list of all maintenance request types.  These types are cached.
	 * @return List of maintenance request types
	 */	
	@Cacheable("maintenaceRequestTypes")	
    public List<MaintenanceRequestType> getMaintenanceRequestTypes(){
    	return maintenanceRequestTypeDAO.findAll(new Sort(Sort.Direction.ASC, "code"));
    }
	
	/**
	 * Returns a list of all maintenance statuses.  These statuses are cached.
	 * @return List of maintenance request statuses
	 */		
	@Cacheable("maintenanceRequestStatuses")		
    public List<MaintenanceRequestStatus> getMaintenanceRequestStatuses(){
    	return maintenanceRequestStatusDAO.findAll(new Sort(Sort.Direction.ASC, "sortOrder"));
    }
	
    
	/**
	 * Returns a list of all maintenance categories.  These categories are cached.
	 * @return List of maintenance categories
	 */		
	@Cacheable("maintenanceCategories")
    public List<MaintenanceCategory> getMaintenanceCategories(){
    	return maintenanceCategoryDAO.findAll(new Sort(Sort.Direction.ASC, "code"));
    }    
	
	/**
	 * Returns a list of all maintenance codes.  These codes are cached.
	 * @return List of maintenance service codes
	 */		
	@Cacheable("maintenanceCodes")
    public List<MaintenanceCode> getMaintenanceCodes(){
    	return maintenanceCodeDAO.findAll(new Sort(Sort.Direction.ASC, "code"));
    }
	
	/**
	 * Returns a list of all maintenance codes in a IndexedCollection.  
	 * These codes are cached. via the Singleton Factory
	 * @return List of maintenance service codes
	 */		
	 public IndexedCollection<MaintenanceCode> getMaintenanceCodesIndexed(){
		if(MALUtilities.isEmpty(maintenanceCodes) || maintenanceCodes.size() == 0) {
			reIndexMaintenanceCodes();
		}

		return maintenanceCodes;
    }
	 
	private void reIndexMaintenanceCodes(){
    	maintenanceCodes = CQEngine.copyFrom(this.getMaintenanceCodes());
		maintenanceCodes.addIndex(HashIndex.onAttribute(LookupQueryAttributes.MAINTENANCE_CODE));
		maintenanceCodes.addIndex(SuffixTreeIndex.onAttribute(LookupQueryAttributes.MAINTENANCE_CODE_DESC));
	}
	
	
	/**
	 * Returns a list of all service provider maintenance codes.  These codes are cached.
	 * @return List of maintenance service provider maintenance codes.
	 */		
	@Cacheable("serviceProviderMaintenanceCodes")		
    public List<ServiceProviderMaintenanceCode> getServiceProviderMaintenanceCodes(){
    	return serviceProviderMaintenanceCodeDAO.findAll(new Sort(Sort.Direction.ASC, "code"));      	
    }
	
	/**
	 * Returns a list of all maintenance codes in a IndexedCollection.  
	 * These codes are cached. via the Singleton Factory
	 * @return List of maintenance service codes
	 */		
	 public IndexedCollection<ServiceProviderMaintenanceCode> getServiceProviderMaintenanceCodesIndexed(){
		if(MALUtilities.isEmpty(serviceProviderMaintenanceCodes) || serviceProviderMaintenanceCodes.size() == 0) {
			reIndexServiceProviderMaintenanceCodes();
		}

		return serviceProviderMaintenanceCodes;
    }
	
	private void reIndexServiceProviderMaintenanceCodes(){
		serviceProviderMaintenanceCodes = CQEngine.copyFrom(this.getServiceProviderMaintenanceCodes());
		serviceProviderMaintenanceCodes.addIndex(HashIndex.onAttribute(LookupQueryAttributes.SERVICE_PROVIDER_MAFS_MAINT_CODE));
		serviceProviderMaintenanceCodes.addIndex(HashIndex.onAttribute(LookupQueryAttributes.SERVICE_PROVIDER_MAINT_CODE_PROVIDER_ID));
		serviceProviderMaintenanceCodes.addIndex(HashIndex.onAttribute(LookupQueryAttributes.SERVICE_PROVIDER_MAINT_LINK_ID));
	}
	
	
	/**
	 * Returns a list of all maintenance recharge codes.  These codes are cached.
	 * @return List of maintenance recharge codes
	 */		
	@Cacheable("maintenanceRechargeCodes")	
    public List<MaintenanceRechargeCode> getMaintenanceRechargeCodes(){
    	return maintenanceRechargeCodeDAO.findAll(new Sort(Sort.Direction.ASC, "code"));    	
    }
	
	/**
	 * Returns a list of all maintenance repair reasons.  These codes are cached.
	 * @return List of maintenance repair reasons
	 */		
	@Cacheable("maintenanceRepairReasons")		
	public List<MaintenanceRepairReasons> getMaintenanceRepairReasons(){
		return maintenanceRepairReasonsDAO.findAll(new Sort(Sort.Direction.ASC, "code"));
	}	

	/**
	 * Returns a list of all finance parameter categories.  These categories are cached.
	 * @return List of finance parameter categories
	 */		
	@Cacheable("financeParameterCategories")
    public List<FinanceParameterCategory> getFinanceParameterCategories(){
    	return financeParameterCategoryDAO.findAll(new Sort(Sort.Direction.ASC, "description"));
    } 	

	/**
	 * Returns a list of all service provider maintenance codes.  These codes are cached.
	 * @return List of maintenance service provider maintenance codes.
	 */		
	@Cacheable("serviceProviderMaintenanceCodes")		
    public List<ServiceProviderMaintenanceCode> getServiceProviderMaintenanceCode(){
    	return serviceProviderMaintenanceCodeDAO.findAll(new Sort(Sort.Direction.ASC, "code"));      	
    }
    
	/**
	 * Returns a list of all client systems that have POC's except client SYSTEM_NAME 'ALL'.  These systems are cached.
	 * This is strictly for the Client System (POC Category) drop down list on Client Point of Communication screen.
	 * @return List of client systems
	 */		

	@Cacheable("clientSystemsWithPOCsExceptSystemAll")
    public List<ClientSystem> getClientSystemsWithPOCsExceptSystemAll(){
			
		return clientSystemDAO.findAllWithPOCsExceptSystemAll(new Sort(Sort.Direction.ASC, "description"));
    }    
	
	/**
	 * Returns a list of cost avoidance reason codes.  These codes are cached.
	 * @return List of cost avoidance reason codes.
	 */		
	@Cacheable("costAvoidanceReasons")		
	public List<CostAvoidanceReason> getCostAvoidanceReasons(){
		return costAvoidanceReasonDAO.findAll(new Sort(Sort.Direction.ASC, "code"));  		
	}

	/**
	 * Returns a list of document transaction types.  These types are cached.
	 * @return List of cost avoidance reason codes.
	 */		
	@Cacheable("documentTransactionTypes")		
    public List<DocumentTransactionType> getDocumentTransactionTypes(){
		return documentTransactionTypeDAO.findAll();    	
    }
    
	/**
	 * Returns a list of model types.  The years are cached.
	 * @return List of model types.
	 */		
	@Cacheable("modelTypes")			
    public List<ModelType> getModelTypes(){
    	return modelTypeDAO.findAll(new Sort(Sort.Direction.ASC, "description"));
    }
        	
	/**
	 * Returns a map of Roles this assigned to the specified resource
	 * @return Map of Roles that are assigned to a resource
	 */
	
	@Cacheable("resourceRoleMap")		
    public Map<String, List<String>> getResourceRoleMap(){
    	return resourceDAO.getResourceRoleMap();   	
    }
	
	@Cacheable(value="serviceProviders")
	public List<ServiceProvider> getServiceProviders(long parentProviderId) {
		return serviceProviderService.getServiceProvidersByParentId(parentProviderId);
	}
	
	@Cacheable(value="optionAccessoryCategory")
	public List<OptionAccessoryCategory> getOptionAccessoryCategories(){
		return optionAccessoryCategoryDAO.findAll(new Sort(Sort.Direction.ASC, "description"));
	}

	@Cacheable(value="vlInternalRepairTypeWorkshopCapabilityCodes")
	public List<WorkshopCapabilityCode> getVLInternalRepairTypeWorkshopCapabilityCodes() {
		return workshopCapabilityCodeDAO.findWorkshopCapabilityCodesByGroupName("VL_INTERNAL_REPAIR_TYPE");
	}

	@Cacheable(value="vlInternalVehicleTypeWorkshopCapabilityCodes")
	public List<WorkshopCapabilityCode> getVLInternalVehicleTypeWorkshopCapabilityCodes() {
		return workshopCapabilityCodeDAO.findWorkshopCapabilityCodesByGroupName("VL_INTERNAL_VEHICLE_TYPE");
	}
	
	@Cacheable(value="vlExternalVehicleTypeWorkshopCapabilityCodes")
	public List<WorkshopCapabilityCode> getVLExternalVehicleTypeWorkshopCapabilityCodes() {
		return workshopCapabilityCodeDAO.findWorkshopCapabilityCodesByGroupName("VL_EXTERNAL_VEHICLE_TYPE");
	}

	@Cacheable(value="vlExternalRepairTypeWorkshopCapabilityCodes")
	public List<WorkshopCapabilityCode> getVLExternalRepairTypeWorkshopCapabilityCodes() {
		List<WorkshopCapabilityCode> workshopCapabilityCodes = 
				workshopCapabilityCodeDAO.findWorkshopCapabilityCodesByGroupName("VL_EXTERNAL_REPAIR_TYPE");
		
		WorkshopCapabilityCode capabilityCode = new WorkshopCapabilityCode();
		capabilityCode.setDescription("Preventive Maintenance");
		for(WorkshopCapabilityCode code : workshopCapabilityCodeDAO.findWorkshopCapabilityCodesByGroupName("VL_EXTERNAL_REPAIR_TYPE_PREVENTIVE")) {
			String workshopCapability = MALUtilities.isEmpty(capabilityCode.getWorkshopCapability()) ? "" : capabilityCode.getWorkshopCapability();
			capabilityCode.setWorkshopCapability(workshopCapability + code.getWorkshopCapability() + ",");
		}
		if(workshopCapabilityCodes.size() > 0) { 
			workshopCapabilityCodes.add(0, capabilityCode);
		} else {
			workshopCapabilityCodes.add(capabilityCode);
		}
		return workshopCapabilityCodes; 
	}	
	
	@Cacheable(value="vlInternalServiceProviderMaintnenaceGroups")
	public List<ServiceProviderMaintenanceGroup> getVLInternalServiceProviderMaintenanceGroups() {
		return serviceProviderDAO.getVLServiceProviderMaintenanceGroups("");
	}

	@Cacheable(value="vlExternalServiceProviderMaintnenaceGroups")
	public List<ServiceProviderMaintenanceGroup> getVLExternalServiceProviderMaintenanceGroups() {
		return serviceProviderDAO.getVLServiceProviderMaintenanceGroups("Y");
	}
	
	@Cacheable(value="supplierVLMaintenanceSupplierCategoryCodes")
	public List<SupplierCategoryCode> getVLMaintenanceSupplierCategoryCodes() {
		try{
			String value = willowConfig.getConfigValue("VL_SC_COM_OPTIONS");
			SupplierCategoryCode hardCodedValue = new SupplierCategoryCode();
			hardCodedValue.setSupplierCategory(value.split(";")[0]);
			hardCodedValue.setDescription(value.split(";")[1]);

			List<SupplierCategoryCode> shopCategoryCodes = new ArrayList<SupplierCategoryCode>();
			shopCategoryCodes.add(hardCodedValue);

			@SuppressWarnings("unchecked")
			List<String> shopCategories = Arrays.asList(willowConfig.getConfigValue("VL_SC_IND_OPTIONS").split(","));
			for(SupplierCategoryCode code : supplierCategoryCodeDAO.getVLMaintenanceSupplierCategories(shopCategories)) {
				shopCategoryCodes.add(code);
			}
			return shopCategoryCodes;
		} catch(Exception ex) {
			throw new MalException("generic.error.occured.while", new String[] { "finding All vl maintenance supplier category codes" }, ex);			
		}
	}	

	@Cacheable(value="carModelTypeMakes")
	public List<Make> getCarModelTypeMakes() {
		return makeDAO.findByModelType(modelTypeDAO.findById(new Long(2)).orElse(null));
	}	

	@Cacheable(value="stockUnits")
    public List<StockUnitsLovVO> getStockUnits() {
		List<StockUnitsLovVO> stockUnitsLovVOs = fleetMasterDAO.findStockUnits(null, null, new PageRequest(0, 500), null);
		for(StockUnitsLovVO vo : stockUnitsLovVOs) {
			LatestOdometerReadingV odoReadingV = odometerReadingDAO.findLatestOdometerReading(vo.getFmsId());
			vo.setStandardAccessories(fleetMasterDAO.getStandardAccessoriesByFmsId(vo.getFmsId()));
			vo.setOptionalAccessories(fleetMasterDAO.getOptionalAccessoriesByFmsId(vo.getFmsId()));
			vo.setDealerAccessories(fleetMasterDAO.getDealerAccessoriesByFmsId(vo.getFmsId()));
			
			if(!MALUtilities.isEmpty(odoReadingV)) {
				vo.setLastOdoReading(odoReadingV.getOdoReading().intValue());				
			}

		}
		return 	stockUnitsLovVOs;	
	}

	/**
	 * Clears all the entries within the specified caches
	 * and makes calls to member funct to re-populate the
	 * entries into cache..
	 */
	@CacheEvict(value = {"titleCodes", 
			"driverManualStatusCodes",
			"addressTypeCodes",
			"uomCodes", 
			"driverAllocCodes",
			"odometerReadingCodes",
			"phoneNumberTypes",
			"relationshipTypes",
			"resourceRoleMap",
			"maintenaceRequestTypes",
			"maintenanceRequestStatuses", 
			"maintenanceCategories",
			"maintenanceCodes",
			"maintenanceRechargeCodes",
			"maintenanceRepairReasons",
			"financeParameterCategories",
			"serviceProviderMaintenanceCodes",
			"costAvoidanceReasons",
			"modelTypes",			
			"maintCodeFinParamMapping",
			"serviceProviders",
			"optionAccessoryCategory",
			"clientSystemsWithPOCsExceptSystemAll",
			"vlInternalRepairTypeWorkshopCapabilityCodes", 
			"vlInternalVehicleTypeWorkshopCapabilityCodes",
			"vlExternalVehicleTypeWorkshopCapabilityCodes",
			"vlExternalRepairTypeWorkshopCapabilityCodes",
			"vlInternalServiceProviderMaintnenaceGroups",
			"vlExternalServiceProviderMaintnenaceGroups",
			"carModelTypeMakes",
			"supplierVLMaintenanceSupplierCategoryCodes", 
			"stockUnits"},
			allEntries = true)
	public void refreshCache(){
		getTitleCodes();
		getDriverManaualStatusCodes();
		getAddressTypeCodes();
		getUomCodes();
		getDriverAllocCodes();
		getOdometerReadingCodes();
		getPhoneNumberTypes();
		getRelationshipTypes();
		getMaintenanceRequestStatuses();
		getMaintenanceRequestTypes();
		getMaintenanceCategories();
		getMaintenanceCodes();
		getMaintenanceRechargeCodes();
		getMaintenanceRepairReasons();
		getFinanceParameterCategories();		
		getServiceProviderMaintenanceCodes();
		getResourceRoleMap();
		getCostAvoidanceReasons();
		getClientSystemsWithPOCsExceptSystemAll();	
		// reIndex / reload the MaintenanceCodesIndexed() variable
		reIndexMaintenanceCodes();
		getModelTypes();
		getOptionAccessoryCategories();
		getVLInternalRepairTypeWorkshopCapabilityCodes();
		getVLInternalVehicleTypeWorkshopCapabilityCodes();
		getVLExternalVehicleTypeWorkshopCapabilityCodes();
		getVLExternalRepairTypeWorkshopCapabilityCodes();
		getVLInternalServiceProviderMaintenanceGroups();
		getVLExternalServiceProviderMaintenanceGroups();
		getVLMaintenanceSupplierCategoryCodes();
		getCarModelTypeMakes();
		fleetMasterService.stockUnitListFromCache();
		logger.info("Refreshed lookup cache");				
	}
	
	/**
	 * Clears the maint codes and makes calls to member funct to re-populate the
	 * entries into cache..
	 */
	@CacheEvict(value = {
			"maintenanceCodes",
			"serviceProviderMaintenanceCodes"}, 
			allEntries = true)
	public void refreshMaintCodeCache(){
		getMaintenanceCodes();
		getServiceProviderMaintenanceCodes();
		// reIndex / reload the MaintenanceCodesIndexed() and  variable
		reIndexMaintenanceCodes();
		reIndexServiceProviderMaintenanceCodes();
		
		logger.info("Refreshed maint code and provider maint code lookup cache");				
	}
}
