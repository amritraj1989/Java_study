package com.mikealbert.service;

import java.util.List;

import org.springframework.data.domain.Pageable;

import com.mikealbert.data.entity.ExtAccAffiliate;
import com.mikealbert.data.entity.ExtAccConsultant;
import com.mikealbert.data.entity.ExternalAccount;
import com.mikealbert.data.entity.ExternalAccountGradeGroup;
import com.mikealbert.data.enumeration.CorporateEntity;
import com.mikealbert.data.vo.CustomerContactVO;
import com.mikealbert.exception.MalBusinessException;
/**
* Public Interface implemented by {@link com.mikealbert.vision.service.CustomerAccountServiceImpl} for interacting with business service methods concerning {@link com.mikealbert.data.entity.ExternalAccount}(s).
*
*  @see com.mikealbert.data.entity.ExternalAccount
 * @see com.mikealbert.vision.service.CustomerAccountServiceImpl
**/
public interface CustomerAccountService {
	
	public List<ExternalAccount> findOpenCustomerAccounts(String accountName, CorporateEntity corpEntity, Pageable page);
	public List<ExternalAccount> findOpenCustomerAccounts(String accountName, CorporateEntity corpEntity);
	
	public List<ExternalAccount> findOpenCustomerAccounts(String accountName, Pageable page);
	public List<ExternalAccount> findOpenCustomerAccounts(String accountName);

	public List<ExternalAccount> findOpenCustomerAccountsByCode(String accountCode, CorporateEntity corpEntity, Pageable page);
	public List<ExternalAccount> findOpenCustomerAccountsByCode(String accountCode, CorporateEntity corpEntity);
	
	public List<ExternalAccount> findOpenCustomerAccountsByCode(String accountCode, Pageable page);
	public List<ExternalAccount> findOpenCustomerAccountsByCode(String accountCode);
	
	public ExternalAccount getCustomerAccount(String accountCode,CorporateEntity corpEntity);
	public ExternalAccount getCustomerAccountWithWebSiteUsers(String accountCode,CorporateEntity corpEntity);
	
	public ExternalAccount getOpenCustomerAccountByCode(String accountCode,CorporateEntity corpEntity);
	public ExternalAccount getOpenCustomerAccountByCode(String accountCode);
	public ExternalAccount getOpenCustomerAccountByName(String accountName, CorporateEntity corpEntity);	
	public long getOpenCustomerCountByName(String accountName);
	public long getOpenCustomerCountByName(String accountName,CorporateEntity corpEntity);
	
	public boolean isAccountsReleated(ExternalAccount account1, ExternalAccount account2);
	public boolean hasActiveDriverInRegion(String accountCode, String countryCode, String regionCode);	

	public List<CustomerContactVO> findCustomerContacts(String accountName, CorporateEntity corpEntity, Pageable page, boolean includeClosedAccounts);

	public List<ExternalAccount> findOpenCustomerAccountsByNameOrCode(String searchValue, CorporateEntity corpEntity, Pageable page);
	public Long getOpenCustomerAccountsByNameOrCodeCount(String searchValue, CorporateEntity corpEntity);
	public long getOpenCustomerAccountsByCodeCount(String accountCode, CorporateEntity corpEntity);
	public long getOpenCustomerCountByCode(String accountCode, CorporateEntity corpEntity);
	
	public List<ExternalAccount> getRelatedAccounts(String accountCode);
	public List<String> getReleatedAccountCodes(String accountCode);
	public List<ExternalAccount> getChildAccounts(ExternalAccount account);
	public ExternalAccount getParentAccount(ExternalAccount account);
	
	public ExternalAccount getCustomerAccount(CorporateEntity cId, String accountType, String accountCode);
	
	public List<ExternalAccount> findAllCustomerAccountsByNameOrCode(String searchValue, CorporateEntity corpEntity, Pageable page);
	public Long getAllCustomerAccountsByNameOrCodeCount(String searchValue, CorporateEntity corpEntity);
	public List<ExtAccAffiliate> findAffiliatesForAccount(ExternalAccount account);
	
	public List<ExternalAccountGradeGroup> getClientGradeGroups(ExternalAccount client);
	
	public ExtAccConsultant getDefaultExtAccConsultantByAccountAndRole(Long cId, String accountType, String accountCode, String roleType);
	public List<String> getCustomerPaymentProfiles(Long cId, String accountType, String accountCode) throws MalBusinessException;
}
