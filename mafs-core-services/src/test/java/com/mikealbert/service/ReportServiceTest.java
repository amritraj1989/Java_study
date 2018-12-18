package com.mikealbert.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;

import com.mikealbert.data.vo.CustomerOrderConfirmationVO;
import com.mikealbert.data.vo.MainPoVO;
import com.mikealbert.data.vo.OeConRevAmortizationScheduleVO;
import com.mikealbert.data.vo.OeConRevScheduleAVO;
import com.mikealbert.data.vo.OeConRevTermsVO;
import com.mikealbert.data.vo.ThirdPartyPoVO;
import com.mikealbert.data.vo.VehicleOrderSummaryVO;
import com.mikealbert.exception.MalException;
import com.mikealbert.service.reporting.JasperReportService;
import com.mikealbert.service.util.email.ByteArrayEmailAttachment;
import com.mikealbert.service.util.email.Email;
import com.mikealbert.service.util.email.EmailAddress;
import com.mikealbert.service.util.email.EmailService;
import com.mikealbert.service.util.email.EmailServiceSpringImpl;
import com.mikealbert.service.util.reporting.JasperReportGenUtil;
import com.mikealbert.testing.BaseTest;
import com.mikealbert.util.MALUtilities;

public class ReportServiceTest extends BaseTest {

	@Resource
	PurchaseOrderReportService purchaseOrderReportService;
	@Resource
	OeContractRevisionTermsService oeContractRevisionTermsService;
	@Resource
	OeContractRevisionSchedAService oeContractRevisionSchedAService;
	@Resource
	OeContractRevisionAmortizationService oeContractRevisionAmortizationService;
	@Resource
	JasperReportService jasperReportService;
	@Resource @Qualifier("emailService") EmailService emailService;

	@Autowired
	private ApplicationContext appContext;

	
	@Test
	public void testMainPoReport() {
		try {
		/*	
			List<MainPoVO> mnVos = purchaseOrderReportService.getMainPoReportVO(4839729L, "N");
		    List<ThirdPartyPoVO> thVos = purchaseOrderReportService.getThirdPartyPoReportVO(4839730L, "N");
			
		    List<VehicleOrderSummaryVO> vosVhos = purchaseOrderReportService.getVehicleOrderSummaryReportVO(4839729L, "N");
		    
		    List<CustomerOrderConfirmationVO> vosCocm = purchaseOrderReportService.getClientConfirmationOrderReportVO(4842037L, "N");
			
			Map<String, Object> parameters;
			parameters = new HashMap<String, Object>();
			parameters.put("REPORT_TITLE", "Purchase Order");
			//mainPoReport.jrxml ,thirdPartyPoReport.jrxml , vehicleOrderSummaryReport.jrxml 
			
			 
			 JasperReport mainReport = JasperCompileManager.compileReport("C:\\DVLP\\workspaces\\vision_workspace\\vision-old\\src\\main\\jasperreports\\mainPoReport.jrxml");
			 JasperReport thReport = JasperCompileManager.compileReport("C:\\DVLP\\workspaces\\vision_workspace\\vision-old\\src\\main\\jasperreports\\thirdPartyPoReport.jrxml");
			 JasperReport vhosReport = JasperCompileManager.compileReport("C:\\DVLP\\workspaces\\vision_workspace\\vision-old\\src\\main\\jasperreports\\vehicleOrderSummaryReport.jrxml");
			 JasperReport vosCocmReport = JasperCompileManager.compileReport("C:\\DVLP\\workspaces\\vision_workspace\\vision-old\\src\\main\\jasperreports\\clientOrderConfirmation.jrxml");
		
			 JasperPrint mainPrint =  JasperReportGenUtil.FillReport(mainReport, mnVos, parameters);
			 OutputStream mainOutput = new FileOutputStream(new File("C://Users//singh//Desktop//mr.pdf")); 
			 JasperExportManager.exportReportToPdfStream(mainPrint, mainOutput); 

			 
			 JasperPrint printTh =  JasperReportGenUtil.FillReport(thReport, thVos, parameters);
			 OutputStream thOutput = new FileOutputStream(new File("C://Users//singh//Desktop//thr.pdf")); 
			 JasperExportManager.exportReportToPdfStream(printTh,thOutput);
			 
			 
			 JasperPrint vhosPrint =  JasperReportGenUtil.FillReport(vhosReport, vosVhos, parameters);
			 OutputStream vhosOutput = new FileOutputStream(new File("C://Users//singh//Desktop//vhos.pdf")); 
			 JasperExportManager.exportReportToPdfStream(vhosPrint,vhosOutput);
		

			 JasperPrint cocmPrint =  JasperReportGenUtil.FillReport(vosCocmReport, vosCocm, parameters);
			 OutputStream cocmOutput = new FileOutputStream(new File("C://Users//singh//Desktop//cocm.pdf")); 
			 JasperExportManager.exportReportToPdfStream(cocmPrint,cocmOutput);
			 */

			System.out.println("Done");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testOpenEndContractRevisionTermsReport() {
		Long currentQmdId = 327260l;
		Long revisionQmdId = 551629l;

		try {	
/*			List<OeConRevTermsVO> oeConRevTermsVos = oeContractRevisionTermsService.getOeConRevTermsReportVO(currentQmdId, revisionQmdId);	
			
			Map<String, Object> parameters;
			parameters = new HashMap<String, Object>();
//			parameters.put("REPORT_TITLE", "Open End Contract Revision Terms Report");
			
			 JasperReport oeConRevTermsReport = JasperCompileManager.compileReport("C:\\Development\\JasperWorkspace\\MyReports\\src\\resources\\oeConRevTerms.jrxml");
			 JasperPrint oeConRevTermsPrint =  JasperReportGenUtil.FillReport(oeConRevTermsReport, oeConRevTermsVos, parameters);
			 OutputStream oeConRevTermsOutput = new FileOutputStream(new File("C://Users//opitz//Desktop//oe.pdf")); 
			 JasperExportManager.exportReportToPdfStream(oeConRevTermsPrint, oeConRevTermsOutput); */			 
			
			System.out.println("Done Terms Report");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testOpenEndContractRevisionScheduleAReport() {
		//unit 00984707
		Long currentQmdId = 327260l;		
		Long revisionQmdId = 551629l;

		try {	
/*			List<OeConRevScheduleAVO> oeConRevScheduleAVOs = oeContractRevisionSchedAService.getOeConRevScheduleAReportVO(currentQmdId,revisionQmdId);	
			
			Map<String, Object> parameters;
			parameters = new HashMap<String, Object>();
//			parameters.put("REPORT_TITLE", "Open End Contract Revision Schedule A Report");
			
			 JasperReport oeConRevScheduleAReport = JasperCompileManager.compileReport("C:\\Development\\JasperWorkspace\\MyReports\\src\\resources\\oeRevisionScheduleA.jrxml");
			 JasperPrint oeConRevScheudleAPrint =  JasperReportGenUtil.FillReport(oeConRevScheduleAReport, oeConRevScheduleAVOs, parameters);
			 OutputStream oeConRevScheudleAOutput = new FileOutputStream(new File("C://Users//opitz//Desktop//oeScheduleA.pdf")); 
			 JasperExportManager.exportReportToPdfStream(oeConRevScheudleAPrint, oeConRevScheudleAOutput); 			 
*/				
			System.out.println("Done ScheduleA Report");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testOpenEndContractRevisionAmortizationReport() {
		//unit 01001170
		Long currentQmdId = 538109l;
		Long revisionQmdId = 554350l;

		try {	
/*			List<OeConRevAmortizationScheduleVO> oeConRevAmortizationScheduleVOs = oeContractRevisionAmortizationService.getOeConRevAmortizationReportVO(currentQmdId,revisionQmdId);	
			
			Map<String, Object> parameters;
			parameters = new HashMap<String, Object>();
//			parameters.put("REPORT_TITLE", "Open End Contract Revision Amortization Report");
			
			 //JasperReport oeConRevAmortizationTableReport = JasperCompileManager.compileReport("C:\\Development\\JasperWorkspace\\MyReports\\src\\resources\\amortizationTable.jrxml");
			 JasperReport oeConRevAmortizationTableReport = JasperCompileManager.compileReport("C:\\Development\\Workspaces\\neon_workspace\\mafs-core-services-oer-RC2\\src\\main\\jasperreports\\amortizationTable.jrxml");			
			 //JasperReport oeConRevAmortizationTableRevisionReport = JasperCompileManager.compileReport("C:\\Development\\JasperWorkspace\\MyReports\\src\\resources\\amortizationTableRevision.jrxml");
			 JasperReport oeConRevAmortizationTableRevisionReport = JasperCompileManager.compileReport("C:\\Development\\Workspaces\\neon_workspace\\mafs-core-services-oer-RC2\\src\\main\\jasperreports\\amortizationTableRevision.jrxml");			 
			 //JasperReport oeConRevAmortizationReport = JasperCompileManager.compileReport("C:\\Development\\JasperWorkspace\\MyReports\\src\\resources\\oeRevisionAmortizationSchedule.jrxml");
			 JasperReport oeConRevAmortizationReport = JasperCompileManager.compileReport("C:\\Development\\Workspaces\\neon_workspace\\mafs-core-services-oer-RC2\\src\\main\\jasperreports\\oeRevisionAmortizationSchedule.jrxml");
			 JasperPrint oeConRevAmortizationPrint =  JasperReportGenUtil.FillReport(oeConRevAmortizationReport, oeConRevAmortizationScheduleVOs, parameters);
			 OutputStream oeConRevAmortizationOutput = new FileOutputStream(new File("C://Users//opitz//Desktop//oeAmortization.pdf")); 
			 JasperExportManager.exportReportToPdfStream(oeConRevAmortizationPrint, oeConRevAmortizationOutput); 			 
	*/		
			System.out.println("Done Amortization Report");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}	

	@Test
	public void testJasperReports() {
		//unit 01001170
		Long currentQmdId = 538109l;
		Long revisionQmdId = 554350l;

		try {	
/*			List<OeConRevAmortizationScheduleVO> oeConRevAmortizationScheduleVOs = oeContractRevisionAmortizationService.getOeConRevAmortizationReportVO(currentQmdId,revisionQmdId);	
			
			Map<String, Object> parameters;
			parameters = new HashMap<String, Object>();
//			parameters.put("REPORT_TITLE", "Open End Contract Revision Amortization Report");
			
			 //JasperReport oeConRevAmortizationTableReport = JasperCompileManager.compileReport("C:\\Development\\JasperWorkspace\\MyReports\\src\\resources\\amortizationTable.jrxml");
			 JasperReport oeConRevAmortizationTableReport = JasperCompileManager.compileReport("C:\\Development\\Workspaces\\neon_workspace\\mafs-core-services-oer-RC2\\src\\main\\jasperreports\\amortizationTable.jrxml");			
			 //JasperReport oeConRevAmortizationTableRevisionReport = JasperCompileManager.compileReport("C:\\Development\\JasperWorkspace\\MyReports\\src\\resources\\amortizationTableRevision.jrxml");
			 JasperReport oeConRevAmortizationTableRevisionReport = JasperCompileManager.compileReport("C:\\Development\\Workspaces\\neon_workspace\\mafs-core-services-oer-RC2\\src\\main\\jasperreports\\amortizationTableRevision.jrxml");			 
			 //JasperReport oeConRevAmortizationReport = JasperCompileManager.compileReport("C:\\Development\\JasperWorkspace\\MyReports\\src\\resources\\oeRevisionAmortizationSchedule.jrxml");
			 JasperReport oeConRevAmortizationReport = JasperCompileManager.compileReport("C:\\Development\\Workspaces\\neon_workspace\\mafs-core-services-oer-RC2\\src\\main\\jasperreports\\oeRevisionAmortizationSchedule.jrxml");
			 JasperPrint oeConRevAmortizationPrint =  JasperReportGenUtil.FillReport(oeConRevAmortizationReport, oeConRevAmortizationScheduleVOs, parameters);
			 OutputStream oeConRevAmortizationOutput = new FileOutputStream(new File("C://Users//opitz//Desktop//oeAmortization.pdf")); 
			 JasperExportManager.exportReportToPdfStream(oeConRevAmortizationPrint, oeConRevAmortizationOutput); 			 
	*/		
			System.out.println("Done Amortization Report");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}	

	
	@Test
	public void testEmailClientOrderConfirmation() throws MalException{

		EmailServiceSpringImpl emailService = (EmailServiceSpringImpl) appContext.getBean("emailServiceImpl");	

		JasperPrint print = jasperReportService.getClientOrderConfirmationReport(4228696L);

		try {
		ByteArrayEmailAttachment attachment;
		EmailAddress senderEmailAddress;
		EmailAddress recipientEmailAddress;
		
		Email email = new Email();

		senderEmailAddress = new EmailAddress();
		senderEmailAddress.setAddress("ejl");
		
		recipientEmailAddress = new EmailAddress();
		recipientEmailAddress.setAddress("ejl@mikealbert.com");
		
		
		email.setFrom(senderEmailAddress);
		email.setTo(recipientEmailAddress);
		email.setSubject("Test subject");
		email.setMessage("test of pdf");
		

		attachment = new ByteArrayEmailAttachment();
		attachment.setContent(JasperExportManager.exportReportToPdf(print));
		attachment.setFileName("test.pdf");		
		email.setAttachment(attachment);

		emailService.sendEmail(email);	

		} catch(Exception e) {
			System.out.println("Exception: " + e);
		}
		
		System.out.println("Email sent: " + print.getName());
	}

}
