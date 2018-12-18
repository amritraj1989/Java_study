package com.mikealbert.service;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.mikealbert.data.entity.FleetMasterVinDetails;
import com.mikealbert.service.vo.VinDecoderRequestVO;



@Service
public interface VinDecoderService   { 
   
    public String getVehicleDescriptionXMLResponse(VinDecoderRequestVO VinDecoderRequestVO) throws Exception;
    
    public Map<String, Object> validateVINAndReturnMessage(String inputVIN, Long fmsId);	
	public FleetMasterVinDetails validateVINByChrome(String inputVIN, Long fmsId);
	
	public FleetMasterVinDetails saveFleetMasterVinDetail(FleetMasterVinDetails fleetMasterVinDetails) throws Exception;
	public FleetMasterVinDetails getFleetMasterVinDetailByFmsId(Long fmsId);
   
	public	String	ERROR_TYPE_BLOCKER ="ERROR_TYPE_BLOCKER";
	public	String	ERROR_TYPE_WARNING ="ERROR_TYPE_WARNING";
	public	String	MESSAGE ="MESSAGE";
}