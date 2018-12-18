package com.mikealbert.service;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

import java.util.List;

import javax.annotation.Resource;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;

import com.mikealbert.data.entity.QuotationProfile;
import com.mikealbert.testing.BaseTest;


public class QuotationProfileServiceTest extends BaseTest{
	
	@Value("${QuotationProfilesService.corpId}") long corpId;
	@Value("${QuotationProfilesService.accountType}") String accountType;
	@Value("${QuotationProfilesService.accountCode}") String accountCode;

	@Resource private QuotationProfileService quotationProfileService;
	@Resource private QuotationElementService quotationElementService;	
	
	@Ignore
	@Test
	public void testFetchCustomerQuotationProfiles(){
		
		List<QuotationProfile> quotationProfileList = null;
		
		quotationProfileList = quotationProfileService.fetchCustomerQuotationProfiles(corpId, accountType, accountCode);
		
		assertNotNull(quotationProfileList);
		assertTrue(quotationProfileList.size() == 3);
	}
	
	@Test
	public void testGetQuotationProfileStatus(){
		Long qmdId = 373505L;
		
		String qprStatus = quotationProfileService.getQuotationProfileStatus(qmdId);
		
		System.out.println("Quotation Profile Status : " + qprStatus);
	}
}
