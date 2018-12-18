package com.mikealbert.vision.service;

import java.util.List;
import org.springframework.data.domain.Sort;
import com.mikealbert.data.entity.queue.ClientFacingQueueV;
import com.mikealbert.exception.MalException;
import com.mikealbert.vision.vo.UnitInfo;

public interface ClientFacingQueueService {
	
	public List<ClientFacingQueueV> findClientFacingQueueList(Sort sort);
	public UnitInfo getSelectedUnitDetails(Long fmsId)throws MalException;
	public void updateProcessStageObjectsIncludeFlag(List<Long> processStageObjectIds, String includeFlagYN) throws MalException;

}
