package com.mikealbert.data.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.mikealbert.data.entity.ModelColour;
import com.mikealbert.data.entity.ModelColourTrim;

/**
* DAO for ModelColourTrim Entity
* @author sibley
*/

public interface ModelColourTrimDAO extends JpaRepository<ModelColourTrim, String> {
	@Query("SELECT mct FROM ModelColourTrim mct WHERE mct.model.modelId = ?1 AND mct.trimCode.newTrimCode = ?2")
	public ModelColourTrim findByModelAndTrimCode(Long modelId, String code);		
	
}
