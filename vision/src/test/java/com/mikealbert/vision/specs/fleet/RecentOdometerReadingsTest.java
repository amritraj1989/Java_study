package com.mikealbert.vision.specs.fleet;

import javax.annotation.Resource;

import com.mikealbert.data.dao.FleetMasterDAO;
import com.mikealbert.data.entity.FleetMaster;
import com.mikealbert.data.entity.VehicleOdometerReadingsV;
import com.mikealbert.service.FleetMasterService;
import com.mikealbert.testing.BaseSpec;

public class RecentOdometerReadingsTest extends BaseSpec {
	
	@Resource private FleetMasterDAO fleetMasterDAO;
	@Resource FleetMasterService fleetMasterService;

	public VehicleOdometerReadingsV testRecentOdometerReadings(String unitNo, int rownum){

		FleetMaster fleetMaster = fleetMasterDAO.findByUnitNo(unitNo);
		return fleetMaster.getVehicleOdometerReadings().get(rownum);
	}

}
