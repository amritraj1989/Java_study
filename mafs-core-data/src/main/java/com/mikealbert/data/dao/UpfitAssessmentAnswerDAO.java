package com.mikealbert.data.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mikealbert.data.entity.UpfitAssessmentAnswer;

/**
* DAO for UpfitAssessmentAnswer Entity
* @author Amritraj
*/

public interface UpfitAssessmentAnswerDAO extends JpaRepository<UpfitAssessmentAnswer, Long> {
	
	@Query("FROM UpfitAssessmentAnswer uaa WHERE uaa.vehicleConfiguration.vcfId = ?1")
	public List<UpfitAssessmentAnswer> getUpfitAssessmentAnswersByVehicleConfigId(Long vcfId);
}
