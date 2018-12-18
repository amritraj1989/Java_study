package com.mikealbert.data.dao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.persistence.Query;
import javax.sql.DataSource;

import com.mikealbert.data.entity.Doc;
import com.mikealbert.data.enumeration.ReportNameEnum;
import com.mikealbert.data.vo.ReportContactVO;
import com.mikealbert.exception.MalException;
import com.mikealbert.util.MALUtilities;

/**
 * DAO implementation for PurchaseOrderReport
 * 
 * @author ravresh
 */

public class PurchaseOrderReportDAOImpl extends GenericDAOImpl<Doc, Long> implements PurchaseOrderReportDAOCustom {

	private static final long serialVersionUID = -4029055333153724372L;
	
	@Resource DataSource dataSource;

	@SuppressWarnings("unchecked")
	@Override
	public List<Object[]> getPurchaseOrderDetailForReport(Long docId, String stockYn) {
		StringBuilder queryStr = new StringBuilder("Select");
		if (stockYn.equals("N")) {
			queryStr.append(
					" d.posted_date, d.doc_no doc_no, d.rev_no rev_no, d.sub_acc_code as deliveringDealerCode, d.c_id doc_context, ea.account_name, ea.account_code,");
			queryStr.append(
					" (SELECT vd.agreement_no   FROM vrb_discounts vd  WHERE vrb_type = 'V'   AND mdl_mdl_id = mdl.mdl_id    AND ea_c_id = ea.c_id   AND ea_account_type = ea.account_type AND ea_account_code = ea.account_code   AND SYSDATE BETWEEN start_date AND NVL (end_date, SYSDATE) "
							+ " AND start_date = (SELECT MAX (start_date) "
							+ " FROM vrb_discounts vd1   WHERE vd1.vrb_type = vd.vrb_type  AND vd1.mdl_mdl_id = vd.mdl_mdl_id  AND vd1.ea_c_id = vd.ea_c_id  "
							+ " AND vd1.ea_account_type = vd.ea_account_type AND vd1.ea_account_code = vd.ea_account_code AND SYSDATE BETWEEN vd1.start_date AND NVL (vd1.end_date, SYSDATE)))  vrb_agreement_no, ");
			queryStr.append(
					" (SELECT  LISTAGG(agreement_no, ', ') WITHIN GROUP (ORDER BY agreement_no)  from make_agreements where mak_mak_id = mdl.mak_mak_id ) incentive_prg ,");
			queryStr.append(" fms.vehicle_cost_centre,  fms.unit_no, d.external_ref_no, fms.vin, "
					+ "(SELECT action_date FROM supplier_progress_history WHERE sph_id = (SELECT min(sph_id) FROM supplier_progress_history sph1 WHERE sph1.doc_id = d.doc_id AND progress_type = '15_DSMFGDV')) as desired_arrrival, ");
			queryStr.append(
					" (SELECT finfan_number FROM ext_acc_fin_fan eaff WHERE eaff.c_id= ea.c_id AND eaff.account_type= ea.account_type AND eaff.account_code= ea.account_code AND eaff.mak_id = mdl.mak_mak_id) fin_fan_number,");
			queryStr.append(
					" DECODE(qmd.client_request_type, 'AS', 'ASAP', 'NS', 'Not Specified', 'OD', TO_CHAR(qmd.required_date, 'mm/dd/rrrr'), qmd.client_request_type) reqd_by, ");
			queryStr.append(
					" (SELECT ea1.account_name FROM external_accounts ea1 WHERE ea1.c_id= d.ea_c_id AND ea1.account_type= d.account_type AND ea1.account_code= d.account_code AND rownum =1) vendor_name, ");
			queryStr.append(" qmd.qmd_id,qmd.order_type,d.update_control_code product_code, quo.drv_drv_id,");
			queryStr.append(
					" (SELECT fms1.fms_id from fleet_masters fms1 where fms_id = qmd.replacement_for_fms_id) replacement_fms_id,");
			queryStr.append(" mdl.mdl_id, mdl.model_desc, cc.description, trc.trim_desc,");
			queryStr.append(
					" (SELECT end_date FROM contract_lines WHERE cln_id = fl_contract.get_contract_line (qmd.replacement_for_fms_id, TRUNC (SYSDATE))) lease_expiration_date, ");
			
			queryStr.append(
					" (SELECT prd.product_type FROM quotations quo,  quotation_models qmd1, quotation_profiles qpr,   products prd, product_type_codes ptc WHERE qmd1.qmd_id  = qmd.qmd_id "
					+ " AND qmd1.quo_quo_id = quo.quo_id   AND quo.qpr_qpr_id = qpr.qpr_id  AND qpr.prd_product_code = prd.product_code  AND prd.product_type = ptc.product_type) lease_type ");
			
			queryStr.append(
					" FROM doc d, quotation_models qmd, quotation_notes qn, quotations quo, external_accounts ea, fleet_masters fms, models mdl, colour_codes cc, trim_codes trc");
			queryStr.append(" WHERE d.generic_ext_id = qmd.qmd_id ");
			queryStr.append(" AND qmd.unit_no = fms.unit_no ");
			queryStr.append(" AND qmd.quo_quo_id = quo.quo_id ");
			queryStr.append(" AND qmd.qmd_id = qn.qmd_qmd_id(+) ");
			queryStr.append(" AND quo.c_id = ea.c_id ");
			queryStr.append(" AND quo.account_type = ea.account_type ");
			queryStr.append(" AND quo.account_code = ea.account_code ");
			queryStr.append(" AND qmd.mdl_mdl_id = mdl.mdl_id ");
			queryStr.append(" AND qmd.colour_code = cc.colour_code ");
			queryStr.append(" AND qmd.trc_trc_id = trc.trc_id ");
		} else {
			queryStr.append(
					" d.posted_date, d.doc_no doc_no ,d.rev_no rev_no, d.sub_acc_code as deliveringDealerCode, d.c_id doc_context, null account_name, null account_code, null vrb_agreement_no, ");
			queryStr.append(
					" (SELECT  LISTAGG(agreement_no, ', ') WITHIN GROUP (ORDER BY agreement_no)  from make_agreements where mak_mak_id = mdl.mak_mak_id ) incentive_prg, ");
			queryStr.append(" fms.vehicle_cost_centre, fms.unit_no, d.external_ref_no, fms.vin, "
					+ " (SELECT action_date FROM supplier_progress_history WHERE sph_id = (SELECT min(sph_id) FROM supplier_progress_history sph1 WHERE sph1.doc_id = d.doc_id AND progress_type = '15_DSMFGDV')) desired_arrrival,"
					+ " null fin_fan_number, null reqd_by,");
			queryStr.append(
					" (SELECT ea1.account_name FROM external_accounts ea1 WHERE ea1.c_id= d.ea_c_id AND ea1.account_type= d.account_type AND ea1.account_code= d.account_code AND rownum =1) vendor_name,");
			queryStr.append(
					" null qmd_id, null order_type, d.update_control_code product_code, null drv_id, null replacement_fms_id, ");
			queryStr.append(" mdl.mdl_id, mdl.model_desc, cc.description, trc.trim_desc, null lease_expiration_date, null lease_type ");
			queryStr.append(
					" FROM doc d,docl dl, dist ds, fleet_masters fms, models mdl, colour_codes cc, trim_codes trc");
			queryStr.append(" WHERE d.doc_id = dl.doc_id");
			queryStr.append(" AND dl.doc_id = ds.docl_doc_id");
			queryStr.append(" AND dl.line_id = ds.docl_line_id");
			queryStr.append(" AND ds.cdb_code_1 = to_char(fms.fms_id)");
			queryStr.append(" AND fms.mdl_mdl_id = mdl.mdl_id");
			queryStr.append(" AND fms.colour_code = cc.colour_code");
			queryStr.append(" AND fms.trim_colour = trc.trim_code");
			queryStr.append(" AND d.generic_ext_id is null");
			queryStr.append(" AND dl.user_def4 = 'MODEL'");
		}

		queryStr.append(" AND d.doc_id = :docId ");

		Query query = entityManager.createNativeQuery(queryStr.toString());
		query.setParameter("docId", docId);

		List<Object[]> poDetails = query.getResultList();

		return poDetails;
	}

	public Date getFirstPOPostedDate(String docNo) {

		String sql = "SELECT d1.posted_date  from DOC d1 where d1.doc_no =:docNo "
				+ " and d1.rev_no in (select min(d2.rev_no) from doc d2  where d2.doc_no =:docNo )";

		Query query = entityManager.createNativeQuery(sql);
		query.setParameter("docNo", docNo);

		return (Date) query.getSingleResult();
	}

	@SuppressWarnings("unchecked")
	public List<Object[]> getAccountAddressInfo(String accountCode) throws MalException {
		String queryString = "Select eal.account_name, eal.address_line_1, eal.address_line_2, tcc.town_description, eal.region, eal.postcode, ea.telephone_number, ea.email from external_account_lov eal "
				+ "LEFT JOIN external_accounts ea ON eal.c_id = ea.c_id AND eal.account_type = ea.account_type AND eal.account_code = ea.account_code "
				+ "LEFT JOIN town_city_codes tcc ON eal.country_code = tcc.country_code AND eal.region = tcc.region_code AND eal.county_code = tcc.county_code AND eal.town_city = tcc.town_name "
				+ "where eal.account_code = :accountCode";

		Query query = entityManager.createNativeQuery(queryString);
		query.setParameter("accountCode", accountCode);

		List<Object[]> resultList = (List<Object[]>) query.getResultList();

		return resultList;
	}

	@SuppressWarnings("unchecked")
	public List<Object[]> getVendorInfo(Long parentDocId) throws MalException {
		String queryString = "Select d.doc_id, eal.account_code, eal.account_name, eal.address_line_1, eal.address_line_2, tcc.town_description, eal.region, eal.postcode,  po_mgr.get_po_lead_time(d.doc_id) lead_time , ea.telephone_number, ea.email,"
				+ " (select po_mgr.get_vendor_eta(dlk.child_doc_id) from dual) eta "
				+ " FROM Doc d, doc_links dlk, external_accounts ea, external_account_lov eal, town_city_codes tcc "
				+ " WHERE d.doc_Id = dlk.child_doc_id "
				+ " and d.ea_c_id = ea.c_id and d.account_type =  ea.account_type and d.account_code = ea.account_code "
				+ " AND eal.c_id = ea.c_id AND eal.account_type = ea.account_type AND eal.account_code = ea.account_code "
				+ " AND eal.country_code = tcc.country_code AND eal.region = tcc.region_code AND eal.county_code = tcc.county_code AND eal.town_city = tcc.town_name "
				+ " and d.doc_status <> 'C'" //Added for HD-504 Saket We do not want to display Cancelled PO's on the documents
				+ " and dlk.parent_doc_Id = :parentDocId";

		Query query = entityManager.createNativeQuery(queryString);
		query.setParameter("parentDocId", parentDocId);

		List<Object[]> resultList = (List<Object[]>) query.getResultList();

		return resultList;
	}

	@SuppressWarnings("unchecked")
	public List<Object[]> getVendorInfo(Long parentDocId , Long qmdId) throws MalException {
		String queryString = "Select d.doc_id, eal.account_code, eal.account_name, eal.address_line_1, eal.address_line_2, tcc.town_description, eal.region, eal.postcode,  po_mgr.get_po_lead_time(d.doc_id) lead_time , ea.telephone_number, ea.email,"
				+ " (select po_mgr.get_vendor_eta(dlk.child_doc_id) from dual) eta "
				+ " FROM Doc d, doc_links dlk, external_accounts ea, external_account_lov eal, town_city_codes tcc "
				+ " WHERE d.doc_Id = dlk.child_doc_id "
				+ " AND d.generic_ext_id =  :qmdId "
				+ " AND d.ea_c_id = ea.c_id and d.account_type =  ea.account_type and d.account_code = ea.account_code "
				+ " AND eal.c_id = ea.c_id AND eal.account_type = ea.account_type AND eal.account_code = ea.account_code "
				+ " AND eal.country_code = tcc.country_code AND eal.region = tcc.region_code AND eal.county_code = tcc.county_code AND eal.town_city = tcc.town_name "
				+ " and d.doc_status <> 'C'" //Added for HD-504 Saket We do not want to display Cancelled PO's on the documents
				+ " AND dlk.parent_doc_Id = :parentDocId";

		Query query = entityManager.createNativeQuery(queryString);	
		query.setParameter("qmdId", qmdId);
		query.setParameter("parentDocId", parentDocId);
		List<Object[]> resultList = (List<Object[]>) query.getResultList();

		return resultList;
	}
	
	@SuppressWarnings("unchecked")
	public List<Object[]> getDealerContactDetailsList(Long cId, String accountType, String accountCode)
			throws MalException {

		String queryString = "Select ss.sst_id, ss.staff_name, " + "ss.qualifications as \"sup_phone\", "
				+ "(Select e_mail from contacts where sup_sup_id = ss.sup_sup_id and contact_type = 'STOCK' and default_ind = 'Y') as \"sup_contact_email\", "
				+ "sup.email_address as \"sup_email\" " + "from suppliers sup, supplier_staff ss "
				+ "where ss.sup_sup_id = sup.sup_id " + "and sup.ea_c_id = :cId "
				+ "and sup.ea_account_type = :accountType " + "and sup.ea_account_code = :accountCode "
				+ "and sup.sstc_service_type_code = 'DEALER' " + "and UPPER(ss.staff_position) like 'STOCK%' "
				+ "order by ss.sst_id desc";

		Query query = entityManager.createNativeQuery(queryString);
		query.setParameter("cId", cId);
		query.setParameter("accountType", accountType);
		query.setParameter("accountCode", accountCode);

		List<Object[]> resultList = (List<Object[]>) query.getResultList();

		return resultList;
	}

	@SuppressWarnings("unchecked")
	public List<Object[]> getVendorContactDetailsList(Long cId, String accountType, String accountCode)
			throws MalException {

		String queryString = "Select ss.sst_id, ss.staff_name, " + "ss.qualifications as \"sup_phone\", "
				+ "(Select e_mail from contacts where sup_sup_id = ss.sup_sup_id and contact_type = 'ORDERING' and default_ind = 'Y') as \"sup_contact_email\", "
				+ "sup.email_address as \"sup_email\" " + "from suppliers sup, supplier_staff ss "
				+ "where ss.sup_sup_id = sup.sup_id " + "and sup.ea_c_id = :cId "
				+ "and sup.ea_account_type = :accountType " + "and sup.ea_account_code = :accountCode "
				+ "and sup.sstc_service_type_code = 'DEALER' " + "and UPPER(ss.staff_position) like  'ORDERING%' "
				+ "order by ss.sst_id desc";

		Query query = entityManager.createNativeQuery(queryString);
		query.setParameter("cId", cId);
		query.setParameter("accountType", accountType);
		query.setParameter("accountCode", accountCode);

		List<Object[]> resultList = (List<Object[]>) query.getResultList();

		return resultList;
	}

	@SuppressWarnings("unchecked")
	public List<Object> getTypedInstalledAccessories(Long qmdId, String categoryCode) throws MalException {

		String queryString = "select DISTINCT description from dealer_price_lists dpl, dealer_accessories dac, dealer_accessory_codes assc "
				+ " where dac_id in (SELECT dac_dac_id from quotation_dealer_accessories where qmd_qmd_id =:qmdId )"
				+ " and category_code =:categoryCode " + " and dpl.dac_dac_id = dac.dac_id"
				+ " and dac.accessory_code = assc.accessory_code " + " order by description ";

		Query query = entityManager.createNativeQuery(queryString);
		query.setParameter("qmdId", qmdId);
		query.setParameter("categoryCode", categoryCode);

		List<Object> resultList = (List<Object>) query.getResultList();

		return resultList;
	}

	@SuppressWarnings("unchecked")
	public List<Object> getTypedInstalledAccessoriesPO(Long docId, String accessoryType, String categoryCode)
			throws MalException {
		String queryString = "SELECT assc.description FROM dealer_accessories dac, dealer_accessory_codes assc "
				+ "WHERE dac.dac_id in(SELECT dl.generic_ext_id FROM doc d, docl dl "
				+ "			WHERE d.doc_id = dl.doc_id " + "			AND dl.generic_ext_id IS NOT NULL "
				+ "			AND dl.user_def4 = :accessoryType " + "			AND d.doc_id = :docId) "
				+ "AND dac.accessory_code = assc.accessory_code " + "AND category_code = :categoryCode ";

		Query query = entityManager.createNativeQuery(queryString);
		query.setParameter("accessoryType", accessoryType);
		query.setParameter("docId", docId);
		query.setParameter("categoryCode", categoryCode);

		List<Object> resultList = (List<Object>) query.getResultList();
		return resultList;
	}

	@SuppressWarnings("unchecked")
	public List<Object> getStandardAccessories(Long qmdId) throws MalException {
		String queryString = "SELECT assc.description FROM quotation_standard_accessories qsa, standard_accessories sac, standard_accessory_codes assc "
				+ " WHERE qsa.sac_sac_id = sac.sac_id " + " AND sac.sacc_sacc_id =  assc.sacc_id "
				+ " AND qsa.qmd_qmd_id = :qmdId  order by assc.description";

		Query query = entityManager.createNativeQuery(queryString);
		query.setParameter("qmdId", qmdId);

		List<Object> resultList = (List<Object>) query.getResultList();
		return resultList;
	}

	@SuppressWarnings("unchecked")
	public List<Object> getModelAccessories(Long qmdId) throws MalException {
		String queryString = "SELECT assc.description  FROM accessory_codes assc, optional_accessories oac, quotation_model_accessories qma "
				+ " WHERE assc.assc_id = oac.assc_assc_id   AND qma.oac_oac_id = oac.oac_id AND qma.qmd_qmd_id= :qmdId  order by assc.description";

		Query query = entityManager.createNativeQuery(queryString);
		query.setParameter("qmdId", qmdId);

		List<Object> resultList = (List<Object>) query.getResultList();
		return resultList;
	}

	@SuppressWarnings("unchecked")
	public List<Object> getDealerAccessories(Long qmdId) throws MalException {
		String queryString = "SELECT dacc.description FROM dealer_accessory_codes dacc, dealer_accessories dac, quotation_dealer_accessories qda "
				+ " WHERE dac.accessory_code = dacc.accessory_code  AND qda.dac_dac_id = dac.dac_id  AND qda.qmd_qmd_id  = :qmdId  order by dacc.description";

		Query query = entityManager.createNativeQuery(queryString);
		query.setParameter("qmdId", qmdId);

		List<Object> resultList = (List<Object>) query.getResultList();
		return resultList;
	}

	public Long getMainDocIdForNonStock(Long thpDocId) throws MalException {
		String queryString = "SELECT parent_doc_id from doc_links where child_doc_id = :thpDocId";
		Query query = entityManager.createNativeQuery(queryString);
		query.setParameter("thpDocId", thpDocId);

		return ((BigDecimal) query.getSingleResult()).longValue();
	}

	@SuppressWarnings("unchecked")
	public List<Object> getDealerAccessoriesForPO(Long docId) throws MalException {
		String queryString = "SELECT assc.description FROM dealer_accessories dac, dealer_accessory_codes assc "
				+ " WHERE dac.dac_id in(SELECT dl.generic_ext_id FROM docl dl "
				+ "			WHERE dl.generic_ext_id IS NOT NULL " + "			AND dl.user_def4 in ('DEALER')  "
				+ "		    AND dac.accessory_code = assc.accessory_code "
				+ "			AND dl.doc_id =:docId ) order by assc.description";

		Query query = entityManager.createNativeQuery(queryString);
		query.setParameter("docId", docId);

		List<Object> resultList = (List<Object>) query.getResultList();
		return resultList;
	}

	@SuppressWarnings("unchecked")
	public List<Object[]> getThirdPartyPODetails(Long tptDocId) throws MalException {
		String queryString = "SELECT d.posted_date, d.doc_no||'/'||d.rev_no doc_no, ea.c_id ,ea.account_type  ,ea.account_name, d.total_doc_cost "
				+ " from doc d , external_accounts ea "
				+ " where d.ea_c_id = ea.c_id AND d.account_type = ea.account_type AND d.account_code = ea.account_code AND d.doc_id  = :tptDocId";

		Query query = entityManager.createNativeQuery(queryString);
		query.setParameter("tptDocId", tptDocId);

		List<Object[]> resultList = (List<Object[]>) query.getResultList();

		return resultList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Object> getVendorQuoteNumbers(Long qmdId, Long tptDocId) throws MalException {
		String queryString = "Select DISTINCT qda.external_reference_no "
				+ " from quotation_dealer_accessories qda, dealer_accessories da, dealer_accessory_codes dac "
				+ " where da.accessory_code = dac.accessory_code " + " and qda.external_reference_no is not null "
				+ " and qda.dac_dac_id = da.dac_id " + " and qda.qmd_qmd_id = :qmdId "
				+ " and qda.dac_dac_id in(Select generic_ext_id from docl where doc_id = :tptDocId)"
				+ " order by qda.external_reference_no";

		Query query = entityManager.createNativeQuery(queryString);
		query.setParameter("qmdId", qmdId);
		query.setParameter("tptDocId", tptDocId);

		List<Object> resultList = (List<Object>) query.getResultList();

		return resultList;
	}

	@SuppressWarnings("unchecked")
	public List<Object> getPowertrainInfo(Long qmdId) throws MalException {
		String queryString = "Select DISTINCT assc.description "
				+ " FROM optional_accessories oac,  accessory_codes assc ,acc_cat_code_list acc, opt_acc_cat opt "
				+ " WHERE acc.cat_code = opt.opt_acc_cat_code " + " AND oac.assc_assc_id = acc.assc_assc_id "
				+ " AND oac.assc_assc_id = assc.assc_id "
				+ " AND exists (SELECT config_value from willow_config where config_name ='POWERTRAIN_ACCESS_CODES' AND config_value like '% '||opt.opt_acc_cat_code||', %')  "
				+ " AND oac.oac_id in (SELECT qma.oac_oac_id FROM  quotation_model_accessories qma where qma.qmd_qmd_id =:qmdId) "
				+ " UNION " + " select DISTINCT assc.description "
				+ " from standard_accessories sac, standard_accessory_cat sacc, opt_acc_cat opt, standard_accessory_codes assc "
				+ " where sacc.cat_code = opt.opt_acc_cat_code " + " and sac.sacc_sacc_id = sacc.sacc_sacc_id "
				+ " and sac.sacc_sacc_id = assc.sacc_id "
				+ " AND exists (SELECT config_value from willow_config where config_name ='POWERTRAIN_ACCESS_CODES' AND config_value like '% '||opt.opt_acc_cat_code||', %')  "
				+ " AND sac.sac_id in (SELECT qsa.sac_sac_id FROM  quotation_standard_accessories qsa where qsa.qmd_qmd_id  =:qmdId) ";

		Query query = entityManager.createNativeQuery(queryString);
		query.setParameter("qmdId", qmdId);

		List<Object> resultList = (List<Object>) query.getResultList();

		return resultList;
	}

	// This would be needed for stock order because stock will have only doc and
	// no standard accessory
	@SuppressWarnings("unchecked")
	public List<Object> getPowertrainInfoForDoc(Long mainPODocId) throws MalException {

		String queryString = "Select DISTINCT assc.description "
				+ " FROM optional_accessories oac,  accessory_codes assc ,acc_cat_code_list acc, opt_acc_cat opt "
				+ " WHERE acc.cat_code = opt.opt_acc_cat_code " + " AND oac.assc_assc_id = acc.assc_assc_id "
				+ " AND oac.assc_assc_id = assc.assc_id "
				+ " AND exists (SELECT config_value from willow_config where config_name ='POWERTRAIN_ACCESS_CODES' AND config_value like '% '||opt.opt_acc_cat_code||', %')  "
				+ " AND oac.oac_id in (  " + " 	SELECT dl.generic_ext_id FROM docl dl  "
				+ " 		WHERE dl.generic_ext_id IS NOT NULL  " + " 			AND dl.user_def4 in ('FACTORY')   "
				+ " 			AND dl.doc_id =:mainPODocId  )";

		Query query = entityManager.createNativeQuery(queryString);
		query.setParameter("mainPODocId", mainPODocId);

		List<Object> resultList = (List<Object>) query.getResultList();

		return resultList;
	}

	@Override
	public String[] getReportEmailRecipients(ReportNameEnum reportName, Long cId, String accountType,
			String accountCode, Long fmsId, Long qmdId) {
		String emailAddressesString;
		String sql;
		Query query;
		String[] emaillAddresses = null;

		sql = "SELECT report_contact_wrapper.getEmailRecipients(:reportName, :cId, :accountType, :accountCode, :callFrom, :fmsId, :qmdId) FROM DUAL";

		query = entityManager.createNativeQuery(sql);
		query.setParameter("reportName", reportName.getModuleName());
		query.setParameter("cId", cId);
		query.setParameter("accountType", accountType);
		query.setParameter("accountCode", accountCode);
		query.setParameter("callFrom", "MAL_EMAIL");
		query.setParameter("fmsId", 0L);
		query.setParameter("qmdId", qmdId);

		emailAddressesString = (String) query.getSingleResult();
		if (!MALUtilities.isEmpty(emailAddressesString)) {
			emaillAddresses = emailAddressesString.split(";");
		}
		return emaillAddresses;
	}

	@Override
	public List<ReportContactVO> getReportEmailContacts(ReportNameEnum reportName, Long cId, String accountType,
			String accountCode, Long fmsId, Long qmdId) {
		
		String sql;
		Query query;
		List<ReportContactVO> reportContacts = new ArrayList<ReportContactVO>();		

		sql = "SELECT * from table(report_contact_wrapper.getEmailReportContacts(:reportName, :cId, :accountType, :accountCode, :callFrom, :fmsId, :qmdId))";

		query = entityManager.createNativeQuery(sql);
		query.setParameter("reportName", reportName.getModuleName());
		query.setParameter("cId", cId);
		query.setParameter("accountType", accountType);
		query.setParameter("accountCode", accountCode);
		query.setParameter("callFrom", "MAL_EMAIL");
		query.setParameter("fmsId", 0L);
		query.setParameter("qmdId", qmdId);

		List<Object[]>resultList = (List<Object[]>)query.getResultList();
		if(resultList != null){
			for(Object[] record : resultList){
				int i = 0;
				
				ReportContactVO reportContact = new ReportContactVO();
				reportContact.setCntId(MALUtilities.isEmpty(record[i]) ? null : ((BigDecimal)record[i]).longValue());
				reportContact.setDrvId(MALUtilities.isEmpty(record[i+=1]) ? null : ((BigDecimal)record[i]).longValue());
				reportContact.setFirstName((String)record[i+=1]);
				reportContact.setLastName((String)record[i+=1]);
				reportContact.setContactType((String)record[i+=1]);
				reportContact.setCorporateContactType((String)record[i+=1]);
				reportContact.setDeliveryMethod((String)record[i+=1]);
				reportContact.setEmailAddres((String)record[i+=1]);
				reportContact.setFaxNumber((String)record[i+=1]);	
				
				reportContacts.add(reportContact);								
			}
		}

		return reportContacts;
	}
	
	@Override
	public String getReportEmailSender(ReportNameEnum reportName) {
		String emailSender;
		String sql;
		Query query;

		sql = "SELECT report_contact_wrapper.getEmailSender(:reportName) FROM DUAL";

		query = entityManager.createNativeQuery(sql);
		query.setParameter("reportName", reportName.getModuleName());

		emailSender = (String) query.getSingleResult();

		return emailSender;
	}

	/**
	 * Moved method to ClientContactDAOImpl
	 */
	@Deprecated	
	@Override
	public String getReportEmailSubject(ReportNameEnum reportName) {
		String emailSubject;
		String sql;
		Query query;

		sql = "SELECT report_contact_wrapper.getEmailSubject(:reportName) FROM DUAL";

		query = entityManager.createNativeQuery(sql);
		query.setParameter("reportName", reportName.getModuleName());

		emailSubject = (String) query.getSingleResult();

		return emailSubject;
	}

	/**
	 * Moved method to ClientContactDAOImpl
	 */
	@Deprecated
	@Override
	public String getReportEmailBody(ReportNameEnum reportName) {
		String emailBody;
		String sql;
		Query query;

		sql = "SELECT report_contact_wrapper.getEmailBody(:reportName) FROM DUAL";

		query = entityManager.createNativeQuery(sql);
		query.setParameter("reportName", reportName.getModuleName());

		emailBody = (String) query.getSingleResult();

		return emailBody;
	}

	@SuppressWarnings("unchecked")
	public String getReportOverridenLogoContext(String productCode) {

		String sql = "SELECT ica_c_id  FROM products prd,  company_logos cl  WHERE product_code =:productCode  AND prd.ica_c_id = cl.c_id   AND NVL(cl.default_logo, 'N') = 'Y'";

		Query query = entityManager.createNativeQuery(sql);
		query.setParameter("productCode", productCode);

		List<Object> resultList = (List<Object>) query.getResultList();

		String context = null;
		if (resultList != null && resultList.size() > 0) {
			context = String.valueOf((BigDecimal) resultList.get(0));
		}

		return context;
	}

	public String getDocNarratives(Long docId, String noteType) throws MalException {

		String sql = "SELECT willow2k.fl_po.get_doc_narratives(:docId, :noteType) from dual";
		Query query = entityManager.createNativeQuery(sql);
		query.setParameter("docId", docId);
		query.setParameter("noteType", noteType);

		return (String) query.getSingleResult();
	}

	public String getDoclNarratives(Long docId, Long lineId) throws MalException {

		String sql = "SELECT willow2k.fl_po.get_docl_narratives(:docId, :lineId) from dual";
		Query query = entityManager.createNativeQuery(sql);
		query.setParameter("docId", docId);
		query.setParameter("lineId", lineId);

		return (String) query.getSingleResult();
	}

	@Override // should return true for TO, LX and SC make configured in
				// DLR_TO_PORT_INST_MAKES
	public boolean isPostProductionIsPortInstalledForMake(Long mdlId) {

		String sql = "SELECT mk.make_code from makes mk , models mdl " + " WHERE  mdl.mak_mak_id = mk.mak_id "
				+ " AND mdl.mdl_id =:mdlId "
				+ " AND exists (SELECT config_value from willow_config where config_name ='DLR_TO_PORT_INST_MAKES' AND config_value like '%'||mk.make_code||', %')";

		Query query = entityManager.createNativeQuery(sql);
		query.setParameter("mdlId", mdlId);
		@SuppressWarnings("unchecked")
		List<String> qryResultList = (List<String>) query.getResultList();

		return qryResultList == null ? false : qryResultList.size() > 0;
	}

	public String getReportText(Long inQmdId, String inReportName, String inTextLocation) {
		String sqlQuery = "SELECT report_language_wrapper.get_report_text(:inQmdId, :inReportName, :inTextLocation) FROM DUAL";
		
		Query query = entityManager.createNativeQuery(sqlQuery);
		query.setParameter("inQmdId", inQmdId);
		query.setParameter("inReportName", inReportName);
		query.setParameter("inTextLocation", inTextLocation);
		return (String) query.getSingleResult();
	}

	@Override
	public String getBailmentIndicatorByQmdIdAndAccountInfo(Long qmdId, String accountCode, String accountType, Long cId) {
		
		String  bailmentIndicator  = "N";
		String sqlQuery = "select ufp.bailment_yn from upfitter_progress ufp "
				+ " where ufp.qmd_qmd_id = :qmdId "
				+ " and ufp.ea_account_code = :accountCode "
				+ " and ufp.ea_account_type = :accountType "
				+ " and ufp.ea_c_id = :cId ";
		
		Query query = entityManager.createNativeQuery(sqlQuery);
		query.setParameter("qmdId", qmdId);
		query.setParameter("accountCode", accountCode);
		query.setParameter("accountType", accountType);
		query.setParameter("cId", cId);
		
		@SuppressWarnings("unchecked")
		List<String> list =  (List<String>) query.getResultList();
		if(list != null && list.size() == 1 ){
			bailmentIndicator  = list.get(0);
		}
		
		return bailmentIndicator;
	}


}
