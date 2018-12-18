package com.mikealbert.data.dao;

import java.util.List;

import com.mikealbert.data.entity.WorkshopCapabilityCode;

public interface WorkshopCapabilityCodeDAOCustom {
	public List<WorkshopCapabilityCode> findWorkshopCapabilityCodesByGroupName(String groupName);
}
