package com.mikealbert.rental.processors;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.mikealbert.common.MalLogger;
import com.mikealbert.common.MalLoggerFactory;
import com.mikealbert.data.dao.QuotationModelDAO;
import com.mikealbert.data.dao.QuotationScheduleDAO;
import com.mikealbert.data.entity.QuotationElement;
import com.mikealbert.data.entity.QuotationModel;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.rental.calculations.QuoteCapitalCosts;
import com.mikealbert.rental.processors.inputoutput.ProcessorInputType;
import com.mikealbert.rental.processors.inputoutput.ProcessorOutput;
import com.mikealbert.rental.processors.inputoutput.ProcessorOutputType;
import com.mikealbert.rental.processors.inputoutput.RecapitalizeProcessorInput;
import com.mikealbert.service.QuotationService;
import com.mikealbert.service.WillowConfigService;

/** Re-capitalized Costs - MAL.
 */
@Service("recapitalizeProcessor")
public class RecapitalizeProcessor extends BaseProcessor{
	private MalLogger logger = MalLoggerFactory.getLogger(this.getClass());
	@Resource private QuotationService quotationService ;
	@Resource private QuotationModelDAO quotationModelDAO;
	@Resource private QuotationScheduleDAO  quotationScheduleDAO;
	@Resource private  WillowConfigService willowConfigService;
	
	/**
	 * RecapitalizeProcessor's concept is taken from the database procedure i.e. MAL_RENTAL_CALCS.recap_calc from willow2k schema.<br>
	 * 
	 * This method is almost similar to AmortizationProcessor. In case of Contract Amendment, similar calculation takes place as of AmortizationProcessor, 
	 * but if not contract Amendment/Revision, calculation takes place by calling methods pmt and interestRate and updates below columns of table
	 * quotation_elements <br>
	 * <OL>
	 *  	<li> rental</li>
	 *  	<li> overhead_amt</li>
	 *  	<li> profit_amt</li>
	 *  	<li> element_cost</li>
	 *  	<li> no_rentals</li>
	 *  </OL>
	 *     
	 * @param  processorInputType List of the input parameters are listed at {@link com.mikealbert.rental.processors.inputoutput.RecapitalizeProcessorInput}
	 * @return processorOutput Content of the processorOutput are listed at {@link com.mikealbert.rental.processors.inputoutput.ProcessorOutput}
	 */	
	@Override
	public ProcessorOutputType process(ProcessorInputType processorInputType) throws MalBusinessException {
		if(!(processorInputType instanceof RecapitalizeProcessorInput))
		    throw new MalBusinessException("not_a_valid_argument_for",new String[]{"RecapitalizeProcessor"} );
		try{
			ProcessorOutput processorOutput = new  ProcessorOutput();//set quotataion element
			RecapitalizeProcessorInput recapitalizeProcessorInput = (RecapitalizeProcessorInput)processorInputType;
			QuotationElement quotationElement = recapitalizeProcessorInput.getQuotationElement();
			long contractPeriod = recapitalizeProcessorInput.getPeriod();
			 // below are finance parameter
			BigDecimal amount = recapitalizeProcessorInput.getAmount();			
			BigDecimal profitAmout = recapitalizeProcessorInput.getProfitAmount();
			BigDecimal interestAdjust = recapitalizeProcessorInput.getAdjustment();
			
			BigDecimal interestRate = null;
			long currentContractPeriod = 0;
			BigDecimal billedToDate = new BigDecimal(0);
			BigDecimal tempBilled = new BigDecimal(0);
			// BigDecimal amount = new BigDecimal(0);
			boolean gaps = false;
			boolean noHistory = true;
			if (amount.compareTo(new BigDecimal(0)) == 0) {
			    processorOutput.setQuotationElement(quotationElement);
			    return processorOutput;
			}
			currentContractPeriod = contractPeriod;
			String reviewFrequency = quotationElement.getQuotationModel().getQuotation().getQuotationProfile().getReviewFrequency();
			String fixedOrFloat = quotationElement.getQuotationModel().getQuotation().getQuotationProfile().getVariableRate().equals("F") ? "FIXED" : "FLOAT" ;
			String  interestType =  quotationService.getInterestType(quotationElement.getQuotationModel().getQmdId(),quotationElement.getLeaseElement().getLelId(),quotationElement.getQuotationModel().getRevisionDate());//added qelid for HD-215
			
			if (quotationElement.getQuotationModel().getQuoteStatus() == 9) { //Amendment
				List<QuotationModel> prevQuoteModels = quotationModelDAO.findPrevQuotationsByUnitNo(quotationElement
						.getQuotationModel().getUnitNo(), quotationElement.getQuotationModel().getQmdId());
				if (prevQuoteModels == null || prevQuoteModels.size() == 0) {
					throw new MalBusinessException("unable_determine_prev_quote",
							new String[] {""+ quotationElement.getQelId()});
				}
				for (QuotationModel quotationModel : prevQuoteModels) {
					if (!containsElement(quotationModel.getQmdId(), quotationElement.getLeaseElement().getLelId())) {
						gaps = true;
						break;
					}
				}
				// All historical qmds had the element on and hence should go
				// for the rate / period of the first contract, i.e. Scenario 1
				if (!gaps) {
					interestRate = prevQuoteModels.get(0).getInterestRate();
					currentContractPeriod = prevQuoteModels.get(0).getContractPeriod() != null ? prevQuoteModels.get(0)
							.getContractPeriod().intValue() : 0;
				} else {
					// Now try to pick up scenario 2. The element has never been
					// on the quote before
					for (QuotationModel quotationModel : prevQuoteModels) {
						if (containsElement(quotationModel.getQmdId(), quotationElement.getLeaseElement().getLelId())) {
							noHistory = false;
						}
					}
					if (noHistory) {// this element was never present in previous quote models
						currentContractPeriod = contractPeriod;
					} else {
						// First calc the amount billed to date
						for (QuotationModel quotationModel : prevQuoteModels) {
							if (containsElement(quotationModel.getQmdId(), quotationElement.getLeaseElement().getLelId())) {
								tempBilled = quotationScheduleDAO.sumOfAmountByQuoteElementAndQmdIdAndLease(
										quotationElement.getQelId(), quotationModel.getQmdId(), quotationElement.getLeaseElement()
												.getLelId());
								billedToDate = billedToDate.add(tempBilled != null ? tempBilled : new BigDecimal(0));
							}
						}
						billedToDate = billedToDate != null ? billedToDate:new BigDecimal(0);
						amount = amount.subtract(billedToDate);
						currentContractPeriod = contractPeriod;
					}
				}
			}// quote status 9 end here
			if (! (quotationElement.getQuotationModel().getQuoteStatus() == 9)) { // Not an amendment
				
			  	Date effectiveDate = quotationElement.getQuotationModel().getRevisionDate();
				//String  interestType = willowConfigService.getConfigValue(MalConstants.RECAP_INTEREST_TYPE_CONFIG);
				interestRate =  quotationService.getInterestRateByType(effectiveDate, contractPeriod, interestType, reviewFrequency, fixedOrFloat);				
			} else if (interestRate == null) {  // it will execute if quote status is 10 or (quote status is  9 and element was NOT present in all previous quote) 
				//interestRate = quotationService.getInterestRate(quotationElement.getQuotationModel().getQmdId());
				interestRate =  quotationService.getInterestRateByType(quotationElement.getQuotationModel().getRevisionDate(), contractPeriod, interestType, reviewFrequency, fixedOrFloat);
			}
			interestRate = interestRate != null ? interestRate:new BigDecimal(0);
			interestRate = interestRate.add(interestAdjust != null ? interestAdjust : new BigDecimal(0));
			profitAmout = profitAmout != null ? profitAmout : new BigDecimal(0);
			
			BigDecimal calculatedPmtAmt = pmt(currentContractPeriod, amount.multiply(new BigDecimal(-1)), new BigDecimal(0), interestRate, 0);
			calculatedPmtAmt = calculatedPmtAmt != null ? calculatedPmtAmt: new BigDecimal(0);
			BigDecimal totalPmtAmt = calculatedPmtAmt.multiply(new BigDecimal(contractPeriod));
			BigDecimal totalProfitAmout = profitAmout.multiply(new BigDecimal(contractPeriod) );
					
			quotationElement.setRental(totalPmtAmt.add(totalProfitAmout));
			quotationElement.setOverheadAmt(BigDecimal.ZERO);
			quotationElement.setProfitAmt(totalProfitAmout);
			quotationElement.setElementCost(totalPmtAmt);
			quotationElement.setNoRentals(new BigDecimal(contractPeriod));
			
			processorOutput.setQuotationElement(quotationElement);
			return processorOutput;	
		}catch(Exception ex){
			if (ex instanceof MalBusinessException){
				throw (MalBusinessException)ex;
			}
			logger.error(ex);
			throw new MalBusinessException("generic.error",new String[]{"Error occured in RecapitalizeProcessor"},ex);
		}
		
	}

	/** Note: this Processor type never calculates CapitalCost based elements **/
	@Override
	public QuoteCapitalCosts getCapitalCostsCalc() {
		throw new UnsupportedOperationException();
	}

}
