package com.mikealbert.data.dao;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.mikealbert.data.entity.DealerAccessoryCode;

/**
* DAO for DealerAccessoryCode Entity
* @author sibley
*/

public interface DealerAccessoryCodeDAO extends JpaRepository<DealerAccessoryCode, String> {
	@Query("SELECT dacc FROM DealerAccessoryCode dacc WHERE lower(dacc.newAccessoryCode) LIKE lower(?1) OR lower(dacc.description) LIKE lower(?1) OR lower(dacc.accessoryCode) LIKE lower(?1)")
	public List<DealerAccessoryCode> findDealerAccessoryCodesByCodeOrDescription(String codeDescription, Pageable page);
	
	@Query("SELECT dacc FROM DealerAccessoryCode dacc JOIN dacc.dealerAccessories dac WHERE dac.model.modelId = ?2 AND (lower(dacc.newAccessoryCode) LIKE lower(?1) OR lower(dacc.description) LIKE lower(?1) OR lower(dacc.accessoryCode) LIKE lower(?1))")
	public List<DealerAccessoryCode> findDealerAccessoryCodesByCodeOrDescriptionAndModelId(String codeDescription, Long mdlId, Pageable page);
	
	@Query("SELECT dacc FROM DealerAccessoryCode dacc WHERE dacc.optionAccessoryCategory.code IN (4, 17, 18) AND (lower(dacc.newAccessoryCode) LIKE lower(?1) OR lower(dacc.description) LIKE lower(?1) OR lower(dacc.accessoryCode) LIKE lower(?1))")
	public List<DealerAccessoryCode> getDealerAccessoryCodesByCodeOrDescription(String codeDescription, Pageable page);
}
