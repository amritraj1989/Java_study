package com.mikealbert.data.dao;

import java.util.Date;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.mikealbert.data.entity.SupplierProgressHistory;

/**
* DAO for SupplierProgressHistory Entity
* @author Singh
*/

public interface SupplierProgressHistoryDAO extends JpaRepository<SupplierProgressHistory, Long> , SupplierProgressHistoryDAOCustom {
	
	@Query("SELECT sph FROM SupplierProgressHistory sph WHERE sph.docId = ?1 and sph.actionDate = ?2 ")
	public List<SupplierProgressHistory> findSupplierProgressHistory(Long docId, Date actionDate);
	
	@Query("SELECT sph FROM SupplierProgressHistory sph WHERE sph.docId = ?1 and sph.progressType = ?2  and sph.actionDate = ?3  ")
	public List<SupplierProgressHistory> findSupplierProgressHistory(Long docId, String progressType ,Date actionDate);
	
	@Query("SELECT sph FROM SupplierProgressHistory sph WHERE sph.docId = ?1 and sph.progressType = ?2  and sph.actionDate = ?3  and sph.supplier = ?4 ")
	public List<SupplierProgressHistory> findSupplierProgressHistory(Long docId, String progressType ,Date actionDate, String supplier);

	@Query("SELECT sph FROM SupplierProgressHistory sph WHERE sph.docId = ?1")
	public List<SupplierProgressHistory> findSupplierProgressHistoryForDoc(Long docId, Sort sort);
	
	@Query("SELECT sph FROM SupplierProgressHistory sph WHERE sph.docId = ?1 and sph.progressType = ?2 and sph.enteredDate = (SELECT MAX(sph2.enteredDate) from SupplierProgressHistory sph2 where sph2.docId = sph.docId and sph.progressType = sph2.progressType) order by sphId desc")
	public List<SupplierProgressHistory> findSupplierProgressHistoryForDocAndType(Long docId, String progressType);
	
	@Query("SELECT sph FROM SupplierProgressHistory sph WHERE sph.docId = ?1 and sph.progressType = ?2 and sph.sphId = (SELECT MAX(sph2.sphId) from SupplierProgressHistory sph2 where sph2.docId = sph.docId and sph.progressType = sph2.progressType)")
	public SupplierProgressHistory findSupplierProgressHistoryForDocAndTypeById(Long docId, String progressType);
	
	@Query("SELECT sph FROM SupplierProgressHistory sph WHERE sph.docId = ?2 and sph.progressType = ?3 and sph.sphId = (SELECT MAX(sph2.sphId) from SupplierProgressHistory sph2 where sph2.docId = sph.docId and sph.progressType = sph2.progressType and sph2.sphId != ?1)")
	public SupplierProgressHistory findByDocIdAndTypeByIdAndExcludedSphId(Long sphId, Long docId, String progressType);
	
	@Query("SELECT sph FROM SupplierProgressHistory sph WHERE sph.docId = ?1 and sph.progressType = ?2 and sph.sphId = (SELECT MIN(sph2.sphId) from SupplierProgressHistory sph2 where sph2.docId = sph.docId and sph.progressType = sph2.progressType)")
	public SupplierProgressHistory findOldestSupplierProgressHistoryForDocAndTypeById(Long docId, String progressType);
}
