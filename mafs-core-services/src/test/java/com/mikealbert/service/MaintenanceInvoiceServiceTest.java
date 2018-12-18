package com.mikealbert.service;

import static org.junit.Assert.*;
import static org.junit.Assert.assertNotNull;

import javax.annotation.Resource;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Query;
import javax.persistence.Table;

import org.junit.Test;

import com.mikealbert.data.TestQueryConstants;
import com.mikealbert.data.entity.Doc;
import com.mikealbert.data.entity.LogBook;
import com.mikealbert.data.entity.MaintenanceRequest;
import com.mikealbert.data.entity.ObjectLogBook;
import com.mikealbert.data.enumeration.LogBookTypeEnum;
import com.mikealbert.data.enumeration.MaintenanceRequestStatusEnum;
import com.mikealbert.testing.BaseTest;


public class MaintenanceInvoiceServiceTest extends BaseTest{
	@Resource MaintenanceRequestService maintenanceRequestService;
	@Resource MaintenanceInvoiceService maintenanceInvoiceService;
	
	@Resource LogBookService logBookService;
	
	static final Long MRQ_ID = 512394L;
	
	@Test
	public void testIsInvoicePosted() {
		boolean isInvoicePosted = false;
		
		MaintenanceRequest mrq = maintenanceRequestService.getMaintenanceRequestByMrqId(MRQ_ID);
		isInvoicePosted = maintenanceInvoiceService.hasPostedInvoice(mrq);
		
		assertTrue("Failed to detect a posted invoice", isInvoicePosted);
	}
	
}
