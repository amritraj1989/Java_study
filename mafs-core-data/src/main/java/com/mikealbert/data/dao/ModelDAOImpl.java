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

import com.mikealbert.data.DataConstants;
import com.mikealbert.data.DataUtilities;
import com.mikealbert.data.entity.Model;
import com.mikealbert.data.vo.ModelSearchCriteriaVO;
import com.mikealbert.data.vo.ModelSearchResultVO;
import com.mikealbert.util.MALUtilities;

public class ModelDAOImpl extends GenericDAOImpl<Model, Long> implements ModelDAOCustom {
	@Resource
	private ModelDAO modelDAO;
	
	private static final long serialVersionUID = -5126660263724406923L;

	public List<ModelSearchResultVO> findModels(ModelSearchCriteriaVO searchCriteria, Pageable pageable, Sort sort){
		List<ModelSearchResultVO> modelSearchResultVOs = null;
		Query query = null;

		query = generateModelsQuery(searchCriteria, sort, false);
		
		if(pageable != null){
			query.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
			query.setMaxResults(pageable.getPageSize());
		}
		
		@SuppressWarnings("unchecked")
		List<Object[]>resultList = (List<Object[]>)query.getResultList();
		if(resultList != null){
			modelSearchResultVOs = new ArrayList<ModelSearchResultVO>();
			
			for(Object[] record : resultList){
				int i = 0;
				
				ModelSearchResultVO mdlVO = new ModelSearchResultVO();
				mdlVO.setMdlId(((BigDecimal)record[i]).longValue());	
				mdlVO.setYear((String)record[i+=1]);					
				mdlVO.setMake((String)record[i+=1]);
				mdlVO.setModel((String)record[i+=1]);
				mdlVO.setTrim((String)record[i+=1]);
				mdlVO.setEngine((String)record[i+=1]);
				mdlVO.setModelType((String)record[i+=1]);				
				mdlVO.setMfgCode((String)record[i+=1]);
				mdlVO.setQuotePermitted(MALUtilities.convertYNToBoolean((String)record[i+=1]));					
				
				modelSearchResultVOs.add(mdlVO);
			}
		}		
		
		return modelSearchResultVOs;
	}
	
	
	public int findModelsCount(ModelSearchCriteriaVO searchCriteria){
		int count = 0;
		Query query = null;
		query = generateModelsQuery(searchCriteria, null, true);
		count = ((BigDecimal)query.getSingleResult()).intValue();
		return count;
	}	
	
	
	private Query generateModelsQuery(ModelSearchCriteriaVO searchCriteria, Sort sort, boolean isCountQuery){
		StringBuilder sqlStmt;		
		Query query = null;
		
		sqlStmt = new StringBuilder("");
		if(isCountQuery){
			sqlStmt.append("SELECT count(1) ");
		} else {
			sqlStmt.append("SELECT mdl.mdl_id, mmy.model_mark_year_desc, mak.make_desc, mrg.make_model_desc, mdl.model_desc, mdl.engine_description, mtp.model_type_desc, mdl.standard_code, mdl.quote_permitted_flag ");
		}
		
		sqlStmt.append("    FROM models mdl " 
		        +"              INNER JOIN make_model_ranges mrg ON mdl.mrg_mrg_id = mrg.mrg_id "
                +"              INNER JOIN model_mark_years mmy ON mdl.mmy_mmy_id = mmy.mmy_id "
                +"              INNER JOIN model_types mtp ON mdl.mtp_mtp_id = mtp.mtp_id "
                +"              INNER JOIN makes mak ON mdl.mak_mak_id = mak.mak_id " 
                +"          WHERE mmy.model_mark_year_desc LIKE :year "
                +"              AND lower(mak.make_desc) LIKE lower(:make) "
                +"              AND lower(mrg.make_model_desc) LIKE lower(:model) "
                +"              AND lower(mrg.make_model_range_code) LIKE lower(:modelCode) "                
                +"              AND lower(mdl.model_desc) LIKE lower(:trim) "
                +"              AND lower(mtp.model_type_desc) LIKE lower(:modelType) "
                +"              AND lower(mdl.standard_code) LIKE lower(:mfgCode)" );
		
		//Defaults order by unless otherwise specified by the passed in sort object		
		if(!isCountQuery){
			if(!MALUtilities.isEmpty(sort)){
				sqlStmt.append(
						"   ORDER BY ");
				for ( Iterator<Order> orderIterator = sort.iterator(); orderIterator.hasNext(); ) {
					Order order = orderIterator.next();
					
					if(DataConstants.MODEL_SEARCH_SORT_YEAR.equals(order.getProperty())){	
						sqlStmt.append(" mmy.model_mark_year_desc " + order.getDirection());
					}					
					if(DataConstants.MODEL_SEARCH_SORT_MAKE.equals(order.getProperty())){	
						sqlStmt.append(" mak.make_desc " + order.getDirection());
					}
					if(DataConstants.MODEL_SEARCH_SORT_MODEL.equals(order.getProperty())){	
						sqlStmt.append(" mrg.make_model_desc " + order.getDirection());
					}
					if(DataConstants.MODEL_SEARCH_SORT_TRIM.equals(order.getProperty())){	
						sqlStmt.append(" mdl.model_desc " + order.getDirection());
					}
					if(DataConstants.MODEL_SEARCH_SORT_MFG_CODE.equals(order.getProperty())){	
						sqlStmt.append(" mdl.standard_code " + order.getDirection());
					}					

					if(orderIterator.hasNext()){
						sqlStmt.append(", ");
					}
				}			
			} else {
				sqlStmt.append("ORDER BY mmy.model_mark_year_desc DESC, mak.make_desc ASC, mrg.make_model_desc ASC, mdl.model_desc ASC ");
			}				
		}		
		
	
				
		Map<String,Object> parameterMap = new HashMap<String,Object>();
		parameterMap.put("year", MALUtilities.isEmpty(searchCriteria.getYear()) ? '%' : searchCriteria.getYear());		
		parameterMap.put("make", DataUtilities.appendWildCardToRight(searchCriteria.getMake()));
		parameterMap.put("model", DataUtilities.appendWildCardToRight(searchCriteria.getModel()));
		parameterMap.put("modelCode", MALUtilities.isEmpty(searchCriteria.getModelCode()) ? '%' : searchCriteria.getModelCode());		
		parameterMap.put("trim", DataUtilities.appendWildCardToRight(searchCriteria.getTrim()));		
		parameterMap.put("modelType", DataUtilities.appendWildCardToRight(searchCriteria.getModelType()));	
		parameterMap.put("mfgCode", DataUtilities.appendWildCardToRight(searchCriteria.getMfgCode()));	
		
		query = entityManager.createNativeQuery(sqlStmt.toString());		 		 
		for (String paramName : parameterMap.keySet()) {
			query.setParameter(paramName, parameterMap.get(paramName));
		} 			
				
		return query;
	}
	
}
