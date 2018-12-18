package com.mikealbert.data.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.mikealbert.data.entity.OptionPackHeader;

/**
* DAO for OptionPackHeader Entity
* @author sibley
*/

public interface OptionPackHeaderDAO extends JpaRepository<OptionPackHeader, Long> {
	@Query("SELECT oph FROM OptionPackHeader oph WHERE oph.model.modelId = ?1 AND oph.optionPackTypeCode.code = ?2")
	public OptionPackHeader findByModelAndCode(Long modelId, String code);
}
