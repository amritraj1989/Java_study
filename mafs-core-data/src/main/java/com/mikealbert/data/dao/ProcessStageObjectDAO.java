package com.mikealbert.data.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mikealbert.data.entity.ProcessStageObject;


/**
* DAO for ProcessStageObject Entity
*/

public interface ProcessStageObjectDAO extends JpaRepository<ProcessStageObject, Long> {
	
	@Query("SELECT pso FROM ProcessStageObject pso JOIN pso.processStage psg WHERE psg.processStageName = ?1 AND pso.objectName = ?2 AND pso.objectId = ?3")
	public ProcessStageObject getProcessStageObjectByProcessStageObjectNameAndId(String processStageName, String objectName, Long objectId);	
	
}
