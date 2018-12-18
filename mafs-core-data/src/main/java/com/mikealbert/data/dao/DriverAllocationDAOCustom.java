package com.mikealbert.data.dao;

import java.util.List;

import com.mikealbert.data.entity.DriverAllocation;
import com.mikealbert.data.vo.DriverAllocationVO;

public interface DriverAllocationDAOCustom {

	public List<DriverAllocation> getCurrentDriverAllocationByDrvId(long drvId);
	
	public List<DriverAllocationVO> getCurrentDriverAllocationVOs(Long drvId);
	public List<DriverAllocationVO> getPreviousDriverAllocationVOs(Long drvId);
}
