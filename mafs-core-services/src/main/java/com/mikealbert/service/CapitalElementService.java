package com.mikealbert.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.mikealbert.data.entity.CapitalElement;
import com.mikealbert.data.entity.Model;
import com.mikealbert.data.entity.QuotationCapitalElement;
import com.mikealbert.exception.MalException;
import com.mikealbert.data.entity.QuotationModel;

@Transactional
public interface CapitalElementService {	
	
	public CapitalElement getCapitalElementByCode(String code);
	
	public BigDecimal getFreightCost(Model trim);	
	
	public BigDecimal getFixedAmount(CapitalElement cel) throws MalException;
	public List<QuotationCapitalElement> getApplicableCapitalElementsWithValue(Long qprId, String standardEDINo, Long corporateId, String accountType, String accountCode, String orderType);
	
	public BigDecimal getCapitalElementValue(Long celId, Long cerId, Long qprId, Long modelId, Long corporateId, String accountType, String accountCode, String quoteElemCalculation) throws MalException;
	
	public QuotationCapitalElement getQuotationCapitalElement(String code, QuotationModel quotationModel) ;
	public QuotationCapitalElement getPopulatedNewCapitalElementObjectByCode(String code, String source, QuotationModel quotationModel);
	
	public List<QuotationCapitalElement> getApplicableCapitalElementList(Long qprId, String standardEDINo);
	public QuotationCapitalElement getCapitalElementWithValue(Long cerId, String standardEDINo, Long corporateId, String accountType, String accountCode, String orderType);
	
	public final String OE_REV_ASSMNT = "OE_REV_ASSMNT";
	public final String OE_REV_INT_ADJ = "OE_REV_INT_ADJ";
	public final String OE_INV_ADJ = "OE_INV_ADJ";	
}
