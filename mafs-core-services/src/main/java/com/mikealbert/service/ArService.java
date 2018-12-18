package com.mikealbert.service;

import java.util.Date;

import com.mikealbert.data.vo.ArCreationVO;
import com.mikealbert.exception.MalBusinessException;



public interface ArService {

	public static final String MSS_REPRINT = "MSS_REPRINT";
	public static final String MSS_REPRINT_PARAMETER_KEY ="MAINT_SCHED_REPL_FEE";
	public static final String OE_REVISION_INT_ADJ_PARAMETER_KEY ="OE_REV_INT_ADJ";
	public static final String OE_REVISION_ASSESSMENT_PARAMETER_KEY ="OE_REV_ASSMNT";
	
	public Long createInvoiceAR(ArCreationVO aRVO) throws MalBusinessException, Exception; 
	
	public Long createInvoiceARForType(String type, ArCreationVO aRVO) throws MalBusinessException, Exception;
	
	public void rechargeCapitalContribution(long cId, long contractLineId, long quotationModelId, Date docDate, String userName) throws MalBusinessException, Exception;


}
