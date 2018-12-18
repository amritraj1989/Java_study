package com.mikealbert.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mikealbert.data.dao.GlCodeDAO;
import com.mikealbert.data.vo.GlCodeLOVVO;
import com.mikealbert.exception.MalBusinessException;

@Service
@Transactional
public class GlCodeServiceImpl implements GlCodeService {
	@Resource
	private GlCodeDAO glCodeDAO;

	public List<GlCodeLOVVO> getGlCodes(String code, Long corpId, Pageable pageable) throws MalBusinessException {
		try {
			return glCodeDAO.findByCodeAndCorpId(code, corpId, pageable);
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new MalBusinessException("generic.error.occured.while",
					new String[] { "getting Gl Code" }, ex);
		}
	}
	
	public int getGlCodesCount(String code, Long corpId) throws MalBusinessException {
		try {
			return glCodeDAO.findByCodeAndCorpIdCount(code, corpId);
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new MalBusinessException("generic.error.occured.while",
					new String[] { "getting Gl Code Count" }, ex);
		}
	}
}
