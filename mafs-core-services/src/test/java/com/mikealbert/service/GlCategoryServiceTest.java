package com.mikealbert.service;

import java.util.List;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.data.domain.Pageable;

import com.mikealbert.data.entity.CostDatabaseCategoryCodes;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.testing.BaseTest;

public class GlCategoryServiceTest extends BaseTest {
	@Resource 
	private GlCategoryService	glCategoryService;
	
	@Test
	public void testGetGlCategories(){
		Pageable page = null;
		List<CostDatabaseCategoryCodes> list;
		String category = "1_LEASE";
		try {
			list = glCategoryService.getGlCategories(category, page);
			Assert.assertNotNull(list);
		} catch (MalBusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
