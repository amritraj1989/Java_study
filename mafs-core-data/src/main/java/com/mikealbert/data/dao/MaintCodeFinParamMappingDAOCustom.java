package com.mikealbert.data.dao;

import java.util.List;

import com.mikealbert.data.vo.MaintCodeFinParamMappingVO;

public interface MaintCodeFinParamMappingDAOCustom {
	public List<MaintCodeFinParamMappingVO> findMaintenanceCodeFinanceParameterValues(String maintCode);

}
