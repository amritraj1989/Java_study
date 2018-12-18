package com.mikealbert.vision.service;

import static org.junit.Assert.assertTrue;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import javax.annotation.Resource;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.data.domain.PageRequest;
import com.mikealbert.data.entity.SupplierProgressHistory;
import com.mikealbert.data.enumeration.UnitProgressCodeEnum;
import com.mikealbert.data.vo.OrderProgressSearchCriteriaVO;
import com.mikealbert.data.vo.OrderProgressSearchResultVO;
import com.mikealbert.testing.BaseTest;

public class OrderProgressServiceTest extends BaseTest {

	@Resource OrderProgressService orderProgressService;
	
	final PageRequest page = new PageRequest(0, 5);
	
	@Test
	public void testFindUnits() {
		OrderProgressSearchCriteriaVO searchCriteria = new OrderProgressSearchCriteriaVO();
		searchCriteria.setOrderType("F");
		
		List<OrderProgressSearchResultVO>  list = orderProgressService.findUnits(searchCriteria, page, null);
		
		assertTrue("Order Progress Search list is empty", list.size() > 0);
	}
	
	@Test
	@Ignore
	public void testSaveETA() {
		Long docId = 0L;
		String employeeNo = "KUMAR_RA";
		Calendar calender = GregorianCalendar.getInstance();
		calender.set(2015, 01, 01);
		calender.add(Calendar.DAY_OF_MONTH, 10);
		
		SupplierProgressHistory etaSPH = new SupplierProgressHistory();
		etaSPH.setDocId(docId);
		etaSPH.setProgressType(UnitProgressCodeEnum.ETA.getCode());
		etaSPH.setProgressNote("Test Note");
		etaSPH.setOpCode(employeeNo);
		etaSPH.setActionDate(calender.getTime());
		etaSPH.setEnteredDate(new Date());
		
		orderProgressService.saveETAandNotes(etaSPH, 0L, "KUAMR_RA", "Test Notes");
		
//		System.out.println("Generated id = " + etaSPH.getSphId());
//		assertTrue("ETA SPH saved successfully", etaSPH.getSphId() != null);
	}
		
}
