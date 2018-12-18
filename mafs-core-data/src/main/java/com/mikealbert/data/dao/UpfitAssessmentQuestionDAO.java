package com.mikealbert.data.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mikealbert.data.entity.UpfitAssessmentQuestion;

/**
* DAO for UpfitAssessmentQuestion Entity
* @author Amritraj
*/

public interface UpfitAssessmentQuestionDAO extends JpaRepository<UpfitAssessmentQuestion, Long> {
	
	@Query("FROM UpfitAssessmentQuestion uaq WHERE uaq.obsoleteYN = 'N' ORDER BY uaq.groupPosition, uaq.linePosition")
	public List<UpfitAssessmentQuestion> getAllActiveUpfitAssessmentQuestions();
	
}
