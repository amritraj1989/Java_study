package com.mikealbert.vision.specs.fleet;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.data.domain.PageRequest;

import com.mikealbert.common.MalConstants;
import com.mikealbert.data.DataConstants;
import com.mikealbert.data.entity.CostAvoidanceReason;
import com.mikealbert.data.entity.MaintenanceRequest;
import com.mikealbert.data.entity.MaintenanceRequestTask;
import com.mikealbert.data.enumeration.CorporateEntity;
import com.mikealbert.data.enumeration.MaintenanceRequestStatusEnum;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.service.FleetMasterService;
import com.mikealbert.service.MaintenanceRequestService;
import com.mikealbert.service.OdometerService;
import com.mikealbert.service.ServiceProviderService;
import com.mikealbert.testing.BaseSpec;

public class MaintenanceCostAvoidanceTest extends BaseSpec{

	@Resource MaintenanceRequestService maintenanceRequestService;
	@Resource ServiceProviderService serviceProviderService;
	@Resource FleetMasterService fleetMasterService;
	@Resource OdometerService odometerService;
	
	final String USERNAME = "SETUP";
	final String SERVICE_PROVIDER_NUMBER_NON_NETWORK = "00065534";
	
	
	public Map<String, String> testCostAvoidancePO(String unitNo){
		final String MAINTENANCE_CATEOGRY_CODE = "OIL_CHANGE";
		final String MAINTENANCE_CODE = "110-305";
		final String RECHARGE_CODE = "ABUSE";
		final String ODO_UOM_CODE_MILE = "MILE";
		final CostAvoidanceReason COST_AVOIDANCE_REASON_DECLINED = maintenanceRequestService.convertCostAvoidanceReason("DECLINED");
		
		PageRequest page = new PageRequest(0,1);
		
		MaintenanceRequest po = null;
		MaintenanceRequestTask task = null;
		
		Map<String, String> results = new HashMap<String, String>();

		try {
			po = new MaintenanceRequest();			
			po.setJobNo(maintenanceRequestService.generateJobNumber(CorporateEntity.MAL));
			po.setServiceProvider(serviceProviderService.getServiceProviderByNameOrCode(SERVICE_PROVIDER_NUMBER_NON_NETWORK,page).get(0));			
			po.setMaintReqStatus(MaintenanceRequestStatusEnum.MAINT_REQUEST_STATUS_BOOKED_IN.getCode());
			po.setMaintReqType("MAINT");
			po.setCurrentOdo(0L);
			po.setUnitofMeasureCode(odometerService.convertOdoUOMCode(ODO_UOM_CODE_MILE));
			po.setActualStartDate(Calendar.getInstance().getTime());
			po.setPlannedEndDate(Calendar.getInstance().getTime());
			po.setFleetMaster(fleetMasterService.findByUnitNo(unitNo));
			po.setServiceProviderMarkupInd(MalConstants.FLAG_N);
			
			task = new MaintenanceRequestTask();
	        task.setMaintCatCode(MAINTENANCE_CATEOGRY_CODE);
	        task.setMaintenanceCode(maintenanceRequestService.convertMaintenanceCode(MAINTENANCE_CODE));
	        task.setTaskQty(new BigDecimal(1));
	        task.setUnitCost(new BigDecimal(200.00).setScale(2, BigDecimal.ROUND_HALF_UP));
	        task.setTotalCost(task.getUnitCost().multiply(task.getTaskQty()).setScale(2, BigDecimal.ROUND_HALF_UP));
	        task.setRechargeFlag("Y");
	        task.setRechargeCode(RECHARGE_CODE);
	        task.setDiscountFlag(DataConstants.DEFAULT_N);	        
	        task.setAuthorizePerson(USERNAME);
	        task.setWasOutstanding(DataConstants.DEFAULT_N);
	        task.setOutstanding(DataConstants.DEFAULT_N);
	        task.setMaintenanceRequest(po);
	        task.setCostAvoidanceIndicator(true);
	        task.setCostAvoidanceCode(COST_AVOIDANCE_REASON_DECLINED.getCode());
	        task.setCostAvoidanceDescription(COST_AVOIDANCE_REASON_DECLINED.getDescription());
	        task.setCostAvoidanceAmount(new BigDecimal(30.00).setScale(2));
			
			po.setMaintenanceRequestTasks(new ArrayList<MaintenanceRequestTask>());
			po.getMaintenanceRequestTasks().add(task);
			
	        po = maintenanceRequestService.saveOrUpdateMaintnenacePO(po, USERNAME);	
			
			em.clear();
			
	        results.put("jobNo", po.getJobNo());
	        results.put("poTotal", maintenanceRequestService.calculatePOSubTotal(po).toString());
	        results.put("poMarkupTotal", maintenanceRequestService.calculateNonNetworkRechargeMarkup(po).toString());	
	        results.put("poRechargeTotal", maintenanceRequestService.sumTaskTotalToRecharge(po.getMaintenanceRequestTasks()).add(maintenanceRequestService.calculateNonNetworkRechargeMarkup(po)).toString());	
	        results.put("poCostAvoidanceTotal", maintenanceRequestService.sumCostAvoidanceTotal(po).toString());
	        results.put("poGoodwillTotal", maintenanceRequestService.sumGoodwillTotal(po).toString());
	        
		} catch (MalBusinessException e) {
			e.printStackTrace();
		}
		
		return results;
	}
	
}
