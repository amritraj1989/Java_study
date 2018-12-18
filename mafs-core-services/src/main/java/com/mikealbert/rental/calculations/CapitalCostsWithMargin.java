package com.mikealbert.rental.calculations;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Component;

import com.mikealbert.common.MalConstants;
import com.mikealbert.data.entity.QuotationCapitalElement;
import com.mikealbert.data.entity.QuotationDealerAccessory;
import com.mikealbert.data.entity.QuotationModel;
import com.mikealbert.data.entity.QuotationModelAccessory;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.exception.MalException;
import com.mikealbert.service.QuotationService;
import com.mikealbert.service.vo.QuoteCost;
import com.mikealbert.service.vo.QuoteCostElementVO;
import com.mikealbert.util.MALUtilities;

@Component("capitalCostsWithMargin")
public final class CapitalCostsWithMargin extends QuoteCapitalCosts {
	
	@Override
	public QuoteCost calcQuotationCost(BigDecimal basePrice, BigDecimal capitalContribution, List<QuotationCapitalElement> qceList) {
		BigDecimal customerCost;
		BigDecimal dealCost;
		BigDecimal dealEleSum = new BigDecimal(0);
		BigDecimal customerEleSum = new BigDecimal(0);
		QuoteCostElementVO quoteCostElementVO = null;
		
	    for (QuotationCapitalElement qCapitalElement : qceList) {
	    	quoteCostElementVO = this.calcQuoteCapitalElement(qCapitalElement);
	    	dealEleSum = dealEleSum.add(quoteCostElementVO.getDealCost());
	    	customerEleSum = customerEleSum.add(quoteCostElementVO.getCustomerCost());
	    }
	    basePrice = basePrice != null ? basePrice : new BigDecimal(0);
	    
	    capitalContribution = capitalContribution != null ? capitalContribution : new BigDecimal(0);
	    customerCost = basePrice.add(customerEleSum).subtract(capitalContribution);
	    dealCost = customerCost;
	    QuoteCost costs = new QuoteCost();
	    costs.setDealCost(dealCost);
	    costs.setCustomerCost(customerCost);
	    return costs;
	}
	
	@Override
	public QuoteCostElementVO calcQuoteCapitalElement(
			QuotationCapitalElement qce) {
		
		QuoteCostElementVO elementCosts = super.createCapitalElementVO(qce);
		
		if(MalConstants.FLAG_Y.equals(super.getOverriddenQuoteCapitalFlag(qce.getCapitalElement().getCelId(), qce))){
			elementCosts.setDealCost(super.getCapitalElementDealCost(qce));
			//this only applies to finalized quotes!!
			if(super.isFinalizedNonFormalExtQuote(qce.getQuotationModel())){
				//change costs once we have finalized based upon margin
				if(qce.getCapitalElement().getLfMarginOnly().equalsIgnoreCase("Y")){
					// this has to be the value off of the quote that was last accepted
					try {
						BigDecimal acceptedCost = this.getAcceptedQuotationCapitalElementCost(qce);
						if(!MALUtilities.isEmpty(acceptedCost)){
							elementCosts.setCustomerCost(this.getAcceptedQuotationCapitalElementCost(qce));
						}else{
							elementCosts.setCustomerCost(super.getCapitalElementCost(qce));
						}
					} catch (MalBusinessException e) {
						throw new MalException("generic.error.occured.while", 
								new String[] { " getting quotation capital customer cost off of the prior accepted quote for quote capital element (qce_id) : " + qce.getQceId()}, e);
					}
				}else{
					elementCosts.setCustomerCost(super.getCapitalElementCost(qce));
				}
			}else{
				elementCosts.setCustomerCost(super.getCapitalElementCost(qce));
			}
			
			
		}else{
			elementCosts.setDealCost(new BigDecimal(0));
			elementCosts.setCustomerCost(new BigDecimal(0));
		}
		
		return elementCosts;
	}
	
	@Override
	public QuoteCostElementVO calcDealerAccessoryElement(
			QuotationDealerAccessory dealerAccessory) {
		
		QuoteCostElementVO elementCosts = super.createDealerAccessoryElementVO(dealerAccessory);
		elementCosts.setRechargeAmt(super.getDealerAccessoryRechargeAmt(dealerAccessory));
		elementCosts.setCustomerCost(super.getDealerAccessoryClientCost(dealerAccessory,elementCosts.getRechargeAmt()));
		elementCosts.setDealCost(super.getDealerAccessoryDealCost(dealerAccessory, elementCosts.getCustomerCost()));
		
		return elementCosts;
	}
	
	@Override
	public QuoteCostElementVO calcModelAccessoryElement(
			QuotationModelAccessory modelAccessory) {
		
		QuoteCostElementVO elementCosts = super.createModelAccessoryElementVO(modelAccessory);
		elementCosts.setRechargeAmt(super.getModelAccessoryRechargeAmt(modelAccessory));
		elementCosts.setCustomerCost(super.getModelAccessoryClientCost(modelAccessory,elementCosts.getRechargeAmt()));
		elementCosts.setDealCost(super.getModelAccessoryDealCost(modelAccessory,elementCosts.getCustomerCost()));
		
		return elementCosts;
	}
}
