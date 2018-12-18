package com.mikealbert.service;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import com.mikealbert.testing.BaseTest;
import com.mikealbert.data.TestQueryConstants;
import com.mikealbert.data.entity.FleetMaster;
import com.mikealbert.data.vo.AccessoryVO;
import com.mikealbert.data.vo.StockUnitsLovVO;
import com.mikealbert.exception.MalBusinessException;

public class FleetMasterServiceTest extends BaseTest{
	
	
	// this should be an older unit; so we don't need to dynamically look it up
	@Value("${driver.unallocatedUnit}")  String unallocatedUnit;
	
	@Value("${generic.unitNumber}")  String unitNo;	 
	
	private String allocatedUnit;
	private Long allocatedDriver;
	
    @Resource FleetMasterService fleetMasterService;
    
	final PageRequest page = new PageRequest(0, 5); 
    
    @PostConstruct
    public void init(){
    	allocatedUnit =  (String)em.createNativeQuery(TestQueryConstants.READ_UNIT_NO_CURRENT_ALLOCATION).getSingleResult();
    	allocatedDriver = ((BigDecimal)em.createNativeQuery(TestQueryConstants.READ_DRV_ID_CURRENT_ALLOCATION).getSingleResult()).longValue();		
    }
    
	//@Test
	public void testGetFleetMasterFilterCurrentAllocaton(){ 	 	
		
		List<FleetMaster> fleetMasters;
	
		//Ensure that a FleetMaster is not returned when a driver is NOT actively allocated to the unit
		fleetMasters = fleetMasterService.getFleetMasterFilterCurrentAllocaton(unallocatedUnit,null);			
		assertTrue("Found Fleet Master where a driver is not actively allocated to its unit", fleetMasters.size() == 0);			
		
		//Ensure that a FleetMaster is returned when a driver is actively allocated to the unit
		fleetMasters = fleetMasterService.getFleetMasterFilterCurrentAllocaton(allocatedUnit,null);	
		assertTrue("Could not find a Fleet Master where a driver is actively allocated to its unit " + fleetMasters.size(), fleetMasters.size() > 0);	
		
		fleetMasters = null;	
		fleetMasters = fleetMasterService.getFleetMasterFilterCurrentAllocaton(null, allocatedDriver);
		assertTrue("Could not find a the Fleet Masters where driver is allocated. drvId " + allocatedDriver, fleetMasters.size() > 0);	
		
	}
	
    
    //@Test
    public void testGetFleetMastersByVINLastSix(){
		final String VIN_LAST_SIX = "111111"; 	
		PageRequest page = null;
		
		List<FleetMaster> fleetMasters = fleetMasterService.findFleetMasterByVinLastSix(VIN_LAST_SIX, true, page);
		assertTrue("No Fleet Master returned for the Last Six VIN query", fleetMasters.size()>0);
	}
    
    @Test
    public void testGetScheduleLatestOdoReading() throws MalBusinessException{
    	Long odoReading = fleetMasterService.getScheduleLatestOdoReading(1050774L);
    	System.out.println(odoReading);
    }
    
    @Test
    public void testFindStockUnitsList() {
    	final String UNIT_NO = null;
    	final String VEHICLE_DESC = null;
    	final PageRequest PAGE = new PageRequest(0, 500);
    	final Sort SORT = null;
		List<StockUnitsLovVO> stock;
		int count = 0;
		
    	stock = fleetMasterService.findStockUnitsList(UNIT_NO, VEHICLE_DESC, PAGE, SORT);    	
		assertTrue("Stock units were not found", stock.size() > 0);
		System.out.println("Stock size = " + stock.size());		
		
		count = fleetMasterService.findStockUnitsListCount(UNIT_NO, VEHICLE_DESC);
		assertTrue("Stock unit count is incorrect", count == stock.size());				
		System.out.println("Stock count = " + count);    	
    }
    
	@Test
	public void testGetDealerAccessoriesByFmsId() {
		final Long FMS_ID = 1014674L;
		
		List<AccessoryVO> accessories = fleetMasterService.getDealerAccessories(FMS_ID);
		assertTrue("Could not find dealer accessories", accessories.size() > 0);
	}
	
	@Test
	public void testGetOptinalAccessoriesByFmsId() { 
		final Long FMS_ID = 1014674L;
		
		List<AccessoryVO> accessories = fleetMasterService.getOptionalAccessories(FMS_ID);
		assertTrue("Could not find optional accessories", accessories.size() > 0);		
	}

	@Test
	public void testGetStandardAccessoriesByFmsId() { 
		final Long FMS_ID = 1014674L;
		
		List<AccessoryVO> accessories = fleetMasterService.getStandardAccessories(FMS_ID);
		assertTrue("Could not find standard accessories", accessories.size() > 0);		
	}    
    
}
