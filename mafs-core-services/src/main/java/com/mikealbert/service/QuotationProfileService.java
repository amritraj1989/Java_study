package com.mikealbert.service;

import java.io.Serializable;
import java.util.List;

import com.mikealbert.data.entity.ExternalAccount;
import com.mikealbert.data.entity.InterestTypeCode;
import com.mikealbert.data.entity.QuotationProfile;
import com.mikealbert.data.entity.ReviewFrequencyCode;

public interface QuotationProfileService extends Serializable{
	
	public List<QuotationProfile> fetchCustomerQuotationProfiles(long corpId, String accountType, String accountCode);
	
	public List<QuotationProfile> getQuotationProfilesByLeaseElement(long leaseElementId, ExternalAccount parentAccount);
	
	public List<QuotationProfile> getGenericQuotationProfiles();	
	
	public Long countQuotationProfilesByLeaseElement(long leaseElementId, ExternalAccount parentAccount);
	
	public InterestTypeCode getInterestTypeByCode(String interestType);
	
	public ReviewFrequencyCode getReviewFrequencyByCode(String reviewFrequencyCode);
	
	public  String getProductTypeByProfile(Long qprId);
	
	public QuotationProfile getQuotationProfileById(Long qprId);
	
	public QuotationProfile getQuotationProfileByProfileCode(String profileCode);
	
	public boolean isProfileValidForAccount(Long corporateId, String accountCode, Long profileId);
	
	public String getQuotationProfileStatus(Long qmdId);
	
	public List<QuotationProfile> fetchCustomerQuotationProfilesByCriteria(Long cId, String accountType, String accountCode, String productCode);


	
}