package com.mikealbert.service;

import static org.junit.Assert.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import javax.annotation.Resource;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;
import com.mikealbert.testing.BaseTest;
import com.mikealbert.data.dao.FleetMasterDAO;
import com.mikealbert.data.entity.ContractLine;
import com.mikealbert.data.entity.Driver;
import com.mikealbert.data.entity.DriverAllocation;
import com.mikealbert.data.entity.FleetMaster;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.service.CustomerAccountService;
import com.mikealbert.service.DriverGradeService;
import com.mikealbert.service.DriverService;

public class DriverAllocationServiceTest extends BaseTest {

	@Resource DriverAllocationService driverAllocationService;
	@Resource CustomerAccountService externalAccountService;
	@Resource DriverGradeService driverGradeService;
	@Resource FleetMasterDAO fleetMasterDAO;
	@Resource ContractService contractService;
	@Resource FleetMasterService fleetMasterService;
	@Resource DriverService driverService;

	@Value("${generic.old.fmsId}")  long oldFmsId;
	
	final Long FMS_ID = 989374L;  //Unit No 00953625
	final long OLD_DRV_ID = 124357; 
	final long NEW_RELATED_DRV_ID = 152787; 
	final long NEW_UNRELATED_DRV_ID = 116685; 
	final long ODO_READING = 5L;
	final String ODO_UOM = "MILE";
	final String OP_CODE = "SIBLEY_W";
	
	FleetMaster fleetMaster;
	SimpleDateFormat sdf;
	Driver oldDriver;
	Driver newRelatedDriver;
	Driver newUnRelatedDriver;
	
	@Before
	public void init(){
		fleetMaster = fleetMasterService.getFleetMasterByFmsId(oldFmsId);
		oldDriver = fleetMaster.getDriverAllocationList().get(0).getDriver();
		newRelatedDriver = driverService.getDriver(NEW_RELATED_DRV_ID); ;
		newUnRelatedDriver = driverService.getDriver(NEW_UNRELATED_DRV_ID); ;
		sdf = new SimpleDateFormat("yyyy-MM-dd");
	}
	
	@Ignore
	@Test
	public void testGetUnitAllocations(){
		assertTrue("Could not find driver allocations for fmsId: " + FMS_ID, driverAllocationService.getUnitAllocations(fleetMaster).size() > 0);
	}
	
	/**
	 * Verifies the end state of successfully deallocating an existing driver and allocating a new driver to a unit.
	 */
	@Ignore
	public void testSaveOrUpdateDriverAllocation() throws MalBusinessException{		
		Calendar calendar = Calendar.getInstance();		
		DriverAllocation oldDriverAllocation;
		DriverAllocation newDriverAllocation = new DriverAllocation();
		List<DriverAllocation> driverAllocations = new ArrayList<DriverAllocation>();
		ContractLine contractLine;

		//Deallocate old driver
		calendar.add(Calendar.DATE, -1);		
    	oldDriverAllocation = oldDriver.getDriverAllocationList().get(0);   	
        oldDriverAllocation.setDeallocationDate(calendar.getTime());
        
        //Allocate new driver
        calendar.add(Calendar.DATE, 1);             	
    	newDriverAllocation.setDriver(newRelatedDriver);
    	newDriverAllocation.setFleetMaster(fleetMaster);
    	newDriverAllocation.setFuelIndFlag("N");
    	newDriverAllocation.setFromOdoReading(ODO_READING);
    	newDriverAllocation.setOdoUom(ODO_UOM);
    	newDriverAllocation.setOpCode(OP_CODE);
    	newDriverAllocation.setAllocationDate(calendar.getTime());
    	    	
    	driverAllocations = driverAllocationService.saveAndUpdateDriversAllocation(oldDriverAllocation, newDriverAllocation);
    	 	
    	//Verify the deallocation and allocation dates for the old and new drivers, respectively
    	calendar.add(Calendar.DATE, -1);
    	assertTrue("Old Driver was not deallocated from unit: " + fleetMaster.getFmsId(), sdf.format(driverAllocations.get(0).getDeallocationDate()).equals(sdf.format(calendar.getTime())));
    	calendar.add(Calendar.DATE, 1);    	
    	assertTrue("New Driver was not allocated to unit: " + fleetMaster.getFmsId(), sdf.format(driverAllocations.get(1).getAllocationDate()).equals(sdf.format(calendar.getTime())));
    	
    	//Verify that the new driver is now on the contract
    	contractLine = contractService.getLastActiveContractLine(fleetMaster, Calendar.getInstance().getTime());
    	assertTrue("New driver was not added to the contract ", driverAllocations.get(1).getDriver().equals(driverAllocations.get(0).getFleetMaster().getContractLine().getDriver()));
    	
	}	
	
	/**
	 * Driver from unrelated client cannot be allocated to a unit.
	 * @throws MalBusinessException
	 */
	@Ignore
	@Test(expected=MalBusinessException.class)	
	public void testUnrealtedDriverAccounts() throws MalBusinessException{	
		Calendar calendar = Calendar.getInstance();		
		DriverAllocation oldDriverAllocation;
		DriverAllocation newDriverAllocation = new DriverAllocation();
		
		//Deallocate old driver
		calendar.add(Calendar.DATE, -1);			
    	oldDriverAllocation = oldDriver.getDriverAllocationList().get(0);   	
        oldDriverAllocation.setDeallocationDate(calendar.getTime());
        
        //Allocate new driver
    	calendar.add(Calendar.DATE, 1);   	     	
    	newDriverAllocation.setDriver(newUnRelatedDriver);
    	newDriverAllocation.setFleetMaster(fleetMaster);
    	newDriverAllocation.setFuelIndFlag("N");
    	newDriverAllocation.setFromOdoReading(ODO_READING);
    	newDriverAllocation.setOdoUom(ODO_UOM);
    	newDriverAllocation.setOpCode(OP_CODE);
    	newDriverAllocation.setAllocationDate(calendar.getTime()); 
    	
    	driverAllocationService.saveAndUpdateDriversAllocation(oldDriverAllocation, newDriverAllocation); 	
	}

	/**
	 * Driver dealloction date must be greater than their allocation date
	 * @throws MalBusinessException
	 */
	@Ignore
	@Test(expected=MalBusinessException.class)	
	public void testBackDateAllocation() throws MalBusinessException {
		Calendar calendar = Calendar.getInstance();		
		DriverAllocation oldDriverAllocation;
		DriverAllocation newDriverAllocation = new DriverAllocation();	
		
		//Deallocate old driver with the day before yesterday's date 
		calendar.add(Calendar.DATE, -2);		
    	oldDriverAllocation = oldDriver.getDriverAllocationList().get(0);   	
        oldDriverAllocation.setDeallocationDate(calendar.getTime());
        
        //Allocate new driver with yesterday's date
    	calendar.add(Calendar.DATE, -1);   	  	
    	newDriverAllocation.setDriver(newRelatedDriver);
    	newDriverAllocation.setFleetMaster(fleetMaster);
    	newDriverAllocation.setFuelIndFlag("N");
    	newDriverAllocation.setFromOdoReading(ODO_READING);
    	newDriverAllocation.setOdoUom(ODO_UOM);
    	newDriverAllocation.setOpCode(OP_CODE);
    	newDriverAllocation.setAllocationDate(calendar.getTime()); 
    	
    	driverAllocationService.saveAndUpdateDriversAllocation(oldDriverAllocation, newDriverAllocation); 			
	}
	
	/**
	 * Driver allocation cannot be forward dated
	 * @throws MalBusinessException
	 */	
	@Ignore
	@Test(expected=MalBusinessException.class)	
	public void testForwardDatingAllocation() throws MalBusinessException {
		Calendar calendar = Calendar.getInstance();		
		DriverAllocation oldDriverAllocation;
		DriverAllocation newDriverAllocation = new DriverAllocation();	
		
		//Deallocate old driver with today's date		
    	oldDriverAllocation = oldDriver.getDriverAllocationList().get(0);   	
        oldDriverAllocation.setDeallocationDate(calendar.getTime());
        
        //Allocate new driver with tomorrow's date
    	calendar.add(Calendar.DATE, 1);   	    	
    	newDriverAllocation.setDriver(newRelatedDriver);
    	newDriverAllocation.setFleetMaster(fleetMaster);
    	newDriverAllocation.setFuelIndFlag("N");
    	newDriverAllocation.setFromOdoReading(ODO_READING);
    	newDriverAllocation.setOdoUom(ODO_UOM);
    	newDriverAllocation.setOpCode(OP_CODE);
    	newDriverAllocation.setAllocationDate(calendar.getTime()); 
    	
    	driverAllocationService.saveAndUpdateDriversAllocation(oldDriverAllocation, newDriverAllocation); 		
	}
	
	/**
	 * Driver deallocation date is required and must be greater than the allocation date.
	 * @throws MalBusinessException
	 */	
	@Ignore
	@Test(expected=MalBusinessException.class)	
	public void testDeallocationDateGreaterThanAllocationDate() throws MalBusinessException {
		Calendar calendar = Calendar.getInstance();		
		DriverAllocation oldDriverAllocation;
		DriverAllocation newDriverAllocation = new DriverAllocation();	
		
		//Deallocate old driver with today's date	
		calendar.add(Calendar.DATE, -3650);   		
    	oldDriverAllocation = oldDriver.getDriverAllocationList().get(0);   	
        oldDriverAllocation.setDeallocationDate(calendar.getTime());
        
        //Allocate new driver with tomorrow's date
        calendar.add(Calendar.DATE, 3650);       	
    	newDriverAllocation.setDriver(newRelatedDriver);
    	newDriverAllocation.setFleetMaster(fleetMaster);
    	newDriverAllocation.setFuelIndFlag("N");
    	newDriverAllocation.setFromOdoReading(ODO_READING);
    	newDriverAllocation.setOdoUom(ODO_UOM);
    	newDriverAllocation.setOpCode(OP_CODE);
    	newDriverAllocation.setAllocationDate(calendar.getTime()); 

    	driverAllocationService.saveAndUpdateDriversAllocation(oldDriverAllocation, newDriverAllocation);

	}	

	/**
	 * Unit is ODO reading is required. 
	 * @throws MalBusinessException
	 */	
	@Ignore
	@Test(expected=MalBusinessException.class)	
	public void testRequiredODOReading() throws MalBusinessException {
		Calendar calendar = Calendar.getInstance();		
		DriverAllocation oldDriverAllocation;
		DriverAllocation newDriverAllocation = new DriverAllocation();	
		
		//Deallocate old driver with today's date	
		calendar.add(Calendar.DATE, -1);   		
    	oldDriverAllocation = oldDriver.getDriverAllocationList().get(0);   	
        oldDriverAllocation.setDeallocationDate(calendar.getTime());
        
        //Allocate new driver with tomorrow's date
		calendar.add(Calendar.DATE, 1);      	
    	newDriverAllocation.setDriver(newRelatedDriver);
    	newDriverAllocation.setFleetMaster(fleetMaster);
    	newDriverAllocation.setFuelIndFlag("N");
    	newDriverAllocation.setOdoUom(ODO_UOM);
    	newDriverAllocation.setOpCode(OP_CODE);
    	newDriverAllocation.setAllocationDate(calendar.getTime()); 

    	driverAllocationService.saveAndUpdateDriversAllocation(oldDriverAllocation, newDriverAllocation);
	}
	
	@Ignore
	@Test
	public void testGetAllAllocationsForUnit(){
		final int SIZE = 3;  // number of allocations for the old fms id
		assertEquals("Incorrect number of allocations found", driverAllocationService.getUnitAllocations(oldFmsId).size(), SIZE);
	}
	
	@Test
	public void testGetCurrentAllocations(){
		final int SIZE = 6; 
		assertEquals("Incorrect number of allocations found", driverAllocationService.getCurrentDriverAllocationVOs(187086L).size(), SIZE);
	}	

	
	@Test
	public void testGetPreviousAllocations(){
		final int SIZE = 54;  
		assertEquals("Incorrect number of allocations found", driverAllocationService.getPreviousDriverAllocationVOs(186272L).size(), SIZE);
	}	
}
