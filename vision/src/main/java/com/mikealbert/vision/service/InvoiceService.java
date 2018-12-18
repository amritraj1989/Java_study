package com.mikealbert.vision.service;

import java.util.Map;

import com.mikealbert.data.entity.Doc;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.exception.MalException;


public interface InvoiceService {	
	
	public boolean postInvoice(Doc poOrderdoc,Doc invoiceDoc , String employeeNo, long contextId) throws MalBusinessException,MalException ;
	
	public void postInvoiceTALNotification(Doc docId, long contextId) throws MalBusinessException,MalException ;
	
	public Map<String, Object> prePostInvoiceValidations(Long invoiceDocId) throws MalBusinessException ;
	
	public void deleteInvoice(String poNumber,Long invoiceId) throws MalBusinessException ;
	
	public static final String PUR_LEASE_BACK_DEALER = "PUR_LEASE_BACK_DEALER";
	public static final String TXN_140 = "140";	
	public static final String TXN_525 = "525";
	public static final String TXN_155 = "155";
	public static final String TXN_220 = "220";
	public static final String STOCK = "STOCK";
	public static final String WHOLESALE = "WHOLESALE";
	public static final String FLORDER = "FLORDER";
	public static final String FLQUOTE = "FLQUOTE";
	public static final String GARAGED = "GARAGED";	
	public static final String POST_INV_TRX_SOURCE = "FLPO002";
	
	
}
