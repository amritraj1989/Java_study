package com.mikealbert.data.dao;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mikealbert.data.entity.Make;
import com.mikealbert.data.entity.ModelType;

/**
* DAO for Make Entity
* @author sibley
*/
public interface MakeDAO extends JpaRepository<Make, Long>{
	@Query("SELECT mak FROM Make mak WHERE lower(mak.makeDesc) = lower(?1) ORDER BY mak.makeCode, mak.makeDesc")
	public List<Make> findByDescription(String description, Pageable pageable); 
	
	@Query("SELECT mak FROM Make mak WHERE lower(mak.makeDesc) like lower(?1) ORDER BY mak.makeCode, mak.makeDesc")
	public List<Make> findByDescription(String description); 
	
	@Query("SELECT count(mak) FROM Make mak WHERE lower(mak.makeDesc) like lower(?1) ORDER BY mak.makeCode, mak.makeDesc")
	public Long findByDescriptionCount(String description);
	
	@Query("SELECT mak FROM Make mak WHERE mak.modelType.description = ?1 ORDER BY mak.makeDesc ASC")
	public List<Make> findByModelType(String modelType);
	
	@Query("SELECT DISTINCT (mak.makeDesc) " +
			"    FROM Make mak" +
			"        JOIN mak.models mdl " +
			"        JOIN mdl.modelMarkYear mmy" +
			"        JOIN mdl.modelType mtp" +
			"    WHERE mmy.modelMarkYearDesc LIKE ?1 AND lower(mtp.description) LIKE ?2 AND lower(mdl.standardCode) LIKE ?3" +
			"    ORDER BY mak.makeDesc ASC")
	public List<String> findByCriteria(String year, String modelType, String mfgCode);
	
	@Query("SELECT mak FROM Make mak WHERE mak.makeCode = ?1")
	public List<Make> findByMakeCode(String makeCode);
	
    @Query("SELECT mak FROM Make mak WHERE mak.modelType = ?1 ORDER BY mak.makeDesc")
    public List<Make> findByModelType(ModelType modelType);
}

