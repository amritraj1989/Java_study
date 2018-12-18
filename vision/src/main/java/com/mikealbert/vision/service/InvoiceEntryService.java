package com.mikealbert.vision.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.mikealbert.data.entity.Doc;
import com.mikealbert.data.entity.FleetMaster;
import com.mikealbert.data.entity.ReclaimLines;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.exception.MalException;
import com.mikealbert.vision.vo.InvoiceEntryVO;
import com.mikealbert.vision.vo.InvoiceLineVO;

public interface InvoiceEntryService {
	InvoiceEntryVO getInvoiceEntryHeader(String poNumber) throws MalBusinessException ;
	
	public Map<String, Object> saveInvoiceHeader(InvoiceEntryVO invoiceEntryVO,Long acceptedQmdId) throws MalBusinessException ;
	
	public Map<String, Object> getDueDatePaymentMethodAndDiscDate(Long cId, String accountCode, String accountType, String docType,
			String updateControlCode, String paymentTermsCode, String crtExtAccType, Date docDate, BigDecimal tpSeqNo, String updateInd,
			String paymentMethod) throws MalBusinessException ;
		
	public boolean createInvoiceDetails(FleetMaster fleetMaster, Doc releasedPO,Doc invoiceHeader) throws MalBusinessException, MalException;
	
	public List<ReclaimLines> createReclaims(Doc doc, FleetMaster fleetMaster, String employeeNo) throws MalBusinessException, MalException;
	
	public void updateInvoiceLineItems(List<InvoiceLineVO> invoiceLineVOList) throws MalBusinessException ;
	
	public void setupMalCapitalCost(Doc docToSave ,Long finalizeQmd , Long fmsId ,List<ReclaimLines> reclaimLines ) throws MalBusinessException;
	
	String	DOC_TYPE_INVOICEAP = "INVOICEAP";
	String	SOURCE_CODE_POINV	 = "POINV";
	String	EXCHANGE_RATE_FIXED	= "FIXED";
	String	DOC_TYPE_PORDER	= "PORDER";
	String	INV_DOC_STATUS_OPEN	= "O";
	String	INV_DOC_STATUS_POSTED	= "P";
	
	String 	KEY_TYPE_IGNITION = "IGNITION";
	String	DOC_DUE_DATE	= "DOC_DUE_DATE";
	String	DOC_PAYMENT_METHOD = "DOC_PAYMENT_METHOD";
	String	DOC_PAYMENT_TERM_CODE	= "DOC_PAYMENT_TERM_CODE";
	
	public	String	ERROR_TYPE_BLOCKER ="ERROR_TYPE_BLOCKER";
	public	String	ERROR_TYPE_WARNING ="ERROR_TYPE_WARNING";
	public	String	MESSAGE ="MESSAGE";
	public	String	INVOICE_HEADER_TOTAL ="INVOICE_HEADER_TOTAL";
	public	String	INVOICE_DETAIL_TOTAL ="INVOICE_DETAIL_TOTAL";
	public String	INVOICE_HEADER_DOC="INVOICE_HEADER_DOC";
	public String REVISED_QMD_ID= "REVISED_QMD_ID";
}
