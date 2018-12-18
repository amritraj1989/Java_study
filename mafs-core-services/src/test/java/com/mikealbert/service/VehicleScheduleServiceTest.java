package com.mikealbert.service;

import static org.junit.Assert.*;
import javax.annotation.Resource;
import junit.framework.Assert;
import org.junit.Ignore;
import org.junit.Test;

import com.mikealbert.data.dao.FleetMasterDAO;
import com.mikealbert.data.dao.MasterScheduleDAO;
import com.mikealbert.data.entity.VehicleScheduleInterval;
import com.mikealbert.service.vo.MaintenanceScheduleIntervalPrintVO;
import com.mikealbert.testing.BaseTest;
import com.mikealbert.util.MALUtilities;
//@TransactionConfiguration(defaultRollback = false)
public class VehicleScheduleServiceTest extends BaseTest {
	@Resource
	private VehicleScheduleService vehicleScheduleService;
	@Resource
	private	MasterScheduleDAO	masterScheduleDAO;
	@Resource
	private	MaintenanceScheduleService	maintenanceScheduleService;
	@Resource
	private	FleetMasterDAO	fleetMasterDAO;

	
	@Test
	public void testCreateVehicleScheduleNoSchedule() {
		try{
			/*
			MasterSchedule masterSchedule = masterScheduleDAO.findOne(6L);
			FleetMaster fleetMaster = fleetMasterDAO.findOne(getVehicleNoSchedule());
			VehicleSchedule schedule = maintenanceScheduleService.createVehicleSchedule(masterSchedule, fleetMaster);
			System.out.println("Schedule Id:"+schedule.getVschId());
			VehicleScheduleInterval interval = new VehicleScheduleInterval();
			interval.setAuthorizationNo(vehicleScheduleService.getAuthorizationNumber(schedule.getVehSchSeq(), "A"));
			interval.setVehicleSchedule(schedule);
			List<VehicleScheduleInterval> list = new ArrayList<VehicleScheduleInterval>();
			list.add(interval);			
			VehicleScheduleInterval interval1 = new VehicleScheduleInterval();
			interval1.setAuthorizationNo(vehicleScheduleService.getAuthorizationNumber(schedule.getVehSchSeq(), "AB"));
			interval1.setVehicleSchedule(schedule);
			List<VehicleScheduleInterval> list1 = new ArrayList<VehicleScheduleInterval>();
			list1.add(interval1);
			vehicleScheduleService.saveVehicleSchedule(schedule, list);
			*/
			
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	/*
	@Test
	public void testCreateVehicleScheduleHasSchedule() {
		
		MasterSchedule masterSchedule = masterScheduleDAO.findOne(6L);
		FleetMaster fleetMaster = fleetMasterDAO.findOne(getVehicleHasSchedule());
		VehicleSchedule schedule = maintenanceScheduleService.createVehicleSchedule(masterSchedule, fleetMaster);
		System.out.println("Schedule Id:"+schedule.getVschId());
		VehicleScheduleInterval interval = new VehicleScheduleInterval();
		interval.setAuthorizationNo(vehicleScheduleService.getAuthorizationNumber(schedule.getVehSchSeq(), "A"));
		interval.setVehicleSchedule(schedule);
		List<VehicleScheduleInterval> list = new ArrayList<VehicleScheduleInterval>();
		list.add(interval);			
		VehicleScheduleInterval interval1 = new VehicleScheduleInterval();
		interval1.setAuthorizationNo(vehicleScheduleService.getAuthorizationNumber(schedule.getVehSchSeq(), "AB"));
		interval1.setVehicleSchedule(schedule);
		List<VehicleScheduleInterval> list1 = new ArrayList<VehicleScheduleInterval>();
		list1.add(interval1);
		try{			
			vehicleScheduleService.saveVehicleSchedule(schedule, list);
		}catch(Exception ex){
			Assert.assertEquals("Vehicle already has an active maintenance schedule", ex.getMessage());
		}
	}
	
	private Long getVehicleHasSchedule() {
		
		Long fmsId = 0L;	   
		
		try {
			
			DataSource dataSource = (DataSource) SpringAppContext.getBean("dataSource");
		    Connection connection = dataSource.getConnection();
		    connection.setAutoCommit(false);		    		     
	    
			PreparedStatement selectStatement = connection
				    .prepareStatement(" Select fm.fms_id"
					    + " FROM  Fleet_Masters fm  "
					    + " WHERE EXISTS (SELECT fms_id "
					    + " FROM Vehicle_Schedules)");
	
			    ResultSet rs = selectStatement.executeQuery();
			    
			    while(rs.next())
			    	fmsId = rs.getLong(1);	
			    
			} catch (SQLException e) {
			    e.printStackTrace();
			}
		return fmsId;		
	}
	
	
	private Long getVehicleNoSchedule() {
		
		Long fmsId = 0L;	   
		
		try {
			
			DataSource dataSource = (DataSource) SpringAppContext.getBean("dataSource");
		    Connection connection = dataSource.getConnection();
		    connection.setAutoCommit(false);		    		     
	    
			PreparedStatement selectStatement = connection
				    .prepareStatement(" Select fm.fms_id"
					    + " FROM  Fleet_Masters fm  "
					    + " WHERE NOT EXISTS (SELECT fms_id "
					    + " FROM Vehicle_Schedules)");
	
			    ResultSet rs = selectStatement.executeQuery();
			    
			    while(rs.next())
			    	fmsId = rs.getLong(1);	
			    
			} catch (SQLException e) {
			    e.printStackTrace();
			}
		return fmsId;		
	}
	*/
	
	@Test 
	public void testIsValidAuthorizationNumber(){
		boolean retVal;
		
		retVal = vehicleScheduleService.isValidAuthorizationNumber("MAFSA10183");
		
		Assert.assertTrue(retVal);
		
		retVal = vehicleScheduleService.isValidAuthorizationNumber("MAFSAA10183");
		
		Assert.assertTrue(retVal);
		
		retVal = vehicleScheduleService.isValidAuthorizationNumber("MAFSBA10183");
		
		Assert.assertFalse(retVal);
		
		retVal = vehicleScheduleService.isValidAuthorizationNumber("MAFS00968738");
		
		Assert.assertFalse(retVal);
		
	}
	
	@Test
	public void testGetIntervalIndex() {
		assertEquals(vehicleScheduleService.getIntervalIndex("MAFSA00000"), 1);
		assertEquals(vehicleScheduleService.getIntervalIndex("MAFSC00000"), 2);
		assertEquals(vehicleScheduleService.getIntervalIndex("MAFSY00000"), 19);
		assertEquals(vehicleScheduleService.getIntervalIndex("MAFSAA00000"), 20);
		assertEquals(vehicleScheduleService.getIntervalIndex("MAFSAC00000"), 21);
		assertEquals(vehicleScheduleService.getIntervalIndex("MAFSAV00000"), 35);
		assertEquals(vehicleScheduleService.getIntervalIndex("MAFSAY00000"), 38);
		assertEquals(vehicleScheduleService.getIntervalIndex("MAFSCA00000"), 39);
		assertEquals(vehicleScheduleService.getIntervalIndex("MAFSCC00000"), 40);
		assertEquals(vehicleScheduleService.getIntervalIndex("MAFSCY00000"), 57);
		assertEquals(vehicleScheduleService.getIntervalIndex("MAFSEY00000"), 76);
		assertEquals(vehicleScheduleService.getIntervalIndex("MAFSFF00000"), 80);
		assertEquals(vehicleScheduleService.getIntervalIndex("MAFSFY00000"), 95);
		
	
	}
	
//  What is the point of these tests??	
//	@Test
//	public void testGetLastUsedVehSchInterval(){
//		VehicleScheduleInterval retVal = vehicleScheduleService.getLastUsedVehSchInterval(1067792L);
//		if (!MALUtilities.isEmpty(retVal))
//			System.out.println(retVal.getAuthorizationNo());
//	}
//	@Test
//	public void testGetNextIntervalAndMileage(){
//		MaintenanceScheduleIntervalPrintVO retVal = vehicleScheduleService.getNextIntervalAndMileage(1067792L);
//		if (!MALUtilities.isEmpty(retVal))
//			System.out.println(retVal.getAuthorizationNumber());
//	}
//	@Test
//	public void testGetNextIntervalAndMileageByOdo(){
//		MaintenanceScheduleIntervalPrintVO retVal = vehicleScheduleService.getNextIntervalAndMileage(1067792L, 154566);
//		if (!MALUtilities.isEmpty(retVal))
//			System.out.println(retVal.getAuthorizationNumber());
//	}
//	@Test
//	public void testGetPreviousIntervalAndMileageByOdo(){
//		MaintenanceScheduleIntervalPrintVO retVal = vehicleScheduleService.getPreviousIntervalAndMileage(1067792L, 140566);
//		if (!MALUtilities.isEmpty(retVal))
//			System.out.println(retVal.getAuthorizationNumber());
//	}
//	@Test
//	public void testGetIndexIntervalAndMileage(){
//		MaintenanceScheduleIntervalPrintVO retVal = vehicleScheduleService.getIndexIntervalAndMileage(1047503L, 2);
//		if (!MALUtilities.isEmpty(retVal))
//			System.out.println(retVal.getAuthorizationNumber());
//	}
	
	@Test
	public void testGetLastUsedVehSchInterval(){
		VehicleScheduleInterval retVal = vehicleScheduleService.getLastUsedVehSchInterval(1067792L);
		
		if (!MALUtilities.isEmpty(retVal))
			System.out.println(retVal.getAuthorizationNo());
	}
	
}
