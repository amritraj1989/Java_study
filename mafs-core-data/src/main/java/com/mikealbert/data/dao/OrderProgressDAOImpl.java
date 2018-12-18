package com.mikealbert.data.dao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;

import com.mikealbert.data.DataConstants;
import com.mikealbert.data.DataUtilities;
import com.mikealbert.data.entity.Model;
import com.mikealbert.data.enumeration.VehicleOrderType;
import com.mikealbert.data.vo.OrderProgressSearchCriteriaVO;
import com.mikealbert.data.vo.OrderProgressSearchResultVO;
import com.mikealbert.util.MALUtilities;

public class OrderProgressDAOImpl extends GenericDAOImpl<Model, Long>implements OrderProgressDAOCustom {
	private static final long serialVersionUID = -5126660263724406923L;

	public List<OrderProgressSearchResultVO> findUnits(OrderProgressSearchCriteriaVO criteria, PageRequest pageable, Sort sort) {
		List<OrderProgressSearchResultVO> orderProgressSearchResultVOs = null;
		Query query = null;

		query = generateUnitsQuery(criteria, sort, false);

		if(pageable != null) {
			query.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
			query.setMaxResults(pageable.getPageSize());
		}

		@SuppressWarnings("unchecked")
		List<Object[]> resultList = (List<Object[]>) query.getResultList();
		if(resultList != null) {
			orderProgressSearchResultVOs = new ArrayList<OrderProgressSearchResultVO>();

			OrderProgressSearchResultVO unitVO = null;	
			for(Object[] record : resultList) {
				int i = 0;

				unitVO = new OrderProgressSearchResultVO();
				unitVO.setQmdId(record[i] != null ? ((BigDecimal) record[i]).longValue() : null );
				unitVO.setOrderType((String) record[i+=1]);
				unitVO.setUnitNo((String) record[i+=1]);
				unitVO.setAccountName(record[i += 1] != null ? (String) record[i] : "");
				unitVO.setAccountCode(record[i += 1] != null ? (String) record[i] : "");
				unitVO.setDocId(((BigDecimal) record[i+=1]).longValue());
				unitVO.setMdlId(((BigDecimal) record[i+=1]).longValue());
				unitVO.setMakeId(((BigDecimal) record[i+=1]).longValue());
				unitVO.setTrim((String) record[i+=1]);
				unitVO.setMake((String) record[i+=1]);
				unitVO.setYear((String) record[i+=1]);
				unitVO.setModel((String) record[i+=1]);
				unitVO.setMfgCode((String) record[i+=1]);
				unitVO.setFmsId(((BigDecimal) record[i+=1]).longValue());
				unitVO.setCss(record[i += 1] != null ? (String) record[i] : "");
				unitVO.setCurrentETADate(record[i+=1] != null ? (Date) record[i] : null);
				
				if(MALUtilities.isNotEmptyString(criteria.getFactoryEquipment())) {
					List<String> equipments = getMatchingEquipments(unitVO.getQmdId(), unitVO.getDocId(), criteria.getFactoryEquipment());
					unitVO.setFactoryEquipments(equipments);
				}
				
				orderProgressSearchResultVOs.add(unitVO);
			}
		}

		return orderProgressSearchResultVOs;
	}

	public int findUnitsCount(OrderProgressSearchCriteriaVO searchCriteria) {
		int count = 0;
		Query query = null;
		query = generateUnitsQuery(searchCriteria, null, true);
		count = ((BigDecimal) query.getSingleResult()).intValue();
		return count;
	}

	private Query generateUnitsQuery(OrderProgressSearchCriteriaVO searchCriteria, Sort sort, boolean isCountQuery) {
		Query query = null;
		StringBuilder sqlStmt = new StringBuilder("");
		if(isCountQuery) {
			sqlStmt.append("SELECT SUM(x) FROM ( SELECT count(1) x ");
		} else {
			sqlStmt.append("select qmd_id, order_type, unit_no, account_name, account_code, doc_id, mdl_id, mak_id, model_desc, make_desc, model_mark_year_desc, make_model_desc, standard_code, fms_id, css, current_eta_date ");
			sqlStmt.append(" from ( ");
			sqlStmt.append("SELECT qmd.qmd_id, qmd.order_type, qmd.unit_no, ea.account_name, ea.account_code, d.doc_id, mdl.mdl_id, mk.mak_id, mdl.model_desc, mk.make_desc, mmy.model_mark_year_desc, mrg.make_model_desc, mdl.standard_code, ");
			sqlStmt.append("(SELECT fms_id FROM fleet_masters fms WHERE fms.unit_no = qmd.unit_no) as fms_id, ");
			sqlStmt.append("(SELECT employee_no from (SELECT eac.* FROM ext_acc_consultants eac where eac.role_type = 'CSR' order by decode(eac.default_ind, 'Y', 1, 2), eac.consul_id DESC) eac1 WHERE rownum = 1 and eac1.ea_account_code = quo.account_code) as css, ");
			sqlStmt.append("(SELECT action_date FROM supplier_progress_history WHERE sph_id = (SELECT max(sph_id) FROM supplier_progress_history sph1 WHERE sph1.doc_id = d.doc_id AND progress_type = '14_ETA')) as current_eta_date ");
		}

		sqlStmt.append("FROM quotations quo ");
		sqlStmt.append("INNER JOIN external_accounts ea ON ea.account_code = quo.account_code ");
		sqlStmt.append("INNER JOIN quotation_models qmd ON qmd.quo_quo_id = quo.quo_id ");
		sqlStmt.append("INNER JOIN models mdl ON qmd.mdl_mdl_id = mdl.mdl_id ");
		sqlStmt.append("INNER JOIN makes mk ON mdl.mak_mak_id = mk.mak_id ");
		sqlStmt.append("INNER JOIN model_mark_years mmy ON mdl.mmy_mmy_id = mmy.mmy_id ");
		sqlStmt.append("INNER JOIN make_model_ranges mrg ON mdl.mrg_mrg_id = mrg.mrg_id ");
		sqlStmt.append("INNER JOIN model_types mtp ON mdl.mtp_mtp_id = mtp.mtp_id ");
		sqlStmt.append("INNER JOIN doc d ON qmd.qmd_id = d.generic_ext_id ");
		sqlStmt.append("WHERE qmd.quote_status = '3' ");
		sqlStmt.append("AND d.doc_status = 'R' ");
		sqlStmt.append("AND d.doc_type = 'PORDER' ");
		sqlStmt.append("AND d.source_code = 'FLQUOTE' ");
		sqlStmt.append("AND nvl(d.order_type, 'X') != 'T' ");
		sqlStmt.append("AND NOT EXISTS (SELECT 1 FROM supplier_progress_history sph1 WHERE sph1.doc_id = d.doc_id AND sph1.progress_type IN('09_INT_DLR', '11_INT_DLR', '12_INT_DLR', '13_INT_DLR', '15_RECEIVD')) ");
		sqlStmt.append("AND quo.account_code like :accountCode ");
		sqlStmt.append("AND qmd.order_type like :orderType ");
		sqlStmt.append("AND qmd.unit_no like :unitNo ");
		sqlStmt.append("AND lower(mdl.standard_code) LIKE lower(:mfgCode) ");
		sqlStmt.append("AND mmy.model_mark_year_desc LIKE :year ");
        sqlStmt.append("AND lower(mk.make_desc) LIKE lower(:make) ");
        sqlStmt.append("AND lower(mrg.make_model_desc) LIKE lower(:model) ");
        sqlStmt.append("AND lower(mrg.make_model_range_code) LIKE lower(:modelCode) ");                
        sqlStmt.append("AND lower(mdl.model_desc) LIKE lower(:trim) ");
        
        if(MALUtilities.isNotEmptyString(searchCriteria.getFactoryEquipment())) {
	    	//Standard Equipments
	    	sqlStmt.append("AND EXISTS ((SELECT sac.description description FROM quotation_standard_accessories qsa, standard_accessories sa, standard_accessory_codes sac ");
	    	sqlStmt.append("WHERE qsa.sac_sac_id = sa.sac_id ");
	    	sqlStmt.append("AND sa.sacc_sacc_id = sac.sacc_id ");
	    	sqlStmt.append("AND qsa.qmd_qmd_id = qmd.qmd_id ");
	    	sqlStmt.append("AND (lower(sac.description) LIKE lower(:term) OR lower(sac.accessory_code) LIKE lower(:term) or UPPER(sac.new_accessory_code) LIKE UPPER(:term))) ");
	    	sqlStmt.append("UNION ");
	    	//Optional Factory Equipments
	    	sqlStmt.append("(SELECT assc.description description FROM quotation_model_accessories qma, optional_accessories oac, accessory_codes assc ");
	    	sqlStmt.append("WHERE qma.oac_oac_id = oac.oac_id ");
	    	sqlStmt.append("AND oac.assc_assc_id = assc.assc_id ");
	    	sqlStmt.append("AND qma.qmd_qmd_id = qmd.qmd_id ");
	    	sqlStmt.append("AND (lower(assc.description) LIKE lower(:term) OR lower(assc.accessory_code) LIKE lower(:term) or UPPER(assc.new_accessory_code) LIKE UPPER(:term)))) ");
        }
        if(VehicleOrderType.FACTORY.getCode().equals(searchCriteria.getOrderType()) && MALUtilities.isEmpty(searchCriteria.getClient())){ // Include stock orders when order type is factory. OTD-4986
	        sqlStmt.append(" UNION ");
	        if(isCountQuery) {
				sqlStmt.append(" SELECT count(1) x ");
			} else {
		        sqlStmt.append(" SELECT null as qmd_id, null as order_type, fms.unit_no, null, null, d.doc_id, mdl.mdl_id, mk.mak_id, mdl.model_desc, mk.make_desc, mmy.model_mark_year_desc, mrg.make_model_desc, mdl.standard_code, fms_id, null as csr, ");
		        sqlStmt.append(" (SELECT action_date FROM supplier_progress_history WHERE sph_id = (SELECT max(sph_id) FROM supplier_progress_history sph1 WHERE sph1.doc_id = d.doc_id AND progress_type = '14_ETA')) as current_eta_date ");
			}
	        sqlStmt.append(" FROM doc d, dist di, docl dl, fleet_masters fms, models mdl,makes mk, model_mark_years mmy, make_model_ranges mrg ");
	        sqlStmt.append(" WHERE d.doc_status = 'R' ");
	        sqlStmt.append(" AND d.doc_type = 'PORDER' ");
	        sqlStmt.append(" AND d.source_code = 'FLORDER' ");
	        sqlStmt.append(" AND d.veh_designation_code = 'STOCK' ");
	        sqlStmt.append(" AND nvl(d.order_type, 'M') = 'M' ");
	        sqlStmt.append(" AND d.generic_ext_id is null ");
	        sqlStmt.append(" AND d.total_doc_price <> 0 ");
	        sqlStmt.append(" AND d.update_control_code = 'INVENTORY' ");
	        sqlStmt.append(" AND NOT EXISTS (SELECT 1 FROM supplier_progress_history sph1 WHERE sph1.doc_id = d.doc_id AND sph1.progress_type IN('09_INT_DLR', '11_INT_DLR', '12_INT_DLR', '13_INT_DLR', '15_RECEIVD')) ");
	        sqlStmt.append(" AND di.cdb_code_8 = 'PORDER' ");
	        sqlStmt.append(" AND di.docl_doc_id = dl.doc_id ");
	        sqlStmt.append(" AND di.docl_line_id = dl.line_id ");
	        sqlStmt.append(" AND dl.user_def4 = 'MODEL' ");
	        sqlStmt.append(" AND dl.doc_id = d.doc_id ");
	        sqlStmt.append(" AND di.cdb_code_1 = to_char(fms.fms_id) ");
	        sqlStmt.append(" AND fms.confirmed_delivery_date IS NULL ");
	        sqlStmt.append(" AND mdl.mdl_id = fms.mdl_mdl_id ");
	        sqlStmt.append(" AND mdl.mak_mak_id = mk.mak_id ");
	        sqlStmt.append(" AND mdl.mmy_mmy_id = mmy.mmy_id ");
	        sqlStmt.append(" AND mdl.mrg_mrg_id = mrg.mrg_id ");
	        sqlStmt.append(" AND fms.unit_no like :unitNo ");
			sqlStmt.append(" AND lower(mdl.standard_code) LIKE lower(:mfgCode) ");
			sqlStmt.append(" AND mmy.model_mark_year_desc LIKE :year ");
	        sqlStmt.append(" AND lower(mk.make_desc) LIKE lower(:make) ");
	        sqlStmt.append(" AND lower(mrg.make_model_desc) LIKE lower(:model) ");
	        sqlStmt.append(" AND lower(mrg.make_model_range_code) LIKE lower(:modelCode) ");                
	        sqlStmt.append(" AND lower(mdl.model_desc) LIKE lower(:trim) ");
	        
	        if(MALUtilities.isNotEmptyString(searchCriteria.getFactoryEquipment())) {
		    	//Standard Equipments
		    	sqlStmt.append("AND EXISTS ((select sac.description from standard_accessories sa, standard_accessory_codes sac ");
		    	sqlStmt.append("WHERE sa.mdl_mdl_id = (SELECT dl2.generic_ext_id FROM docl dl2 WHERE dl2.doc_id= d.doc_id AND dl2.user_def4 = 'MODEL') ");
		    	sqlStmt.append("AND sa.sacc_sacc_id = sac.sacc_id ");
		    	sqlStmt.append("AND (lower(sac.description) LIKE lower(:term) OR lower(sac.accessory_code) LIKE lower(:term) or UPPER(sac.new_accessory_code) LIKE UPPER(:term))) ");
		    	sqlStmt.append("UNION ");
		    	//Optional Factory Equipments
		    	sqlStmt.append("(SELECT dl2.line_description FROM docl dl2 WHERE dl2.doc_id = d.doc_id AND dl2.user_def4 = 'FACTORY' AND dl2.line_status='R' ");
		    	sqlStmt.append("AND lower(dl2.line_description) LIKE lower(:term) )) ");
	        }
		}
        sqlStmt.append(" )");

		// Defaults order by unless otherwise specified by the passed in sort object
		if(!isCountQuery) {
			if(!MALUtilities.isEmpty(sort)) {
				sqlStmt.append(" ORDER BY ");
				for(Iterator<Order> orderIterator = sort.iterator(); orderIterator.hasNext();) {
					Order order = orderIterator.next();
					
					if(DataConstants.SEARCH_SORT_FIELD_UNIT_NO.equals(order.getProperty())) {
						sqlStmt.append(" unit_no " + order.getDirection());
					}
					if(DataConstants.SEARCH_SORT_FIELD_ACCOUNT.equals(order.getProperty())) {
						sqlStmt.append(" account_name " + order.getDirection());
					}
					if(DataConstants.SEARCH_SORT_FIELD_CSS.equals(order.getProperty())) {
						sqlStmt.append(" css " + order.getDirection());
					}
					if(DataConstants.MODEL_SEARCH_SORT_YEAR.equals(order.getProperty())) {
						sqlStmt.append(" model_mark_year_desc " + order.getDirection());
					}
					if(DataConstants.MODEL_SEARCH_SORT_MAKE.equals(order.getProperty())) {
						sqlStmt.append(" make_desc " + order.getDirection());
					}
					if(DataConstants.MODEL_SEARCH_SORT_MODEL.equals(order.getProperty())) {
						sqlStmt.append(" make_model_desc " + order.getDirection());
					}
					if(DataConstants.MODEL_SEARCH_SORT_TRIM.equals(order.getProperty())) {
						sqlStmt.append(" model_desc " + order.getDirection());
					}
					if(DataConstants.SEARCH_SORT_FIELD_ETA_DATE.equals(order.getProperty())) {
						sqlStmt.append(" current_eta_date " + order.getDirection());
					}
					
					if(orderIterator.hasNext()) {
						sqlStmt.append(", ");
					}
				}
			} else {
				sqlStmt.append("ORDER BY unit_no ASC ");
			}
		}

		Map<String, Object> parameterMap = new HashMap<String, Object>();
		parameterMap.put("accountCode", MALUtilities.isEmpty(searchCriteria.getClient()) ? '%' : searchCriteria.getClient());
		parameterMap.put("orderType", MALUtilities.isEmpty(searchCriteria.getOrderType()) ? '%' : searchCriteria.getOrderType());
		parameterMap.put("unitNo", DataUtilities.appendWildCardToRight(DataUtilities.prependWildCardToLeft(searchCriteria.getUnitNo())));
		parameterMap.put("mfgCode", DataUtilities.appendWildCardToRight(DataUtilities.prependWildCardToLeft(searchCriteria.getMfgCode())));
		parameterMap.put("year", MALUtilities.isEmpty(searchCriteria.getYear()) ? '%' : searchCriteria.getYear());
		parameterMap.put("make", DataUtilities.appendWildCardToRight(searchCriteria.getMake()));
		parameterMap.put("model", DataUtilities.appendWildCardToRight(searchCriteria.getModel()));
		parameterMap.put("modelCode", MALUtilities.isEmpty(searchCriteria.getModelCode()) ? '%' : searchCriteria.getModelCode());
		parameterMap.put("trim", DataUtilities.appendWildCardToRight(DataUtilities.prependWildCardToLeft(searchCriteria.getTrim())));
		
		if(MALUtilities.isNotEmptyString(searchCriteria.getFactoryEquipment())) {
			parameterMap.put("term", DataUtilities.appendWildCardToRight(DataUtilities.prependWildCardToLeft(searchCriteria.getFactoryEquipment())));
        }
		

		query = entityManager.createNativeQuery(sqlStmt.toString());
		for(String paramName : parameterMap.keySet()) {
			query.setParameter(paramName, parameterMap.get(paramName));
		}

		return query;
	}
	
	@SuppressWarnings("unchecked")
	private List<String> getMatchingEquipments(Long qmdId, Long docId, String term) {
		List<String> equipments = new ArrayList<String>();
		StringBuilder sqlStmt = new StringBuilder("");
		if(!MALUtilities.isEmpty(qmdId)){
			//Standard Equipments
			sqlStmt.append("(SELECT sac.description description FROM quotation_standard_accessories qsa, standard_accessories sa, standard_accessory_codes sac  ");
			sqlStmt.append("WHERE qsa.sac_sac_id = sa.sac_id ");
			sqlStmt.append("AND sa.sacc_sacc_id = sac.sacc_id ");
			sqlStmt.append("AND qsa.qmd_qmd_id = :qmdId ");
			sqlStmt.append("AND (lower(sac.description) LIKE lower(:term) OR lower(sac.accessory_code) LIKE lower(:term) or UPPER(sac.new_accessory_code) LIKE UPPER(:term))) ");
			sqlStmt.append("UNION ");
			//Optional Factory Equipments
			sqlStmt.append("(SELECT assc.description description FROM quotation_model_accessories qma, optional_accessories oac, accessory_codes assc ");
			sqlStmt.append("WHERE qma.oac_oac_id = oac.oac_id ");
			sqlStmt.append("AND oac.assc_assc_id = assc.assc_id ");
			sqlStmt.append("AND qma.qmd_qmd_id = :qmdId ");
			sqlStmt.append("AND (lower(assc.description) LIKE lower(:term) OR lower(assc.accessory_code) LIKE lower(:term) or UPPER(assc.new_accessory_code) LIKE UPPER(:term))) ");
			sqlStmt.append("ORDER BY description ASC ");
		}else{
			//Standard Equipments
			sqlStmt.append("(select sac.description from standard_accessories sa, standard_accessory_codes sac ");
	    	sqlStmt.append("WHERE sa.mdl_mdl_id = (SELECT dl2.generic_ext_id FROM docl dl2 WHERE dl2.doc_id= :docId AND dl2.user_def4 = 'MODEL') ");
	    	sqlStmt.append("AND sa.sacc_sacc_id = sac.sacc_id ");
	    	sqlStmt.append("AND (lower(sac.description) LIKE lower(:term) OR lower(sac.accessory_code) LIKE lower(:term) or UPPER(sac.new_accessory_code) LIKE UPPER(:term))) ");
	    	sqlStmt.append("UNION ");
	    	//Optional Factory Equipments
	    	sqlStmt.append("(SELECT dl2.line_description FROM docl dl2 WHERE dl2.doc_id = :docId AND dl2.user_def4 = 'FACTORY' AND dl2.line_status='R' ");
	    	sqlStmt.append("AND lower(dl2.line_description) LIKE lower(:term) ) ");
		}
		Query query = entityManager.createNativeQuery(sqlStmt.toString());
		if(!MALUtilities.isEmpty(qmdId)){
			query.setParameter("qmdId", qmdId);
		}else{
			query.setParameter("docId", docId);
		}
		query.setParameter("term", DataUtilities.appendWildCardToRight(DataUtilities.prependWildCardToLeft(term)));
		equipments = (List<String>) query.getResultList();
		
		return equipments;
	}
	
	
	public Long getManufacturerLeadTime(Long fmsId) {
		Long leadTime = 0L;
		String stmt = "SELECT standard_delivery_lead_time FROM models WHERE mdl_id = (SELECT mdl_mdl_id FROM fleet_masters WHERE fms_id = :fmsId)";
		
		Query query = entityManager.createNativeQuery(stmt);
		query.setParameter("fmsId", fmsId);

		BigDecimal leadTimeResult = (BigDecimal) query.getSingleResult();
		if(leadTimeResult != null){
			leadTime = ((BigDecimal) leadTimeResult).longValue();
		}
		
		return leadTime;
	}
	
	public Long getPDILeadTime(Long fmsId) {
		Long leadTime = 0L;
		String stmt = "SELECT pdi_lead_time FROM manufacturers WHERE mfg_code = (SELECT mfg_code FROM recall_mfg_makes WHERE mak_mak_id = "
														+ "(SELECT mak_mak_id FROM models WHERE mdl_id = (SELECT mdl_mdl_id FROM fleet_masters WHERE fms_id = :fmsId)))";
		
		Query query = entityManager.createNativeQuery(stmt);
		query.setParameter("fmsId", fmsId);

		BigDecimal leadTimeResult = (BigDecimal) query.getSingleResult();
		if(leadTimeResult != null){
			leadTime = ((BigDecimal) leadTimeResult).longValue();
		}
		
		return leadTime;
	}
	
	public Long getUnitLeadTimeByDocId(Long docId) {
		Long leadTime = 0L;
		String stmt;
		
		stmt = "SELECT po_mgr.get_unit_lead_time(d.doc_id) lead_time "
				+" FROM doc d "
				+" WHERE d.doc_id = :docId ";
		
		Query query = entityManager.createNativeQuery(stmt);
		query.setParameter("docId", docId);

		BigDecimal leadTimeResult = (BigDecimal) query.getSingleResult();
		if(leadTimeResult != null){
			leadTime = ((BigDecimal) leadTimeResult).longValue();
		}
		
		return leadTime;
	}
	
	public Long getPurchaseOrderLeadTimeByDocId(Long docId) {
		Long leadTime = 0L;
		String stmt;
		
		stmt = "SELECT po_mgr.get_po_lead_time(d.doc_id) lead_time "
				+" FROM doc d "
				+" WHERE d.doc_id = :docId ";
		
		Query query = entityManager.createNativeQuery(stmt);
		query.setParameter("docId", docId);

		BigDecimal leadTimeResult = (BigDecimal) query.getSingleResult();
		if(leadTimeResult != null){
			leadTime = ((BigDecimal) leadTimeResult).longValue();
		}
		
		return leadTime;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<String> getPossibleStandardAccessoriesByDocId(Long docId){
		List<String> accessories = new ArrayList<String>();
		
		StringBuilder sqlStmt = new StringBuilder("");
		//Standard Equipments
		sqlStmt.append("SELECT sac.description FROM standard_accessories sa, standard_accessory_codes sac ");
	    sqlStmt.append("WHERE sa.mdl_mdl_id = (SELECT dl2.generic_ext_id FROM docl dl2 WHERE dl2.doc_id= :docId AND dl2.user_def4 = 'MODEL') ");
	    sqlStmt.append("AND sa.sacc_sacc_id = sac.sacc_id ");
	    
		Query query = entityManager.createNativeQuery(sqlStmt.toString());
		query.setParameter("docId", docId);
		
		accessories = (List<String>) query.getResultList();
		return accessories;
	}
	
}
