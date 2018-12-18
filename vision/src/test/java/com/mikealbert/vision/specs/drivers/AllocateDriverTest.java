package com.mikealbert.vision.specs.drivers;

import java.util.Calendar;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Before;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;

import com.mikealbert.data.dao.FleetMasterDAO;
import com.mikealbert.data.entity.DriverAllocation;
import com.mikealbert.data.entity.FleetMaster;
import com.mikealbert.data.vo.DriverSearchVO;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.service.ContractService;
import com.mikealbert.service.CustomerAccountService;
import com.mikealbert.service.DriverAllocationService;
import com.mikealbert.service.DriverGradeService;
import com.mikealbert.service.DriverService;
import com.mikealbert.service.FleetMasterService;
import com.mikealbert.testing.BaseSpec;
import com.mikealbert.util.MALUtilities;

public class AllocateDriverTest extends BaseSpec{

	@Resource DriverAllocationService driverAllocationService;
	@Resource CustomerAccountService externalAccountService;
	@Resource DriverGradeService driverGradeService;
	@Resource FleetMasterDAO fleetMasterDAO;
	@Resource ContractService contractService;
	@Resource FleetMasterService fleetMasterService;
	@Resource DriverService driverService;

	@Value("${generic.old.fmsId}")  long oldFmsId;
	
	final String ODO_UOM = "MILE";
	final String OP_CODE = "SIBLEY_W";
	
	DriverAllocation oldDriverAllocation;
	DriverAllocation newDriverAllocation;
	
	@Before
	public void init(){
		
	}
	
	public boolean testGetUnitAllocations(Long fmsId){
		FleetMaster fm = fleetMasterService.getFleetMasterByFmsId(fmsId);
		if(driverAllocationService.getUnitAllocations(fm).size() > 0){
			return true;
		}
		else{
			return false;
		}
	}
	
	
	/**
	 * Verifies that an allocation/deallocation can occur
	 */
	
	public boolean testValidateDeallocationAllocation(String unitNo, String newDriverName, String odo ) throws MalBusinessException{
		Calendar calendar = Calendar.getInstance();	
		
		setupForAllocDealloc(unitNo, newDriverName);

		//Deallocate old driver
		calendar.add(Calendar.DATE, -1);	
        oldDriverAllocation.setDeallocationDate(calendar.getTime());
        
        //Allocate new driver
        calendar.add(Calendar.DATE, 1);   
    	newDriverAllocation.setFromOdoReading(MALUtilities.isEmpty(odo) ? null : Long.parseLong(odo));
    	newDriverAllocation.setAllocationDate(calendar.getTime());
    	    	
    	try{
    		driverAllocationService.validateDeallocationAllocation(oldDriverAllocation, newDriverAllocation);
    	}
    	catch(Exception ex){
    		return true;
    	}
    	return false;
    	
	}
	
	public boolean testValidateAllocationDates(String unitNo, String newDriverName, int year, int month, int day ) throws MalBusinessException{
		Calendar calendar = Calendar.getInstance();		
		
		setupForAllocDealloc(unitNo, newDriverName);
		calendar.add(Calendar.DATE, -1);		 	
        oldDriverAllocation.setDeallocationDate(calendar.getTime());
        
        //Allocate new driver
        calendar.set(year, month-1, day);  //Month-1 because months are 0-11 in 'calendar' and user friendly is 1-12
    	newDriverAllocation.setFromOdoReading(1000000L); //High odo reading so error does not occur on odo reading
    	newDriverAllocation.setAllocationDate(calendar.getTime());
    	    	
    	try{
    		driverAllocationService.validateDeallocationAllocation(oldDriverAllocation, newDriverAllocation);
    	}
    	catch(Exception ex){
    		return true;
    	}
    	return false;
    	
	}
	
	public boolean testValidateDeallocationDates(String unitNo, String newDriverName, int year, int month, int day ) throws MalBusinessException{
		Calendar calendar = Calendar.getInstance();		
		
		setupForAllocDealloc(unitNo, newDriverName);
		calendar.set(year, month-1, day);  //Month-1 because months are 0-11 in 'calendar' and user friendly is 1-12
        oldDriverAllocation.setDeallocationDate(calendar.getTime());
        
        //Allocate new driver
        calendar.setTime(Calendar.getInstance().getTime());
    	newDriverAllocation.setFromOdoReading(1000000L); //High odo reading so error does not occur on odo reading
    	newDriverAllocation.setAllocationDate(calendar.getTime());
    	    	
    	try{
    		driverAllocationService.validateDeallocationAllocation(oldDriverAllocation, newDriverAllocation);
    	}
    	catch(Exception ex){
    		return true;
    	}
    	return false;
    	
	}
	
	private void setupForAllocDealloc(String unitNo, String newDriverName){
		newDriverAllocation = new DriverAllocation();
		Sort sort = null;
		
		FleetMaster fm = fleetMasterService.findByUnitNo(unitNo);
		List<DriverAllocation> driverAllocList = driverAllocationService.getUnitAllocations(fm);
		oldDriverAllocation = driverAllocList.get(0); // get most recent allocation   	
		
		List<DriverSearchVO> driverList = driverService.searchDriver(newDriverName, null, "", "", "", "", "", "", false, "Y", null, sort);
		newDriverAllocation.setDriver(driverService.getDriver(driverList.get(0).getDrvId())) ;
    	newDriverAllocation.setFleetMaster(fm);
    	newDriverAllocation.setFuelIndFlag("N");
    	newDriverAllocation.setOdoUom(ODO_UOM);
    	newDriverAllocation.setOpCode(OP_CODE);
	}

}
