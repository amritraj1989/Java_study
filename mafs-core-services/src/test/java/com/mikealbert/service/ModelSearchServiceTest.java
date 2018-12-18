package com.mikealbert.service;

import static org.junit.Assert.assertTrue;
import java.util.List;
import javax.annotation.Resource;
import org.junit.Test;
import org.springframework.data.domain.PageRequest;

import com.mikealbert.data.vo.MakeModelRangeVO;
import com.mikealbert.data.vo.ModelSearchCriteriaVO;
import com.mikealbert.data.vo.ModelSearchResultVO;
import com.mikealbert.testing.BaseTest;


public class ModelSearchServiceTest extends BaseTest{
	@Resource ModelSearchService modelSearchService;

	final String YEAR = "2015";
	final String MFG_CODE = "F65";
	final String MAKE = "BMW";
	
	@Test
	public void testFindDistinctYears(){
		List<String> distinctYears;
		ModelSearchCriteriaVO criteria;
		
		criteria = new ModelSearchCriteriaVO();
		criteria.setMfgCode(MFG_CODE);
		
		distinctYears = modelSearchService.findDistinctYears(criteria);
		
		assertTrue("No model years were found", distinctYears.size() > 0);
	}
	
	@Test
	public void testFindDistinctMakes(){
		List<String> distinctMakes;
		ModelSearchCriteriaVO criteria;
		
		criteria = new ModelSearchCriteriaVO();
		//criteria.setYear(YEAR);
		criteria.setMfgCode(MFG_CODE);
		
		distinctMakes = modelSearchService.findDistinctMakes(criteria);
		
		assertTrue("No makes were found", distinctMakes.size() > 0);
	}
	
	@Test
	public void testFindModelRanges(){
		List<MakeModelRangeVO> modelRanges;
		ModelSearchCriteriaVO criteria;
		PageRequest page;
		
		page = new PageRequest(0, 2);
		
		criteria = new ModelSearchCriteriaVO();
		criteria.setMfgCode("f65");
		criteria.setModel("f");
		criteria.setMake("");
		
		modelRanges = modelSearchService.findModelRanges(criteria, page);
		
		assertTrue("No model ranges were found", modelRanges.size() > 0);
	}	

	@Test
	public void testFindModelRangesCount(){
		int count;
		ModelSearchCriteriaVO criteria;
		
		criteria = new ModelSearchCriteriaVO();
		criteria.setMfgCode("f65");
		criteria.setModel("f");
		criteria.setMake("");
		
		count = modelSearchService.findModelRangesCount(criteria);
		
		assertTrue("Zero count was returned from make range search", count > 0);
	}
	
	
	@Test
	public void testFindModels(){
		List<ModelSearchResultVO> models;
		ModelSearchCriteriaVO criteria;
		PageRequest page;
		
		page = new PageRequest(0, 2);
		
		criteria = new ModelSearchCriteriaVO();
		criteria.setMfgCode("f65");
		criteria.setModel("f");
		//criteria.setMake(MAKE);
		
		models = modelSearchService.findModels(criteria, page, null);
		
		assertTrue("Model search yield no results", models.size() > 0);
	}	

	@Test
	public void testFindModelsCount(){
		int count;
		ModelSearchCriteriaVO criteria;
		
		criteria = new ModelSearchCriteriaVO();
		criteria.setMake(MAKE);
		
		count = modelSearchService.findModelsCount(criteria);
		
		assertTrue("Model search count is zero", count > 0);
	}	

}
