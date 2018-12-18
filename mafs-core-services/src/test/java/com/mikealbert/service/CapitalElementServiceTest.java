package com.mikealbert.service;

import java.util.List;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import com.mikealbert.data.dao.QuotationCapitalElementDAO;
import com.mikealbert.data.entity.QuotationCapitalElement;
import com.mikealbert.testing.BaseTest;

public class CapitalElementServiceTest extends BaseTest {
	@Resource
	private CapitalElementService	capitalElementService;
	@Resource QuotationCapitalElementDAO quotationCapitalElementDAO;
	
	@Ignore
	@Test
	public void testCapitalElementsWithAndWithoutQuote(){
		
		Long qmdId = 463765l;
		
		List<QuotationCapitalElement> qceListByQmdId = quotationCapitalElementDAO.findByQmdID(qmdId);
		
		List<QuotationCapitalElement> qceListWithoutQuote =  capitalElementService.getApplicableCapitalElementsWithValue(25586l, 
																														"USC70CHS341A0", 
																														1l, 
																														"C", 
																														"00028573",
																														"F");
		
		Assert.assertTrue(qceListByQmdId.size() == qceListWithoutQuote.size());
		
	}
	
	@Ignore
	@Test
	public void testCapitalElementsValueWithAndWithoutQuote(){
		
		Long qmdId = 463765l;
		
		List<QuotationCapitalElement> qceListByQmdId = quotationCapitalElementDAO.findByQmdID(qmdId);
		
		List<QuotationCapitalElement> qceListWithoutQuote =  capitalElementService.getApplicableCapitalElementsWithValue(25586l, 
																														"USC70CHS341A0", 
																														1l, 
																														"C", 
																														"00028573",
																														"F");
		boolean valueMatches = false;
		for(QuotationCapitalElement qceByQmd : qceListByQmdId){
			for(QuotationCapitalElement qceWithoutQmd : qceListWithoutQuote){
				
				if(qceByQmd.getCapitalElement().getCode().equals(qceWithoutQmd.getCapitalElement().getCode())){
					if(qceByQmd.getValue().compareTo(qceWithoutQmd.getValue()) == 0){
						valueMatches = true;
					}else{
						Assert.assertTrue(false);
					}
					System.out.println(qceByQmd.getCapitalElement().getCode() + "----: " + qceByQmd.getValue() + "-- without quote value:" + qceWithoutQmd.getValue());
					break;
				}
			}
		}
			
		Assert.assertTrue(valueMatches);
		
	}

}
