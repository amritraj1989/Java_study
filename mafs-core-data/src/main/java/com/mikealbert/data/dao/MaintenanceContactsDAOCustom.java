package com.mikealbert.data.dao;

import java.util.List;

import com.mikealbert.data.vo.MaintenanceContactsVO;

public interface MaintenanceContactsDAOCustom {
	public List<MaintenanceContactsVO> getMaintenanceContactsByAccount(Long cId, String accountType, String accountCode);
}
