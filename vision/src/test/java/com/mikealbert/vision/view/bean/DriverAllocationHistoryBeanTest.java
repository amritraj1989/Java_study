package com.mikealbert.vision.view.bean;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import com.mikealbert.service.DriverService;
import com.mikealbert.util.SpringAppContext;



public class DriverAllocationHistoryBeanTest extends BeanTestCaseSetup{
	
	
	// This unit is no longer active so should be safe to use.  It had 3 allocations during its life.
	// The second allocation had a driver with 1 history address and 2 current addresses (GARAGED, POST) during the time frame
	// so the total size of the address list should be 3 which will include a GARAGED and a POST address.
	static long fmsId = 221540l;	
	
	@Resource DriverService driverService;
	DriverAllocationHistoryBean spyDriverAllocationHistoryBean = null;

	@Before
	public void setup() {
			
		Map<String, Object> pageInitParam = new HashMap<String, Object>();
		pageInitParam.put("FMS_ID", fmsId);		
		setupPageContract(pageInitParam);
		clearFaceMessages();
		DriverAllocationHistoryBean driverAllocationHistoryBean = (DriverAllocationHistoryBean) SpringAppContext.getBean(DriverAllocationHistoryBean.class);		
		spyDriverAllocationHistoryBean = Mockito.spy(driverAllocationHistoryBean);
		Mockito.doReturn(getUser()).when(spyDriverAllocationHistoryBean).getLoggedInUser();
	}	
	
	@Test
	public void testSetSelectedUnit() {

		long testFmsId = spyDriverAllocationHistoryBean.getSelectedUnit().getFmsId();
		assertEquals(testFmsId, fmsId);
	}
	
	@Test
	public void testAllocations() {

		int size = 3;
		assertEquals(spyDriverAllocationHistoryBean.getAllocationList().size(), size);
	}

	@Test
	public void testAddresses() {

		int size = 3;
		int allocationToGetIndex = 1;
		assertEquals(spyDriverAllocationHistoryBean.getAllocationList().get(allocationToGetIndex).getDriverAddressesVO().size(), size);
	}

}
