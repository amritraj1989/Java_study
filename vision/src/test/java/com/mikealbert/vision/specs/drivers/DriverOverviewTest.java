package com.mikealbert.vision.specs.drivers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.data.domain.Sort;

import com.mikealbert.data.dao.DriverAllocationDAO;
import com.mikealbert.data.entity.Driver;
import com.mikealbert.data.entity.DriverAddressHistory;
import com.mikealbert.data.entity.DriverAllocation;
import com.mikealbert.data.entity.DriverCostCenter;
import com.mikealbert.data.entity.FleetMaster;
import com.mikealbert.data.vo.DriverSearchVO;
import com.mikealbert.service.DriverAllocationService;
import com.mikealbert.service.DriverService;
import com.mikealbert.service.FleetMasterService;
import com.mikealbert.testing.BaseSpec;
import com.mikealbert.vision.util.AddressStartDateComparator;
import com.mikealbert.vision.util.CostCenterAllocatedDateComparator;

public class DriverOverviewTest extends BaseSpec{
	
	@Resource DriverAllocationDAO driverAllocationDAO;
	@Resource DriverAllocationService driverAllocationService;
	@Resource DriverService driverService;
	@Resource FleetMasterService fleetMasterService;

	public List<FleetMaster> testUnitsAllocatedToDriver(String driverName, String custNo){
		Sort sort = null;
		List<FleetMaster> fleetMasters = new ArrayList<FleetMaster>();
		
		try{
			List<DriverSearchVO> driverList = driverService.searchDriver(driverName, null, "", custNo, "", "", "", "", false, "Y", null, sort);
			List<DriverAllocation> driverAllocations = driverAllocationDAO.findByDrvId(driverList.get(0).getDrvId());
			for(DriverAllocation da : driverAllocations){
				fleetMasters.add(da.getFleetMaster());
			}
			
		}
		catch(Exception e){
			
		}
	    return fleetMasters;
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
	
	public List<DriverCostCenter> testFindDriverCostCenters(String driverName, String custNo){
		Sort sort = null;
		List<DriverCostCenter> driverCostCenters = new ArrayList<DriverCostCenter>();

		try{
			List<DriverSearchVO> driverList = driverService.searchDriver(driverName, null, "", custNo, "", "", "", "", false, "Y", null, sort);
			Driver driver = driverService.getDriver(driverList.get(0).getDrvId());
			driverCostCenters.addAll(driver.getDriverCostCenterList());
			Comparator<DriverCostCenter> costCenterAllocatedDateComparator = new CostCenterAllocatedDateComparator();
			Collections.sort(driverCostCenters, Collections.reverseOrder(costCenterAllocatedDateComparator));
		}catch(Exception e){
			
		}
		return driverCostCenters;
	}
}
