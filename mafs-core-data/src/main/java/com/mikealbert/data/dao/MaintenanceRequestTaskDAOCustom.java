package com.mikealbert.data.dao;

import java.util.List;
import com.mikealbert.data.entity.MaintenanceRequestTask;
import com.mikealbert.data.vo.HistoricalMaintCatCodeVO;
import com.mikealbert.data.vo.MaintenanceCodeVO;

public interface MaintenanceRequestTaskDAOCustom {
	
	public List<HistoricalMaintCatCodeVO> getHistoricalMaintCatCode(List<Long> fmsIds, String maintCatCode, Long mrqId);
	public List<String> getDistinctHistoricalCatCodes(List<Long> fmsIds, Long mrqId);
	public List<MaintenanceCodeVO> findTasksMaintenanceCodes(List<Long> mrqIdList);
}
