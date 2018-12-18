package com.mikealbert.vision.specs.fleet;

import java.util.List;
import javax.annotation.Resource;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import com.mikealbert.data.dao.FleetMasterDAO;
import com.mikealbert.data.entity.FleetMaster;
import com.mikealbert.data.vo.MaintenanceServiceHistoryVO;
import com.mikealbert.service.MaintenanceHistoryService;
import com.mikealbert.testing.BaseSpec;
import com.mikealbert.util.MALUtilities;
import com.mikealbert.vision.service.VehicleMaintenanceService;

public class ServiceHistoryTest extends BaseSpec {
	
	@Resource VehicleMaintenanceService vehicleMaintenanceService;
	@Resource FleetMasterDAO fleetMasterDAO;
	@Resource MaintenanceHistoryService maintenanceHistoryService;
	
	public MaintenanceServiceHistoryVO testServiceHistoryForGivenPo(String unitNo, String poNumber){
		Sort sort = null;
		PageRequest page = null;
		
		FleetMaster fleetMaster = fleetMasterDAO.findByUnitNo(unitNo);
		String vin = fleetMaster.getVin();
		MaintenanceServiceHistoryVO maintenanceServiceHistoryVO = new MaintenanceServiceHistoryVO();
	
		List<MaintenanceServiceHistoryVO> maintenanceServiceHistoryList = maintenanceHistoryService.getMaintenanceServiceHistoryByVIN(vin,page,sort,null,null, null);
		try {		
			for (MaintenanceServiceHistoryVO result : maintenanceServiceHistoryList) {
				if(result.getPoNumber().equals(poNumber)){
					maintenanceServiceHistoryVO = result;
				}
			}
		}
			catch(Exception e){
		}		

		return maintenanceServiceHistoryVO;
	}
	
	public Boolean testServiceHistoryMaintCodeFilter(String unitNo, String maintCodeDescFilter){
		Sort sort = null;
		PageRequest page = null;
		
		FleetMaster fleetMaster = fleetMasterDAO.findByUnitNo(unitNo);
		String vin = fleetMaster.getVin();

		List<MaintenanceServiceHistoryVO> maintenanceServiceHistoryList = maintenanceHistoryService.getMaintenanceServiceHistoryByVIN(vin,page,sort,null,null, maintCodeDescFilter);

		if (maintenanceServiceHistoryList.size() > 0) {
			return true;
		}

		return false;
	}

}
