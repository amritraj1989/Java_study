package com.mikealbert.data.dao;

import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.mikealbert.data.vo.VehicleSearchCriteriaVO;
import com.mikealbert.data.vo.VehicleSearchResultVO;


public interface VehicleSearchDAOCustom  {	
	List<VehicleSearchResultVO> searchVehicles(VehicleSearchCriteriaVO vehicleSearchCriteriaVO, Pageable pageable, Sort sort);
	int searchVehiclesCount(VehicleSearchCriteriaVO vehicleSearchCriteriaVO);
}
