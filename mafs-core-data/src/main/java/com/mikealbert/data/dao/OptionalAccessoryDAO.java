package com.mikealbert.data.dao;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.mikealbert.data.entity.OptionalAccessory;

/**
* DAO for OptionalAccessory Entity
* @author Singh
*/

public interface OptionalAccessoryDAO extends JpaRepository<OptionalAccessory, Long> {
	
	@Query("SELECT oac FROM OptionalAccessory oac WHERE oac.model.modelId = ?1 AND oac.accessoryCode.newAccessoryCode = ?2")
	public List<OptionalAccessory> findByModelAndNewAccessoryCode(Long modelId, String code);	
}
