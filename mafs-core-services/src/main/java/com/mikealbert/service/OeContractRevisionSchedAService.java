package com.mikealbert.service;

import java.util.List;

import com.mikealbert.data.vo.OeConRevScheduleAVO;
import com.mikealbert.exception.MalException;

public interface OeContractRevisionSchedAService {
	public List<OeConRevScheduleAVO> getOeConRevScheduleAReportVO(Long currentQmdId, Long revisionQmdId) throws MalException;
}
