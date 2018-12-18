package com.mikealbert.vision.specs.fleet;

import javax.annotation.Resource;

import com.mikealbert.data.dao.VehicleSearchDAO;
import com.mikealbert.data.entity.MaintenanceRequest;
import com.mikealbert.data.enumeration.CorporateEntity;
import com.mikealbert.data.vo.VehicleSearchCriteriaVO;
import com.mikealbert.service.MaintenanceRequestService;
import com.mikealbert.testing.BaseSpec;
import com.mikealbert.vision.service.VehicleMaintenanceService;

public class PODisplayTest extends BaseSpec{
	
	@Resource VehicleMaintenanceService vehicleMaintenanceService;
	@Resource MaintenanceRequestService maintenanceRequestService;	
	
	public MaintenanceRequest testCreatePurchaseOrder(String purchaseOrderNumber){
		
		VehicleSearchCriteriaVO vehicleSearchCriteria = new VehicleSearchCriteriaVO();
		MaintenanceRequest mainteanceRequest = null;
		vehicleSearchCriteria.setPurchaseOrderNumber(purchaseOrderNumber);
		vehicleSearchCriteria.setCorporateEntity(CorporateEntity.MAL);
		vehicleSearchCriteria.setUnitStatus(VehicleSearchDAO.VEHICLE_SEARCH_STATUS_BOTH);
		mainteanceRequest = maintenanceRequestService.getMaintenanceRequestByJobNo(purchaseOrderNumber);
		return mainteanceRequest;
	}
	
public MaintenanceRequest testViewPurchaseOrder(String purchaseOrderNumber){
		
		VehicleSearchCriteriaVO vehicleSearchCriteria = new VehicleSearchCriteriaVO();
		MaintenanceRequest mainteanceRequest = null;
		vehicleSearchCriteria.setPurchaseOrderNumber(purchaseOrderNumber);
		vehicleSearchCriteria.setCorporateEntity(CorporateEntity.MAL);
		vehicleSearchCriteria.setUnitStatus(VehicleSearchDAO.VEHICLE_SEARCH_STATUS_BOTH);
		mainteanceRequest = maintenanceRequestService.getMaintenanceRequestByJobNo(purchaseOrderNumber);
		return mainteanceRequest;
	}
}
