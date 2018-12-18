package com.mikealbert.data.dao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import com.mikealbert.data.DataConstants;
import com.mikealbert.data.entity.CostCentreCode;
import com.mikealbert.data.entity.CostCentreCodePK;
import com.mikealbert.data.entity.ExternalAccount;
import com.mikealbert.data.vo.CostCenterHierarchicalVO;
import com.mikealbert.util.MALUtilities;

public class CostCenterDAOImpl extends GenericDAOImpl<CostCentreCode, CostCentreCodePK> implements CostCenterDAOCustom {
	@Resource
	private CostCenterDAO costCenterDAO;
	private static final long serialVersionUID = -2873586208182277297L;
	@PersistenceContext protected EntityManager entityManager;

    
	/**
	 * Retrieves the accounts cost center with attributes set to represent the hierarchical relationship
	 * between parent and child
	 * @param ExternalAccount The Client Account
	 * @param Pageable Pageable for paging the data set
	 * @param Sort Sort for sorting the data set. Not really needed as sorting can be done via the Pageable
	 */
	public List<CostCenterHierarchicalVO> findCostCenterHierarchicalVOByAccount(ExternalAccount account, Pageable pageable, Sort sort){
		List<CostCenterHierarchicalVO> costCenterVOs = null;
		Query query = null;

		query = generateCostCenterHierarchicalVOsByAccountQuery(account, sort, false);
		
		if(pageable != null){
			query.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
			query.setMaxResults(pageable.getPageSize());
		}		
		
		@SuppressWarnings("unchecked")
		List<Object[]>resultList = (List<Object[]>)query.getResultList();
		if(resultList != null){
			costCenterVOs = new ArrayList<CostCenterHierarchicalVO>();
			
			for(Object[] record : resultList){
				int i = 0;
				
				CostCenterHierarchicalVO vo = new CostCenterHierarchicalVO();
				vo.setLevel(((BigDecimal)record[i]).longValue());
				vo.setCode((String)record[i+=1]);				
				vo.setCorporateEntityId(((BigDecimal)record[i+=1]).longValue());
				vo.setAccountType((String)record[i+=1]);
				vo.setAccountCode((String)record[i+=1]);
				vo.setDescription((String)record[i+=1]);				
				vo.setParentCode((String)record[i+=1]);	
				vo.setParentCorporateEntityId(record[i+=1] != null ? ((BigDecimal)record[i]).longValue() : null);				
				vo.setParentAccountType((String)record[i+=1]);
				vo.setParentAccountCode((String)record[i+=1]);	
				
				costCenterVOs.add(vo);
			}
		}
	
		
		return costCenterVOs;
	}
	
	public int getContactVOsByPOCCount(ExternalAccount account){
		int count = 0;
		Query query = null;
		query = generateCostCenterHierarchicalVOsByAccountQuery(account, null, false);		
		count = ((BigDecimal)query.getSingleResult()).intValue();
		return count;
	}

	
	private Query generateCostCenterHierarchicalVOsByAccountQuery(ExternalAccount account, Sort sort, boolean isCountQuery) {
		StringBuilder sqlStmt;		
		Query query = null;
		
		sqlStmt = new StringBuilder("");
		if(isCountQuery){
			sqlStmt.append("SELECT count(1) ");
		} else {
			sqlStmt.append("SELECT LEVEL, cost_centre_code, ea_c_id, ea_account_type, ea_account_code, "
					+ "              description, parent_cc_code, parent_c_id, parent_account_type, parent_account_code");
		}
		
		sqlStmt.append("    FROM willow2k.cost_centre_codes ccc "
				+ "         WHERE ccc.ea_c_id = :corporateEntityId "
				+ "             AND ccc.ea_account_type = :accountType "
				+ "             AND ccc.ea_account_code = :accountCode "				
                + "         START WITH ccc.ea_account_code = :accountCode AND ccc.parent_account_code IS NULL "       
                + "         CONNECT BY PRIOR ccc.cost_centre_code = ccc.parent_cc_code AND ea_account_code = ccc.parent_account_code ");        
		
		//ORDER BY	
		if(!isCountQuery){
			if(!MALUtilities.isEmpty(sort)){
				sqlStmt.append("ORDER SIBLINGS BY ");
				for ( Iterator<Order> orderIterator = sort.iterator(); orderIterator.hasNext(); ) {
					Order order = orderIterator.next();
					
					if(DataConstants.COST_CENTER_SORT_CODE.equals(order.getProperty())){	
						sqlStmt.append(" ccc.cost_centre_code " + order.getDirection());
					}							

					if(orderIterator.hasNext()){
						sqlStmt.append(", ");
					}
				}			
			} else {
				sqlStmt.append(" ORDER SIBLINGS BY ccc.cost_centre_code ASC ");
			}				
		}
		
		//Query parameter assignments
		Map<String,Object> parameterMap = new HashMap<String,Object>();
		parameterMap.put("corporateEntityId", account.getExternalAccountPK().getCId());
		parameterMap.put("accountType", account.getExternalAccountPK().getAccountType());
		parameterMap.put("accountCode", account.getExternalAccountPK().getAccountCode());			
		
		query = entityManager.createNativeQuery(sqlStmt.toString());		 		 
		for (String paramName : parameterMap.keySet()) {
			query.setParameter(paramName, parameterMap.get(paramName));
		} 							
				
		return query;
	}

}
