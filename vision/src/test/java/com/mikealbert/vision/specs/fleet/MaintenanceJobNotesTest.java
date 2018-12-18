package com.mikealbert.vision.specs.fleet;

import java.util.List;

import javax.annotation.Resource;

import com.mikealbert.data.entity.FleetNotes;
import com.mikealbert.data.entity.MaintenanceRequest;
import com.mikealbert.service.MaintenanceRequestService;
import com.mikealbert.testing.BaseSpec;
import com.mikealbert.vision.service.VehicleMaintenanceService;

public class MaintenanceJobNotesTest extends BaseSpec{

	@Resource VehicleMaintenanceService vehicleMaintenanceService;
	@Resource MaintenanceRequestService maintenanceRequestService;
	
	public String jobNote;
	
	public boolean testJobNotesExist(String jobNo){
		MaintenanceRequest po = maintenanceRequestService.getMaintenanceRequestByJobNo(jobNo);
		List<FleetNotes> jobNotes;
		jobNote = null;
		jobNotes = vehicleMaintenanceService.getFleetNotesByMaintReqId(po.getMrqId());
		if(jobNotes != null && jobNotes.size() > 0){
			jobNote = jobNotes.get(0).getNote();
			return true;
		}else{
			return false;
		}
	}

	public String getJobNote() {
		return jobNote;
	}

	public void setJobNote(String jobNote) {
		this.jobNote = jobNote;
	}
	
	
}
