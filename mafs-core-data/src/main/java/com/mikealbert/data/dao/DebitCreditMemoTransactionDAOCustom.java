package com.mikealbert.data.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.mikealbert.data.entity.DebitCreditMemoTransaction;
import com.mikealbert.data.vo.DebitCreditInvoiceSearchCriteriaVO;
import com.mikealbert.data.vo.DebitCreditInvoiceSearchResultVO;
import com.mikealbert.data.vo.DebitCreditMemoSearchCriteriaVO;
import com.mikealbert.data.vo.DebitCreditMemoSearchResultVO;
import com.mikealbert.exception.MalException;

public interface DebitCreditMemoTransactionDAOCustom {
	
	public Long getNextPK() throws MalException;
	public List<DebitCreditMemoSearchResultVO> findDebitCreditMemoTransactions(DebitCreditMemoSearchCriteriaVO searchCriteria, Pageable pageable, Sort sort);
	public int findDebitCreditMemoTransactionsCount(DebitCreditMemoSearchCriteriaVO searchCriteria);	
	public List<DebitCreditInvoiceSearchResultVO> findDebitCreditInvoices(DebitCreditInvoiceSearchCriteriaVO searchCriteria);
	public Map<Long,Date> getBillingPeriodDate(List<Long>  billingPeriodKeyList);
	public void createDCMemoInvoice(Long cId, Long dcNo, String employeeNo) throws MalException;

}
