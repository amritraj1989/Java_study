package com.mikealbert.vision.specs.fleet;

import java.text.DecimalFormat;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.data.domain.PageRequest;

import com.mikealbert.data.dao.VehicleSearchDAO;
import com.mikealbert.data.enumeration.CorporateEntity;
import com.mikealbert.data.vo.VehicleInformationVO;
import com.mikealbert.data.vo.VehicleSearchCriteriaVO;
import com.mikealbert.data.vo.VehicleSearchResultVO;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.service.FleetMasterService;
import com.mikealbert.service.OdometerService;
import com.mikealbert.service.VehicleSearchService;
import com.mikealbert.testing.BaseSpec;
import com.mikealbert.vision.service.VehicleMaintenanceService;
import com.mikealbert.vision.view.ViewConstants;

public class DisplayWarrantyDataTest extends BaseSpec {
	
	@Resource VehicleMaintenanceService vehicleMaintenanceService;
	@Resource VehicleSearchService vehicleService;
	@Resource OdometerService odometerService;
	@Resource FleetMasterService fleetMasterService;
	
	public String testWarrantyInfo(String unitNumber) throws MalBusinessException{		
		List<VehicleSearchResultVO> vehicleSearchResult;
		VehicleSearchCriteriaVO vehicleSearchCriteria = new VehicleSearchCriteriaVO();
		VehicleInformationVO vehicleInformationVO = new VehicleInformationVO();
		VehicleSearchResultVO selectedSearchVO;
		
		vehicleSearchCriteria.setUnitNo(unitNumber);
		vehicleSearchCriteria.setCorporateEntity(CorporateEntity.MAL);
		vehicleSearchCriteria.setUnitStatus(VehicleSearchDAO.VEHICLE_SEARCH_STATUS_BOTH);
		PageRequest page = new PageRequest(0,10);
		vehicleSearchResult = vehicleService.findBySearchCriteria(vehicleSearchCriteria, page, null);
		selectedSearchVO = vehicleSearchResult.get(0);
		vehicleInformationVO = vehicleMaintenanceService.getVehicleInformationByFmsId(selectedSearchVO.getFmsId());
		
		String uomCode;
		Double warrantyMileage;
    	String uomDescription;
    	String warrantyInfo = null;
    	DecimalFormat decimalFormat = new DecimalFormat("############.#");
    	
    	if (vehicleInformationVO.getVehicleTechInfo() != null && vehicleInformationVO.getVehicleTechInfo().getVehicleWarrantyMileage() != null && vehicleInformationVO.getVehicleTechInfo().getVehicleWarrantyMonths() != null) {
    		uomCode = odometerService.getCurrentOdometer(fleetMasterService.getFleetMasterByFmsId(vehicleInformationVO.getFmsId())).getUomCode();
			uomDescription = (odometerService.convertOdoUOMCode(uomCode)).getDescription();
			
			if (uomCode.equals("KM")) {
				warrantyMileage = vehicleInformationVO.getVehicleTechInfo().getVehicleWarrantyMileage() * ViewConstants.MILE_TO_KM_CONVERSION_FACTOR;
			} else {
				warrantyMileage = vehicleInformationVO.getVehicleTechInfo().getVehicleWarrantyMileage().doubleValue();
			}
			
    		if (vehicleInformationVO.getVehicleTechInfo().getVehicleWarrantyMonths() == 999) {
    			if (vehicleInformationVO.getVehicleTechInfo().getVehicleWarrantyMileage() == 99999999) {
    				warrantyInfo = "Unlimited Months / Unlimited " + uomDescription;
    			} else {
    				warrantyInfo = "Unlimited Months / " + decimalFormat.format(warrantyMileage) + " " + uomDescription;
    			}
    		} else {
    			if (vehicleInformationVO.getVehicleTechInfo().getVehicleWarrantyMileage() == 99999999) {
    				warrantyInfo = vehicleInformationVO.getVehicleTechInfo().getVehicleWarrantyMonths() + " Months / Unlimited " + uomDescription;
    			} else {
    				warrantyInfo = vehicleInformationVO.getVehicleTechInfo().getVehicleWarrantyMonths() + " Months / " + decimalFormat.format(warrantyMileage) + " " + uomDescription;
    			}
    		}
		}
		
		return warrantyInfo;
	}
}
