package com.mikealbert.data.dao;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.mikealbert.data.entity.MakeModelRange;

/**
* DAO for MakeModelRange Entity
* @author sibley
*/
public interface MakeModelRangeDAO extends JpaRepository<MakeModelRange, Long>, MakeModelRangeDAOCustom{
	@Query("SELECT DISTINCT (mrg.description) " +
			"    FROM MakeModelRange mrg" +
			"        JOIN mrg.models mdl " +
			"        JOIN mdl.make mak " +
			"        JOIN mdl.modelMarkYear mmy" +
			"        JOIN mdl.modelType mtp" +
			"    WHERE mmy.modelMarkYearDesc LIKE ?1 " +
			"        AND lower(mak.makeDesc) LIKE ?2 " +
			"        AND lower(mrg.description) LIKE ?3 " +
			"        AND lower(mtp.description) LIKE ?4 " +
			"        AND lower(mdl.standardCode) LIKE ?5" +
			"    ORDER BY mrg.description ASC")
	public List<String> findByCriteria(String year, String make, String modelRange, String modelType, String mfgCode, Pageable page);
	
	//TODO Need to restructure to return count only
	@Query("SELECT DISTINCT (mrg.description) " +
			"    FROM MakeModelRange mrg" +
			"        JOIN mrg.models mdl " +
			"        JOIN mdl.make mak " +
			"        JOIN mdl.modelMarkYear mmy" +
			"        JOIN mdl.modelType mtp" +
			"    WHERE mmy.modelMarkYearDesc LIKE ?1 " +
			"        AND lower(mak.makeDesc) LIKE ?2 " +
			"        AND lower(mrg.description) LIKE ?3 " +
			"        AND lower(mtp.description) LIKE ?4 " +
			"        AND lower(mdl.standardCode) LIKE ?5")
	public List<String> findByCriteriaCount(String year, String make, String modelRange, String modelType, String mfgCode);
	
}
