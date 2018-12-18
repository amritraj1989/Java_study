package com.mikealbert.vision.specs.fleet;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import com.mikealbert.data.DataConstants;
import com.mikealbert.data.entity.CostAvoidanceReason;
import com.mikealbert.data.entity.MaintenanceRequest;
import com.mikealbert.data.entity.MaintenanceRequestTask;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.service.MaintenanceRequestService;
import com.mikealbert.testing.BaseSpec;

public class MaintenanceGoodwillTest extends BaseSpec{

	@Resource MaintenanceRequestService maintenanceRequestService;
	
	final String USERNAME = "SETUP";
	
	String goodwillPOJobNo = null;
	
	public Map<String, String> testCreateGoodwillPO(String jobNo){
		MaintenanceRequest originalPO = null;
		MaintenanceRequest goodwillPO = null;
		Map<String, String> results = new HashMap<String, String>();

		try {
			originalPO = maintenanceRequestService.getMaintenanceRequestByJobNo(jobNo);
			
			em.clear();
			
			goodwillPO = maintenanceRequestService.createGoodwillMaintenanceRequest(originalPO, USERNAME);		
			
			em.clear();
			
	        results.put("jobNo", goodwillPO.getJobNo());
	        results.put("hasSuffix", goodwillPO.getJobNo().contains("CR") ? "CRn" : "no suffix found");
	        results.put("poTotal", maintenanceRequestService.calculatePOSubTotal(goodwillPO).toString());
	        results.put("poMarkupTotal", maintenanceRequestService.calculateNonNetworkRechargeMarkup(goodwillPO).toString());	
	        results.put("poRechargeTotal", maintenanceRequestService.sumTaskTotalToRecharge(goodwillPO.getMaintenanceRequestTasks()).toString());			     
	        
	        goodwillPOJobNo = goodwillPO.getJobNo();
	        
		} catch (MalBusinessException e) {
			e.printStackTrace();
		}
		
		return results;
	}
	
	public Map<String, String> testGoodwillPOUpdateNonCostAvoidanceTask(){		
		final String MAINTENANCE_CODE = "560-103"; 
		
		Map<String, String> results = new HashMap<String, String>();
		MaintenanceRequest goodwillPO = null;
		MaintenanceRequestTask keptTask = null;		
		
		try{
			goodwillPO = maintenanceRequestService.getMaintenanceRequestByJobNo(goodwillPOJobNo);
			
			em.clear();
			
	        for(MaintenanceRequestTask task : goodwillPO.getMaintenanceRequestTasks()){
	        	if(task.getMaintenanceCode().getCode().equals(MAINTENANCE_CODE)){
	        		task.setCostAvoidanceIndicator(false);
	        		keptTask = task;
	        		break;
	        	}
	        }               
	        	        	        	        
	        goodwillPO.getMaintenanceRequestTasks().clear();
	        goodwillPO.getMaintenanceRequestTasks().add(keptTask);	        	                     
	        
	        goodwillPO = maintenanceRequestService.saveOrUpdateMaintnenacePO(goodwillPO, USERNAME);	
	        
	        em.clear();
	        
	        results.put("costAvoidanceReason", goodwillPO.getMaintenanceRequestTasks().get(0).getCostAvoidanceCode());
	        results.put("costAvoidanceAmount", goodwillPO.getMaintenanceRequestTasks().get(0).getCostAvoidanceAmount().toString());
	        results.put("goodwillReason", goodwillPO.getMaintenanceRequestTasks().get(0).getGoodwillReason());
	        results.put("goodwillAmount", goodwillPO.getMaintenanceRequestTasks().get(0).getGoodwillCost().toString());
	        results.put("goodwillPercent", goodwillPO.getMaintenanceRequestTasks().get(0).getGoodwillPercent().toString());
	                
			
		} catch (MalBusinessException e) {
			e.printStackTrace();
		}
		
		return results;		
	}
	
	public Map<String, String> testGoodwillPOAddCostAvoidanceTask(){
		final String MAINTENANCE_CATEOGRY_CODE = "TRANS";
		final String MAINTENANCE_CODE = "560-103"; 
		final CostAvoidanceReason COST_AVOIDANCE_REASON_GOODWILL = maintenanceRequestService.convertCostAvoidanceReason("GOODWILL");
		
		Map<String, String> results = new HashMap<String, String>();
		MaintenanceRequest goodwillPO = null;
		MaintenanceRequestTask keptTask = null;		
		
		try{
			goodwillPO = maintenanceRequestService.getMaintenanceRequestByJobNo(goodwillPOJobNo);
			
			em.clear();
			           	        	        	        	        
	        goodwillPO.getMaintenanceRequestTasks().clear();	        	        
	        
	        keptTask = new MaintenanceRequestTask();
	        keptTask.setMaintCatCode(MAINTENANCE_CATEOGRY_CODE);
	        keptTask.setMaintenanceCode(maintenanceRequestService.convertMaintenanceCode(MAINTENANCE_CODE));
	        keptTask.setTaskQty(new BigDecimal(1));
	        keptTask.setUnitCost(new BigDecimal(0.00).setScale(2, BigDecimal.ROUND_HALF_UP));
	        keptTask.setTotalCost(keptTask.getUnitCost().multiply(keptTask.getTaskQty()).setScale(2, BigDecimal.ROUND_HALF_UP));
	        keptTask.setRechargeFlag("Y");
	        keptTask.setDiscountFlag("N");	        
	        keptTask.setAuthorizePerson(USERNAME);
	        keptTask.setWasOutstanding(DataConstants.DEFAULT_N);
	        keptTask.setOutstanding(DataConstants.DEFAULT_N);
	        keptTask.setMaintenanceRequest(goodwillPO);
	        keptTask.setCostAvoidanceIndicator(true);
	        keptTask.setCostAvoidanceCode(COST_AVOIDANCE_REASON_GOODWILL.getCode());
	        keptTask.setCostAvoidanceDescription(COST_AVOIDANCE_REASON_GOODWILL.getDescription());
	        keptTask.setCostAvoidanceAmount(new BigDecimal(639.25).setScale(2));
	        keptTask.setGoodwillReason("Received goodwill from GM for Torque converter replacement");
	        keptTask.setGoodwillPercent(new BigDecimal(100));
	        keptTask.setGoodwillCost(new BigDecimal(639.25).setScale(2));
	        
	        goodwillPO.getMaintenanceRequestTasks().add(keptTask);        
	        
	        goodwillPO = maintenanceRequestService.saveOrUpdateMaintnenacePO(goodwillPO, USERNAME);	
	        
	        em.clear();
	        	
	        results.put("jobNo", goodwillPO.getJobNo());
	        results.put("poTotal", maintenanceRequestService.calculatePOSubTotal(goodwillPO).toString());
	        results.put("poMarkupTotal", maintenanceRequestService.calculateNonNetworkRechargeMarkup(goodwillPO).toString());	
	        results.put("poRechargeTotal", maintenanceRequestService.sumTaskTotalToRecharge(goodwillPO.getMaintenanceRequestTasks()).toString());
	        results.put("poGoodwillTotal", maintenanceRequestService.sumGoodwillTotal(goodwillPO).toString());
	        results.put("poCostAvoidanceTotal", maintenanceRequestService.sumCostAvoidanceTotal(goodwillPO).toString());
	        results.put("costAvoidanceReason", goodwillPO.getMaintenanceRequestTasks().get(0).getCostAvoidanceCode());
	        results.put("costAvoidanceAmount", goodwillPO.getMaintenanceRequestTasks().get(0).getCostAvoidanceAmount().toString());
	        results.put("goodwillReason", goodwillPO.getMaintenanceRequestTasks().get(0).getGoodwillReason());
	        results.put("goodwillAmount", goodwillPO.getMaintenanceRequestTasks().get(0).getGoodwillCost().toString());
	        results.put("goodwillPercent", goodwillPO.getMaintenanceRequestTasks().get(0).getGoodwillPercent().toString());	        
			
		} catch (MalBusinessException e) {
			e.printStackTrace();
		}
		
		return results;	
		
	}
	
}
