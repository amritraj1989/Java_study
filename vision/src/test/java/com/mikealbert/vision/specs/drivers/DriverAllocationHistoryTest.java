package com.mikealbert.vision.specs.drivers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.data.domain.Sort;

import com.mikealbert.data.dao.DriverAllocationDAO;
import com.mikealbert.data.entity.Driver;
import com.mikealbert.data.entity.DriverAddressHistory;
import com.mikealbert.data.entity.DriverAllocation;
import com.mikealbert.data.entity.FleetMaster;
import com.mikealbert.data.vo.DriverAddressVO;
import com.mikealbert.data.vo.DriverSearchVO;
import com.mikealbert.service.DriverAllocationService;
import com.mikealbert.service.DriverService;
import com.mikealbert.service.FleetMasterService;
import com.mikealbert.testing.BaseSpec;
import com.mikealbert.vision.util.AddressStartDateComparator;

public class DriverAllocationHistoryTest extends BaseSpec {
	@Resource DriverAllocationDAO driverAllocationDAO;
	@Resource DriverAllocationService driverAllocationService;
	@Resource DriverService driverService;
	@Resource FleetMasterService fleetMasterService;

	/**
	 * Finds all the drivers that have ever been allocated to the given unit
	 * @param unit Unit Number given
	 * @return Returns List of Drivers that have been allocated to the given unit
	 */
	public List<Driver> testFindDriverAllocatedByUnit(String unit){
		List<Driver> driverList = new ArrayList<Driver>();
		
		try{
			FleetMaster fleetMaster = fleetMasterService.findByUnitNo(unit);
			List<DriverAllocation> driverAllocations = driverAllocationService.getUnitAllocations(fleetMaster);
			for(DriverAllocation da: driverAllocations){
				driverList.add(driverService.getDriver(da.getDriver().getDrvId()));
			}
		}
		catch(Exception e){
			
		}
	    return driverList;
	}
	
	/**
	 * Finds all the current addresses and historical addresses for a driver
	 * @param driverName Name of driver in this format; Last, First
	 * @param custNo The customer's number that the driver works for
	 * @return Returns all the Garaged and Post addresses
	 */
	public List<DriverAddressHistory> testFindDriverAddresses(String driverName, String custNo){
		Sort sort = null;
		List<DriverAddressHistory> driverAddresses = new ArrayList<DriverAddressHistory>();

		try{
			List<DriverSearchVO> driverList = driverService.searchDriver(driverName, null, "", custNo, "", "", "", "", false, "Y", null, sort);
			driverAddresses = driverService.getDriverAddressesByType(driverList.get(0).getDrvId(), DriverService.GARAGED_ADDRESS_TYPE);
			driverAddresses.addAll(driverService.getDriverAddressesByType(driverList.get(0).getDrvId(), DriverService.POST_ADDRESS_TYPE));
			Collections.sort(driverAddresses, Collections.reverseOrder(new AddressStartDateComparator()));
		}catch(Exception e){
			
		}
		return driverAddresses;
	}
	
}
