package com.mikealbert.service;

import java.util.List;

/**
* Public Interface implemented by: 
*     {@link com.mikealbert.service.PurchasingEmailRequestProcessorServiceImpl}.
* 
*  @see com.mikealbert.data.entity.EmailRequestLog
* */
public interface EmailRequestProcessorService {
	public void sendToAccount(Long cId,  String accountType, String accountCode, List<Long> emailRequestIds);	
}
