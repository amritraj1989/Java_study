package com.mikealbert.service;

import static org.junit.Assert.*;

import java.util.ArrayList;
import javax.annotation.Resource;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import com.mikealbert.data.dao.ExternalAccountDAO;
import com.mikealbert.data.dao.QuoteRequestDAO;
import com.mikealbert.data.dao.QuoteRequestStatusDAO;
import com.mikealbert.data.dao.QuoteRequestTypeDAO;
import com.mikealbert.data.entity.ObjectLogBook;
import com.mikealbert.data.entity.OnbaseUploadedDocs;
import com.mikealbert.data.entity.QuoteRequest;
import com.mikealbert.data.entity.QuoteRequestQuote;
import com.mikealbert.data.entity.QuoteRequestVehicle;
import com.mikealbert.data.enumeration.LogBookTypeEnum;
import com.mikealbert.data.enumeration.QuoteRequestStatusEnum;
import com.mikealbert.data.enumeration.QuoteRequestTypeEnum;
import com.mikealbert.testing.BaseTest;
import com.mikealbert.util.MALUtilities;


public class QuoteRequestServiceTest extends BaseTest{
	@Resource QuoteRequestDAO quoteRequestDAO;
	@Resource QuoteRequestStatusDAO quoteRequestStatusDAO;
	@Resource QuoteRequestTypeDAO quoteRequestTypeDAO;	
	@Resource QuoteRequestService quoteRequestService;
	@Resource ExternalAccountDAO externalAccountDAO;
	@Resource PlateTypeCodeService plateTypeCodeService;
	@Resource QuotationProfileService quotationProfileService;
	@Resource LogBookService logBookService;
		
	static final String REQUESTOR_A = "JUNIT_A";
	static final String REQUESTOR_B = "JUNIT_B";	
	static final String CLIENT_ACCOUNT_CODE = "00000967";
	static final long CLIENT_C_ID = 1L;	
	static final String CLIENT_ACCOUNT_TYPE = "C";	
	static final String DRIVER_ZIP_CODE = "90210";
	static final String VEHICLE_DESCRIPTION = "2017 BMW M6";
	static final long LEASE_TERM = 12L;
	static final String BLAH = "BLAH";	
	
	
	QuoteRequest quoteRequest;
	
	@Before
	public void initializeTest() {
		this.quoteRequest = new QuoteRequest();
		
		this.quoteRequest.setQuoteRequestQuotes(new ArrayList<QuoteRequestQuote>());
		this.quoteRequest.getQuoteRequestQuotes().add(new QuoteRequestQuote());
		this.quoteRequest.getQuoteRequestQuotes().get(0).setQuoteRequest(this.quoteRequest);
		
		this.quoteRequest.setQuoteRequestVehicles(new ArrayList<QuoteRequestVehicle>());
		this.quoteRequest.getQuoteRequestVehicles().add(new QuoteRequestVehicle());
		this.quoteRequest.getQuoteRequestVehicles().get(0).setQuoteRequest(this.quoteRequest);		
		this.quoteRequest.getQuoteRequestVehicles().get(0).setOnbaseUploadedDocs(new ArrayList<OnbaseUploadedDocs>());
		this.quoteRequest.setReturningUnitYN("N");
		this.quoteRequest.setManufacturerIncentiveYN("N");
		this.quoteRequest.setContactDriverYN("N");
	}
	
	@Test
	public void testSave() throws Exception{		
		this.quoteRequest.setQuoteRequestType(quoteRequestService.getQuoteRequestType(QuoteRequestTypeEnum.IMM_NEED_LOCATE));
		this.quoteRequest.setClientAccount(externalAccountDAO.findByAccountCodeAndAccountTypeAndCId(CLIENT_ACCOUNT_CODE, CLIENT_ACCOUNT_TYPE, CLIENT_C_ID));
		this.quoteRequest = quoteRequestService.save(this.quoteRequest, REQUESTOR_A);	
		
		assertNotNull("Quote request was not saved", this.quoteRequest.getQrqId());
	}
	
	@Ignore
	@Test
	public void testSubmit() throws Exception{
		this.quoteRequest.setQuoteRequestType(quoteRequestService.getQuoteRequestType(QuoteRequestTypeEnum.IMM_NEED_LOCATE));
		this.quoteRequest.setClientAccount(externalAccountDAO.findByAccountCodeAndAccountTypeAndCId(CLIENT_ACCOUNT_CODE, CLIENT_ACCOUNT_TYPE, CLIENT_C_ID));
		this.quoteRequest = quoteRequestService.save(this.quoteRequest, REQUESTOR_A);	
		em.clear();
		
		this.quoteRequest = quoteRequestService.getQuoteRequestVO(this.quoteRequest.getQrqId()).getQuoteRequest();
		this.quoteRequest.setDriverZipCode(DRIVER_ZIP_CODE);		
		this.quoteRequest.getQuoteRequestVehicles().get(0).setVehicleDescription(VEHICLE_DESCRIPTION);
		this.quoteRequest.getQuoteRequestVehicles().get(0).setPlateTypeCode(plateTypeCodeService.getPlateTypeCodeList().get(0));
		this.quoteRequest.getQuoteRequestVehicles().get(0).setQuoteRequest(this.quoteRequest);
		this.quoteRequest.getQuoteRequestQuotes().get(0).setQuotationProfile(quotationProfileService.fetchCustomerQuotationProfiles(CLIENT_C_ID, CLIENT_ACCOUNT_TYPE, CLIENT_ACCOUNT_CODE).get(0));
		this.quoteRequest.getQuoteRequestQuotes().get(0).setLeaseTerm(LEASE_TERM);
		this.quoteRequest.getQuoteRequestQuotes().get(0).setQuoteRequest(this.quoteRequest);
		this.quoteRequest = quoteRequestService.submit(this.quoteRequest, REQUESTOR_A);
		
		assertEquals("Quote request was not submitted", this.quoteRequest.getQuoteRequestStatus().getCode(), QuoteRequestStatusEnum.SUBMITTED.getCode());		
	}
	
	@Ignore
	@Test
	public void testComplete() throws Exception{
		this.quoteRequest.setQuoteRequestType(quoteRequestService.getQuoteRequestType(QuoteRequestTypeEnum.IMM_NEED_LOCATE));
		this.quoteRequest.setClientAccount(externalAccountDAO.findByAccountCodeAndAccountTypeAndCId(CLIENT_ACCOUNT_CODE, CLIENT_ACCOUNT_TYPE, CLIENT_C_ID));
		this.quoteRequest = quoteRequestService.save(this.quoteRequest, REQUESTOR_A);	
		em.clear();
		
		this.quoteRequest = quoteRequestService.getQuoteRequestVO(this.quoteRequest.getQrqId()).getQuoteRequest();
		this.quoteRequest.setDriverZipCode(DRIVER_ZIP_CODE);		
		this.quoteRequest.getQuoteRequestVehicles().get(0).setVehicleDescription(VEHICLE_DESCRIPTION);
		this.quoteRequest.getQuoteRequestVehicles().get(0).setPlateTypeCode(plateTypeCodeService.getPlateTypeCodeList().get(0));
		this.quoteRequest.getQuoteRequestVehicles().get(0).setQuoteRequest(this.quoteRequest);
		this.quoteRequest.getQuoteRequestQuotes().get(0).setQuotationProfile(quotationProfileService.fetchCustomerQuotationProfiles(CLIENT_C_ID, CLIENT_ACCOUNT_TYPE, CLIENT_ACCOUNT_CODE).get(0));
		this.quoteRequest.getQuoteRequestQuotes().get(0).setLeaseTerm(LEASE_TERM);
		this.quoteRequest.getQuoteRequestQuotes().get(0).setQuoteRequest(this.quoteRequest);
		this.quoteRequest = quoteRequestService.submit(this.quoteRequest, REQUESTOR_A);
		em.clear();

		this.quoteRequest = quoteRequestService.getQuoteRequestVO(this.quoteRequest.getQrqId()).getQuoteRequest();
		this.quoteRequest = quoteRequestService.complete(this.quoteRequest, REQUESTOR_A);		
		
		
		assertEquals("Quote request was not completed", this.quoteRequest.getQuoteRequestStatus().getCode(), QuoteRequestStatusEnum.COMPLETED.getCode());		
	}
	
	@Ignore
	@Test
	public void testRework() throws Exception{
		ObjectLogBook olb;
		
		this.quoteRequest.setQuoteRequestType(quoteRequestService.getQuoteRequestType(QuoteRequestTypeEnum.IMM_NEED_LOCATE));
		this.quoteRequest.setClientAccount(externalAccountDAO.findByAccountCodeAndAccountTypeAndCId(CLIENT_ACCOUNT_CODE, CLIENT_ACCOUNT_TYPE, CLIENT_C_ID));
		this.quoteRequest = quoteRequestService.save(this.quoteRequest, REQUESTOR_A);	
		em.clear();
		
		this.quoteRequest = quoteRequestService.getQuoteRequestVO(this.quoteRequest.getQrqId()).getQuoteRequest();
		this.quoteRequest.setDriverZipCode(DRIVER_ZIP_CODE);		
		this.quoteRequest.getQuoteRequestVehicles().get(0).setVehicleDescription(VEHICLE_DESCRIPTION);
		this.quoteRequest.getQuoteRequestVehicles().get(0).setPlateTypeCode(plateTypeCodeService.getPlateTypeCodeList().get(0));
		this.quoteRequest.getQuoteRequestVehicles().get(0).setQuoteRequest(this.quoteRequest);
		this.quoteRequest.getQuoteRequestQuotes().get(0).setQuotationProfile(quotationProfileService.fetchCustomerQuotationProfiles(CLIENT_C_ID, CLIENT_ACCOUNT_TYPE, CLIENT_ACCOUNT_CODE).get(0));
		this.quoteRequest.getQuoteRequestQuotes().get(0).setLeaseTerm(LEASE_TERM);
		this.quoteRequest.getQuoteRequestQuotes().get(0).setQuoteRequest(this.quoteRequest);
		this.quoteRequest = quoteRequestService.submit(this.quoteRequest, REQUESTOR_A);
		em.clear();

		this.quoteRequest = quoteRequestService.getQuoteRequestVO(this.quoteRequest.getQrqId()).getQuoteRequest();		
		this.quoteRequest = quoteRequestService.rework(this.quoteRequest, BLAH, REQUESTOR_B);
		
		olb = logBookService.getObjectLogBook(this.quoteRequest, LogBookTypeEnum.TYPE_QUOTE_REQEUST_NOTES);
		
		assertTrue("Rework Quote Request status update failed", this.quoteRequest.getQuoteRequestStatus().getCode().equals(QuoteRequestStatusEnum.SAVED.getCode()));
		assertTrue("Rework Quote Request null out fields failed", MALUtilities.isEmpty(this.quoteRequest.getSubmittedDate()) && MALUtilities.isEmpty(this.quoteRequest.getDueDate()));
		assertTrue("Rework Quote Request clear quotes failed", MALUtilities.isEmpty(this.quoteRequest.getQuoteRequestQuotes().get(0).getQuoId()));
		assertTrue("Rework Quote Request requestor change failed", this.quoteRequest.getSubmittedBy().equals(REQUESTOR_B));	
		assertTrue("Rework Quote Request note creation failed", !MALUtilities.isEmpty(olb.getLogBookEntries()) && olb.getLogBookEntries().get(0).getDetail().contains(BLAH)); 
		assertTrue("Rework Quote Request activity creation failed", !MALUtilities.isEmpty(this.quoteRequest.getQuoteRequestActivities()));
		assertTrue("Rework Quote Request rework detection failed", quoteRequestService.isRework(this.quoteRequest));		
		
	}
	
	@Ignore
	@Test
	public void testAssignToEmployee() throws Exception{
		this.quoteRequest.setQuoteRequestType(quoteRequestService.getQuoteRequestType(QuoteRequestTypeEnum.IMM_NEED_LOCATE));
		this.quoteRequest.setClientAccount(externalAccountDAO.findByAccountCodeAndAccountTypeAndCId(CLIENT_ACCOUNT_CODE, CLIENT_ACCOUNT_TYPE, CLIENT_C_ID));
		this.quoteRequest = quoteRequestService.save(this.quoteRequest, REQUESTOR_A);	
		em.clear();
		
		this.quoteRequest = quoteRequestService.getQuoteRequestVO(this.quoteRequest.getQrqId()).getQuoteRequest();
		this.quoteRequest.setDriverZipCode(DRIVER_ZIP_CODE);		
		this.quoteRequest.getQuoteRequestVehicles().get(0).setVehicleDescription(VEHICLE_DESCRIPTION);
		this.quoteRequest.getQuoteRequestVehicles().get(0).setPlateTypeCode(plateTypeCodeService.getPlateTypeCodeList().get(0));
		this.quoteRequest.getQuoteRequestVehicles().get(0).setQuoteRequest(this.quoteRequest);
		this.quoteRequest.getQuoteRequestQuotes().get(0).setQuotationProfile(quotationProfileService.fetchCustomerQuotationProfiles(CLIENT_C_ID, CLIENT_ACCOUNT_TYPE, CLIENT_ACCOUNT_CODE).get(0));
		this.quoteRequest.getQuoteRequestQuotes().get(0).setLeaseTerm(LEASE_TERM);
		this.quoteRequest.getQuoteRequestQuotes().get(0).setQuoteRequest(this.quoteRequest);
		this.quoteRequest = quoteRequestService.submit(this.quoteRequest, REQUESTOR_A);
		em.clear();
		
		this.quoteRequest = quoteRequestService.getQuoteRequestVO(this.quoteRequest.getQrqId()).getQuoteRequest();		
		this.quoteRequest = quoteRequestService.assignToEmployee(this.quoteRequest, REQUESTOR_B);
		
		assertEquals("Quote request is not in a In Progress state", this.quoteRequest.getQuoteRequestStatus().getCode(), QuoteRequestStatusEnum.IN_PROGRESS.getCode());		
		assertEquals("Quote request was not assigned", this.quoteRequest.getAssignedTo(), REQUESTOR_B);		
				
	}
	
	@Ignore
	@Test
	public void testAssignToAccountConsultant() throws Exception{
		this.quoteRequest.setQuoteRequestType(quoteRequestService.getQuoteRequestType(QuoteRequestTypeEnum.IMM_NEED_LOCATE));
		this.quoteRequest.setClientAccount(externalAccountDAO.findByAccountCodeAndAccountTypeAndCId(CLIENT_ACCOUNT_CODE, CLIENT_ACCOUNT_TYPE, CLIENT_C_ID));
		this.quoteRequest = quoteRequestService.save(this.quoteRequest, REQUESTOR_A);	
		em.clear();
		
		this.quoteRequest = quoteRequestService.getQuoteRequestVO(this.quoteRequest.getQrqId()).getQuoteRequest();
		this.quoteRequest.setDriverZipCode(DRIVER_ZIP_CODE);		
		this.quoteRequest.getQuoteRequestVehicles().get(0).setVehicleDescription(VEHICLE_DESCRIPTION);
		this.quoteRequest.getQuoteRequestVehicles().get(0).setPlateTypeCode(plateTypeCodeService.getPlateTypeCodeList().get(0));
		this.quoteRequest.getQuoteRequestVehicles().get(0).setQuoteRequest(this.quoteRequest);
		this.quoteRequest.getQuoteRequestQuotes().get(0).setQuotationProfile(quotationProfileService.fetchCustomerQuotationProfiles(CLIENT_C_ID, CLIENT_ACCOUNT_TYPE, CLIENT_ACCOUNT_CODE).get(0));
		this.quoteRequest.getQuoteRequestQuotes().get(0).setLeaseTerm(LEASE_TERM);
		this.quoteRequest.getQuoteRequestQuotes().get(0).setQuoteRequest(this.quoteRequest);
		this.quoteRequest = quoteRequestService.submit(this.quoteRequest, REQUESTOR_A);
		em.clear();
		
		this.quoteRequest = quoteRequestService.getQuoteRequestVO(this.quoteRequest.getQrqId()).getQuoteRequest();		
		this.quoteRequest = quoteRequestService.assignToAccountConsultant(this.quoteRequest, "TE");
		
		assertEquals("Quote request is not in a In Progress state", this.quoteRequest.getQuoteRequestStatus().getCode(), QuoteRequestStatusEnum.IN_PROGRESS.getCode());		
		assertNotNull("Quote request was not assigned", this.quoteRequest.getAssignedTo());		
		
		
		
	}	
		
}

