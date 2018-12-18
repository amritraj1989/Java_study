package com.mikealbert.vision.specs.fleet;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import javax.annotation.Resource;

import com.mikealbert.data.dao.VehicleSearchDAO;
import com.mikealbert.data.entity.MaintenanceRequest;
import com.mikealbert.data.entity.MaintenanceRequestTask;
import com.mikealbert.data.enumeration.CorporateEntity;
import com.mikealbert.data.vo.VehicleSearchCriteriaVO;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.service.MaintenanceRequestService;
import com.mikealbert.testing.BaseSpec;
import com.mikealbert.vision.service.VehicleMaintenanceService;

public class CoordinatorNameLastSavedPOTest extends BaseSpec{
	
	@Resource VehicleMaintenanceService vehicleMaintenanceService;
	@Resource MaintenanceRequestService maintenanceRequestService;
	
	String lastChangedDate;

	SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
	
	public MaintenanceRequest testCreatePurchaseOrder(String purchaseOrderNumber){
		
		VehicleSearchCriteriaVO vehicleSearchCriteria = new VehicleSearchCriteriaVO();
		MaintenanceRequest mainteanceRequest = null;
		vehicleSearchCriteria.setPurchaseOrderNumber(purchaseOrderNumber);
		vehicleSearchCriteria.setCorporateEntity(CorporateEntity.MAL);
		vehicleSearchCriteria.setUnitStatus(VehicleSearchDAO.VEHICLE_SEARCH_STATUS_BOTH);
		mainteanceRequest = maintenanceRequestService.getMaintenanceRequestByJobNo(purchaseOrderNumber);
		setLastChangedDate(dateFormat.format(mainteanceRequest.getVersionts()));
		return mainteanceRequest;
	}
	
	public MaintenanceRequest testUpdatePO(String purchaseOrderNumber){
		
		VehicleSearchCriteriaVO vehicleSearchCriteria = new VehicleSearchCriteriaVO();
		MaintenanceRequest mainteanceRequest = null;
		vehicleSearchCriteria.setPurchaseOrderNumber(purchaseOrderNumber);
		vehicleSearchCriteria.setCorporateEntity(CorporateEntity.MAL);
		vehicleSearchCriteria.setUnitStatus(VehicleSearchDAO.VEHICLE_SEARCH_STATUS_BOTH);
		mainteanceRequest = maintenanceRequestService.getMaintenanceRequestByJobNo(purchaseOrderNumber);
		mainteanceRequest.setCurrentOdo(1500L);
		try {
			maintenanceRequestService.saveOrUpdateMaintnenacePO(mainteanceRequest, "SHARMA_R");
		} catch (MalBusinessException e) {
			e.printStackTrace();
		}
		setLastChangedDate(dateFormat.format(mainteanceRequest.getVersionts()));
		return mainteanceRequest;
	}
	
	public MaintenanceRequest testUpdatePOLine(String purchaseOrderNumber){
		
		VehicleSearchCriteriaVO vehicleSearchCriteria = new VehicleSearchCriteriaVO();
		MaintenanceRequest mainteanceRequest = null;
		vehicleSearchCriteria.setPurchaseOrderNumber(purchaseOrderNumber);
		vehicleSearchCriteria.setCorporateEntity(CorporateEntity.MAL);
		vehicleSearchCriteria.setUnitStatus(VehicleSearchDAO.VEHICLE_SEARCH_STATUS_BOTH);
		mainteanceRequest = maintenanceRequestService.getMaintenanceRequestByJobNo(purchaseOrderNumber);
		mainteanceRequest.getMaintenanceRequestTasks().get(0).setUnitCost(new BigDecimal(777));
		mainteanceRequest.getMaintenanceRequestTasks().get(0).setTotalCost(mainteanceRequest.getMaintenanceRequestTasks().get(0).getUnitCost().multiply(mainteanceRequest.getMaintenanceRequestTasks().get(0).getTaskQty()));
		mainteanceRequest.getMaintenanceRequestTasks().get(0).setRechargeTotalCost(mainteanceRequest.getMaintenanceRequestTasks().get(0).getTotalCost());
		try {
			maintenanceRequestService.saveOrUpdateMaintnenacePO(mainteanceRequest, "SHARMA_R");
		} catch (MalBusinessException e) {
			e.printStackTrace();
		}
		setLastChangedDate(dateFormat.format(mainteanceRequest.getVersionts()));
		return mainteanceRequest;
	}

	public MaintenanceRequest testUpdatePOAddLine(String purchaseOrderNumber){
		VehicleSearchCriteriaVO vehicleSearchCriteria = new VehicleSearchCriteriaVO();
		MaintenanceRequest mainteanceRequest = null;
		vehicleSearchCriteria.setPurchaseOrderNumber(purchaseOrderNumber);
		vehicleSearchCriteria.setCorporateEntity(CorporateEntity.MAL);
		vehicleSearchCriteria.setUnitStatus(VehicleSearchDAO.VEHICLE_SEARCH_STATUS_BOTH);
		mainteanceRequest = maintenanceRequestService.getMaintenanceRequestByJobNo(purchaseOrderNumber);
		
		MaintenanceRequestTask maintReqTask = mainteanceRequest.getMaintenanceRequestTasks().get(0);
		maintReqTask.setMrtId(null);
		maintReqTask.setTaskQty(new BigDecimal(2));
		maintReqTask.setUnitCost(new BigDecimal(99));
		maintReqTask.setTotalCost(maintReqTask.getUnitCost().multiply(maintReqTask.getTaskQty()));
		maintReqTask.setRechargeTotalCost(maintReqTask.getTotalCost());
		
		mainteanceRequest.getMaintenanceRequestTasks().add(maintReqTask);
		
		try {
			maintenanceRequestService.saveOrUpdateMaintnenacePO(mainteanceRequest, "SHARMA_R");
		} catch (MalBusinessException e) {
			e.printStackTrace();
		}
		setLastChangedDate(dateFormat.format(mainteanceRequest.getVersionts()));
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

public String getLastChangedDate() {
	return lastChangedDate;
}

public void setLastChangedDate(String lastChangedDate) {
	this.lastChangedDate = lastChangedDate;
}
}
