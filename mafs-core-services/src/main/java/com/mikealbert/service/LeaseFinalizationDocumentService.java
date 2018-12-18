package com.mikealbert.service;

import com.mikealbert.data.enumeration.DocumentNameEnum;
import com.mikealbert.data.enumeration.ReportNameEnum;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.exception.MalException;
import com.mikealbert.service.task.LeaseFinalizationDocumentArchiveTask;

public interface LeaseFinalizationDocumentService {
	
	public byte[] getArchivedDocument(String objectId, String objectType, DocumentNameEnum documentName) throws MalBusinessException, MalException;	
	public void process(LeaseFinalizationDocumentArchiveTask task) throws MalException, MalBusinessException;	
	
	public void archiveOERevisionDocument(Long activeContractQmdId, Long revisionQmdId, DocumentNameEnum reportNameEnum, String deliveryMethod, String user) throws MalBusinessException, MalException;
	public boolean isReportIsSetupForEmail(Long qmdId , ReportNameEnum reportNameEnum);
	
	public static final String OPEN_END_REVISION = "OPEN_END_REVISION";
	
}
