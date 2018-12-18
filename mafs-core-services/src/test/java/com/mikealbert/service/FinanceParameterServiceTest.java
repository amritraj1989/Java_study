package com.mikealbert.service;

import static org.junit.Assert.*;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import com.mikealbert.testing.BaseTest;

import com.mikealbert.data.entity.FinanceParameter;
import com.mikealbert.data.vo.QuotationProfileFinanceVO;


public class FinanceParameterServiceTest  extends BaseTest{
		
	@Resource FinanceParameterService financeParameterService;
    
	@Test
    public void testGetFinanceParameters(){

		Date targetDate;
		String financeParameterKey;
		FinanceParameter financeParameter;
		Calendar cal = Calendar.getInstance();
		
		
		// get the one for today
		financeParameterKey = "MINIMUM_BANK_RATE";
		targetDate = cal.getTime();
		financeParameter = financeParameterService.getEffectiveFinanceParameterByDate(financeParameterKey, targetDate);
		assertEquals("Getting today failed", financeParameter.getParameterId(), "1359");
		
		// get an old one
		financeParameterKey = "MINIMUM_BANK_RATE";
		cal.set(2009, 1, 15);   // 1=feb
		targetDate = cal.getTime();
		financeParameter = financeParameterService.getEffectiveFinanceParameterByDate(financeParameterKey, targetDate);
		assertEquals("Getting an old one failed", financeParameter.getParameterId(), "899");
		
		// get active one with an inactive
		financeParameterKey = "CE_MIN_PROFIT";
		cal.set(2009, 1, 15);   // 1=feb
		targetDate = cal.getTime();
		financeParameter = financeParameterService.getEffectiveFinanceParameterByDate(financeParameterKey, targetDate);
		assertEquals("Getting good one with inactive failed", financeParameter.getParameterId(), "618");
		
		// try to get one for an inactive date
		financeParameterKey = "CE_MIN_PROFIT";
		cal.set(2007, 1, 15);   // 1=feb
		targetDate = cal.getTime();
		financeParameter = financeParameterService.getEffectiveFinanceParameterByDate(financeParameterKey, targetDate);
		assertEquals("Getting bad one with inactive failed", financeParameter, null);
	}
	
	@Ignore
	@Test
	public void findProfileFinanceByClientAndParameter(){
		List<QuotationProfileFinanceVO> quotationProfileFinanceList = financeParameterService.getProfileFinancesByClientAndParameter((long) 1, "C", "00004168", (long) 38);	
        Assert.assertTrue(quotationProfileFinanceList.size() == 10);
        }
	
	@Ignore
	@Test
	public void countProfileFinanceByClientAndParameter(){
		int count = financeParameterService.countProfileFinancesByClientAndParameter((long) 1, "C", "00004168", (long) 38);	
        Assert.assertTrue(count == 10);
    }
}
