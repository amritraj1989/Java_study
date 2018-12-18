package com.mikealbert.vision.specs.fleet;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.data.domain.PageRequest;

import com.mikealbert.data.dao.VehicleSearchDAO;
import com.mikealbert.data.entity.WarrantyUnitLink;
import com.mikealbert.data.enumeration.CorporateEntity;
import com.mikealbert.data.vo.VehicleInformationVO;
import com.mikealbert.data.vo.VehicleSearchCriteriaVO;
import com.mikealbert.data.vo.VehicleSearchResultVO;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.service.VehicleSearchService;
import com.mikealbert.testing.BaseSpec;
import com.mikealbert.util.MALUtilities;
import com.mikealbert.vision.service.VehicleMaintenanceService;

public class DisplayExtWarrantyIndTest extends BaseSpec {
	
	@Resource VehicleMaintenanceService vehicleMaintenanceService;
	@Resource VehicleSearchService vehicleService;
	
	public String testExtWarrantyInd(String unitNumber, Date startDate) throws MalBusinessException{		
		List<VehicleSearchResultVO> vehicleSearchResult;
		VehicleSearchCriteriaVO vehicleSearchCriteria = new VehicleSearchCriteriaVO();
		VehicleInformationVO vehicleInformationVO = new VehicleInformationVO();
		VehicleSearchResultVO selectedSearchVO;
		String extWarrantyInd = null;
		
		vehicleSearchCriteria.setUnitNo(unitNumber);
		vehicleSearchCriteria.setCorporateEntity(CorporateEntity.MAL);
		vehicleSearchCriteria.setUnitStatus(VehicleSearchDAO.VEHICLE_SEARCH_STATUS_BOTH);
		PageRequest page = new PageRequest(0,10);
		vehicleSearchResult = vehicleService.findBySearchCriteria(vehicleSearchCriteria, page, null);
		selectedSearchVO = vehicleSearchResult.get(0);
		vehicleInformationVO = vehicleMaintenanceService.getVehicleInformationByFmsId(selectedSearchVO.getFmsId());
		
		Date currentDate = Calendar.getInstance().getTime();
    	
    	if (startDate != null) {
    		currentDate = startDate;
    	}
    	
    	for (WarrantyUnitLink extWar : vehicleInformationVO.getWarrantyUnitLinks()) {
    		if ((MALUtilities.compateDates(currentDate, extWar.getStartDate()) == 1 || MALUtilities.compateDates(currentDate, extWar.getStartDate()) == 0)
    				&& (MALUtilities.compateDates(currentDate, extWar.getEndDate()) == -1 || MALUtilities.compateDates(currentDate, extWar.getEndDate()) == 0)) {
    			extWarrantyInd = "(Ext. Warranty)";
    			break;
    		}
    	}
		
		
		return extWarrantyInd;
	}
}
