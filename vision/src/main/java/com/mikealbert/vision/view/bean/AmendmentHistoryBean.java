package com.mikealbert.vision.view.bean;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.mikealbert.common.CommonCalculations;
import com.mikealbert.common.MalConstants;
import com.mikealbert.data.dao.ContractLineDAO;
import com.mikealbert.data.dao.ProductTypeCodeDAO;
import com.mikealbert.data.dao.QuotationModelDAO;
import com.mikealbert.data.entity.ContractLine;
import com.mikealbert.data.entity.FormulaParameter;
import com.mikealbert.data.entity.ProductTypeCode;
import com.mikealbert.data.entity.QuotationElement;
import com.mikealbert.data.entity.QuotationElementStep;
import com.mikealbert.data.entity.QuotationModel;
import com.mikealbert.data.entity.QuotationModelFinances;
import com.mikealbert.data.vo.FinanceParameterVO;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.service.FinanceParameterService;
import com.mikealbert.service.QuotationService;
import com.mikealbert.util.MALUtilities;
import com.mikealbert.vision.service.AmendmentHistoryService;
import com.mikealbert.vision.view.ViewConstants;
import com.mikealbert.vision.vo.AmendmentHistoryVO;
import com.mikealbert.vision.vo.EleAmendmentDetailVO;

@Component
@Scope("view")
public class AmendmentHistoryBean extends StatefulBaseBean {

	private static final long serialVersionUID = 1L;
	private Long incomingQmdId;
	private QuotationModel quotationModel;
	private String accountCode;
	private String accountName;
	private String quote;
	private String productDesc;
	private String unitNo;
	private String unitDescription;
	private Date contractStart;
	private Date contractEnd;
	private Long terms;
	private Long distance;
	private String costTreatment;
	private String interestTreatment;
	private String interestIndex;
	private String floatDescription;
	private final String COST_FIXED = "Fixed";
	private final String COST_VARIABLE = "Variable";
	public static final String ELEMENT_TYPE_MODEL = "MODEL";
	public static final String ELEMENT_TYPE_DEALER = "DEALER";
	public static final String ELEMENT_TYPE_SERVICE = "SERVICE";
	public static final String RECHARGED_TEXT = "Recharged";
	public static final String INFORMAL_TEXT = "Informal";
	private String quoteType;

	private List<AmendmentHistoryVO> amendmentHistoryList = new ArrayList<AmendmentHistoryVO>();
	private boolean invalidAccessPath = false;

	@Resource
	private AmendmentHistoryService amendmentHistoryService;

	@Resource
	private QuotationService quotationService;

	@Resource
	private ProductTypeCodeDAO productTypeCodeDAO;

	@Resource
	private ContractLineDAO contractLineDAO;

	@Resource
	private QuotationModelDAO quotationModelDAO;
	@Resource
	private FinanceParameterService financeParameterService;

	@Override
	protected void loadNewPage() {
		thisPage.setPageDisplayName("Amendment History");
		thisPage.setPageUrl(ViewConstants.AMENDMENT_HISTORY);
		if (thisPage.getInputValues().get(ViewConstants.VIEW_PARAM_QUOTE_MODEL_ID) != null) {
			incomingQmdId = (Long) thisPage.getInputValues().get(ViewConstants.VIEW_PARAM_QUOTE_MODEL_ID);
		} else {
			String paramQmdId = getRequestParameter("qmdId");
			if (paramQmdId != null) {
				incomingQmdId = Long.valueOf(paramQmdId);
			}

		}

	}

	@Override
	protected void restoreOldPage() {
		incomingQmdId = (Long) thisPage.getRestoreStateValues().get(ViewConstants.VIEW_PARAM_QUOTE_MODEL_ID);
	}

	public String cancel() {
		return super.cancelPage();
	}

	@PostConstruct
	public void init() {
		super.openPage();
		invalidAccessPath = false;
		try {
			if (incomingQmdId != null) {
				quotationModel = quotationService.getQuotationModel(incomingQmdId);
				loadData();
			} else {
				super.addErrorMessage("generic.error",
						"This link is not available through VISION directly. Please use Willow to get quote overview.");
				invalidAccessPath = true;
			}

		} catch (Exception ex) {
			logger.error(ex);
			handleException("generic.error.occured.while", new String[] { "page load" }, ex, null);
		}
	}

	private void loadData() throws MalBusinessException {
		if (quotationModel != null) {
			setAccountCode(quotationModel.getQuotation().getExternalAccount().getExternalAccountPK().getAccountCode());
			setAccountName(quotationModel.getQuotation().getExternalAccount().getAccountName());
			setQuote(Long.toString(quotationModel.getQuotation().getQuoId()) + "/"
					+ Long.toString(quotationModel.getQuoteNo()) + "/" + Long.toString(quotationModel.getRevisionNo()));
			String leaseType = quotationService.getLeaseType(incomingQmdId);
			if (leaseType != null) {
				ProductTypeCode productTypeCode = productTypeCodeDAO.findById(leaseType).orElse(null);
				quoteType = productTypeCode != null ? productTypeCode.getProductType() : null;
				setProductDesc(productTypeCode == null ? "" : productTypeCode.getDescription());
			}
			setUnitNo(quotationModel.getUnitNo());
			setUnitDescription(quotationModel.getModel().getModelDescription());

			setCostTreatment(quotationModel.getPreContractFixedCost().equals("F") ? COST_FIXED : COST_VARIABLE);
			setInterestTreatment(quotationModel.getPreContractFixedInterest().equals("F") ? COST_FIXED : COST_VARIABLE);
			setFloatDescription(quotationModel.getQuotation().getQuotationProfile().getVariableRate().equals("V") ? "Float"
					: "Non-Float");
			setInterestIndex(quotationModel.getQuotation().getQuotationProfile().getItcInterestType());
			setTerms(quotationModel.getContractPeriod());
			setDistance(quotationModel.getContractDistance());
			ContractLine	contractLine = null; 
			List<ContractLine> contractLines = contractLineDAO.findByQmdId(quotationModel.getQmdId());
			if (contractLines == null || contractLines.isEmpty()) {
				
			} else {
				if (contractLines.size() > 1) {
					for (ContractLine contractLine2 : contractLines) {
						if (contractLine2.getEarlyTermQuoteId() == null) {
							contractLine = contractLine2;
							break;
						}
					}
				} else {
					contractLine = contractLines.get(0);

				}
			}
			setContractStart(contractLine != null ? contractLine.getStartDate() : null);
			setContractEnd(contractLine != null ? contractLine.getEndDate() : null);
			setAmendmentHistoryList(amendmentHistoryService.getAmendedQuotesWithAmendmentDetail(incomingQmdId,true,true));
			
		}
	}

	
	public String viewQuoteOverview() {
		logger.debug("AmendmentHistoryBean:viewQuoteOverview:Start");
		Long selectedQmdId = Long.parseLong(getFaceExternalContext().getRequestParameterMap().get("selectedQmdId"));
		saveRestoreStateValues(getCurrentPageRestoreStateValuesMap());
		Map<String, Object> nextPageValues = new HashMap<String, Object>();
		nextPageValues.put(ViewConstants.VIEW_PARAM_QUOTE_MODEL_ID, selectedQmdId);
		saveNextPageInitStateValues(nextPageValues);
		logger.debug("AmendmentHistoryBean:viewQuoteOverview:End");
		String nextScreen = quoteType != null && quoteType.equals("CE") ? ViewConstants.QUOTE_OVERVIEW
				: quoteType != null && quoteType.equals("OE") ? ViewConstants.QUOTE_OVERVIEW_OE : null;
		return nextScreen;
	}

	private Map<String, Object> getCurrentPageRestoreStateValuesMap() {
		Map<String, Object> restoreStateValues = new HashMap<String, Object>();
		restoreStateValues.put(ViewConstants.VIEW_PARAM_QUOTE_MODEL_ID, incomingQmdId);
		return restoreStateValues;
	}

	public Long getIncomingQmdId() {
		return incomingQmdId;
	}

	public void setIncomingQmdId(Long incomingQmdId) {
		this.incomingQmdId = incomingQmdId;
	}

	public QuotationModel getQuotationModel() {
		return quotationModel;
	}

	public void setQuotationModel(QuotationModel quotationModel) {
		this.quotationModel = quotationModel;
	}

	public String getAccountCode() {
		return accountCode;
	}

	public void setAccountCode(String accountCode) {
		this.accountCode = accountCode;
	}

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public String getQuote() {
		return quote;
	}

	public void setQuote(String quote) {
		this.quote = quote;
	}

	public String getUnitNo() {
		return unitNo;
	}

	public void setUnitNo(String unitNo) {
		this.unitNo = unitNo;
	}

	public String getUnitDescription() {
		return unitDescription;
	}

	public void setUnitDescription(String unitDescription) {
		this.unitDescription = unitDescription;
	}

	public Date getContractStart() {
		return contractStart;
	}

	public void setContractStart(Date contractStart) {
		this.contractStart = contractStart;
	}

	public Date getContractEnd() {
		return contractEnd;
	}

	public void setContractEnd(Date contractEnd) {
		this.contractEnd = contractEnd;
	}

	public Long getTerms() {
		return terms;
	}

	public void setTerms(Long terms) {
		this.terms = terms;
	}

	public Long getDistance() {
		return distance;
	}

	public void setDistance(Long distance) {
		this.distance = distance;
	}

	public String getCostTreatment() {
		return costTreatment;
	}

	public void setCostTreatment(String costTreatment) {
		this.costTreatment = costTreatment;
	}

	public String getInterestTreatment() {
		return interestTreatment;
	}

	public void setInterestTreatment(String interestTreatment) {
		this.interestTreatment = interestTreatment;
	}

	public String getInterestIndex() {
		return interestIndex;
	}

	public void setInterestIndex(String interestIndex) {
		this.interestIndex = interestIndex;
	}

	public List<AmendmentHistoryVO> getAmendmentHistoryList() {
		return amendmentHistoryList;
	}

	public void setAmendmentHistoryList(List<AmendmentHistoryVO> amendmentHistoryList) {
		this.amendmentHistoryList = amendmentHistoryList;
	}

	public String getProductDesc() {
		return productDesc;
	}

	public void setProductDesc(String productDesc) {
		this.productDesc = productDesc;
	}

	public boolean isInvalidAccessPath() {
		return invalidAccessPath;
	}

	public void setInvalidAccessPath(boolean invalidAccessPath) {
		this.invalidAccessPath = invalidAccessPath;
	}

	public String getFloatDescription() {
		return floatDescription;
	}

	public void setFloatDescription(String floatDescription) {
		this.floatDescription = floatDescription;
	}

}
