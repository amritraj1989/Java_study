package com.mikealbert.service;

import java.math.BigDecimal;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mikealbert.common.CommonCalculations;
import com.mikealbert.common.MalConstants;
import com.mikealbert.common.MalLogger;
import com.mikealbert.common.MalLoggerFactory;
import com.mikealbert.common.MalMessage;
import com.mikealbert.data.dao.FormulaParameterDAO;
import com.mikealbert.data.dao.LeaseElementDAO;
import com.mikealbert.data.dao.MulQuoteEleDAO;
import com.mikealbert.data.dao.QuotationCapitalElementDAO;
import com.mikealbert.data.dao.QuotationElementDAO;
import com.mikealbert.data.dao.QuotationModelDAO;
import com.mikealbert.data.entity.ExternalAccount;
import com.mikealbert.data.entity.FormulaParameter;
import com.mikealbert.data.entity.LeaseElement;
import com.mikealbert.data.entity.MulQuoteEle;
import com.mikealbert.data.entity.QuotationCapitalElement;
import com.mikealbert.data.entity.QuotationDealerAccessory;
import com.mikealbert.data.entity.QuotationElement;
import com.mikealbert.data.entity.QuotationModel;
import com.mikealbert.data.entity.QuotationModelAccessory;
import com.mikealbert.data.vo.QuotationElementAmountsVO;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.rental.processors.LeaseElementProcessor;
import com.mikealbert.rental.processors.LeaseElementProducer;
import com.mikealbert.rental.processors.inputoutput.ProcessorInputType;
import com.mikealbert.rental.processors.inputoutput.ProcessorOutputType;
import com.mikealbert.util.MALUtilities;
import com.mikealbert.util.SpringAppContext;

@Service("quotationElementService")
@Transactional
public class QuotationElementServiceImpl implements QuotationElementService {

	@Resource
	private QuotationService quotationService;
	@Resource
	private LeaseElementProducer leaseElementProducer;
	@Resource
	private FormulaParameterDAO formulaParameterDAO;
	@Resource
	private LeaseElementDAO leaseElementDAO;
	@Resource
	private QuotationElementDAO quoteElementDAO;
	@Resource
	private MalMessage malMessage;
	@Resource QuotationCapitalElementDAO quotationCapitalElementDAO;
	@Resource QuotationModelDAO quotationModelDAO;
	
	@Resource MulQuoteEleDAO mulQuoteEleDAO;
	
	MalLogger logger = MalLoggerFactory.getLogger(this.getClass());	
	
	/***
	 * This method is used to create a calculated new quotation element. 
	 *	
	 */
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public QuotationElement getCalculatedNewQuotationElement(QuotationModel quoteModel,LeaseElement leaseElement ,QuotationModelAccessory modelAccessory,
															QuotationDealerAccessory dealerAccessory ,boolean skipQuoteLvlFinanceParams) throws MalBusinessException {
		
		MulQuoteEle financeFlagedMulQuoteEle = mulQuoteEleDAO.findMulQuoteEleByQuoIdLelId(quoteModel.getQuotation().getQuoId(), leaseElement.getLelId());
		QuotationElement newQuotationElement = leaseElementProducer.getQuotationElement(leaseElement, quoteModel, modelAccessory, dealerAccessory, financeFlagedMulQuoteEle);
				 
		return this.getCalculatedQuotationElement(newQuotationElement, quoteModel, false, null);
	}
	
	/***
	 * This method is used to return a calculated quotation element. 
	 * The quotation element to be passed in is the one we are trying to calculate.
	 * Additionally a quotation model is passed in but only because of distance, period (no of rentals)
	 * and the model id and profile id and external account for use in getting the finance parameters. 
	 */
	@Override
	public QuotationElement getCalculatedQuotationElement(
			QuotationElement quoteElement, QuotationModel quoteModel, boolean skipQuoteLvlFinanceParams) throws MalBusinessException {

		return this.getCalculatedQuotationElement(quoteElement, quoteModel, skipQuoteLvlFinanceParams,null);
	}

	/***
	 * This method is used to return a calculated quotation element. 
	 * The quotation element to be passed in is the one we are trying to calculate.
	 * Additionally a quotation model is passed because of distance, period (no of rentals)
	 * and the model id and profile id.
	 * An ExternalAccount is passed in for account information
	 * both are used in getting the finance parameters. 
	 */
	@Override
	public QuotationElement getCalculatedQuotationElement(
			QuotationElement quoteElement, QuotationModel quoteModel,
			boolean skipQuoteLvlFinanceParams, ExternalAccount extAcct)
			throws MalBusinessException {
		

		// optionally quote model and quote profile id(s)
		Long distance = quoteModel.getContractDistance();
		Long period = this.getNoOfPeriods(quoteElement, quoteModel);
		
		LeaseElement le = leaseElementDAO.findById(quoteElement.getLeaseElement().getLelId()).orElse(null);
		List<FormulaParameter> fmList = formulaParameterDAO.findByLeaseElementId(le.getLelId());
		String processorName = le.getProcessorName();
		// No formula is configured in database
		if (MALUtilities.isEmptyString(processorName)) {
			// return quotationModel;
			throw new MalBusinessException(malMessage.getMessage("field.nullvalue", "Processor Name"));
		}

		LeaseElementProcessor leaseElementProcessor = (LeaseElementProcessor) SpringAppContext.getBean(processorName);
		ProcessorInputType processorInputType = (ProcessorInputType) SpringAppContext.getBean(processorName + "Input");
		processorInputType.setQuotationElement(quoteElement);
		processorInputType.setDistance(distance);
		processorInputType.setPeriod(period);

		if (quoteElement.getQuotationDealerAccessory() != null) {
			processorInputType.setQdaId(quoteElement.getQuotationDealerAccessory().getQdaId());
		}
		if (quoteElement.getQuotationModelAccessory() != null) {
			processorInputType.setQmaId(quoteElement.getQuotationModelAccessory().getQmaId());
		}
		for (FormulaParameter formulaParameter : fmList) {
			String parameterName = formulaParameter.getParameterName();
			String parameterType = formulaParameter.getParameterType();
			Long sequenceNo = formulaParameter.getSequenceNo();
			Double financeParamValue = null;
			if (MalConstants.PARAM_TYPE_F.equals(parameterType)) {
				parameterName = parameterName.replaceAll("/", "").replaceAll("-", "");
				if (parameterName.equals("NA")) {
					financeParamValue = 00.00;
				} else {
					financeParamValue = quotationService.getFinanceParam(parameterName, quoteModel.getQmdId(), quoteModel
							.getQuotation().getQuotationProfile().getQprId(),null,skipQuoteLvlFinanceParams,extAcct);
					if (financeParamValue == null) {
						throw new MalBusinessException(malMessage.getMessage("field.nullvalue", "Finance Param"));
					}
				}
				processorInputType.bindFinanceParamer(sequenceNo.intValue(), new BigDecimal(financeParamValue));
			}
		}
		ProcessorOutputType processorOutputType = leaseElementProcessor.process(processorInputType);
		QuotationElement processedQuotationElement = processorOutputType.getQuotationElement();
		// set rental amount for this element to have only 2 decimal places
		processedQuotationElement
				.setRental(CommonCalculations.getRoundedValue(processedQuotationElement.getRental(), CommonCalculations.CURRENCY_DECIMALS));
		
		return processedQuotationElement;
		
	}

	@Override
	public Long getNoOfPeriods(QuotationElement quoteElement,
			QuotationModel quoteModel) {
		Long period;
		
		period = quoteModel.getContractPeriod();
		if (quoteElement.getNoRentals() != null) {
			period = quoteElement.getNoRentals().longValue();
		}
		
		return period;
	}

	@Override
	public QuotationElement getOriginalQuotationElement(
			QuotationElement quoteElement, QuotationModel quoteModel)
			throws MalBusinessException {
		QuotationElement retVal;
		
		// get the element if it already exists on the quotation model
		retVal = findQuotationElementIfExists(quoteModel.getQuotationElements(),quoteElement);
		
		long contractPeriod	 =  this.getNoOfPeriods(quoteElement, quoteModel);
		
		if(!MALUtilities.isEmpty(retVal)){
			//if found update it with original values
			QuotationElementAmountsVO quoteEleAmtsVO = quoteElementDAO.callQuotationElementOriginalValues(quoteModel.getQmdId(), retVal.getLeaseElement().getLelId());
			this.setElementCosts(retVal,contractPeriod,quoteEleAmtsVO);
		}else{ // else it is a new element
			//TODO: why are we re-querying the DB?
			//LeaseElement le = leaseElementDAO.findOne(quoteElement.getLeaseElement().getLelId());
			
			// find the finace mul quote ele (for use with lease element producer (to produce an empty quotation element)
			MulQuoteEle financeFlagedMulQuoteEle = this.findFinanceMulQuoteEle(quoteModel.getQuotation().getMulQuoteElems());
			
			// TODO: we don't do anything with original prices for quotation model accessories or quotation dealer accessories in this method?!?
			
			retVal = leaseElementProducer.getQuotationElement(quoteElement.getLeaseElement(), quoteModel, null, null, financeFlagedMulQuoteEle);
			QuotationElementAmountsVO quoteEleAmtsVO = quoteElementDAO.callQuotationElementOriginalValues(quoteModel.getQmdId(), retVal.getLeaseElement().getLelId());
			this.setElementCosts(retVal,contractPeriod,quoteEleAmtsVO);	
		}

		return retVal;
	}
	
	public QuotationElement findQuotationElementIfExists(List<QuotationElement> allElements, QuotationElement elementToCheck){
		QuotationElement retVal = null;
		for(QuotationElement elem: allElements){
			// if the lelid's match
			if(elem.getLeaseElement().getLelId() == elementToCheck.getLeaseElement().getLelId()){
				// if the model accessory set, then also use that to compare 
				if(!MALUtilities.isEmpty(elementToCheck.getQuotationModelAccessory()) && !MALUtilities.isEmpty(elem.getQuotationModelAccessory()) 
						&&  elementToCheck.getQuotationModelAccessory().getQmaId().equals(elem.getQuotationModelAccessory().getQmaId())){
					retVal = elem;
					break;
				}
				
				// if the dealer accessory set, then also user that to compare
				if(!MALUtilities.isEmpty(elementToCheck.getQuotationDealerAccessory()) && !MALUtilities.isEmpty(elem.getQuotationDealerAccessory()) 
						&& elementToCheck.getQuotationDealerAccessory().getQdaId().equals(elem.getQuotationDealerAccessory().getQdaId())){
					retVal = elem;
					break;
				}
				
				// if neither has dealer or model accessories then base our match off of just the lelId
				if(MALUtilities.isEmpty(elem.getQuotationDealerAccessory()) && MALUtilities.isEmpty(elem.getQuotationModelAccessory())
						&& MALUtilities.isEmpty(elementToCheck.getQuotationDealerAccessory()) && MALUtilities.isEmpty(elementToCheck.getQuotationModelAccessory())){
					retVal = elem;
					break;
				}
			}
		}
		return retVal;
	}
	
	private void setElementCosts(QuotationElement quoteElement, long contractPeriod, QuotationElementAmountsVO origElemAmtsVO) throws MalBusinessException{
		// nulls?
		if(!MALUtilities.isEmpty(origElemAmtsVO.getRentalAmount())){
			quoteElement.setRental(origElemAmtsVO.getRentalAmount());
		}else{
			throw new MalBusinessException("Original Element Rental Amount is Null for QuoteModel: " + quoteElement.getQuotationModel().getQmdId());
		}
		
		// nulls?
		if(!MALUtilities.isEmpty(origElemAmtsVO.getOverheadAmount())){
			quoteElement.setOverheadAmt(origElemAmtsVO.getOverheadAmount());
		}else{
			throw new MalBusinessException("Original Element Overhead Amount is Null for QuoteModel: " + quoteElement.getQuotationModel().getQmdId());
		}
			
		// nulls?
		if(!MALUtilities.isEmpty(origElemAmtsVO.getProfitAmount())){
			quoteElement.setProfitAmt(origElemAmtsVO.getProfitAmount());
		}else{
			throw new MalBusinessException("Original Element Profit Amount is Null for QuoteModel: " + quoteElement.getQuotationModel().getQmdId());
		}
		
		// nulls?
		if(!MALUtilities.isEmpty(origElemAmtsVO.getElementCost())){
			quoteElement.setElementCost(origElemAmtsVO.getElementCost());
		}else{
			throw new MalBusinessException("Original Element Element Cost is Null for QuoteModel: " + quoteElement.getQuotationModel().getQmdId());
		}
		
	}

	@Override
	public MulQuoteEle findFinanceMulQuoteEle(List<MulQuoteEle> mulQuoteElems) {
		MulQuoteEle financeFlagedMulQuoteEle = null;
		
		for (MulQuoteEle mulQuoteEle : mulQuoteElems) {
			if((mulQuoteEle.getAelId() == null || mulQuoteEle.getPelId() == null) 
			&& MalConstants.FLAG_Y.equals(mulQuoteEle.getSelectedInd())){
				if(mulQuoteEle.getElementType().equals(MalConstants.FINANCE_ELEMENT))
					financeFlagedMulQuoteEle = mulQuoteEle;
			}			
		}

		return financeFlagedMulQuoteEle;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void updateCdfeeDifferenceInCapitalCost(QuotationModel qmd, BigDecimal origCdfeeCost) throws MalBusinessException {
		BigDecimal updatedCdfeeCost = BigDecimal.ZERO;
		
		QuotationCapitalElement qceCdFee = quotationCapitalElementDAO.findByQmdIDAndCapitalElementCode(qmd.getQmdId(), "CD_FEE");
		if(!MALUtilities.isEmpty(qceCdFee) && qceCdFee.getValue() != null){
			updatedCdfeeCost = qceCdFee.getValue();
		}
		try{
			if(updatedCdfeeCost != origCdfeeCost){
				QuotationElement mainQuoteElement = quoteElementDAO.findMainQuoteElement(qmd.getQmdId());
				BigDecimal currentQuoteCapital = BigDecimal.ZERO;
				if(mainQuoteElement != null){
					currentQuoteCapital = mainQuoteElement.getCapitalCost();
					mainQuoteElement.setCapitalCost(currentQuoteCapital.add(updatedCdfeeCost).subtract(origCdfeeCost));
					mainQuoteElement.setDepreciation(mainQuoteElement.getCapitalCost());
					mainQuoteElement.setInterest(mainQuoteElement.getCapitalCost().negate());
					quoteElementDAO.saveAndFlush(mainQuoteElement);
					
					QuotationModel quotationModel = quotationService.getQuotationModel(qmd.getQmdId());
					BigDecimal qmdQuoteCapital = quotationModel.getQuoteCapital();
					quotationModel.setQuoteCapital(qmdQuoteCapital.add(updatedCdfeeCost).subtract(origCdfeeCost));
					quotationModelDAO.saveAndFlush(quotationModel);
					
				}
			}
		}catch(Exception ex){
			logger.error(ex, "Error while updating QuotationElements for qmd Id: " + qmd.getQmdId());
			throw ex;
		}
	}

	@Override
	public String findFinanceElementExistByProfileId(Long qprId, Long cId) {

		return quoteElementDAO.findFinanceElementExistByProfileId(qprId, cId); 
	}
	
	
	
}
