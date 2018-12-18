package com.mikealbert.data.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mikealbert.data.entity.DocPropertyValue;
import com.mikealbert.data.entity.ExternalAccountPK;

public interface DocPropertyValueDAO extends JpaRepository<DocPropertyValue, Long>{
	
	@Query("SELECT dpv FROM DocPropertyValue dpv WHERE  dpv.docProperty.name = ?1 AND dpv.doc.docId = ?2 AND dpv.externalAccount.externalAccountPK = ?3")
	public DocPropertyValue findByNameDocIdVendor(String dpName, Long docId, ExternalAccountPK vendor);
	
	@Query("SELECT dpv FROM DocPropertyValue dpv WHERE  dpv.docProperty.name = ?1 AND dpv.doc.docId = ?2")
	public DocPropertyValue findByNameDocId(String dpName, Long docId);
	
	@Query("SELECT dpv FROM DocPropertyValue dpv WHERE dpv.doc.docId = ?1")
	public List<DocPropertyValue> findAllByDocId(Long docId);

}
