package com.mikealbert.rental.processors.inputoutput;

import com.mikealbert.data.entity.QuotationElement;

public class ProcessorOutput implements ProcessorOutputType {	
	
	private QuotationElement quotationElement; 
	
	public QuotationElement getQuotationElement(){
		return quotationElement;
	}
	public void setQuotationElement(QuotationElement quotationElement){
		this.quotationElement = quotationElement;
	}

}
