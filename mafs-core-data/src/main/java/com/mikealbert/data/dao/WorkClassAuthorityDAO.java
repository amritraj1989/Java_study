package com.mikealbert.data.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mikealbert.data.entity.WorkClassAuthority;

/**
* DAO for WorkClassAuthority Entity
*/
public interface WorkClassAuthorityDAO extends JpaRepository<WorkClassAuthority, Long>{ 
	@Query("SELECT wca FROM WorkClassAuthority wca WHERE wca.workClass.id.workClass = ?1 AND wca.workClass.id.cId = ?2 AND wca.moduleName = ?3")
	public List<WorkClassAuthority> findByWorkClassAndModule(String workClass, Long cId, String moduleName);

	@Query("SELECT wca FROM WorkClassAuthority wca WHERE wca.workClass.id.workClass = ?1 AND wca.workClass.id.cId = ?2 AND wca.moduleName = ?3 AND wca.financeParameter.parameterId = ?4")
	public WorkClassAuthority findByWorkClassAndModuleAndParameterId(String workClass, Long cId, String moduleName, String parameterId);

}

