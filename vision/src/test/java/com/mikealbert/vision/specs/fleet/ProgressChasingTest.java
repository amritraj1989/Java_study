package com.mikealbert.vision.specs.fleet;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.data.domain.PageRequest;

import com.mikealbert.data.dao.FleetMasterDAO;
import com.mikealbert.data.vo.ProgressChasingQueueVO;
import com.mikealbert.data.vo.ProgressChasingVO;
import com.mikealbert.service.MaintenanceRequestService;
import com.mikealbert.testing.BaseSpec;
import com.mikealbert.vision.service.VehicleMaintenanceService;

public class ProgressChasingTest extends BaseSpec {
	@Resource MaintenanceRequestService maintenanceRequestService;
	
	@Resource VehicleMaintenanceService vehicleMaintenanceService;
	@Resource FleetMasterDAO fleetMasterDAO;
	
	public Boolean testProgressChasingDataList(String poStatusDescription){
		boolean results = false;
		List<ProgressChasingQueueVO> progressChasingQueueVO = maintenanceRequestService.getProgressChasingDataList();
		
		for (ProgressChasingQueueVO pcqVO : progressChasingQueueVO) {
			if (pcqVO.getMaintRequestStatusDescription().contains(poStatusDescription)){
				results = true;
				break;
			}
		}
		return results;
	}	
	
	public ProgressChasingVO testProgressChasingByPoStatus(String poStatus, String poNumber){
		PageRequest page = null;
		ProgressChasingVO progressChasingVO = new ProgressChasingVO();
		
		List<ProgressChasingVO> progressChasingList = maintenanceRequestService.getProgressChasingByPoStatus(poStatus, page, null);
		try {
			for (ProgressChasingVO result : progressChasingList) {
				if (result.getPoNumber().equals(poNumber)){
					progressChasingVO = result;
				}
			}
		}
		catch 
			(Exception e){
		}
		return progressChasingVO;
	}

	public Boolean testProgressChasingLastUpdatedByFilter(String poStatus, String lastUpdatedByFilter){
		PageRequest page = null;
		boolean results = true;

		List<ProgressChasingVO> progressChasingList = maintenanceRequestService.getProgressChasingByPoStatus(poStatus, page, lastUpdatedByFilter);

		for (ProgressChasingVO pcVO : progressChasingList) {
			if (!pcVO.getLastChangedBy().contains(lastUpdatedByFilter)) {
				results = false;
				break;
			}
		}

		return results;
	}
	
}

