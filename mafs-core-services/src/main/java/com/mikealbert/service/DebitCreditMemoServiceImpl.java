package com.mikealbert.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.mikealbert.common.MalLogger;
import com.mikealbert.common.MalLoggerFactory;
import com.mikealbert.data.dao.AnalysisCategoryDAO;
import com.mikealbert.data.dao.AnalysisCodeDAO;
import com.mikealbert.data.dao.DebitCreditMemoTransactionDAO;
import com.mikealbert.data.dao.DebitCreditMemoTypeDAO;
import com.mikealbert.data.dao.DebitCreditStatusCodeDAO;
import com.mikealbert.data.dao.DocDAO;
import com.mikealbert.data.dao.DoclDAO;
import com.mikealbert.data.dao.ExternalAccountDAO;
import com.mikealbert.data.dao.GlCodeDAO;
import com.mikealbert.data.dao.TimePeriodDAO;
import com.mikealbert.data.dao.WillowUserDAO;
import com.mikealbert.data.entity.AnalysisCategory;
import com.mikealbert.data.entity.AnalysisCode;
import com.mikealbert.data.entity.Contract;
import com.mikealbert.data.entity.DebitCreditMemoTransaction;
import com.mikealbert.data.entity.DebitCreditMemoType;
import com.mikealbert.data.entity.DebitCreditStatusCode;
import com.mikealbert.data.entity.Doc;
import com.mikealbert.data.entity.ExternalAccountPK;
import com.mikealbert.data.entity.FleetMaster;
import com.mikealbert.data.entity.GlCode;
import com.mikealbert.data.entity.GlCodePK;
import com.mikealbert.data.entity.PersonnelBase;
import com.mikealbert.data.entity.TimePeriod;
import com.mikealbert.data.enumeration.CorporateEntity;
import com.mikealbert.data.enumeration.DebitCreditStatusEnum;
import com.mikealbert.data.vo.DebitCreditInvoiceSearchCriteriaVO;
import com.mikealbert.data.vo.DebitCreditInvoiceSearchResultVO;
import com.mikealbert.data.vo.DebitCreditMemoSearchCriteriaVO;
import com.mikealbert.data.vo.DebitCreditMemoSearchResultVO;
import com.mikealbert.data.vo.DebitCreditTransactionVO;
import com.mikealbert.data.vo.DocumentFileVO;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.exception.MalException;
import com.mikealbert.util.MALUtilities;


@Service("debitCreditMemoService")
@Transactional
public class DebitCreditMemoServiceImpl implements DebitCreditMemoService{
	
	private MalLogger logger = MalLoggerFactory.getLogger(this.getClass());

	@Resource DebitCreditMemoTypeDAO debitCreditMemoTypeDAO;
	@Resource AnalysisCodeDAO analysisCodeDAO;
	@Resource AnalysisCategoryDAO analysisCategoryDAO;
	@Resource DebitCreditMemoTransactionDAO debitCreditMemoTransactionDAO;
	@Resource DebitCreditStatusCodeDAO debitCreditStatusCodeDAO;	
	@Resource WillowUserDAO willowUserDAO;
	@Resource DocDAO docDAO;
	@Resource DoclDAO doclDAO;
	@Resource FleetMasterService fleetMasterService;
	@Resource TimePeriodDAO timePeriodDAO;
	@Resource ExternalAccountDAO externalAccountDAO;
	@Resource WillowConfigService willowConfigService;	
	@Resource ContractService contractService;
	@Resource GlCodeService glCodeService; 	
	@Resource GlCodeDAO glCodeDao;
	
	private static final long ANALYSIS_CATEGORY_FLBILLING = 15; //"FLBILLING"; 	
	private static final long ANALYSIS_CATEGORY_FLINFORMAL = 21;//"FLINFORMAL"; 
	private static final String ACCOUNT_TYPE_CUSTOMER = "C";	
	private static final SimpleDateFormat dateFormatter = new SimpleDateFormat("MM/dd/yy", Locale.US);
	private static final String INVALID_DC_ID = "INVALID_DC_ID";
	private static final String RECORDS_PROCESSED = "RECORDS_PROCESSED_SUCCESSFULLY";
	
	@Override
	public List<DebitCreditMemoType> getDebitCreditMemoTypes() {
		return debitCreditMemoTypeDAO.getDebitCreditMemoTypeList();
	}
	
	@Override
	public DebitCreditMemoType getDebitCreditMemoType(Long dcmtId) {
		return debitCreditMemoTypeDAO.findById(dcmtId).orElse(null);
	}

	@Override
	public List<AnalysisCode> getAnalysisCodes(long categoryId) {
		return analysisCodeDAO.findByCategoryId(categoryId);
	}

	@Override
	public List<AnalysisCategory> getAnalysisCategories() {
		return analysisCategoryDAO.findByDebitCreditMemoInd();
	}

	@Override
	public List<DebitCreditStatusCode> getDebitCreditStatusList() {
		return debitCreditStatusCodeDAO.findDebitCreditStatusList();
	}	
	
	public List<DebitCreditMemoTransaction>  getDebitCreditMemoForRentApplicableDate(String unitNo, Long categoryId, String analysisCode, BigDecimal netAmount, Date rentApplicableDate, ExternalAccountPK clientPk, Long dcmtId ){
		return debitCreditMemoTransactionDAO.findDebitCreditMemoForRentApplicableDate(unitNo, categoryId, analysisCode, netAmount, rentApplicableDate, clientPk, dcmtId );
	}
	
	public List<DebitCreditMemoTransaction>  getDebitCreditMemoWithoutRentApplicableDate(String unitNo, Long categoryId, String analysisCode, BigDecimal netAmount, ExternalAccountPK clientPk, Long dcmtId ){
		return debitCreditMemoTransactionDAO.findDebitCreditMemoWithoutRentApplicableDate(unitNo, categoryId, analysisCode, netAmount, clientPk, dcmtId );
	}

	public List<DebitCreditMemoSearchResultVO> getDebitCreditMemoTransactions(DebitCreditMemoSearchCriteriaVO searchCriteria, Pageable pageable, Sort sort){
		return debitCreditMemoTransactionDAO.findDebitCreditMemoTransactions(searchCriteria, pageable, sort);
	}
	
	public int getDebitCreditMemoTransactionsCount(DebitCreditMemoSearchCriteriaVO searchCriteria){
		return debitCreditMemoTransactionDAO.findDebitCreditMemoTransactionsCount(searchCriteria);
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public DebitCreditMemoTransaction saveOrUpdateDebitCreditMemo(DebitCreditMemoTransaction debitCreditMemoTransaction) throws MalException {
		try {
			return debitCreditMemoTransactionDAO.save(debitCreditMemoTransaction);
		} catch (Exception ex) {
			logger.error(ex);
			throw new MalException("generic.error.occured.while", new String[] { "saving debit credit memo transaction" }, ex);
		}
	}

	@Override
	public List<PersonnelBase> getDebitCreditApproverList() {
		List<PersonnelBase> debitCreditUserNamesList = willowUserDAO.getDebitCreditApproverList();
		return debitCreditUserNamesList;
	}
	
	@Transactional(rollbackFor = Exception.class)
	public void deleteDebitCreditMemoTransaction(DebitCreditMemoTransaction debitCreditMemoTransaction){
		debitCreditMemoTransactionDAO.delete(debitCreditMemoTransaction);
	}
	
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public DebitCreditMemoTransaction getDebitCreditMemoTransaction(Long dcNoPk){
		return debitCreditMemoTransactionDAO.findById(dcNoPk).orElse(null);
	}
	
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public Map<String,Long> approveDebitCreditMemoTransactionList(List<Long> dcNoList, String approveByEmpNo) throws MalException{
		try{
			Long recordProcessed = 0L;
			Map<String,Long> invalidRecords = new HashMap<String,Long>();
			List<DebitCreditMemoTransaction> debitCreditMemoTransactionList =  new ArrayList<DebitCreditMemoTransaction>();
			for (Long dcNo : dcNoList) {
				DebitCreditMemoTransaction debitCreditMemoTransaction = debitCreditMemoTransactionDAO.findById(dcNo).orElse(null);
				if(!approveByEmpNo.equalsIgnoreCase(debitCreditMemoTransaction.getSubmitter())){
					debitCreditMemoTransaction.setApprovedDate(new Date());
					debitCreditMemoTransaction.setApprover(approveByEmpNo);
					debitCreditMemoTransaction.setStatus(DebitCreditStatusEnum.STATUS_APPROVED.getCode());			
					debitCreditMemoTransactionList.add(debitCreditMemoTransaction);
					recordProcessed +=1;
				}else{
					debitCreditMemoTransactionDAO.saveAll(debitCreditMemoTransactionList);
					invalidRecords.put(INVALID_DC_ID,dcNo);
					invalidRecords.put(RECORDS_PROCESSED,recordProcessed);
					return invalidRecords;
				}				
			}
			debitCreditMemoTransactionDAO.saveAll(debitCreditMemoTransactionList);
			return null;
		} catch (Exception ex) {
			logger.error(ex);
			throw new MalException("generic.error.occured.while", new String[] { "approving debit credit memo transactions" }, ex);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void allocateDebitCreditMemoTransactionList(List<Long> dcNoList, String allocatedByEmpNo) throws MalException {
		try{
			List<DebitCreditMemoTransaction> debitCreditMemoTransactionList =  new ArrayList<DebitCreditMemoTransaction>();
			for (Long dcNo : dcNoList) {
				DebitCreditMemoTransaction debitCreditMemoTransaction = debitCreditMemoTransactionDAO.findById(dcNo).orElse(null);
				debitCreditMemoTransaction.setStatus(DebitCreditStatusEnum.STATUS_COMPLETED.getCode());	
				debitCreditMemoTransaction.setAllocator(allocatedByEmpNo);
				debitCreditMemoTransaction.setAllocatedDate(new Date());
				
				debitCreditMemoTransactionList.add(debitCreditMemoTransaction);
			}
			
			debitCreditMemoTransactionDAO.saveAll(debitCreditMemoTransactionList);
		} catch (Exception ex) {
			logger.error(ex);
			throw new MalException("generic.error.occured.while", new String[] { "allocating debit credit memo transactions" }, ex);
		}			
		
	}	

	@Override
	public List<DebitCreditInvoiceSearchResultVO> searchInvoices(DebitCreditInvoiceSearchCriteriaVO searchCriteria) {	
		List<DebitCreditInvoiceSearchResultVO>  invoiceList  =  debitCreditMemoTransactionDAO.findDebitCreditInvoices(searchCriteria);	
		Set<Long>  billingPeriodKeyList  = new HashSet<Long>();//used set to remove duplicates
		for (DebitCreditInvoiceSearchResultVO debitCreditInvoiceSearchResultVO : invoiceList) {
			if(debitCreditInvoiceSearchResultVO.getBillingPeriodKey() != null){
				billingPeriodKeyList.add(debitCreditInvoiceSearchResultVO.getBillingPeriodKey());
			}
		}		
		Map<Long,Date> billingPeriodInfoMap =  debitCreditMemoTransactionDAO.getBillingPeriodDate(new ArrayList<Long>(billingPeriodKeyList));		 
		for (DebitCreditInvoiceSearchResultVO debitCreditInvoiceSearchResultVO : invoiceList) {//updating object with Billing Period Date
			if(debitCreditInvoiceSearchResultVO.getBillingPeriodKey() != null && billingPeriodInfoMap.containsKey(debitCreditInvoiceSearchResultVO.getBillingPeriodKey())){
				debitCreditInvoiceSearchResultVO.setBillingPeriodDate(billingPeriodInfoMap.get(debitCreditInvoiceSearchResultVO.getBillingPeriodKey()));
			}
		}
		
		return invoiceList;
	}
	
	
	@Override
	public BigDecimal findTotalAmountForUnPostedInvoiceNo(String invoiceNo) {
		return doclDAO.findTotalAmountForUnPostedInvoiceNo(invoiceNo);
	}

	@Override
	public List<Doc> findPostedInvoiceForDebitCredit(String invoiceNo, String accountCode, List<String>  docTypes,  List<String>  status){
		return docDAO.findDocByDocNoAndClientAndTypesAndStatus(invoiceNo, accountCode, docTypes,  status);
	}
	@Override
	public boolean isInvoiceNoIsValid(String invoiceNo, String accountCode){
		String[] docTypes =  {"INVOICEAR", "CREDITAR", "DEBITAR", "RECEIPT"};
		String[] status =  {"O", "P"};		
		List<Doc> docList = docDAO.findDocByDocNoAndClientAndTypesAndStatus(invoiceNo, accountCode, Arrays.asList(docTypes) , Arrays.asList(status) );
		if(docList != null && docList.size() > 0){		
			return true;
		}else{
			return false;
		}
		
	}	
	
	@Override
	public void processDebitCreditMemo(String empNo, DebitCreditMemoTransaction dcMemoTransactionInput) throws MalException {
		String[] docTypes =  {"INVOICEAR", "CREDITAR", "DEBITAR", "RECEIPT"};
		String[] status =  {"P"};		
		
		debitCreditMemoTransactionDAO.createDCMemoInvoice(dcMemoTransactionInput.getExternalAccount().getExternalAccountPK().getcId(), 
															dcMemoTransactionInput.getDcNo() , empNo);
		
		BigDecimal totalAmount = BigDecimal.ZERO;
		String statusCode = DebitCreditStatusEnum.STATUS_COMPLETED.getCode();
		if (!MALUtilities.isEmpty(dcMemoTransactionInput.getInvoiceNo())){	
			//check NON-POSTED invoices - ARP14
			totalAmount = findTotalAmountForUnPostedInvoiceNo(dcMemoTransactionInput.getInvoiceNo());
			//check POSTED invoices - ARE1
			if (totalAmount == null || totalAmount.compareTo(BigDecimal.ZERO) == 0){
			
				List<Doc> postedInvoice = findPostedInvoiceForDebitCredit(dcMemoTransactionInput.getInvoiceNo(),dcMemoTransactionInput.getExternalAccount().getExternalAccountPK().getAccountCode(),
						Arrays.asList(docTypes), Arrays.asList(status));
				
				if(postedInvoice != null && postedInvoice.size() > 1){
					throw new MalException("service.validation", new String[] { "Multiple documents exist for the entered invoice number" });
				}
				if (!MALUtilities.isEmpty(postedInvoice)){
					totalAmount = postedInvoice.get(0).getUnallocAmount();
				}
			}
			
			if (totalAmount != null && totalAmount.compareTo(BigDecimal.ZERO) != 0){
				statusCode = DebitCreditStatusEnum.STATUS_PROCESSED.getCode();
			} 
		}
		
		
		DebitCreditMemoTransaction dbDCMemoTransaction = debitCreditMemoTransactionDAO.findById(dcMemoTransactionInput.getDcNo()).orElse(null);
		dbDCMemoTransaction.setProcessor(empNo);
		dbDCMemoTransaction.setProcessedDate(new Date());
		dbDCMemoTransaction.setStatus(statusCode);
		
		saveOrUpdateDebitCreditMemo(dbDCMemoTransaction);
	}
	
	@Override
	public DebitCreditMemoType convertDebitCreditMemoType(String debitCreditType) {
		DebitCreditMemoType debitCreditMemoType = null;
		for (DebitCreditMemoType dcType : getDebitCreditMemoTypes()) {
			if(dcType.getDebitCreditType().equals(debitCreditType)){
				debitCreditMemoType = dcType;
				break;
			}
		}
		return debitCreditMemoType;
	}

	@Override
	public AnalysisCategory convertAnalysisCategory(String category) {
		AnalysisCategory analysisCategory = null;
		for (AnalysisCategory ac : getAnalysisCategories()){
			if (ac.getAnalysisCategory().equals(category)){
				analysisCategory = ac;
				break;
			}
		}	
		return analysisCategory;
	}
	
    /*
     * Defined as currently on contract, on order, or was ever on contract for the specified account (RE-649)
     */
	@Override
    public boolean isCustomerUnit(String accountCode, String unitNo){
    	boolean isCustomerUnit = false;
    	
    	if (unitNo.equals(willowConfigService.getConfigValue("GENERIC_UNIT"))){
    		isCustomerUnit = true; //RE-938 accept unit #99999999 for any client
    		return isCustomerUnit;
    	}
    	
    	FleetMaster fleetMaster = fleetMasterService.findByUnitNo(unitNo);
    	if(fleetMaster != null) {
    		List<Contract> contracts = new ArrayList<Contract>();
    		
    		try {
	    		contracts = contractService.getContracts(fleetMaster);
	    		if(contracts != null && contracts.size() > 0){
		    		for(Contract c : contracts){	
		    			if(accountCode.equals(c.getExternalAccount().getExternalAccountPK().getAccountCode())){
		    				isCustomerUnit = true;
		    			}
		    		}
	    		} else {
	    			isCustomerUnit = false;
	    		}
    		}catch(Exception ex){
    			isCustomerUnit = false;
    		}	    		
    	}
		
    	return isCustomerUnit;
    }
    
	@Override
    public boolean isValidDebitCreditAnalysisCode(Long categoryId, String analysisCode){
    	boolean isFound = false;

    	for (AnalysisCode ac : getAnalysisCodes(categoryId)){
			if (ac.getId().getAnalysisCode().equals(analysisCode)){
				isFound = true;
				break;
			}
		}
		return isFound;    	
    }
    
	@Override
	public boolean isValidDebitCreditApprover(String requestedApprover){
		boolean isFound = false;

		for (PersonnelBase personnelBase : getDebitCreditApproverList()) {
			if(personnelBase.getEmployeeNo().equals(requestedApprover)){
				isFound = true;
				break;
			}
		}
		return isFound;
	}
	
	@Override
	public boolean isApproverRequired(String submitter){
		boolean isRequired = true;

		for (PersonnelBase personnelBase : getDebitCreditApproverList()) {
			if(personnelBase.getEmployeeNo().equals(submitter)){
				isRequired = false;
				break;
			}
		}
		return isRequired;
	}	
	
	@Override
	public boolean isValidSubmitter(String submitter){
		PersonnelBase personnelBase = willowUserDAO.findById(submitter).orElse(null);

		if(personnelBase != null){
			return true;
		}
		return false;
	}
		
	@Override
    public boolean isValidGlCode(String glCode){
    	boolean isValid = true;

		try {
			GlCodePK glPk = new GlCodePK();
			glPk.setCode(glCode);
			glPk.setCorpId(CorporateEntity.MAL.getCorpId());
			GlCode gl = glCodeDao.findById(glPk).orElse(null);			
			if(gl == null){ 
				isValid = false;
			}
		}catch(Exception ex){
			isValid = false;
		}
		
		return isValid;
    }  
	
	@Override
	public boolean isDuplicateTransaction(Long dcNo, String unitNo, Long categoryId, String analysisCode, BigDecimal netAmount, 
			Date rentApplicableDate, ExternalAccountPK clientPk, Long dcmtId) {
		
		List<DebitCreditMemoTransaction> dcMemoTransactionList = null;
		boolean isDuplicate = false;
		
		if(MALUtilities.isEmpty(rentApplicableDate)){
			dcMemoTransactionList  = getDebitCreditMemoWithoutRentApplicableDate(unitNo, categoryId, analysisCode, netAmount, clientPk, dcmtId);
		} else {
			dcMemoTransactionList  = getDebitCreditMemoForRentApplicableDate(unitNo, categoryId, analysisCode, netAmount, rentApplicableDate, clientPk, dcmtId );
		}
		if(dcMemoTransactionList != null && dcMemoTransactionList.size() > 0){
			if(! (dcMemoTransactionList.size() == 1 &&  dcMemoTransactionList.get(0).getDcNo().equals(dcNo))){
			    isDuplicate = true;
			}
		}
		return isDuplicate;
	}	
	
	@Override
	public DebitCreditMemoTransaction convertDebitCreditMemoTransaction(DebitCreditTransactionVO dcVO, boolean securedFile) throws Exception{
		DebitCreditMemoTransaction dcmt = new DebitCreditMemoTransaction();
		dateFormatter.setLenient(false);

		dcmt.setDcNo(debitCreditMemoTransactionDAO.getNextPK());
		dcmt.setTaxOnly("N");
		dcmt.setReason(dcVO.getReason());
		dcmt.setExternalAccount(externalAccountDAO.findByAccountCodeAndAccountTypeAndCId(dcVO.getAccountCode(), ACCOUNT_TYPE_CUSTOMER, CorporateEntity.MAL.getCorpId()));
		dcmt.setUnitNo(dcVO.getUnitNo());
	
		if (dcVO.getIsClientUnit().equalsIgnoreCase("Y") || dcVO.getIsClientUnit().equalsIgnoreCase("N")){
			dcmt.setIsClientUnit(dcVO.getIsClientUnit().toUpperCase());
		} else {
			throw new MalBusinessException("Unit Belongs to Client must be Y or N");			
		}
		
		dcmt.setTransactionDate(dateFormatter.parse(dcVO.getTransactionDate()));
		dcmt.setInvoiceNo(dcVO.getInvoiceNo());
		dcmt.setGlCode(dcVO.getGlCode());
		dcmt.setInvoiceNote(dcVO.getInvoiceNote());
		dcmt.setTicketNo(dcVO.getTicketNo());
		dcmt.setStatus("S"); 
		dcmt.setSource(dcVO.getFileName());
		
		if(MALUtilities.isEmpty(dcVO.getSubmitter())){
			throw new MalBusinessException("Submitter is required");
		} else {
			if (!securedFile && !isValidSubmitter(dcVO.getSubmitter().toUpperCase())){
				throw new MalBusinessException("Invalid Submitter");
			} else {
				dcmt.setSubmitter(dcVO.getSubmitter().toUpperCase());
			}
		}	
				
		dcmt.setSubmittedDate(dateFormatter.parse(dcVO.getSubmittedDate()));
		dcmt.setLastModifiedBy(dcmt.getSubmitter()); 
		dcmt.setLastModifiedDate(new Date());		
		
		if(!securedFile) {
			if (isApproverRequired(dcmt.getSubmitter())) {
				if (MALUtilities.isEmpty(dcVO.getSelectedApprover())){
					throw new MalBusinessException("Requested Approver is required");
				} else {
					if (!isValidDebitCreditApprover(dcVO.getSelectedApprover().toUpperCase())){
						throw new MalBusinessException("Invalid Requested Approver");
					} else {
						dcmt.setSelectedApprover(dcVO.getSelectedApprover().toUpperCase());
					}
				}
			} else {
				if (!MALUtilities.isEmpty(dcVO.getSelectedApprover())){
					if (!isValidDebitCreditApprover(dcVO.getSelectedApprover().toUpperCase())){
						throw new MalBusinessException("Invalid Requested Approver");
					} else {
						dcmt.setSelectedApprover(dcVO.getSelectedApprover().toUpperCase());
					}
				}
			}			
		}
		
		if (!MALUtilities.isNumeric(dcVO.getNetAmount())){
			throw new MalBusinessException("Net Amount must be positive number without special characters");
		} else {
			BigDecimal netAmount = new BigDecimal(dcVO.getNetAmount());
			if (netAmount.compareTo(BigDecimal.ZERO) < 0) {
				throw new MalBusinessException("Net Amount must be positive number");
			}
			dcmt.setNetAmount(netAmount); 
		}

		if (MALUtilities.isEmptyString(dcVO.getTaxAmount())){
			dcmt.setTaxAmount(null); 
		} else {
			if (!MALUtilities.isNumeric(dcVO.getTaxAmount())){
				throw new MalBusinessException("Tax Amount must be positive number without special characters");
			}
			BigDecimal taxAmount = new BigDecimal(dcVO.getTaxAmount());
			if (taxAmount.compareTo(BigDecimal.ZERO) < 0) {
				throw new MalBusinessException("Tax Amount must be positive number");
			} else {
				dcmt.setTaxAmount(new BigDecimal(dcVO.getTaxAmount())); 
			}
		}
		
		BigDecimal tax = dcmt.getTaxAmount() != null ? dcmt.getTaxAmount() : BigDecimal.ZERO;

		BigDecimal total = dcmt.getNetAmount().add(tax);
		if(total.compareTo(new BigDecimal(99999.99)) < 1) {
			dcmt.setTotalAmount(total);		
		} else {
			throw new MalBusinessException("Total amount (net + tax) must be below $100,000");
		}

		DebitCreditMemoType debitCreditMemoType = convertDebitCreditMemoType(dcVO.getDebitCreditType().toUpperCase());
		if (debitCreditMemoType != null){
			dcmt.setDcmtDcmtId(debitCreditMemoType.getDcmtId());
		} else {
			throw new MalBusinessException("Invalid or missing Debit Credit Memo Type");
		}
		
		AnalysisCategory analysisCategory = convertAnalysisCategory(dcVO.getCategory().toUpperCase());
		if (analysisCategory != null){
			dcmt.setCategoryId(analysisCategory.getCategoryId());
		} else {
			throw new MalBusinessException("Invalid or missing Analysis Category");
		}
		
		if (dcmt.getCategoryId() != null && (dcmt.getCategoryId() == ANALYSIS_CATEGORY_FLBILLING || dcmt.getCategoryId() == ANALYSIS_CATEGORY_FLINFORMAL)){
			dcmt.setRentApplicableDate(dateFormatter.parse(dcVO.getRentApplicableDate()));
		} else {
			dcmt.setRentApplicableDate(null);
		}
		
		if (MALUtilities.isEmpty(dcVO.getAnalysisCode())){ 
			throw new MalBusinessException("Analysis Code is required");
		} else {
			if (!isValidDebitCreditAnalysisCode(dcmt.getCategoryId(), dcVO.getAnalysisCode().toUpperCase())){
				throw new MalBusinessException("Invalid Analysis Code");
			} else {
				dcmt.setAnalysisCode(dcVO.getAnalysisCode().toUpperCase());
			}
		}
		
		dcmt.setLineDescription(dcmt.getUnitNo() + "-" + dcmt.getAnalysisCode() + "-" + "DC " + dcmt.getDcNo() + "-" + dcVO.getLineDescription());	
		
		if(!MALUtilities.isEmpty(dcmt.getRentApplicableDate())){
			TimePeriod timePeriod = timePeriodDAO.findByStartDateAndContextId(dcmt.getRentApplicableDate(), CorporateEntity.MAL.getCorpId());
			if (timePeriod == null){
				throw new MalBusinessException("Cannot find Time Period for Rent Applicable Date");
			} else {
				dcmt.setTpSeqNoAr(timePeriod.getSequenceNo());	
			}
		}else{
			dcmt.setTpSeqNoAr(null);	
		}	
		
		return dcmt;
	}	
	
	@Override
	public boolean validateDebitCreditMemoTransaction(DebitCreditMemoTransaction debitCreditMemoTransaction) {
		boolean isValid = true;
		
		if (MALUtilities.isEmpty(debitCreditMemoTransaction.getReason())){
			isValid = false;
		}
		
		if (debitCreditMemoTransaction.getExternalAccount() == null || MALUtilities.isEmpty(debitCreditMemoTransaction.getExternalAccount().getExternalAccountPK().getAccountCode())){
			isValid = false;
		}
		
		if (MALUtilities.isEmpty(debitCreditMemoTransaction.getUnitNo())){
			isValid = false;
    	} else {
			FleetMaster fleetMaster = fleetMasterService.findByUnitNo(debitCreditMemoTransaction.getUnitNo());
			if (fleetMaster == null) {
				isValid = false;
			} 
    	}	
		
		if (debitCreditMemoTransaction.getExternalAccount() != null && !MALUtilities.isEmpty(debitCreditMemoTransaction.getExternalAccount().getExternalAccountPK().getAccountCode()) && !MALUtilities.isEmpty(debitCreditMemoTransaction.getUnitNo())){
			if (debitCreditMemoTransaction.getIsClientUnit().equalsIgnoreCase("Y") || debitCreditMemoTransaction.getIsClientUnit().equalsIgnoreCase("N")){
				if (debitCreditMemoTransaction.getIsClientUnit().equalsIgnoreCase("Y")){
					if(!isCustomerUnit(debitCreditMemoTransaction.getExternalAccount().getExternalAccountPK().getAccountCode(),debitCreditMemoTransaction.getUnitNo())){
						isValid = false;
					}
				}
			} else {
				isValid = false;
			}
		}
		
		if(!MALUtilities.isEmpty(debitCreditMemoTransaction.getRentApplicableDate())){ 
			Calendar c = new GregorianCalendar();
		    c.setTime(debitCreditMemoTransaction.getRentApplicableDate());
		    if (c.get(Calendar.DAY_OF_MONTH) != 1) { 
				isValid = false;					
			}
		}
		
		if(!MALUtilities.isEmpty(debitCreditMemoTransaction.getGlCode())){
			if(!isValidGlCode(debitCreditMemoTransaction.getGlCode())){
				isValid = false;
			}
		}		
		
		if (MALUtilities.isNotEmptyString(debitCreditMemoTransaction.getInvoiceNo()) && debitCreditMemoTransaction.getExternalAccount() != null){
			boolean isInvoiceNoIsValid = isInvoiceNoIsValid(debitCreditMemoTransaction.getInvoiceNo(), debitCreditMemoTransaction.getExternalAccount().getExternalAccountPK().getAccountCode());
			if(!isInvoiceNoIsValid){
				isValid = false;
			}
		}

		return isValid;
	}
	
    public void copyDebitCreditUploadFile(DocumentFileVO documentFileVO, String debitCreditUploadDir) throws MalException {
    	String destination=debitCreditUploadDir;
    	OutputStream outStream = null;
    	
    	try {
    		outStream = new FileOutputStream(new File(destination + documentFileVO.getFileName()));
    		outStream.write(documentFileVO.getFileData());
			logger.info("File :" + documentFileVO.getFileName() + " created successfully");			
	        
	        outStream.flush();
	        outStream.close();
	        outStream = null; 
			documentFileVO = null;

		} catch (Exception ex) {
			logger.error(ex);
			throw new MalException("generic.error.occured.while", new String[] { "uploading Debit Credit file" }, ex);
		}    	
    }

	@Override
	public List<DebitCreditTransactionVO> requestDebitCreditUploadErrors(String url) throws MalException {
		List<DebitCreditTransactionVO> debitCreditTransactionVoErrors = new ArrayList<DebitCreditTransactionVO>();
		RestTemplate restTemplate = new RestTemplate();
		
		try {	
			debitCreditTransactionVoErrors = Arrays.asList(restTemplate.getForObject(url, DebitCreditTransactionVO[].class));
		} catch (Exception e) {
			logger.error(e);
			throw new MalException("generic.error.occured.while", new String[] { "requesting Debit Credit Error file" }, e);
		}
		return debitCreditTransactionVoErrors;
	}

	@Override
	public void removeDebitCreditErrorsFromQueue(List<String> jmsIdList, String url) throws MalException {

		RestTemplate restTemplate = new RestTemplate();
		final String uri = url + "/{jmsMessageId}";	
		Map<String,String> params = new HashMap<String, String>();
		DebitCreditTransactionVO debitCreditTransactionVoError = new DebitCreditTransactionVO();
    
		try{
			for (String jmsId : jmsIdList) {
			    params.put("jmsMessageId", jmsId);
				restTemplate.delete(uri, params);	

//TODO: Hack to check that it was deleted from queue.  Jason and Ed discussed creating Tech Debt item to investigate other framework implementation. 	
				debitCreditTransactionVoError = restTemplate.getForObject(uri, DebitCreditTransactionVO.class, params);
			
				if (debitCreditTransactionVoError != null){
					logger.error(null,"Error deleting jmsId: " + jmsId);
					throw new MalException("generic.error.occured.while", new String[] { "deleting selected record(s) from queue" });
				}
			}
		} catch (Exception e) {
			logger.error(e);
			throw new MalException(e.getMessage());
		}			
	}

}
