package com.mikealbert.service;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import junit.framework.Assert;

import org.junit.Test;

import com.mikealbert.data.dao.FleetMasterDAO;
import com.mikealbert.data.dao.MaintSchedulesProcessedDAO;
import com.mikealbert.data.dao.MasterScheduleDAO;
import com.mikealbert.data.entity.MaintSchedulesProcessed;
import com.mikealbert.data.entity.VehicleScheduleInterval;
import com.mikealbert.service.reporting.VehicleScheduleReportService;
import com.mikealbert.service.vo.MaintenanceScheduleIntervalPrintVO;
import com.mikealbert.testing.BaseTest;

public class VehicleScheduleReportServiceTest extends BaseTest {

	@Resource
	private VehicleScheduleReportService vehicleScheduleReportService;

	@Resource
	private MaintSchedulesProcessedDAO maintSchedulesProcessedDAO;

	
	
	@Test
	public void testCreateScheduleList() {

		MaintSchedulesProcessed msp = maintSchedulesProcessedDAO.findById(1061l).orElse(null);   
		
		List<MaintenanceScheduleIntervalPrintVO> intervalList = vehicleScheduleReportService.getFinalScheduleListWithHeader(msp);

		int i = 1;
		for(MaintenanceScheduleIntervalPrintVO vo : intervalList) {
			System.out.println("Line#: " + i + "==>" + vo.getAuthorizationNumber() + " | " + vo.getIntervalDescription() + 
					" | " + vo.getColumn1() + " | " + vo.getColumn2()+ " | " + vo.getColumn3() + " | " + vo.getColumn4());			
			i++;
		}
		

			
		assertTrue(true);

	}
	
}
