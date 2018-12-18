package com.mikealbert.rental.calculations;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import com.mikealbert.data.dao.CapitalElementDAO;
import com.mikealbert.data.dao.MalCapitalCostDAO;
import com.mikealbert.data.entity.CapitalElement;
import com.mikealbert.data.entity.MalCapitalCost;
import com.mikealbert.data.entity.QuotationCapitalElement;
import com.mikealbert.data.entity.QuotationCapitalElementBackup;
//import com.mikealbert.data.entity.QuotationCapitalElementBackup;
import com.mikealbert.data.entity.QuotationDealerAccessory;
import com.mikealbert.data.entity.QuotationModel;
import com.mikealbert.data.entity.QuotationModelAccessory;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.service.CapitalCostElementService;
import com.mikealbert.service.QuotationService;
import com.mikealbert.service.enumeration.NonCapitalElementEnum;
import com.mikealbert.service.vo.QuoteCost;
import com.mikealbert.service.vo.QuoteCostElementVO;
import com.mikealbert.util.MALUtilities;

public abstract class QuoteCapitalCosts {
	@Resource CapitalCostElementService capitalCostElementService;
	@Resource CapitalElementDAO capitalElementDAO;	
	@Resource MalCapitalCostDAO malCapitalCostDAO;
	@Resource QuotationService quotationService;
	
	public abstract QuoteCost calcQuotationCost(BigDecimal basePrice, BigDecimal capitalContribution, List<QuotationCapitalElement> qceList);
	
	public abstract QuoteCostElementVO calcQuoteCapitalElement(QuotationCapitalElement qce);
	public abstract QuoteCostElementVO calcDealerAccessoryElement(QuotationDealerAccessory dealerAccessory);
	public abstract QuoteCostElementVO calcModelAccessoryElement(QuotationModelAccessory modelAccessory);
	
	
	public  List<QuoteCostElementVO> calcQuoteCapitalElement(List<QuotationCapitalElement> qceList){		
		List<QuoteCostElementVO>  qceVOList = new ArrayList<QuoteCostElementVO>();
		for (QuotationCapitalElement qce : qceList) {
			qceVOList.add(calcQuoteCapitalElement(qce));
		}		
		return qceVOList;
	}
	
	public QuoteCostElementVO calcCapitalCostOther(QuotationModel quotationModel, QuoteCostElementVO targetElement){
		if (targetElement.getElementName().equalsIgnoreCase(NonCapitalElementEnum.BASE_VEHICLE_ELEMENT.getElementName())) {
			targetElement.setCustomerCost(quotationModel.getBasePrice());
			targetElement.setDealCost(targetElement.getCustomerCost());
			
			if(this.isFinalizedNonFormalExtQuote(quotationModel)){
				// overwrite the deal cost if there is anything to overwrite it with
				List<MalCapitalCost> malCapitalCostList = malCapitalCostDAO.findMalCapitalCostByFinalizeQuote(quotationModel.getQmdId());
				if(malCapitalCostList != null && malCapitalCostList.size() > 0){
					for (MalCapitalCost malCapitalCost : malCapitalCostList) {
						if(malCapitalCost.getElementType().equals("MODEL")){
							targetElement.setDealCost(malCapitalCost.getTotalPrice());
							break;
						}
					}
				}
			}
		} else if (targetElement.getElementName().equalsIgnoreCase(
			// capital contribution is not show on the capital cost overview page
			// the user cannot change this on UNTREC; there is nothing broken out for it by element_type in MAL_CAPITAL_COST
			// so in all scenarios we skip looking there.
				NonCapitalElementEnum.CAPITAL_CONTRIBUTION_ELEMENT.getElementName())) {
			BigDecimal capitalContribution = quotationModel.getCapitalContribution();
			if (capitalContribution != null) {
				targetElement.setDealCost(capitalContribution.multiply(new BigDecimal(-1)));
				targetElement.setCustomerCost(targetElement.getDealCost());
			}else{
				targetElement.setDealCost(BigDecimal.ZERO);
				targetElement.setCustomerCost(BigDecimal.ZERO);
			}
		} else{
			targetElement.setDealCost(BigDecimal.ZERO);
			targetElement.setCustomerCost(BigDecimal.ZERO);
		}
		
		return targetElement;
	}
	
	protected QuoteCostElementVO createCapitalElementVO(QuotationCapitalElement quoteCapitalElement){
		QuoteCostElementVO quoteCostElementVO = capitalCostElementService.createCapitalElementVO(null, 0, 0, quoteCapitalElement.getCapitalElement());
		quoteCostElementVO.setQuotationCapitalElementId(quoteCapitalElement.getQceId());
		return quoteCostElementVO;
	}
	
	protected QuoteCostElementVO createDealerAccessoryElementVO(QuotationDealerAccessory dealerAccessory){
		QuoteCostElementVO quoteCostElementVO = capitalCostElementService.createDealerAccessoryVO(0, 0, dealerAccessory);
		return quoteCostElementVO;
	}
	
	protected QuoteCostElementVO createModelAccessoryElementVO(QuotationModelAccessory modelAccessory){
		QuoteCostElementVO quoteCostElementVO = capitalCostElementService.createModelAccessoryVO(0, 0, modelAccessory);
		return quoteCostElementVO;
	}
	
	protected String getOverriddenQuoteCapitalFlag(Long celId, QuotationCapitalElement quotationCapitalElement) {

		String quoteCapitalFlag = quotationCapitalElement.getQuoteCapital();
		if (quoteCapitalFlag == null && celId != null) {
			CapitalElement capitalElement = capitalElementDAO.findById(celId).orElse(null);
			quoteCapitalFlag = capitalElement.getQuoteCapital();
		}
		return quoteCapitalFlag;
	}
	
	protected String getOverriddenOninvoiceFlag(Long celId, QuotationCapitalElement quotationCapitalElement) {

		String onInvoiceFlag = quotationCapitalElement.getOnInvoice();
		if (onInvoiceFlag == null && celId != null) {
			CapitalElement capitalElement = capitalElementDAO.findById(celId).orElse(null);
			onInvoiceFlag = (capitalElement.getOnInvoice() != null ? capitalElement.getOnInvoice() : "N");
		}
		return onInvoiceFlag;
	}
	
	protected String getOverriddenFixedAssetFlag(Long celId, QuotationCapitalElement quotationCapitalElement) {

		String fixedAssetFlag = quotationCapitalElement.getFixedAsset();
		if (fixedAssetFlag == null && celId != null) {
			CapitalElement capitalElement = capitalElementDAO.findById(celId).orElse(null);
			fixedAssetFlag = (capitalElement.getFixedAsset() != null ? capitalElement.getFixedAsset() : "N");
		}
		return fixedAssetFlag;
	}
	
 
	// this is used to get the deal/"Mike Albert" cost which is sometimes the same as the client cost depending upon the
	// calc strategy used (how the flags on QM209 are interpreted).
	protected BigDecimal getCapitalElementDealCost(QuotationCapitalElement qce){
		// look in the MalCapitalCost first if there is $ there then use them
		// otherwise get the value from the QuotationCapitalElement
		BigDecimal cost;
		
		if(this.isFinalizedNonFormalExtQuote(qce.getQuotationModel())){
			MalCapitalCost finalCapCost = this.findMalCapCostForQuoteCapitalElement(qce);
			if(!MALUtilities.isEmpty(finalCapCost)){
				cost = finalCapCost.getTotalPrice();
			}else{
				cost = this.getCapitalElementCost(qce);
			}
		}else{
			//return the value from the backup if there is one.
			cost = this.getCapitalElementCost(qce);
		}
		
		return cost;
	}
	
	// this is used to get the client cost (or as the default strategy)
	protected BigDecimal getCapitalElementCost(QuotationCapitalElement qce){
		// look in the MalCapitalCost first if there is $ there then use them
		// otherwise get the value from the QuotationCapitalElement
		BigDecimal cost;
		if(!quotationService.hasFinalizedQuote(qce.getQuotationModel().getQmdId())){
			//return the value from the backup if there is one.
			QuotationCapitalElementBackup qceBackup = null;
			if(!MALUtilities.isEmpty(qce.getQuotationModel().getQuotationCapitalElementsBackup())){
				for(QuotationCapitalElementBackup elemBackup : qce.getQuotationModel().getQuotationCapitalElementsBackup()){
					if(elemBackup.getCapitalElement().getCelId() == qce.getCapitalElement().getCelId()){
						qceBackup = elemBackup;
						break;
					}
				}
			}
			
			if(!MALUtilities.isEmpty(qceBackup)){
				cost = qceBackup.getValue() != null ? qceBackup.getValue(): new BigDecimal(0);
			}else{
				//else return the values from quote capital elements
				cost = qce.getValue() != null ? qce.getValue(): new BigDecimal(0);
			}
			
		}else{
			//else return the values from quote capital elements
			// this condition gets hit for formal extension quotes; as well as for client costs for finalized quotes; for deal costs of finalized quotes see getCapitalElementDealCost
			cost = qce.getValue() != null ? qce.getValue(): new BigDecimal(0);
		}

		return cost;
	}
	
	protected BigDecimal getModelAccessoryDealCost(QuotationModelAccessory modelAccessory, BigDecimal customerCost){
		if(this.isFinalizedNonFormalExtQuote(modelAccessory.getQuotationModel())){
			MalCapitalCost finalCapCost = this.findMalCapCostForModelAccessory(modelAccessory);
			if(!MALUtilities.isEmpty(finalCapCost)){
				return finalCapCost.getTotalPrice();
			}else{
				return customerCost;
			}
		}else{
			return customerCost;
		}
	}
	
	protected BigDecimal getDealerAccessoryDealCost(QuotationDealerAccessory dealerAccessory, BigDecimal customerCost){
		if(this.isFinalizedNonFormalExtQuote(dealerAccessory.getQuotationModel())){
			MalCapitalCost finalCapCost = this.findMalCapCostForDealerAccessory(dealerAccessory);
			if(!MALUtilities.isEmpty(finalCapCost)){
				return finalCapCost.getTotalPrice();
			}else{
				return customerCost;
			}
		}else{
			return customerCost;
		}
	}
	
	protected BigDecimal getModelAccessoryClientCost(QuotationModelAccessory modelAccessory, BigDecimal rechargeAmt){
		return modelAccessory.getDiscPrice().subtract(rechargeAmt);
	}
	
	protected BigDecimal getDealerAccessoryClientCost(QuotationDealerAccessory dealerAccessory, BigDecimal rechargeAmt){
		return dealerAccessory.getDiscPrice().subtract(rechargeAmt);
	}
	
	protected BigDecimal getModelAccessoryRechargeAmt(QuotationModelAccessory modelAccessory){
		return modelAccessory.getRechargeAmount() != null ? modelAccessory.getRechargeAmount() : new BigDecimal(0);
	}
	
	protected BigDecimal getDealerAccessoryRechargeAmt(QuotationDealerAccessory dealerAccessory){
		return dealerAccessory.getRechargeAmount() != null ? dealerAccessory.getRechargeAmount() : new BigDecimal(0);
	}
	
	private MalCapitalCost findMalCapCostForQuoteCapitalElement(QuotationCapitalElement quotationCapitalElement){
		MalCapitalCost retVal = null;
		
		List<MalCapitalCost> malCapitalCostList = malCapitalCostDAO.findMalCapitalCostByFinalizeQuote(quotationCapitalElement.getQuotationModel().getQmdId());
		
		if(malCapitalCostList != null && malCapitalCostList.size() > 0){
			for (MalCapitalCost malCapitalCost : malCapitalCostList) {
				if(malCapitalCost.getElementId() != null  && quotationCapitalElement.getCapitalElement() != null){
					if(malCapitalCost.getElementId() == quotationCapitalElement.getCapitalElement().getCelId()){
						retVal =  malCapitalCost;
						break;
					}		
				}
			}
		}
		
		return retVal;
	}
	
	private MalCapitalCost findMalCapCostForDealerAccessory(QuotationDealerAccessory dealerAccessory){
		MalCapitalCost retVal = null;
		
		List<MalCapitalCost> malCapitalCostList = malCapitalCostDAO.findMalCapitalCostByFinalizeQuote(dealerAccessory.getQuotationModel().getQmdId());
		
		if(malCapitalCostList != null && malCapitalCostList.size() > 0){
			for (MalCapitalCost malCapitalCost : malCapitalCostList) {
				if(malCapitalCost.getElementId() != null  && dealerAccessory.getDealerAccessory() != null && malCapitalCost.getElementType().equalsIgnoreCase("DEALER")){
					if(malCapitalCost.getElementId().equals(dealerAccessory.getDealerAccessory().getDacId())){
						retVal =  malCapitalCost;
						break;
					}		
				}
			}
		}
		
		return retVal;
	}
	
	private MalCapitalCost findMalCapCostForModelAccessory(QuotationModelAccessory modelAccessory){
		MalCapitalCost retVal = null;
		
		List<MalCapitalCost> malCapitalCostList = malCapitalCostDAO.findMalCapitalCostByFinalizeQuote(modelAccessory.getQuotationModel().getQmdId());
		
		if(malCapitalCostList != null && malCapitalCostList.size() > 0){
			for (MalCapitalCost malCapitalCost : malCapitalCostList) {
				if(malCapitalCost.getElementId() != null  && modelAccessory.getOptionalAccessory() != null && malCapitalCost.getElementType().equalsIgnoreCase("FACTORY")){
					if(malCapitalCost.getElementId().equals(modelAccessory.getOptionalAccessory().getOacId())){
						retVal =  malCapitalCost;
						break;
					}		
				}
			}
		}
		
		return retVal;
	}
	
	
	protected BigDecimal getAcceptedQuotationCapitalElementCost(QuotationCapitalElement qce) throws MalBusinessException{
		BigDecimal cost = null;
		
		QuotationModel acceptedQuoteModel = null;
		
		// if revision
		// then get the status 3 quote for quo_id as the "prior accepted quote"
		if(qce.getQuotationModel().getQuoteStatus() == QuotationService.STATUS_REVISION){
			acceptedQuoteModel = quotationService.getAcceptedQuoteFromRevision(qce.getQuotationModel());			
		// else
		}else{
			QuotationModel finalizedQuote = quotationService.getQuotationModel(quotationService.getFinalizeQmd(qce.getQuotationModel().getQmdId()));
			if(!MALUtilities.isEmpty(finalizedQuote.getOriginalQmdId())){
				acceptedQuoteModel = quotationService.getQuotationModel(finalizedQuote.getOriginalQmdId());
			}
		}
			
		if(!MALUtilities.isEmpty(acceptedQuoteModel)){
			for(QuotationCapitalElement elem: acceptedQuoteModel.getQuotationCapitalElements()){
				if(elem.getCapitalElement().getCelId() == qce.getCapitalElement().getCelId()){
					// get the capital element cost off of the corresponding element from the accepted quote
					cost = this.getCapitalElementCost(elem);
					break;
				}
			}
		}
		
		return cost;
	}	
		
	/** This method is used to identify quotes that have been finalized but not formal extensions (because these need to be shown with the "current" costs.)
	 *  or quotes getting ready to be finalized going through UNTREC (quote status 4 / revised quotes);  
	 **/
	protected boolean isFinalizedNonFormalExtQuote(QuotationModel quotationModel){
		if((quotationService.hasFinalizedQuote(quotationModel.getQmdId()) && (!quotationService.isFormalExtension(quotationModel))) 
				|| (quotationModel.getQuoteStatus() == QuotationService.STATUS_REVISION && quotationService.hasPriorAcceptedQuoteModel(quotationModel.getQmdId()))){
			
			return true;
		}else{
			return false;
		}
	}
}
