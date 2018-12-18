package com.mikealbert.service;

import java.util.List;

import com.mikealbert.data.entity.FleetMaster;
import com.mikealbert.data.entity.QuotationModel;
import com.mikealbert.data.enumeration.ReportNameEnum;
import com.mikealbert.data.vo.ReportContactVO;

public interface ReportContactService {	
	public List<ReportContactVO> getReportContacts(ReportNameEnum reportName, FleetMaster fms, QuotationModel qmd);	
	public String getReportContactsEmailSubject(ReportNameEnum reportName);
	public String getReportContactsEmailBody(ReportNameEnum reportName);	
}
