package com.mikealbert.service;

import static org.junit.Assert.*;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import org.junit.Test;
import com.googlecode.cqengine.IndexedCollection;
import com.mikealbert.testing.BaseTest;
import com.mikealbert.data.entity.AddressTypeCode;
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
import com.mikealbert.data.entity.ModelType;
import com.mikealbert.data.entity.OdometerReadingCode;
import com.mikealbert.data.entity.OptionAccessoryCategory;
import com.mikealbert.data.entity.PhoneNumberType;
import com.mikealbert.data.entity.RelationshipType;
import com.mikealbert.data.entity.ServiceProvider;
import com.mikealbert.data.entity.ServiceProviderMaintenanceCode;
import com.mikealbert.data.entity.TitleCode;
import com.mikealbert.data.entity.UomCode;
import com.mikealbert.service.LookupCacheService;

public class LookupCacheServiceTest extends BaseTest {
	@Resource LookupCacheService lookupCacheService;
	
	@Test
	public void getlookupFromCache(){
		//Title Codes
		// second lookup should get results from the cache i.e. memory ref to 1st list results
		List<TitleCode> titleCodes1 = lookupCacheService.getTitleCodes();
		List<TitleCode> titleCodes2 = lookupCacheService.getTitleCodes();				
		assertEquals("Title Codes were not cached", titleCodes1, titleCodes2);
		
		//Driver Manual Status Codes 
		// second lookup should get results from the cache i.e. memory ref to 1st list results
		List<DriverManualStatusCode> driverManualStatusCodes1 = lookupCacheService.getDriverManaualStatusCodes();
		List<DriverManualStatusCode> driverManualStatusCodes2 = lookupCacheService.getDriverManaualStatusCodes();		
		assertEquals("Driver Manual Status Codes where not cached", driverManualStatusCodes1, driverManualStatusCodes2);
		
		//Uom Codes 
		// second lookup should get results from the cache i.e. memory ref to 1st list results
		List<UomCode> uomCodes1 = lookupCacheService.getUomCodes();
		List<UomCode> uomCodes2 = lookupCacheService.getUomCodes();		
		assertEquals("UOM Codes where not cached", uomCodes1, uomCodes2);
		
		//Driver Allocation Codes 
		// second lookup should get results from the cache i.e. memory ref to 1st list results
		List<DriverAllocCode> driverAllocCodes1 = lookupCacheService.getDriverAllocCodes();
		List<DriverAllocCode> driverAllocCodes2 = lookupCacheService.getDriverAllocCodes();
		assertEquals("Driver Allocation Codes where not cached", driverAllocCodes1, driverAllocCodes2);					
		
		//Address Type Codes
		// second lookup should get results from the cache i.e. memory ref to 1st list results
		List<AddressTypeCode> addressTypeCodes1 = lookupCacheService.getAddressTypeCodes();
		List<AddressTypeCode> addressTypeCodes2 = lookupCacheService.getAddressTypeCodes();		
		assertEquals("Address Type Codes where not cached", addressTypeCodes1, addressTypeCodes2);				
		
		//Odometer Reading Codes 
		// second lookup should get results from the cache i.e. memory ref to 1st list results
		List<OdometerReadingCode> odometerReadingCodes1 = lookupCacheService.getOdometerReadingCodes();
		List<OdometerReadingCode> odometerReadingCodes2 = lookupCacheService.getOdometerReadingCodes();	
		assertEquals("Odometer Reading Codes where not cached", odometerReadingCodes1, odometerReadingCodes2);					
		
		//Phone number types 
		// second lookup should get results from the cache i.e. memory ref to 1st list results
		List<PhoneNumberType> phoneNumberTypes1 = lookupCacheService.getPhoneNumberTypes();
		List<PhoneNumberType> phoneNumberTypes2 = lookupCacheService.getPhoneNumberTypes();		
		assertEquals("Phone Number Types where not cached", phoneNumberTypes1, phoneNumberTypes2);		
				
		//Relationship Types 
		// second lookup should get results from the cache i.e. memory ref to 1st list results
		List<RelationshipType> relationshipType1 = lookupCacheService.getRelationshipTypes();
		List<RelationshipType> relationshipType2 = lookupCacheService.getRelationshipTypes();
		assertEquals("Relationship Types where not cached", relationshipType1, relationshipType2);
		
		//Maintenance Request Types 
		// second lookup should get results from the cache i.e. memory ref to 1st list results
		List<MaintenanceRequestType> maintenaceRequestTypes1 = lookupCacheService.getMaintenanceRequestTypes();
		List<MaintenanceRequestType> maintenaceRequestTypes2 = lookupCacheService.getMaintenanceRequestTypes();
		assertEquals("Maintenance Request Types where not cached", maintenaceRequestTypes1, maintenaceRequestTypes2);	
		
		//Maintenance Request Statuses 
		// second lookup should get results from the cache i.e. memory ref to 1st list results
		List<MaintenanceRequestStatus> maintenanceRequestStatuses1 = lookupCacheService.getMaintenanceRequestStatuses();
		List<MaintenanceRequestStatus> maintenanceRequestStatuses2 = lookupCacheService.getMaintenanceRequestStatuses();
		assertEquals("Maintenance Request Statuses where not cached", maintenanceRequestStatuses1, maintenanceRequestStatuses2);
		
		//Maintenance Categories 
		// second lookup should get results from the cache i.e. memory ref to 1st list results
		List<MaintenanceCategory> maintenanceCategories1 = lookupCacheService.getMaintenanceCategories();
		List<MaintenanceCategory> maintenanceCategories2 = lookupCacheService.getMaintenanceCategories();
		assertEquals("Maintenance Categories where not cached", maintenanceCategories1, maintenanceCategories2);			
		
		//Maintenance Codes (Indexed)
		// second lookup should get results from the cache i.e. memory ref to 1st list results
		IndexedCollection<MaintenanceCode> maintenanceCodesIdx1 = lookupCacheService.getMaintenanceCodesIndexed();
		IndexedCollection<MaintenanceCode> maintenanceCodesIdx2 = lookupCacheService.getMaintenanceCodesIndexed();
		assertEquals("Maintenance Codes where not cached", maintenanceCodesIdx1, maintenanceCodesIdx2);	
		
		//Maintenance Codes
		// second lookup should get results from the cache i.e. memory ref to 1st list results
		List<MaintenanceCode> maintenanceCodes1 = lookupCacheService.getMaintenanceCodes();
		List<MaintenanceCode> maintenanceCodes2 = lookupCacheService.getMaintenanceCodes();
		assertEquals("Maintenance Codes where not cached", maintenanceCodes1, maintenanceCodes2);	
		
		//Maintenance Recharge Codes 
		// second lookup should get results from the cache i.e. memory ref to 1st list results
		List<MaintenanceRechargeCode> maintenanceRechargeCodes1 = lookupCacheService.getMaintenanceRechargeCodes();
		List<MaintenanceRechargeCode> maintenanceRechargeCodes2 = lookupCacheService.getMaintenanceRechargeCodes();
		assertEquals("Maintenance Recharge Codes where not cached", maintenanceRechargeCodes1, maintenanceRechargeCodes2);	
		
		//Maintenance Repair Reasons 
		// second lookup should get results from the cache i.e. memory ref to 1st list results
		List<MaintenanceRepairReasons> maintenanceRepairReasons1 = lookupCacheService.getMaintenanceRepairReasons();
		List<MaintenanceRepairReasons> maintenanceRepairReasons2 = lookupCacheService.getMaintenanceRepairReasons();
		assertEquals("Maintenance Repair Reasons where not cached", maintenanceRepairReasons1, maintenanceRepairReasons2);			
		
		//Service Provider Maintenance Codes 
		// second lookup should get results from the cache i.e. memory ref to 1st list results
		List<ServiceProviderMaintenanceCode> serviceProviderMaintenanceCodes1 = lookupCacheService.getServiceProviderMaintenanceCodes();
		List<ServiceProviderMaintenanceCode> serviceProviderMaintenanceCodes2 = lookupCacheService.getServiceProviderMaintenanceCodes();
		assertEquals("Service Provider Maintenance Codes where not cached", serviceProviderMaintenanceCodes1, serviceProviderMaintenanceCodes2);	
		
		//Relationship Types
		// second lookup should get results from the cache i.e. memory ref to 1st list results	
		Map<String, List<String>> resourceRoleMap1 = lookupCacheService.getResourceRoleMap();
		Map<String, List<String>> resourceRoleMap2 = lookupCacheService.getResourceRoleMap();
		assertEquals("AppResource Role Map was not cached", resourceRoleMap1, resourceRoleMap2);
		
		//Cost Avoidance Reason
		// second lookup should get results from the cache i.e. memory ref to 1st list results	
		List<CostAvoidanceReason> costAvoidanceReasons1 = lookupCacheService.getCostAvoidanceReasons();
		List<CostAvoidanceReason> costAvoidanceReasons2 = lookupCacheService.getCostAvoidanceReasons();
		assertEquals("Cost Avoidance Reasons where not cached", costAvoidanceReasons1, costAvoidanceReasons2);	
		
		//Document Transaction Type
		// second lookup should get results from the cache i.e. memory ref to 1st list results	
		List<DocumentTransactionType> documentTransactionType1 = lookupCacheService.getDocumentTransactionTypes();
		List<DocumentTransactionType> documentTransactionType2 = lookupCacheService.getDocumentTransactionTypes();
		assertEquals("Document Transaction Types where not cached", documentTransactionType1, documentTransactionType2);
		
		//Car Model Types
		// second lookup should get results from the cache i.e. memory ref to 1st list results	
		List<ModelType> modelType1 = lookupCacheService.getModelTypes();
		List<ModelType> modelType2 = lookupCacheService.getModelTypes();
		assertEquals("Model Types where not cached", modelType1, modelType2);
				
		//Finance Parameter Categories 
		// second lookup should get results from the cache i.e. memory ref to 1st list results
		List<FinanceParameterCategory> financeParameterCategories1 = lookupCacheService.getFinanceParameterCategories();
		List<FinanceParameterCategory> financeParameterCategories2 = lookupCacheService.getFinanceParameterCategories();
		assertEquals("FinanceParameter Categories where not cached", financeParameterCategories1, financeParameterCategories2);			
		
		//Service Providers
		// second lookup should get results from the cache i.e. memory ref to 1st list results	
		List<ServiceProvider> serviceProvider1 = lookupCacheService.getServiceProviders(19396L);
		List<ServiceProvider> serviceProvider2 = lookupCacheService.getServiceProviders(19396L);
		assertEquals("Service Providers where not cached", serviceProvider1, serviceProvider2);		
		
		//Option Accessory Categories
		// second lookup should get results from the cache i.e. memory ref to 1st list results	
		List<OptionAccessoryCategory> optionAccessoryCategories1 = lookupCacheService.getOptionAccessoryCategories();
		List<OptionAccessoryCategory> optionAccessoryCategories2 = lookupCacheService.getOptionAccessoryCategories();
		assertEquals("Service Providers where not cached", optionAccessoryCategories1, optionAccessoryCategories2);			
			
	}
	
	@Test
	public void testRefreshLookupCache(){
		//Retrieve cached codes
		List<OdometerReadingCode> odometerReadingCodes1 = lookupCacheService.getOdometerReadingCodes();		
		IndexedCollection<MaintenanceCode> maintenanceCodes1 = lookupCacheService.getMaintenanceCodesIndexed();	
		
		//Test Refresh Cache - memory locations should be different for the list
		lookupCacheService.refreshCache();
		List<OdometerReadingCode> odometerReadingCodes2 = lookupCacheService.getOdometerReadingCodes();
		assertFalse("Cache was not refreshed", odometerReadingCodes1 == odometerReadingCodes2);
		IndexedCollection<MaintenanceCode> maintenanceCodes2 = lookupCacheService.getMaintenanceCodesIndexed();
		assertFalse("Cache was not refreshed", maintenanceCodes1 == maintenanceCodes2);
	}

}
