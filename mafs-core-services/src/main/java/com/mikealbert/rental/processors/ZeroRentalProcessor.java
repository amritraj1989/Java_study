package com.mikealbert.rental.processors;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import com.mikealbert.common.MalLogger;
import com.mikealbert.common.MalLoggerFactory;
import com.mikealbert.data.entity.QuotationElement;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.rental.calculations.QuoteCapitalCosts;
import com.mikealbert.rental.processors.inputoutput.ProcessorInputType;
import com.mikealbert.rental.processors.inputoutput.ProcessorOutput;
import com.mikealbert.rental.processors.inputoutput.ProcessorOutputType;
import com.mikealbert.rental.processors.inputoutput.ZeroRentalProcessorInput;

@Component("zeroRentalProcessor")
public class ZeroRentalProcessor implements LeaseElementProcessor{
	MalLogger logger = MalLoggerFactory.getLogger(this.getClass());
	
	/**
	 * ZeroRentalProcessor's concept is taken from the database procedure i.e. MAL_RENTAL_CALCS.zero_rental from willow2k schema.<br>
	 * <br>
	 * This method is mean to set zero of the following columns of table quotation_elements for a particular Quotation Element Id (i.e. QEL_ID).<br>
	 *  1. rental<br>
	 *  2. overhead_amt<br>
	 *  3. element_cost<br>
	 *  4. profit_amt
	 *     
	 * @param  processorInputType List of the input parameters are listed at {@link com.mikealbert.rental.processors.inputoutput.ZeroRentalProcessorInput}
	 * @return processorOutput Content of the processorOutput are listed at {@link com.mikealbert.rental.processors.inputoutput.ProcessorOutput}
	 */	
	@Override
	public ProcessorOutputType process(ProcessorInputType processorInputType) throws MalBusinessException {
		if(!(processorInputType instanceof ZeroRentalProcessorInput))
		    throw new MalBusinessException("not_a_valid_argument_for",new String[]{"ZeroRentalProcessor"} );
		try{
			ZeroRentalProcessorInput zeroRentalProcessorInput = (ZeroRentalProcessorInput)processorInputType;
			
			QuotationElement quotationElement =  zeroRentalProcessorInput.getQuotationElement();
			if (quotationElement != null) {
				quotationElement.setRental(new BigDecimal(0));
				quotationElement.setOverheadAmt(new BigDecimal(0));
				quotationElement.setElementCost(new BigDecimal(0));
				quotationElement.setProfitAmt(new BigDecimal(0));
			}
			
			ProcessorOutput processorOutput = new  ProcessorOutput();//set quotataion element
			processorOutput.setQuotationElement(quotationElement);		
			
			return processorOutput;
		}catch(Exception ex){
			if (ex instanceof MalBusinessException){
				throw (MalBusinessException)ex;
			}
			logger.error(ex);
			throw new MalBusinessException("generic.error",new String[]{"Error occured in ZeroRentalProcessor"},ex);
		}
		
		
	}

	/** Note: this Processor type never calculates CapitalCost based elements **/
	@Override
	public QuoteCapitalCosts getCapitalCostsCalc() {
		throw new UnsupportedOperationException();
	}

}
