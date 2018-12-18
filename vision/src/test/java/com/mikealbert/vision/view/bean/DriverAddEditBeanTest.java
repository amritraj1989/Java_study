package com.mikealbert.vision.view.bean;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Value;

import com.mikealbert.data.dao.DriverDAO;
import com.mikealbert.data.dao.FleetMasterDAO;
import com.mikealbert.data.entity.CostCentreCode;
import com.mikealbert.data.entity.Driver;
import com.mikealbert.data.entity.DriverAddress;
import com.mikealbert.data.entity.DriverGrade;
import com.mikealbert.data.entity.ExternalAccount;
import com.mikealbert.data.entity.PhoneNumber;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.service.ContractService;
import com.mikealbert.service.CustomerAccountService;
import com.mikealbert.service.DriverGradeService;
import com.mikealbert.service.DriverService;
import com.mikealbert.service.FleetMasterService;
import com.mikealbert.service.LookupCacheService;
import com.mikealbert.util.SpringAppContext;


public class DriverAddEditBeanTest extends BeanTestCaseSetup{
	
	@Resource DriverService driverService;
	@Resource DriverDAO driverDAO;
	@Resource FleetMasterDAO fleetMasterDAO;
	@Resource FleetMasterService  fleetMasterService;
	
	@Value("${generic.externalAccount.customer}")  String externalAccountData;
	@Value("${generic.externalAccount.customer}")  String customerAccount;
	@Value("${driver.unallocatedDriver.name}")  String unallocatedDriver;
	@Value("${generic.opCode}")  String userName;
	
	@Resource CustomerAccountService externalAccountService;
	@Resource DriverGradeService driverGradeService;
	@Resource LookupCacheService lookupCacheService;
	
	@Resource ContractService contractService;

	private static DriverAddEditBean driverAddEditBean ;

	@Before
	public void setup() {
		
		
		clearFaceMessages();
	}

	@Test
	public void testAddDriver() throws MalBusinessException{
		DriverAddEditBean driverAddEditBean = getDriverAddEditBean();
		DriverAddEditBean spyDriverAddEditBean = Mockito.spy(driverAddEditBean);
		DriverService mockedDriverService = mock(DriverService.class);
		when(mockedDriverService.saveOrUpdateDriver(any(ExternalAccount.class),any(DriverGrade.class),
				any(Driver.class),anyListOf(DriverAddress.class),anyListOf(PhoneNumber.class),any(String.class),
				any(CostCentreCode.class)  )).thenReturn( new Driver());
	
		
		
		
	
		
		Mockito.doReturn(getUser()).when(spyDriverAddEditBean).getLoggedInUser();

		spyDriverAddEditBean.setDriver(getDriverForAdd(spyDriverAddEditBean));
		generateAddresses(spyDriverAddEditBean);
		generatePhoneNumbers(spyDriverAddEditBean);
		spyDriverAddEditBean.setSelectedDriverActiveIndicatorFlag("Y");
		//TODO With Mockito DB rollback is not happening. We need to research on it to find root cause.
		// As an alternative , we can mock driverService.saveOrUpdateDriver call while calling save method of DriverAddEditBean
	/*	
		spyDriverAddEditBean.save();
    	
    	Map<String,Object> errorMsgMap  = getErrorFaceMessages();
		Map<String,Object> successMsgMap  = getSuccessFaceMessages();
	
		System.out.println(errorMsgMap.size()+"=MAP="+successMsgMap.size());
		assertTrue("New Driver should get save properly have have success message",successMsgMap.size()== 1);
		assertTrue("New Driver should get save properly with out any error message" ,errorMsgMap.size()== 0);
		*/
	}

	private  DriverAddEditBean getDriverAddEditBean(){
		
		if(driverAddEditBean == null){
		 driverAddEditBean = (DriverAddEditBean) SpringAppContext.getBean(DriverAddEditBean.class);
		}
		
		return driverAddEditBean;
	}

	private Driver getDriverForAdd(DriverAddEditBean driverAddEditBean) {
		  
		
		Driver driver = new Driver();
		ExternalAccount externalAccount = externalAccountService.getOpenCustomerAccountByCode(externalAccountData);
		DriverGrade driverGrade = driverGradeService.getExternalAccountDriverGrades(externalAccount).get(0);
		
		driverAddEditBean.setSelectedExternalAccount(externalAccount);
		driverAddEditBean.setCustomerLOVParam(externalAccountData);
		driverAddEditBean.setRechargeCustomerLOVParam(externalAccountData);
		driverAddEditBean.setSelectedRechargeExternalAccount(externalAccount);
		driverAddEditBean.setSelectedDriverGrade(driverGrade);
		
		//Driver initialization
		driver.setExternalAccount(externalAccount);
		driver.setDgdGradeCode(driverGrade);
		driver.setActiveInd("Y");
		driver.setTitle(lookupCacheService.getTitleCodes().get(0).getTitleCode());
		driver.setDriverForename("Driver Service Add");
		driver.setDriverSurname("Test");
		driver.setManualStatus(lookupCacheService.getDriverManaualStatusCodes().get(0).getStatusCode());	
		driverAddEditBean.setDriver(driver);
		
		return driver;
	}

	private List<DriverAddress> generateAddresses(DriverAddEditBean driverAddEditBean) {
		
		List<DriverAddress> addresses = new ArrayList<DriverAddress>();
		driverAddEditBean.setDriverAddresses(addresses);
		DriverAddress driverAddress = new DriverAddress();

		addresses.add(driverAddress);
		driverAddress.setAddressLine1(" Driver Service Test");
		driverAddress.setPostcode("45241");
		driverAddress.setGeoCode("0800");
		driverAddress.setAddressType(driverService.getDriverAddressTypeCodes().get(0));
		driverAddress.setBusinessInd("N");
		driverAddress.setDriver(driverAddEditBean.getDriver());

		return addresses;
	}

	private List<PhoneNumber> generatePhoneNumbers(DriverAddEditBean driverAddEditBean){
		List<PhoneNumber> phoneNumbers = new ArrayList<PhoneNumber>();
		driverAddEditBean.setPhoneNumbers(phoneNumbers);
		
		PhoneNumber phoneNumber = new PhoneNumber();
		phoneNumbers.add(new PhoneNumber());
		phoneNumber.setCountryCode("USA");
		phoneNumber.setAreaCode("513");
		phoneNumber.setNumber("554-2930");
		phoneNumber.setType(lookupCacheService.getPhoneNumberTypes().get(0));
		phoneNumber.setPreferredInd("Y");
		phoneNumber.setDriver(driverAddEditBean.getDriver());
		
		driverAddEditBean.setWorkPhone(phoneNumber);
		driverAddEditBean.setPreferredPhone("2");
		
		return phoneNumbers;
	}
}