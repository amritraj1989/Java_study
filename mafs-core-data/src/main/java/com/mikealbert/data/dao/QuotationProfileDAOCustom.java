package com.mikealbert.data.dao;

import java.util.List;

import com.mikealbert.data.entity.ExternalAccount;
import com.mikealbert.data.entity.QuotationProfile;

public interface QuotationProfileDAOCustom {

	public List<QuotationProfile> fetchCustomerQuotationProfiles(long corpId, String accountType, String accountCode);
	
	public List<QuotationProfile> getQuotationProfilesByLeaseElement(long leaseElementId, ExternalAccount parentAccount);
	
	public Long countQuotationProfilesByLeaseElement(long leaseElementId, ExternalAccount parentAccount);
	
	public  String getProductTypeByProfile(Long qprId);

	public List<QuotationProfile> fetchCustomerQuotationProfilesByCriteria(Long cId, String accountType, String accountCode, String productCode);
	
}
