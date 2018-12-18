package com.mikealbert.data.dao;

import java.util.List;

import com.mikealbert.data.vo.MaintenancePreferencesVO;

public interface MaintenancePreferencesDAOCustom {
	public List<MaintenancePreferencesVO> getMaintenancePreferencesByAccount(Long cId, String accountType, String accountCode);

}
