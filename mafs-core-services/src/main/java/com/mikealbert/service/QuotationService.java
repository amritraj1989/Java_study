package com.mikealbert.service;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import com.mikealbert.data.entity.Doc;
import com.mikealbert.data.entity.EarlyTermQuote;
import com.mikealbert.data.entity.ExtAccAffiliate;
import com.mikealbert.data.entity.ExternalAccount;
import com.mikealbert.data.entity.Product;
import com.mikealbert.data.entity.Quotation;
import com.mikealbert.data.entity.QuotationDealerAccessory;
import com.mikealbert.data.entity.QuotationElement;
import com.mikealbert.data.entity.QuotationModel;
import com.mikealbert.data.entity.QuotationStepStructure;
import com.mikealbert.data.entity.QuoteRejectCode;
import com.mikealbert.data.enumeration.CorporateEntity;
import com.mikealbert.data.vo.ActiveQuoteVO;
import com.mikealbert.data.vo.CnbvVO;
import com.mikealbert.data.vo.QuotationSearchVO;
import com.mikealbert.data.vo.QuotationStepStructureVO;
import com.mikealbert.data.vo.QuoteOEVO;
import com.mikealbert.data.vo.RevisionVO;
import com.mikealbert.data.vo.WillowReportParamVO;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.exception.MalException;
import com.mikealbert.service.vo.FormalQuoteInput;


/**
* Public Interface implemented by {@link com.mikealbert.vision.service.QuotationServiceImpl} 
* for interacting with business service methods concerning {@link com.mikealbert.vision.entity.Quotation}(s) .
*
* @see com.mikealbert.vision.entity.Quotation
* @see com.mikealbert.vision.entity.QuotationModel
* @see com.mikealbert.vision.service.QuotationServiceImpl
*/
public interface QuotationService {
	
	public QuotationModel getOriginalQuoteModel(Long qmdId);
	public Long getOriginalQmdIdOnCurrentContract(Long qmdId);
	
	public Long getFinalizeQmd(long incomingQmdId);
	public boolean hasFinalizedQuote(long qmdId);
	public boolean isFinalizedQuote(long qmdId);
	
	public boolean isFormalExtension(QuotationModel qm);
	
	public boolean isQuotationForShortTerm(Long quoId)  throws MalBusinessException;
	public String getLeaseType(Long qmdId);
	public	boolean isStandardQuoteModel(Long qmdId) throws MalBusinessException ;
	public	Quotation getQuote(Long quoId) throws MalBusinessException;	
	public	QuotationModel getQuotationModel(Long qmdId) throws MalBusinessException;
	public	void updateQuotation(Quotation quotation) ;
	public	void updateQuotationModel(QuotationModel quotationModel) ;
	public	void updateQuotationElement(QuotationElement quotationElement) ;
	public	void updateQuotationElements(List<QuotationElement> quotationElements) ;
	public	void updateRecalcNeededFlag(Long qmdId, boolean flag) ;
	
	public	String  getQuoteProgramDescription(Long qmdId) throws MalBusinessException;	
	public Double getProfitability(Long qmdID, String profitType, String profitSource) throws MalBusinessException;
	public Double getMinimumProfit(Long qmdID, String profitType, String profitSource) throws MalBusinessException;
	public Double getFinanceParam(String  parameterkey, Long qmdId, Long qprId)throws MalBusinessException;
	public Double getFinanceParam(String  parameterkey, Long qmdId, Long qprId, Date effectiveDate)throws MalBusinessException;
	public Double getFinanceParam(String  parameterkey, Long qmdId, Long qprId, Date effectiveDate, boolean skipQuoteLvlCheck)throws MalBusinessException;
	public Double getFinanceParam(String  parameterkey, Long qmdId, Long qprId, Date effectiveDate, boolean skipQuoteLvlCheck, ExternalAccount extAcct)throws MalBusinessException;
	
	public BigDecimal getInterestRate(Long qmdId) throws MalBusinessException;	
	public QuotationElement getMainQuoteElement(Long qmdId);
	public BigDecimal getInterestRate(Long qmdId, Long qprId,Date effectiveDate,Long contractPeriod);
	public BigDecimal getGlobalInterestRate(Date effectiveDate, Long contractPeriod, String interestType);	
	public BigDecimal getInterestRateByType(Date effectiveDate, Long contractPeriod, String interestType, String reviewFrequency, String fixedOrFloat);
	public String getInterestType(Long qmdId ,Long lelId, Date revisionDate);// added lelid fro HD-215
	public Long getQmdIdFromUnitNo(String unitNo);
	public QuotationModel getQuotationModelFromUnitNo(String unitNo);
	public QuotationModel getQuotationModelWithCapitalCosts(Long qmdId)throws MalBusinessException;
	public QuotationModel getQuotationModelWithCostAndAccessories(Long qmdId)throws MalBusinessException;
	public BigDecimal getMafsAuthorizationLimit(Long corpId, String accountType, String accountCode, String unitNo) throws MalBusinessException;
	public BigDecimal getDriverAuthorizationLimit(Long corpId, String accountType, String accountCode, String unitNo) throws MalBusinessException;
	public Long	getDealerAcc(Long oldQmdId, Long newQmdId, Long newQdaId);
	public BigDecimal	getCustInvPrice(Long qmdId);
	public List<QuotationStepStructure> getQuotationModelStepStructure(Long qmdId) ;
	public boolean updateVrbDiscountValues(QuotationModel quotationModel);
	public Doc findMainPurchaseOrderByQmdIdAndStatus(Long qmdId, String docStatus);
	public ExternalAccount getDeliveringDealer(Long qmdId);
	public ExternalAccount getOrderingDealer(Long qmdId);
	public Long getQmdIdFromFmsId(Long fmsId, Date date);
	public Long createRevisedQuote(Long acceptedQmdId, String opCode);
	public Long getRevisedQmd(long acceptedQmd);
	public QuotationModel getAcceptedQuoteFromRevision(QuotationModel revisedQuoteModel) throws MalBusinessException;
	public boolean hasPriorAcceptedQuoteModel(long qmdId);
	
	public void saveOrUpdateQuotationDealerAccessory(QuotationDealerAccessory quotationDealerAccessory) throws MalBusinessException, Exception;
	public void deleteQuotationDealerAccessoryFromQuote(Long qmdId, QuotationDealerAccessory quotationDealerAccessory) throws MalBusinessException, Exception;
	public void saveDealerAccessories(List<QuotationDealerAccessory> quotationDealerAccessories) throws MalBusinessException, Exception;
	public Long getQuoteAccessoryLeadTimeByQdaId(Long qdaId);
	String getApplicableMilageProgram( Long qmdId);
	public QuotationModel getOriginalQuoteModelOnContractLine(Long qmdId) throws MalBusinessException;
	public String getClientRequestTypeValue(Long qmdId);
	public String acceptQuotation(Long qmdId, String loggedInUser) throws MalBusinessException;
	
	public List<String> getStandardAccessories(Long qmdId);
	public String getPlateTypeDescription(String plateTypeCode);
	public String getDriverGradeGroupDescription(String gradeGroupCode);
	public List<QuotationSearchVO> searchQuotations(CorporateEntity corporateEntity, String clientString, List<String> productTypeCodes, String unitNo, String vin6, Long projectedMonths, Pageable pageable , Sort sort) throws MalBusinessException;
	public int searchQuotationsCount(CorporateEntity corporateEntity, String clientString, List<String> productTypeCodes, String unitNo, String vin6, Long projectedMonths);
	public boolean isQuoteStatusExistsInQmd(String unitNo); 
	public List<ActiveQuoteVO> getAllActiveQuotesByFmsId(Long fmsId);
	public String getExcessMileBand(Long qmdId);
	public void updateQmdProjectedMonth(Long quoId, Long qmdId, Long projectedMonth) throws MalBusinessException;
	public boolean hasLeaseElementInQuote(QuotationModel quotationModel, String leaseElementName);
	public boolean hasLeaseElementInQuote(Long qmdId, String leaseElementName);
	
	public void saveOrUpdateAccountAffiliate(QuotationModel quoteModel, ExtAccAffiliate externalAccountAffiliate) throws MalBusinessException;
	public boolean hasBeenSumittedForAcceptance(QuotationModel quoteModel);

	public Long createConractRevisionQuote(Long accptedQmdId, String revisionType, String opUser) throws MalBusinessException;
	public boolean isQuoteEligibleForRevision(Long accptedQmdId, Long fmsId, String revisionType, String opUser) throws MalBusinessException;
	public Date getQuoteExpirationDate(Long cId, Date revisionEffectiveDate);
	public Date getContractEffectiveDate(Long cId);	
	public List<QuotationStepStructureVO> getCalculateQuotationStepStructure(List<QuotationStepStructure> quotationStepStructureList, QuotationModel quotationModel, 
			BigDecimal depreciationFactor, BigDecimal adminFactor, BigDecimal customerCost);
	public BigDecimal getRevisionInterestAdjustment(QuoteOEVO quoteOEVO, QuoteOEVO revisionOEQuoteVO, Product product, String isOEQuoteRevision) throws MalBusinessException;	
	public double monthsBetween(Calendar date1, Calendar date2);
	public BigDecimal malActualMonthsBetween(Date inputStartDate, Date inputEndDate);
	public List<RevisionVO> getQuotationModelsByQuoteStatus(Long qmdId, List<String> quoteStatusCodes);
	public EarlyTermQuote getEarlyTermQuote(Long etqId) throws MalBusinessException;
	public List<EarlyTermQuote> getUnacceptedEtQuotesByFmsId(Long fmsId);
	public void updateEarlyTermQuote(EarlyTermQuote etQuote);	
	public BigDecimal getBaseRate(QuotationModel quotationModel) throws MalException;
	public BigDecimal getProfileBaseRateByTerm(QuotationModel quotationModel, long term) throws MalException;
	public List<QuoteRejectCode> getAllQuoteRejectReasons();
	public void rejectQuoteWithReason(long qmdId, String rejectReason) throws MalBusinessException;
	
	public List<QuotationStepStructureVO> getAllQuoteSteps(Long qmdId) throws MalBusinessException;
	public BigDecimal getApplicableCapitalContribution(Long qmdId);
	public List<CnbvVO> getCnbv(Long qmdId);
	public List<CnbvVO> getCnbvForQuotationModel(Long LastestQmdId, Long anyOldQmdId);	
	public QuotationModel changeProfileOnQuotationModels(QuotationModel quotationModel);
	
	public Long generateQuoId();
	public Quotation createQuote(FormalQuoteInput formalQuoteInputVO);
	
	public String getWillowReportUrl(List <WillowReportParamVO> params) throws MalBusinessException;
	
	public byte[] getVLOReport(QuotationModel quotationModel) throws MalBusinessException;
	public byte[] getVQReport(QuotationModel quotationModel) throws MalBusinessException;
	public byte[] getVQSummaryReport(List<QuotationModel> quotationModelList) throws MalBusinessException;
	
	public List<String> validateVehicleQuotationBeforePrinting(QuotationModel quotationModel) throws MalBusinessException;
	public List<String> validateVehicleQuotationBeforePrinting(FormalQuoteInput formalQuoteInput) throws MalBusinessException;
	
	public BigDecimal getInterestRateByQuotationProfile(Long qprId, Date effectiveDate, Long term) throws MalBusinessException;
	
	public List<QuotationModel> getQuotationModels(Quotation quotation);
	public List<QuotationDealerAccessory> getQuotationDealerAccessoriesByQmdId(Long qmdId);
	public long getCountOfTermAndMiles(Long distance, Long hpdDistId);
	public BigDecimal getResidualAmount(QuotationDealerAccessory quotationDealerAccessory) throws Exception;
	
	public Long generateCfgId();	
	
	
	public static final int STATUS_ACCEPTED = 3;
	public static final int STATUS_ON_OFFER = 1;
	public static final int STATUS_ON_CONTRACT = 6;	
	public static final int STATUS_STANDARD_ORDER = 15;
	public static final int STATUS_ALLOCATED_TO_GRD = 16;
	public static final int STATUS_GRD_COMPLETE = 17;
	public static final int STATUS_STANDARD_ORDER_REVISION = 18;
	public static final int STATUS_CONTRACT_REVISION = 10;
	public static final int STATUS_REVISION = 4;
	public static final int STATUS_REJECTED = 8;
	//public static final int STATUS_STANDARD_ORDER_REJECTED = 8; //Its a wrong quote status code, 8 is status code for Rejected status
	public static final int STATUS_INACTIVE_STANDARD_ORDER_REVISION = 19;
	public static final int STATUS_AMENDED = 9;
	public static final String OPEN_END_LEASE = "OE";
	public static final String CLOSE_END_LEASE = "CE";	
	public static final String SHORT_TERM_PRODUCT_CODE =  "ST";
	public static final String MAX_FLEET_SERVICES = "MAX";
	public static final String DEMO_FLEET_SERVICES = "DEMO";
	public static final String FORMAL_EXT  = "Formal" ;
	
	public static final int TIME_OUT = 300000;

	
}
