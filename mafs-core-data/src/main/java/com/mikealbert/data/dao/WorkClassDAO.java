package com.mikealbert.data.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mikealbert.data.entity.WorkClass;
import com.mikealbert.data.entity.WorkClassPK;

/**
* DAO for WorkClass Entity
*/
public interface WorkClassDAO extends JpaRepository<WorkClass, WorkClassPK> {
	
    @Query("select wc from WorkClass wc where wc.id.cId = ?1 order by wc.id.workClass")
	public List<WorkClass> findAllForContextId(long contextId);
	
    @Query("select wc from WorkClass wc where wc.id.workClass LIKE ?1 AND wc.id.cId = ?2")
    public WorkClass findWorkClass(String workClass, long contextId);
}
