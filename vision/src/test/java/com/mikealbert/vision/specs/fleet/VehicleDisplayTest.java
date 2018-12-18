package com.mikealbert.vision.specs.fleet;

import java.util.List;
import javax.annotation.Resource;

import org.springframework.data.domain.PageRequest;

import com.mikealbert.data.dao.VehicleSearchDAO;
import com.mikealbert.data.enumeration.CorporateEntity;
import com.mikealbert.data.vo.VehicleInformationVO;
import com.mikealbert.data.vo.VehicleSearchCriteriaVO;
import com.mikealbert.data.vo.VehicleSearchResultVO;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.service.VehicleSearchService;
import com.mikealbert.testing.BaseSpec;
import com.mikealbert.vision.service.VehicleMaintenanceService;

public class VehicleDisplayTest extends BaseSpec {
	
	@Resource VehicleMaintenanceService vehicleMaintenanceService;
	@Resource VehicleSearchService vehicleService;
	
	public boolean testVehicleSearchResultsPOButtonEnabled(String unitNumber, String status) throws MalBusinessException{		
		List<VehicleSearchResultVO> vehicleSearchResult;
		VehicleSearchCriteriaVO vehicleSearchCriteria = new VehicleSearchCriteriaVO();
		
		vehicleSearchCriteria.setUnitNo(unitNumber);
		vehicleSearchCriteria.setCorporateEntity(CorporateEntity.MAL);
		if(status.equalsIgnoreCase("ACTIVE")){
			vehicleSearchCriteria.setUnitStatus(VehicleSearchDAO.VEHICLE_SEARCH_STATUS_ACTIVE);
		}else if(status.equalsIgnoreCase("INACTIVE")){
			vehicleSearchCriteria.setUnitStatus(VehicleSearchDAO.VEHICLE_SEARCH_STATUS_INACTIVE);
		}else {
			vehicleSearchCriteria.setUnitStatus(VehicleSearchDAO.VEHICLE_SEARCH_STATUS_BOTH);
		}

		try {
			PageRequest page = new PageRequest(0,10);
			vehicleSearchResult = vehicleService.findBySearchCriteria(vehicleSearchCriteria, page, null);
			VehicleSearchResultVO selectedSearchVO = vehicleSearchResult.get(0); 
	    	if(selectedSearchVO != null && selectedSearchVO.getFmsId() != null){
/*				if(selectedSearchVO.getUnitStatus().equals(ActiveVehicleStatus.STATUS_ON_ORDER.getDescription())
						|| selectedSearchVO.getUnitStatus().equals(ActiveVehicleStatus.STATUS_PENDING_LIVE.getDescription())
						|| selectedSearchVO.getUnitStatus().equals(ActiveVehicleStatus.STATUS_ON_CONTRACT.getDescription())
						|| selectedSearchVO.getUnitStatus().equals(ActiveVehicleStatus.STATUS_ON_STOCK.getDescription())){
					return true;
				}*/
	    		return true;
	    	}			
	    	return false;
			
		} catch (Exception malEx) {
			return false;
		}
	}
	
	
	public VehicleInformationVO testVehicleInformation(String unitNumber, String status){
		List<VehicleSearchResultVO> vehicleSearchResult;
		VehicleSearchCriteriaVO vehicleSearchCriteria = new VehicleSearchCriteriaVO();
		VehicleInformationVO vehicleInformationVO = new VehicleInformationVO();
		vehicleSearchCriteria.setUnitNo(unitNumber);
		vehicleSearchCriteria.setCorporateEntity(CorporateEntity.MAL);
		if(status.equalsIgnoreCase("ACTIVE")){
			vehicleSearchCriteria.setUnitStatus(VehicleSearchDAO.VEHICLE_SEARCH_STATUS_ACTIVE);
		}else if(status.equalsIgnoreCase("INACTIVE")){
			vehicleSearchCriteria.setUnitStatus(VehicleSearchDAO.VEHICLE_SEARCH_STATUS_INACTIVE);
		}else {
			vehicleSearchCriteria.setUnitStatus(VehicleSearchDAO.VEHICLE_SEARCH_STATUS_BOTH);
		}

		try {
			PageRequest page = new PageRequest(0,10);
			vehicleSearchResult = vehicleService.findBySearchCriteria(vehicleSearchCriteria, page, null);
			VehicleSearchResultVO selectedSearchVO = vehicleSearchResult.get(0); 			
			vehicleInformationVO = vehicleMaintenanceService.getVehicleInformationByFmsId(selectedSearchVO.getFmsId());
			
			return vehicleInformationVO;
			
		} catch (Exception malEx) {
			return vehicleInformationVO;
		}		
	}

}
