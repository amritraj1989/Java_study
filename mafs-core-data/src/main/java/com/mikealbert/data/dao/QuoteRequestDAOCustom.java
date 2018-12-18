package com.mikealbert.data.dao;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.mikealbert.data.entity.ExternalAccount;
import com.mikealbert.data.entity.ExternalAccountPK;
import com.mikealbert.data.entity.QuotationProfile;
import com.mikealbert.data.vo.ClientConsultantsVO;
import com.mikealbert.data.vo.ClientCreditLimitsVO;
import com.mikealbert.data.vo.ClientFleetCodeVO;
import com.mikealbert.data.vo.ClientPocVO;
import com.mikealbert.data.vo.ClientPrefSupplierVO;
import com.mikealbert.data.vo.ClientQuoteRequestServiceElementVO;
import com.mikealbert.data.vo.QuoteRequestClientProfilesVO;
import com.mikealbert.data.vo.QuoteRequestQuoteModelVO;
import com.mikealbert.data.vo.QuoteRequestSearchCriteriaVO;
import com.mikealbert.data.vo.QuoteRequestSearchResultVO;


public interface QuoteRequestDAOCustom{
	
	public List<QuoteRequestSearchResultVO> findQuoteRequests(QuoteRequestSearchCriteriaVO searchCriteria, Pageable pageable, Sort sort);
	
	public int findQuoteRequestsCount(QuoteRequestSearchCriteriaVO searchCriteria);
	public List<ClientPocVO> getClientPoc(ExternalAccount ea);
	public List<ClientPrefSupplierVO> getClientPrefSupplier(ExternalAccount ea);
	public List<ClientFleetCodeVO> getClientFleetCodes(ExternalAccount ea);
	public List<ClientConsultantsVO> getClientConsultants(ExternalAccount ea);
	public List<ClientQuoteRequestServiceElementVO> getClientServiceElements(ExternalAccount ea);
	public ClientCreditLimitsVO getClientCreditLimits (ExternalAccount ea);
	public List<QuoteRequestClientProfilesVO> getClientProfiles(ExternalAccount ea);
	public List<QuoteRequestQuoteModelVO> getQuoteRequestQuoteModels(ExternalAccountPK ea, Long quoId);
	public List<QuotationProfile> getQuotationProfiles(long corpId, String accountType, String accountCode);	
}
	