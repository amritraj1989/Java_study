package com.mikealbert.rental.processors;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;
import com.mikealbert.common.MalLogger;
import com.mikealbert.common.MalLoggerFactory;
import com.mikealbert.data.dao.PaymentHeaderDAO;
import com.mikealbert.data.dao.QuotationCapitalElementDAO;
import com.mikealbert.data.entity.PaymentHeader;
import com.mikealbert.data.entity.QuotationCapitalElement;
import com.mikealbert.data.entity.QuotationElement;
import com.mikealbert.data.entity.QuotationElementStep;
import com.mikealbert.data.entity.QuotationModel;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.rental.calculations.QuoteCapitalCosts;
import com.mikealbert.rental.processors.inputoutput.ProcessorInputType;
import com.mikealbert.rental.processors.inputoutput.ProcessorOutput;
import com.mikealbert.rental.processors.inputoutput.ProcessorOutputType;
import com.mikealbert.rental.processors.inputoutput.VmpProcessorInput;


@Component("vmpProcessor")
public class VmpProcessor extends BaseProcessor implements LeaseElementProcessor {
	private MalLogger logger = MalLoggerFactory.getLogger(this.getClass());
	
	@Resource(name="capitalCostsWithMargin") private QuoteCapitalCosts capitalCostsCalc;
	
	@Resource  private PaymentHeaderDAO paymentHeaderDAO;
	@Resource
	private	QuotationCapitalElementDAO	quotationCapitalElementDAO;
	/**
	 * VmpProcessor's concept is taken from the database procedure i.e. MAL_RENTAL_CALCS.vmp_calc from willow2k schema.<br>
	 * <br>
	 * Following columns of table quotation_elements being updated here for a particular Quotation Element Id (i.e. QEL_ID).<br>
	 * <OL>
	 *  <li> rental </li>
	 *  <li> overhead_amt </li>
	 *  <li> element_cost </li>
	 *  <li> profit_amt </li>
	 *  <li> capital_cost </li>
	 *  <li> residual_value </li>
	 *  <li> depreciation </li>
	 *  <li> interest </li>
	 *  <li> no_rentals </li>
	 *  <li> final_payment </li>
	 *  <li> apr </li>
	 *     
	 * @param  processorInputType List of the input parameters are listed at {@link com.mikealbert.rental.processors.inputoutput.VmpProcessorInput}
	 * @return processorOutput Content of the processorOutput are listed at {@link com.mikealbert.rental.processors.inputoutput.ProcessorOutput}
	 */	
	@Override
	public ProcessorOutputType process(ProcessorInputType processorInputType) throws MalBusinessException {
		if (!(processorInputType instanceof VmpProcessorInput))
			throw new MalBusinessException("not_a_valid_argument_for",new String[]{"VmpProcessor"});
		try{
			VmpProcessorInput input = (VmpProcessorInput) processorInputType;
			input.loadQuoteData();

			Long noOfPeriods = null;
			BigDecimal capitalCost = null;
			BigDecimal residualValue = null;
			BigDecimal rental = null;
			BigDecimal averageInt = new BigDecimal(0);

			QuotationElement quotationElement = input.getQuotationElement();
    		QuotationModel quotationModel = quotationElement.getQuotationModel();//16318
    		
			BigDecimal vmpFee = input.getVmpFee();
			Long qmaId = input.getQmaId();
			Long qdaId = input.getQdaId();
			//Bug16318
    		BigDecimal basicPrice = new BigDecimal(0);
    		BigDecimal capitalContribution = new BigDecimal(0);
    	
			//capitalCost = input.getQmQuoteCapital();Bug16318 commented the code
			residualValue = input.getQmResidualValue();
			Long paymentId = input.getQmPaymentId();
			if (paymentId != null) {
				PaymentHeader paymentHeader = paymentHeaderDAO.findById(paymentId).orElse(null);
				if (paymentHeader != null) {
					BigDecimal tempPeriod = new BigDecimal(input.getQmContractPeriod())
								.divide(new BigDecimal(paymentHeader.getPmtPaymentFrequencyCode()), MC)
								.multiply(new BigDecimal(paymentHeader.getAnnualPeriods()), MC);
						
					noOfPeriods = tempPeriod != null ? tempPeriod.longValue() : 0L;
				}
			}
					
			if (qmaId != null && qmaId != 0) {
				capitalCost = this.capitalCostsCalc.calcModelAccessoryElement(quotationElement.getQuotationModelAccessory()).getCustomerCost();
				
				residualValue = input.getmResidualAmt();			
				rental = new BigDecimal(0);
			} else if (qdaId != null && qdaId != 0) {
				capitalCost = this.capitalCostsCalc.calcDealerAccessoryElement(quotationElement.getQuotationDealerAccessory()).getCustomerCost();
				
				residualValue = input.getdResidualAmt();			
				rental = new BigDecimal(0);
			} else {
				//Bug16318 Start, calculating the capital cost for main element 
				basicPrice = quotationModel.getBasePrice();
    		    capitalContribution = quotationModel.getCapitalContribution();
    		    List<QuotationCapitalElement> qceList = quotationCapitalElementDAO.findByQmdID(quotationModel.getQmdId());
    		    
    		    capitalCost = capitalCostsCalc.calcQuotationCost(basicPrice, capitalContribution, qceList).getCustomerCost();
    		    		    
				rental = vmpFee;
			}
			if(residualValue == null)
			    residualValue = BigDecimal.ZERO;
			
			List<QuotationElementStep> stepList = new ArrayList<QuotationElementStep>();
			averageInt = new BigDecimal(0);
			QuotationElementStep quotationElementStep = new QuotationElementStep();
			quotationElementStep.setQuotationElement(quotationElement);
			quotationElementStep.setFromPeriod(new BigDecimal(1));
			quotationElementStep.setToPeriod(new BigDecimal(noOfPeriods));
			quotationElementStep.setStartCapital(capitalCost);
			quotationElementStep.setEndCapital(residualValue);
			quotationElementStep.setRentalValue(rental);
			stepList.add(quotationElementStep);
			quotationElement.setQuotationElementSteps(stepList);

			quotationElement.setRental(rental.multiply(new BigDecimal(noOfPeriods)));
			quotationElement.setOverheadAmt(new BigDecimal(0));
			quotationElement.setProfitAmt(null);//proc is setting empty value
			//quotationElement.setElementCost(rental.multiply(new BigDecimal(noOfPeriods)));
			quotationElement.setCapitalCost(capitalCost);
			quotationElement.setResidualValue(residualValue);
			quotationElement.setDepreciation(capitalCost.subtract(residualValue));
			quotationElement.setNoRentals(new BigDecimal(noOfPeriods));
			quotationElement.setInterest(averageInt.multiply(new BigDecimal(noOfPeriods)));
			quotationElement.setFinalPayment(new BigDecimal(0));
			quotationElement.setApr(new BigDecimal(0));
			
			quotationElement.setCalcRentalApplicable(false);

			ProcessorOutput processorOutput = new ProcessorOutput();
			processorOutput.setQuotationElement(quotationElement);

			return processorOutput;
		}catch(Exception ex){
			if (ex instanceof MalBusinessException){
				throw (MalBusinessException)ex;
			}
			logger.error(ex);
			throw new MalBusinessException("generic.error",new String[]{"Error occured in VmpProcessor"},ex);
		}
		
	}
	@Override
	public QuoteCapitalCosts getCapitalCostsCalc() {
		return capitalCostsCalc;
	}
}
