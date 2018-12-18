package com.mikealbert.service;

import static org.junit.Assert.*;

import javax.annotation.Resource;

import org.junit.Test;

import com.mikealbert.data.entity.QuotationModel;
import com.mikealbert.data.entity.QuoteModelPropertyValue;
import com.mikealbert.data.enumeration.QuoteModelPropertyEnum;
import com.mikealbert.testing.BaseTest;

public class QuoteModelPropertyValueServiceTest extends BaseTest{

	@Resource QuoteModelPropertyValueService quoteModelPropertyValueService;
	@Resource QuotationService quotationService;
	
	@Test
	public void testSaveOrUpdateQuoteModelPropertyValue() throws Exception {
		final Long QMD_ID = 325175L;
		final String PROPERTY_VALUE = "JUNIT-TEST";
		
		QuotationModel qmd = quotationService.getQuotationModel(QMD_ID);
		QuoteModelPropertyValue qmpv = quoteModelPropertyValueService.saveOrUpdateQuoteModelPropertyValue(
				qmd, QuoteModelPropertyEnum.INFORMAL_QUOTE_ID, PROPERTY_VALUE);
		
		assertNotNull("Quote Model Property Value was not ssaved", qmpv.getQmpvId());
		
	}

}
