package com.mikealbert.data.dao;

import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.mikealbert.data.entity.DealerAccessory;

/**
* DAO for DealerAccessory Entity
* @author sibley
*/

public interface DealerAccessoryDAO extends DealerAccessoryDAOCustom,JpaRepository<DealerAccessory, Long> {
	@Query("SELECT da.dealerAccessoryCode.description FROM DealerAccessory da WHERE da.dacId IN (?1)")
	public List<String> getAccessoriesDescription(List<Long> dacIdsList);
	
	@Query("SELECT da.dealerAccessoryCode.description FROM DealerAccessory da WHERE da.dacId IN (Select distinct dl.genericExtId from Docl dl where dl.id.docId = ?1) order by da.dealerAccessoryCode.description")
	public List<String> getPOAccessoriesDescription(Long docId);
	
	@Query("SELECT dac FROM DealerAccessory dac WHERE lower(dac.dealerAccessoryCode.newAccessoryCode) LIKE lower(?1) OR lower(dac.dealerAccessoryCode.description) LIKE lower(?1) OR lower(dac.dealerAccessoryCode.accessoryCode) LIKE lower(?1)")
	public List<DealerAccessory> findDealerAccessoryByCodeOrDescription(String codeDescription, Pageable page);

	@Query("SELECT dac FROM DealerAccessory dac WHERE dac.model.modelId = ?1 AND dac.dealerAccessoryCode.accessoryCode = ?2")
	public DealerAccessory findByModelAndAccessoryCode(Long modelId, String accessoryCode);
	
	@Query("SELECT da FROM DealerAccessory da WHERE da.model.modelId = ?1 AND (lower(da.dealerAccessoryCode.newAccessoryCode) LIKE lower(?2) OR lower(da.dealerAccessoryCode.description) LIKE lower(?2) OR lower(da.dealerAccessoryCode.accessoryCode) LIKE lower(?2))")
	public List<DealerAccessory> findByModelAndCodeOrDescription(Long modelId, String codeDescription, Pageable page);
	
	@Query("SELECT da FROM DealerAccessory da "
			+ "INNER JOIN FETCH da.dealerAccessoryPrices dpl "
			+ "WHERE da.model.modelId = ?1 "
			+ "AND dpl.effectiveDate IN(SELECT MAX(dpl1.effectiveDate) from DealerAccessoryPrice dpl1 where dpl1.dealerAccessory.dacId = da.dacId AND dpl1.effectiveDate <= CURRENT_DATE) "
			+ "AND (lower(da.dealerAccessoryCode.newAccessoryCode) LIKE lower(?2) OR lower(da.dealerAccessoryCode.description) LIKE lower(?2) OR lower(da.dealerAccessoryCode.accessoryCode) LIKE lower(?2))")
	public List<DealerAccessory> findEffectiveByModelAndCodeOrDescription(Long modelId, String codeDescription, Pageable page);
	
	@Query("SELECT dac FROM DealerAccessory dac WHERE dac.model.modelId = ?1 AND dac.dealerAccessoryCode.newAccessoryCode = ?2")
	public List<DealerAccessory> findByModelAndNewAccessoryCode(Long modelId, String accessoryCode);	
}