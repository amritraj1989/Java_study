package com.mikealbert.service;

import java.util.List;

import com.mikealbert.data.entity.AcceptanceQueueV;
import com.mikealbert.data.entity.RejectReason;
import com.mikealbert.exception.MalBusinessException;

public interface AcceptanceQueueService {
	
	public List<AcceptanceQueueV> getAcceptanceQueueList();
	
	public void rejectQuoteFromAcceptanceQueue(Long qmdId, String rejectReasonCode) throws MalBusinessException;
	public String acceptQuoteFromAcceptanceQueue( AcceptanceQueueV acceptanceQueueV, String loggedInUser) throws Exception;	
	public List<RejectReason> getRejectReasons();
	public void rejectOutstandingOeQuoteFromAcceptanceQueue(Long qmdId) throws MalBusinessException;
	public void rejectOutstandingOeEtQuoteFromAcceptanceQueue(Long fmsId) throws MalBusinessException;

}
