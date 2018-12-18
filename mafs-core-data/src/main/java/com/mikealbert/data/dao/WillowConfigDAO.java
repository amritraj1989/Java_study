package com.mikealbert.data.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mikealbert.data.entity.WillowConfig;

/**
* DAO for AccessoryCode Entity
* @author Singh
*/

public interface WillowConfigDAO extends JpaRepository<WillowConfig, String> {
	
	@Query("SELECT c FROM WillowConfig c WHERE c.configName in ( ?1 )")
	public List<WillowConfig> getMultipleWillowConfigWithValue(List<String>  configNames);
	
	
}
