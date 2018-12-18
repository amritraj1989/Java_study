package com.mikealbert.service;

import java.math.BigDecimal;
import java.util.List;

import com.mikealbert.data.entity.ContractLine;
import com.mikealbert.data.entity.Product;
import com.mikealbert.data.entity.QuotationElement;
import com.mikealbert.data.entity.QuotationModel;
import com.mikealbert.data.vo.QuotationStepStructureVO;
import com.mikealbert.data.vo.QuoteOEVO;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.service.vo.QuoteCost;

public interface OpenEndService {
	
	public BigDecimal getCalculatedDepreciationFactor(QuoteOEVO quoteOEVO);
	public BigDecimal getModifiedNBV(BigDecimal nbv);
	public List<QuotationStepStructureVO> loadSteps(QuoteOEVO quoteOEVO);
	public List<QuotationStepStructureVO>  getCurrentContractStepsforRevisionSteps(QuoteOEVO currentOEQuoteVO, QuoteOEVO revisionOEQuoteVO, ContractLine contractLine) throws Exception;
	public String getLogoName(Product product);
	public String getClientAddress(QuoteOEVO quoteOEVO);
	public  QuoteCost getQuoteCost(QuotationModel qm) throws MalBusinessException;
	public BigDecimal getRentalPeriods(QuotationElement qe, QuoteOEVO quoteOEVO);
	public long getContractChangeEventPeriod(QuoteOEVO revisionOEQuoteVO, ContractLine contractLine);
	public BigDecimal calculateEffDateNBV(QuoteOEVO currentOEQuoteVO, QuoteOEVO revisionOEQuoteVO, ContractLine contractLine);
	public BigDecimal  getRevCustomerCostTotal(QuoteOEVO revisionOEQuoteVO);
	public String compareProfilesForMigration(long oldQprId, long newQprId) throws MalBusinessException;	
}
