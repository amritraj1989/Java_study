package com.mikealbert.data.dao;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import com.mikealbert.data.vo.VehicleConfigurationSearchCriteriaVO;
import com.mikealbert.data.vo.VehicleConfigurationSearchResultVO;

public interface VehicleConfigurationDAOCustom  {
	public List<VehicleConfigurationSearchResultVO> searchVehicleConfiguration(VehicleConfigurationSearchCriteriaVO vehicleConfigurationSearchCriteriaVO, Pageable pageable, Sort sort);
	public int searchVehicleConfigurationCount(VehicleConfigurationSearchCriteriaVO vehicleConfigurationSearchCriteriaVO);	
}
