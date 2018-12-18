package com.mikealbert.data.dao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.persistence.Query;

import com.mikealbert.common.MalLogger;
import com.mikealbert.common.MalLoggerFactory;
import com.mikealbert.data.entity.QuotationProfileFinance;
import com.mikealbert.data.entity.QuotationProfileFinancePK;
import com.mikealbert.data.vo.QuotationProfileFinanceVO;
import com.mikealbert.util.MALUtilities;

public class QuotationProfileFinanceDAOImpl extends GenericDAOImpl<QuotationProfileFinance, Long> implements QuotationProfileFinanceDAOCustom {
	
	@Resource
	private QuotationProfileFinanceDAO quotationProfileFinanceDAO;
	
	private static final long serialVersionUID = 1L;
	private MalLogger logger = MalLoggerFactory.getLogger(ServiceProviderDAOImpl.class);

	public int countFinanceParamsByClientAndParameter(Long cId, String accountType, String accountCode, Long parameterId) {
		
		Query countQuery = generateQuotationProfileFinanceQuery(cId, accountType, accountCode, parameterId, true);
		BigDecimal count = (BigDecimal) countQuery.getSingleResult();
		
		return count.intValue();
	}
	
	public List<QuotationProfileFinanceVO> getFinanceParamsByClientAndParameter(Long cId, String accountType, String accountCode, Long parameterId) {
		Query query = generateQuotationProfileFinanceQuery(cId, accountType, accountCode, parameterId, false);
		
		List<QuotationProfileFinanceVO>  quotationProfileFinanceList = new ArrayList<QuotationProfileFinanceVO>();;
		@SuppressWarnings("unchecked")
		List<Object[]> objectList  = (List<Object[]>) query.getResultList();
		if (objectList != null && objectList.size() > 0) {	
			
			for (Iterator<Object[]> iterator = objectList.iterator(); iterator.hasNext();) {
				Object[] object = iterator.next();				
				QuotationProfileFinanceVO quotationProfileFinanceVO = new QuotationProfileFinanceVO();
				
				quotationProfileFinanceVO.setProfileCode(object[0]						!= null ? (String) object[0] 		: null);
				quotationProfileFinanceVO.setProfileDescription(object[1]				!= null ? (String) object[1] 		: null);
				quotationProfileFinanceVO.setProfileFinanceNumericValue(object[2]		!= null ? (BigDecimal) object[2] 	: null);
				quotationProfileFinanceVO.setProfileFinanceCharValue(object[3]			!= null ? (String) object[3] 		: null);
				
				quotationProfileFinanceList.add(quotationProfileFinanceVO);
			}
		}
		
		return quotationProfileFinanceList;
	}

	private Query generateQuotationProfileFinanceQuery(Long cId, String accountType, String accountCode, Long parameterId, boolean isCountOnly) {
		Query query =  null;
		
		Map<String,Object> parameterMap = new HashMap<String,Object>();
		
		StringBuilder sqlStmt = new StringBuilder("");
		
		if(isCountOnly){
			sqlStmt.append("SELECT COUNT(1)");
		}
		else{
			sqlStmt.append("SELECT qpr.profile_code, qpr.description, qpf.nvalue, qpf.cvalue");
		}
                
		sqlStmt.append( "  FROM quotation_profile_finances qpf, quote_profile_cust qpc, external_accounts ea, quotation_profiles qpr, finance_parameters fp" +
			            " WHERE qpc.ea_c_id = ea.c_id" +
			            "   AND qpc.ea_account_type = ea.account_type" +
			            "   AND qpc.ea_account_code = ea.account_code" +
			            "   AND qpc.qpr_qpr_id = qpf.qpr_qpr_id" +
			            "   AND qpc.qpr_qpr_id = qpr.qpr_id" +
			            "   AND fp.parameter_id = qpf.parameter_id");
		
		if(!MALUtilities.isEmpty(cId)){	            
			sqlStmt.append( "   AND ea.c_id = :cId");
		}
		
		if(MALUtilities.isNotEmptyString(accountType)){	            
			sqlStmt.append("   AND ea.account_type = :accountType");
		}
		
		if(MALUtilities.isNotEmptyString(accountCode)){	            
			sqlStmt.append( "   AND ea.account_code = :accountCode");
		}
		
		if(!MALUtilities.isEmpty(parameterId)){	            
			sqlStmt.append( "      AND fp.parameter_id = :parameterId");
		}

		sqlStmt.append( " ORDER BY qpr.profile_code");

		logger.debug("Final Query: " + sqlStmt.toString());  
		query = entityManager.createNativeQuery(sqlStmt.toString());	

		if(!MALUtilities.isEmpty(cId)){	
			parameterMap.put("cId", cId);
		}
		
		if(MALUtilities.isNotEmptyString(accountType)){	            
			parameterMap.put("accountType", accountType);
		}

		if(MALUtilities.isNotEmptyString(accountCode)){	            
			parameterMap.put("accountCode", accountCode);
		}
		
		if(!MALUtilities.isEmpty(parameterId)){	            
			parameterMap.put("parameterId", parameterId);
		}
		
		for (String paramName : parameterMap.keySet()) {
			query.setParameter(paramName, parameterMap.get(paramName));
		} 

		return query;
	}

	
}
