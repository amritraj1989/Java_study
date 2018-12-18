package com.mikealbert.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mikealbert.data.dao.DriverDAO;
import com.mikealbert.data.dao.DriverGradeDAO;
import com.mikealbert.data.dao.ExtAccAffiliateDAO;
import com.mikealbert.data.dao.ExtAccConsultantDAO;
import com.mikealbert.data.dao.ExternalAccountDAO;
import com.mikealbert.data.dao.ExternalAccountGradeGroupDAO;
import com.mikealbert.data.entity.Contact;
import com.mikealbert.data.entity.Driver;
import com.mikealbert.data.entity.DriverAddress;
import com.mikealbert.data.entity.ExtAccAffiliate;
import com.mikealbert.data.entity.ExtAccConsultant;
import com.mikealbert.data.entity.ExternalAccount;
import com.mikealbert.data.entity.ExternalAccountGradeGroup;
import com.mikealbert.data.entity.ExternalAccountPK;
import com.mikealbert.data.entity.WebsiteUser;
import com.mikealbert.data.enumeration.CorporateEntity;
import com.mikealbert.data.vo.CustomerContactVO;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.exception.MalException;

/**
 * Implementation of {@link com.mikealbert.vision.service.CustomerAccountService}
 */
@Service("externalAccountService")
@Transactional
public class CustomerAccountServiceImpl implements CustomerAccountService {
	@Resource ExternalAccountDAO externalAccountDAO;
	@Resource DriverGradeDAO driverGradeDAO;
	@Resource DriverDAO driverDAO;
	@Resource ExtAccAffiliateDAO extAccAffiliateDAO;
	@Resource ContactService contactService;
	@Resource ExternalAccountGradeGroupDAO externalAccountGradeGroupDAO;
	@Resource ExtAccConsultantDAO extAccConsultantDAO;

	public static final String DEFAULT_IND_SET = "Y";
	
	/**
	 * Open Mike Albert Leasing Customer Accounts are the only type of accounts that can normally be associated with
	 * a unit, quote (lease), driver, etc; this method should be used to return customer accounts in the most typical usage scenarios<br>
	 * 
	 * This method has an explicit wildcard match on the end of the customer name<br>
	 * 
	 * This method is an overload that offers paging functionality<br>
	 * Used by:<br>
	 * 1) Client LOV
	 * @param accountName String used for finding open customer accounts
	 * @param page Used for paging
	 * @return List of open customer accounts found from supplied customer account name string
	 */
	public List<ExternalAccount> findOpenCustomerAccounts(String accountName, Pageable page){
		return findOpenCustomerAccounts(accountName,CorporateEntity.MAL, page);
	}
	
	/**
	 * Open Mike Albert Leasing Customer Accounts are the only type of accounts that can normally be associated with
	 * a unit, quote (lease), driver, etc; this method should be used to return customer accounts in the most typical usage scenarios<br>
	 * 
	 * This method has an explicit wildcard match on the end of the customer name<br>
	 * Used by:<br>
	 * 1) Client LOV
	 * @param  accountName String used for finding open customer accounts
	 * @return List of open customer accounts found from supplied customer account name string
	 */
	public List<ExternalAccount> findOpenCustomerAccounts(String accountName){
		return findOpenCustomerAccounts(accountName,CorporateEntity.MAL);
	}

	/**
	 * Open Mike Albert Leasing Customer Accounts are the only type of accounts that can normally be associated with
	 * a unit, quote (lease), driver, etc; this method should be used to return customer accounts in the most typical usage scenarios<br>
	 * 
	 * This method has an explicit wildcard match on the end of the customer code<br>
	 * 
	 * This method is an overload that offers paging functionality.<br>
	 * Used by:<br>
	 * 1) Client LOV
	 * @param accountCode String used for finding open customer accounts
	 * @param page Used for paging
	 * @return List of customer account found from supplied account code string
	 */
	public List<ExternalAccount> findOpenCustomerAccountsByCode(String accountCode, Pageable page){
		return findOpenCustomerAccountsByCode(accountCode,CorporateEntity.MAL,page);
	}
	
	/**
	 * Open Mike Albert Leasing Customer Accounts are the only type of accounts that can normally be associated with
	 * a unit, quote (lease), driver, etc..; this method should be used to return customer accounts in the most typical usage scenarios<br>
	 * 
	 * This method has an explicit wildcard match on the end of the customer code<br>
	 * 
	 * This method is an overload that offers paging functionality<br>
	 * Used by:<br>
	 * 1) Client LOV
	 * @param accountCode String used for finding open customer accounts
	 * @return List of customer account found from supplied account code string
	 */
	public List<ExternalAccount> findOpenCustomerAccountsByCode(String accountCode){
		return this.findOpenCustomerAccountsByCode(accountCode, CorporateEntity.MAL);
	}
	
	
	/**
	 * This method will find a Mike Albert Leasing Customer Account (not suppliers nor LTD / CASHBOOK accounts).<br>
	 * Used by:<br>
	 * 1) Client LOV
	 * @param accountCode The value used to find customer account
	 * @return Customer Account that matches the supplied account code
	 */
	public ExternalAccount getOpenCustomerAccountByCode(String accountCode) {
		return getOpenCustomerAccountByCode(accountCode,CorporateEntity.MAL);
	}

	/**
	 * This method will get the total count of open mike albert customer accounts (not suppliers nor LTD / CASHBOOK accounts) by account name pattern<br>
	 * 
	 * This method has an explicit wildcard match on the end of the customer code<br>
	 * Used By:<br>
	 * 1) Client LOV
	 * @param accountName The value used to find customer accounts
	 * @return Number of customers found using input account name string
	 */
	public long getOpenCustomerCountByName(String accountName) {
		return this.getOpenCustomerCountByName(accountName, CorporateEntity.MAL);
	}

	
	/**
	 * Open Customer Accounts can exists for Mike Albert Leasing of other legal entities (in theory) so within the Vision system (internally)
	 * this method should be used to return a customer account for use with a  unit, quote (lease), driver, etc<br>
	 * 
	 * This method has an explicit wildcard match on the end of the customer name<br>
	 * 
	 * This method is an overload that offers paging functionality.<br>
	 * Used By:<br>
	 * 1) ClientLOV
	 * @param accountName The value used to find customer accounts
	 * @param corpEntity The users business entity
	 * @param page Used for paging
	 * @return List of customer accounts
	 */
	public List<ExternalAccount> findOpenCustomerAccounts(String accountName,
			CorporateEntity corpEntity, Pageable page) {
		try {
			
			if(!accountName.endsWith("%") && !accountName.matches("\\%")){
				accountName = "%" + accountName + "%";
			}
			
			return externalAccountDAO.findByAccountNameAndAccountStatusAndAccountTypes(accountName,ExternalAccountDAO.ACC_STATUS_OPEN, corpEntity.getCorpId(),page, new ArrayList<String>() {{add(ExternalAccountDAO.ACCOUNT_TYPE_CUSTOMER);}});
		} catch (Exception ex) {
			throw new MalException("generic.error.occured.while", 
					new String[] { "finding Accounts by Account Name and Account Status" }, ex);			
		}
	}

	/**
	 * Open Customer Accounts can exists for Mike Albert Leasing of other legal entities (in theory) so within the Vision system (internally)
	 * this method should be used to return a customer account for use with a  unit, quote (lease), driver, etc<br>
	 * 
	 * This method has an explicit wildcard match on the end of the customer name<br>
	 * Used By: <br>
	 * 1) ClientLOV
	 * @param accountName The value used to find customer accounts
	 * @param corpEntity The users business entity
	 * @return List of customer accounts
	 */
	public List<ExternalAccount> findOpenCustomerAccounts(String accountName,
			CorporateEntity corpEntity) {
		try {
			
			if(!accountName.endsWith("%") && !accountName.matches("\\%")){
				accountName = accountName + "%";
			}
			
			return externalAccountDAO.findByAccountNameAndAccountStatusAndAccountTypes(accountName,ExternalAccountDAO.ACC_STATUS_OPEN, corpEntity.getCorpId(), new ArrayList<String>() {{add(ExternalAccountDAO.ACCOUNT_TYPE_CUSTOMER);}});
		} catch (Exception ex) {
			throw new MalException("generic.error.occured.while", 
					new String[] { "finding Accounts by Account Name and Account Status" }, ex);			
		}
	}

	/**
	 * Open Customer Accounts can exists for Mike Albert Leasing of other legal entities (in theory) so within the Vision system (internally)
	 * this method should be used to return a customer account for use with a  unit, quote (lease), driver, etc <br>
	 *
	 * This method has an explicit wildcard match on the end of the customer code<br>
	 * 
	 * This method is an overload that offers paging functionality.<br>
	 * Used by:
	 * 1)
	 * @param accountCode The value to find customer accounts
	 * @param corpEntity The users business entity
	 * @param page Used for paging
	 * @return List of customer accounts based on the account code provided
	 */
	public List<ExternalAccount> findOpenCustomerAccountsByCode(
			String accountCode, CorporateEntity corpEntity, Pageable page) {
		try {
			
			if(!accountCode.endsWith("%") && !accountCode.matches("\\%")){
				accountCode = "%" + accountCode + "%";
			}
			
			return externalAccountDAO.findByAccountCodeAndAccountStatusAndAccountTypes(
					accountCode,ExternalAccountDAO.ACC_STATUS_OPEN,
					corpEntity.getCorpId(),
					page,
					new ArrayList<String>() {{add(ExternalAccountDAO.ACCOUNT_TYPE_CUSTOMER);}});
		} catch (Exception ex) {
			throw new MalException("generic.error.occured.while", 
					new String[] { "finding Accounts by Account Name and Account Status" }, ex);			
		}
	}

	/**
	 * Open Customer Accounts can exists for Mike Albert Leasing of other legal entities (in theory) so within the Vision system (internally)
	 * this method should be used to return a customer account for use with a  unit, quote (lease), driver, etc..
	 *
	 * This method has an explicit wildcard match on the end of the customer code
	 * @param accountCode The value to find customer accounts
	 * @param corpEntity The users business entity
	 * @return List of customer accounts based on account code provided
	 */
	public List<ExternalAccount> findOpenCustomerAccountsByCode(
			String accountCode, CorporateEntity corpEntity) {
		try {
			
			if(!accountCode.endsWith("%") && !accountCode.matches("\\%")){
				accountCode = accountCode + "%";
			}

			return externalAccountDAO.findByAccountCodeAndAccountStatusAndAccountTypes(
					accountCode,ExternalAccountDAO.ACC_STATUS_OPEN,
					corpEntity.getCorpId(),
					new ArrayList<String>() {{add(ExternalAccountDAO.ACCOUNT_TYPE_CUSTOMER);}}
					);
		} catch (Exception ex) {
			throw new MalException("generic.error.occured.while", 
					new String[] { "finding Accounts by Account Name and Account Status" }, ex);			
		}
	}

	/**
	 * This method will get the total count of open mike albert customer accounts by account code pattern
	 * 
	 * This method has an explicit wildcard match on the end of the customer code
	 * @param accountCode The value used to find customer accounts
	 * @return Number of customers found using input account code string
	 */
	public long getOpenCustomerAccountsByCodeCount(String accountCode, CorporateEntity corpEntity) {
		return this.getOpenCustomerCountByCode(accountCode, corpEntity);
	}
	
	/**
	 * This method will find a Customer Account (not suppliers nor LTD / CASHBOOK accounts)
	 * @param accountCode The value to find customer accounts
	 * @param corpEntity The users business entity
	 * @return List of customer accounts based on account code provided
	 */
	public ExternalAccount getOpenCustomerAccountByCode(String accountCode,
			CorporateEntity corpEntity) {
		try {
			ExternalAccountPK key = new ExternalAccountPK();
			key.setCId(corpEntity.getCorpId());
			key.setAccountType(ExternalAccountDAO.ACCOUNT_TYPE_CUSTOMER);
			key.setAccountCode(accountCode);
			return externalAccountDAO.findById(key).orElse(null);
		} catch (Exception ex) {
			throw new MalException("generic.error.occured.while", 
					new String[] { "finding Accounts by Account Code and Account Status" }, ex);			
		}
	}
	
	/**
	 * This method will get the open customer account based on an exact match of the account name
	 * @param accountName The exact name of the account
	 * @param corpEntity The users business entity
	 * @return A single account that with exact account name
	 */
	public ExternalAccount getOpenCustomerAccountByName(
			String accountName, CorporateEntity corpEntity) {
		try {
			return externalAccountDAO.findByAccountNameAndAccountStatusAndAccountTypes(
					accountName, ExternalAccountDAO.ACC_STATUS_OPEN, corpEntity.getCorpId(), new ArrayList<String>() {{add(ExternalAccountDAO.ACCOUNT_TYPE_CUSTOMER);}}).get(0);
		} catch (Exception ex) {
			throw new MalException("generic.error.occured.while", 
					new String[] { "getting an open customer account by Account Name" }, ex);			
		}
	}
	

	/**
	 * This method will get the total count of open customer accounts (not suppliers nor LTD / CASHBOOK accounts) by account name pattern
	 * 
	 * This method has an explicit wildcard match on the end of the customer code
	 * @param accountName The value of the account name
	 * @param corpEntity The users business entity
	 * @return Number of customer accounts found based on provided account name
	 */
	public long getOpenCustomerCountByName(String accountName,
			CorporateEntity corpEntity) {
		
		if(!accountName.endsWith("%") && !accountName.matches("\\%")){
			accountName = "%" + accountName + "%";
		}
		
		try {
			return externalAccountDAO.getTotalAccountCount(accountName, ExternalAccountDAO.ACC_STATUS_OPEN, corpEntity.getCorpId(), new ArrayList<String>() {{add(ExternalAccountDAO.ACCOUNT_TYPE_CUSTOMER);}});
		} catch (Exception ex) {
			throw new MalException("generic.error.occured.while", 
					new String[] { "finding Accounts by Account Code and Account Status" }, ex);			
		}
	}
	
	/**
	 * This method will get the total count of open customer accounts (not suppliers nor LTD / CASHBOOK accounts) by account name pattern
	 * 
	 * This method has an explicit wildcard match on the end of the customer code
	 * @param accountCode The value to find customer accounts
	 * @param corpEntity The users business entity
	 * @return Number of customer accounts found by provided account code
	 */
	public long getOpenCustomerCountByCode(String accountCode,
			CorporateEntity corpEntity) {
		
		if(!accountCode.endsWith("%") && !accountCode.matches("\\%")){
			accountCode = "%" + accountCode + "%";
		}
		
		try {
			return externalAccountDAO.getTotalAccountCodeCount(accountCode, ExternalAccountDAO.ACC_STATUS_OPEN, corpEntity.getCorpId(), new ArrayList<String>() {{add(ExternalAccountDAO.ACCOUNT_TYPE_CUSTOMER);}});
		} catch (Exception ex) {
			throw new MalException("generic.error.occured.while", 
					new String[] { "finding Accounts by Account Code and Account Status" }, ex);			
		}
	}
	
	/**
	 * Evaluates two accounts and determines whether they are related or not. Accounts
	 * are related when either is a parent, child, or sibling of the other account.
	 * @param account1 First account
	 * @param account2 Second account
	 * @return true, when accounts are related. Otherwise, false is returned.
	 */
	public boolean isAccountsReleated(ExternalAccount account1, ExternalAccount account2){
	
		try {
			//Are the accounts the same?
			if(account1.equals(account2)){
				return true;
			}

			//Is account one the child of the account 2?
			if((account1.getParentExternalAccount() != null) && (account1.getParentExternalAccount().equals(account2))){
				return true;
			}

			//Is account one the parent of account 2?		
			account1 = externalAccountDAO.findById(account1.getExternalAccountPK()).orElse(null);		
			if((account1.getChildExternalAccounts() != null) && (account1.getChildExternalAccounts().contains(account2))){
				return true;
			}

			//Is account one a sibling of account 2?
			if((account1.getParentExternalAccount() != null) && (account1.getParentExternalAccount().equals(account2.getParentExternalAccount()))){
				return true;
			}
		} catch (Exception ex) {
			throw new MalException("generic.error.occured.while", 
					new String[] { "Determing whether two accounts are related" }, ex);			
		}
		
		return false;
	}
	
	/**
	 * Evaluates the customer's account it's drivers that are currently allocated to units with a 
	 * garaged address in a particular state/region.
	 * @param accountCode The client's account code
	 * @param countryCode MAFS does business in USA and CN
	 * @param regionCode State or region of US or CN, respectively
	 */
	public boolean hasActiveDriverInRegion(String accountCode, String countryCode, String regionCode){
		boolean isRegistered = false;
		//Retrieve external account, it's drivers (currently allocated to units), with there (garaged) address.
		ExternalAccount customer = this.getOpenCustomerAccountByCode(accountCode);

		for(Iterator<Driver> i = customer.getDriversList().iterator(); (i.hasNext() && !isRegistered);){
			Driver driver = driverDAO.getDriverByIdWithCurrentAllocations(((Driver)i.next()).getDrvId());			
			if(driver != null){		
				for(Iterator<DriverAddress> j = driver.getDriverAddressList().iterator(); (j.hasNext() && !isRegistered);){				
					DriverAddress address = (DriverAddress)j.next();					
					if(address.getRegionCode().getCountry().getCountryCode().equals(countryCode)
							&& address.getRegionCode().getRegionCodesPK().getRegionCode().equals(regionCode)
							&& address.getAddressType().getAddressType().equals("GARAGED")){
						isRegistered = true;
					}						
				}
			}
		}
		
		return isRegistered;
	}	

	/**
	 * This procedure finds the customers from the provided accountName and corpEntity.
	 * For each of the customers the primary contact and fleet admin are found and set.
	 * 
	 * @param accountName The client's account name
	 * @param corpEntity The users business entity
	 * @param page Used when paging
	 * @return List of customers 
	 */
	public List<CustomerContactVO> findCustomerContacts(String accountName,
			CorporateEntity corpEntity, Pageable page, boolean includeClosedAccounts) {
		List<CustomerContactVO> customerContacts = new ArrayList<CustomerContactVO>();
		
		List<ExternalAccount> customers = null;
		if(includeClosedAccounts){
			customers = this.findAllCustomerAccountsByNameOrCode(accountName, corpEntity, page);
		}else{
			customers = this.findOpenCustomerAccountsByNameOrCode(accountName, corpEntity, page);
		}
		
		
		for(ExternalAccount customer: customers){
			CustomerContactVO customerContact = new CustomerContactVO();
			customerContact.setCustomer(customer);
			customerContact.setPrimaryContact(contactService.determinePrimaryContact(customer.getContacts()));
			// get the fleet admin contact from the current account (Tier-2) if it is null then get the one from it's parent (Tier-1) per business rules
			Contact fleetAdminContact = contactService.determineFleetAdminContact(customer.getWebsiteUsers());
			if(fleetAdminContact != null){
				customerContact.setFleetAdminContact(fleetAdminContact);
			}else{ 	 
				if(customer.getParentExternalAccount() != null){
					customerContact.setFleetAdminContact(contactService.determineFleetAdminContact(customer.getParentExternalAccount().getWebsiteUsers()));
				}
			}
			customerContacts.add(customerContact);
		}

		return customerContacts;
	}
	
	/**
	 *  Finds a list of customer accounts based on the provided search parameter;  If the search parameter has a numeric digit as 
	 *  the first character of the searchValue parameter, then the search looks through account codes;  Otherwise it looks
	 *  through account names<br> 
	 *  
	 *  Allows for wildcard between any of the characters<br>
	 *  Used By:<br>
	 *  1) Client Decode Functionality on Driver Add Edit
	 * 
	 * @param searchValue The value used to search for customer accounts
	 * @param corpEntity The users business entity
	 * @param page Used when paging
	 * @return List of customers 
	 */
	public List<ExternalAccount> findOpenCustomerAccountsByNameOrCode(String searchValue, CorporateEntity corpEntity, Pageable page) {
		/*   "(\\%)?    1.Optional Wildcard
		 *   +\\d*      2.Required Digit
		 *   +(\\d*)?   3.Optional Digit
		 *   In order to search by code, there must be a digit. Wildcards can occur anywhere 
		 *   between digits. Wildcards can occur before the digit string and after. Client codes are 8 digits long*/
		if(searchValue.matches("(\\%)?+\\d*+(\\%)?+(\\d*)?+(\\%)?+(\\d*)?+(\\%)?+(\\d*)?+(\\%)?+(\\d*)?+(\\%)?+(\\d*)?+(\\%)?+(\\d*)?+(\\%)?+(\\d*)?+(\\%)?")) {
			return findOpenCustomerAccountsByCode(searchValue, corpEntity, page);
		} else {
			return findOpenCustomerAccounts(searchValue, corpEntity, page);
		}
	}

	/**
	 *  Finds a count of the list of customer accounts based on the provided search parameter;  If the search parameter 
	 *  has a numeric digit as the first character of the searchValue parameter; then the search looks through account
	 *  codes;  Otherwise it looks through account names <br> 
	 *  
	 *  Allows for wildcard between any of the characters<br>
	 *  Used By:<br>
	 *  1) Client Decode Functionality on Driver Add Edit
	 * 
	 * @param searchValue The value used to search for customer accounts
	 * @param corpEntity The users business entity
	 * @return Number of customer accounts found
	 */
	public Long getOpenCustomerAccountsByNameOrCodeCount(String searchValue, CorporateEntity corpEntity) {
		/*   "(\\%)?    1.Optional Wildcard
		 *   +\\d*      2.Required Digit
		 *   +(\\d*)?   3.Optional Digit
		 *   In order to search by code, there must be a digit. Wildcards can occur anywhere 
		 *   between digits. Wildcards can occur before the digit string and after. Client codes are 8 digits long*/
		if(searchValue.matches("(\\%)?+\\d*+(\\%)?+(\\d*)?+(\\%)?+(\\d*)?+(\\%)?+(\\d*)?+(\\%)?+(\\d*)?+(\\%)?+(\\d*)?+(\\%)?+(\\d*)?+(\\%)?+(\\d*)?+(\\%)?")) {
			return getOpenCustomerAccountsByCodeCount(searchValue, corpEntity);
		} else {
			return getOpenCustomerCountByName(searchValue, corpEntity);
		}
	}
	
	/**
	 * Creates a list of accounts related (Parent, Children, and siblings) to a specified account.
	 * The list also includes the specified account.
	 * @param accountCode Exact account code used to find related accounts.
	 * @return List of related customer accounts
	 */
	public List<ExternalAccount> getRelatedAccounts(String accountCode){
		List<ExternalAccount> relatedAccts = new ArrayList<ExternalAccount>();
		List<ExternalAccount> accts;
		
		accts = externalAccountDAO.findByAccountCodeAndAccountStatusAndAccountTypes(
				accountCode, ExternalAccountDAO.ACC_STATUS_OPEN, 
				CorporateEntity.MAL.getCorpId(),
				new ArrayList<String>() {{add(ExternalAccountDAO.ACCOUNT_TYPE_CUSTOMER);}} 
				);
		
		if(accts != null && accts.size() > 0){
			
			//Add passed in account to the list
			relatedAccts.add(accts.get(0));
			
			if(accts.get(0).getParentExternalAccount() != null) {
				//Add parent accounts to the list
				relatedAccts.add(accts.get(0).getParentExternalAccount());
				
				//Add sibling accounts to the list			
				relatedAccts.addAll(
						externalAccountDAO.findByParentAccountCodeAndAccountStatusAndAccountTypes(
								accts.get(0).getParentExternalAccount().getExternalAccountPK().getAccountCode(), ExternalAccountDAO.ACC_STATUS_OPEN,
								CorporateEntity.MAL.getCorpId(), new ArrayList<String>() {{add(ExternalAccountDAO.ACCOUNT_TYPE_CUSTOMER);}}));				
				
			}
			
			//Add child accounts to the list
			if(accts.get(0).getChildExternalAccounts() != null && accts.get(0).getChildExternalAccounts().size() > 0)
				relatedAccts.addAll(accts.get(0).getChildExternalAccounts());			
		}
		
		return relatedAccts;
	}
	
	
	/**
	 * Returns a list of ExternalAccounts that are children of the passed in accountCode
	 * @param account Exact ExternalAccount used to find child accounts.
	 * @return List of related customer accounts
	 */
	public List<ExternalAccount> getChildAccounts(ExternalAccount account){
		List<ExternalAccount> accts;
		List<ExternalAccount> childAccts = new ArrayList<ExternalAccount>();
		List<String> acctTypes = new ArrayList<String>();
		
		if(account != null && account.getExternalAccountPK() != null){
			acctTypes.add(account.getExternalAccountPK().getAccountType());
			accts = externalAccountDAO.findByAccountCodeAndAccountStatusAndAccountTypes(
					account.getExternalAccountPK().getAccountCode(), account.getAccStatus(), 
					account.getExternalAccountPK().getCId(),
					acctTypes
					);
				
			//Get child accounts
			if(accts.size() > 0 && accts.get(0).getChildExternalAccounts() != null && accts.get(0).getChildExternalAccounts().size() > 0){
				childAccts = accts.get(0).getChildExternalAccounts();			
			
				Collections.sort(childAccts, new Comparator<ExternalAccount>() { 
					public int compare(ExternalAccount ea1, ExternalAccount ea2) { 
						return ea1.getExternalAccountPK().getAccountCode().compareTo(ea2.getExternalAccountPK().getAccountCode()); 
					}
				});
			}
		}
		
		return childAccts;
	}
	
	/**
	 * Returns an ExternalAccount that isthe parent of the passed in accountCode
	 * @param account Exact ExternalAccount used to find parent account.
	 * @return Parent ExternalAccount
	 */
	public ExternalAccount getParentAccount(ExternalAccount account){
		List<ExternalAccount> accts;
		ExternalAccount parentAcct = new ExternalAccount();
		List<String> acctTypes = new ArrayList<String>();
		
		if(account != null && account.getExternalAccountPK() != null){
			acctTypes.add(account.getExternalAccountPK().getAccountType());
		
			accts = externalAccountDAO.findByAccountCodeAndAccountStatusAndAccountTypes(
					account.getExternalAccountPK().getAccountCode(), account.getAccStatus(), 
					account.getExternalAccountPK().getCId(),
					acctTypes);
			
			if(accts != null && accts.size() > 0){
				if(accts.get(0).getParentExternalAccount() != null) {
					parentAcct = accts.get(0).getParentExternalAccount();
				}
			}
		}
		
		return parentAcct;
	}
	
	/**
	 * Retrieves a list of account codes related to the passed in account.<br>
	 * Used By:
	 * 1) ClientLOV
	 * @param accountCode String Account code
	 * @return List of related account codes
	 */
	public List<String> getReleatedAccountCodes(String accountCode){
		List<String> relatedAccountCodes = new ArrayList<String>();	    
		List<ExternalAccount> relatedAccounts = getRelatedAccounts(accountCode);
		for(ExternalAccount account : relatedAccounts)
			relatedAccountCodes.add(account.getExternalAccountPK().getAccountCode());
		return relatedAccountCodes;		
	}
	
	/**
	 * A convenience  method used to get the customer account based on the key information.
	 * Callers will only have to pass in the info and now be concerned with initializing 
	 * a primary key.
	 * @param CorporateEntity The corporate Id also known as c_id
	 * @param String Account Type
	 * @param String Account Code
	 */
	public ExternalAccount getCustomerAccount(CorporateEntity cId, String accountType, String accountCode){		
		ExternalAccountPK externalAccountPK = new ExternalAccountPK();
		externalAccountPK.setCId(cId.getCorpId());
		externalAccountPK.setAccountType(accountType);
		externalAccountPK.setAccountCode(accountCode);
		return externalAccountDAO.findById(externalAccountPK).orElse(null);
	}

	@Override
	public ExternalAccount getCustomerAccount(String accountCode,CorporateEntity corpEntity) {
		try {
			ExternalAccountPK externalAccountPK = new ExternalAccountPK();
			externalAccountPK.setCId(corpEntity.getCorpId());
			externalAccountPK.setAccountType(ExternalAccountDAO.ACCOUNT_TYPE_CUSTOMER);
			externalAccountPK.setAccountCode(accountCode);
			
			ExternalAccount customer = externalAccountDAO.findById(externalAccountPK).orElse(null);
			return customer;
		} catch (Exception ex) {
			throw new MalException("generic.error.occured.while", new String[] { "finding a customer account " }, ex);
		}
	}

	/**
	 *  Finds All (Open and closed ) customer accounts based on the provided search parameter;  If the search parameter has a numeric digit as 
	 *  the first character of the searchValue parameter, then the search looks through account codes;  Otherwise it looks
	 *  through account names<br> 
	 *  
	 *  Allows for wildcard between any of the characters<br>
	 * 
	 * @param searchValue The value used to search for customer accounts
	 * @param corpEntity The users business entity
	 * @param page Used when paging
	 * @return List of customers 
	 */
	public List<ExternalAccount> findAllCustomerAccountsByNameOrCode(String searchValue, CorporateEntity corpEntity, Pageable page) {
		/*   "(\\%)?    1.Optional Wildcard
		 *   +\\d*      2.Required Digit
		 *   +(\\d*)?   3.Optional Digit
		 *   In order to search by code, there must be a digit. Wildcards can occur anywhere 
		 *   between digits. Wildcards can occur before the digit string and after. Client codes are 8 digits long*/
		if(searchValue.matches("(\\%)?+\\d*+(\\%)?+(\\d*)?+(\\%)?+(\\d*)?+(\\%)?+(\\d*)?+(\\%)?+(\\d*)?+(\\%)?+(\\d*)?+(\\%)?+(\\d*)?+(\\%)?+(\\d*)?+(\\%)?")) {
			return findAllCustomerAccountsByCode(searchValue, corpEntity, page);
		} else {
			return findAllCustomerAccountsByName(searchValue, corpEntity, page);
		}
	}
	
	/**
	 * Customer Accounts can exists for Mike Albert Leasing of other legal entities (in theory) so within the Vision system (internally)
	 * this method should be used to return a customer account for use with a  unit, quote (lease), driver, etc <br>
	 *
	 * This method has an explicit wildcard match on the end of the customer code<br>
	 * 
	 * This methods fetches all accounts that match by code irrespective of account status
	 * 
	 * @param accountCode The value to find customer accounts
	 * @param corpEntity The users business entity
	 * @param page Used for paging
	 * @return List of customer accounts based on the account code provided
	 */
	public List<ExternalAccount> findAllCustomerAccountsByCode(
			String accountCode, CorporateEntity corpEntity, Pageable page) {
		try {
			
			if(!accountCode.endsWith("%") && !accountCode.matches("\\%")){
				accountCode = "%" + accountCode + "%";
			}
			return externalAccountDAO.findAllByAccountCodeAndAccountTypes(accountCode, corpEntity.getCorpId(), new ArrayList<String>() {{add(ExternalAccountDAO.ACCOUNT_TYPE_CUSTOMER);}},page);
		} catch (Exception ex) {
			throw new MalException("generic.error.occured.while", 
					new String[] { "finding Accounts by Account Code" }, ex);			
		}
	}
	
	/**
	 * Customer Accounts can exists for Mike Albert Leasing of other legal entities (in theory) so within the Vision system (internally)
	 * this method should be used to return a customer account irrespective of its account status. <br>
	 * 
	 * This method has an explicit wildcard match on the end of the customer name<br>
	 * 
	 * This methods fetches all accounts that match by name irrespective of account status
	 * 
	 * Used By:<br>
	 * 1) ClientLOV
	 * @param accountName The value used to find customer accounts
	 * @param corpEntity The users business entity
	 * @param page Used for paging
	 * @return List of customer accounts
	 */
	public List<ExternalAccount> findAllCustomerAccountsByName(String accountName,
			CorporateEntity corpEntity, Pageable page) {
		try {
			
			if(!accountName.endsWith("%") && !accountName.matches("\\%")){
				accountName = "%" + accountName + "%";
			}
			return externalAccountDAO.findAllByAccountNameAndAccountTypes(accountName, corpEntity.getCorpId(), page, new ArrayList<String>() {{add(ExternalAccountDAO.ACCOUNT_TYPE_CUSTOMER);}});
		} catch (Exception ex) {
			throw new MalException("generic.error.occured.while", 
					new String[] { "finding Accounts by Account Name" }, ex);			
		}
	}
	
	/**
	 *  Finds a count of the list of customer accounts based on the provided search parameter;  If the search parameter 
	 *  has a numeric digit as the first character of the searchValue parameter; then the search looks through account
	 *  codes;  Otherwise it looks through account names <br> 
	 *  
	 *  This methods fetches count for all accounts that match by name or code irrespective of account status
	 *  
	 *  Allows for wildcard between any of the characters<br>
	 *  Used By:<br>
	 *  1) Client Decode Functionality on Driver Add Edit
	 * 
	 * @param searchValue The value used to search for customer accounts
	 * @param corpEntity The users business entity
	 * @return Number of customer accounts found
	 */
	public Long getAllCustomerAccountsByNameOrCodeCount(String searchValue, CorporateEntity corpEntity) {
		/*   "(\\%)?    1.Optional Wildcard
		 *   +\\d*      2.Required Digit
		 *   +(\\d*)?   3.Optional Digit
		 *   In order to search by code, there must be a digit. Wildcards can occur anywhere 
		 *   between digits. Wildcards can occur before the digit string and after. Client codes are 8 digits long*/
		if(searchValue.matches("(\\%)?+\\d*+(\\%)?+(\\d*)?+(\\%)?+(\\d*)?+(\\%)?+(\\d*)?+(\\%)?+(\\d*)?+(\\%)?+(\\d*)?+(\\%)?+(\\d*)?+(\\%)?+(\\d*)?+(\\%)?")) {
			return getAllCustomerAccountsByCodeCount(searchValue, corpEntity);
		} else {
			return getAllCustomersCountByName(searchValue, corpEntity);
		}
	}
	
	/**
	 * This method will get the total count of open mike albert customer accounts by account code pattern
	 * 
	 * This methods fetches count for all accounts that match by code irrespective of account status
	 * 
	 * This method has an explicit wildcard match on the end of the customer code
	 * @param accountCode The value used to find customer accounts
	 * @return Number of customers found using input account code string
	 */
	public long getAllCustomerAccountsByCodeCount(String accountCode, CorporateEntity corpEntity) {
		
		if(!accountCode.endsWith("%") && !accountCode.matches("\\%")){
			accountCode = "%" + accountCode + "%";
		}
		
		try {
			return externalAccountDAO.getAllStatusAccountCodeCount(accountCode, corpEntity.getCorpId(), new ArrayList<String>() {{add(ExternalAccountDAO.ACCOUNT_TYPE_CUSTOMER);}});
		} catch (Exception ex) {
			throw new MalException("generic.error.occured.while", 
					new String[] { "finding count for Accounts by Account Code" }, ex);			
		}
	}
	
	/**
	 * This method will get the total count of customer accounts irrespective of account status (not suppliers nor LTD / CASHBOOK accounts) by account name pattern
	 * 
	 * This method has an explicit wildcard match on the end of the customer code
	 * @param accountName The value of the account name
	 * @param corpEntity The users business entity
	 * @return Number of customer accounts found based on provided account name
	 */
	public long getAllCustomersCountByName(String accountName,
			CorporateEntity corpEntity) {
		
		if(!accountName.endsWith("%") && !accountName.matches("\\%")){
			accountName = "%" + accountName + "%";
		}
		
		try {
			return externalAccountDAO.getAllStatusAccountNameCount(accountName, corpEntity.getCorpId(), new ArrayList<String>() {{add(ExternalAccountDAO.ACCOUNT_TYPE_CUSTOMER);}});
		} catch (Exception ex) {
			throw new MalException("generic.error.occured.while", 
					new String[] { "finding count for Accounts by Account Name" }, ex);			
		}
	}
	
	@Override
	public List<ExtAccAffiliate> findAffiliatesForAccount(
			ExternalAccount account) {
		
		List<ExtAccAffiliate> list = new ArrayList<ExtAccAffiliate>();
		try{
			list  = extAccAffiliateDAO.findByExtAccount(account.getExternalAccountPK().getcId(), account.getExternalAccountPK().getAccountType(), account.getExternalAccountPK().getAccountCode());
		} catch (Exception ex) {
			throw new MalException("generic.error.occured.while", new String[] { "retrieving account affiliates" }, ex);
		}	
		return list;
	}
	
	/**
	 * Retrieves the client's grade groups
	 * 
	 * @param client ExternalAccount
	 */
	@Override
	public List<ExternalAccountGradeGroup> getClientGradeGroups(ExternalAccount client) {
		List<ExternalAccountGradeGroup> clientGradeGroups = new ArrayList<>();
		clientGradeGroups = externalAccountGradeGroupDAO.getGradeGroupByClient(client.getExternalAccountPK().getCId(), 
				client.getExternalAccountPK().getAccountType(), client.getExternalAccountPK().getAccountCode());
		return clientGradeGroups;
	}

	@Override
	public ExtAccConsultant getDefaultExtAccConsultantByAccountAndRole(Long cId, String accountType, String accountCode, String roleType) {
		
		return extAccConsultantDAO.findByExtAccountAndRoleType(cId, accountType, accountCode, roleType);
	}

	@Override
	public List<String> getCustomerPaymentProfiles(Long cId, String accountType, String accountCode) throws MalBusinessException {
		return externalAccountDAO.getCustomerPaymentProfiles(cId, accountType, accountCode);
	}

	@Override
	public ExternalAccount getCustomerAccountWithWebSiteUsers(String accountCode, CorporateEntity corpEntity) {
		try {
			ExternalAccountPK externalAccountPK = new ExternalAccountPK();
			externalAccountPK.setCId(corpEntity.getCorpId());
			externalAccountPK.setAccountType(ExternalAccountDAO.ACCOUNT_TYPE_CUSTOMER);
			externalAccountPK.setAccountCode(accountCode);
			
			ExternalAccount customer = externalAccountDAO.findById(externalAccountPK).orElse(null);
			customer.getWebsiteUsers().size();
			
			return customer;
		} catch (Exception ex) {
			throw new MalException("generic.error.occured.while", new String[] { "finding a customer account " }, ex);
		}
	}

}
