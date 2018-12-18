package com.mikealbert.vision.service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mikealbert.common.MalLogger;
import com.mikealbert.common.MalLoggerFactory;
import com.mikealbert.data.dao.ClientFacingQueueDAO;
import com.mikealbert.data.dao.ExternalAccountDAO;
import com.mikealbert.data.dao.ProcessStageObjectDAO;
import com.mikealbert.data.dao.UnitProgressDAO;
import com.mikealbert.data.entity.ProcessStageObject;
import com.mikealbert.data.entity.queue.ClientFacingQueueV;
import com.mikealbert.exception.MalException;
import com.mikealbert.service.FleetMasterService;
import com.mikealbert.service.ProcessStageService;
import com.mikealbert.util.MALUtilities;
import com.mikealbert.vision.vo.UnitInfo;

@Service("clientFacingQueueService")
@Transactional
public class ClientFacingQueueServiceImpl implements ClientFacingQueueService {
	public MalLogger logger = MalLoggerFactory.getLogger(this.getClass());

	@Resource ExternalAccountDAO externalAccountDAO;
	@Resource UnitProgressDAO unitProgressDAO;
	@Resource ClientFacingQueueDAO clientFacingQueueDAO;
	@Resource FleetMasterService fleetMasterService;
	@Resource ProcessStageObjectDAO processStageObjectDAO;
	@Resource ProcessStageService processStageService;

	@Override
	@Transactional(readOnly = true)
	public List<ClientFacingQueueV> findClientFacingQueueList(Sort sort) {
		return clientFacingQueueDAO.findClientFacingQueueList(sort);
	}
	
	@Override
	@Transactional(readOnly = true)
	public UnitInfo getSelectedUnitDetails(Long fmsId) {
		UnitInfo unitInfo = null; 
		
		List<Object[]> detailsList;
		try {
			detailsList = unitProgressDAO.getSelectedUnitDetails(fmsId);
			if(detailsList != null && detailsList.size() > 0) {
				Object[] unitDetails = detailsList.get(0);
				
				unitInfo = new UnitInfo();
				unitInfo.setTrim((String) unitDetails[0]);  
				unitInfo.setColor((String) unitDetails[1]);
				unitInfo.setVin(unitDetails[2] != null ? (String) unitDetails[2] : "");
			}
		} catch (Exception e) {
			throw new MalException("Error while getting unit details for fleet master "+fmsId , e);
		}
		
		return unitInfo;
	}
	
	public	void updateProcessStageObjectsIncludeFlag(List<Long> processStageObjectIds, String includeFlagYN) {
		List<ProcessStageObject> processStageObjects = new ArrayList<ProcessStageObject>();
		ProcessStageObject processStageObject;
		
		for(Long psoId : processStageObjectIds){
			processStageObject = processStageObjectDAO.findById(psoId).orElse(null);
			processStageObjects.add(processStageObject);
		}
		
		if(MALUtilities.convertYNToBoolean(includeFlagYN)){
			processStageService.includeStagedObjects(processStageObjects);			
		} else {
			processStageService.excludeStagedObjects(processStageObjects);			
		}
			
	}

}
