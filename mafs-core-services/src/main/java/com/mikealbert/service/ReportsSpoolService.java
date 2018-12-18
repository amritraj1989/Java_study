package com.mikealbert.service;

import java.util.List;

import com.mikealbert.data.entity.ReportsSpool;

/**
 * Public Interface implemented by {@link com.mikealbert.vision.service.ReportsSpoolServiceImpl} for interacting with business service methods.
 * 
 * @see com.mikealbert.data.entity.ReportsSpool
 * @see com.mikealbert.vision.service.ReportsSpoolServiceImpl
 * */
public interface  ReportsSpoolService {
	
	public void addReportsSpool(List<ReportsSpool> reportsSpoolList);
	
	public Long generateId();
	
}
