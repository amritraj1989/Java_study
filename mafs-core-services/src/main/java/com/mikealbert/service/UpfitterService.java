package com.mikealbert.service;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.mikealbert.data.entity.DealerAccessoryPrice;
import com.mikealbert.data.entity.ExtAccAddress;
import com.mikealbert.data.entity.ExternalAccount;
import com.mikealbert.data.entity.ExternalAccountPK;
import com.mikealbert.data.entity.Model;
import com.mikealbert.data.entity.UpfitterQuote;
import com.mikealbert.data.enumeration.CorporateEntity;
import com.mikealbert.data.vo.UpfitterSearchCriteriaVO;
import com.mikealbert.data.vo.UpfitterSearchResultVO;
import com.mikealbert.exception.MalBusinessException;
/**
* Public Interface implemented by {@link com.mikealbert.vision.service.UpfitterServiceImpl} 
* for interacting with business service methods concerning: 
* 
* @see com.mikealbert.vision.service.UpfitterQuote
*  @see com.mikealbert.data.entity.ExternalAccount
*  @see com.mikealbert.data.entity.ExternalAccountPK
*  @see com.mikealbert.data.entity.ServiceProvider
*  @see com.mikealbert.vision.service.UpfitterServiceImpl
* */
public interface UpfitterService {
	public List<UpfitterSearchResultVO> searchUpfitters(UpfitterSearchCriteriaVO searchCriteria, Pageable page, Sort sort);
    public List<UpfitterSearchResultVO> searchUpfittersAccessoriesOptional(Model model, UpfitterSearchCriteriaVO searchCriteria, Pageable page, Sort sort);
	public List<UpfitterSearchResultVO> searchVendors(String searchTerm, Pageable page, Sort sort);
	public ExternalAccount getUpfitterAccount(String accountCode, CorporateEntity corporateEntity);
	
	public Long determineLeadTime(DealerAccessoryPrice dealerAccessoryPrice);
	
	public ExtAccAddress getUpfitterDefaultPostAddress(String accountCode, long cId);
	
	public String formatUpfitterAddress(UpfitterSearchResultVO upfitterVO);
	
	public List<UpfitterQuote> getUpfitterQuotes(ExternalAccount upfitter);
	
	public List<UpfitterQuote> searchTrimUpfitterQuotesForSetup(ExternalAccountPK upfitterAccountKey, Model trim, String searchCriteria, Pageable page);
	
	public List<UpfitterQuote> searchTrimUpfitterQuotesForQuoting(ExternalAccountPK upfitterAccountKey, Model trim, String searchCriteria, boolean vehicleConfigOnly, Pageable page);	
	
	public String getFormattedAddress(ExtAccAddress extAccAddress, String separatorString);
	
	public List<Object[]> getUpfitterDealerPostAddress(long cId,String accountCode); // Added for HD-252
	
	//TODO This should be refactored to centralize the core logic for determining the effective, regardless of with or without quote.
	public DealerAccessoryPrice getUpfitterEffectivePrice(List<DealerAccessoryPrice> upfitterAccessoryPrices, Model model);
	public DealerAccessoryPrice getUpfitterEffectivePriceWithoutQuote(List<DealerAccessoryPrice> upfitterAccessoryPrices, Model model);
	public DealerAccessoryPrice getEffectiveQuotedPrice(UpfitterQuote quote, Model model, List<DealerAccessoryPrice> upfitterAccessoryPrices);	
	
	public UpfitterQuote saveOrUpdateUpfitterQuote(UpfitterQuote upfitterQuote, Model model) throws MalBusinessException;	
	
	public boolean isUniqueQuoteNumber(ExternalAccount upfitterAccount, String quoteNumber);
	
	public boolean isReferencedByClientQuote(UpfitterQuote upfitterQuote);
	public boolean isReferencedByOtherTrim(UpfitterQuote upfitterQuote, Model model);
	
	public UpfitterQuote getUpfitterQuote(Long ufqId);
	public UpfitterQuote getUpfitterQuote(UpfitterQuote upfitterQuote, Model trim);	
	
	
}
