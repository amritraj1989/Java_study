package com.mikealbert.service;

import java.util.List;

import org.springframework.data.domain.Pageable;

import com.mikealbert.data.vo.GlCodeLOVVO;
import com.mikealbert.exception.MalBusinessException;

public interface GlCodeService {
		List<GlCodeLOVVO>	getGlCodes(String code, Long corpId, Pageable pageable) throws MalBusinessException;
		public int getGlCodesCount(String code, Long corpId) throws MalBusinessException;
}
