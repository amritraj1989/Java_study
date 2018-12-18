package com.mikealbert.service;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import javax.annotation.Resource;

import org.junit.Test;

import com.mikealbert.data.vo.WillowUserLovVO;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.testing.BaseTest;
import com.mikealbert.util.MALUtilities;

public class WillowUserServiceTest  extends BaseTest {
	
	@Resource WillowUserService willowUserService;
	
	@Test
	public void testGetWilloUserByAdName(){
		WillowUserLovVO vo = null;
		try {
			vo = willowUserService.getWillowUserByAdUsername("saket.maheshwary");
		} catch (MalBusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertNotNull(vo);
	}	
	
	@Test
	public void testGetEmailForEmployeeNo(){
		WillowUserLovVO vo = null;
		String email;
		try {
			vo = willowUserService.getWillowUserByAdUsername("saket.maheshwary");
		} catch (MalBusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(vo != null){
			try {
				email = willowUserService.getEmailAddress(vo.getEmployeeNo(), 1l);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		assertNotNull(vo);
	}
}
