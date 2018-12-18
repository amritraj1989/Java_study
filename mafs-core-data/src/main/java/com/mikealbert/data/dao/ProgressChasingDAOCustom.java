package com.mikealbert.data.dao;

import java.util.List;

import org.springframework.data.domain.Pageable;
import com.mikealbert.data.vo.ProgressChasingQueueVO;
import com.mikealbert.data.vo.ProgressChasingVO;

public interface ProgressChasingDAOCustom {
	public List<ProgressChasingVO> getProgressChasingByPoStatus(String selectedPoStatus, Pageable pageable, String lastUpdatedByFilter);
	public int getProgressChasingByPoStatusCount(String selectedPoStatus, String lastUpdatedByFilter);
	public List<ProgressChasingQueueVO> getProgressChasing();
}
