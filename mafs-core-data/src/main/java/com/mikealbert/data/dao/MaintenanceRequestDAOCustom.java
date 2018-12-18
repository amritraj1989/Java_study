package com.mikealbert.data.dao;

import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.mikealbert.data.entity.MaintenanceRequest;
import com.mikealbert.data.vo.MaintenanceServiceHistoryVO;

public interface MaintenanceRequestDAOCustom {
	public List<MaintenanceServiceHistoryVO> getMaintenanceServiceHistory(List<Long> fmsIds, Pageable pageable, Sort sort, String providerFilter, String maintCategoryFilter, String maintCodeDescFilter, List<String> excludeMaintCodesFromPOTotal);

	public int getMaintRequestByFmsIdsCount(List<Long> fmsIds, String providerFilter, String maintCategoryFilter, String maintCodeDescFilter, List<String> excludeMaintCodesFromPOTotal);
	
	public int getMaintRequestByMrqMrqIdCount(Long parentMrqId);
	
	public void createPurchaseOrderDocument(MaintenanceRequest maintenanceRequest);
	
	public void createWebNotification(MaintenanceRequest maintenanceRequest);
	
	public void delete(MaintenanceRequest maintReq);
}
