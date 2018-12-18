package com.mikealbert.vision.service;

import javax.annotation.Resource;

import net.sf.jasperreports.engine.JasperPrint;

import org.junit.Test;

import com.mikealbert.data.entity.SupplierProgressHistory;
import com.mikealbert.data.enumeration.CorporateEntity;
import com.mikealbert.exception.MalException;
import com.mikealbert.service.PurchaseOrderReportService;
import com.mikealbert.service.reporting.JasperReportService;
import com.mikealbert.testing.BaseTest;

import static org.mockito.Mockito.*;

public class PurchasingEmailServiceTest extends BaseTest{
	@Resource PurchasingEmailService purchasingEmailService;
	@Resource PurchaseOrderReportService purchaseOrderReportService;
	@Resource JasperReportService jasperReportService;	
	
	static final String USERNAME = "UNIT_TEST";
	
	
	
	@Test
	public void testEmailClientOrderConfirmation() throws MalException{
		JasperPrint print = jasperReportService.getClientOrderConfirmationReport(4228696L);
		purchasingEmailService.emailClientConfirmation(print, 4228696L, USERNAME);
	}
	

}
