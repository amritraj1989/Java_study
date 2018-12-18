package com.mikealbert.vision.view.bean;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Component;

import com.mikealbert.common.CommonCalculations;
import com.mikealbert.common.MalConstants;
import com.mikealbert.common.MalLogger;
import com.mikealbert.common.MalLoggerFactory;
import com.mikealbert.data.dao.ContractLineDAO;
import com.mikealbert.data.dao.DocDAO;
import com.mikealbert.data.dao.DocLinkDAO;
import com.mikealbert.data.dao.DoclDAO;
import com.mikealbert.data.dao.ProductDAO;
import com.mikealbert.data.dao.QuotationCapitalElementDAO;
import com.mikealbert.data.dao.QuotationModelDAO;
import com.mikealbert.data.dao.ReclaimHeaderDAO;
import com.mikealbert.data.dao.ReclaimLineDAO;
import com.mikealbert.data.entity.ContractLine;
import com.mikealbert.data.entity.Doc;
import com.mikealbert.data.entity.DocLink;
import com.mikealbert.data.entity.DriverAddress;
import com.mikealbert.data.entity.ExternalAccount;
import com.mikealbert.data.entity.FleetMaster;
import com.mikealbert.data.entity.Product;
import com.mikealbert.data.entity.QuotationDealerAccessory;
import com.mikealbert.data.entity.QuotationElement;
import com.mikealbert.data.entity.QuotationModel;
import com.mikealbert.data.entity.QuotationModelAccessory;
import com.mikealbert.data.entity.QuotationProfitability;
import com.mikealbert.data.entity.QuotationStepStructure;
import com.mikealbert.data.entity.ReclaimLines;
import com.mikealbert.data.entity.SupplierProgressHistory;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.service.CapitalCostElementService;
import com.mikealbert.service.CapitalCostService;
import com.mikealbert.service.CapitalElementService;
import com.mikealbert.service.ContractService;
import com.mikealbert.service.DriverService;
import com.mikealbert.service.FleetMasterService;
import com.mikealbert.service.ProfitabilityService;
import com.mikealbert.service.QuotationElementService;
import com.mikealbert.service.QuotationService;
import com.mikealbert.service.RentalCalculationService;
import com.mikealbert.service.enumeration.NonCapitalElementEnum;
import com.mikealbert.service.vo.CapitalCostModeValuesVO;
import com.mikealbert.data.vo.QuotationStepStructureVO;
import com.mikealbert.service.vo.QuoteCostElementVO;
import com.mikealbert.service.vo.QuoteVO;
import com.mikealbert.util.MALUtilities;
import com.mikealbert.vision.service.AmendmentHistoryService;
import com.mikealbert.vision.service.CapitalCostOverviewService;
import com.mikealbert.vision.service.InvoiceEntryService;
import com.mikealbert.vision.service.InvoiceService;
import com.mikealbert.vision.service.SupplierService;
import com.mikealbert.vision.view.ViewConstants;
import com.mikealbert.vision.vo.AmendmentHistoryVO;
import com.mikealbert.vision.vo.CapitalCostEquipmentRowVO;
import com.mikealbert.vision.vo.CapitalCostsRowVO;
import com.mikealbert.vision.vo.EleAmendmentDetailVO;
import com.mikealbert.vision.vo.VehicleTimelineEventVO;

@Component
@Scope("view")
public class CapitalCostsBean extends StatefulBaseBean {
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
	@Resource DoclDAO doclDAO;
	@Resource CapitalCostOverviewService capitalCostOverviewService;
	@Resource AmendmentHistoryService amendmentHistoryService;
	@Resource ProfitabilityService profitabilityService;
	@Resource RentalCalculationService	rentalCalculationService;
	@Resource DocLinkDAO docLinkDAO;
	@Resource  InvoiceEntryService invoiceEntryService;
	@Resource DriverService driverService;
	@Resource ContractService contractService;
	@Resource SupplierService supplierService;
	@Resource ContractLineDAO contractLineDAO;
	@Resource CapitalElementService capitalElementService;
	@Resource ProductDAO productDAO;	
	@Resource InvoiceService invoiceService;
	@Resource QuotationElementService quotationElementService;

	private QuoteVO standardOrderQuote;
	private QuoteVO firstQuote;
	private QuoteVO finalizedQuote;
	private QuoteVO invoice;
	private Long incomingQmdId;
	private List<CapitalCostsRowVO> rowList;
	private String unitNo;
	private QuotationModel quotationModel;
	private String accountCode;
	private String accountName;
	private String quoteType;
	private String screenUnitNo;
	private String capitalCostCalcType;
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
	private BigDecimal standardOrderIrr;
	private BigDecimal firstLeaseRate;
	private BigDecimal firstGlobalMin;
	private BigDecimal firstQuoteIrr;
	private BigDecimal finalizedLeaseRate;
	private BigDecimal finalizedGlobalMin;
	private BigDecimal finalizedQuoteIrr;
	
	private String targetPONumber;
	private boolean editMode = false;
	private boolean finalizeMode = false;
	private boolean editInvoice = false;
	private String mode;
	private boolean disableSave = true;
	private boolean disablePost = true;
	private boolean disableCalculate = true;
	private boolean fixedDealFlag = false;
	private String mainPONumber;
	
	private Long invoiceHeaderId;
	private boolean userConfirmation = false;
	private boolean showWarning = false;
	private String warningMessage;
	private BigDecimal	invoiceHeaderTotal= null;
	private BigDecimal	invoiceDetailTotal= null;
	private boolean		invoiveAmountMismatch = false;
	private String	productName ;
	private String productCode;

	private List<CapitalCostEquipmentRowVO> factoryEquipmentDetailList = new ArrayList<CapitalCostEquipmentRowVO>();
	private List<CapitalCostEquipmentRowVO> afterMarketEquipmentDetailList = new ArrayList<CapitalCostEquipmentRowVO>();
	private List<AmendmentHistoryVO> amendmentHistoryList = new ArrayList<AmendmentHistoryVO>();
	private List<QuoteCostElementVO> allCostElement = null;
	private Long	incomingQmdIdForHistory = null;
	
	private boolean canadaUnit;
	
	private List<QuotationStepStructureVO> quoteSteps	=	null; 
	private	BigDecimal	newFinalNBV	= BigDecimal.ZERO;
	private BigDecimal customerCostFinalized = BigDecimal.ZERO;
	private BigDecimal mikealbertCostFinalized = BigDecimal.ZERO;
	private BigDecimal depreciationFinalized = BigDecimal.ZERO;
	
	private	BigDecimal	standardQuoteResidual	= BigDecimal.ZERO;
	private	BigDecimal	acceptedQuoteResidual	= BigDecimal.ZERO;
	private	BigDecimal	finalizeQuoteResidual	= BigDecimal.ZERO;
	private	String	partRechargeCapEle;
	private List<VehicleTimelineEventVO> vehicleTimeline; 
	private QuotationModel calcQuotationModel;	

	@PostConstruct
	public void init() {

		super.openPage();

		initializeDataTable(440, 770, new int[] { 30, 16, 3, 8, 13,21});
		try {
			if (incomingQmdId != null) {
				incomingQmdIdForHistory	= incomingQmdId;
				initialize();
				Long finalisedQmdId = quotationService.getFinalizeQmd(incomingQmdId);
				if (finalisedQmdId != null ) {
					incomingQmdId = finalisedQmdId;
				}

				quotationModel = quotationService.getQuotationModelWithCostAndAccessories(incomingQmdId);
				productCode = quotationModel.getQuotation().getQuotationProfile().getPrdProductCode();
				Product	product	= productDAO.findById(productCode).orElse(null);
				setProductName(product.getProductName());
				if (quotationModel != null) {
					allCostElement = capitalCostElementService.getCapitalCostElementList(incomingQmdId);
					accountCode = quotationModel.getQuotation().getExternalAccount().getExternalAccountPK().getAccountCode();
					accountName = quotationModel.getQuotation().getExternalAccount().getAccountName();
					quoteType = quotationService.getLeaseType(quotationModel.getQmdId());
					capitalCostCalcType = capitalCostService.findQuoteCapitalCostsCalcType(quotationModel);
					if (quotationModel.getModel() != null) {
						modelDescription = quotationModel.getModel().getModelDescription();
					}
					partRechargeCapEle	= quotationModel.getQuotation().getQuotationProfile().getPartRechargeCapEle();
					partRechargeCapEle = MALUtilities.isEmpty(partRechargeCapEle) ? "N":partRechargeCapEle;

				} else {
					super.addErrorMessage("notfound", "quote");
				}

				loadData();
				if(getStandardOrderQuote() != null){
					standardQuoteResidual	= getResidualValue(getStandardOrderQuote().getQuotationModel());
				}
				if(getFirstQuote() != null){
					acceptedQuoteResidual	= getResidualValue(getFirstQuote().getQuotationModel());
				}
				if(getFinalizedQuote() != null){
					finalizeQuoteResidual	= getResidualValue(getFinalizedQuote().getQuotationModel());
				}
				
				if(!editMode && getFinalizedQuote() != null){
					loadAmendmentData();
				}
			}
		} catch (Exception ex) {
			logger.error(ex);
			super.addErrorMessage("generic.error", ex.getMessage());
		}
	}
	private void loadAmendmentData() throws MalBusinessException{
		amendmentHistoryList = amendmentHistoryService.getAmendedQuotesWithAmendmentDetail(incomingQmdIdForHistory, true, false);
		for (AmendmentHistoryVO amendmentHistoryVO : amendmentHistoryList) {
			if(!amendmentHistoryVO.isOERevision()){
				if(amendmentHistoryVO.getAfterMarketEquipments() == null ||  amendmentHistoryVO.getAfterMarketEquipments().isEmpty()){
					amendmentHistoryVO.getAfterMarketEquipments().add(new EleAmendmentDetailVO());
					amendmentHistoryVO.setNoChangeInfo("No equipment changes");
				}
			}
		}

	}
	public void viewServiceElements() {
		logger.debug("CapitalCostsBean:viewServiceElements:Start");
		Long selectedQmdId = Long.parseLong(getFaceExternalContext().getRequestParameterMap().get("selectedQmdId"));
		saveRestoreStateValues(getCurrentPageRestoreStateValuesMap());
		Map<String, Object> nextPageValues = new HashMap<String, Object>();
		nextPageValues.put(ViewConstants.VIEW_PARAM_QUOTE_MODEL_ID, selectedQmdId);
		saveNextPageInitStateValues(nextPageValues);
		logger.debug("CapitalCostsBean:viewServiceElements:End");
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
			thisPage.setPageDisplayName("Capital Costs");
		} else {
			inquiry = true;
			thisPage.setPageDisplayName("Capital Cost Inquiry");
		}

		if(thisPage.getInputValues().get(ViewConstants.VIEW_PARAM_RELEASED_PO_DOC_NO) != null){
			targetPONumber = (String) thisPage.getInputValues().get(ViewConstants.VIEW_PARAM_RELEASED_PO_DOC_NO);
		}
		
		invoiceHeaderId = (Long) thisPage.getInputValues().get(ViewConstants.VIEW_PARAM_INVOICE_HEADER_DOC_ID);

		String inputMode = (String) thisPage.getInputValues().get(ViewConstants.VIEW_PARAM_MODE);
		if(inputMode != null && inputMode.equalsIgnoreCase(ViewConstants.VIEW_MODE_EDIT) && hasPermission("postReconcileUnitCosts")) {
			editMode = true;
		} else if(inputMode != null && inputMode.equalsIgnoreCase(ViewConstants.VIEW_MODE_FINALIZE) && hasPermission("postReconcileUnitCosts")){
			editMode = true;
			finalizeMode = true;
		}else{
			editMode = false;	
		}
		thisPage.setPageUrl(ViewConstants.CAPITAL_COSTS);

	}

	@Override
	protected void restoreOldPage() {
		incomingQmdId = (Long) thisPage.getRestoreStateValues().get(ViewConstants.VIEW_PARAM_QUOTE_MODEL_ID);
		targetPONumber = (String) thisPage.getRestoreStateValues().get(ViewConstants.VIEW_PARAM_RELEASED_PO_DOC_NO);
		invoiceHeaderId = (Long) thisPage.getRestoreStateValues().get(ViewConstants.VIEW_PARAM_INVOICE_HEADER_DOC_ID);
		String inputMode = (String) thisPage.getRestoreStateValues().get(ViewConstants.VIEW_PARAM_MODE);
		if(inputMode != null && inputMode.equalsIgnoreCase(ViewConstants.VIEW_MODE_EDIT)) {
			editMode = true;
		} else {
			editMode = false;
		}

	}


	public String cancel() {
		return super.cancelPage();
	}

	//TODO: move to core and use in recalc for service
	private void saveCalculatedFinalizeQuote()throws Exception{
		try{
			QuotationModel finalizeQuotationModelDB = quotationService.getQuotationModelWithCostAndAccessories(finalizedQuote.getQuotationModel().getQmdId());
			QuotationProfitability quotationProfitability = profitabilityService.getQuotationProfitability(finalizeQuotationModelDB);
			// We don't want to change the service elements that are on the quote; so we need to pass isReproces = false; just like we do for 
			// formal extensions
			
			if("OE".equals(quoteType)){
				quotationProfitability.setProfitAmount(finalizedQuoteIrr);
				rentalCalculationService.saveCalculatedQuoteOE(finalizeQuotationModelDB, calcQuotationModel, quoteSteps,
																quotationProfitability, null);
			}else{
				//For RC-1861, save accepted quote IRR for revised quote
				if (finalizedQuote.getQuotationModel().getPreContractFixedCost().equals("F")){
					quotationProfitability.setProfitAmount(finalizedQuoteIrr);
				}else{
					quotationProfitability.setProfitAmount(firstQuoteIrr);
				}
				calcQuotationModel.setCalculatedMontlyRental(finalizedLeaseRate);
				rentalCalculationService.saveCalculatedQuote(finalizeQuotationModelDB,calcQuotationModel, finalizeQuotationModelDB.getResidualValue(),
						quotationProfitability, true);
			}
			
			calcQuotationModel = null;
			
		}catch(MalBusinessException mbe){
			throw mbe;
		}catch(Exception ex){
			throw new MalBusinessException("generic.error",new String[]{"saving calculated rental"},ex);
		}	
	}
	
	public void save() {
		if(calculateFinalizedQuote()) {
			if(saveData()){
				try {
						//reload again page
					reloadPageData();
				} catch (MalBusinessException e) {
					
					handleException("generic.error.occured.while", new String[] { "loading page after save" },e,null);
					logger.error(e, "Error in reloading page after save");
				}catch(Exception ex){
					handleException("generic.error.occured.while", new String[] { "loading page after save" },ex,null);
				}
			}
			
		}
		
		
	}
	
	
	
	
	private boolean saveData() {
		boolean success = false;
		try {
				//while creating revise quote, unit number field does not get populated on revise one 
				if(finalizedQuote.getQuotationModel().getUnitNo() == null){
					finalizedQuote.getQuotationModel().setUnitNo(this.unitNo);
				}
				//For RC-1927 Update finalize quote capital with finalized customer cost
				//finalizedQuote.getQuotationModel().setQuoteCapital(customerCostFinalized);
				capitalCostOverviewService.saveCapitalCost(finalizedQuote.getQuotationModel(), invoiceHeaderId ,rowList ,finalizeMode);
				saveCalculatedFinalizeQuote();
				//For RC-1861/RC-1938
				if ("CE".equals(quoteType)) {
					if (!finalizedQuote.getQuotationModel().getPreContractFixedCost().equals("F")){
						finalizedQuoteIrr	= firstQuoteIrr;
					}
					
				} 
				
				addSuccessMessage("saved.success", "Capital cost");
				success = true;
			} catch (Exception e) {
				handleException("generic.error.occured.while", new String[] { "saving capital cost" },e,null);
			
				logger.error(e, "Error in save capital cost");
			}
		return success;
	}
	
	
	private void reloadPageData() throws Exception{
		try{
			//reload again page
			quotationModel = quotationService.getQuotationModelWithCostAndAccessories(quotationModel.getQmdId());			
			CapitalCostModeValuesVO capitalCostModeValuesVO = capitalCostService.getModeValues(quotationModel);			
			QuotationModel  firstQuoteModel = capitalCostModeValuesVO.getFirstQuoteModel();		
			QuotationModel finalizedQuoteModel = quotationService.getQuotationModelWithCostAndAccessories(finalizedQuote.getQuotationModel().getQmdId());	
			boolean isStockOrder = capitalCostModeValuesVO.getIsStockOrder();	
			if(finalizedQuoteModel != null){
				finalizedQuote = capitalCostService.getQuoteCapitalCosts(finalizedQuoteModel, true, firstQuote, isStockOrder);
				finalizedQuote = capitalCostService.populatePODetails(firstQuoteModel, finalizedQuote);
				invoice = capitalCostService.getInvoiceCapitalCosts(firstQuoteModel,allCostElement,isStockOrder, false);			
				invoice =  capitalCostService.populateReclaimCosts(firstQuoteModel,finalizedQuoteModel, invoice) ;
			}
			loadRows();
		}catch(Exception ex){
			throw ex;
		}
	}
	
	public void calculate() {
		// call functions to update the screen fields for the finalized quote lease rate and IRR
		disableSave = false;
		calculateFinalizedQuote();
	}
	
	private	BigDecimal	getResidualValue(QuotationModel quotationModel) throws MalBusinessException{
		
		BigDecimal	totalResidual	= BigDecimal.ZERO;
		
		if(quotationModel != null){			
				BigDecimal mainElementResidual = quotationModel.getResidualValue();
				BigDecimal equipmentResidual = BigDecimal.ZERO;
				if(quotationModel.getQuotationElements() != null && quotationModel.getQuotationElements().size()  > 0 ){
					equipmentResidual =rentalCalculationService.getEquipmentResidual(quotationModel);
				}else{
					equipmentResidual = rentalCalculationService.getEquipmentResidualOfNewOEQuote(quotationModel.getQmdId());
				}
				totalResidual = mainElementResidual.add(equipmentResidual);
		}
		
		return totalResidual;
	}
	public boolean calculateFinalizedQuote()  {
		
		boolean success = false;
		try {
			if (finalizedQuote != null) {
				BigDecimal adminFactor = null;
				BigDecimal interestRate = null;
				BigDecimal finalResidual = null;				
				BigDecimal finalResidualAcceptedQuote = null;
				BigDecimal customerCostAcceptedQuote = BigDecimal.ZERO;
				
				String groupColumnName = "Total Cost  to Place In Service:";
				QuotationModel dbQuotationModel = finalizedQuote.getQuotationModel();
				finalizedQuoteInfo = String.valueOf(finalizedQuote.getQuotationModel().getQuotation().getQuoId())
						+ "/"
						+ finalizedQuote.getQuotationModel().getQuoteNo()
						+ "/"
						+ finalizedQuote.getQuotationModel().getRevisionNo();
				QuotationProfitability quotationProfitability = profitabilityService.getQuotationProfitability(dbQuotationModel);
				//customerCost = totalCapCostForCalculation;
				finalizedGlobalMin = CommonCalculations.getRoundedValue(quotationProfitability.getProfitBase(), 3);
				finalizedQuoteIrr = CommonCalculations.getRoundedValue(quotationProfitability.getProfitAmount(), RentalCalculationService.IRR_CALC_DECIMALS);
				
				for (CapitalCostsRowVO capitalCostsRowVO : rowList) {
					if(capitalCostsRowVO.getIsFooter()){
						if(capitalCostsRowVO.getName().equals(groupColumnName)){
							customerCostFinalized = capitalCostsRowVO.getFinalizedQuoteCustomerCost();
							mikealbertCostFinalized = capitalCostsRowVO.getFinalizedQuoteDealCost();
							customerCostAcceptedQuote = capitalCostsRowVO.getFirstQuoteCustomerCost();
						}
					}
				}
				if(calcQuotationModel == null){
					QuotationModel initQuotationModel = quotationService.getQuotationModelWithCostAndAccessories(finalizedQuote.getQuotationModel().getQmdId());
					calcQuotationModel = rentalCalculationService.validateQuotationModelForRental(initQuotationModel);//HD-475
				}
				reconcileQuotaionElements();
				populateUpdatedQuotationElementCost(calcQuotationModel);//updated cost is needed because we are  calculating rental per line			
					
				if ("OE".equals(quoteType)) {
					
					finalResidualAcceptedQuote = acceptedQuoteResidual;
					
					Double adminFactorFinV = quotationService.getFinanceParam(MalConstants.FIN_PARAM_ADMIN_FACT,
							getFirstQuote().getQuotationModel().getQmdId(), getFirstQuote().getQuotationModel().getQuotation().getQuotationProfile().getQprId());
					adminFactor = BigDecimal.valueOf(adminFactorFinV);

					interestRate = getFirstQuote().getQuotationModel().getInterestRate();
					interestRate	= CommonCalculations.getRoundedValue(interestRate, 3) ;
					interestRate = interestRate.divide(new BigDecimal(1200), CommonCalculations.MC);

					
					BigDecimal depreciationFactor = profitabilityService.getDepreciationFactor(customerCostAcceptedQuote, finalResidualAcceptedQuote, new BigDecimal(
							dbQuotationModel.getContractPeriod()));
					depreciationFactor = depreciationFactor.movePointRight(2);
					depreciationFactor = depreciationFactor.setScale(7, RoundingMode.HALF_UP);
					depreciationFinalized	= depreciationFactor;
					
					boolean isFixedCostQuote = dbQuotationModel.getPreContractFixedCost().equals("F") ? true : false;
					
					depreciationFactor = depreciationFactor.divide(new BigDecimal(100), CommonCalculations.MC);
					
					List<QuotationStepStructure> quotationStepStructureList = quotationService.getQuotationModelStepStructure(getFirstQuote().getQuotationModel().getQmdId());
					getFirstQuote().getQuotationModel().setQuotationStepStructure(quotationStepStructureList);

					List<QuotationStepStructureVO> quotationStepStructureVOs = rentalCalculationService.getQuotationStepStructureVOs(getFirstQuote().getQuotationModel());
					quotationStepStructureVOs = rentalCalculationService.updateQuotationStepStructureWithElementsCost(calcQuotationModel, quotationStepStructureVOs);
					
					BigDecimal finalNBV = finalResidualAcceptedQuote;
					
					if(customerCostFinalized.compareTo(customerCostAcceptedQuote) != 0){// Cost got changed so calculate NBV based on new cost and accepted depreciation
						 finalNBV = profitabilityService.getFinalNBV(customerCostFinalized, depreciationFinalized, new BigDecimal(calcQuotationModel.getContractPeriod()));	 
					}
					
					quoteSteps = calculateOELease(depreciationFactor, finalNBV , interestRate, adminFactor, quotationStepStructureVOs,  isFixedCostQuote);						
				
					BigDecimal adminFee = profitabilityService.getFinanceParameter(MalConstants.CLOSED_END_LEASE_ADMIN, dbQuotationModel.getQmdId(), dbQuotationModel.getQuotation().getQuotationProfile().getQprId());
					BigDecimal calculatedIrr = profitabilityService.calculateIrrFromOEStep(quoteSteps, mikealbertCostFinalized,adminFee);
					calculatedIrr = calculatedIrr != null ? CommonCalculations.getRoundedValue(calculatedIrr, RentalCalculationService.IRR_CALC_DECIMALS) : null;
					finalizedQuoteIrr = calculatedIrr;
				
					finalizeQuoteResidual	= BigDecimal.ZERO;	
					finalizedLeaseRate = BigDecimal.ZERO;	
					newFinalNBV = BigDecimal.ZERO;	
					if (quoteSteps != null && !quoteSteps.isEmpty()) {
						finalizedLeaseRate = quoteSteps.get(0).getLeaseRate();
						finalizeQuoteResidual = quoteSteps.get(quoteSteps.size() -1).getNetBookValue();
						newFinalNBV = finalizeQuoteResidual;
					}
										
				}else {
					finalResidual = BigDecimal.ZERO;
					BigDecimal monthlyRental = BigDecimal.ZERO;
				
					finalResidual = getResidualValue(dbQuotationModel);					
					
					if(! calcQuotationModel.isCalcRentalApplicable()){
						try{
							finalizedQuoteIrr = profitabilityService.calculateIRR(dbQuotationModel,mikealbertCostFinalized,finalResidual,BigDecimal.ZERO);
							finalizedQuoteIrr = CommonCalculations.getRoundedValue(finalizedQuoteIrr, RentalCalculationService.IRR_CALC_DECIMALS);
						} catch (Exception e) {
							finalizedQuoteIrr = BigDecimal.ZERO;
						}
						
					}else{
						
						
						if (dbQuotationModel.getPreContractFixedCost().equals("F")) {
							//for RC-1818/207
							BigDecimal calculatedIRR = profitabilityService.calculateIRR(dbQuotationModel,mikealbertCostFinalized,finalResidual,firstLeaseRate);
							finalizedQuoteIrr = CommonCalculations.getRoundedValue(calculatedIRR, RentalCalculationService.IRR_CALC_DECIMALS);;
							finalizedLeaseRate = CommonCalculations.getRoundedValue(firstLeaseRate, 2);
							// customerCostFinalized should be same as acceptedCostFinalized in this case. we need to calculate to setup/save quotation element . 
							monthlyRental = profitabilityService.calculateMonthlyRental(calcQuotationModel, customerCostFinalized, finalResidual,firstQuoteIrr);	
							 
						}else{
							
							monthlyRental = profitabilityService.calculateMonthlyRental(calcQuotationModel, customerCostFinalized, finalResidual,firstQuoteIrr);
							finalizedLeaseRate = CommonCalculations.getRoundedValue(monthlyRental, 2);
							//for RC-1861
							BigDecimal calculatedIRR = profitabilityService.calculateIRR(dbQuotationModel,mikealbertCostFinalized,finalResidual,finalizedLeaseRate);
							finalizedQuoteIrr = CommonCalculations.getRoundedValue(calculatedIRR, RentalCalculationService.IRR_CALC_DECIMALS);							
						}
					}
					newFinalNBV	= finalResidual;
				}

			}
			success = true;
		} catch (Exception ex) {
			handleException("generic.error.occured.while", new String[] { "calculating quote" },ex,null);
			ex.printStackTrace();
		}
		return success;
	}

	public	void	holdPosting(){
		userConfirmation	= false;
		showWarning = false;
		warningMessage = null;
		invoiveAmountMismatch = false;
	}
	
	public void postInvoice() {
		if(calculateFinalizedQuote()) {
			if(saveData()){
				doPosting();
			}
		}
	
	}

		
	private void doPosting() {
		try {
			if (invoiceHeaderId == null) {
				return;
			}
			Doc invoiceDoc = docDAO.findById(invoiceHeaderId).orElse(null);
			DocLink docLink = docLinkDAO.findByChildDocId(invoiceDoc.getDocId());
			Doc poOrderdoc = null;
			if (!MALUtilities.isEmpty(docLink)) {
				Long parentDocId = docLink.getId().getParentDocId();			
				poOrderdoc = docDAO.findById(parentDocId).orElse(null);
			}
			
			showWarning = false;
			warningMessage = null;
			invoiveAmountMismatch = false;
			if (!userConfirmation) {
				Map<String, Object> validationResultMap = invoiceService.prePostInvoiceValidations(invoiceDoc.getDocId());
				if (validationResultMap != null && !validationResultMap.isEmpty()) {
					boolean isFail = MALUtilities.isEmpty(validationResultMap.get(InvoiceEntryService.ERROR_TYPE_BLOCKER)) ? false
							: (Boolean) validationResultMap.get(InvoiceEntryService.ERROR_TYPE_BLOCKER);
					boolean isWarning = MALUtilities.isEmpty(validationResultMap.get(InvoiceEntryService.ERROR_TYPE_WARNING)) ? false
							: (Boolean) validationResultMap.get(InvoiceEntryService.ERROR_TYPE_WARNING);
					String errorMessage = MALUtilities.isEmpty(validationResultMap.get(InvoiceEntryService.MESSAGE)) ? null
							: (String) validationResultMap.get(InvoiceEntryService.MESSAGE);
					System.out.println("Error Message:" + errorMessage);
					if (isFail) {
						addSimplErrorMessage(errorMessage);
						return;
					}
					if (isWarning) {
						warningMessage = errorMessage;
						showWarning = true;
						invoiceHeaderTotal	= (BigDecimal)validationResultMap.get(InvoiceEntryService.INVOICE_HEADER_TOTAL);
						invoiceDetailTotal= (BigDecimal)validationResultMap.get(InvoiceEntryService.INVOICE_DETAIL_TOTAL);
						if(invoiceDetailTotal.compareTo(invoiceHeaderTotal) != 0){
							invoiveAmountMismatch	= true;
						}
						return;
					}
				}
			}
			boolean success = invoiceService.postInvoice(poOrderdoc, invoiceDoc , getLoggedInUser().getEmployeeNo(), getLoggedInUser().getCorporateEntity().getCorpId());
			addSuccessMessage("process.success", "Post Invoice");
			disablePost = true;
			disableSave = false;
			editInvoice = false;
			finalizeMode = true;
			thisPage.getInputValues().put(ViewConstants.VIEW_PARAM_MODE, ViewConstants.VIEW_MODE_FINALIZE);  // in case they use the breadcrumb to refresh the page
			disableCalculate	= false;
			loadDates();
			String orderType = poOrderdoc.getOrderType() != null ? poOrderdoc.getOrderType() : String.valueOf("X");
			if( success && ! orderType.equalsIgnoreCase("T") && ! "PUR".equalsIgnoreCase(quotationService.getLeaseType(quotationModel.getQmdId()))){	
				invoiceService.postInvoiceTALNotification(poOrderdoc , getLoggedInUser().getCorporateEntity().getCorpId());	
			}

			if(success) {
				try {
					reloadPageData();
				} catch (MalBusinessException e) {
					handleException("generic.error.occured.while", new String[] { "loading page after invoice posting" },e,null);
					logger.error(e, "Error in reloading page after invoice posting");
				}catch(Exception ex){
					handleException("generic.error.occured.while", new String[] { "loading page after invoice posting" },ex,null);
				}
				
			}


		} catch (Exception ex) {
			handleException("generic.error", new String[] { "posting invoice" }, ex, "postInvoice");
			logger.error(ex);
		}
		
	}
		
		

	public void saveOnConfirm() {
		userConfirmation = true;
		doPosting();
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
			mode = capitalCostModeValuesVO.getMode();
			QuotationModel  firstQuoteModel = capitalCostModeValuesVO.getFirstQuoteModel();
			QuotationModel finalizedQuoteModel = capitalCostModeValuesVO.getFinalQuoteModel();
			QuotationModel standardOrderQuoteModel = capitalCostModeValuesVO.getStandardOrderQuoteModel();
			if(editMode){
				Long finalizeInProcessQmd = quotationService.getRevisedQmd(incomingQmdId);				
				finalizedQuoteModel = quotationService.getQuotationModelWithCostAndAccessories(finalizeInProcessQmd);
				mode  = CapitalCostService.FINALIZED_MODE;
			}

			boolean isStockOrder = capitalCostModeValuesVO.getIsStockOrder();
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
					firstQuoteIrr = CommonCalculations.getRoundedValue(quotationProfitability.getProfitAmount(),RentalCalculationService.IRR_CALC_DECIMALS);
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
					standardOrderGlobalMin = CommonCalculations.getRoundedValue(quotationProfitability.getProfitBase(),3);
					standardOrderIrr = CommonCalculations.getRoundedValue(quotationProfitability.getProfitAmount(),3);
				}
				standardOrderLeaseRate = getLeaseRate(standardOrderQuote.getQuotationModel());
			}
			if (finalizedQuoteModel != null) {
				if(editMode) {
					finalizedQuote = capitalCostService.populatePODetails(firstQuoteModel, finalizedQuote);
					invoice = capitalCostService.getInvoiceCapitalCosts(firstQuoteModel,allCostElement,isStockOrder, false);
				}else{
					finalizedQuote = capitalCostService.populatePODetails(finalizedQuoteModel, finalizedQuote);
					invoice = capitalCostService.getInvoiceCapitalCosts(finalizedQuoteModel,allCostElement,isStockOrder, true);
				}
						
				
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
					finalizedQuoteIrr = CommonCalculations.getRoundedValue(quotationProfitability.getProfitAmount(),RentalCalculationService.IRR_CALC_DECIMALS);
				}
				finalizedLeaseRate = getLeaseRate(finalizedQuote.getQuotationModel());
			} else if (firstQuoteModel != null) {
				firstQuote = capitalCostService.populatePODetails(firstQuoteModel, firstQuote);
				invoice = capitalCostService.getInvoiceCapitalCosts(firstQuoteModel,allCostElement,	isStockOrder, true);
				invoice =  capitalCostService.populateReclaimCosts(firstQuoteModel,null, invoice) ;
				invoice.setInvoiceQmdId(firstQuoteModel.getQmdId());
			}

			if(editMode) {
				mainPONumber = getMainPONumber(finalizedQuote);
				if(finalizedQuoteModel == null) {
					populateEmptyFinalizedQuote();
				}
			}

			setEditFlags();

			if(editInvoice) {
				canadaUnit = isDriverAddressCanadian();
			}

			loadDates();
			
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
	
	private void loadDates() {
		VehicleTimelineEventVO vehicleTimelineEventVO;
		vehicleTimeline = new ArrayList<VehicleTimelineEventVO>();
		FleetMaster fleetMaster = fleetMasterService.findByUnitNo(unitNo);
		if(fleetMaster != null) {
			ContractLine originalContractLine = contractService.getOriginalContractLine(fleetMaster, quotationModel.getQuotation().getExternalAccount());
			if(originalContractLine != null) {
				ContractLine contractLine = contractLineDAO.findByClnIdWithQuotationModel(originalContractLine.getClnId());
				Doc doc = docDAO.findMainPurchaseOrderByQmdIdAndStatus(contractLine.getQuotationModel().getQmdId(), "R");
				if(doc != null) {
					Long docId = doc.getDocId();
					/* 
					 * Updated for OTD-1001 : Sort parameter added externally
					 */
					List<SupplierProgressHistory> supplierProgressHistoryList = supplierService.getSupplierProgressHistory(docId, new Sort(new Sort.Order(Direction.ASC, "progressType"), new Sort.Order(Direction.ASC, "sphId")));
					for(SupplierProgressHistory sph : supplierProgressHistoryList) {
						if(! sph.getProgressType().equals("24_IN-SERV")) {   // this type is not correctly populated by the system so don't show it
							vehicleTimelineEventVO = new VehicleTimelineEventVO(sph.getProgressType(), sph.getProgressTypeCode().getDescription(), sph.getActionDate());
							vehicleTimeline.add(vehicleTimelineEventVO);
						}
					}
				}

				if(contractLine.getInServDate() != null) {
					vehicleTimeline.add(new VehicleTimelineEventVO("InServiceDate", "In Service Date", contractLine.getInServDate()));					
				}
				if(contractLine.getStartDate() != null) {
					vehicleTimeline.add(new VehicleTimelineEventVO("BillStartDate", "Bill Start Date", contractLine.getStartDate()));					
				}

				if(vehicleTimeline.size() > 0) {
					Collections.sort(vehicleTimeline);
				}
				
			}
		}

	}

	private boolean isDriverAddressCanadian() {
		
		if(quotationModel.getQuotation().getDrvDrvId() != null) {
			DriverAddress driverAddress = driverService.getDriverAddress(quotationModel.getQuotation().getDrvDrvId(), DriverService.GARAGED_ADDRESS_TYPE); 
			if(driverAddress != null) {
				if(driverAddress.getCountry().getCountryCode().equalsIgnoreCase("CN")) {
					return true;
				}
			}
		}
		return false;
	}
	
	private void populateEmptyFinalizedQuote() {
		// move data from accepted quote
		finalizedQuote = firstQuote;
	}
	
	
	private void setEditFlags() {
		
		editInvoice = false;
		if(editMode) {
			if(targetPONumber != null && !hasInvoiceBeenPosted()) {
				editInvoice = true;
			}						
			disableCalculate = false;
			disableSave = false;
			disablePost = false;
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
		rowList = new ArrayList<CapitalCostsRowVO>();
		fixedDealFlag = false;
	}

	private void loadRows() throws MalBusinessException {
		rowList.clear();
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
		CapitalCostsRowVO row;		
		for (QuoteCostElementVO costElement : allCostElement) {
			row = new CapitalCostsRowVO();
			row.setElementGroup(costElement.getElementGroupOrder());
			hiddenCost = false;
			row.setHasReclaim(false);
			row.setId(costElement.getElementId());
			row.setCapitalElement(costElement.isCapitalElements());
			//row.setBaseVehicle(costElement.isModelAccessories());
			row.setModelAccessories(costElement.isModelAccessories());
			row.setDealerAccessories(costElement.isDealerAccessories());
			
			if (lastGroupName == null) {
				lastGroupName = costElement.getElementGroupName();
			}
			if (costElement.getElementGroupOrder() != lastGroupOrder) {
				row.setName(lastGroupName + ":");
				row.setElementGroup(lastGroupOrder*-1);
				row.setFooter(true);
				row.setId(null);
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
				row = new CapitalCostsRowVO();
				row.setId(costElement.getElementId());
				row.setCapitalElement(costElement.isCapitalElements());				
				row.setModelAccessories(costElement.isModelAccessories());
				row.setDealerAccessories(costElement.isDealerAccessories());
				row.setElementGroup(costElement.getElementGroupOrder());

			}
			row.setName(costElement.getElementName());
			row.setAccessoryData(costElement.isModelAccessories() || costElement.isDealerAccessories());
			row.setType(costElement.getElementType());
			row.setCode(costElement.getElementcode());
			// if(costElement.getQuoteConcealed() != null &&
			// costElement.getQuoteConcealed().equals("Y")) {
			// hiddenCost = true;
			// }
			hiddenCost = false;
			BigDecimal rechargeAmount;

			if (!hiddenCost) {
				if (standardOrderQuote != null) {
					QuoteCostElementVO element = getCostElementOnQuote(standardOrderQuote, costElement);
					BigDecimal dealCost = BigDecimal.ZERO;
					BigDecimal custCost = BigDecimal.ZERO;
					if(element != null) {
						if(element.getDealCost() != null) {
							dealCost = element.getDealCost();
						}
						if(element.getCustomerCost() != null) {
							custCost = element.getCustomerCost();
						}
					}

					row.setStandardOrderDealCost(dealCost);
					standardOrderDealSum = standardOrderDealSum.add(dealCost);
					row.setStandardOrderCustomerCost(custCost);
					standardOrderCustomerSum = standardOrderCustomerSum.add(custCost);
				}
				if (firstQuote != null) {
					QuoteCostElementVO element = getCostElementOnQuote(firstQuote, costElement);
					BigDecimal dealCost = BigDecimal.ZERO;
					BigDecimal custCost = BigDecimal.ZERO;
					rechargeAmount = BigDecimal.ZERO;
					if(element != null) {
						if(element.getDealCost() != null) {
							dealCost = element.getDealCost();
						}
						if(element.getCustomerCost() != null) {
							custCost = element.getCustomerCost();
						}
						if(element.getRechargeAmt() != null) {
							rechargeAmount = element.getRechargeAmt();
						}
					}
					
					row.setFirstQuoteDealCost(dealCost);
					row.setRechargeAmt(rechargeAmount);
					

					firstQuoteDealSum = firstQuoteDealSum.add(dealCost);
	
					row.setFirstQuoteCustomerCost(custCost);
					setPODetails( row,  firstQuote);
					firstQuoteCustomerSum = firstQuoteCustomerSum.add(custCost);
				}
				if (finalizedQuote != null) {
					QuoteCostElementVO element = getCostElementOnQuote(finalizedQuote, costElement);
					BigDecimal dealCost = BigDecimal.ZERO;
					BigDecimal custCost = BigDecimal.ZERO;
					if(element != null) {
						if(element.getDealCost() != null) {
							dealCost = element.getDealCost();
						}
						if(element.getCustomerCost() != null) {
							custCost = element.getCustomerCost();
						}
						if(element.getRechargeAmt() != null) {
							rechargeAmount = element.getRechargeAmt();
							row.setRechargeAmt(rechargeAmount);
						}
					}
					row.setFinalizedQuoteDealCost(dealCost);
					finalizedQuoteDealSum = finalizedQuoteDealSum.add(dealCost);
					row.setFinalizedQuoteCustomerCost(custCost);
					setPODetails( row,  finalizedQuote);
					finalizedQuoteCustomerSum = finalizedQuoteCustomerSum.add(custCost);

				}
				if (row.getRechargeAmt() != null) {
					rechargeAmtSum = rechargeAmtSum.add(row.getRechargeAmt());						
				}

			}

			if (invoice != null) {
				cost = getDealCostFromQuote(invoice, costElement);
				if((row.getRechargeAmt() != null) && (row.getRechargeAmt().compareTo(BigDecimal.ZERO) != 0)) {
					if(getDealCostFromQuote(firstQuote, costElement).compareTo(BigDecimal.ZERO) != 0) {
						row.setFullRechargeFlag(false);
					} else {
						row.setFullRechargeFlag(true);
					}
				}

				row.setHasReclaim(isReclaim(invoice, costElement));
				
				row.setInvoiceCost(cost);

			}

			if(editMode && firstQuote != null) {
				if((row.getRechargeAmt() != null) && (row.getRechargeAmt().compareTo(BigDecimal.ZERO) != 0) ){
					row.setClientCostAdjustmentFlag(false);
				} else {
					// right now using firstQuote, this should be changed to finalized quote when that story is done
					row.setClientCostAdjustmentFlag(getAdjustmentFlag(firstQuote, costElement, row.getPoNumber()));
					
					//for OE and PUR calculation strategies work this way; maybe all capital elements?!
					if(costElement.isCapitalElements() && (capitalCostCalcType.equalsIgnoreCase("CapitalCosts") || capitalCostCalcType.equalsIgnoreCase("CapitalCostsWithMargin"))) {
						if(costElement.getQuoteCapital() != null && costElement.getQuoteCapital().equalsIgnoreCase("Y")) {
							row.setDealCostAdjustmentFlag(true);
						} else {
							row.setDealCostAdjustmentFlag(false);
						}
					}else{
						row.setDealCostAdjustmentFlag(true);
					}
					

					if(row.getHasReclaim() && !editInvoice ) {
						row.setPossibleReclaim(false);
					} else {
						// continue to use firstQuote as reclaims are set up against the accepted quote, not against the finalized one
						Long qceId = getCostElementOnQuote(firstQuote, costElement).getQuotationCapitalElementId();
						if(qceId != null) {
							populatePossibleReclaimInfo(row, qceId);						
						} else {
							row.setPossibleReclaim(false);
						}

					}
					
				}

			}
			
			if(row.getInvoiceCost() != null && !row.getIsFooter()) {
				invoiceSum = invoiceSum.add(row.getInvoiceCost());					
			}

			
			if(row.getIsAccessoryData() ||  finalizedQuote ==  null || editMode){
				rowList.add(row);
			} else if (editInvoice) {
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

		row = new CapitalCostsRowVO();
		row.setName(lastGroupName + ":");
		row.setElementGroup(lastGroupOrder * -1);
		row.setFooter(true);
		row.setId(null);
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

	
	
	
	private boolean getAdjustmentFlag(QuoteVO quote, QuoteCostElementVO costElement, String rowPONumber) {
	
		boolean flag;
		if(quote.getQuotationModel().getPreContractFixedCost().equalsIgnoreCase("F")) {
			flag = false; 
			fixedDealFlag = true;
		} else if (!mainPONumber.equalsIgnoreCase(targetPONumber) && (rowPONumber == null || !rowPONumber.equalsIgnoreCase(targetPONumber) && !finalizeMode)) {
			flag = false;  // don't let user adjust main PO cost if on non-main PO entry
		}  else {
			flag = getBaseAdjustmentFlag(quote, costElement);
		}
			
		return flag;
	
	}
	
	//TODO: rewrite this trash
	private boolean getBaseAdjustmentFlag(QuoteVO quote, QuoteCostElementVO parentCostElement) {
		boolean flag = true;

		QuoteCostElementVO costElement = getCostElementOnQuote(quote, parentCostElement);
		String capitalCostCalcType = capitalCostService.findQuoteCapitalCostsCalcType(quote.getQuotationModel());
		
		if(costElement != null) {
			if(!costElement.isCapitalElements()) {
				if(costElement.getElementName().equalsIgnoreCase("Capital Contribution")) {
					flag = false;
				} else {
					flag = true;
				}
			// TODO: this is really bad hard coding; Courtesy Delivery Fee should be a rechargable element
		    // and quote capital because it costs Mike Albert $$ for vehicle delivery, we recharge the customer
			// and it's financed in leasing products but QM209 validation does not allow both of these flags to be set
			// (that does not make complete business sense) and prevents us from setting it up this way.
			} else if("Y".equalsIgnoreCase(partRechargeCapEle) && ("Y".equalsIgnoreCase(costElement.getRechargable()) || "Courtesy Delivery Fee".equalsIgnoreCase(costElement.getElementName()))){
				flag = false;
			} else if(capitalCostCalcType.equalsIgnoreCase("CapitalClientInvoiceCosts")) {
				if(costElement.getOnInvoice() != null && costElement.getOnInvoice().equalsIgnoreCase("Y")) {
					flag = true;
				} else {
					flag = false;
				}
			} else if(capitalCostCalcType.equalsIgnoreCase("CapitalCostsWithMargin")) {
				if(costElement.getLfMarginOnly() != null && costElement.getLfMarginOnly().equalsIgnoreCase("N")) {
					flag = true;
				} else {
					flag = false;
				}
			} else if(capitalCostCalcType.equalsIgnoreCase("CapitalCosts")) {
				if(costElement.getQuoteCapital() != null && costElement.getQuoteCapital().equalsIgnoreCase("Y")) {
					flag = true;
				} else {
					flag = false;
				}
			}else {
				flag = true;
			}
			
		} else {
			flag = false;
		}
		
		return flag;
	}
	

	private void populatePossibleReclaimInfo(CapitalCostsRowVO row, long qceId) {
		ReclaimLines reclaimLine = reclaimLineDAO.findReclaimbleLinesByQceId(qceId);
		if(reclaimLine != null) {
			row.setPossibleReclaim(true);
			row.setReclaimLineId(reclaimLine.getRclId());
			row.setInvoiceCost(reclaimLine.getReclaimAmount().negate());
		}
	}
	
	
	private QuoteCostElementVO getCostElementOnQuote(QuoteVO quote, QuoteCostElementVO parentCostElement) {
		for (QuoteCostElementVO element : quote.getCostElements()) {
			if (element.getElementName().equals(parentCostElement.getElementName())) {
				return element;
			}
		}
		return null;
		
	}
	
	
	private void setPODetails(CapitalCostsRowVO row, QuoteVO quote){
		for (QuoteCostElementVO element : quote.getCostElements()) {
			if (element.getElementName().equals(row.getName())) {
				row.setPoNumber(element.getPoNumber());
				row.setPoRevNo(element.getPoRevNo());
				row.setPoOrderDate(element.getPoOrderDate());
				row.setAccountInfo(element.getAccountInfo());
			}
		}
	}

	private BigDecimal getDealCostFromQuote(QuoteVO quote,QuoteCostElementVO costElement) {

		for (QuoteCostElementVO element : quote.getCostElements()) {
			if (element.getElementName().equals(costElement.getElementName())) {
				return element.getDealCost();
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
			
		if (quoteType.equals(QuotationService.CLOSE_END_LEASE)) {
			leaseRate = rentalCalculationService.getFinanceLeaseElementCostForCE(quotationModel);
		} else if (quoteType.equals(QuotationService.OPEN_END_LEASE)) {
			leaseRate	= amendmentHistoryService.getLeaseRateOE(quotationModel);
		}
		return leaseRate;
	}

	private String getMainPONumber(QuoteVO quote) {
		for (QuoteCostElementVO element : quote.getCostElements()) {
			if (element.getElementName().equals(NonCapitalElementEnum.BASE_VEHICLE_ELEMENT.getElementName())) {
				return element.getPoNumber();
			}
		}
		return null;
	}
	
	
	private boolean hasInvoiceBeenPosted() {
		if(invoiceHeaderId != null){
			Doc invoice = docDAO.findById(invoiceHeaderId).orElse(null);
			if(invoice != null && invoice.getDocStatus().equalsIgnoreCase("O")) {
				return false;
			} else {
				return true;
			}
		}else{
			return true;
		}

	}
	
	private List<QuotationStepStructureVO> calculateOELease(BigDecimal depreciationFactor, BigDecimal finalResidual , BigDecimal interestRate, BigDecimal adminFactor, List<QuotationStepStructureVO> quotationStepStructureVOs , boolean isFixedCostQuote) throws Exception{
		List<QuotationStepStructureVO>   stepStructureVOs;
		List<BigDecimal> clientCosts = new ArrayList<>();
		
		int baseElementIdx = -1;
		BigDecimal totalQuoteElementCost = BigDecimal.ZERO;
		BigDecimal baseElementCost = BigDecimal.ZERO;
		
		for(CapitalCostsRowVO capCostRowVO : getRowList()) {
			if(capCostRowVO.isBase()) {
				
				if(isFixedCostQuote){
					baseElementCost = capCostRowVO.getFirstQuoteCustomerCost();//pull cost from accepted  quote
				}else{
					baseElementCost = capCostRowVO.getFinalizedQuoteCustomerCost();//pull cost from finalizedquote
				}
				clientCosts.add(baseElementCost);
				totalQuoteElementCost = totalQuoteElementCost.add(baseElementCost);
				baseElementIdx = clientCosts.size() - 1;

				continue;
			}
			
			if((capCostRowVO.isDealerAccessories() ||capCostRowVO.isModelAccessories()) && !MALUtilities.isEmpty(capCostRowVO.getId())) {
				
				if(isFixedCostQuote){
					clientCosts.add(capCostRowVO.getFirstQuoteCustomerCost());
				}else{
					clientCosts.add(capCostRowVO.getFinalizedQuoteCustomerCost());
				}				
				
				totalQuoteElementCost = totalQuoteElementCost.add(clientCosts.get(clientCosts.size() - 1));
				continue;
			}							
		}
		
		//Adjust the base vehicle cost as it contains cost that is not factored into the lease rate calc
		baseElementCost = baseElementCost.subtract(totalQuoteElementCost.subtract(customerCostFinalized));
		clientCosts.set(baseElementIdx, baseElementCost);	
		
		stepStructureVOs = profitabilityService.calculateOEStepLease(depreciationFactor, finalResidual , interestRate, adminFactor, quotationStepStructureVOs);
			
		return stepStructureVOs;
	}

	// This method add/remove Quotation Element based on entered zero/non-zero cost
	private void reconcileQuotaionElements() throws MalBusinessException{
		
		List<QuotationElement> quotationElementList = calcQuotationModel.getQuotationElements();
		
		for(CapitalCostsRowVO capCostRowVO : getRowList()) {
			
				if( !MALUtilities.isEmpty(capCostRowVO.getId()) &&  (capCostRowVO.isModelAccessories() || capCostRowVO.isDealerAccessories())){
					
					BigDecimal accessoryCost = capCostRowVO.getFinalizedQuoteCustomerCost();
					if(accessoryCost == null || accessoryCost.compareTo(BigDecimal.ZERO) == 0){
					//remove associated element
					for (Iterator<QuotationElement> quotationElementItr = quotationElementList.iterator(); quotationElementItr.hasNext(); ) {
						QuotationElement  qe = quotationElementItr.next() ;
						if((qe.getQuotationModelAccessory() != null && capCostRowVO.getId().equals(qe.getQuotationModelAccessory().getOptionalAccessory().getOacId()))
								|| (qe.getQuotationDealerAccessory() != null && capCostRowVO.getId().equals(qe.getQuotationDealerAccessory().getDealerAccessory().getDacId()))){
							
								quotationElementItr.remove();break;
							}
						}
					
					}else{
							
						//make sure associated element present otherwise add it
						boolean quotationElementFound = false;
						for (Iterator<QuotationElement> quotationElementItr = quotationElementList.iterator(); quotationElementItr.hasNext(); ) {
							QuotationElement  qe = quotationElementItr.next() ;
							
							if( (qe.getQuotationModelAccessory() != null && capCostRowVO.getId().equals(qe.getQuotationModelAccessory().getOptionalAccessory().getOacId()))								
								|| (qe.getQuotationDealerAccessory() != null && capCostRowVO.getId().equals(qe.getQuotationDealerAccessory().getDealerAccessory().getDacId()))){
							
								quotationElementFound = true;break;
							}
							
						 }
						if(!quotationElementFound){
							
							QuotationDealerAccessory dealerAccessory = null;
							QuotationModelAccessory modelAccessory = null;
							if(capCostRowVO.isDealerAccessories()){
								for (QuotationDealerAccessory quotationDealerAccessory : calcQuotationModel.getQuotationDealerAccessories()) {
									if(capCostRowVO.getId().equals(quotationDealerAccessory.getDealerAccessory().getDacId())){
										dealerAccessory = quotationDealerAccessory; 		
										//this is needed so that process can have ui input value to operate
										dealerAccessory.setRechargeAmount(capCostRowVO.getRechargeAmt());	
										dealerAccessory.setDiscPrice(capCostRowVO.getFinalizedQuoteCustomerCost().add(capCostRowVO.getRechargeAmt()));	
										dealerAccessory.setTotalPrice(dealerAccessory.getDiscPrice().subtract(dealerAccessory.getRechargeAmount()));
										dealerAccessory.setBasicPrice(dealerAccessory.getDiscPrice());
										dealerAccessory.setFreeOfChargeYn(MalConstants.FLAG_N);										
										break;
									}								
								}
								
								
								 
							}else{
								for (QuotationModelAccessory quotationModelAccessory : calcQuotationModel.getQuotationModelAccessories()) {
									if(capCostRowVO.getId().equals(quotationModelAccessory.getOptionalAccessory().getOacId())){
										modelAccessory = quotationModelAccessory; 
										
										modelAccessory.setRechargeAmount(capCostRowVO.getRechargeAmt());	
										modelAccessory.setDiscPrice(capCostRowVO.getFinalizedQuoteCustomerCost().add(capCostRowVO.getRechargeAmt()));	
										modelAccessory.setTotalPrice(modelAccessory.getDiscPrice().subtract(modelAccessory.getRechargeAmount()));
										modelAccessory.setBasicPrice(modelAccessory.getDiscPrice());
										modelAccessory.setFreeOfChargeYn(MalConstants.FLAG_N);
										break;
									}								
								}
							} 
							
							if(dealerAccessory != null || modelAccessory != null){
								QuotationElement mainElement = rentalCalculationService.getMainQuotationModelElement(calcQuotationModel);
								QuotationElement newElement = quotationElementService.getCalculatedNewQuotationElement(calcQuotationModel, mainElement.getLeaseElement(), 
																												              modelAccessory, dealerAccessory, false);
								quotationElementList.add(newElement);	
							}
						}
					
					}
			  }	
		}
	}
	
	private void populateUpdatedQuotationElementCost(QuotationModel calcQuotationModel) throws Exception{
		
		BigDecimal totalQuoteElementCost = BigDecimal.ZERO;
		BigDecimal baseElementCost = BigDecimal.ZERO;
		
		//Bind rowList finalized cost to its respective element
		for(QuotationElement qe : calcQuotationModel.getQuotationElements()) {
			if (qe.getLeaseElement().getElementType().equals(MalConstants.FINANCE_ELEMENT) && MALUtilities.convertYNToBoolean(qe.getIncludeYn())) {
				
				if(MALUtilities.isEmpty(qe.getQuotationModelAccessory()) && MALUtilities.isEmpty(qe.getQuotationDealerAccessory())) {
					for(CapitalCostsRowVO capCostRowVO : getRowList()) {
						if(capCostRowVO.isBase()) {
							qe.setCapitalCost(capCostRowVO.getFinalizedQuoteCustomerCost());
							totalQuoteElementCost = totalQuoteElementCost.add(qe.getCapitalCost());
							baseElementCost = qe.getCapitalCost();
							break;
						}
					}					
				}

				if(!MALUtilities.isEmpty(qe.getQuotationModelAccessory())) {
					for(CapitalCostsRowVO capCostRowVO : getRowList()) {
						if(capCostRowVO.isModelAccessories() 
								&& !MALUtilities.isEmpty(capCostRowVO.getId())
								&& capCostRowVO.getId().equals(qe.getQuotationModelAccessory().getOptionalAccessory().getOacId())) {
							qe.setCapitalCost(capCostRowVO.getFinalizedQuoteCustomerCost());
							totalQuoteElementCost = totalQuoteElementCost.add(qe.getCapitalCost());
							break;
						}
					}					
				}

				if(!MALUtilities.isEmpty(qe.getQuotationDealerAccessory())) {
					for(CapitalCostsRowVO capCostRowVO : getRowList()) {
						if(capCostRowVO.isDealerAccessories() 
								&& !MALUtilities.isEmpty(capCostRowVO.getId())
								&& capCostRowVO.getId().equals(qe.getQuotationDealerAccessory().getDealerAccessory().getDacId())) {
							qe.setCapitalCost(capCostRowVO.getFinalizedQuoteCustomerCost());
							totalQuoteElementCost = totalQuoteElementCost.add(qe.getCapitalCost());
							break;
						}
					}					
				}
			}			
		}

		//Adjust the base vehicle cost as it contains cost that is not factored into the lease rate
		baseElementCost = baseElementCost.subtract(totalQuoteElementCost.subtract(customerCostFinalized));
		for(QuotationElement qe : calcQuotationModel.getQuotationElements()) {
			if(qe.getLeaseElement().getElementType().equals(MalConstants.FINANCE_ELEMENT)
					&& MALUtilities.isEmpty(qe.getQuotationModelAccessory()) && MALUtilities.isEmpty(qe.getQuotationDealerAccessory())) {
				qe.setCapitalCost(baseElementCost);
				break;
			}
		}

	
	}	
	
	public List<CapitalCostsRowVO> getRowList() {
		return rowList;
	}

	public void setRowList(List<CapitalCostsRowVO> rowList) {
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
	public BigDecimal getStandardOrderIrr() {
		return standardOrderIrr;
	}
	public void setStandardOrderIrr(BigDecimal standardOrderIrr) {
		this.standardOrderIrr = standardOrderIrr;
	}
	public BigDecimal getFirstQuoteIrr() {
		return firstQuoteIrr;
	}
	public void setFirstQuoteIrr(BigDecimal firstQuoteIrr) {
		this.firstQuoteIrr = firstQuoteIrr;
	}
	public BigDecimal getFinalizedQuoteIrr() {
		return finalizedQuoteIrr;
	}
	public void setFinalizedQuoteIrr(BigDecimal finalizedQuoteIrr) {
		this.finalizedQuoteIrr = finalizedQuoteIrr;
	}
	public boolean isEditInvoice() {
		return editInvoice;
	}
	public void setEditInvoice(boolean editInvoice) {
		this.editInvoice = editInvoice;
	}	
	public String getMode() {
		return mode;
	}
	public void setMode(String mode) {
		this.mode = mode;
	}
	public boolean isEditMode() {
		return editMode;
	}
	public void setEditMode(boolean editMode) {
		this.editMode = editMode;
	}
	
	public boolean isFinalizeMode() {
		return finalizeMode;
	}
	public void setFinalizeMode(boolean finalizeMode) {
		this.finalizeMode = finalizeMode;
	}
	public String getTargetPONumber() {
		return targetPONumber;
	}
	public void setTargetPONumber(String targetPONumber) {
		this.targetPONumber = targetPONumber;
	}

	public List<AmendmentHistoryVO> getAmendmentHistoryList() {
		return amendmentHistoryList;
	}
	public void setAmendmentHistoryList(
			List<AmendmentHistoryVO> amendmentHistoryList) {
		this.amendmentHistoryList = amendmentHistoryList;
	}
	public boolean isDisableSave() {
		return disableSave;
	}
	public void setDisableSave(boolean disableSave) {
		this.disableSave = disableSave;
	}
	public boolean isDisablePost() {
		return disablePost;
	}
	public void setDisablePost(boolean disablePost) {
		this.disablePost = disablePost;
	}
	public boolean isDisableCalculate() {
		return disableCalculate;
	}
	public void setDisableCalculate(boolean disableCalculate) {
		this.disableCalculate = disableCalculate;
	}
	public boolean isFixedDealFlag() {
		return fixedDealFlag;
	}
	public void setFixedDealFlag(boolean fixedDealFlag) {
		this.fixedDealFlag = fixedDealFlag;
	}
	public String getMainPONumber() {
		return mainPONumber;
	}
	public void setMainPONumber(String mainPONumber) {
		this.mainPONumber = mainPONumber;
	}
	public boolean isUserConfirmation() {
		return userConfirmation;
	}
	public void setUserConfirmation(boolean userConfirmation) {
		this.userConfirmation = userConfirmation;
	}
	public boolean isShowWarning() {
		return showWarning;
	}
	public void setShowWarning(boolean showWarning) {
		this.showWarning = showWarning;
	}
	public String getWarningMessage() {
		return warningMessage;
	}
	public void setWarningMessage(String warningMessage) {
		this.warningMessage = warningMessage;
	}
	public BigDecimal getInvoiceHeaderTotal() {
		return invoiceHeaderTotal;
	}
	public void setInvoiceHeaderTotal(BigDecimal invoiceHeaderTotal) {
		this.invoiceHeaderTotal = invoiceHeaderTotal;
	}
	public BigDecimal getInvoiceDetailTotal() {
		return invoiceDetailTotal;
	}
	public void setInvoiceDetailTotal(BigDecimal invoiceDetailTotal) {
		this.invoiceDetailTotal = invoiceDetailTotal;
	}
	public boolean isInvoiveAmountMismatch() {
		return invoiveAmountMismatch;
	}
	public void setInvoiveAmountMismatch(boolean invoiveAmountMismatch) {
		this.invoiveAmountMismatch = invoiveAmountMismatch;
	}
	public boolean isCanadaUnit() {
		return canadaUnit;
	}
	public void setCanadaUnit(boolean canadaUnit) {
		this.canadaUnit = canadaUnit;
	}
	public BigDecimal getNewFinalNBV() {
		return newFinalNBV;
	}
	public void setNewFinalNBV(BigDecimal newFinalNBV) {
		this.newFinalNBV = newFinalNBV;
	}
	public BigDecimal getStandardQuoteResidual() {
		return standardQuoteResidual;
	}
	public void setStandardQuoteResidual(BigDecimal standardQuoteResidual) {
		this.standardQuoteResidual = standardQuoteResidual;
	}
	public BigDecimal getAcceptedQuoteResidual() {
		return acceptedQuoteResidual;
	}
	public void setAcceptedQuoteResidual(BigDecimal acceptedQuoteResidual) {
		this.acceptedQuoteResidual = acceptedQuoteResidual;
	}
	public BigDecimal getFinalizeQuoteResidual() {
		return finalizeQuoteResidual;
	}
	public void setFinalizeQuoteResidual(BigDecimal finalizeQuoteResidual) {
		this.finalizeQuoteResidual = finalizeQuoteResidual;
	}
	public String getPartRechargeCapEle() {
		return partRechargeCapEle;
	}
	public void setPartRechargeCapEle(String partRechargeCapEle) {
		this.partRechargeCapEle = partRechargeCapEle;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public List<VehicleTimelineEventVO> getVehicleTimeline() {
		return vehicleTimeline;
	}
	public void setVehicleTimeline(List<VehicleTimelineEventVO> vehicleTimeline) {
		this.vehicleTimeline = vehicleTimeline;
	}
	public String getProductCode() {
		return productCode;
	}
	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}
	public String getCapitalCostCalcType() {
		return capitalCostCalcType;
	}
	public void setCapitalCostCalcType(String capitalCostCalcType) {
		this.capitalCostCalcType = capitalCostCalcType;
	}
	
}