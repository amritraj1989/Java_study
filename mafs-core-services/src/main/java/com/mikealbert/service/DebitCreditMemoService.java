package com.mikealbert.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.mikealbert.data.entity.AnalysisCategory;
import com.mikealbert.data.entity.AnalysisCode;
import com.mikealbert.data.entity.DebitCreditMemoTransaction;
import com.mikealbert.data.entity.DebitCreditMemoType;
import com.mikealbert.data.entity.DebitCreditStatusCode;
import com.mikealbert.data.entity.Doc;
import com.mikealbert.data.entity.ExternalAccountPK;
import com.mikealbert.data.entity.PersonnelBase;
import com.mikealbert.data.vo.DebitCreditInvoiceSearchCriteriaVO;
import com.mikealbert.data.vo.DebitCreditInvoiceSearchResultVO;
import com.mikealbert.data.vo.DebitCreditMemoSearchCriteriaVO;
import com.mikealbert.data.vo.DebitCreditMemoSearchResultVO;
import com.mikealbert.data.vo.DebitCreditTransactionVO;
import com.mikealbert.data.vo.DocumentFileVO;
import com.mikealbert.exception.MalException;

public interface DebitCreditMemoService {

	public List<DebitCreditMemoType> getDebitCreditMemoTypes();
	public DebitCreditMemoType getDebitCreditMemoType(Long dcmtId);
	public List<AnalysisCode> getAnalysisCodes(long categoryId);	
	public List<AnalysisCategory> getAnalysisCategories();	
	public List<DebitCreditStatusCode> getDebitCreditStatusList();	
	public List<DebitCreditMemoTransaction>  getDebitCreditMemoForRentApplicableDate(String unitNo, Long categoryId, String analysisCode, BigDecimal netAmount, Date rentApplicableDate, ExternalAccountPK clientPk, Long dcmtId );
	public List<DebitCreditMemoTransaction>  getDebitCreditMemoWithoutRentApplicableDate(String unitNo, Long categoryId, String analysisCode, BigDecimal netAmount, ExternalAccountPK clientPk, Long dcmtId );
	public List<DebitCreditMemoSearchResultVO> getDebitCreditMemoTransactions(DebitCreditMemoSearchCriteriaVO searchCriteria, Pageable pageable, Sort sort);	
	public int getDebitCreditMemoTransactionsCount(DebitCreditMemoSearchCriteriaVO searchCriteria);
	public DebitCreditMemoTransaction saveOrUpdateDebitCreditMemo(DebitCreditMemoTransaction debitCreditMemoTransaction) throws MalException;
	public List<PersonnelBase> getDebitCreditApproverList();
	public void deleteDebitCreditMemoTransaction(DebitCreditMemoTransaction debitCreditMemoTransaction);
	public DebitCreditMemoTransaction getDebitCreditMemoTransaction(Long dcNoPk);
	public Map<String,Long> approveDebitCreditMemoTransactionList(List<Long> dcNoList, String approveByEmpNo) throws MalException;
	public void allocateDebitCreditMemoTransactionList(List<Long> dcNoList, String allocatedByEmpNo)  throws MalException;
	public List<DebitCreditInvoiceSearchResultVO> searchInvoices(DebitCreditInvoiceSearchCriteriaVO searchCriteria);
	public BigDecimal findTotalAmountForUnPostedInvoiceNo(String invoiceNo);
	public List<Doc> findPostedInvoiceForDebitCredit(String invoiceNo, String accountCode, List<String>  docTypes,  List<String>  status);	
	public void processDebitCreditMemo(String empNo, DebitCreditMemoTransaction dcMemoTransaction) throws MalException ;
	public boolean isInvoiceNoIsValid(String invoiceNo, String accountCode);
	public DebitCreditMemoTransaction convertDebitCreditMemoTransaction(DebitCreditTransactionVO debitCreditTransactionVO, boolean securedFile) throws Exception;
	public DebitCreditMemoType convertDebitCreditMemoType(String debitCreditType);
	public AnalysisCategory convertAnalysisCategory(String analysisCategory);
	public boolean isCustomerUnit(String accountCode, String unitNo);
	public boolean isValidDebitCreditAnalysisCode(Long categoryId, String analysisCode);
	public boolean isValidDebitCreditApprover(String requestedApprover);
	public boolean isValidSubmitter(String submitter);
	public boolean isApproverRequired(String submitter);
	public boolean isValidGlCode(String glCode);
	public boolean isDuplicateTransaction(Long dcNo, String unitNo, Long categoryId, String analysisCode, BigDecimal netAmount, Date rentApplicableDate, ExternalAccountPK clientPk, Long dcmtId );
	public boolean validateDebitCreditMemoTransaction(DebitCreditMemoTransaction debitCreditMemoTransaction);
    public void copyDebitCreditUploadFile(DocumentFileVO documentFileVO, String debitCreditUploadDir) throws MalException;	
    public List<DebitCreditTransactionVO> requestDebitCreditUploadErrors(String debitCreditProcessingBaseURL) throws MalException;
    public void removeDebitCreditErrorsFromQueue(List<String> dcNoList, String debitCreditProcessingBaseURL) throws MalException;

}
