package com.mikealbert.vision.specs.lov;

import java.util.List;
import javax.annotation.Resource;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.exception.MalException;
import com.mikealbert.service.DriverService;
import com.mikealbert.testing.BaseSpec;
import com.mikealbert.data.vo.DriverLOVVO;

public class DriverLovTest extends BaseSpec {
	@Resource
	DriverService driverService;

	public boolean testSearchActiveDrvByLastName(String searchCriteria)
			throws MalBusinessException {

		try {
			List<DriverLOVVO> listDriver = null;
			listDriver = driverService.searchLOVDriver(searchCriteria, null, null, null, false, false, false, null);
			if (listDriver.size() > 0){
				for(DriverLOVVO driverLOVVO : listDriver){
					if (driverLOVVO.getActiveInd().equals("Y"))
						return true;
				}
			}
		return false;
		} catch (MalException malEx) {
			return false;
		}
	}
	
	public boolean testSearchInActiveDrvByLastName(String searchCriteria)
			throws MalBusinessException {

		try {
			List<DriverLOVVO> listDriver = null;
			listDriver = driverService.searchLOVDriver(searchCriteria, null, null, null, false, false, false, null);
			if (listDriver.size() > 0){
				for(DriverLOVVO driverLOVVO : listDriver){
					if (driverLOVVO.getActiveInd().equals("N"))
						return true;
				}
			}
		return false;
		} catch (MalException malEx) {
			return false;
		}
	}

}
