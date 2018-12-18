package com.mikealbert.data.dao;

import com.mikealbert.exception.MalException;

/**
* Custom DAO for Quotation 
* @author sibley
*/

public interface QuotationDAOCustom {
	public Long generateId();	
	public Long createQuotation(Long cId, String accountCode, Long qprId, String driverGradeGroupCode, Long term, Long distance, Long cfgId, 
			String orderTypeCode, String unitNo,  Long odoReading, String employeeNo, Long desiredQuoId) throws MalException;
}
