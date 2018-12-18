package com.mikealbert.data.dao;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.mikealbert.data.entity.ExternalAccount;
import com.mikealbert.data.entity.Model;
import com.mikealbert.data.vo.UpfitterSearchCriteriaVO;
import com.mikealbert.data.vo.UpfitterSearchResultVO;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.exception.MalException;

public interface ExternalAccountDAOCustom {
	public List<UpfitterSearchResultVO> findUpfitters(UpfitterSearchCriteriaVO searchCriteria, Pageable pageable, Sort sort);
	public List<UpfitterSearchResultVO> findUpfitters(Model model, UpfitterSearchCriteriaVO searchCriteria, Pageable pageable, Sort sort);
	public List<UpfitterSearchResultVO> findVendors(String searchTerm, Pageable pageable, Sort sort);
	public int findUpfitterCount(UpfitterSearchCriteriaVO searchCriteria);	
	public List<Object[]> getDealerContactDetailsList(Long cId, String accountType, String accountCode) throws MalBusinessException;
	public List<Object[]> getVendorContactDetailsList(Long cId, String accountType, String accountCode) throws MalBusinessException;
	public List<ExternalAccount>	findAccountsForClientScheduleType();
	
	public List<Object[]> getAccountAddressByType(Long cId, String accountType, String accountCode, String addressType) throws MalException;
	public List<Object[]> getTaxExemptListByAccountInfo(Long cId, String accountType, String accountCode) throws MalException;
	
	public BigDecimal getHurdleInterestRate(Long cId, Long qprId, Date effectiveDate, Long contractPeriod);	
	
	public List<String> getCustomerPaymentProfiles(Long cId, String accountType, String accountCode) throws MalBusinessException;
}
