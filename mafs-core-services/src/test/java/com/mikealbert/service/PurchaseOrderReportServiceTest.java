package com.mikealbert.service;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;

import com.mikealbert.testing.BaseTest;
import com.mikealbert.util.MALUtilities;
import com.mikealbert.data.dao.FleetMasterDAO;
import com.mikealbert.data.entity.ContractLine;
import com.mikealbert.data.entity.DocPropertyValue;
import com.mikealbert.data.entity.Driver;
import com.mikealbert.data.entity.DriverAllocation;
import com.mikealbert.data.entity.FleetMaster;
import com.mikealbert.data.enumeration.DocPropertyEnum;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.service.CustomerAccountService;
import com.mikealbert.service.DriverGradeService;
import com.mikealbert.service.DriverService;

public class PurchaseOrderReportServiceTest extends BaseTest {

	@Resource PurchaseOrderReportService purchaseOrderReportService;
	
	static final String PO_RELEASED_PRIOR_TO_PO_QUEUE_INSTALL_WITH_DELIVERING_DEALER = 
			"SELECT max(d.doc_id) " 
              + "FROM doc d " 
              + "WHERE d.doc_type = 'PORDER' "  
              + "    AND d.doc_status = 'R' " 
              + "    AND nvl(d.order_type, 'x') != 'T' " 
              + "    AND d.source_code IN ('FLORDER', 'FLQUOTE') " 
              + "    AND d.op_code != 'CONV' "
              + "    AND d.sub_acc_code IS NOT NULL "
              + "    AND d.posted_date < to_date('12/31/2015', 'MM/DD/YYYY')";
		
	static final String PO_RELEASED_PRIOR_TO_PO_QUEUE_INSTALL_WITH_NO_DELIVERING_DEALER = 
			"SELECT max(d.doc_id) " 
              + "FROM doc d " 
              + "WHERE d.doc_type = 'PORDER' "  
              + "    AND d.doc_status = 'R' " 
              + "    AND nvl(d.order_type, 'x') != 'T' " 
              + "    AND d.source_code IN ('FLORDER', 'FLQUOTE') " 
              + "    AND d.op_code != 'CONV' "
              + "    AND d.sub_acc_code IS NULL "
              + "    AND d.posted_date < to_date('12/31/2015', 'MM/DD/YYYY')";	
	
	@Test 
	public void testHasClientConfirmationBeenGenerated() {
		boolean isEmailed = false;
		Object result;
		Long mainPODocId = ((BigDecimal)em.createNativeQuery(PO_RELEASED_PRIOR_TO_PO_QUEUE_INSTALL_WITH_DELIVERING_DEALER).getSingleResult()).longValue();	
		isEmailed = purchaseOrderReportService.hasClientConfirmationBeenGenerated(mainPODocId);
	    assertTrue("Did not detect that the Client Order Confirmation had been generated/emailed", isEmailed);
	    
		isEmailed = false;
		result = em.createNativeQuery(PO_RELEASED_PRIOR_TO_PO_QUEUE_INSTALL_WITH_NO_DELIVERING_DEALER).getSingleResult();		
		if(!MALUtilities.isEmpty(result)) {
			mainPODocId = ((BigDecimal)result).longValue();	
			isEmailed = purchaseOrderReportService.hasClientConfirmationBeenGenerated(mainPODocId);
			assertTrue("Did not detect that the Client Order Confirmation had not been generated/emailed", !isEmailed);
		} else {
			System.out.println("Could not perform negative test in testHasClientConfirmationBeenGenerated");
		}
	    
	}
}
