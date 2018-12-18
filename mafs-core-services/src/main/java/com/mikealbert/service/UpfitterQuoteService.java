package com.mikealbert.service;

import com.mikealbert.data.entity.UpfitterQuote;

/**
* Public Interface implemented by {@link com.mikealbert.vision.service.UpfitterQuoteImpl} for interacting with business service methods concerning: 
* 
*  @see com.mikealbert.data.entity.UpfitterQuote
* */
public interface UpfitterQuoteService {
	
	public UpfitterQuote getUpfitterQuote(Long ufqId);
}
