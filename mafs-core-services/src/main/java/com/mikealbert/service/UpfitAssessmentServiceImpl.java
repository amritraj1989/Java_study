package com.mikealbert.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mikealbert.data.dao.UpfitAssessmentAnswerDAO;
import com.mikealbert.data.dao.UpfitAssessmentQuestionDAO;
import com.mikealbert.data.dao.VehicleConfigurationDAO;
import com.mikealbert.data.entity.UpfitAssessmentAnswer;
import com.mikealbert.data.entity.UpfitAssessmentQuestion;
import com.mikealbert.data.entity.VehicleConfiguration;
import com.mikealbert.data.vo.UpfitAssessmentVO;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.exception.MalException;
import com.mikealbert.util.MALUtilities;

/**
 * Implementation of {@link com.mikealbert.vision.service.UpfitAssessmentService}
 */
@Service("upfitAssessmentService")
public class UpfitAssessmentServiceImpl implements UpfitAssessmentService {
	
	@Resource UpfitAssessmentQuestionDAO upfitAssessmentQuestionDAO;
	@Resource UpfitAssessmentAnswerDAO upfitAssessmentAnswerDAO;
	@Resource VehicleConfigurationDAO vehicleConfigurationDAO;

	@Override
	public List<UpfitAssessmentVO> prepareUpfitAssessmentVoForVehicleConfigId(Long vcfId) {
		
		List<UpfitAssessmentVO> upfitAssessmentVoList = new ArrayList<UpfitAssessmentVO>();
		UpfitAssessmentVO upfitAssessmentVO = null;
		VehicleConfiguration vehConfig = null;
		
		List<UpfitAssessmentQuestion> questionList = upfitAssessmentQuestionDAO.getAllActiveUpfitAssessmentQuestions();
		
		if(vcfId != null){
			vehConfig = vehicleConfigurationDAO.findById(vcfId).orElse(null);
		}
		
		for(UpfitAssessmentQuestion uaq : questionList){
			upfitAssessmentVO = new UpfitAssessmentVO();
			
			upfitAssessmentVO.setUpfitAssessmentQuestion(uaq);
			upfitAssessmentVO.setUpfitAssessmentAnswer(new UpfitAssessmentAnswer());
			upfitAssessmentVO.getUpfitAssessmentAnswer().setUpfitAssessmentQuestion(uaq);
			upfitAssessmentVO.getUpfitAssessmentAnswer().setVehicleConfiguration(vehConfig);
			
			upfitAssessmentVoList.add(upfitAssessmentVO);
		}
		
		return upfitAssessmentVoList;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void saveOrUpdateUpfitAssessmentAnswers(List<UpfitAssessmentVO> upfitAssessmentVOs, String userName) throws MalBusinessException, MalException {
		if(!MALUtilities.isEmpty(upfitAssessmentVOs)){
			
			List<UpfitAssessmentAnswer> answerList = new ArrayList<UpfitAssessmentAnswer>();
			UpfitAssessmentAnswer answer = null;
			for(UpfitAssessmentVO upfitAssessmentVO: upfitAssessmentVOs){
				answer = upfitAssessmentVO.getUpfitAssessmentAnswer();
				answer.setLastUpdatedBy(userName);
				answer.setLastUpdatedDate(new Date());
				answerList.add(answer);
			}
			if(answerList.size() > 0){
				upfitAssessmentAnswerDAO.saveAll(answerList);
			}
		}
	}
	
	@Override
	public List<UpfitAssessmentAnswer> getUpfitAssessmentAnswersByVehicleConfigId(Long vcfId){
		List<UpfitAssessmentAnswer> answerList = null;
		if(!MALUtilities.isEmpty(vcfId))
			answerList = upfitAssessmentAnswerDAO.getUpfitAssessmentAnswersByVehicleConfigId(vcfId);
		
		return answerList;
		
	}

	@Override
	public List<UpfitAssessmentVO> getUpfitAssessmentVosByVehicleConfigId(Long vcfId) {
		
		List<UpfitAssessmentVO> upfitAssessmentVoList = new ArrayList<UpfitAssessmentVO>();
		UpfitAssessmentVO upfitAssessmentVO = null;
		
		List<UpfitAssessmentAnswer> answerList = upfitAssessmentAnswerDAO.getUpfitAssessmentAnswersByVehicleConfigId(vcfId);
		
		for(UpfitAssessmentAnswer uaa : answerList){
			upfitAssessmentVO = new UpfitAssessmentVO();
			
			upfitAssessmentVO.setUpfitAssessmentAnswer(uaa);
			upfitAssessmentVO.setUpfitAssessmentQuestion(uaa.getUpfitAssessmentQuestion());
			
			upfitAssessmentVoList.add(upfitAssessmentVO);
		}
		return upfitAssessmentVoList;
	}

}
