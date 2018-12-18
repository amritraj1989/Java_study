package com.mikealbert.rental.processors.inputoutput;

import java.math.BigDecimal;

import com.mikealbert.data.entity.QuotationElement;
import com.mikealbert.exception.MalBusinessException;


public interface ProcessorInputType {	
	
	public Long getDistance() ;
	public void setDistance(Long distance);
	public Long getQelId() ;
	public void setQelId(Long qelId);
	public Long getQmaId();
	public void setQmaId(Long qmaId) ;
	public Long getQdaId();
	public void setQdaId(Long qdaId);	
	public Long getPeriod();
	public void setPeriod(Long period) ;
	public void bindFinanceParamer(int index, BigDecimal  value) throws MalBusinessException;
	
	public QuotationElement getQuotationElement();	
	public void setQuotationElement(QuotationElement quotationElement);
	public void loadQuoteData();
}
