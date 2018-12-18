package com.mikealbert.vision.service;

import java.util.List;

import com.mikealbert.data.vo.ManufacturerProgressQueueVO;
import com.mikealbert.data.vo.ManufacturerProgressSearchCriteriaVO;
import com.mikealbert.exception.MalException;
import com.mikealbert.vision.vo.UnitInfo;

public interface ManufacturerProgressQueueService {
	
	public List<ManufacturerProgressQueueVO> getManufacturerQueueSearchResults(ManufacturerProgressSearchCriteriaVO searchCriteria)throws MalException;
	public UnitInfo getSelectedUnitDetails(Long fmsId)throws MalException;
	public ManufacturerProgressQueueVO getUpdatedManufactureQueueSearch(ManufacturerProgressQueueVO manufacturerProgressQueueVo);
	public int getMaxToleranceElapsedDaysForManufacturer(String psgName, String propertyName);
	public String getPropertyValueByName(String psgName, String propertyName);
	public boolean isItemWithinTolerance(Long psoId);

}
	