package com.mikealbert.vision.view.bean;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.context.annotation.Scope;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.mikealbert.common.CommonCalculations;
import com.mikealbert.common.MalLogger;
import com.mikealbert.common.MalLoggerFactory;
import com.mikealbert.data.dao.DocDAO;
import com.mikealbert.data.dao.QuotationCapitalElementDAO;
import com.mikealbert.data.dao.QuotationModelDAO;
import com.mikealbert.data.dao.ReclaimHeaderDAO;
import com.mikealbert.data.dao.ReclaimLineDAO;
import com.mikealbert.data.entity.Doc;
import com.mikealbert.data.entity.ExternalAccount;
import com.mikealbert.data.entity.FleetMaster;
import com.mikealbert.data.entity.QuotationModel;
import com.mikealbert.data.entity.QuotationProfitability;
import com.mikealbert.data.entity.User;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.exception.MalException;
import com.mikealbert.service.CapitalCostElementService;
import com.mikealbert.service.CapitalCostService;
import com.mikealbert.service.FleetMasterService;
import com.mikealbert.service.ProfitabilityService;
import com.mikealbert.service.QuotationService;
import com.mikealbert.service.RentalCalculationService;
import com.mikealbert.service.vo.CapitalCostModeValuesVO;
import com.mikealbert.service.vo.QuoteCostElementVO;
import com.mikealbert.service.vo.QuoteVO;
import com.mikealbert.util.MALUtilities;
import com.mikealbert.vision.service.AmendmentHistoryService;
import com.mikealbert.vision.service.CapitalCostOverviewService;
import com.mikealbert.vision.service.InvoiceService;
import com.mikealbert.vision.view.ViewConstants;
import com.mikealbert.vision.vo.AmendmentHistoryVO;
import com.mikealbert.vision.vo.CapitalCostEquipmentRowVO;
import com.mikealbert.vision.vo.CapitalCostOverviewRowVO;
import com.mikealbert.vision.vo.EleAmendmentDetailVO;

@Component
@Scope("view")
public class CapitalCostsOverviewBean extends StatefulBaseBean {
	private static final long serialVersionUID = -8806821952041781659L;
	MalLogger logger = MalLoggerFactory.getLogger(this.getClass());
	
	@Resource CapitalCostElementService capitalCostElementService;
	@Resource CapitalCostService capitalCostService;
	@Resource QuotationService quotationService;
	@Resource FleetMasterService fleetMasterService;
	@Resource QuotationModelDAO quotationModelDAO;
	@Resource QuotationCapitalElementDAO quotationCapitalElementDAO;
	@Resource ReclaimHeaderDAO reclaimHeaderDAO;
	@Resource ReclaimLineDAO reclaimLineDAO;
	@Resource DocDAO docDAO;
	@Resource CapitalCostOverviewService capitalCostOverviewService;
	@Resource AmendmentHistoryService amendmentHistoryService;
	@Resource ProfitabilityService profitabilityService;
	@Resource RentalCalculationService	rentalCalculationService;
	@Resource InvoiceService invoiceService;


	
	private QuoteVO standardOrderQuote;
	private QuoteVO firstQuote;
	private QuoteVO finalizedQuote;
	private QuoteVO invoice;
	private Long incomingQmdId;
	private List<CapitalCostOverviewRowVO> rowList;
	private String unitNo;
	private QuotationModel quotationModel;
	private String accountCode;
	private String accountName;
	private String quoteType;
	private String screenUnitNo;
	private Boolean inquiry = false;
	private String standardQuoteInfo;
	private String firstQuoteInfo;
	private String finalizedQuoteInfo;
	private String modelDescription;
	private String orderingDealerCode;
	private String orderingDealerName;
	private String deliveringDealerCode;
	private String deliveringDealerName;
	private QuotationProfitability quotationProfitability;
	private BigDecimal standardOrderLeaseRate;
	private BigDecimal standardOrderGlobalMin;
	private BigDecimal firstLeaseRate;
	private BigDecimal firstGlobalMin;
	private BigDecimal finalizedLeaseRate;
	private BigDecimal finalizedGlobalMin;
	
	
	private List<CapitalCostEquipmentRowVO> factoryEquipmentDetailList = new ArrayList<CapitalCostEquipmentRowVO>();
	private List<CapitalCostEquipmentRowVO> afterMarketEquipmentDetailList = new ArrayList<CapitalCostEquipmentRowVO>();
	private List<AmendmentHistoryVO> amendmentHistoryList = new ArrayList<AmendmentHistoryVO>();
	List<QuoteCostElementVO> allCostElement = null;
	private Long	incomingQmdIdForHistory = null;

	public List<AmendmentHistoryVO> getAmendmentHistoryList() {
		return amendmentHistoryList;
	}
	public void setAmendmentHistoryList(
			List<AmendmentHistoryVO> amendmentHistoryList) {
		this.amendmentHistoryList = amendmentHistoryList;
	}
	@PostConstruct
	public void init() {
		super.openPage();
		initializeDataTable(440, 770, new int[] { 45, 16, 3, 8, 12});
		try {
			if (incomingQmdId != null) {
				incomingQmdIdForHistory	= incomingQmdId;
				initialize();				
				Long finalisedQmdId = quotationService.getFinalizeQmd(incomingQmdId);
				if (finalisedQmdId != null ) {
					incomingQmdId = finalisedQmdId;
				}
				
				quotationModel = quotationService.getQuotationModelWithCostAndAccessories(incomingQmdId);
			
				if (quotationModel != null) {
					allCostElement = capitalCostElementService.getCapitalCostElementList(incomingQmdId);
					accountCode = quotationModel.getQuotation().getExternalAccount().getExternalAccountPK().getAccountCode();
					accountName = quotationModel.getQuotation().getExternalAccount().getAccountName();
					quoteType = quotationService.getLeaseType(quotationModel.getQmdId());
					if (quotationModel.getModel() != null) {
						modelDescription = quotationModel.getModel().getModelDescription();
					}
					
				} else {
					super.addErrorMessage("notfound", "quote");
				}
				
				loadData();
				loadAmendmentData();
				
			}
		} catch (Exception ex) {
			logger.error(ex);
			super.addErrorMessage("generic.error", ex.getMessage());
		}
	}
	private void loadAmendmentData() throws MalBusinessException{
		amendmentHistoryList = amendmentHistoryService.getAmendedQuotesWithAmendmentDetail(incomingQmdIdForHistory, true, false);
		for (AmendmentHistoryVO amendmentHistoryVO : amendmentHistoryList) {
			if(amendmentHistoryVO.getAfterMarketEquipments() == null ||  amendmentHistoryVO.getAfterMarketEquipments().isEmpty()){
				amendmentHistoryVO.getAfterMarketEquipments().add(new EleAmendmentDetailVO());
				amendmentHistoryVO.setNoChangeInfo("No equipment changes");
			}
		}
		
	}
	public void viewServiceElements() {
		logger.debug("CapitalCostOverviewBean:viewServiceElements:Start");
		Long selectedQmdId = Long.parseLong(getFaceExternalContext().getRequestParameterMap().get("selectedQmdId"));
		saveRestoreStateValues(getCurrentPageRestoreStateValuesMap());
		Map<String, Object> nextPageValues = new HashMap<String, Object>();
		nextPageValues.put(ViewConstants.VIEW_PARAM_QUOTE_MODEL_ID, selectedQmdId);
		saveNextPageInitStateValues(nextPageValues);
		logger.debug("CapitalCostOverviewBean:viewServiceElements:End");
		forwardToURL(ViewConstants.SERVICE_ELEMENTS);
	}
	private Map<String, Object> getCurrentPageRestoreStateValuesMap() {
		Map<String, Object> restoreStateValues = new HashMap<String, Object>();
		restoreStateValues.put(ViewConstants.VIEW_PARAM_QUOTE_MODEL_ID, incomingQmdId);
		return restoreStateValues;
	}

	@Override
	protected void loadNewPage() {

		if (thisPage.getInputValues().get(ViewConstants.VIEW_PARAM_QUOTE_MODEL_ID) != null) {
			incomingQmdId = (Long) thisPage.getInputValues().get(ViewConstants.VIEW_PARAM_QUOTE_MODEL_ID);			
			thisPage.setPageDisplayName("Capital Cost Overview");
		} else {
			inquiry = true;
			thisPage.setPageDisplayName("Capital Cost Inquiry");
		}
		thisPage.setPageUrl(ViewConstants.CAPITAL_COST_OVERVIEW);

	}

	@Override
	protected void restoreOldPage() {
		incomingQmdId = (Long) thisPage.getRestoreStateValues().get(ViewConstants.VIEW_PARAM_QUOTE_MODEL_ID);
	}

	
	
	public String cancel() {
		return super.cancelPage();
	}

	public void loadScreen() {
		if (MALUtilities.isEmptyString(screenUnitNo)) {
			super.addErrorMessage("required.field", "Unit No");
		} else {
			FleetMaster fleetMaster = fleetMasterService.findByUnitNo(screenUnitNo);
			if (fleetMaster == null) {
				super.addErrorMessage("notfound", "Unit");
			} else {
				incomingQmdId = quotationService.getQmdIdFromUnitNo(screenUnitNo);
				if (incomingQmdId != null) {
					loadData();
				} else {
					super.addErrorMessage("not_on_contract", "Unit");
				}
			}
		}
	}

	public void loadData() {

		try {

			loadDealers();

			CapitalCostModeValuesVO capitalCostModeValuesVO = capitalCostService.getModeValues(quotationModel);
			String mode = capitalCostModeValuesVO.getMode();
			QuotationModel  firstQuoteModel = capitalCostModeValuesVO.getFirstQuoteModel();
			QuotationModel finalizedQuoteModel = capitalCostModeValuesVO.getFinalQuoteModel();
			QuotationModel standardOrderQuoteModel = capitalCostModeValuesVO.getStandardOrderQuoteModel();
			
			Boolean isStockOrder = capitalCostModeValuesVO.getIsStockOrder();
			unitNo = capitalCostModeValuesVO.getUnitNo();

			if (mode.equals(CapitalCostService.STANDARD_ORDER_MODE)) {
				;
			} else if (mode.equals(CapitalCostService.FIRST_MODE)) {
				firstQuote = capitalCostService.getQuoteCapitalCosts(firstQuoteModel, false, null, isStockOrder);
				
			} else if (mode.equals(CapitalCostService.FINALIZED_MODE)) {
				firstQuote = capitalCostService.getQuoteCapitalCosts(firstQuoteModel, false, null, isStockOrder);
				finalizedQuote = capitalCostService.getQuoteCapitalCosts(finalizedQuoteModel, true, firstQuote, isStockOrder);
			} else {
				super.addErrorMessage("unknown_mode_of_inquiry");
			}

			if (firstQuote != null) {
				firstQuoteInfo = String.valueOf(firstQuote.getQuotationModel().getQuotation().getQuoId())
						+ "/"
						+ firstQuote.getQuotationModel().getQuoteNo()
						+ "/" + firstQuote.getQuotationModel().getRevisionNo();
				
				quotationProfitability = profitabilityService.getQuotationProfitability(firstQuote.getQuotationModel());
				if(quotationProfitability != null){
					firstGlobalMin = CommonCalculations.getRoundedValue(quotationProfitability.getProfitBase(),3);
				}
				firstLeaseRate = getLeaseRate(firstQuote.getQuotationModel());
				Long originalQmdId = quotationModelDAO.getOriginalQuoteModelId(firstQuote.getQuotationModel().getQmdId());
				if (originalQmdId != null && !isStockOrder) {
					if (quotationService.isStandardQuoteModel(originalQmdId)) {
						standardOrderQuoteModel = quotationService.getQuotationModelWithCostAndAccessories(originalQmdId);
					}
				}
			}
			if (standardOrderQuoteModel != null) {
				standardOrderQuote = capitalCostService.getQuoteCapitalCosts(standardOrderQuoteModel, false, null, isStockOrder);
				standardQuoteInfo = String.valueOf(standardOrderQuote.getQuotationModel().getQuotation().getQuoId())
						+ "/"
						+ standardOrderQuote.getQuotationModel().getQuoteNo()
						+ "/"
						+ standardOrderQuote.getQuotationModel().getRevisionNo();
				
				quotationProfitability = profitabilityService.getQuotationProfitability(standardOrderQuote.getQuotationModel());
				if(quotationProfitability != null){
					standardOrderGlobalMin = CommonCalculations.getRoundedValue(quotationProfitability.getProfitBase(),3);;
				}
				standardOrderLeaseRate = getLeaseRate(standardOrderQuote.getQuotationModel());
			}
			if (finalizedQuoteModel != null) {
				finalizedQuote = capitalCostService.populatePODetails(finalizedQuoteModel, finalizedQuote);
				invoice = capitalCostService.getInvoiceCapitalCosts(finalizedQuoteModel,allCostElement,isStockOrder, true);
				invoice =  capitalCostService.populateReclaimCosts(firstQuoteModel,finalizedQuoteModel, invoice) ;
				invoice.setInvoiceQmdId(finalizedQuoteModel.getQmdId());				
				finalizedQuoteInfo = String.valueOf(finalizedQuote.getQuotationModel().getQuotation().getQuoId())
						+ "/"
						+ finalizedQuote.getQuotationModel().getQuoteNo()
						+ "/"
						+ finalizedQuote.getQuotationModel().getRevisionNo();
				quotationProfitability = profitabilityService.getQuotationProfitability(finalizedQuote.getQuotationModel());
				if(quotationProfitability != null){
					finalizedGlobalMin = CommonCalculations.getRoundedValue(quotationProfitability.getProfitBase(),3);
				}
				finalizedLeaseRate = getLeaseRate(finalizedQuote.getQuotationModel());
			} else if (firstQuoteModel != null) {
				firstQuote = capitalCostService.populatePODetails(firstQuoteModel, firstQuote);
				invoice = capitalCostService.getInvoiceCapitalCosts(firstQuoteModel,allCostElement,	isStockOrder, true);
				invoice =  capitalCostService.populateReclaimCosts(firstQuoteModel,null, invoice) ;
				invoice.setInvoiceQmdId(firstQuoteModel.getQmdId());
			}

			loadRows();

		} catch (Exception e) {
			logger.error(e);
			if (e instanceof MalBusinessException) {
				super.addErrorMessage(e.getMessage());
			} else {
				super.addErrorMessage("generic.error.occured.while",
						" building capital cost overview");
			}
		}
	}

	private void initialize() {
		standardOrderQuote = null;
		firstQuote = null;
		finalizedQuote = null;
		invoice = null;
		quotationModel = null;
		accountCode = null;
		accountName = null;
		quoteType = null;
		standardQuoteInfo = null;
		firstQuoteInfo = null;
		finalizedQuoteInfo = null;
		modelDescription = null;
		unitNo = null;
		rowList = new ArrayList<CapitalCostOverviewRowVO>();
	}

	private void loadRows() throws MalBusinessException {
		int lastGroupOrder = 1;
		BigDecimal cost;
		BigDecimal standardOrderDealSum = BigDecimal.ZERO;;
		BigDecimal standardOrderCustomerSum = BigDecimal.ZERO;;
		BigDecimal firstQuoteDealSum = BigDecimal.ZERO;;
		BigDecimal firstQuoteCustomerSum = BigDecimal.ZERO;;
		BigDecimal invoiceSum = BigDecimal.ZERO;;
		BigDecimal rechargeAmtSum = BigDecimal.ZERO;
		BigDecimal finalizedQuoteDealSum = BigDecimal.ZERO;;
		BigDecimal finalizedQuoteCustomerSum = BigDecimal.ZERO;;
		Boolean hiddenCost = false;
		String lastGroupName = null;
		CapitalCostOverviewRowVO row;
		
		for (QuoteCostElementVO costElement : allCostElement) {
			row = new CapitalCostOverviewRowVO();
			hiddenCost = false;
			row.setHasReclaim(false);
			if (lastGroupName == null) {
				lastGroupName = costElement.getElementGroupName();
			}
			if (costElement.getElementGroupOrder() != lastGroupOrder) {
				row.setName(lastGroupName + ":");
				row.setFooter(true);
				lastGroupOrder = costElement.getElementGroupOrder();
				lastGroupName = costElement.getElementGroupName();
				row.setStandardOrderDealCost(standardOrderDealSum);
				row.setStandardOrderCustomerCost(standardOrderCustomerSum);
				row.setFirstQuoteDealCost(firstQuoteDealSum);
				row.setFirstQuoteCustomerCost(firstQuoteCustomerSum);
				row.setInvoiceCost(invoiceSum);
				row.setRechargeAmt(rechargeAmtSum);				
				row.setFinalizedQuoteDealCost(finalizedQuoteDealSum);
				row.setFinalizedQuoteCustomerCost(finalizedQuoteCustomerSum);
				rowList.add(row);
				row = new CapitalCostOverviewRowVO();
			}
			row.setName(costElement.getElementName());
			row.setAccessoryData(costElement.isModelAccessories() || costElement.isDealerAccessories());
			row.setType(costElement.getElementType());
			// if(costElement.getQuoteConcealed() != null &&
			// costElement.getQuoteConcealed().equals("Y")) {
			// hiddenCost = true;
			// }
			hiddenCost = false;

			if (!hiddenCost) {
				if (standardOrderQuote != null) {
					cost = getDealCostFromQuote(standardOrderQuote, costElement);
					row.setStandardOrderDealCost(cost);
					standardOrderDealSum = standardOrderDealSum.add(cost);
					cost = getCustomerCostFromQuote(standardOrderQuote,costElement);
					row.setStandardOrderCustomerCost(cost);
					standardOrderCustomerSum = standardOrderCustomerSum.add(cost);
				}
				if (firstQuote != null) {
					cost = getDealCostFromQuote(firstQuote, costElement);
					row.setFirstQuoteDealCost(cost);
					// For RC-32
					row.setRechargeAmt(getRechargeAmtFromQuote(firstQuote,costElement));
					if (row.getRechargeAmt() != null)
						rechargeAmtSum = rechargeAmtSum.add(row.getRechargeAmt());
					firstQuoteDealSum = firstQuoteDealSum.add(cost);
					cost = getCustomerCostFromQuote(firstQuote, costElement);
					row.setFirstQuoteCustomerCost(cost);
					setPODetails( row,  firstQuote);
					firstQuoteCustomerSum = firstQuoteCustomerSum.add(cost);
				}
				if (finalizedQuote != null) {
					cost = getDealCostFromQuote(finalizedQuote, costElement);
					row.setFinalizedQuoteDealCost(cost);
					finalizedQuoteDealSum = finalizedQuoteDealSum.add(cost);
					cost = getCustomerCostFromQuote(finalizedQuote, costElement);
					row.setFinalizedQuoteCustomerCost(cost);
					setPODetails( row,  finalizedQuote);
					finalizedQuoteCustomerSum = finalizedQuoteCustomerSum.add(cost);
					
				}
			}

			if (invoice != null) {
				
				cost = getDealCostFromQuote(invoice, costElement);
				
				row.setHasReclaim(isReclaim(invoice, costElement));				
				row.setInvoiceCost(cost);
				if(cost != null)
				invoiceSum = invoiceSum.add(cost);
			}
			
			if(row.getIsAccessoryData() ||  finalizedQuote ==  null ){
				rowList.add(row);
			}else if(!((row.getStandardOrderDealCost() == null || row.getStandardOrderDealCost().compareTo(BigDecimal.ZERO)  == 0)
					&& (row.getStandardOrderDealCost() == null || row.getStandardOrderCustomerCost().compareTo(BigDecimal.ZERO) == 0)
					&& (row.getFirstQuoteDealCost() == null || row.getFirstQuoteDealCost().compareTo(BigDecimal.ZERO) == 0)
					&& (row.getFirstQuoteCustomerCost() == null || row.getFirstQuoteCustomerCost().compareTo(BigDecimal.ZERO) == 0)
					&& (row.getRechargeAmt() == null || row.getRechargeAmt().compareTo(BigDecimal.ZERO) == 0)
					&& (row.getInvoiceCost() == null || row.getInvoiceCost().compareTo(BigDecimal.ZERO) == 0	)			
					&& (row.getFinalizedQuoteDealCost() == null || row.getFinalizedQuoteDealCost().compareTo(BigDecimal.ZERO) == 0)
					&& (row.getFinalizedQuoteCustomerCost() == null || row.getFinalizedQuoteCustomerCost().compareTo(BigDecimal.ZERO) == 0))){
			
				rowList.add(row);
			}
		}
		
		row = new CapitalCostOverviewRowVO();
		row.setName(lastGroupName + ":");
		row.setFooter(true);
		row.setStandardOrderDealCost(standardOrderDealSum);
		row.setStandardOrderCustomerCost(standardOrderCustomerSum);
		row.setFirstQuoteDealCost(firstQuoteDealSum);
		row.setFirstQuoteCustomerCost(firstQuoteCustomerSum);
		row.setInvoiceCost(invoiceSum);
		row.setRechargeAmt(rechargeAmtSum);
		row.setFinalizedQuoteDealCost(finalizedQuoteDealSum);
		row.setFinalizedQuoteCustomerCost(finalizedQuoteCustomerSum);

		rowList.add(row);
	}

	private void setPODetails(CapitalCostOverviewRowVO row, QuoteVO quote){
		for (QuoteCostElementVO element : quote.getCostElements()) {
			if (element.getElementName().equals(row.getName())) {
				row.setPoNumber(element.getPoNumber());
				row.setPoOrderDate(element.getPoOrderDate());
				row.setAccountInfo(element.getAccountInfo());
			}
		}
	}

	private BigDecimal getDealCostFromQuote(QuoteVO quote,QuoteCostElementVO costElement) {

		for (QuoteCostElementVO element : quote.getCostElements()) {
			if (element.getElementName().equals(costElement.getElementName())) {
				if(element.getDealCost() != null)
				return element.getDealCost().compareTo(BigDecimal.ZERO)== 0 ? BigDecimal.ZERO:element.getDealCost();
			}
		}
		return BigDecimal.ZERO;
	}
	
	private boolean isReclaim(QuoteVO quote,QuoteCostElementVO costElement) {

		for (QuoteCostElementVO element : quote.getCostElements()) {
			if (element.getElementName().equals(costElement.getElementName())) {
				return element.isReclaim();
			}
		}
		return false;
	}

	private BigDecimal getRechargeAmtFromQuote(QuoteVO quote,QuoteCostElementVO costElement) {

		for (QuoteCostElementVO element : quote.getCostElements()) {
			if (element.getElementName().equals(costElement.getElementName())) {
				return element.getRechargeAmt();
			}
		}
		return BigDecimal.ZERO;
	}

	private BigDecimal getCustomerCostFromQuote(QuoteVO quote, QuoteCostElementVO costElement) {

		for (QuoteCostElementVO element : quote.getCostElements()) {
			if (element.getElementName().equals(costElement.getElementName())) {
				if(element.getCustomerCost() != null)
				return element.getCustomerCost().compareTo(BigDecimal.ZERO)== 0 ? BigDecimal.ZERO:element.getCustomerCost();
			}
		}
		return BigDecimal.ZERO;
	}

	private void loadDealers() {
		ExternalAccount orderingDealer = quotationService.getOrderingDealer(incomingQmdId);
		if(orderingDealer != null) {
			orderingDealerCode = orderingDealer.getExternalAccountPK().getAccountCode();
			orderingDealerName = orderingDealer.getAccountName();
		}
		ExternalAccount deliveringDealer = quotationService.getDeliveringDealer(incomingQmdId);
		if(deliveringDealer != null) {
			deliveringDealerCode = deliveringDealer.getExternalAccountPK().getAccountCode();
			deliveringDealerName = deliveringDealer.getAccountName();
		}
	}
	
	private BigDecimal getLeaseRate(QuotationModel quotationModel) {
		BigDecimal leaseRate = BigDecimal.ZERO;
		String leaseType = quotationService.getLeaseType(quotationModel.getQmdId());		
		if (leaseType.equals(QuotationService.CLOSE_END_LEASE)) {
			leaseRate = rentalCalculationService.getFinanceLeaseElementCostForCE(quotationModel);
		} else if (leaseType.equals(QuotationService.OPEN_END_LEASE)) {
			leaseRate	= amendmentHistoryService.getLeaseRateOE(quotationModel);
		}
		return leaseRate;
	}
	
	 public User getLoggedInUser(){
	    	
	    	User user = null;
	    	Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			if(authentication != null && authentication.getPrincipal() != null && authentication.getPrincipal() instanceof User)		{
				user = ((User)authentication.getPrincipal());
			}
			return user;
	    }
	 
	public List<CapitalCostOverviewRowVO> getRowList() {
		return rowList;
	}

	public void setRowList(List<CapitalCostOverviewRowVO> rowList) {
		this.rowList = rowList;
	}

	public List<CapitalCostEquipmentRowVO> getFactoryEquipmentDetailList() {
		return factoryEquipmentDetailList;
	}

	public void setFactoryEquipmentDetailList(
			List<CapitalCostEquipmentRowVO> factoryEquipmentDetailList) {
		this.factoryEquipmentDetailList = factoryEquipmentDetailList;
	}

	public List<CapitalCostEquipmentRowVO> getAfterMarketEquipmentDetailList() {
		return afterMarketEquipmentDetailList;
	}

	public void setAfterMarketEquipmentDetailList(
			List<CapitalCostEquipmentRowVO> afterMarketEquipmentDetailList) {
		this.afterMarketEquipmentDetailList = afterMarketEquipmentDetailList;
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

	public String getQuoteType() {
		return quoteType;
	}

	public void setQuoteType(String quoteType) {
		this.quoteType = quoteType;
	}

	public String getUnitNo() {
		return unitNo;
	}

	public void setUnitNo(String unitNo) {
		this.unitNo = unitNo;
	}

	public String getScreenUnitNo() {
		return screenUnitNo;
	}

	public void setScreenUnitNo(String screenUnitNo) {
		this.screenUnitNo = screenUnitNo;
	}

	public Boolean getInquiry() {
		return inquiry;
	}

	public void setInquiry(Boolean inquiry) {
		this.inquiry = inquiry;
	}

	public String getStandardQuoteInfo() {
		return standardQuoteInfo;
	}

	public void setStandardQuoteInfo(String standardQuoteInfo) {
		this.standardQuoteInfo = standardQuoteInfo;
	}

	public String getFirstQuoteInfo() {
		return firstQuoteInfo;
	}

	public void setFirstQuoteInfo(String firstQuoteInfo) {
		this.firstQuoteInfo = firstQuoteInfo;
	}

	public String getModelDescription() {
		return modelDescription;
	}

	public void setModelDescription(String modelDescription) {
		this.modelDescription = modelDescription;
	}

	public String getFinalizedQuoteInfo() {
		return finalizedQuoteInfo;
	}

	public void setFinalizedQuoteInfo(String finalizedQuoteInfo) {
		this.finalizedQuoteInfo = finalizedQuoteInfo;
	}
	
	public String getOrderingDealerCode() {
		return orderingDealerCode;
	}

	public void setOrderingDealerCode(String orderingDealerCode) {
		this.orderingDealerCode = orderingDealerCode;
	}

	public String getOrderingDealerName() {
		return orderingDealerName;
	}

	public void setOrderingDealerName(String orderingDealerName) {
		this.orderingDealerName = orderingDealerName;
	}

	public String getDeliveringDealerCode() {
		return deliveringDealerCode;
	}

	public void setDeliveringDealerCode(String deliveringDealerCode) {
		this.deliveringDealerCode = deliveringDealerCode;
	}

	public String getDeliveringDealerName() {
		return deliveringDealerName;
	}

	public void setDeliveringDealerName(String deliveringDealerName) {
		this.deliveringDealerName = deliveringDealerName;
	}
	public QuoteVO getStandardOrderQuote() {
		return standardOrderQuote;
	}
	public void setStandardOrderQuote(QuoteVO standardOrderQuote) {
		this.standardOrderQuote = standardOrderQuote;
	}
	public QuoteVO getFirstQuote() {
		return firstQuote;
	}
	public void setFirstQuote(QuoteVO firstQuote) {
		this.firstQuote = firstQuote;
	}
	public QuoteVO getFinalizedQuote() {
		return finalizedQuote;
	}
	public void setFinalizedQuote(QuoteVO finalizedQuote) {
		this.finalizedQuote = finalizedQuote;
	}
	public BigDecimal getStandardOrderLeaseRate() {
		return standardOrderLeaseRate;
	}
	public void setStandardOrderLeaseRate(BigDecimal standardOrderLeaseRate) {
		this.standardOrderLeaseRate = standardOrderLeaseRate;
	}
	public BigDecimal getStandardOrderGlobalMin() {
		return standardOrderGlobalMin;
	}
	public void setStandardOrderGlobalMin(BigDecimal standardOrderGlobalMin) {
		this.standardOrderGlobalMin = standardOrderGlobalMin;
	}
	public BigDecimal getFirstLeaseRate() {
		return firstLeaseRate;
	}
	public void setFirstLeaseRate(BigDecimal firstLeaseRate) {
		this.firstLeaseRate = firstLeaseRate;
	}
	public BigDecimal getFirstGlobalMin() {
		return firstGlobalMin;
	}
	public void setFirstGlobalMin(BigDecimal firstGlobalMin) {
		this.firstGlobalMin = firstGlobalMin;
	}
	public BigDecimal getFinalizedLeaseRate() {
		return finalizedLeaseRate;
	}
	public void setFinalizedLeaseRate(BigDecimal finalizedLeaseRate) {
		this.finalizedLeaseRate = finalizedLeaseRate;
	}
	public BigDecimal getFinalizedGlobalMin() {
		return finalizedGlobalMin;
	}
	public void setFinalizedGlobalMin(BigDecimal finalizedGlobalMin) {
		this.finalizedGlobalMin = finalizedGlobalMin;
	}

}