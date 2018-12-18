package com.mikealbert.data.dao;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.persistence.Query;
import javax.sql.DataSource;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.jdbc.datasource.DataSourceUtils;

import com.mikealbert.data.DataConstants;
import com.mikealbert.data.DataUtilities;
import com.mikealbert.data.entity.ExternalAccount;
import com.mikealbert.data.entity.ExternalAccountPK;
import com.mikealbert.data.entity.QuotationProfile;
import com.mikealbert.data.entity.QuoteRequest;
import com.mikealbert.data.enumeration.AccountCreditStatusEnum;
import com.mikealbert.data.vo.ClientConsultantsVO;
import com.mikealbert.data.vo.ClientCreditLimitsVO;
import com.mikealbert.data.vo.ClientFleetCodeVO;
import com.mikealbert.data.vo.ClientPocVO;
import com.mikealbert.data.vo.ClientPrefSupplierVO;
import com.mikealbert.data.vo.ClientQuoteRequestServiceElementVO;
import com.mikealbert.data.vo.QuoteRequestClientProfilesVO;
import com.mikealbert.data.vo.QuoteRequestQuoteModelVO;
import com.mikealbert.data.vo.QuoteRequestSearchCriteriaVO;
import com.mikealbert.data.vo.QuoteRequestSearchResultVO;
import com.mikealbert.util.MALUtilities;

public class QuoteRequestDAOImpl extends GenericDAOImpl<QuoteRequest, Long> implements QuoteRequestDAOCustom {

	@Resource
	private QuoteRequestDAO quoteRequestDAO;
	/**
	 * 
	 */
	@Resource DataSource dataSource;
	
	private static final long serialVersionUID = 8249709217532451721L;

	@Override
	public List<QuoteRequestSearchResultVO> findQuoteRequests(QuoteRequestSearchCriteriaVO searchCriteria, Pageable pageable, Sort sort){
		List<QuoteRequestSearchResultVO> quoteRequestSearchResultVOs = null;
		Query query = null;

		query = generateQuoteRequestsQuery(searchCriteria, sort, false);
		
		if(pageable != null){
			query.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
			query.setMaxResults(pageable.getPageSize());
		}
		
		@SuppressWarnings("unchecked")
		List<Object[]>resultList = (List<Object[]>)query.getResultList();
		if(resultList != null){
			quoteRequestSearchResultVOs = new ArrayList<QuoteRequestSearchResultVO>();
			
			for(Object[] record : resultList){
				int i = 0;
				
				QuoteRequestSearchResultVO quoteRequestVO = new QuoteRequestSearchResultVO();
				
				quoteRequestVO.setQrqId(((BigDecimal)record[i]).longValue());	
				quoteRequestVO.setRequestType((String)record[i+=1]);					
				quoteRequestVO.setClientAccountName((String)record[i+=1]);
				quoteRequestVO.setClientAccountCode((String)record[i+=1]);
				quoteRequestVO.setClientAccountType((String)record[i+=1]);
				quoteRequestVO.setClientCId(record[i+=1] != null ? ((BigDecimal)record[i]).longValue() : null);
				quoteRequestVO.setRequestStatus((String)record[i+=1]);
				quoteRequestVO.setDateRequestSubmitted((Date) record[i+=1]);
				quoteRequestVO.setDueDate((Date) record[i+=1]);				
				quoteRequestVO.setRequestor((String)record[i+=1]);
				quoteRequestVO.setAssignedTo((String)record[i+=1]);
				quoteRequestVO.setCompletedDate((Date) record[i+=1]);
				quoteRequestVO.setClosedDate((Date) record[i+=1]);
				quoteRequestVO.setToleranceYN(String.valueOf(record[i+=1]));				
				quoteRequestVO.setVehicleDescription((String)record[i+=1]);
				quoteRequestVO.setVqPrinted(record[i+=1] != null ? true : false);
				quoteRequestVO.setVqAccepted(record[i+=1] != null ? true : false);
				quoteRequestVO.setQuoteAccepted(record[i+=1] != null ? true : false);
				if(record[i+=1] != null || record[i+=1] != null) {
					quoteRequestVO.setPoPaid(true);
				} else {
					quoteRequestVO.setPoPaid(false);
				}

				quoteRequestSearchResultVOs.add(quoteRequestVO);
			}
		}
		
		return quoteRequestSearchResultVOs;
	}
	
	@Override
	public int findQuoteRequestsCount(QuoteRequestSearchCriteriaVO searchCriteria){
		int count = 0;
		Query query = null;
		query = generateQuoteRequestsQuery(searchCriteria, null, true);
		count = ((BigDecimal)query.getSingleResult()).intValue();
		return count;	
	}	
	
	
	private Query generateQuoteRequestsQuery(QuoteRequestSearchCriteriaVO searchCriteria, Sort sort, boolean isCountQuery){
		SimpleDateFormat dateFormat = new SimpleDateFormat(MALUtilities.DATE_PATTERN);
		StringBuilder sqlStmt;
		Query query = null;
		
		sqlStmt = new StringBuilder("");
		if(isCountQuery){
			sqlStmt.append("SELECT COUNT(1) ");
		} else {
			sqlStmt.append("SELECT * ");
		}
		
		sqlStmt.append(" FROM (SELECT qrq.qrq_id, qrt.name as request_type, ext.account_name, ext.account_code, ext.account_type, ext.c_id, qrs.name as request_status, "
					 + " qrq.submitted_date, qrq.due_date, qrq.submitted_by, qrq.assigned_to, qrq.completed_date, qrq.closed_date,"
				     + " CASE WHEN SYSDATE > nvl(qrq.due_date, SYSDATE) THEN 'N' ELSE 'Y' END AS tolerance_yn, " 
		             + " (SELECT LISTAGG(DECODE(NVL(qrv.fms_fms_id, 1), 1, qrv.description, (SELECT mdl.model_desc FROM models mdl, fleet_masters fms WHERE mdl.mdl_id = fms.mdl_mdl_id AND fms.fms_id = qrv.fms_fms_id)), ',') WITHIN GROUP (ORDER BY qrv.description) FROM quote_request_vehicles qrv WHERE qrv.qrq_qrq_id = qrq.qrq_id) vehicle_description,"
				     + " (SELECT 1 FROM quotation_models qm, quote_request_quotes qrqt where qrqt.qrq_qrq_id = qrq.qrq_id AND qm.quo_quo_id = qrqt.quo_quo_id AND qm.printed_ind = 'Y' and rownum=1), "
		             + " (SELECT 1 FROM quotation_models qm, quote_request_quotes qrqt where qrqt.qrq_qrq_id = qrq.qrq_id AND qm.quo_quo_id = qrqt.quo_quo_id AND qm.acceptance_date is not null and rownum=1),"
				     + " (SELECT 1 FROM quotation_models qm, quote_request_quotes qrqt where qrqt.qrq_qrq_id = qrq.qrq_id AND qm.quo_quo_id = qrqt.quo_quo_id AND qm.quote_status in ('3','6','15','16','17') and rownum=1),  " 		
				     + " (SELECT 1 FROM quotation_models qm, quote_request_quotes qrqt, doc d where qrqt.qrq_qrq_id = qrq.qrq_id AND qm.quo_quo_id = qrqt.quo_quo_id AND d.doc_type = 'PORDER' AND d.source_code IN ('FLQUOTE', 'FLORDER') AND NVL (d.order_type, 'M') = 'M' AND d.doc_status = 'R' AND d.generic_ext_id = qm.qmd_id  and rownum=1),  "
					 + " (SELECT 1 FROM quotation_models qm, quote_request_quotes qrqt, fleet_masters fm where qrqt.qrq_qrq_id = qrq.qrq_id AND qm.quo_quo_id = qrqt.quo_quo_id AND qm.fms_fms_id = fm.fms_id AND fm.fleet_category = 'STOCK' and rownum=1)  ");
		
		sqlStmt.append(", qrs.code request_status_code, qrt.code request_type_code");
		
		if (!MALUtilities.isEmpty(searchCriteria.getRequestor()) || !MALUtilities.isEmpty(searchCriteria.getRequestorNo())) {
			sqlStmt.append(", requestor.first_name req_first_name, requestor.last_name req_last_name");
		}
		
		if (!MALUtilities.isEmpty(searchCriteria.getAssignedTo()) || !MALUtilities.isEmpty(searchCriteria.getAssignedToNo())) {
			sqlStmt.append(", assignedto.first_name ass_first_name, assignedto.last_name ass_last_name");
		}
		
		sqlStmt.append("    FROM quote_requests qrq, quote_request_types qrt, quote_request_statuses qrs, external_accounts ext");
		
		if (!MALUtilities.isEmpty(searchCriteria.getRequestor()) || !MALUtilities.isEmpty(searchCriteria.getRequestorNo())) {
			sqlStmt.append(", personnel_base requestor");
		}
		
		if (!MALUtilities.isEmpty(searchCriteria.getAssignedTo()) || !MALUtilities.isEmpty(searchCriteria.getAssignedToNo())) {
			sqlStmt.append(", personnel_base assignedto");
		}
		
		sqlStmt.append("   WHERE qrq.qrt_qrt_id = qrt.qrt_id "
		        	  +"     AND qrq.qrs_qrs_id = qrs.qrs_id "
		        	  +"     AND qrq.client_account_code = ext.account_code "
		        	  +"     AND qrq.client_account_type = ext.account_type "
		        	  +"     AND qrq.client_c_id = ext.c_id");

		if (!MALUtilities.isEmpty(searchCriteria.getRequestor()) || !MALUtilities.isEmpty(searchCriteria.getRequestorNo())) {
			sqlStmt.append("  AND requestor.employee_no = qrq.submitted_by");
		}
		
		if (!MALUtilities.isEmpty(searchCriteria.getAssignedTo()) || !MALUtilities.isEmpty(searchCriteria.getAssignedToNo())) {
			sqlStmt.append("  AND assignedto.employee_no = qrq.assigned_to");
		}
		
		sqlStmt.append(" ) quote_request WHERE 1 = 1");
		
		
		Map<String,Object> parameterMap = new HashMap<String,Object>();
		
		if(searchCriteria.getRequestStatuses() != null && searchCriteria.getRequestStatuses().size() > 0){
			StringBuilder requestStatusBuilder = new StringBuilder(); 
			int index = 0;
			for(String requestStatus : searchCriteria.getRequestStatuses()) {
				requestStatusBuilder.append("'").append(requestStatus).append("'");index++;
				if(index  < searchCriteria.getRequestStatuses().size())
					requestStatusBuilder.append(",");
			}
			String requestStatusCriteria = requestStatusBuilder.toString();
			sqlStmt.append(  " AND quote_request.request_status_code IN ( ").append(requestStatusCriteria).append(" )  ");
		}
		
		if (!MALUtilities.isEmpty(searchCriteria.getRequestedClientCode())) {
			parameterMap.put("client", DataUtilities.appendWildCardToRight(DataUtilities.prependWildCardToLeft(searchCriteria.getRequestedClientCode())));
			sqlStmt.append(" AND LOWER(quote_request.account_code) LIKE LOWER(:client)");
			
		}
		
		if (!MALUtilities.isEmpty(searchCriteria.getRequestedClient())) {
			parameterMap.put("client", DataUtilities.appendWildCardToRight(DataUtilities.prependWildCardToLeft(searchCriteria.getRequestedClient())));
			sqlStmt.append(" AND LOWER(quote_request.account_name) LIKE LOWER(:client)");
			
		}
		if(searchCriteria.getRequestTypes() != null && searchCriteria.getRequestTypes().size() > 0){
			StringBuilder requestTypeBuilder = new StringBuilder(); 
			int index = 0;
			for (String requestType : searchCriteria.getRequestTypes()) {
				requestTypeBuilder.append("'").append(requestType).append("'");index++;
				if(index  < searchCriteria.getRequestTypes().size())
					requestTypeBuilder.append(",");
			}
			String requestTypeCriteria = requestTypeBuilder.toString();
			sqlStmt.append(  " AND quote_request.request_type_code IN ( ").append(requestTypeCriteria).append(" )  ");
		}
		
		if (!MALUtilities.isEmpty(searchCriteria.getDateSubmittedFrom()) || !MALUtilities.isEmpty(searchCriteria.getDateSubmittedTo())) {
			parameterMap.put("dateFrom", (!MALUtilities.isEmpty(searchCriteria.getDateSubmittedFrom()) ? searchCriteria.getDateSubmittedFrom(): new Date(1)));
			parameterMap.put("dateTo",  (!MALUtilities.isEmpty(searchCriteria.getDateSubmittedTo()) ? searchCriteria.getDateSubmittedTo(): new Date()));
			sqlStmt.append(" AND TRUNC(quote_request.submitted_date) BETWEEN TRUNC(:dateFrom) AND TRUNC(:dateTo) ");
		}
		
		if (!MALUtilities.isEmpty(searchCriteria.getRequestedEta())) {
			sqlStmt.append(" AND TO_CHAR(quote_request.due_date, 'mm/dd/yyyy') = :requestedEta ");			
			parameterMap.put("requestedEta", dateFormat.format(searchCriteria.getRequestedEta()));			
		}
		
		if (!MALUtilities.isEmpty(searchCriteria.getRequestorNo())) {
			parameterMap.put("requestorNo", searchCriteria.getRequestorNo());
			sqlStmt.append(" AND quote_request.submitted_by = :requestorNo");
		} else {
			if (!MALUtilities.isEmpty(searchCriteria.getRequestor())) {
				//generate SQL for last name ,first name 
				String lastNameRequestor =  null;
				String firstNameRequestor =  null;
				
				String[] nameArray= searchCriteria.getRequestor().split(",");			
				lastNameRequestor = searchCriteria.getRequestor().split(",")[0];
				if(nameArray.length > 1){			
					firstNameRequestor =  searchCriteria.getRequestor().split(",")[1];
				}
				
				if(!MALUtilities.isEmpty(lastNameRequestor)){
					sqlStmt.append(" AND UPPER(quote_request.req_last_name) LIKE :lastNameRequestor ");
					if(!MALUtilities.isEmpty(firstNameRequestor)){				
						parameterMap.put("lastNameRequestor", lastNameRequestor.toUpperCase());
					}else {
						parameterMap.put("lastNameRequestor", DataUtilities.appendWildCardToRight(lastNameRequestor.toUpperCase()));
					}
				}
				if(!MALUtilities.isEmpty(firstNameRequestor)){
					sqlStmt.append("AND UPPER(quote_request.req_first_name) LIKE :firstNameRequestor ");			
					parameterMap.put("firstNameRequestor", DataUtilities.appendWildCardToRight(firstNameRequestor.trim().toUpperCase()));
				}
			}
		}
		
		if (!MALUtilities.isEmpty(searchCriteria.getAssignedToNo())) {
			parameterMap.put("assignedToNo", searchCriteria.getAssignedToNo());
			sqlStmt.append(" AND quote_request.assigned_to = :assignedToNo");
		} else {
			if (!MALUtilities.isEmpty(searchCriteria.getAssignedTo())) {
				
				String lastNameAssignedTo =  null;
				String firstNameAssignedTo =  null;
				
				String[] nameArray= searchCriteria.getAssignedTo().split(",");			
				lastNameAssignedTo = searchCriteria.getAssignedTo().split(",")[0];
				if(nameArray.length > 1){			
					firstNameAssignedTo =  searchCriteria.getAssignedTo().split(",")[1];
				}
				
				if(!MALUtilities.isEmpty(lastNameAssignedTo)){
					sqlStmt.append(" AND UPPER(quote_request.ass_last_name) LIKE :lastNameAssignedTo ");
					if(!MALUtilities.isEmpty(firstNameAssignedTo)){				
						parameterMap.put("lastNameAssignedTo", lastNameAssignedTo.toUpperCase());
					}else {
						parameterMap.put("lastNameAssignedTo", DataUtilities.appendWildCardToRight(lastNameAssignedTo.toUpperCase()));
					}
				}
				if(!MALUtilities.isEmpty(firstNameAssignedTo)){
					sqlStmt.append("AND UPPER(quote_request.ass_first_name) LIKE :firstNameAssignedTo ");			
					parameterMap.put("firstNameAssignedTo", DataUtilities.appendWildCardToRight(firstNameAssignedTo.trim().toUpperCase()));
				}
			}
		}
		
		if (!MALUtilities.isEmpty(searchCriteria.getVehicleDescription())) {
			parameterMap.put("vehicleDesc", DataUtilities.appendWildCardToRight(DataUtilities.prependWildCardToLeft(searchCriteria.getVehicleDescription())));
			sqlStmt.append(" AND LOWER(quote_request.vehicle_description) LIKE LOWER(:vehicleDesc)");
		}
		
		if (!MALUtilities.isEmpty(searchCriteria.getRequestId())) {
			parameterMap.put("requestId", searchCriteria.getRequestId());
			sqlStmt.append(" AND quote_request.qrq_id = :requestId");
		}		
		
		//Order by specified in the passed sort object		
		if(!isCountQuery){
			if(!MALUtilities.isEmpty(sort)){
				sqlStmt.append(
					"   ORDER BY ");
				for ( Iterator<Order> orderIterator = sort.iterator(); orderIterator.hasNext(); ) {
					Order order = orderIterator.next();
						
					if(DataConstants.QUOTE_REQ_SRH_SORT_TOL.equals(order.getProperty())){	
						sqlStmt.append(" quote_request.tolerance_yn " + order.getDirection());
					}									
					if(DataConstants.QUOTE_REQ_SRH_SORT_CLIENT_NAME.equals(order.getProperty())){	
						sqlStmt.append(" quote_request.account_name " + order.getDirection());
					}					
					if(DataConstants.QUOTE_REQ_SRH_SORT_REQUEST_TYPE.equals(order.getProperty())){	
						sqlStmt.append(" quote_request.request_type " + order.getDirection());
					}
					if(DataConstants.QUOTE_REQ_SRH_SORT_DT_REQ_SUB.equals(order.getProperty())){	
						sqlStmt.append(" quote_request.submitted_date " + order.getDirection());
					}
					if(DataConstants.QUOTE_REQ_SRH_SORT_DUE_DATE.equals(order.getProperty())){	
						sqlStmt.append(" quote_request.due_date " + order.getDirection());
					}
					if(DataConstants.QUOTE_REQ_SRH_SORT_REQUESTOR.equals(order.getProperty())){	
						sqlStmt.append(" quote_request.submitted_by " + order.getDirection());
					}					
					if(DataConstants.QUOTE_REQ_SRH_SORT_ASSIGN_TO.equals(order.getProperty())){	
						sqlStmt.append(" quote_request.assigned_to " + order.getDirection());
					}
					if(DataConstants.QUOTE_REQ_SRH_SORT_STATUS.equals(order.getProperty())){	
						sqlStmt.append(" quote_request.request_status " + order.getDirection());
					}
					if(DataConstants.QUOTE_REQ_SRH_SORT_QRQ_ID.equals(order.getProperty())){	
						sqlStmt.append(" quote_request.qrq_id " + order.getDirection());
					}					

					if(orderIterator.hasNext()){
						sqlStmt.append(", ");
					}
				}			
			} else {
				sqlStmt.append("   ORDER BY quote_request.due_date ASC, quote_request.qrq_id ASC ");
			}
		}
		
		query = entityManager.createNativeQuery(sqlStmt.toString());
		for (String paramName : parameterMap.keySet()) {
			query.setParameter(paramName, parameterMap.get(paramName));
		} 
				
		return query;
	}
	
	public List<ClientPocVO> getClientPoc(ExternalAccount ea) {
		List<ClientPocVO> clientPocVoList = new ArrayList<ClientPocVO>();
		
		StringBuilder sqlStmt;
		Query query = null;
		
		sqlStmt = new StringBuilder("");
		
		sqlStmt.append("SELECT csys.system_description category," +
					   "	   cpnt.point_name name, " +
					   "	   cpnt.point_description description," +
					   "	   NVL2 (cnt.last_name, cnt.last_name, NULL) || NVL2 (cnt.first_name, ', ' || cnt.first_name, NULL) contact_name," +
					   "	   DECODE (ccon.drv_ind, 'Y', 'Yes', 'No') send_to_driver" +
					   "  FROM client_systems csys,client_points cpnt, client_point_accounts cpnta, client_contacts ccon, contacts cnt" +
					   " WHERE cpnt.csys_csys_id = csys.csys_id" +
					   "   AND cpnta.cpnt_cpnt_id = cpnt.cpnt_id" +
					   "   AND cpnta.c_id = :cId" +
					   "   AND cpnta.account_type = :accType" +
					   "   AND cpnta.account_code = :accCode" +
					   "   AND ccon.cpnta_cpnta_id = cpnta.cpnta_id" +
					   "   AND ccon.cnt_cnt_id = cnt.cnt_id " +
					   "   AND cnt.default_ind = 'Y'");

		Map<String,Object> parameterMap = new HashMap<String,Object>();
		
		parameterMap.put("cId", ea.getExternalAccountPK().getCId());
		parameterMap.put("accType", ea.getExternalAccountPK().getAccountType());
		parameterMap.put("accCode", ea.getExternalAccountPK().getAccountCode());
		
		query = entityManager.createNativeQuery(sqlStmt.toString());
		for (String paramName : parameterMap.keySet()) {
			query.setParameter(paramName, parameterMap.get(paramName));
		}
		
		@SuppressWarnings("unchecked")
		List<Object[]>resultList = (List<Object[]>)query.getResultList();
		if(resultList != null){
			for(Object[] record : resultList){
				int i = 0;
				
				ClientPocVO clientPocVo = new ClientPocVO();
				
				clientPocVo.setPocCategory((String)(record[i] 			!= null ? record[i] : null));
				clientPocVo.setPocName((String)(record[i+=1] 			!= null ? record[i] : null));
				clientPocVo.setPocDescription((String)(record[i+=1] 	!= null ? record[i] : null));
				clientPocVo.setContactName((String)(record[i+=1] 		!= null ? record[i] : null));
				clientPocVo.setSendToDriver((String)(record[i+=1] 		!= null ? record[i] : null));
				
				clientPocVoList.add(clientPocVo);
			}
		}
		
		return clientPocVoList;
	}
	
	public List<ClientPrefSupplierVO> getClientPrefSupplier(ExternalAccount ea) {
		List<ClientPrefSupplierVO> clientPrefSupplierVoList = new ArrayList<ClientPrefSupplierVO>();
		
		StringBuilder sqlStmt;
		Query query = null;
		
		sqlStmt = new StringBuilder("");
		
		sqlStmt.append("SELECT ea.account_code," +
					   "	   ea.account_name" +
					   "  FROM ext_acc_pref_suppliers eaps, external_accounts ea" +
					   " WHERE ea.c_id = eaps.c_id_supp" +
					   "   AND ea.account_type = eaps.account_type_supp" +
					   "   AND ea.account_code = eaps.account_code_supp" +
					   "   AND eaps.c_id = :cId" +
					   "   AND eaps.account_type = :accType" +
					   "   AND eaps.account_code = :accCode");

		Map<String,Object> parameterMap = new HashMap<String,Object>();
		
		parameterMap.put("cId", ea.getExternalAccountPK().getCId());
		parameterMap.put("accType", ea.getExternalAccountPK().getAccountType());
		parameterMap.put("accCode", ea.getExternalAccountPK().getAccountCode());
		
		query = entityManager.createNativeQuery(sqlStmt.toString());
		for (String paramName : parameterMap.keySet()) {
			query.setParameter(paramName, parameterMap.get(paramName));
		}
		
		@SuppressWarnings("unchecked")
		List<Object[]>resultList = (List<Object[]>)query.getResultList();
		if(resultList != null){
			for(Object[] record : resultList){
				int i = 0;
				
				ClientPrefSupplierVO clientPrefSupplierVo = new ClientPrefSupplierVO();
				
				clientPrefSupplierVo.setPrefSupplierCode((String)(record[i] 			!= null ? record[i] : null));
				clientPrefSupplierVo.setPrefSupplierName((String)(record[i+=1] 			!= null ? record[i] : null));
				
				clientPrefSupplierVoList.add(clientPrefSupplierVo);
			}
		}
		
		return clientPrefSupplierVoList;
	}
	
	public List<ClientFleetCodeVO> getClientFleetCodes(ExternalAccount ea) {
		List<ClientFleetCodeVO> clientFleetCodeVOList = new ArrayList<ClientFleetCodeVO>();
		
		StringBuilder sqlStmt;
		Query query = null;
		
		sqlStmt = new StringBuilder("");
		
		sqlStmt.append("SELECT DISTINCT mak.make_desc," +
					   "	   fin.finfan_number" +
					   "  FROM ext_acc_fin_fan fin, makes mak" +
					   " WHERE mak.mak_id = fin.mak_id" +
					   "   AND fin.c_id = :cId" +
					   "   AND fin.account_type = :accType" +
					   "   AND fin.account_code = :accCode" +
					   " ORDER BY mak.make_desc");

		Map<String,Object> parameterMap = new HashMap<String,Object>();
		
		parameterMap.put("cId", ea.getExternalAccountPK().getCId());
		parameterMap.put("accType", ea.getExternalAccountPK().getAccountType());
		parameterMap.put("accCode", ea.getExternalAccountPK().getAccountCode());
		
		query = entityManager.createNativeQuery(sqlStmt.toString());
		for (String paramName : parameterMap.keySet()) {
			query.setParameter(paramName, parameterMap.get(paramName));
		}
		
		@SuppressWarnings("unchecked")
		List<Object[]>resultList = (List<Object[]>)query.getResultList();
		if(resultList != null){
			for(Object[] record : resultList){
				int i = 0;
				
				ClientFleetCodeVO clientFleetCodeVO = new ClientFleetCodeVO();
				
				clientFleetCodeVO.setManufacturer((String)(record[i] 			!= null ? record[i] : null));
				clientFleetCodeVO.setFleetCode((String)(record[i+=1] 			!= null ? record[i] : null));
				
				clientFleetCodeVOList.add(clientFleetCodeVO);
			}
		}
		
		return clientFleetCodeVOList;
	}
	
	public List<ClientConsultantsVO> getClientConsultants(ExternalAccount ea) {
		List<ClientConsultantsVO> clientConsultantsVOList = new ArrayList<ClientConsultantsVO>();
		
		StringBuilder sqlStmt;
		Query query = null;
		
		sqlStmt = new StringBuilder("");
		
		sqlStmt.append("SELECT pb.first_name," +
					   "	   pb.last_name," +
					   "	   eac.role_type," +
					   "	   rtc.description" +
					   "  FROM ext_acc_consultants eac, personnel_base pb, role_type_codes rtc" +
					   " WHERE eac.employee_no = pb.employee_no" +
					   "   AND eac.role_type = rtc.role_type" +
					   "   AND eac.ea_c_id = :cId" +
					   "   AND eac.ea_account_type = :accType" +
					   "   AND eac.ea_account_code = :accCode" +
					   " ORDER BY pb.first_name");

		Map<String,Object> parameterMap = new HashMap<String,Object>();
		
		parameterMap.put("cId", ea.getExternalAccountPK().getCId());
		parameterMap.put("accType", ea.getExternalAccountPK().getAccountType());
		parameterMap.put("accCode", ea.getExternalAccountPK().getAccountCode());
		
		query = entityManager.createNativeQuery(sqlStmt.toString());
		for (String paramName : parameterMap.keySet()) {
			query.setParameter(paramName, parameterMap.get(paramName));
		}
		
		@SuppressWarnings("unchecked")
		List<Object[]>resultList = (List<Object[]>)query.getResultList();
		if(resultList != null){
			for(Object[] record : resultList){
				int i = 0;
				
				ClientConsultantsVO clientConsultantsVO = new ClientConsultantsVO();
				
				clientConsultantsVO.setFirstName((String)(record[i] 			!= null ? record[i] : null));
				clientConsultantsVO.setLastName((String)(record[i+=1] 			!= null ? record[i] : null));
				clientConsultantsVO.setRoleType((String)(record[i+=1] 			!= null ? record[i] : null));
				clientConsultantsVO.setRoleDescription((String)(record[i+=1] 	!= null ? record[i] : null));
				
				clientConsultantsVOList.add(clientConsultantsVO);
			}
		}
		
		return clientConsultantsVOList;
	}
	
	public List<ClientQuoteRequestServiceElementVO> getClientServiceElements(ExternalAccount ea) {
		List<ClientQuoteRequestServiceElementVO> clientQuoteRequestServiceElementVOList = new ArrayList<ClientQuoteRequestServiceElementVO>();
		
		StringBuilder sqlStmt;
		Query query = null;
		
		sqlStmt = new StringBuilder("");
		
		sqlStmt.append("SELECT lel.lel_id," +
				 	   "	   lel.description," +
					   "	   status," +
					   "	   prd_product_code," +
					   "	   eag_eag_id," +
					   "	   eagg.driver_grade_group," +
					   "	   dg.grade_desc" +
					   "  FROM (SELECT cse.*, DECODE (removed_flag, 'Y', 'ACC PRD_EXCLUSIONS') status" +
					   "		  FROM client_service_elements cse, client_serv_element_types cset" +
					   "		 WHERE cse.cset_cset_id = cset.cset_id" +
					   "		   AND cset.service_element_type_code = 'AC'" +
					   "		   AND cse.ea_c_id = :cId" +
					   "		   AND cse.ea_account_type = :accType" +
					   "		   AND cse.ea_account_code = :accCode" +
					   "		 UNION" +
					   "		SELECT cse.*, DECODE (removed_flag, 'Y', DECODE (prd_product_code, NULL, 'GG_EXCLUSION', 'GG_PRD_EXCLUSION')) status" +
					   "		  FROM client_service_elements cse, client_serv_element_types cset" +
					   "	     WHERE cse.cset_cset_id = cset.cset_id" +
					   "		   AND cset.service_element_type_code = 'GG'" +
					   "		   AND cse.ea_c_id = :cId" +
					   "		   AND cse.ea_account_type = :accType" +
					   "		   AND cse.ea_account_code = :accCode) cse, client_contract_service_ele ccse, lease_elements lel, external_account_grade_groups eagg, driver_grade_group_codes dggc, driver_grades dg" +
					   " WHERE eagg.eag_id (+) = cse.eag_eag_id" +
					   "   AND dggc.driver_grade_group (+) = eagg.driver_grade_group" +
					   "   AND dggc.driver_grade_group = dg.grade_code (+)" +
					   "   AND cse.ccse_ccse_id = ccse.ccse_id" +
					   "   AND ccse.lel_lel_id = lel.lel_id" +
					   " ORDER BY lel.description");

		Map<String,Object> parameterMap = new HashMap<String,Object>();
		
		parameterMap.put("cId", ea.getExternalAccountPK().getCId());
		parameterMap.put("accType", ea.getExternalAccountPK().getAccountType());
		parameterMap.put("accCode", ea.getExternalAccountPK().getAccountCode());
		
		query = entityManager.createNativeQuery(sqlStmt.toString());
		for (String paramName : parameterMap.keySet()) {
			query.setParameter(paramName, parameterMap.get(paramName));
		}
		
		@SuppressWarnings("unchecked")
		List<Object[]>resultList = (List<Object[]>)query.getResultList();
		if(resultList != null){
			for(Object[] record : resultList){
				int i = 0;
				
				ClientQuoteRequestServiceElementVO qrseVO = new ClientQuoteRequestServiceElementVO();
				
				qrseVO.setElementId((record[i] 					!= null ? ((BigDecimal)record[i]).longValue() 	: null));
				qrseVO.setElementName((String)(record[i+=1] 	!= null ? record[i] 							: null));
				qrseVO.setElementStatus((String)(record[i+=1] 	!= null ? record[i] 							: null));
				qrseVO.setProductCode((String)(record[i+=1] 	!= null ? record[i]								: null));
				qrseVO.setClientGradeGroupId((record[i+=1]		!= null ? ((BigDecimal)record[i]).longValue() 	: null));
				qrseVO.setGradeGroupCode((String)(record[i+=1] 	!= null ? record[i] 							: null));
				qrseVO.setGradeGroupDesc((String)(record[i+=1] 	!= null ? record[i] 							: null));
				
				clientQuoteRequestServiceElementVOList.add(qrseVO);
			}
		}
		return clientQuoteRequestServiceElementVOList;
	}
	
	public ClientCreditLimitsVO getClientCreditLimits (ExternalAccount ea) {
		ClientCreditLimitsVO creditLimit = new ClientCreditLimitsVO();
		
		Connection connection = null;
		CallableStatement callableStatement = null;
			
		try {
			connection = DataSourceUtils.getConnection(dataSource);
			callableStatement = connection.prepareCall("{ call fl_credit_management_wrapper.get_limits(?,?,?,?,?,?,?,?,?,?,?,?,?,?) }");
			
			callableStatement.setLong(1, ea.getExternalAccountPK().getCId());
			callableStatement.setString(2, ea.getExternalAccountPK().getAccountType());
			callableStatement.setString(3, ea.getExternalAccountPK().getAccountCode());
			callableStatement.registerOutParameter(4, Types.VARCHAR);
			callableStatement.registerOutParameter(5, Types.NUMERIC);
			callableStatement.registerOutParameter(6, Types.NUMERIC);
			callableStatement.registerOutParameter(7, Types.NUMERIC);
			callableStatement.registerOutParameter(8, Types.NUMERIC);
			callableStatement.registerOutParameter(9, Types.NUMERIC);
			callableStatement.registerOutParameter(10, Types.NUMERIC);
			callableStatement.registerOutParameter(11, Types.NUMERIC);
			callableStatement.registerOutParameter(12, Types.NUMERIC);
			callableStatement.registerOutParameter(13, Types.VARCHAR);
			callableStatement.registerOutParameter(14, Types.VARCHAR);
			
			callableStatement.execute();
			
			if (!MALUtilities.isEmpty(callableStatement.getString(13))) {
				creditLimit.setError(callableStatement.getString(14));
			}
			
			creditLimit.setCreditStatus(AccountCreditStatusEnum.getByStatus(ea.getCredApprStatus()).getDescription());
			creditLimit.setBalanceCeilLimit(callableStatement.getBigDecimal(5));
			creditLimit.setBalanceCurrentLimit(callableStatement.getBigDecimal(6));
			creditLimit.setCapitalCeilLimit(callableStatement.getBigDecimal(7));
			creditLimit.setCapitalCurrentLimit(callableStatement.getBigDecimal(8));
			creditLimit.setUnitCeilLimit(callableStatement.getBigDecimal(9));
			creditLimit.setUnitCurrentLimit(callableStatement.getBigDecimal(10));
			
			
		} catch (Exception ex) {
			
		}
		
		return creditLimit;
	}
	
	public List<QuoteRequestClientProfilesVO> getClientProfiles(ExternalAccount ea) {
		List<QuoteRequestClientProfilesVO> quoteRequestClientProfilesVOList = new ArrayList<QuoteRequestClientProfilesVO>();
		
		StringBuilder sqlStmt;
		Query query = null;
		
		sqlStmt = new StringBuilder("");
		
		sqlStmt.append("SELECT qpr.qpr_id," +
				 	   "	   qpr.itc_interest_type," +
					   "	   qpr.prd_product_code," +
					   "	   qpr.effective_from," +
					   "	   DECODE (qpr.pre_contract_fixed_cost,  'F', 'Fixed',  'V', 'Variable') cost_treatment," +
					   "	   (SELECT LISTAGG (description, ', ') WITHIN GROUP (ORDER BY description)" +
					   "          FROM quotation_profile_finances" +
					   "         WHERE qpr_qpr_id = qpr.qpr_id) finance_params," +
					   "	   (SELECT listagg(program_description, ', ') WITHIN GROUP (ORDER BY program_description)" +
					   "          FROM et_xsm_programs, quote_profile_programs" +
					   "         WHERE exp_exp_id = exp_id" +
					   "           AND qpr_qpr_id = qpr_id) mileage_program," +
					   "       exm.excess_mile_name," +
					   "	   qpr.profile_code || ' - ' || qpr.description " +
					   "  FROM quote_profile_cust qpc, quotation_profiles qpr, excess_mileage exm" +
					   " WHERE qpr.qpr_id = qpc.qpr_qpr_id" +
					   "   AND NVL (qpr.effective_to, TRUNC (SYSDATE)) >= TRUNC (SYSDATE)" +
					   "   AND exm.excess_mile_id = qpr.excess_mile_id" +
					   "   AND qpc.ea_c_id = :cId" +
					   "   AND qpc.ea_account_type = :accType" +
					   "   AND qpc.ea_account_code = :accCode" +
					   " ORDER BY qpr.effective_from DESC");

		Map<String,Object> parameterMap = new HashMap<String,Object>();
		
		parameterMap.put("cId", ea.getExternalAccountPK().getCId());
		parameterMap.put("accType", ea.getExternalAccountPK().getAccountType());
		parameterMap.put("accCode", ea.getExternalAccountPK().getAccountCode());
		
		query = entityManager.createNativeQuery(sqlStmt.toString());
		for (String paramName : parameterMap.keySet()) {
			query.setParameter(paramName, parameterMap.get(paramName));
		}
		
		@SuppressWarnings("unchecked")
		List<Object[]>resultList = (List<Object[]>)query.getResultList();
		if(resultList != null){
			for(Object[] record : resultList){
				int i = 0;
				
				QuoteRequestClientProfilesVO clientProfileVO = new QuoteRequestClientProfilesVO();
				
				clientProfileVO.setQprId((record[i] 								!= null ? ((BigDecimal)record[i]).longValue() 	: null));
				clientProfileVO.setInterestType((String)(record[i+=1] 				!= null ? record[i] 							: null));
				clientProfileVO.setProductType((String)(record[i+=1] 				!= null ? record[i] 							: null));
				clientProfileVO.setEffectiveFrom((Date)(record[i+=1] 				!= null ? record[i] 							: null));
				clientProfileVO.setCostTreatment((String)(record[i+=1] 				!= null ? record[i]								: null));
				clientProfileVO.setFinanceParams((String)(record[i+=1] 				!= null ? record[i] 							: null));
				clientProfileVO.setMileageProgram((String)(record[i+=1] 			!= null ? record[i] 							: null));
				clientProfileVO.setMileageBand((String)(record[i+=1] 				!= null ? record[i] 							: null));
				clientProfileVO.setProfileCodeDescription((String)(record[i+=1] 	!= null ? record[i] 							: null));
				
				quoteRequestClientProfilesVOList.add(clientProfileVO);
			}
		}
		return quoteRequestClientProfilesVOList;
	}
	
	public List<QuoteRequestQuoteModelVO> getQuoteRequestQuoteModels(ExternalAccountPK ea, Long quoId) {
		List<QuoteRequestQuoteModelVO> quoteRequestQuoteModelVOList = new ArrayList<QuoteRequestQuoteModelVO>();
		
		StringBuilder sqlStmt;
		Query query = null;
		
		sqlStmt = new StringBuilder("");
		
		sqlStmt.append("SELECT qmd.quo_quo_id," +
					   "       qmd.quote_no," +
					   "       qmd.revision_no," + 
					   "       qmd.qmd_id," +
					   "       mdl.model_desc," +
					   "       (SELECT drv.driver_surname || NVL2 (drv.driver_forename, ', ' || drv.driver_forename, drv.driver_forename)" +
					   "          FROM drivers drv" +
					   "         WHERE drv.drv_id = quo.drv_drv_id)," +
					   "       qmd.contract_period," +
					   "       qmd.contract_distance," +
					   "       qsc.description" +
					   "  FROM quotations quo, quotation_models qmd, models mdl, quotation_status_codes qsc" +
					   " WHERE qmd.mdl_mdl_id = mdl.mdl_id" +
					   "   AND qmd.quo_quo_id = quo.quo_id" +
					   "   AND qmd.quote_status = qsc.quotation_status" +
					   "   AND quo.c_id = :cId" +
					   "   AND quo.account_type = :accType" +
					   "   AND quo.account_code = :accCode" +
					   "   AND qmd.quote_status NOT IN (5, 6, 7, 8, 9, 10, 11, 15, 18, 19)" +
					   "   AND NOT EXISTS (SELECT 1 FROM quote_request_quotes qrq WHERE qrq.quo_quo_id = quo.quo_id)");
		
		if (!MALUtilities.isEmpty(quoId)) {
			sqlStmt.append("AND qmd.quo_quo_id = :quoId");
		}
		
		sqlStmt.append(" ORDER BY qmd.quo_quo_id desc, qmd.quote_no desc, qmd.revision_no DESC");
		
		query = entityManager.createNativeQuery(sqlStmt.toString());
		
		query.setParameter("cId", ea.getcId());
		query.setParameter("accType", ea.getAccountType());
		query.setParameter("accCode", ea.getAccountCode());
		
		if (!MALUtilities.isEmpty(quoId)) {
			query.setParameter("quoId", quoId);
		}
		
		@SuppressWarnings("unchecked")
		List<Object[]>resultList = (List<Object[]>)query.getResultList();
		if(resultList != null){
			for(Object[] record : resultList){
				int i = 0;
				
				QuoteRequestQuoteModelVO quoteRequestQuoteModelVO = new QuoteRequestQuoteModelVO();
				
				quoteRequestQuoteModelVO.setQuoId((record[i] 					!= null ? ((BigDecimal)record[i]).longValue() 	: null));
				quoteRequestQuoteModelVO.setQuoteNo((record[i+=1] 				!= null ? ((BigDecimal)record[i]).longValue() 	: null));
				quoteRequestQuoteModelVO.setRevisionNo((record[i+=1] 			!= null ? ((BigDecimal)record[i]).longValue() 	: null));
				quoteRequestQuoteModelVO.setQmdId((record[i+=1] 				!= null ? ((BigDecimal)record[i]).longValue() 	: null));
				quoteRequestQuoteModelVO.setModelDesc((String)(record[i+=1] 	!= null ? record[i] 							: null));
				quoteRequestQuoteModelVO.setDriverName((String)(record[i+=1] 	!= null ? record[i] 							: null));
				quoteRequestQuoteModelVO.setContractPeriod((record[i+=1] 		!= null ? ((BigDecimal)record[i]).longValue() 	: null));
				quoteRequestQuoteModelVO.setContractDistance((record[i+=1] 		!= null ? ((BigDecimal)record[i]).longValue() 	: null));
				quoteRequestQuoteModelVO.setQuoteStatus((String)(record[i+=1] 	!= null ? record[i] 							: null));
				
				quoteRequestQuoteModelVOList.add(quoteRequestQuoteModelVO);
				
			}
		}
		
		return quoteRequestQuoteModelVOList;
	}
	
	@SuppressWarnings("unchecked")
	public List<QuotationProfile> getQuotationProfiles(long corpId, String accountType, String accountCode){
		List<QuotationProfile> quotationProfileList = null;
		
		String queryString = "select * from quotation_profiles where qpr_id in (select qpr_qpr_id from quote_profile_cust where ea_c_id =:corpId and ea_account_type =:accountType and ea_account_code =:accountCode) and effective_to is null order by effective_from desc";
		
		Query query = entityManager.createNativeQuery(queryString, QuotationProfile.class);
		
		query.setParameter("corpId", corpId);
		query.setParameter("accountType", accountType);
		query.setParameter("accountCode", accountCode);
		
		quotationProfileList = query.getResultList();
		
		return quotationProfileList;
	}
	
}
