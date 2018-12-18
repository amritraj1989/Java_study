package com.mikealbert.data.dao;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Types;
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

import com.mikealbert.common.MalLogger;
import com.mikealbert.common.MalLoggerFactory;
import com.mikealbert.data.DataConstants;
import com.mikealbert.data.entity.DebitCreditMemoTransaction;
import com.mikealbert.data.entity.Model;
import com.mikealbert.data.vo.DebitCreditInvoiceSearchCriteriaVO;
import com.mikealbert.data.vo.DebitCreditInvoiceSearchResultVO;
import com.mikealbert.data.vo.DebitCreditMemoSearchCriteriaVO;
import com.mikealbert.data.vo.DebitCreditMemoSearchResultVO;
import com.mikealbert.exception.MalException;
import com.mikealbert.util.MALUtilities;

public class DebitCreditMemoTransactionDAOImpl extends GenericDAOImpl<Model, Long> implements DebitCreditMemoTransactionDAOCustom {
	
	@Resource
	private DebitCreditMemoTransactionDAO debitCreditMemoTransactionDAO;
	
	private static final long serialVersionUID = -5126660263724406923L;
	
	@Resource DataSource dataSource;
	
	private static MalLogger logger = MalLoggerFactory.getLogger(DebitCreditMemoTransactionDAOImpl.class);
	
	@Override
	public Long getNextPK() throws MalException {		
		
		StringBuilder sqlStmt = new StringBuilder("SELECT DCT_DCNO_SEQ.nextval FROM DUAL");
	
		Query query = entityManager.createNativeQuery(sqlStmt.toString());
		return ((BigDecimal)query.getSingleResult()).longValue();
	}	
	
	public List<DebitCreditMemoSearchResultVO> findDebitCreditMemoTransactions(DebitCreditMemoSearchCriteriaVO searchCriteria, Pageable pageable, Sort sort){
	
		Query query = generateDebitCreditMemoSearchQuery(searchCriteria, false, sort);
		return populateDebitCreditMemoSearchResultVO(query, pageable);
	}
	
	public int findDebitCreditMemoTransactionsCount(DebitCreditMemoSearchCriteriaVO searchCriteria) {
		Query query = generateDebitCreditMemoSearchQuery(searchCriteria, true, null);
		return populateDebitCreditMemoSearchCount(query);
	}
	
	
	
	private Query generateDebitCreditMemoSearchQuery(DebitCreditMemoSearchCriteriaVO searchCriteria, Boolean isCountQuery, Sort sort) {
		Query query =  null;
		Map<String, Object> parameterMap = new HashMap<String, Object>();
		StringBuilder sqlStmt = new StringBuilder("");
		
		sqlStmt.append("  SELECT dcm.dc_no, ea.account_code, ea.account_name, dcm.unit_no, dcm.status, dcms.status_desc, dcmt.type_description, "
						+" act.analysis_category, act.description as analysis_category_description , ac.analysis_code, ac.description as analysis_code_description, "
						+" dcm.selected_approver, dcm.submitter, dcm.submitted_date, dcm.approver, dcm.approved_date, dcm.processor, dcm.processed_date, dcm.allocator, dcm.allocated_date, "
						+" dcm.invoice_no, dcm.net_amount, dcm.total_amount, dcm.line_description, "
						+" (SELECT  dadd.region from DRIVER_ALLOCATIONS da,  DRIVER_ADDRESSES dadd WHERE da.fms_fms_id = fm.fms_id  "
						+"  	AND da.drv_drv_id =  dadd.drv_drv_id  "
						+" 		AND  dcm.submitted_date BETWEEN from_date and nvl(to_date, sysdate) "
						+" 		AND dadd.address_type = 'GARAGED'  "
						+"  ) as transaction_state,dcm.reason,dcm.source, dcm.ticket_no, dcm.warning_ind, dcm.is_client_unit, "
						+" dcm.tax_amount, dcm.transaction_date, dcm.rent_applicable_date, dcm.gl_code, dcm.invoice_note "
						+" FROM debit_credit_transactions dcm , debit_credit_memo_types dcmt, debit_credit_status_codes dcms, analysis_categories  act , analysis_codes ac , fleet_masters fm , external_accounts ea "
						+" WHERE dcm.dcmt_dcmt_id = dcmt.dcmt_id "
						+" AND dcm.status = dcms.dc_status_code "
						+" AND dcm.category_id = act.category_id "
						+" AND dcm.category_id = ac.category_id "
						+" AND dcm.analysis_code = ac.analysis_code "
						+" AND dcm.unit_no = fm.unit_no  "
						+" AND dcm.c_id = ea.c_id  "
						+" AND dcm.account_code = ea.account_code  "
						+" AND dcm.account_type = ea.account_type " );
		
		if (!MALUtilities.isEmpty(searchCriteria.getDcNo())) {
			sqlStmt.append("  AND dcm.dc_no = :dc_no ");
			parameterMap.put("dc_no", searchCriteria.getDcNo());
		}
		
		if (!MALUtilities.isEmpty(searchCriteria.getClientAccountCode())) {
			sqlStmt.append("  AND ea.c_id = :c_id");
			sqlStmt.append("  AND ea.account_type = :account_type");
			sqlStmt.append("  AND ea.account_code = :account_code");
			
			parameterMap.put("c_id", searchCriteria.getClientCid());
			parameterMap.put("account_type", searchCriteria.getClientAccountType());
			parameterMap.put("account_code", searchCriteria.getClientAccountCode());
		}else if (!MALUtilities.isEmpty(searchCriteria.getClientAccountName())) {
			sqlStmt.append("  AND ea.c_id = :c_id");
			sqlStmt.append("  AND ea.account_type = :account_type");
			sqlStmt.append("  AND LOWER(ea.account_name) LIKE LOWER(:account_name) ");
			
			parameterMap.put("c_id", searchCriteria.getClientCid());
			parameterMap.put("account_type", searchCriteria.getClientAccountType());
			parameterMap.put("account_name", "%" +searchCriteria.getClientAccountName()+ "%");
		}
		
		if (!MALUtilities.isEmpty(searchCriteria.getUnitNo())) {
			sqlStmt.append("  AND LOWER(dcm.unit_no) LIKE LOWER(:unit_no)");
			parameterMap.put("unit_no", "%" + searchCriteria.getUnitNo() + "%");
		}
		
		if (!MALUtilities.isEmpty(searchCriteria.getStatusCode())) {
			sqlStmt.append("  AND dcm.status = :status ");
			parameterMap.put("status", searchCriteria.getStatusCode());
		}
		
		if (!MALUtilities.isEmpty(searchCriteria.getAnalysisCategory())) {
			sqlStmt.append("  AND dcm.category_id = :category_id ");			
			parameterMap.put("category_id", searchCriteria.getAnalysisCategory().getCategoryId());
		}
		
		if (!MALUtilities.isEmpty(searchCriteria.getSubmittedSinceDate())) {		
			sqlStmt.append("  AND trunc(dcm.submitted_date) >= :submitted_date ");
			parameterMap.put("submitted_date", MALUtilities.clearTimeFromDate(searchCriteria.getSubmittedSinceDate()));
		}
		
		if (!MALUtilities.isEmpty(searchCriteria.getSelectedApprover())) {		
			sqlStmt.append("  AND LOWER(dcm.selected_approver) LIKE LOWER(:selected_approver)");
			parameterMap.put("selected_approver", "%" + searchCriteria.getSelectedApprover() + "%");
		}else  if ((!MALUtilities.isEmpty(searchCriteria.getSelectedApproverNameOrEmpNo())) && searchCriteria.getSelectedApproverNameOrEmpNo().indexOf(",") >= 0) {
			String[] nameParts = searchCriteria.getSelectedApproverNameOrEmpNo().split(",");
			String lastName =  nameParts[0].trim();
			String firstName =  nameParts[1].trim();
			
			sqlStmt.append(" AND EXISTS ( SELECT 1 from personnel_base pb1 where pb1.employee_no = dcm.selected_approver AND  LOWER(pb1.first_name) LIKE LOWER(:selectedApproverFirstName) AND LOWER(pb1.last_name) LIKE LOWER(:selectedApproverLastName)  )");
			parameterMap.put("selectedApproverFirstName", "%" + firstName + "%");
			parameterMap.put("selectedApproverLastName", "%" + lastName + "%");
		}		
		
		if (!MALUtilities.isEmpty(searchCriteria.getSubmitter())) {		
			sqlStmt.append("  AND LOWER(dcm.submitter) LIKE LOWER(:submitter)");
			parameterMap.put("submitter", "%" + searchCriteria.getSubmitter() + "%");
		}else  if ((!MALUtilities.isEmpty(searchCriteria.getSubmitterNameOrEmpNo())) && searchCriteria.getSubmitterNameOrEmpNo().indexOf(",") >= 0) {
			String[] nameParts = searchCriteria.getSubmitterNameOrEmpNo().split(",");
			String lastName =  nameParts[0].trim();
			String firstName =  nameParts[1].trim();
			
			sqlStmt.append(" AND EXISTS ( SELECT 1 from personnel_base pb1 where pb1.employee_no = dcm.submitter AND  LOWER(pb1.first_name) LIKE LOWER(:submitterFirstName) AND LOWER(pb1.last_name) LIKE LOWER(:submitterLastName)  )");
			parameterMap.put("submitterFirstName", "%" + firstName + "%");
			parameterMap.put("submitterLastName", "%" + lastName + "%");
		}
		
		if (!MALUtilities.isEmpty(searchCriteria.getApprover())) {		
			sqlStmt.append("  AND LOWER(dcm.approver) LIKE LOWER(:approver)");
			parameterMap.put("approver", "%" + searchCriteria.getApprover() + "%");
		}else if ((!MALUtilities.isEmpty(searchCriteria.getApproverNameOrEmpNo())) && searchCriteria.getApproverNameOrEmpNo().indexOf(",") >= 0) {
			String[] nameParts = searchCriteria.getApproverNameOrEmpNo().split(",");
			String lastName =  nameParts[0].trim();
			String firstName =  nameParts[1].trim();
			
			sqlStmt.append(" AND EXISTS ( SELECT 1 from personnel_base pb1 where pb1.employee_no = dcm.approver AND  LOWER(pb1.first_name) LIKE LOWER(:approverFirstName) AND LOWER(pb1.last_name) LIKE LOWER(:approverLastName)  )");
			parameterMap.put("approverFirstName", "%" + firstName + "%");
			parameterMap.put("approverLastName", "%" + lastName + "%");
		}
		
		if (!MALUtilities.isEmpty(searchCriteria.getProcessor())) {		
			sqlStmt.append("  AND LOWER(dcm.processor) LIKE LOWER(:processor)");
			parameterMap.put("processor", "%" + searchCriteria.getProcessor() + "%");
		}else if ((!MALUtilities.isEmpty(searchCriteria.getProcessorNameOrEmpNo())) && searchCriteria.getProcessorNameOrEmpNo().indexOf(",") >= 0) {
			String[] nameParts = searchCriteria.getProcessorNameOrEmpNo().split(",");
			String lastName =  nameParts[0].trim();
			String firstName =  nameParts[1].trim();
			
			sqlStmt.append(" AND EXISTS ( SELECT 1 from personnel_base pb1 where pb1.employee_no = dcm.processor AND LOWER(pb1.first_name) LIKE LOWER(:processorFirstName) AND LOWER(pb1.last_name) LIKE LOWER(:processorLastName)  )");
			parameterMap.put("processorFirstName", "%" + firstName + "%");
			parameterMap.put("processorLastName", "%" + lastName + "%");
		}
		
		if (!MALUtilities.isEmpty(searchCriteria.getAllocator())) {		
			sqlStmt.append("  AND LOWER(dcm.allocator) LIKE LOWER(:allocator)");
			parameterMap.put("allocator", "%" + searchCriteria.getAllocator() + "%");
		}else if ((!MALUtilities.isEmpty(searchCriteria.getAllocatorNameOrEmpNo())) && searchCriteria.getAllocatorNameOrEmpNo().indexOf(",") >= 0) {
			String[] nameParts = searchCriteria.getAllocatorNameOrEmpNo().split(",");
			String lastName =  nameParts[0].trim();
			String firstName =  nameParts[1].trim();
			
			sqlStmt.append(" AND EXISTS ( SELECT 1 from personnel_base pb1 where pb1.employee_no = dcm.allocator AND LOWER(pb1.first_name) LIKE LOWER(:allocatorFirstName) AND LOWER(pb1.last_name) LIKE LOWER(:allocatorLastName)  )");
			parameterMap.put("allocatorFirstName", "%" + firstName + "%");
			parameterMap.put("allocatorLastName", "%" + lastName + "%");
		}
		
		if (!MALUtilities.isEmpty(searchCriteria.getTicketNo())) {

			sqlStmt.append("  AND LOWER(dcm.ticket_no) LIKE LOWER(:ticket_no)");
			parameterMap.put("ticket_no", searchCriteria.getTicketNo() + "%");
		}

		if (!MALUtilities.isEmpty(searchCriteria.getUnitNo())) {
			sqlStmt.append("  AND LOWER(dcm.unit_no) LIKE LOWER(:unit_no)");
			parameterMap.put("unit_no", "%" + searchCriteria.getUnitNo() + "%");
		}

		
		if(!isCountQuery) {
			if(!MALUtilities.isEmpty(sort)) {
				sqlStmt.append(" ORDER BY ");
				for(Iterator<Order> configIterator = sort.iterator(); configIterator.hasNext();) {
					Order order = configIterator.next();
					
					
					if(DataConstants.DEBIT_CREDIT_MEMO_SEARCH_SORT_DC_NO.equals(order.getProperty())) {
						sqlStmt.append(" dcm.dc_no " + order.getDirection());
					}					
					if(DataConstants.DEBIT_CREDIT_MEMO_SEARCH_SORT_CLIENT_CODE.equals(order.getProperty())) {
						sqlStmt.append(" ea.account_code " + order.getDirection());
					}
					if(DataConstants.DEBIT_CREDIT_MEMO_SEARCH_SORT_UNIT_NO.equals(order.getProperty())) {
						sqlStmt.append(" dcm.unit_no " + order.getDirection());
					}					
					if(DataConstants.DEBIT_CREDIT_MEMO_SEARCH_SORT_STATUS.equals(order.getProperty())) {
						sqlStmt.append(" dcm.status " + order.getDirection());
					}
					if(DataConstants.DEBIT_CREDIT_MEMO_SEARCH_SORT_ENTRY_TYPE.equals(order.getProperty())) {
						sqlStmt.append(" dcmt.type_description " + order.getDirection());
					}					
					if(DataConstants.DEBIT_CREDIT_MEMO_SEARCH_SORT_ANALYSIS_CAT.equals(order.getProperty())) {
						sqlStmt.append(" act.analysis_category " + order.getDirection());
					}
					if(DataConstants.DEBIT_CREDIT_MEMO_SEARCH_SORT_ANALYSIS_CODE.equals(order.getProperty())) {
						sqlStmt.append(" ac.analysis_code " + order.getDirection());
					}					
					if(DataConstants.DEBIT_CREDIT_MEMO_SEARCH_SORT_SELECTED_APPROVED_BY.equals(order.getProperty())) {
						sqlStmt.append(" dcm.selected_approver " + order.getDirection());
					}
					if(DataConstants.DEBIT_CREDIT_MEMO_SEARCH_SORT_SUBMITTED_DATE.equals(order.getProperty())) {
						sqlStmt.append(" dcm.submitted_date " + order.getDirection());
					}
					if(DataConstants.DEBIT_CREDIT_MEMO_SEARCH_SORT_SUBMITTED_BY.equals(order.getProperty())) {
						sqlStmt.append(" dcm.submitter " + order.getDirection());
					}				
					if(DataConstants.DEBIT_CREDIT_MEMO_SEARCH_SORT_APPROVED_BY.equals(order.getProperty())) {
						sqlStmt.append(" dcm.approver , dcm.approved_date " + order.getDirection());
					}
					if(DataConstants.DEBIT_CREDIT_MEMO_SEARCH_SORT_PROCESSED_BY.equals(order.getProperty())) {
						sqlStmt.append(" dcm.processor , dcm.processed_date  " + order.getDirection());
					}				
					if(DataConstants.DEBIT_CREDIT_MEMO_SEARCH_SORT_ALLOCATED_BY.equals(order.getProperty())) {
						sqlStmt.append(" dcm.allocator , dcm.allocated_date " + order.getDirection());
					}
					if(DataConstants.DEBIT_CREDIT_MEMO_SEARCH_SORT_INVOICE_NO.equals(order.getProperty())) {
						sqlStmt.append(" dcm.invoice_no " + order.getDirection());
					}					
					if(DataConstants.DEBIT_CREDIT_MEMO_SEARCH_SORT_NET_AMOUNT.equals(order.getProperty())) {
						sqlStmt.append(" dcm.net_amount " + order.getDirection());
					}	
					if(DataConstants.DEBIT_CREDIT_MEMO_SEARCH_SORT_TOTAL_AMOUNT.equals(order.getProperty())) {
						sqlStmt.append(" dcm.total_amount " + order.getDirection());
					}
					if(DataConstants.DEBIT_CREDIT_MEMO_SEARCH_SORT_LINE_DESC.equals(order.getProperty())) {
						sqlStmt.append(" dcm.line_description " + order.getDirection());
					}					
					if(DataConstants.DEBIT_CREDIT_MEMO_SEARCH_SORT_TXN_STATE_BY.equals(order.getProperty())) {
						sqlStmt.append(" transaction_state " + order.getDirection());
					}
					if(DataConstants.DEBIT_CREDIT_MEMO_SEARCH_SORT_SOURCE.equals(order.getProperty())) {
						sqlStmt.append(" source " + order.getDirection());
					}
					if(DataConstants.DEBIT_CREDIT_MEMO_SEARCH_SORT_TICKET_NO.equals(order.getProperty())) {
						sqlStmt.append(" ticket_no " + order.getDirection());
					}
					if(configIterator.hasNext()) {
						sqlStmt.append(", ");
					}
				}
			} else {
				sqlStmt.append(" ORDER BY dcm.dc_no ASC");
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
	private List<DebitCreditMemoSearchResultVO> populateDebitCreditMemoSearchResultVO (Query query, Pageable pageable) {
		
		List<DebitCreditMemoSearchResultVO>  debitCreditMemoSearchResultList = new ArrayList<DebitCreditMemoSearchResultVO>();
		
		if(pageable != null) {
			query.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
			query.setMaxResults(pageable.getPageSize());
		}
		
		List<Object[]> objectList  = (List<Object[]>) query.getResultList();
		
		if (objectList != null && objectList.size() > 0) {	
			
			for (Iterator<Object[]> iterator = objectList.iterator(); iterator.hasNext();) {
				Object[] object = iterator.next();				
				DebitCreditMemoSearchResultVO debitCreditMemoSearchResultVO = new DebitCreditMemoSearchResultVO();
				int i = 0;
				
				debitCreditMemoSearchResultVO.setDcNumber(object[i]				!= null ? ((BigDecimal) object[i]).longValue() 	: null);
				debitCreditMemoSearchResultVO.setClientCode(object[i+=1]		!= null ? (String) 		object[i]			 	: null);
				debitCreditMemoSearchResultVO.setClientName(object[i+=1]		!= null ? (String) 		object[i]			 	: null);
				debitCreditMemoSearchResultVO.setUnitNo(object[i+=1]			!= null ? (String) 		object[i]			 	: null);
				debitCreditMemoSearchResultVO.setStatusCode(object[i+=1]		!= null ? (String) 		object[i]			 	: null);
				debitCreditMemoSearchResultVO.setStatus(object[i+=1]			!= null ? (String) 		object[i]			 	: null);
				debitCreditMemoSearchResultVO.setType(object[i+=1]				!= null ? (String) 		object[i]			 	: null);
				debitCreditMemoSearchResultVO.setCategoryCode(object[i+=1]		!= null ? (String) 		object[i]			 	: null);
				debitCreditMemoSearchResultVO.setCategoryDesc(object[i+=1]		!= null ? (String) 		object[i]			 	: null);
				debitCreditMemoSearchResultVO.setAnalysisCode(object[i+=1]		!= null ? (String) 		object[i]			 	: null);
				debitCreditMemoSearchResultVO.setAnalysisDesc(object[i+=1]		!= null ? (String) 		object[i]			 	: null);
				debitCreditMemoSearchResultVO.setSelectedApprover(object[i+=1]	!= null ? (String) 		object[i]			 	: null);
				debitCreditMemoSearchResultVO.setSubmitter(object[i+=1]			!= null ? (String) 		object[i]			 	: null);
				debitCreditMemoSearchResultVO.setSubmittedDate(object[i+=1]		!= null ? (Date) 		object[i]			 	: null);
				debitCreditMemoSearchResultVO.setApprover(object[i+=1]			!= null ? (String) 		object[i]			 	: null);
				debitCreditMemoSearchResultVO.setApprovedDate(object[i+=1]		!= null ? (Date) 		object[i]			 	: null);
				debitCreditMemoSearchResultVO.setProcessor(object[i+=1]			!= null ? (String) 		object[i]			 	: null);
				debitCreditMemoSearchResultVO.setProcessedDate(object[i+=1]		!= null ? (Date) 		object[i]			 	: null);
				debitCreditMemoSearchResultVO.setAllocator(object[i+=1]			!= null ? (String) 		object[i]			 	: null);
				debitCreditMemoSearchResultVO.setAllocatedDate(object[i+=1]		!= null ? (Date) 		object[i]			 	: null);
				debitCreditMemoSearchResultVO.setInvoiceNo(object[i+=1]			!= null ? (String) 		object[i]			 	: null);
				debitCreditMemoSearchResultVO.setNetAmount(object[i+=1]			!= null ? (BigDecimal)	object[i]			 	: null);
				debitCreditMemoSearchResultVO.setTotalAmount(object[i+=1]		!= null ? (BigDecimal)	object[i]			 	: null);
				debitCreditMemoSearchResultVO.setLineDescription(object[i+=1]	!= null ? (String) 		object[i]			 	: null);				
				debitCreditMemoSearchResultVO.setDriverAddressState(object[i+=1]!= null ? (String) 		object[i]			 	: null);
				debitCreditMemoSearchResultVO.setReason(object[i+=1]			!= null ? (String) 		object[i]			 	: null);
				debitCreditMemoSearchResultVO.setSource(object[i+=1]			!= null ? (String) 		object[i]			 	: null);
				debitCreditMemoSearchResultVO.setTicketNo(object[i+=1]			!= null ? (String) 		object[i]			 	: null);
				String warning = (String) object[i+=1];
				if(warning != null && warning.equalsIgnoreCase("Y")) {
					debitCreditMemoSearchResultVO.setWarningInd(true);
				} else {
					debitCreditMemoSearchResultVO.setWarningInd(false);
				}
				debitCreditMemoSearchResultVO.setClientUnit(object[i+=1]			!= null ? (Character) 		object[i]			 	: null);
				debitCreditMemoSearchResultVO.setTaxAmount(object[i+=1]				!= null ? (BigDecimal) 		object[i]			 	: null);
				debitCreditMemoSearchResultVO.setTransactionDate(object[i+=1]		!= null ? (Date) 			object[i]			 	: null);
				debitCreditMemoSearchResultVO.setRentApplicableDate(object[i+=1]	!= null ? (Date) 			object[i]			 	: null);
				debitCreditMemoSearchResultVO.setGlCode(object[i+=1]				!= null ? (String)	 		object[i]			 	: null);
				debitCreditMemoSearchResultVO.setInvoiceNote(object[i+=1]			!= null ? (String)	 		object[i]			 	: null);
				
				debitCreditMemoSearchResultList.add(debitCreditMemoSearchResultVO);
			}
		}
		
		return debitCreditMemoSearchResultList;
	}
	
	@SuppressWarnings("unchecked")
	private int  populateDebitCreditMemoSearchCount (Query query) {
		int count  = 0;	
		List<Object[]> objectList  = (List<Object[]>) query.getResultList();
		if (objectList != null) {	
			count = objectList.size();
		}
	
		return count;
		
	}
	
	@SuppressWarnings("unchecked")
	@Override  
	public List<DebitCreditInvoiceSearchResultVO> findDebitCreditInvoices(DebitCreditInvoiceSearchCriteriaVO searchCriteria) {
		List<DebitCreditInvoiceSearchResultVO> matchingInvoices = new ArrayList<DebitCreditInvoiceSearchResultVO>();
		
		Query query = generateSearchInvoicesQuery(searchCriteria);
		
		List<Object[]> resultList = (List<Object[]>)query.getResultList();	
		if(resultList != null){
			for(Object[] record : resultList){
				int i = 0;
				DebitCreditInvoiceSearchResultVO billingView = new DebitCreditInvoiceSearchResultVO();
				billingView.setInvoiceDate((Date)record[i]);
				billingView.setContractNo((String)record[i+=1]);
				billingView.setDocNo((String)record[i+=1]);
				billingView.setSourceCode((String)record[i+=1]);
				billingView.setCategoryCode((String)record[i+=1]);
				billingView.setAnalysisCode((String)record[i+=1]);
				billingView.setAnalysisCodeDesc((String)record[i+=1]);
				billingView.setInvoiceAmount((BigDecimal)record[i+=1]);
				billingView.setInvoiceTax((BigDecimal)record[i+=1]);
				billingView.setInvoiceTotalAmount((BigDecimal)record[i+=1]);
				billingView.setLineDocDate((Date)record[i+=1]);
				String userDef7 = (String)record[i+=1];
				billingView.setBillingPeriodKey(userDef7 != null ? Long.parseLong(userDef7)  : null);
				billingView.setLineDescription((String)record[i+=1]);
				billingView.setLineAmount((BigDecimal)record[i+=1]);
				billingView.setLineTax((BigDecimal)record[i+=1]);	
				billingView.setLineTotalAmount((BigDecimal)record[i+=1]);
				matchingInvoices.add(billingView);
			}
		}
	
		return matchingInvoices;	
	}
	
	private Query generateSearchInvoicesQuery(DebitCreditInvoiceSearchCriteriaVO searchCriteria) {
		
		StringBuilder stmt = new StringBuilder();	
		stmt.append("SELECT DISTINCT big.invoice_date, big.contract_no, big.doc_no, big.source_code, big.update_control_code, ad.analysis_code, ad.description, big.docl_total_price , big.docl_total_tax, "+
						" (big.docl_total_price + big.docl_total_tax) docl_unalloc_amount, dl.doc_date, dl.user_def7, dl.line_description,dl.unit_price, dl.unit_tax, nvl(dl.docl_total_price, (dl.total_price + dl.Unit_tax)) detail_line_total_amount ");
		
		
		stmt.append(" FROM "+
					" 		(	SELECT  account_code, contract_no, doc_no, source_code, doc_type, "+
					" 					SUM(docl_total_price) docl_total_price, "+
					" 					SUM(docl_total_tax) docl_total_tax, "+
					" 					SUM(docl_unalloc_amount) docl_unalloc_amount,  "+
					" 					doc_id, c_id, account_type, update_control_code, cdb_code_1, unit_no, invoice_date "+
					" 			FROM all_billing_view "+
					" 			WHERE cdb_code_1 = to_char(:fmsId) AND doc_type in ('INVOICEAR','CREDITAR') "+
					" 			AND update_control_code = :updateControlCode "+
					" 			AND source_code <> :sourceCode "+
					"			AND TRUNC(invoice_date) >= ADD_MONTHS(TRUNC(SYSDATE) ,-:invoiceOldInMonth) "+
					" 			GROUP BY	doc_id,account_code, c_id, account_type, doc_no, update_control_code, source_code, contract_no, "+
					" 	 					doc_type, unit_no, invoice_date,  cdb_code_1,hold_description "+
					" ) big, docl dl, dist di, docl_analysis da , analysis_codes ad "+ 
					" WHERE big.doc_id = dl.doc_id "+
					" AND TO_NUMBER(di.cdb_code_1) = :fmsId "+
					" AND dl.doc_id = di.docl_doc_id "+
					" AND dl.line_id = di.docl_line_id "+
					" AND big.source_code = dl.source_code "+
					" AND da.doc_id = dl.doc_id "+
					" AND da.line_id = dl.line_id "+	
					" AND da.category_id = :categoryId "+
					" AND da.category_id = ad.category_id "+
					" AND da.analysis_code = ad.analysis_code ");	    

		if(searchCriteria.getAnalysisCode() != null ){
	    	stmt.append(" AND da.analysis_code = :analysisCode ");	    	
	    }	
		
			
		stmt.append(" ORDER BY big.invoice_date DESC , big.doc_no DESC");
		
		Query query = entityManager.createNativeQuery(stmt.toString());
		
		query.setParameter("fmsId", searchCriteria.getFmsId());	
		query.setParameter("categoryId", searchCriteria.getAnalysisCategoryId());
		query.setParameter("invoiceOldInMonth", searchCriteria.getInvoiceOldInMonth());
		
		if(searchCriteria.getAnalysisCode() != null ){
			query.setParameter("analysisCode", searchCriteria.getAnalysisCode());    	
	    }		
		
		if(searchCriteria.getAnalysisCategory().equals("FLBILLING") 
				|| searchCriteria.getAnalysisCategory().equals("FLINFORMAL")){
			query.setParameter("updateControlCode", "FLBILLING");					
			query.setParameter("sourceCode", "FLMAINT");
		}else{
			query.setParameter("updateControlCode", "FLMISC");
			query.setParameter("sourceCode", "FLRECLAIM");
		}
		
		return query;

	}
	
	@Override
	public void createDCMemoInvoice(Long cId, Long dcNo, String employeeNo) throws MalException{
		Connection connection = null;
		CallableStatement callableStatement = null;
		
		try {
			connection = DataSourceUtils.getConnection(dataSource);
			
			callableStatement = connection.prepareCall("{ ? = call vision.fl_bill_wrapper.create_dc_memo_invoice(?,?,?) }");
			callableStatement.registerOutParameter(1, Types.VARCHAR);
			callableStatement.setLong(2, cId);
			callableStatement.setLong(3, dcNo);
			callableStatement.setString(4, employeeNo);
			
			callableStatement.execute();
			String resultString = callableStatement.getString(1);
			
			if (!MALUtilities.isEmpty(resultString)) {
				throw new MalException(resultString);
			}
			
		} catch (Exception ex) {
			throw new MalException(ex.getMessage());
		}
	}
	
	@SuppressWarnings("unchecked")
	public Map<Long,Date> getBillingPeriodDate(List<Long>  billingPeriodKeyList){		
		
		String sqlStmt = " SELECT sequence_no, start_date from time_periods where sequence_no in (:billingPeriodKeysChunk) "+
							" UNION "+
						 " SELECT qs.qsc_id, qs.trans_date " +
							" FROM QUOTATION_SCHEDULES qs " +
								" LEFT JOIN time_periods tp " +
								" ON qs.qsc_id = tp.sequence_no " +
							" WHERE tp.sequence_no IS NULL " +
							" AND qs.qsc_id in (:billingPeriodKeysChunk) ";
		
		Map<Long,Date> reqult = new HashMap<Long,Date>();
		
		Query query =  entityManager.createNativeQuery(sqlStmt);
		//some JDBC driver does not not support more than 100 entry in in expression clause so we are breaking it of in chunk of 500 
		List<List<Long>> billingPeriodKeyChunkList = getChunkList(billingPeriodKeyList, 500);
		for (List<Long> billingPeriodKeysChunk : billingPeriodKeyChunkList) {
			
			query.setParameter("billingPeriodKeysChunk", billingPeriodKeysChunk);			
			List<Object[]> objectList  = (List<Object[]>) query.getResultList();
		
			if (objectList != null && objectList.size() > 0) {
				
				for (Iterator<Object[]> iterator = objectList.iterator(); iterator.hasNext();) {
					Object[] object = iterator.next();				
					int i = 0;
					Long key  	= object[i] 	!= null ? ((BigDecimal) object[i]).longValue() 	: null;
					Date value  =  object[i+=1]	!= null ? (Date) 		object[i]			 	: null;
					if(! reqult.containsKey(key)){
						reqult.put(key, value);
					}
				}
			}
		
		}
		
	 return reqult;
	 
	}
	
	/**
     * Returns List of the List argument passed to this function with size = chunkSize     * 
     * @param largeList input list to be portioned
     * @param chunkSize maximum size of each partition
     * @param <T> Generic type of the List
     * @return A list of Lists which is portioned from the original list 
     */
    private <T> List<List<T>> getChunkList(List<T> largeList , int chunkSize) {
        List<List<T>> chunkList = new ArrayList<>();
        for (int i = 0 ; i <  largeList.size() ; i += chunkSize) {
            chunkList.add(largeList.subList(i , i + chunkSize >= largeList.size() ? largeList.size() : i + chunkSize));
        }
        return chunkList;
    }
    
}
