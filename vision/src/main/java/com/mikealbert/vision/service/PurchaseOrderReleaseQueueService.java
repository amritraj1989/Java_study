package com.mikealbert.vision.service;

import java.util.List;

import com.mikealbert.data.entity.PurchaseOrderReleaseQueueV;
import com.mikealbert.exception.MalException;
import com.mikealbert.vision.vo.UnitInfo;

public interface PurchaseOrderReleaseQueueService {
	
	public List<PurchaseOrderReleaseQueueV> findPurchaseOrderQueueList();
	public UnitInfo getSelectedUnitDetails(Long fmsId)throws MalException;

}
