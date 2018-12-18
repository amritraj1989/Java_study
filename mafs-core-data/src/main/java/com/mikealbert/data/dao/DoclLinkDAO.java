package com.mikealbert.data.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mikealbert.data.entity.DoclLink;
import com.mikealbert.data.entity.DoclLinkPK;

public interface DoclLinkDAO extends JpaRepository<DoclLink, DoclLinkPK>{
	@Query("select d from DoclLink d where d.id.parentDocId = ?1 and d.id.childDocId = ?2")
	List<DoclLink> findByParentDocIdAndChildDocId(Long parentDocId, Long childDocId);
}
