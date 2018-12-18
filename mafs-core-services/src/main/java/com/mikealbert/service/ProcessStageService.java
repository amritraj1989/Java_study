package com.mikealbert.service;

import java.util.List;

import com.mikealbert.data.entity.ProcessStageObject;
import com.mikealbert.data.enumeration.OrderToDeliveryProcessStageEnum;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.exception.MalException;
/**
* Public Interface implemented by {@link com.mikealbert.vision.service.ProcessStageServiceImpl} 
* for interacting with business service methods concerning: 
* 
*  @see com.mikealbert.vision.service.ProcessStageServiceImpl
*  @see com.mikealbert.data.entity.ProcessStage
*  @see com.mikealbert.data.entity.ProcessStageObject
* */
public interface ProcessStageService {
	public void excludeStagedObject(ProcessStageObject processStageObject);
	public void excludeStagedObjects(List<ProcessStageObject> processStageObjects);

	public void includeStagedObject(ProcessStageObject processStageObject);
	public void includeStagedObjects(List<ProcessStageObject> processStageObjects);
	
	public ProcessStageObject getStagedObject(OrderToDeliveryProcessStageEnum processStageEnum, Long processStageObjectObjectId);
	public ProcessStageObject getStagedObject(Long processStageObjectId);	
	
	public ProcessStageObject copyToClientFacing(ProcessStageObject processStageObject, String reason, String username) throws MalBusinessException, MalException;
	
	public ProcessStageObject addToClientFacing(Long qmdId, String reason, String username);
	
	public void excludeStagedObject(Long psoId);
	
	public void updateStagedObjectReason(Long psoId, String reason);
	public ProcessStageObject saveOrUpdateProcessStageObject(ProcessStageObject processStageObject);
	
	public void updateStagedObjectReasonAndProperties(Long psoId, String reason, String property1, String property2, String property3, String property4, String property5);
	
}
