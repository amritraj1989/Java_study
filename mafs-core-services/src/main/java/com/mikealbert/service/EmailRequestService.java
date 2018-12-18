package com.mikealbert.service;

import java.util.Date;
import java.util.List;

import com.mikealbert.data.entity.Doc;
import com.mikealbert.data.entity.EmailRequestLog;
import com.mikealbert.data.entity.ExternalAccount;
import com.mikealbert.data.enumeration.EmailRequestEventEnum;
import com.mikealbert.data.enumeration.EmailRequestStatusEnum;

/**
* Public Interface implemented by: 
*     {@link com.mikealbert.service.EmailRequestServiceImpl}.
* 
*  @see com.mikealbert.data.entity.EmailRequestLog
* */
public interface EmailRequestService {
	public EmailRequestLog stageRequest(Doc purchaseOrder, EmailRequestEventEnum emailType, boolean scheduled, String username);
	public EmailRequestLog stageRequest(EmailRequestLog emailRequest);
	public List<EmailRequestLog> stagedRequests();	
	public List<EmailRequestLog> stagedRequests(EmailRequestEventEnum event, boolean scheduled);
	public List<EmailRequestLog> processedRequests(EmailRequestEventEnum event, Date processedDate);	
	public EmailRequestLog getEmailRequest(Long erlId);
	public void updateEmailRequestStatus(List<Long> erlIds, EmailRequestStatusEnum status, String message);
	public void updateEmailRequestStatus(Long erlId, EmailRequestStatusEnum status, String message);
	public void updateEmailRequest(EmailRequestLog emailRequestLog);	
}
