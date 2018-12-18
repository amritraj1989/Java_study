package com.mikealbert.data.dao;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import com.mikealbert.data.entity.FleetMaster;
import com.mikealbert.data.vo.ManufacturerProgressQueueVO;
import com.mikealbert.data.vo.ManufacturerProgressSearchCriteriaVO;
import com.mikealbert.exception.MalException;
import com.mikealbert.util.MALUtilities;

public class ManufacturerProgressQueueDAOImpl extends GenericDAOImpl<FleetMaster, Long> implements ManufacturerProgressQueueDAOCustom {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6059249624757596761L;

	@Override
	public int findLimitCount(String psgName, String propertyName) {
				
		String queryString = "select psp.value from process_stage_properties psp, process_stages psg"
				+ " where psp.psg_psg_id = psg.psg_id "
				+ " and psg.name = :psgName "
				+ " and psp.name = :propertyName";
		
		Query query = entityManager.createNativeQuery(queryString);
		query.setParameter("psgName", psgName);
		query.setParameter("propertyName", propertyName);
		
		int returnValue =  MALUtilities.getIntegerValue((String)query.getSingleResult());

		return returnValue;
	}
	
	@Override
	public String getPropertyValueByName(String psgName, String propertyName) {
				
		String queryString = "select psp.value from process_stage_properties psp, process_stages psg"
				+ " where psp.psg_psg_id = psg.psg_id "
				+ " and psg.name = :psgName "
				+ " and psp.name = :propertyName";
		
		Query query = entityManager.createNativeQuery(queryString);
		query.setParameter("psgName", psgName);
		query.setParameter("propertyName", propertyName);
		
		String returnValue = null;
		try{
			returnValue = (String) query.getSingleResult();
		} catch (NoResultException nre){

		}
		return returnValue;
	}
	
	public boolean isWithinTolerance(Long psoId) {
		boolean isWithinTolerance = false;
		
		String sql = "SELECT otd_queue_wrapper.is_mfg_item_witin_tolerance(:psoId) FROM DUAL";
		Query query = entityManager.createNativeQuery(sql);
		query.setParameter("psoId", psoId);
		
		isWithinTolerance = MALUtilities.convertYNToBoolean((String)query.getSingleResult());
		
		return isWithinTolerance;
	}

	@Override
	public List<ManufacturerProgressQueueVO> getManufacturerQueueSearchResults(ManufacturerProgressSearchCriteriaVO searchCriteria) throws MalException{
		
		StringBuilder sqlStmt = new StringBuilder();
		Map<String, Object> parameterMap = new HashMap<String, Object>();
		
		sqlStmt.append("WITH main_qry AS "
				+ " (SELECT pso.pso_id, pso.psg_psg_id as psg_id, pso.object_id AS doc_id, pso.property_10 As make_desc, pso.property_2 AS unit_no, pso.property_8 AS model_desc, "
				+ " pso.property_1 AS fms_id, pso.property_7 AS doc_date, pso.property_9 AS standard_delivery_lead_time,  pso.property_6 AS PDI_LEAD_TIME, "
				+ " (SELECT sph.action_date FROM supplier_progress_history sph WHERE sph.sph_id = (SELECT MIN(sph1.sph_id) FROM supplier_progress_history sph1 WHERE sph1.doc_id = pso.object_id AND progress_type = '15_DSMFGDV')) AS first_15_dsmfgdv, "
				+ " (SELECT sph.action_date FROM supplier_progress_history sph WHERE sph.sph_id = (SELECT MAX(sph1.sph_id) FROM supplier_progress_history sph1 WHERE sph1.doc_id = pso.object_id AND progress_type = '14_ETA')) AS latest_14_eta, "
				+ " 'N' AS tolerance_yn, pso.reason AS tolerance_message,  "
				+ " (SELECT MIN(lbe.follow_up_date) FROM log_book_entries lbe, object_log_books olb, log_books lbk, fleet_masters fms1 WHERE fms1.unit_no = pso.property_2 AND lbk.type in ('BASE_VEH_ORDER_NOTES') AND olb.lbk_lbk_id = lbk.lbk_id AND olb.object_name = 'FLEET_MASTERS' AND olb.object_id = fms1.fms_id AND lbe.olb_olb_id = olb.olb_id) AS follow_up_date, "
				+ " pso.property_3 AS mdl_id "
				+ " FROM process_stage_objects pso  "
				+ " WHERE pso.psg_psg_id = (select psg.psg_id from process_stages psg where psg.name = 'MANUFACTURER' ) "
				+ "   AND object_name    = 'DOCS' "
				+ "   AND pso.include_yn = 'Y'"
				+ " )," +
				"	  acc_dtls AS" +
				"	  (SELECT distinct quo.c_id," +
				"	    quo.account_type," +
				"	    quo.account_code account_code," +
				"	    ea.account_name," +
				"	    qmd.unit_no" +
				"	  FROM quotations quo," +
				"	    quotation_models qmd," +
				"	    external_accounts ea," +
				"	    main_qry" +
				"	  WHERE quo.quo_id    = qmd.quo_quo_id" +
				"	  AND ea.c_id         = quo.c_id" +
				"	  AND ea.account_type = quo.account_type" +
				"	  AND ea.account_code = quo.account_code" +
				"	  AND (qmd.unit_no   (+) = main_qry.unit_no )" +
				"	  )" +
				"	SELECT main_qry.pso_id, main_qry.psg_id, main_qry.doc_id, main_qry.fms_id, main_qry.make_desc, main_qry.unit_no, main_qry.model_desc," +
				"	       acc_dtls.c_id, acc_dtls.account_type, acc_dtls.account_code, acc_dtls.account_name, " +
				"	       main_qry.doc_date, main_qry.standard_delivery_lead_time, main_qry.pdi_lead_time, main_qry.first_15_dsmfgdv, main_qry.latest_14_eta," +
				"	       main_qry.tolerance_yn, main_qry.TOLERANCE_MESSAGE, main_qry.follow_up_date, main_qry.mdl_id" +
				"	  FROM acc_dtls, main_qry" +
				"	 WHERE acc_dtls.unit_no (+) = main_qry.unit_no ");

		
		if(searchCriteria != null){
			if(!MALUtilities.isEmpty(searchCriteria.getUnitNo())){
				sqlStmt.append("   AND main_qry.unit_no LIKE (:unitNo)  ");
				parameterMap.put("unitNo", "%" + searchCriteria.getUnitNo() + "%");
			}
			
			if(!MALUtilities.isEmpty(searchCriteria.getMake())){
				sqlStmt.append("   AND LOWER(main_qry.make_desc) like LOWER(:make)  ");
				parameterMap.put("make", "%" + searchCriteria.getMake() + "%");
			}
			
			if(!MALUtilities.isEmpty(searchCriteria.getClient())){
				sqlStmt.append("   AND (LOWER(acc_dtls.account_code) LIKE LOWER(:client) OR LOWER(acc_dtls.account_name) LIKE LOWER(:client) ) ");
				parameterMap.put("client", "%" + searchCriteria.getClient() + "%");
			}
			
		}
		sqlStmt.append(" ORDER BY  main_qry.make_desc, main_qry.unit_no ");
		
		Query query = entityManager.createNativeQuery(sqlStmt.toString());
		for(String paramName : parameterMap.keySet()) {
			query.setParameter(paramName, parameterMap.get(paramName));
		}
		
		List<ManufacturerProgressQueueVO> manufacturerProgressQueueList = new ArrayList<ManufacturerProgressQueueVO>();
		try{
			
			@SuppressWarnings("unchecked")
			List<Object[]> objectList  = (List<Object[]>) query.getResultList();
			
			SimpleDateFormat dateFormat = new SimpleDateFormat(MALUtilities.DATE_PATTERN);
			
			
			ManufacturerProgressQueueVO manufacturerProgressQueue = null;
			if (objectList != null && objectList.size() > 0) {	
				
				for (Object[] object : objectList) {
					
					manufacturerProgressQueue = new ManufacturerProgressQueueVO();
					int i = 0;
					
					manufacturerProgressQueue.setPsoId(object[i]					!= null ? ((BigDecimal) object[i]).longValue() 	: null);
					manufacturerProgressQueue.setPsgId(object[i+=1]					!= null ? ((BigDecimal) object[i]).longValue() 	: null);
					manufacturerProgressQueue.setDocId(object[i+=1]					!= null ? ((BigDecimal) object[i]).longValue() 	: null);
					manufacturerProgressQueue.setFmsId(object[i+=1]					!= null ? (new BigDecimal( (String)object[i]).longValue()) 	: null);
					manufacturerProgressQueue.setMake(object[i+=1]					!= null ? (String) 		object[i]			 	: null);
					manufacturerProgressQueue.setUnitNo(object[i+=1]				!= null ? (String) 		object[i]			 	: null);
					manufacturerProgressQueue.setTrim(object[i+=1]					!= null ? (String) 		object[i]			 	: null);
					manufacturerProgressQueue.setClientAccountCId(object[i+=1]		!= null ? ((BigDecimal) object[i]).longValue() 	: null);
					manufacturerProgressQueue.setClientAccountType(object[i+=1]		!= null ? (String) 		object[i]			 	: null);
					manufacturerProgressQueue.setClientAccountCode(object[i+=1]		!= null ? (String) 		object[i]			 	: null);
					manufacturerProgressQueue.setClientAccountName(object[i+=1]		!= null ? (String) 		object[i]			 	: null);
					manufacturerProgressQueue.setPoDate(object[i+=1]				!= null ? dateFormat.parse(((String)object[i]))	: null);
					manufacturerProgressQueue.setLeadTime(object[i+=1]				!= null ? Long.valueOf((String)object[i])		: null);
					manufacturerProgressQueue.setPdiLeadTime(object[i+=1]			!= null ? Long.valueOf((String)object[i])		: null);
					manufacturerProgressQueue.setDesiredToDealer(object[i+=1]		!= null ? (Date) 		object[i]				: null);
					manufacturerProgressQueue.setExpectedDate(object[i+=1]			!= null ? (Date) 		object[i]				: null);
					manufacturerProgressQueue.setToleranceYn(object[i+=1]			!= null ? String.valueOf(object[i])			 	: null);
					manufacturerProgressQueue.setToleranceMessage(object[i+=1]		!= null ? (String) 		object[i]			 	: null);
					manufacturerProgressQueue.setFollowUpDate(object[i+=1]			!= null ? (Date) 		object[i]				: null);
					
					manufacturerProgressQueueList.add(manufacturerProgressQueue);
				}
			}
		} catch (Exception ex){
			throw new MalException("Error while getting Manufacturing Queue list.", ex);
		}
		
		return manufacturerProgressQueueList;
	}	
	
}
