package com.mikealbert.data.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.mikealbert.data.entity.ProcessStage;
import com.mikealbert.data.entity.ProcessStageObject;


/**
* DAO for ProcessStage Entity
*/

public interface ProcessStageDAO extends JpaRepository<ProcessStage, Long> {
	
	@Query("SELECT psg FROM ProcessStage psg JOIN psg.process prs WHERE prs.processName = ?1 AND psg.processStageName = ?2")
	public ProcessStage getProcessStageByProcessNameAndStageName(String processName, String processStageName);	
	
}
