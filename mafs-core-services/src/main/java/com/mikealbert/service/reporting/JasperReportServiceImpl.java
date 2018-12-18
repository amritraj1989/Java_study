package com.mikealbert.service.reporting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import net.sf.jasperreports.engine.JasperPrint;

import org.springframework.stereotype.Service;

import com.mikealbert.data.enumeration.ReportNameEnum;
import com.mikealbert.data.vo.CourtesyDeliveryInstructionVO;
import com.mikealbert.data.vo.CustomerOrderConfirmationVO;
import com.mikealbert.data.vo.MainPoVO;
import com.mikealbert.data.vo.OeConRevAmortizationScheduleVO;
import com.mikealbert.data.vo.OeConRevScheduleAVO;
import com.mikealbert.data.vo.OeConRevTermsVO;
import com.mikealbert.data.vo.PurchaseOrderCoverSheetVO;
import com.mikealbert.data.vo.ThirdPartyPoVO;
import com.mikealbert.data.vo.VehicleOrderSummaryVO;
import com.mikealbert.exception.MalException;
import com.mikealbert.service.OeContractRevisionAmortizationService;
import com.mikealbert.service.OeContractRevisionSchedAService;
import com.mikealbert.service.OeContractRevisionTermsService;
import com.mikealbert.service.PurchaseOrderReportService;
import com.mikealbert.service.util.reporting.JasperReportGenUtil;
import com.mikealbert.util.MALUtilities;

@Service("jasperReportService")
public class JasperReportServiceImpl implements JasperReportService {
	@Resource PurchaseOrderReportService purchaseOrderReportService;
	@Resource OeContractRevisionTermsService oEContractRevisionTermsService;
	@Resource OeContractRevisionSchedAService oeContractRevisionSchedAService;
	@Resource OeContractRevisionAmortizationService oeContractRevisionAmortizationService;

	
	public JasperPrint getClientOrderConfirmationReport(Long docId){
		JasperPrint print;
		List<CustomerOrderConfirmationVO> vos = purchaseOrderReportService.getClientConfirmationOrderReportVO(docId);
		Map<String, Object> parameters;
		
		try {
			parameters = new HashMap<String, Object>();
			parameters.put("REPORT_TITLE", "Client Order Confirmation");					
			print = JasperReportGenUtil.FillReport(ReportNameEnum.CLIENT_ORDER_CONFIRMATION, vos, parameters);	
			purchaseOrderReportService.logClientOrderConfirmationGenerated(docId);
		} catch (Exception e) {
			throw new MalException(e.getMessage());
		}		
		
		return print;
	}
	
	public JasperPrint getMainPurchaseOrderReport(Long docId, String stockYn) {
		JasperPrint print;
		List<MainPoVO> vos = purchaseOrderReportService.getMainPoReportVO(docId, stockYn);
		Map<String, Object> parameters;
		
		try {
			parameters = new HashMap<String, Object>();
			parameters.put("REPORT_TITLE", "Purchase Order");					
			print = JasperReportGenUtil.FillReport(ReportNameEnum.MAIN_PURHCASE_ORDER, vos, parameters);			
		} catch (Exception e) {
			throw new MalException(e.getMessage());
		}		
		
		return print;		
	}
	
	public JasperPrint getCourtesyDeliveryInstructionReport(Long docId) {
		JasperPrint print;
		List<CourtesyDeliveryInstructionVO> vos = purchaseOrderReportService.getCourtesyDeliveryInstructionVO(docId);
		Map<String, Object> parameters;
		
		try {
			parameters = new HashMap<String, Object>();
			parameters.put("REPORT_TITLE", "Courtesy Delivery Instruction");					
			print = JasperReportGenUtil.FillReport(ReportNameEnum.COURTESY_DELIVERY_INSTRUCTION, vos, parameters);			
		} catch (Exception e) {
			throw new MalException(e.getMessage());
		}		
		
		return print;		
	}
	
	public JasperPrint getThirdPartyPurchaseOrderReport(Long docId, String stockYn){
		JasperPrint print;
		List<ThirdPartyPoVO> vos = purchaseOrderReportService.getThirdPartyPoReportVO(docId, stockYn);
		Map<String, Object> parameters;
		
		try {
			parameters = new HashMap<String, Object>();
			parameters.put("REPORT_TITLE", "Third Party PO");					
			print = JasperReportGenUtil.FillReport(ReportNameEnum.THIRD_PARTY_PURHCASE_ORDER, vos, parameters);			
		} catch (Exception e) {
			throw new MalException(e.getMessage());
		}		
		
		return print;	
	}
	
	public JasperPrint getVehicleOrderSummaryReport(Long docId, String stockYn){
		JasperPrint print;
		List<VehicleOrderSummaryVO> vos = purchaseOrderReportService.getVehicleOrderSummaryReportVO(docId, "N");
		Map<String, Object> parameters;
		
		try {
			parameters = new HashMap<String, Object>();
			parameters.put("REPORT_TITLE", "Vehicle Order Summary");					
			print = JasperReportGenUtil.FillReport(ReportNameEnum.VEHICLE_PURCHASE_ORDER_SUMMARY, vos, parameters);			
		} catch (Exception e) {
			throw new MalException(e.getMessage());
		}		
		
		return print;			
	}
	
	public JasperPrint getClientOrderConfirmationBlankReport(){
		JasperPrint print;	
		Map<String, Object> parameters = new HashMap<String, Object>();
		List<Object> vos = new ArrayList<Object>();		
		
		try {
			print = JasperReportGenUtil.FillReport(ReportNameEnum.CLIENT_ORDER_CONFIRMATION_EMAILED, vos, parameters);
		} catch (Exception e) {
			throw new MalException(e.getMessage());
		}	
		
		return print;		
	}
	
	public JasperPrint getPurchaseOrderCoverSheetReport(Long docId) {
		JasperPrint print = null;
		List<PurchaseOrderCoverSheetVO> vos = new ArrayList<PurchaseOrderCoverSheetVO>(); 
		PurchaseOrderCoverSheetVO vo = purchaseOrderReportService.getPurchaseOrderCoverSheetReportVO(docId);
		if(!MALUtilities.isEmpty(vo)){
			vos.add(vo);
		}
		Map<String, Object> parameters;
		
		try {
			parameters = new HashMap<String, Object>();
			print = JasperReportGenUtil.FillReport(ReportNameEnum.PRINT_COVER_SHEET, vos, parameters);			
		} catch (Exception e) {
			throw new MalException(e.getMessage());
		}		
		
		return print;
	}	
	
	public JasperPrint getOpenEndContractRevisionDocument(Long currentQmdId, Long revisionQmdId) {
		JasperPrint print = null;
		List<OeConRevTermsVO> vos = oEContractRevisionTermsService.getOeConRevTermsReportVO(currentQmdId, revisionQmdId); 
		Map<String, Object> parameters;
		
		try {
			parameters = new HashMap<String, Object>();
			print = JasperReportGenUtil.FillReport(ReportNameEnum.OPEN_END_REVISION_DOCUMENT, vos, parameters);			
		} catch (Exception e) {
			throw new MalException(e.getMessage());
		}		
		
		return print;
		
	}
	
	public JasperPrint getOpenEndRevisionScheduleA(Long currentQmdId, Long revisionQmdId){
		JasperPrint print = null;
		List<OeConRevScheduleAVO> vos = oeContractRevisionSchedAService.getOeConRevScheduleAReportVO(currentQmdId, revisionQmdId); 
		Map<String, Object> parameters;
		
		try {
			parameters = new HashMap<String, Object>();
			print = JasperReportGenUtil.FillReport(ReportNameEnum.OPEN_END_REVISION_SCHEDULE_A, vos, parameters);			
		} catch (Exception e) {
			throw new MalException(e.getMessage());
		}		
		
		return print;
	}

	public JasperPrint getOpenEndRevisionAmortizationSchedule(Long currentQmdId, Long revisionQmdId){
		JasperPrint print = null;
		List<OeConRevAmortizationScheduleVO> vos = oeContractRevisionAmortizationService.getOeConRevAmortizationReportVO(currentQmdId, revisionQmdId); 
		Map<String, Object> parameters;
		
		try {
			parameters = new HashMap<String, Object>();
			print = JasperReportGenUtil.FillReport(ReportNameEnum.OPEN_END_REVISION_AMORTIZATION, vos, parameters);			
		} catch (Exception e) {
			throw new MalException(e.getMessage());
		}		
		
		return print;
	}

}
