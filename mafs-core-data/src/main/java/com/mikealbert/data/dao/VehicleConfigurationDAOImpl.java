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
import com.mikealbert.data.entity.VehicleConfiguration;
import com.mikealbert.data.vo.VehicleConfigModelVO;
import com.mikealbert.data.vo.VehicleConfigurationSearchCriteriaVO;
import com.mikealbert.data.vo.VehicleConfigurationSearchResultVO;
import com.mikealbert.util.MALUtilities;

public  class VehicleConfigurationDAOImpl extends GenericDAOImpl<VehicleConfiguration, Long> implements VehicleConfigurationDAOCustom {

	@Resource
	private VehicleConfigurationDAO vehicleConfigurationDAO;
	@Resource VehicleConfigGroupingDAO vehicleConfigGroupingDAO;
	
	private static final long serialVersionUID = 1L;
	private static MalLogger logger = MalLoggerFactory.getLogger(VehicleConfigurationDAOImpl.class);
	
	public List<VehicleConfigurationSearchResultVO> searchVehicleConfiguration(VehicleConfigurationSearchCriteriaVO searchCriteria, Pageable pageable, Sort sort) {
		Query query = searchVehicleConfigurationQuery(searchCriteria, false, sort);
		return populateVehicleConfigurationSearchResultVO(query, pageable, sort);
	}
	
	public int searchVehicleConfigurationCount(VehicleConfigurationSearchCriteriaVO searchCriteria) {
		Query query = searchVehicleConfigurationQuery(searchCriteria, true, null);
		return populateVehicleConfigurationSearchCount(query);
	}
	
	private List<VehicleConfigModelVO> getVehicleConfigModelQuery(String vcfIdList) {
		
		List<VehicleConfigModelVO> vehicleConfigModels = new ArrayList<VehicleConfigModelVO>();
		
		Query query = null;
		
		StringBuilder sqlStmt = new StringBuilder("");
		
		sqlStmt.append("SELECT vcm.vcf_vcf_id, " +
					   "       vcm.mfg_code, " +
					   "       vcm.make, " +
					   "       mrg.make_model_desc, " +
					   "	   vcm.year, " +
					   "       mdl.model_desc");
		
		sqlStmt.append("  FROM vehicle_config_models vcm, make_model_ranges mrg, models mdl" +
				       " WHERE vcm.mdl_mdl_id = mdl.mdl_id (+)" +
				       "   AND vcm.mrg_mrg_id = mrg.mrg_id (+)" +
				       "   AND vcm.obsolete_yn = 'N'");
		
		
		if (!MALUtilities.isEmpty(vcfIdList)) {
			sqlStmt.append("  AND vcm.vcf_vcf_id IN (").append(vcfIdList).append(")");
		}
		
		logger.debug("Final Query: " + sqlStmt.toString());
		query = entityManager.createNativeQuery(sqlStmt.toString());
		
		@SuppressWarnings("unchecked")
		List<Object[]> objectList  = (List<Object[]>) query.getResultList();
		
		if (objectList != null && objectList.size() > 0) {	
			
			for (Iterator<Object[]> iterator = objectList.iterator(); iterator.hasNext();) {
				Object[] object = iterator.next();				
				VehicleConfigModelVO VehicleConfigModelVO = new VehicleConfigModelVO();
				int i = 0;
				
				VehicleConfigModelVO.setVcfId(object[i]			!= null ? ((BigDecimal) object[i]).longValue() 	: null);
				VehicleConfigModelVO.setMfgCode(object[i+=1]	!= null ? (String) 		object[i]			 	: null);
				VehicleConfigModelVO.setMake(object[i+=1]		!= null ? (String)      object[i] 				: null);
				VehicleConfigModelVO.setModel(object[i+=1]		!= null ? (String) 		object[i]			 	: null);
				VehicleConfigModelVO.setYear(object[i+=1]		!= null ? (String) 		object[i]			 	: null);
				VehicleConfigModelVO.setTrim(object[i+=1]		!= null ? (String) 		object[i]			 	: null);
				vehicleConfigModels.add(VehicleConfigModelVO);

			}
		}
		
		return vehicleConfigModels;
	}
	
	private Query searchVehicleConfigurationQuery(VehicleConfigurationSearchCriteriaVO searchCriteria, Boolean isCountQuery, Sort sort) {
		Query query =  null;
		Map<String, Object> parameterMap = new HashMap<String, Object>();
		StringBuilder sqlStmt = new StringBuilder("");
		
		
		sqlStmt.append("SELECT DISTINCT " +
					   "       vcf.vcf_id, " +
					   "       vcg.vcg_id, " +
					   "       client.account_name, " +
					   "       vcf.description config_desc, " +
					   "       ot.description order_type_desc, " +
					   "	   vcf.obsolete_ind ");
		
		sqlStmt.append("  FROM vehicle_configurations vcf, vehicle_config_groupings vcg, vehicle_config_upfit_quotes vuq, upfitter_quotes ufq, external_accounts client, external_accounts vendor, vehicle_config_models vcm, models mdl, make_model_ranges mrg, order_types ot" +
					   " WHERE vcf.vcf_id = vcg.vcf_vcf_id (+)" +
					   "   AND vcg.vcg_id = vuq.vcg_vcg_id (+)" +
					   "   AND vuq.ufq_ufq_id = ufq.ufq_id(+)" +
					   "   AND client.c_id = vcf.ea_c_id" +
					   "   AND client.account_type = vcf.ea_account_type" +
					   "   AND client.account_code = vcf.ea_account_code" +
					   "   AND vendor.c_id (+) = ufq.ea_c_id" +
					   "   AND vendor.account_type (+) = ufq.ea_account_type" +
					   "   AND vendor.account_code (+)= ufq.ea_account_code" +
					   "   AND vcf.vcf_id = vcm.vcf_vcf_id (+)" +
					   "   AND mdl.mdl_id (+) = vcm.mdl_mdl_id" +
					   "   AND mrg.mrg_id (+) = vcm.mrg_mrg_id" +
					   "   AND vcf.order_type_code = ot.code");

		if (!MALUtilities.isEmpty(searchCriteria.getClientAccountCode())) {
			sqlStmt.append("  AND client.c_id = :v_c_id");
			sqlStmt.append("  AND client.account_type = :v_account_type");
			sqlStmt.append("  AND client.account_code = :v_account_code");
			
			parameterMap.put("v_c_id", MALUtilities.isEmpty(searchCriteria.getClientCid()) ? null : searchCriteria.getClientCid());
			parameterMap.put("v_account_type", MALUtilities.isEmpty(searchCriteria.getClientAccountType()) ? null : searchCriteria.getClientAccountType());
			parameterMap.put("v_account_code", MALUtilities.isEmpty(searchCriteria.getClientAccountCode()) ? null : searchCriteria.getClientAccountCode());
		}
		
		if (!MALUtilities.isEmpty(searchCriteria.getConfigDescription())) {
			sqlStmt.append("  AND LOWER(vcf.description) LIKE LOWER(:v_config_desc)");
			parameterMap.put("v_config_desc", MALUtilities.isEmpty(searchCriteria.getConfigDescription()) ? null : "%" + searchCriteria.getConfigDescription() + "%");
		}
		
		if (!MALUtilities.isEmpty(searchCriteria.getMfgCode())) {
			sqlStmt.append("  AND LOWER(vcm.mfg_code) = LOWER(:v_mfg_code)");
			parameterMap.put("v_mfg_code", MALUtilities.isEmpty(searchCriteria.getMfgCode()) ? null : searchCriteria.getMfgCode());
		}
		
		if (!MALUtilities.isEmpty(searchCriteria.getMake())) {
			sqlStmt.append("  AND vcm.make = :v_make");
			parameterMap.put("v_make", MALUtilities.isEmpty(searchCriteria.getMake()) ? null : searchCriteria.getMake());
		}
		
		if (!MALUtilities.isEmpty(searchCriteria.getModel())) {
			sqlStmt.append("  AND mrg.make_model_desc = :v_model");
			parameterMap.put("v_model", MALUtilities.isEmpty(searchCriteria.getModel()) ? null : searchCriteria.getModel());
		}
		
		if (!MALUtilities.isEmpty(searchCriteria.getYear())) {
			sqlStmt.append("  AND vcm.year = :v_year");
			parameterMap.put("v_year", MALUtilities.isEmpty(searchCriteria.getYear()) ? null : searchCriteria.getYear());
		}
		
		if (!MALUtilities.isEmpty(searchCriteria.getTrim())) {
			sqlStmt.append("  AND LOWER(mdl.model_desc) LIKE LOWER(:v_trim)");
			parameterMap.put("v_trim", MALUtilities.isEmpty(searchCriteria.getTrim()) ? null : "%" + searchCriteria.getTrim() + "%");
		}
		
		if (!MALUtilities.isEmpty(searchCriteria.getVendorAccountCode())) {
			sqlStmt.append("  AND vendor.c_id = :v_vendor_c_id");
			sqlStmt.append("  AND vendor.account_type = :v_vendor_acc_type");
			sqlStmt.append("  AND vendor.account_code = :v_vendor_acc_code");
			
			parameterMap.put("v_vendor_c_id", MALUtilities.isEmpty(searchCriteria.getVendorCid()) ? null : searchCriteria.getVendorCid());
			parameterMap.put("v_vendor_acc_type", MALUtilities.isEmpty(searchCriteria.getVendorAccountType()) ? null : searchCriteria.getVendorAccountType());
			parameterMap.put("v_vendor_acc_code", MALUtilities.isEmpty(searchCriteria.getVendorAccountCode()) ? null : searchCriteria.getVendorAccountCode());
		}
		
		if (!MALUtilities.isEmpty(searchCriteria.getVehConfingObsoleteInd())) {
			sqlStmt.append("  AND LOWER(vcf.obsolete_ind) = LOWER(:v_obsolete_ind) ");
			parameterMap.put("v_obsolete_ind", searchCriteria.getVehConfingObsoleteInd());
		}
		
		if(!isCountQuery) {
			if(!MALUtilities.isEmpty(sort)) {
				sqlStmt.append(" ORDER BY ");
				for(Iterator<Order> configIterator = sort.iterator(); configIterator.hasNext();) {
					Order order = configIterator.next();
					
					if(DataConstants.VEHICLE_CONFIG_SEARCH_SORT_CLIENT.equals(order.getProperty())) {
						sqlStmt.append(" client.account_name " + order.getDirection());
					}
					
					if(DataConstants.VEHICLE_CONFIG_SEARCH_SORT_DESC.equals(order.getProperty())) {
						sqlStmt.append(" vcf.description " + order.getDirection());
					}
					
					if(configIterator.hasNext()) {
						sqlStmt.append(", ");
					}
				}
			} else {
				sqlStmt.append(" ORDER BY client.account_name ASC, vcf.description ASC");
			}
		}
		
		logger.debug("Final Query: " + sqlStmt.toString());
		query = entityManager.createNativeQuery(sqlStmt.toString());
		
		for(String paramName : parameterMap.keySet()) {
			query.setParameter(paramName, parameterMap.get(paramName));
		}
		
		return query;
	}
	
	@SuppressWarnings("unchecked")
	private List<VehicleConfigurationSearchResultVO> populateVehicleConfigurationSearchResultVO (Query query, Pageable pageable, Sort sort) {
		List<VehicleConfigurationSearchResultVO>  vehicleConfigurationSearchResultVOList = new ArrayList<VehicleConfigurationSearchResultVO>();;
		
		if(pageable != null) {
			query.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
			query.setMaxResults(pageable.getPageSize());
		}
		
		List<Object[]> objectList  = (List<Object[]>) query.getResultList();
		
		String vcfIdList = new String();
		
		if (objectList != null && objectList.size() > 0) {	
			
			for (Iterator<Object[]> iterator = objectList.iterator(); iterator.hasNext();) {
				Object[] object = iterator.next();				
				VehicleConfigurationSearchResultVO vehicleConfigurationSearchResultVO = new VehicleConfigurationSearchResultVO();
				int i = 0;
				
				vehicleConfigurationSearchResultVO.setVehicleConfigId(object[i]				!= null ? ((BigDecimal) object[i]).longValue() 	: null);
				vehicleConfigurationSearchResultVO.setVehicleConfigGroupingId(object[i+=1]	!= null ? ((BigDecimal) object[i]).longValue() 	: null);
				vehicleConfigurationSearchResultVO.setClientName(object[i+=1]				!= null ? (String) 		object[i]			 	: null);
				vehicleConfigurationSearchResultVO.setConfigDescription(object[i+=1]		!= null ? (String) 		object[i]			 	: null);
				vehicleConfigurationSearchResultVO.setLocateFlag(object[i+=1]				!= null ? (String) 		object[i]			 	: null);
				vehicleConfigurationSearchResultVO.setVehConfingObsoleteInd(object[i+=1]	!= null ? (String) 		object[i]			 	: null);
				
				vehicleConfigurationSearchResultVO.setVendorName(vehicleConfigGroupingDAO.getVendorsByConfigGroupingId(vehicleConfigurationSearchResultVO.getVehicleConfigGroupingId()));
				
				vehicleConfigurationSearchResultVOList.add(vehicleConfigurationSearchResultVO);
				if (!MALUtilities.isEmpty(vcfIdList)) {
					vcfIdList = vcfIdList.concat(", ");
				}
				vcfIdList = vcfIdList.concat(vehicleConfigurationSearchResultVO.getVehicleConfigId().toString());
			}
		}
		vehicleConfigurationSearchResultVOList = populateVehicleConfigModel(vehicleConfigurationSearchResultVOList, vcfIdList);
		return vehicleConfigurationSearchResultVOList;
	}
	
	private List<VehicleConfigurationSearchResultVO> populateVehicleConfigModel (List<VehicleConfigurationSearchResultVO> searchResultList, String vcfIdList) {
	
		List<VehicleConfigModelVO> configModels = getVehicleConfigModelQuery(vcfIdList);
		Map<Long, List<VehicleConfigModelVO>> vehicleConfigMap = new HashMap<Long, List<VehicleConfigModelVO>>();
		
		Long vehicleConfigId = null;
		
		for (VehicleConfigModelVO configModel : configModels) {
			vehicleConfigId = configModel.getVcfId();
			if (vehicleConfigMap.containsKey(vehicleConfigId)) {
				vehicleConfigMap.get(vehicleConfigId).add(configModel);
			} else {
				List<VehicleConfigModelVO> configModelVOList = new ArrayList<VehicleConfigModelVO>();
				configModelVOList.add(configModel);
				vehicleConfigMap.put(vehicleConfigId, configModelVOList);
			}
		}
		
		Iterator<VehicleConfigurationSearchResultVO> it = searchResultList.iterator();
		while (it.hasNext()) {
			VehicleConfigurationSearchResultVO searchResultVO = (VehicleConfigurationSearchResultVO) it.next();
			List<VehicleConfigModelVO> vcmVo = vehicleConfigMap.get(searchResultVO.getVehicleConfigId());
			searchResultVO.setVehicleConfigModelVOs(vcmVo);

		}

		return searchResultList;
	}
	
	@SuppressWarnings("unchecked")
	private int  populateVehicleConfigurationSearchCount (Query query) {
		int count  = 0;	
		List<Object[]> objectList  = (List<Object[]>) query.getResultList();
		if (objectList != null) {	
			count = objectList.size();
		}
	
		return count;
		
	}
	
}
