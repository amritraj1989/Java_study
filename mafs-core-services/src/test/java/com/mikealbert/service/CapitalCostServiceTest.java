package com.mikealbert.service;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Test;

import com.mikealbert.data.dao.QuotationModelDAO;
import com.mikealbert.data.entity.CapitalElement;
import com.mikealbert.data.entity.QuotationModel;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.service.CapitalCostService;
import com.mikealbert.service.vo.QuoteVO;
import com.mikealbert.testing.BaseTest;

public class CapitalCostServiceTest extends BaseTest {
	@Resource CapitalCostService capitalCostService;
	private Long TEST_QMD_ID = 255871L;
	@Resource QuotationModelDAO	quotationModelDAO;
	
	@Test
	public void testGetEuipmentDetail() throws MalBusinessException{
		 QuotationModel quotationModel = quotationModelDAO.findById(TEST_QMD_ID).orElse(null);
		 Map<String, Double> map = capitalCostService.getEquipmentDetail(quotationModel, "FACTORY");
		 Assert.assertNotNull(map);
	}
	
	@Test
	public void testGetInvoicedEquipmentDetail() throws MalBusinessException{
		 Map<String, Double> map = capitalCostService.getInvoicedEquipmentDetail(TEST_QMD_ID, "FACTORY","INVOICEAP", "POINV");
		 Assert.assertNotNull(map);
	}	
	@Test
	public void getCapitalElementByProductCode(){
		List<CapitalElement> list = capitalCostService.getCapitalElementByProductCode("CE_LTD");
		Assert.assertNotNull(list);
	}	
}
