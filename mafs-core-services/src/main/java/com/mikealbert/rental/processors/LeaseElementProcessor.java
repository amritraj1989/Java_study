package com.mikealbert.rental.processors;

import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.rental.calculations.QuoteCapitalCosts;
import com.mikealbert.rental.processors.inputoutput.ProcessorInputType;
import com.mikealbert.rental.processors.inputoutput.ProcessorOutputType;

/** This interface is being implements by BaseProcessor and it further extends by the all rental calculation service processors.
 **/
public interface LeaseElementProcessor {	
	 
	public ProcessorOutputType process(ProcessorInputType processorInputType) throws MalBusinessException;
	
	public String ZERO_RENTAL_PROCESSOR = "zeroRentalProcessor";
	public String MAINTENANCE_PROCESSOR = "maintenanceProcessor";
	public String RECAPITALIZE_PROCESSOR = "recapitalizeProcessor";
	public String AMORTIZATION_PROCESSOR = "amortizationProcessor";
	public String CLOSED_END_PROCESSOR = "closedEndProcessor";
	public String VMP_PROCESSOR = "vmpProcessor";
	public String OVERHEAD_PROFIT_COST_PROCESSOR = "overheadProfitCostProcessor";
	public String OVERHEAD_PROFIT_ADJUSTMENT_PROCESSOR = "overheadProfitAdjustmentProcessor";
	
	//used to determine the capital cost calc strategy used with the main lease element
	public QuoteCapitalCosts getCapitalCostsCalc();
}
