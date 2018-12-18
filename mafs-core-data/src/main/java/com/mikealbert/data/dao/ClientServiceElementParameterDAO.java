package com.mikealbert.data.dao;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mikealbert.data.entity.ClientServiceElementParameter;

public interface ClientServiceElementParameterDAO extends ClientServiceElementParameterDAOCustom, JpaRepository<ClientServiceElementParameter, Long> {
	
	@Query("SELECT SUM(csep.value) FROM ClientServiceElementParameter csep WHERE csep.clientServiceElement.clientServiceElementId = ?1")
	public BigDecimal getParameterClientValuesSum(Long clientServiceElementId);
	
	@Query("select csep FROM ClientServiceElementParameter csep WHERE csep.clientServiceElement.clientServiceElementId = ?1 AND csep.formulaParameter.fprId = ?2")
	public ClientServiceElementParameter getClientServiceElementParameter(Long clientServiceElementId, Long formulaParameterId);
	
	@Query("FROM ClientServiceElementParameter csep WHERE csep.clientServiceElement.clientServiceElementId = ?1")
	public List<ClientServiceElementParameter> findByClientServiceElementId(Long clientServiceElementId);
	
}
