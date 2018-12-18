package com.mikealbert.vision.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mikealbert.common.MalLogger;
import com.mikealbert.common.MalLoggerFactory;
import com.mikealbert.data.dao.PurchaseOrderReleaseQueueDAO;
import com.mikealbert.data.dao.UnitProgressDAO;
import com.mikealbert.data.entity.PurchaseOrderReleaseQueueV;
import com.mikealbert.exception.MalException;
import com.mikealbert.vision.vo.UnitInfo;

@Service("poReleaseQueueService")
@Transactional
public class PurchaseOrderReleaseQueueServiceImpl implements  PurchaseOrderReleaseQueueService {
	public MalLogger logger = MalLoggerFactory.getLogger(this.getClass());

	@Resource PurchaseOrderReleaseQueueDAO purchaseOrderReleaseQueueDAO;
	@Resource UnitProgressDAO unitProgressDAO;

	@Override
	@Transactional(readOnly = true)
	public List<PurchaseOrderReleaseQueueV> findPurchaseOrderQueueList() { 
		return purchaseOrderReleaseQueueDAO.findAllPurchaseOrderReleaseQueueList();
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
	
}
