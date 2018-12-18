package com.mikealbert.service;

import javax.annotation.Resource;
import junit.framework.Assert;

import org.junit.Ignore;
import org.junit.Test;

import com.mikealbert.data.entity.VehicleRegistrationV;
import com.mikealbert.testing.BaseTest;
public class VehicleRegistrationServiceTest extends BaseTest {
	@Resource
	private VehicleRegistrationService vehicleRegistrationService;
	
	@Test
	public void testGetVehicleRegistration() {
		try{
			VehicleRegistrationV vehicleRegistration = vehicleRegistrationService.getVehicleRegistration(24113l);
			Assert.assertNotNull(vehicleRegistration);
			
		}catch(Exception ex){
			Assert.fail();
		}
	}
	
	@Ignore
	@Test
	public void testGetVehicleRegistrationPDF() {
		try{
			String retVal = vehicleRegistrationService.getVehicleRegistrationPDF(1072090l);
			Assert.assertNotNull(retVal);
			
		}catch(Exception ex){
			Assert.fail();
		}
	}
}
