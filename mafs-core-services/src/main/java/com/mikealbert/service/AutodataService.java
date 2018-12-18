package com.mikealbert.service;

import com.mikealbert.data.entity.SerializedConfig;
import com.mikealbert.ws.vehcfg.client.VehicleConfigurationType;

/**
* Public Interface implemented by {@link com.mikealbert.vision.service.AutodataServiceImpl} 
* for interacting with business service methods concerning: 
* 
*  @see com.mikealbert.vision.service.AutodataServiceImpl
* */
public interface AutodataService {
    public SerializedConfig saveOrUpdateSerializedConfig(SerializedConfig serializedConfig);
    public VehicleConfigurationType getVehicleConfiguration(Long cfgId) throws Exception; 
    public void deleteVehicleConfiguration(Long cfgId) throws Exception;    
}
