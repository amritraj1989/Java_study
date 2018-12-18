package com.mikealbert.vision.entity;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.validation.ConstraintViolation;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.mikealbert.data.dao.ExternalAccountDAO;
import com.mikealbert.data.entity.Driver;
import com.mikealbert.data.entity.EntityTestCaseSetup;
import com.mikealbert.service.CustomerAccountService;
import com.mikealbert.service.DriverGradeService;
import com.mikealbert.service.DriverService;
import com.mikealbert.service.LookupCacheService;

@ContextConfiguration(locations={"classpath:applicationContextTest.xml"}) 
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class DriverTest extends EntityTestCaseSetup{
	
	@Value("${driver.unallocatedDriver.drvId}")  long drvId;
	@Value("${driver.unallocatedDriver.garagedAddress}")  String garagedAddress;
	@Value("${driver.hasCurrentCostCenter.drvId}")  long hasCurrentCostCenterDrvId;
	@Value("${driver.hasCurrentCostCenter.costCenter}")  String currentCostCenterCode;
	@Value("${driver.noCurrentCostCenter.drvId}")  long noCurrentCostCenterDrvId;
	
	@Resource DriverService driverService;
	@Resource CustomerAccountService externalAccountService;
	@Resource DriverGradeService driverGradeService;
	@Resource LookupCacheService lookupCacheService;
	
	//TODO Make account types an Enum
	final List<String> ACCOUNT_TYPES = Arrays.asList(ExternalAccountDAO.ACCOUNT_TYPE_CUSTOMER);	

	/**
	 * Accept. Criteria: Story 9 - New Driver Email
	 * The email address matches standard formatting
	 */
	@Test
	public void testInvalidEmailValidation(){
		Driver driver = new Driver();
		
		driver.setEmail("test@");
	    
	    Set<ConstraintViolation<Driver>> constraintViolations = validator.validateProperty(driver, "email");
	    //Checks that the email constraint has failed
		assertEquals(1, constraintViolations.size());
		for (ConstraintViolation<Driver> constraintViolation : constraintViolations) {
			//compares invalid email message to the constraint violation message
			assertEquals(DEFAULT_EMAIL_MESSAGE, constraintViolation.getMessage());
			assertEquals("email",constraintViolation.getPropertyPath().toString());
		}
	}
	
	/**
	 * Accept. Criteria: Story 9 - New Driver Email
	 * The email address matches standard formatting
	 */
	@Test
	public void testValidEmailValidation(){
		Driver driver = new Driver();
		
		driver.setEmail("test@test.com");
	    
	    Set<ConstraintViolation<Driver>> constraintViolations = validator.validateProperty(driver, "email");
	    //Checks that no validations have failed for the email property
		assertEquals(0, constraintViolations.size());
	}

	/**
	 * Returns the garaged address and verify it is populated for the driver under test
	 */
	@Test
	public void testReturnGaragedAddress(){
		
		assertEquals(driverService.getDriver(drvId).getGaragedAddress().getAddressLine1(), garagedAddress);
	}

	/**
	 * Return a list of cost centers (all) for a driver that has had one or more cost centre; verifies a list if returned
	 */
	@Test
	public void testGetCostCenterList(){

		Driver driverWithCostCenter = driverService.getDriver(hasCurrentCostCenterDrvId);
		assertTrue(driverWithCostCenter.getDriverCostCenterList().size() > 0);
	}

	/**
	 * Tests to make sure a current cost centre can be returned for the driver under test
	 */
	@Test
	public void testGetCurrentCostCenter(){

		Driver driverWithCostCenter = driverService.getDriver(hasCurrentCostCenterDrvId);
		assertEquals(driverWithCostCenter.getDriverCurrentCostCenter().getCostCenterCode(), currentCostCenterCode);
	}

	/**
	 * Tests to make sure no current cost centre is returned for a driver that does not have one
	 */
	@Test
	public void testGetCurrentCostCenterWithNoCostCenter(){
		
		Driver driverWithNoCostCenter = driverService.getDriver(noCurrentCostCenterDrvId);
		assertEquals(driverWithNoCostCenter.getDriverCurrentCostCenter(), null);
	}
	
}
