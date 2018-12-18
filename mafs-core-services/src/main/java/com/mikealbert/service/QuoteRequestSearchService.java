package com.mikealbert.service;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.mikealbert.data.entity.QuoteRequestStatus;
import com.mikealbert.data.entity.QuoteRequestType;
import com.mikealbert.data.vo.QuoteRequestSearchCriteriaVO;
import com.mikealbert.data.vo.QuoteRequestSearchResultVO;

/**
 * Public Interface implemented by {@link com.mikealbert.vision.service.QuoteRequestSearchServiceImpl} 
 *
 * @see com.mikealbert.vision.service.QuoteRequestSearchServiceImpl
 **/
public interface QuoteRequestSearchService {	
	
	static final String SORT_BY_TOLERANCE = "toleranceYN";
	static final String SORT_BY_CLIENT = "clientAccountName";
	static final String SORT_BY_REQUEST_TYPE = "requestType";
	static final String SORT_BY_DT_REQ_SUBMITTED = "dateRequestSubmitted";
	static final String SORT_BY_DUE_DATE = "dueDate";
	
	static final String SORT_BY_REQUESTOR = "requestor";
	static final String SORT_BY_ASSIGNED_TO = "assignedTo";
	static final String SORT_BY_REQUEST_STATUS = "requestStatus";
	static final String SORT_BY_QRQ_ID = "qrqId";	
	
	
	public List<QuoteRequestSearchResultVO> findQuoteRequests(QuoteRequestSearchCriteriaVO searchCriteria, Pageable page, Sort sort);
	public int findQuoteRequestsCount(QuoteRequestSearchCriteriaVO searchCriteria);
	public List<QuoteRequestStatus> getAllRequestStatus();
	public List<QuoteRequestType> getAllRequestType();
	
	public String resolveSortByName(String columnName);
	
}
