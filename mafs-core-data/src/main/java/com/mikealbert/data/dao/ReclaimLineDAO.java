package com.mikealbert.data.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import com.mikealbert.data.entity.ReclaimLines;

/**
* DAO for ReclaimLines Entity
* 
* @author Roshan
*/

public interface ReclaimLineDAO extends JpaRepository<ReclaimLines, Long> {

		
	
	@Query("SELECT rl FROM ReclaimLines rl WHERE rl.doclDocId= ?1 AND rl.doclLineId= ?2")
	public List<ReclaimLines> findReclaimbleLines(Long docId , long doclLineId);	
	
	@Query("SELECT rl FROM ReclaimLines rl WHERE rl.doclDocId= ?1")
	public List<ReclaimLines> findReclaimbleLines(Long docId);	

	@Query("SELECT rl FROM ReclaimLines rl WHERE rl.qceId= ?1")
	public ReclaimLines findReclaimbleLinesByQceId(Long qceId);

}
