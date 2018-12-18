package com.mikealbert.service;

import static org.junit.Assert.*;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;
import com.mikealbert.testing.BaseTest;
import com.mikealbert.common.MalConstants;
import com.mikealbert.data.entity.Driver;
import com.mikealbert.data.entity.DriverAllocation;
import com.mikealbert.data.entity.DriverRelationship;
import com.mikealbert.exception.MalBusinessException;

public class DriverRelationshipServiceTest extends BaseTest {
	@Resource DriverRelationshipService driverRelationshipService;
	@Resource CustomerAccountService accountService;
	@Resource DriverService driverService;
	@Resource LookupCacheService lookupCacheService;
	
	@Value("${generic.externalAccount.parentAccount}") String parentAccount;
	@Value("${generic.externalAccount.childAccount}") String childAccount;
	@Value("${generic.externalAccount.siblingAccount}") String siblingAccount;	
	@Value("${driverRelationship.parent.drvId}") Long PRIMARY_DRV_ID;
	@Value("${driverRelationship.child.drvId}") Long RELATED_DRV_ID;		
			
	Driver primaryDriver;
	Driver relatedDriver;
	
	List<DriverRelationship> driverRelationships;
	List<Driver> availableDrivers;
	
	@Before
	public void init(){
		primaryDriver = driverService.getDriver(PRIMARY_DRV_ID);
		relatedDriver = driverService.getDriver(RELATED_DRV_ID);
		availableDrivers = driverRelationshipService.getAvailableDrivers(primaryDriver, "Ca%");
	}
	
	/**
	 * Verify that when given a primary driver's id that a list
	 * of secondary/related drivers is returned. Related drivers
	 * should not have related drivers.
	 */
	@Test
    public void testGetRelatedDrivers(){
		List<Driver> drivers;
		
		drivers = driverRelationshipService.getRelatedDrivers(PRIMARY_DRV_ID);
		assertTrue("Failed to find related drivers", drivers.size() > 0);		
		
		drivers.clear();
		drivers = driverRelationshipService.getRelatedDrivers(RELATED_DRV_ID);
		assertTrue("Found secondary drivers linked to a related driver", drivers.size() == 0);
	}
	
	/**
	 * Verify that the available driver's list does not contain drivers 
	 * that are related or allocated to a unit.
	 */
	@Test
	public void testGetAvailableDrivers(){		
		for(Driver driver : availableDrivers) {			
			for(DriverAllocation allocation : driver.getDriverAllocationList()){
				assertTrue("Found allocated driver in unrelated and unallocated driver list", allocation.getDeallocationDate() != null);
			}
			assertTrue("Found related driver in unrealated and unallocated driver list " + driver.getDrvId(), !driverRelationshipService.isRelatedDriver(driver.getDrvId()));			
		}			
	}
	
	/**
	 * Verify that the available driver's list does not contain any Inactive drivers
	 */
	@Test
	public void testGetActiveDriversOnlyInAvailableDriversList() {
		for (Driver driver : availableDrivers) {
			assertTrue("Found InActive driver in available driver list",driver.getActiveInd().equals(MalConstants.FLAG_Y));

		}
	}
	
	/**
	 * Verifies that the primary driver's relationships can be retrieved.
	 */	
	@Test
	public void testGetDriverRelationships(){	
		driverRelationships = driverRelationshipService.getDriverRelationships(PRIMARY_DRV_ID);	
	    assertTrue(driverRelationships.size() > 0);
	}
	
	/**
	 * Verifies that a primary driver is detected as being a primary driver. 
	 * Also, verifies that a related driver is not detected as a primary driver.
	 */
	@Test
	public void testIsParentDriver(){	
	    assertTrue("Primary driver was not detected as primary", driverRelationshipService.isParentDriver(PRIMARY_DRV_ID));
	    assertFalse("Related driver was detected as a primary driver", driverRelationshipService.isParentDriver(RELATED_DRV_ID));
	}
	
	/**
	 * Verifies that a related driver is detected as being a related driver. 
	 * Also, verifies that a primary driver is not detected as a related driver.
	 */
	@Test
	public void testIsRelatedDriver(){	
	    assertTrue("Related driver was not detected as related", driverRelationshipService.isRelatedDriver(RELATED_DRV_ID));
	    assertFalse("Primary driver was detected as related", driverRelationshipService.isRelatedDriver(PRIMARY_DRV_ID));
	}
	
	/**
	 * Verifies adding a driver relationship can be done by following a successful 
	 * path, i.e. passing all validations.
	 * @throws MalBusinessException
	 */
	@Test
	public void testSaveOrUpdateRelatedDrivers() throws MalBusinessException{	
		List<DriverRelationship> relationships = genDriverRelationship(1);
		relationships = driverRelationshipService.saveOrUpdateRelatedDrivers(primaryDriver, relationships);
		
		assertTrue("Driver relationship did not save", relationships.get(0).getRdvId() != null);
        assertTrue("Saved driver relationship type is not incorrect", 
        		relationships.get(0).getRelationshipType().equals(lookupCacheService.getRelationshipTypes().get(0).getCode()));				
	}
	
	/**
	 * Ensures that a relationship type is assigned to each of the relationships
	 * @throws MalBusinessException
	 */
	@Test(expected=MalBusinessException.class)	
	public void testSaveOrUpdateRelatedDriversNoRelationshipType() throws MalBusinessException{
		List<DriverRelationship> relationships = genDriverRelationship(2);
		relationships.get(1).setRelationshipType(null);
		relationships = driverRelationshipService.saveOrUpdateRelatedDrivers(primaryDriver, relationships);		
	}
	
	/**
	 * Generates a list of driver relationships based on the primary driver.
	 * @param size The amount of relationships to generate
	 * @return A list of driver relationships
	 */
	private List<DriverRelationship> genDriverRelationship(int size){
		List<DriverRelationship> relationships = new ArrayList<DriverRelationship>();		
		DriverRelationship relationship;
		
		for(int i = 0; i < size; i++){
			relationship = new DriverRelationship();
			relationship.setPrimaryDriver(primaryDriver);
			relationship.setSecondaryDriver(availableDrivers.get(i));
			relationship.setRelationshipType(lookupCacheService.getRelationshipTypes().get(0).getCode());
			relationships.add(relationship);			
		}		
		
		return relationships;
	}
}
