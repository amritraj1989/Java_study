package com.mikealbert.rental.processors;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import com.mikealbert.common.MalConstants;
import com.mikealbert.common.MalLogger;
import com.mikealbert.common.MalLoggerFactory;
import com.mikealbert.data.entity.QuotationElement;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.rental.calculations.QuoteCapitalCosts;
import com.mikealbert.rental.processors.inputoutput.OverheadProfitAdjustmentProcessorInput;
import com.mikealbert.rental.processors.inputoutput.ProcessorInputType;
import com.mikealbert.rental.processors.inputoutput.ProcessorOutput;
import com.mikealbert.rental.processors.inputoutput.ProcessorOutputType;

@Service("overheadProfitAdjustmentProcessor")
public class OverheadProfitAdjustmentProcessor implements LeaseElementProcessor{
	private MalLogger logger = MalLoggerFactory.getLogger(this.getClass());
	
	/**
	 * OverheadProfitAdjustmentProcessor's concept is taken from the database procedure i.e. WILLOW_RENTAL_CALCS.setup_base_service4 from willow2k schema.<br>
	 * <br>
	 * This method sets up the overhead and overhead profit on quotation elements. An adjustment can be made at quotation level using the finance parameter passed.<br>
	 * 
	 * Following columns of table quotation_elements being updated here for a particular Quotation Element Id (i.e. QEL_ID).<br>
	 * <OL>
	 *  <li> rental </li>
	 *  <li> overhead_amt </li>
	 *  <li> element_cost (set to ZERO)</li>
	 *  <li> profit_amt </li>
	 * </OL> 
	 *     
	 * @param  processorInputType List of the input parameters are listed at {@link com.mikealbert.rental.processors.inputoutput.OverheadProfitAdjustmentProcessorInput}
	 * @return processorOutput Content of the processorOutput are listed at {@link com.mikealbert.rental.processors.inputoutput.ProcessorOutput}
	 */	
	@Override
	public ProcessorOutputType process(ProcessorInputType processorInputType) throws MalBusinessException {
		if(!(processorInputType instanceof OverheadProfitAdjustmentProcessorInput))
		    throw new MalBusinessException("not_a_valid_argument_for",new String[]{"OverheadProfitAdjustmentProcessor"} );
		try{
			OverheadProfitAdjustmentProcessorInput input = (OverheadProfitAdjustmentProcessorInput)processorInputType;
			
			BigDecimal overHead  = input.getOverhead();
			BigDecimal overHeadProfit  = input.getOverheadProfit();
			BigDecimal overHeadAdjust  = input.getOverheadAdjust();
			QuotationElement quotationElement = input.getQuotationElement();
			long contractPeriod	 =  input.getPeriod();
		
			overHead = overHead == null ? new BigDecimal(0) : overHead;
			overHeadProfit = overHeadProfit == null ? new BigDecimal(0) : overHeadProfit;
			overHeadAdjust = overHeadAdjust == null ? new BigDecimal(0) : overHeadAdjust;
			
			
			if (MalConstants.FLAG_N.equals(quotationElement.getAcceptedInd())) {
			    
				quotationElement.setRental((overHead.add(overHeadProfit).add(overHeadAdjust)).multiply(new BigDecimal(contractPeriod)));
				quotationElement.setOverheadAmt(overHead.multiply(new BigDecimal(contractPeriod)));
				quotationElement.setProfitAmt(overHeadProfit.add(overHeadAdjust).multiply(new BigDecimal(contractPeriod)));
				quotationElement.setElementCost(new BigDecimal(0));
			}
			
			ProcessorOutput processorOutput = new  ProcessorOutput();//set quotataion element
			processorOutput.setQuotationElement(quotationElement);
			
			return processorOutput;
		}catch(Exception ex){
			logger.error(ex);
			throw new MalBusinessException("generic.error",new String[]{"Error occured in OverheadProfitAdjustmentProcessor"},ex);
			
		}
		
	}
	/** Note: this Processor type never calculates CapitalCost based elements **/
	@Override
	public QuoteCapitalCosts getCapitalCostsCalc() {
		throw new UnsupportedOperationException();
	}

}
	
	
