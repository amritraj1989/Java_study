package com.mikealbert.service;

import com.googlecode.cqengine.attribute.Attribute;
import com.googlecode.cqengine.attribute.SimpleAttribute;
import com.mikealbert.data.entity.MaintenanceCode;
import com.mikealbert.data.entity.ServiceProviderMaintenanceCode;
import com.mikealbert.util.MALUtilities;

public class LookupQueryAttributes {

	
    public static final Attribute<MaintenanceCode, String> MAINTENANCE_CODE = new SimpleAttribute<MaintenanceCode, String>("maintenanceCode") {         
    	public String getValue(MaintenanceCode maintenanceCode) { return maintenanceCode.getCode(); }     
    };
    
    public static final Attribute<MaintenanceCode, String> MAINTENANCE_CODE_DESC = new SimpleAttribute<MaintenanceCode, String>("maintenanceCodeDesc") {         
    	public String getValue(MaintenanceCode maintenanceCode) { return maintenanceCode.getDescription().toUpperCase(); }     
    };
    
    public static final Attribute<ServiceProviderMaintenanceCode, String> SERVICE_PROVIDER_MAFS_MAINT_CODE = new SimpleAttribute<ServiceProviderMaintenanceCode, String>("serviceProviderMafsCode") {         
    	public String getValue(ServiceProviderMaintenanceCode serviceProviderMaintenanceCode) { 
    			String code = "";
    			if((!MALUtilities.isEmpty(serviceProviderMaintenanceCode.getMaintenanceCode())) && (!MALUtilities.isEmpty(serviceProviderMaintenanceCode.getMaintenanceCode().getCode()))){
    				code = serviceProviderMaintenanceCode.getMaintenanceCode().getCode();
    			}
    			return code; 
    		}     
    };
    
    public static final Attribute<ServiceProviderMaintenanceCode, Long> SERVICE_PROVIDER_MAINT_CODE_PROVIDER_ID = new SimpleAttribute<ServiceProviderMaintenanceCode, Long>("serviceProviderMaintCodeProviderId") {         
    	public Long getValue(ServiceProviderMaintenanceCode serviceProviderMaintenanceCode) {return serviceProviderMaintenanceCode.getServiceProvider().getServiceProviderId(); }
    };  
    
    public static final Attribute<ServiceProviderMaintenanceCode, Long> SERVICE_PROVIDER_MAINT_LINK_ID = new SimpleAttribute<ServiceProviderMaintenanceCode, Long>("serviceProviderMaintLinkId") {         
    	public Long getValue(ServiceProviderMaintenanceCode serviceProviderMaintenanceCode) {return serviceProviderMaintenanceCode.getSmlId(); }
    };
}
