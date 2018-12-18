package com.mikealbert.data.dao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.mikealbert.data.DataUtilities;
import com.mikealbert.data.entity.ExternalAccount;
import com.mikealbert.data.entity.ExternalAccountPK;
import com.mikealbert.data.entity.Model;
import com.mikealbert.data.vo.UpfitterSearchCriteriaVO;
import com.mikealbert.data.vo.UpfitterSearchResultVO;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.exception.MalException;
import com.mikealbert.util.MALUtilities;
import javax.annotation.Resource;


public  class ExternalAccountDAOImpl extends GenericDAOImpl<ExternalAccount, ExternalAccountPK> implements ExternalAccountDAOCustom {

	@Resource
	ExternalAccountDAO externalAccountDAO;

	private static final long serialVersionUID = -9111678744683834203L;
	
	public List<UpfitterSearchResultVO> findUpfitters(UpfitterSearchCriteriaVO searchCriteria, Pageable pageable, Sort sort){
		List<UpfitterSearchResultVO> results = null;
		Query query = null;

		query = generateUpfitterSearchQuery(searchCriteria, sort, false);
		
		if(pageable != null){
			query.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
			query.setMaxResults(pageable.getPageSize());
		}		
		
		@SuppressWarnings("unchecked")
		List<Object[]>resultList = (List<Object[]>)query.getResultList();
		if(resultList != null){
			results = new ArrayList<UpfitterSearchResultVO>();
			
			for(Object[] record : resultList){
				int i = 0;
				
				UpfitterSearchResultVO result = new UpfitterSearchResultVO();
				result.setPayeeCorporateId(((BigDecimal)record[i]).longValue());	
				result.setPayeeAccountType((String)record[i+=1]);					
				result.setPayeeAccountCode((String)record[i+=1]);
				result.setPayeeAccountName((String)record[i+=1]);
				result.setPayeeAccountStatus((String)record[i+=1]);					
				result.setPayeeAddress1((String)record[i+=1]);				
				result.setPayeeAddress2((String)record[i+=1]);
				result.setPayeeAddress3((String)record[i+=1]);
				result.setPayeeCity((String)record[i+=1]);					
				result.setPayeeState((String)record[i+=1]);
				result.setPayeeZip((String)record[i+=1]);
				result.setPayeePhoneNumber((String)record[i+=1]);				
				
				results.add(result);
			}
		}		
		
		return results;
	}
	
	public int findUpfitterCount(UpfitterSearchCriteriaVO searchCriteria){
		int count = 0;
		Query query = null;
		query = generateUpfitterSearchQuery(searchCriteria, null, true);
		count = ((BigDecimal)query.getSingleResult()).intValue();				
		return count;
	}
	
	/**
	 * Finds the Upfitter accounts that have have quoted prices for the trim
	 */
	public List<UpfitterSearchResultVO> findUpfitters(Model model, UpfitterSearchCriteriaVO searchCriteria, Pageable pageable, Sort sort){
		List<UpfitterSearchResultVO> results = null;
		Query query = null;	
		
		query = generateTrimQuotedUpfitterSearchQuery(model, searchCriteria, sort, false);
		
		if(pageable != null){
			query.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
			query.setMaxResults(pageable.getPageSize());
		}		
		
		@SuppressWarnings("unchecked")
		List<Object[]>resultList = (List<Object[]>)query.getResultList();
		if(resultList != null){
			results = new ArrayList<UpfitterSearchResultVO>();
			
			for(Object[] record : resultList){
				int i = 0;
				
				UpfitterSearchResultVO result = new UpfitterSearchResultVO();
				result.setPayeeCorporateId(((BigDecimal)record[i]).longValue());	
				result.setPayeeAccountType((String)record[i+=1]);					
				result.setPayeeAccountCode((String)record[i+=1]);
				result.setPayeeAccountName((String)record[i+=1]);
				result.setPayeeAccountStatus((String)record[i+=1]);					
				result.setPayeeAddress1((String)record[i+=1]);				
				result.setPayeeAddress2((String)record[i+=1]);
				result.setPayeeAddress3((String)record[i+=1]);
				result.setPayeeCity((String)record[i+=1]);					
				result.setPayeeState((String)record[i+=1]);
				result.setPayeeZip((String)record[i+=1]);				
				
				results.add(result);
			}
		}		
		
		return results;				
	}
	
	public int findTrimQuotedUpfitter(Model model, UpfitterSearchCriteriaVO searchCriteria){
		int count = 0;
		Query query = null;
		query = generateTrimQuotedUpfitterSearchQuery(model, searchCriteria, null, true);
		count = ((BigDecimal)query.getSingleResult()).intValue();				
		return count;
	}	
	
	public List<UpfitterSearchResultVO> findVendors(String searchTerm, Pageable pageable, Sort sort){
		List<UpfitterSearchResultVO> results = null;
		Query query = generateVendorSearchQuery(searchTerm, sort, false);
		
		if(pageable != null){
			query.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
			query.setMaxResults(pageable.getPageSize());
		}		
		
		@SuppressWarnings("unchecked")
		List<Object[]>resultList = (List<Object[]>)query.getResultList();
		if(resultList != null){
			results = new ArrayList<UpfitterSearchResultVO>();
			for(Object[] record : resultList){
				int i = 0;
				
				UpfitterSearchResultVO result = new UpfitterSearchResultVO();
				result.setPayeeCorporateId(((BigDecimal)record[i]).longValue());	
				result.setPayeeAccountType((String)record[i+=1]);					
				result.setPayeeAccountCode((String)record[i+=1]);
				result.setPayeeAccountName((String)record[i+=1]);	
				result.setPayeeAccountStatus((String)record[i+=1]);	
				result.setPayeeAddress1((String)record[i+=1]);				
				result.setPayeeAddress2((String)record[i+=1]);
				result.setPayeeAddress3((String)record[i+=1]);
				result.setPayeeCity((String)record[i+=1]);					
				result.setPayeeState((String)record[i+=1]);
				result.setPayeeZip((String)record[i+=1]);
				result.setPayeePhoneNumber((String)record[i+=1]);				
				results.add(result);
			}
		}		
		
		return results;
	}
		
	@SuppressWarnings("unchecked")
	public List<Object[]> getDealerContactDetailsList(Long cId, String accountType, String accountCode) throws MalBusinessException {
		
		String queryString = "Select ss.sst_id, ss.staff_name, "
				+ "DECODE (ss.qualifications, NULL, 'NO PHONE AVAILABLE', ss.qualifications) as \"sup_phone\", "
				+ "(Select e_mail from contacts where sup_sup_id = ss.sup_sup_id and contact_type = 'STOCK' and default_ind = 'Y') as \"sup_contact_email\", "
				+ "DECODE(sup.email_address, NULL, 'NO EMAIL AVAILABLE', sup.email_address) as \"sup_email\", "
				+ "DECODE (sup.telephone_number, NULL, 'NO PHONE AVAILABLE', sup.telephone_number) as \"dealer_phone\" "
				+ "from suppliers sup, supplier_staff ss "
				+ "where ss.sup_sup_id = sup.sup_id "
				+ "and sup.ea_c_id = :cId "
				+ "and sup.ea_account_type = :accountType "
				+ "and sup.ea_account_code = :accountCode "
				+ "and sup.sstc_service_type_code = 'DEALER' "
				+ "and UPPER(ss.staff_position) like 'STOCK%' "
				+ "order by ss.sst_id desc";
		
		Query query = entityManager.createNativeQuery(queryString);
		query.setParameter("cId", cId);
		query.setParameter("accountType", accountType);
		query.setParameter("accountCode", accountCode);
		
		
		List<Object[]>resultList = (List<Object[]>) query.getResultList();
				
		return resultList;
	}

	@SuppressWarnings("unchecked")
	public List<Object[]> getVendorContactDetailsList(Long cId, String accountType, String accountCode) throws MalBusinessException {
		
		String queryString = "Select ss.sst_id, ss.staff_name, "
				+ "DECODE (ss.qualifications, NULL, 'NO PHONE AVAILABLE', ss.qualifications) as \"sup_phone\", "
				+ "(Select e_mail from contacts where sup_sup_id = ss.sup_sup_id and contact_type = 'ORDERING' and default_ind = 'Y') as \"sup_contact_email\", "
				+ "DECODE(sup.email_address, NULL, 'NO EMAIL AVAILABLE', sup.email_address) as \"sup_email\", "
				+ "DECODE (sup.telephone_number, NULL, 'NO PHONE AVAILABLE', sup.telephone_number) as \"dealer_phone\" "
				+ "from suppliers sup, supplier_staff ss "
				+ "where ss.sup_sup_id = sup.sup_id "
				+ "and sup.ea_c_id = :cId "
				+ "and sup.ea_account_type = :accountType "
				+ "and sup.ea_account_code = :accountCode "
				+ "and sup.sstc_service_type_code = 'DEALER' "
				+ "and UPPER(ss.staff_position) like  'ORDERING%' "
				+ "order by ss.sst_id desc";
		
		Query query = entityManager.createNativeQuery(queryString);
		query.setParameter("cId", cId);
		query.setParameter("accountType", accountType);
		query.setParameter("accountCode", accountCode);
		
		
		List<Object[]>resultList = (List<Object[]>) query.getResultList();
				
		return resultList;
	}
	
	public List<ExternalAccount>	findAccountsForClientScheduleType(){
		List<ExternalAccount> resultAccount = new ArrayList<ExternalAccount>();
		String queryString = "select ea.c_id,ea.account_type,ea.account_code,ea.account_name from external_accounts  ea,maint_pref_acct mpa "
				+" where mpa.cst_cst_id = 1003 and  ea.c_id = mpa.ea_c_id"
				+" and ea.account_code = mpa.ea_account_code "
				+" and ea.account_type = mpa.ea_account_type order by ea.account_name asc";
		Query query = entityManager.createNativeQuery(queryString);
		List<Object[]>resultList = query.getResultList();
		for (Object[] objects : resultList) {
			ExternalAccount account = new ExternalAccount();
			ExternalAccountPK id = new ExternalAccountPK();
			id.setCId(((BigDecimal)objects[0]).longValue());
			id.setAccountType((String)objects[1]);
			id.setAccountCode((String)objects[2]);
			account.setExternalAccountPK(id);
			account.setAccountName((String)objects[3]);
			resultAccount.add(account);
		}
		return resultAccount;
		
	}
	
	
	
	/**
	 * Queries the EXTERNAL_ACCOUNTS and SUPPLIERS table where the search term makes the account code or account name or supplier name
	 * An implicit wild card is appended to the search term.
	 * @param searchCriteria Search criteria to perform search against
	 * @param sort Specifies the ordering of the result
	 * @param isCountQuery Flag used to indicate whether to retrieve a count of the result or not.
	 * @return
	 */
	private Query generateUpfitterSearchQuery(UpfitterSearchCriteriaVO searchCriteria, Sort sort, boolean isCountQuery){
		StringBuilder sqlStmt;	    
		Query query = null;

		sqlStmt = new StringBuilder("");
		if(isCountQuery){
			sqlStmt.append("SELECT count(1) ");
		} else {
			sqlStmt.append("SELECT eal.c_id, eal.account_type, eal.account_code, eal.account_name, eal.ea_acc_status, eal.address_line_1, eal.address_line_2, eal.address_line_3, eal.town_city, eal.region, eal.postcode, ea.telephone_number ");			
		}

		sqlStmt.append("        FROM external_account_lov eal"
				+ "             LEFT JOIN external_accounts ea ON eal.c_id = ea.c_id AND eal.account_type = ea.account_type AND eal.account_code = ea.account_code "				
				+ "             WHERE eal.account_type = 'S'" 
				+ "                 AND (lower(eal.account_code) LIKE lower(:criteria) OR lower(eal.account_name) LIKE lower(:criteria) ) "
				+ "                 AND eal.ea_acc_status like :accountStatus "
				+ "                 AND eal.account_name != 'CASHBOOK' ");

		if(!MALUtilities.isEmpty(searchCriteria.getModelId())){
			if(searchCriteria.isWithQuoteNo()) {
				sqlStmt.append("        AND EXISTS (SELECT 1 FROM dealer_price_lists dpl, dealer_accessories dac WHERE dpl.dac_dac_id = dac.dac_id AND dpl.c_id = eal.c_id AND dpl.account_type = eal.account_type AND dpl.account_code = eal.account_code AND dpl.ufq_ufq_id is not null AND dac.mdl_mdl_id = :modelId) ");
			} else {
				sqlStmt.append("        AND EXISTS (SELECT 1 FROM dealer_price_lists dpl, dealer_accessories dac WHERE dpl.dac_dac_id = dac.dac_id AND dpl.c_id = eal.c_id AND dpl.account_type = eal.account_type AND dpl.account_code = eal.account_code AND dac.mdl_mdl_id = :modelId) ");
			}

		}

		//Defaults order by unless otherwise specified by the passed in sort object		
		if(!isCountQuery){
			sqlStmt.append("ORDER BY eal.account_name ASC ");
		}	

		//Binding query parameters with properties
		Map<String,Object> parameterMap = new HashMap<String,Object>();
		parameterMap.put("criteria", DataUtilities.appendWildCardToRight(searchCriteria.getTerm()));
		parameterMap.put("accountStatus", MALUtilities.isEmpty(searchCriteria.getAccountStatus()) ? "%" : searchCriteria.getAccountStatus().getCode());

		if(!MALUtilities.isEmpty(searchCriteria.getModelId())){
			parameterMap.put("modelId", searchCriteria.getModelId());
		}


		query = entityManager.createNativeQuery(sqlStmt.toString());		 		 
		for (String paramName : parameterMap.keySet()) {
			query.setParameter(paramName, parameterMap.get(paramName));
		} 	

		return query;

	}

	/**
	 * Creates the query that will provide a list of upfitters that have a quote against a specific trim
	 * @param searchCriteria
	 * @param sort
	 * @param isCountQuery
	 * @return
	 */
	private Query generateTrimQuotedUpfitterSearchQuery(Model model, UpfitterSearchCriteriaVO searchCriteria, Sort sort, boolean isCountQuery){
		StringBuilder sqlStmt;	    
		Query query = null;	

		sqlStmt = new StringBuilder("");
		if(isCountQuery){
			sqlStmt.append("SELECT count(1) ");
		} else {
			sqlStmt.append("SELECT DISTINCT eal.c_id, eal.account_type, eal.account_code, eal.account_name, eal.ea_acc_status, eal.address_line_1, eal.address_line_2, eal.address_line_3, eal.town_city, eal.region, eal.postcode ");			
		}

		sqlStmt.append("        FROM external_account_lov eal"
				//+ "                 INNER JOIN willow2k.external_accounts ea ON eal.c_id = ea.c_id AND eal.account_type = ea.account_type AND eal.account_code = ea.account_code "
				+ "                 INNER JOIN willow2k.upfitter_quotes ufq ON eal.c_id = ufq.ea_c_id AND eal.account_type = ufq.ea_account_type AND eal.account_code = ufq.ea_account_code"
				+ "             WHERE eal.account_type = 'S'" 
				+ "                 AND (lower(eal.account_code) LIKE lower(:criteria) OR lower(eal.account_name) LIKE lower(:criteria) ) "
				+ "                 AND eal.ea_acc_status like :accountStatus "
				+ "                 AND eal.account_name != 'CASHBOOK' "
				+ "                 AND ( ( NOT EXISTS (SELECT 1 "
				+ "                                   FROM willow2k.vehicle_config_upfit_quotes vuq1 "
				+ "                                       INNER JOIN willow2k.upfitter_quotes ufq1 ON vuq1.ufq_ufq_id = ufq1.ufq_id "
				+ "                                   WHERE ufq1.ufq_id = ufq.ufq_id) "
				+ "                          AND EXISTS (SELECT 1 FROM dealer_price_lists dpl, dealer_accessories dac WHERE dpl.dac_dac_id = dac.dac_id AND dpl.c_id = eal.c_id AND dpl.account_type = eal.account_type AND dpl.account_code = eal.account_code AND dac.mdl_mdl_id = :mdlId) ) "
				+ "                        OR EXISTS (SELECT 1"
				+ "                                       FROM willow2k.upfitter_quotes ufq1 "
				+ "                                           INNER JOIN willow2k.vehicle_config_upfit_quotes vuq1 ON ufq1.ufq_id = vuq1.ufq_ufq_id "
				+ "                                           INNER JOIN willow2k.vehicle_config_groupings vcg1 ON vuq1.vcg_vcg_id = vcg1.vcg_id "
				+ "                                           INNER JOIN willow2k.vehicle_configurations vcf1 ON vcg1.vcf_vcf_id = vcf1.vcf_id "
				+ "                                           INNER JOIN willow2k.vehicle_config_models vcm1 ON vcf1.vcf_id = vcm1.vcf_vcf_id AND :mfgCode = nvl(vcm1.mfg_code, :mfgCode) AND :year LIKE nvl(vcm1.year, :year) "
				+ "                                                   AND :make LIKE nvl(vcm1.make, :make ) AND :mrgId = nvl(vcm1.mrg_mrg_id, :mrgId) "
				+ "                                                   AND :mdlId = nvl(vcm1.mdl_mdl_id, :mdlId) AND :mtpId = nvl(vcm1.mtp_mtp_id, :mtpId) "
				+ "                                       WHERE ufq1.ufq_id = ufq.ufq_id) ) ");

		//Defaults order by unless otherwise specified by the passed in sort object		
		if(!isCountQuery){
			sqlStmt.append("ORDER BY eal.account_name ASC ");
		}

		//Binding query parameters with properties
		Map<String,Object> parameterMap = new HashMap<String,Object>();
		parameterMap.put("criteria", DataUtilities.appendWildCardToRight(searchCriteria.getTerm()));
		parameterMap.put("accountStatus", MALUtilities.isEmpty(searchCriteria.getAccountStatus()) ? "%" : searchCriteria.getAccountStatus().getCode());
		parameterMap.put("mfgCode", model.getStandardCode());
		parameterMap.put("year", model.getModelMarkYear().getModelMarkYearDesc());
		parameterMap.put("make", model.getMake().getMakeDesc());			
		parameterMap.put("mrgId", model.getMakeModelRange().getMrgId());
		parameterMap.put("mdlId", model.getModelId());			
		parameterMap.put("mtpId", model.getModelType().getMtpId());	


		query = entityManager.createNativeQuery(sqlStmt.toString());		 		 
		for (String paramName : parameterMap.keySet()) {
			query.setParameter(paramName, parameterMap.get(paramName));
		} 	

		return query;		

	}	

	private Query generateVendorSearchQuery(String searchTerm, Sort sort, boolean isCountQuery){
		StringBuilder sqlStmt = new StringBuilder("");
		if(isCountQuery){
			sqlStmt.append("SELECT count(1) ");
		} else {
			sqlStmt.append("SELECT eal.c_id, eal.account_type, eal.account_code, eal.account_name, eal.ea_acc_status, eal.address_line_1, eal.address_line_2, eal.address_line_3, eal.town_city, eal.region, eal.postcode, ea.telephone_number ");			
		}
	    
		sqlStmt.append(" FROM external_account_lov eal");
		sqlStmt.append(" LEFT JOIN external_accounts ea ON eal.c_id = ea.c_id AND eal.account_type = ea.account_type AND eal.account_code = ea.account_code"); 				
		sqlStmt.append(" LEFT JOIN suppliers sup ON sup.ea_c_id = ea.c_id AND sup.ea_account_type = ea.account_type AND sup.ea_account_code = ea.account_code");
		sqlStmt.append(" WHERE eal.account_type = 'S'");
		sqlStmt.append(" AND (lower(eal.account_code) LIKE lower(:criteria) OR lower(eal.account_name) LIKE lower(:criteria))");
		sqlStmt.append(" AND sup.supplier_category = 'PUR_DLR'");
		sqlStmt.append(" AND sup.inactive_ind = 'N'");
		
		if(!isCountQuery){
			sqlStmt.append("ORDER BY eal.account_name ASC ");
		}	
		
		Map<String,Object> parameterMap = new HashMap<String,Object>();
		parameterMap.put("criteria", DataUtilities.appendWildCardToRight(searchTerm));
		
		Query query = entityManager.createNativeQuery(sqlStmt.toString());		 		 
		for (String paramName : parameterMap.keySet()) {
			query.setParameter(paramName, parameterMap.get(paramName));
		} 	
		
	    return query;
	
	}

	@Override
	public List<Object[]> getAccountAddressByType(Long cId, String accountType,
			String accountCode, String addressType) throws MalException {

		String queryString = "SELECT ea.account_name, FL_CARD.revert(comp_reg_no) as reg_no, eaa.address_line_1, eaa.address_line_2, INITCAP(eaa.town_city), eaa.region, eaa.postcode, eaa.country"
				+ " FROM external_accounts ea, ext_acc_addresses eaa"
				+ " WHERE eaa.account_code = :accountCode"
				+ " AND eaa.account_type = :accountType"
				+ " AND eaa.c_id = :cId"
				+ " AND UPPER(eaa.address_type) = UPPER(:addressType)"
				+ " AND ea.account_type = eaa.account_type"
				+ " AND ea.account_code = eaa.account_code"
				+ " AND eaa.default_ind = 'Y'";

		Query query = entityManager.createNativeQuery(queryString);
		query.setParameter("cId", cId);
		query.setParameter("accountType", accountType);
		query.setParameter("accountCode", accountCode);
		query.setParameter("addressType", addressType);

		List<Object[]> resultList = (List<Object[]>) query.getResultList();

		return resultList;
	}
	
	@Override
	public List<Object[]> getTaxExemptListByAccountInfo(Long cId,
			String accountType, String accountCode) throws MalException {
		
		String queryString = "SELECT region_code, tax_exempt_no"
				+ "	FROM EXT_ACC_TAX_EXEMPT"
				+ " WHERE c_id = :cId"
				+ " AND account_type = :accountType"
				+ " AND account_code = :accountCode"
				+ " AND effective_to > sysdate";

			Query query = entityManager.createNativeQuery(queryString);
			query.setParameter("cId", cId);
			query.setParameter("accountType", accountType);
			query.setParameter("accountCode", accountCode);

			List<Object[]> resultList = (List<Object[]>) query.getResultList();

			return resultList;
	}
	
	@Override
	public BigDecimal getHurdleInterestRate(Long cId, Long qprId, Date effectiveDate, Long contractPeriod) { 
		
		String stmt = "SELECT quotation_wrapper.GET_HURDLE_RATE(?,?,?,?) FROM dual";
		Query query = entityManager.createNativeQuery(stmt);
		query.setParameter(1, cId);
		query.setParameter(2, qprId);
		query.setParameter(3, effectiveDate);
		query.setParameter(4, contractPeriod);		
		
		BigDecimal hurdleInterestRate = (BigDecimal) query.getSingleResult();
		
		return hurdleInterestRate;
		
	}

	@Override
	public List<String> getCustomerPaymentProfiles(Long cId, String accountType, String accountCode)
			throws MalBusinessException {

		String queryString = "select L_PH.PAYMENT_NAME  L_PH_PAYMENT_NAME"
				+ " from   PAYMENT_HEADERS L_PH"
				+ " where l_ph.payment_header_id in (select ph_payment_id from cust_pmt_prof where ea_account_code = :accountCode and"
				+ " ea_account_type = :accountType and ea_c_id = :cId )";

		Query query = entityManager.createNativeQuery(queryString);
		query.setParameter("cId", cId);
		query.setParameter("accountType", accountType);
		query.setParameter("accountCode", accountCode);
		List<String> paymentList  = null;
		
		@SuppressWarnings("unchecked")
		List<Object[]> resultList = (List<Object[]>) query.getResultList();
		if(resultList != null){
			paymentList = new ArrayList<String>();
			
			for(Object record : resultList){
				paymentList.add((String) record); 
			}
		}
		return paymentList;
	}	

}
