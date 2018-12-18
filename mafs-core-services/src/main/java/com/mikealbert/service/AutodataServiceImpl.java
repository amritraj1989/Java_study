package com.mikealbert.service;

import java.net.URL;

import javax.annotation.Resource;
import javax.xml.namespace.QName;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.mikealbert.common.MalLogger;
import com.mikealbert.common.MalLoggerFactory;
import com.mikealbert.data.dao.SerializedConfigDAO;
import com.mikealbert.data.entity.SerializedConfig;
import com.mikealbert.ws.vehcfg.client.DeleteConfigurationRequestType;
import com.mikealbert.ws.vehcfg.client.GetConfigurationRequestType;
import com.mikealbert.ws.vehcfg.client.GetConfigurationResponseType;
import com.mikealbert.ws.vehcfg.client.ObjectFactory;
import com.mikealbert.ws.vehcfg.client.VehicleConfigurationService;
import com.mikealbert.ws.vehcfg.client.VehicleConfigurationType;

/**
* Public Interface implemented by {@link com.mikealbert.vision.service.AutodataServiceImpl} 
* for interacting with business service methods concerning: 
* 
*  @see com.mikealbert.vision.service.AutodataServiceImpl
* */
@Service("autodataService")
public class AutodataServiceImpl implements AutodataService {
	@Resource SerializedConfigDAO serializedConfigDAO;
	
	@Value("${vehcfg.wsdlURL:NOT_DEFINED}") private String wsdlURL;
	@Value("${vehcfg.namespace:NOT_DEFINED}") private String namespace;
	@Value("${vehcfg.service:NOT_DEFINED}") private String service;	
	
	private MalLogger logger = MalLoggerFactory.getLogger(this.getClass());	
	
    public SerializedConfig saveOrUpdateSerializedConfig(SerializedConfig serializedConfig) {
    	return serializedConfigDAO.saveAndFlush(serializedConfig);
    }
    
    public VehicleConfigurationType getVehicleConfiguration(Long cfgId) throws Exception{
		final URL WSDL = new URL(wsdlURL);
	    final QName SERVICE = new QName(namespace, service);
	    
		GetConfigurationRequestType request = new GetConfigurationRequestType();
		request.setVehicleConfigurationId(cfgId);
		
		VehicleConfigurationService service = new VehicleConfigurationService(WSDL, SERVICE);
		GetConfigurationResponseType response = service.getVehicleConfigurationPortSoap11().getConfiguration(request);
		
		return response.getVehicleConfiguration();		
    }
    
    public void deleteVehicleConfiguration(Long cfgId) throws Exception{
		final URL WSDL = new URL(wsdlURL);
	    final QName SERVICE = new QName(namespace, service);
	    
	    DeleteConfigurationRequestType request = new DeleteConfigurationRequestType();
		request.setVehicleConfigurationId(cfgId);
		
		VehicleConfigurationService service = new VehicleConfigurationService(WSDL, SERVICE);
		service.getVehicleConfigurationPortSoap11().deleteConfiguration(request);
				
    }    
}
