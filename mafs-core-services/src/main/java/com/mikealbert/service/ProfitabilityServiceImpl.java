package com.mikealbert.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mikealbert.common.CommonCalculations;
import com.mikealbert.common.MalConstants;
import com.mikealbert.common.MalLogger;
import com.mikealbert.common.MalLoggerFactory;
import com.mikealbert.data.dao.ExternalAccountDAO;
import com.mikealbert.data.dao.QuotationProfitabilityDAO;
import com.mikealbert.data.dao.WillowConfigDAO;
import com.mikealbert.data.entity.QuotationElement;
import com.mikealbert.data.entity.QuotationModel;
import com.mikealbert.data.entity.QuotationProfile;
import com.mikealbert.data.entity.QuotationProfitability;
import com.mikealbert.data.entity.WillowConfig;
import com.mikealbert.data.enumeration.CorporateEntity;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.exception.MalException;
import com.mikealbert.data.vo.PeriodFinalNBVVO;
import com.mikealbert.data.vo.QuotationStepStructureVO;
import com.mikealbert.data.vo.QuoteElementStepVO;
import com.mikealbert.util.MALUtilities;

@Transactional
@Service("profitabilityService")
public class ProfitabilityServiceImpl implements ProfitabilityService {
	
	private MalLogger logger = MalLoggerFactory.getLogger(this.getClass());
	
	@Resource WillowConfigDAO willowConfigDAO;
	@Resource QuotationService quotationService;
	@Resource QuotationProfitabilityDAO quotationProfitabilityDAO;
	@Resource ExternalAccountDAO externalAccountDAO;

	/**
	 * Calculates monthly rental of quotation and sets each financed quotation elements rental in the process.
	 * The admin fee, disposal cost, and passed in residualAmount will be applied to the main element only.
	 * 
	 * 
	 * For Minimum lease rate , we can  calcQuotationModel or quotationModel Object but 
	 * For actual lease rate we can only pass calcQuotationModel.
	 *  
	 * @throws MalBusinessException
	 */
	public BigDecimal calculateMonthlyRental(QuotationModel calcQuotationModel, BigDecimal capitalCost,BigDecimal residualAmount, BigDecimal irr) throws MalBusinessException {

		BigDecimal totalMonthlyRental = new BigDecimal(0);
		BigDecimal consolidatedMonthlyRental = new BigDecimal(0);
		BigDecimal monthlyRental = new BigDecimal(0);
		
		long qmdId = calcQuotationModel.getQmdId();
		long qprId = calcQuotationModel.getQuotation().getQuotationProfile().getQprId();
		BigDecimal disposalCost = getFinanceParameter(getWillowConfigValue(MalConstants.CLOSED_END_DISP_COST), qmdId, qprId);
		BigDecimal adminFees = getFinanceParameter(getWillowConfigValue(MalConstants.CLOSED_END_LEASE_ADMIN), qmdId, qprId);
		BigDecimal totalCapCostSum = new BigDecimal(0);		
	
		disposalCost = disposalCost == null ? BigDecimal.ZERO : disposalCost;
		adminFees = adminFees == null ? BigDecimal.ZERO : adminFees;
		
		int contractPeriod = calcQuotationModel.getContractPeriod().intValue();
		
		if (calcQuotationModel.isCalcRentalApplicable()) {
			
			// OER-71 Calculating and setting the rental on each of the financed elements.
			//  Then keeping a running total of the elements calculated monthly rental.
			//  Returning the total monthly rental of the quote, i.e. the sum of the financed 
			//  elements' rental is the total rental for the quote/unit
			// Note: This code was ported from RentalCalculationService.distributeRentalAmount()
			for (QuotationElement quotationElement : calcQuotationModel.getQuotationElements()) {
				if (quotationElement.getLeaseElement().getElementType().equals(MalConstants.FINANCE_ELEMENT)) {
					totalCapCostSum = totalCapCostSum.add(quotationElement.getCapitalCost(), CommonCalculations.MC);
				}
			}
			boolean matchToConsolidatedRental  = false;
			for(QuotationElement qel : calcQuotationModel.getQuotationElements()) {
				if (qel.getLeaseElement().getElementType().equals(MalConstants.FINANCE_ELEMENT) && MALUtilities.convertYNToBoolean(qel.getIncludeYn())) {
					if ((!MALUtilities.isEmpty(calcQuotationModel.getCalculatedMontlyRental()) && calcQuotationModel.getCalculatedMontlyRental().compareTo(BigDecimal.ZERO) == 0) || totalCapCostSum.compareTo(BigDecimal.ZERO) == 0) {
						qel.setRental(BigDecimal.ZERO);
						qel.setElementCost(qel.getRental());						
					} else {
						if(MALUtilities.isEmpty(qel.getQuotationModelAccessory()) && MALUtilities.isEmpty(qel.getQuotationDealerAccessory())) { //Main element
							monthlyRental = BigDecimal.valueOf( CommonCalculations.calculateRentalfromIRR(irr.doubleValue(), contractPeriod,
									qel.getCapitalCost().doubleValue(), adminFees.doubleValue(), residualAmount.doubleValue(), disposalCost.doubleValue()) );
							monthlyRental = CommonCalculations.getRoundedValue(monthlyRental , RentalCalculationService.RENTAL_CALC_DECIMALS);
						} else {
							monthlyRental = BigDecimal.valueOf( CommonCalculations.calculateRentalfromIRR(irr.doubleValue(), contractPeriod,
									qel.getCapitalCost().doubleValue(), BigDecimal.ZERO.doubleValue(), qel.getResidualValue().doubleValue(), BigDecimal.ZERO.doubleValue()) );	
							monthlyRental = CommonCalculations.getRoundedValue(monthlyRental , RentalCalculationService.CURRENCY_DECIMALS);
						}
					
						qel.setRental(monthlyRental.multiply(BigDecimal.valueOf(calcQuotationModel.getContractPeriod()), CommonCalculations.MC));
						qel.setElementCost(qel.getRental());
						
						totalMonthlyRental = totalMonthlyRental.add(monthlyRental);		
						matchToConsolidatedRental = true;
					}									
				}
			}	
			
			if(matchToConsolidatedRental == true){				
				
				consolidatedMonthlyRental = BigDecimal.valueOf( CommonCalculations.calculateRentalfromIRR(irr.doubleValue(), contractPeriod,
														capitalCost.doubleValue(), adminFees.doubleValue(), residualAmount.doubleValue(), disposalCost.doubleValue()) );
				BigDecimal differenceInRental = consolidatedMonthlyRental.subtract(totalMonthlyRental);
				
				for(QuotationElement qel : calcQuotationModel.getQuotationElements()) {
					if (qel.getLeaseElement().getElementType().equals(MalConstants.FINANCE_ELEMENT) && MALUtilities.convertYNToBoolean(qel.getIncludeYn())
							&&  MALUtilities.isEmpty(qel.getQuotationModelAccessory()) && MALUtilities.isEmpty(qel.getQuotationDealerAccessory())){ //Main element
						
						BigDecimal updatedRental  = qel.getRental().add(differenceInRental.multiply(BigDecimal.valueOf(calcQuotationModel.getContractPeriod()), CommonCalculations.MC));	
						qel.setRental(CommonCalculations.getRoundedValue(updatedRental , RentalCalculationService.RENTAL_CALC_DECIMALS));
						qel.setElementCost(qel.getRental());;
					}
				}		
			}
			
			totalMonthlyRental = consolidatedMonthlyRental;
			
		} else {
			totalMonthlyRental = getFinaceParamBasedRental(calcQuotationModel);
		}

		
		return totalMonthlyRental;	

	}
	
	/**
	 * Calculates monthly rental of quotation based on supplied capital cost,
	 * residual amount and irr It uses disposal cost and admin fees of quotation
	 * model. Returns calclated monthly rental
	 * 
	 * 
	 * For Minimum lease rate , we can  calcQuotationModel or quotationModel Object but 
	 * For actual lease rate we can only pass calcQuotationModel.
	 *  
	 * @throws MalBusinessException
	 */
	public BigDecimal calculateMonthlyRentalConsolidated(QuotationModel calcQuotationModel, BigDecimal capitalCost,BigDecimal residualAmount, BigDecimal irr) throws MalBusinessException {

		BigDecimal monthlyRental = new BigDecimal(0);
		long qmdId = calcQuotationModel.getQmdId();
		long qprId = calcQuotationModel.getQuotation().getQuotationProfile().getQprId();
		BigDecimal disposalCost = getFinanceParameter(getWillowConfigValue(MalConstants.CLOSED_END_DISP_COST), qmdId, qprId);
		BigDecimal adminFees = getFinanceParameter(getWillowConfigValue(MalConstants.CLOSED_END_LEASE_ADMIN), qmdId, qprId);
	
		disposalCost = disposalCost == null ? BigDecimal.ZERO : disposalCost;
		adminFees = adminFees == null ? BigDecimal.ZERO : adminFees;
		
		int contractPeriod = calcQuotationModel.getContractPeriod().intValue();

		
		if (calcQuotationModel.isCalcRentalApplicable()) {
			double dMonthlyRental = CommonCalculations.calculateRentalfromIRR(irr.doubleValue(), contractPeriod,
					capitalCost.doubleValue(), adminFees.doubleValue(), residualAmount.doubleValue(), disposalCost.doubleValue());
			monthlyRental = BigDecimal.valueOf(dMonthlyRental);

		} else {
			monthlyRental = getFinaceParamBasedRental(calcQuotationModel);
		}

		
		return monthlyRental;

	}	
    
	/**
	 * Returns rental based on finance parameter which indicates that rental
	 * calculation is applicable
	 * 
	 * @param calcQuotationModel
	 * @return monthly rental
	 * @throws MalBusinessException
	 */
	private BigDecimal getFinaceParamBasedRental(QuotationModel calcQuotationModel) throws MalBusinessException {
		BigDecimal monthlyRental = new BigDecimal(0);
		for (QuotationElement quotationElement : calcQuotationModel.getQuotationElements()) {
			if (!quotationElement.isCalcRentalApplicable())
				monthlyRental = monthlyRental.add(quotationElement.getRental());
		}
		return monthlyRental;
	}

	/**
	 * Calculates IRR based on capital cost, residual and monthly lease rate.
	 * 
	 * @param quotationModel
	 * @param capitalCost
	 * @param monthlyLeaseRate
	 * @return calculated irr
	 * @throws MalBusinessException
	 */
	public BigDecimal calculateIRR(QuotationModel quotationModel, BigDecimal capitalCost, BigDecimal residualAmount,BigDecimal monthlyLeaseRate) throws MalException , MalBusinessException{
	    
		long qmdId = quotationModel.getQmdId();
		long qprId = quotationModel.getQuotation().getQuotationProfile().getQprId();
		Long contractPeriod = quotationModel.getContractPeriod();
		BigDecimal disposalCost = getFinanceParameter(getWillowConfigValue(MalConstants.CLOSED_END_DISP_COST), qmdId, qprId);
		BigDecimal adminFees = getFinanceParameter(getWillowConfigValue(MalConstants.CLOSED_END_LEASE_ADMIN), qmdId, qprId);
		disposalCost = disposalCost == null ? BigDecimal.ZERO : disposalCost;
		adminFees = adminFees == null ? BigDecimal.ZERO : adminFees;
	
        //TODO Replace this implementation with a call to the private member calculateIRRCore(...)		
		double finalResidual = residualAmount.doubleValue() - disposalCost.doubleValue();
		Double[] monthlyCashFlowArray = new Double[contractPeriod.intValue() + 1];
		for (int i = 0; i < monthlyCashFlowArray.length; i++) {
			if (i == 0) {
				monthlyCashFlowArray[i] = -capitalCost.doubleValue();
			} else if (i == monthlyCashFlowArray.length - 1) {
				monthlyCashFlowArray[i] = monthlyLeaseRate.doubleValue() + finalResidual - adminFees.doubleValue();
			} else {
				monthlyCashFlowArray[i] = monthlyLeaseRate.doubleValue() - adminFees.doubleValue();
			}
		}

		double calculatedIRR = CommonCalculations.getIrrFromMonthlyLeaseRate(monthlyCashFlowArray, IRR_GUESS, IRR_TOLERANCE,
				IRR_MAX_ITERATION);
		if (Double.compare(Double.NaN, calculatedIRR) == 0)
			throw new MalException("calculation.formula.Error");
		
		return new BigDecimal(String.valueOf(calculatedIRR)).multiply(new BigDecimal(100));
	}

	public BigDecimal calculateIRR(BigDecimal capitalCost, BigDecimal residualAmount, BigDecimal monthlyLeaseRate, Long contractPeriod, BigDecimal disposalCost, BigDecimal adminFee) {
		return calculateIRRCore(capitalCost, residualAmount, monthlyLeaseRate, contractPeriod, disposalCost, adminFee);
	}
	
	/**
	 * Core calculation of IRR.
	 * 
	 * Note Implementation was extracted from the initial calculateIRR method
	 * 
	 * @param capitalCost 		Mike Albert's capital cost, a.k.a deal cost
	 * @param residualAmount 	Residual amount of the vehicle
	 * @param monthlyLeaseRate	Monthly lease payment
	 * @param contractPeriod	Term
	 * @param disposalCost		Disposal cost from client's finance parameter structure
	 * @param adminFee			Admin fee, SG&A, from client's finance parameter structure
	 * @return The calculated IRR based on the passed in parameters
	 */
	private BigDecimal calculateIRRCore(BigDecimal capitalCost, BigDecimal residualAmount, BigDecimal monthlyLeaseRate, Long contractPeriod, BigDecimal disposalCost, BigDecimal adminFee) {
		BigDecimal irr;
		disposalCost = disposalCost == null ? BigDecimal.ZERO : disposalCost;
		adminFee = adminFee == null ? BigDecimal.ZERO : adminFee;
		double finalResidual = residualAmount.doubleValue() - disposalCost.doubleValue();
		Double[] monthlyCashFlowArray = new Double[contractPeriod.intValue() + 1];
		for (int i = 0; i < monthlyCashFlowArray.length; i++) {
			if (i == 0) {
				monthlyCashFlowArray[i] = -capitalCost.doubleValue();
			} else if (i == monthlyCashFlowArray.length - 1) {
				monthlyCashFlowArray[i] = monthlyLeaseRate.doubleValue() + finalResidual - adminFee.doubleValue();
			} else {
				monthlyCashFlowArray[i] = monthlyLeaseRate.doubleValue() - adminFee.doubleValue();
			}
		}
		
		double calculatedIRR = CommonCalculations.getIrrFromMonthlyLeaseRate(monthlyCashFlowArray, IRR_GUESS, IRR_TOLERANCE,
				IRR_MAX_ITERATION);
		if (Double.compare(Double.NaN, calculatedIRR) == 0)
			throw new MalException("calculation.formula.Error");
		
		irr = new BigDecimal(String.valueOf(calculatedIRR)).multiply(new BigDecimal(100));
		
		return irr;
	}

	public QuotationProfitability getQuotationProfitability(QuotationModel quotationModel) {
	   
    		QuotationProfitability quotationProfitability = null;
        	List<QuotationProfitability> listQuotationProfitability = quotationProfitabilityDAO.findByQmdIdAndProfitType(quotationModel.getQmdId(), "P");
        	if (listQuotationProfitability != null) {
        	    if (listQuotationProfitability.size() > 1) {
        		for (QuotationProfitability tempProfitability : listQuotationProfitability) {
        		    if (tempProfitability.getProfitSource().equals("M")) {
        			quotationProfitability = tempProfitability;
        		    }
        		}
        	    } else if (listQuotationProfitability.size() == 1) {
        		quotationProfitability = listQuotationProfitability.get(0);
        	    }
        	}
	    return quotationProfitability;

	}

	@Transactional(propagation= Propagation.REQUIRED, rollbackFor = Exception.class)
	public void saveQuotationProfitability(QuotationProfitability quotationProfitability){
		
		quotationProfitabilityDAO.save(quotationProfitability);
		
	}

	/**
	 * Returns value of configured finance parameter for quotation profile
	 * 
	 * @param qmdId
	 * @param qprId
	 * @return value of finance parameter
	 * @throws MalBusinessException
	 */
	public BigDecimal getFinanceParameter(String financeParam, Long qmdId, Long qprId) throws MalBusinessException {
		
		Double financeParameterValue = null;
		if (financeParam != null) {
			financeParameterValue = quotationService.getFinanceParam(financeParam, qmdId, qprId, new Date());
		}
		if(financeParameterValue == null ){
			return null;
		}else{
			return new BigDecimal(String.valueOf(financeParameterValue));
		}
	}

	/**
	 * Returns value of configuration from willow config
	 * 
	 * @param configName
	 * @return value of willow config
	 * @throws MalBusinessException
	 */
	public String getWillowConfigValue(String configName) throws MalBusinessException {
		String configValue = null;

		if (configName != null) {
			WillowConfig willowConfig = willowConfigDAO.findById(configName).orElse(null);
			configValue = willowConfig.getConfigValue();
		}

		return configValue;
	}

	/**
	 * Returns total residual of finance type quotation elements of quotation
	 * model
	 * 
	 * @param quotation
	 *            model
	 * @throws MalBusinessException
	 */
	public double getResidualAmount(QuotationModel quotationModel) throws MalBusinessException {
		BigDecimal residualAmount = BigDecimal.ZERO;
		for (QuotationElement quotationElement : quotationModel.getQuotationElements()) {
			if (!quotationElement.getLeaseElement().getElementType().equals(MalConstants.FINANCE_ELEMENT))
				continue;

			if (MalConstants.FLAG_Y.equals(quotationElement.getIncludeYn()) && quotationElement.getResidualValue() != null) {
				residualAmount = residualAmount.add(quotationElement.getResidualValue());
			}
		}
		return residualAmount == null ? 00.00D : residualAmount.doubleValue();
	}
	
	public BigDecimal getDepreciationFactor(BigDecimal customerCost, BigDecimal finalResidual, BigDecimal bdPeriod) {
	    if(customerCost.compareTo(BigDecimal.ZERO) == 0){
		return BigDecimal.ZERO;
	    }
	    return  BigDecimal.ONE.divide( bdPeriod, CommonCalculations.MC) .multiply(
			BigDecimal.ONE.subtract( (finalResidual .divide(customerCost,  CommonCalculations.MC) )),CommonCalculations.MC); 
	}
	public  BigDecimal getFinalNBV(BigDecimal customerCost, BigDecimal depreciationFactor, BigDecimal bdPeriod) {
		
		BigDecimal temp1	= customerCost.divide(new BigDecimal(100),  CommonCalculations.MC);
		BigDecimal temp2 = customerCost.subtract(depreciationFactor.multiply(bdPeriod, CommonCalculations.MC).multiply(temp1,  CommonCalculations.MC), CommonCalculations.MC);
		if(temp2 != null){
			temp2	= CommonCalculations.getRoundedValue(temp2, 2);	
		}
		return temp2;
		
	}
		
	/**
	 * This method calculates rental and net book value for steps of open end quote.
	 * it returns list of  QuotationStepStructureVO for supplied input of quote.
	 * 
	 * The depreciationFactor and finalNBV should be sync with cost otherwise may result wrong rental. We pass finalNBV , just to match small penny issue.
	 * 
	 * OER-245 - Changed tempRental data type to BigDecimal and updated rounding
	 *           to fix penny issue
	 */
	public  List<QuotationStepStructureVO> calculateOEStepLease(BigDecimal depreciationFactor, BigDecimal finalNBV, BigDecimal netInterestRate, BigDecimal adminFactor, List<QuotationStepStructureVO> stepStructureVOList) {

	    	BigDecimal stepNBV = null;
	    	BigDecimal stepRental = null;
	    	BigDecimal adminCost = null;
	    
	    	for (int i = 0; i < stepStructureVOList.size(); i++) {
	    	
	    		QuotationStepStructureVO stepStructure = stepStructureVOList.get(i);	
			    long fromPeriod = stepStructure.getFromPeriod();
			    long toPeriod = stepStructure.getToPeriod();
			    long monthsInStep = toPeriod - fromPeriod + 1;
			    
			    stepNBV = BigDecimal.ZERO;
			    stepRental = BigDecimal.ZERO;
			    QuotationStepStructureVO firstStepStructure = stepStructureVOList.get(0);	

				for (int j = 0; j < stepStructure.getQuoteElementStepVOs().size(); j++) {
				
					QuoteElementStepVO quoteElementStepVO =   stepStructure.getQuoteElementStepVOs().get(j);
					
					BigDecimal originalElemenetCost =  firstStepStructure.getQuoteElementStepVOs().get(j).getOriginalCost();
					
			      	BigDecimal elementNBV = quoteElementStepVO.getStartCapital().subtract((originalElemenetCost.multiply(depreciationFactor, CommonCalculations.MC).multiply(new BigDecimal(monthsInStep), CommonCalculations.MC))); 
			    	elementNBV = elementNBV.setScale(2, BigDecimal.ROUND_HALF_UP);			    	
			    	stepNBV = stepNBV.add(elementNBV);	
			    	
			    	if( i == stepStructureVOList.size() -1 ){//last step
			    		
			    		if(finalNBV.compareTo(BigDecimal.ZERO) == 0){			    			
			    			elementNBV = BigDecimal.ZERO;
			    			stepNBV = BigDecimal.ZERO;
			    		}else if(j == stepStructure.getQuoteElementStepVOs().size() -1 ){// last item of last step	    		
				    		if(stepNBV.compareTo(finalNBV) != 0){
				    			BigDecimal diffNBV = finalNBV.subtract(stepNBV);
				    			stepNBV = stepNBV.add(diffNBV);
				    			elementNBV = elementNBV.add(diffNBV);
				    		}
			    		}
			    	}
			    	
			    	
			    	quoteElementStepVO.setEndCapital(elementNBV);
			    	
				    adminCost = originalElemenetCost.divide(new BigDecimal(1000) , CommonCalculations.MC).multiply(adminFactor, CommonCalculations.MC);			    
				    BigDecimal elementRental = BigDecimal.valueOf(CommonCalculations.pmt(netInterestRate.doubleValue(), monthsInStep, quoteElementStepVO.getStartCapital().negate().doubleValue(), quoteElementStepVO.getEndCapital().doubleValue(), false));
				    elementRental = elementRental.add(adminCost);				    
				    elementRental =  CommonCalculations.getRoundedValue(elementRental, 2);
				    
				    quoteElementStepVO.setRental(elementRental);
				    stepRental = stepRental.add(elementRental);		
				    
				    logger.debug(stepStructure.getFromPeriod() +"=startCap="+quoteElementStepVO.getStartCapital().negate().doubleValue() +"=endCapital="+quoteElementStepVO.getEndCapital() 
				    						+"=rentalValue="+elementRental +"=adminCost="+adminCost  +"=stepRental="+stepRental);
				    
				    if( i < (stepStructureVOList.size()-1) ){
				    	stepStructureVOList.get(i+1).getQuoteElementStepVOs().get(j).setStartCapital(quoteElementStepVO.getEndCapital());
			    }
			}
		    		    
		  	    
			stepStructure.setNetBookValue(stepNBV);
			stepStructure.setLeaseRate(stepRental);  

		}
		
		return stepStructureVOList;
	}
	
	

    /*
     * This method is same as calculateIrrFromOEStep except cash flow of revision Start Period month to  include one time charge
     */
	public BigDecimal calculateRevIrrFromOEStep(List<QuotationStepStructureVO> quotationStepList, BigDecimal costToCompany, BigDecimal adminFee ,
												Map<Long, BigDecimal> eventPeriodAmendmentChargeAfterLastRev, Map<Long, BigDecimal> eventPeriodOneTimeChargeMap) throws MalException {
		QuotationStepStructureVO lastQuotationStep = quotationStepList.get(quotationStepList.size() - 1);
		Double[] monthlyCashFlowArray = new Double[lastQuotationStep.getToPeriod().intValue() + 1];
		double monthlyLeaseRate = 00.00;
		adminFee = adminFee == null ? BigDecimal.ZERO : adminFee ; 
		for (int i = 0; i < monthlyCashFlowArray.length; i++) {

		    for (QuotationStepStructureVO quotationStep : quotationStepList) {
		    	if (i == quotationStep.getFromPeriod()) {
				    monthlyLeaseRate = quotationStep.getLeaseRate().doubleValue();
				    break;
				}
		    }
		    if (i == 0) {
		    	monthlyCashFlowArray[i] = -costToCompany.doubleValue();
		    }else if (i == monthlyCashFlowArray.length - 1) {
		    	monthlyCashFlowArray[i] = monthlyLeaseRate + lastQuotationStep.getNetBookValue().doubleValue() - adminFee.doubleValue();
		    } else {
		    	monthlyCashFlowArray[i] = monthlyLeaseRate - adminFee.doubleValue();
		    	//Below are additional   
		    	 if(eventPeriodAmendmentChargeAfterLastRev.containsKey(Long.valueOf(i))){
			    	monthlyCashFlowArray[i] = monthlyCashFlowArray[i] + eventPeriodAmendmentChargeAfterLastRev.get(Long.valueOf(i)).doubleValue() ;
			    }
		    	if(eventPeriodOneTimeChargeMap.containsKey(Long.valueOf(i))){
			    	monthlyCashFlowArray[i] = monthlyCashFlowArray[i] + eventPeriodOneTimeChargeMap.get(Long.valueOf(i)).doubleValue();
			    }
		    }
		}

		double calculatedIRR = CommonCalculations.getIrrFromMonthlyLeaseRate(monthlyCashFlowArray, IRR_GUESS, IRR_TOLERANCE,IRR_MAX_ITERATION);
		
		if (Double.compare(Double.NaN, calculatedIRR) == 0)
			throw new MalException("calculation.formula.Error");
		
		return new BigDecimal(String.valueOf(calculatedIRR)).multiply(new BigDecimal(100));
	}
	
	/**
	 * This method calculates irr based on stepped cash flow  of open end quote.
	 * @return irr.
	 */
	public BigDecimal calculateIrrFromOEStep(List<QuotationStepStructureVO> quotationStepList, BigDecimal costToCompany , BigDecimal adminFee) throws MalException  {

		QuotationStepStructureVO lastQuotationStep = quotationStepList.get(quotationStepList.size() - 1);
		Double[] monthlyCashFlowArray = new Double[lastQuotationStep.getToPeriod().intValue() + 1];
		double monthlyLeaseRate = 00.00;
		adminFee = adminFee == null ? BigDecimal.ZERO : adminFee ; 
		for (int i = 0; i < monthlyCashFlowArray.length; i++) {

		    for (QuotationStepStructureVO quotationStep : quotationStepList) {
			if (i == quotationStep.getFromPeriod()) {
			    monthlyLeaseRate = quotationStep.getLeaseRate().doubleValue();
			    break;
			}
		    }
		    if (i == 0) {
			monthlyCashFlowArray[i] = -costToCompany.doubleValue();
		    } else if (i == monthlyCashFlowArray.length - 1) {
			monthlyCashFlowArray[i] = monthlyLeaseRate + lastQuotationStep.getNetBookValue().doubleValue() - adminFee.doubleValue();
		    } else {
			monthlyCashFlowArray[i] = monthlyLeaseRate - adminFee.doubleValue();
		    }
		}

		double calculatedIRR = CommonCalculations.getIrrFromMonthlyLeaseRate(monthlyCashFlowArray, IRR_GUESS, IRR_TOLERANCE,IRR_MAX_ITERATION);
		
		if (Double.compare(Double.NaN, calculatedIRR) == 0)
			throw new MalException("calculation.formula.Error");
		
		return new BigDecimal(String.valueOf(calculatedIRR)).multiply(new BigDecimal(100));

	    }
	
	/**
	 * Calculates the lease factor based on input parameters formula: ((payment
	 * - ((depreciation factor/100) * customer cost) + ((admin factor/100) *
	 * customer cost)) / customer cost ) * 100
	 * 
	 */
	public BigDecimal getLeaseFactor(BigDecimal payment, BigDecimal depreciationFactor, BigDecimal adminFactor, BigDecimal customerCost) throws MalException  {

        	try {
        
        	    BigDecimal factor1 = depreciationFactor.movePointLeft(2);
        	    factor1 = factor1.multiply(customerCost);
        	    BigDecimal factor2 = adminFactor.movePointLeft(2);
        	    factor2 = factor2.multiply(customerCost);
        	    BigDecimal factor3 = payment.subtract(factor1.add(factor2));
        	    BigDecimal adjustedInterest = factor3.divide(customerCost, CommonCalculations.MC);
        	    adjustedInterest = adjustedInterest.movePointRight(2);
        	    BigDecimal leaseFactor = adjustedInterest.add(adminFactor).add(depreciationFactor);
        
        	    return leaseFactor;
        	} catch (Exception e) {
        	    throw new MalException("calculation.formula.Error");
        	}
	}
	

	/**
	 * Added provision to calc margin to cross verify if margin to IRR script conversion are correct.
	 * This method can calculate $ margin for given quote data with supplied interest rate.
	 */
	public BigDecimal calcMargin(BigDecimal capCost, BigDecimal totalResidual, int period, BigDecimal interestRate,
		    BigDecimal monthlyRental, BigDecimal disposalCost, BigDecimal adminFee) {

		BigDecimal[] arrayPrincipalBalanceAmount = new BigDecimal[period + 1];
		BigDecimal[] arrayInterestExpense = new BigDecimal[period + 1];
		BigDecimal[] arrayMontlyMargin = new BigDecimal[period + 1];
		BigDecimal totalMargin = BigDecimal.ZERO;
        	BigDecimal depreciationExpense = capCost.subtract(totalResidual).divide(new BigDecimal(period), CommonCalculations.MC)
        		.setScale(2, BigDecimal.ROUND_HALF_UP);
        
        	for (int i = 0; i <= period; i++) {
        
        	    if (i == 0) {
        
        		arrayPrincipalBalanceAmount[i] = capCost;
        		arrayInterestExpense[i] = BigDecimal.ZERO;
        		arrayMontlyMargin[i] = BigDecimal.ZERO;
        
        	    } else {
        
        		arrayInterestExpense[i] = arrayPrincipalBalanceAmount[i - 1].multiply(interestRate)
        			.divide(new BigDecimal(12), CommonCalculations.MC).setScale(2, BigDecimal.ROUND_HALF_UP);
        
        		if (i == period)
        		    arrayMontlyMargin[i] = monthlyRental.subtract(depreciationExpense).subtract(arrayInterestExpense[i]).subtract(adminFee)
        			    .subtract(disposalCost);
        		else
        		    arrayMontlyMargin[i] = monthlyRental.subtract(depreciationExpense).subtract(arrayInterestExpense[i]).subtract(adminFee);
        
        		arrayPrincipalBalanceAmount[i] = arrayPrincipalBalanceAmount[i - 1].subtract(monthlyRental).add(arrayInterestExpense[i])
        			.add(adminFee);
        
        	    }
        
        	}

		for (int i = 0; i < arrayMontlyMargin.length; i++) {
		    totalMargin = totalMargin.add(arrayMontlyMargin[i]);

		}

		return totalMargin.setScale(0, BigDecimal.ROUND_HALF_UP);

	}
	
	public BigDecimal getHurdleInterestRate(CorporateEntity corporateEntity, QuotationProfile quotationProfile, Date effectiveDate, Long contractPeriod) {
		BigDecimal hurdleRate;
		hurdleRate = externalAccountDAO.getHurdleInterestRate(corporateEntity.getCorpId(), quotationProfile.getQprId(), effectiveDate, contractPeriod);
		return hurdleRate;
	}
	
	public BigDecimal getProfitAdjustmentInterestRate(QuotationProfile quotationProfile, Date effectiveDate) throws MalBusinessException {
		BigDecimal clientProfitAdjustment;		
		clientProfitAdjustment = BigDecimal.valueOf(quotationService.getFinanceParam(MalConstants.CLIENT_PROFIT_ADJ, -1L, quotationProfile.getQprId(), effectiveDate, true));
		return clientProfitAdjustment;
	}
	
	public BigDecimal getMinimumIRR(CorporateEntity corporateEntity, QuotationProfile quotationProfile, Date effectiveDate, Long contractPeriod) throws MalBusinessException {
		BigDecimal hurdleRate;
		BigDecimal clientProfitAdjustment;
		BigDecimal minIRR = BigDecimal.ZERO;
		
		hurdleRate = externalAccountDAO.getHurdleInterestRate(corporateEntity.getCorpId(), quotationProfile.getQprId(), effectiveDate, contractPeriod);
		clientProfitAdjustment = BigDecimal.valueOf(quotationService.getFinanceParam(MalConstants.CLIENT_PROFIT_ADJ, -1L, quotationProfile.getQprId(), effectiveDate, true));
		minIRR = hurdleRate.add(clientProfitAdjustment);
		
		return minIRR;
	}	
		
	/**
	 * Calculates and returns the final NBV for a range of periods.  Note this is the value at the END of the given period.
	 */

    public List<PeriodFinalNBVVO> getFinalNBVPeriods(BigDecimal cost, BigDecimal depreciationFactor, int startingPeriod, int endingPeriod) {
    	List<PeriodFinalNBVVO> periods = new ArrayList<PeriodFinalNBVVO>();
    	
    	for(int i=startingPeriod; i<=endingPeriod; i++) {
    		PeriodFinalNBVVO period = new PeriodFinalNBVVO();
    		period.setPeriod((long)(i));
    		period.setFinalNBV(getFinalNBV(cost, depreciationFactor, new BigDecimal(i)));
    		periods.add(period);
    	}
    	return periods;
    }		
}
