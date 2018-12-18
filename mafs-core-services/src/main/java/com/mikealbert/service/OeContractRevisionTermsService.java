package com.mikealbert.service;

import java.util.List;

import com.mikealbert.data.vo.OeConRevTermsVO;
import com.mikealbert.exception.MalException;

public interface OeContractRevisionTermsService {
	public List<OeConRevTermsVO> getOeConRevTermsReportVO(Long currentQmdId, Long revisionQmdId) throws MalException;
}
