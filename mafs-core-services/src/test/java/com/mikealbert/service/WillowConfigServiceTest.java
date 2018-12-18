package com.mikealbert.service;

import java.util.List;

import javax.annotation.Resource;

import org.junit.Ignore;
import org.junit.Test;
import static org.junit.Assert.*;
import com.mikealbert.data.vo.AccountVO;
import com.mikealbert.testing.BaseTest;

public class WillowConfigServiceTest  extends BaseTest {
	
	@Resource WillowConfigService willowConfigService;
	
	@Test
	public void testGetConfigValue(){
		String configName = "LEASE_PLAN_PAYEE_ACC_CODE";
		String configValue = willowConfigService.getConfigValue(configName);	
		assertNotNull(configValue);
		//assertTrue(configValue.equals("00156597,00041851,00162520"));
	}	
	
	@Ignore
	@Test
	public void testGetLeasePlanPayeeDetail(){
		List<AccountVO> accountList = willowConfigService.getLeasePlanPayeeDetail();	
		assertNotNull(accountList);
		//assertTrue(accountList.size() == 3);
	}
	
}
