package com.mikealbert.data.dao;

import java.math.BigDecimal;
import java.util.List;

import com.mikealbert.data.entity.CapitalElement;
import com.mikealbert.data.entity.QuotationCapitalElement;
import com.mikealbert.exception.MalException;



public interface CapitalElementDAOCustom  {	
	
	public List<CapitalElement> getCapitalElementsByProductCode(String productCode);	
	public List<QuotationCapitalElement> getApplicableCapitalElementsWithValue(Long qprId, String standardEDINo, Long corporateId, String accountType, String accountCode, String orderType);
	
	public BigDecimal getCapitalElementValue(Long celId, Long cerId, Long qprId, Long modelId, Long corporateId, String accountType, String accountCode, String quoteElemCalculation) throws MalException;
	

	public List<QuotationCapitalElement> getApplicableCapitalElementList(Long qprId, String standardEDINo);
	public QuotationCapitalElement getCapitalElementWithValue(Long cerId, String standardEDINo, Long corporateId, String accountType, String accountCode, String orderType);
	
}
