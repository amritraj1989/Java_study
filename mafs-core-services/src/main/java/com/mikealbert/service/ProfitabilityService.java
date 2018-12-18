package com.mikealbert.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Transactional;

import com.mikealbert.data.entity.QuotationModel;
import com.mikealbert.data.entity.QuotationProfile;
import com.mikealbert.data.entity.QuotationProfitability;
import com.mikealbert.data.enumeration.CorporateEntity;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.exception.MalException;
import com.mikealbert.data.vo.PeriodFinalNBVVO;
import com.mikealbert.data.vo.QuotationStepStructureVO;


@Transactional
public interface ProfitabilityService {
    public double IRR_GUESS  =  .1;     
    public double IRR_TOLERANCE   =  1E-7;    
    public int IRR_MAX_ITERATION = 20; 
    
    public BigDecimal calculateMonthlyRental(QuotationModel calcQuotationModel,BigDecimal capitalCost, BigDecimal residualAmount ,BigDecimal irr) throws MalBusinessException;
	public BigDecimal calculateMonthlyRentalConsolidated(QuotationModel calcQuotationModel, BigDecimal capitalCost,BigDecimal residualAmount, BigDecimal irr) throws MalBusinessException;    
    
    public BigDecimal calculateIRR(QuotationModel quotationModel,BigDecimal capitalCost,BigDecimal residualAmount ,BigDecimal monthlyLeaseRate) throws MalBusinessException, MalException;
	public BigDecimal calculateIRR(BigDecimal capitalCost, BigDecimal residualAmount, BigDecimal monthlyLeaseRate, Long contractPeriod, BigDecimal disposalCost, BigDecimal adminFee);    
        
    public  List<QuotationStepStructureVO> calculateOEStepLease(BigDecimal depreciationFactor, BigDecimal finalNBV, BigDecimal netInterestRate,
			 BigDecimal adminFactor, List<QuotationStepStructureVO> inputStepStructureVOList) throws MalBusinessException;    
	    
    public BigDecimal calculateIrrFromOEStep(List<QuotationStepStructureVO> quotationStepList, BigDecimal costToCompany, BigDecimal adminFee) throws MalException;
    
    public BigDecimal calculateRevIrrFromOEStep(List<QuotationStepStructureVO> quotationStepList, BigDecimal costToCompany, BigDecimal adminFee,
    										Map<Long, BigDecimal> eventPeriodAmendmentChargeAfterLastRev,  Map<Long, BigDecimal> eventPeriodOneTimeChargeMap) throws MalException;
    
    public BigDecimal getLeaseFactor(BigDecimal payment, BigDecimal depreciationFactor, BigDecimal adminFactor, BigDecimal customerCost) throws MalException;
    
    
    public BigDecimal calcMargin(BigDecimal capCost, BigDecimal totalResidual, int period, BigDecimal interestRate,
	    			BigDecimal monthlyRental, BigDecimal disposalCost, BigDecimal adminFee) throws MalBusinessException;
   
    public double getResidualAmount(QuotationModel quotationModel) throws MalBusinessException;
    
    public BigDecimal getDepreciationFactor(BigDecimal customerCost, BigDecimal finalResidual, BigDecimal bdPeriod);
    
    public QuotationProfitability getQuotationProfitability(QuotationModel quotationModel);
    
    public void saveQuotationProfitability(QuotationProfitability quotationProfitability);
    
    public BigDecimal getFinanceParameter(String financeParamWillowConfig , Long qmdId, Long qprId) throws MalBusinessException;
    public BigDecimal getFinalNBV(BigDecimal customerCost, BigDecimal depreciationFactor, BigDecimal bdPeriod);
   
    public List<PeriodFinalNBVVO> getFinalNBVPeriods(BigDecimal cost, BigDecimal depreciationFactor, int startingPeriod, int endingPeriod);    

    
	public BigDecimal getHurdleInterestRate(CorporateEntity corporateEntity, QuotationProfile quotationProfile, Date effectiveDate, Long contractPeriod);
	
	public BigDecimal getProfitAdjustmentInterestRate(QuotationProfile quotationProfile, Date effectiveDate) throws MalBusinessException;	
	
	public BigDecimal getMinimumIRR(CorporateEntity corporateEntity, QuotationProfile quotationProfile, Date effectiveDate, Long contractPeriod) throws MalBusinessException;
    
    
   
    
}

