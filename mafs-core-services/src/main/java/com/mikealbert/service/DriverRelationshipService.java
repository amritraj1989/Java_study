package com.mikealbert.service;

import java.util.List;

import com.mikealbert.data.entity.Driver;
import com.mikealbert.data.entity.DriverRelationship;
import com.mikealbert.data.entity.ExternalAccount;
import com.mikealbert.data.enumeration.CorporateEntity;
import com.mikealbert.exception.MalBusinessException;
/**
* Public Interface implemented by {@link com.mikealbert.vision.service.DriverRelationshipServiceImpl} for interacting with business service methods concerning {@link com.mikealbert.data.entity.DriverRelationship}(s) .
*
* @see com.mikealbert.data.entity.DriverRelationship
 * @see com.mikealbert.vision.service.DriverRelationshipServiceImpl
*/
public interface DriverRelationshipService {
	public List<DriverRelationship> getDriverRelationships(Long id);	
	public List<Driver> getRelatedDrivers(Long drvId);
	public List<Driver> getParentDrivers(Long childDrvId);
	public List<Driver> getAvailableDrivers(Driver primaryDriver, String lastNameFilter);
    public boolean isParentDriver(Long drvId);	
	public boolean isRelatedDriver(Long drvId);
	public void validateRelationships(Driver driver, List<DriverRelationship> driverRelationships) throws MalBusinessException;
	public List<DriverRelationship> saveOrUpdateRelatedDrivers(Driver driver, List<DriverRelationship> driverRelationships)throws MalBusinessException;
}
