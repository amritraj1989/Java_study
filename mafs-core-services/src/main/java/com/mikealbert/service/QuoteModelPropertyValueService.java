package com.mikealbert.service;

import java.util.List;
import java.util.Map;

import com.mikealbert.data.entity.QuotationModel;
import com.mikealbert.data.entity.QuoteModelProperty;
import com.mikealbert.data.entity.QuoteModelPropertyValue;
import com.mikealbert.data.enumeration.QuoteModelPropertyEnum;
import com.mikealbert.exception.MalBusinessException;


public interface QuoteModelPropertyValueService {	
	
	public QuoteModelProperty findQuoteModelProperty(String propertyName);
	
	public QuoteModelPropertyValue findByNameAndQmdId(String propertyName, Long qmdId);
	public QuoteModelPropertyValue saveOrUpdateQuoteModelPropertyValue(QuotationModel quotationModel, QuoteModelPropertyEnum quoteModelPropertyEnum, String value);
	
    	public List<QuoteModelPropertyValue> findAllByQmdId(Long qmdId);
	
	public Map<String, String> findPropertyNameValuePair(Long qmdId);
	public void populateQuoteModelPropertyValue (QuoteModelPropertyValue quoteModelPropertyValue) throws MalBusinessException;
	public void removeQuoteModelPropertyValue (long qmdId, String propertyName) throws MalBusinessException;
	

}
