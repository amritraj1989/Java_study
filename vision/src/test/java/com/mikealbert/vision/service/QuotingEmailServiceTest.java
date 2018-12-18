package com.mikealbert.vision.service;

import javax.annotation.Resource;

import org.junit.Test;

import com.mikealbert.data.entity.QuoteRequest;
import com.mikealbert.data.entity.QuoteRequestVehicle;
import com.mikealbert.data.enumeration.CorporateEntity;
import com.mikealbert.data.enumeration.QuoteRequestStatusEnum;
import com.mikealbert.data.enumeration.QuoteRequestTypeEnum;
import com.mikealbert.exception.MalException;
import com.mikealbert.service.CustomerAccountService;
import com.mikealbert.service.QuoteRequestService;
import com.mikealbert.testing.BaseTest;

import java.util.ArrayList;
import java.util.Date;

public class QuotingEmailServiceTest extends BaseTest{
	@Resource QuotingEmailService quotingEmailService;
	@Resource QuoteRequestService quoteRequestService;
	@Resource CustomerAccountService customerAccountService;
	
	static final String USERNAME = "UNIT_TEST";
	static final String CUSTOMER_ACCOUNT = "00000423";
	static final Long C_ID = 1L;		
	
	
	@Test
	public void testEmailQuoteRequestStatusChange() throws Exception{
		QuoteRequest qrq = new QuoteRequest();
		qrq.setQrqId(-1L);
		qrq.setSubmittedBy("SIBLEY_W");
		qrq.setSubmittedDate(new Date());
		qrq.setCompletedDate(new Date());
		qrq.setDueDate(new Date());
		qrq.setQuoteRequestType(quoteRequestService.getQuoteRequestType(QuoteRequestTypeEnum.IMM_NEED_CLIENT));
		qrq.setQuoteRequestStatus(quoteRequestService.getQuoteRequestStatus(QuoteRequestStatusEnum.COMPLETED));
		qrq.setClientAccount(customerAccountService.getCustomerAccount(CUSTOMER_ACCOUNT, CorporateEntity.fromCorpId(C_ID)));
		qrq.setQuoteRequestVehicles(new ArrayList<QuoteRequestVehicle>());
		qrq.getQuoteRequestVehicles().add(new QuoteRequestVehicle());
		qrq.getQuoteRequestVehicles().get(0).setVehicleDescription("2017 Dodge Challenger SRT Hellcat");
		quotingEmailService.emailQuoteRequestStatusChange(qrq);
	}
	
}
