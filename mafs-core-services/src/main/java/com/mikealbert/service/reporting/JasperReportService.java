package com.mikealbert.service.reporting;

import net.sf.jasperreports.engine.JasperPrint;

public interface JasperReportService {
	
	public JasperPrint getClientOrderConfirmationReport(Long docId);
	public JasperPrint getMainPurchaseOrderReport(Long docId, String stockYn);
	public JasperPrint getCourtesyDeliveryInstructionReport(Long docId);
	public JasperPrint getThirdPartyPurchaseOrderReport(Long docId, String stockYn);
	public JasperPrint getVehicleOrderSummaryReport(Long docId, String stockYn);
	public JasperPrint getClientOrderConfirmationBlankReport();
	public JasperPrint getPurchaseOrderCoverSheetReport(Long docId);	
	public JasperPrint getOpenEndContractRevisionDocument(Long currentQmdId, Long revisionQmdId);
	public JasperPrint getOpenEndRevisionScheduleA(Long currentQmdId, Long revisionQmdId);
	public JasperPrint getOpenEndRevisionAmortizationSchedule(Long currentQmdId, Long revisionQmdId);
	
}
