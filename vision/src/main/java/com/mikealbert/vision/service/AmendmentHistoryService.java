package com.mikealbert.vision.service;

import java.math.BigDecimal;
import java.util.List;

import com.mikealbert.data.entity.ContractLine;
import com.mikealbert.data.entity.QuotationModel;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.vision.vo.AmendmentHistoryVO;
import com.mikealbert.vision.vo.EleAmendmentDetailVO;

public interface AmendmentHistoryService {
	public List<AmendmentHistoryVO> getAmendedQuotes(Long qmdId, Boolean showDealAmendment, Boolean showServiceEleAmendment)
			throws MalBusinessException;

	public List<EleAmendmentDetailVO> getElementsFromInformalAmendment(Long quotationModelId)
			throws MalBusinessException;

	public Boolean isAmendmentExistsOnQuote(QuotationModel quotationModel,ContractLine contractLine);
	
	public List<AmendmentHistoryVO> getAmendedQuotesWithAmendmentDetail(Long qmdId, Boolean showDealAmendment,
			Boolean showServiceEleAmendment) throws MalBusinessException ;
	
	public String getContractSource(QuotationModel inComingQuotationModel,ContractLine contractLine);
	public BigDecimal getLeaseRateOE(QuotationModel quotationModel);
	public List<ContractLine> getApplicableContractLinesForHistory(QuotationModel inComingQuotationModel) ;

	public static final String AMEND_IND_ADDED = "+";
	public static final String AMEND_IND_REMOVED = "-";
	public static final String AMEND_IND_PRICE_CHANGE = "$";
	public static final String ELEMENT_TYPE_MODEL = "MODEL";
	public static final String ELEMENT_TYPE_DEALER = "DEALER";
	public static final String ELEMENT_TYPE_SERVICE = "SERVICE";
	public static final String RECHARGED_TEXT = "Recharged";
	public static final String INFORMAL_TEXT = "Informal";
	public static final String	CONTRACT_SOURCE_AMENDMENT = "A";
	public static final String	CONTRACT_SOURCE_FORMAL = "F";
	public static final String	CONTRACT_SOURCE_REVISION = "R";
	public static final String	CONTRACT_SOURCE_EARLY_TERMINATE = "E";
	
	public static final	String	INFORMAL_AMEND_ADD = "A";
	public static final	String	INFORMAL_AMEND_REMOVE = "R";
	

	public final String OE_REV_ASSMT_INRATE_YN = "OE_REV_ASSMT_INRATE_YN";
	public final String OE_REV_INT_ADJ_INRATE_YN = "OE_REV_INT_ADJ_INRATE_YN";
	public final String IN_RATE_TYPE = "InRate";
	public final String ONE_TIME_CHARGE_TYPE = "OneTime";	
	
	public final String DISPLAY_NAME_REV_ASSESSMENT = "Revision Assessment - In-Rate";
	public final String DISPLAY_NAME_REV_INT_ADJ = "Revision Interest Adjustment - In-Rate";
	public final String DISPLAY_NAME_REV_INVOICE_ADJ = "Invoice Adjustment";
	public final String DISPLAY_NAME_REV_CAP_CONTR = "Capital Contribution";
}
