package com.mikealbert.data.dao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.persistence.Query;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;

import com.mikealbert.common.MalLogger;
import com.mikealbert.common.MalLoggerFactory;
import com.mikealbert.data.DataConstants;
import com.mikealbert.data.entity.ServiceProviderMaintenanceCode;
import com.mikealbert.data.vo.ProviderMaintCodeSearchCriteriaVO;
import com.mikealbert.data.vo.ProviderMaintCodeSearchResultLineVO;
import com.mikealbert.data.vo.ProviderMaintCodeSearchResultsVO;
import com.mikealbert.data.vo.ServiceProviderDiscountVO;
import com.mikealbert.util.MALUtilities;

public class ServiceProviderMaintenanceCodeDAOImpl extends GenericDAOImpl<ServiceProviderMaintenanceCode, Long> implements ServiceProviderMaintenanceCodeDAOCustom {
	
	@Resource
	private ServiceProviderMaintenanceCodeDAO serviceProviderMaintenanceCodeDAO;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7371977314217724913L;
	private static MalLogger logger = MalLoggerFactory.getLogger(VehicleSearchDAOImpl.class);

	@Override
	public ProviderMaintCodeSearchResultsVO searchProviderMaintCodes(
			ProviderMaintCodeSearchCriteriaVO providerMaintCodeSearchCriteriaVO,
			Pageable pageable, Sort sort) {
		
		ProviderMaintCodeSearchResultsVO results = new ProviderMaintCodeSearchResultsVO();
		
		Query query = generateProviderMaintenanceCodeQuery( providerMaintCodeSearchCriteriaVO, false, sort);
		
		if(pageable != null){
			query.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
			query.setMaxResults(pageable.getPageSize());
		}
		
		List<ProviderMaintCodeSearchResultLineVO>  maintCodeVOList = new ArrayList<ProviderMaintCodeSearchResultLineVO>();
		@SuppressWarnings("unchecked")
		List<Object[]> objectList  = (List<Object[]>) query.getResultList();
		if (objectList != null && objectList.size() > 0) {	
			
			for (Iterator<Object[]> iterator = objectList.iterator(); iterator.hasNext();) {
				Object[] object = iterator.next();				
				ProviderMaintCodeSearchResultLineVO maintCodeVO = new ProviderMaintCodeSearchResultLineVO();
				
				maintCodeVO.setProvideMaintCodeId(object[0] != null ? ((BigDecimal) object[0]).longValue() : null);
				maintCodeVO.setServiceProvideName(object[1] != null ? (String) object[1] : null);
				maintCodeVO.setServiceProvideMaintCategory(object[2] != null ? (String) object[2] : null);
				maintCodeVO.setServiceProvideCode(object[3] != null ? (String) object[3] : null);
				maintCodeVO.setProvideMaintCode(object[4] != null ? (String) object[4] : null);
				maintCodeVO.setProvideMaintCodeDesc(object[5] != null ? (String) object[5] : null);
				
				maintCodeVO.setMafsMaintCodeId(object[6] != null ? ((BigDecimal) object[6]).longValue() : null);
				maintCodeVO.setMafsMaintCode(object[7] != null ? (String) object[7] : null);
				maintCodeVO.setMafsMaintCodeDesc(object[8] != null ? (String) object[8] : null);				
				maintCodeVO.setApproved(object[9] != null ? true : false);
				
				maintCodeVOList.add(maintCodeVO);
			}
		}
		
		results.setResultsLines(maintCodeVOList);
		
		Query countQuery = generateProviderMaintenanceCodeQuery( providerMaintCodeSearchCriteriaVO, true, sort);
		BigDecimal count = (BigDecimal) countQuery.getSingleResult();
		
		results.setResultCount(count.intValue());
		
		return results;
	}
	
	private Query generateProviderMaintenanceCodeQuery(ProviderMaintCodeSearchCriteriaVO providerMaintCodeSearchCriteriaVO, boolean isCount, Sort sort){
		Query query = null;
		boolean criteriaAdded = false;
		
		StringBuilder sqlStmt = new StringBuilder("");
		sqlStmt.append("SELECT");
		
		if(isCount){
			sqlStmt.append(
					"  COUNT(SML.SML_ID)  ");				
		} else {
			sqlStmt.append(
					"  SML.SML_ID, SUP.SUPPLIER_NAME, MC.MAINT_CAT_CODE, SUP.SUPPLIER_CODE, SML.VENDOR_CODE, SML.DESCRIPTION, MC.MCO_ID, MC.MAINT_CODE, MC.MAINT_CODE_DESC, SML.APPROVED_BY   ");			
		}
		
		sqlStmt.append(" FROM SUPP_MAINT_LINK SML ");
		sqlStmt.append(" INNER JOIN SUPPLIERS SUP ON SML.SUP_SUP_ID = SUP.SUP_ID ");
		sqlStmt.append(" LEFT JOIN MAINTENANCE_CODES MC ON SML.MCO_MCO_ID = MC.MCO_ID ");
		
		if(providerMaintCodeSearchCriteriaVO.getApprovedStatus().equalsIgnoreCase(ProviderMaintCodeSearchCriteriaVO.NEEDS_APPROVE_STATUS) || providerMaintCodeSearchCriteriaVO.getApprovedStatus().equalsIgnoreCase(ProviderMaintCodeSearchCriteriaVO.NEEDS_RE_APPROVE_STATUS)){
			sqlStmt.append(" WHERE SML.APPROVED_BY IS NULL ");
			criteriaAdded = true;
		}else if(providerMaintCodeSearchCriteriaVO.getApprovedStatus().equals(ProviderMaintCodeSearchCriteriaVO.APPROVED_STATUS)){
			sqlStmt.append(" WHERE SML.APPROVED_BY IS NOT NULL ");
			criteriaAdded = true;
		}
		
		if(!MALUtilities.isEmptyString(providerMaintCodeSearchCriteriaVO.getServiceProvider()) && providerMaintCodeSearchCriteriaVO.getServiceProvider().length() > 0) {
			if(criteriaAdded){
				sqlStmt.append(" AND ( nvl(upper(trim(SUP.SUPPLIER_NAME)), '%') LIKE upper(:supplier) OR nvl(upper(trim(SUP.SUPPLIER_CODE)), '%') LIKE upper(:supplier) ) ");
			} else {
				sqlStmt.append(" WHERE ( nvl(upper(trim(SUP.SUPPLIER_NAME)), '%') LIKE upper(:supplier) OR nvl(upper(trim(SUP.SUPPLIER_CODE)), '%') LIKE upper(:supplier) ) ");
				criteriaAdded = true;
			}
			sqlStmt.append("  ");
		}
		
		if(!MALUtilities.isEmptyString(providerMaintCodeSearchCriteriaVO.getMaintenanceCategory()) && providerMaintCodeSearchCriteriaVO.getMaintenanceCategory().length() > 0) {
			if(criteriaAdded){
				sqlStmt.append(" AND MC.MAINT_CAT_CODE = upper(:category) ");
			} else {
				sqlStmt.append(" WHERE MC.MAINT_CAT_CODE = upper(:category) ");
				criteriaAdded = true;
			}
		}
		
		//Defaults order by unless otherwise specified by the passed in sort object
		if(!MALUtilities.isEmpty(sort)){
			sqlStmt.append(
					"     ORDER BY ");
			for ( Iterator<Order> orderIterator = sort.iterator(); orderIterator.hasNext(); ) {
				Order order = orderIterator.next();
				
				if(DataConstants.PROVIDER_MAINT_CODE_SORT_MAFS_CODE.equals(order.getProperty())){	
					sqlStmt.append(" MC.MAINT_CODE " + order.getDirection());
				}
				if(DataConstants.PROVIDER_MAINT_CODE_SORT_MAFS_DESC.equals(order.getProperty())){	
					sqlStmt.append(" MC.MAINT_CODE_DESC " + order.getDirection());
				}
				if(DataConstants.PROVIDER_MAINT_CODE_SORT_SERVICE_PROVIDER.equals(order.getProperty())){	
					sqlStmt.append(" SUP.SUPPLIER_NAME " + order.getDirection());
				}
				if(DataConstants.PROVIDER_MAINT_CODE_SORT_SERVICE_PROVIDER_CODE.equals(order.getProperty())){	
					sqlStmt.append(" SML.VENDOR_CODE " + order.getDirection());
				}
				if(DataConstants.PROVIDER_MAINT_CODE_SORT_SERVICE_PROVIDER_DESC.equals(order.getProperty())){	
					sqlStmt.append(" SML.DESCRIPTION " + order.getDirection());
				}
				
				if(orderIterator.hasNext()){
					sqlStmt.append(", ");
				}
	
			}			
		} else {
			sqlStmt.append("  ORDER BY SUP.SUPPLIER_NAME asc, SML.APPROVED_BY asc");
		}
		
		Map<String,Object> parameterMap = new HashMap<String,Object>();
		if(!MALUtilities.isEmptyString(providerMaintCodeSearchCriteriaVO.getServiceProvider())){
			parameterMap.put("supplier", MALUtilities.isEmptyString(providerMaintCodeSearchCriteriaVO.getServiceProvider()) ? "%" : providerMaintCodeSearchCriteriaVO.getServiceProvider());
		}
		if(!MALUtilities.isEmptyString(providerMaintCodeSearchCriteriaVO.getMaintenanceCategory())){
			parameterMap.put("category", MALUtilities.isEmptyString(providerMaintCodeSearchCriteriaVO.getMaintenanceCategory()) ? "%" : providerMaintCodeSearchCriteriaVO.getMaintenanceCategory());
		}

		
		logger.debug("Final Status Query: " + sqlStmt.toString());
		query = entityManager.createNativeQuery(sqlStmt.toString());		 
		 
		for (String paramName : parameterMap.keySet()) {
			query.setParameter(paramName, parameterMap.get(paramName));
		} 		
		
		return query;
	}

	@Override
	public boolean isProviderMaintCodeAdded(String providerMaintCode, long parentProviderId,
			boolean excludeUnapproved) {
		boolean retVal;
		
		Query countQuery = generateCountOfProvider(providerMaintCode,parentProviderId,excludeUnapproved);
		
		BigDecimal count = (BigDecimal) countQuery.getSingleResult();
		
		if(count.intValue() == 0){
			retVal = false;
		}else{
			retVal = true;
		}

		return retVal;
	}
	
	private Query generateCountOfProvider(String providerMaintCode, long parentProviderId,
			boolean excludeUnapproved){
		Query query = null;

		StringBuilder sqlStmt = new StringBuilder("");
		sqlStmt.append("SELECT");
		sqlStmt.append("  COUNT(SML.SML_ID)  ");	
		sqlStmt.append("  FROM SUPP_MAINT_LINK SML  ");
		sqlStmt.append("  WHERE SML.SUP_SUP_ID =  :parentProviderId ");
		sqlStmt.append("  AND UPPER(SML.VENDOR_CODE) = UPPER(:providerMaintCode) ");
		if(excludeUnapproved){
			sqlStmt.append("  AND APPROVED_BY IS NULL  ");
		}
		
		Map<String,Object> parameterMap = new HashMap<String,Object>();
		parameterMap.put("parentProviderId", parentProviderId);
		parameterMap.put("providerMaintCode", providerMaintCode);
		
		logger.debug("Final Status Query: " + sqlStmt.toString());
		query = entityManager.createNativeQuery(sqlStmt.toString());		 
		 
		for (String paramName : parameterMap.keySet()) {
			query.setParameter(paramName, parameterMap.get(paramName));
		} 
				
		return query;
	}
	
	@Override
	public List<ServiceProviderDiscountVO> getServiceProviderDiscountList(String supplierName, String maintCode, String maintCodeCategory) {
		Query query = generateServiceProviderDiscountQuery(supplierName, maintCode, maintCodeCategory);
		@SuppressWarnings("unchecked")
		List<Object[]> objectList  = (List<Object[]>) query.getResultList();

		List<ServiceProviderDiscountVO> list = new ArrayList<ServiceProviderDiscountVO>();
		if (objectList != null && objectList.size() > 0) {	
			
			for (Object[] obj : objectList)  {
				ServiceProviderDiscountVO spdVO = new ServiceProviderDiscountVO();
				
				spdVO.setMcoId(((BigDecimal) obj[0]).longValue());
				spdVO.setDiscount(((String) obj[1]).equalsIgnoreCase("Y") ? true : false);
				spdVO.setMaintCode((String) obj[2]);
				spdVO.setMaintCodeDescription((String) obj[3]);
				spdVO.setMaintCategoryCode((String) obj[4]);

				list.add(spdVO);
			}
		}
		
		return list;
	}
	
	private Query generateServiceProviderDiscountQuery(String supplierName, String maintCode, String maintCodeCategory){
		Query query = null;

		Map<String,Object> parameterMap = new HashMap<String,Object>();
		parameterMap.put("supplierName", supplierName + "%");

		StringBuilder sqlStmt = new StringBuilder("");
		sqlStmt.append("select mco_mco_id, discount_flag, maint_code, maint_code_desc, maint_cat_code from (");
		sqlStmt.append(" select mco_mco_id, discount_flag from supp_maint_link sml, suppliers s");	
		sqlStmt.append(" where s.sup_id = sml.sup_sup_id");
		sqlStmt.append(" and upper(s.supplier_name) LIKE upper(:supplierName)");
		sqlStmt.append(" and mco_mco_id is not null");
		
		if(!MALUtilities.isEmptyString(maintCode)){
			sqlStmt.append(" and upper((select maint_code from maintenance_codes where mco_mco_id = mco_id)) like upper(:maintCode)");
			parameterMap.put("maintCode", maintCode + "%");
		} 
		if(!MALUtilities.isEmptyString(maintCodeCategory)) {
			sqlStmt.append(" and (select maint_cat_code from maintenance_codes where mco_mco_id = mco_id) = :maintCodeCategory");
			parameterMap.put("maintCodeCategory", maintCodeCategory);
		}

		sqlStmt.append(" group by mco_mco_id, discount_flag)");
		sqlStmt.append(" left join");
		sqlStmt.append(" (select mco_id, maint_code, maint_code_desc, maint_cat_code from maintenance_codes)");
		sqlStmt.append(" on mco_mco_id = mco_id");
		sqlStmt.append(" order by maint_code");
		
		
		
		query = entityManager.createNativeQuery(sqlStmt.toString());		 

		for (String paramName : parameterMap.keySet()) {
			query.setParameter(paramName, parameterMap.get(paramName));
		} 		
		

		
		return query;
	}

}
