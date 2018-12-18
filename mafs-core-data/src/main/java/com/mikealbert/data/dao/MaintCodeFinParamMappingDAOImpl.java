package com.mikealbert.data.dao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.persistence.Query;

import org.springframework.cache.annotation.Cacheable;

import com.mikealbert.data.entity.MaintenanceCode;
import com.mikealbert.data.vo.MaintCodeFinParamMappingVO;

public class MaintCodeFinParamMappingDAOImpl extends GenericDAOImpl<MaintenanceCode, Long> implements MaintCodeFinParamMappingDAOCustom{
	private static final long serialVersionUID = 1L;
	//private MalLogger logger = MalLoggerFactory.getLogger(MaintenanceCodeFeeDAOImpl.class);	

	@Cacheable("maintCodeFinParamMapping")
	public List<MaintCodeFinParamMappingVO> findMaintenanceCodeFinanceParameterValues(String maintCode) {
		Query query = generateMaintCodeFinParamQuery(maintCode);
		return populateMaintCodeFinParamVO(query);
	}

	@SuppressWarnings("unchecked")
	private List<MaintCodeFinParamMappingVO> populateMaintCodeFinParamVO (Query query) {
		List<MaintCodeFinParamMappingVO>  maintCodeFinParamVOList = new ArrayList<MaintCodeFinParamMappingVO>();;
		
		List<Object[]> objectList  = (List<Object[]>) query.getResultList();
		
		if (objectList != null && objectList.size() > 0) {	
			
			for (Iterator<Object[]> iterator = objectList.iterator(); iterator.hasNext();) {
				Object[] object = iterator.next();				
				MaintCodeFinParamMappingVO maintCodeFinParamMappingVO = new MaintCodeFinParamMappingVO();
				int i = 0;
				
				maintCodeFinParamMappingVO.setMaintCodeFinParamMapId(object[i]			!= null ? ((BigDecimal) object[i]).longValue() 	: null);
				maintCodeFinParamMappingVO.setMaintCodeId(object[i+=1]					!= null ? ((BigDecimal) object[i]).longValue() 	: null);
				maintCodeFinParamMappingVO.setMaintCode(object[i+=1]						!= null ? (String) 		object[i] 				: null);
				maintCodeFinParamMappingVO.setMaintCodeDescription(object[i+=1]			!= null ? (String) 		object[i] 				: null);
				maintCodeFinParamMappingVO.setMaintCategoryCode(object[i+=1]				!= null ? (String) 		object[i] 				: null);
				maintCodeFinParamMappingVO.setFinanceParameterId(object[i+=1]				!= null ? (String)      object[i]           	: null);
				maintCodeFinParamMappingVO.setFinanceParameterKey(object[i+=1]			!= null ? (String) 		object[i] 				: null);
				maintCodeFinParamMappingVO.setFinanceParameterDescription(object[i+=1]	!= null ? (String) 		object[i] 				: null);
				maintCodeFinParamMappingVO.setFinanceParameterEffectiveFrom(object[i+=1]	!= null ? (Date)		object[i] 				: null);
				maintCodeFinParamVOList.add(maintCodeFinParamMappingVO);
			}
		}
		return maintCodeFinParamVOList;
	}
	
	private Query generateMaintCodeFinParamQuery(String maintCode) {
		Query query =  null;
		StringBuilder sqlStmt = new StringBuilder("");
		
		sqlStmt.append("SELECT" +
					   "     mcfpm.mcfp_id," + 
				       "     mc.mco_id," +
				       "     mc.maint_code," + 
				       "     mc.maint_code_desc," + 
				       "     mc.maint_cat_code," +
				       "     fp.parameter_id," + 
				       "     fp.parameter_key," +
				       "     fp.description," + 
				       "     fp.effective_from" +
				       " FROM maintenance_codes mc" +
				       " INNER JOIN maint_codes_fin_params_mapping mcfpm" + 
				       "   ON mcfpm.mco_mco_id = mc.mco_id" +
				       " INNER JOIN finance_parameters fp" + 
				       "   ON fp.parameter_id = mcfpm.param_param_id" +
				       " WHERE mc.maint_code = :maintCode");
		
		//logger.debug("Final Query: " + sqlStmt.toString());  
		query = entityManager.createNativeQuery(sqlStmt.toString());
		query.setParameter("maintCode", maintCode);
		
		return query;
	}	
}
