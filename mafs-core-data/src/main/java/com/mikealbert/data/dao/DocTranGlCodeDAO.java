package com.mikealbert.data.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.mikealbert.data.entity.DocTranGlCode;
import com.mikealbert.data.entity.DocTranGlCodePK;

/**
 * @author Roshan K
 */
public interface DocTranGlCodeDAO extends JpaRepository<DocTranGlCode, DocTranGlCodePK> {
	
	@Query("SELECT d1.glCode FROM DocTranGlCode d1 WHERE d1.id.cId = ?1 and d1.id.docType = ?2 and d1.id.tranType = ?3 and d1.id.glPostingType = 'SALES'")
	public String findGlCodeByCidDocTypeTranType(Long cId, String docType, String tranType);
	
}