package com.mikealbert.service;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.mikealbert.data.vo.VehicleSearchCriteriaVO;
import com.mikealbert.data.vo.VehicleSearchResultVO;
import com.mikealbert.exception.MalBusinessException;

/**
 * Public Interface implemented by {@link com.mikealbert.vision.service.VehicleSearchServiceImpl} for interacting with business service methods concerning {@link com.mikealbert.data.entity.FleetMaster}(s).
 *
 * @see com.mikealbert.data.entity.FleetMaster
 * @see com.mikealbert.vision.service.VehicleSearchServiceImpl
 **/
public interface VehicleSearchService {
	public List<VehicleSearchResultVO> findBySearchCriteria(VehicleSearchCriteriaVO vehicleSearchCriteriaVO, Pageable pageable, Sort sort) throws MalBusinessException;
	public int findBySearchCriteriaCount(VehicleSearchCriteriaVO vehicleSearchCriteriaVO) throws MalBusinessException;
}
