package com.mikealbert.vision.specs.drivers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import javax.annotation.Resource;

import com.mikealbert.data.vo.DriverSearchVO;
import com.mikealbert.service.DriverService;
import com.mikealbert.testing.BaseSpec;

public class DriverSearchTest extends BaseSpec{
	
	@Resource DriverService driverService;
	
	/**
	 * Ensures Driver data is returned when I search by First / Last name
	 */
	public boolean searchForDriversByDriverName_FuncTest(String driverName){ 
		List<DriverSearchVO> drivers = driverService.searchDriver(driverName, null, null, null, null, null, null, null, false, null, null, null);	
		long driversCnt = driverService.searchDriverCount(driverName, null, null, null, null, null, null, null, false, null);
		
		if(driversCnt > 0 && drivers.size() > 0){
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * Ensures Driver data is returned when I search by by Vehicle Vin number
	 */
	public boolean searchForDriversByVinNumber_FuncTest(String vin){ 
		List<DriverSearchVO> drivers = driverService.searchDriver(null, null, null, null, null, vin, null, null, true,null, null, null);	
		long driversCnt = driverService.searchDriverCount(null, null, null, null, null, vin, null, null, true,null);
		
		if(driversCnt > 0 && drivers.size() > 0){
			return true;
		}else{
			return false;
		}		
	}
	
	/**
	 * Ensures Driver data is returned when I search by Account code
	 */
	public boolean searchForDriversByClientNo_FuncTest(String customerAccount){ 
		List<DriverSearchVO> drivers = driverService.searchDriver(null, null, null, customerAccount, null, null, null, null, false, null, null, null);	
		long driversCnt = driverService.searchDriverCount(null, null, null, customerAccount, null, null, null, null, false, null);
		
		if(driversCnt > 0 && drivers.size() > 0){
			return true;
		}else{
			return false;
		}	
	}
	
	/**
	 * Ensures Driver data is returned when I search by license plate number in TAL
	 */
	public boolean searchForDriversByTALPlate_FuncTest(String talPlate){ 
		List<DriverSearchVO> drivers = driverService.searchDriver(null, null, null, null, null, null, null, talPlate, false, null, null, null);	
		long driversCnt = driverService.searchDriverCount(null, null, null, null, null, null, null, talPlate, false, null);
		
		if(driversCnt > 0 && drivers.size() > 0){
			return true;
		}else{
			return false;
		}	
	}
	
	/**
	 * Ensures Driver data is returned when I search by license plate (reg) number stored in Willow
	 */
	public boolean searchForDriversByWillowPlate_FuncTest(String willowPlate){ 
		List<DriverSearchVO> drivers = driverService.searchDriver(null, null, null, null, null, null, null, willowPlate, true,null, null, null);	
		long driversCnt = driverService.searchDriverCount(null, null, null, null, null, null, null, willowPlate, true,null);
		
		if(driversCnt > 0 && drivers.size() > 0){
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * Ensures Driver data is returned when I search by the fleet reference number stored with the quote.
	 */
	public boolean searchForDriversByFleetRef_FuncTest(String fleetRef){ 
		List<DriverSearchVO> drivers = driverService.searchDriver(null, null, null, null, null, null, fleetRef, null, true,null, null, null);	
		long driversCnt = driverService.searchDriverCount(null, null, null, null, null, null, fleetRef, null, true,null);
		
		if(driversCnt > 0 && drivers.size() > 0){
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * Ensures Driver data is returned when I search for an unallocated driver by name
	 */
	public boolean searchForUnallocatedDriversByName_FuncTest(String unallocatedDriverName){ 
		List<DriverSearchVO> drivers = driverService.searchDriver(unallocatedDriverName, null, null, null, null, null, null, null, false, null, null, null);	
		long driversCnt = driverService.searchDriverCount(unallocatedDriverName, null, null, null, null, null, null, null, false, null);
		
		if(driversCnt > 0 && drivers.size() > 0){
			return true;
		}else{
			return false;
		}		
	}
	
	/**
	 * Ensures Drivers can be found using the active indicator parameter
	 */
	public boolean searchForActiveInactiveBothByDriverName_FuncTest(String driverName, String activeInactiveBoth){ 
		List<DriverSearchVO> drivers = driverService.searchDriver(driverName, null, null, null, null, null, null, null, false, activeInactiveBoth, null, null);	
		long driversCnt = driverService.searchDriverCount(driverName, null, null, null, null, null, null, null, false, activeInactiveBoth);
		
		if(driversCnt > 0 && drivers.size() > 0){
			return true;
		}else{
			return false;
		}		
	}
	
	/**
	 * Ensures a disposed of unit can be found
	 */
	public boolean searchForDisposedUnit_FuncTest(String unitNo){ 
		List<DriverSearchVO> drivers = driverService.searchDriver(null, null, null, null, unitNo, null, null, null, true, null, null, null);	
		long driversCnt = driverService.searchDriverCount(null, null, null, null, unitNo, null, null, null, true, null);
		
		//Searching by unit number should only ever return one record
		if(driversCnt == 1 && drivers.size() == 1){
			return true;
		}else{
			return false;
		}		
	}

}
