package com.mikealbert.service; 

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.mikealbert.data.DataConstants;
import com.mikealbert.data.dao.QuoteRequestDAO;
import com.mikealbert.data.dao.QuoteRequestStatusDAO;
import com.mikealbert.data.dao.QuoteRequestTypeDAO;
import com.mikealbert.data.entity.QuoteRequestStatus;
import com.mikealbert.data.entity.QuoteRequestType;
import com.mikealbert.data.vo.QuoteRequestSearchCriteriaVO;
import com.mikealbert.data.vo.QuoteRequestSearchResultVO;


@Service("quoteRequestSearchService")
public class QuoteRequestSearchServiceImpl implements QuoteRequestSearchService {

	@Resource QuoteRequestDAO quoteRequestDAO;
	@Resource QuoteRequestStatusDAO quoteRequestStatusDAO;
	@Resource QuoteRequestTypeDAO quoteRequestTypeDAO;
	
	private static final Map<String, String> SORT_BY_MAPPING = new HashMap<String, String>();
	static{
		SORT_BY_MAPPING.put(SORT_BY_TOLERANCE, DataConstants.QUOTE_REQ_SRH_SORT_TOL);
		SORT_BY_MAPPING.put(SORT_BY_CLIENT, DataConstants.QUOTE_REQ_SRH_SORT_CLIENT_NAME);
		SORT_BY_MAPPING.put(SORT_BY_REQUEST_TYPE, DataConstants.QUOTE_REQ_SRH_SORT_REQUEST_TYPE);
		SORT_BY_MAPPING.put(SORT_BY_DT_REQ_SUBMITTED, DataConstants.QUOTE_REQ_SRH_SORT_DT_REQ_SUB);
		SORT_BY_MAPPING.put(SORT_BY_DUE_DATE, DataConstants.QUOTE_REQ_SRH_SORT_DUE_DATE);
		SORT_BY_MAPPING.put(SORT_BY_REQUESTOR, DataConstants.QUOTE_REQ_SRH_SORT_REQUESTOR);
		SORT_BY_MAPPING.put(SORT_BY_ASSIGNED_TO, DataConstants.QUOTE_REQ_SRH_SORT_ASSIGN_TO);
		SORT_BY_MAPPING.put(SORT_BY_REQUEST_STATUS, DataConstants.QUOTE_REQ_SRH_SORT_STATUS);
		SORT_BY_MAPPING.put(SORT_BY_QRQ_ID, DataConstants.QUOTE_REQ_SRH_SORT_QRQ_ID);		
	}
	
	@Override
	public List<QuoteRequestSearchResultVO> findQuoteRequests(QuoteRequestSearchCriteriaVO searchCriteria, Pageable page, Sort sort) {
		List<QuoteRequestSearchResultVO> vehicleRequests;
		
		vehicleRequests = quoteRequestDAO.findQuoteRequests(searchCriteria, page, sort);

		return vehicleRequests;
	}
	
	@Override
	public int findQuoteRequestsCount(QuoteRequestSearchCriteriaVO searchCriteria) {
		int count;
		
		count = quoteRequestDAO.findQuoteRequestsCount(searchCriteria);

		return count;
	}
		
	public List<QuoteRequestStatus> getAllRequestStatus() {
		return quoteRequestStatusDAO.getAllRequestStatus();
	}
	
	public List<QuoteRequestType> getAllRequestType() {
		return quoteRequestTypeDAO.findAll();
	}

	public String resolveSortByName(String columnName){
		return SORT_BY_MAPPING.get(columnName);		
	}

}
