package com.mikealbert.data.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.mikealbert.data.entity.ModelColour;

/**
* DAO for ModelColour Entity
* @author sibley
*/

public interface ModelColourDAO extends JpaRepository<ModelColour, String> {
	@Query("SELECT mdc FROM ModelColour mdc WHERE mdc.model.modelId = ?1 AND mdc.colourCode.newColourCode = ?2")
	public ModelColour findByModelAndColourCode(Long modelId, String code);		
	
}
