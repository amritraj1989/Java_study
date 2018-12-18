package com.mikealbert.data.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mikealbert.data.entity.CapEleParameter;

public interface CapEleParameterDAO extends JpaRepository<CapEleParameter, Long>{
	
	@Query("from CapEleParameter where celCelId = ?1 and parameterType = ?2")
	public List<CapEleParameter> findByCelIdAndParameterType(Long celId, String parameterType);
	
	@Query("SELECT cep FROM CapEleParameter cep WHERE cep.parameterName = ?1 AND cep.parameterType = ?2")
	public CapEleParameter findByParameterName(String parameterName, String prameterType);
}
