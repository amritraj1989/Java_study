package com.mikealbert.service;

import java.util.List;

import com.mikealbert.data.vo.CourtesyDeliveryInstructionVO;
import com.mikealbert.data.vo.CustomerOrderConfirmationVO;
import com.mikealbert.data.vo.MainPoVO;
import com.mikealbert.data.vo.PurchaseOrderCoverSheetVO;
import com.mikealbert.data.vo.ThirdPartyPoVO;
import com.mikealbert.data.vo.VehicleOrderSummaryVO;
import com.mikealbert.exception.MalException;

public interface PurchaseOrderReportService {
	public List<MainPoVO> getMainPoReportVO(Long docId, String stockYn) throws MalException;
	public List<ThirdPartyPoVO> getThirdPartyPoReportVO(Long docId, String stockYn) throws MalException;
	public List<VehicleOrderSummaryVO> getVehicleOrderSummaryReportVO(Long docId, String stockYn) throws MalException;
	public List<CustomerOrderConfirmationVO> getClientConfirmationOrderReportVO(Long docId) throws MalException;
	public List<CourtesyDeliveryInstructionVO> getCourtesyDeliveryInstructionVO(Long docId) throws MalException;
	public PurchaseOrderCoverSheetVO getPurchaseOrderCoverSheetReportVO(Long docId) throws MalException;
	
	public boolean hasClientConfirmationBeenGenerated(Long docId);	
	
	public void logClientOrderConfirmationGenerated(Long docId);
	public void logClientOrderConfirmationEmailed(Long docId);	
	
}
