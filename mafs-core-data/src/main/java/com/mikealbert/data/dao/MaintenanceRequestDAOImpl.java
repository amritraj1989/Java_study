package com.mikealbert.data.dao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;

import com.mikealbert.data.DataConstants;
import com.mikealbert.data.entity.MaintenanceRequest;
import com.mikealbert.data.vo.MaintenanceServiceHistoryVO;

public class MaintenanceRequestDAOImpl extends GenericDAOImpl<MaintenanceRequest, Long> implements MaintenanceRequestDAOCustom {

	@Resource
	private MaintenanceRequestDAO maintenanceRequestDAO;
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * Retrieve Service History based on a list of fmsIds
	 */
	@SuppressWarnings("unchecked")
	public List<MaintenanceServiceHistoryVO> getMaintenanceServiceHistory(List<Long> fmsIds, Pageable pageable, Sort sort, String providerFilter, String maintCategoryFilter, String maintCodeDescFilter, List<String> excludeMaintCodesFromPOTotal){
		List<MaintenanceServiceHistoryVO> maintenanceServiceHistoryList	= new ArrayList<MaintenanceServiceHistoryVO>();
		
		if(!fmsIds.isEmpty()) {
			Query query = generateMaintenanceServiceHistoryQuery(false, fmsIds, sort, providerFilter, maintCategoryFilter, maintCodeDescFilter, excludeMaintCodesFromPOTotal);

			if(pageable != null){
				query.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
				query.setMaxResults(pageable.getPageSize());
			}

			List<Object[]>resultList = (List<Object[]>)query.getResultList();
			if(resultList != null){
				for(Object[] record : resultList){
					int i = 0;

					MaintenanceServiceHistoryVO maintenanceServiceHistoryVO = new MaintenanceServiceHistoryVO();				
					maintenanceServiceHistoryVO.setMrqId(((BigDecimal)record[i]).longValue());
					maintenanceServiceHistoryVO.setPoNumber((String)record[i+=1]);				
					maintenanceServiceHistoryVO.setActualStartDate((Date)record[i+=1]);
					maintenanceServiceHistoryVO.setOdo(((BigDecimal)record[i+=1]).intValue());
					maintenanceServiceHistoryVO.setSupId((BigDecimal)record[i+=1]);
					maintenanceServiceHistoryVO.setServiceProviderNumber((String)record[i+=1]);
					maintenanceServiceHistoryVO.setServiceProviderName((String)record[i+=1]);
					maintenanceServiceHistoryVO.setServiceProviderTelephone((String)record[i+=1]);
					maintenanceServiceHistoryVO.setMaintRequestStatus(((String)record[i+=1]));	
					maintenanceServiceHistoryVO.setTaskTotalCost(((BigDecimal)record[i+=1]).setScale(2, BigDecimal.ROUND_HALF_UP));
					maintenanceServiceHistoryVO.setTaskTotalCostRechN(((BigDecimal)record[i+=1]).setScale(2, BigDecimal.ROUND_HALF_UP));
					maintenanceServiceHistoryVO.setActualCustCharge(record[i+=1] != null ? ((BigDecimal)record[i]).setScale(2, BigDecimal.ROUND_HALF_UP) : null);// Added for Bug 16387
					maintenanceServiceHistoryVO.setRechargeTotal(((BigDecimal)record[i+=1]).setScale(2, BigDecimal.ROUND_HALF_UP));
					maintenanceServiceHistoryVO.setActualInvoice(record[i+=1] != null ? ((BigDecimal)record[i]).setScale(2, BigDecimal.ROUND_HALF_UP) : null); // Added for Bug 16387
					maintenanceServiceHistoryVO.setDoclTotalCost(record[i+=1] != null ? ((BigDecimal)record[i]).setScale(2, BigDecimal.ROUND_HALF_UP) : null);
					maintenanceServiceHistoryVO.setConcatCategoryDescriptions((record[i+=1] != null ? ((String)record[i]) : null));

					maintenanceServiceHistoryList.add(maintenanceServiceHistoryVO);
				}

			}
		}
							
		return maintenanceServiceHistoryList;
	}
	
	private Query generateMaintenanceServiceHistoryQuery(boolean countQuery, List<Long> fmsIds, Sort sort, String providerFilter, String maintCategoryFilter, String maintCodeDescFilter, List<String> excludeMaintCodesFromPOTotal) {
		Query query =  null;
		StringBuilder sqlStmt = new StringBuilder("");
		
		//If countQuery is true, only count the number of records
		if(countQuery == true){
			sqlStmt.append("SELECT count(*)");
		}else{
			sqlStmt.append("SELECT mrq_id,job_no,TO_DATE (actual_start_date),mr.current_odo,mr.sup_sup_id,s.supplier_code,s.supplier_name,s.telephone_number,"
					+"		         (SELECT description"
					+"		            FROM maint_request_status_codes mrsc"
					+"		           WHERE mrsc.maint_request_status = mr.maint_request_status) Maint_Req_Status,"
					+"		         (SELECT NVL(SUM (task_total_cost), 0)"
					+"		            FROM maintenance_request_tasks"
					+"		           WHERE mrq_mrq_id = mrq_id) Request_Total_Tasks,"
					+"				 (SELECT NVL(SUM (task_total_cost), 0)"
					+"		            FROM maintenance_request_tasks"
					+"		           WHERE mrq_mrq_id = mrq_id"
					+"						 AND recharge_flag = 'N') Request_Total_Tasks_Rech_N,"
					+"		         (SELECT sum((d.total_doc_price) - NVL (d.total_doc_tax, 0))"
					+"					FROM docl dl,doc d, maintenance_request_tasks mrt"
					+"					WHERE dl.line_type IN ('INVOICEAR', 'DEBITAR')"
					+"					AND d.doc_id = dl.doc_id"
					+"					AND dl.generic_ext_id = mrt.mrt_id"
					+"					AND dl.line_status = 'P'"
					+"					AND dl.source_code = 'FLMAINT'"
					+"					AND mrt.mrq_mrq_id = mr.mrq_id" 
					+"					AND rownum =1) ACTUAL_CUST_CHARGE,"
					+"		         (SELECT NVL(SUM (rech_total_cost), 0)"
					+"		            FROM maintenance_request_tasks"
					+"		           WHERE mrq_mrq_id = mrq_id"
					+"		                 AND recharge_flag = 'Y') Recharge_Total,"
					+"				 (SELECT sum(d.total_price)"
					+"        			FROM docl d, maintenance_request_tasks mrt" 
					+"                WHERE d.line_type IN ('INVOICEAP')"
					+"     			    AND d.line_status = 'P'" 
					+"            		AND d.source_code = 'FLMAINT'"
					+"					AND mrt.mrq_mrq_id = mr.mrq_id"
					+"            		AND d.generic_ext_id = mrt.mrt_id  ) TOTAL_PAID_PRICE,"
					+"		         (SELECT SUM (d.total_price)"
					+"		            FROM docl d, maintenance_request_tasks mrt" 
					+"					WHERE     d.generic_ext_id = mrt.mrt_id"
					+"		                 AND mrt.mrq_mrq_id = mr.mrq_id"
					+"		                 AND mrt.recharge_flag = 'Y'"
					+"						 AND mrt.maint_code NOT IN (:excludeMaintCodesFromPOTotal)"
					+"		                 AND d.line_type IN ('INVOICEAR','DEBITAR')"
					+"						 AND d.line_status = 'P'"
					+"		                 AND d.source_code = 'FLMAINT') Request_Total_Docl,"
					+"		         (SELECT LISTAGG (mrt.mcg_maint_cat_code, ', ')"
					+"		                    WITHIN GROUP (ORDER BY mrt.mrq_mrq_id)"
					+"		            FROM (SELECT DISTINCT mrq_mrq_id, mcg_maint_cat_code"
					+"		                    FROM maintenance_request_tasks) mrt"
					+"		           WHERE mrt.mrq_mrq_id = mrq_id) Categories");
		}
		
		sqlStmt.append("		   FROM maintenance_requests mr, suppliers s, maint_request_status_codes mrsc "
			+"		   				WHERE mr.sup_sup_id = s.sup_id " 
			+"						AND ");
		
		sqlStmt.append("				mr.fms_fms_id IN (:fmsIds) "
				+"                       AND mr.maint_request_status = mrsc.maint_request_status ");
		
		//Filtering for Service Provider Name or Code occurs here
		if(providerFilter != null && !providerFilter.isEmpty()){
			sqlStmt.append("		   AND (lower(s.supplier_name) LIKE lower(:serviceProvider) escape '*' OR lower(s.supplier_code) LIKE lower(:serviceProvider) escape '*')");
		}
		// Filtering on maintenance categories occurs here
		if(maintCategoryFilter != null && !maintCategoryFilter.isEmpty()){
			sqlStmt.append("		   AND mr.mrq_id IN (Select DISTINCT mrt.mrq_mrq_id from maintenance_request_tasks mrt where mrt.mrq_mrq_id IN (mr.mrq_id) AND lower(MRT.MCG_MAINT_CAT_CODE) LIKE lower(:categoryCode) escape '*' )");
		} 
		
		// Filtering on Maintenance Code Description occurs here
		if(maintCodeDescFilter != null && !maintCodeDescFilter.isEmpty()){
			sqlStmt.append("		   AND mr.mrq_id IN (Select DISTINCT mrt.mrq_mrq_id from maintenance_request_tasks mrt, maintenance_codes mc where mrt.maint_code = mc.maint_code AND mrt.mrq_mrq_id IN (mr.mrq_id) AND lower(NVL(MRT.VENDOR_CODE_DESC, MC.MAINT_CODE_DESC)) LIKE lower(:maintCodeDesc) escape '*' )");
		}
		
		if(sort!= null && countQuery == false){
			Order order = sort.iterator().next();
			String sortOrder = "DESC";
			if(order.isAscending()){
				sortOrder = "ASC";
			}
		
			if(DataConstants.SERVICE_HISTORY_SORT_FIELD_START_DATE.equalsIgnoreCase(order.getProperty())){
				sqlStmt.append(" ORDER BY mr.actual_start_date ").append(sortOrder);	
			}else if(DataConstants.SERVICE_HISTORY_SORT_FIELD_ODO.equalsIgnoreCase(order.getProperty())){
				sqlStmt.append(" ORDER BY mr.current_odo "+sortOrder);		
			}else if(DataConstants.SERVICE_HISTORY_SORT_FIELD_STATUS.equalsIgnoreCase(order.getProperty())){
				sqlStmt.append(" ORDER BY mrsc.sort_order " + sortOrder + ","
						+"		         mr.actual_start_date DESC");	
			}
			else{
				sqlStmt.append(" ORDER BY decode(mod(4, mrsc.sort_order),4, 5, mrsc.sort_order) asc,"
						+"		         mr.actual_start_date DESC");		
			}
		}
		
		query = entityManager.createNativeQuery(sqlStmt.toString());	
		query.setParameter("fmsIds", fmsIds);
		if(providerFilter != null && !providerFilter.isEmpty()){
			query.setParameter("serviceProvider", '%' + providerFilter.replaceAll("_", "*_") + '%');
		}
		if(maintCategoryFilter != null && !maintCategoryFilter.isEmpty()){
			query.setParameter("categoryCode", '%' + maintCategoryFilter.replaceAll("_", "*_") + '%');
		}
		if(maintCodeDescFilter != null && !maintCodeDescFilter.isEmpty()){
			query.setParameter("maintCodeDesc", '%' + maintCodeDescFilter.replaceAll("_", "*_") + '%');
		}
		if(excludeMaintCodesFromPOTotal != null && !excludeMaintCodesFromPOTotal.isEmpty()){
			query.setParameter("excludeMaintCodesFromPOTotal", excludeMaintCodesFromPOTotal);
		}
		

		return query;
	}
	
	public int getMaintRequestByFmsIdsCount(List<Long> fmsIds, String providerFilter, String maintCategoryFilter, String maintCodeDescFilter, List<String> excludeMaintCodesFromPOTotal)
	{
		int result = 0;
		
		if(!fmsIds.isEmpty()) {		
			Query query = generateMaintenanceServiceHistoryQuery(true, fmsIds, null, providerFilter, maintCategoryFilter, maintCodeDescFilter, excludeMaintCodesFromPOTotal);

			try {
				result = ((BigDecimal) query.getSingleResult()).intValue();
			}
			catch (NoResultException nre){
				//not found
			}
		}
		
		return result;

	}
	
	/**
	 * Retrieves the count of children a given maintenance request has.
	 * @param parentMrqId The parent Maintenance Request Id.
	 * @return int Count of children
	 */
	public int getMaintRequestByMrqMrqIdCount(Long parentMrqId){			
		String sqlStmt = "SELECT count(1) FROM maintenance_requests WHERE mrq_mrq_id = :mrqMrqId";
		Query query = entityManager.createNativeQuery(sqlStmt);
		query.setParameter("mrqMrqId", parentMrqId);
		BigDecimal count = (BigDecimal) query.getSingleResult();		
		return count.intValue();		
	}
	
	/**
	 * Creates a PORDER (DOC/DOCL records) for a given maintenance request.
	 * @param MaintenanceRequest Maintenance Request entity
	 */
	public void createPurchaseOrderDocument(MaintenanceRequest maintenanceRequest){
		String sqlStmt = "BEGIN MAINTENANCE_REQUEST_WRAPPER.CREATE_MAINTENANCE_PO_DOCUMENT(?, ?, ?, ?); END;";
		Query query = entityManager.createNativeQuery(sqlStmt);
    	query.setParameter(1, maintenanceRequest.getServiceProvider().getPayeeAccount().getExternalAccountPK().getCId());
    	query.setParameter(2, maintenanceRequest.getLastChangedBy());
    	query.setParameter(3, maintenanceRequest.getFleetMaster().getFmsId());
    	query.setParameter(4, maintenanceRequest.getMrqId());
    	query.executeUpdate();
	}
	
	/**
	 * Calls the web notification module to setup notification events when
	 * the maintenance request/PO total exceeds amounts pre-determined 
	 * by our customers via the WEB CA app.
	 * @param MaintenanceRequest Maintenance Request entity
	 */
	public void createWebNotification(MaintenanceRequest maintenanceRequest){
		String sqlStmt = "BEGIN WEB_NOTIFICATION_WRAPPER.NOTIFY_MAINT_PO_EXCEEDS_AMT(?); END;";
		Query query = entityManager.createNativeQuery(sqlStmt);
    	query.setParameter(1, maintenanceRequest.getMrqId());
    	query.executeUpdate();		
	}

	public void delete(MaintenanceRequest maintReq) {
		maintenanceRequestDAO.deleteById(maintReq.getMrqId());
	}
}
