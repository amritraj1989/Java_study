package com.mikealbert.service;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;

import com.mikealbert.data.entity.FleetMaster;
import com.mikealbert.data.entity.Odometer;
import com.mikealbert.data.entity.OdometerReading;
import com.mikealbert.testing.BaseTest;

public class OdometerServiceTest extends BaseTest{
	
	@Value("${generic.opCode}")  String userId;
	@Value("${driver.allocatedUnit}")  String allocatedUnit;
	
	
	@Resource OdometerService odometerService;
	@Resource FleetMasterService fleetMasterService;
	
	final Long ODO_READING = 1000000L;
	final String STMT_IND = "N";
	
	FleetMaster fleetMaster;
	Odometer currentOdometer;

	@PostConstruct
	public void init(){
		// we are looking for a specific unit that we know have ODO readings (not really just any allocated unit) 
		List<FleetMaster> fleetMasterList = fleetMasterService.getFleetMasterFilterCurrentAllocaton(allocatedUnit,null);
		
		if(fleetMasterList != null && fleetMasterList.size() > 0){
			fleetMaster = fleetMasterList.get(0);
			currentOdometer = odometerService.getCurrentOdometer(fleetMaster);
		}
	}
	
	@Test
	public void testGetCurrentOdometer(){
		if(fleetMaster != null){
			assertNotNull("Could not find the current odometer for unit " + allocatedUnit, currentOdometer);
		}
	}
	
	
	@Test
	public void testsaveAndUpdateOdometerReading(){
		if(fleetMaster != null){
			OdometerReading newOdometerReading = odometerService.saveOdometerReading(fleetMaster, "ESTODO", ODO_READING, userId);		
			assertTrue("Failed to save odometer reading ", newOdometerReading.getOdorId() > 0);
		}
	}	
}
