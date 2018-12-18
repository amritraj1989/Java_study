package com.mikealbert.data.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mikealbert.data.entity.Dist;

public interface DistDAO extends JpaRepository<Dist, Long>{
	@Query("SELECT d FROM Dist d WHERE d.doc.docId = ?1")
	public List<Dist> findDistByDocId(Long docId);	
	
	@Query("SELECT d FROM Dist d WHERE d.cdbCode1 = ?1 and d.cdbCode4 = ?2")
	public List<Dist> findDistByCDBCode1And4(String  cdb_code_1 ,String  cdb_code_4 );
	
	@Query("SELECT d FROM Dist d WHERE d.docl.id.docId = ?1 and d.docl.id.lineId = ?2")
	public List<Dist> findDistByDoclIdAndDoclLineId(Long doclId,Long doclLineId);	
	
}
