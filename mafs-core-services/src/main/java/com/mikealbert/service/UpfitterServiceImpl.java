package com.mikealbert.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mikealbert.data.DataUtilities;
import com.mikealbert.data.dao.DealerAccessoryDAO;
import com.mikealbert.data.dao.DealerAccessoryPriceDAO;
import com.mikealbert.data.dao.ExtAccAddressDAO;
import com.mikealbert.data.dao.ExternalAccountDAO;
import com.mikealbert.data.dao.QuotationDealerAccessoryDAO;
import com.mikealbert.data.dao.UpfitterQuoteDAO;
import com.mikealbert.data.dao.SupplierDAO;
import com.mikealbert.data.entity.DealerAccessory;
import com.mikealbert.data.entity.DealerAccessoryCode;
import com.mikealbert.data.entity.DealerAccessoryPrice;
import com.mikealbert.data.entity.ExtAccAddress;
import com.mikealbert.data.entity.ExternalAccount;
import com.mikealbert.data.entity.ExternalAccountPK;
import com.mikealbert.data.entity.Model;
import com.mikealbert.data.entity.QuotationDealerAccessory;
import com.mikealbert.data.entity.UpfitterQuote;
import com.mikealbert.data.enumeration.CorporateEntity;
import com.mikealbert.data.util.DisplayFormatHelper;
import com.mikealbert.data.vo.UpfitterSearchCriteriaVO;
import com.mikealbert.data.vo.UpfitterSearchResultVO;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.util.MALUtilities;

/**
 * Implementation of {@link com.mikealbert.vision.service.UpfitterService}
 */
@Service("upfitterService")
public class UpfitterServiceImpl implements UpfitterService {
	
	private final String ADDRESS_TYPE_POST = "POST";// added for HD-252
	@Resource ExternalAccountDAO externalAccountDAO;
	@Resource UpfitterQuoteDAO upfitterQuoteDAO;
	@Resource ExtAccAddressDAO extAccAddressDAO;
	@Resource DealerAccessoryDAO dealerAccessoryDAO;
	@Resource ModelService modelService;
	@Resource QuotationDealerAccessoryDAO quotationDealerAccessoryDAO;
	@Resource SupplierDAO supplierDAO; // added for HD-252
	@Resource DealerAccessoryPriceDAO dealerAccessoryPriceDAO;
	
	/**
	 * Searches for upfitters external accounts based on the search criteria. Upfitters are determined based on it
	 * having pricing against a dealer accessory that is assigned to the trim. 
	 * @param Model known as trim
	 * @param The vendor name or account code search term
	 * @param Page
	 * @param Sort
	 */	
	public List<UpfitterSearchResultVO> searchUpfitters(UpfitterSearchCriteriaVO searchCriteria, Pageable page, Sort sort){
		List<UpfitterSearchResultVO> results = new ArrayList<UpfitterSearchResultVO>();
		
		results = externalAccountDAO.findUpfitters(searchCriteria, page, sort);
		
		return results;
	}
	
	/**
	 * Searches for upfitter external accounts based on the specified trim. Upfitters are determined based on having priced
	 * the trim's dealer accessories or having just a vehicle configuration where the model criteria matches the trim.
	 * Dealer Accessories are not required 
	 * @param Model known as trim
	 * @param The vendor name or account code search term
	 * @param Page
	 * @param Sort
	 */
	public List<UpfitterSearchResultVO> searchUpfittersAccessoriesOptional(Model model, UpfitterSearchCriteriaVO searchCriteria, Pageable page, Sort sort){
		List<UpfitterSearchResultVO> results = new ArrayList<UpfitterSearchResultVO>();
		
		results = externalAccountDAO.findUpfitters(model, searchCriteria, page, sort);
		
		return results;		
	}
	
	/**
	 * Search for applicable Upfitter quotes for a given trim. Within in the context of maintaining 
	 * a Vendor Quote, quotes with or without pricing should be returned.
	 * @param upfitterAccountKey ExternalAccountPK The primary key of the external account/Upfitter account
	 * @param Model The selected trim
	 * @param searchCriteria String The search term
	 * @param page Used to paginate result set
	 * @return List of the trim's Upfitter's quotes
	 */	
	public List<UpfitterQuote> searchTrimUpfitterQuotesForSetup(ExternalAccountPK upfitterAccountKey, Model trim, String searchCriteria, Pageable page){
		List<UpfitterQuote> resultsWithoutVehCfg = new ArrayList<UpfitterQuote>();
		List<UpfitterQuote> resultsWithVehCfg = new ArrayList<UpfitterQuote>();
		List<UpfitterQuote> results = new ArrayList<UpfitterQuote>();		
		
		resultsWithoutVehCfg = upfitterQuoteDAO.findByUpfitterAndTrimAndQuoteNumberWithoutConfiguration(upfitterAccountKey.getCId(), 
				upfitterAccountKey.getAccountType(), upfitterAccountKey.getAccountCode(), trim.getModelId(), 
				DataUtilities.appendWildCardToRight(searchCriteria), page);
		
		resultsWithVehCfg = upfitterQuoteDAO.findByUpfitterAndTrimAndQuoteNumberWithConfiguration(upfitterAccountKey.getCId(), 
				upfitterAccountKey.getAccountType(), upfitterAccountKey.getAccountCode(), trim.getStandardCode(), 
				trim.getModelMarkYear().getModelMarkYearDesc(), trim.getMake().getMakeDesc(), trim.getMakeModelRange().getMrgId(), 
				trim.getModelId(), DataUtilities.appendWildCardToRight(searchCriteria), page);

		results.addAll(resultsWithVehCfg);		
		results.addAll(resultsWithoutVehCfg);		
		
		return results;
	}

	/**
	 * Search for applicable Upfitter quotes for a given trim. Within in the context of creating 
	 * a MAFS Quote, only quotes with pricing should be returned.
	 * @param upfitterAccountKey ExternalAccountPK The primary key of the external account/Upfitter account
	 * @param Model The selected trim
	 * @param searchCriteria String The search term
	 * @param page Used to paginate result set
	 * @return List of the trim's Upfitter's quotes
	 */	
	public List<UpfitterQuote> searchTrimUpfitterQuotesForQuoting(ExternalAccountPK upfitterAccountKey, Model trim, String searchCriteria, boolean vehicleConfigOnly, Pageable page){
		List<UpfitterQuote> resultsWithoutVehCfg = new ArrayList<UpfitterQuote>();
		List<UpfitterQuote> resultsWithVehCfg = new ArrayList<UpfitterQuote>();		
		List<UpfitterQuote> results = new ArrayList<UpfitterQuote>();		
				
		if(!vehicleConfigOnly) {
			resultsWithoutVehCfg = upfitterQuoteDAO.findByUpfitterAndTrimAndQuoteNumberWithoutConfiguration(upfitterAccountKey.getCId(), 
					upfitterAccountKey.getAccountType(), upfitterAccountKey.getAccountCode(), trim.getModelId(), 
					DataUtilities.appendWildCardToRight(searchCriteria), page);
		}
		
		resultsWithVehCfg = upfitterQuoteDAO.findByUpfitterAndTrimAndQuoteNumberWithConfigurationAndPricing(upfitterAccountKey.getCId(), 
				upfitterAccountKey.getAccountType(), upfitterAccountKey.getAccountCode(), trim.getStandardCode(), 
				trim.getModelMarkYear().getModelMarkYearDesc(), trim.getMake().getMakeDesc(), trim.getMakeModelRange().getMrgId(), 
				trim.getModelId(), DataUtilities.appendWildCardToRight(searchCriteria), page);
		
		results.addAll(resultsWithVehCfg);		
		results.addAll(resultsWithoutVehCfg);
		
		return results;
	}	
	
	@Transactional(readOnly=true)
	public ExternalAccount getUpfitterAccount(String accountCode, CorporateEntity corporateEntity){
		ExternalAccountPK externalAccountPK = null;
		ExternalAccount upfitter = null;
		
		externalAccountPK = new ExternalAccountPK();
	    externalAccountPK.setCId(corporateEntity.getCorpId());
		externalAccountPK.setAccountCode(accountCode);
		externalAccountPK.setAccountType("S");	
	    
		upfitter = externalAccountDAO.findById(externalAccountPK).orElse(null);
				
		return upfitter;
	}
	
	/**
	 * Determines which lead time to use. If a lead time has not
	 * been specified at the upfitter price level, then use the default
	 * lead time specified at the dealer accessory level.
	 * @param dealerAccessoryPrice The effective upfitter price
	 * @return Long The determined lead time for the upfitter accessory
	 */
	public Long determineLeadTime(DealerAccessoryPrice dealerAccessoryPrice){
	    Long leadTime;
	    leadTime = dealerAccessoryPrice.getLeadTime();
	    if(MALUtilities.isEmpty(leadTime)){
	    	leadTime = dealerAccessoryPrice.getDealerAccessory().getDealerAccessoryCode().getLeadTime();
	    }
	    return leadTime;
	}	
	
	@Transactional
	public ExtAccAddress getUpfitterDefaultPostAddress(String accountCode, long cId) {
		ExtAccAddress vendorDefaultAddress = null;
		
		List<ExtAccAddress> vendorAddresses = extAccAddressDAO.getExtAccAddressessByAccount(cId, "S", accountCode);
		
		for(ExtAccAddress extAccAddress : vendorAddresses) {
			if("POST".equals(extAccAddress.getAddressType().getAddressType()) && "Y".equals(extAccAddress.getDefaultInd())) {
				vendorDefaultAddress = extAccAddress;
				break;
			}
		}
		
		return vendorDefaultAddress;
	}
	
	public String formatUpfitterAddress(UpfitterSearchResultVO upfitterVO){
		return DisplayFormatHelper.formatAddressForSingleLineLabel(
				null, upfitterVO.getPayeeAddress1(), null, null, null, upfitterVO.getPayeeCity(), upfitterVO.getPayeeState(), upfitterVO.getPayeeZip());
	}
	
	/**
	 * Retrieves a list of quotes in the system for a given upfitter's external account
	 * 
	 * @param The external account for the upfitter
	 * @return A list containing all the upfit quotes in the system for the upfitter.
	 */
	@Transactional
	public List<UpfitterQuote> getUpfitterQuotes(ExternalAccount upfitter){
		List<UpfitterQuote> quotes;
		
		quotes = upfitterQuoteDAO.findByAccount(upfitter.getExternalAccountPK().getCId(), upfitter.getExternalAccountPK().getAccountType(), upfitter.getExternalAccountPK().getAccountCode());
		for(UpfitterQuote quote : quotes){
			quote.getDealerAccessoryPrices().size();
			for(DealerAccessoryPrice price : quote.getDealerAccessoryPrices()){
				price.getDealerAccessory().getDealerAccessoryPrices().size();
			}
		}
		
		return quotes;
	}
		
	/**
	 * @param extAccAddress
	 * @param separatorString - value to be appended in between of address components e.g. , 
	 * @return
	 * 
	 * Method to format Address 
	 */
	public String getFormattedAddress(ExtAccAddress extAccAddress, String separatorString) {
		return DisplayFormatHelper.formatAddressForTable(null, extAccAddress.getAddressLine1(), null, null, null, extAccAddress.getCityDescription(), extAccAddress.getRegionAbbreviation(), extAccAddress.getPostcode(), separatorString);
	}
	
	/**
	 * Determines the effective price from an dealer accessory price list based on the effective date 
	 * that is closest to today irrespective of Vendor Quote Number
	 * @param List of DealerAccessoryPrice. The list should be based on a single vendor.
	 * @param Model The trim
	 * @return The effective dealer accessory price for the trim's dealer accessory price list
	 */
	public DealerAccessoryPrice getUpfitterEffectivePrice(List<DealerAccessoryPrice> upfitterAccessoryPrices, Model model){
		DealerAccessoryPrice effectivePrice = null; 
		DealerAccessoryPrice futureDealerAccessoryPrice = null;
		
		//Sorts the list in ASC order
		Collections.sort(upfitterAccessoryPrices, new Comparator<DealerAccessoryPrice>() { 
			public int compare(DealerAccessoryPrice dp1, DealerAccessoryPrice dp2) {
				int compareValue;
				
				if(MALUtilities.clearTimeFromDate(dp1.getEffectiveDate()).compareTo(MALUtilities.clearTimeFromDate(dp2.getEffectiveDate())) == 0){
					compareValue = dp1.getDplId().compareTo(dp2.getDplId());
				} else {
					compareValue = MALUtilities.clearTimeFromDate(dp1.getEffectiveDate()).compareTo(MALUtilities.clearTimeFromDate(dp2.getEffectiveDate()));
				}
				
				return compareValue; 
			}
		});	
		
		// Looking for the latest date in the list that is less than or equal to today's date
		// If an effective date was not found then looking for the first future date.		
		for(DealerAccessoryPrice dealerPrice : upfitterAccessoryPrices){
			//OTD-2238
			if(dealerPrice.getDealerAccessory().getModel().getModelId().equals(model.getModelId())){
				if(dealerPrice.getEffectiveDate().compareTo(Calendar.getInstance().getTime()) <= 0){
					effectivePrice = dealerPrice;				
				}
				if(dealerPrice.getEffectiveDate().compareTo(Calendar.getInstance().getTime()) > 0
						&& MALUtilities.isEmpty(futureDealerAccessoryPrice)){
					futureDealerAccessoryPrice = dealerPrice;				
				}			
			}
		}
		 
		// If an effective date was not found then looking for the first future date.			
		if(MALUtilities.isEmpty(effectivePrice)){
			effectivePrice = futureDealerAccessoryPrice;
		}
				
		return effectivePrice;
	}	
	
	/**
	 * Determines the effective price from an dealer accessory price list based on the effective date 
	 * that is closest to today, without Vendor Quote Number
	 * @param List of DealerAccessoryPrice. The list should be based on a single vendor.
	 * @param DealerAccessoryPrice The effective price
	 */
	public DealerAccessoryPrice getUpfitterEffectivePriceWithoutQuote(List<DealerAccessoryPrice> upfitterAccessoryPrices, Model model){
		DealerAccessoryPrice effectivePrice = null; 
		DealerAccessoryPrice futureDealerAccessoryPrice = null;
		
		//Sorts the list in ASC order
		Collections.sort(upfitterAccessoryPrices, new Comparator<DealerAccessoryPrice>() { 
			public int compare(DealerAccessoryPrice dp1, DealerAccessoryPrice dp2) {
				int compareValue;
				
				if(MALUtilities.clearTimeFromDate(dp1.getEffectiveDate()).compareTo(MALUtilities.clearTimeFromDate(dp2.getEffectiveDate())) == 0){
					compareValue = dp1.getDplId().compareTo(dp2.getDplId());
				} else {
					compareValue = MALUtilities.clearTimeFromDate(dp1.getEffectiveDate()).compareTo(MALUtilities.clearTimeFromDate(dp2.getEffectiveDate()));
				}
				
				return compareValue; 
			}
		});	
		
		// Looking for the latest date in the list that is less than or equal to today's date
		// If an effective date was not found then looking for the first future date.		
		for(DealerAccessoryPrice dealerPrice : upfitterAccessoryPrices){
			//OTD-2238
			if(dealerPrice.getDealerAccessory().getModel().getModelId().equals(model.getModelId())){
				if(MALUtilities.isEmpty(dealerPrice.getUpfitterQuote())){
					if(dealerPrice.getEffectiveDate().compareTo(Calendar.getInstance().getTime()) <= 0){
						effectivePrice = dealerPrice;				
					}
					if(dealerPrice.getEffectiveDate().compareTo(Calendar.getInstance().getTime()) > 0 && MALUtilities.isEmpty(futureDealerAccessoryPrice)){
						futureDealerAccessoryPrice = dealerPrice;				
					}			
				}	
			}
		}
		 
		// If an effective date was not found then looking for the first future date.			
		if(MALUtilities.isEmpty(effectivePrice)){
			effectivePrice = futureDealerAccessoryPrice;
		}
				
		return effectivePrice;
	}
	
	
	/**
	 * Determines the effective price from a dealer accessory price list based on the 
	 * quote number and effective date. 
	 * @param The quote used to determine the effective quoted price
	 * @param List of DealerAccessoryPrice. 
	 * @param DealerAccessoryPrice The effective price
	 */	
	public DealerAccessoryPrice getEffectiveQuotedPrice(UpfitterQuote quote, Model model, List<DealerAccessoryPrice> upfitterAccessoryPrices){
		DealerAccessoryPrice effectivePrice = null; 
		DealerAccessoryPrice futureDealerAccessoryPrice = null;
		
		//Sorts the list in ASC order
		Collections.sort(upfitterAccessoryPrices, new Comparator<DealerAccessoryPrice>() { 
			public int compare(DealerAccessoryPrice dp1, DealerAccessoryPrice dp2) {
				int compareValue;
				
				if(MALUtilities.clearTimeFromDate(dp1.getEffectiveDate()).compareTo(MALUtilities.clearTimeFromDate(dp2.getEffectiveDate())) == 0){
					compareValue = dp1.getDplId().compareTo(dp2.getDplId());
				} else {
					compareValue = MALUtilities.clearTimeFromDate(dp1.getEffectiveDate()).compareTo(MALUtilities.clearTimeFromDate(dp2.getEffectiveDate()));
				}
				
				return compareValue; 
			}
		});	
		
		// Based on the specific upffiter and quote, looking for the latest date in 
		// the list that is less than or equal to today's date If an effective date 
		// was not found then looking for the first future date.		
		for(DealerAccessoryPrice dealerPrice : upfitterAccessoryPrices){
			if(!MALUtilities.isEmpty(dealerPrice.getPayeeAccount())					
					&& !MALUtilities.isEmpty(dealerPrice.getUpfitterQuote())
					&& dealerPrice.getPayeeAccount().equals(quote.getExternalAccount())					
					&& dealerPrice.getUpfitterQuote().equals(quote)
					&& dealerPrice.getDealerAccessory().getModel().getModelId().equals(model.getModelId())){
				if(dealerPrice.getEffectiveDate().compareTo(Calendar.getInstance().getTime()) <= 0){
					effectivePrice = dealerPrice;				
				}
				if(dealerPrice.getEffectiveDate().compareTo(Calendar.getInstance().getTime()) > 0
						&& MALUtilities.isEmpty(futureDealerAccessoryPrice)){
					futureDealerAccessoryPrice = dealerPrice;				
				}			
			}
		}
		 
		// If an effective date was not found then looking for the first future date.			
		if(MALUtilities.isEmpty(effectivePrice)){
			effectivePrice = futureDealerAccessoryPrice;
		}
				
		return effectivePrice;		
	}
	
	@Transactional
	public UpfitterQuote saveOrUpdateUpfitterQuote(UpfitterQuote upfitterQuote, Model model) throws MalBusinessException{
		if(MALUtilities.isEmpty(upfitterQuote.getUfqId())){
			saveNewUpfitterQuote(upfitterQuote);
		} else {
			saveUpdatedUpfitterQuote(upfitterQuote, model);
		}
		return upfitterQuote;
	}
	
	@Transactional(readOnly=true)
	public boolean isUniqueQuoteNumber(ExternalAccount upfitterAccount, String quoteNumber){
		List<UpfitterQuote> quotes;
		
		quotes = upfitterQuoteDAO.findByAccountAndQuoteNumber(
				upfitterAccount.getExternalAccountPK().getCId(), 
				upfitterAccount.getExternalAccountPK().getAccountType(), 
				upfitterAccount.getExternalAccountPK().getAccountCode(), 
				quoteNumber);
		
		return quotes.isEmpty() ? true : false; 
		
	}
	
	/**
	 * Checks the client quotes to see if any of them contain a dealer
	 * accessory that references the given upfitter quote.
	 */
	public boolean isReferencedByClientQuote(UpfitterQuote upfitterQuote){
		boolean isReferenced;
		List<QuotationDealerAccessory> quotationDealerAccessories;
		quotationDealerAccessories = quotationDealerAccessoryDAO.findByExternalReferenceNo(upfitterQuote.getQuoteNumber());
		isReferenced = quotationDealerAccessories.isEmpty() ? false : true;
		return isReferenced;
	}
	
	/**
	 * OTD-2238
	 * Detects whether the upfitter quote has been assigned to another trim.
	 * @param UpfitterQuote The Upfitter's quote
	 * @param Model The trim to exclude from the detection
	 * @return Boolean Indicating whether the quote is assigned (true) to another trim or not (false)
	 */
	public boolean isReferencedByOtherTrim(UpfitterQuote upfitterQuote, Model model){
		boolean isReferenced = false;
				
		for(DealerAccessoryPrice price : upfitterQuote.getDealerAccessoryPrices()){			
			if(!price.getDealerAccessory().getModel().getModelId().equals(model.getModelId())){
				isReferenced = true;
				break;
			}
		}
		
		return isReferenced;		
	}
		
	private UpfitterQuote saveNewUpfitterQuote(UpfitterQuote upfitterQuote) throws MalBusinessException{
		UpfitterQuote quote = new UpfitterQuote();

		//TODO Perform validation here
		for(DealerAccessoryPrice price : upfitterQuote.getDealerAccessoryPrices()){
			if(!MALUtilities.isEmpty(price.getDealerAccessory().getDacId())){
				modelService.validateUniqueVendorEffectiveDate(modelService.getDealerAccessoryWithPrices(price.getDealerAccessory().getDacId()), price);
			}
		}
		
		quote.setExpirationDate(upfitterQuote.getExpirationDate());
		quote.setExternalAccount(upfitterQuote.getExternalAccount());
		quote.setQuoteNumber(upfitterQuote.getQuoteNumber());
		quote.setDescription(upfitterQuote.getDescription());
		quote = upfitterQuoteDAO.save(quote);
		
		// Setting pricing related information based on business rules.
		// Note: DealerAccessor must exist in the model regardless if has been persisted or not.
		// Assumes that each price on a quote is for a unique dealer accessory. A single upfitter
		// quote would not have multiple prices for the same accessory
		for(DealerAccessoryPrice price : upfitterQuote.getDealerAccessoryPrices()){
			price.setVatAmount(new BigDecimal(0));
			price.setTotalPrice(price.getBasePrice());
			price.setUpfitterQuote(quote);

			modelService.saveOrUpdateDealerAccessory(price.getDealerAccessory());
		}
				
		quote = upfitterQuoteDAO.findById(quote.getUfqId()).orElse(null);
		
		return upfitterQuote;
	}
		
	private UpfitterQuote saveUpdatedUpfitterQuote(UpfitterQuote upfitterQuote, Model model) throws MalBusinessException{
		UpfitterQuote quote;
		Map<DealerAccessoryCode, DealerAccessory> dealerAccessoryCodeMap;
					
		// Grouping the prices by their respective dealer accessory to obtain a distinct list of dealer accessories
		// Uniquely identifying the dealer accessory by it's mdlId and accessory code. 
		dealerAccessoryCodeMap = new HashMap<DealerAccessoryCode, DealerAccessory>();
		for(DealerAccessoryPrice price : upfitterQuote.getDealerAccessoryPrices()){				
			if(price.getDealerAccessory().getModel().getModelId().equals(model.getModelId())){
				if(!dealerAccessoryCodeMap.containsKey(price.getDealerAccessory().getDealerAccessoryCode())){
					dealerAccessoryCodeMap.put(price.getDealerAccessory().getDealerAccessoryCode(), price.getDealerAccessory());
				}
			}			
		}
		
		//Saving price changes by dealer accessory
		for(DealerAccessoryCode key : dealerAccessoryCodeMap.keySet()){	
			modelService.saveOrUpdateDealerAccessory(dealerAccessoryCodeMap.get(key));			
		}	
						
		//Saving changes made to the upfitter's quote header	
		quote = upfitterQuoteDAO.findById(upfitterQuote.getUfqId()).orElse(null);
		quote.setQuoteNumber(upfitterQuote.getQuoteNumber());
		quote.setDescription(upfitterQuote.getDescription());
		upfitterQuote = upfitterQuoteDAO.saveAndFlush(quote);
		
		return upfitterQuote;
	}	
	
	@Transactional
	public UpfitterQuote getUpfitterQuote(Long ufqId){
		UpfitterQuote quote;
		
		quote = upfitterQuoteDAO.findById(ufqId).orElse(null);
		
		for(DealerAccessoryPrice price : quote.getDealerAccessoryPrices()){
			price.getDealerAccessory().getDealerAccessoryPrices().size();
		}
		
		return quote;
	}
	
	/**
	 * Retrieves the UpfitterQuote and loads the accessories/pricing specific to the trim.
	 * 
	 * This is necessary because a single UpfitterQuote can have many accessories across 
	 * many trims.
	 * @param UpfitterQuote The upfitters quote
	 * @param Model The trim
	 * @return UpfitterQuote directly from the database that contains accessories/pricing specific to the trim
	 * 
	 */
	@Override
	public UpfitterQuote getUpfitterQuote(UpfitterQuote upfitterQuote, Model trim)	 {
		UpfitterQuote quote;
		quote = upfitterQuoteDAO.findById(upfitterQuote.getUfqId()).orElse(null);
		quote.setDealerAccessoryPrices(dealerAccessoryPriceDAO.findByUpfitterQuoteAndTrim(upfitterQuote.getUfqId(), trim.getModelId()));
		return quote;
	}		
	
	public List<UpfitterSearchResultVO> searchVendors(String searchTerm, Pageable page, Sort sort){
		return externalAccountDAO.findVendors(searchTerm, page, sort);
	}

	@Override
	public List<Object[]> getUpfitterDealerPostAddress(long cId,    // created for HD-252
			String accountCode) {
		
		return supplierDAO.getSupplierAddressByType(cId, 
				"S", 
				accountCode,
				ADDRESS_TYPE_POST);
	}
	
}
