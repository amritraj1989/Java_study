package com.mikealbert.data.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.mikealbert.data.entity.OnbaseUploadedDocs;



public interface OnbaseUploadedDocsDAO extends JpaRepository<OnbaseUploadedDocs, Long> , OnbaseUploadedDocsDAOCustom {

@Modifying
@Query("update OnbaseUploadedDocs obd set obd.indexKey = ?1 where obd.obdId = ?2")
int updateIndexKey(String indexKey, Long obdId);

@Query("SELECT obud from OnbaseUploadedDocs obud where obud.objectId = ?1 and obud.objectType = ?2")
public List<OnbaseUploadedDocs> getOnBaseUploadedDocsByObjectIdAndType(String objectId, String objectType);
}
