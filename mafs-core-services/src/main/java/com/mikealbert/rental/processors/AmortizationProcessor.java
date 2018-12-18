package com.mikealbert.rental.processors;

import java.math.BigDecimal;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.mikealbert.data.dao.QuotationModelDAO;
import com.mikealbert.data.dao.QuotationScheduleDAO;
import com.mikealbert.data.entity.QuotationElement;
import com.mikealbert.data.entity.QuotationModel;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.rental.calculations.QuoteCapitalCosts;
import com.mikealbert.rental.processors.inputoutput.AmortizationProcessorInput;
import com.mikealbert.rental.processors.inputoutput.ProcessorInputType;
import com.mikealbert.rental.processors.inputoutput.ProcessorOutput;
import com.mikealbert.rental.processors.inputoutput.ProcessorOutputType;
import com.mikealbert.service.QuotationService;

@Service("amortizationProcessor")
public class AmortizationProcessor extends BaseProcessor {
	
	@Resource QuotationModelDAO quotationModelDAO;
	@Resource QuotationScheduleDAO quotationScheduleDAO;
	@Resource QuotationService quotationService ;
	/**
	 * AmortizationProcessor's concept is taken from the database procedure i.e. MAL_RENTAL_CALCS.amortization_calc from willow2k schema.<br>
	 * This method is basically designed for catering the amortization calculation of Contract Amendments.<br>
	 * 
	 * Here, We can consider three Contract Amendment scenarios.
	 *<OL>
	 *<li>The element exists on the original quote and this element is probably not the reason for the amendment, in which case the
	 *calculation should use the original contract period and interest rate to calculate the quote on, i.e. to get back to the same result.</li>
	 *<li>The element is being added to a quote for the first time. In this case the amount is to be capitalized at the current interest rate.</li>
	 *<li>The element was part of the contract, removed, billed for a number of months and then is being re-attached. </li></OL>
	 *     
	 * @param  processorInputType : Inputs are Quotation Element Id, Amount, Period.{@link com.mikealbert.rental.processors.inputoutput.AmortizationProcessorInput}
	 * @return processorOutput : Record of Quotation Elements for a given element id.{@link com.mikealbert.rental.processors.inputoutput.ProcessorOutput}
	 */
	@Override
	public ProcessorOutputType process(ProcessorInputType processorInputType) throws MalBusinessException {
		if (!(processorInputType instanceof AmortizationProcessorInput))
		    throw new MalBusinessException("not_a_valid_argument_for",new String[]{"AmortizationProcessor"});

		AmortizationProcessorInput amortizationProcessorInput = (AmortizationProcessorInput) processorInputType;

		QuotationElement quotationElement = amortizationProcessorInput.getQuotationElement();		
		BigDecimal amount = amortizationProcessorInput.getAmount();
		long contractPeriod = amortizationProcessorInput.getPeriod();
		ProcessorOutput processorOutput = new ProcessorOutput();
		processorOutput.setQuotationElement(quotationElement);
		BigDecimal calculatedAmount = null;
		long currentContractPeriod = 0;
		BigDecimal billedToDate = new BigDecimal(0);
		BigDecimal tempBilled;

		boolean gaps = false;
		boolean noHistory = true;
		if (amount.compareTo(new BigDecimal(0)) == 0) {
			return processorOutput;
		}
		currentContractPeriod = contractPeriod;
		calculatedAmount = amount;
		if (quotationElement.getQuotationModel().getQuoteStatus() == 9) {// Contract
																			// amendment
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
				if (noHistory) {
					currentContractPeriod = contractPeriod;
				} else {
					//  Now into scenario 3
					// First calc the amount billed to date.
					for (QuotationModel quotationModel : prevQuoteModels) {
						if (containsElement(quotationModel.getQmdId(), quotationElement.getLeaseElement().getLelId())) {// TODO	  with DB
							tempBilled = quotationScheduleDAO.sumOfAmountByQuoteElementAndQmdIdAndLease(quotationElement
									.getQelId(), quotationModel.getQmdId(), quotationElement.getLeaseElement().getLelId());
							billedToDate = billedToDate.add(tempBilled != null ? tempBilled : new BigDecimal(0));
						}
					}
					amount = amount.subtract(billedToDate);
					currentContractPeriod = contractPeriod;
				}
			}
		}// condition for status 9 ends
		
		if (currentContractPeriod > 0) {
			calculatedAmount = amount.divide(new BigDecimal(currentContractPeriod),MC);
		} else if (currentContractPeriod == 0) {
			calculatedAmount = amount;
		}
		
		if (quotationElement.getQuotationModel().getQuoteStatus() == 10) { // Contract revision
			calculatedAmount = super.getInRatePMTAmountForQuoteRevision(quotationElement, amount, contractPeriod);
		}
		
		quotationElement.setRental(calculatedAmount.multiply(new BigDecimal(contractPeriod)));
		quotationElement.setOverheadAmt(new BigDecimal(0));
		quotationElement.setProfitAmt(new BigDecimal(0));
		quotationElement.setElementCost(calculatedAmount.multiply(new BigDecimal(contractPeriod)));
		quotationElement.setNoRentals(new BigDecimal(contractPeriod));

		processorOutput.setQuotationElement(quotationElement);

		return processorOutput;
	}

	/** Note: this Processor type never calculates CapitalCost based elements **/
	@Override
	public QuoteCapitalCosts getCapitalCostsCalc() {
		throw new UnsupportedOperationException();
	}
}
