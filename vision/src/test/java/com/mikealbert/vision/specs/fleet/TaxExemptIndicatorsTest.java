package com.mikealbert.vision.specs.fleet;

import java.util.List;

import javax.annotation.Resource;

import com.mikealbert.data.entity.MaintenanceRequest;
import com.mikealbert.data.entity.RegionCode;
import com.mikealbert.data.vo.VehicleInformationVO;
import com.mikealbert.service.MaintenanceRequestService;
import com.mikealbert.testing.BaseSpec;
import com.mikealbert.vision.service.VehicleMaintenanceService;

public class TaxExemptIndicatorsTest extends BaseSpec{

	@Resource VehicleMaintenanceService vehicleMaintenanceService;
	@Resource MaintenanceRequestService maintenanceRequestService;
	
	private VehicleInformationVO vehicleInfo;
	private boolean taxExemptedIndicator;
	private boolean taxExemptedRegionsExist;
	
	
    public void testTaxExemptedRegions(String jobNo){
    	MaintenanceRequest po = maintenanceRequestService.getMaintenanceRequestByJobNo(jobNo);
    	if(po != null){
    		if(po.getFleetMaster() != null){
    			vehicleInfo = vehicleMaintenanceService.getVehicleInformationByFmsId(po.getFleetMaster().getFmsId());
    		}else{
    			vehicleInfo = vehicleMaintenanceService.getVehicleInformationByMrqId(po.getMrqId());
    		}
    	}    	
    	if(vehicleInfo != null && vehicleInfo.getClientTaxIndicator() != null && vehicleInfo.getClientTaxIndicator().equals("E")){
    		taxExemptedIndicator = true;
    		List<RegionCode> taxExemptedRegionList = vehicleMaintenanceService.getTaxExemptedRegions(vehicleInfo.getClientCorporateId(), vehicleInfo.getClientAccountType(), vehicleInfo.getClientAccountNumber());
    		if(taxExemptedRegionList != null && taxExemptedRegionList.size() > 0 ){
    			taxExemptedRegionsExist = true;	
    		}else{
    			taxExemptedRegionsExist = false;
    		}
    	}else{
    		taxExemptedIndicator = false;
    		taxExemptedRegionsExist = false;
    	}
    }


	public boolean isTaxExemptedIndicator() {
		return taxExemptedIndicator;
	}


	public void setTaxExemptedIndicator(boolean taxExemptedIndicator) {
		this.taxExemptedIndicator = taxExemptedIndicator;
	}


	public boolean isTaxExemptedRegionsExist() {
		return taxExemptedRegionsExist;
	}


	public void setTaxExemptedRegionsExist(boolean taxExemptedRegionsExist) {
		this.taxExemptedRegionsExist = taxExemptedRegionsExist;
	}
    
    
}
