package com.mikealbert.rental.calculations;

import java.math.BigDecimal;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.mikealbert.common.MalConstants;
import com.mikealbert.data.dao.DoclDAO;
import com.mikealbert.data.entity.QuotationCapitalElement;
import com.mikealbert.data.entity.QuotationDealerAccessory;
import com.mikealbert.data.entity.QuotationModelAccessory;
import com.mikealbert.service.vo.QuoteCost;
import com.mikealbert.service.vo.QuoteCostElementVO;

@Component("capitalClientInvoiceCosts")
public final class CapitalClientInvoiceCosts extends QuoteCapitalCosts {
	@Resource DoclDAO doclDAO;
	
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
	    // deal cost does not subtract capitalContributions
	    dealCost = basePrice.add(dealEleSum);
	    QuoteCost costs = new QuoteCost();
	    costs.setDealCost(dealCost);
	    costs.setCustomerCost(customerCost);
	    return costs;
	}

	@Override
	public QuoteCostElementVO calcQuoteCapitalElement(
			QuotationCapitalElement qce) {
		
		QuoteCostElementVO elementCosts = super.createCapitalElementVO(qce);
		
		//customer/capital cost
		if(super.getOverriddenOninvoiceFlag(qce.getCapitalElement().getCelId(), qce).equalsIgnoreCase("Y")){
			elementCosts.setCustomerCost(super.getCapitalElementCost(qce));
		}else{
			elementCosts.setCustomerCost(new BigDecimal(0));
		}

		//deal cost
		if(MalConstants.FLAG_Y.equals(super.getOverriddenQuoteCapitalFlag(qce.getCapitalElement().getCelId(), qce))){
			elementCosts.setDealCost(super.getCapitalElementDealCost(qce));
		}else{
			elementCosts.setDealCost(new BigDecimal(0));
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
