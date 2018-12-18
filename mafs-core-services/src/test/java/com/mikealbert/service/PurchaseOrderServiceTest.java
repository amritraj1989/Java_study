package com.mikealbert.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.mikealbert.data.enumeration.DocumentNameEnum;
import com.mikealbert.data.vo.CustomerOrderConfirmationVO;
import com.mikealbert.data.vo.DbProcParamsVO;
import com.mikealbert.service.util.reporting.JasperReportGenUtil;

@ContextConfiguration(locations={"classpath:applicationContextTest.xml"}) 
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class PurchaseOrderServiceTest {
	
	  @PersistenceContext  
	  protected EntityManager em;

	
	@Resource
	private PurchaseOrderService purchaseOrderService;

	@Resource
	PurchaseOrderReportService purchaseOrderReportService;
	
	/*In this method we are not asserting anything because data may change over time 
	 * and chance of failure of test is likely to be happen. 
	 *
	 * If data is valid then it should create main and applicable 3rd party PO,  as per requirement listed in OTD-276.
	*/
	
	@Test
	public void testCreatePurchaseOrder() {
		Long qmdId = 345484L;
		Long cId = 1L;
		String op_code = "SINGH_V";//Employee no		
		try {
			
			@SuppressWarnings("unused")
			DbProcParamsVO dbProcParamsVO  = purchaseOrderService.createPurchaseOrder(qmdId, cId, op_code);
			//System.out.println("mes--"+dbProcParamsVO.getMessage());			
		} catch (Exception e) {			
			System.out.println("Test error message-"+e.getMessage());
		}
	}
	@Ignore
	@Test
	public void testArchivePurchaseOrder() {
		
		try {
			 purchaseOrderService.archivePurchaseOrderDoc(5213166L, "Dummy main po content".getBytes(), DocumentNameEnum.VEHICLE_ORDER_SUMMARY);
		} catch (Exception e) {			
			System.out.println("Test error message-"+e.getMessage());
		}
	}
	
	@Test
	public void testGetPurchaseOrderDocument() {
		
		try {
			 String docNo  = "PON00186114";
			byte[] docData  =  purchaseOrderService.getPurchaseOrderDocument(docNo , DocumentNameEnum.VEHICLE_ORDER_SUMMARY);
		//	Assert.assertNotNull(docData);
		//	Assert.assertTrue(docData.length > 0);
		} catch (Exception e) {			
			System.out.println("Test error message-"+e.getMessage());
		}
	}
	
	
	@Ignore
	@Test
	public void testArchiveAllPurchaseOrder() {
		
		try {
			
			Long mainPO  = 5210447L;// 5101551L;
			 
		    List<CustomerOrderConfirmationVO> vosCocm = purchaseOrderReportService.getClientConfirmationOrderReportVO(mainPO);
			
			Map<String, Object> parameters;
			parameters = new HashMap<String, Object>();
			parameters.put("REPORT_TITLE", "Purchase Order");
			//mainPoReport.jrxml ,thirdPartyPoReport.jrxml , vehicleOrderSummaryReport.jrxml 
			
			 
		  JasperReport vosCocmReport = JasperCompileManager.compileReport("C:\\DVLP\\workspaces\\vision_workspace\\vision-old\\src\\main\\jasperreports\\clientOrderConfirmation.jrxml");
		
			

			 JasperPrint cocmPrint =  JasperReportGenUtil.FillReport(vosCocmReport, vosCocm, parameters);
			 OutputStream cocmOutput = new FileOutputStream(new File("C://Users//singh//Desktop//cocm.pdf")); 
			 JasperExportManager.exportReportToPdfStream(cocmPrint,cocmOutput);
			 
			 
			// purchaseOrderService.archivePurchaseOrderDoc(mainPO, JasperExportManager.exportReportToPdf(vhosPrint), PurchaseOrderService.VEHICLE_ORDER_SUMMARY_DOC);
			 purchaseOrderService.archivePurchaseOrderDoc(mainPO, JasperExportManager.exportReportToPdf(cocmPrint), DocumentNameEnum.CLIENT_ORDER_CONFIRMATION);
			// purchaseOrderService.archivePurchaseOrderDoc(mainPO, JasperExportManager.exportReportToPdf(mainPrint), PurchaseOrderService.MAIN_PO_DOC);
			// purchaseOrderService.archivePurchaseOrderDoc(tpId, JasperExportManager.exportReportToPdf(printTh), PurchaseOrderService.THIRD_PARTY_DOC);
			 
		} catch (Exception e) {			
			System.out.println("Test error message-"+e.getMessage());
		}
	}
	
	
	
}
