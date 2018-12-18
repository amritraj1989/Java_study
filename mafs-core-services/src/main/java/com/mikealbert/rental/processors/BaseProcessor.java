package com.mikealbert.rental.processors;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.List;

import javax.annotation.Resource;

import com.mikealbert.common.CommonCalculations;
import com.mikealbert.common.MalLogger;
import com.mikealbert.common.MalLoggerFactory;
import com.mikealbert.data.dao.CurrencyUnitDAO;
import com.mikealbert.data.dao.DocDAO;
import com.mikealbert.data.dao.QuotationElementDAO;
import com.mikealbert.data.dao.WillowConfigDAO;
import com.mikealbert.data.dao.WillowEntityConfigDAO;
import com.mikealbert.data.dao.WillowEntityDefaultDAO;
import com.mikealbert.data.entity.QuotationElement;
import com.mikealbert.data.entity.QuotationModel;
import com.mikealbert.data.entity.WillowConfig;
import com.mikealbert.data.entity.WillowEntityConfig;
import com.mikealbert.data.entity.WillowEntityConfigPK;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.service.QuotationService;

/** This class is parent class which implements by all other rental calc service processors, also some common methods (as pv, pmt etc.) are being created to use by other methods.  
 **/
public abstract class BaseProcessor implements LeaseElementProcessor{
    
	private MalLogger logger = MalLoggerFactory.getLogger(this.getClass());
	
	@Resource QuotationService quotationService ;
	@Resource private QuotationElementDAO quotationElementDAO;
	@Resource private WillowConfigDAO willowConfigDAO;
	@Resource private WillowEntityConfigDAO  willowEntityConfigDAO;
	@Resource private WillowEntityDefaultDAO  willowEntityDefaultDAO;
	@Resource private CurrencyUnitDAO   currencyUnitDAO;
	@Resource private DocDAO  docDAO;	
	
	protected static final MathContext MC = CommonCalculations.MC;  
	protected static final String INRATE_TAX_INT_ADJ_FINANCE_PARAMETER_NAME  = "INRATE_TAX_INT_ADJ";
	
	protected boolean containsElement(Long qmdId, Long leaseElementId) {
		List<QuotationElement> list = quotationElementDAO.findByQmdIdAndLeaseEleId(qmdId, leaseElementId);
		if (list != null && list.size() > 1) {
			return true;
		}
		return false;
	}
	
	protected   BigDecimal pmt(long contractPeriod, BigDecimal pAmount, BigDecimal fAmount, BigDecimal interestRate,
			Integer type) throws MalBusinessException {
		try {
		BigDecimal returnAmt = new BigDecimal(0);
			BigDecimal temp = new BigDecimal(0);
			BigDecimal monthlyRate = new BigDecimal(0);
			interestRate = interestRate != null ? interestRate : new BigDecimal(0);
			if (interestRate.compareTo(new BigDecimal(0)) != 0) {
				monthlyRate = interestRate.divide(new BigDecimal(1200),MC);
				Double tempDouble = Math.pow((monthlyRate.doubleValue() + 1), contractPeriod);
				if (tempDouble != null) {
					temp = new BigDecimal(tempDouble);
				}
				BigDecimal amt1 = fAmount.add(pAmount.multiply(temp));
				
				
				BigDecimal amt2 = monthlyRate.divide(temp.subtract(new BigDecimal(1)),MC);
				
				BigDecimal amt3 = monthlyRate.multiply(new BigDecimal(type));
				amt3 = new BigDecimal(1).add(amt3);
				amt3= new BigDecimal(1).divide(amt3,MC);
				
				returnAmt =new BigDecimal(-1).multiply( amt1.multiply(amt2).multiply(amt3));
			} else {
				BigDecimal amt1 = new BigDecimal(-1).multiply(pAmount).subtract(fAmount);
				returnAmt = amt1.divide(new BigDecimal(contractPeriod),MC);
			}
			return returnAmt;
		} catch (Exception ex) {
			logger.error(ex);
			throw new MalBusinessException("service.validation", new String[] { "Error in pmt calculation." });
		}
	}

	//Added for OE to get PMT amount for in_rate_treatment_type lease element but may be used for CE as well in future
	protected   BigDecimal getInRatePMTAmountForQuoteRevision(QuotationElement quotationElement, BigDecimal amount , long contractPeriod , BigDecimal interestRate) throws MalBusinessException{
	
		QuotationModel quotationModel = quotationElement.getQuotationModel();
		Long profileId = quotationModel.getQuotation().getQuotationProfile().getQprId();
		Double interestAdjust =  quotationService.getFinanceParam(INRATE_TAX_INT_ADJ_FINANCE_PARAMETER_NAME, quotationModel.getQmdId(), profileId, null, true, null);
		interestAdjust = interestAdjust != null ? interestAdjust : 0;
		interestRate = interestRate.add(new BigDecimal(interestAdjust));
		BigDecimal calculatedPmtAmt = pmt(contractPeriod, amount.negate(), BigDecimal.ZERO, interestRate, 0);
		calculatedPmtAmt = calculatedPmtAmt != null ? calculatedPmtAmt: BigDecimal.ZERO;
		
		
		return calculatedPmtAmt;
	}
	
	//Added for OE to get PMT amount for in_rate_treatment_type lease element but may be used for CE as well in future
	protected   BigDecimal getInRatePMTAmountForQuoteRevision(QuotationElement quotationElement, BigDecimal amount , long contractPeriod ) throws MalBusinessException{
	
		QuotationModel quotationModel = quotationElement.getQuotationModel();
		String reviewFrequency = quotationElement.getQuotationModel().getQuotation().getQuotationProfile().getReviewFrequency();
		String fixedOrFloat = quotationElement.getQuotationModel().getQuotation().getQuotationProfile().getVariableRate().equals("F") ? "FIXED" : "FLOAT" ;
		String  interestType =  quotationService.getInterestType(quotationModel.getQmdId(),quotationElement.getLeaseElement().getLelId(),quotationModel.getRevisionDate());
		BigDecimal interestRate =  quotationService.getInterestRateByType(quotationModel.getRevisionDate(), contractPeriod, interestType, reviewFrequency, fixedOrFloat);
		
		return getInRatePMTAmountForQuoteRevision(quotationElement, amount, contractPeriod, interestRate);
	}

	
	
	protected String getWillowConfig(Long cId, String configName) throws MalBusinessException{
		WillowEntityConfigPK willowEntityConfigPKId = new WillowEntityConfigPK();
		willowEntityConfigPKId.setCId(cId);
		willowEntityConfigPKId.setConfigName(configName);
		WillowEntityConfig willowEntityConfig = willowEntityConfigDAO.findById(willowEntityConfigPKId).orElse(null);
		if(willowEntityConfig == null){
			WillowConfig willowConfig =  willowConfigDAO.findById(configName).orElse(null);
			if(willowConfig == null){
				throw new MalBusinessException("close_end_config_lease_admin_not_setup");
			}
			return  willowConfig.getConfigValue();
		}else{
			return  willowEntityConfig.getConfigValue();
		}
	}

}
