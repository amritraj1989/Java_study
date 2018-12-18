package com.mikealbert.vision.specs.drivers;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Value;

import com.mikealbert.testing.BaseSpec;
import com.mikealbert.data.entity.CityZipCode;
import com.mikealbert.data.entity.Driver;
import com.mikealbert.data.entity.DriverAddress;
import com.mikealbert.data.entity.DriverGrade;
import com.mikealbert.data.entity.ExternalAccount;
import com.mikealbert.data.entity.PhoneNumber;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.exception.MalException;
import com.mikealbert.service.AddressService;
import com.mikealbert.service.CustomerAccountService;
import com.mikealbert.service.DriverGradeService;
import com.mikealbert.service.DriverService;
import com.mikealbert.service.LookupCacheService;

public class AddEditDriverTest extends BaseSpec{
	@Resource DriverService driverService;
	@Resource CustomerAccountService externalAccountService;
	@Resource DriverGradeService driverGradeService;
	@Resource LookupCacheService lookupCacheService;
	@Resource AddressService addressService;
	
	@Value("${generic.opCode}")  String userName;
	@Value("${generic.externalAccount.customer}")  String customerAccount;
	
	Driver driver;
	ExternalAccount externalAccount;
	DriverGrade driverGrade;
	
	/**
	 * Accept. Criteria: Story 7 - New Driver
	 * New Driver cannot be added to the system without garaged address (it will throw an error)
	 */
	public boolean testSaveWithoutGaraged(int numOfAddresses) throws MalBusinessException{
		setUpDriverForAdd();
		
		boolean saveErrored = false;
		
		try{
			driver = driverService.saveOrUpdateDriver(externalAccount, driverGrade, driver,  generateAddresses(driver,numOfAddresses), generatePhoneNumbers(driver, 1), userName, null);
		}catch(MalException malEx){
			saveErrored = true;
		}
		
		return saveErrored;
	}
	
	
	/**
	 * Accept. Criteria: Story 7 - New Driver
	 * New Driver cannot be added to the system without customer's account
	 */
	public boolean testSaveWithoutCustomersAccount(String customerAccount) throws MalBusinessException{
		setUpDriverForAdd();
		ExternalAccount thisAccount;
		boolean saveErrored = false;
		
		try{
			thisAccount = externalAccountService.getOpenCustomerAccountByCode(customerAccount);
			driver = driverService.saveOrUpdateDriver(thisAccount, driverGrade, driver,  generateAddresses(driver, 1), generatePhoneNumbers(driver, 1), userName, null);
		}catch(MalException malEx){
			saveErrored = true;
		}
		
		return saveErrored;
	}
	
	/**
	 * Accept. Criteria: Story 7 - New Driver
	 * New Driver cannot be added to the system without driver grade group
	 */
	public boolean testSaveWithoutGradeGroup(String customerAccount, String driverGradeName) throws MalBusinessException{
		setUpDriverForAdd();
		
		boolean saveErrored = false;
		
		try{
			externalAccount = externalAccountService.getOpenCustomerAccountByCode(customerAccount);
			DriverGrade driverGrade = new DriverGrade();
			for(DriverGrade grade : driverGradeService.getExternalAccountDriverGrades(externalAccount)){
				if (grade.getGradeDesc().equalsIgnoreCase(driverGradeName)){
					driverGrade = grade;
					break;
				}
			}
			driver = driverService.saveOrUpdateDriver(externalAccount, driverGrade, driver,  generateAddresses(driver, 1), generatePhoneNumbers(driver, 1), userName, null);
		}catch(MalException malEx){
			saveErrored = true;
		}
		
		return saveErrored;
	}
	
	/**
	 * Accept. Criteria: Story 7 - New Driver
	 * New Driver cannot be added to the system without First Name, Last Name, Title
	 */
	public boolean testSaveWithoutFirstNameLastNameTitle(String firstName, String lastName, String title) throws MalBusinessException{
		setUpDriverForAdd();
		boolean saveErrored = false;
		
		driver.setDriverForename(firstName);
		driver.setDriverSurname(lastName);
		driver.setTitle(title);
		try{
			driver = driverService.saveOrUpdateDriver(externalAccount, driverGrade, driver,  generateAddresses(driver, 1), generatePhoneNumbers(driver, 1), userName, null);
		}catch(MalException malEx){
			saveErrored = true;
		}
		
		return saveErrored;
	}
	
	/**
	 * Accept. Criteria: Story 11 - Address search capability
	 * Searching by ZipCode returns a list of CityZipCodes
	 */
	public List<CityZipCode> testGetAllCityZipCodesByZipCode(String zip){
		List<CityZipCode> cityZips = addressService.getAllCityZipCodesByZipCode(zip);
		return cityZips;
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
			addresses.get(i).setAddressType(driverService.getDriverAddressTypeCodes().get(i));
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
