package com.mikealbert.data.dao;

import java.util.List;

import com.mikealbert.data.vo.ManufacturerProgressQueueVO;
import com.mikealbert.data.vo.ManufacturerProgressSearchCriteriaVO;
import com.mikealbert.exception.MalException;


public interface ManufacturerProgressQueueDAOCustom {
	public int findLimitCount(String psgName, String propertyName);	
	public boolean isWithinTolerance(Long psoId);
	public String getPropertyValueByName(String psgName, String propertyName);
	
	public List<ManufacturerProgressQueueVO> getManufacturerQueueSearchResults(ManufacturerProgressSearchCriteriaVO searchCriteria) throws MalException;
}
