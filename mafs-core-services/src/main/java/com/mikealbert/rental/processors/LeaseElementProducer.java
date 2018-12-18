package com.mikealbert.rental.processors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mikealbert.common.MalConstants;
import com.mikealbert.common.MalLogger;
import com.mikealbert.common.MalLoggerFactory;
import com.mikealbert.data.dao.MulQuoteEleDAO;
import com.mikealbert.data.dao.TaxCodeDAO;
import com.mikealbert.data.entity.LeaseElement;
import com.mikealbert.data.entity.MulQuoteEle;
import com.mikealbert.data.entity.MulQuoteOpt;
import com.mikealbert.data.entity.QuotationDealerAccessory;
import com.mikealbert.data.entity.QuotationElement;
import com.mikealbert.data.entity.QuotationModel;
import com.mikealbert.data.entity.QuotationModelAccessory;
import com.mikealbert.data.entity.QuoteElementParameter;
import com.mikealbert.data.entity.TaxCode;
import com.mikealbert.data.vo.ServiceElementsVO;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.service.ServiceElementService;
import com.mikealbert.util.MALUtilities;

@Transactional
@Service("leaseElementProducer")

public class LeaseElementProducer {
	 MalLogger logger = MalLoggerFactory.getLogger(this.getClass());
	@Resource MulQuoteEleDAO mulQuoteEleDAO;
	@Resource ServiceElementService serviceElementService;
	@Resource TaxCodeDAO taxCodeDao;
	
	public List<QuotationElement> getEligibleQuotationElementForProcess(QuotationModel quotationModel, boolean requeryQuoteElemsFromDB) throws MalBusinessException {
		try{
			List<MulQuoteEle> mulQuoteEleList;
			if(requeryQuoteElemsFromDB){
				mulQuoteEleList = mulQuoteEleDAO.findMulQuoteEleByQuotationId(quotationModel.getQuotation().getQuoId());
			}else{
				mulQuoteEleList = quotationModel.getQuotation().getMulQuoteElems();
			}
			
			
			List<ServiceElementsVO>  serviceElementsList  = serviceElementService.getAvailableServiceElements(quotationModel);
		
			for (ServiceElementsVO serviceElementsVO : serviceElementsList) {
				if(serviceElementsVO.getTaxId() != null){
					 TaxCode txCode = taxCodeDao.findById(serviceElementsVO.getTaxId()).orElse(null);
					 if(txCode != null){
						 serviceElementsVO.setTaxCode(txCode.getTaxCode());
						 serviceElementsVO.setTaxRate(txCode.getTaxRate());
						 
					 }
				}
			}
			
			
			List<QuotationElement>  eligibleQuotationElementList  = new ArrayList<QuotationElement>();
			Map<LeaseElement,MulQuoteEle >  leaseMulQuoteElementMap  = new HashMap<LeaseElement,MulQuoteEle>();
			
			MulQuoteEle financeFlagedMulQuoteEle = null;
			for (MulQuoteEle mulQuoteEle : mulQuoteEleList) {
				if((mulQuoteEle.getAelId() == null || mulQuoteEle.getPelId() == null) 
				&& MalConstants.FLAG_Y.equals(mulQuoteEle.getSelectedInd())){
					leaseMulQuoteElementMap.put(mulQuoteEle.getLeaseElement(), mulQuoteEle);
					if(mulQuoteEle.getElementType().equals(MalConstants.FINANCE_ELEMENT))
						financeFlagedMulQuoteEle = mulQuoteEle;
				}			
			}
			
			for (Map.Entry<LeaseElement, MulQuoteEle>  leaseMulQuoteEntry : leaseMulQuoteElementMap.entrySet()) {
				QuotationElement newQuotationElement = getQuotationElement(leaseMulQuoteEntry.getKey() ,quotationModel, null, null, leaseMulQuoteElementMap.get(leaseMulQuoteEntry.getKey()) , serviceElementsList);
				eligibleQuotationElementList.add(newQuotationElement);				
			}
			
			 
			for (QuotationDealerAccessory quotationDealerAccessory : quotationModel.getQuotationDealerAccessories()) {
				
				if(quotationDealerAccessory.getBasicPrice().doubleValue() != quotationDealerAccessory.getRechargeAmount().doubleValue()   
						&& quotationDealerAccessory.getFreeOfChargeYn().equals(MalConstants.FLAG_N)){			
					
					QuotationElement newQuotationElement = getQuotationElement(financeFlagedMulQuoteEle.getLeaseElement(), quotationModel, null, quotationDealerAccessory, financeFlagedMulQuoteEle, serviceElementsList);					
					eligibleQuotationElementList.add(newQuotationElement);	
					
				}
			}
			
			for (QuotationModelAccessory quotationModelAccessory : quotationModel.getQuotationModelAccessories()) {
			
				if(quotationModelAccessory.getBasicPrice().doubleValue() != quotationModelAccessory.getRechargeAmount().doubleValue()   
						&& quotationModelAccessory.getFreeOfChargeYn().equals(MalConstants.FLAG_N)){
				
					QuotationElement newQuotationElement = getQuotationElement(financeFlagedMulQuoteEle.getLeaseElement(), quotationModel, quotationModelAccessory, null, financeFlagedMulQuoteEle, serviceElementsList);					
					eligibleQuotationElementList.add(newQuotationElement);
				}	
			}
			
				
			return eligibleQuotationElementList;
			
		}catch(Exception ex){
			logger.error(ex);
			throw new MalBusinessException("generic.error",new String[]{"Error occured in LeaseElementProducer"},ex);
		}
		
	}
	
	@Deprecated
	public List<QuotationElement> getEligibleQuotationElementForProcess(QuotationModel quotationModel) throws MalBusinessException {
		try{
			
			return this.getEligibleQuotationElementForProcess(quotationModel, true);
			
		}catch(Exception ex){
			logger.error(ex);
			throw new MalBusinessException("generic.error",new String[]{"Error occured in LeaseElementProducer"},ex);
		}
		
	}
	
	private QuotationElement getQuotationElement( LeaseElement eligibleLeaseElement , QuotationModel quotationModel , QuotationModelAccessory quotationModelAccessory,
						QuotationDealerAccessory quotationDealerAccessory ,MulQuoteEle mulQuoteEle , List<ServiceElementsVO>  serviceElementsList) throws MalBusinessException{
		
		try{
			
			QuotationElement newQuotationElement = new QuotationElement();
			newQuotationElement.setLeaseElement(eligibleLeaseElement);
			
			if(serviceElementsList != null){
				for (ServiceElementsVO serviceElementsVO : serviceElementsList) {
					if( serviceElementsVO.getLelId() == eligibleLeaseElement.getLelId()){
						newQuotationElement.setBillingOptions(serviceElementsVO.getBillingOptions());					
						newQuotationElement.setTaxCode(serviceElementsVO.getTaxCode());						
						newQuotationElement.setTaxRate(serviceElementsVO.getTaxRate());					
					}
				}
			}
			if(MALUtilities.isEmpty(newQuotationElement.getTaxCode())){
				newQuotationElement.setTaxCode(mulQuoteEle.getTaxCode());				
			}
			if(MALUtilities.isEmpty(newQuotationElement.getTaxRate())){
				newQuotationElement.setTaxRate(mulQuoteEle.getTaxRate());
			}
			
			newQuotationElement.setQuotationModel(quotationModel);
			newQuotationElement.setQuotationModelAccessory(quotationModelAccessory);
			newQuotationElement.setQuotationDealerAccessory(quotationDealerAccessory);			
			newQuotationElement.setPoRequiredYn(eligibleLeaseElement.getPoRequiredYn());
		
			newQuotationElement.setMandatoryYn(MalConstants.FLAG_Y);
			newQuotationElement.setIncludeYn(MalConstants.FLAG_Y);
			newQuotationElement.setAcceptedInd(MalConstants.FLAG_N);
			newQuotationElement.setFinalPaymentSel(MalConstants.FLAG_N);
			
			newQuotationElement = loadQuoteElementParameters(newQuotationElement, mulQuoteEle);
			return newQuotationElement;
		}catch(Exception ex){
			logger.error(ex);
			throw new MalBusinessException("generic.error",new String[]{"Error occured in LeaseElementProducer"},ex);
		}
		
	}
	
	
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public QuotationElement getQuotationElement( LeaseElement eligibleLeaseElement , QuotationModel quotationModel , QuotationModelAccessory quotationModelAccessory,
			QuotationDealerAccessory quotationDealerAccessory ,MulQuoteEle mulQuoteEle) throws MalBusinessException{
		
		QuotationElement newQuotationElement  = null;		
		try{
			
			
			if(eligibleLeaseElement.getElementType().equals(MalConstants.FINANCE_ELEMENT)){
				
				newQuotationElement = getQuotationElement(eligibleLeaseElement, quotationModel, quotationModelAccessory, quotationDealerAccessory, mulQuoteEle, null );
				
			}else{
				
				List<ServiceElementsVO>  serviceElementsList  = serviceElementService.getAvailableServiceElements(quotationModel);				
				for (ServiceElementsVO serviceElementsVO : serviceElementsList) {
					if( serviceElementsVO.getLelId() == eligibleLeaseElement.getLelId()){					
						TaxCode txCode = taxCodeDao.findById(serviceElementsVO.getTaxId()).orElse(null);
						if(!MALUtilities.isEmpty(txCode)){
							serviceElementsVO.setTaxCode(txCode.getTaxCode());
							serviceElementsVO.setTaxRate(txCode.getTaxRate());
						}
					}
				}
				
				newQuotationElement = getQuotationElement(eligibleLeaseElement, quotationModel, quotationModelAccessory, quotationDealerAccessory, mulQuoteEle, serviceElementsList);
			}			
			
		}catch(Exception ex){
			logger.error(ex);
			throw new MalBusinessException("generic.error",new String[]{"Error occured in LeaseElementProducer"},ex);
		}
		
		return newQuotationElement;
	}
	

	private QuotationElement loadQuoteElementParameters(QuotationElement qe , MulQuoteEle mqe ) {			
			
			qe.setQuoteElementParameters(new ArrayList<QuoteElementParameter>());			
			for(MulQuoteOpt mqo : mqe.getMulQuoteOpts()) {
					QuoteElementParameter quoteElementParameter = new QuoteElementParameter();
					quoteElementParameter.setFprFormulaParameter(mqo.getFprFprId());
					quoteElementParameter.setPoParameterOptions(mqo.getPoxPoxId());						
					quoteElementParameter.setQuotationElement(qe);
					qe.getQuoteElementParameters().add(quoteElementParameter);							
				}							
					
		return qe;
	}

}
