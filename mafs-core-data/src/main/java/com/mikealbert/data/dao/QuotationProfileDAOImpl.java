package com.mikealbert.data.dao;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;

import org.springframework.transaction.annotation.Transactional;

import com.mikealbert.common.MalLogger;
import com.mikealbert.common.MalLoggerFactory;
import com.mikealbert.data.entity.ExternalAccount;
import com.mikealbert.data.entity.QuotationProfile;
import com.mikealbert.util.MALUtilities;

public class QuotationProfileDAOImpl extends GenericDAOImpl<QuotationProfile, Long> implements QuotationProfileDAOCustom{

	private static final long serialVersionUID = 1L;
	private MalLogger logger = MalLoggerFactory.getLogger(QuotationProfileDAOImpl.class);

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<QuotationProfile> fetchCustomerQuotationProfiles(long corpId, String accountType, String accountCode){
		List<QuotationProfile> quotationProfileList = null;
		
		String queryString = "select * from quotation_profiles where qpr_id in (select qpr_qpr_id from quote_profile_cust where ea_c_id =:corpId and ea_account_type =:accountType and ea_account_code =:accountCode and effective_to is null)";
		
		Query query = entityManager.createNativeQuery(queryString, QuotationProfile.class);
		
		query.setParameter("corpId", corpId);
		query.setParameter("accountType", accountType);
		query.setParameter("accountCode", accountCode);
		
		quotationProfileList = query.getResultList();
		
		return quotationProfileList;
	}
	
	public Long countQuotationProfilesByLeaseElement(long leaseElementId, ExternalAccount parentAccount) {
		Query query = queryQuotationProfilesByLeaseElement(leaseElementId, parentAccount, true);
		
		return ((BigDecimal) query.getSingleResult()).longValue();
		
	}
	
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<QuotationProfile> getQuotationProfilesByLeaseElement(long leaseElementId, ExternalAccount parentAccount) {
		List<QuotationProfile> quotationProfileList = null;
		Query query = queryQuotationProfilesByLeaseElement(leaseElementId, parentAccount, false);
		quotationProfileList = query.getResultList();
		return quotationProfileList;
	}
	
	public Query queryQuotationProfilesByLeaseElement(long leaseElementId, ExternalAccount parentAccount, Boolean countOnly) {
		Map<String,Object> parameterMap = new HashMap<String,Object>();
		Query query = null;
		StringBuilder sqlStmt = new StringBuilder("");
		
		if (countOnly) {
			sqlStmt.append("SELECT COUNT(qpr.qpr_id)");
		} else {
			sqlStmt.append("SELECT qpr.*");
		}

		sqlStmt.append("  FROM quote_profile_cust qpc, quotation_profiles qpr" +
					   " WHERE qpr.qpr_id = qpc.qpr_qpr_id" +
					   "   AND qpr.effective_from <= SYSDATE" +
					   "   AND (qpr.effective_to IS NULL OR qpr.effective_to >= SYSDATE)" +
					   "   AND qpc.ea_c_id = :parentAccountCId" +
					   "   AND qpc.ea_account_type = :parentAccountType" +
					   "   AND qpc.ea_account_code = :parentAccountCode" +
					   "   AND EXISTS (SELECT 1" +
					   "                 FROM additional_elements" +
					   "                WHERE qpr_qpr_id = qpr.qpr_id" +
					   "                  AND lel_id = :lelId)");
		
        if (!countOnly) {
        	sqlStmt.append(" ORDER BY qpr.profile_code");
		}
		
		logger.debug("Final Query: " + sqlStmt.toString());
		
		if (!countOnly) {
			query = entityManager.createNativeQuery(sqlStmt.toString(), QuotationProfile.class);
		} else {
			query = entityManager.createNativeQuery(sqlStmt.toString());
		}
		
		if(!MALUtilities.isEmpty(parentAccount)){	            
			parameterMap.put("parentAccountCId", parentAccount.getExternalAccountPK().getCId());
			parameterMap.put("parentAccountType", parentAccount.getExternalAccountPK().getAccountType());
			parameterMap.put("parentAccountCode", parentAccount.getExternalAccountPK().getAccountCode());
		}
		
		
		if(!MALUtilities.isEmpty(leaseElementId)){	            
			parameterMap.put("lelId", leaseElementId);
		}
		
		for (String paramName : parameterMap.keySet()) {
			query.setParameter(paramName, parameterMap.get(paramName));
		}
		
		return query;
	}
	
	@Override
	public String getProductTypeByProfile(Long qprId) {
		
		String stmt = "SELECT prd.product_type FROM quotation_profiles qpr, products prd, product_type_codes ptc "
					 + "WHERE qpr.qpr_id = :qprId  AND qpr.prd_product_code = prd.product_code  AND prd.product_type = ptc.product_type";
		
		Query query = entityManager.createNativeQuery(stmt);
		query.setParameter("qprId", qprId);

		return (String) query.getSingleResult();
	}


	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<QuotationProfile> fetchCustomerQuotationProfilesByCriteria(Long cId, String accountType, String accountCode, String productCode){
		List<QuotationProfile> quotationProfileList = null;
		
		String queryString = "SELECT * "
				+ "FROM quotation_profiles "
				+ "WHERE qpr_id in (SELECT qpr_qpr_id "
				+ "					FROM quote_profile_cust "
				+ "					WHERE ea_c_id =:cId "
				+ "					AND ea_account_type =:accountType "
				+ "					AND ea_account_code =:accountCode) "
				+ "					AND prd_product_code =:productCode "		
				+ "                 AND profile_status = 'APPROVED' "
				+ "					AND (effective_to IS NULL OR effective_to >= SYSDATE)"
				+ "                 ORDER BY effective_from DESC, profile_code ASC";
		
		Query query = entityManager.createNativeQuery(queryString, QuotationProfile.class);
		
		query.setParameter("cId", cId);
		query.setParameter("accountType", accountType);
		query.setParameter("accountCode", accountCode);
		query.setParameter("productCode", productCode);
		quotationProfileList = query.getResultList();
		
		return quotationProfileList;
	}

}
