package com.mikealbert.vision.view.bean;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.mikealbert.common.MalLogger;
import com.mikealbert.common.MalLoggerFactory;
import com.mikealbert.data.dao.FleetMasterDAO;
import com.mikealbert.data.entity.FleetMaster;
import com.mikealbert.data.entity.MasterSchedule;
import com.mikealbert.data.entity.VehicleSchedule;
import com.mikealbert.service.MaintenanceScheduleService;


@Component
@Scope("view")
public class TestVSCreateBean extends StatefulBaseBean {
	
	private static final long serialVersionUID = 5459137983854538998L;

	MalLogger logger = MalLoggerFactory.getLogger(this.getClass());

	@Resource
	private	MaintenanceScheduleService	maintenanceScheduleService;

	@Resource
	private	FleetMasterDAO	fleetMasterDAO;

	
	
	private String masterScheduleName;
	private String unit;
	
	
	@PostConstruct
	public void init() {
		logger.debug("init is called");
		
		super.openPage();
		
	}

	@Override
	protected void loadNewPage() {
		thisPage.setPageDisplayName("Test");
		thisPage.setPageUrl("/view/maintSchedules/dashboard");

	}

	@Override
	protected void restoreOldPage() {}

	
	public void create() {
		
		FleetMaster fm = fleetMasterDAO.findByUnitNo(unit);
		MasterSchedule ms;
		
		if(maintenanceScheduleService.getMasterScheduleByCode(masterScheduleName).size() > 0) {
			ms = maintenanceScheduleService.getMasterScheduleByCode(masterScheduleName).get(0);
		} else {
			ms = null;
		}
		
		if(fm != null && ms != null) {
			try{
				VehicleSchedule vs = maintenanceScheduleService.createVehicleSchedule(ms, fm);
				addSuccessMessage("custom.message", "Created vehicle schedule id " + vs.getVschId() + " for unit " + unit);
			} catch (Exception e) {
				super.addErrorMessage("generic.error", e.getMessage());
			}
		} else {
			super.addErrorMessage("generic.error", "Bad input data");
		}
		
		

	}
	
	
	public String getMasterScheduleName() {
		return masterScheduleName;
	}

	public void setMasterScheduleName(String masterScheduleName) {
		this.masterScheduleName = masterScheduleName;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}





	
	



}