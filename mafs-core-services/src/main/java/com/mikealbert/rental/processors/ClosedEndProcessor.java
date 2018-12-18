package com.mikealbert.rental.processors;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.mikealbert.common.MalConstants;
import com.mikealbert.common.MalLogger;
import com.mikealbert.common.MalLoggerFactory;
import com.mikealbert.data.dao.PaymentHeaderDAO;
import com.mikealbert.data.dao.QuotationCapitalElementDAO;
import com.mikealbert.data.dao.QuotationModelDAO;
import com.mikealbert.data.entity.PaymentHeader;
import com.mikealbert.data.entity.QuotationCapitalElement;
import com.mikealbert.data.entity.QuotationDealerAccessory;
import com.mikealbert.data.entity.QuotationElement;
import com.mikealbert.data.entity.QuotationElementStep;
import com.mikealbert.data.entity.QuotationModel;
import com.mikealbert.data.entity.QuotationModelAccessory;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.rental.calculations.CapitalCostsWithMargin;
import com.mikealbert.rental.calculations.QuoteCapitalCosts;
import com.mikealbert.rental.processors.inputoutput.ClosedEndProcessorInput;
import com.mikealbert.rental.processors.inputoutput.ProcessorInputType;
import com.mikealbert.rental.processors.inputoutput.ProcessorOutput;
import com.mikealbert.rental.processors.inputoutput.ProcessorOutputType;

@Service("closedEndProcessor")
public class ClosedEndProcessor extends BaseProcessor {
	 private MalLogger logger = MalLoggerFactory.getLogger(this.getClass());
    @Resource  private PaymentHeaderDAO paymentHeaderDAO;
    @Resource  private QuotationModelDAO quotationModelDAO;
    @Resource  private QuotationCapitalElementDAO quotationCapitalElementDAO;
    
    @Resource(name="capitalCostsWithMargin") private QuoteCapitalCosts capitalCostsCalc;
    
	/**
	 * ClosedEndProcessor's concept is taken from the database procedure i.e. MAL_RENTAL_CALCS.closed_end_calc from willow2k schema.<br>
	 * <br>
	 * Following columns of table quotation_elements being updated here for a particular Quotation Element Id (i.e. QEL_ID).<br>
	 * <OL>
	 *  <li> rental </li>
	 *  <li> overhead_amt (set to ZERO)</li>
	 *  <li> element_cost </li>
	 *  <li> profit_amt </li>
	 *  <li> capital_cost </li>
	 *  <li> residual_value </li>
	 *  <li> depreciation </li>
	 *  <li> no_rentals </li>
	 *  <li> interest </li>
	 *  <li> final_payment (set to ZERO) </li>
	 *  <li> apr </li>
	 * </OL> 
	 *     
	 * @param  processorInputType List of the input parameters are listed at {@link com.mikealbert.rental.processors.inputoutput.ClosedEndProcessorInput}
	 * @return processorOutput Content of the processorOutput are listed at {@link com.mikealbert.rental.processors.inputoutput.ProcessorOutput}
	 */	    
    @Override
    public ProcessorOutputType process(ProcessorInputType processorInputType) throws MalBusinessException {
    	try{
    		final String QUOTE_TYPE_A = "A";
    	    final String QUOTE_TYPE_Q = "Q";
    		if (!(processorInputType instanceof ClosedEndProcessorInput))
    		    throw new MalBusinessException("not_a_valid_argument_for",new String[]{"ClosedEndProcessor"});
    		
    		ClosedEndProcessorInput closedEndProcessorInput = (ClosedEndProcessorInput) processorInputType;
    		ProcessorOutput processorOutput = new ProcessorOutput();
    		QuotationElement quotationElement = closedEndProcessorInput.getQuotationElement();
    		QuotationModel quotationModel = quotationElement.getQuotationModel();
    		

    		BigDecimal capCost;
    		BigDecimal resValue;
    		Long qmdId = null;
    		Integer noPeriods = null;
    		BigDecimal interestRate = null;
    		Long qprId = null;
    		Long cId = null;
    		BigDecimal qelRental = null;
    		String manualProfit = null;
    		String  quoteType = "R";
    		Long oldQmdId = null;
    		Long oriContractPeriod = null;
    		BigDecimal basicPrice = new BigDecimal(0);
    		BigDecimal capitalContribution = new BigDecimal(0);
    		BigDecimal capitalEleSum = new BigDecimal(0);
    		String willowConfigValue = null;
    		Double leaseAdmin = null;
    		BigDecimal pvOfLeaseAdmin = null;
    		Double disposalCost = null;
    		Integer type = 1;

    		capCost = quotationModel.getQuoteCapital();
    		resValue = quotationModel.getResidualValue();

    		if (quotationModel != null) {
    		    qmdId = quotationModel.getQmdId();
    		    PaymentHeader paymentHeader = paymentHeaderDAO.findById(quotationModel.getPaymentId()).orElse(null);
    		    if (paymentHeader != null) {
    			BigDecimal bd1 = new BigDecimal(quotationModel.getContractPeriod().intValue());
    			BigDecimal bd2 = new BigDecimal(paymentHeader.getPmtPaymentFrequencyCode());
    			BigDecimal bd3 = new BigDecimal(paymentHeader.getAnnualPeriods());
    			noPeriods = bd1.divide(bd2,MC).multiply(bd3,MC).intValue(); 
    		    }
    		    interestRate = quotationModel.getInterestRate();
    		    qprId = quotationModel.getQuotation().getQuotationProfile().getQprId();
    		    cId = quotationModel.getQuotation().getExternalAccount().getExternalAccountPK().getCId();
    		    qelRental = quotationElement.getRental();
    		    manualProfit = quotationModel.getManualProfitUpdate() != null ? quotationElement.getQuotationModel().getManualProfitUpdate()
    			    : MalConstants.FLAG_N;
    		}
    		if (MalConstants.FLAG_Y.equals(manualProfit) && qelRental != null) {// TODO
    		    processorOutput.setQuotationElement(quotationElement);
    		    return processorOutput;
    		}

    		// set detail from origional qmd
    		if (quotationModel.getRevisionQmdId() == null) {
    		    quoteType = QUOTE_TYPE_A;
    		}
    		QuotationModel revQuotationModel = quotationModelDAO.findRevisionQmdId(qmdId);

    		if (revQuotationModel == null) {
    		    quoteType = QUOTE_TYPE_Q;
    		} else {
    		    oldQmdId = revQuotationModel.getQmdId();
    		}
    		if (QUOTE_TYPE_A.equals(quoteType)) {
    		    QuotationModel quotationModelTemp = quotationModelDAO.findById(oldQmdId).orElse(null);
    		    if (quotationModelTemp != null) {
    			oriContractPeriod = quotationModelTemp.getContractPeriod();

    		    }
    		}
    		QuotationModelAccessory modelAccessory = quotationElement.getQuotationModelAccessory();
    		QuotationDealerAccessory dealerAccessory = quotationElement.getQuotationDealerAccessory();
    		if (modelAccessory != null && modelAccessory.getQmaId().intValue() != 0) {
    		    if (modelAccessory.getDiscPrice() == null) {
    		    	modelAccessory.setDiscPrice(new BigDecimal(0));
    		    }

    		    capCost = this.capitalCostsCalc.calcModelAccessoryElement(modelAccessory).getCustomerCost();
    		    
    		    resValue = modelAccessory.getResidualAmt() != null ? modelAccessory.getResidualAmt() : new BigDecimal(0);

    		} else if (dealerAccessory != null && dealerAccessory.getQdaId().intValue() != 0) {
    			capCost = this.capitalCostsCalc.calcDealerAccessoryElement(dealerAccessory).getCustomerCost();
    			
    		    resValue = dealerAccessory.getResidualAmt() != null ? dealerAccessory.getResidualAmt() : new BigDecimal(0);
    		} else {
    			basicPrice = quotationModel.getBasePrice();
    		    capitalContribution = quotationModel.getCapitalContribution();
    		    List<QuotationCapitalElement> qceList = quotationCapitalElementDAO.findByQmdID(qmdId);
    		    
    		    capCost = this.capitalCostsCalc.calcQuotationCost(basicPrice, capitalContribution, qceList).getCustomerCost();

    		    quotationModel.setQuoteCapital(capCost);
    		}
    		
    		QuotationElementStep quotationElementStep = new QuotationElementStep();
    		if (QUOTE_TYPE_A.equals(quoteType)) {
    		    Long fPeriod = oriContractPeriod - noPeriods + 1;
    		    quotationElementStep.setFromPeriod(new BigDecimal(fPeriod));
    		    quotationElementStep.setToPeriod(new BigDecimal(oriContractPeriod));
    		} else {
    		    quotationElementStep.setFromPeriod(new BigDecimal(1));
    		    quotationElementStep.setToPeriod(new BigDecimal(noPeriods));
    		}
    		quotationElementStep.setQuotationElement(quotationElement);
    		quotationElementStep.setStartCapital(capCost);
    		quotationElementStep.setEndCapital(resValue);
    		quotationElementStep.setRentalValue(capCost);
    		List<QuotationElementStep> list = new ArrayList<QuotationElementStep>();
    		list.add(quotationElementStep);
    		quotationElement.setQuotationElementSteps(list);

    		quotationElement.setRental(capCost);
    		quotationElement.setOverheadAmt(new BigDecimal(0));
    		quotationElement.setProfitAmt(new BigDecimal(0));
    		quotationElement.setCapitalCost(capCost);
    		quotationElement.setResidualValue(resValue);
    		quotationElement.setDepreciation(capCost.subtract(resValue));
    		quotationElement.setNoRentals(new BigDecimal(noPeriods));
    		
    		quotationElement.setFinalPayment(new BigDecimal(0));
    		quotationElement.setApr(new BigDecimal(0));

    		processorOutput.setQuotationElement(quotationElement);

    		return processorOutput;
    	}catch(Exception ex){
    		if(ex instanceof MalBusinessException){
    			throw (MalBusinessException)ex;
    		}
    		logger.error(ex);
    		throw new MalBusinessException("generic.error ", new String[]{"Error in closed end processor."}, ex);
    	}
    
    }


	@Override
	public QuoteCapitalCosts getCapitalCostsCalc() {
		return capitalCostsCalc;
	}
   
}
