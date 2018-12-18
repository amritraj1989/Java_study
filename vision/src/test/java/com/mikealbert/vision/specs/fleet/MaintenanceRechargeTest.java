package com.mikealbert.vision.specs.fleet;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.annotation.Resource;

import com.mikealbert.data.dao.FleetMasterDAO;
import com.mikealbert.data.dao.MaintenanceRequestDAO;
import com.mikealbert.data.entity.FleetMaster;
import com.mikealbert.data.entity.MaintenanceRequest;
import com.mikealbert.service.FleetMasterService;
import com.mikealbert.testing.BaseSpec;
import com.mikealbert.vision.service.VehicleMaintenanceService;

public class MaintenanceRechargeTest extends BaseSpec{
	
	@Resource private MaintenanceRequestDAO maintenanceRequestDAO;
	@Resource private FleetMasterDAO fleetMasterDAO;
	@Resource VehicleMaintenanceService vehicleMaintenanceService;
	@Resource FleetMasterService fleetMasterService;
	
	public String getRechargeFlag(long mrqId){
		MaintenanceRequest maintenanceRequest = maintenanceRequestDAO.getMaintenanceRequestByMrqId(mrqId);
		return vehicleMaintenanceService.getDefaultMaintRechargeFlag(maintenanceRequest);
	}
	
	public String getRechargeCode(long mrqId){
		MaintenanceRequest maintenanceRequest = maintenanceRequestDAO.getMaintenanceRequestByMrqId(mrqId);
		return vehicleMaintenanceService.getDefaultMaintRechargeCode(maintenanceRequest).getCode();
	}
	
	public MaintenanceRequest getMaxMaintenanceRequest(String unitNo){
		FleetMaster fleetMaster =  fleetMasterDAO.findByUnitNo(unitNo);
		
		List<MaintenanceRequest> maintenanceRequests = maintenanceRequestDAO.findByFmsId(fleetMaster.getFmsId());
		MaintenanceRequest maxMaintRequest = Collections.max(maintenanceRequests, new Comparator<MaintenanceRequest>(){
			public int compare(MaintenanceRequest m1, MaintenanceRequest m2) { 
				return m1.getMrqId().compareTo(m2.getMrqId()); 
			}
		});
		
		return maxMaintRequest;
	}

/*fm-154*/	
	public String testActualEndDatePriorToMaintStartDate(String unitNo){
		//long mrqId = 634795;
		return getRechargeFlag(getMaxMaintenanceRequest(unitNo).getMrqId());
	}
	
	public String testMaintProgramEqualsMaintMgmt(String unitNo){
		//long mrqId = 712691;
		return getRechargeFlag(getMaxMaintenanceRequest(unitNo).getMrqId());		
	}
	
	public String testMaintProgramEqualsFullMaint(String unitNo){
		//long mrqId = 612435;
		return getRechargeFlag(getMaxMaintenanceRequest(unitNo).getMrqId());		
	}
	
	public String testNoMaintProgram(String unitNo){
		FleetMaster fleetMaster = fleetMasterDAO.findByUnitNo(unitNo);
		
		MaintenanceRequest maintenanceRequest = new MaintenanceRequest();
    	maintenanceRequest.setFleetMaster(fleetMasterService.getFleetMasterByFmsId(fleetMaster.getFmsId()));
    	maintenanceRequest.setMaintReqType("MAINT");
		
		return vehicleMaintenanceService.getDefaultMaintRechargeFlag(maintenanceRequest);	
	}	
	
	public String testMaintReqTypeRiskMgt(String unitNo){
		FleetMaster fleetMaster = fleetMasterDAO.findByUnitNo(unitNo);
		
		MaintenanceRequest maintenanceRequest = new MaintenanceRequest();
		maintenanceRequest.setFleetMaster(fleetMasterService.getFleetMasterByFmsId(fleetMaster.getFmsId()));
		maintenanceRequest.setMaintReqType("RISK_MGMT");
		
		return vehicleMaintenanceService.getDefaultMaintRechargeFlag(maintenanceRequest);
	}
	

/*fm-156*/
	
	public String testMaintMgtAndMaint(String unitNo){
		//long mrqId = 702631;
		return getRechargeCode(getMaxMaintenanceRequest(unitNo).getMrqId());
	}

	public String testBothMaintMgtAndRiskMgtAndMaint(String unitNo){
		//long mrqId = 718952;
		return getRechargeCode(getMaxMaintenanceRequest(unitNo).getMrqId());	
	}	
	
	public String testBothMaintMgtAndRiskMgtAndRiskMgt(String unitNo){
		FleetMaster fleetMaster = fleetMasterDAO.findByUnitNo(unitNo);
		
		MaintenanceRequest maintenanceRequest = new MaintenanceRequest();
    	maintenanceRequest.setFleetMaster(fleetMasterService.getFleetMasterByFmsId(fleetMaster.getFmsId()));
    	maintenanceRequest.setMaintReqType("RISK_MGMT");
		
		return vehicleMaintenanceService.getDefaultMaintRechargeCode(maintenanceRequest).getCode();			
	}
	
	public String testBothFullBgtAndRiskMgtAndMaint(String unitNo){
		String result = "Blank";
		FleetMaster fleetMaster = fleetMasterDAO.findByUnitNo(unitNo);
		
		MaintenanceRequest maintenanceRequest = new MaintenanceRequest();
    	maintenanceRequest.setFleetMaster(fleetMasterService.getFleetMasterByFmsId(fleetMaster.getFmsId()));
    	maintenanceRequest.setMaintReqType("MAINT");
		
		if (vehicleMaintenanceService.getDefaultMaintRechargeCode(maintenanceRequest) != null){
			if (vehicleMaintenanceService.getDefaultMaintRechargeCode(maintenanceRequest).getCode() != null){
				result = "Not Blank";
			}
		}
		return result;
	}
	
	public String testBothFullBgtAndRiskMgtAndRiskMgmt(String unitNo){
		FleetMaster fleetMaster = fleetMasterDAO.findByUnitNo(unitNo);
		
		MaintenanceRequest maintenanceRequest = new MaintenanceRequest();
    	maintenanceRequest.setFleetMaster(fleetMasterService.getFleetMasterByFmsId(fleetMaster.getFmsId()));
    	maintenanceRequest.setMaintReqType("RISK_MGMT");
		
		return vehicleMaintenanceService.getDefaultMaintRechargeCode(maintenanceRequest).getCode();			
	}
	
	public String testNoMaintPrograms(String unitNo){
		FleetMaster fleetMaster = fleetMasterDAO.findByUnitNo(unitNo);

		MaintenanceRequest maintenanceRequest = new MaintenanceRequest();
    	maintenanceRequest.setFleetMaster(fleetMasterService.getFleetMasterByFmsId(fleetMaster.getFmsId()));
    	maintenanceRequest.setMaintReqType("MAINT");
		
		return vehicleMaintenanceService.getDefaultMaintRechargeCode(maintenanceRequest).getCode();			
	}
}
