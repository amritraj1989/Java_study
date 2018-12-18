package com.mikealbert.vision.specs.drivers;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.*;

import com.mikealbert.data.entity.Driver;
import com.mikealbert.data.vo.DriverSearchVO;
import com.mikealbert.service.DriverRelationshipService;
import com.mikealbert.service.DriverService;
import com.mikealbert.testing.BaseSpec;


public class DriverRelationshipTest extends BaseSpec{
	@Resource DriverRelationshipService driverRelationshipService;
	@Resource DriverService driverService;
	
	/**
	 * Ensures available drivers do not consist of drivers that cannot be related
	 * @param driverNameParent 
	 * @param driverNameChild
	 * @return True if child is not available for relation to the parent
	 */
	public boolean testCannotRelateDriver(String driverNameParent, String driverNameChild){	
		List<DriverSearchVO> driverList = driverService.searchDriver(driverNameParent, null, "", "", "", "", "", "", false, "Y", null, null);
		Driver parent = driverService.getDriver(driverList.get(0).getDrvId()) ;
		driverList = driverService.searchDriver(driverNameChild, null, "", "", "", "", "", "", false, "Y", null, null);
		Driver child = driverService.getDriver(driverList.get(0).getDrvId()) ;
		
		//Available Drivers are drivers that can be related to the parent driver
		List<Driver> availableDrivers = driverRelationshipService.getAvailableDrivers(parent, "Ca%");
		for(Driver d : availableDrivers){
			if(d.getDrvId().equals(child.getDrvId())){
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Verify that when given a primary driver's id that a list
	 * of secondary/related drivers is returned. Related drivers
	 * should not have related drivers.
	 */
    public List<Driver> testGetRelatedDrivers(String driverNameParent, String customerNo){
		List<DriverSearchVO> driverList = driverService.searchDriver(driverNameParent, null, "", customerNo, "", "", "", "", false, "N", null, null);
		Driver parent = driverService.getDriver(driverList.get(0).getDrvId()) ;
		return driverRelationshipService.getRelatedDrivers(parent.getDrvId());		
	}
    
    /**
	 * Verify that the available driver's list does not contain any Inactive drivers
	 */
	public boolean testGetActiveDriversOnlyInAvailableDriversList(String parentDriverName, String possibleRelatedDriverLastName, String customerNo) {
		List<DriverSearchVO> driverList = new ArrayList<DriverSearchVO>();
		List<Driver> availableDrivers = new ArrayList<Driver>();
		
		//Retrieve parent driver
		driverList = driverService.searchDriver(parentDriverName, null, "", customerNo, "", "", "", "", false, "Y", null, null);
		
		//Retrieve the available related drivers for the parent driver
		availableDrivers = driverRelationshipService.getAvailableDrivers(driverService.getDriver(driverList.get(0).getDrvId()) , possibleRelatedDriverLastName);
		if(availableDrivers.size() == 0 ){
			return true;
		}else{
			return false;
		}
	}
	

}
