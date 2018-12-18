package com.mikealbert.service;

import java.math.BigDecimal;
import java.util.List;

import com.mikealbert.data.entity.ExternalAccount;
import com.mikealbert.data.entity.LeaseElement;
import com.mikealbert.data.entity.MulQuoteEle;
import com.mikealbert.data.entity.QuotationDealerAccessory;
import com.mikealbert.data.entity.QuotationElement;
import com.mikealbert.data.entity.QuotationModel;
import com.mikealbert.data.entity.QuotationModelAccessory;
import com.mikealbert.exception.MalBusinessException;

public interface QuotationElementService {
	public QuotationElement getCalculatedNewQuotationElement(QuotationModel quoteModel,LeaseElement leaseElement ,QuotationModelAccessory modelAccessory,QuotationDealerAccessory dealerAccessory ,boolean skipQuoteLvlFinanceParams) throws MalBusinessException;
	public QuotationElement getCalculatedQuotationElement(QuotationElement quoteElement, QuotationModel quoteModel, boolean skipQuoteLvlFinanceParams)  throws MalBusinessException;
	public QuotationElement getCalculatedQuotationElement(QuotationElement quoteElement, QuotationModel quoteModel, boolean skipQuoteLvlFinanceParams, ExternalAccount extAcct)  throws MalBusinessException;
	public QuotationElement getOriginalQuotationElement(QuotationElement quoteElement, QuotationModel quoteModel)  throws MalBusinessException;
	public Long getNoOfPeriods(QuotationElement quoteElement, QuotationModel quoteModel);
	public QuotationElement findQuotationElementIfExists(List<QuotationElement> allElements, QuotationElement elementToCheck);
	public MulQuoteEle findFinanceMulQuoteEle(List<MulQuoteEle> mulQuoteElems);
	public void updateCdfeeDifferenceInCapitalCost(QuotationModel qmd, BigDecimal origCdFeeCost) throws MalBusinessException;
	public String findFinanceElementExistByProfileId(Long qprId, Long cId);
}
