package com.mikealbert.service;

import static org.junit.Assert.*;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Resource;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import com.mikealbert.testing.BaseTest;
import com.mikealbert.data.dao.ExternalAccountDAO;
import com.mikealbert.data.entity.ExternalAccount;
import com.mikealbert.data.enumeration.CorporateEntity;
import com.mikealbert.service.CustomerAccountService;

public class ExternalAccountServiceTest extends BaseTest{
	
	@Value("${generic.externalAccount.customer}")  String customerAccount;
	@Value("${generic.externalAccount.customerName}")  String customerAccountName;
	@Value("${generic.externalAccount.customerNameFuzzy}")  String customerAccountNameFuzzy;
	@Value("${generic.externalAccount.parentAccount}")  String parentAccount;
	@Value("${generic.externalAccount.childAccount}")  String childAccount;
	@Value("${generic.externalAccount.siblingAccount}")  String siblingAccount;
	
	
	@Resource
	CustomerAccountService externalAccountService;
	final List<String> ACCOUNT_TYPES = Arrays.asList(ExternalAccountDAO.ACCOUNT_TYPE_CUSTOMER, ExternalAccountDAO.ACCOUNT_TYPE_SUPPLIER);	
	final PageRequest PAGE = new PageRequest( 
			  0, 2
			); 
	
	@Test
	public void testFindOpenCustomerAccountsPagable() {				
		List<ExternalAccount> externalAccountList = 
				externalAccountService.findOpenCustomerAccounts(customerAccountNameFuzzy, PAGE);
		assertTrue(externalAccountList.size() == 2);		
	}
	
	@Test
	public void testFindetOpenCustomerAccounts() {				
		List<ExternalAccount> externalAccountList = 
				externalAccountService.findOpenCustomerAccounts(customerAccountNameFuzzy);
		assertTrue(externalAccountList.size() > 1);		
	}
	
	@Test
	public void testGetTotalAccountCount() {				
		long externalAccountCount = 
				externalAccountService.getOpenCustomerCountByName(customerAccountNameFuzzy);
		assertTrue(externalAccountCount > 1);		
	}
	
	@Ignore
	public void testGetOpenCustomerAccountByName(){				
		ExternalAccount externalAccount = 
				externalAccountService.getOpenCustomerAccountByName(customerAccountName, CorporateEntity.MAL);
		assertNotNull("Open Customer Account could not be found by exact match on name", externalAccount);				
	}
	
	@Test
    public void testIsAccountsReleated(){
		ExternalAccount account1;
		ExternalAccount account2;
		
		//Same accounts should evaluate to true.
		account1 = 
				externalAccountService.getOpenCustomerAccountByCode(customerAccount, CorporateEntity.MAL);
		account2 = 
				externalAccountService.getOpenCustomerAccountByCode(customerAccount, CorporateEntity.MAL);
		assertTrue("Same accounts where determined unrelated", externalAccountService.isAccountsReleated(account1, account2));
		
		//One account (1) that is the parent of the other (2)
		account1 = 
				externalAccountService.getOpenCustomerAccountByCode(parentAccount, CorporateEntity.MAL);
		account2 = 
				externalAccountService.getOpenCustomerAccountByCode(childAccount, CorporateEntity.MAL);
		assertTrue("An account that is the parent of another was determined unrelated", externalAccountService.isAccountsReleated(account1, account2));		
		
		//One account (1) that is the child of another (2)
		account1 = 
				externalAccountService.getOpenCustomerAccountByCode(childAccount, CorporateEntity.MAL);
		account2 = 				
		        externalAccountService.getOpenCustomerAccountByCode(parentAccount, CorporateEntity.MAL);
		assertTrue("An account that is the parent of another was determined unrelated", externalAccountService.isAccountsReleated(account1, account2));			
		
		//One account that is a sibling of another
		account1 = 
				externalAccountService.getOpenCustomerAccountByCode(childAccount, CorporateEntity.MAL);
		account2 = 				
		        externalAccountService.getOpenCustomerAccountByCode(siblingAccount, CorporateEntity.MAL);
		assertTrue("Two accounts that are siblings were determined unrelated", externalAccountService.isAccountsReleated(account1, account2));			
		
		//Two accounts with different parents.
		account1 = 
				externalAccountService.getOpenCustomerAccountByCode(siblingAccount, CorporateEntity.MAL);
		account2 = 				
		        externalAccountService.getOpenCustomerAccountByCode(customerAccount, CorporateEntity.MAL);
		assertFalse("Two unrelated accounts were determined related", externalAccountService.isAccountsReleated(account1, account2));			
	}

	@Ignore 
	public void testfindOpenCustomerAccountsByNameOrCode(){				

		final String BAD_CODE = customerAccount.concat("x");
		final String NUMERIC_NAME = "1360 Homer";
		final String WILDCARD_CODE = customerAccount.substring(0, 7) + "%" + customerAccount.substring(7);

		assertEquals("Failed to find exact match using code", 
				externalAccountService.findOpenCustomerAccountsByNameOrCode(customerAccount, CorporateEntity.MAL, PAGE).get(0).getAccountName(), customerAccountName);
		
		assertTrue("Failed to find matches using partial name", 
				externalAccountService.findOpenCustomerAccountsByNameOrCode(customerAccountNameFuzzy, CorporateEntity.MAL, PAGE).size() == 2);
		
		assertTrue("Matched on invalid name value", 
				externalAccountService.findOpenCustomerAccountsByNameOrCode(BAD_CODE, CorporateEntity.MAL, PAGE).isEmpty());
		
		assertEquals("Failed to match on valid numeric name", 
				externalAccountService.findOpenCustomerAccountsByNameOrCode(NUMERIC_NAME, CorporateEntity.MAL, PAGE).get(0).getAccountName(), NUMERIC_NAME);
		
		assertEquals("Failed to find wildcard match using code", 
				externalAccountService.findOpenCustomerAccountsByNameOrCode(WILDCARD_CODE, CorporateEntity.MAL, PAGE).get(0).getAccountName(), customerAccountName);

	}
	
	@Test
	public void testHasActiveDriverInRegion(){
		final String ACCOUNT_CODE = "00005679"; //ArjoHuntleigh Inc.
		final String COUNTRY = "USA";
		final String REGION_WITH_DRIVER = "OH";
		final String REGION_WITH_NO_DRIVER = "NM";
		
		boolean found = false;
				
		found = externalAccountService.hasActiveDriverInRegion(ACCOUNT_CODE, COUNTRY, REGION_WITH_NO_DRIVER);
		assertFalse("Found active unit/driver in unregistered region " + REGION_WITH_NO_DRIVER, found);
		
		found = externalAccountService.hasActiveDriverInRegion(ACCOUNT_CODE, COUNTRY, REGION_WITH_DRIVER);
		assertTrue("Did not find active unit/driver in registered region " + REGION_WITH_DRIVER, found);		
	}
	
	@Test
	public void testGetRelatedAccounts(){
		final String ACCOUNT_CODE = "00027393";		
		List<ExternalAccount> externalAccounts;
		
		externalAccounts = externalAccountService.getRelatedAccounts(ACCOUNT_CODE);
	
		assertTrue("Zero external accounts where returned", externalAccounts.size() > 0);		
		
	}

}
