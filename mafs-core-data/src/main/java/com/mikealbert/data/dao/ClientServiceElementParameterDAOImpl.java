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
import com.mikealbert.data.entity.ClientServiceElementParameter;
import com.mikealbert.data.enumeration.ClientServiceElementTypeCodes;
import com.mikealbert.data.vo.ClientServiceElementParameterVO;

public class ClientServiceElementParameterDAOImpl extends GenericDAOImpl<ClientServiceElementParameter, Long> implements ClientServiceElementParameterDAOCustom {
	@Resource
	private ClientServiceElementParameterDAO clientServiceElementParameterDAO;
	
	private static final long serialVersionUID = 1L;
	private MalLogger logger = MalLoggerFactory.getLogger(ClientFinanceDAOImpl.class);

	@Override
	public List<ClientServiceElementParameterVO> getServiceElementParametersByClientServiceElement(Long leaseElementId, Long clientServiceElementId) {
		Query query = serviceElementParametersByClientServiceElementQuery(leaseElementId, clientServiceElementId);
		return populateClientServiceElementParameterVO(query);
	}
	
	@Override
	public List<ClientServiceElementParameterVO> getGradeGroupServiceElementParametersByClientServiceElement(Long leaseElementId, Long clientServiceElementId) {
		Query query = gradeGroupServiceElementParamsByClientServiceElementQuery(leaseElementId, clientServiceElementId);
		return populateGradeGroupServiceElementParameterVO(query);
	}
	
	@Override
	public List<ClientServiceElementParameterVO> getProductServiceElementParametersByClientServiceElement(Long leaseElementId, Long clientServiceElementId) {
		Query query = clientProductOverrideServiceElementParamsQuery(leaseElementId, clientServiceElementId);
		return populateClientProductServiceElementParameterVO(query);
	}	
	
	@Override
	public List<ClientServiceElementParameterVO> getGradeGroupProductServiceElementParametersByClientServiceElement(Long leaseElementId, Long clientServiceElementId) {
		Query query = gradeGroupProductOverrideServiceElementParamsQuery(leaseElementId, clientServiceElementId);
		return populateGradeGroupProductServiceElementParameterVO(query);
	}		
	
	@SuppressWarnings("unchecked")
	private List<ClientServiceElementParameterVO> populateClientServiceElementParameterVO(Query query) {
		List<ClientServiceElementParameterVO>  clientServiceElementParameterVOList = new ArrayList<ClientServiceElementParameterVO>();;
		
		List<Object[]> objectList  = (List<Object[]>) query.getResultList();
		
		if (objectList != null && objectList.size() > 0) {	
			
			for (Iterator<Object[]> iterator = objectList.iterator(); iterator.hasNext();) {
				Object[] object = iterator.next();				
				ClientServiceElementParameterVO clientServiceElementParameterVO = new ClientServiceElementParameterVO();
				int i = 0;
				
				clientServiceElementParameterVO.setClientServiceElementParameterId(object[i]	!= null ? ((BigDecimal) 	object[i]).longValue()	: null);
				clientServiceElementParameterVO.setClientServiceElementId((object[i+=1]			!= null ? ((BigDecimal) 	object[i]).longValue()	: null));
				clientServiceElementParameterVO.setFormulaParameterId((object[i+=1]				!= null ? ((BigDecimal) 	object[i]).longValue()	: null));
				clientServiceElementParameterVO.setDefaultValue((object[i+=1]					!= null ? ((BigDecimal) 	object[i])				: null));
				clientServiceElementParameterVO.setClientValue(object[i+=1]						!= null ? ((BigDecimal) 	object[i])				: null);
				clientServiceElementParameterVO.setParameterId((String) object[i+=1]			!= null ? (String) 			object[i] 				: null);
				clientServiceElementParameterVO.setParameterKey((String) (object[i+=1]			!= null ? (String) 			object[i] 				: null));
				clientServiceElementParameterVO.setParameterDescription((String) object[i+=1]	!= null ? (String) 			object[i]				: null);
				clientServiceElementParameterVO.setLastUpdated(object[i+=1]						!= null ? (Date)			object[i] 				: null);

				clientServiceElementParameterVOList.add(clientServiceElementParameterVO);
			}
		}
		return clientServiceElementParameterVOList;
		
	}
	
	@SuppressWarnings("unchecked")
	private List<ClientServiceElementParameterVO> populateGradeGroupServiceElementParameterVO(Query query) {
		List<ClientServiceElementParameterVO>  clientServiceElementParameterVOList = new ArrayList<ClientServiceElementParameterVO>();;
		
		List<Object[]> objectList  = (List<Object[]>) query.getResultList();
		
		if (objectList != null && objectList.size() > 0) {	
			
			for (Iterator<Object[]> iterator = objectList.iterator(); iterator.hasNext();) {
				Object[] object = iterator.next();				
				ClientServiceElementParameterVO clientServiceElementParameterVO = new ClientServiceElementParameterVO();
				int i = 0;
				
				clientServiceElementParameterVO.setClientServiceElementParameterId(object[i]	!= null ? ((BigDecimal) 	object[i]).longValue()	: null);
				clientServiceElementParameterVO.setClientServiceElementId((object[i+=1]			!= null ? ((BigDecimal) 	object[i]).longValue()	: null));
				clientServiceElementParameterVO.setFormulaParameterId((object[i+=1]				!= null ? ((BigDecimal) 	object[i]).longValue()	: null));
				clientServiceElementParameterVO.setDefaultValue((object[i+=1]					!= null ? ((BigDecimal) 	object[i])				: null));
				clientServiceElementParameterVO.setClientValue(object[i+=1]						!= null ? ((BigDecimal) 	object[i])				: null);
				clientServiceElementParameterVO.setGradeGroupValue(object[i+=1]					!= null ? ((BigDecimal) 	object[i])				: null);
				clientServiceElementParameterVO.setParameterId((String) object[i+=1]			!= null ? (String) 			object[i] 				: null);
				clientServiceElementParameterVO.setParameterKey((String) (object[i+=1]			!= null ? (String) 			object[i] 				: null));
				clientServiceElementParameterVO.setParameterDescription((String) object[i+=1]	!= null ? (String) 			object[i]				: null);
				clientServiceElementParameterVO.setLastUpdated(object[i+=1]						!= null ? (Date)			object[i] 				: null);

				clientServiceElementParameterVOList.add(clientServiceElementParameterVO);
			}
		}
		return clientServiceElementParameterVOList;
		
	}
	
	@SuppressWarnings("unchecked")
	private List<ClientServiceElementParameterVO> populateClientProductServiceElementParameterVO(Query query) {
		List<ClientServiceElementParameterVO>  clientServiceElementParameterVOList = new ArrayList<ClientServiceElementParameterVO>();;
		
		List<Object[]> objectList  = (List<Object[]>) query.getResultList();
		
		if (objectList != null && objectList.size() > 0) {	
			
			for (Iterator<Object[]> iterator = objectList.iterator(); iterator.hasNext();) {
				Object[] object = iterator.next();				
				ClientServiceElementParameterVO clientServiceElementParameterVO = new ClientServiceElementParameterVO();
				int i = 0;
				
				clientServiceElementParameterVO.setClientServiceElementParameterId(object[i]	!= null ? ((BigDecimal) 	object[i]).longValue()	: null);
				clientServiceElementParameterVO.setClientServiceElementId((object[i+=1]			!= null ? ((BigDecimal) 	object[i]).longValue()	: null));
				clientServiceElementParameterVO.setFormulaParameterId((object[i+=1]				!= null ? ((BigDecimal) 	object[i]).longValue()	: null));
				clientServiceElementParameterVO.setDefaultValue((object[i+=1]					!= null ? ((BigDecimal) 	object[i])				: null));
				clientServiceElementParameterVO.setClientValue(object[i+=1]						!= null ? ((BigDecimal) 	object[i])				: null);
				clientServiceElementParameterVO.setProductValue(object[i+=1]					!= null ? ((BigDecimal) 	object[i])				: null);
				clientServiceElementParameterVO.setParameterId((String) object[i+=1]			!= null ? (String) 			object[i] 				: null);
				clientServiceElementParameterVO.setParameterKey((String) (object[i+=1]			!= null ? (String) 			object[i] 				: null));
				clientServiceElementParameterVO.setParameterDescription((String) object[i+=1]	!= null ? (String) 			object[i]				: null);
				clientServiceElementParameterVO.setLastUpdated(object[i+=1]						!= null ? (Date)			object[i] 				: null);

				clientServiceElementParameterVOList.add(clientServiceElementParameterVO);
			}
		}
		return clientServiceElementParameterVOList;
	}
	
	@SuppressWarnings("unchecked")
	private List<ClientServiceElementParameterVO> populateGradeGroupProductServiceElementParameterVO(Query query) {
		List<ClientServiceElementParameterVO>  clientServiceElementParameterVOList = new ArrayList<ClientServiceElementParameterVO>();;
		
		List<Object[]> objectList  = (List<Object[]>) query.getResultList();
		
		if (objectList != null && objectList.size() > 0) {	
			
			for (Iterator<Object[]> iterator = objectList.iterator(); iterator.hasNext();) {
				Object[] object = iterator.next();				
				ClientServiceElementParameterVO clientServiceElementParameterVO = new ClientServiceElementParameterVO();
				int i = 0;
				
				clientServiceElementParameterVO.setClientServiceElementParameterId(object[i]	!= null ? ((BigDecimal) 	object[i]).longValue()	: null);
				clientServiceElementParameterVO.setClientServiceElementId((object[i+=1]			!= null ? ((BigDecimal) 	object[i]).longValue()	: null));
				clientServiceElementParameterVO.setFormulaParameterId((object[i+=1]				!= null ? ((BigDecimal) 	object[i]).longValue()	: null));
				clientServiceElementParameterVO.setDefaultValue((object[i+=1]					!= null ? ((BigDecimal) 	object[i])				: null));
				clientServiceElementParameterVO.setClientValue(object[i+=1]						!= null ? ((BigDecimal) 	object[i])				: null);
				clientServiceElementParameterVO.setGradeGroupValue(object[i+=1]					!= null ? ((BigDecimal) 	object[i])				: null);
				clientServiceElementParameterVO.setProductValue(object[i+=1]					!= null ? ((BigDecimal) 	object[i])				: null);
				clientServiceElementParameterVO.setParameterId((String) object[i+=1]			!= null ? (String) 			object[i] 				: null);
				clientServiceElementParameterVO.setParameterKey((String) (object[i+=1]			!= null ? (String) 			object[i] 				: null));
				clientServiceElementParameterVO.setParameterDescription((String) object[i+=1]	!= null ? (String) 			object[i]				: null);
				clientServiceElementParameterVO.setLastUpdated(object[i+=1]						!= null ? (Date)			object[i] 				: null);

				clientServiceElementParameterVOList.add(clientServiceElementParameterVO);
			}
		}
		return clientServiceElementParameterVOList;
		
	}	
	
	private Query serviceElementParametersByClientServiceElementQuery(Long leaseElementId, Long clientServiceElementId) {
		Query query =  null;
		
		StringBuilder sqlStmt = new StringBuilder("");
		
		sqlStmt.append("SELECT csep.csep_id," +
					   "       (SELECT cse_id FROM client_service_elements WHERE cse_id = :cseId)," +
					   "       default_params.fpr_id," +
					   "       default_params.nvalue," +
					   "       csep.value," +
					   "       default_params.parameter_id," +
					   "       default_params.parameter_key," +
					   "       default_params.description," +
					   "       csep.versionts" +
					   "  FROM (SELECT *" +
					   "          FROM finance_parameters fp, formula_parameters fpr" +
					   "         WHERE effective_from IN (SELECT MAX(effective_from)" +
					   "                                    FROM finance_parameters" +
					   "                                   WHERE parameter_key = fp.parameter_key" +
					   "                                     AND effective_from <= SYSDATE)" +
					   "           AND fpr.parameter_type = 'F'" +
					   "           AND fpr.lel_lel_id = :lelId" +
					   "           AND fp.parameter_key = fpr.parameter_name) default_params, client_serv_element_params csep" +
					   " WHERE csep.fpr_fpr_id (+) = default_params.fpr_id" +
					   "   AND csep.cse_cse_id (+) = :cseId" +
					   " ORDER BY default_params.parameter_key");
		
		logger.debug("Final Query: " + sqlStmt.toString());  
		query = entityManager.createNativeQuery(sqlStmt.toString());

		query.setParameter("lelId", leaseElementId);
		query.setParameter("cseId", clientServiceElementId);
		
		return query;
	}

	private Query clientProductOverrideServiceElementParamsQuery(Long leaseElementId, Long clientServiceElementId) {
		Query query =  null;
		
		StringBuilder sqlStmt = new StringBuilder("");
		
		
		sqlStmt.append("WITH" +
						"	default_params AS (SELECT fpr.fpr_id, fp.parameter_id, fp.parameter_key, fp.description, fp.nvalue" +
						"	                    FROM finance_parameters fp, formula_parameters fpr" +
						"	                   WHERE effective_from IN (SELECT MAX (effective_from)" +
						"	                                              FROM finance_parameters" +
						"	                                               WHERE parameter_key = fp.parameter_key" +
						"	                                                 AND effective_from <= SYSDATE)" +
						"	                       AND fpr.parameter_type = 'F'" +
						"	                       AND fpr.lel_lel_id = :lelId" +
						"	                       AND fp.parameter_key = fpr.parameter_name)," +
						"	client_params AS (SELECT csep.csep_id, cse.cse_id, csep.value, csep.fpr_fpr_id, csep.versionts" +
						"	                    FROM client_serv_element_params csep, (SELECT cse.*" +
						"	                                                             FROM client_service_elements cse, client_service_elements pcse" +
						"	                                                            WHERE pcse.cse_id = :cseId" +
						"	                                                              AND pcse.ea_c_id = cse.ea_c_id" +
						"	                                                              AND pcse.ea_account_type = cse.ea_account_type" +
						"	                                                              AND pcse.ea_account_code = cse.ea_account_code" +
						"	                                                              AND cse.cset_cset_id = :cseType" +
						"	                                                              AND pcse.ccse_ccse_id = cse.ccse_ccse_id" + 
						"	                                                               AND cse.prd_product_code is null" +
						"	                                                              AND cse.start_date <= SYSDATE" +
						"	                                                              AND NVL(cse.end_date, SYSDATE) >= SYSDATE) cse" +
						"	                   WHERE csep.cse_cse_id (+) = cse.cse_id)," +
						"	product_params AS (SELECT csep.csep_id, cse.cse_id, csep.value, csep.fpr_fpr_id, csep.versionts" +
						"	               FROM client_serv_element_params csep, client_service_elements cse" +
						"	              WHERE cse.cse_id = :cseId" +
						"	                AND csep.cse_cse_id (+) = cse.cse_id" +
						"	                AND cse.cset_cset_id = 1)" + 
						"	 SELECT product_params.csep_id csep_id," +
						"	        (SELECT cse_id FROM client_service_elements WHERE cse_id = :cseId) cse_id," +
						"	        default_params.fpr_id fpr_id," +
						"	        default_params.nvalue default_value," +
						"	        client_params.value client_value," +
						"	        product_params.value product_value," +
						"	        default_params.parameter_id," +
						"	        default_params.parameter_key," +
						"	        default_params.description," +
						"	        product_params.versionts" +
						"	   FROM default_params, product_params, client_params" +
						"	  WHERE default_params.fpr_id = product_params.fpr_fpr_id(+)" +
						"	    AND default_params.fpr_id = client_params.fpr_fpr_id(+)" +
						"	  ORDER BY default_params.parameter_key");                        
	
		logger.debug("Final Query: " + sqlStmt.toString());  
		query = entityManager.createNativeQuery(sqlStmt.toString());

		query.setParameter("lelId", leaseElementId);
		query.setParameter("cseId", clientServiceElementId);
		query.setParameter("cseType", ClientServiceElementTypeCodes.ACCOUNT.getId());
		
		return query;
	}	
	
	
	
	private Query gradeGroupServiceElementParamsByClientServiceElementQuery(Long leaseElementId, Long clientServiceElementId) {
		Query query =  null;
		
		StringBuilder sqlStmt = new StringBuilder("");
		
		
		sqlStmt.append("WITH" +
	               "   default_params AS (SELECT fpr.fpr_id, fp.parameter_id, fp.parameter_key, fp.description, fp.nvalue" +
	               "                       FROM finance_parameters fp, formula_parameters fpr" +
	               "                      WHERE effective_from IN (SELECT MAX (effective_from)" +
	               "                                                 FROM finance_parameters" +
	               "                                                  WHERE parameter_key = fp.parameter_key" +
	               "                                                    AND effective_from <= SYSDATE)" +
	               "                          AND fpr.parameter_type = 'F'" +
	               "                          AND fpr.lel_lel_id = :lelId" +
	               "                          AND fp.parameter_key = fpr.parameter_name)," +
	               "    client_params AS (SELECT csep.csep_id, cse.cse_id, csep.value, csep.fpr_fpr_id, csep.versionts" +
	               "                        FROM client_serv_element_params csep, (SELECT cse.*" +
	               "                                                                 FROM client_service_elements cse, client_service_elements ggcse"+
	               "                                                                WHERE ggcse.cse_id = :cseId" +
	               "                                                                  AND ggcse.ea_c_id = cse.ea_c_id" + 
	               "                                                                  AND ggcse.ea_account_type = cse.ea_account_type" +
	               "                                                                  AND ggcse.ea_account_code = cse.ea_account_code" +
	               "                                                                  AND ggcse.cset_cset_id = :gradeGroupType" +
	               "                                                                  AND cse.cset_cset_id = :cseType" +
	               "																  AND cse.prd_product_code is null" +		
	               "                                                                  AND ggcse.ccse_ccse_id = cse.ccse_ccse_id" + 
	               "                                                                  AND cse.start_date <= SYSDATE" +
	               "                                                                  AND NVL(cse.end_date, SYSDATE) >= SYSDATE) cse" +
	               "                       WHERE csep.cse_cse_id (+) = cse.cse_id)," +
	               "    gg_params AS (SELECT csep.csep_id, cse.cse_id, csep.value, csep.fpr_fpr_id, csep.versionts" +
	               "                    FROM client_serv_element_params csep, client_service_elements cse" +
	               "                   WHERE cse.cse_id = :cseId" +
	               "                     AND csep.cse_cse_id (+) = cse.cse_id" +
	               "                     AND cse.cset_cset_id = 2)" +
	               "    SELECT gg_params.csep_id csep_id," +
	               "           (SELECT cse_id FROM client_service_elements WHERE cse_id = :cseId) cse_id," +
	               "           default_params.fpr_id fpr_id," +
	               "           default_params.nvalue default_value," +
	               "           client_params.value client_value," +
	               "           gg_params.value gg_value," +
	               "           default_params.parameter_id," +
	               "           default_params.parameter_key," +
	               "           default_params.description," +
	               "           gg_params.versionts" +
	               "      FROM default_params, gg_params, client_params" +
	               "     WHERE default_params.fpr_id = gg_params.fpr_fpr_id(+)" +
	               "       AND default_params.fpr_id = client_params.fpr_fpr_id(+)" +
	               "     ORDER BY default_params.parameter_key");
		
		logger.debug("Final Query: " + sqlStmt.toString());  
		query = entityManager.createNativeQuery(sqlStmt.toString());

		query.setParameter("lelId", leaseElementId);
		query.setParameter("cseId", clientServiceElementId);
		query.setParameter("cseType", ClientServiceElementTypeCodes.ACCOUNT.getId());
		query.setParameter("gradeGroupType", ClientServiceElementTypeCodes.EXTERNAL_ACCOUNT_GRADE_GROUP.getId());
		
		return query;
	}
	
	//TODO: Should probably be integrated with QUERY above
	private Query gradeGroupProductOverrideServiceElementParamsQuery(Long leaseElementId, Long clientServiceElementId) {
		Query query =  null;
		
		StringBuilder sqlStmt = new StringBuilder("");
		
		
		sqlStmt.append("WITH" +	
					"   default_params AS (SELECT fpr.fpr_id, fp.parameter_id, fp.parameter_key, fp.description, fp.nvalue" +
                    "		                 FROM finance_parameters fp, formula_parameters fpr" +
                    "		                WHERE effective_from IN (SELECT MAX (effective_from)" +
                    "		                                          FROM finance_parameters" +
                    "		                                           WHERE parameter_key = fp.parameter_key" +
                    "		                                             AND effective_from <= SYSDATE)" +
                    "		                  AND fpr.parameter_type = 'F'" +
                    "		                  AND fpr.lel_lel_id = :lelId" +
                    "                   	  AND fp.parameter_key = fpr.parameter_name)," +
                    "   client_params AS (SELECT csep.csep_id, cse.cse_id, csep.value, csep.fpr_fpr_id, csep.versionts" +
                    "                       FROM client_serv_element_params csep, (SELECT cse.*" +
                    "                                                                FROM client_service_elements cse, client_service_elements ggcse" +
                    "                                                         	    WHERE ggcse.cse_id = :cseId" +
                    "                                                                 AND ggcse.ea_c_id = cse.ea_c_id" +
                    "                                                                 AND ggcse.ea_account_type = cse.ea_account_type" +
                    "                                                                 AND ggcse.ea_account_code = cse.ea_account_code" +
                    "                                                                 AND ggcse.cset_cset_id = :gradeGroupType" +
                    "                                                                 AND cse.cset_cset_id = :cseType" +
                    "                                                                 AND cse.prd_product_code is null" +
                    "                                                                 AND ggcse.ccse_ccse_id = cse.ccse_ccse_id" +
                    "                                                                 AND cse.start_date <= SYSDATE" +
                    "                                                                 AND NVL(cse.end_date, SYSDATE) >= SYSDATE) cse" +
                    "                      WHERE csep.cse_cse_id (+) = cse.cse_id)," +
                    "   gg_params AS (SELECT csep.csep_id, cse.cse_id, csep.value, csep.fpr_fpr_id, csep.versionts" +
                    "                   FROM client_serv_element_params csep, (SELECT cse.*" +
                    "                                                            FROM client_service_elements cse, client_service_elements ggcse" +
                    "                                                           WHERE ggcse.cse_id = :cseId" +
                    "                                                             AND ggcse.ea_c_id = cse.ea_c_id" +
                    "                                                             AND ggcse.ea_account_type = cse.ea_account_type" +
                    "                                                             AND ggcse.ea_account_code = cse.ea_account_code" +
                    "                                                             AND ggcse.eag_eag_id = cse.eag_eag_id" +
                    "                                                             AND ggcse.cset_cset_id = 2" +
                    "                                                             AND cse.prd_product_code is null" +
                    "                                                             AND ggcse.ccse_ccse_id = cse.ccse_ccse_id" +
                    "                                                             AND cse.start_date <= SYSDATE" +
                    "                                                             AND NVL(cse.end_date, SYSDATE) >= SYSDATE) cse" +
                    "                  WHERE csep.cse_cse_id (+) = cse.cse_id)," +
                    "   product_params AS (SELECT csep.csep_id, cse.cse_id, csep.value, csep.fpr_fpr_id, csep.versionts" +
                    "                        FROM client_serv_element_params csep, client_service_elements cse" +
                    "                       WHERE cse.cse_id = :cseId" +
                    "                         AND csep.cse_cse_id (+) = cse.cse_id" +
                    "                         AND cse.cset_cset_id = 2)" +                                      
                    "   SELECT product_params.csep_id csep_id," +
                    "          (SELECT cse_id FROM client_service_elements WHERE cse_id = :cseId) cse_id," +
                    "          default_params.fpr_id fpr_id," +
                    "          default_params.nvalue default_value," +
                    "          client_params.value client_value," +
                    "          gg_params.value gg_value," +
                    "          product_params.value product_value," +
                    "          default_params.parameter_id," +
                    "          default_params.parameter_key," +
                    "          default_params.description," +
                    "          product_params.versionts" +
                    "     FROM default_params, gg_params, client_params, product_params" +
                    "    WHERE default_params.fpr_id = gg_params.fpr_fpr_id(+)" +
                    "      AND default_params.fpr_id = client_params.fpr_fpr_id(+)" +
                    "      AND default_params.fpr_id = product_params.fpr_fpr_id(+)" +
                    "    ORDER BY default_params.parameter_key");		
				
		logger.debug("Final Query: " + sqlStmt.toString());  
		query = entityManager.createNativeQuery(sqlStmt.toString());

		query.setParameter("lelId", leaseElementId);
		query.setParameter("cseId", clientServiceElementId);
		query.setParameter("cseType", ClientServiceElementTypeCodes.ACCOUNT.getId());
		query.setParameter("gradeGroupType", ClientServiceElementTypeCodes.EXTERNAL_ACCOUNT_GRADE_GROUP.getId());
		
		return query;
	}
	

	@Override
	public BigDecimal getParameterDefaultValuesSum(Long leaseElementId, Long clientServiceElementId) {
		Query query =  null;
		
		StringBuilder sqlStmt = new StringBuilder("");
		
		sqlStmt.append("SELECT SUM (fp.nvalue)" +
					   "  FROM client_serv_element_params csep, formula_parameters fpr, finance_parameters fp" +
					   " WHERE fp.effective_From IN (SELECT MAX (fpsub.effective_From)" +
					   "                               FROM finance_parameters fpsub" +
					   "                              WHERE fpsub.parameter_key = fp.parameter_key" +
					   "                                AND fpsub.effective_From <= SYSDATE)" +
					   "   AND fp.parameter_key = fpr.parameter_name" +
					   "   AND fpr.lel_lel_Id = :lelId" +
					   "   AND fpr.fpr_id = csep.fpr_fpr_id (+)" +
					   "   AND csep.cse_cse_id (+) = :cseId");
		
		logger.debug("Final Query: " + sqlStmt.toString());  
		query = entityManager.createNativeQuery(sqlStmt.toString());

		query.setParameter("lelId", leaseElementId);
		query.setParameter("cseId", clientServiceElementId);
		return (BigDecimal) query.getSingleResult();
	}

}
