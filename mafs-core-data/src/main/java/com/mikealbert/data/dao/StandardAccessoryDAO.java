package com.mikealbert.data.dao;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.mikealbert.data.entity.StandardAccessory;

/**
* DAO for StandardAccessory Entity
* @author Sibley
*/

public interface StandardAccessoryDAO extends JpaRepository<StandardAccessory, Long> {
	
	@Query("SELECT sac FROM StandardAccessory sac WHERE sac.model.modelId = ?1 AND sac.standardAccessoryCode.newAccessoryCode = ?2")
	public List<StandardAccessory> findByModelAndNewAccessoryCode(Long modelId, String code);	
}
