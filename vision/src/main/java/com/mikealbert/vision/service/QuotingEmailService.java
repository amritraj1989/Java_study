package com.mikealbert.vision.service;

import com.mikealbert.data.entity.QuoteRequest;

/**
 * Public Interface implemented by {@link com.mikealbert.vision.service.QuotingEmailServiceImpl}.
 * 
 * @see com.mikealbert.vision.service.QuotingEmailServiceImpl
 * */
public interface QuotingEmailService {		
	public void emailQuoteRequestStatusChange(QuoteRequest quoteRequest) throws Exception;	
	
}
