package com.mikealbert.vision.service;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mikealbert.common.MalLogger;
import com.mikealbert.common.MalLoggerFactory;
import com.mikealbert.data.dao.ClientFacingQueueDAO;
import com.mikealbert.data.dao.ExternalAccountDAO;
import com.mikealbert.data.dao.ManufacturerProgressQueueDAO;
import com.mikealbert.data.dao.ProcessStageObjectDAO;
import com.mikealbert.data.dao.UnitProgressDAO;
import com.mikealbert.data.vo.ManufacturerProgressQueueVO;
import com.mikealbert.data.vo.ManufacturerProgressSearchCriteriaVO;
import com.mikealbert.exception.MalException;
import com.mikealbert.service.FleetMasterService;
import com.mikealbert.service.ProcessStageService;
import com.mikealbert.vision.vo.UnitInfo;

@Service("manufactureQueueService")
@Transactional
public class ManufacturerProgressQueueServiceImpl implements  ManufacturerProgressQueueService {
	public MalLogger logger = MalLoggerFactory.getLogger(this.getClass());

	@Resource ExternalAccountDAO externalAccountDAO;
	@Resource UnitProgressDAO unitProgressDAO;
	@Resource ClientFacingQueueDAO clientFacingQueueDAO;
	@Resource FleetMasterService fleetMasterService;
	@Resource ProcessStageObjectDAO processStageObjectDAO;
	@Resource ProcessStageService processStageService;
	@Resource ManufacturerProgressQueueDAO manufacturerProgressQueueDAO;

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
	
	@Override
	public ManufacturerProgressQueueVO getUpdatedManufactureQueueSearch(ManufacturerProgressQueueVO manufacturerProgressQueueVo) {
	
		Object[] record = unitProgressDAO.getUpdatedSupllierProgressData(manufacturerProgressQueueVo.getDocId());
		if(record != null) {
			int j = 0;
			
			manufacturerProgressQueueVo.setExpectedDate(record[j] != null ? (Date) (record[j]) : null);
			manufacturerProgressQueueVo.setVehicleReadyDate(record[j += 1] != null ? (Date) record[j] : null);
			manufacturerProgressQueueVo.setInvProcDate(record[j += 1] != null ? (Date) (record[j]) : null);
			manufacturerProgressQueueVo.setFactoryShipDate(record[j += 1] != null ? (Date) (record[j]) : null);
			manufacturerProgressQueueVo.setDealerReceivedDate(record[j += 1] != null ? (Date) (record[j]) : null);
			manufacturerProgressQueueVo.setDlrRecdDate(record[j += 1] != null ? (Date) (record[j]) : null);
			manufacturerProgressQueueVo.setInterimDealerDate(record[j += 1] != null ? (Date) record[j] : null);
			manufacturerProgressQueueVo.setProdDate(record[j += 1] != null ? (Date) record[j] : null);
//			manufacturerProgressQueueVo.setExpectedDate(record[j += 1] != null ? (Date) record[j] : null);
			manufacturerProgressQueueVo.setDesiredToDealer(record[j += 2] != null ? (Date) record[j] : null);
			
		}

		return manufacturerProgressQueueVo;
	}

	@Override
	public int getMaxToleranceElapsedDaysForManufacturer(String psgName, String propertyName) {

		return manufacturerProgressQueueDAO.findLimitCount(psgName, propertyName);
	}
	
	@Override
	public boolean isItemWithinTolerance(Long psoId) {
		boolean isWithinTolerance = false;
		isWithinTolerance = manufacturerProgressQueueDAO.isWithinTolerance(psoId);
		return isWithinTolerance;
	}

	@Override
	public String getPropertyValueByName(String psgName, String propertyName) {
		return manufacturerProgressQueueDAO.getPropertyValueByName(psgName, propertyName);
	}

	@Override
	public List<ManufacturerProgressQueueVO> getManufacturerQueueSearchResults(ManufacturerProgressSearchCriteriaVO searchCriteria) throws MalException {
		
		return manufacturerProgressQueueDAO.getManufacturerQueueSearchResults(searchCriteria);
	}

}
