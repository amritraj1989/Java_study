package com.mikealbert.data.vo;

import com.mikealbert.data.entity.UpfitAssessmentAnswer;
import com.mikealbert.data.entity.UpfitAssessmentQuestion;

public class UpfitAssessmentVO {
	
	private UpfitAssessmentQuestion upfitAssessmentQuestion;
	private UpfitAssessmentAnswer upfitAssessmentAnswer;
	
	public UpfitAssessmentQuestion getUpfitAssessmentQuestion() {
		return upfitAssessmentQuestion;
	}
	public void setUpfitAssessmentQuestion(
			UpfitAssessmentQuestion upfitAssessmentQuestion) {
		this.upfitAssessmentQuestion = upfitAssessmentQuestion;
	}
	public UpfitAssessmentAnswer getUpfitAssessmentAnswer() {
		return upfitAssessmentAnswer;
	}
	public void setUpfitAssessmentAnswer(UpfitAssessmentAnswer upfitAssessmentAnswer) {
		this.upfitAssessmentAnswer = upfitAssessmentAnswer;
	}	
	
}
