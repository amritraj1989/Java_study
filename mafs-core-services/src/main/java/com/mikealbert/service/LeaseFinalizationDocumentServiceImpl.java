package com.mikealbert.service; 

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import net.sf.jasperreports.engine.JasperExportManager;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.mikealbert.common.MalLogger;
import com.mikealbert.common.MalLoggerFactory;
import com.mikealbert.data.dao.OnbaseUploadedDocsDAO;
import com.mikealbert.data.dao.PurchaseOrderReportDAO;
import com.mikealbert.data.entity.EmailRequestLog;
import com.mikealbert.data.entity.ExternalAccountPK;
import com.mikealbert.data.entity.FleetMaster;
import com.mikealbert.data.entity.OnbaseUploadedDocs;
import com.mikealbert.data.entity.QuotationModel;
import com.mikealbert.data.enumeration.DocumentNameEnum;
import com.mikealbert.data.enumeration.EmailRequestEventEnum;
import com.mikealbert.data.enumeration.ReportNameEnum;
import com.mikealbert.data.vo.ReportContactVO;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.exception.MalException;
import com.mikealbert.service.enumeration.OnbaseDocTypeEnum;
import com.mikealbert.service.enumeration.OnbaseIndexEnum;
import com.mikealbert.service.reporting.JasperReportService;
import com.mikealbert.service.task.LeaseFinalizationDocumentArchiveTask;
import com.mikealbert.service.task.TaskResponse;
import com.mikealbert.service.vo.OnbaseKeywordVO;
import com.mikealbert.util.MALUtilities;


@Service("leaseFinalizationDocumentService")
public class LeaseFinalizationDocumentServiceImpl implements LeaseFinalizationDocumentService {
	
	MalLogger logger = MalLoggerFactory.getLogger(this.getClass());	
	
	@Resource WillowConfigService willowConfigService;
	@Resource OnbaseArchivalService onbaseArchivalService; 
	@Resource OnbaseUploadedDocsDAO onbaseUploadedDocsDAO;
	@Resource FleetMasterService fleetMasterService;
	@Resource QuotationService quotationService;
	@Resource OnbaseRetrievalService onbaseRetrievalService;
	@Resource EmailRequestService emailRequestService; 
	@Resource JasperReportService jasperReportService;	
	@Resource PurchaseOrderReportDAO purchaseOrderReportDAO;
	
	
	public void archiveOERevisionDocument(Long activeContractQmdId, Long revisionQmdId, DocumentNameEnum documentNameEnum, String deliveryMethod, String user) throws MalBusinessException, MalException{				

		LeaseFinalizationDocumentArchiveTask task = new LeaseFinalizationDocumentArchiveTask();
		task.setTaskId(revisionQmdId);
		task.setQmdId(revisionQmdId);
		task.setQuoteType(OPEN_END_REVISION);
		task.setActiveContractQmdId(activeContractQmdId);
		task.setFileName(documentNameEnum.getName());
		task.setDocumentName(String.valueOf(documentNameEnum));
		task.setDeliveryMethod(deliveryMethod);
		task.setUser(user);
		
		requestArchiveOERevisionDocument(task);
		
	}
	
	//need to move central place.
	public void	requestArchiveOERevisionDocument(LeaseFinalizationDocumentArchiveTask archieveTask) throws MalBusinessException, MalException{
		
		try {
			
			logger.info("Received requested to lease finalization documents"+archieveTask.getTaskId() +" : doc Type:"+ archieveTask.getDocumentName() +" : quote Type:"+ archieveTask.getQuoteType());	
			
			String taskProcessorWebSvcURL = willowConfigService.getConfigValue("TASK_PROCSR_WEBSVC_URL");
			
			if(taskProcessorWebSvcURL == null ){				
					throw new MalBusinessException("Task Processor WebSvc URL Willow Config is not configured" );
			}
			TaskResponse result = new RestTemplate().postForObject( taskProcessorWebSvcURL+ "/tasks/create/leaseFinalizationDocumentArchiveTask", archieveTask, TaskResponse.class);
		
			logger.info("Successfully requested to lease finalization documents"+archieveTask.getTaskId() +" : doc Type:"+ archieveTask.getDocumentName() +" : quote Type:"+ archieveTask.getQuoteType()+" : requestId :"+ result.getRequestId());
		
		} catch (Exception e) {
			logger.error(e ,"Unable to submit Onbase archieve request : Error :"+e.getMessage());
			throw new MalException("Unable to submit Onbase archieve request: Error :"+e.getMessage());
		}
	}


	@Override
	public void process(LeaseFinalizationDocumentArchiveTask finalizationDocTask) throws MalException, MalBusinessException {
		List<OnbaseUploadedDocs> docs = new ArrayList<OnbaseUploadedDocs>();
		OnbaseUploadedDocs obd = new OnbaseUploadedDocs();		
		
		OnbaseDocTypeEnum onbaseDocTypeEnum;		
		EmailRequestLog emailRequestLog;		
		String fileName;
		String fileExtension = "PDF";
		
		EmailRequestEventEnum emailRequestEvent;
		
		try {
			logger.info("Starting Lease Finalization Document Upload to Onbase...:task:"+finalizationDocTask.getTaskId());
			
			byte[] fileData = null;
			if(! MALUtilities.isEmpty(finalizationDocTask.getQuoteType())  &&  finalizationDocTask.getQuoteType().equals(OPEN_END_REVISION)){
				fileName = finalizationDocTask.getFileName();
				fileData = generateDocuments(finalizationDocTask);
				
			}else{
				
				StringBuilder reportFullPath = new StringBuilder();
				
				
				fileName = finalizationDocTask.getFileName();			
				if(fileName.contains(".")) {
					fileExtension = fileName.substring(fileName.indexOf(".") + 1);
					fileName = fileName.substring(0, fileName.indexOf(".") );
				}			
							
				//Retrieve configured path to directory in which the report is stored
				//TODO We should interface with the report server here.
				
				
				reportFullPath.append(willowConfigService.getConfigValue(WillowConfigService.REPORTS_DIRECTORY));
				reportFullPath.append(reportFullPath.toString().endsWith("\\") ? "" : "\\"); 
				reportFullPath.append(fileName);
				reportFullPath.append("." + fileExtension);		
				fileData = Files.readAllBytes(Paths.get(reportFullPath.toString()));
				
			}
			
			//Resolve Onbase document type from document name
			switch(DocumentNameEnum.valueOf(finalizationDocTask.getDocumentName())) {
			    case SCHEDULE_A:
				    onbaseDocTypeEnum = OnbaseDocTypeEnum.TYPE_SCHEDULE_A;
				    emailRequestEvent = EmailRequestEventEnum.LEASE_FINALIZATION_SCHEDULE_A;
				    break;
			    case AMORTIZATION_SCHEDULE:
			    	onbaseDocTypeEnum =  OnbaseDocTypeEnum.TYPE_AMORTIZATION_SCHEDULE;
				    emailRequestEvent = EmailRequestEventEnum.LEASE_FINALIZATION_AMORTIZATION;			    	
			    	break;
			    default:
			    	throw new Exception("Unsupported Onbase Document Type for Lease Finalization");
			}
						
			obd.setObdId(onbaseUploadedDocsDAO.getNextPK());		
			obd.setObjectId(String.valueOf(finalizationDocTask.getQmdId()));
			obd.setObjectType("QUOTATION_MODELS");
			obd.setDocType(onbaseDocTypeEnum.getValue());
			obd.setIndexKey(onbaseArchivalService.generateOnbaseKeywordsIndex(getOnBaseKeyWords(obd, onbaseDocTypeEnum)));
			obd.setObsoleteYn("N");
			obd.setFileType(fileExtension.toUpperCase());	
			obd.setFileData(fileData);
			obd.setFileName(fileName);
			obd.setDocSubType(finalizationDocTask.getDocumentName());
			docs.add(obd);

			logger.info("Index " + docs.get(0).getIndexKey());

			onbaseUploadedDocsDAO.saveAll(docs);		
			onbaseArchivalService.archiveDocumentInOnBase(docs);
			
			
			//TODO Extract this to a separate generic task service
			if(!MALUtilities.isEmptyString(finalizationDocTask.getDeliveryMethod()) && finalizationDocTask.getDeliveryMethod().equals("E")) {
				logger.info("Staging Email request for Lease Finalization Document :task:"+finalizationDocTask.getTaskId());
				emailRequestLog = new EmailRequestLog(String.valueOf(finalizationDocTask.getQmdId()), "QUOTATION_MODELS", 
						emailRequestEvent.getCode(), "Y", finalizationDocTask.getUser());
				emailRequestLog.setProperty1(finalizationDocTask.getDocumentName());
				emailRequestService.stageRequest(emailRequestLog);
			}
			
			logger.info("Completed Lease Finalization Document Upload to Onbase...:task:"+finalizationDocTask.getTaskId());		
		} catch (Exception e) {
			logger.error(e);
			throw new MalException("generic.error.occured.while", new String[] { "attempting to staging lease finalization doc upload to Onbase"}, e);
		}
	}
	
	
	public byte[] generateDocuments(LeaseFinalizationDocumentArchiveTask  archieveTask) throws MalException, MalBusinessException {		
	
			byte[] data = null;		
			
			try {

				if(archieveTask.getDocumentName().equals(String.valueOf(DocumentNameEnum.SCHEDULE_A))){					
					data = JasperExportManager.exportReportToPdf(jasperReportService.getOpenEndRevisionScheduleA(archieveTask.getActiveContractQmdId() , archieveTask.getQmdId()));
				
				} else if(archieveTask.getDocumentName().equals(String.valueOf(DocumentNameEnum.AMORTIZATION_SCHEDULE))){
					data = JasperExportManager.exportReportToPdf(jasperReportService.getOpenEndRevisionAmortizationSchedule(archieveTask.getActiveContractQmdId() , archieveTask.getQmdId()));					
				}
			
			} catch (Exception e) {				
				logger.error(e ,"Unable to generate document while archiving lease finalization document");
				throw new MalException("Unable to generate document while archiving lease finalization document :Error:"+e.getMessage());
			}
			
		return data;
	}
	
	/*
	 *  
	 */
	public byte[] getArchivedDocument(String objectId, String objectType, DocumentNameEnum documentName) throws MalBusinessException, MalException {		
		byte[] byteArray = null;
		OnbaseUploadedDocs onbaseUploadedDoc = null;
		OnbaseDocTypeEnum docType;
		List<OnbaseKeywordVO> onbaseKeywords = null;
		OnbaseKeywordVO onbaseKeywordVO;
		
		//TODO Doc Name to Doc Type could be a bit more symmetric, which eliminate the need for this switch statement 
		switch(documentName) {
		    case SCHEDULE_A:
		    	docType = OnbaseDocTypeEnum.TYPE_SCHEDULE_A;
		    	break;
		    case AMORTIZATION_SCHEDULE:
		    	docType = OnbaseDocTypeEnum.TYPE_AMORTIZATION_SCHEDULE;
		    	break;
		    default:
		    	throw new MalException("custom.message", new String[] {"Unsupported Onbase Document Type detected for getArchiveDocument"});
		}

		//TODO Just discovered that there can be multiple entities with the same objectId and objectType.
		//     Need to cater for more than one scenario by using the latest entry. 
		//    FYI - I don't believe there is an order by on the DAO method, i.e. may need to implement a comparator
		List<OnbaseUploadedDocs> onbaseDocList = onbaseUploadedDocsDAO.getOnBaseUploadedDocsByObjectIdAndType(objectId, objectType);
		for (OnbaseUploadedDocs uploadedDoc : onbaseDocList) {
			if(uploadedDoc.getDocSubType() != null && uploadedDoc.getDocSubType().equals(documentName.name())){
				onbaseUploadedDoc = uploadedDoc;
				break;
			}
		}
		
		//From what I understand, the upload Id is the only keyword needed to retrieve the document
		if(onbaseUploadedDoc != null){
			try {
				onbaseKeywordVO =  new OnbaseKeywordVO(OnbaseIndexEnum.UPLOAD_ID.getName() ,String.valueOf(onbaseUploadedDoc.getObdId()));
				onbaseKeywords = new ArrayList<OnbaseKeywordVO>();
				onbaseKeywords.add(onbaseKeywordVO);				
				
				byteArray = onbaseRetrievalService.getDoc(docType, onbaseKeywords); 
			} catch (Exception e) {
				logger.error(e, "Not able to get docuement from onbase for " + documentName + " doc type. keyword: "+ onbaseKeywords);
				throw e;
			}
			
		}
		
		return byteArray;	
	}	
	
	
	/**
	 * Assembles the keyword list values for the passed in doc type
	 * @param onbaseUploadedDoc
	 * @param OnbaseDocTypeEnum document type
	 * @return
	 */
	private List<OnbaseKeywordVO> getOnBaseKeyWords(OnbaseUploadedDocs onbaseUploadedDoc, OnbaseDocTypeEnum onbaseDocTypeEnum) throws Exception {
		List<OnbaseKeywordVO>  keywordVOList  =  null;
		String vin;
		QuotationModel qmd;
		
		qmd = quotationService.getQuotationModel(Long.parseLong(onbaseUploadedDoc.getObjectId()));
		vin = fleetMasterService.findByUnitNo(qmd.getUnitNo()).getVin();
		
		keywordVOList =  onbaseArchivalService.getOnbaseKeywords(onbaseDocTypeEnum);
		
		for (OnbaseKeywordVO onbaseKeywordVO :keywordVOList) {				
			if(onbaseKeywordVO.getKeywordName().equals(OnbaseIndexEnum.UPLOAD_ID.getName())){
				onbaseKeywordVO.setKeywordValue(String.valueOf(onbaseUploadedDoc.getObdId()));
			}else if(onbaseKeywordVO.getKeywordName().equals(OnbaseIndexEnum.UNIT_NO.getName())){
				onbaseKeywordVO.setKeywordValue(String.valueOf(qmd.getUnitNo())) ;
			}else if(onbaseKeywordVO.getKeywordName().equals(OnbaseIndexEnum.VIN_LAST_8.getName())){
				onbaseKeywordVO.setKeywordValue(vin.substring(vin.length() - 8));
			}else if(onbaseKeywordVO.getKeywordName().equals(OnbaseIndexEnum.CUSTOMER_NO.getName())){
				onbaseKeywordVO.setKeywordValue(qmd.getQuotation().getExternalAccount().getExternalAccountPK().getAccountCode());				
			}else if(onbaseKeywordVO.getKeywordName().equals(OnbaseIndexEnum.CUSTOMER_NAME.getName())){
				onbaseKeywordVO.setKeywordValue(qmd.getQuotation().getExternalAccount().getAccountName());
			}else if(onbaseKeywordVO.getKeywordName().equals(OnbaseIndexEnum.CORPORATE_NO.getName())){
				onbaseKeywordVO.setKeywordValue(qmd.getQuotation().getExternalAccount().getExternalAccountPK().getAccountCode());
			}else if(onbaseKeywordVO.getKeywordName().equals(OnbaseIndexEnum.CORPORATE_NAME.getName())){
				onbaseKeywordVO.setKeywordValue(qmd.getQuotation().getExternalAccount().getAccountName());
			}else if(onbaseKeywordVO.getKeywordName().equals(OnbaseIndexEnum.VIN.getName())){
				onbaseKeywordVO.setKeywordValue(vin);
			}		
		}	
		
		return keywordVOList;
	}	
	

	public boolean isReportIsSetupForEmail(Long qmdId , ReportNameEnum reportNameEnum) {
		
		boolean isEmailable = false;		
		try {
			
			QuotationModel quotationModel = quotationService.getQuotationModel(qmdId);
			FleetMaster fleetMaster = fleetMasterService.findByUnitNo(quotationModel.getUnitNo());
			ExternalAccountPK clientAccountPK = quotationModel.getQuotation().getExternalAccount().getExternalAccountPK();
				
			List<ReportContactVO> reportContacts = purchaseOrderReportDAO.getReportEmailContacts(
										reportNameEnum, clientAccountPK.getCId(), 
										clientAccountPK.getAccountType(), 
										clientAccountPK.getAccountCode(), 
										fleetMaster.getFmsId(), qmdId);
				
		
			
			if(!(MALUtilities.isEmpty(reportContacts) || reportContacts.size() == 0 )){				
				for(ReportContactVO reportContactVO : reportContacts) {
					if(reportContactVO.getDeliveryMethod().equals("EMAIL") && !MALUtilities.isEmpty(reportContactVO.getEmailAddres())) {
						isEmailable = true;						
						break;
					}
				}				
			}			
			
		} catch(Exception e) {
			logger.error(e, "Error in isReportIsSetupForEmail : " + qmdId);
		}
		
		return isEmailable;
	}	

}
