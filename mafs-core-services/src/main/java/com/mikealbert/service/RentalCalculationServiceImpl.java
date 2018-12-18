package com.mikealbert.service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mikealbert.common.CommonCalculations;
import com.mikealbert.common.MalConstants;
import com.mikealbert.common.MalLogger;
import com.mikealbert.common.MalLoggerFactory;
import com.mikealbert.common.MalMessage;
import com.mikealbert.data.dao.FormulaParameterDAO;
import com.mikealbert.data.dao.FpNoShowReportsDAO;
import com.mikealbert.data.dao.LeaseElementDAO;
import com.mikealbert.data.dao.MulQuoteEleDAO;
import com.mikealbert.data.dao.QmfNoShowReportsDAO;
import com.mikealbert.data.dao.QuotationCapitalElementDAO;
import com.mikealbert.data.dao.QuotationDealerAccessoryDAO;
import com.mikealbert.data.dao.QuotationElementDAO;
import com.mikealbert.data.dao.QuotationElementStepDAO;
import com.mikealbert.data.dao.QuotationModelAccessoryDAO;
import com.mikealbert.data.dao.QuotationModelDAO;
import com.mikealbert.data.dao.QuotationModelFinancesDAO;
import com.mikealbert.data.dao.QuotationProfitabilityDAO;
import com.mikealbert.data.dao.QuotationScheduleDAO;
import com.mikealbert.data.dao.QuotationStepStructureDAO;
import com.mikealbert.data.dao.QuoteElementParameterDAO;
import com.mikealbert.data.dao.WillowConfigDAO;
import com.mikealbert.data.entity.ContractLine;
import com.mikealbert.data.entity.FinanceParameter;
import com.mikealbert.data.entity.FpNoShowReports;
import com.mikealbert.data.entity.LeaseElement;
import com.mikealbert.data.entity.MulQuoteEle;
import com.mikealbert.data.entity.QmfNoShowReports;
import com.mikealbert.data.entity.QmfNoShowReportsPK;
import com.mikealbert.data.entity.Quotation;
import com.mikealbert.data.entity.QuotationCapitalElement;
import com.mikealbert.data.entity.QuotationDealerAccessory;
import com.mikealbert.data.entity.QuotationElement;
import com.mikealbert.data.entity.QuotationElementStep;
import com.mikealbert.data.entity.QuotationModel;
import com.mikealbert.data.entity.QuotationModelAccessory;
import com.mikealbert.data.entity.QuotationModelFinances;
import com.mikealbert.data.entity.QuotationProfitability;
import com.mikealbert.data.entity.QuotationSchedule;
import com.mikealbert.data.entity.QuotationStepStructure;
import com.mikealbert.data.entity.QuotationStepStructurePK;
import com.mikealbert.data.entity.QuoteModelPropertyValue;
import com.mikealbert.data.entity.WillowConfig;
import com.mikealbert.data.enumeration.QuoteModelPropertyEnum;
import com.mikealbert.data.vo.QuotationStepStructureVO;
import com.mikealbert.data.vo.QuoteElementStepVO;
import com.mikealbert.data.vo.ServiceElementsVO;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.exception.MalException;
import com.mikealbert.rental.calculations.QuoteCapitalCosts;
import com.mikealbert.rental.processors.LeaseElementProcessor;
import com.mikealbert.rental.processors.LeaseElementProducer;
import com.mikealbert.service.vo.QuoteCostElementVO;
import com.mikealbert.service.vo.QuoteVO;
import com.mikealbert.util.MALUtilities;
import com.mikealbert.util.SpringAppContext;

@Transactional
@Service("rentalCalculationService")
public class RentalCalculationServiceImpl implements RentalCalculationService {
	private MalLogger logger = MalLoggerFactory.getLogger(this.getClass());
	private static final String INVOICE_ADJUSTMENT = "OE_INV_ADJ";
	private static final String AM_INVOICE_ADJUSTMENT = "PUR_INV_ADJ";
	@Resource
	private QuotationService quotationService;
	@Resource
	private QuotationModelDAO quotationModelDAO;
	@Resource
	private LeaseElementProducer leaseElementProducer;
	@Resource
	private FormulaParameterDAO formulaParameterDAO;
	@Resource
	private LeaseElementDAO leaseElementDAO;
	@Resource
	private MulQuoteEleDAO mulQuoteEleDAO;
	@Resource
	private QuotationElementDAO quotationElementDAO;
	@Resource
	private QuotationElementStepDAO quotationElementStepDAO;
	@Resource
	private QuoteElementParameterDAO quoteElementParameterDAO;
	@Resource
	private ProfitabilityService profitabilityService;
	@Resource
	private QuotationStepStructureDAO quotationStepStructureDAO;
	@Resource
	private WillowConfigDAO willowConfigDAO;
	@Resource
	private MalMessage malMessage;
	@Resource
	private QuotationModelFinancesDAO quotationModelFinancesDAO;
	@Resource
	private FinanceParameterService financeParameterService;
	@Resource
	QuotationModelAccessoryDAO quotationModelAccessoryDAO;
	@Resource
	QuotationDealerAccessoryDAO quotationDealerAccessoryDAO;
	@Resource
	QmfNoShowReportsDAO qmfNoShowReportsDAO;
	@Resource
	FpNoShowReportsDAO fpNoShowReportsDAO;
	@Resource
	private QuotationProfitabilityDAO quotationProfitabilityDAO;
	
	@Resource
	private CapitalCostService capitalCostService;
	@Resource
	private QuotationElementService quotationElementService;
	
	@Resource ServiceElementService serviceElementService;
	
	@Resource
	private QuotationCapitalElementDAO qotationCapitalElementDAO;
	@Resource
	private QuotationScheduleDAO quotationScheduleDAO;
	@Resource CapitalElementService capitalElementService;
	@Resource QuotationCapitalElementDAO quotationCapitalElementDAO;
	@Resource ContractService contractService;
	@Resource QuoteModelPropertyValueService quoteModelPropertyValueService;
	

	protected MathContext MC = CommonCalculations.MC;
	public static final int QUOTE_STATUS_ON_OFFER = 1;
	public static final int QUOTE_STATUS_AWAITING_CREDIT_REVIEW = 2;
	public static final int QUOTE_STATUS_ACCEPTED = 3;
	public static final int QUOTE_STATUS_REVISED = 4;
	public static final int QUOTE_STATUS_AUTHORISED = 5;
	public static final int QUOTE_STATUS_ON_CONTRACT = 6;
	public static final int QUOTE_STATUS_CLOSED = 7;
	public static final int QUOTE_STATUS_REJECTED = 8;
	public static final int QUOTE_STATUS_AMENDMENT = 9;
	public static final int QUOTE_STATUS_CONTRACT_REVISION = 10;
	public static final int QUOTE_STATUS_FUTURE_CONTRACT_AMENDMENT = 11;
	public static final int QUOTE_STATUS_REQUEST_ORDER_REVISION = 12;
	public static final int QUOTE_STATUS_STANDARD_ORDER = 15;
	public static final int QUOTE_STATUS_FINALISED_ORDER_REVISION = 13;
	public static final int QUOTE_STATUS_RETIRED = 14;
	public static final int QUOTE_STATUS_ALLOCATED_TO_GRD = 16;
	public static final int QUOTE_STATUS_GRD_COMPTETE = 17;
	public static final int QUOTE_STATUS_STANDARD_ORDER_REVISION = 18;
	public static final int QUOTE_STATUS_INACTIVE_STANDARD_ORDER = 19;
	private final int DECIMAL_DIG_PRECISION_THREE = 3;
	private final int DECIMAL_DIG_PRECISION_FIVE = 5;
	

	/**
	 * It updates quotation model with quotation elements applicable in this
	 * quotation. It executes formulas of quotation elements to calculate rental
	 * on each element. All updates happen in in memory java object.Nothing is
	 * saved into database yet.
	 * 
	 * @param qmdId
	 *            , Quotation Model which will be updated
	 * @param isReProcess
	 *            , flag to indicate that elements will be obtained as fresh to
	 *            replace existing one.
	 */
	@Transactional(readOnly = true)
	public QuotationModel getCalculatedQuotationModel(Long qmdId, boolean isReProcess, BigDecimal updatedCapitalContribution)throws MalBusinessException {
		
		QuotationModel quotationModel = quotationService.getQuotationModelWithCostAndAccessories(qmdId);
		// BigDecimal interestRate = quotationService.getInterestRate(qmdId);
		// quotationModel.setInterestRate(interestRate);
		if (quotationModel.getManualProfitUpdate() != null && quotationModel.getManualProfitUpdate().equals(MalConstants.FLAG_Y)) {
			quotationModel.setManualProfitUpdate(MalConstants.FLAG_N);
		}
		// we don't want to always pull in service elements (i.e. in the case of formal extensions for example)
		// in this case isReProcess will be "false"
		if (isReProcess) {
			// automatically pull the list of all elements that are identified as change.
			List<ServiceElementsVO>  changedServiceElements  = serviceElementService.determineElementsWithChanges(quotationModel);

			// merge them into the list of all of the mul_quote_elems we will want to calculate and put them back on the quote
			List<MulQuoteEle> allMulQuoteElements = serviceElementService.getMulQuoteElemsForServiceElements(quotationModel,changedServiceElements);
			quotationModel.getQuotation().setMulQuoteElems(allMulQuoteElements);
		}	
		
		if (updatedCapitalContribution != null) {
			quotationModel.setCapitalContribution(updatedCapitalContribution);
		}
		// get the quotation elements off of the MulQuoteEle list on the quotation (thru the quoteModel) instead of re-querying the DB.
		List<QuotationElement> eligibleQuotationElementList = leaseElementProducer.getEligibleQuotationElementForProcess(quotationModel,false);	
		
		for (QuotationElement quotationElement : eligibleQuotationElementList) {
			
			QuotationElement processedQuotationElement = quotationElementService.getCalculatedQuotationElement(quotationElement,quotationModel,false);
			

			// if this is a quote that should partially calculate
			if(this.isQuoteForPartialCalculation(quotationModel)){
				// check to see if it's an element type that should be recalculated
				String reprocess = quotationElementDAO.findRecalcNeededByLeaseElementId(quotationElement.getLeaseElement().getLelId());
				// if "N" and it is a service element calculate and return the original values
				if(reprocess.equalsIgnoreCase("N") && serviceElementService.isServiceElement(quotationElement)){   // copy or create it from a prior quote model
					this.addOrUpdateQuoteElementFromOrig(quotationModel, processedQuotationElement);
					
					
				}else{ // or add / update the element
					this.addOrUpdateQuoteElementFromCalc(quotationModel, processedQuotationElement);
				}
			}else{ // else we are doing a full calculation/recalculation
				this.addOrUpdateQuoteElementFromCalc(quotationModel,processedQuotationElement);
			}

		}
		// remove any quote elements that has been removed from mul_quote_ele or are selectedInd = 'N'
		this.removeInvalidQuotationElements(quotationModel, eligibleQuotationElementList);
		
		// set amount for calculated cap cost to have only 2 decimal places
		quotationModel.setCalculatedCapCost(CommonCalculations.getRoundedValue(getCapitalCostAmount(quotationModel), CURRENCY_DECIMALS));
		quotationModel.setQuoteCapital(quotationModel.getCalculatedCapCost());
		return quotationModel;
	}
	
	private void addOrUpdateQuoteElementFromCalc(QuotationModel quoteModel, QuotationElement processedQuotationElement){
		// get the element if it already exists on the quotation model
		QuotationElement foundElem = quotationElementService.findQuotationElementIfExists(quoteModel.getQuotationElements(),processedQuotationElement);
		
		if(!MALUtilities.isEmpty(foundElem)){
			// replace the element
			quoteModel.getQuotationElements().remove(foundElem);
			quoteModel.getQuotationElements().add(processedQuotationElement);		
			if (!processedQuotationElement.isCalcRentalApplicable()) {
				quoteModel.setCalcRentalApplicable(false);
			}
		}else{ // else it is a new element
			// add it.
			quoteModel.getQuotationElements().add(processedQuotationElement);
			if (!processedQuotationElement.isCalcRentalApplicable()) {
				quoteModel.setCalcRentalApplicable(false);
			}
		}
	}
	
	private void removeInvalidQuotationElements(QuotationModel quoteModel, List<QuotationElement> eligibleQuotationElementList){
		List<QuotationElement> itemsToRemove = new ArrayList<QuotationElement>();
		
		//find quotation_elements that are not in the eligibleQuotationElementList
		// these would also be not in mul_quote_ele or are in mul_quote_ele but selected_ind = 'N'
		// but would not be a a dealer and model accessory (these are added to this list)
		for(QuotationElement qelFromQuote: quoteModel.getQuotationElements()){
			QuotationElement foundElem = quotationElementService.findQuotationElementIfExists(eligibleQuotationElementList,qelFromQuote);
			//if not found then remove them from the quote, as they are no longer eligible
			if(MALUtilities.isEmpty(foundElem)){
				itemsToRemove.add(qelFromQuote);
			}
		}
		
		for(QuotationElement itemToREmove: itemsToRemove){
			quoteModel.getQuotationElements().remove(itemToREmove);
		}
		
	}
	
	
	//update the element from the original quotation model
	private void addOrUpdateQuoteElementFromOrig(QuotationModel quoteModel, QuotationElement quotationElement) throws MalBusinessException{
		// get the element if it already exists on the quotation model
		QuotationElement foundElem = quotationElementService.findQuotationElementIfExists(quoteModel.getQuotationElements(),quotationElement);
		
		// if found 
		if(!MALUtilities.isEmpty(foundElem)){
			// get the quotation element updated with original values
			QuotationElement updatedElement = quotationElementService.getOriginalQuotationElement(quotationElement,quoteModel);
			//TODO: check this!!
			quoteModel.getQuotationElements().remove(foundElem);
			quoteModel.getQuotationElements().add(updatedElement);	
			
			if (!updatedElement.isCalcRentalApplicable()) {
				quoteModel.setCalcRentalApplicable(false);
			}
		}else{ // else it is a new element
			// create a new quotation element with the original values
			QuotationElement newElement = quotationElementService.getOriginalQuotationElement(quotationElement,quoteModel);
			quoteModel.getQuotationElements().add(newElement);	
			
			if (!newElement.isCalcRentalApplicable()) {
				quoteModel.setCalcRentalApplicable(false);
			}
		}
	}

	

	/**
	 * Saved updated quotation model with rental details in database. It also
	 * updates profitability table with IRR and profitAdjustment.
	 * 
	 * @param calcQuotationModel
	 *            , updated quotation model
	 * @param baseResidual
	 * @param actualIrr
	 * @param hurdleIrr
	 * @param profitAdjustment
	 *            , client profit adjustment
	 * @param isReProcess
	 *            , flag to indicate cleanup of existing data of quotation model
	 */
	

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void saveCalculatedQuote(QuotationModel dbQuotationModel, QuotationModel calcQuotationModel, BigDecimal baseResidual,
			QuotationProfitability quotationProfitability, boolean isReProcess) throws MalBusinessException, Exception {

		try {
						
			QuotationElement mainQuotationElement = getMainQuotationModelElement(calcQuotationModel);
			if (mainQuotationElement != null) {
				mainQuotationElement.setResidualValue(baseResidual);
				mainQuotationElement.setDepreciation(mainQuotationElement.getCapitalCost().subtract(baseResidual));
			}

			// dbQuotationModel.setInterestRate(actualIrr);
			dbQuotationModel.setQuoteCapital(calcQuotationModel.getQuoteCapital());
			dbQuotationModel.setCapitalContribution(calcQuotationModel.getCapitalContribution());
			dbQuotationModel.setResidualValue(baseResidual);
			dbQuotationModel.setReCalcNeeded("N");

			List<QuotationElement> newQuotationElementList = calcQuotationModel.getQuotationElements();
			for (QuotationElement quotationElement : newQuotationElementList) {
				if (quotationElement.getLeaseElement().getElementType().equals(MalConstants.FINANCE_ELEMENT)) {
					BigDecimal avgInterest = quotationElement
							.getRental()
							.divide(quotationElement.getNoRentals(), CommonCalculations.MC)
							.subtract(
									((quotationElement.getCapitalCost().subtract(quotationElement.getResidualValue(), CommonCalculations.MC))
											.divide(quotationElement.getNoRentals(), CommonCalculations.MC)), CommonCalculations.MC);
					quotationElement.setInterest(avgInterest.multiply(quotationElement.getNoRentals(), CommonCalculations.MC));
				}
				
				if (quotationElement.getQuotationElementSteps() != null && quotationElement.getQuotationElementSteps().size() > 0) {
					QuotationElementStep quotationElementStep = quotationElement.getQuotationElementSteps().get(0);
					quotationElementStep.setRentalValue(quotationElement.getRental().divide(quotationElement.getNoRentals(),
							CommonCalculations.MC));
					quotationElementStep.setEndCapital(quotationElement.getResidualValue());

				}
			}

			if (dbQuotationModel.getQuotationElements() != null) {
				dbQuotationModel.getQuotationElements().clear();
				dbQuotationModel.getQuotationElements().addAll(newQuotationElementList);
			} else {
				dbQuotationModel.setQuotationElements(newQuotationElementList);
			}

			// reload the list(s) that could have been already updated from the DB to avoid optimistic locking
			if (dbQuotationModel.getQuotationModelFinances() != null) {
				dbQuotationModel.setQuotationModelFinances(quotationModelFinancesDAO.findByQmdId(dbQuotationModel.getQmdId()));
			}
			if (dbQuotationModel.getQuotationCapitalElements() != null) {
				dbQuotationModel.setQuotationCapitalElements(qotationCapitalElementDAO.findByQmdID(dbQuotationModel.getQmdId()));
			}
	
			quotationModelDAO.saveWithLock(dbQuotationModel);

			profitabilityService.saveQuotationProfitability(quotationProfitability);

		} catch (Exception ex) {
			if (ex instanceof MalBusinessException) {
				throw (MalBusinessException) ex;
			}
			logger.error(ex);
			throw ex;
		}

	}

	/**
	 * Returns main quotation element of quotation model
	 */
	public QuotationElement getMainQuotationModelElement(QuotationModel quotationModel) {
		QuotationElement mainQuotationElement = null;
		for (QuotationElement quotationElement : quotationModel.getQuotationElements()) {
			if (quotationElement.getLeaseElement().getElementType().equals(MalConstants.FINANCE_ELEMENT)
					&& quotationElement.getQuotationDealerAccessory() == null && quotationElement.getQuotationModelAccessory() == null
					&& MalConstants.FLAG_Y.equals(quotationElement.getIncludeYn())) {
				mainQuotationElement = quotationElement;

			}
		}
		return mainQuotationElement;
	}

	/**
	 * Returns total capital cost of quotation element
	 */
	public BigDecimal getCapitalCostAmount(QuotationModel quotationModel) {
		BigDecimal finalCapitalCost = new BigDecimal("0");
		for (QuotationElement quotationElement : quotationModel.getQuotationElements()) {
			if (!quotationElement.getLeaseElement().getElementType().equals(MalConstants.FINANCE_ELEMENT))
				continue;
			if (MalConstants.FLAG_Y.equals(quotationElement.getIncludeYn()) && quotationElement.getCapitalCost() != null) {
				finalCapitalCost = finalCapitalCost.add(quotationElement.getCapitalCost());
			}
		}
		return finalCapitalCost;
	}

	/**
	 * Returns finance lease element cost of quotation model. For editable
	 * quote, we are getting rental from quote element because step might not
	 * present for brand new not calculated quote.
	 * 
	 * @param quotation
	 *            model returns finance lease element cost
	 */
	public BigDecimal getFinanceLeaseElementCostForCE(QuotationModel quotationModel) {

		boolean isQuoteEditable = isQuoteEditable(quotationModel);
		BigDecimal financeCost = BigDecimal.valueOf(MalConstants.DEFAULT_CURRENCY_VALUE);
		BigDecimal bdPeriod = BigDecimal.valueOf(quotationModel.getContractPeriod());

		if (quotationModel != null) {
			List<QuotationElement> quotationElementList = quotationModel.getQuotationElements();

			for (QuotationElement quotationElement : quotationElementList) {
				LeaseElement leaseElement = quotationElement.getLeaseElement();
				if (leaseElement != null && leaseElement.getElementType().equals(MalConstants.FINANCE_ELEMENT)) {
					if (isQuoteEditable) {
						BigDecimal elementRental = quotationElement.getRental();
						if (elementRental != null && elementRental.compareTo(BigDecimal.ZERO) != 0){
							elementRental =  elementRental.divide(quotationElement.getNoRentals() != null ? quotationElement.getNoRentals() : bdPeriod, MC);
							financeCost = financeCost.add(CommonCalculations.getRoundedValue(elementRental, CURRENCY_DECIMALS));
						}
					} else {
						QuotationElementStep step = quotationElement.getQuotationElementSteps().get(0);
						BigDecimal rental = null;
						// Bug16259 Start
						if(step.getManualRental() != null ){
							rental = step.getManualRental();
						}else{                               //Bug16259 End
							rental = step.getRentalValue();
						}
					
						if (rental != null){							
							financeCost = financeCost.add(CommonCalculations.getRoundedValue(rental, CURRENCY_DECIMALS));						   
						}
					}
				}
			}
		}

		return financeCost;

	}

	/**
	 * Calculates sum of all factory/after market equipments
	 * 
	 * @param quotationModel
	 * @return a BigDecimal,sum of residual of all factory/after market
	 *         equipments of Quotation model
	 * @throws MalBusinessException
	 */
	@Transactional(readOnly = true)
	public BigDecimal getEquipmentResidual(QuotationModel quotationModel) throws MalBusinessException {
		BigDecimal equipmentResidual = new BigDecimal(0);
		try {
			quotationModel = quotationModelDAO.findById(quotationModel.getQmdId()).orElse(null);
			if (quotationModel != null) {

				for (QuotationElement qe : quotationModel.getQuotationElements()) {
					if (qe.getQuotationDealerAccessory() != null || qe.getQuotationModelAccessory() != null) {
						if (qe.getResidualValue() != null) {
							equipmentResidual = equipmentResidual.add(qe.getResidualValue());
						}
					}
				}
			}
		} catch (Exception ex) {
			logger.error(ex, "Error occured in getting equipment residual for qmdId =" + quotationModel.getQmdId());
			throw new MalBusinessException("generic.error.occured.while", new String[] { "getting equipment residual." });
		}
		return equipmentResidual;
	}

	/**
	 * Returns true if quotation element is a finance element and but not and
	 * involving VMP formula else return false if VMP processor is need to
	 * calculate rental of quotation model
	 * 
	 * @param quotationModel
	 */
	public boolean isCalcRentalCalculationApplicable(QuotationModel quotationModel) {
		for (QuotationElement quotationElement : quotationModel.getQuotationElements()) {
			LeaseElement leaseElement = quotationElement.getLeaseElement();
			if (leaseElement.getElementType().equals(MalConstants.FINANCE_ELEMENT)
					&& VMP_PROCESSOR_NAME.equals(leaseElement.getProcessorName())) {

				return false;
			}
		}
		return true;
	}
	
	/**
	 * Returns true if possible quotation element is a finance element and but not and
	 * involving VMP formula else return false if VMP processor is need to
	 * calculate rental of quotation model
	 * 
	 * @param Quotation
	 */
	public boolean isCalcRentalCalculationApplicable(Quotation quotation) {
		List<MulQuoteEle> mulQuoteEleList = mulQuoteEleDAO.findMulQuoteEleByQuotationId(quotation.getQuoId());
		
		boolean isCalcRentalCalculationApplicable = true;
		for (MulQuoteEle mulQuoteEle : mulQuoteEleList) {
		
			LeaseElement leaseElement = mulQuoteEle.getLeaseElement() ;
			if(leaseElement != null  && VMP_PROCESSOR_NAME.equals(leaseElement.getProcessorName())){
						isCalcRentalCalculationApplicable = false;
						break;
			}			
		}		
		return isCalcRentalCalculationApplicable;
	}
	

	/**
	 * Return true is VQ is printed for quotation model.
	 * 
	 * @param quotationModelId
	 * @return true/false
	 */
	public boolean isVQPrinted(Long quotationModelId) {
		String vqRefNo = quotationModelDAO.getVQRefNo(quotationModelId, null);
		if (!MALUtilities.isEmpty(vqRefNo)) {
			return true;
		}
		return false;
	}

	/**
	 * Returns true/false, checks if calculation is allowed on quotation model
	 * based on quote status
	 */
	private boolean isQuoteStatusValidForEdit(QuotationModel quotationModel) {
		int quoteStatus = quotationModel != null ? quotationModel.getQuoteStatus() : null;
		if (QUOTE_STATUS_ON_OFFER == quoteStatus || QUOTE_STATUS_AWAITING_CREDIT_REVIEW == quoteStatus
				|| QUOTE_STATUS_REVISED == quoteStatus || QUOTE_STATUS_AUTHORISED == quoteStatus
				|| QUOTE_STATUS_FUTURE_CONTRACT_AMENDMENT == quoteStatus || QUOTE_STATUS_REQUEST_ORDER_REVISION == quoteStatus
				|| QUOTE_STATUS_STANDARD_ORDER_REVISION == quoteStatus && !MALUtilities.convertYNToBoolean(quotationModel.getPrintedInd())) {
			return true;
		}
		return false;
	}

	/**
	 * Returns true or false based on condition. true is quote status is valid
	 * for edit and its VQ is not printed false.
	 */
	public boolean isQuoteEditable(QuotationModel quotationModel) {
		boolean isQuoteStatusValidForEdit = isQuoteStatusValidForEdit(quotationModel);
		boolean isVQprinted = isVQPrinted(quotationModel.getQmdId());
		if (!isVQprinted && isQuoteStatusValidForEdit) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Returns true or false based on condition. true is quote is for a Used
	 * Stock vehicle.
	 */
	public boolean isUsedStock(QuotationModel quotationModel) {
		if (quotationModel.getUsedVehicle().equalsIgnoreCase("Y")) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Returns true or false based on condition. true is quote is for a formal
	 * extension.
	 */
	public boolean isFormalExtension(QuotationModel quotationModel) {
		if (quotationModel.getContractLine() != null) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Returns value of configuration from willow config
	 * 
	 * @param configName
	 * @return value of willow config
	 * @throws MalBusinessException
	 */
	public String getWillowConfigValue(String configName) throws MalBusinessException {
		String configValue = null;

		if (configName != null) {
			WillowConfig willowConfig = willowConfigDAO.findById(configName).orElse(null);
			configValue = willowConfig.getConfigValue();
		}

		return configValue;
	}

	private QuotationElement getMainQuotationModelElement(List<QuotationElement> list) {
		QuotationElement mainQuotationElement = null;
		for (QuotationElement quotationElement : list) {
			if (quotationElement.getLeaseElement().getElementType().equals(MalConstants.FINANCE_ELEMENT)
					&& quotationElement.getQuotationDealerAccessory() == null && quotationElement.getQuotationModelAccessory() == null
					&& MalConstants.FLAG_Y.equals(quotationElement.getIncludeYn())) {
				mainQuotationElement = quotationElement;
			}
		}
		return mainQuotationElement;
	}

	/**
	 * Inserts/updates/deletes a finance parameter on quote based on nvalue in
	 * parameter. If no record exits, creates a new finance parameter. If record
	 * exits, updates existing one. If record exits but nvalue in parameter is
	 * null/empty, existing will be deleted.
	 * 
	 * Side effects; it also updates QuoteCapitalElement
	 */
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void saveFinanceParamOnQuote(BigDecimal finParamNValue, String finParamKey, QuotationModel quotationModel)
			throws MalBusinessException {
		try {
			QuotationModelFinances quotationModelFinancesInDb = quotationModelFinancesDAO.findByQmdIdAndParameterKey(
					quotationModel.getQmdId(), finParamKey);
			BigDecimal finParamNValueInDb = quotationModelFinancesInDb != null ? quotationModelFinancesInDb.getnValue() : null;
			if (finParamNValue != null && finParamNValueInDb == null) {
				// create new
				Date today = Calendar.getInstance().getTime();
				FinanceParameter financeParameter = financeParameterService.getEffectiveFinanceParameterByDate(finParamKey, today);
				if (financeParameter != null) {
					QuotationModelFinances quotationModelFinances = new QuotationModelFinances();
					quotationModelFinances.setnValue(finParamNValue);
					quotationModelFinances.setQuotationModel(quotationModel);
					quotationModelFinances.setStatus(financeParameter.getStatus());
					quotationModelFinances.setParameterId(financeParameter.getParameterId());
					quotationModelFinances.setParameterKey(financeParameter.getParameterKey());
					quotationModelFinances.setDescription(financeParameter.getDescription());
					quotationModelFinances.setEffectiveFromDate(financeParameter.getEffectiveFrom());
					quotationModelFinancesDAO.save(quotationModelFinances);
				} else {
					throw new Exception("No Finance Parameter found");
				}
			} else if (finParamNValue == null && finParamNValueInDb != null) {
				// delete old
				quotationModelFinancesDAO.delete(quotationModelFinancesInDb);
			} else {
				if (finParamNValue != null && finParamNValueInDb != null) {
					if (finParamNValue.compareTo(finParamNValueInDb) != 0) {
						// update
						quotationModelFinancesInDb.setnValue(finParamNValue);
						quotationModelFinancesDAO.save(quotationModelFinancesInDb);
					}
				}
			}
			
			if (finParamKey.equals(MalConstants.FIN_PARAM_OE_INV_ADJ)) {

				// As per open end product configuration,
				// Quotation_Capital_Element entry should be present
				// for OE_INV_ADJ.
				// Here we are just updating the Quotation_Capital_Element
				// default creted by willow for oen end product.
				capitalCostService.updateQuoteCapitalElement(quotationModel.getQmdId(), finParamKey, finParamNValue);
			}else if(finParamKey.equals(MalConstants.FIN_PARAM_AM_ADJ)){

				// As per AM product configuration,
				// Quotation_Capital_Element entry should be present
				// Here we are just updating the Quotation_Capital_Element
				// default created by willow for AM product.
				capitalCostService.updateQuoteCapitalElement(quotationModel.getQmdId(),AM_INVOICE_ADJUSTMENT, finParamNValue);
			}
		} catch (Exception ex) {
			throw new MalBusinessException("generic.error.occured.while", new String[] { "saving finance parameter " + finParamKey });
		}

	}
	// Creating a map of  event period  and amended accessories cost for all amendments done after last revision
	public Map<Long, BigDecimal> getEventPeriodAndAmendedAccessCostAfterLastRev(Long qmdId , Long fmsId){
		
		Map<Long, BigDecimal> eventPeriodAmendmentChargeAfterLastRev = new HashMap<Long, BigDecimal>();
		
		//list is based on rev no ASC
		List<ContractLine> currentContractLines = contractService.getContractLinesOfLastestContract(fmsId);
		int lastRevisonIndex = currentContractLines.size() -1 ;
		Long lastRevisionEventChangePeriod  = 0L;
		
		for (int i = currentContractLines.size() -1 ;  i >= 0 ; i--) {
			QuotationModel  quotationModel = currentContractLines.get(i).getQuotationModel();
			QuoteModelPropertyValue qmpv = quoteModelPropertyValueService.findByNameAndQmdId(QuoteModelPropertyEnum.QUOTE_TYPE.getName() , quotationModel.getQmdId());
			if(qmpv != null && "R".equals(qmpv.getPropertyValue())){					
					lastRevisonIndex = i;
					lastRevisionEventChangePeriod = quotationModel.getContractChangeEventPeriod(); 
					break;
			}
		 }		
		
		BigDecimal amendedQuoteAccessCostAfterLastRev = BigDecimal.ZERO;
		for (int i = lastRevisonIndex + 1 ; i < currentContractLines.size()  ; i++) {		
			QuotationModel  quotationModel = currentContractLines.get(i).getQuotationModel();
			QuoteModelPropertyValue qmpv = quoteModelPropertyValueService.findByNameAndQmdId(QuoteModelPropertyEnum.QUOTE_TYPE.getName() , quotationModel.getQmdId());
			if(qmpv != null && "A".equals(qmpv.getPropertyValue()) ){
					BigDecimal currentAmendedQuoteAccessCost = capitalCostService.getDealerAccessoryCost(quotationModel);
					BigDecimal lastQuoteAccessCost = capitalCostService.getDealerAccessoryCost(currentContractLines.get(i -1).getQuotationModel());
					BigDecimal addedRemovedQuoteAccessCost = currentAmendedQuoteAccessCost.subtract(lastQuoteAccessCost);
					amendedQuoteAccessCostAfterLastRev = amendedQuoteAccessCostAfterLastRev.add(addedRemovedQuoteAccessCost);
			}
		}
		
		if(lastRevisionEventChangePeriod > 0){
			eventPeriodAmendmentChargeAfterLastRev.put(lastRevisionEventChangePeriod, amendedQuoteAccessCostAfterLastRev.negate());
		}
		
		return eventPeriodAmendmentChargeAfterLastRev;
	}
	
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void saveCalculatedRevisedQuoteOE(QuotationModel quotationModel,List<QuotationStepStructureVO> quotationStepResponseList,
												QuotationProfitability quotationProfitability, Map<String, Object> finParamMap)throws MalBusinessException, Exception {
		
		try {
						
			QuotationModel calculatedQuotationModel = new QuotationModel();
			BeanUtils.copyProperties(quotationModel, calculatedQuotationModel);
			//this is needed because we need to create deep copy(new object) not shallow copy.
			calculatedQuotationModel.setQuotationElements(new ArrayList<QuotationElement>(quotationModel.getQuotationElements()));
			this.saveCalculatedQuoteOE(quotationModel, calculatedQuotationModel, quotationStepResponseList,  quotationProfitability, finParamMap);
		
		
		} catch (Exception ex) {
			logger.error(ex);
			throw ex;
		}
		
	}
		
	
	
	
	/**
	 * Saves quotation model with calculated rental in database.
	 * 
	 * @param QuotationModel - quotation model with calculated rental populated in elements.
	 * @param List - list of {@link} QuotationStepStructureVO populated with  start capital, net book value and rental of each element.
	 * @param BigDecimal  base residual
	 * @param BigDecimal   hurdleIrr- calculated irr
	 * @param BigDecimal    profitAdjustment - profit
	 * @param BigDecimal  approvedMinimumIRR
	 * @param boolean isReprocess - true if create fresh records after clean up of old record
	 */
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void saveCalculatedQuoteOE(QuotationModel dbQuotationModel, QuotationModel calcQuotationModel,
			List<QuotationStepStructureVO> quotationStepResponseList,QuotationProfitability quotationProfitability, Map<String, Object> finParamMap)
			throws MalBusinessException, Exception {
		try {
			dbQuotationModel.setReCalcNeeded(calcQuotationModel.getReCalcNeeded());
			dbQuotationModel.setInterestRate(calcQuotationModel.getInterestRate());
			dbQuotationModel.setCapitalContribution(calcQuotationModel.getCapitalContribution());
			dbQuotationModel.setDepreciationFactor(calcQuotationModel.getDepreciationFactor());

			if (quotationStepResponseList.size() > 1) {
				dbQuotationModel.setSteppedCalc("Y");
			} else {
				dbQuotationModel.setSteppedCalc("N");
			}
			dbQuotationModel.setReCalcNeeded("N");

			List<QuotationStepStructure> newStepStructureList = getQuotationStepStructure(calcQuotationModel.getQmdId(),quotationStepResponseList);
			List<QuotationStepStructure> newStepStructureListFinal = new ArrayList<QuotationStepStructure>();
			dbQuotationModel.setQuotationStepStructure(quotationStepStructureDAO.findByQmdId(dbQuotationModel.getQmdId()));
			List<QuotationStepStructure> oldSteps = dbQuotationModel.getQuotationStepStructure();
			for (QuotationStepStructure quotationStepStructure : newStepStructureList) {
				// maintain the coherence of the object graph
				quotationStepStructure.setQuotationModel(dbQuotationModel);
				boolean isExistingStep = false;
				for (QuotationStepStructure quotationStepStructureOld : oldSteps) {
					QuotationStepStructurePK newId = quotationStepStructure.getId();
					QuotationStepStructurePK oldId = quotationStepStructureOld.getId();
					if (newId.getFromPeriod() == oldId.getFromPeriod() && newId.getQmdQmdId() == oldId.getQmdQmdId()) {
						// same step, assign it back to new list
						quotationStepStructureOld.setToPeriod(quotationStepStructure.getToPeriod());
						newStepStructureListFinal.add(quotationStepStructureOld);
						isExistingStep = true;
						break;
					}
				}
				if (!isExistingStep) {
					newStepStructureListFinal.add(quotationStepStructure);
				}
			}

			List<QuotationElement> newQuotationElementList = calcQuotationModel.getQuotationElements();
			List<QuotationElementStep> newQuotationElementStepList = new ArrayList<QuotationElementStep>();
		
			
			populateFinalNetBookValue(newQuotationElementList,  quotationStepResponseList );			 
			populateQuotationElementStep(newQuotationElementList, quotationStepResponseList, newQuotationElementStepList);				 
			updateOEQuotationElement(newQuotationElementList);
			QuotationElement mainQuotationElement = getMainQuotationModelElement(newQuotationElementList);
			dbQuotationModel.setResidualValue(mainQuotationElement.getResidualValue());
			
			dbQuotationModel = updateQuotationModelAccessory(dbQuotationModel ,newQuotationElementList);
			dbQuotationModel = updateQuotationDealerAccessory(dbQuotationModel ,newQuotationElementList);

			if (dbQuotationModel.getQuotationElements() != null) {
				dbQuotationModel.getQuotationElements().clear();
				dbQuotationModel.getQuotationElements().addAll(newQuotationElementList);
			} else {
				dbQuotationModel.setQuotationElements(newQuotationElementList);
			}
			
			if (dbQuotationModel.getQuotationStepStructure() != null) {
				dbQuotationModel.getQuotationStepStructure().clear();
				if (newStepStructureListFinal.size() > 1) {
					dbQuotationModel.getQuotationStepStructure().addAll(newStepStructureListFinal);
				}
			} else {
				if (newStepStructureList.size() > 1) {
					dbQuotationModel.setQuotationStepStructure(newStepStructureListFinal);
				}
			}
			
			updateRentalOnQuotationModel(dbQuotationModel);//This call ideally need to make for close end also.
			quotationModelDAO.saveWithLock(dbQuotationModel);

			// save finance parameters
			if (finParamMap != null && !finParamMap.isEmpty()) {
				Iterator<String> itr = finParamMap.keySet().iterator();
				while (itr.hasNext()) {
					String finParamKey = (String) itr.next();
					BigDecimal finParamNValue = (BigDecimal) finParamMap.get(finParamKey);
					saveFinanceParamOnQuote(finParamNValue, finParamKey, dbQuotationModel);
				}
			}
			if(quotationProfitability != null){
				profitabilityService.saveQuotationProfitability(quotationProfitability);
			}

		} catch (Exception ex) {
			if (ex instanceof MalBusinessException) {
				throw (MalBusinessException) ex;
			}
			logger.error(ex);
			throw ex;
		}
	}
     /*
      * This method update rental on QuotationModel based on calculated element's rental
      */
	private void updateRentalOnQuotationModel(QuotationModel quotationModel){
		
		BigDecimal period = BigDecimal.valueOf(quotationModel.getContractPeriod());
		QuotationElement mainElement = getMainQuotationModelElement(quotationModel.getQuotationElements());
		if (mainElement.getNoRentals() != null) {
			period = mainElement.getNoRentals();
		}

		BigDecimal financeElementsRental = BigDecimal.ZERO;
		BigDecimal serviceElementsRental = BigDecimal.ZERO;

		for (QuotationElement quotationElement : quotationModel.getQuotationElements()) {
			if (MalConstants.FLAG_Y.equals(quotationElement.getIncludeYn())) {
				BigDecimal amount = quotationElement.getRental() != null ? quotationElement.getRental() : BigDecimal.ZERO;
				if (quotationElement.getLeaseElement().getElementType().equals(MalConstants.FINANCE_ELEMENT)) {
					financeElementsRental = financeElementsRental.add(amount);
				} else {
					serviceElementsRental = serviceElementsRental.add(amount);
				}
			}
		}

		quotationModel.setFinanceElementRental(CommonCalculations.getRoundedValue(financeElementsRental.divide(period, CommonCalculations.MC), CURRENCY_DECIMALS));
		quotationModel.setServiceElementRental(CommonCalculations.getRoundedValue(serviceElementsRental.divide(period, CommonCalculations.MC), CURRENCY_DECIMALS));
	}
	
	public boolean isReportsHidden(QuotationModelFinances quotationModelFinances) {
		boolean hideFlag = false;
		//get by IDs for this type of QuoteModelFinances
		//TODO: is it safe to assume that if we've "waived" 1 we've "waived" them all?
		List<FpNoShowReports> flist = fpNoShowReportsDAO.findByParameterId(quotationModelFinances.getParameterId());
		if(flist.size() > 0){
			QmfNoShowReportsPK id = new QmfNoShowReportsPK();
			id.setQmfQmfId(quotationModelFinances.getQmfId());
			id.setFpnsrFpnsrId(flist.get(0).getFpnsr_Id());
			if (qmfNoShowReportsDAO.findById(id).orElse(null) != null) {
				hideFlag = true;
			}else{
				hideFlag = false;
			}
		}
		
		return hideFlag;
	}

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void saveHideInvoiceAdjustment(QuotationModelFinances qmf, boolean hide) throws MalBusinessException {
		if (hide) {
			// hide all possible reports (for that parameter type)
			for (FpNoShowReports f : fpNoShowReportsDAO.findByParameterId(qmf.getParameterId())) {
				QmfNoShowReportsPK qPK = new QmfNoShowReportsPK();
				qPK.setQmfQmfId(qmf.getQmfId());
				qPK.setFpnsrFpnsrId(f.getFpnsr_Id());
				QmfNoShowReports q = new QmfNoShowReports();
				q.setId(qPK);
				qmfNoShowReportsDAO.save(q);
			}
		} else {
			List<QmfNoShowReports> l = qmfNoShowReportsDAO.findByQmfId(qmf.getQmfId());
			qmfNoShowReportsDAO.deleteAll(l);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void updateFinanceParameter(QuotationModelFinances qmf) throws MalBusinessException {
		quotationModelFinancesDAO.save(qmf);
	}

	/**
	 * get quotation step structure data based on steps info ov.
	 */

	private List<QuotationStepStructure> getQuotationStepStructure(Long qmdId, List<QuotationStepStructureVO> newQuoteSteps) {

		List<QuotationStepStructure> newStepStructureList = new ArrayList<QuotationStepStructure>();

		for (QuotationStepStructureVO qssVO : newQuoteSteps) {

			QuotationStepStructurePK id = new QuotationStepStructurePK();
			id.setQmdQmdId(qmdId);
			id.setFromPeriod(qssVO.getFromPeriod());
			QuotationStepStructure quotationStepStructure = new QuotationStepStructure();
			quotationStepStructure.setId(id);
			quotationStepStructure.setToPeriod(new BigDecimal(qssVO.getToPeriod()));

			newStepStructureList.add(quotationStepStructure);
		}

		return newStepStructureList;

	}

	private QuotationModel updateQuotationDealerAccessory(QuotationModel quotationModel ,List<QuotationElement> newQuotationElementList) {

		List<QuotationDealerAccessory> dbDealerAccessoryList = quotationModel.getQuotationDealerAccessories();

		for (QuotationElement quotationElement : newQuotationElementList) {
			QuotationDealerAccessory dealerAccessory = quotationElement.getQuotationDealerAccessory();
			if (dealerAccessory != null) {
				
				for (QuotationDealerAccessory dbQuotationDealerAccessory : dbDealerAccessoryList) {
					if(dbQuotationDealerAccessory.getQdaId().longValue() == dealerAccessory.getQdaId().longValue()){
						dbQuotationDealerAccessory.setResidualAmt(quotationElement.getResidualValue());
						break;
					}
				}
			}
		}

		return quotationModel;
	}

	private QuotationModel updateQuotationModelAccessory(QuotationModel quotationModel , List<QuotationElement> newQuotationElementList) {

		List<QuotationModelAccessory> dbModelAccessoryList = quotationModel.getQuotationModelAccessories();

		for (QuotationElement quotationElement : newQuotationElementList) {
			QuotationModelAccessory modelAccessory = quotationElement.getQuotationModelAccessory();
			if (modelAccessory != null) {
				for (QuotationModelAccessory dbQuotationModelAccessory : dbModelAccessoryList) {
					if(dbQuotationModelAccessory.getQmaId().longValue() == modelAccessory.getQmaId().longValue()){
						dbQuotationModelAccessory.setResidualAmt(quotationElement.getResidualValue());
						break;
					}
				}
				
			}
		}

		return quotationModel;
	}

	private void populateFinalNetBookValue(List<QuotationElement> newQuotationElementList, List<QuotationStepStructureVO> quotationStepResponseList ) {
	
		QuotationStepStructureVO quotationStepStructureVO = quotationStepResponseList.get(quotationStepResponseList.size() -1);
		for (QuotationElement quotationElement : newQuotationElementList) {
				if (quotationElement.getLeaseElement().getElementType().equals(MalConstants.FINANCE_ELEMENT)
//						&& (quotationElement.getQuotationDealerAccessory() != null || quotationElement.getQuotationModelAccessory() != null)
						&& MalConstants.FLAG_Y.equals(quotationElement.getIncludeYn())) {
					for (QuoteElementStepVO quoteElementStepVO : quotationStepStructureVO.getQuoteElementStepVOs()) {
						
						if(quoteElementStepVO.getOriginalCost().doubleValue() == quotationElement.getCapitalCost().doubleValue()){
							quotationElement.setResidualValue(quoteElementStepVO.getEndCapital());
							quotationElement.setFinalPayment(quoteElementStepVO.getEndCapital());
							quotationElement.setDepreciation(quotationElement.getCapitalCost().subtract(quotationElement.getResidualValue()));
						}
					}
				}
			}
	}

	// This method populate rental in Quotation Element based on step rental value
	private void updateOEQuotationElement(List<QuotationElement> newQuotationElementList) {

		for (QuotationElement quotationElement : newQuotationElementList) {
			if (quotationElement.getLeaseElement().getElementType().equals(MalConstants.FINANCE_ELEMENT)
					&& MalConstants.FLAG_Y.equals(quotationElement.getIncludeYn())) {

				BigDecimal totalRental = BigDecimal.ZERO;
				for (QuotationElementStep quotationElementStep : quotationElement.getQuotationElementSteps()) {
					BigDecimal montalyRental = quotationElementStep.getRentalValue();
					BigDecimal periodToMultiply = quotationElementStep.getToPeriod().subtract(quotationElementStep.getFromPeriod()).add(new BigDecimal(1));

					totalRental = totalRental.add(montalyRental.multiply(periodToMultiply, CommonCalculations.MC));
				}
				quotationElement.setRental(totalRental);
				quotationElement.setElementCost(quotationElement.getRental());
				BigDecimal interest = quotationElement.getRental().subtract((quotationElement.getCapitalCost().subtract(quotationElement.getResidualValue(), CommonCalculations.MC)));
				quotationElement.setInterest(interest);
			}
		}
	}

	/**
	 * Calculates sum of all factory/after market equipments for new quote
	 * 
	 * @param quotationModel
	 * @return a BigDecimal,sum of residual of all factory/after market
	 *         equipments of new Quotation model
	 * @throws MalBusinessException
	 */
	@Transactional(readOnly = true)
	public BigDecimal getEquipmentResidualOfNewOEQuote(Long qmdId) throws MalBusinessException {
		BigDecimal equipmentResidual = BigDecimal.ZERO;
		try {
			BigDecimal sumOfDealerAcc = quotationDealerAccessoryDAO.getSumOfResidual(qmdId);
			sumOfDealerAcc = sumOfDealerAcc != null ? sumOfDealerAcc : BigDecimal.ZERO;

			BigDecimal sumOfModelAcc = quotationModelAccessoryDAO.getSumOfResidual(qmdId);
			sumOfModelAcc = sumOfModelAcc != null ? sumOfModelAcc : BigDecimal.ZERO;

			equipmentResidual = equipmentResidual.add(sumOfDealerAcc, MathContext.UNLIMITED).add(sumOfModelAcc, MathContext.UNLIMITED);

		} catch (Exception ex) {
			logger.error(ex, "Error occured in getting equipment residual for qmdId =" + qmdId);
			throw new MalBusinessException("generic.error.occured.while", new String[] { "getting equipment residual." });
		}
		return equipmentResidual;
	}
	// this method cascade save QuotationCapitalElement, quotationModelFinances and quotationModel
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public boolean saveCapitalCostChanges(QuotationModel quotationModel) throws Exception {
		try {
			for (QuotationCapitalElement quotationCapitalElement : quotationModel.getQuotationCapitalElements()) {			
				if (quotationCapitalElement.getCapitalElement().getCode().equalsIgnoreCase(INVOICE_ADJUSTMENT)) {
					QuotationModelFinances invAdjQuotationModelFinance =  null;				
					List<QuotationModelFinances> quotationModelFinancesList =  quotationModel.getQuotationModelFinances();				
						for (QuotationModelFinances qmf : quotationModelFinancesList) {
							if (qmf.getParameterKey().equalsIgnoreCase(INVOICE_ADJUSTMENT)) {
								invAdjQuotationModelFinance = qmf ;
								break;
							}
						}
					
					if(invAdjQuotationModelFinance != null){					
						invAdjQuotationModelFinance.setnValue(quotationCapitalElement.getValue());					
					}else{
						Date today = Calendar.getInstance().getTime();
						FinanceParameter financeParameter = financeParameterService.getEffectiveFinanceParameterByDate(INVOICE_ADJUSTMENT, today);
						if (financeParameter != null) {
							QuotationModelFinances quotationModelFinances = new QuotationModelFinances();
							quotationModelFinances.setnValue(quotationCapitalElement.getValue());
							quotationModelFinances.setQuotationModel(quotationModel);
							quotationModelFinances.setStatus(financeParameter.getStatus());
							quotationModelFinances.setParameterId(financeParameter.getParameterId());
							quotationModelFinances.setParameterKey(financeParameter.getParameterKey());
							quotationModelFinances.setDescription(financeParameter.getDescription());
							quotationModelFinances.setEffectiveFromDate(financeParameter.getEffectiveFrom());
							
							quotationModel.getQuotationModelFinances().add(quotationModelFinances);							
						}
					}					
					break;// break outer loop since we got INVOICE_ADJUSTMENT
				}
			}	
				
			quotationService.updateQuotationModel(quotationModel);
			return true;
		} catch (Exception ex) {
			throw ex;
		}

	}

	//TODO: fixed cost + IRR /vs Rental; are these needed?!
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void calculateAndSaveQuote(QuotationModel dbQuotationModel, QuotationModel calcQuotationModel) throws MalBusinessException {
		try {
			String quoteType = quotationService.getLeaseType(dbQuotationModel.getQmdId());

			// calculate rental
			if ("OE".equals(quoteType)) {			
				calculateOEQuoteAndSave(dbQuotationModel, calcQuotationModel);
			} else{
				calculateCEQuoteAndSave(dbQuotationModel, calcQuotationModel);
			}
		} catch (MalBusinessException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e);
			throw new MalBusinessException("generic.error.occured.while", new String[] { "calculating and saving quote" },e);
		}
	}

	private void calculateCEQuoteAndSave(QuotationModel dbQuotationModel, QuotationModel calcQuotationModel) throws MalBusinessException {
		try {
			QuotationProfitability quotationProfitability = profitabilityService.getQuotationProfitability(dbQuotationModel);
			if (quotationProfitability == null) {
				BigDecimal baseIrr = CommonCalculations.getRoundedValue(getHurdleRate(dbQuotationModel), DECIMAL_DIG_PRECISION_THREE);
				BigDecimal profitAdjustment = CommonCalculations.getRoundedValue(getProfitAdj(dbQuotationModel),
						DECIMAL_DIG_PRECISION_THREE);
				BigDecimal actualIrrValue = baseIrr.add(profitAdjustment, CommonCalculations.MC);

				quotationProfitability = new QuotationProfitability();
				quotationProfitability.setQuotationModel(dbQuotationModel);
				quotationProfitability.setProfitBase(baseIrr);
				quotationProfitability.setProfitAdjustment(profitAdjustment);
				quotationProfitability.setProfitAmount(actualIrrValue);
				quotationProfitability.setProfitSource("N");
				quotationProfitability.setProfitType("P");
			}
			BigDecimal monthlyRental = profitabilityService.calculateMonthlyRental(calcQuotationModel,
					calcQuotationModel.getCalculatedCapCost(), calcQuotationModel.getResidualValue(),
					CommonCalculations.getRoundedValue(quotationProfitability.getProfitAmount(), DECIMAL_DIG_PRECISION_THREE));
			calcQuotationModel.setCalculatedMontlyRental(monthlyRental);
			// save quotation
			saveCalculatedQuote(dbQuotationModel, calcQuotationModel, dbQuotationModel.getResidualValue(), quotationProfitability, true);
		} catch (MalBusinessException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e);
			throw new MalBusinessException("generic.error.occured.while", new String[] { "calculating quote" });
		}

	}

	private void calculateOEQuoteAndSave(QuotationModel dbQuotationModel, QuotationModel calcQuotationModel) throws MalBusinessException {
		try {

			BigDecimal adminFactor = null;
			BigDecimal interestRate = null;
			BigDecimal finalResidual = null;
			BigDecimal customerCost = null;
			BigDecimal invoiceAdjustment = null;
			BigDecimal grdDeliveryRechargeAmount = null;

			QuotationProfitability quotationProfitability = profitabilityService.getQuotationProfitability(dbQuotationModel);
			QuoteVO quoteCost = capitalCostService.resolveAndCalcCapitalCosts(dbQuotationModel);
			customerCost = quoteCost.getTotalCostToPlaceInServiceCustomer();

			finalResidual = BigDecimal.ZERO;
			BigDecimal mainElementResidual = dbQuotationModel.getResidualValue();
			BigDecimal equipmentResidual = getEquipmentResidualOfNewOEQuote(dbQuotationModel.getQmdId());
			finalResidual = mainElementResidual.add(equipmentResidual);

			Double adminFactorFinV = quotationService.getFinanceParam(MalConstants.FIN_PARAM_ADMIN_FACT, dbQuotationModel.getQmdId(),
					dbQuotationModel.getQuotation().getQuotationProfile().getQprId());
			adminFactor = BigDecimal.valueOf(adminFactorFinV);

			Double interestAdjustmentDouble = quotationService.getFinanceParam(MalConstants.FIN_PARAM_INT_ADJ, dbQuotationModel.getQmdId(),
					dbQuotationModel.getQuotation().getQuotationProfile().getQprId());
			BigDecimal interestAdjustment = BigDecimal.valueOf(interestAdjustmentDouble);
			
			interestRate = dbQuotationModel.getInterestRate();
			interestRate = interestRate != null ? interestRate : BigDecimal.ZERO;			

			QuotationModelFinances quotationModelFinancesInDb = financeParameterService.getQuotationModelFinances(
					dbQuotationModel.getQmdId(), MalConstants.FIN_PARAM_GRD_DEL_RECHARGE);
			grdDeliveryRechargeAmount = quotationModelFinancesInDb != null ? quotationModelFinancesInDb.getnValue() : null;

			BigDecimal depreciationFactor = profitabilityService.getDepreciationFactor(customerCost, finalResidual, new BigDecimal(
					dbQuotationModel.getContractPeriod()));
			depreciationFactor = depreciationFactor.movePointRight(2);
			depreciationFactor = depreciationFactor.setScale(DECIMAL_DIG_PRECISION_FIVE, RoundingMode.HALF_UP);

			List<QuotationStepStructureVO> quoteSteps = getQuotationStepStructureVOs(dbQuotationModel);
			
			quoteSteps = profitabilityService.calculateOEStepLease(depreciationFactor.divide(new BigDecimal(100), CommonCalculations.MC), 
					finalResidual , interestRate.divide(new BigDecimal(1200), CommonCalculations.MC), adminFactor, quoteSteps);
			

			Map<String, Object> finParamMap = new HashMap<String, Object>();
			finParamMap.put(MalConstants.FIN_PARAM_ADMIN_FACT, adminFactor);
			finParamMap.put(MalConstants.FIN_PARAM_INT_ADJ, interestAdjustment);
			finParamMap.put(MalConstants.FIN_PARAM_OE_INV_ADJ, invoiceAdjustment);
			finParamMap.put(MalConstants.FIN_PARAM_GRD_DEL_RECHARGE, grdDeliveryRechargeAmount);

			calcQuotationModel.setInterestRate(interestRate);

			saveCalculatedQuoteOE(dbQuotationModel, calcQuotationModel, quoteSteps, quotationProfitability, finParamMap);
		} catch (Exception e) {
			throw new MalBusinessException("generic.error.occured.while", new String[] { "calculating quote" });
		}

	}

	public BigDecimal getHurdleRate(QuotationModel quotationModel) throws MalException {
		BigDecimal baseIRR = new BigDecimal("0.00");
		try {
			baseIRR = quotationModelDAO.getHurdleRate(quotationModel.getQmdId(), null);

		} catch (Exception ex) {
			logger.error(ex);
			throw new MalException("generic.error.occured.while", new String[] { "getting global hurdle IRR" });
		}
		return baseIRR;
	}
	
	public BigDecimal getHurdleRateByTerm(QuotationModel quotationModel, Long term) throws MalException {
		BigDecimal baseIRR = new BigDecimal("0.00");
		try {

			baseIRR = quotationModelDAO.getHurdleRate(quotationModel.getQmdId(), term);
		} catch (Exception ex) {
			logger.error(ex);
			throw new MalException("generic.error.occured.while", new String[] { "getting global hurdle IRR" });
		}
		return baseIRR;
	}
	
	public BigDecimal getProfitAdj(QuotationModel quotationModel) throws MalException {
		Double profitAdj = -1D;
		try {

			profitAdj = quotationService.getFinanceParam(MalConstants.CLIENT_PROFIT_ADJ, quotationModel.getQmdId(), quotationModel
					.getQuotation().getQuotationProfile().getQprId());

			if (profitAdj == null) {
				profitAdj = -1D;
			}
		} catch (Exception ex) {
			logger.error(ex);
			throw new MalException("generic.error.occured.while", new String[] { "getting Profit Adjustment" });
		}
		return BigDecimal.valueOf(profitAdj);
	}

	@Override
	public boolean isQuoteForPartialCalculation(QuotationModel quotationModel) {
		boolean retVal = false;
		
		if(quotationModel.getQuoteStatus() == QUOTE_STATUS_REVISED || 
				quotationModel.getQuoteStatus() == QUOTE_STATUS_REQUEST_ORDER_REVISION || 
				quotationModel.getQuoteStatus() == QUOTE_STATUS_STANDARD_ORDER_REVISION){
			retVal = true; 
		}
		
		return retVal;
	}

	// This method populate QuotationElementStep for QuotationElement
	public void populateQuotationElementStep(List<QuotationElement> newQuotationElementList , List<QuotationStepStructureVO> stepList, List<QuotationElementStep> newAllElementStepList) {
		
		
		for (int step = 0; step < stepList.size(); step++) {

			QuotationStepStructureVO quotationStepStructure = stepList.get(step);
		
		
			for (QuotationElement quotationElement : newQuotationElementList) {
				if (quotationElement.getLeaseElement().getElementType().equals(MalConstants.FINANCE_ELEMENT)
						&& MalConstants.FLAG_Y.equals(quotationElement.getIncludeYn())) {

					QuotationElementStep quotationElementStep = new QuotationElementStep();

					quotationElementStep.setFromPeriod(new BigDecimal(quotationStepStructure.getFromPeriod()));
					quotationElementStep.setToPeriod(new BigDecimal(quotationStepStructure.getToPeriod()));		
					QuoteElementStepVO targetElementStepVO = null;
					for (QuoteElementStepVO quoteElementStepVO : stepList.get(step).getQuoteElementStepVOs()) {	
						// trick to find quote element with out id based on cost. (rental start capital end capital is based on cost only)	
						if(quoteElementStepVO.getOriginalCost().doubleValue() == quotationElement.getCapitalCost().doubleValue()){						
							targetElementStepVO = quoteElementStepVO;
							break;
						}
					}
				
					quotationElementStep.setStartCapital(targetElementStepVO.getStartCapital());
					quotationElementStep.setEndCapital(targetElementStepVO.getEndCapital());
					quotationElementStep.setRentalValue(targetElementStepVO.getRental());
					
					
					newAllElementStepList.add(quotationElementStep);
					if (quotationElement.getQuotationElementSteps() == null) {
						quotationElement.setQuotationElementSteps(new ArrayList<QuotationElementStep>());
					} else {
						if (step == 0) {
							quotationElement.getQuotationElementSteps().clear();
						}
					}
					quotationElementStep.setQuotationElement(quotationElement);
					quotationElement.getQuotationElementSteps().add(quotationElementStep);
					
				}
			}
		}
	}	
	

	public List<QuotationStepStructureVO> getQuotationStepStructureVOs(QuotationModel quotationModel) {		
		List<QuotationStepStructure> quotationStepStructureList = null;
		List<QuotationStepStructureVO> quoteSteps = new ArrayList<QuotationStepStructureVO>();
		
		quotationStepStructureList = quotationModel.getQuotationStepStructure();//ModelStepStructure(quotationModel.getQmdId());

		if (quotationStepStructureList != null && !quotationStepStructureList.isEmpty()) {
			for (QuotationStepStructure quotationStepStructure : quotationStepStructureList) {
				QuotationStepStructureVO qssVO = new QuotationStepStructureVO();
				List<QuoteElementStepVO> quoteElementStepVOs = new ArrayList<>();			
				
				qssVO.setFromPeriod(quotationStepStructure.getId().getFromPeriod());
				qssVO.setToPeriod(quotationStepStructure.getToPeriod().longValue());
				
				for (QuotationElement quotationElement : quotationModel.getQuotationElements()) {
					
					if (quotationElement.getLeaseElement().getElementType().equals(MalConstants.FINANCE_ELEMENT)
							&& MalConstants.FLAG_Y.equals(quotationElement.getIncludeYn())) {
						
						quoteElementStepVOs.add( new QuoteElementStepVO(quotationElement.getCapitalCost(), quotationElement.getCapitalCost(), quotationElement.getResidualValue())) ;
					}
				} 
				
				qssVO.setQuoteElementStepVOs(quoteElementStepVOs);				
				quoteSteps.add(qssVO);
			}

		} else {
			QuotationStepStructureVO qssVO = new QuotationStepStructureVO();
			List<QuoteElementStepVO> quoteElementStepVOs = new ArrayList<>();
			for (QuotationElement quotationElement : quotationModel.getQuotationElements()) {
				if (quotationElement.getLeaseElement().getElementType().equals(MalConstants.FINANCE_ELEMENT)
						&& MalConstants.FLAG_Y.equals(quotationElement.getIncludeYn())) {
					quoteElementStepVOs.add( new QuoteElementStepVO(quotationElement.getCapitalCost(),quotationElement.getCapitalCost(), quotationElement.getResidualValue())) ;
				}
			} 
			
			qssVO.setQuoteElementStepVOs(quoteElementStepVOs);	
			
			qssVO.setFromPeriod(1L);
			qssVO.setToPeriod(quotationModel.getContractPeriod());
			quoteSteps.add(qssVO);
		}		
		
		return quoteSteps;
	}
	
	
	public List<QuotationStepStructureVO> updateQuotationStepStructureWithElementsCost(QuotationModel quotationModel , List<QuotationStepStructureVO>  quoteSteps) {
		
		for (QuotationStepStructureVO quotationStepStructure : quoteSteps) {				
			List<QuoteElementStepVO> quoteElementStepVOs = new ArrayList<>();
			for (QuotationElement quotationElement : quotationModel.getQuotationElements()) {
				
				if (quotationElement.getLeaseElement().getElementType().equals(MalConstants.FINANCE_ELEMENT)
						&& MalConstants.FLAG_Y.equals(quotationElement.getIncludeYn())) {
					BigDecimal capitalCost = quotationElement.getCapitalCost() != null ? quotationElement.getCapitalCost() : BigDecimal.ZERO;
					BigDecimal residual = quotationElement.getResidualValue() != null ? quotationElement.getResidualValue() : BigDecimal.ZERO;
					quoteElementStepVOs.add( new QuoteElementStepVO(capitalCost, capitalCost, residual)) ;
				}
			} 
			
			quotationStepStructure.setQuoteElementStepVOs(quoteElementStepVOs);	
		}
		
		return quoteSteps;
	}

	@Override
	public List<LeaseElement> getLeaseElementByTypeAndProfile(String elementType, Long qprId) {
		
		return leaseElementDAO.getLeaseElementByTypeAndProfile(elementType, qprId);
	}
	
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public QuotationModel setupDataForNewOERevQuote(QuotationModel quotationModel, BigDecimal billedPeriod, 
														BigDecimal depreciationFactor, BigDecimal depreciatedCost) throws MalException {
		
		if(quotationModel.getDepreciationFactor() == null){
			quotationModel.setDepreciationFactor(depreciationFactor);
		}
		
		List<QuotationSchedule> quotationScheduleList = new ArrayList<QuotationSchedule>();
		for (QuotationElement quotationElement : quotationModel.getQuotationElements()) {
			// some time no_rental is null for service element and below line will fix that
			if(quotationElement.getRental() != null && quotationElement.getRental().compareTo(BigDecimal.ZERO) != 0){
				quotationElement.setNoRentals(new BigDecimal(quotationModel.getContractPeriod()));
			}
			
			quotationScheduleList.addAll(quotationElement.getQuotationSchedules());
			
			// depreciate all finance type element's capital cost
			if (quotationElement.getLeaseElement().getElementType().equals(MalConstants.FINANCE_ELEMENT) && MalConstants.FLAG_Y.equals(quotationElement.getIncludeYn())) {
				
				quotationElement.setCapitalCost(profitabilityService.getFinalNBV(quotationElement.getCapitalCost(), quotationModel.getDepreciationFactor(), billedPeriod));
				
				if (quotationElement.getQuotationModelAccessory() != null) {					
					quotationElement.getQuotationModelAccessory().setRechargeAmount(BigDecimal.ZERO);	
					quotationElement.getQuotationModelAccessory().setDiscPrice(quotationElement.getCapitalCost());	
					quotationElement.getQuotationModelAccessory().setTotalPrice(quotationElement.getCapitalCost());
					quotationElement.getQuotationModelAccessory().setBasicPrice(quotationElement.getCapitalCost());
				}else if(quotationElement.getQuotationDealerAccessory() != null){					
					quotationElement.getQuotationDealerAccessory().setRechargeAmount(BigDecimal.ZERO);	
					quotationElement.getQuotationDealerAccessory().setDiscPrice(quotationElement.getCapitalCost());	
					quotationElement.getQuotationDealerAccessory().setTotalPrice(quotationElement.getCapitalCost());
					quotationElement.getQuotationDealerAccessory().setBasicPrice(quotationElement.getCapitalCost());						
				} 
			}
		}		
	
		BigDecimal pennyDiff = depreciatedCost.subtract(getCapitalCostAmount(quotationModel));
		QuotationElement mainQuotationElement = getMainQuotationModelElement(quotationModel.getQuotationElements());
		mainQuotationElement.setCapitalCost(mainQuotationElement.getCapitalCost().add(pennyDiff));//try to match cost to avid penny issue
		
		if(quotationModel.getQuotationCapitalElements() == null){
			quotationModel.setQuotationCapitalElements(new ArrayList<QuotationCapitalElement>());
		}
		
		if(capitalElementService.getQuotationCapitalElement(CapitalElementService.OE_INV_ADJ, quotationModel) ==  null){
			QuotationCapitalElement newQceRevIntAdjustment = capitalElementService.getPopulatedNewCapitalElementObjectByCode(CapitalElementService.OE_INV_ADJ, "QUOTE", quotationModel);
			newQceRevIntAdjustment.setOnInvoice("Y");
			newQceRevIntAdjustment.setFixedAsset("Y");
			quotationModel.getQuotationCapitalElements().add(newQceRevIntAdjustment);
		}
		
		if(capitalElementService.getQuotationCapitalElement(CapitalElementService.OE_REV_INT_ADJ, quotationModel) ==  null){
			QuotationCapitalElement newQceRevIntAdjustment = capitalElementService.getPopulatedNewCapitalElementObjectByCode(CapitalElementService.OE_REV_INT_ADJ, "QUOTE", quotationModel);
			newQceRevIntAdjustment.setOnInvoice("Y");
			newQceRevIntAdjustment.setFixedAsset("Y");
			quotationModel.getQuotationCapitalElements().add(newQceRevIntAdjustment);
		}
		
		if(capitalElementService.getQuotationCapitalElement(CapitalElementService.OE_REV_ASSMNT, quotationModel) ==  null){
			QuotationCapitalElement newQceRevAssmt = capitalElementService.getPopulatedNewCapitalElementObjectByCode(CapitalElementService.OE_REV_ASSMNT, "QUOTE", quotationModel);
			newQceRevAssmt.setFixedAsset("Y");
			newQceRevAssmt.setOnInvoice("Y");
			quotationModel.getQuotationCapitalElements().add(newQceRevAssmt);
		}
		
		// Below cap element user will override in revision UI and reset to zero , so need to moved to basic price	
		BigDecimal depreciatedOEInvAdjRevAssmntRevIntAdj = BigDecimal.ZERO;
		for(QuotationCapitalElement quotationCapitalElement : quotationModel.getQuotationCapitalElements()){
			if(quotationCapitalElement.getValue() != null){
				quotationCapitalElement.setValue(profitabilityService.getFinalNBV(quotationCapitalElement.getValue(), quotationModel.getDepreciationFactor(), billedPeriod));			
			
				if(quotationCapitalElement.getCapitalElement().getCode().equals(CapitalElementService.OE_INV_ADJ)
						|| quotationCapitalElement.getCapitalElement().getCode().equals(CapitalElementService.OE_REV_INT_ADJ)
						|| quotationCapitalElement.getCapitalElement().getCode().equals(CapitalElementService.OE_REV_ASSMNT)){
					
					depreciatedOEInvAdjRevAssmntRevIntAdj = depreciatedOEInvAdjRevAssmntRevIntAdj.add(quotationCapitalElement.getValue());
					quotationCapitalElement.setValue(BigDecimal.ZERO);
				}
			
		 }
		}
		// depreciating quotation model basic price 
		BigDecimal depreciatedBasePrice = profitabilityService.getFinalNBV(quotationModel.getBasePrice(), quotationModel.getDepreciationFactor(), billedPeriod);
		
		quotationModel.setCapitalContribution(BigDecimal.ZERO);
		// adjusting inv adj in base because we do reset value of OE_INV_ADJ field in UI, when revision quote gets created.
	
		depreciatedBasePrice = depreciatedBasePrice.add(depreciatedOEInvAdjRevAssmntRevIntAdj);
		quotationModel.setBasePrice(depreciatedBasePrice);
		
	
		LeaseElementProcessor leaseElementProcessor = (LeaseElementProcessor) SpringAppContext.getBean(mainQuotationElement.getLeaseElement().getProcessorName());
		QuoteCapitalCosts quoteCapitalCosts = leaseElementProcessor.getCapitalCostsCalc();
		List<QuoteCostElementVO> qceVOList = quoteCapitalCosts.calcQuoteCapitalElement(quotationModel.getQuotationCapitalElements());		
		BigDecimal sumQCE = BigDecimal.ZERO;
		for (QuoteCostElementVO quoteCostElementVO : qceVOList) {
			if(quoteCostElementVO.getCustomerCost() != null){
				sumQCE = sumQCE. add(quoteCostElementVO.getCustomerCost());
			}
		}
		
		pennyDiff = mainQuotationElement.getCapitalCost().subtract(sumQCE.add(quotationModel.getBasePrice()));// didn't consider capital contribution since it will be always zero
		quotationModel.setBasePrice(quotationModel.getBasePrice().add(pennyDiff));
		quotationScheduleDAO.deleteAll(quotationScheduleList);		
		quotationModel = quotationModelDAO.save(quotationModel);
		
		return quotationModel;
	}
	//HD-475
	public QuotationModel validateQuotationModelForRental(QuotationModel qmd){
		List<QuotationElement> quoEleList =  new ArrayList<QuotationElement>();
		for (QuotationElement quoEle : qmd.getQuotationElements()) {
			LeaseElement leaseElement = quoEle.getLeaseElement() ;
			if(leaseElement != null  && VMP_FORMULAE.equalsIgnoreCase(leaseElement.getFormulae())){
				quoEle.setCalcRentalApplicable(false);
				qmd.setCalcRentalApplicable(false);
				quoEleList.add(quoEle);
			}else{
				quoEleList.add(quoEle);
			}			
		}		
		qmd.setQuotationElements(quoEleList);
		return qmd;		
	}
}
