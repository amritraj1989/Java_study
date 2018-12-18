package com.mikealbert.data.dao;

import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mikealbert.data.entity.Model;

/**
 * DAO for Model Entity
 * 
 * @author Singh
 */

public interface ModelDAO extends JpaRepository<Model, Long>, ModelDAOCustom {
	@Query("from Model md where upper(md.modelType.description)= upper(?1) and upper(md.modelMarkYear.modelMarkYearDesc)= upper(?2) and  upper(md.make.makeDesc)  = upper(?3)")
	List<Model> findByModelTypeYearAndMake(String modelType, String year, String make);
	
	@Query("SELECT DISTINCT (mdl.modelDescription) FROM Model mdl" +
			" WHERE lower(mdl.standardCode) LIKE ?1 AND mdl.modelMarkYear.modelMarkYearDesc LIKE ?2 AND lower(mdl.make.makeDesc) LIKE ?3 AND lower(mdl.makeModelRange.description) LIKE ?4" + 
			" ORDER BY mdl.modelDescription ASC")
	public List<String> findByCriteria(String mfgCode, String year, String make, String modelCode);
	
	@Query("SELECT DISTINCT(mdl.standardCode) FROM Model mdl WHERE lower(mdl.standardCode) LIKE ?1 ORDER BY mdl.standardCode ASC")
	public List<String> findMfgCodes(String term, Pageable page);
	
	@Query("SELECT mdl FROM Model mdl WHERE mdl.standardEDINo = ?1 ORDER BY mdl.standardEDINo ASC")
	public Model findByStandardEDINo(String standardEDINo);	
}
