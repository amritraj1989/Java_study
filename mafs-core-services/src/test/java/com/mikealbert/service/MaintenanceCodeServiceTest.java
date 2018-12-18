package com.mikealbert.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.data.domain.PageRequest;

import com.mikealbert.data.dao.MaintenanceCodeDAO;
import com.mikealbert.data.dao.ServiceProviderMaintenanceCodeDAO;
import com.mikealbert.data.entity.MaintenanceCode;
import com.mikealbert.data.entity.ServiceProviderMaintenanceCode;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.testing.BaseTest;


public class MaintenanceCodeServiceTest extends BaseTest{

	@Resource MaintenanceCodeService maintenanceCodeService;
	@Resource MaintenanceCodeDAO maintenanceCodeDAO;
	@Resource ServiceProviderMaintenanceCodeDAO serviceProviderMaintenanceCodeDAO;	
	
	@Test
	public void testGetMaintenanceCodesByCode(){
		List<MaintenanceCode> codes = maintenanceCodeService.getMaintenanceCodesByNameOrCode("100-101");
		assertTrue(codes.size() > 0);
	}
	
	@Test
	public void testGetMaintenanceCodesByDesc(){
		List<MaintenanceCode> codes = maintenanceCodeService.getMaintenanceCodesByNameOrCode("Wind");
		assertTrue(codes.size() > 0);
		assertTrue(true);
	}
	
	
	@Test
	public void testFindMaintenanceCodesByDesc(){
		PageRequest page1 = new PageRequest(0,10);
		List<MaintenanceCode> codes = maintenanceCodeService.findMaintenanceCodesByNameOrCode("Wind",page1);
		assertTrue(codes.size() == 10);
		long count = maintenanceCodeService.getMaintenanceCodeCountByNameOrCode("Wind");
		assertTrue(count > 10);
	}
	
	@Test
	public void testGetExactCodeForAmmendment(){
		MaintenanceCode code = maintenanceCodeService.getExactMaintenanceCodeByNameOrCode(MaintenanceCodeService.AMMENDMENT);
		assertEquals(code.getCode(),MaintenanceCodeService.AMMENDMENT);
	}
	
	@Test
	public void testGetServiceProviderMaintenanceByMafsCode(){
		System.out.println("Provider Code, Starting first lookup - " + System.currentTimeMillis());
		List<ServiceProviderMaintenanceCode> codes = maintenanceCodeService.getServiceProviderMaintenanceByMafsCode("200-101",(List)new ArrayList<Long>(Arrays.asList(122994L)), true);
		System.out.println("Provider Code, Ending first lookup - " + System.currentTimeMillis());
		
		System.out.println("Provider Code, Starting cached lookup - " + System.currentTimeMillis());
		codes = maintenanceCodeService.getServiceProviderMaintenanceByMafsCode("200-101",(List)new ArrayList<Long>(Arrays.asList(122994L)), true);
		System.out.println("Provider Code, Ending cached lookup - " + System.currentTimeMillis());
		
		assertTrue(codes.size() > 0);
	}
	
	@Ignore
	@Test
	public void testSaveServiceProviderMainteneceCode() throws MalBusinessException{
				
		ServiceProviderMaintenanceCode serviceProviderMaintenanceCode = serviceProviderMaintenanceCodeDAO.findById(2L).orElse(null);
	
		serviceProviderMaintenanceCode.setCode("RC-04-805");
		serviceProviderMaintenanceCode.setDescription("Oil drain gasket");
		MaintenanceCode maintenanceCode = maintenanceCodeService.getExactMaintenanceCodeByNameOrCode("100-103");		
		serviceProviderMaintenanceCode.setMaintenanceCode(maintenanceCode);
				
		serviceProviderMaintenanceCode.setApprovedBy("AUTO");
		serviceProviderMaintenanceCode.setApprovedDate(new Date());
		serviceProviderMaintenanceCode.setDiscountFlag("Y");
		maintenanceCodeService.saveServiceProviderMaintCode(serviceProviderMaintenanceCode);
	
		ServiceProviderMaintenanceCode savedServiceProviderMaintenanceCode = serviceProviderMaintenanceCodeDAO.findById(2L).orElse(null);
		Assert.assertTrue("AUTO".equals(savedServiceProviderMaintenanceCode.getApprovedBy()));
		Assert.assertNotNull(savedServiceProviderMaintenanceCode.getApprovedDate());
	}
	
	@Test
	public void testSaveMaintenanceCode() throws MalBusinessException{
		MaintenanceCode mainCode = maintenanceCodeDAO.findById(11510l).orElse(null);
		mainCode.setMcoId(11510l);
		mainCode.setCode("100-209");
		mainCode.setDescription("Diesel particle filter");
		mainCode.setMaintCatCode("MISC_MAINT");
		
		
		maintenanceCodeService.saveMaintenanceCode(mainCode);
		MaintenanceCode savedMaintenanceCode = maintenanceCodeDAO.findById(11510l).orElse(null);
		
		Assert.assertTrue("100-209".equals(savedMaintenanceCode.getCode()));
		Assert.assertNotNull(savedMaintenanceCode);

	}
	
	}
