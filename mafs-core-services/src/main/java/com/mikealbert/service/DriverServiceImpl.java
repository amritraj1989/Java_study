package com.mikealbert.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.annotation.Resource;

import org.hibernate.Hibernate;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mikealbert.common.MalConstants;
import com.mikealbert.data.dao.DriverAddressDAO;
import com.mikealbert.data.dao.DriverAddressHistoryDAO;
import com.mikealbert.data.dao.DriverCostCenterDAO;
import com.mikealbert.data.dao.DriverDAO;
import com.mikealbert.data.dao.ExternalAccountDAO;
import com.mikealbert.data.dao.PhoneNumberDAO;
import com.mikealbert.data.dao.VehicleStatusVDAO;
import com.mikealbert.data.entity.AddressTypeCode;
import com.mikealbert.data.entity.CostCentreCode;
import com.mikealbert.data.entity.CostCentreCodePK;
import com.mikealbert.data.entity.Driver;
import com.mikealbert.data.entity.DriverAddress;
import com.mikealbert.data.entity.DriverAddressHistory;
import com.mikealbert.data.entity.DriverAllocation;
import com.mikealbert.data.entity.DriverCostCenter;
import com.mikealbert.data.entity.DriverGrade;
import com.mikealbert.data.entity.ExternalAccount;
import com.mikealbert.data.entity.ExternalAccountPK;
import com.mikealbert.data.entity.FleetMaster;
import com.mikealbert.data.entity.PhoneNumber;
import com.mikealbert.data.enumeration.CorporateEntity;
import com.mikealbert.data.vo.DriverAddressVO;
import com.mikealbert.data.vo.DriverInfoVO;
import com.mikealbert.data.vo.DriverLOVVO;
import com.mikealbert.data.vo.DriverSearchVO;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.exception.MalException;
import com.mikealbert.util.MALUtilities;
//import com.mikealbert.vision.common.AppConstants;

/**
 * Implementation of {@link com.mikealbert.vision.service.DriverService}
 */
@Service("driverService")
public class DriverServiceImpl implements DriverService {
	
	@Resource private DriverDAO driverDAO;
	@Resource private LookupCacheService lookupCacheService;
	@Resource private DriverAddressDAO driverAddressDAO;
	@Resource private PhoneNumberDAO phoneNumberDAO;	
	@Resource private DriverAddressHistoryDAO driverAddressHistoryDAO;	
	@Resource private VehicleStatusVDAO vehicleStatusVDAO;	 
	@Resource private DriverCostCenterDAO driverCostCenterDAO;
	@Resource private CostCenterService costCenterService;
	@Resource private NotificationService notifyService;
	@Resource private DriverAllocationService  driverAllocationService;	
	
	/**
	 * Sets up the driver to be saved.
	 * @param externalAccount Account to assign to driver
	 * @param driverGrade Grade to assign to driver
	 * @param driver Driver to save
	 * @param addresses Addresses to assign to driver
	 * @param phoneNumbers Phone Numbers to assign to driver
	 * @param userName User that added/updated addresses
	 * @param driverCostCenter Cost Center to assign to driver
	 * @return Driver that has been saved
	 */
	
	@Transactional	
	public Driver saveOrUpdateDriver(ExternalAccount externalAccount, DriverGrade driverGrade, Driver driver, 
			List<DriverAddress> addresses, List<PhoneNumber> phoneNumbers, String userName, CostCentreCode driverCostCenter) throws MalBusinessException {
		
		List<PhoneNumber> removedPhoneNumbers;
		Driver persistedDriver = null;
		
		// used in conjunction with a check to see if the driver has a pending purchase order; if they do then we need to create a diary entry
		boolean existingAddressHasChanged = false;
		
		try {
			driver.setExternalAccount(externalAccount);
			driver.setDgdGradeCode(driverGrade);
			driver.setDriverAddressList(addresses);			
			driver.setPhoneNumbers(phoneNumbers);
							
			// validate the phone numbers and detect and raise any exception conditions
			validateDriverPhoneNumbers(driver);
			
			// apply cost centre changes
			applyCostCentreChanges(driver, driverCostCenter);

			// Set the default indicator for all driver addresses (assumes only one of each type). 
			for(DriverAddress driverAddress : driver.getDriverAddressList()){
				driverAddress.setDefaultInd("Y");
	    	    driverAddress.setDriver(driver);
		    	if(driverAddress.getDraId() == null){
		    	    driverAddress.setUserName(userName);
				}
			}						
			
			//Process changes made to an existing driver
			if(!MALUtilities.isEmpty(driver.getDrvId())){
				persistedDriver = driverDAO.findById(driver.getDrvId()).orElse(null);

				//check before InActivate driver
				if(driver.getActiveInd().equals(MalConstants.FLAG_N)){
					validateIfDriverCanInactived(driver.getDrvId());
				}
				//Delete removed phone numbers
				removedPhoneNumbers = new ArrayList<PhoneNumber>(persistedDriver.getPhoneNumbers());
				removedPhoneNumbers.removeAll(driver.getPhoneNumbers());				
				for(PhoneNumber removedPhoneNumber : removedPhoneNumbers){
					persistedDriver.getPhoneNumbers().remove(removedPhoneNumber);
				}
		         
				// check if an existing address has changed and set things up for deletion and logging change history
				existingAddressHasChanged = determineAndApplyAddressChanges(driver, persistedDriver, userName);
				
				// if the address has changed on an existing driver send a notification to purchasing if needed
				if(existingAddressHasChanged){
					notifyService.notifyPurchasingOfDriverAddressChgIfUnitOnOrder(driver.getDrvId(),userName);
				}
			}
			
			// update related drivers
			Driver savedDriver = null; 
			if(driver.getDrvId() != null  && persistedDriver.getChildRelationships().size() > 0 ){
				List<Driver> relatedDriversList = persistedDriver.getRelatedDrivers();
				List<Driver> updatableDriversList = new ArrayList<Driver>();
				String parentDriverGrade = driverGrade.getGradeCode();
				
				boolean cascadeActiveIndicatorChanges = this.hasActiveIndicatorChanged(driver);
				
				for (Driver relatedDriver : relatedDriversList) {
					if(! relatedDriver.getExternalAccount().equals(externalAccount))
						continue;
					
					String relatedDriverGrade = relatedDriver.getDgdGradeCode().getGradeCode();					
					DriverCostCenter relatedDriverCostCenter  = relatedDriver.getDriverCurrentCostCenter();
					CostCentreCode  relatedDriverCostCenterObj =  null;
					String  relatedDriverCostCenterCode =   relatedDriverCostCenter == null ? null : relatedDriverCostCenter.getCostCenterCode(); 
					
					if(relatedDriverCostCenterCode != null ){
						ExternalAccountPK externalAccountPK = externalAccount.getExternalAccountPK();
						CostCentreCodePK costCentreCodePK = new  CostCentreCodePK(relatedDriverCostCenterCode, externalAccountPK.getCId(),
																				externalAccountPK.getAccountType() ,externalAccountPK.getAccountCode());
						relatedDriverCostCenterObj =  new CostCentreCode();					
						relatedDriverCostCenterObj.setCostCentreCodesPK(costCentreCodePK);
					}
					boolean isCostCenterCodeAreSame =   isCostCenterCodeAreSame(driverCostCenter ,relatedDriverCostCenterObj);
					
					if(! ( relatedDriverGrade.equals(parentDriverGrade) && isCostCenterCodeAreSame && cascadeActiveIndicatorChanges == false  )){					
						
						if(!  relatedDriverGrade.equals(parentDriverGrade) ){
							relatedDriver.setDgdGradeCode(driverGrade);
						}
						if(! isCostCenterCodeAreSame ){						
							if(driverCostCenter == null &&  relatedDriverCostCenterCode != null){
								terminateCurrentCostCentre(relatedDriver);
								relatedDriver.setCostCentreCode(null);
								relatedDriver.setCostCentreAccountCorporateId(null);
								relatedDriver.setCostCentreAccountType(null);
								relatedDriver.setCostCentreAccountCode(null);
							}else{
								addNewCostCenterToDriver(relatedDriver, driverCostCenter);
							}
						}
						// has the driver's active indicator changed then we want to cascade those changes
						if(cascadeActiveIndicatorChanges){
							relatedDriver.setActiveInd(driver.getActiveInd());
						}
						
						updatableDriversList.add(relatedDriver);
					}
				}
				updatableDriversList.add(driver);
				updatableDriversList =  driverDAO.saveAll(updatableDriversList);
				driverDAO.flush();
				for (Driver updatedDriver : updatableDriversList) {
					if(updatedDriver.getDrvId().equals(driver.getDrvId())){
						savedDriver =updatedDriver;	break;
					}
				}
				
			}else{
				savedDriver = driverDAO.saveAndFlush(driver);
			}
			
			
			return savedDriver;
			
		} catch(DataIntegrityViolationException dvex) {
			throw new MalException("generic.error.occured.while", new String[] { "attempting to remove an existing address"}, dvex);
		} catch(MalBusinessException mbe) {
			throw mbe;			
		} catch (Exception ex) {
			throw new MalException("generic.error.occured.while", new String[] { "saving or updating a driver" }, ex);
		}		
	}
	
	/**
	 * Checks in the DB to see if the active indicator on screen is different that what has been saved
	 * @param driver (from UI)
	 * @return boolean indicating whether the active indicator has been changed on the UI
	 */
	
	@Transactional
	public boolean hasActiveIndicatorChanged(Driver driver) {
		boolean retVal = false;
		
		if(driver.getDrvId() != null){
			Driver peristedDriver = this.getDriver(driver.getDrvId());
			retVal = driver.getActiveInd().equalsIgnoreCase(peristedDriver.getActiveInd()) ? false : true;
		}
		return retVal;
	}
	
	//START - private methods to make saveOrUpdateDriver a little more readable / modular 
	/**
	 *  This method determines whether there are address changes 
	 *	and applies (handles removal so deletion happens correctly and captures change history) changes
	 * @param driver Contains driver info that has been updated on the screen.
	 * @param persistedDriver Contains driver info from database
	 * @param userName User that changed addresses
	 * @return True if their were changes otherwise false
	 */
	private boolean determineAndApplyAddressChanges(Driver driver, Driver persistedDriver, String userName){
		List<DriverAddress> removedAddresses;
		boolean existingAddressHasChanged = false;
		
		//Delete removed addresses				
		removedAddresses = new ArrayList<DriverAddress>(persistedDriver.getDriverAddressList());
		removedAddresses.removeAll(driver.getDriverAddressList());	
		for(DriverAddress removedAddress : removedAddresses){
			driverAddressDAO.delete(removedAddress);
		}
		
		//Store original/persisted address of the changed address into the history table
		for(DriverAddress address : driver.getDriverAddressList()){
			if(!MALUtilities.isEmpty(address.getDraId())){
				for(DriverAddress persistedAddress : persistedDriver.getDriverAddressList()){							
					if(address.equals(persistedAddress) && address.hashCode() != persistedAddress.hashCode()){
						// if the original address has changed, populate and save the address history object
						driverAddressHistoryDAO.saveAndFlush(populateAddressHistory(persistedAddress));		
						
						//The user and date need to be updated when driver address has changed.
						address.setUserName(userName);
						address.setInputDate(new Date());
						
						// also set a flag if an existing address was changed
						if(!existingAddressHasChanged){
							existingAddressHasChanged = true;
						}
					}
				}
			}					
		}
				
		return existingAddressHasChanged;
	}
	
	/**
	 * This method populates a history record off of an Original/Persisted address.
	 * @param persistedAddress Address to be added as history record
	 * @return Returns saved driverAddressHistory object
	 */
	private DriverAddressHistory populateAddressHistory(DriverAddress persistedAddress){
		DriverAddressHistory driverAddressHistory = new DriverAddressHistory();
		
		driverAddressHistory.setBusinessInd(persistedAddress.getBusinessInd());
		driverAddressHistory.setBusinessAddressLine(persistedAddress.getBusinessAddressLine());
		driverAddressHistory.setAddressLine1(persistedAddress.getAddressLine1());
		driverAddressHistory.setAddressLine2(persistedAddress.getAddressLine2());
		driverAddressHistory.setAddressType(persistedAddress.getAddressType());
		driverAddressHistory.setDefaultInd(persistedAddress.getDefaultInd());
		driverAddressHistory.setDriver(persistedAddress.getDriver());
		driverAddressHistory.setGeoCode(persistedAddress.getGeoCode());
		driverAddressHistory.setInputDate(new Date());
		driverAddressHistory.setPostcode(persistedAddress.getPostcode());
		driverAddressHistory.setStreetNo(persistedAddress.getStreetNo());
		driverAddressHistory.setTownCityCode(persistedAddress.getTownCityCode());
		driverAddressHistory.setUserName(persistedAddress.getUserName());
		
		return driverAddressHistory;
	}
	
	/**
	 * This method validates the phone numbers we are trying to save an raises any exceptions that are detected.
	 * @param driver Driver containing phone numbers
	 * @throws MalBusinessException
	 */
	private void validateDriverPhoneNumbers(Driver driver) throws MalBusinessException{
		int preferredNumberCount = 0;
		
		//Phone Number list should contain one preferred number, otherwise reject.
		if(driver.getPhoneNumbers() != null && driver.getPhoneNumbers().size() > 0){
			for(PhoneNumber phoneNumber : driver.getPhoneNumbers()) {
				for(PhoneNumber otherPhoneNumber : driver.getPhoneNumbers()){
					//if(phoneNumber.hashCode() != (otherPhoneNumber.hashCode()) && phoneNumber.getType().equals(otherPhoneNumber.getType()))
					if(phoneNumber != otherPhoneNumber && phoneNumber.getType().equals(otherPhoneNumber.getType()))						
						throw new MalBusinessException("service.validation", new String[]{"Unique phone number types are required"});							
				}
				if(phoneNumber.getPreferredInd().equals("Y"))
					preferredNumberCount += 1;
			}
			if(preferredNumberCount > 1)
				throw new MalBusinessException("service.validation", new String[]{"Driver can only have one preferred phone number"});
			if(preferredNumberCount < 1)
				throw new MalBusinessException("service.validation", new String[]{"Driver must have one preferred phone number"});				
		}
		else {
			throw new MalBusinessException("service.validation", new String[]{"Driver must have one preferred phone number"});
		}
	}
	
	/**
	 * This method applies Cost Centre changes against the primary Driver.<br>
	 * Used by:<br>
	 * 1) When saving/updating a driver cost center.
	 * @param driver Primary Driver
	 * @param driverCostCenter Cost Center to assign to Driver
	 * @throws MalBusinessException
	 */
	private void applyCostCentreChanges(Driver driver, CostCentreCode driverCostCenter) throws MalBusinessException{
		// check for a newly added or a removed cost center
		boolean addFlag = true;
		boolean removeFlag = false;
		
		if(driverCostCenter != null) {
			if(driver.getDriverCurrentCostCenter() != null) {
				addFlag = 
					driverCostCenter.getCostCentreCodesPK().getCostCentreCode().equals(driver.getDriverCurrentCostCenter().getCostCenterCode()) 
					? false : true;
			 

			}
			if(addFlag) {
				if(validCostCenter(driver, driverCostCenter)) {
					addNewCostCenterToDriver(driver, driverCostCenter);
				}
				else {
					throw new MalBusinessException("service.validation", new String[]{"Cost Center not valid for Driver"});
				}
			}
		}else{
			removeFlag = MALUtilities.isEmpty(driverCostCenter) ? true : false;
			
			// if we had a cost centre but now we don't; we need to terminate date the old one
			if(removeFlag){
				// implement the terminate date logic on the current one
				// and null out the entry on the driver table
				terminateCurrentCostCentre(driver);
				driver.setCostCentreCode(null);
				driver.setCostCentreAccountCorporateId(null);
				driver.setCostCentreAccountType(null);
				driver.setCostCentreAccountCode(null);
			}
		}
	}
	
	/* END - private methods to make saveOrUpdateDriver a little more readable / modular */


	/**
	 * Retrieves the driver only if the driver is active; Used by Driver Allocation and Driver Relationship 
	 * @param driverId Driver primary key
	 * @return Active Driver
	 */
	
	@Transactional
	public Driver getActiveDriver(Long driverId) {
		
		Driver driver = getDriver( driverId);
		if(driver.getActiveInd().equalsIgnoreCase(MalConstants.FLAG_Y))
			return driver;
		else
			return null;
	}
	
	
	/**
	 * Retrieves the driver; Used by Driver Add Edit, Driver Overview and Driver Search screens.
	 * @param driverId Driver primary key
	 * @return Driver
	 */
	
	@Transactional
	public Driver getDriver(Long driverId) {
		try {
			Driver driver = driverDAO.findById(driverId).orElse(null);
            Hibernate.initialize(driver.getPhoneNumbers());
			Hibernate.initialize(driver.getExternalAccount1());
			Hibernate.initialize(driver.getDriverCostCenterList());
			Hibernate.initialize(driver.getDriverAllocationList());
			//TODO: Driver Overview test is breaking; rolling back this change made for MF-1430
			// driver.setDriverAllocationList(driverAllocationService.getDriverAllocationsByDrvId(driver.getDrvId()));
			return driver;
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new MalException("generic.error.occured.while", new String[] { "finding a driver by driverId" }, ex);
		}
	}
	
	/**
	 * Retrieves the status of the provided driver; Used by Driver Add Edit 
	 * @param driverId Driver primary key
	 * @return String value representing the status of driver
	 */
	public String getDriverStatus(Long driverId){
		return driverDAO.getDriverStatus(driverId);
	}
	
	
	/** Retrieves an array that contains the last name and (optionally) the first name from a formatted 
	 * string with format of "LastName, FirstName"; Used as a helper method for some of the driver service
	 * methods;
	 *@param driverName with the format of "LastName, FirstName"
	 *@return Formatted array of driver first and last name
	 */
	public String[] splitDriverNameIntoLastAndFirst(String driverName){
		String[] lastFirstName = new String[2];
		
		if (!MALUtilities.isEmptyString(driverName)) {
			if (driverName.indexOf(",") < 0) {
				lastFirstName[0] = driverName;
			} else {
				String[] nameArr = driverName.split(",");
				lastFirstName[0] = nameArr[0];
				if (nameArr.length > 1) {
					lastFirstName[1] = nameArr[1];
					lastFirstName[1] = lastFirstName[1].trim();
				}
			}
		}
		
		return lastFirstName;
	}

	/**
	 * Sets up wildcards for a provided string:<br>
	 * 1) If the provided string is empty. Wildcard is set to '%'<br>
	 * 2) If the provided string ends with '%', a wildcard is not added to the end of the string <br>
	 * 3) If the provided string does not end with '%', a wildcard is added to the end of the string <br>
	 * Used as a helper method for some of the driver services
	 * @param searchTerm String to add wildcard
	 * @return String with wildcard
	 */
	private String setupWildCards(String searchTerm){
		if(MALUtilities.isEmptyString(searchTerm)){
			searchTerm = "%";
		}
		
		if(!searchTerm.endsWith("%") && !searchTerm.matches("\\%")){
			searchTerm = searchTerm + "%";
		}
		
		return searchTerm;
	}
	
	/**
	 * Retrieves available driver address type codes; Used by Driver Add Edit to set the list of possible address types
	 * @return List of address type codes
	 */
	
	public List<AddressTypeCode> getDriverAddressTypeCodes(){
		List<AddressTypeCode> driverAddressTypeCodes = new ArrayList<AddressTypeCode>();		
		try{			
			for(AddressTypeCode atc : lookupCacheService.getAddressTypeCodes()){
				if(atc.getAssignDrivers().equals("Y"))
					driverAddressTypeCodes.add(atc);
			}			
		} catch (Exception ex) {
			throw new MalException("generic.error.occured.while", new String[] { "finding driver address type codes" }, ex);			
		}
		return driverAddressTypeCodes;
	}

	/**
	 * Finds driver by Driver Name (format: "LastName, FirstName") for a specific open customer account code and corporate entity
	 * 
	 * This method has an explicit wildcard match on the end of the driver name <br>
	 * This method detects whether a first name or part of a fist name is supplied and conditionally adds that to the find operation <br>
	 * This method offers paging functionality VV <br>
	 * Used by Driver Allocation decode functionality
	 * @param driverName Search parameter for finding drivers
	 * @param customerAcctCodes Search parameter for finding drivers
	 * @param corpEntity Legal Corporate Entity
	 * @param activeInd Used to determine whether to find active, inactive, or both types of drivers
	 * @param pageReq Used for paging
	 * @return List of drivers 
	 */
	
	public List<Driver> getDrivers(String driverName, List<String> customerAcctCodes,
			CorporateEntity corpEntity, String activeInd, PageRequest pageReq) {
		
		List<Driver> listDriver = null;
		try {
			// split driver name into first and last name and call the appropriate service method
			String[] lastNameFirstName = splitDriverNameIntoLastAndFirst(driverName);
			if(lastNameFirstName[1] == null || lastNameFirstName[1].isEmpty()){
				lastNameFirstName[0] = setupWildCards(lastNameFirstName[0]);
				if(activeInd == null || MalConstants.ALL.equals(activeInd)){
					listDriver =  driverDAO.findDriversByLastName(lastNameFirstName[0], ExternalAccountDAO.ACC_STATUS_OPEN, corpEntity.getCorpId(), pageReq, customerAcctCodes, new ArrayList<String>() {{add(ExternalAccountDAO.ACCOUNT_TYPE_CUSTOMER);}}).getContent();
				}else{
					listDriver =  driverDAO.findDriversByLastNameWithActiveIndicator(lastNameFirstName[0],ExternalAccountDAO.ACC_STATUS_OPEN ,corpEntity.getCorpId(),activeInd, pageReq, customerAcctCodes, new ArrayList<String>() {{add(ExternalAccountDAO.ACCOUNT_TYPE_CUSTOMER);}}).getContent();
				}
			}else{
				lastNameFirstName[1] = setupWildCards(lastNameFirstName[1]);
				if(activeInd == null || MalConstants.ALL.equals(activeInd)){
					listDriver = driverDAO.findDriversByLastAndFirstName(lastNameFirstName[0], lastNameFirstName[1],ExternalAccountDAO.ACC_STATUS_OPEN, corpEntity.getCorpId(), pageReq ,customerAcctCodes, new ArrayList<String>() {{add(ExternalAccountDAO.ACCOUNT_TYPE_CUSTOMER);}}).getContent();
				}else{
					listDriver = driverDAO.findDriversByLastAndFirstNameWithActiveIndicator(lastNameFirstName[0], lastNameFirstName[1],ExternalAccountDAO.ACC_STATUS_OPEN, corpEntity.getCorpId(), activeInd, pageReq, customerAcctCodes,new ArrayList<String>() {{add(ExternalAccountDAO.ACCOUNT_TYPE_CUSTOMER);}}).getContent();
				}
			}
		} catch (Exception ex) {
			throw new MalException("generic.error.occured.while", new String[] { "finding drivers by lastName" }, ex);
		}
		return listDriver;
	}
	
	/**
	 * Driver's primary key used to find driver's addresses; Not used
	 * @param drvId Driver Primary Key
	 * @return List of Driver Addresses
	 */
	public List<DriverAddress> getDriverAddressList(long drvId) {
		return driverAddressDAO.findByDrvId(drvId);
	}
	
	/**
	 * Driver's primary key used to find driver's historical addresses; Not used
	 * @param drvId Driver Primary Key
	 * @return List of Driver Address History
	 */
	public List<DriverAddressHistory> getDriverAddressHistoryList(long drvId) {
		return driverAddressHistoryDAO.findByDrvId(drvId);
	}

	/**
	 * Finds a driver's address history for a provided address type; Not used
	 * @param drvId Driver Primary Key
	 * @param type Address Type to find
	 * @return List of Driver Address History
	 */
	
	public List<DriverAddressHistory> getDriverAddressHistoryListAsc(Long drvId, String type) {
		return driverAddressHistoryDAO.findByDrvIdAndTypeAsc(drvId, type);
	}

	/**
	 * Finds a driver's address for a provided address type; Not currently used
	 * @param drvId Driver Primary Key
	 * @param type Address Type to find
	 * @return List of Driver Addresses
	 */
	
	public DriverAddress getDriverAddress(Long drvId, String type) {
		return driverAddressDAO.findByDrvIdAndType(drvId, type);
	}

	/**
	 * This procedure uses all passed in parameters as search parameters and a count of the drivers
	 * found using these parameters are returned; Used by Driver Search screen
	 * @param driverName Driver's Name used as a search parameter
	 * @param driverId Driver's Primary Key used as a search parameter
	 * @param customerName Customer Name String used as a search parameter
	 * @param customerNo Customer Code primary key used as search parameter
	 * @param unitNo Unit Number used as a search parameter
	 * @param vin VIN used as a search parameter
	 * @param regNo Reg Number String used as search parameter
	 * @param licPlate License plate String used as search parameter
	 * @param showRecentUnits 
	 * @param driverActiveInd Search flag determining whether to find active, inactive, or both active and inactive drivers
	 * @return Number of records found from the search.
	 */
	
	public int searchDriverCount(String driverName, Long driverId , String customerName, String customerNo,String unitNo,String vin, String regNo,String licPlate,boolean showRecentUnits ,String driverActiveInd){
		return driverDAO.searchDriverCount(driverName, driverId , customerName, customerNo,unitNo, vin, regNo, licPlate, showRecentUnits , driverActiveInd);
	}
	
	/**
	 * This procedure uses all passed in parameters as search parameters and a list of the drivers
	 * found using these parameters are returned; Used by Driver Search screen
	 * @param driverName Driver's Name used as a search parameter
	 * @param driverId Driver's Primary Key used as a search parameter
	 * @param customerName Customer Name String used as a search parameter
	 * @param customerNo Customer Code primary key used as search parameter
	 * @param unitNo Unit Number used as a search parameter
	 * @param vin VIN used as a search parameter
	 * @param regNo Reg Number String used as search parameter
	 * @param licPlate License plate String used as search parameter
	 * @param showRecentUnits 
	 * @param driverActiveInd Search flag determining whether to find active, inactive, or both active and inactive drivers
	 * @param sort Determines which way the list should be sorted. ASC or DESC
	 * @return List of DriverSearchVO found from the search.
	 */
	
	public  List<DriverSearchVO> searchDriver(String driverName, Long driverId , String customerName, String customerNo,String unitNo,String vin,String regNo,String licPlate , boolean showRecentUnits, String driverActiveInd ,Pageable pageable , Sort sort){
	
		List<DriverSearchVO>  driverSearchVOList = 	driverDAO.searchDriver(driverName, driverId , customerName, customerNo,unitNo, vin,regNo, licPlate, showRecentUnits,driverActiveInd, pageable ,sort );
		
		for (DriverSearchVO driverSearchVO : driverSearchVOList) {
			if(driverSearchVO.getFmsId() !=null)
				driverSearchVO.setVehicleStatus(vehicleStatusVDAO.findById(driverSearchVO.getFmsId()).orElse(null).getVehicleStatus());
		} 
		
		return 	 driverSearchVOList;
		 
	}
	

	/**
	 * Retrieves a count of records found using the provided parameters as search parameters; Used by DriverLOV
	 * @param driverName Driver Name String used as parameter
	 * @param acctCodeList List of Account Codes to use as parameters
	 * @param acctType Account Type to use as parameter
	 * @param accContextId Context Id to use as parameter
	 * @param fetchOpenAccontOnly Include only open accounts as parameter
	 * @param fetchRelatedDriversAlso Include related drivers as parameter
	 * @param fetchActiveDriverOnly Include active drivers only as parameter
	 * @return Number of records found using the parameters
	 */
	public int searchLOVDriverCount(String driverName, List<String> acctCodeList,String acctType, String accContextId, boolean fetchOpenAccontOnly , boolean fetchRelatedDriversAlso,boolean fetchActiveDriverOnly){	
		return driverDAO.searchLOVDriverCount(driverName, acctCodeList,acctType, accContextId, fetchOpenAccontOnly , fetchRelatedDriversAlso, fetchActiveDriverOnly);	
	}
	
	/**
	 * Retrieves a list of drivers using the provided parameters as search parameters; Used by DriverLOV
	 * @param driverName Driver Name String used as parameter
	 * @param acctCodeList List of Account Codes to use as parameters
	 * @param acctType Account Type to use as parameter
	 * @param accContextId Context Id to use as parameter
	 * @param fetchOpenAccontOnly Include only open accounts as parameter
	 * @param fetchRelatedDriversAlso Include related drivers as parameter
	 * @param fetchActiveDriverOnly Include active drivers only as parameter
	 * @param pageReq Used for paging
	 * @return List of DriverLOVVO using the provided search parameters
	 */
	public List<DriverLOVVO> searchLOVDriver(String driverName, List<String> acctCodeList,String acctType, String accContextId , boolean fetchOpenAccontOnly, boolean fetchRelatedDriversAlso,boolean fetchActiveDriverOnly , PageRequest pageReq){
		return driverDAO.searchLOVDriver(driverName, acctCodeList,acctType, accContextId, fetchOpenAccontOnly , fetchRelatedDriversAlso, fetchActiveDriverOnly, pageReq);
	}
	
	/**
	 * Uses the provided parameters to search for a driver based on the driver's first name, last name, email,
	 * and account;  A list of driver primary keys are returned for the drivers found; Used as a validation
	 * condition in Driver Add Edit screen
	 * @param lastName Driver's Last Name
	 * @param firstName Driver's First Name
	 * @param email Driver's email
	 * @param accountCode Driver's account
	 * @param contextId Context Id
	 * @return List of Long for the drivers found using the provided parameters
	 */
	
	public List<Long> findDriverByLastAndFirstNameAndEmailForAccount(String lastName, String firstName,  String email, String accountCode, long contextId){
		return driverDAO.findDriverByLastAndFirstNameAndEmailForAccount(lastName, firstName, email, accountCode, contextId);
	}
	
	/**
	 * Applies the provided cost center to a driver; The driver's old cost center is given an end date of yesterday;
	 * The driver's new cost center is given a start date of today; <br> 
	 * If two cost centers are applied within the same day, the cost center applied first in the day is removed from
	 * the database and only the newest one remains with the driver;<br>
	 * Used by Driver Add Edit
	 * @param driver Driver to apply cost center
	 * @param newDriverCostCenterCode New cost center to apply to provided driver
	 */
	private void addNewCostCenterToDriver(Driver driver, CostCentreCode newDriverCostCenterCode) {
		// should be set to midnight of today
		Calendar todayCal = new GregorianCalendar();
		todayCal.set(Calendar.HOUR, 0);
		todayCal.set(Calendar.MINUTE, 0);
		todayCal.set(Calendar.SECOND, 0);
		todayCal.set(Calendar.MILLISECOND, 0);
		Date today = todayCal.getTime();
		
		Date yesterday = MALUtilities.subtractDays(today,1);
		DriverCostCenter currentCostCenter = driver.getDriverCurrentCostCenter();
		if(currentCostCenter != null) {
			if(currentCostCenter.getDateAllocated().after(yesterday)) {
				driver.getDriverCostCenterList().remove(currentCostCenter);
				driverCostCenterDAO.delete(currentCostCenter);
			}
			else {
				this.terminateCurrentCostCentre(driver);			
			}
		}
		DriverCostCenter newDriverCostCenter = new DriverCostCenter();
		newDriverCostCenter.setDateAllocated(today);
		newDriverCostCenter.setDriver(driver);
		newDriverCostCenter.setEffectiveToDate(null);
		newDriverCostCenter.setCostCenterCode(newDriverCostCenterCode.getCostCentreCodesPK().getCostCentreCode());
		newDriverCostCenter.setExternalAccount(newDriverCostCenterCode.getExternalAccount());
		if(driver.getDriverCostCenterList() == null){
			driver.setDriverCostCenterList(new ArrayList<DriverCostCenter>());
		}
		driver.getDriverCostCenterList().add(newDriverCostCenter);
		driver.setCostCentreAccountCode(driver.getExternalAccount().getExternalAccountPK().getAccountCode());
		driver.setCostCentreAccountType(driver.getExternalAccount().getExternalAccountPK().getAccountType());
		driver.setCostCentreAccountCorporateId(driver.getExternalAccount().getExternalAccountPK().getCId());
		
		// also update the cost centre field on the driver
		driver.setCostCentreCode(newDriverCostCenter.getCostCenterCode());
	}

	/**
	 * Sets driver's current cost center's end date to yesterday; <br>
	 * If we are terminating a cost center on the same day we created it, we will remove that cost center from the database;
	 * Used as helper method
	 * @param driver
	 */
	private void terminateCurrentCostCentre(Driver driver) {
		// should be set to yesterday
		Calendar todayCal = new GregorianCalendar();
		todayCal.set(Calendar.HOUR, 0);
		todayCal.set(Calendar.MINUTE, 0);
		todayCal.set(Calendar.SECOND, 0);
		todayCal.set(Calendar.MILLISECOND, 0);
		Date yesterday = MALUtilities.subtractDays(todayCal.getTime(),1); 
		
		if(!MALUtilities.isEmpty(driver.getDriverCostCenterList()) && driver.getDriverCostCenterList().size() > 0 && !MALUtilities.isEmpty(driver.getDriverCurrentCostCenter())){
			// check to see if we are terminating a cost center on the same day we created it.. if so we just want to remove it
			if(driver.getDriverCurrentCostCenter().getDateAllocated().after(yesterday)) {
				DriverCostCenter currentCostCenter = driver.getDriverCurrentCostCenter();
				driver.getDriverCostCenterList().remove(currentCostCenter);
				driverCostCenterDAO.delete(currentCostCenter);
			}else{
				driver.getDriverCurrentCostCenter().setEffectiveToDate(yesterday);
			}
			
		}
	}

	/**
	 * This will compare the cost center codes and return true if they are equal;
	 * @param costCentreCode1 Cost Center Code 1
	 * @param costCentreCode2 Cost Center Code 2
	 * @return True if cost center codes are equal.
	 */
	public boolean isCostCenterCodeAreSame(CostCentreCode  costCentreCode1, CostCentreCode costCentreCode2){
		
		if(costCentreCode1 == null && costCentreCode2 == null){
			
			return true;
		}else if((costCentreCode1 == null && costCentreCode2 != null) 
				|| (costCentreCode1 != null && costCentreCode2 == null) ){
			
			return false;
		}else{
			
			CostCentreCodePK costCenterCodePK1  =  costCentreCode1.getCostCentreCodesPK();
			CostCentreCodePK costCenterCodePK2  =  costCentreCode2.getCostCentreCodesPK();
			
			if(costCenterCodePK1 == null && costCenterCodePK2 == null){
				return true;
			
			}else if((costCenterCodePK1 == null && costCenterCodePK2 != null) 
					|| (costCenterCodePK1 != null && costCenterCodePK2 == null) ){
				
				return false;
			}else{
				
				return costCenterCodePK1.getCostCentreCode().equals(costCenterCodePK2.getCostCentreCode());
			}
			
		}
	}
	
	/**
	 * Validates whether a driver can be set to inactive:  <br>
	 * 1) If a driver has a unit on order, it cannot be set to inactive<br>
	 * 2) If driver is allocated to a unit, it cannot be set to inactive <br>
	 * Used as a helper method
	 * @param drvId Driver primary key
	 * @throws MalBusinessException
	 */
	private void validateIfDriverCanInactived(long drvId) throws MalBusinessException {
		
		// we need to get the stock/inventory on order units as well
		List<String> unitNumbers = getOnOrderUnitNumbersByDriverId(drvId,true);
		if(unitNumbers.size() > 0)
			throw new MalBusinessException("driver.inactive.validation", new String[]{"Driver has vehicle on order"});
		
		List<DriverAllocation> allocationsList = driverAllocationService.getDriverAllocations(drvId);
		if(allocationsList.size() > 0)
			throw new MalBusinessException("driver.inactive.validation", new String[]{"Driver has unit allocation"});
		
	}
	
	/**
	 * Checks to see if the provided cost center is a valid cost center for the provided
	 * driver's account; Used when updating a driver's cost center
	 * @param driver Driver to retrieve account info from
	 * @param costCentreCode Cost center code
	 * @return True if the cost center is valid for the provided driver's account
	 */
	
	public boolean validCostCenter(Driver driver, CostCentreCode costCentreCode) {
		return costCenterService.accountHasCostCenter(driver.getExternalAccount(), costCentreCode.getCostCentreCodesPK().getCostCentreCode());
	}

	/**
	 * Gets a list of addresses for the driver;  The returned list will include history addresses and the current address;
	 * The list is going to be a list of Driver History records because that entity has the start and end date fields on it;
	 * This means we must convert the current address record to a history address; <br>
	 * Used by Driver Overview and Drivers By Unit
	 *
	 * @param driverId Uniquely identifies the driver
	 * @param type The address type we are going to build a list of
	 * @return A list of history addresses and the current address for the given driver ID
	 */
	
	public List<DriverAddressHistory> getDriverAddressesByType(Long driverId, String type) {
		List<DriverAddressHistory> list = new ArrayList<DriverAddressHistory>();
		Date calculatedStartDate = null;
		for (DriverAddressHistory dah : getDriverAddressHistoryListAsc(driverId, type)) {
			if(calculatedStartDate != null) {
				if(calculatedStartDate.after(dah.getInputDate())) {
					calculatedStartDate = dah.getInputDate();
				}
			}
			dah.setStartDate(calculatedStartDate);
			dah.setEndDate(dah.getInputDate());
			list.add(dah);
			calculatedStartDate = MALUtilities.addDays(dah.getInputDate(), 1);
		}
		
		// get the current address of the specified type and convert and add if found
		DriverAddress da = getDriverAddress(driverId, type);
		if(da != null) {
			list.add(convertAddressToHistory(da));			
		}
		
		return list;
	}

	/**
	 * This sets up the values for the driver address history object to be saved; The driver address
	 *  history object is built from the provided driver address input; Used by Driver Add Edit when
	 *  updating a driver's address 
	 * @param da Driver Address
	 * @return Driver Address History object
	 */
	private DriverAddressHistory convertAddressToHistory(DriverAddress da) {
		Date newDate = addSeconds(da.getInputDate(), 1);  // ensure the current address has an input date slightly later than the history
		DriverAddressHistory convertedDriverAddress = new DriverAddressHistory();
		convertedDriverAddress.setAddressLine1(da.getAddressLine1());
		convertedDriverAddress.setAddressLine2(da.getAddressLine2());
		convertedDriverAddress.setAddressType(da.getAddressType());
		convertedDriverAddress.setBusinessAddressLine(da.getBusinessAddressLine());
		convertedDriverAddress.setBusinessInd(da.getBusinessInd());
		convertedDriverAddress.setDefaultInd(da.getDefaultInd());
		convertedDriverAddress.setDriver(da.getDriver());
		convertedDriverAddress.setEndDate(null);
		convertedDriverAddress.setGeoCode(da.getGeoCode());
		convertedDriverAddress.setInputDate(newDate);
		convertedDriverAddress.setPostcode(da.getPostcode());
		convertedDriverAddress.setStartDate(newDate);
		convertedDriverAddress.setStreetNo(da.getStreetNo());
		convertedDriverAddress.setTownCityCode(da.getTownCityCode());
		convertedDriverAddress.setUserName(da.getUserName());

		return convertedDriverAddress;
	}
	

	/**
	 * Loads the passed in address list with appropriate records; Used by Driver Overview and Drivers By Unit
	 * to set up the address list
	 * @param addressList List of addresses that will be converted to a VO and put into a new list
	 * @return a list of addressVOs 
	 */
	
	public List<DriverAddressVO> getAddressVOList(List<DriverAddressHistory> addressList) {
		List<DriverAddressVO> list = new ArrayList<DriverAddressVO>();
		for(DriverAddressHistory dah : addressList) {
			DriverAddressVO driverAddressVO = new DriverAddressVO();
			driverAddressVO.setAddressLine1(dah.getAddressLine1());
			driverAddressVO.setAddressLine2(dah.getAddressLine2());
			driverAddressVO.setCityDescription(dah.getCityDescription());
			if(dah.getCountry() != null) {
				driverAddressVO.setCountry(dah.getCountry().getCountryCode());	
			}
			driverAddressVO.setRegionDescription(dah.getRegionAbbreviation());
			driverAddressVO.setPostcode(dah.getPostcode());
			driverAddressVO.setType(dah.getAddressType().getDescription());
			driverAddressVO.setToDate(dah.getEndDate());
			driverAddressVO.setFromDate(dah.getStartDate());
			driverAddressVO.setDisplayAddressLine1(dah.getAddressLine1());
			if(dah.getAddressLine2() != null) {
				driverAddressVO.setDisplayAddressLine2(dah.getAddressLine2());
				driverAddressVO.setDisplayAddressLine2Details(dah.getCityDescription() + ", " + 
				dah.getRegionAbbreviation() + " " + dah.getPostcode());
			}
			else {
				driverAddressVO.setDisplayAddressLine2(dah.getCityDescription() + ", " + 
				dah.getRegionAbbreviation() + " " + dah.getPostcode());
			}
			if(dah.getBusinessInd() != null) {
				if(dah.getBusinessInd().equals("Y")) {
					driverAddressVO.setBusinessAddressLine(dah.getBusinessAddressLine());
				}
			}
			
			
			list.add(driverAddressVO);
		}
		return list;		
	}

	/**
	 * Adds the passed in number of seconds to the passed in date time; The new date time
	 * is returned.
	 * @param date Date provided
	 * @param increase Number of seconds to add
	 * @return Date with added seconds
	 */
	private Date addSeconds(Date date, int increase) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.SECOND, increase);
		
		return cal.getTime();	
	}
	
	
	/**
	 * Gets a list of Unit Numbers for any units on order (based upon specific quote statuses and no in service contract lines for the quote)
	 * for a specific driver; and also allows the caller to decide whether vehicles that are in our inventory (Stock)
	 * that are being prepared or have been prepared thru GRD gets included within this list
	 *
	 * @param driverId Uniquely identifies the driver
	 * @param includeStock is a flag to used to determine whether Allocated to GRD (16) and/or GRD Complete (17) quote statuses should be included 
	 * @return a list of unit numbers (or an empty list) for all of the on order units for that driver
	 */
	
	public List<String> getOnOrderUnitNumbersByDriverId(Long driverId, boolean includeStock) {
		return driverDAO.getOnOrderUnitNumbersByDriverId(driverId, includeStock);
	}

	@Override
	public Driver getActiveDriverForUnit(FleetMaster unit) {
		Driver currDriver = null;
		DriverAllocation alloc = driverAllocationService.getCurrentAllocation(unit);
		if(!MALUtilities.isEmpty(alloc)){
			currDriver = alloc.getDriver();
		}
		return currDriver;
	}

	public DriverAddress getDriverAddress(Long draId) {
		return driverAddressDAO.findById(draId).orElse(null);
	}

	@Override
	public List<DriverInfoVO> searchDriverInfo(String driverName, Long driverId, Long fmsId, ExternalAccountPK externalAccountPK, Pageable pageable, Sort sort) {
		return driverDAO.searchDriverInfo(driverName, driverId, fmsId, externalAccountPK, pageable, sort);
	}

	@Override
	public int searchDriverInfoCount(String driverName, ExternalAccountPK externalAccountPK) {
		return driverDAO.searchDriverInfoCount(driverName, externalAccountPK);
	}

	@Override
	@Transactional
	public Driver getDriverWithCurrentAllocation(Long drvId) {
		try {
			Driver driver = driverDAO.getDriverByIdWithCurrentAllocations(drvId);
	        Hibernate.initialize(driver.getPhoneNumbers());
			Hibernate.initialize(driver.getExternalAccount1());
			Hibernate.initialize(driver.getDriverCostCenterList());
			return driver;
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new MalException("generic.error.occured.while", new String[] { "finding a driver by driverId" }, ex);
		}
	}
	
	/**
	 * Retrieves the driver without loading allocations; This should be used any time we just need a driver as it's much more performance for driver's that have had many cars
	 * @param driverId Driver primary key
	 * @return Driver
	 */
	
	@Transactional
	public Driver getDriverExcludeAllocations(Long driverId) {
		try {
			Driver driver = driverDAO.findById(driverId).orElse(null);
            Hibernate.initialize(driver.getPhoneNumbers());
			Hibernate.initialize(driver.getExternalAccount1());
			Hibernate.initialize(driver.getDriverCostCenterList());
			return driver;
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new MalException("generic.error.occured.while", new String[] { "finding a driver by driverId" }, ex);
		}
	}
	
	
	
}
