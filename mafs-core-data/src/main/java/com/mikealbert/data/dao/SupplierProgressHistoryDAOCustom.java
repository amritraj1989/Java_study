package com.mikealbert.data.dao;

import java.util.Date;

import com.mikealbert.exception.MalException;

public interface SupplierProgressHistoryDAOCustom {
	
	public void performPostSupplierUpdateJob(String checkWillowConfig, String checkRemainder, Long cId,
											String progressType, Date enteredDate, Date actionDate, Long fmsId,
											Long makeId, Long qmdId, Long docId, String enteredBy,String supplier) throws MalException;
	
	
}
