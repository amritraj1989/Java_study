package com.mikealbert.data.dao;

import java.util.List;

import javax.persistence.Query;

import com.mikealbert.data.entity.ThirdPartyPoQueueV;
import com.mikealbert.exception.MalException;

public class ThirdPartyPoQueueDAOImpl extends GenericDAOImpl<ThirdPartyPoQueueV, Long> implements ThirdPartyPoQueueDAOCustom {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4971420360448887242L;

	@SuppressWarnings("unchecked")
	public List<Object[]> getThirdPartyUpfitList() throws MalException {
		String queryString = "SELECT * "
				+"               FROM ( (SELECT d.generic_ext_id qmd_id, "
				+"                               ea.account_name vendor_name, ea.account_code vendor_code, ea.c_id vendor_ctx, "
				+"                               d.doc_id doc_id, "
				+"                               DECODE(NVL(d.order_type, 'M'), 'M', fl_supp_prog.get_ea_upfit_days_main_po(d.doc_id), fl_supp_prog.get_ea_upfit_days(d.doc_id)) lead_time, "
				+"                               DECODE(NVL(d.order_type, 'M'), 'M', 'M', 'T', 'T', 'X') order_type, "
				+"                               DECODE(NVL(to_char(ufp.end_date, 'MM/DD/YYYY'), 'N'), 'N', 'N', 'Y') task_completed, "
				+"                               DECODE(d.source_code, 'FLGRD', 'Y', 'N') stock_yn, "
				+"                               DECODE(ufp.ufp_ufp_id, NULL, 'N', 'Y') linked,  "				
				+"                               NVL(ufp.sequence_no, 999999) sequence_no "
				+"                           FROM doc d, upfitter_progress ufp, external_accounts ea, quotation_models qmd "
				+"                           WHERE qmd.quote_status IN ('3', '16', '17') "
				+"                               AND d.generic_ext_id = qmd.qmd_id "
				+"                               AND d.doc_type = 'PORDER' "
				+"                               AND nvl(d.order_type, 'M') IN ('T', 'M') "
				+"                               AND ( (nvl(d.order_type, 'M') = 'M' AND d.doc_status = 'O') OR (nvl(d.order_type, 'M') = 'T' AND d.doc_status = 'O') ) "
				+"                               AND d.source_code IN ('FLQUOTE', 'FLGRD', 'FLORDER') "
				+"                               AND d.c_id = ea.c_id "
				+"                               AND d.account_code = ea.account_code "
				+"                               AND d.account_type = ea.account_type "
				+"                               AND d.generic_ext_id = ufp.qmd_qmd_id (+) "
				+"                               AND d.c_id = ufp.ea_c_id (+) "
				+"                               AND d.account_code = ufp.ea_account_code (+) "
				+"                               AND d.account_type = ufp.ea_account_type  (+) "				
				+"                               AND EXISTS (SELECT 1 "
				+"                                               FROM docl dl "
				+"                                               WHERE dl.doc_id = d.doc_id "
				+"                                                   AND dl.user_def4 = 'DEALER')  ) )";
		
		Query query = entityManager.createNativeQuery(queryString);
		List<Object[]>resultList = (List<Object[]>) query.getResultList();
				
		return resultList;
	}
}
