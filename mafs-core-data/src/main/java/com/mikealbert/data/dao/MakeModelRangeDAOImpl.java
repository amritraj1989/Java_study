package com.mikealbert.data.dao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.persistence.Query;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.mikealbert.data.DataUtilities;
import com.mikealbert.data.entity.MakeModelRange;
import com.mikealbert.data.vo.MakeModelRangeVO;
import com.mikealbert.data.vo.ModelSearchCriteriaVO;
import com.mikealbert.util.MALUtilities;

public class MakeModelRangeDAOImpl extends GenericDAOImpl<MakeModelRange, Long> implements MakeModelRangeDAOCustom {
	
	@Resource
	private MakeModelRangeDAO makeModelRangeDAO;
	
	private static final long serialVersionUID = -5126660263724406923L;

	public List<MakeModelRangeVO> findModelRanges(ModelSearchCriteriaVO searchCriteria, Pageable pageable, Sort sort){
		List<MakeModelRangeVO> makeModelRangeVOs = null;
		Query query = null;

		query = generateMakeModelRangesQuery(searchCriteria, false);
		
		if(pageable != null){
			query.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
			query.setMaxResults(pageable.getPageSize());
		}
		
		@SuppressWarnings("unchecked")
		List<Object[]>resultList = (List<Object[]>)query.getResultList();
		if(resultList != null){
			makeModelRangeVOs = new ArrayList<MakeModelRangeVO>();
			
			for(Object[] record : resultList){
				int i = 0;
				
				MakeModelRangeVO mrgVO = new MakeModelRangeVO();
				mrgVO.setMrgId(((BigDecimal)record[i]).longValue());	
				mrgVO.setCode((String)record[i+=1]);					
				mrgVO.setDescription((String)record[i+=1]);			
				mrgVO.setModelType((String)record[i+=1]);
				
				makeModelRangeVOs.add(mrgVO);
			}
		}		
		
		return makeModelRangeVOs;
	}
	
	
	public int findModelRangesCount(ModelSearchCriteriaVO searchCriteria){
		int count = 0;
		Query query = null;
		query = generateMakeModelRangesQuery(searchCriteria, true);
		count = ((BigDecimal)query.getSingleResult()).intValue();
		return count;
	}	
	
	
	private Query generateMakeModelRangesQuery(ModelSearchCriteriaVO searchCriteria, boolean isCountQuery){
		StringBuilder sqlStmt;		
		Query query = null;
		
		sqlStmt = new StringBuilder("");
		if(isCountQuery){
			sqlStmt.append("SELECT count(DISTINCT mrg.mrg_id || make_model_range_code || mrg.make_model_desc) ");
		} else {
			sqlStmt.append("SELECT DISTINCT mrg.mrg_id, make_model_range_code, mrg.make_model_desc, mtp.model_type_desc ");
		}
		
		sqlStmt.append("    FROM make_model_ranges mrg ");
		sqlStmt.append("              INNER JOIN models mdl ON mrg.mrg_id = mdl.mrg_mrg_id "
                +"              INNER JOIN model_mark_years mmy ON mdl.mmy_mmy_id = mmy.mmy_id "
                +"              INNER JOIN model_types mtp ON mdl.mtp_mtp_id = mtp.mtp_id "
                +"              INNER JOIN makes mak ON mdl.mak_mak_id = mak.mak_id ");
        
		if (!MALUtilities.isEmpty(searchCriteria.getLovSource()) && searchCriteria.getLovSource().equals("VEHICLE_CONFIG_MODEL_LOV")) {
			sqlStmt.append("  INNER JOIN vehicle_config_models vcm ON mrg.mrg_id = vcm.mrg_mrg_id ");
		}
		
		sqlStmt.append("          WHERE mmy.model_mark_year_desc LIKE :year "
                +"              AND lower(mak.make_desc) LIKE lower(:make) "
                +"              AND lower(mrg.make_model_desc) LIKE lower(:model) "
                +"              AND lower(mtp.model_type_desc) LIKE lower(:modelType) "
                +"              AND lower(mdl.standard_code) LIKE lower(:mfgCode) ");
		
		if(!isCountQuery){
			sqlStmt.append("ORDER BY mrg.make_model_desc ASC ");			
		}		
				
		Map<String,Object> parameterMap = new HashMap<String,Object>();
		parameterMap.put("year", DataUtilities.appendWildCardToRight(searchCriteria.getYear()));		
		parameterMap.put("make", DataUtilities.appendWildCardToRight(searchCriteria.getMake()));
		parameterMap.put("model", DataUtilities.appendWildCardToRight(searchCriteria.getModel()));		
		parameterMap.put("modelType", DataUtilities.appendWildCardToRight(searchCriteria.getModelType()));	
		parameterMap.put("mfgCode", DataUtilities.appendWildCardToRight(searchCriteria.getMfgCode()));	
		
		query = entityManager.createNativeQuery(sqlStmt.toString());		 		 
		for (String paramName : parameterMap.keySet()) {
			query.setParameter(paramName, parameterMap.get(paramName));
		} 			
				
		return query;
	}
	
}
