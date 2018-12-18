package com.mikealbert.data.dao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;
import javax.persistence.Query;

import com.mikealbert.data.entity.ClientServiceElement;
import com.mikealbert.data.entity.ExternalAccount;
import com.mikealbert.data.vo.ServiceElementsVO;
import com.mikealbert.util.MALUtilities;


public  class ClientServiceElementsDAOImpl extends GenericDAOImpl<ClientServiceElement, Long> implements ClientServiceElementsDAOCustom {

	@Resource
	private ClientServiceElementsDAO clientServiceElementsDAO;
	
	private static final long serialVersionUID = 1L;	

	@SuppressWarnings("unchecked")
	public List<ServiceElementsVO> getAvailableServiceElementsByAccountGradeGroupProfile(ExternalAccount externalAccount, String gradeGroup, Long qprId) {
				
		StringBuilder sqlStmt = new StringBuilder("");
		sqlStmt.append("  SELECT cse.lel_id, cse.element_name, cse.description, cse.billing_option, cse.tax_id " +
						"    FROM external_accounts ea, ");
				
						// conditionally exclude grade group if it is null
						if(MALUtilities.isNotEmptyString(gradeGroup)){
							sqlStmt.append("         external_account_grade_groups eagg, ");
						}

						sqlStmt.append("         TABLE(service_standards.client_elements(ea.c_id, " +
						"                                                 ea.account_type, " +
						"                                                 ea.account_code, ");
						// conditionally exclude grade group if it is null
						if(MALUtilities.isNotEmptyString(gradeGroup)){
							sqlStmt.append("			eagg.eag_id,");
						}else{
							sqlStmt.append("			null,");
						}
						
						if(qprId != null){
							sqlStmt.append(" ?) ");			
						} 
						else {
							sqlStmt.append(" null) ");
						}
						sqlStmt.append("                ) cse " +
						"  WHERE ea.c_id = ? " +
						"    AND ea.account_type = ? " +
						"    AND ea.account_code = ?  " +
						"    AND cse.element_type <> 'FINANCE'");
						
						// conditionally exclude grade group if it is null
						if(MALUtilities.isNotEmptyString(gradeGroup)){
							sqlStmt.append("    AND ea.c_id = eagg.ea_c_id " +
							"    AND ea.account_type = eagg.ea_account_type " +
							"    AND ea.account_code = eagg.ea_account_code " +
							"    AND eagg.driver_grade_group = ? "); 
						}
						
		Query query = entityManager.createNativeQuery(sqlStmt.toString());

		if(qprId != null){
			query.setParameter(1, qprId);
		}
		query.setParameter(2, externalAccount.getExternalAccountPK().getCId());
		query.setParameter(3, externalAccount.getExternalAccountPK().getAccountType());
		query.setParameter(4, externalAccount.getExternalAccountPK().getAccountCode());
		
		// conditionally exclude grade group if it is null
		if(MALUtilities.isNotEmptyString(gradeGroup)){
			query.setParameter(5, gradeGroup);
		}
		
		List<Object[]> results = (List<Object[]>) query.getResultList();
	
		List<ServiceElementsVO> list = new ArrayList<ServiceElementsVO>(); 

		if (results != null && results.size() > 0) {	
			// we will populate what we can get from the service_standards.client_elements function
			for (Iterator<Object[]> iterator = results.iterator(); iterator.hasNext();) {
				Object[] object = iterator.next();				
				ServiceElementsVO serviceElementsVO = new ServiceElementsVO();
				serviceElementsVO.setLelId(object[0] != null ? ((BigDecimal) object[0]).longValue() : null);
				serviceElementsVO.setName(object[1] != null ? (object[1].toString()) : null);
				serviceElementsVO.setDescription(object[2] != null ? (object[2].toString()) : null);
				serviceElementsVO.setBillingOptions(object[3] != null ? (object[3].toString()) : null);
				serviceElementsVO.setTaxId(object[4] != null ? ((BigDecimal) object[4]).longValue() : null);
								
				list.add(serviceElementsVO);
			}
		}
		return list;
	}
	
	public int getElementOverrideCountOnGradeGroups(Long cId, String accountType, String accountCode) {
				
		String stmt = 	" SELECT COUNT(cse.cse_id) cnt " +
						"   FROM client_service_elements cse, " +
						"        client_serv_element_types cset " +
						"  WHERE cse.ea_c_id = ? " +
						"    AND cse.ea_account_type = ? " +
						"    AND cse.ea_account_code = ? " +
						"    AND SYSDATE BETWEEN start_date AND NVL(end_date, SYSDATE) " +
						"    AND cse.cset_cset_id = cset.cset_id " +
						"    AND service_element_type_code = 'GG'" ; 

		Query query = entityManager.createNativeQuery(stmt);
		query.setParameter(1, cId);
		query.setParameter(2, accountType);
		query.setParameter(3, accountCode);
		
		int count = ((BigDecimal) query.getSingleResult()).intValue();
		
		if (MALUtilities.isEmpty(count)) {
			count = 0;
		}
		
		return count;
	}

}
