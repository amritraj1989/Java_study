package com.mikealbert.service;

import java.util.Calendar;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mikealbert.common.MalLogger;
import com.mikealbert.common.MalLoggerFactory;
import com.mikealbert.data.dao.EmailRequestLogDAO;
import com.mikealbert.data.entity.EmailRequestLog;

/**
 * Implementation of {@link com.mikealbert.EmailRequestService.service.EmailRequestProcessorService}
 */
@Service("emailRequestProcessorService")
public class PurchasingEmailRequestProcessorServiceImpl implements EmailRequestProcessorService {
	@Resource EmailRequestLogDAO emailRequestLogDAO;
	private MalLogger logger = MalLoggerFactory.getLogger(this.getClass());
	
	@Override
	@Transactional(rollbackFor=Exception.class)
	public void sendToAccount(Long cId,  String accountType, String accountCode, List<Long> emailRequestIds) {
		// TODO Auto-generated method stub
		EmailRequestLog emailRequest;

		//TODO Retrieve the account's PDF documents
		
		//TODO Number of emails by max attachment size
		
		//TODO Send email
		
		for(Long erlId: emailRequestIds) {
			emailRequest = emailRequestLogDAO.findById(erlId).orElse(null);			
			emailRequest.setStatus("SUCCESS");
			emailRequest.setProcessedDate(Calendar.getInstance().getTime());
			emailRequestLogDAO.save(emailRequest);
		}		
	}	
}
