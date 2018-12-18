package com.mikealbert.service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mikealbert.data.dao.CapEleParameterDAO;
import com.mikealbert.data.dao.CapitalEleSourceCodeDAO;
import com.mikealbert.data.dao.CapitalElementDAO;
import com.mikealbert.data.entity.CapEleParameter;
import com.mikealbert.data.entity.CapitalElement;
import com.mikealbert.data.entity.DeliveryCost;
import com.mikealbert.data.entity.FinanceParameter;
import com.mikealbert.data.entity.Model;
import com.mikealbert.data.entity.QuotationCapitalElement;
import com.mikealbert.data.entity.QuotationModel;
import com.mikealbert.exception.MalException;
import com.mikealbert.util.MALUtilities;

@Service("capitalElementService")
@Transactional(readOnly = true)
public class CapitalElementServiceImpl implements  CapitalElementService{	
	
	@Resource private CapitalElementDAO capitalElementDAO;
	@Resource private CapEleParameterDAO capEleParameterDAO;
	@Resource private FinanceParameterService financeParameterService;
	@Resource CapitalEleSourceCodeDAO capitalEleSourceCodeDAO;		

	
	/**
	 * Retrieves the capital element based on its code.
	 * 
	 * @param Capital Element's Code
	 * @return the capital element with the code
	 */
	@Override
	public CapitalElement getCapitalElementByCode(String code) {
		return capitalElementDAO.findByCode(code);
	}
	
	/**
	 * Determines the freight charge based on the trim.
	 * 
	 * This data is maintained by Autodata's data feed.
	 * 
	 * @param Model 
	 * @return Cost for freight
	 */
	@Override
	public BigDecimal getFreightCost(Model trim) {
		BigDecimal cost = BigDecimal.ZERO;
		
		Collections.sort(trim.getDeliveryCosts(), new Comparator<DeliveryCost>() { 
			public int compare(DeliveryCost op2, DeliveryCost op1) { 
				return op2.getEffectiveFrom().compareTo(op1.getEffectiveFrom()); 
			}
		});	
		
		for(DeliveryCost deliveryCost : trim.getDeliveryCosts()) {
			if(deliveryCost.getEffectiveFrom().compareTo(new Date()) <= 0) {
				if(MALUtilities.isEmpty(deliveryCost.getEffectiveTo()) 
						|| deliveryCost.getEffectiveTo().compareTo(new Date()) >= 0) {
					cost = deliveryCost.getActualCost();
					break;
				}
			}
		}
		
		return cost;
	}
	
	/**
	 * Gets the fixed amount from the capital element's finance parameter.
	 * 
	 * @param cel Capital Element
	 * @return The nvalue of the capital element's 'F'ixed finance parameter
	 * 
	 */
	public BigDecimal getFixedAmount(CapitalElement cel) throws MalException{
		List<CapEleParameter> capEleParameters;
		FinanceParameter financeParameter;		
	
		capEleParameters = capEleParameterDAO.findByCelIdAndParameterType(cel.getCelId(), "F");
		if(capEleParameters.isEmpty() || capEleParameters.size() != 1) {
			throw new MalException("Unable to find capital element finance parameter mapping for celId = " + cel.getCelId());
		}
		
		financeParameter = financeParameterService.findByParameterKey(capEleParameters.get(0).getParameterName());
		if(MALUtilities.isEmpty(financeParameter)) {
			throw new MalException("Unable to find finance parameter for celId = " + cel.getCelId());			
		}
		
		return financeParameter.getNvalue();
	}

	@Override
	public List<QuotationCapitalElement> getApplicableCapitalElementsWithValue(Long qprId, String standardEDINo, Long corporateId, String accountType, String accountCode, String orderType) {

		return capitalElementDAO.getApplicableCapitalElementsWithValue(qprId, standardEDINo, corporateId, accountType, accountCode, orderType);
	}
	
	@Override
	public BigDecimal getCapitalElementValue(Long celId, Long cerId, Long qprId, 
											Long modelId, Long corporateId, String accountType, 
											String accountCode, String quoteElemCalculation) throws MalException{
		return capitalElementDAO.getCapitalElementValue(celId, cerId, qprId, modelId, corporateId, accountType, accountCode, quoteElemCalculation);
	}
	
	public QuotationCapitalElement getQuotationCapitalElement(String code, QuotationModel quotationModel) {
		
		QuotationCapitalElement targetQCE = null;
		for(QuotationCapitalElement quotationCapitalElement : quotationModel.getQuotationCapitalElements()){
			if(quotationCapitalElement.getCapitalElement().getCode().equals(code)){
				targetQCE = quotationCapitalElement;
			}
		}
		
		return targetQCE;
	}

	public QuotationCapitalElement getPopulatedNewCapitalElementObjectByCode(String code, String source, QuotationModel quotationModel) {
		
		
		QuotationCapitalElement newQCE = new QuotationCapitalElement();
		
		newQCE.setQuotationModel(quotationModel);
		newQCE.setValue(BigDecimal.ZERO);
		newQCE.setCapitalElement(getCapitalElementByCode(code));
		newQCE.setReclaimActioned("N");
		newQCE.setQuoteCapital("N");
		newQCE.setPurchaseOrder("N");
		newQCE.setFixedAsset("N");
		newQCE.setQuoteConcealed("N");
		newQCE.setOnInvoice("N");
		newQCE.setCapitalEleSourceCode(capitalEleSourceCodeDAO.findById(source).orElse(null));		
		
		return newQCE;
	}

	@Override
	public List<QuotationCapitalElement> getApplicableCapitalElementList(Long qprId, String standardEDINo) {
		 return capitalElementDAO.getApplicableCapitalElementList(qprId, standardEDINo);
	}

	@Override
	public QuotationCapitalElement getCapitalElementWithValue(Long cerId, String standardEDINo, Long corporateId,
			String accountType, String accountCode, String orderType) {

		return capitalElementDAO.getCapitalElementWithValue(cerId, standardEDINo, corporateId, accountType, accountCode, orderType);
	}	
}
