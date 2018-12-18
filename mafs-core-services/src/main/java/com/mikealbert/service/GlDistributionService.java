package com.mikealbert.service;

import java.util.List;

import com.mikealbert.data.entity.Dist;
import com.mikealbert.exception.MalBusinessException;

public interface GlDistributionService {

	public List<Dist> getGlDistByDocId(Long docId) throws MalBusinessException;
	public void saveOrUpdateGLDist(List<Dist> distList) throws MalBusinessException;
}
