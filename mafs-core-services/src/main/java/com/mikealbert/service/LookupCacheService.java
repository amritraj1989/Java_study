package com.mikealbert.service;

import java.util.List;
import java.util.Map;

import com.googlecode.cqengine.IndexedCollection;
import com.mikealbert.data.entity.AddressTypeCode;
import com.mikealbert.data.entity.ClientSystem;
import com.mikealbert.data.entity.CostAvoidanceReason;
import com.mikealbert.data.entity.DocumentTransactionType;
import com.mikealbert.data.entity.DriverAllocCode;
import com.mikealbert.data.entity.DriverManualStatusCode;
import com.mikealbert.data.entity.FinanceParameterCategory;
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

/**
 * Public Interface implemented by {@link com.mikealbert.vision.service.LookupCacheServiceImpl} for interacting with business service methods concerning {@link com.mikealbert.data.entity.TitleCode}(s), {@link com.mikealbert.data.entity.DriverManualStatusCode}(s), {@link com.mikealbert.data.entity.AddressTypeCode}(s), {@link com.mikealbert.data.entity.UomCode}(s), {@link com.mikealbert.data.entity.DriverAllocCode}(s), {@link com.mikealbert.data.entity.OdometerReadingCode}(s), {@link com.mikealbert.data.entity.PhoneNumberType}(s), {@link com.mikealbert.data.entity.RelationshipType}(s).
 *
 * @see com.mikealbert.data.entity.TitleCode
 * @see com.mikealbert.data.entity.DriverManualStatusCode
 * @see com.mikealbert.data.entity.AddressTypeCode
 * @see com.mikealbert.data.entity.UomCode
 * @see com.mikealbert.data.entity.DriverAllocCode
 * @see com.mikealbert.data.entity.OdometerReadingCode
 * @see com.mikealbert.data.entity.PhoneNumberType
 * @see com.mikealbert.data.entity.RelationshipType
 * @see com.mikealbert.vision.service.LookupCacheServiceImpl
 * @see com.mikealbert.entity.PermissionSet
 * @see com.mikealbert.entity.WorkClass
 **/
public interface LookupCacheService {
    public List<TitleCode> getTitleCodes();
    public List<DriverManualStatusCode> getDriverManaualStatusCodes();
    public List<AddressTypeCode> getAddressTypeCodes();
    public List<UomCode> getUomCodes();
    public List<DriverAllocCode> getDriverAllocCodes();  
    public List<OdometerReadingCode> getOdometerReadingCodes();
    public List<PhoneNumberType> getPhoneNumberTypes(); 
    public List<RelationshipType> getRelationshipTypes();
    public List<MaintenanceRequestType> getMaintenanceRequestTypes();    
    public List<MaintenanceRequestStatus> getMaintenanceRequestStatuses(); 
    public List<MaintenanceCategory> getMaintenanceCategories();   
    public List<ClientSystem> getClientSystemsWithPOCsExceptSystemAll();
    public List<FinanceParameterCategory> getFinanceParameterCategories();
    // TODO: this is only used by a few unit tests in Vision still; once these are adjusted to use MaintenanceCodeService
    // in core then this can be removed
    @Deprecated
    public List<MaintenanceCode> getMaintenanceCodes();
    public IndexedCollection<MaintenanceCode> getMaintenanceCodesIndexed();
    public List<MaintenanceRechargeCode> getMaintenanceRechargeCodes();  
    public List<MaintenanceRepairReasons> getMaintenanceRepairReasons();     
    public List<ServiceProviderMaintenanceCode> getServiceProviderMaintenanceCodes();
    public IndexedCollection<ServiceProviderMaintenanceCode> getServiceProviderMaintenanceCodesIndexed();
    public List<CostAvoidanceReason> getCostAvoidanceReasons();
    public List<DocumentTransactionType> getDocumentTransactionTypes();
    public List<ModelType> getModelTypes();
    public Map<String, List<String>> getResourceRoleMap();
    public List<ServiceProvider> getServiceProviders(long parentProviderId);
    public List<OptionAccessoryCategory> getOptionAccessoryCategories();
    public List<WorkshopCapabilityCode> getVLInternalRepairTypeWorkshopCapabilityCodes();
    public List<WorkshopCapabilityCode> getVLInternalVehicleTypeWorkshopCapabilityCodes();
    public List<WorkshopCapabilityCode> getVLExternalVehicleTypeWorkshopCapabilityCodes();
    public List<WorkshopCapabilityCode> getVLExternalRepairTypeWorkshopCapabilityCodes();
    public List<ServiceProviderMaintenanceGroup> getVLInternalServiceProviderMaintenanceGroups();
    public List<ServiceProviderMaintenanceGroup> getVLExternalServiceProviderMaintenanceGroups();
    public List<SupplierCategoryCode> getVLMaintenanceSupplierCategoryCodes();
    public List<Make> getCarModelTypeMakes();
    public List<StockUnitsLovVO> getStockUnits();
	public void refreshCache(); 
	public void refreshMaintCodeCache();
}
