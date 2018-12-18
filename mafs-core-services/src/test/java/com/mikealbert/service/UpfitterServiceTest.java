package com.mikealbert.service;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.data.domain.PageRequest;

import com.mikealbert.data.TestQueryConstants;
import com.mikealbert.data.dao.ModelDAO;
import com.mikealbert.data.dao.UpfitterQuoteDAO;
import com.mikealbert.data.entity.DealerAccessory;
import com.mikealbert.data.entity.DealerAccessoryCode;
import com.mikealbert.data.entity.DealerAccessoryPrice;
import com.mikealbert.data.entity.ExtAccAddress;
import com.mikealbert.data.entity.ExternalAccount;
import com.mikealbert.data.entity.Model;
import com.mikealbert.data.entity.UpfitterQuote;
import com.mikealbert.data.enumeration.CorporateEntity;
import com.mikealbert.data.vo.UpfitterSearchCriteriaVO;
import com.mikealbert.data.vo.UpfitterSearchResultVO;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.testing.BaseTest;
import com.mikealbert.util.MALUtilities;

//TODO Remove hardcoded values or provide null checks for test.
public class UpfitterServiceTest extends BaseTest{
	@Resource UpfitterService upfitterService;
	@Resource ModelService modelService;
	@Resource UpfitterQuoteDAO upfitterQuoteDAO;
	@Resource ModelDAO modelDAO;

	final static String QUOTE_NO = "QN-UNIT-TEST-007";
	final static String QUOTE_DESC = "QUOTE-DESCRIPTION-UNIT-TEST-007";	
	final static String UPFITTER_ACCOUNT_NAME = "Toyota Of Elgin";
	final static String UPFITTER_ACCOUNT_CODE = "00042097";		
	final static String TRIM_DEALER_ACCESSORY_CODE = "535825"; //Cargo Mat (CA-01)
	final static String DEALER_ACCESSORY_CODE = "16388"; //Rear Lip Spoiler (LR-01)	
	final static Long MDL_ID = 142984L;
	final static BigDecimal PRICE = new BigDecimal(123.45).setScale(2, BigDecimal.ROUND_HALF_UP);
	final static Long LEAD_TIME = 123L;
	
	@Test
	public void searchUpfitters(){
		final String SEARCH_TERM = "Inc";
		
		UpfitterSearchCriteriaVO criteria;
		List<UpfitterSearchResultVO> result;
		
		criteria = new UpfitterSearchCriteriaVO();
		criteria.setTerm(SEARCH_TERM);
		
		result = upfitterService.searchUpfitters(criteria, new PageRequest(0, 1), null);
				
		assertNotNull("Upfitter was not found", result);			
		assertTrue("Upfitter search result size is 0", result.size() > 0);
	}
	
	
	@Test 
	public void getUpfitterAccountDefaultAddress() throws Exception {
		ExtAccAddress upfitterDefaulPostAddress = upfitterService.getUpfitterDefaultPostAddress("00044727", 1);
		
		String formattedAddress = upfitterService.getFormattedAddress(upfitterDefaulPostAddress, ",");
		System.out.println(formattedAddress);
		
		assertNotNull("Upfitter default POST address not found", upfitterDefaulPostAddress);
	}
	
	@Test
	public void testSaveOrUpdateUfitterQuoteCreate() throws MalBusinessException{
		Long mdlId = 142984L; //2014 Kia Optima SX Turbo 4dr Sedan (55282)
		UpfitterQuote quote; 
		DealerAccessoryCode dlrAccCode;
		List<DealerAccessory> trimAccessories;
		DealerAccessory trimAccessory, notTrimAccessory;
		Model model;
				
		dlrAccCode = modelService.getDealerAccessoryCode(TRIM_DEALER_ACCESSORY_CODE);
		model = modelService.getModel(mdlId);
				
		trimAccessory = modelService.getModelDealerAccessory(model, dlrAccCode);
		
		em.clear();
		
		notTrimAccessory = new DealerAccessory();		
		notTrimAccessory.setDealerAccessoryCode(modelService.getDealerAccessoryCode(DEALER_ACCESSORY_CODE));
		notTrimAccessory.setModel(model);
		notTrimAccessory.setDealerAccessoryPrices(new ArrayList<DealerAccessoryPrice>());
		
		trimAccessories = new ArrayList<DealerAccessory>();
		trimAccessories.add(trimAccessory);
		trimAccessories.add(notTrimAccessory);
		
		quote = new UpfitterQuote();
		quote.setQuoteNumber(QUOTE_NO);
		quote.setDescription(QUOTE_DESC);
		quote.setExternalAccount(upfitterService.getUpfitterAccount(UPFITTER_ACCOUNT_CODE, CorporateEntity.MAL));
		quote.setDealerAccessoryPrices(new ArrayList<DealerAccessoryPrice>());
		
		for(int i=0; i < trimAccessories.size(); i++){
			quote.getDealerAccessoryPrices().add(new DealerAccessoryPrice());		
			quote.getDealerAccessoryPrices().get(i).setPayeeAccount(quote.getExternalAccount());
			//		quote.getDealerAccessoryPrices().get(0).setUpfitterQuote(quote);
			quote.getDealerAccessoryPrices().get(i).setBasePrice(new BigDecimal(i + ".00").setScale(2));
			quote.getDealerAccessoryPrices().get(i).setVatAmount(quote.getDealerAccessoryPrices().get(i).getBasePrice());
			quote.getDealerAccessoryPrices().get(i).setMsrp(quote.getDealerAccessoryPrices().get(i).getBasePrice());
			quote.getDealerAccessoryPrices().get(i).setTotalPrice(quote.getDealerAccessoryPrices().get(i).getBasePrice());			
			quote.getDealerAccessoryPrices().get(i).setLeadTime(Long.valueOf(i));
			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.DAY_OF_MONTH, i * 2);
			quote.getDealerAccessoryPrices().get(i).setEffectiveDate(calendar.getTime());
			quote.getDealerAccessoryPrices().get(i).setDealerAccessory(trimAccessories.get(i));
			trimAccessories.get(i).getDealerAccessoryPrices().add(quote.getDealerAccessoryPrices().get(i));
		}
		
		quote = upfitterService.saveOrUpdateUpfitterQuote(quote, model);
		
		assertNotNull("Upfitter Quote was not saved successfully", quote);
		
	}
		
	@Test
	public void testSaveOrUpdateUfitterQuoteUpdateAddPricing() throws MalBusinessException{
		Long ufqId;
		Query query; 
		UpfitterQuote quote;
		DealerAccessoryPrice newPrice;
		Map<DealerAccessory, List<DealerAccessoryPrice>> dealerAccessoryPriceMap;
		DealerAccessory notTrimAccessory;
		boolean found;
		int initialNumOfQuotedPrices = 0;
		Model model;
				
		try{
			query = em.createNativeQuery(TestQueryConstants.READ_UPFITTER_QUOTE_ID);
			ufqId = ((BigDecimal)query.getSingleResult()).longValue();
		} catch (NoResultException nre) {
			System.err.println("Skipping testSaveOrUpdateUfitterQuoteUpdateAddPricing as an Upfitter Quote Id could not be sampled.");
			return;
		}
		
		System.out.println("Testing Upfitter Quote Id: " + ufqId);
		
		quote = upfitterQuoteDAO.findById(ufqId).orElse(null);
		quote.getDealerAccessoryPrices().size();
		quote.setQuoteNumber(QUOTE_NO);
		quote.setDescription(QUOTE_DESC);
		
		initialNumOfQuotedPrices = quote.getDealerAccessoryPrices().size();
		
		for(DealerAccessoryPrice price : quote.getDealerAccessoryPrices()){
			price.getDealerAccessory().getDealerAccessoryPrices().size();
		}
		
		em.clear();
		
		model = quote.getDealerAccessoryPrices().get(0).getDealerAccessory().getModel();
		//Add an new accessory to the trim
		notTrimAccessory = new DealerAccessory();		
		notTrimAccessory.setDealerAccessoryCode(modelService.getDealerAccessoryCode(DEALER_ACCESSORY_CODE));
		notTrimAccessory.setModel(model);
		notTrimAccessory.setDealerAccessoryPrices(new ArrayList<DealerAccessoryPrice>());
		notTrimAccessory.getDealerAccessoryPrices().add(new DealerAccessoryPrice());
		notTrimAccessory.getDealerAccessoryPrices().get(0).setBasePrice(PRICE);
		notTrimAccessory.getDealerAccessoryPrices().get(0).setVatAmount(PRICE);
		notTrimAccessory.getDealerAccessoryPrices().get(0).setTotalPrice(PRICE.multiply(new BigDecimal(2)));		
		notTrimAccessory.getDealerAccessoryPrices().get(0).setEffectiveDate(Calendar.getInstance().getTime());
		notTrimAccessory.getDealerAccessoryPrices().get(0).setPayeeAccount(quote.getExternalAccount());
		notTrimAccessory.getDealerAccessoryPrices().get(0).setUpfitterQuote(quote);
		notTrimAccessory.getDealerAccessoryPrices().get(0).setDealerAccessory(notTrimAccessory);		
        quote.getDealerAccessoryPrices().addAll(notTrimAccessory.getDealerAccessoryPrices());
        
		dealerAccessoryPriceMap = groupPricingByDealerAccessory(quote);		
				
		//Adds new pricing to each of the dealer accessories, excluding the previous created accessory
		for(DealerAccessory dealerAccessory : dealerAccessoryPriceMap.keySet()){
			if(!MALUtilities.isEmpty(dealerAccessory.getDacId())){
				newPrice = new DealerAccessoryPrice();
				newPrice.setPayeeAccount(quote.getExternalAccount());
				newPrice.setBasePrice(PRICE);
				newPrice.setVatAmount(PRICE);
				newPrice.setTotalPrice(PRICE.multiply(new BigDecimal(2)));
				newPrice.setEffectiveDate(Calendar.getInstance().getTime());
				newPrice.setLeadTime(LEAD_TIME);
				newPrice.setDealerAccessory(dealerAccessory);
				newPrice.setUpfitterQuote(quote);
				dealerAccessory.getDealerAccessoryPrices().add(newPrice);
			}		
		}
		
		quote = upfitterService.saveOrUpdateUpfitterQuote(quote, model);
		
		em.clear();
		
		quote = upfitterQuoteDAO.findById(ufqId).orElse(null);
		
		//Test that the new accessory has been added to the trip
		found = false;
		for(DealerAccessoryPrice quotedPrice : quote.getDealerAccessoryPrices()){
		    if(quotedPrice.getDealerAccessory().getDealerAccessoryCode().getAccessoryCode().equals(DEALER_ACCESSORY_CODE)){
		    	found = true;
		    	break;
		    }
		}
						
		dealerAccessoryPriceMap = groupPricingByDealerAccessory(quote);	
				
		assertNotNull("We do not have a returned quote from save service", quote);
		assertTrue("Upfitter quote does not contain the correct amount of prices", quote.getDealerAccessoryPrices().size() - initialNumOfQuotedPrices == dealerAccessoryPriceMap.size());		
		assertTrue("Upfitter quote does not contain new dealer accessory", found);		

	}
	
	@Test
	public void testSaveOrUpdateUfitterQuoteUpdatePricing() throws MalBusinessException{
		Long ufqId;
		Query query; 
		UpfitterQuote quote;
		Calendar calendar;
		Map<DealerAccessory, List<DealerAccessoryPrice>> dealerAccessoryPriceMap;		
		int initialNumOfQuotedPrices = 0;
		boolean isUpdated = false;
		Model model;
		
		
		try {
			query = em.createNativeQuery(TestQueryConstants.READ_UPFITTER_QUOTE_ID);				
			ufqId = ((BigDecimal)query.getSingleResult()).longValue();
		} catch(NoResultException nre) {
			System.err.println("Skipping testSaveOrUpdateUfitterQuoteUpdateUpdatePricing as an Upfitter Quote Id could not be sampled.");
			return;			
		}
				
		System.out.println("Testing update pricing for Upfitter Quote Id: " + ufqId);
		
		quote = upfitterQuoteDAO.findById(ufqId).orElse(null);
		quote.setQuoteNumber(QUOTE_NO);
		quote.setDescription(QUOTE_DESC);
		
		model = quote.getDealerAccessoryPrices().get(0).getDealerAccessory().getModel();
		
		initialNumOfQuotedPrices = quote.getDealerAccessoryPrices().size();
		
		dealerAccessoryPriceMap = groupPricingByDealerAccessory(quote);

		em.clear();
		
		for(DealerAccessory dealerAccessory : dealerAccessoryPriceMap.keySet()){
			for(DealerAccessoryPrice price : dealerAccessory.getDealerAccessoryPrices()){
				if(!MALUtilities.isEmpty(price.getUpfitterQuote()) && price.getUpfitterQuote().getUfqId().equals(ufqId)){				
					price.setBasePrice(PRICE);	
					calendar = Calendar.getInstance();
					calendar.add(Calendar.DAY_OF_MONTH, 365);
					price.setEffectiveDate(calendar.getTime());	
					price.setLeadTime(LEAD_TIME);
				}
			}
		}
		

		quote = upfitterService.saveOrUpdateUpfitterQuote(quote, model);
		quote = upfitterQuoteDAO.findById(ufqId).orElse(null);
		em.close();
		
		for(DealerAccessoryPrice price : quote.getDealerAccessoryPrices()){
			if(price.getBasePrice().equals(PRICE) && price.getLeadTime().equals(LEAD_TIME)){
				isUpdated = true;
			    break;
			}			
		}
		
		assertNotNull("We do not have a returned quote from save service", quote);
		assertTrue("Upfitter quoted prices were not updated", isUpdated);
		assertTrue("Upfitter quote number was not updated", quote.getQuoteNumber().equals(QUOTE_NO));
		assertTrue("Upfitter quote description was not updated", quote.getDescription().equals(QUOTE_DESC));		
		assertTrue("Upfitter quote has unknown pricing records", initialNumOfQuotedPrices == quote.getDealerAccessoryPrices().size());		
		
	}	
	
	@Test
	public void testSaveOrUpdateUfitterQuoteRemovePricing() throws MalBusinessException{
		Long ufqId;
		Query query; 
		UpfitterQuote quote;
		Map<DealerAccessory, List<DealerAccessoryPrice>> dealerAccessoryPriceMap;		
		int initialNumOfQuotedPrices = 0;
		Model model;
		
		try {
			query = em.createNativeQuery(TestQueryConstants.READ_UPFITTER_QUOTE_ID);				
			ufqId = ((BigDecimal)query.getSingleResult()).longValue();		
		} catch (NoResultException nre) {
			System.err.println("Skipping testSaveOrUpdateUfitterQuoteRemovePricing as an Upfitter Quote Id could not be sampled.");
			return;			
		}
		
		System.out.println("Testing remove pricing for Upfitter Quote Id: " + ufqId);
		
		quote = upfitterQuoteDAO.findById(ufqId).orElse(null);
		quote.setQuoteNumber(QUOTE_NO);
		quote.setDescription(QUOTE_DESC);
		
		model = quote.getDealerAccessoryPrices().get(0).getDealerAccessory().getModel();
		
		initialNumOfQuotedPrices = quote.getDealerAccessoryPrices().size();
		
		dealerAccessoryPriceMap = groupPricingByDealerAccessory(quote);

		em.clear();

		//Removing the first matched quoted price for each of the dealer accessories.
		for(DealerAccessory dealerAccessory : dealerAccessoryPriceMap.keySet()){
			for(DealerAccessoryPrice price : dealerAccessory.getDealerAccessoryPrices()){
				if(!MALUtilities.isEmpty(price.getUpfitterQuote()) && price.getUpfitterQuote().getUfqId().equals(ufqId)){	
					//quote.getDealerAccessoryPrices().remove(price);
						price.setUpfitterQuote(null);
					break;
				}
			}
		}
		

		quote = upfitterService.saveOrUpdateUpfitterQuote(quote, model);
		quote = upfitterQuoteDAO.findById(ufqId).orElse(null);
		
		assertNotNull("We do not have a returned quote from save service", quote);
		assertTrue("Quote number was not updated as part of deleting its accessories", quote.getQuoteNumber().equals(QUOTE_NO));
		assertTrue("Quote description was not updated as part of deleting its accessories", quote.getDescription().equals(QUOTE_DESC));		
		assertTrue("Quoted pricing was not removed from Upfitter Quote", quote.getDealerAccessoryPrices().size() < initialNumOfQuotedPrices );
		
	}
	
	@Test
	public void testIsReferencedByClientQuote(){
		Long ufqId;
		Query query; 
		UpfitterQuote quote;	
		boolean found;
		
		try {
			query = em.createNativeQuery(TestQueryConstants.READ_UPFITTER_QUOTE_BY_CLIENT_QUOTE_REFERENCE);				
			ufqId = ((BigDecimal)query.getSingleResult()).longValue();
		} catch(NoResultException nre) {
			System.err.println("Skipping testIsReferencedByClientQuote as an Upfitter Quote Id could not be sampled.");
			return;			
		}
		
		quote = upfitterQuoteDAO.findById(ufqId).orElse(null);
		found = upfitterService.isReferencedByClientQuote(quote);
		
		assertTrue("Referenced upfitter quote was not detected", found);
	}
	
	@Ignore
	@Test
	public void testSearchTrimUpfitterQuotes(){
		ExternalAccount ea;
		Model model;
		List<UpfitterQuote> quotes;
		
		ea = upfitterService.getUpfitterAccount("00157104", CorporateEntity.MAL);
		model = modelDAO.findById(152148L).orElse(null);		
	    quotes = upfitterService.searchTrimUpfitterQuotesForSetup(ea.getExternalAccountPK(), model, "%", new PageRequest(0,50));
	    
	    assertTrue("Did not find trim''s upfitter quote", quotes.size() > 0);
	}
	
	private Map<DealerAccessory, List<DealerAccessoryPrice>> groupPricingByDealerAccessory(UpfitterQuote quote) {
		Map<DealerAccessory, List<DealerAccessoryPrice>> dealerAccessoryPriceMap;
		dealerAccessoryPriceMap = new HashMap<DealerAccessory, List<DealerAccessoryPrice>>();
		for(DealerAccessoryPrice price : quote.getDealerAccessoryPrices()){	
			if(dealerAccessoryPriceMap.containsKey(price.getDealerAccessory())){
				dealerAccessoryPriceMap.get(price.getDealerAccessory()).addAll(price.getDealerAccessory().getDealerAccessoryPrices());
			} else {
				dealerAccessoryPriceMap.put(price.getDealerAccessory(), new ArrayList<DealerAccessoryPrice>(price.getDealerAccessory().getDealerAccessoryPrices()));
			}			
		}
		return dealerAccessoryPriceMap;
	}	
			
}
