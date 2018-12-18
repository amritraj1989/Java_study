package com.mikealbert.data.dao;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.Date;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import com.mikealbert.common.MalLogger;
import com.mikealbert.common.MalLoggerFactory;
import com.mikealbert.data.entity.QuotationModel;
import com.mikealbert.data.enumeration.OrderToDeliveryProcessStageEnum;
import com.mikealbert.exception.MalException;
import com.mikealbert.util.MALUtilities;

/**
* DAO implementation for UpfitProgressChasing 
* @author ravresh
*/

public class UnitProgressDAOImpl extends GenericDAOImpl<QuotationModel, Long> implements UnitProgressDAOCustom {
	
	private static final long serialVersionUID = -4029055333153724372L;
	
	private MalLogger logger = MalLoggerFactory.getLogger(this.getClass());	

	@SuppressWarnings("unchecked")
	public List<Object[]> getBasicAcceptedQuotesDetailForUpfitProgressChasing() throws MalException {

		String stmt = "SELECT pso_id, stock_yn, driver_name, account_name, unit_no, vin, doc_id, qmd_id, order_type, replacement_for_fms_id, old_unit_no, delivery_dealer_name, delivery_dealer_code, "
				+ "invoice_processed_date, factory_ship_date, dealer_recvd_date, client_eta_date, vehicle_ready_date, follow_up_date, reqd_by, model_desc, tolerance_yn "
				+ "FROM upfit_queue_v";
		
		Query query = entityManager.createNativeQuery(stmt);
		List<Object[]> qmdFmsDetails = query.getResultList();
		
		return qmdFmsDetails;
	}
	
	// getPendingInServiceUnit  and getPendingInServiceUnit method should have always same result set otherwise functionality will be broken
	@SuppressWarnings("unchecked")
	public List<Object[]> getPendingInServiceUnitsList()  {
		StringBuffer queryString = new StringBuffer("");
		queryString.append("SELECT pso_id, stock_yn, driver_name, drv_id, account_name, account_code, unit_no, vin, doc_id, qmd_id, quote_status, used_vehicle, order_type, lic_plate_no, plate_expiry_date, "
				+ "replacement_for_fms_id, old_unit_no, delivery_dealer_name, delivery_dealer_code, invoice_processed_date, "
				+ "dealer_recvd_date, client_eta_date, vehicle_ready_date, follow_up_date, reqd_by, model_desc, lead_time, dr_description, purchase_yn, exterior_colour, manufacturer FROM in_service_queue_v");
		
		
		Query query = entityManager.createNativeQuery(queryString.toString());
		List<Object[]>resultList = (List<Object[]>) query.getResultList();
				
		return resultList;
	}
	// getPendingInServiceUnit  and getPendingInServiceUnit method should have always same result set otherwise functionality will be broken
	@SuppressWarnings("unchecked")
	public List<Object[]> getPendingInServiceUnit(String unitNo)  {
		StringBuffer queryString = new StringBuffer("");
		queryString.append("SELECT NULL, DECODE (d.source_code, 'FLQUOTE', 'N', 'Y'), drv.driver_surname || ', ' || drv.driver_forename, drv.drv_id, ea.account_name, ea.account_code," +
						   "       fms.unit_no, fms.vin, d.doc_id, qmd.qmd_id, qmd.quote_status, qmd.used_vehicle, qmd.order_type, ur.lic_plate_no, ur.plate_expiry_date, qmd.replacement_for_fms_id," +
						   "       (SELECT unit_no FROM fleet_masters WHERE fms_id = qmd.replacement_for_fms_id)," +
						   "       (SELECT account_name FROM external_accounts WHERE c_id = d.sub_acc_c_id AND account_type = d.sub_acc_type AND account_code = d.sub_acc_code)," +
						   "       (SELECT account_code FROM external_accounts WHERE c_id = d.sub_acc_c_id AND account_type = d.sub_acc_type AND account_code = d.sub_acc_code)," +
						   "       (SELECT action_date FROM supplier_progress_history WHERE sph_id = (SELECT MAX (sph_id) FROM supplier_progress_history sph1 WHERE sph1.doc_id = d.doc_id AND progress_type = '20_INVPROC'))," +
						   "       (SELECT action_date FROM supplier_progress_history WHERE sph_id = (SELECT MAX (sph_id) FROM supplier_progress_history sph1 WHERE sph1.doc_id = d.doc_id AND progress_type = '15_RECEIVD'))," +
						   "       (SELECT action_date FROM supplier_progress_history WHERE sph_id = (SELECT MAX (sph_id) FROM supplier_progress_history sph1 WHERE sph1.doc_id = d.doc_id AND progress_type = '14_ETA'))," +
						   "       (SELECT action_date FROM supplier_progress_history WHERE sph_id = (SELECT MAX (sph_id) FROM supplier_progress_history sph1 WHERE sph1.doc_id = d.doc_id AND progress_type = '15_VEHRDY'))," +
						   "       (SELECT MIN (follow_up_date) FROM log_book_entries lbe, object_log_books olb WHERE lbe.olb_olb_id = olb.olb_id AND olb.lbk_lbk_id IN (SELECT lbk_id FROM log_books WHERE TYPE = 'INSERV_PRG_NOTES') AND olb.object_id = fms.fms_id AND lbe.follow_up_date IS NOT NULL)," +
						   "       DECODE (qmd.client_request_type, 'AS', 'ASAP', 'NS', 'Not Specified', 'OD', TO_CHAR (qmd.required_date, 'mm/dd/rr'), qmd.client_request_type)," +
						   "       mdl.model_desc, willow2k.po_mgr.get_unit_lead_time (d.doc_id), dr.dr_description, DECODE(quotation1.get_product_type(qmd.qmd_id,null), 'A', 'Y', 'N'), " +
						   "	   (SELECT description from colour_codes where colour_code = qmd.colour_code) exterior_colour, " +
						   "	   (SELECT make_desc FROM makes WHERE mak_Id = qmd.mak_mak_id) manufacturer " +
						   "  FROM fleet_masters fms, quotation_models qmd, doc d, docl dl, dist di, quotations quo, drivers drv, external_accounts ea, unit_registrations_v ur, models mdl, delay_reasons dr" +
						   " WHERE qmd.quo_quo_id = quo.quo_id" +
						   "       AND quo.drv_drv_id = drv.drv_id" +
						   "       AND quo.c_id = ea.c_id" +
						   "       AND quo.account_type = ea.account_type" +
						   "       AND quo.account_code = ea.account_code" +
						   "       AND qmd.mdl_mdl_id = mdl.mdl_id" +
						   "       AND fms.dr_dr_code = dr.dr_code(+)" +
						   "       AND (fms.fms_id = qmd.fms_fms_id OR fms.unit_no = qmd.unit_no)" +
						   "       AND d.doc_id = dl.doc_id" +
						   "       AND dl.doc_id = di.docl_doc_id" +
						   "       AND dl.user_def4 = 'MODEL'" +
						   "       AND di.cdb_code_1 = fms.fms_id" +
						   "       AND di.cdb_code_4 IN ('1_LEASE', 'STOCK')" +
						   "       AND qmd.quote_status IN ('3', '16', '17')" +
						   "       AND fms.unit_no = ur.unit_no(+)" +
						   "       AND d.doc_type = 'PORDER'" +
						   "       AND d.doc_status = 'R'" +
						   "       AND NVL (d.order_type, 'M') = 'M'" +
						   "       AND d.source_code IN ('FLQUOTE', 'FLGRD', 'FLORDER')");
		
		if(!MALUtilities.isEmptyString(unitNo)) {
			queryString.append(" AND fms.unit_no = :unitNo");
		}
		
		Query query = entityManager.createNativeQuery(queryString.toString());
		if(!MALUtilities.isEmptyString(unitNo)) {
			query.setParameter("unitNo", unitNo);
		}
		List<Object[]>resultList = (List<Object[]>) query.getResultList();
				
		return resultList;
	}
	
	@SuppressWarnings("unchecked")
	public List<Object[]> getUpfitTPTPOList() throws MalException {
		String queryString =  "SELECT /*+ LEADING(pso) */  ufp.qmd_qmd_id" +
				"    , ea.account_name vendor_name, ufp.ea_account_code vendor_code, ufp.ea_c_id vendor_ctx, d.doc_id doc_id," +
				"    po_mgr.get_po_lead_time(d.doc_id) lead_time," +
				"    DECODE(NVL(d.order_type, 'M'), 'M', 'M', 'T', 'T', 'X') order_type," +
				"    NVL((SELECT 'Y' FROM upfitter_progress ufp2 WHERE ufp2.qmd_qmd_id = ufp.qmd_qmd_id AND ufP2.sequence_no = ufp.sequence_no AND ufp2.end_date IS NOT NULL), 'N') task_completed," +
				"    DECODE(d.source_code, 'FLGRD', 'Y', 'N') stock_yn," +
				"    DECODE(ufp.ufp_ufp_id, NULL, 'N', 'Y') linked, 				" +
				"    NVL(ufp.sequence_no, 999999) sequence_no," +
				"    d.doc_date doc_date,	" +
				"    ufp.ufp_id ufp_id					" +
				" FROM process_stage_objects pso, doc d, upfitter_progress ufp, external_accounts ea" +
				" WHERE   pso.psg_psg_id = (select psg.psg_id from process_stages psg where psg.name = 'UPFIT') " +
				"    AND pso.object_name = 'DOCS'" +
				"    AND pso.include_yn = 'Y'" +
				"    AND pso.property_3 = ufp.qmd_qmd_id" +
				"    AND d.generic_ext_id = ufp.qmd_qmd_id" +
				"    AND nvl(d.order_type, 'M') IN ('T', 'M')" +
				"    AND d.doc_type = 'PORDER'" +
				"    AND ( (nvl(d.order_type, 'M') = 'M' AND d.doc_status = 'R') OR (nvl(d.order_type, 'M') = 'T' AND d.doc_status in ('O', 'R')) )				" +
				"    AND d.source_code = any('FLQUOTE', 'FLGRD', 'FLORDER')" +
				"    AND d.c_id = ufp.ea_c_id" +
				"    AND d.account_code = ufp.ea_account_code " +
				"    AND d.account_type = ufp.ea_account_type 				" +
				"    AND ea.account_code = ufp.ea_account_code" +
				"    AND ea.c_id = ufp.ea_c_id" +
				"    AND ea.account_type = ufp.ea_account_type";

		
		Query query = entityManager.createNativeQuery(queryString);
		List<Object[]>resultList = (List<Object[]>) query.getResultList();
				
		return resultList;
	}
	
	@SuppressWarnings("unchecked")
	public List<Object[]> getUpfitTPTPOListByQmdId(Long qmdId) throws MalException {
		String queryString = "SELECT ufp.qmd_qmd_id, ea.account_name vendor_name, ufp.ea_account_code vendor_code, ufp.ea_c_id vendor_ctx, d.doc_id doc_id, "
				+"    po_mgr.get_po_lead_time(d.doc_id) lead_time, "
				+"    DECODE(NVL(d.order_type, 'M'), 'M', 'M', 'T', 'T', 'X') order_type, "
				+"    DECODE(NVL(to_char(ufp.end_date, 'MM/DD/YYYY'), 'N'), 'N', 'N', 'Y') task_completed, "
				+"    DECODE(d.source_code, 'FLGRD', 'Y', 'N') stock_yn, "
				+"    DECODE(ufp.ufp_ufp_id, NULL, 'N', 'Y') linked,  "				
				+"    NVL(ufp.sequence_no, 999999) sequence_no, "
				+"    d.doc_date doc_date, "	
				+"    ufp.ufp_id ufp_id "				
				+"FROM doc d, upfitter_progress ufp, external_accounts ea "
				+"WHERE ufp.qmd_qmd_id = :qmdId "
				+"    AND d.generic_ext_id = ufp.qmd_qmd_id "
				+"    AND nvl(d.order_type, 'M') IN ('T', 'M') "
				+"    AND d.doc_type = 'PORDER' "
				+"    AND ( (nvl(d.order_type, 'M') = 'M' AND d.doc_status = 'R') OR (nvl(d.order_type, 'M') = 'T' AND d.doc_status in ('O', 'R')) ) "				
				+"    AND d.source_code = any('FLQUOTE', 'FLGRD', 'FLORDER') "
                +"    AND d.c_id = ufp.ea_c_id "
                +"    AND d.account_code = ufp.ea_account_code " 
                +"    AND d.account_type = ufp.ea_account_type " 				
				+"    AND ea.account_code = ufp.ea_account_code "
				+"    AND ea.c_id = ufp.ea_c_id "
				+"    AND ea.account_type = ufp.ea_account_type ";
		
		Query query = entityManager.createNativeQuery(queryString);
		query.setParameter("qmdId", qmdId); 		
		List<Object[]>resultList = (List<Object[]>) query.getResultList();
				
		return resultList;
	}	
	
	
	@SuppressWarnings("unchecked")
	public List<Object[]> getReconciledUpfitListByQmdId(Long qmdId, List<String> statuses, Boolean includeMainPO) throws MalException {
		StringBuilder stmt;
		Query query;
		
		stmt = new StringBuilder("");
		stmt = stmt.append("SELECT * ")
				.append("               FROM ( (SELECT d.generic_ext_id qmd_id, ")
				.append("                               ea.account_name vendor_name, ea.account_code vendor_code, ea.c_id vendor_ctx, ")
				.append("                               d.doc_id doc_id, ")
				.append("                               po_mgr.get_po_lead_time(d.doc_id) lead_time, ")
                .append("                               DECODE(NVL(d.order_type, 'M'), 'M', 'M', 'T', 'T', 'X') order_type, ")
				.append("                               DECODE(NVL(to_char(ufp.end_date, 'MM/DD/YYYY'), 'N'), 'N', 'N', 'Y') task_completed, ")
				.append("                               DECODE(d.source_code, 'FLGRD', 'Y', 'N') stock_yn, ")
				.append("                               DECODE(ufp.ufp_ufp_id, NULL, 'N', 'Y') linked,  ")				
				.append("                               NVL(ufp.sequence_no, 999999) sequence_no, ")
				.append("                               d.posted_date posted_date, ")	
				.append("                               ufp.ufp_id ufp_id ")					
				.append("                           FROM doc d, upfitter_progress ufp, external_accounts ea, quotation_models qmd ")
				.append("                           WHERE qmd.qmd_id = :qmdId ")
				.append("                               AND d.generic_ext_id = qmd.qmd_id ")
				.append("                               AND d.doc_type = 'PORDER' ");
				
		if(includeMainPO) {			
			stmt.append("                               AND ( (nvl(d.order_type, 'M') = 'M' AND d.doc_status = 'R') OR (nvl(d.order_type, 'X') = 'T' AND d.doc_status in (:statuses) ) ) ");
		} else {
			stmt.append("                               AND nvl(d.order_type, 'X') = 'T' AND d.doc_status in (:statuses) ");			
		}
		
		stmt				
				.append("                               AND d.source_code IN ('FLQUOTE', 'FLGRD', 'FLORDER') ")
				.append("                               AND d.c_id = ea.c_id ")
				.append("                               AND d.account_code = ea.account_code ")
				.append("                               AND d.account_type = ea.account_type ")
				.append("                               AND d.generic_ext_id = ufp.qmd_qmd_id (+) ")
				.append("                               AND d.c_id = ufp.ea_c_id (+) ")
				.append("                               AND d.account_code = ufp.ea_account_code (+) ")
				.append("                               AND d.account_type = ufp.ea_account_type  (+) ")				
				.append("                               AND EXISTS (SELECT 1 ")
				.append("                                               FROM docl dl ")
				.append("                                               WHERE dl.doc_id = d.doc_id ")
				.append("                                                   AND dl.user_def4 = 'DEALER')  ) )");
		
		query = entityManager.createNativeQuery(stmt.toString());
		query.setParameter("qmdId", qmdId); 
		query.setParameter("statuses", statuses); 
		
		List<Object[]>resultList = (List<Object[]>) query.getResultList();
				
		return resultList;
	}	
	
	@SuppressWarnings("unchecked")
	public List<Object[]> getSelectedUnitDetails(Long fmsId) throws MalException {
		String queryString = "Select mdl.model_desc, cc.description, fms.vin from models mdl, fleet_masters fms, quotation_models qmd, colour_codes cc "
				+ "where fms.mdl_mdl_id = mdl.mdl_id "
				+ "and fms.unit_no = qmd.unit_no "
				+ "and qmd.colour_code = cc.colour_code "
				+ "and fms.fms_id = :fmsId";
		
		Query query = entityManager.createNativeQuery(queryString);
		query.setParameter("fmsId", fmsId);
		
		List<Object[]>resultList = (List<Object[]>) query.getResultList();
				
		return resultList;
	}
	
	@SuppressWarnings("unchecked")
	public Long getLatestOdoReading(Long fmsId) {
	
		Long ododReading = null;
		
		String queryString = "	SELECT odr.odo_reading  from ODOMETERS_READINGS odr , odometers od where odr.odo_id = od.odo_id  "
				+ " and od.fms_id = :fmsId and odr.odo_reading_type in ('GRNODO', 'ODOMSO')  and odr.odo_reading_date =(   "
				+ " SELECT max(odr.odo_reading_date)  from ODOMETERS_READINGS odr1 , odometers od1 where odr1.odo_id = od1.odo_id   "
				+ " and od1.fms_id = :fmsId and odr1.odo_reading_type in ('GRNODO', 'ODOMSO') )  "
				+ " order by odr.odo_reading desc";
		Query query = entityManager.createNativeQuery(queryString);
		query.setParameter("fmsId", fmsId);
		List<Object> odoReadingList = (List<Object>) query.getResultList();	
	
		if(odoReadingList != null && odoReadingList.size() > 0) {
			BigDecimal res =  (BigDecimal)odoReadingList.get(0);
			ododReading = res.longValue();
		}
		return ododReading;
	}
	
	public Long getVehicleOdoReadingByQmdId(Long qmdId) {
		Long ododReading = null;
		
		String queryString = "Select quote_start_odo from quotation_models where qmd_id = :qmdId";
		Query query = entityManager.createNativeQuery(queryString);
		query.setParameter("qmdId", qmdId);
		
		BigDecimal odoReadingValue = (BigDecimal) query.getSingleResult();	
	
		if(odoReadingValue != null) {
			ododReading =  odoReadingValue.longValue();
		}
		return ododReading;
	}
	
	//Added for Bug 16598
	public void updateInService(Long fmsId, Date inServiceDate) throws MalException{
		
		 	String error = "";				 	
			String stmt = "BEGIN quote_services.updateInService(?,?,?); END;";
			Query query = entityManager.createNativeQuery(stmt);
			query.setParameter(1, fmsId);			
			query.setParameter(2, inServiceDate);
			query.setParameter(3, error);		
       	    query.executeUpdate();
	}
	
	public void putUnitInService(String unitNo, Long contextId, Date dealerReceivedDate, Date inServiceDate,Long odoReading,
			 Date odoReadingDate, String odoReadingType,String userId) throws MalException{
		
		 	String error = "";	
		 	Integer clnId = new Integer(-1);	
		 	
			String stmt = "BEGIN quote_services.putUnitInService(?,?,?,?,?,?,?,?,?,?); END;";
			Query query = entityManager.createNativeQuery(stmt);
			query.setParameter(1, unitNo);
			query.setParameter(2, contextId);
			query.setParameter(3, dealerReceivedDate);
			query.setParameter(4, inServiceDate);
			query.setParameter(5, odoReading);
			query.setParameter(6, odoReadingDate);
			query.setParameter(7, odoReadingType);
			query.setParameter(8, userId);		
			query.setParameter(9, clnId);
			query.setParameter(10, error);
			
        	query.executeUpdate();
	}
		
	public String getGrdStatus(Long contextId, String accountType, String accountCode, Long celId, Long docId) {
		String status = "Y";
		
		String queryString = "SELECT willow2k.fl_grd.get_grd_status(?,?,?,?,?)  from dual ";
		
		Query query = entityManager.createNativeQuery(queryString);
		query.setParameter(1, contextId);
		query.setParameter(2, accountType);
		query.setParameter(3, accountCode);
		query.setParameter(4, celId);
		query.setParameter(5, docId);
		
		Object statusValue = (Object) query.getSingleResult();
		
		if(statusValue != null && statusValue instanceof String ) {
			String value =  (String) statusValue;
			return value;
		}
		
		return status;
	}
	
	@SuppressWarnings("unchecked")
	public Object[] getUpdatedSupllierProgressData(Long docId) {
		String queryString = "Select "
				+ "(Select action_date from supplier_progress_history where sph_id = (select max(sph_id) from supplier_progress_history sph1 where sph1.doc_id = d.doc_id and progress_type = '14_ETA')) as client_eta_date, "
				+ "(Select action_date from supplier_progress_history where sph_id = (select max(sph_id) from supplier_progress_history sph1 where sph1.doc_id = d.doc_id and progress_type = '15_VEHRDY')) as vehicle_ready_date, "
				+ "(Select action_date from supplier_progress_history where sph_id = (select max(sph_id) from supplier_progress_history sph1 where sph1.doc_id = d.doc_id and progress_type = '20_INVPROC')) as invoice_processed_date, "
				+ "(Select action_date from supplier_progress_history where sph_id = (select max(sph_id) from supplier_progress_history sph1 where sph1.doc_id = d.doc_id and progress_type = '08_SHIP1')) as factory_ship_date, "
				+ "(Select action_date from supplier_progress_history where sph_id = (select max(sph_id) from supplier_progress_history sph1 where sph1.doc_id = d.doc_id and progress_type = '09_INT_DLR')) as dealer_recvd_date, "
				+ "(Select action_date from supplier_progress_history where sph_id = (select max(sph_id) from supplier_progress_history sph1 where sph1.doc_id = d.doc_id and progress_type = '15_RECEIVD')) as dlr_recd_date, "
				+ "(Select action_date from supplier_progress_history where sph_id = (select max(sph_id) from supplier_progress_history sph1 where sph1.doc_id = d.doc_id and progress_type = '11_INT_DLR')) as interim_dealer_date, "
				+ "(Select action_date from supplier_progress_history where sph_id = (select max(sph_id) from supplier_progress_history sph1 where sph1.doc_id = d.doc_id and progress_type = '07_PROD')) as prod_date, "
				+ "(Select action_date from supplier_progress_history where sph_id = (select min(sph_id) from supplier_progress_history sph1 where sph1.doc_id = d.doc_id and progress_type = '25_EXPECTD')) as expected_date, "
				+ "(Select action_date from supplier_progress_history where sph_id = (select min(sph_id) from supplier_progress_history sph1 where sph1.doc_id = d.doc_id and progress_type = '15_DSMFGDV')) as desired_to_dealer "
				+ "from doc d where d.doc_id = :docId ";
		
		Query query = entityManager.createNativeQuery(queryString);
		query.setParameter("docId", docId);
		List<Object[]> resultList = (List<Object[]>) query.getResultList();
		
		if(resultList != null && resultList.size() >0 ) {
			return resultList.get(0);
		}
		return null;
	}
	@Override
	public String grnCreatedYn(Long fmsId) {
		String queryString = "SELECT 'Y'"
							+ "FROM dist di, docl dl, doc d "
							+ "WHERE di.cdb_code_1 =  TO_CHAR(:fmsId) "
							+ "AND di.docl_doc_id = dl.doc_id "
							+ "AND di.docl_line_id = dl.line_id "
							+ "AND dl.doc_id = d.doc_id "
							+ "AND dl.user_def4 = 'MODEL' "   
							+ "AND d.doc_type = 'STOCKRCPT' "
							+ "AND d.source_code = 'FLGRN'";
		
		try {
			Query query = entityManager.createNativeQuery(queryString);
			query.setParameter("fmsId", fmsId);

			Object returnValue = (Object) query.getSingleResult();

			if (returnValue != null) {
				String value = String.valueOf(returnValue);
				return value;
			} else {
				return "N";
			}
		} catch (NoResultException re) {
			return "N";
		}		
	}

	@Override
	public Long getMainPoDocIdByFmsId(Long fmsId) {
		String queryString = "SELECT d.doc_id "
							+ "FROM  doc d, docl dl, dist di, fleet_masters fms "
							+ "WHERE fms.fms_id = di.cdb_code_1 "
							+ "AND d.doc_type = 'PORDER' "
							+ "AND dl.user_def4 = 'MODEL' "
							+ "AND d.doc_id = dl.doc_id "  
							+ "AND dl.doc_id = di.docl_doc_id "
							+ "AND dl.line_id = di.docl_line_id "
							+ "AND NVL(d.order_type, 'X') != 'T' "
							+ "AND fms.fms_id = :fmsId";

		Query query = entityManager.createNativeQuery(queryString);
		query.setParameter("fmsId", fmsId);
			
		return ((BigDecimal) query.getSingleResult()).longValue();
	}
	
	
	@Override
	public boolean getItemToleranceFlag(Long docId, OrderToDeliveryProcessStageEnum processStage) throws MalException {
		boolean toleranceFlag;
		
		String stmt = "SELECT tolerance_yn "
				+ "        FROM {0} "
				+ "        WHERE doc_id = :docId";
		
		stmt = MessageFormat.format(stmt, processStage.getViewName());
		
		try{
		
			Query query = entityManager.createNativeQuery(stmt);
			query.setParameter("docId", docId);

			toleranceFlag = MALUtilities.convertYNToBoolean((String)query.getSingleResult());
		
		} catch(NoResultException nre) {
			logger.error(nre, "docId = " + docId + " viewName = " + processStage.getViewName());
			throw nre;
		}
		
		return toleranceFlag;
	}	
}
