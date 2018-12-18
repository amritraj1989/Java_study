package com.mikealbert.data.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mikealbert.data.entity.DocSupplier;

public interface DocSupplierDAO extends JpaRepository<DocSupplier, Long>{
	
	@Query("SELECT ds FROM DocSupplier ds WHERE  ds.doc.docId = ?1")
	public List<DocSupplier> findDocSuppliersByDocId(Long docId);

}
