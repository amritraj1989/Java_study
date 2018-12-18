package com.mikealbert.service;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mikealbert.data.dao.ProcessStageDAO;
import com.mikealbert.data.dao.ProcessStageObjectDAO;
import com.mikealbert.data.entity.Doc;
import com.mikealbert.data.entity.FleetMaster;
import com.mikealbert.data.entity.ObjectLogBook;
import com.mikealbert.data.entity.ProcessStage;
import com.mikealbert.data.entity.ProcessStageObject;
import com.mikealbert.data.enumeration.LogBookTypeEnum;
import com.mikealbert.data.enumeration.OrderToDeliveryProcessStageEnum;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.exception.MalException;
import com.mikealbert.util.MALUtilities;

/** 
* 
*  @see com.mikealbert.data.entity.ProcessStageObject
* */
@Service("processStageService")
public class ProcessStageServiceImpl implements ProcessStageService{
	@Resource ProcessStageDAO processStageDAO;
	@Resource ProcessStageObjectDAO processStageObjectDAO;
	@Resource QuotationService quotationService;
	@Resource DocumentService documentService;
	@Resource LogBookService logBookService;
	@Resource FleetMasterService fleetMasterService;
	
	static final String REASON_SEPARATOR = "||";
	
	public ProcessStageObject getStagedObject(OrderToDeliveryProcessStageEnum processStageEnum, Long processStageObjectObjectId){
		return processStageObjectDAO.getProcessStageObjectByProcessStageObjectNameAndId(
				processStageEnum.getCode(), processStageEnum.getObjectName(), processStageObjectObjectId);
	}
	
	public ProcessStageObject getStagedObject(Long processStageObjectId){
		return processStageObjectDAO.findById(processStageObjectId).orElse(null);		
	}
	
	/**
	 * Copies an item (ProcessStageObject) from one queue (ProcessStage) to another queue. 
	 * Also, logs the client's input reason for the move into the Manufacturer Log Book.
	 * @param ProcessStageObject The staged item to move
	 * @param String A descriptive reason for the move
	 * @return ProcessStageObject The item in the Client Facing queue
	 */
	@Transactional(rollbackFor = Exception.class)
	public ProcessStageObject copyToClientFacing(ProcessStageObject processStageObject, String reason, String username) throws MalBusinessException, MalException{
		ProcessStageObject updatedProcessStageObject;
		Doc doc;
		ObjectLogBook logBook;
		FleetMaster fleetMaster = null;
		Long qmdId;
		LogBookTypeEnum logBookTypeEnum;
		String reasonSummary;
	
		try {
			
			//Resolve source staged item object type to target stage item object type
			if(processStageObject.getObjectName().equals("DOCS")){
				//TODO Use native queries as this simple read is a hog. It is eagerly fetching the associations
				doc = documentService.getDocumentByDocId(processStageObject.getObjectId());
				fleetMaster = fleetMasterService.getFleetMasterByDocId(doc.getDocId());
				qmdId = doc.getGenericExtId();				
			} else if(processStageObject.getObjectName().equals("QUOTATION_MODELS")){ 
				qmdId = processStageObject.getObjectId();
				fleetMaster = fleetMasterService.findByUnitNo(quotationService.getQuotationModel(qmdId).getUnitNo());
			}else {
				throw new MalException("Cannot move " + processStageObject.getProcessStage().getProcessStageName() + " item to " + OrderToDeliveryProcessStageEnum.CLIENT_FACING.getCode() + " queue");				
			}
			
			//Determine the target reason based on the source process stage
			if(processStageObject.getProcessStage().getProcessStageName().equals(OrderToDeliveryProcessStageEnum.UPFIT.getCode())) {
				reasonSummary = "Upfit Updates";
			} else if(processStageObject.getProcessStage().getProcessStageName().equals(OrderToDeliveryProcessStageEnum.MANUFACTURER.getCode())){
				reasonSummary = "Manufacturer Updates";
			} else if(processStageObject.getProcessStage().getProcessStageName().equals(OrderToDeliveryProcessStageEnum.IN_SERVICE.getCode())){
				reasonSummary = "In Service Updates";				
			} else if(processStageObject.getProcessStage().getProcessStageName().equals(OrderToDeliveryProcessStageEnum.PO_RELEASE.getCode())){
				reasonSummary = "Purchase Order Updates";	
				if(MALUtilities.isEmpty(qmdId)){
					throw new MalBusinessException("Stock orders cannot be sent to the Client Facing Queue.");
				}
			}else {
				throw new MalException("Cannot move " + processStageObject.getProcessStage().getProcessStageName() + " item to " + OrderToDeliveryProcessStageEnum.CLIENT_FACING.getCode() + " queue");				
			}
			
			updatedProcessStageObject = addToClientFacing(qmdId, reasonSummary, username);
									
			//Inserts reason as a Log Book Entry
			logBookTypeEnum = processStageLogBookType(processStageObject);
			logBook = logBookService.createObjectLogBook(fleetMaster, logBookTypeEnum);
			logBookService.addEntry(logBook, username, reason, null, false);
			
		} catch (Exception e) {
			if(e instanceof MalBusinessException){
				throw new MalBusinessException(e.getMessage());
			}else{
				throw new MalException(e.getMessage());
			}
		}
		
		return updatedProcessStageObject;		
	}
	
    /**Use this method to manually add items to client facing queue
     * 	
     */
	@Transactional(rollbackFor = Exception.class)	
	public ProcessStageObject addToClientFacing(Long qmdId, String reason, String username){
		ProcessStageObject processStageObject;
		ProcessStage processStage;
		
		//Based on whether the item exists in the client facing queue....
		processStageObject = this.getStagedObject(OrderToDeliveryProcessStageEnum.CLIENT_FACING, qmdId);			
		if(MALUtilities.isEmpty(processStageObject)){
			processStage = processStageDAO.getProcessStageByProcessNameAndStageName("OTD", OrderToDeliveryProcessStageEnum.CLIENT_FACING.getCode());
			
			processStageObject = new ProcessStageObject();
			processStageObject.setProcessStage(processStage);
			processStageObject.setObjectName(OrderToDeliveryProcessStageEnum.CLIENT_FACING.getObjectName());				
			processStageObject.setObjectId(qmdId);
			processStageObject.setOpUser(username);
			processStageObject.setEnteredDate(Calendar.getInstance().getTime());								
		} else {
			if(!MALUtilities.convertYNToBoolean(processStageObject.getIncludeYN())) {
				processStageObject.setEnteredDate(Calendar.getInstance().getTime());
				processStageObject.setOpUser(username);
				processStageObject.setReason(null);
			}
		}
		
		if(MALUtilities.isEmpty(processStageObject.getReason())) {
			processStageObject.setReason(reason);					
		} else {
			if(!processStageObject.getReason().contains(reason)) {
				processStageObject.setReason((processStageObject.getReason() + REASON_SEPARATOR + reason).trim());
			}
		}			
		
		processStageObject.setIncludeYN("Y");
		processStageObject.setRefreshYN("N");			
					
		saveOrUpdateProcessStageObject(processStageObject);	
		
		return processStageObject;
	}
	
	private LogBookTypeEnum processStageLogBookType(ProcessStageObject processStageObject){
		LogBookTypeEnum logBookTypeEnum = null;
		if(processStageObject.getProcessStage().getProcessStageName().equals(OrderToDeliveryProcessStageEnum.UPFIT.getCode())) {
			logBookTypeEnum = LogBookTypeEnum.TYPE_UPFIT_PRG_NOTES;
		} else if(processStageObject.getProcessStage().getProcessStageName().equals(OrderToDeliveryProcessStageEnum.MANUFACTURER.getCode())) {
			logBookTypeEnum = LogBookTypeEnum.TYPE_BASE_VEH_ORDER_NOTES;
		} else if(processStageObject.getProcessStage().getProcessStageName().equals(OrderToDeliveryProcessStageEnum.IN_SERVICE.getCode())) {
			logBookTypeEnum = LogBookTypeEnum.TYPE_IN_SERV_PRG_NOTES;	
		} else if(processStageObject.getProcessStage().getProcessStageName().equals(OrderToDeliveryProcessStageEnum.PO_RELEASE.getCode())) {
			logBookTypeEnum = LogBookTypeEnum.TYPE_PURCHASE_ORDER_NOTES;			
		} else {
			throw new MalException("Log Book Type mapping for " + processStageObject.getProcessStage().getProcessStageName() + " has not been mapped");				
		}		
		return logBookTypeEnum;
	}
	
	@Transactional(rollbackFor = Exception.class)
	public void excludeStagedObject(ProcessStageObject processStageObject){
		processStageObject = processStageObjectDAO.findById(processStageObject.getPsoId()).orElse(null);
		if(processStageObject != null){
			processStageObject.setIncludeYN("N");
			processStageObject.setRefreshYN("Y");			
			
			saveOrUpdateProcessStageObject(processStageObject);		
		}
	}
	
	@Transactional(rollbackFor = Exception.class)
	public void excludeStagedObject(Long psoId){
		ProcessStageObject processStageObject = processStageObjectDAO.findById(psoId).orElse(null);
		if(processStageObject != null){
			processStageObject.setIncludeYN("N");
			processStageObject.setRefreshYN("Y");			
			
			saveOrUpdateProcessStageObject(processStageObject);		
		}
	}
	
	@Transactional(rollbackFor = Exception.class)	
	public void excludeStagedObjects(List<ProcessStageObject> processStageObjects){
		List<ProcessStageObject> updatedProcessStageObjects = new ArrayList<ProcessStageObject>();	
		ProcessStageObject updatedProcessStageObject;
		
		for(ProcessStageObject processStageObject : processStageObjects){
			updatedProcessStageObject = processStageObjectDAO.findById(processStageObject.getPsoId()).orElse(null);
			updatedProcessStageObject.setIncludeYN("N");
			updatedProcessStageObject.setRefreshYN("Y");
			updatedProcessStageObjects.add(updatedProcessStageObject);
		}
		
		saveOrUpdateProcessStageObjects(updatedProcessStageObjects);		
	}
	
	@Transactional(rollbackFor = Exception.class)
	public void includeStagedObject(ProcessStageObject processStageObject){
		processStageObject = processStageObjectDAO.findById(processStageObject.getPsoId()).orElse(null);
		processStageObject.setIncludeYN("Y");

		saveOrUpdateProcessStageObject(processStageObject);		
	}

	@Transactional(rollbackFor = Exception.class)
	public void includeStagedObjects(List<ProcessStageObject> processStageObjects){
		List<ProcessStageObject> updatedProcessStageObjects = new ArrayList<ProcessStageObject>();	
		ProcessStageObject updatedProcessStageObject;

		for(ProcessStageObject processStageObject : processStageObjects){
			updatedProcessStageObject = processStageObjectDAO.findById(processStageObject.getPsoId()).orElse(null);
			updatedProcessStageObject.setIncludeYN("Y");
			updatedProcessStageObjects.add(updatedProcessStageObject);
		}

		saveOrUpdateProcessStageObjects(updatedProcessStageObjects);		
	}
		
	public ProcessStageObject saveOrUpdateProcessStageObject(ProcessStageObject processStageObject){		
		return processStageObjectDAO.saveAndFlush(processStageObject);
	}
		
	private List<ProcessStageObject> saveOrUpdateProcessStageObjects(List<ProcessStageObject> processStageObjects){
		return processStageObjectDAO.saveAll(processStageObjects);
	}
	
	@Transactional(rollbackFor = Exception.class)
	public void updateStagedObjectReason(Long psoId, String reason){
		try {
			ProcessStageObject processStageObject = processStageObjectDAO.findById(psoId).orElse(null);
			if(processStageObject != null){				
				processStageObject.setReason(reason);
				saveOrUpdateProcessStageObject(processStageObject);		
			}
		} catch (Exception e) {
			// do nothing if it fails.
		}
		
	}

	@Transactional(rollbackFor = Exception.class)
	public void updateStagedObjectReasonAndProperties(Long psoId,
			String reason, String property1, String property2,
			String property3, String property4, String property5) {
		try {
			ProcessStageObject processStageObject = processStageObjectDAO.findById(psoId).orElse(null);
			if(processStageObject != null){				
				processStageObject.setReason(reason);
				processStageObject.setProperty1(property1);
				processStageObject.setProperty2(property2);
				processStageObject.setProperty3(property3);
				processStageObject.setProperty4(property4);
				processStageObject.setProperty5(property5);
				
				saveOrUpdateProcessStageObject(processStageObject);		
			}
		} catch (Exception e) {
			// do nothing if it fails.
		}
		
	}
}
