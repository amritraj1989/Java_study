package com.mikealbert.data.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mikealbert.data.entity.UpfitterProgress;

/**
* DAO for UpfitterQuote Entity
* @author sibley
*/

public interface UpfitterProgressDAO extends JpaRepository<UpfitterProgress, Long> {
	@Query("SELECT ufp FROM UpfitterProgress ufp INNER JOIN ufp.quotationModel qmd WHERE qmd.qmdId = ?1 ")
	public List<UpfitterProgress> findByQmdId(Long qmdId);
	
	@Query("SELECT ufp from UpfitterProgress ufp INNER JOIN ufp.childrenUpfitterProgress cufp WHERE cufp.parentUpfitterProgress.ufpId = ?1")
	public List<UpfitterProgress> findChildrenByParentUfpId(Long parentUfpId);
}
