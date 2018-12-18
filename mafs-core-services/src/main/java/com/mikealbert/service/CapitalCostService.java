package com.mikealbert.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Transactional;

import com.mikealbert.data.entity.CapitalElement;
import com.mikealbert.data.entity.Doc;
import com.mikealbert.data.entity.QuotationCapitalElement;
import com.mikealbert.data.entity.QuotationModel;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.service.vo.QuoteCost;
import com.mikealbert.service.vo.QuoteCostElementVO;
import com.mikealbert.service.vo.QuoteVO;
import com.mikealbert.service.vo.CapitalCostModeValuesVO;

@Transactional
public interface CapitalCostService {	
	
	public BigDecimal getFactoryEquipmentCost(QuotationModel quotationModel);
	
	public BigDecimal getDealerAccessoryCost(QuotationModel quotationModel);
	
	public QuoteCost getTotalCostsForQuote(QuotationModel quotationModel) throws Exception;
	
	public BigDecimal getLeaseElementCostByType(QuotationModel quotationModel, String type);

	
	public Map<String, Double> getEquipmentDetail(QuotationModel quotationModel, String equipmentType) throws MalBusinessException;
	
	public Map<String, Double> getInvoicedEquipmentDetail(long  qmdId, String equipmentType, String docType, String sourceType) throws MalBusinessException;
	
	public QuoteVO getQuoteCapitalCosts(QuotationModel quotationModel, Boolean finalized,QuoteVO priorQuote, Boolean isStockOrder) throws MalBusinessException;
	public QuoteVO getQuoteCapitalCosts2(QuotationModel quotationModel, Boolean finalized,QuoteVO priorQuote, Boolean isStockOrder) throws MalBusinessException;

	public QuoteVO getInvoiceCapitalCosts(QuotationModel quotationModel,List<QuoteCostElementVO> allCostElement , Boolean isStockOrder, boolean postedOnlyFlag) throws MalBusinessException;
	public QuoteVO populateReclaimCosts(QuotationModel firstQuote ,QuotationModel finalizedQuoteModel ,QuoteVO quoteVO ) throws MalBusinessException;
	public QuoteVO populatePODetails(QuotationModel firstQuote ,QuoteVO quoteVO ) throws MalBusinessException;
	
	public List<CapitalElement> getCapitalElementByProductCode(String productCode);
	public QuotationCapitalElement saveQuotationCapitalElement(QuotationCapitalElement quotationCapitalElement) throws Exception;
	
	public  BigDecimal getQuoteCapitalElementValue(Long qmdId, String capitalElementCode) throws MalBusinessException;

	public void updateQuoteCapitalElement(long qmdId, String capitalElementCode , BigDecimal  value) throws MalBusinessException;
	public List<Doc> getInvoiceForCapitalElementByQuote(Long qmdId, String docType, String sourceType, boolean postedOnlyFlag) ;

	final static String DOC_STATUS_POSTED = "P";
	public static final String STANDARD_ORDER_MODE  = "SO" ;
	public static final String FINALIZED_MODE  = "FINAL" ;
	public static final String FIRST_MODE  = "FIRST" ;
	
	public CapitalCostModeValuesVO getModeValues(QuotationModel quotationModel) throws MalBusinessException;
	public QuoteVO resolveAndCalcCapitalCosts(QuotationModel quotationModel) throws MalBusinessException;
	
	public String findQuoteCapitalCostsCalcType(QuotationModel quotationModel);
	
}
