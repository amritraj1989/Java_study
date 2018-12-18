package com.mikealbert.service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mikealbert.data.dao.DriverAllocationDAO;
import com.mikealbert.data.dao.DriverDAO;
import com.mikealbert.data.dao.DriverGradeDAO;
import com.mikealbert.data.dao.DriverRelationshipDAO;
import com.mikealbert.data.dao.ExternalAccountDAO;
import com.mikealbert.data.entity.Driver;
import com.mikealbert.data.entity.DriverAllocation;
import com.mikealbert.data.entity.DriverGrade;
import com.mikealbert.data.entity.DriverRelationship;
import com.mikealbert.data.entity.ExternalAccount;
import com.mikealbert.data.enumeration.CorporateEntity;
import com.mikealbert.common.MalConstants;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.exception.MalException;

/**
 * Implementation of {@link com.mikealbert.vision.service.DriverRelationshipService}
 */
@Service("driverRelationshipService")
@Transactional
public class DriverRelationshipServiceImpl implements DriverRelationshipService {
	@Resource DriverRelationshipDAO driverRelationshipDAO;
	@Resource DriverDAO driverDAO;
	@Resource DriverAllocationDAO driverAllocationDAO;
	@Resource CustomerAccountService customerAccountService;
	
	/**
	 * Retrieves a list of the provided driver parameter's DriverRelationship objects; Used by Driver Relationship screen
	 * @param drvId Parent Driver
	 * @return List of Driver Relationships for provided driver
	 */
	//TODO This should be a list of secondary relationships. Need to rename.
	public List<DriverRelationship> getDriverRelationships(Long drvId){
		try{
			return driverRelationshipDAO.findByParentDriverDrvId(drvId);
		} catch (Exception ex) {
			throw new MalException("generic.error.occured.while", 
					new String[] { "finding a driver''s relationships" }, ex);				
		}
	}
	
	/**
	 * Retrieves a list of drivers that are related to the passed in driver,
	 * ex: a list of secondary drivers;  Used by Driver Add Edit when updating related drivers and when checking if related 
	 * driver has same account as parent driver;
	 * @param drvId Parent driver
	 * @return List of Drivers for provided driver
	 */
	public List<Driver> getRelatedDrivers(Long drvId){
		List<DriverRelationship> relationships;
		List<Driver> drivers = new ArrayList<Driver>();
		
		relationships = driverRelationshipDAO.findByParentDriverDrvId(drvId);
		for(DriverRelationship relationship : relationships){
			drivers.add(relationship.getSecondaryDriver());
		}
		return drivers;
	}
	
	/**
	 * Retrieves a list of parent drivers (typically one) of the passed in driver,
	 * ex: a list of parent drivers;  Used by Driver Add Edit when determining whether a driver has inactive parents (to lock down active indicator)
	 * @param drvId Child driver
	 * @return List of Drivers for provided driver
	 */
	public List<Driver> getParentDrivers(Long childDrvId){
		List<DriverRelationship> relationships;
		List<Driver> drivers = new ArrayList<Driver>();
		
		relationships = driverRelationshipDAO.findByRelatedDriverDrvId(childDrvId);
		for(DriverRelationship relationship : relationships){
			if(!drivers.contains(relationship.getPrimaryDriver())){
				drivers.add(relationship.getPrimaryDriver());
			}
		}
		return drivers;
	}
	
	/**
	 * Retrieves a list of drivers where the following is true: <br>
	 * 1) The driver has an account that is the same as primary driver's account or account related to primary driver's account <br>
	 * 2) The driver has a last name equal to the last name provided as a parameter <br>
	 * Wildcard is added to the end of the last name if one has not been specified <br>
	 * Used by Driver Relationships to retrieve only possible related drivers
	 * @param primaryDriver Primary Driver
	 * @param lastNameFilter Used to find possible related drivers by last name
	 * @return List of possible related drivers for the provided primary driver
	 */
	public List<Driver> getAvailableDrivers(Driver primaryDriver, String lastNameFilter){
		List<ExternalAccount> relatedAccounts = new ArrayList<ExternalAccount>();		
		List<Driver> availableDrivers = null;
		List<String> relatedAccountCodes = new ArrayList<String>();
		String lastName = lastNameFilter;
		try{		
			//Accounts related to the primary driver's account, including he primary driver's account.
			relatedAccounts.addAll(customerAccountService.getRelatedAccounts(primaryDriver.getExternalAccount().getExternalAccountPK().getAccountCode()));
			
			//List of the account codes asigned to the related accounts
			for(ExternalAccount account : relatedAccounts){
				relatedAccountCodes.add(account.getExternalAccountPK().getAccountCode());
			}
			
			//Adding wildcard when one has not been specified.
			if(!lastName.endsWith("%") && !lastName.matches("\\%")){
				lastName += "%";
			}
		
			availableDrivers = driverDAO.findDriversByLastNameWithoutAllocationAndRelationship(
					ExternalAccountDAO.ACC_STATUS_OPEN,
					CorporateEntity.MAL.getCorpId(), 
					lastName.toLowerCase(), 
					MalConstants.FLAG_Y,
					relatedAccountCodes, 
					new ArrayList<String>() {{add(ExternalAccountDAO.ACCOUNT_TYPE_CUSTOMER);}}					 
					);	
			
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new MalException("generic.error.occured.while", 
					new String[] { "finding a dirver's relationships" }, ex);				
		}	
		
		return availableDrivers;
			
	}

	
	/**
	 * Validates whether provided driver is a parent driver; Used by Driver Add Edit when checking if related driver has same
	 * account as parent
	 * @param drvId Parent driver
	 * @return True if provided driver is a parent driver
	 */
	public boolean isParentDriver(Long drvId){	
		try{
			return driverRelationshipDAO.findByParentDriverDrvId(drvId).size() == 0 ? false : true;
		} catch (Exception ex) {
			throw new MalException("generic.error.occured.while", 
					new String[] { "determining wether the driver is a primary driver. drvId = " + drvId }, ex);				
		}		
	}
	
	/**
	 * Validates whether provided driver is a related driver; Used when saving driver relationships to enforce validations.
	 * @param drvId Related driver
	 * @return True if provided driver is a related driver
	 */
	public boolean isRelatedDriver(Long drvId){
		List<DriverRelationship> myParentRelationships;
		try{
			myParentRelationships = driverRelationshipDAO.findByRelatedDriverDrvId(drvId);
			return myParentRelationships == null || myParentRelationships.size() == 0 ? false : true;
		} catch (Exception ex) {
			throw new MalException("generic.error.occured.while", 
					new String[] { "determining wether the driver is a realtive of a primary driver. drvId = " + drvId }, ex);				
		}
	}
	

	/**
	 * This procedure will save new, updated, or removed relations to the provided driver; Used by Related Driver screen.
	 * @param driver Parent driver
	 * @param driverRelationships List of relations to parent driver
	 * @return List of relations to parent driver
	 */
	public List<DriverRelationship> saveOrUpdateRelatedDrivers(Driver driver, List<DriverRelationship> driverRelationships) throws MalBusinessException{
		List<DriverRelationship> persistedRelationships = new ArrayList<DriverRelationship>();
		try {
			validateRelationships(driver, driverRelationships);
			
			//Delete relationships that were removed.
			for(DriverRelationship relationship : this.removedRelationships(driver, driverRelationships))
				driverRelationshipDAO.delete(relationship);
			
			//Persist new or updated relationships
			for(DriverRelationship relationship : driverRelationships)				
				persistedRelationships.add(driverRelationshipDAO.saveAndFlush(relationship));
			
		} catch (MalBusinessException mbe) {
			throw mbe; 
		}catch(Exception ex) {
			ex.printStackTrace();
			throw new MalException("generic.error.occured.while", 
					new String[] { "saving related driver(s)" }, ex);			
		}

		return persistedRelationships;
	}
	

	/**
	 * Validates the passed in driver relationships and raises a MalBusiness exception when any of the 
	 * validations fail; Used when saving driver relationships to enforce validations
	 * @param driver Primary Driver
	 * @param driverRelationships Related drivers to the primary driver
	 */
	public void validateRelationships(Driver driver, List<DriverRelationship> driverRelationships) throws MalBusinessException{
		ArrayList<String> messages = new ArrayList<String>();
		
		for(DriverRelationship relationship : driverRelationships){
			if(relationship.getRelationshipType() == null)
				messages.add("A relationship type is required for driver " + relationship.getSecondaryDriver().getDriverSurname() 
						+ ", " + relationship.getSecondaryDriver().getDriverForename());
			if(relationship.getPrimaryDriver() == null)
			    messages.add("Primary driver is required");
			if(relationship.getSecondaryDriver() == null)
			    messages.add("Related driver is required");			
			if(!driver.equals(relationship.getPrimaryDriver()) && this.isRelatedDriver(relationship.getPrimaryDriver().getDrvId())) 
				messages.add("Driver " + relationship.getPrimaryDriver().getDriverSurname() + ", " 
						+ relationship.getPrimaryDriver().getDriverForename() + " is already related to another driver");
			if(!driver.equals(relationship.getPrimaryDriver()) && this.isRelatedDriver(relationship.getSecondaryDriver().getDrvId()))		
				messages.add("Driver " + relationship.getSecondaryDriver().getDriverSurname() + ", " 
						+ relationship.getSecondaryDriver().getDriverForename() + " is already related to another driver");				
		}
		
		if(messages.size() > 0)
			throw new MalBusinessException("service.validation", messages.toArray(new String[messages.size()]));					
	}
	
	/**
	 * Compares the driver's relationships stored in the database with those in the updated list to determine 
	 * the relationships that were removed; Used when saving driver relationships to remove relationships that have
	 * no longer exist to the primary driver
	 * @param driver Primary Driver
	 * @param driverRelationships List of related drivers
	 */
	private List<DriverRelationship> removedRelationships(Driver driver, List<DriverRelationship> driverRelationships){
		List<DriverRelationship> removedRelationships = new ArrayList<DriverRelationship>();
		removedRelationships.addAll(driverRelationshipDAO.findByParentDriverDrvId(driver.getDrvId()));
		removedRelationships.removeAll(driverRelationships);		
		return removedRelationships;
	}
}
