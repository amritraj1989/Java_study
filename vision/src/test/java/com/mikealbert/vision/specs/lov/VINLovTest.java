package com.mikealbert.vision.specs.lov;

import java.util.List;
import javax.annotation.Resource;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.exception.MalException;
import com.mikealbert.service.FleetMasterService;
import com.mikealbert.testing.BaseSpec;
import com.mikealbert.data.entity.FleetMaster;

public class VINLovTest extends BaseSpec {
	@Resource
	FleetMasterService fleetMasterService;

	public boolean testSearchVINByLastSix(String searchCriteria)
			throws MalBusinessException {

		try {
			List<FleetMaster> listFleetMaster = null;
			listFleetMaster = fleetMasterService.findFleetMasterByVinLastSix(searchCriteria, false, null);
			return listFleetMaster.size() > 0 ? true : false;
		} catch (MalException malEx) {
			return false;
		}
	}

}
