package com.mikealbert.vision.view.bean;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Value;

import com.mikealbert.service.CostCenterService;
import com.mikealbert.service.DriverService;
import com.mikealbert.util.SpringAppContext;
import com.mikealbert.vision.view.ViewConstants;



public class DriverOverviewBeanTest extends BeanTestCaseSetup{
	

	
	@Resource DriverService driverService;
	@Resource CostCenterService costCenterService;
	
	@Value("${driverOverviewBean.driverId}")  long driverId;
	
	DriverOverviewBean spyDriverOverviewBean = null;

	@Before
	public void setup() {
			
		Map<String, Object> pageInitParam = new HashMap<String, Object>();
		pageInitParam.put(ViewConstants.VIEW_PARAM_DRIVER_ID, driverId);		
		setupPageContract(pageInitParam);
		clearFaceMessages();
		DriverOverviewBean driverOverviewBean = (DriverOverviewBean) SpringAppContext.getBean(DriverOverviewBean.class);		
		spyDriverOverviewBean = Mockito.spy(driverOverviewBean);
		Mockito.doReturn(getUser()).when(spyDriverOverviewBean).getLoggedInUser();
	}	
	
	@Test
	public void testAllocations() {

		assertEquals(spyDriverOverviewBean.getAllocations().size(), 4);
		
		Date topAllocationStart = spyDriverOverviewBean.getAllocations().get(0).getAllocationDate();
		Date prevAllocationStart = spyDriverOverviewBean.getAllocations().get(1).getAllocationDate();
		assertTrue(topAllocationStart.after(prevAllocationStart) );
	}
	
	@Test
	public void testAddresses() {

		assertEquals(spyDriverOverviewBean.getGaragedAddresses().size(), 5);

		Date topAddressStart = spyDriverOverviewBean.getGaragedAddresses().get(0).getFromDate();
		Date prevAddressStart = spyDriverOverviewBean.getGaragedAddresses().get(1).getFromDate();
		assertTrue(topAddressStart.after(prevAddressStart) );
	}

	@Test
	public void testCostCenters() {

		assertEquals(spyDriverOverviewBean.getCostCenters().size(), 4);

		Date topCostCenterStart = spyDriverOverviewBean.getCostCenters().get(0).getDriverCostCenter().getDateAllocated();
		Date prevCostCenterStart = spyDriverOverviewBean.getCostCenters().get(1).getDriverCostCenter().getDateAllocated();
		assertTrue(topCostCenterStart.after(prevCostCenterStart) );
	}

}
