package com.mikealbert.vision.service;

import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;

import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.testing.BaseTest;
import com.mikealbert.vision.vo.AmendmentHistoryVO;
import com.mikealbert.vision.vo.EleAmendmentDetailVO;

public class AmendmentHistoryServiceTest extends BaseTest {
	@Resource
	private AmendmentHistoryService amendmentHistoryService;

	//@Test
	public void testGetAmendedQuotes() {
		Long qmdId = 288492L;
		List<AmendmentHistoryVO> list;
		try {
			list = amendmentHistoryService.getAmendedQuotes(qmdId,true,false);
			System.out.println(list.size());
			for (AmendmentHistoryVO amendmentHistoryVO : list) {
				System.out.println("*************");

			}
		} catch (MalBusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Test
	public void testGetInformalAmendedQuoteDetails(){
		Long quotationModelId = 267891L;
		try {
			List<EleAmendmentDetailVO> list = amendmentHistoryService.getElementsFromInformalAmendment(quotationModelId);
			System.out.println(list.size());
		} catch (MalBusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
