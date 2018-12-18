package com.mikealbert.data.dao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;
import javax.persistence.Query;

import com.mikealbert.common.MalLogger;
import com.mikealbert.common.MalLoggerFactory;
import com.mikealbert.data.entity.ClientFinance;
import com.mikealbert.data.enumeration.ClientFinanceTypeCodes;
import com.mikealbert.data.vo.ClientFinanceVO;

public class ClientFinanceDAOImpl extends GenericDAOImpl<ClientFinance, Long> implements ClientFinanceDAOCustom{

	@Resource
	private ClientFinanceDAO clientFinanceDAO;
	
	private static final long serialVersionUID = 1L;
	private MalLogger logger = MalLoggerFactory.getLogger(ClientFinanceDAOImpl.class);

	public List<ClientFinanceVO> searchClientFinanceParametersByAccount(Long corpId, String accountType, String accountCode, Long paramCategoryId){
		Query query = generateSearchAccountQuery(corpId, accountType, accountCode, paramCategoryId);
		return populateClientFinanceVO(query);
	}
	
	public List<ClientFinanceVO> searchClientFinanceParametersByGradeGroup(Long corpId, String accountType, String accountCode, String parameterId){
		Query query = generateSearchClientGradeGroupQuery(corpId, accountType, accountCode, parameterId);
		return populateClientFinanceGradeGroupVO(query);
	}
	
	public List<ClientFinanceVO> searchClientFinanceParametersByCostCentre(Long corpId, String accountType, String accountCode, String parameterId){
		Query query = generateSearchClientCostCentreQuery(corpId, accountType, accountCode, parameterId);
		return populateClientFinanceCostCentreVO(query);
	}
	
	@SuppressWarnings("unchecked")
	private List<ClientFinanceVO> populateClientFinanceVO (Query query) {
		List<ClientFinanceVO>  clientFinanceVOList = new ArrayList<ClientFinanceVO>();;
		
		List<Object[]> objectList  = (List<Object[]>) query.getResultList();
		
		if (objectList != null && objectList.size() > 0) {	
			
			for (Iterator<Object[]> iterator = objectList.iterator(); iterator.hasNext();) {
				Object[] object = iterator.next();				
				ClientFinanceVO clientFinanceVO = new ClientFinanceVO();
				int i = 0;
				
				clientFinanceVO.setClientFinanceId(object[i]			!= null ? ((BigDecimal) object[i]).longValue() 	: null);
				clientFinanceVO.setEaCId((Long) (object[i+=1]			!= null ? ((BigDecimal) object[i]).longValue()	: query.getParameterValue("v_c_id")));
				clientFinanceVO.setEaAccountType((String) (object[i+=1]	!= null ? (String) 		object[i]			 	: query.getParameterValue("v_account_type")));
				clientFinanceVO.setEaAccountCode((String) (object[i+=1]	!= null ? (String) 		object[i]			 	: query.getParameterValue("v_account_code")));
				clientFinanceVO.setClientGradeGroupId(object[i+=1]		!= null ? ((BigDecimal) object[i]).longValue()	: null);
				clientFinanceVO.setClientCostCentreCode(object[i+=1]	!= null ? (String) 		object[i] 				: null);
				clientFinanceVO.setParameterId((String) (object[i+=1]	!= null ? (String) 		object[i] 				: query.getParameterValue("v_parameter_id")));
				clientFinanceVO.setFinanceParameterValue(object[i+=1]	!= null ? (String) 		object[i]				: null);
				clientFinanceVO.setLastUpdated(object[i+=1]				!= null ? (Date)		object[i] 				: null);
				clientFinanceVO.setDescription(object[i+=1]				!= null ? (String)		object[i] 				: null);
				clientFinanceVO.setFinanceParamCatId(object[i+=1]		!= null ? ((BigDecimal)	object[i]).longValue() 	: null);
				clientFinanceVO.setFinanceParamCategory(object[i+=1]	!= null ? (String)		object[i] 				: null);
				clientFinanceVO.setStatus(object[i+=1]					!= null ? (String)		object[i]				: null);
				clientFinanceVO.setDefaultnvalue(object[i+=1]			!= null ? (BigDecimal)	object[i]				: null);
				clientFinanceVO.setDefaultcvalue(object[i+=1]			!= null ? (String)		object[i]				: null);
				clientFinanceVO.setGgIndicator(object[i+=1]				!= null ? 				object[i].toString()	: null);
				clientFinanceVO.setCcIndicator(object[i+=1]				!= null ? 				object[i].toString()	: null);
				clientFinanceVOList.add(clientFinanceVO);
			}
		}
		return clientFinanceVOList;
	}
	
	private Query generateSearchAccountQuery(Long cId, String accountType, String accountCode, Long paramCategoryId) {
		Query query =  null;
		
		StringBuilder sqlStmt = new StringBuilder("");
		
		sqlStmt.append("SELECT " +
				" cf.cf_id," +
				" cf.ea_c_id," +
				" cf.ea_account_type," +
				" cf.ea_account_code," +
				" cf.eag_eag_id," +
				" cf.cost_centre_code," +
				" fp.parameter_id," +
				" NVL(TO_CHAR(cf.nvalue), cf.cvalue)," +
				" cf.last_updated," + 
				" fp.description," +
				" fpc.fpc_id," +
				" fpc.category_name," +
				" fp.status," + 
				" fp.nvalue defaultnvalue," + 
				" fp.cvalue defaultcvalue, " +
				"(SELECT 'Y' " +
			    "	FROM client_finances cfgg" +
	            "     WHERE cfgg.ea_c_id = :v_c_id" +
	            "     AND cfgg.ea_account_type = :v_account_type" +
	            "     AND cfgg.ea_account_code = :v_account_code" +
	            "     AND cfgg.eag_eag_id IS NOT NULL" +
	            "	  AND cfgg.parameter_id = fp.parameter_id" +
	            "     AND ROWNUM = 1) gg_indicator," +
	            "(SELECT 'Y' " +
			    "	FROM client_finances cfcc" +
	            "     WHERE cfcc.ea_c_id = :v_c_id" +
	            "     AND cfcc.ea_account_type = :v_account_type" +
	            "     AND cfcc.ea_account_code = :v_account_code" +
	            "     AND cfcc.cost_centre_code IS NOT NULL" +
	            "	  AND cfcc.parameter_id = fp.parameter_id" +
	            "     AND ROWNUM = 1) cc_indicator" +
				" FROM finance_parameters fp" +
				" INNER JOIN finance_parameter_categories fpc on fpc.fpc_id = fp.fpc_fpc_id" +
				" AND fp.fpc_fpc_id is not null");
		if(paramCategoryId > 0) {
			sqlStmt.append(" AND fpc.fpc_id = :fpc_id");
		}
		
		sqlStmt.append(" LEFT JOIN client_finances cf on cf.parameter_id = fp.parameter_id" +
                	   " AND cf.ea_c_id = :v_c_id" +
                	   " AND cf.ea_account_type = :v_account_type" +
                	   " AND cf.ea_account_code = :v_account_code" +
					   " AND cf.cost_centre_code IS NULL" +
				       " AND cf.eag_eag_id IS NULL");
		
		sqlStmt.append(" LEFT JOIN client_finance_types cft" +
					   " ON cft.cft_id = cf.cft_cft_id" +
					   " AND cft.finance_type_code = :v_finance_type_code");

		sqlStmt.append(" ORDER BY fpc.category_name ASC, fp.description ASC");
		
		logger.debug("Final Query: " + sqlStmt.toString());  
		query = entityManager.createNativeQuery(sqlStmt.toString());

		query.setParameter("v_c_id", cId);
		query.setParameter("v_account_type", accountType);
		query.setParameter("v_account_code", accountCode);
		query.setParameter("v_finance_type_code", ClientFinanceTypeCodes.ACCOUNT.getId());

		if(paramCategoryId > 0){
			query.setParameter("fpc_id", paramCategoryId);
		}
		
		return query;
	}
	
	@SuppressWarnings("unchecked")
	private List<ClientFinanceVO> populateClientFinanceGradeGroupVO (Query query) {
		List<ClientFinanceVO>  clientFinanceVOList = new ArrayList<ClientFinanceVO>();;
		
		List<Object[]> objectList  = (List<Object[]>) query.getResultList();
		
		if (objectList != null && objectList.size() > 0) {	
			
			for (Iterator<Object[]> iterator = objectList.iterator(); iterator.hasNext();) {
				Object[] object = iterator.next();				
				ClientFinanceVO clientFinanceVO = new ClientFinanceVO();
				int i = 0;
				
				clientFinanceVO.setClientFinanceId(object[i]			!= null ? ((BigDecimal) object[i]).longValue() 	: null);
				clientFinanceVO.setClientFinanceTypeId(object[i+=1]		!= null ? ((BigDecimal) object[i]).longValue() 	: null);
				clientFinanceVO.setEaCId((object[i+=1]					!= null ? ((BigDecimal) object[i]).longValue()	: (Long)   query.getParameterValue("v_c_id")));
				clientFinanceVO.setEaAccountType((object[i+=1]			!= null ? (String) 		object[i]			 	: (String) query.getParameterValue("v_account_type")));
				clientFinanceVO.setEaAccountCode((object[i+=1]			!= null ? (String) 		object[i]			 	: (String) query.getParameterValue("v_account_code")));
				clientFinanceVO.setClientGradeGroupId(object[i+=1]		!= null ? ((BigDecimal) object[i]).longValue()	: null);
				clientFinanceVO.setClientGradeGroupCode(object[i+=1]	!= null ? (String) 		object[i] 				: null);
				clientFinanceVO.setClientGradeGroupDesc(object[i+=1]	!= null ? (String) 		object[i] 				: null);
				clientFinanceVO.setClientCostCentreCode(object[i+=1]	!= null ? (String) 		object[i] 				: null);
				clientFinanceVO.setParameterId(object[i+=1]				!= null ? (String) 		object[i] 				: (String) query.getParameterValue("v_parameter_id"));
				clientFinanceVO.setFinanceParameterValue(object[i+=1]	!= null ? (String) 		object[i]				: null);
				clientFinanceVO.setLastUpdated(object[i+=1]				!= null ? (Date)		object[i] 				: null);
				clientFinanceVO.setFinParamAllow(object[i+=1]			!= null ? (String) 		object[i].toString()	: null);
				clientFinanceVOList.add(clientFinanceVO);
			}
		}
		return clientFinanceVOList;
	}
	
	private Query generateSearchClientGradeGroupQuery(Long cId, String accountType, String accountCode, String parameterId) {
		Query query =  null;
		
		StringBuilder sqlStmt = new StringBuilder("");
		
		sqlStmt.append("SELECT " + 
						"       cf.cf_id," + 
						"       cf.cft_cft_id," +
						"       eagg.ea_c_id," +
						"       eagg.ea_account_type," +
						"       eagg.ea_account_code," +
						"       eagg.eag_id," +
						"       dggc.driver_grade_group," +
						"       dggc.description," +
						"       cf.cost_centre_code," +
						"       cf.parameter_id," +
						"       NVL(TO_CHAR(cf.nvalue), cf.cvalue)," + 
						"		cf.last_updated," +
						"		eagg.fin_param_allow");
		
		sqlStmt.append("  FROM external_account_grade_groups eagg, client_finances cf, driver_grade_group_codes dggc");
		
		sqlStmt.append(" WHERE cf.ea_c_id (+) = eagg.ea_c_id" +
					   "       AND cf.ea_account_type (+) = eagg.ea_account_type" +
					   "       AND cf.ea_account_code (+) = eagg.ea_account_code" +
					   "	   AND cf.eag_eag_id (+) = eagg.eag_id" +
					   "	   AND cf.cft_cft_id (+) = :v_finance_type_code" +
					   " 	   AND dggc.driver_grade_group = eagg.driver_grade_group" +
					   "	   AND cf.parameter_id (+) = :v_parameter_id" +
					   "	   AND eagg.ea_c_id = :v_c_id" +
					   "	   AND eagg.ea_account_type = :v_account_type" +
					   "       AND eagg.ea_account_code = :v_account_code");
		
		sqlStmt.append(" ORDER BY dggc.driver_grade_group ASC");
		
		logger.debug("Final Query: " + sqlStmt.toString());  
		query = entityManager.createNativeQuery(sqlStmt.toString());

		query.setParameter("v_c_id", cId);
		query.setParameter("v_account_type", accountType);
		query.setParameter("v_account_code", accountCode);
		query.setParameter("v_parameter_id", parameterId);
		query.setParameter("v_finance_type_code", ClientFinanceTypeCodes.EXTERNAL_ACCOUNT_GRADE_GROUP.getId());

		return query;
	}
	
	@SuppressWarnings("unchecked")
	private List<ClientFinanceVO> populateClientFinanceCostCentreVO (Query query) {
		List<ClientFinanceVO>  clientFinanceVOList = new ArrayList<ClientFinanceVO>();;
		
		List<Object[]> objectList  = (List<Object[]>) query.getResultList();
		
		if (objectList != null && objectList.size() > 0) {	
			
			for (Iterator<Object[]> iterator = objectList.iterator(); iterator.hasNext();) {
				Object[] object = iterator.next();				
				ClientFinanceVO clientFinanceVO = new ClientFinanceVO();
				int i = 0;
				
				clientFinanceVO.setClientFinanceId(object[i]			!= null ? ((BigDecimal) object[i]).longValue() 	: null);
				clientFinanceVO.setClientFinanceTypeId(object[i+=1]		!= null ? ((BigDecimal) object[i]).longValue() 	: null);
				clientFinanceVO.setEaCId(object[i+=1]					!= null ? ((BigDecimal) object[i]).longValue()	: (Long)   query.getParameterValue("v_c_id"));
				clientFinanceVO.setEaAccountType(object[i+=1]			!= null ? (String) 		object[i]			 	: (String) query.getParameterValue("v_account_type"));
				clientFinanceVO.setEaAccountCode(object[i+=1]			!= null ? (String) 		object[i]			 	: (String) query.getParameterValue("v_account_code"));
				clientFinanceVO.setClientCostCentreLevel(object[i+=1]	!= null ? ((BigDecimal) object[i]).intValue() 	: null);
				clientFinanceVO.setClientCostCentreCode(object[i+=1]	!= null ? (String) 		object[i] 				: null);
				clientFinanceVO.setClientCostCentreDesc(object[i+=1]	!= null ? (String) 		object[i] 				: null);
				clientFinanceVO.setClientCostCentreParent(object[i+=1]	!= null ? (String) 		object[i] 				: null);
				clientFinanceVO.setParameterId(object[i+=1]				!= null ? (String) 		object[i] 				: (String) query.getParameterValue("v_parameter_id"));
				clientFinanceVO.setFinanceParameterValue(object[i+=1]	!= null ? (String) 		object[i]				: null);
				clientFinanceVO.setLastUpdated(object[i+=1]				!= null ? (Date)		object[i] 				: null);
				clientFinanceVO.setFinParamAllow(object[i+=1]			!= null ? (String) 		object[i].toString()	: null);
				clientFinanceVOList.add(clientFinanceVO);
			}
		}
		return clientFinanceVOList;
	}
	
	private Query generateSearchClientCostCentreQuery(Long cId, String accountType, String accountCode, String parameterId) {
		Query query =  null;
		
		StringBuilder sqlStmt = new StringBuilder("");
		
		sqlStmt.append("            SELECT" + 
	              	   "                   cf.cf_id," +
	                   "                   cf.cft_cft_id," +
	                   "                   cost_centres.ea_c_id," +
	                   "                   cost_centres.ea_account_type," +
	                   "                   cost_centres.ea_account_code," +
	                   "                   LEVEL," +
	                   "                   cost_centres.cost_centre_code," +
	                   "                   cost_centres.description," +
	                   "                   cost_centres.parent_cc_code," +
	                   "                   cf.parameter_id," +
	                   "                   NVL(TO_CHAR(cf.nvalue), cf.cvalue)," +
	                   "                   cf.last_updated," +
					   "                   cost_centres.fin_param_allow");
		
		sqlStmt.append("              FROM (SELECT *" +
                	   "                      FROM cost_centre_codes" +
                       "                     WHERE ea_c_id = :v_c_id" +
                       "                       AND ea_account_type = :v_account_type" +
                       "                       AND ea_account_code = :v_account_code) cost_centres, client_finances cf");
		
		sqlStmt.append("             WHERE cf.ea_c_id (+) = cost_centres.ea_c_id" +
                  	   "               AND cf.ea_account_type (+) = cost_centres.ea_account_type" +
                       "	           AND cf.ea_account_code (+) = cost_centres.ea_account_code" +
                       "	           AND cf.cost_centre_code (+) = cost_centres.cost_centre_code" +
                       "	  		   AND cf.parameter_id (+) = :v_parameter_id" +
                       "	  		   AND cf.cft_cft_id (+) = :v_finance_type_code" + 
                       "             START WITH cost_centres.parent_cc_code IS NULL" +
                       "	       CONNECT BY PRIOR cost_centres.cost_centre_code = cost_centres.parent_cc_code" +
					   " ORDER SIBLINGS BY cost_centres.cost_centre_code");
		
		logger.debug("Final Query: " + sqlStmt.toString());  
		query = entityManager.createNativeQuery(sqlStmt.toString());

		query.setParameter("v_c_id", cId);
		query.setParameter("v_account_type", accountType);
		query.setParameter("v_account_code", accountCode);
		query.setParameter("v_parameter_id", parameterId);
		query.setParameter("v_finance_type_code", ClientFinanceTypeCodes.DRIVER_COST_CENTRE.getId());

		return query;
	}
	
}
