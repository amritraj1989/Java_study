package com.mikealbert.data.dao;

import java.util.List;

import org.springframework.data.domain.Pageable;

import com.mikealbert.data.entity.Dist;
import com.mikealbert.data.entity.GlCode;
import com.mikealbert.data.vo.GlCodeLOVVO;
import com.mikealbert.data.vo.GlCodeProcParamsVO;

public interface GlCodeDAOCustom {
	public List<GlCodeLOVVO> findByCodeAndCorpId(String code, Long corpId, Pageable pageable);
	public int findByCodeAndCorpIdCount(String code, Long corpId);
	public List<GlCode> findGlCodeByDist(List<Dist> glDistList);
	public String findGlCodeByProc(GlCodeProcParamsVO glCodeProcParamsVO);	
}
