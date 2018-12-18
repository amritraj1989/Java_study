package com.mikealbert.vision.specs.fleet;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import com.mikealbert.data.entity.MaintenanceRequest;
import com.mikealbert.data.vo.MaintenanceServiceHistoryVO;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.service.FleetMasterService;
import com.mikealbert.service.MaintenanceHistoryService;
import com.mikealbert.service.MaintenanceRequestService;
import com.mikealbert.testing.BaseSpec;
import com.mikealbert.vision.service.VehicleMaintenanceService;

public class ReasonForServiceInPOTest extends BaseSpec{
	
	@Resource VehicleMaintenanceService vehicleMaintenanceService;
	@Resource MaintenanceRequestService maintenanceRequestService;	
	@Resource FleetMasterService fleetMasterService;
	@Resource MaintenanceHistoryService maintenanceHistoryService;
	
	public MaintenanceRequest testCreatePurchaseOrder(String inUnitNo, String inReasonForService){

		List<MaintenanceServiceHistoryVO> maintHistory = new ArrayList<MaintenanceServiceHistoryVO>();
		MaintenanceRequest maintenanceRequest = new MaintenanceRequest();
		maintHistory = maintenanceHistoryService.getMaintenanceServiceHistoryByVIN(fleetMasterService.findByUnitNo(inUnitNo).getVin(), null, null, null, null, null);
		maintenanceRequest = maintenanceRequestService.getMaintenanceRequestByJobNo(maintHistory.get(0).getPoNumber());
		maintenanceRequest.setReasonForService(inReasonForService);
		try {
			maintenanceRequest = maintenanceRequestService.saveOrUpdateMaintnenacePO(maintenanceRequest,"SHARMA_R");
		} catch (MalBusinessException e) {
			e.printStackTrace();
		}
	
		return maintenanceRequest;
	}
	
	public MaintenanceRequest testViewPurchaseOrder(String inPurchaseOrder){
		
		MaintenanceRequest mainteanceRequest = new MaintenanceRequest();
		mainteanceRequest = maintenanceRequestService.getMaintenanceRequestByJobNo(inPurchaseOrder);
		return mainteanceRequest;
	}

}
