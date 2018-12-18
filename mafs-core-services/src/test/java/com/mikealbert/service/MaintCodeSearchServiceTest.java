package com.mikealbert.service;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

import com.mikealbert.data.DataConstants;
import com.mikealbert.data.vo.ProviderMaintCodeSearchCriteriaVO;
import com.mikealbert.data.vo.ProviderMaintCodeSearchResultsVO;
import com.mikealbert.testing.BaseTest;

public class MaintCodeSearchServiceTest extends BaseTest {
	
	@Resource MaintCodeSearchService searchSvc;
	
	@Test
	public void searchProviderMaintCodesTest(){
		ProviderMaintCodeSearchCriteriaVO searchCriteriaVO = new ProviderMaintCodeSearchCriteriaVO();

		searchCriteriaVO.setApprovedStatus(ProviderMaintCodeSearchCriteriaVO.ALL_STATUS);
		searchCriteriaVO.setMaintenanceCategory("MISC_MAINT");
		searchCriteriaVO.setServiceProvider("Nwrk - Conversion");
		
		PageRequest page1 = new PageRequest(0,10);
		List<String> fields = new ArrayList<String>();
		fields.add(DataConstants.PROVIDER_MAINT_CODE_SORT_MAFS_DESC);
		Sort sort1 = new Sort(Direction.DESC,fields);
		
		ProviderMaintCodeSearchResultsVO results = searchSvc.searchProviderMaintCodes(searchCriteriaVO, page1, sort1);
	
		assertTrue(results.getResultsLines().size() == 10);
		assertTrue(results.getResultCount() > 10);
	}

}
