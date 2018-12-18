package com.mikealbert.service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mikealbert.data.dao.EmailRequestLogDAO;
import com.mikealbert.data.entity.Doc;
import com.mikealbert.data.entity.EmailRequestLog;
import com.mikealbert.data.enumeration.EmailRequestEventEnum;
import com.mikealbert.data.enumeration.EmailRequestStatusEnum;
import com.mikealbert.exception.MalException;
import com.mikealbert.util.MALUtilities;

/**
 * Implementation of {@link com.mikealbert.EmailRequestService.service.EmailRequestService}
 */
@Service("emailRequestService")
public class EmailRequestServiceImpl implements EmailRequestService {
	@Resource EmailRequestLogDAO emailRequestLogDAO;	
	
	@Override
	@Transactional(rollbackFor=Exception.class)
	public EmailRequestLog stageRequest(Doc purchaseOrder, EmailRequestEventEnum emailType, boolean scheduled, String username) {			
		EmailRequestLog request;
		
		request = new EmailRequestLog(purchaseOrder.getDocNo(), "DOC", emailType.getCode(), scheduled ? "Y" : "N", username);	

		return save(request);
	}
	
	@Transactional(rollbackFor=Exception.class)	
	public EmailRequestLog stageRequest(EmailRequestLog emailRequest){
		validateForSave(emailRequest);
		return save(emailRequest);	
	}

	@Override
	@Transactional(readOnly=true)
	public List<EmailRequestLog> stagedRequests() {
		return emailRequestLogDAO.findUnprocessedRequests();
	}
	
	@Override
	@Transactional(readOnly=true)
	public List<EmailRequestLog> stagedRequests(EmailRequestEventEnum event, boolean scheduled) {
		List<EmailRequestLog> requests;
		Date currentDate = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(currentDate);
		cal.add(Calendar.MINUTE, -15);
		Date updatedDateTime = cal.getTime();
		requests = emailRequestLogDAO.findByEventAndScheduleYN(event.getCode(), scheduled ? "Y" : "N",updatedDateTime);
		return requests;
	}
	
	@Override
	@Transactional(readOnly=true)	
	public List<EmailRequestLog> processedRequests(EmailRequestEventEnum event, Date processedDate) {		
		return emailRequestLogDAO.findByEventProccessedDate(event.getCode(), processedDate);
	}
	
	@Override
	@Transactional(rollbackFor=Exception.class)
	public void updateEmailRequestStatus(Long erlId, EmailRequestStatusEnum status, String message){
		EmailRequestLog emailRequest;
		
		emailRequest = emailRequestLogDAO.findById(erlId).orElse(null);
		emailRequest.setStatus(status.getCode());
		emailRequest.setStatusMessage(message);
		emailRequest.setProcessedDate(Calendar.getInstance().getTime());
		
		emailRequestLogDAO.saveAndFlush(emailRequest);		
	}

	@Override
	@Transactional(rollbackFor=Exception.class)
	public void updateEmailRequestStatus(List<Long> erlIds, EmailRequestStatusEnum status, String message){
		for(Long erlId : erlIds) {
			updateEmailRequestStatus(erlId, status, message);			
		}
	}
	
	@Override
	@Transactional(rollbackFor=Exception.class)	
	public void updateEmailRequest(EmailRequestLog emailRequestLog){
		EmailRequestLog erl;
		
		erl = emailRequestLogDAO.findById(emailRequestLog.getErlId()).orElse(null);
		erl.setSender(emailRequestLog.getSender());
		erl.setRecipients(emailRequestLog.getRecipients());
		erl.setStatus(emailRequestLog.getStatus());
		erl.setStatusMessage(emailRequestLog.getStatusMessage());
		erl.setProperty1(emailRequestLog.getProperty1());
		erl.setProperty2(emailRequestLog.getProperty2());
		erl.setProperty3(emailRequestLog.getProperty3());
		erl.setProperty4(emailRequestLog.getProperty4());
		erl.setProperty5(emailRequestLog.getProperty5());		
		erl.setProcessedDate(Calendar.getInstance().getTime());
		
		emailRequestLogDAO.saveAndFlush(erl);			
	}
	
	@Override
	public EmailRequestLog getEmailRequest(Long erlId) {
		return emailRequestLogDAO.findById(erlId).orElse(null);
	}
	
	private EmailRequestLog save(EmailRequestLog emailRequest) {
		EmailRequestLog request;
		
		request = emailRequestLogDAO.findByUniqueKeys(emailRequest.getObjectId(), 
				emailRequest.getObjectType(), emailRequest.getEvent());
		
		if(MALUtilities.isEmpty(request)) {
			request = emailRequest;	
		} else {
			request.setStatus(null);
			request.setStatusMessage(null);
			request.setProcessedDate(null);
			request.setUsername(emailRequest.getUsername());			
			request.setScheduledYN(emailRequest.getScheduledYN());			
		}
		
		request.setRequestDate(Calendar.getInstance().getTime());			
		request = emailRequestLogDAO.saveAndFlush(request);
		
		return request;		
	}
	
	private void validateForSave(EmailRequestLog emailRequest) {
		StringBuilder messageBuilder = new StringBuilder();
		
		if(MALUtilities.isEmpty(emailRequest.getObjectId())){
			messageBuilder.append("objectId");
		}
		
		if(MALUtilities.isEmpty(emailRequest.getObjectType())) {
			messageBuilder.append("object");
		}
		
		if(MALUtilities.isEmpty(emailRequest.getEvent())) {
			messageBuilder.append("event");
		}		
		
		if(MALUtilities.isEmpty(emailRequest.getUsername())) {
			messageBuilder.append("username");
		}		
		
		if(MALUtilities.isEmpty(emailRequest.getScheduledYN())) {
			messageBuilder.append("scheduledYN");
		}
		
		if(messageBuilder.length() > 0) {
			throw new MalException("generic.error", new String[]{"Missing " + messageBuilder.toString()} );
		}
	}
	
}
