package com.mikealbert.service;

import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import com.mikealbert.data.vo.MaintenanceServiceHistoryVO;


public interface MaintenanceHistoryService {	
	public List<MaintenanceServiceHistoryVO> getMaintenanceServiceHistoryByVIN(String vin, Pageable pageable , Sort sort, String providerFilter, String maintCategoryFilter, String maintCodeDescFilter);
	public List<MaintenanceServiceHistoryVO> getMaintenanceServiceHistoryByFmsId(Long fmsId, Pageable pageable , Sort sort, String providerFilter, String maintCategoryFilter, String maintCodeDescFilter);    
	
	public int getMaintenanceServiceHistoryByVINCount(String vin, String providerFilter, String maintCategoryFilter, String maintCodeDescFilter);
	public int getMaintenanceServiceHistoryByFmsIdCount(Long fmsId, String providerFilter, String maintCategoryFilter, String maintCodeDescFilter);	
}
