package com.mikealbert.service;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Test;

import com.mikealbert.testing.BaseTest;

public class VelocityServiceTest extends BaseTest {
	@Resource
	private VelocityService	velocityService;
	
	@Test
	public void testMergedTemplate(){

		Map<String,Object> data = new HashMap<String,Object>();
		data.put("testValue", new String("it worked"));
		
		String subject = velocityService.getMergedTemplate(data, "test-subject.vm");
		Assert.assertTrue(subject.contains("it worked"));

		
		
	}

}
