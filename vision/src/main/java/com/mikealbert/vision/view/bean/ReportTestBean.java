package com.mikealbert.vision.view.bean;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.primefaces.context.RequestContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.mikealbert.data.enumeration.ReportNameEnum;
import com.mikealbert.data.vo.CustomerOrderConfirmationVO;
import com.mikealbert.exception.MalException;
import com.mikealbert.service.PurchaseOrderReportService;
import com.mikealbert.service.reporting.JasperReportService;
import com.mikealbert.service.util.email.ByteArrayEmailAttachment;
import com.mikealbert.service.util.email.Email;
import com.mikealbert.service.util.email.EmailAddress;
import com.mikealbert.service.util.email.EmailService;
import com.mikealbert.service.util.reporting.JasperReportGenUtil;
import com.mikealbert.vision.view.ViewConstants;

import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;

@Component
@Scope("view")
public class ReportTestBean extends StatefulBaseBean {

	private static final long serialVersionUID = 2437933046906999010L;
	
	@Resource JasperReportService jasperReportService;
	@Resource JasperReportBean jasperReportBean;
	@Resource PurchaseOrderReportService purchaseOrderReportService;
	@Resource EmailService emailService;

	
	@PostConstruct
	public void init() {
		openPage();
	}
	

	public void print() {
		RequestContext.getCurrentInstance().execute("showDocument()");
	}
	
	public void email() {
		
		JasperPrint print = generateReport(5093031l);

		try {
			ByteArrayEmailAttachment attachment;
			EmailAddress senderEmailAddress;
			EmailAddress recipientEmailAddress;
			
			Email email = new Email();
	
			senderEmailAddress = new EmailAddress();
			senderEmailAddress.setAddress("edward.lizak@mikealbert.com");
			
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
			addSimpleMessage("PDF emailed");
		} catch(Exception e) {
			logger.error(e);
			addSimplErrorMessage("Error emailing document - " + e.getMessage());			
		}

		
	}
	
	public void showDocument() {
		try {
			JasperPrint jasperPrint = generateReport(5093031l);			
			jasperReportBean.displayPDFReport(jasperPrint);	
		} catch(Exception ex) {
			logger.error(ex);
			addSimplErrorMessage("Error printing document to screen - " + ex.getMessage());
		}		
	}
		

	private JasperPrint generateReport(Long docId) {
		JasperPrint print;
		List<CustomerOrderConfirmationVO> vos = purchaseOrderReportService.getClientConfirmationOrderReportVO(docId);
		Map<String, Object> parameters;
		
		try {
			parameters = new HashMap<String, Object>();
			parameters.put("REPORT_TITLE", "Client Order Confirmation");					
			print = JasperReportGenUtil.FillReport(ReportNameEnum.CLIENT_ORDER_CONFIRMATION, vos, parameters);	
		} catch (Exception e) {
			throw new MalException(e.getMessage());
		}		

		return print;
	}

	
	
	
	@Override
	protected void loadNewPage() {
		thisPage.setPageDisplayName("Report Test");
		thisPage.setPageUrl(ViewConstants.WEB_USER_SEARCH);
	}


	@Override
	protected void restoreOldPage() {

	}

}