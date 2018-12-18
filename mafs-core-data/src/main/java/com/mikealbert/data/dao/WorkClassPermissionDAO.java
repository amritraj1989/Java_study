package com.mikealbert.data.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mikealbert.data.entity.WorkClassPermission;

/**
* DAO for WorkClassPermission Entity
*/
public interface WorkClassPermissionDAO extends JpaRepository<WorkClassPermission, Long>{ 
	@Query("SELECT wcp FROM WorkClassPermission wcp WHERE wcp.workClass.id.workClass = ?1 AND wcp.workClass.id.cId = ?2 ")
	public List<WorkClassPermission> findByWorkClass(String workClass, Long cId);
}

