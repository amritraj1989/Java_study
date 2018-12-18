package com.mikealbert.data.dao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.springframework.data.domain.Pageable;

import com.mikealbert.data.entity.MaintenanceRequest;
import com.mikealbert.data.entity.WillowConfig;
import com.mikealbert.data.vo.ProgressChasingQueueVO;
import com.mikealbert.data.vo.ProgressChasingVO;
import com.mikealbert.util.MALUtilities;

public class ProgressChasingDAOImpl extends GenericDAOImpl<MaintenanceRequest, Long> implements ProgressChasingDAOCustom {

	/**
	 * Find progress chasing for purchase orders
	 */
	private static final long serialVersionUID = 1L;
	
	@Resource
	private WillowConfigDAO willowConfigDAO;
	@Resource
	private ProgressChasingDAO progressChasingDAO;	
	
	@SuppressWarnings("unchecked")
	public List<ProgressChasingVO> getProgressChasingByPoStatus(String selectedPoStatus, Pageable pageable, String lastUpdatedByFilter) {
		List<ProgressChasingVO> progressChasingList = new ArrayList<ProgressChasingVO>();
		Query query = generateProgressChasingDataQuery(false, selectedPoStatus, lastUpdatedByFilter);
		
		if(pageable != null){
			query.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
			query.setMaxResults(pageable.getPageSize());
		}
		List<Object[]> resultList = (List<Object[]>)query.getResultList();
		if (resultList != null){
			for (Object[] record : resultList){
				int i = 0;
				
				ProgressChasingVO progressChasingVO = new ProgressChasingVO();
				progressChasingVO.setMrqId(((BigDecimal)record[i]).longValue());
				progressChasingVO.setFmsId(((BigDecimal)record[i+=1]).longValue());
				progressChasingVO.setUnitNo((String)record[i+=1]);
				progressChasingVO.setPoNumber((String)record[i+=1]);
				progressChasingVO.setActualStartDate((Date)record[i+=1]);	
				progressChasingVO.setOdo(((BigDecimal)record[i+=1]).intValue());
				progressChasingVO.setSupId((BigDecimal)record[i+=1]);
				progressChasingVO.setLastChangedBy((String)record[i+=1]);
				progressChasingVO.setLastChangedDate((Date)record[i+=1]);
				progressChasingVO.setMaintRequestStatusCode(((String)record[i+=1]));
				progressChasingVO.setServiceProviderContactInfo((String)record[i+=1]);
				progressChasingVO.setMaintRequestStatusDescription((String)record[i+=1]);
				progressChasingVO.setTaskTotalCost(((BigDecimal)record[i+=1]).setScale(2, BigDecimal.ROUND_HALF_UP));
				progressChasingVO.setRechargeTotal(((BigDecimal)record[i+=1]).setScale(2, BigDecimal.ROUND_HALF_UP));
				progressChasingVO.setConcatCategoryDescriptions((record[i+=1] != null ? ((String)record[i]) : null));
				progressChasingVO.setSumTaskTotalCost(((BigDecimal)record[i+=1]).setScale(2, BigDecimal.ROUND_HALF_UP));	
				progressChasingVO.setSumRechargeTotal(((BigDecimal)record[i+=1]).setScale(2, BigDecimal.ROUND_HALF_UP));

				progressChasingList.add(progressChasingVO);
			}
		}
		return progressChasingList;
				
	}
	
	public int getProgressChasingByPoStatusCount(String selectedPoStatus, String lastUpdatedByFilter) {
		int result = 0;
		Query query = generateProgressChasingDataQuery(true, selectedPoStatus, lastUpdatedByFilter);

		try {
			result = ((BigDecimal) query.getSingleResult()).intValue();
		}
		catch (NoResultException nre){
			//not found
		}

		return result;
	}		
	
	@SuppressWarnings("unchecked")
	public  List<ProgressChasingQueueVO> getProgressChasing() {
		List<ProgressChasingQueueVO> progressChasingQueueList = new ArrayList<ProgressChasingQueueVO>();
		Query query = generateProgressChasingDataQuery(false, null, null);
		
		List<Object[]> resultList = (List<Object[]>)query.getResultList();
		if (resultList != null){
			for (Object[] record : resultList){
				int i = 0;
				
				ProgressChasingQueueVO progressChasingQueueVO = new ProgressChasingQueueVO();
				progressChasingQueueVO.setMaintRequestStatus((String)record[i]);	
				progressChasingQueueVO.setMaintRequestStatusDescription((String)record[i+=1]);
				progressChasingQueueVO.setPoStatusCount(((BigDecimal)record[i+=1]).intValue());
				
				progressChasingQueueList.add(progressChasingQueueVO);
			}
		}
		return progressChasingQueueList;
	}
	
	public Query generateProgressChasingDataQuery(boolean countQuery, String selectedPoStatus, String lastUpdatedByFilter) {
		
		Query query = null;
		StringBuilder sqlStmt = new StringBuilder("");
		WillowConfig willowConfig = willowConfigDAO.findById("PROGESS_CHASING_CUTOFF").orElse(null);
		String cutoffMonth= willowConfig != null ? willowConfig.getConfigValue() :"24";
		StringBuffer subTable = new StringBuffer();
		subTable.append(" ( WITH invap AS (SELECT doc_id, parent_doc_id FROM doc,doc_links  WHERE child_doc_id = doc_id  AND source_code = 'FLMAINT' AND doc_status  = 'P' AND doc_type = 'INVOICEAP' )");
		subTable.append(" SELECT DISTINCT d.doc_no main_doc,invap.doc_id ap_doc  FROM doc d,invap WHERE d.doc_id    = invap.parent_doc_id(+)  AND d.doc_type    = 'PORDER'");
		subTable.append("  AND d.doc_status  = 'R' ");  
		subTable.append("  AND d.source_code = 'FLMAINT' ");  
		subTable.append(" AND invap.doc_id IS NULL) tmp ");          
       
		if(countQuery == true){
			sqlStmt.append("SELECT count(1)");
		} else {	
			if(selectedPoStatus == null){
				StringBuffer countStmt = new StringBuffer( " decode(mrsc.maint_request_status, 'C',(SELECT count(1)");
				countStmt.append(" from ").append(subTable).append(", maintenance_requests mr where mr.job_no = tmp.main_doc and mr.maint_request_status='C' ");
				countStmt.append(" AND mr.last_changed_date >= Add_months(Trunc(SYSDATE) ,").append("-").append(cutoffMonth).append("))") ;
				countStmt.append(", COUNT(mr1.maint_request_status))");
				sqlStmt.append("SELECT mrsc.maint_request_status, mrsc.description,COUNT(mr1.maint_request_status)").append(" FROM (");
      
				//sqlStmt.append("SELECT mrsc.maint_request_status, mrsc.description, COUNT(mr1.maint_request_status) FROM (");
			}
			if(!MALUtilities.isEmpty(selectedPoStatus)){
				sqlStmt.append(" select tst.mrq_id,tst.fms_fms_id,tst.unit_no,tst.job_no,tst.actual_start_date,"
						+ "tst.current_odo,tst.sup_sup_id,tst.last_changed_by,tst.last_changed_date,tst.maint_request_status,tst.supplier_contact_info, "
						+  " maint_req_status_desc," 
						+ "tst.Request_Total_Tasks,tst.Recharge_Total,Categories,sum(tst.Request_Total_Tasks) over(), sum(tst.Recharge_Total) over()  "
				   		
						+ "from ( ");
			}
			sqlStmt.append("SELECT" +		
						   " mr.mrq_id," +
					       " mr.fms_fms_id, " +
					       " fm.unit_no," +
						   " mr.job_no," +
						   " mr.actual_start_date," +
						   " mr.current_odo," +
						   " mr.sup_sup_id," +
						   " mr.last_changed_by," +
						   " mr.last_changed_date," +
						   " mr.maint_request_status," +
						   " mr.supplier_contact_info," +
						   " (SELECT description" +
						   		" FROM maint_request_status_codes mrsc" +
						   		" WHERE mrsc.maint_request_status = mr.maint_request_status) maint_req_status_desc," +
						   " (SELECT NVL(SUM (task_total_cost), 0)" +
						   		" FROM maintenance_request_tasks" +
						   		" WHERE mrq_mrq_id = mrq_id) Request_Total_Tasks," +
						   " (SELECT NVL(SUM (rech_total_cost), 0)" +
						   		" FROM maintenance_request_tasks" +
						   		" WHERE mrq_mrq_id = mrq_id" +
						   		" AND recharge_flag = 'Y') Recharge_Total," +
						   " (SELECT LISTAGG (NVL(mrt.mcg_maint_cat_code, ''), ', ')" +
						   		" WITHIN GROUP (ORDER BY mrt.mrq_mrq_id)" +
						   		" FROM (SELECT DISTINCT mrq_mrq_id, mcg_maint_cat_code" +
						   				" FROM maintenance_request_tasks) mrt" +
						   				" WHERE mrt.mrq_mrq_id = mrq_id) Categories");
					
					/*
					//Sort of a hack here ... getting totals up front because couldn't get datatable footer totals to update properly when filtering.
					if(selectedPoStatus != null){
						sqlStmt.append(
						   ", (SELECT NVL(SUM (task_total_cost), 0)" + 
				   			" FROM maintenance_request_tasks mrt, maintenance_requests mr  ");
						
				   			sqlStmt.append(" WHERE mrt.mrq_mrq_id = mr.mrq_id");
						
						   			
							if(lastUpdatedByFilter != null && !lastUpdatedByFilter.isEmpty()){
								sqlStmt.append(" AND (lower(mr.last_changed_by) LIKE lower(:lastUpdatedBy) escape '*' )");			
							} 						   			
							sqlStmt.append(" AND maint_request_status = :selectedPoStatus) sum_task_total_cost," );
							
													   		
							sqlStmt.append(" (SELECT NVL(SUM (rech_total_cost), 0)" + 
				   			" FROM maintenance_request_tasks mrt , maintenance_requests mr ");
				   			
				   			sqlStmt.append( " WHERE mrt.mrq_mrq_id = mr.mrq_id" +
				   			" AND recharge_flag = 'Y'");
							
							if(lastUpdatedByFilter != null && !lastUpdatedByFilter.isEmpty()){
								sqlStmt.append(" AND (lower(mr.last_changed_by) LIKE lower(:lastUpdatedBy) escape '*' )");			
							} 
							
							sqlStmt.append(" AND maint_request_status = :selectedPoStatus) sum_rech_total_cost ");
					} */
		
			}

		sqlStmt.append(
			   		" FROM maintenance_requests mr, fleet_masters fm, maint_request_status_codes mrsc" );//+ " ,"+subTable.toString()+
			   		if("C".equals(selectedPoStatus)){
						sqlStmt.append(" ,").append(subTable);
					}
			   		sqlStmt.append(" WHERE fm.fms_id = mr.fms_fms_id AND mr.maint_request_status = mrsc.maint_request_status");

		
		if(selectedPoStatus != null){
			sqlStmt.append(
					   " AND mr.maint_request_status = :selectedPoStatus");

			// Filtering on last updated by
			if(lastUpdatedByFilter != null && !lastUpdatedByFilter.isEmpty()){
				sqlStmt.append(" AND (lower(mr.last_changed_by) LIKE lower(:lastUpdatedBy) escape '*' )");			
			} 
			if("C".equals(selectedPoStatus)){
				sqlStmt.append(" and mr.job_no = tmp.main_doc ");
				sqlStmt.append(" AND mr.last_changed_date >= Add_months(Trunc(SYSDATE) ,").append("-").append(cutoffMonth).append(")") ;
				
			}
			sqlStmt.append(
					   " order by mr.actual_start_date asc");	
		} else {		
			sqlStmt.append(			
					   " AND mr.maint_request_status in ('WI','P','WCA','S','B','I','GP','H')" +
					   " ) mr1, maint_request_status_codes mrsc" +
					   " WHERE mr1.maint_request_status(+) = mrsc.maint_request_status" +
					   " AND mrsc.maint_request_status in ('WI','P','WCA','S','B','I','GP','H')" +
					   " GROUP BY mrsc.maint_request_status, mrsc.description" + 
					   " order by decode(mrsc.maint_request_status,'P',1,2) asc," +
					            " decode(mrsc.maint_request_status,'S',1,2) asc," +
					            " decode(mrsc.maint_request_status,'B',1,2) asc," +
					            " decode(mrsc.maint_request_status,'GP',1,2) asc," +
					            " decode(mrsc.maint_request_status,'WCA',1,2) asc," +        
					            " decode(mrsc.maint_request_status,'I',1,2) asc," +
								" decode(mrsc.maint_request_status,'WI',1,2) asc,"+				            
								" decode(mrsc.maint_request_status,'H',1,2) asc");			//HPS-1460 included the PO with status H//			
		}
		if(!MALUtilities.isEmpty(selectedPoStatus) && countQuery == false){
			sqlStmt.append("  ) tst");
		}
		System.err.println(sqlStmt.toString());
		query = entityManager.createNativeQuery(sqlStmt.toString());
		
		if(selectedPoStatus != null){
			query.setParameter("selectedPoStatus", selectedPoStatus);
		}
		
		if(lastUpdatedByFilter != null && !lastUpdatedByFilter.isEmpty()){
			query.setParameter("lastUpdatedBy", '%' + lastUpdatedByFilter.replaceAll("_", "*_") + '%');
		}		
		
		return query;
	}
}