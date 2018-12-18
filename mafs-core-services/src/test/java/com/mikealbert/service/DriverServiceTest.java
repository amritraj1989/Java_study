package com.mikealbert.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;

import com.mikealbert.testing.BaseTest;
import com.mikealbert.common.MalConstants;
import com.mikealbert.data.TestQueryConstants;
import com.mikealbert.data.dao.ExternalAccountDAO;
import com.mikealbert.data.entity.CostCentreCode;
import com.mikealbert.data.entity.Driver;
import com.mikealbert.data.entity.DriverAddress;
import com.mikealbert.data.entity.DriverAddressHistory;
import com.mikealbert.data.entity.DriverCostCenter;
import com.mikealbert.data.entity.DriverGrade;
import com.mikealbert.data.entity.ExternalAccount;
import com.mikealbert.data.entity.PhoneNumber;
import com.mikealbert.data.entity.PhoneNumberType;
import com.mikealbert.data.enumeration.CorporateEntity;
import com.mikealbert.data.vo.DriverLOVVO;
import com.mikealbert.data.vo.DriverSearchVO;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.exception.MalException;
import com.mikealbert.util.MALUtilities;

public class DriverServiceTest extends BaseTest{
		
	@Value("${generic.externalAccount.customer}")  String customerAccount;
	@Value("${driver.unallocatedDriver.name}")  String unallocatedDriver;
	@Value("${driver.allocatedDriver.drvId}") String allocatedDriverId;

	@Value("${driver.noneCostCentreDriver.drvId}")  Long noneCostCentreDriverId;
	@Value("${driver.noCurrentCostCenter.drvId}")  long noCurrentCostCenterDrvId;
	
	@Value("${driver.hasOnOrderUnit.drvId}") long hasOnOrderUnitDrvId;
	
	@Value("${driver.parentDrvId}")  Long parentDrvId;
	@Value("${driver.parentDriverName}") String parentDriverName;
	
	@Value("${generic.opCode}")  String userName;

	@Value("${driver.hasCostCenterToChange.drvId}")  long hasCostCenterToChangeDrvId;
	@Value("${driverSearch.vin}")  String vin;
	@Value("${driverSearch.fleetRef}")  String fleetRef;
	@Value("${driverSearch.willowPlate}")  String willowPlate;
	@Value("${driverSearch.talPlate}")  String talPlate;
	@Value("${driverSearch.driverName}")  String driverName;
	@Value("${driverSearch.accountId}")  String accountId;
	
	@Value("${driverSearch.terminatedUnit}")  String terminatedUnit;	

	@Resource DriverService driverService;
	@Resource CustomerAccountService externalAccountService;
	@Resource DriverGradeService driverGradeService;
	@Resource LookupCacheService lookupCacheService;
	@Resource FleetMasterService fleetMasterService;
	@Resource ContractService contractService;
	@Resource private CostCenterService costCenterService;
	@Resource private DriverRelationshipService   driverRelationshipService;
	
	
	//TODO Make account types an Enum
	final List<String> ACCOUNT_TYPES = Arrays.asList(ExternalAccountDAO.ACCOUNT_TYPE_CUSTOMER);	

	Driver driver;
	ExternalAccount externalAccount;
	DriverGrade driverGrade;
	String pendingLiveUnit;
	Long hasCurrentCostCenterDrvId;
	Long unallocatedDriverId;	

	
	@Test
	public void getDriversByDriverNameForCustomer(){
		PageRequest page1 = new PageRequest(0,1);
		
        assertEquals(1, driverService.getDrivers(unallocatedDriver, externalAccountService.getReleatedAccountCodes(customerAccount), CorporateEntity.MAL, null, page1).size());
	}
	
	
	@Test
	public void searchDriversLOVByDriverName(){
		
		PageRequest pageReq = new PageRequest(0,500);
		List<DriverLOVVO> driverVOList =   driverService.searchLOVDriver(driverName, null, null, null, false, true, false, pageReq);
		int count =    driverService.searchLOVDriverCount(driverName,  null, null, null, false, true, false);
		assertEquals(driverVOList.size(), count);
		
	}	
	@Test
	public void searchDriversLOVByDriverNameAndCustomer(){
		
		PageRequest pageReq = new PageRequest(0,500);
		List<String>  acctCodeList  = 	new ArrayList<String>();
		acctCodeList.add(accountId);
		List<DriverLOVVO> driverVOList =   driverService.searchLOVDriver("S",acctCodeList, "C", "1",true, false, true, pageReq);
		int count =    driverService.searchLOVDriverCount("S",  acctCodeList, "C", "1", true, false, true);
		assertEquals(driverVOList.size(), count);
		List<String> relatedAccountList =  externalAccountService.getReleatedAccountCodes(accountId);
		
		for (DriverLOVVO driverLOVVO : driverVOList) {
			
			assertEquals(driverLOVVO.getAccountStatus(), ExternalAccountDAO.ACC_STATUS_OPEN);
			assertEquals(driverLOVVO.getActiveInd(), MalConstants.FLAG_Y);
			String resultAccountId = driverLOVVO.getAccountId();
			boolean accountIsOkay = false;
			if(accountId.equals(resultAccountId) ){	
				accountIsOkay = true;
			}else{
				for (String account : relatedAccountList) {
					if(account.equals(accountId)){
						accountIsOkay = true;
					}
				}
			}
			
			assertTrue("Account in DRIVER LOV is not as per expectation",accountIsOkay);
			
		}
		
		
	}	
	
	/**
	 * Accept. Criteria: Story 7 - New Driver
	 * Ensures that a driver can be saved with valid values
	 */
	@Test
	public void testSaveOrUpdateDriverSave() throws MalBusinessException{
	    
		setUpDriverForAdd();
		driver = driverService.saveOrUpdateDriver(externalAccount, driverGrade, driver, generateAddresses(driver, 1), generatePhoneNumbers(driver, 1), userName, null);
		
	    assertNotNull("Add driver failed", driverService.getDriver(driver.getDrvId())); 
	    assertNotNull("Contact number was not saved", driver.getPhoneNumbers().get(0).getCnrId());
	    assertNotNull("Driver address was not saved", driver.getDriverAddressList().get(0).getDraId());
	    assertTrue("Driver address does not have a default ind", driver.getDriverAddressList().get(0).getDefaultInd() == "Y");
	}
	
	/**
	 * Ensures that a getActiveDriver service method only return active drivers.
	 */
	@Test
	public void testActiveDriver() throws MalBusinessException{
	    
		setUpDriverForAdd();
		driver.setActiveInd(MalConstants.FLAG_N);
		driver = driverService.saveOrUpdateDriver(externalAccount, driverGrade, driver, generateAddresses(driver, 1), generatePhoneNumbers(driver, 1), userName, null);		
		assertEquals("Active driver should null but it got data", null, driverService.getActiveDriver(driver.getDrvId())); 
	  
	}
	
	@Test
	public void testUpdateDriverWithRelatedDrivers()
			throws MalBusinessException {

		Driver driver = driverService.getDriver(parentDrvId); //later can change it to get driver dynamically through query. (Maybe) 
		ExternalAccount Account = driver.getExternalAccount();
		List<DriverGrade> driverGrades = driverGradeService.getExternalAccountDriverGrades(Account);

		for (DriverGrade driverGrade : driverGrades) {
			if (!driverGrade.equals(driver.getDgdGradeCode())) {
				driver.setDgdGradeCode(driverGrade);
				break;
			}
		}

		driver = driverService.saveOrUpdateDriver(driver.getExternalAccount(),driver.getDgdGradeCode(), driver,	driver.getDriverAddressList(), driver.getPhoneNumbers(),userName, null);
		List<Driver> relatedDriversList = driverRelationshipService.getRelatedDrivers(driver.getDrvId());
		for (Driver relatedDriver : relatedDriversList) {
			assertEquals("Related driver's grade should same as parent",driver.getDgdGradeCode(), relatedDriver.getDgdGradeCode());
			if(relatedDriver.getExternalAccount().equals(driver.getExternalAccount())){
				assertEquals("Related driver's cost center should same as parent",driver.getDriverCurrentCostCenter(), relatedDriver.getDriverCurrentCostCenter());
			}
		}

	}
	
	@Ignore
	public void testValidationsInActivateDriver()throws MalBusinessException {
		
		Driver driver = driverService.getDriver(parentDrvId); 
		boolean successUpdate = false;
		
		//try to Inactivate  allocated driver.System should not allow it and throw business exception. 
		@SuppressWarnings("unchecked")
		long allocatedDriverId= ((BigDecimal)em.createNativeQuery(TestQueryConstants.READ_DRV_ID_CURRENT_ALLOCATION).getSingleResult()).longValue();
		driver = driverService.getDriver(allocatedDriverId);
		driver.setActiveInd(MalConstants.FLAG_N);
		
		try {
			driver = driverService.saveOrUpdateDriver(driver.getExternalAccount(),driver.getDgdGradeCode(), driver,	driver.getDriverAddressList(), driver.getPhoneNumbers(),userName, null);
			successUpdate = true;
		} catch (Exception e) {
			assertEquals(e.getMessage(), "driver.inactive.validation");		
		}
		
		//try to Inactivate  driver if it has unit on order. System should not allow it and throw business exception. 
		
		@SuppressWarnings("unchecked")
		List<BigDecimal>  driverList= ((List<BigDecimal>)em.createNativeQuery(TestQueryConstants.READ_DRIVER_ID_HAVING_VEHICLE_ON_ORDER).getResultList());		
		if(driverList.size() > 0){			
			driver = driverService.getDriver(driverList.get(0).longValue());
			driver.setActiveInd(MalConstants.FLAG_N);
			driver.setPhoneNumbers(generatePhoneNumbers(driver,1));
			
			try {
				driver = driverService.saveOrUpdateDriver(driver.getExternalAccount(),driver.getDgdGradeCode(), driver,	driver.getDriverAddressList(), driver.getPhoneNumbers(),userName, null);
				successUpdate = true;
			} catch (Exception e) {
				assertEquals(e.getMessage(), "driver.inactive.validation");		
			}
		}
		
		assertFalse("Inactive driver should throw exception ",successUpdate);
	}
	
	/**
	 * Accept. Criteria: Story 7 - New Driver
	 * New Driver cannot be added to the system without customer's account
	 */
	@Test(expected=MalException.class)
	public void testSaveWithoutCustomersAccount() throws MalBusinessException{
	    
		setUpDriverForAdd();
		externalAccount = new ExternalAccount();
		driver = driverService.saveOrUpdateDriver(externalAccount, driverGrade, driver,  generateAddresses(driver, 1), generatePhoneNumbers(driver, 1), userName, null);
	}
	
	/**
	 * Accept. Criteria: Story 7 - New Driver
	 * New Driver cannot be added to the system without garaged address
	 */
	@Test(expected=MalException.class)
	public void testSaveWithoutGaraged() throws MalBusinessException{
	    List<DriverAddress> driverAddress = null;
		setUpDriverForAdd();
		
		driver = driverService.saveOrUpdateDriver(externalAccount, driverGrade, driver,  driverAddress, generatePhoneNumbers(driver, 1), userName, null);
	}
	
	/**
	 * Accept. Criteria: Story 7 - New Driver
	 * New Driver cannot be added to the system without driver grade group
	 */
	@Test(expected=MalException.class)
	public void testSaveWithoutGradeGroup() throws MalBusinessException{
		setUpDriverForAdd();
		driverGrade = new DriverGrade();
		driver = driverService.saveOrUpdateDriver(externalAccount, driverGrade, driver,  generateAddresses(driver, 1), generatePhoneNumbers(driver, 1), userName, null);
	}
	
	/**
	 * Accept. Criteria: Story 7 - New Driver
	 * New Driver cannot be added to the system without First Name
	 */
	@Test(expected=MalException.class)
	public void testSaveWithoutFirstName() throws MalBusinessException{
		setUpDriverForAdd();
		driver.setDriverForename(null);
		driver = driverService.saveOrUpdateDriver(externalAccount, driverGrade, driver,  generateAddresses(driver, 1), generatePhoneNumbers(driver, 1), userName, null);
	}
	
	/**
	 * Accept. Criteria: Story 7 - New Driver
	 * New Driver cannot be added to the system without Last Name
	 */
	@Test(expected=MalException.class)
	public void testSaveWithoutLastName() throws MalBusinessException{
		setUpDriverForAdd();
		driver.setDriverSurname(null);
		driver = driverService.saveOrUpdateDriver(externalAccount, driverGrade, driver,  generateAddresses(driver, 1), generatePhoneNumbers(driver, 1), userName, null);
	}
	
	/**
	 * Accept. Criteria: Story 7 - New Driver
	 * New Driver cannot be added to the system without Title
	 */
	@Test(expected=MalException.class)
	public void testSaveWithoutTitle() throws MalBusinessException{
		setUpDriverForAdd();
		driver.setTitle(null);
		driver = driverService.saveOrUpdateDriver(externalAccount, driverGrade, driver,  generateAddresses(driver, 1), generatePhoneNumbers(driver, 1), userName, null);
	}
	
	/**
	 * Accept. Criteria: Story 32 - Add Grade Group to Driver
	 * The grade group is displayed when the driver is displayed.
	 */
	public void testExistingDriverHasGradeGroup() throws MalBusinessException{
		unallocatedDriverId = ((BigDecimal)em.createNativeQuery(TestQueryConstants.READ_DRIVER_ID_UNALLOCATED).getSingleResult()).longValue();		
		driver = driverService.getDriver(unallocatedDriverId);
		assertNotNull("Grade Group does not exist for driver Id " +unallocatedDriverId, driver.getDgdGradeCode() );
	}
	
	/**
	 * Accept. Criteria: Story 8 - New Driver Recharge Acct.
	 * New driver can be assigned a recharge account.
	 */
	@Test
	public void testSaveWithRechargeAcct() throws MalBusinessException{
		setUpDriverForAdd();
		driver.setRechargeCode(externalAccount.getExternalAccountPK().getAccountCode());
		driver = driverService.saveOrUpdateDriver(externalAccount, driverGrade, driver,  generateAddresses(driver, 1), generatePhoneNumbers(driver, 1), userName, null);
		
		assertEquals("Recharge Acct Code does not match " + externalAccount.getExternalAccountPK().getAccountCode() ,driver.getRechargeCode(), externalAccount.getExternalAccountPK().getAccountCode());
	}
	
	/**
	 * Accept. Criteria: Story 8 - New Driver Recharge Acct.
	 * New driver recharge account can be over written with any customer 
	 *  account that exists in the system.
	 */
	@Test
	public void testSaveRechAcctDifferentFromClientAcct() throws MalBusinessException{
		String rechAcct = "00004168";
		setUpDriverForAdd();
		driver.setRechargeCode(rechAcct);
		driver = driverService.saveOrUpdateDriver(externalAccount, driverGrade, driver,  generateAddresses(driver, 1), generatePhoneNumbers(driver, 1), userName, null);
		
		assertEquals("Recharge Acct Code does not match " + rechAcct, driver.getRechargeCode(), rechAcct);
	}
	
	/**
	 * Accept. Criteria: Story 9 - New Driver Email
	 * I can enter a driver's email address and see it as part
	 *  of the driver's information.
	 */
	@Test
	public void testSaveDriverEmail() throws MalBusinessException{
		String email = "noone@mikealbert.com";
		setUpDriverForAdd();
		driver.setEmail(email);
		driver = driverService.saveOrUpdateDriver(externalAccount, driverGrade, driver,  generateAddresses(driver, 1), generatePhoneNumbers(driver, 1), userName, null);
		
		assertEquals("Inserted Email does not match " + email, driver.getEmail(), email);
	}
	
	
	/**
	 * Accept. Criteria: Story 10 - New Driver Multiple Addresses
	 *  Multiple addresses are available for a given driver.
	 */
	@Test
	public void testRetrieveMultipleAddressesForDriver() throws MalBusinessException{
		// driver with more than one address
		Long drv_id = 168535L;
		List<DriverAddress> da = driverService.getDriver(drv_id).getDriverAddressList();
		assertTrue(da.size() > 1);
		
	}
	
	
	// TODO: this is not reflecting the change; we need to fix it !!!
	/**
	 * Ensures that a driver address change triggers a new history entry
	 */
	@Ignore
	public void testUpdateDriverAddressSave() throws MalBusinessException{
	    // find the existing driver
		Driver existingDriver = driverService.getDriver(unallocatedDriverId);
		// get the existing drivers address
		List<DriverAddress> modifiedAddresses = new ArrayList<DriverAddress>();
		DriverAddress modifiedAddress = existingDriver.getDriverAddressList().get(0);
		existingDriver.getDriverAddressList().remove(0);
		
		modifiedAddress.setAddressLine1("ASDF");
		modifiedAddresses.add(modifiedAddress);
		
		// save the driver
		Driver updatedDriver = driverService.saveOrUpdateDriver(existingDriver.getExternalAccount(), existingDriver.getDgdGradeCode(), existingDriver, modifiedAddresses, existingDriver.getPhoneNumbers(), userName, null);
		
		// verify a new entry in the address history table for today
		boolean historyRecordForToday = false;
		Calendar todayCal = new GregorianCalendar();
		todayCal.set(Calendar.HOUR_OF_DAY, 0);
		todayCal.set(Calendar.MINUTE, 0);
		todayCal.set(Calendar.SECOND, 0);
		todayCal.set(Calendar.MILLISECOND, 0);
		Date today = todayCal.getTime();
		for(DriverAddressHistory hist : updatedDriver.getDriverAddressHistoryList())
		{
			if(hist.getInputDate().after(today)){
				historyRecordForToday = true;
				break;
			}
		}

	    assertTrue("Driver address history does not have a record for today, Address was not changed", historyRecordForToday);
	}
	
	
	/**
	 * Accept. Criteria: Story 47 - Pool Manager
	 *  When a driver is designated as a pool manager, the designation 
	 *  will be visible whenever the driver's information is viewed.
	 */
	@Test
	public void testSetPoolMgrAndRetrieve() throws MalBusinessException{
		Driver d = new Driver();
		setUpDriverForAdd();
		driver.setPoolManager("Y");
		driver = driverService.saveOrUpdateDriver(externalAccount, driverGrade, driver,  generateAddresses(driver, 1), generatePhoneNumbers(driver, 1), userName, null);
		
		d = driverService.getDriver(driver.getDrvId());
		
		assertEquals("Pool Manager is not set to 'Y'", d.getPoolManager(), "Y");
		
	}


	@Test
	/**
	 * Verify that all driver addresss get set as default (with default indicator) for each type
	 */
	public void testDriverAddressesDefaultIndicatorSetForAllAddresses() throws MalBusinessException{
		setUpDriverForAdd();
		driver = driverService.saveOrUpdateDriver(externalAccount, driverGrade, driver, generateAddresses(driver, 1), generatePhoneNumbers(driver, 1), userName, null);
		List<DriverAddress> addressList = driver.getDriverAddressList();
		boolean valid = true;
		for(DriverAddress driverAddress : addressList){
			if(!driverAddress.getDefaultInd().equalsIgnoreCase("Y")) {
				valid = false;
			}
		}
		assertTrue("Default Indicator not Y", valid);
	}
	
	

	private void setUpDriverForAdd() {
		driver = new Driver();
		externalAccount = 
				externalAccountService.getOpenCustomerAccountByCode(customerAccount);
		driverGrade = driverGradeService.getExternalAccountDriverGrades(externalAccount).get(0);
		
		//Driver initialization
		driver.setExternalAccount(externalAccount);
		driver.setDgdGradeCode(driverGrade);
		driver.setTitle(lookupCacheService.getTitleCodes().get(0).getTitleCode());
		driver.setDriverForename("Driver Service Add");
		driver.setDriverSurname("Test");
		driver.setManualStatus(lookupCacheService.getDriverManaualStatusCodes().get(0).getStatusCode());	
		driver.setPoolManager("N");
		driver.setActiveInd("Y");
	}
	
	/**
	 * Ensures that when a driver does not have preferred contact number in 
	 * its list of contact numbers a MalBusinessException is thrown by the service.
	 * @throws MalBusinessException
	 */	
	@Test(expected=MalBusinessException.class)
	public void testSaveOrUpdateDriverNoPreferredNumber() throws MalBusinessException{
		setUpDriverForAdd();
		driver.setDriverAddressList(generateAddresses(driver, 1));
		driver.setPhoneNumbers(generatePhoneNumbers(driver, 1));
		driver.getPhoneNumbers().get(0).setPreferredInd("N");
		driver = driverService.saveOrUpdateDriver(driver.getExternalAccount(), driver.getDgdGradeCode(), driver, driver.getDriverAddressList(), driver.getPhoneNumbers(), userName, null);
	}
	
	/**
	 * Ensures that when a driver has multiple preferred contact number
	 * a MalBusinessException is thrown by the service.
	 * @throws MalBusinessException
	 */
	@Test(expected=MalBusinessException.class)
	public void testSaveOrUpdateDriverMultiPreferredNumbers() throws MalBusinessException{
		setUpDriverForAdd();
		driver.setPhoneNumbers(generatePhoneNumbers(driver, 2));		
		driver.getPhoneNumbers().get(0).setPreferredInd("Y");
		driver.getPhoneNumbers().get(1).setPreferredInd("Y");		
		driver.setDriverAddressList(generateAddresses(driver, 1));
		driver = driverService.saveOrUpdateDriver(driver.getExternalAccount(), driver.getDgdGradeCode(), driver, driver.getDriverAddressList(), driver.getPhoneNumbers(), userName, null);
	}	
	
	/**
	 * Ensures that when a driver has duplicate contact number types
	 * a MalBusinessException is thrown by the service.
	 * @throws MalBusinessException
	 */
	@Test(expected=MalBusinessException.class)
	public void testSaveOrUpdateDriverDuplicatePhoneNumberTypes() throws MalBusinessException{
		setUpDriverForAdd();
		driver.setPhoneNumbers(generatePhoneNumbers(driver, 4));		
		driver.getPhoneNumbers().get(0).setPreferredInd("N");
		driver.getPhoneNumbers().get(1).setPreferredInd("N");
		driver.getPhoneNumbers().get(2).setPreferredInd("N");	
		driver.getPhoneNumbers().get(3).setPreferredInd("Y");		
		driver.getPhoneNumbers().get(0).setType(new PhoneNumberType("WORK"));
		driver.getPhoneNumbers().get(1).setType(new PhoneNumberType("CELL"));
		driver.getPhoneNumbers().get(2).setType(new PhoneNumberType("WORK"));
		driver.getPhoneNumbers().get(3).setPreferredInd("N");		
		driver.setDriverAddressList(generateAddresses(driver, 1));
		driver = driverService.saveOrUpdateDriver(driver.getExternalAccount(), driver.getDgdGradeCode(), driver, driver.getDriverAddressList(), driver.getPhoneNumbers(), userName, null);
	}	
	
	/**
	 * Update an existing Driver by changing values that are required and verifying that they can be read back after update
	 * @throws MalBusinessException
	 */
	@Test
	public void testSaveOrUpdateDriverUpdate() throws MalBusinessException{	
		final long drvId = 203692;
		final String FNAME = "Update";
		final String LNAME = "Driver Test";
		final String PHONE_NUMBER = "554-2728";
		
		Driver driver;
		
		driver = driverService.getDriver(drvId);
		driver.setDriverForename(FNAME);
		driver.setDriverSurname(LNAME);		
		driver.getDriverAddressList().get(0).setAddressLine1("111");	
		driver.getPhoneNumbers().get(0).setNumber(PHONE_NUMBER);
		driver = driverService.saveOrUpdateDriver(driver.getExternalAccount(), driver.getDgdGradeCode(), driver, driver.getDriverAddressList(), driver.getPhoneNumbers(), userName, null);	
		driver = driverService.getDriver(drvId);
		
        assertEquals("Update driver first name failed ", driver.getDriverForename(), FNAME);	
		assertEquals("Update driver last name failed ", driver.getDriverSurname(), LNAME);		
		assertEquals("Update driver phone number failed ", driver.getPhoneNumbers().get(0).getNumber(), PHONE_NUMBER);		
	}
	
	/**
	 * Save a cost centre for a driver and a specific client; verify the cost centre is returned in both places in the driver object
	 * @throws MalBusinessException
	 */
	@Test
	public void addNewCostCenterToADriver_FuncTest() throws MalBusinessException{
		hasCurrentCostCenterDrvId = ((BigDecimal)em.createNativeQuery(TestQueryConstants.READ_DRIVER_ID_WITH_CURR_COST_CENTER).getSingleResult()).longValue();
		
		Driver driverWithCostCenter = driverService.getDriver(hasCurrentCostCenterDrvId);

		CostCentreCode expectedCenter = costCenterService.getCostCenters(driverWithCostCenter.getExternalAccount()).get(1);
		
		Driver actualDriver = driverService.saveOrUpdateDriver(driverWithCostCenter.getExternalAccount(), driverWithCostCenter.getDgdGradeCode(), driverWithCostCenter, driverWithCostCenter.getDriverAddressList(), driverWithCostCenter.getPhoneNumbers(), "Testing", expectedCenter);
		        
		assertEquals(actualDriver.getDriverCurrentCostCenter().getCostCenterCode(), expectedCenter.getCostCentreCodesPK().getCostCentreCode());
		assertEquals(actualDriver.getCostCentreCode(), expectedCenter.getCostCentreCodesPK().getCostCentreCode());
	}
	
	/**
	 * Save a cost centre  with a "null" value for a driver and a specific client; verify the cost centre is not returned and the last cost centre has been terminated
	 * @throws MalBusinessException
	 */
	@Test
	public void removeCostCenterFromDriver_FuncTest() throws MalBusinessException{
		
		hasCurrentCostCenterDrvId = ((BigDecimal)em.createNativeQuery(TestQueryConstants.READ_DRIVER_ID_WITH_CURR_COST_CENTER).getSingleResult()).longValue();
		
		Driver driverWithCostCenter = driverService.getDriver(hasCurrentCostCenterDrvId);
		
		Driver actualDriver = driverService.saveOrUpdateDriver(driverWithCostCenter.getExternalAccount(), driverWithCostCenter.getDgdGradeCode(), driverWithCostCenter, driverWithCostCenter.getDriverAddressList(), driverWithCostCenter.getPhoneNumbers(), "Testing", null);
		
		assertEquals(actualDriver.getDriverCurrentCostCenter(), null);
		DriverCostCenter lastCostCentre =  actualDriver.getDriverCostCenterList().get(actualDriver.getDriverCostCenterList().size() - 1);
		assertNotNull(lastCostCentre.getEffectiveToDate());
		assertEquals(actualDriver.getCostCentreCode(),null);
	}
	
	/**
	 * Save a cost centre for a driver and a specific client twice on the same day; verify one one with an allocation date of yesterday is present
	 * @throws MalBusinessException
	 */
	@Test
	public void addNewCostCenterOnSameDay_FuncTest() throws MalBusinessException{

        em.createNativeQuery(TestQueryConstants.UPDATE_COST_CENTER_MAKE_CURRENT).setParameter(1, hasCostCenterToChangeDrvId).executeUpdate();
        em.flush();
        
		Driver driverWithCostCenter = driverService.getDriver(hasCostCenterToChangeDrvId);
		
		assertEquals(driverWithCostCenter.getDriverCurrentCostCenter().getCostCenterCode(), "02");
		
		CostCentreCode newCenter = costCenterService.getCostCenters(driverWithCostCenter.getExternalAccount()).get(2);
		Driver actualDriver = driverService.saveOrUpdateDriver(driverWithCostCenter.getExternalAccount(), driverWithCostCenter.getDgdGradeCode(), driverWithCostCenter, driverWithCostCenter.getDriverAddressList(), driverWithCostCenter.getPhoneNumbers(), "Testing", newCenter);
		
		int currCount = 0;
		
		for(DriverCostCenter costCenter : actualDriver.getDriverCostCenterList()){
			Calendar cal = new GregorianCalendar();
			cal.set(Calendar.HOUR, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			Date yesterday = MALUtilities.addDays(cal.getTime(), -1); 
			if(costCenter.getDateAllocated().after(yesterday)){
				currCount++;
			}
		}
		
		assertEquals(currCount, 1);
	}
	
	/**
	 * Save a cost centre for a driver and a specific client twice on the same day; verify one one with an allocation date of yesterday is present
	 * @throws MalBusinessException
	 */
	@Test
	public void addNewCostCenterThenNoneOnSameDay_FuncTest() throws MalBusinessException{

        em.createNativeQuery(TestQueryConstants.UPDATE_COST_CENTER_MAKE_CURRENT).setParameter(1, hasCostCenterToChangeDrvId).executeUpdate();
        em.flush();
        
		Driver driverWithCostCenter = driverService.getDriver(hasCostCenterToChangeDrvId);
		
		assertEquals(driverWithCostCenter.getDriverCurrentCostCenter().getCostCenterCode(), "02");

		hasCurrentCostCenterDrvId = ((BigDecimal)em.createNativeQuery(TestQueryConstants.READ_DRIVER_ID_WITH_CURR_COST_CENTER).getSingleResult()).longValue();		
		driverWithCostCenter = driverService.getDriver(hasCurrentCostCenterDrvId);
		Driver actualDriver = driverService.saveOrUpdateDriver(driverWithCostCenter.getExternalAccount(), driverWithCostCenter.getDgdGradeCode(), driverWithCostCenter, driverWithCostCenter.getDriverAddressList(), driverWithCostCenter.getPhoneNumbers(), "Testing", null);
		
		assertEquals(actualDriver.getDriverCurrentCostCenter(), null);
		assertEquals(actualDriver.getCostCentreCode(), null);

	}
		
	/**
	 * Ensures that pending live driver allocations can be returned by unit numbers
	 */
	@Test
	public void searchForPendingLiveByUnitNbr_FuncTest(){
		pendingLiveUnit = (String)em.createNativeQuery(TestQueryConstants.READ_PENDING_LIVE_UNIT_NO).getSingleResult().toString();
		List<DriverSearchVO> drivers = driverService.searchDriver(null, null, null, null, pendingLiveUnit, null, null, null, true,null, null, null);	
		long driversCnt = driverService.searchDriverCount(null, null, null, null, pendingLiveUnit, null, null, null, true,null);
		// we get back one result
		assertEquals(1, driversCnt);
		
		if(drivers.get(0).getContractLineStartDate() != null){
			// that result has a start date in the future
			assertTrue(drivers.get(0).getContractLineStartDate().after(new Date(System.currentTimeMillis())));
		}
	}
	
	/**
	 * Ensures that old units and their "last/most recent" allocation can be returned by unit numbers
	 */
	@Test
	public void searchForTerminatedUnitByUnitNbr_FuncTest(){ 		
		List<DriverSearchVO> drivers = driverService.searchDriver(null, null, null, null, terminatedUnit, null, null, null, true,null, null, null);	
		long driversCnt = driverService.searchDriverCount(null, null, null, null, terminatedUnit, null, null, null, true,null);
		
		// we get back one result
		assertEquals(1, driversCnt);
		// that result has a end date in the past	
		assertTrue(drivers.get(0).getContractLineEndDate().before(new Date(System.currentTimeMillis())));		
	}
	


	/**
	 * Ensures Driver data is returned when I search by First / Last name
	 */
	@Test
	public void searchForDriversByDriverName_FuncTest(){ 
		List<DriverSearchVO> drivers = driverService.searchDriver(driverName, null, null, null, null, null, null, null, false, null, null, null);	
		long driversCnt = driverService.searchDriverCount(driverName, null, null, null, null, null, null, null, false, null);
		
		// we get back results of the same length as the list returned
		assertEquals(drivers.size(), driversCnt);
		
		// the list is greater than 0
		assertTrue(driversCnt > 0);		
	}
	
	/**
	 * Ensures Driver data is returned when I search by by Vehicle Vin number
	 */
	@Test
	public void searchForDriversByVinNumber_FuncTest(){ 
		List<DriverSearchVO> drivers = driverService.searchDriver(null, null, null, null, null, vin, null, null, true,null, null, null);	
		long driversCnt = driverService.searchDriverCount(null, null, null, null, null, vin, null, null, true,null);
		
		// we get back results of the same length as the list returned
		assertEquals(drivers.size(), driversCnt);
		
		// the list is greater than 0
		assertTrue(driversCnt > 0);			
	}
	
	/**
	 * Ensures Driver data is returned when I search by Account code
	 */
	@Test
	public void searchForDriversByClientNo_FuncTest(){ 
		List<DriverSearchVO> drivers = driverService.searchDriver(null, null, null, customerAccount, null, null, null, null, false, null, null, null);	
		long driversCnt = driverService.searchDriverCount(null, null, null, customerAccount, null, null, null, null, false, null);
		
		// we get back results of the same length as the list returned
		assertEquals(drivers.size(), driversCnt);
		
		// the list is greater than 0
		assertTrue(driversCnt > 0);			
	}
	
	/**
	 * Ensures Driver data is returned when I search by license plate number in TAL
	 */
	@Ignore
	public void searchForDriversByTALPlate_FuncTest(){ 
		List<DriverSearchVO> drivers = driverService.searchDriver(null, null, null, null, null, null, null, talPlate, false, null, null, null);	
		long driversCnt = driverService.searchDriverCount(null, null, null, null, null, null, null, talPlate, false, null);
		
		// we get back results of the same length as the list returned
		assertEquals(drivers.size(), driversCnt);
		
		// the list is greater than 0
		assertTrue(driversCnt > 0);			
	}
	
	/**
	 * Ensures Driver data is returned when I search by license plate (reg) number stored in Willow
	 */
	@Test
	public void searchForDriversByWillowPlate_FuncTest(){ 
		List<DriverSearchVO> drivers = driverService.searchDriver(null, null, null, null, null, null, null, willowPlate, true,null, null, null);	
		long driversCnt = driverService.searchDriverCount(null, null, null, null, null, null, null, willowPlate, true,null);
		
		// we get back results of the same length as the list returned
		assertEquals(drivers.size(), driversCnt);
		
		// the list is greater than 0
		assertTrue(driversCnt > 0);			
	}
	
	/**
	 * Ensures Driver data is returned when I search by the fleet reference number stored with the quote.
	 */
	@Test
	public void searchForDriversByFleetRef_FuncTest(){ 
		List<DriverSearchVO> drivers = driverService.searchDriver(null, null, null, null, null, null, fleetRef, null, true,null, null, null);	
		long driversCnt = driverService.searchDriverCount(null, null, null, null, null, null, fleetRef, null, true,null);
		
		// we get back results of the same length as the list returned
		assertEquals(drivers.size(), driversCnt);
		
		// the list is greater than 0
		assertTrue(driversCnt > 0);			
	}
	
	/**
	 * Ensures Driver data is returned when I search for an unallocated driver by name
	 */
	@Test
	public void searchForUnallocatedDriversByName_FuncTest(){ 
		List<DriverSearchVO> drivers = driverService.searchDriver(unallocatedDriver, null, null, null, null, null, null, null, false, null, null, null);	
		long driversCnt = driverService.searchDriverCount(unallocatedDriver, null, null, null, null, null, null, null, false, null);
		
		// we get back results of the same length as the list returned
		assertEquals(drivers.size(), driversCnt);
		
		// the list is greater than 0
		assertTrue(driversCnt > 0);			
	}
	
	/**
	 * Ensures Driver data is returned when I search for an Active InActive driver 
	 */
	@Test
	public void searchForActiveInActiveDrivers_FuncTest() throws MalBusinessException { 
	
			//add a driver with active ind fald as Y and perform search
			setUpDriverForAdd();
			driver = driverService.saveOrUpdateDriver(externalAccount, driverGrade, driver, generateAddresses(driver, 1), generatePhoneNumbers(driver, 1), userName, null);
			List<DriverSearchVO> drivers = driverService.searchDriver(null, driver.getDrvId(), null, null, null, null, null, null, false, MalConstants.FLAG_Y, null, null);	
			long driversCnt = driverService.searchDriverCount(null, driver.getDrvId(), null, null, null, null, null, null, false, MalConstants.FLAG_Y);
			
			assertTrue("Driver has not active indicator flag as Y", driver.getActiveInd().equals(MalConstants.FLAG_Y));
			assertTrue("Driver search didn't pulled driver with active indicator flag as Y",driversCnt == 1);	
			assertTrue("Driver search didn't pulled driver with active indicator flag as Y",drivers.size() == 1);	
			
			//add a driver with active ind fald as N and perform search
			setUpDriverForAdd();
			driver.setActiveInd(MalConstants.FLAG_N);	
			driver = driverService.saveOrUpdateDriver(externalAccount, driverGrade, driver, generateAddresses(driver, 1), generatePhoneNumbers(driver, 1), userName, null);
			drivers = driverService.searchDriver(null, driver.getDrvId(), null, null, null, null, null, null, false, MalConstants.FLAG_N, null, null);	
			driversCnt = driverService.searchDriverCount(null, driver.getDrvId(), null, null, null, null, null, null, false, MalConstants.FLAG_N);
			
			assertTrue("Driver has not active indicator flag as N", driver.getActiveInd().equals(MalConstants.FLAG_N));
			assertTrue("Driver search didn't pulled driver with active indicator flag as N",driversCnt == 1);	
			assertTrue("Driver search didn't pulled driver with active indicator flag as N",drivers.size() == 1);
			
			// Driver search should fetch active inactive both if we will passed active ind flag  as null
			drivers = driverService.searchDriver(null, driver.getDrvId(), null, null, null, null, null, null, false, null, null, null);	
			driversCnt = driverService.searchDriverCount(null, driver.getDrvId(), null, null, null, null, null, null, false, null);
			assertTrue("Driver search didn't pulled driver with active indicator flag as null",driversCnt == 1);	
			assertTrue("Driver search didn't pulled driver with active indicator flag as null",drivers.size() == 1);
			
			
	}
	/**
	 * Test driver addresses are returned properly for various situations
	 */
	@Test
	public void testDriverAddressesReturned(){ 
		// Left the driver values in this test method as the driver id and the expected values are very closely tied and extracting
		// them out to a properties file doesn't seem to make sense as these are old drivers that will not likely change.
		
		long driverId = 3l;  // driver with no post address, 2 garaged history addresses and 1 current
		assertEquals(driverService.getDriverAddressesByType(driverId, DriverService.POST_ADDRESS_TYPE).size(), 0);	
		assertEquals(driverService.getDriverAddressesByType(driverId, DriverService.GARAGED_ADDRESS_TYPE).size(), 3);
		
		driverId = 4l;  // driver with no history addresses
		assertEquals(driverService.getDriverAddressesByType(driverId, DriverService.GARAGED_ADDRESS_TYPE).size(), 1);
		
		driverId = 125989l;  // driver with multiple post
		List<DriverAddressHistory> list = driverService.getDriverAddressesByType(driverId, DriverService.POST_ADDRESS_TYPE);
		assertEquals(list.size(), 4);
		// check end date of first record
		assertEquals(list.get(0).getEndDate().toString(), "2006-12-09 09:39:05.0");   
		// check calculated start date of history record when change on same day
		assertEquals(list.get(1).getStartDate().toString(), "2006-12-09 09:58:51.0");
		// check calculated start date of history record when change not on same day
		assertEquals(list.get(2).getStartDate().toString(), "Sun Dec 10 00:00:00 EST 2006");  //TODO This is too brittle need to redo date/time test
		// check start date of current record
		assertEquals(list.get(3).getStartDate().toString(), "Fri Apr 03 00:00:01 EDT 2009");  //TODO This is too brittle need to redo date/time test
	}

	/**
	 * Test returning a driver using a quicker approach
	 */
	@Test
	public void testGetDriverExcludeAllocations(){ 
		assertNotNull(driverService.getDriverExcludeAllocations(239537L)); 
	}
	
	
	
	
	/**
	 * Generates a collection of driver address
	 * @param driver The driver the addresses will be assigned
	 * @param amount The amount of contact numbers to generate
	 * @return List of driver addresses
	 */
	private List<DriverAddress> generateAddresses(Driver driver, int amount){
		List<DriverAddress> addresses = new ArrayList<DriverAddress>();
		for(int i=0; i < amount; i++){ 		
			addresses.add(new DriverAddress());
			addresses.get(i).setBusinessInd("N");
			addresses.get(i).setAddressLine1(i + " Driver Service Test");
			addresses.get(i).setPostcode("45241");
			addresses.get(i).setGeoCode("0800");		
			addresses.get(i).setAddressType(driverService.getDriverAddressTypeCodes().get(0));
			addresses.get(i).setDriver(driver);
		}
		return addresses;
	}
	
	/**
	 * Generates a collection of contact numbers
	 * @param driver The driver the contact numbers will be assigned
	 * @param amount The amount of contact numbers to generate
	 * @return List of contact numbers
	 */
	private List<PhoneNumber> generatePhoneNumbers(Driver driver, int amount){
		List<PhoneNumber> phoneNumbers = new ArrayList<PhoneNumber>();
		for(int i=0; i < amount; i++){ 
			phoneNumbers.add(new PhoneNumber());
			phoneNumbers.get(i).setCountryCode("USA");
			phoneNumbers.get(i).setAreaCode("513");
			phoneNumbers.get(i).setNumber("554-2930");		
			phoneNumbers.get(i).setType(lookupCacheService.getPhoneNumberTypes().get(0));	
			phoneNumbers.get(i).setPreferredInd(i == 0 ? "Y" : "N");
			phoneNumbers.get(i).setDriver(driver);	
		}
		return phoneNumbers;
	}
	

}
