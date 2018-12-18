package com.mikealbert.service;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;

import com.mikealbert.testing.BaseTest;
import com.mikealbert.data.entity.Driver;
import com.mikealbert.data.entity.DriverAddress;
import com.mikealbert.service.AddressService;

public class AddressServiceTest extends BaseTest{
	@Resource AddressService addressService;
	@Resource DriverService driverService;

	@Test
	public void testGetAllCityZipCodesByZipCode(){
		final int SIZE = 16;
		final String ZIP = "45014";
			
        assertEquals(addressService.getAllCityZipCodesByZipCode(ZIP).size(), SIZE);
        }
//	
//	public String testGetAllCityZipCodesByZipCode(String zip){
//		return String.valueOf(addressService.getAllCityZipCodesByZipCode(zip).size());
//    }
	
	@Test
	public void testAddressListCompare(){
		Driver d = new Driver();
		d = driverService.getDriver(164418L);
		List<DriverAddress> daList = new ArrayList<DriverAddress>();
		//Compare driver's address list against a null address list
		assertFalse(addressService.addressListCompare(daList, d.getDrvId()));
		//Compare driver's address list with itself
		assertTrue(addressService.addressListCompare(d.getDriverAddressList(), d.getDrvId()));
	}

}
