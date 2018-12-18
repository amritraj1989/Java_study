package com.mikealbert.data.dao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.persistence.Query;

import org.springframework.data.domain.Pageable;

import com.mikealbert.data.entity.LeaseElement;
import com.mikealbert.data.entity.QuotationProfile;


public class LeaseElementDAOImpl extends GenericDAOImpl<LeaseElement, Long> implements LeaseElementDAOCustom {

	@Resource
	private LeaseElementDAO leaseElementDAO;
	
	private static final long serialVersionUID = 3690743823431658820L;

	@Override
	public List<LeaseElement> findAllFilterByFinanceTypeAndElementList(String elementNameParam, List<Long> listOfExcludedElements, Pageable pageable) {
		List<LeaseElement> leaseElementList = new ArrayList<LeaseElement>();
		
		Query query = generatefindAllFilterByFinanceTypeAndElementList(false, elementNameParam, listOfExcludedElements);

		if(pageable != null){
			query.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
			query.setMaxResults(pageable.getPageSize());
		}

		List<Object[]>resultList = (List<Object[]>)query.getResultList();
		if(resultList != null){
			for(Object[] record : resultList){
				int i = 0;

				LeaseElement leaseElement = new LeaseElement();
				leaseElement.setElementName((String)record[i]);
				leaseElement.setDescription((String)record[i+=1]);

				leaseElementList.add(leaseElement);
			}
		}
							
		return leaseElementList;
	}
	
	public int findAllFilterByFinanceTypeAndElementListCount(String elementNameParam, List<Long> listOfExcludedElements) {
		
		Query countQuery = generatefindAllFilterByFinanceTypeAndElementList(true, elementNameParam, listOfExcludedElements);
		BigDecimal count = (BigDecimal) countQuery.getSingleResult();
		
		return count.intValue();
	}
	
	private Query generatefindAllFilterByFinanceTypeAndElementList(boolean isCountQuery, String elementNameParam, List<Long> listOfExcludedElements) {
		Query query =  null;
		StringBuilder sqlStmt = new StringBuilder("");
		
		if(isCountQuery == false){
			sqlStmt.append("SELECT le.element_name, le.description ");
		}else{
			sqlStmt.append("SELECT count(*) ");
		}
		
		sqlStmt.append(" FROM lease_elements le, analysis_codes acod, analysis_categories acat " +
						" WHERE le.element_type != 'FINANCE'" +
						" AND acod.analysis_code = le.element_name" +				
						" AND acat.category_id = acod.category_id" + 
						" AND acat.analysis_category = 'FLBILLING'" +
						" AND acod.tax_id is not null");
		if(elementNameParam != null && !elementNameParam.isEmpty()){
			sqlStmt.append(" 	   AND lower(le.element_name) LIKE lower(:elementNameParam)");
		}
		if(listOfExcludedElements != null && !listOfExcludedElements.isEmpty()){
			sqlStmt.append(" 	   AND le.lel_id NOT IN :listOfExcludedElements");
		}
		sqlStmt.append(" ORDER BY le.element_name asc");
		
		query = entityManager.createNativeQuery(sqlStmt.toString());	
		if(listOfExcludedElements != null && !listOfExcludedElements.isEmpty()){
			query.setParameter("listOfExcludedElements", listOfExcludedElements);
		}
		if(elementNameParam != null && !elementNameParam.isEmpty()){
			query.setParameter("elementNameParam", elementNameParam + '%');
		}
		

		return query;
	}
	
	
	public List<LeaseElement> findAllMaintenanceLeaseElements() {
		List<LeaseElement> leaseElementList = new ArrayList<LeaseElement>();
		
		Query query = getMaintenanceLeaseElements();

		List<Object[]>resultList = (List<Object[]>)query.getResultList();
		if(resultList != null){
			for(Object[] record : resultList){
				int i = 0;

				LeaseElement leaseElement = new LeaseElement();
				leaseElement.setLelId(((BigDecimal)record[i]).longValue());
				leaseElement.setElementName((String)record[i+=1]);
				leaseElement.setDescription((String)record[i+=1]);

				leaseElementList.add(leaseElement);
			}
		}
							
		return leaseElementList;
	}
		
	/**
	 * Use native query to return all maintenance lease elements as listed in csc_lease_elements table
	 */
	public Query getMaintenanceLeaseElements() {
		String stmt = "SELECT le.lel_id, le.element_name, le.description " + 
				      "FROM lease_elements le " +
				      "WHERE le.lel_id IN (SELECT csc.lel_lel_id from csc_lease_elements csc)";

		Query query = entityManager.createNativeQuery(stmt);
		
		return query;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<LeaseElement> getLeaseElementByTypeAndProfile(String elementType, Long qprId) {

		List<LeaseElement> leaseElementList = null;
		Map<String,Object> parameterMap = new HashMap<String,Object>();
		
		Query query = null;
		String  sqlStmt = "SELECT lel.*"
				+ " FROM quotation_profiles qpr,"
				+ " products prd,"
				+ " product_elements pel,"
				+ " lease_elements lel"
				+ " WHERE qpr.qpr_id = :qprId"
				+ " AND qpr.prd_product_code = prd.product_code"
				+ " AND prd.product_code = pel.prd_product_code"
				+ " AND pel.lel_lel_id = lel.lel_id"
				+ " AND lel.element_type = :elementType";
		
		
		query = entityManager.createNativeQuery(sqlStmt, LeaseElement.class);
		parameterMap.put("qprId", qprId);
		parameterMap.put("elementType", elementType);
		
		for (String paramName : parameterMap.keySet()) {
			query.setParameter(paramName, parameterMap.get(paramName));
		}
		
		leaseElementList = query.getResultList();
		return leaseElementList;
	}
	
	@Override
	public List<LeaseElement> findInRateLeaseElements(){
		List<LeaseElement> leaseElementList = new ArrayList<LeaseElement>();
		
		String stmt = "SELECT le.lel_id, le.element_name, le.description " + 
					"FROM lease_elements le " +
			      "WHERE le.in_rate_treatment = 'Y'";

	Query query = entityManager.createNativeQuery(stmt);


		List<Object[]>resultList = (List<Object[]>)query.getResultList();
		if(resultList != null){
			for(Object[] record : resultList){
				int i = 0;

				LeaseElement leaseElement = new LeaseElement();
				leaseElement.setLelId(((BigDecimal)record[i]).longValue());
				leaseElement.setElementName((String)record[i+=1]);
				leaseElement.setDescription((String)record[i+=1]);

				leaseElementList.add(leaseElement);
			}
		}
							
		return leaseElementList;
	}

}
