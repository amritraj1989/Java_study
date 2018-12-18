package com.mikealbert.service;

import static org.junit.Assert.*;

import java.util.List;

import javax.annotation.Resource;
import org.junit.Test;
import org.springframework.data.domain.PageRequest;

import com.mikealbert.data.DataUtilities;
import com.mikealbert.data.dao.DealerAccessoryDAO;
import com.mikealbert.data.dao.ExternalAccountDAO;
import com.mikealbert.data.entity.DealerAccessory;
import com.mikealbert.data.entity.DealerAccessoryCode;
import com.mikealbert.data.entity.DealerAccessoryPrice;
import com.mikealbert.data.entity.ExternalAccount;
import com.mikealbert.data.entity.ExternalAccountPK;
import com.mikealbert.data.entity.Model;
import com.mikealbert.testing.BaseTest;
import com.mikealbert.util.MALUtilities;


public class ModelServiceTest extends BaseTest{
	@Resource ModelService modelService;
	@Resource DealerAccessoryDAO dealerAccessoryDAO;
	@Resource ExternalAccountDAO externalAccountDAO;

	final Long MDL_ID = 119729L;
	
	@Test
	public void testGetModel(){
		Model model;
		
		model = modelService.getModel(MDL_ID);
				
		assertNotNull("Model was not found", model);			
		assertTrue("Model Prices were not found", model.getModelPrices().size() > 0);
	}
	
	@Test
	public void testGetDealerAccessoryPriceList(){
		Long dacId = 499918L;
		Long cId = 1L;
		String accountType = "S";
		String accountCode = "00052970";
		ExternalAccount vendorAccount;
		ExternalAccountPK vendorAccountPK;		
		DealerAccessory dealerAccessory;
		List<DealerAccessoryPrice> dealerAccessoryPrices;	
		boolean accountsMatch = true;
		boolean codesMatch = true;
		
		vendorAccountPK = new ExternalAccountPK();
		vendorAccountPK.setCId(cId);
		vendorAccountPK.setAccountType(accountType);
		vendorAccountPK.setAccountCode(accountCode);
		
		dealerAccessory = dealerAccessoryDAO.findById(dacId).orElse(null);
		vendorAccount = externalAccountDAO.findById(vendorAccountPK).orElse(null);
		
		dealerAccessoryPrices = modelService.getDealerAccessoryPriceList(dealerAccessory, vendorAccount);		
		for(DealerAccessoryPrice dpl : dealerAccessoryPrices){
			if(!dpl.getPayeeAccount().getExternalAccountPK().getAccountCode().equals(accountCode)){
				accountsMatch = false;
				break;
			}
			
			if(!dpl.getDealerAccessory().getDacId().equals(dacId)){
				codesMatch = false;
				break;
			}			
		}		
		assertTrue("Dealer Accessory Price List size is empty ", dealerAccessoryPrices.size() > 0);
		assertTrue("Dealer Accessory identifier does not match", codesMatch);			
		assertTrue("Dealer Accessory Price List vendor accounts do not match ", accountsMatch);
		
		
		dealerAccessoryPrices = modelService.getDealerAccessoryPriceList(dealerAccessory, null);		
		for(DealerAccessoryPrice dpl : dealerAccessoryPrices){
			if(!MALUtilities.isEmpty(dpl.getPayeeAccount())){
				accountsMatch = false;
				break;
			}
			
			if(!dpl.getDealerAccessory().getDacId().equals(dacId)){
				codesMatch = false;
				break;
			}			
		}		
		assertTrue("Dealer Accessory Price List size is empty ", dealerAccessoryPrices.size() > 0);
		assertTrue("Dealer Accessory identifier does not match", codesMatch);			
		assertTrue("Dealer Accessory Price List vendor accounts is not null ", accountsMatch);		
		
		
	}
	
	@Test
	public void testGetDealerAccessories(){
		List<DealerAccessoryCode> dealerAccessoryCodes;
		String criteria = "fog";
		dealerAccessoryCodes = modelService.getDealerAccessoryCodes(DataUtilities.appendWildCardToRight(criteria.toLowerCase()), new PageRequest(0,5));
		assertTrue("Dealer Accessory Code(s) could not be found", dealerAccessoryCodes.size() > 0);
	}
	
	@Test
	public void testGetDealerAccessoryCode(){
		final String ID = "10592";
		DealerAccessoryCode dealerAccessoryCode;
		dealerAccessoryCode = modelService.getDealerAccessoryCode(ID);
		assertNotNull("Dealer Accessory code could not be found for id = " + ID, dealerAccessoryCode);
	}

}
