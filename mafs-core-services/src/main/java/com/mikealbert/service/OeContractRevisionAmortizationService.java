package com.mikealbert.service;

import java.util.List;

import com.mikealbert.data.vo.OeConRevAmortizationScheduleVO;
import com.mikealbert.exception.MalException;

public interface OeContractRevisionAmortizationService {
	public List<OeConRevAmortizationScheduleVO> getOeConRevAmortizationReportVO(Long currentQmdId, Long revisionQmdId) throws MalException;
}
