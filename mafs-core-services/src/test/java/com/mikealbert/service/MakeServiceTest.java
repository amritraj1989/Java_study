package com.mikealbert.service;

import static org.junit.Assert.assertTrue;

import java.util.List;

import javax.annotation.Resource;
import org.junit.Test;
import org.springframework.data.domain.PageRequest;

import com.mikealbert.data.DataUtilities;
import com.mikealbert.data.vo.MakeVO;
import com.mikealbert.testing.BaseTest;

public class MakeServiceTest extends BaseTest{
	@Resource MakeService makeService;

	final String makeDesciption = "hyundai";
	final String partialMakeDesciption = "toy";
	
	@Test
	public void testGetMakeVOsByDescription(){
		PageRequest pageable = new PageRequest(0,1);
		List<MakeVO> makeVOs = makeService.getMakeVOsByDescription(makeDesciption, pageable);
		
		assertTrue("Zero Makes were found", makeVOs.size() > 0);
		assertTrue("Incorrect Make results", makeVOs.get(0).getMake().getMakeDesc().toLowerCase().contains(makeDesciption));
	}
	
	@Test
	public void testGetMakeVOsByDescriptionCount(){
		Long count = makeService.getMakeVOsByDescriptionCount(DataUtilities.appendWildCardToRight(partialMakeDesciption));
		
		assertTrue("Makes count is zero", count > 1);
	}	

}
