package com.mikealbert.vision.view.bean;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

import com.mikealbert.data.entity.User;
import com.mikealbert.testing.jsf.MockFaceContext;
import com.mikealbert.view.ClientState;
import com.mikealbert.vision.view.ViewConstants;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContextTest.xml" })
@Transactional
@TestExecutionListeners({

DependencyInjectionTestExecutionListener.class,
		DirtiesContextTestExecutionListener.class })
public abstract class BeanTestCaseSetup {

	@BeforeClass
	public static void setUp() {   
	} 
	
	public  User getUser() {
		User user = new User();
		user.setEmployeeNo("LIZAK_E");
		return user;
	}	
	public  void  clearFaceMessages() {
		 MockFaceContext.clearFacesMessages();	
	}
	
	public Map<String, Object> getSuccessFaceMessages() {
		return MockFaceContext.getSuccessMessageMap();
	}

	public Map<String, Object> getErrorFaceMessages() {
		return MockFaceContext.getErrorMessageMap();
	}

	public  void  setupPageContract(Map<String, Object> pageInitParam) {		
		ClientState nextPage = new ClientState();
		nextPage.getInputValues().putAll(pageInitParam);
		
		List<ClientState> pageList = new ArrayList<ClientState>();
		pageList.add(nextPage);
		Map<String, Object> requestMap = MockFaceContext.getMockRequestMap();
		requestMap.put(ViewConstants.PAGE_LIST, pageList);
	} 
	
}
