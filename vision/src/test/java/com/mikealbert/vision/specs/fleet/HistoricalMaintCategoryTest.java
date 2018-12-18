package com.mikealbert.vision.specs.fleet;

import java.util.List;

import javax.annotation.Resource;

import com.mikealbert.data.entity.FleetMaster;
import com.mikealbert.data.vo.HistoricalMaintCatCodeVO;
import com.mikealbert.service.FleetMasterService;
import com.mikealbert.service.MaintenanceRequestService;
import com.mikealbert.testing.BaseSpec;

public class HistoricalMaintCategoryTest extends BaseSpec{

	@Resource MaintenanceRequestService maintRequestService;
	@Resource FleetMasterService fleetMasterService;
	
	public boolean getHistoricalMaintCategoryListSize(String unitNo, String maintCatCode, Long mrqId){
		try{
			FleetMaster fm = fleetMasterService.findByUnitNo(unitNo);
			List<HistoricalMaintCatCodeVO> historicalMaintCatCodeList = maintRequestService.getHistoricalMaintCatCodes(fm.getVin(), maintCatCode, mrqId);
			if(historicalMaintCatCodeList.size() > 0){
				return true;
			}else{
				return false;
			}
		}catch(Exception ex){
			return false;
		}
	}

}