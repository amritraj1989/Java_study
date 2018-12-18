package com.mikealbert.data.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mikealbert.data.entity.Doc;
import com.mikealbert.data.entity.DocLink;
import com.mikealbert.data.entity.DocLinkPK;

public interface DocLinkDAO extends JpaRepository<DocLink, DocLinkPK> {
	
	@Query("from DocLink where id.parentDocId = ?1")
	List<DocLink>	findByParentDocId(Long docId);
	
	@Query("select dl.id.childDocId from DocLink dl where dl.id.parentDocId in (?1 )")
	List<Long>	findByParentDocIds(List<Long> parentDocId);
	
	@Query("select dl.id.parentDocId from DocLink dl where dl.id.childDocId in (?1 )")
	List<Long>	findByChildDocIds(List<Long> childDocId);
	
	@Query("select doclink from DocLink doclink where doclink.id.childDocId= ?1")
	DocLink		findByChildDocId(Long childDocId);
	
	@Query("select dl.childDoc from DocLink dl where dl.childDoc.docType = 'PORDER' and dl.childDoc.orderType = 'T' AND dl.childDoc.docStatus <> 'C' and dl.id.parentDocId in (?1 )")
	List<Doc> findThirdPartyDocsByParentDocId(Long docId);

	@Query("select dl.id.childDocId from DocLink dl where dl.childDoc.docType = 'PORDER' and dl.childDoc.orderType = 'T' AND dl.childDoc.docStatus <> 'C' and dl.id.parentDocId in (?1 )")
	List<Long> findThirdPartyDocsIdByParentDocId(Long docId);
	
	}
