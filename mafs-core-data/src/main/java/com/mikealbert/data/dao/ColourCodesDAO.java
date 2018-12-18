package com.mikealbert.data.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mikealbert.data.entity.ColourCodes;

/**
* DAO for ColourCodes Entity
* @author Singh
*/

public interface ColourCodesDAO extends JpaRepository<ColourCodes, String> {
	
	
}
