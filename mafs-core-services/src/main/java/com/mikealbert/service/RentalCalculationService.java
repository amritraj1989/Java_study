package com.mikealbert.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Transactional;

import com.mikealbert.data.entity.LeaseElement;
import com.mikealbert.data.entity.Quotation;
import com.mikealbert.data.entity.QuotationElement;
import com.mikealbert.data.entity.QuotationElementStep;
import com.mikealbert.data.entity.QuotationModel;
import com.mikealbert.data.entity.QuotationModelFinances;
import com.mikealbert.data.entity.QuotationProfitability;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.exception.MalException;
import com.mikealbert.data.vo.QuotationStepStructureVO;

@Transactional
public interface RentalCalculationService {
		
    public QuotationModel getCalculatedQuotationModel(Long qmdId, boolean isReProcess, BigDecimal updatedCapitalContribution) throws MalBusinessException;
    
    public boolean isCalcRentalCalculationApplicable(QuotationModel quotationModel);
    
    public boolean isCalcRentalCalculationApplicable(Quotation quotation);
    
    public boolean isQuoteForPartialCalculation(QuotationModel quotationModel);

    public BigDecimal getCapitalCostAmount(QuotationModel quotationModel) throws MalBusinessException;

    public BigDecimal getFinanceLeaseElementCostForCE(QuotationModel quotationModel);
    
    public  void saveCalculatedQuote(QuotationModel dbQuotationModel ,QuotationModel calcQuotationModel, BigDecimal baseResidual, 
	    			 QuotationProfitability quotationProfitability, boolean isReProcess) throws MalBusinessException , Exception;

    public  BigDecimal getEquipmentResidual(QuotationModel quotationModel) throws MalBusinessException;
    
    public  QuotationElement getMainQuotationModelElement(QuotationModel quotationModel);

    public boolean isQuoteEditable(QuotationModel quotationModel);
    
    public boolean isVQPrinted(Long quotationModelId);
    public Map<Long, BigDecimal> getEventPeriodAndAmendedAccessCostAfterLastRev(Long qmdId , Long fmsId);   
    public void saveCalculatedQuoteOE(QuotationModel dbQuotationModel, QuotationModel calcQuotationModel,
										List<QuotationStepStructureVO> quotationStepResponseList,QuotationProfitability quotationProfitability, Map<String, Object> finParamMap) throws MalBusinessException , Exception;

	public void saveCalculatedRevisedQuoteOE(QuotationModel quotationModel,List<QuotationStepStructureVO> quotationStepResponseList,
												QuotationProfitability quotationProfitability, Map<String, Object> finParamMap)throws MalBusinessException, Exception ;
    
    public void saveFinanceParamOnQuote(BigDecimal finParamNValue, String finParamKey, QuotationModel quotationModel) throws MalBusinessException;

    public boolean isUsedStock(QuotationModel quotationModel);
    public boolean isFormalExtension(QuotationModel quotationModel);
    
    public boolean isReportsHidden(QuotationModelFinances quotationModelFinances);
    public void saveHideInvoiceAdjustment(QuotationModelFinances qmf, boolean hide) throws MalBusinessException;
    public void updateFinanceParameter(QuotationModelFinances qmf) throws MalBusinessException;
    
	public BigDecimal getEquipmentResidualOfNewOEQuote(Long qmdId) throws MalBusinessException;
	public boolean	saveCapitalCostChanges(QuotationModel quotationModel) throws Exception;
	public void calculateAndSaveQuote(QuotationModel dbQuotationModel,QuotationModel calcQuotationModel) throws MalBusinessException ;	
	
	public QuotationModel setupDataForNewOERevQuote(QuotationModel quotationModel, BigDecimal effectiveContractChangeEventPeriod, BigDecimal depreciationFactor, BigDecimal depreciatedCost)  throws MalException ;
	public void populateQuotationElementStep(List<QuotationElement> newQuotationElementList , List<QuotationStepStructureVO> stepList, List<QuotationElementStep> newAllElementStepList);	
	public List<QuotationStepStructureVO> getQuotationStepStructureVOs(QuotationModel quotationModel);
	public List<QuotationStepStructureVO> updateQuotationStepStructureWithElementsCost(QuotationModel quotationModel , List<QuotationStepStructureVO>  quoteSteps) ;
	public BigDecimal getHurdleRate(QuotationModel quotationModel) throws MalException;
	public BigDecimal getHurdleRateByTerm(QuotationModel quotationModel, Long term) throws MalException;
	public BigDecimal getProfitAdj(QuotationModel quotationModel) throws MalException;
	

    public  String VMP_PROCESSOR_NAME =  "vmpProcessor";
    public  String OPEN_END_ADMIN_FACTOR	= "OPEN_END_ADMIN_FACTOR";
    public  String	NOVALUEFOUND	= "NOVALUEFOUND";
    public String ADMIN_FACTOR_DIVISOR = "1000";
    public static final int IRR_CALC_DECIMALS = 5;
    public static final int CURRENCY_DECIMALS = 2;
    public static final int RENTAL_CALC_DECIMALS = 5; 
    public static final int DEPRECIATION_FACTOR_SCALE = 7;
    public static final String VMP_FORMULAE = "mal_rental_calcs.vmp_calc";//HD-475
	
	public List<LeaseElement> getLeaseElementByTypeAndProfile(String elementType, Long qprId);
	public QuotationModel validateQuotationModelForRental(QuotationModel qmd); //HD-475
}
