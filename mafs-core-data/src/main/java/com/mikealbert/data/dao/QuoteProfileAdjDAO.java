
/**
 * QuoteProfileAdjDAO.java
 * mafs-core-data
 * Mar 11, 2013
 * 3:31:23 PM
 */
package com.mikealbert.data.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mikealbert.data.entity.QuoteProfileAdj;

/**
 * @author anand.mohan
 *
 */
public interface QuoteProfileAdjDAO extends JpaRepository<QuoteProfileAdj, Long>{
	@Query("from QuoteProfileAdj where qprId = ?1 and adjType = ?2 and gridType = ?3")
	public QuoteProfileAdj findByQprIdAdjTypeAndGridType(Long qprId, String adjType,String gridType);
}
