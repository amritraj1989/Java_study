package com.mikealbert.vision.service;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.mikealbert.data.entity.MalCapitalCost;
import com.mikealbert.data.entity.QuotationModel;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.exception.MalException;
import com.mikealbert.service.vo.CapitalCostModeValuesVO;
import com.mikealbert.service.vo.QuoteCost;
import com.mikealbert.vision.vo.CapitalCostsRowVO;

@Transactional
public interface CapitalCostOverviewService {
	
	public QuoteCost getQuoteCost(QuotationModel qm) throws MalBusinessException;
	public QuoteCost getQuoteCost2(QuotationModel qm) throws MalBusinessException;
	
	public List<MalCapitalCost>   getMalCapitalCostByDoc(Long  docId);
	public List<MalCapitalCost>   getMalCapitalCostByFinalizeQuote(Long  qmdId);
	
	public void saveCapitalCost(QuotationModel finalizeQuotationModel ,  Long invoiceHeaderId , List<CapitalCostsRowVO> capitalCostsRowVOList , boolean finalizeMode ) throws MalBusinessException ,MalException;
	
	public static final String CD_FEE  = "CD_FEE" ;
	
	public static final String FIXED_COST_FLAG  = "F" ;
	
	
	public static final String FACTORY_EQUIPMENT_INV  = "FACTORY" ;
	public static final String AFTER_MARKET_DEALER_INV  = "DEALER" ;
	public static final String BASE_MODEL_INV  = "MODEL" ;
	public static final String quoteType  = "MODEL" ;

	public static final String STANDARD_QUOTE_TYPE  = "STANDARD_QUOTE_TYPE" ;
	public static final String ACCEPTED_QUOTE_TYPE  = "ACCEPTED_QUOTE_TYPE" ;
	public static final String FINALIZE_QUOTE_TYPE  = "FINALIZE_QUOTE_TYPE" ;
}
