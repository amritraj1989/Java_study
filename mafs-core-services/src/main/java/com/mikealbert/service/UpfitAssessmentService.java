package com.mikealbert.service;

import java.util.List;

import com.mikealbert.data.entity.UpfitAssessmentAnswer;
import com.mikealbert.data.vo.UpfitAssessmentVO;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.exception.MalException;

/**
 * 
 * @see com.mikealbert.data.entity.UpfitAssessmentQuestion
 * */
public interface UpfitAssessmentService {
	
	public List<UpfitAssessmentVO> prepareUpfitAssessmentVoForVehicleConfigId(Long vcfId);
	public List<UpfitAssessmentVO> getUpfitAssessmentVosByVehicleConfigId(Long vcfId);
	
	public void saveOrUpdateUpfitAssessmentAnswers(List<UpfitAssessmentVO> upfitAssessmentVOs, String userName) throws MalBusinessException, MalException; 
	
	public List<UpfitAssessmentAnswer> getUpfitAssessmentAnswersByVehicleConfigId(Long vcfId);
}
