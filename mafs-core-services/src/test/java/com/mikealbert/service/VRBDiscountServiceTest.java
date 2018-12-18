package com.mikealbert.service;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.persistence.NoResultException;

import org.junit.Test;

import com.mikealbert.data.dao.CapitalElementDAO;
import com.mikealbert.data.dao.UpfitterProgressDAO;
import com.mikealbert.data.dao.VrbDiscountDAO;
import com.mikealbert.data.entity.CapitalElement;
import com.mikealbert.data.entity.ExternalAccount;
import com.mikealbert.data.entity.Model;
import com.mikealbert.data.entity.QuotationModel;
import com.mikealbert.data.entity.UpfitterProgress;
import com.mikealbert.data.entity.VrbDiscount;
import com.mikealbert.data.enumeration.CorporateEntity;
import com.mikealbert.data.enumeration.DocumentStatus;
import com.mikealbert.data.vo.VendorInfoVO;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.testing.BaseTest;
import com.mikealbert.util.MALUtilities;


public class VRBDiscountServiceTest extends BaseTest{
	@Resource VolumeRelatedBonusService vrbDiscountService;
	@Resource CustomerAccountService customerAccountService;
	@Resource ModelService modelService;
	@Resource VrbDiscountDAO vrbDiscountDAO;
	@Resource CapitalElementDAO capitalElementDAO;

	
	
	@Test
	public void testGetClientVolumeRelatedBonuses() throws Exception{
		final Long C_ID = 1L;
		final String ACCOUNT_CODE = "00000423";
		final String ACCOUNT_TYPE = "C";
		final Long mdlId = 162041L;
		final String productCode = "CE_LTD";
		final Long TERM = 24L;
		final Long CEL_ID = 3L;
		VrbDiscount discount;
		List<VrbDiscount> discounts;		
		ExternalAccount client;
		Model trim;
		CapitalElement cel;
		
		client = customerAccountService.getCustomerAccount(CorporateEntity.fromCorpId(C_ID), ACCOUNT_TYPE, ACCOUNT_CODE);
		trim = modelService.getModelById(mdlId);
		
		discounts = vrbDiscountService.getClientVolumeRelatedBonuses(client, trim, productCode, null);
		
		assertTrue("No VRB discounts were found", !discounts.isEmpty());
		
		cel = capitalElementDAO.findById(CEL_ID).orElse(null);

		discount = vrbDiscountService.getClientVolumeRelatedBonus(client, trim, productCode, cel, null);
		
		assertNotNull("Client's VRB discount from Capital Element could not be found ", discount);		
	}
	
	@Test
	public void testFindCapitalElemet() {
		final Long VRBD_ID = 442220L;
		VrbDiscount vrbDiscount = vrbDiscountDAO.findById(VRBD_ID).orElse(null);
		 CapitalElement captialElement = vrbDiscountService.findCapitalElemet(vrbDiscount);
		 
		 assertNotNull("Capital Element could not be found for VRB", captialElement);
	}
}

