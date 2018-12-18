package com.mikealbert.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mikealbert.common.MalLogger;
import com.mikealbert.common.MalLoggerFactory;
import com.mikealbert.data.dao.AcceptanceQueueDAO;
import com.mikealbert.data.dao.DriverDAO;
import com.mikealbert.data.dao.FleetMasterDAO;
import com.mikealbert.data.dao.RejectReasonDAO;
import com.mikealbert.data.entity.AcceptanceQueueV;
import com.mikealbert.data.entity.ContractLine;
import com.mikealbert.data.entity.EarlyTermQuote;
import com.mikealbert.data.entity.FleetMaster;
import com.mikealbert.data.entity.ProcessStageObject;
import com.mikealbert.data.entity.QuotationModel;
import com.mikealbert.data.entity.QuoteModelPropertyValue;
import com.mikealbert.data.entity.RejectReason;
import com.mikealbert.data.enumeration.OrderToDeliveryProcessStageEnum;
import com.mikealbert.data.enumeration.QuoteModelPropertyEnum;
import com.mikealbert.data.vo.ArCreationVO;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.util.MALUtilities;

@Service("acceptanceQueueService")
public class AcceptanceQueueServiceImpl implements AcceptanceQueueService {
	public MalLogger logger = MalLoggerFactory.getLogger(this.getClass());

	@Resource AcceptanceQueueDAO acceptanceQueueDAO;
	@Resource QuotationService quotationService;
	@Resource RejectReasonDAO rejectReasonDAO;
	@Resource ProcessStageService processStageService;
	@Resource ArService aRService;
	@Resource QuoteModelPropertyValueService quoteModelPropertyValueService;
	@Resource FleetMasterDAO fleetMasterDAO;
	@Resource DriverDAO driverDAO;
	@Resource ContractService contractService;
	
	private static final int REJECTED_QUOTE_STATUS_CODE = 8;

	@Override
	public List<AcceptanceQueueV> getAcceptanceQueueList() {
		return acceptanceQueueDAO.getAcceptanceQueueList();
	}
	public void rejectQuoteFromAcceptanceQueue(Long qmdId, String rejectReasonCode) throws MalBusinessException {
		QuotationModel qm = quotationService.getQuotationModel(qmdId);
		staleCheck(qm); 
		qm.setAcceptanceDate(null);
		qm.setDateReceived(null);
		qm.setRequestForAcceptanceBy(null);
		qm.setRequestForAcceptanceDate(null);
		qm.setRequestForAcceptanceType(null);
		qm.setRequestForAcceptanceYn(null);
		qm.setRejectReasonFromQueue(rejectReasonCode);
		quotationService.updateQuotationModel(qm);
	}
	
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public String acceptQuoteFromAcceptanceQueue(AcceptanceQueueV acceptanceQueueV, String loggedInUser) throws Exception {
		QuotationModel qm = quotationService.getQuotationModel(acceptanceQueueV.getQmdId());
		staleCheck(qm);
		String acceptQuoationMessage = quotationService.acceptQuotation(acceptanceQueueV.getQmdId(), loggedInUser);
		
		QuoteModelPropertyValue quoteTypePropertyValue = quoteModelPropertyValueService.findByNameAndQmdId(QuoteModelPropertyEnum.QUOTE_TYPE.getName() , acceptanceQueueV.getQmdId());
		if(quoteTypePropertyValue != null && "R".equals(quoteTypePropertyValue.getPropertyValue())){
			processOneTimeCharges(qm, loggedInUser, acceptanceQueueV);
		}
		
		ProcessStageObject pso = processStageService.getStagedObject(OrderToDeliveryProcessStageEnum.ACCEPTANCE, acceptanceQueueV.getQmdId());
		if(pso != null) {
			processStageService.excludeStagedObject(pso);			
		}
		
		return acceptQuoationMessage;
	}
	private void staleCheck(QuotationModel qm) throws MalBusinessException {
		if (MALUtilities.isEmpty(qm.getRequestForAcceptanceYn()) || qm.getRequestForAcceptanceYn().equalsIgnoreCase("N")) {
			throw new MalBusinessException("plain.message", new String[] {"This quote is invalid. Please refresh the page."});
		}
	}
	@Override
	public List<RejectReason> getRejectReasons() {
		return rejectReasonDAO.findAll();
	}		

	private void processOneTimeCharges(QuotationModel quotationModel, String loggedInUser, AcceptanceQueueV acceptanceQueueV) throws Exception {
				
		boolean revAssessmentCharge = false;
		boolean revIntCharge = false;
		BigDecimal revAssessmentAmount = BigDecimal.ZERO;
		BigDecimal revIntAmount = BigDecimal.ZERO;
		
		List<QuoteModelPropertyValue> list = quoteModelPropertyValueService.findAllByQmdId(quotationModel.getQmdId());
		for(QuoteModelPropertyValue qmpv : list) {
			if(qmpv.getQuoteModelProperty().getName().equalsIgnoreCase("OE_REV_ASSMT_INRATE_YN")) {
				revAssessmentCharge = qmpv.getPropertyValue().equalsIgnoreCase("Y") ? false : true;
				continue;
			}			
			if(qmpv.getQuoteModelProperty().getName().equalsIgnoreCase("OE_REV_INT_ADJ_INRATE_YN")) {
				revIntCharge = qmpv.getPropertyValue().equalsIgnoreCase("Y") ? false : true;
				continue;
			}
			if(qmpv.getQuoteModelProperty().getName().equalsIgnoreCase("OE_REV_ASSMT")) {
				revAssessmentAmount = new BigDecimal(qmpv.getPropertyValue());
				continue;
			}
			if(qmpv.getQuoteModelProperty().getName().equalsIgnoreCase("OE_REV_INT_ADJ")) {
				revIntAmount = new BigDecimal(qmpv.getPropertyValue());
				continue;
			}		
		}
		
		ArCreationVO aRVO = null;
		
		if(revAssessmentCharge && (revAssessmentAmount.compareTo(BigDecimal.ZERO) > 0)) {
			aRVO = getPopulatedArVO(quotationModel, acceptanceQueueV);
			aRVO.setUserName(loggedInUser);
			aRVO.setAmount(revAssessmentAmount);
			aRVO.setLineDescription("Unit No - " + acceptanceQueueV.getUnitNo()  + " Revision Assessment");
			aRService.createInvoiceARForType(ArService.OE_REVISION_ASSESSMENT_PARAMETER_KEY, aRVO);
		}
		if(revIntCharge && (revIntAmount.compareTo(BigDecimal.ZERO) > 0)) {
			if(aRVO == null) {
				aRVO = getPopulatedArVO(quotationModel, acceptanceQueueV);				
				aRVO.setUserName(loggedInUser);
			}
			aRVO.setAmount(revIntAmount);
			aRVO.setLineDescription("Unit No - " + acceptanceQueueV.getUnitNo()  + " Revision Interest Adjustment");
			aRService.createInvoiceARForType(ArService.OE_REVISION_INT_ADJ_PARAMETER_KEY, aRVO);
		}

		if(quotationModel.getCapitalContribution().compareTo(BigDecimal.ZERO) > 0) {
			FleetMaster fleetMaster = fleetMasterDAO.findByUnitNo(acceptanceQueueV.getUnitNo());
			ContractLine contractLine = contractService.getCurrentOrFutureContractLine(fleetMaster);
			aRService.rechargeCapitalContribution(Long.parseLong(acceptanceQueueV.getClientAccountCid()), contractLine.getClnId(), quotationModel.getQmdId(), new Date(), loggedInUser);
		}
		
	}

	private ArCreationVO getPopulatedArVO(QuotationModel quotationModel, AcceptanceQueueV acceptanceQueueV) {

		ArCreationVO aRVO = new ArCreationVO();
		
		aRVO.setcId(1l);
		aRVO.setDocDate(new Date());
		aRVO.setDriver(driverDAO.findById(acceptanceQueueV.getDriverId()).orElse(null));		
		aRVO.setExternalAccount(quotationModel.getQuotation().getExternalAccount());
		aRVO.setFleetMaster(fleetMasterDAO.findByUnitNo(acceptanceQueueV.getUnitNo()));
		
		return aRVO;
				
	}

	public void rejectOutstandingOeQuoteFromAcceptanceQueue(Long qmdId) throws MalBusinessException {
		QuotationModel qm = quotationService.getQuotationModel(qmdId);
		qm.setAcceptanceDate(null);
		qm.setDateReceived(null);
		qm.setRequestForAcceptanceBy(null);
		qm.setRequestForAcceptanceDate(null);
		qm.setRequestForAcceptanceType(null);
		qm.setRequestForAcceptanceYn(null);
		qm.setQuoteStatus(REJECTED_QUOTE_STATUS_CODE);
		qm.setRejectReasonFromQueue("EXPIRED");
		quotationService.updateQuotationModel(qm);
	}
	
	public void rejectOutstandingOeEtQuoteFromAcceptanceQueue(Long etqId) throws MalBusinessException {
		
		EarlyTermQuote etQuote = quotationService.getEarlyTermQuote(etqId);
		etQuote.setRejectFlag("Y");
		quotationService.updateEarlyTermQuote(etQuote); 
	}

}
